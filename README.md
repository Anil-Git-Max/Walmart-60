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

### Orders & Tracking
- `POST /api/orders`
- `GET /api/orders/{id}/track`

## Seed Data
On startup, a default customer and demo products are auto-created, including:
- Cage-Free Eggs (Rollback)
- Whole Milk, 1/2 Gal - `$2.45`
- Fresh Bananas, Bunch - `$1.58`

This makes the API immediately usable for demos.

## Diagrams
- See architecture, flow, sequence, and security diagrams in `docs/DIAGRAMS.md`.
- Raw Mermaid scripts are available under `docs/diagrams/*.mmd`.

## Troubleshooting: Missing Spring dependencies in IDE/compiler
If you see errors such as:
- `package org.springframework.data.jpa.repository does not exist`

Use Maven to resolve dependencies and refresh your project model:

```bash
mvn -U clean compile
```

If using IntelliJ:
1. Open the project as a **Maven** project (not plain Java).
2. In Maven tool window, click **Reload All Maven Projects**.
3. Ensure annotation processing is enabled for Lombok.

If using VS Code:
1. Install **Extension Pack for Java**.
2. Run **Java: Clean Java Language Server Workspace**.
3. Reimport Maven project.


### Sample order payload
```json
{
  "userId": 1,
  "cartId": 1
}
```


> Note: `POST /api/orders` accepts `userId: 0` (or negative) to use the default seeded user automatically.


## Recommended API execution order
For a fresh app run, call APIs in this order to avoid empty-cart errors:

1. **Fetch essentials**
   - `GET /api/products/essentials`
2. **Create/fill cart**
   - `POST /api/cart/pre-fill` with `{ "userId": 1 }` or custom items payload
3. **Place order**
   - `POST /api/orders` with:
   ```json
   {
     "userId": 1,
     "cartId": 1
   }
   ```
4. **Track order**
   - `GET /api/orders/{orderId}/track`

### Behavior update
`POST /api/orders` now auto-creates a cart for the user if one does not already exist, then syncs submitted items into that cart before creating the order.


### Sample pre-fill payload
```json
{
  "userId": 1,
  "items": [
    { "productId": 1, "quantity": 1 },
    { "productId": 2, "quantity": 2 }
  ]
}
```

If `items` is omitted or empty, the API pre-fills with default essentials.

If an item has `productId: 0` (or negative/null), it is ignored; when all provided items are invalid, pre-fill falls back to default essentials.
