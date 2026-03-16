# Expense Tracker Backend Analysis

## Current Backend Status

| Component | Exists? | Details |
|-----------|---------|---------|
| **Entities** | Yes | User, Expense, Category, Budget |
| **Controllers** | Yes | AuthController, ExpenseController, CategoryController, BudgetController |
| **Services** | Yes | AuthService, ExpenseService, CategoryService, BudgetService, JwtService, UserDetailsServiceImpl |
| **Repositories** | Yes | UserRepository, ExpenseRepository, CategoryRepository, BudgetRepository |
| **Security/JWT** | Yes | JWT authentication with Spring Security, BCrypt password encoding |
| **Database Config** | Yes | PostgreSQL with Flyway migrations |

### Project Structure

```
backend/
├── src/main/java/com/expensetracker/
│   ├── BackendApplication.java
│   ├── config/SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── ExpenseController.java
│   │   ├── CategoryController.java
│   │   └── BudgetController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── ExpenseService.java
│   │   ├── CategoryService.java
│   │   ├── BudgetService.java
│   │   ├── JwtService.java
│   │   └── UserDetailsServiceImpl.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Expense.java
│   │   ├── Category.java
│   │   └── Budget.java
│   ├── repository/
│   ├── dto/
│   ├── security/JwtAuthenticationFilter.java
│   └── exception/
├── pom.xml (Spring Boot 3.3.5, Java 21)
└── src/main/resources/application.properties
```

### Current Endpoints

- **Auth**: `POST /api/auth/register`, `POST /api/auth/login`
- **Expenses**: CRUD at `/api/expenses` + `/recent`, `/monthly-total`, `/summary`, `/daily`
- **Categories**: GET/POST at `/api/categories`
- **Budgets**: GET/POST at `/api/budgets` + `/status`

### Database Configuration

- **Database**: PostgreSQL
- **Host**: localhost:5433
- **Database Name**: expense_tracker
- **Migration**: Flyway (src/main/resources/db/migration)
- **Hibernate**: ddl-auto=validate

### Security Setup

- JWT token-based authentication
- BCrypt password encoding
- CORS enabled for all origins
- Stateless session management
- Endpoints `/api/auth/**` are public, all others require authentication

---

## Gap Analysis (Android vs Backend)

| Android Feature | Backend Support | Action Needed |
|-----------------|-----------------|---------------|
| User Auth | ✅ Complete | None |
| Transactions CRUD | ⚠️ Partial | Missing PUT endpoint |
| Accounts CRUD | ❌ Missing | **Create Account entity/controller/repo** |
| Analytics/Dashboard | ⚠️ Partial | Missing filter params (page, size, categoryId) |
| Categories | ✅ Complete | None |
| SMS Sync | ❌ Missing | No endpoints for SMS parsing/import |
| Transaction Type (Income/Expense) | ❌ Missing | No `type` field - only expenses |
| Merchant Name | ❌ Missing | No merchant field |
| Account Linkage | ❌ Missing | No account_id on transactions |
| Recurring Transactions | ❌ Missing | No recurring field |
| Transaction Tags | ❌ Missing | No tags support |
| Pagination | ❌ Missing | No page/size params |

### Android App Features (for reference)

The Android app has the following domain models that the backend doesn't fully support:

1. **Account**: Bank Account, Credit Card, Wallet, Cash types with balance tracking
2. **Transaction**: Includes merchantName, accountId, type (INCOME/EXPENSE), isRecurring, tags
3. **Category**: Predefined categories with icons and colors
4. **Analytics**: Spending trends, insights, category breakdowns
5. **SMS Sync**: Parse bank SMS notifications to auto-create transactions

---

## Recommended Refactoring

### 1. Add Account Entity

Create Account entity with bank account/credit card/wallet/cash types, link to transactions:

```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String name;
    private String type; // BANK, CREDIT_CARD, WALLET, CASH
    private BigDecimal balance;
    private String bankName;
    private String lastFourDigits;
    private String colorPrimary;
    private String colorSecondary;
    private boolean isActive;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### 2. Enhance Expense Model

Add the following fields to Expense entity:

- `type` (INCOME/EXPENSE)
- `merchantName`
- `accountId` (FK to Account)
- `isRecurring`
- `tags` (List<String>)

### 3. Add Pagination

Add `page`, `size` query params to expense endpoints:

```
GET /api/expenses?page=0&size=20&categoryId=xxx
```

### 4. Add PUT Endpoint

Currently missing for expenses - only POST/DELETE exist.

### 5. Add SMS Sync API

Create endpoint to receive parsed SMS data and create transactions:

```
POST /api/transactions/from-sms
```

### 6. Add Analytics Endpoints

Add more sophisticated analytics:

- Category breakdown
- Spending trends
- Monthly comparisons
- Budget vs actual

### 7. Add Account Endpoints

```
GET /api/accounts
POST /api/accounts
PUT /api/accounts/{id}
DELETE /api/accounts/{id}
GET /api/accounts/{id}/transactions
```

---

## Summary

The backend has a solid foundation with authentication, basic expense/category/budget management, and proper security configuration. However, it's missing several features required by the Android app:

1. **No Account support** - Cannot track which account a transaction belongs to
2. **No Income transactions** - Only tracks expenses
3. **No merchant tracking** - Missing merchant name field
4. **No pagination** - Cannot handle large datasets efficiently
5. **No SMS sync** - Cannot receive parsed bank SMS notifications
6. **No recurring transactions** - Missing recurring flag
7. **No transaction tags** - Missing tags support
8. **No PUT for expenses** - Update endpoint missing

The most critical missing feature is the **Account entity** since it's fundamental to the Android app's architecture and affects how transactions are stored and displayed.
