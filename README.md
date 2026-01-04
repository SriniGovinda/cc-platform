# CC Platform (Interview Practice Project)

Monorepo Spring Boot microservices project you can push to GitHub and deploy on EC2.

## What you get
- **API Gateway** (Spring Cloud Gateway) with **JWT validation**
- **Eureka Discovery** (service registration + `lb://` routing)
- **Centralized Config** (Spring Cloud Config Server, native repo in `config-repo/`)
- **Auth service** (Register/Login) → issues JWT
- **Account service** (Customers/Cards + limit reservation)
- **Transaction service** (Transactions + stream/collection practice + **S3 upload/download** for statements)
- **Async CSV Export Job** (batch pagination) + **Audit logs for every export request/download**
- **Saga service** (Orchestration-based Saga) → **distributed rollback via compensation**


## Architecture (High level)
Client → **gateway-service (8080)** → routes to:
- auth-service (8081)
- account-service (8082)
- transaction-service (8083)
- saga-service (8084)

## Architecture Principles
- API Gateway as single entry point
- Centralized JWT security
- Database per service
- Orchestration-based Saga for rollback

## Run locally (Docker required)
```bash
docker compose up -d
```

### Start platform services (recommended order)
```bash
mvn -q -pl eureka-server spring-boot:run
mvn -q -pl config-server spring-boot:run
mvn -q -pl auth-service spring-boot:run
mvn -q -pl account-service spring-boot:run
mvn -q -pl transaction-service spring-boot:run
mvn -q -pl saga-service spring-boot:run
mvn -q -pl gateway-service spring-boot:run
```

Eureka UI: http://localhost:8761
Config Server: http://localhost:8888

Environment variables (optional):
- `JWT_SECRET`, `JWT_ISSUER`
- `DB_URL`, `DB_USER`, `DB_PASS`
- `S3_ENDPOINT` (default LocalStack http://localhost:4566)
- `S3_BUCKET` (default cc-statements)

## Quick test (curl)

### 1) Register (or login)
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"Password123"}'
```
Copy `accessToken`.

### 2) Call protected endpoint via gateway
```bash
export TOKEN="PASTE_TOKEN"
curl -s http://localhost:8080/api/account/me \
  -H "Authorization: Bearer $TOKEN"
```

### 3) Saga debit (with rollback simulation)
```bash
curl -s -X POST http://localhost:8080/api/saga/debit \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"cardId":1,"amountYen":5000,"merchant":"Amazon","category":"ECOMMERCE","simulateFailureAfterCharge":true}'
```
Expected: `ROLLED_BACK` and compensation calls executed.

### 4) Transaction summary (Streams/Collections)
```bash
curl -s http://localhost:8080/api/transactions/summary \
  -H "Authorization: Bearer $TOKEN"
```

### 5) Upload statement to S3 (LocalStack)
```bash
printf "date,amount\n2025-01-01,1200\n" > sample.csv
curl -s -X POST http://localhost:8080/api/statements/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F file=@sample.csv
```
Then download using returned `id`.

## DB query practice ideas (Postgres)
Try these on the `transactions` table:
- 2nd highest amount per user/card (window function)
- monthly totals by category
- failed rate per merchant

## Async Export (CSV)
1) Create a job:
```bash
curl -s -X POST "http://localhost:8080/api/exports?fromDate=2025-01-01&toDate=2025-01-31&status=SUCCESS" \
  -H "Authorization: Bearer $TOKEN"
```
2) Poll status:
```bash
curl -s http://localhost:8080/api/exports/<jobId> -H "Authorization: Bearer $TOKEN"
```
3) Download CSV (streams from S3):
```bash
curl -L -o txns.csv http://localhost:8080/api/exports/<jobId>/download -H "Authorization: Bearer $TOKEN"
```

Audit logs are recorded in `audit_logs` for **EXPORT_REQUEST**, **EXPORT_COMPLETED/FAILED**, and **EXPORT_DOWNLOAD**.

## Deploy idea (EC2 + RDS + real S3)
- Build jars: `mvn -q -DskipTests package`
- Copy jars to EC2 (or build Docker images)
- Set env vars for RDS endpoint and AWS credentials
- Run services behind one gateway (8080) + security group rules

> This repo intentionally includes Eureka + Config + async export + audit logs so you can describe a realistic enhancement in interviews.
