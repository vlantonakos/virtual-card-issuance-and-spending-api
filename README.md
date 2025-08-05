# Virtual Card Issuance and Spending API

## 💼 Project Overview

This project implements the backend for a **Virtual Card Platform** where customers can create virtual cards, top them up, and spend from them. The platform guarantees that overspending is **never allowed**, even under concurrent usage, ensuring **transactional integrity and data consistency**.

---

## ✅ Core Features

### Key Business Rules Implemented
- **Atomic Card Operations:** Card balance can never go below zero.
- **No Double-Spend:** Optimistic locking (@Version) on card updates prevents race conditions.
- **Block/Unblock:** Cards can be blocked and re-activated; blocked cards cannot be used for spending.
- **Rate Limiting:** Maximum 5 spend operations per card per minute.
- **Transaction History:** List and paginate all card transactions.
- **Robust Validation:** No spending on non-existent or blocked cards; strong input validation.
- **Consistent Error Handling:** Global exception handler maps business and concurrency errors to clear HTTP responses.

---

## 🔌 API Endpoints

- `POST /cards`  
  Creates a new virtual card with initial balance.

- `POST /cards/{id}/spend`  
  Spend an amount from the card.
  - Returns **400 Bad Request** if balance is insufficient, card is blocked/non-existent, or rate limit exceeded.

- `POST /cards/{id}/topup`  
  Add funds to an existing card.

- `GET /cards/{id}`  
  Retrieve card details including current balance and status.

- `GET /cards/{id}/transactions`  
  Returns transaction history with pagination support (page number and size query parameters).

- `GET /cards/{id}/balance`  
  Returns current balance and card status.

- `GET /cards/{id}/status`  
  Returns card status and cardholder information.

---

## ⚙️ Technical Stack and Implementation

- **Spring Boot:** (Java 11), Spring Data JPA, Spring Web, Bean Validation
- **Optimistic Locking:** JPA @Version for safe, concurrent updates
- **Database:**
  - `local` profile: Oracle DB
  - `dev` profile: H2 in-memory DB for fast development/testing.
- **Liquibase:** Schema migrations (CARD_PLATFORM schema)
- **Logging:** Console and file output with timestamped pattern (application.yml)
- **Profiles:** Easily switch between Oracle and H2 via spring.profiles.active
- **Global Exception Handler:** Unified error responses, including 409 Conflict for concurrency issues

---

## 🧠 Design Decisions

- **Layered Architecture:** Controller → Service → Repository; DTOs and domain models for separation of concerns.

- **Domain-Driven:** Core business logic (locking, rate limiting, pagination) lives in the domain/service layer.

- **Transactional Safety:** All state-changing operations use @Transactional.

- **Integration Testing:** Includes concurrent spend simulation to guarantee race condition safety and correct error handling.

---

## 🚀 How to Use

- **Activate profiles**:
  - `local` (Oracle DB): `-Dspring.profiles.active=local`
  - `dev` (H2 DB): `-Dspring.profiles.active=dev`

## Prerequisites

- Java 11 (OpenJDK 11.0.28)
- Maven 3.8.7
- Tested on Linux (WSL2)