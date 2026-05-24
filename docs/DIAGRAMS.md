# 60-Minute Cart Diagrams

This document provides architecture, flow, sequence, and security diagrams for the implemented Spring Boot backend.

## 1) Architecture Diagram
```mermaid
flowchart TB
    subgraph Client[Client Layer]
      UI[UI Prototype / Web App]
      SW[Swagger UI]
    end

    subgraph API[Spring Boot API Layer]
      PC[ProductController]
      CC[CartController]
      OC[OrderController]
      GEH[GlobalExceptionHandler]
    end

    subgraph Service[Service Layer]
      PS[ProductService]
      CS[CartService]
      OS[OrderService]
      DI[DataInitializer]
    end

    subgraph Data[Persistence Layer]
      PR[ProductRepository]
      CR[CartRepository]
      CIR[CartItemRepository]
      OR[OrderRepository]
      UR[UserRepository]
    end

    subgraph DB[Database]
      H2[(H2 In-Memory DB)]
      ENT[Entities: User, Product, Cart, CartItem, Order]
    end

    UI --> PC
    UI --> CC
    UI --> OC
    SW --> PC
    SW --> CC
    SW --> OC

    PC --> PS
    CC --> CS
    OC --> OS

    PS --> PR
    CS --> CR
    CS --> CIR
    CS --> PR
    CS --> UR
    OS --> OR
    OS --> CR
    OS --> UR

    DI --> PR
    DI --> UR

    PR --> H2
    CR --> H2
    CIR --> H2
    OR --> H2
    UR --> H2
    ENT --> H2
    GEH -. handles exceptions for .-> API
```

Raw Mermaid source: `docs/diagrams/architecture.mmd`

## 2) Flow Diagram
```mermaid
flowchart TD
    A[Client starts 60-Minute Cart flow] --> B[GET /api/products/essentials]
    B --> C[Display essentials cards in UI]

    C --> D{User action}
    D -->|Pre-fill| E[POST /api/cart/pre-fill]
    D -->|Add/Update item| F[POST /api/cart/items]

    E --> G[CartService loads user and essentials]
    F --> H[CartService upserts cart item]

    G --> I[Cart updated in H2]
    H --> I

    I --> J[POST /api/orders]
    J --> K[OrderService calculates total]
    K --> L[Set estimatedCompletionTime = now + 60 min]
    L --> M[Persist order with PLACED status]

    M --> N[GET /api/orders/{id}/track]
    N --> O{Elapsed time since order placement}
    O -->|< 20 min| P[PLACED]
    O -->|20-44 min| Q[PICKING_ESSENTIALS]
    O -->|45-59 min| R[READY_FOR_PICKUP]
    O -->|>= 60 min| S[DELIVERED]

    P --> T[Return trackerStage + remainingSeconds]
    Q --> T
    R --> T
    S --> T
```

Raw Mermaid source: `docs/diagrams/flow.mmd`

## 3) Sequence Diagram
```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant C as CartController
    participant CS as CartService
    participant PRepo as ProductRepository
    participant CartRepo as CartRepository
    participant O as OrderController
    participant OS as OrderService
    participant ORepo as OrderRepository

    U->>C: POST /api/cart/pre-fill { userId }
    C->>CS: preFillCart(userId)
    CS->>CartRepo: findByUserId(userId)
    CS->>PRepo: findByEssentialTrue()
    CS->>CartRepo: save cart/items
    C-->>U: 201 Created + Cart

    U->>O: POST /api/orders { userId }
    O->>OS: placeOrder(userId)
    OS->>CartRepo: findByUserId(userId)
    OS->>OS: compute total + ETA(now + 60)
    OS->>ORepo: save(order)
    O-->>U: 201 Created + Order

    loop tracker polling
      U->>O: GET /api/orders/{id}/track
      O->>OS: trackOrder(orderId)
      OS->>ORepo: findById(orderId)
      OS->>OS: derive stage from elapsed time
      OS->>ORepo: save(status if changed)
      O-->>U: 200 OK + status/stage/countdown
    end
```

Raw Mermaid source: `docs/diagrams/sequence.mmd`

## 4) Security Diagram
```mermaid
flowchart TB
    subgraph TrustBoundary1[Public Client Boundary]
      Client[Client / UI]
    end

    subgraph TrustBoundary2[Application Boundary - Spring Boot]
      Controllers[REST Controllers]
      Validation[Bean Validation @Valid/@NotNull/@Min]
      ExceptionHandler[GlobalExceptionHandler]
      Services[Business Services]
    end

    subgraph TrustBoundary3[Data Boundary]
      JPA[JPA Repositories]
      H2[(H2 In-Memory DB)]
    end

    Client -->|HTTP JSON| Controllers
    Controllers --> Validation
    Validation --> Services
    Services --> JPA
    JPA --> H2

    Controllers --> ExceptionHandler

    Threat1[[Input Tampering]] -. mitigated by .-> Validation
    Threat2[[Unhandled Errors / Info Leakage]] -. mitigated by .-> ExceptionHandler
    Threat3[[SQL Injection]] -. mitigated by .-> JPA
    Threat4[[Unauthorized Access]] -. gap: no authn/authz in current code .-> Controllers
    Threat5[[Sensitive data in prod memory DB]] -. gap: H2 only for demo/dev .-> H2
```

Raw Mermaid source: `docs/diagrams/security.mmd`
