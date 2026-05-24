# Walmart-60: 60-Minute Cart REST API

Spring Boot backend for a "60-Minute Cart" essentials delivery flow. The API supports product essentials discovery, pre-filled carts, cart item updates, order placement, and real-time order tracking based on a 60-minute completion target.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Web, Spring Data JPA, Validation
- H2 In-Memory Database
- Maven
- Springdoc OpenAPI (Swagger UI)

## Run the application
```bash
mvn spring-boot:run
```

## API & Tooling URLs
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:walmart60db`
  - User: `sa`
  - Password: *(empty)*

## Implemented Endpoints
### Product Catalog
- `GET /api/products/essentials`

### Cart Management
- `POST /api/cart/pre-fill`
- `POST /api/cart/items`

### Orders & Tracking
- `POST /api/orders`
- `GET /api/orders/{id}/track`

## Seed Data
On startup, a default customer and demo products are auto-created, including:
- Cage-Free Eggs (Rollback)
- Whole Milk, 1/2 Gal - `$2.45`
- Fresh Bananas, Bunch - `$1.58`

This makes the API immediately usable for demos.
