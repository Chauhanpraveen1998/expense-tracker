#!/bin/bash

# ============================================================
# Expense Tracker - Repository Setup Script
# ============================================================
# Run this script after creating the repo on GitHub
# ============================================================

set -e  # Exit on error

echo "🚀 Setting up Expense Tracker repository..."

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ------------------------------------------------------------
# Step 1: Clone and enter directory
# ------------------------------------------------------------
echo -e "${BLUE}Step 1: Cloning repository...${NC}"
git clone https://github.com/Chauhanpraveen1998/expense-tracker.git
cd expense-tracker

# ------------------------------------------------------------
# Step 2: Create directory structure
# ------------------------------------------------------------
echo -e "${BLUE}Step 2: Creating directory structure...${NC}"

mkdir -p backend/src/main/java/com/expensetracker
mkdir -p backend/src/main/resources/db/migration
mkdir -p backend/src/test/java/com/expensetracker

mkdir -p android/app/src/main/java/com/expensetracker/android
mkdir -p android/app/src/main/res
mkdir -p android/app/src/test

mkdir -p docs/architecture
mkdir -p docs/api
mkdir -p docs/sms-patterns

mkdir -p scripts

# ------------------------------------------------------------
# Step 3: Create .gitignore
# ------------------------------------------------------------
echo -e "${BLUE}Step 3: Creating .gitignore...${NC}"

cat > .gitignore << 'EOF'
# ========================
# Backend (Spring Boot)
# ========================
backend/target/
backend/*.jar
backend/*.war
backend/.mvn/
backend/mvnw
backend/mvnw.cmd

# ========================
# Android
# ========================
android/.gradle/
android/build/
android/app/build/
android/local.properties
android/*.iml
android/.idea/
android/captures/
android/.externalNativeBuild/
android/.cxx/

# ========================
# IDE
# ========================
.idea/
*.iml
*.ipr
*.iws
.vscode/
*.swp
*.swo
.DS_Store

# ========================
# Environment
# ========================
.env
.env.local
*.env

# ========================
# Logs
# ========================
*.log
logs/

# ========================
# Build outputs
# ========================
dist/
out/
build/

# ========================
# Dependencies
# ========================
node_modules/

# ========================
# Secrets (NEVER commit)
# ========================
**/application-prod.yml
**/application-secrets.yml
*.pem
*.key
EOF

# ------------------------------------------------------------
# Step 4: Create docker-compose.yml
# ------------------------------------------------------------
echo -e "${BLUE}Step 4: Creating docker-compose.yml...${NC}"

cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: expense-tracker-db
    environment:
      POSTGRES_DB: expense_tracker
      POSTGRES_USER: expense_user
      POSTGRES_PASSWORD: expense_pass_dev
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U expense_user -d expense_tracker"]
      interval: 5s
      timeout: 5s
      retries: 5

  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: expense-tracker-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@expense.local
      PGADMIN_DEFAULT_PASSWORD: admin
      PGADMIN_CONFIG_SERVER_MODE: 'False'
    ports:
      - "5050:80"
    depends_on:
      - postgres
    volumes:
      - pgadmin_data:/var/lib/pgadmin

volumes:
  postgres_data:
  pgadmin_data:

# Usage:
# ------
# Start:    docker-compose up -d
# Stop:     docker-compose down
# Reset DB: docker-compose down -v && docker-compose up -d
# Logs:     docker-compose logs -f postgres
#
# Connection details:
# -------------------
# Host:     localhost
# Port:     5432
# Database: expense_tracker
# Username: expense_user
# Password: expense_pass_dev
#
# pgAdmin:  http://localhost:5050
# Email:    admin@expense.local
# Password: admin
EOF

# ------------------------------------------------------------
# Step 5: Create README.md
# ------------------------------------------------------------
echo -e "${BLUE}Step 5: Creating README.md...${NC}"

cat > README.md << 'EOF'
# 💰 Personal Expense Tracker

An intelligent expense tracking application that automatically detects financial transactions from SMS messages.

## 🎯 Features

- **Auto SMS Parsing**: Automatically reads and parses financial SMS messages
- **Smart Categorization**: Rule-based merchant categorization
- **Offline-First**: Works without internet, syncs when available
- **Privacy-Focused**: All SMS parsing happens locally on device
- **Cross-Platform**: Android app + Web dashboard (future)

## 🏗️ Architecture

