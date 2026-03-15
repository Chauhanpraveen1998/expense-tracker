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
