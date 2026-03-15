#!/bin/bash

# ============================================================
# pgAdmin Server Auto-Registration Script
# Expense Tracker Project
# ============================================================
# Run this AFTER: docker-compose up -d
# Usage: chmod +x scripts/setup-pgadmin-server.sh && ./scripts/setup-pgadmin-server.sh
# ============================================================

set -e

PGADMIN_URL="http://localhost:5050"
PGADMIN_EMAIL="admin@example.com"
PGADMIN_PASSWORD="admin"

PG_HOST="postgres"
PG_PORT=5432
PG_DB="expense_tracker"
PG_USER="expense_user"
PG_PASSWORD="expense_pass_dev"

COOKIE_FILE="/tmp/pgadmin_cookies_$$.txt"

cleanup() {
  rm -f "$COOKIE_FILE"
}
trap cleanup EXIT

echo ""
echo "=============================================="
echo "  pgAdmin Server Setup - Expense Tracker"
echo "=============================================="
echo ""

# Step 1: Wait for pgAdmin
echo "⏳ Waiting for pgAdmin to be ready..."
MAX_WAIT=60
WAITED=0
until curl -s "$PGADMIN_URL/misc/ping" > /dev/null 2>&1; do
  if [ $WAITED -ge $MAX_WAIT ]; then
    echo "❌ pgAdmin did not start within ${MAX_WAIT}s. Is Docker running?"
    exit 1
  fi
  sleep 2
  WAITED=$((WAITED + 2))
  echo "   Still waiting... (${WAITED}s)"
done
echo "✅ pgAdmin is ready!"
echo ""

# Step 2: Wait for PostgreSQL
echo "⏳ Waiting for PostgreSQL to be ready..."
WAITED=0
until docker exec expense-tracker-db pg_isready -U "$PG_USER" > /dev/null 2>&1; do
  if [ $WAITED -ge $MAX_WAIT ]; then
    echo "❌ PostgreSQL did not become ready within ${MAX_WAIT}s."
    exit 1
  fi
  sleep 2
  WAITED=$((WAITED + 2))
  echo "   Still waiting... (${WAITED}s)"
done
echo "✅ PostgreSQL is ready!"
echo ""

# Step 3: Get CSRF token + Login
echo "🔐 Logging into pgAdmin..."

# First GET to grab CSRF token
CSRF_TOKEN=$(curl -s -c "$COOKIE_FILE" "$PGADMIN_URL/login" | grep -o 'csrf_token[^"]*"[^"]*"' | head -1 | grep -o '"[^"]*"$' | tr -d '"')

LOGIN_RESPONSE=$(curl -s -c "$COOKIE_FILE" -b "$COOKIE_FILE" \
  -X POST "$PGADMIN_URL/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Referer: $PGADMIN_URL/login" \
  --data-urlencode "email=$PGADMIN_EMAIL" \
  --data-urlencode "password=$PGADMIN_PASSWORD" \
  -L)

if echo "$LOGIN_RESPONSE" | grep -q "Gravatar\|pgAdmin 4\|browser"; then
  echo "✅ Logged in successfully!"
else
  echo "❌ Login may have failed. Continuing anyway..."
fi
echo ""

# Step 4: Register server using Python inside pgAdmin container
echo "🔗 Registering PostgreSQL server via pgAdmin container..."

docker exec expense-tracker-pgadmin python3 - << PYEOF
import sqlite3
import os
import json

# pgAdmin stores server config in SQLite
db_path = "/var/lib/pgadmin/pgadmin4.db"

if not os.path.exists(db_path):
    print("❌ pgAdmin database not found at", db_path)
    exit(1)

conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# Check if server already exists
cursor.execute("SELECT id FROM server WHERE name = 'Expense Tracker DB'")
existing = cursor.fetchone()

if existing:
    print("ℹ️  Server 'Expense Tracker DB' already exists. Skipping.")
else:
    # Get the first user id
    cursor.execute("SELECT id FROM user LIMIT 1")
    user_row = cursor.fetchone()
    if not user_row:
        print("❌ No user found in pgAdmin database.")
        exit(1)
    user_id = user_row[0]

    # Get or create default server group
    cursor.execute("SELECT id FROM servergroup WHERE user_id = ? LIMIT 1", (user_id,))
    group_row = cursor.fetchone()
    if group_row:
        group_id = group_row[0]
    else:
        cursor.execute("INSERT INTO servergroup (user_id, name) VALUES (?, 'Servers')", (user_id,))
        group_id = cursor.lastrowid

    # Insert the server
    cursor.execute("""
        INSERT INTO server (
            user_id, servergroup_id, name, host, port,
            maintenance_db, username, password, save_password,
            ssl_mode, connect_timeout
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, (
        user_id, group_id,
        "Expense Tracker DB",
        "postgres", 5432,
        "expense_tracker",
        "expense_user",
        "expense_pass_dev",
        1,
        "prefer", 10
    ))
    conn.commit()
    print("✅ Server 'Expense Tracker DB' registered successfully!")

conn.close()
PYEOF

echo ""
echo "=============================================="
echo "  🎉 Setup Complete!"
echo "=============================================="
echo ""
echo "  pgAdmin URL : http://localhost:5050"
echo "  Email       : $PGADMIN_EMAIL"
echo "  Password    : $PGADMIN_PASSWORD"
echo ""
echo "  DB Server   : Expense Tracker DB"
echo "  Database    : $PG_DB"
echo "  User        : $PG_USER"
echo "=============================================="
echo ""
echo "  👉 Refresh pgAdmin in your browser to see the server."
echo ""
