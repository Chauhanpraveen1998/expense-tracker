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