```
┌─────────────────────┐      ┌─────────────────────┐
│   Android App       │      │   Spring Boot API   │
│   (Kotlin/Compose)  │◄────►│   (Java 21)         │
│   + Room + SQLCipher│      │   + PostgreSQL      │
└─────────────────────┘      └─────────────────────┘
```

## 🛠️ Tech Stack

### Backend
- Java 21 + Spring Boot 3.2
- Spring Security + JWT
- PostgreSQL 16 + Flyway
- Maven

### Android
- Kotlin + Jetpack Compose
- Room + SQLCipher
- Hilt (DI)
- Retrofit + OkHttp
- WorkManager

## 🚀 Getting Started

### Prerequisites
- JDK 21
- Docker & Docker Compose
- Android Studio (Hedgehog or newer)
- PostgreSQL client (optional)

### Start Database
```bash
docker-compose up -d
```

### Run Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Run Android App
Open `android/` folder in Android Studio and run on emulator.

## 📁 Project Structure

```
expense-tracker/
├── backend/           # Spring Boot application
├── android/           # Android application
├── docs/              # Documentation
├── scripts/           # Utility scripts
└── docker-compose.yml # Development database
```

## 📝 License

MIT License - see LICENSE file for details.

## 👨‍💻 Author

Praveen Chauhan ([@Chauhanpraveen1998](https://github.com/Chauhanpraveen1998))
EOF

# ------------------------------------------------------------
# Step 6: Create backend placeholder
# ------------------------------------------------------------
echo -e "${BLUE}Step 6: Creating backend placeholder...${NC}"

cat > backend/README.md << 'EOF'
# Backend - Spring Boot Application

## Setup

1. Generate project from [start.spring.io](https://start.spring.io/) with:
   - Project: Maven
   - Language: Java
   - Spring Boot: 3.2.x
   - Group: com.expensetracker
   - Artifact: backend
   - Java: 21
   - Dependencies:
     - Spring Web
     - Spring Security
     - Spring Data JPA
     - PostgreSQL Driver
     - Flyway Migration
     - Lombok
     - Validation
     - Spring Boot DevTools

2. Extract contents here (replace this README)

3. Create `src/main/resources/application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/expense_tracker
    username: expense_user
    password: expense_pass_dev
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080
```

4. Run: `./mvnw spring-boot:run -Dspring.profiles.active=dev`
EOF

# ------------------------------------------------------------
# Step 7: Create android placeholder
# ------------------------------------------------------------
echo -e "${BLUE}Step 7: Creating android placeholder...${NC}"

cat > android/README.md << 'EOF'
# Android - Kotlin/Compose Application

## Setup

1. Open Android Studio
2. File → New → New Project
3. Select: Empty Activity (Compose)
4. Settings:
   - Name: ExpenseTracker
   - Package: com.expensetracker.android
   - Save location: This folder
   - Language: Kotlin
   - Minimum SDK: API 26 (Android 8.0)
   - Build configuration: Kotlin DSL

5. Wait for Gradle sync

6. Add dependencies in `app/build.gradle.kts`:
```kotlin
dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // SQLCipher
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
}
```

7. Run on emulator or device
EOF

# ------------------------------------------------------------
# Step 8: Create initial Flyway migration
# ------------------------------------------------------------
echo -e "${BLUE}Step 8: Creating initial database migration...${NC}"

cat > backend/src/main/resources/db/migration/V1__initial_schema.sql << 'EOF'
-- ============================================================
-- V1: Initial Schema for Expense Tracker
-- ============================================================

-- Users table
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(100),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Devices (for multi-device sync)
CREATE TABLE devices (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_name     VARCHAR(100),
    device_token    VARCHAR(255),
    last_sync_at    TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT uk_device_token UNIQUE (device_token)
);

-- Categories (system + user-defined)
CREATE TABLE categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(50) NOT NULL,
    icon            VARCHAR(50),
    color           VARCHAR(7),
    is_system       BOOLEAN DEFAULT FALSE,
    display_order   INTEGER DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT uk_category_name UNIQUE (user_id, name)
);

-- Transactions
CREATE TABLE transactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id           UUID REFERENCES devices(id),
    
    amount              DECIMAL(12,2) NOT NULL,
    transaction_type    VARCHAR(10) NOT NULL CHECK (transaction_type IN ('CREDIT', 'DEBIT')),
    category_id         UUID REFERENCES categories(id),
    merchant            VARCHAR(255),
    
    payment_method      VARCHAR(20),
    account_mask        VARCHAR(20),
    reference_no        VARCHAR(50),
    
    transaction_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    
    description         TEXT,
    notes               TEXT,
    tags                TEXT[],
    
    source              VARCHAR(20) DEFAULT 'SMS',
    confidence_score    DECIMAL(3,2),
    is_verified         BOOLEAN DEFAULT FALSE,
    
    local_id            VARCHAR(100),
    sms_hash            VARCHAR(64),
    
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    synced_at           TIMESTAMP WITH TIME ZONE,
    
    CONSTRAINT uk_transaction_local_id UNIQUE (user_id, local_id),
    CONSTRAINT uk_transaction_sms_hash UNIQUE (user_id, sms_hash)
);

-- Budgets
CREATE TABLE budgets (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id         UUID REFERENCES categories(id),
    
    amount              DECIMAL(12,2) NOT NULL,
    period              VARCHAR(10) NOT NULL CHECK (period IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
    start_date          DATE NOT NULL,
    
    alert_threshold     DECIMAL(3,2) DEFAULT 0.80,
    is_active           BOOLEAN DEFAULT TRUE,
    
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Goals
CREATE TABLE goals (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    name                VARCHAR(100) NOT NULL,
    target_amount       DECIMAL(12,2) NOT NULL,
    saved_amount        DECIMAL(12,2) DEFAULT 0,
    target_date         DATE,
    
    icon                VARCHAR(50),
    color               VARCHAR(7),
    priority            INTEGER DEFAULT 0,
    is_completed        BOOLEAN DEFAULT FALSE,
    
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Goal contributions
CREATE TABLE goal_contributions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    goal_id             UUID NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    amount              DECIMAL(12,2) NOT NULL,
    contributed_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    notes               TEXT
);

-- Indexes
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_at DESC);
CREATE INDEX idx_transactions_category ON transactions(user_id, category_id, transaction_at);
CREATE INDEX idx_transactions_type_date ON transactions(user_id, transaction_type, transaction_at);
CREATE INDEX idx_transactions_sync ON transactions(user_id, synced_at) WHERE synced_at IS NULL;
CREATE INDEX idx_budgets_user_active ON budgets(user_id) WHERE is_active = TRUE;
CREATE INDEX idx_goals_user_active ON goals(user_id) WHERE is_completed = FALSE;

-- Seed system categories
INSERT INTO categories (name, icon, color, is_system, display_order) VALUES
    ('Food & Dining', 'restaurant', '#FF6B6B', TRUE, 1),
    ('Transport', 'directions_car', '#4ECDC4', TRUE, 2),
    ('Shopping', 'shopping_bag', '#45B7D1', TRUE, 3),
    ('Entertainment', 'movie', '#96CEB4', TRUE, 4),
    ('Utilities', 'bolt', '#FFEAA7', TRUE, 5),
    ('Health', 'local_hospital', '#DDA0DD', TRUE, 6),
    ('Groceries', 'local_grocery_store', '#98D8C8', TRUE, 7),
    ('Travel', 'flight', '#F7DC6F', TRUE, 8),
    ('Education', 'school', '#BB8FCE', TRUE, 9),
    ('Investment', 'trending_up', '#82E0AA', TRUE, 10),
    ('Salary', 'account_balance_wallet', '#85C1E9', TRUE, 11),
    ('Uncategorized', 'help_outline', '#BDC3C7', TRUE, 99);
EOF

# ------------------------------------------------------------
# Step 9: Initial commit
# ------------------------------------------------------------
echo -e "${BLUE}Step 9: Making initial commit...${NC}"

git add .
git commit -m "chore: initial project structure

- Add monorepo structure (backend + android)
- Add docker-compose for PostgreSQL
- Add initial Flyway migration with schema
- Add project documentation"

git push origin main

# ------------------------------------------------------------
# Done!
# ------------------------------------------------------------
echo ""
echo -e "${GREEN}✅ Repository setup complete!${NC}"
echo ""
echo "Next steps:"
echo "1. cd expense-tracker"
echo "2. docker-compose up -d"
echo "3. Generate Spring Boot project from start.spring.io"
echo "4. Generate Android project in Android Studio"
echo ""
echo "Repository: https://github.com/Chauhanpraveen1998/expense-tracker"
EOF
