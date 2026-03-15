#!/usr/bin/env python3
"""
Register a PostgreSQL server in pgAdmin's SQLite database.

Run this script inside the expense-tracker-pgadmin container:
    python3 /tmp/register_pgadmin_server.py
"""

import base64
import json
import os
import sqlite3
import sys

# ── pgAdmin uses AES-CFB8 with the master password as the encryption key ──────
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import Cipher
from cryptography.hazmat.primitives.ciphers.algorithms import AES
from cryptography.hazmat.primitives.ciphers.modes import CFB8

# ── Configuration ──────────────────────────────────────────────────────────────
PGADMIN_DB       = "/var/lib/pgadmin/pgadmin4.db"
PGADMIN_EMAIL    = "pgadmin4@pgadmin.org"   # default desktop user
MASTER_PASSWORD  = "admin"                  # pgAdmin master / login password

SERVER_NAME      = "Expense Tracker DB"
SERVER_HOST      = "postgres"
SERVER_PORT      = 5432
SERVER_DB        = "expense_tracker"
SERVER_USER      = "expense_user"
SERVER_PASSWORD  = "expense_pass_dev"
SERVER_SSL_MODE  = "prefer"
# ──────────────────────────────────────────────────────────────────────────────

_IV_SIZE        = AES.block_size // 8   # 16 bytes
_PAD_CHAR       = b"}"


def _pad_key(key: str) -> bytes:
    """Replicate pgAdmin's key-padding logic."""
    raw = key.encode() if isinstance(key, str) else key
    raw = raw[:32]
    if len(raw) in (16, 24, 32):
        return raw
    return raw.ljust(32, _PAD_CHAR)


def encrypt(plaintext: str, master_password: str) -> str:
    """Encrypt *plaintext* with AES-CFB8 using *master_password* as the key.

    Returns a plain str (not bytes) so SQLite stores it as TEXT, which is what
    pgAdmin expects when it reads the password column back.
    """
    iv = os.urandom(_IV_SIZE)
    cipher = Cipher(AES(_pad_key(master_password)), CFB8(iv), default_backend())
    enc = cipher.encryptor()
    ct  = enc.update(plaintext.encode()) + enc.finalize()
    return base64.b64encode(iv + ct).decode()   # str, not bytes


def main() -> int:
    conn = sqlite3.connect(PGADMIN_DB)
    cur  = conn.cursor()

    # 1. Resolve user id ───────────────────────────────────────────────────────
    cur.execute("SELECT id FROM user WHERE email = ?", (PGADMIN_EMAIL,))
    row = cur.fetchone()
    if not row:
        print(f"ERROR: user '{PGADMIN_EMAIL}' not found in pgAdmin database.")
        conn.close()
        return 1
    user_id = row[0]
    print(f"[+] User id: {user_id}  ({PGADMIN_EMAIL})")

    # 2. Resolve (or create) server group ──────────────────────────────────────
    cur.execute(
        "SELECT id, name FROM servergroup WHERE user_id = ? ORDER BY id LIMIT 1",
        (user_id,),
    )
    grp = cur.fetchone()
    if grp:
        servergroup_id, grp_name = grp
        print(f"[+] Using server group: '{grp_name}' (id={servergroup_id})")
    else:
        cur.execute(
            "INSERT INTO servergroup (user_id, name) VALUES (?, ?)",
            (user_id, "Servers"),
        )
        conn.commit()
        servergroup_id = cur.lastrowid
        print(f"[+] Created server group 'Servers' (id={servergroup_id})")

    # 3. Guard against duplicate entries ───────────────────────────────────────
    cur.execute(
        "SELECT id, name FROM server WHERE user_id = ? AND host = ? AND port = ?",
        (user_id, SERVER_HOST, SERVER_PORT),
    )
    existing = cur.fetchone()
    if existing:
        print(f"[!] Server already registered: '{existing[1]}' (id={existing[0]})")
        conn.close()
        return 0

    # 4. Encrypt the password the same way pgAdmin does ────────────────────────
    encrypted_password = encrypt(SERVER_PASSWORD, MASTER_PASSWORD)
    print(f"[+] Password encrypted successfully.")

    # 5. Insert the server row ─────────────────────────────────────────────────
    # connection_params must be a JSON string (not NULL) — pgAdmin does
    # membership tests on it and raises "NoneType is not iterable" if absent.
    connection_params = json.dumps({"sslmode": SERVER_SSL_MODE, "connect_timeout": 10})

    cur.execute(
        """
        INSERT INTO server (
            user_id, servergroup_id, name,
            host, port, maintenance_db, username,
            password, save_password,
            connection_params,
            use_ssh_tunnel, tunnel_port, tunnel_authentication, tunnel_keep_alive,
            shared, kerberos_conn, cloud_status
        ) VALUES (
            ?, ?, ?,
            ?, ?, ?, ?,
            ?, 1,
            ?,
            0, 22, 0, 0,
            0, 0, 0
        )
        """,
        (
            user_id, servergroup_id, SERVER_NAME,
            SERVER_HOST, SERVER_PORT, SERVER_DB, SERVER_USER,
            encrypted_password,
            connection_params,
        ),
    )
    conn.commit()
    server_id = cur.lastrowid
    print(f"[+] Server '{SERVER_NAME}' registered successfully (id={server_id}).")

    # 6. Verify the row is readable ────────────────────────────────────────────
    cur.execute(
        "SELECT id, name, host, port, maintenance_db, username, save_password "
        "FROM server WHERE id = ?",
        (server_id,),
    )
    cols = [d[0] for d in cur.description]
    vals = cur.fetchone()
    conn.close()

    print("\n── Verification ─────────────────────────────────────────────────")
    for col, val in zip(cols, vals):
        print(f"   {col:<20} = {val}")
    print("─────────────────────────────────────────────────────────────────")
    print("[✓] Done. Refresh pgAdmin in your browser to see the new server.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
