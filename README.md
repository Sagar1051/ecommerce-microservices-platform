# eCommerce platform (microservices + AI recommendations)

Fresh multi-module Spring Boot project. Built module by module — right now
`eureka-server`, `api-gateway`, and `auth-service` are fully working.
The rest (`product-service`, `cart-service`, `order-service`,
`payment-service`, `recommendation-service`) are stubs: they boot, register
with Eureka, and are ready to be filled in next.

## Stack
- Java 21, Spring Boot 3.3.4, Spring Cloud 2023.0.3
- Eureka (service discovery), Spring Cloud Gateway (routing)
- PostgreSQL, Redis, Kafka (via docker-compose)
- JWT auth (jjwt 0.12.6), BCrypt password hashing
- Lombok 1.18.34 (pinned — this version is confirmed to work with Java 21,
  so you won't hit the old `ExceptionInInitializerError at EndPosTable` issue)

## Run order

1. Start infra:
   ```
   docker-compose up -d
   ```
2. Start `eureka-server` first (port 8761) — visit http://localhost:8761 to
   confirm it's up.
3. Start `auth-service` (port 8081).
4. Start `api-gateway` (port 8080) last, once services are registered.

Each module has its own `main` class — run them individually from IntelliJ
(right-click → Run), or `mvn spring-boot:run` inside each module folder.

## Try the auth service

Register:
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Jack Sharma",
  "email": "jack@example.com",
  "password": "password123"
}
```

Login:
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "jack@example.com",
  "password": "password123"
}
```

Both return a JWT you'll attach as `Authorization: Bearer <token>` to
protected endpoints in the other services once they're built out.

## Build order (what's next)

1. ✅ Auth service — JWT register/login
2. Product service — CRUD + PostgreSQL, publishes view/purchase events to Kafka
3. Cart service — Redis-backed
4. Order service — consumes cart, publishes order events to Kafka
5. Payment service — mock gateway integration
6. Recommendation service — consumes Kafka events, does collaborative
   filtering, then calls an LLM to re-rank + explain picks

## Common gotchas already handled here

- Parent POM pins Spring Boot 3.3.4 + Spring Cloud 2023.0.3 — these versions
  are compatible with each other and with Java 21.
- Lombok is pinned to 1.18.34 in every module's `pom.xml`, not left to
  whatever version the Spring Initializr default pulls in.
- `application.yml` only (no mixing with `.properties` files) to avoid
  syntax collisions.
