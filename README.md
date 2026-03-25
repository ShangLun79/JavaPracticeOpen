# Mini Banking API

以 Spring Boot 3 實作的銀行轉帳 RESTful API，涵蓋 JWT 驗證、JPA 進階查詢、交易一致性與完整單元測試。

## 技術棧

Java 17 / Spring Boot 3.2 / Spring Security + JWT / Spring Data JPA / H2 / JUnit 5 + Mockito / Swagger UI

## 功能模組

- **Auth**：註冊 / 登入，BCrypt 加密，JWT Token 發放
- **Account**：開戶 / 查詢 / 關戶，UUID 帳號生成
- **Transaction**：存款 / 取款 / 轉帳，分頁交易紀錄

## 技術亮點

**JWT 驗證流程**
`OncePerRequestFilter` 攔截請求 → 解析 Token → 載入使用者 → 設定 `SecurityContext`，全程無狀態，支援水平擴展。

**@Transactional 轉帳一致性**
扣款、入帳、寫入交易紀錄在同一個 Transaction 中，任何一步失敗全部回滾，保障 ACID。

**JPA 進階練習**
| 主題 | 實作方式 |
|------|---------|
| 動態查詢 | `Specification` + `JpaSpecificationExecutor` |
| 自訂查詢 | `@Query` JPQL / Native SQL、`@Modifying` |
| N+1 問題 | `JOIN FETCH` vs `@EntityGraph` 兩種解法比較 |
| 自動審計 | `@MappedSuperclass` + `@CreatedDate` / `@LastModifiedDate` |
| 部分欄位查詢 | Interface-based Projection |

**JDBC vs JPA 對照實驗**
以 `JdbcTemplate` + 手寫 `RowMapper` 重現相同查詢，理解 JPA 自動化了什麼。

**單元測試**
Service 層以 Mockito Mock 依賴，`@Nested` 分組測試情境，AssertJ 驗證例外與副作用。

## 專案結構

```
src/main/java/com/shawn/side/
├── config/        # SecurityConfig, JpaConfig, OpenApiConfig
├── controller/    # AuthController, AccountController, TransactionController
├── service/       # 業務邏輯，@Transactional 邊界
├── repository/    # JPA Repository, Specification, JdbcAccountDao
├── entity/        # User, Account, Transaction, AuditableEntity
├── dto/
│   ├── request/   # RegisterRequest, DepositRequest, TransferRequest ...
│   └── response/  # ApiResponse<T>, AccountResponse, TransactionResponse ...
├── exception/     # GlobalExceptionHandler, 自定義領域例外
└── security/      # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl
```

## 本地啟動

```bash
./mvnw spring-boot:run
```

- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- H2 Console：`http://localhost:8080/h2-console`（JDBC URL: `jdbc:h2:mem:bankingdb`）
