# E-commerce REST API

A complete **E-commerce REST API** built using **Java, Spring Boot, Spring Data JPA, Hibernate, MySQL and REST principles**.

This project demonstrates real-world backend development concepts including:

* REST API development
* Layered architecture
* Entity relationships
* DTO pattern
* Request DTO / Response DTO
* Jakarta Validation
* JPQL queries
* Native SQL queries
* Named queries
* Enum classes
* Custom exceptions
* Global exception handling
* JPA auditing
* Transaction management
* Scheduler
* Swagger / OpenAPI
* Logging
* API versioning
* Spring Boot Actuator
* Lombok
* Product image upload
* MySQL
* Complete CRUD operations
* Shopping cart and checkout workflow
* Payment management

---

# 1. Project Overview

## Project Name

**E-commerce REST API**

## Technology Stack

| Technology           | Version / Usage           |
| -------------------- | ------------------------- |
| Java                 | 25                        |
| Spring Boot          | 3.4.13                    |
| Spring Web           | REST API                  |
| Spring Data JPA      | Database operations       |
| Hibernate            | ORM                       |
| MySQL                | Relational database       |
| Maven                | Build management          |
| Lombok               | 1.18.38                   |
| Jakarta Validation   | Request validation        |
| Swagger / OpenAPI    | 2.8.17 (SpringDoc)        |
| Spring Boot Actuator | Application monitoring    |
| Spring Boot DevTools | Hot reload (development)  |
| SLF4J                | Logging                   |
| JPA Auditing         | `createdAt` / `updatedAt` |

---

# 2. Business Context

This REST API represents the backend of an e-commerce platform.

The application manages:

* Customers
* Products
* Shopping Carts
* Cart Items
* Orders
* Order Items
* Payments

The primary business flow is:

```text
Customer
   ↓
Product
   ↓
Shopping Cart
   ↓
Cart Item
   ↓
Checkout
   ↓
Order
   ↓
Order Items
   ↓
Payment
   ↓
Order Confirmation
```

The API can later be integrated with:

* React
* Angular
* Vue
* Android / iOS applications
* Other REST API consumers

---

# 3. Main Modules

The project contains seven major database modules:

```text
1. Customer
2. Product
3. ShoppingCart
4. CartItem
5. Order
6. OrderItem
7. Payment
```

---

# 4. Database Design

## 4.1 Customers

```text
customers
------------------------------------------------
id              BIGINT PRIMARY KEY
name            VARCHAR(100)
email           VARCHAR
phone           VARCHAR(20)
address         VARCHAR(255)
created_at      DATETIME
updated_at      DATETIME
```

Stores customer information.

---

## 4.2 Products

```text
products
------------------------------------------------
id              BIGINT PRIMARY KEY
name            VARCHAR(255)
description     VARCHAR(1000)
price           DECIMAL(10,2)
stock           INTEGER
category        VARCHAR
status          VARCHAR
image_url       VARCHAR
created_at      DATETIME
updated_at      DATETIME
```

Stores products available in the e-commerce system.

---

## 4.3 Shopping Carts

```text
shopping_carts
------------------------------------------------
id              BIGINT PRIMARY KEY
customer_id     BIGINT FOREIGN KEY
created_at      DATETIME
updated_at      DATETIME
```

Stores shopping carts belonging to customers.

---

## 4.4 Cart Items

```text
cart_items
------------------------------------------------
id              BIGINT PRIMARY KEY
cart_id         BIGINT FOREIGN KEY
product_id      BIGINT FOREIGN KEY
quantity        INTEGER
created_at      DATETIME
updated_at      DATETIME
```

Stores products added to shopping carts.

---

## 4.5 Orders

```text
orders
------------------------------------------------
id                  BIGINT PRIMARY KEY
customer_id         BIGINT FOREIGN KEY
order_date          DATETIME
status              VARCHAR
shipping_address    VARCHAR(255)
total_amount        DECIMAL(10,2)
created_at          DATETIME
updated_at          DATETIME
```

Stores customer orders.

---

## 4.6 Order Items

```text
order_items
------------------------------------------------
id              BIGINT PRIMARY KEY
order_id        BIGINT FOREIGN KEY
product_id      BIGINT FOREIGN KEY
quantity        INTEGER
price           DECIMAL(10,2)
created_at      DATETIME
updated_at      DATETIME
```

Stores individual products belonging to an order.

Example:

```text
Order #1001

Product A → quantity 2 → price ₹500
Product B → quantity 1 → price ₹1000
```

Total:

```text
2 × ₹500 + 1 × ₹1000 = ₹2000
```

---

## 4.7 Payments

```text
payments
------------------------------------------------
id              BIGINT PRIMARY KEY
order_id        BIGINT FOREIGN KEY
payment_date    DATETIME
amount          DECIMAL(10,2)
method          VARCHAR
status          VARCHAR
transaction_id  VARCHAR
notes           VARCHAR
created_at      DATETIME
updated_at      DATETIME
```

Stores payment information associated with orders.

---

# 5. Entity Relationships

```text
Customer
   │
   ├───────────────< Order
   │                    │
   │                    ├──────< OrderItem >──── Product
   │                    │
   │                    └────── Payment
   │
   └───────────────< ShoppingCart
                        │
                        └──────< CartItem >──── Product
```

## Relationships

### Customer → Order

```text
Customer 1 ─────── * Order
```

### Customer → ShoppingCart

```text
Customer 1 ─────── * ShoppingCart
```

### ShoppingCart → CartItem

```text
ShoppingCart 1 ─────── * CartItem
```

### Product → CartItem

```text
Product 1 ─────── * CartItem
```

### Order → OrderItem

```text
Order 1 ─────── * OrderItem
```

### Product → OrderItem

```text
Product 1 ─────── * OrderItem
```

Therefore:

```text
Order ↔ Product
```

is effectively a many-to-many relationship through `OrderItem`.

### Order → Payment

```text
Order 1 ─────── 1 Payment
```

---

# 6. Project Architecture

The application follows a layered architecture.

## Request Flow

```text
Client
  ↓
Controller
  ↓
Request DTO
  ↓
Service
  ↓
Repository
  ↓
Entity
  ↓
MySQL
```

## Response Flow

```text
MySQL
  ↓
Entity
  ↓
Service
  ↓
Response DTO
  ↓
Controller
  ↓
Client
```

---

# 7. Package Structure

```text
com.example.ecommerce
│
├── EcommerceApplication.java
│
├── config
│   └── OpenApiConfig.java
│
├── controller
│   ├── v1
│   │   ├── CustomerController.java
│   │   ├── ProductController.java
│   │   ├── OrderController.java
│   │   ├── OrderItemController.java
│   │   ├── ShoppingCartController.java
│   │   ├── CartItemController.java
│   │   └── PaymentController.java
│   │
│   └── v2
│       └── ProductV2Controller.java
│
├── dto
│   ├── customer
│   │   ├── CustomerRequestDto.java
│   │   └── CustomerResponseDto.java
│   │
│   ├── product
│   │   ├── ProductRequestDto.java
│   │   └── ProductResponseDto.java
│   │
│   ├── order
│   │   ├── OrderRequestDto.java
│   │   ├── OrderResponseDto.java
│   │   └── CheckoutRequestDto.java
│   │
│   ├── orderitem
│   │   ├── OrderItemRequestDto.java
│   │   └── OrderItemResponseDto.java
│   │
│   ├── cart
│   │   ├── ShoppingCartRequestDto.java
│   │   └── ShoppingCartResponseDto.java
│   │
│   ├── cartitem
│   │   ├── CartItemRequestDto.java
│   │   └── CartItemResponseDto.java
│   │
│   └── payment
│       ├── PaymentRequestDto.java
│       └── PaymentResponseDto.java
│
├── entity
│   ├── BaseEntity.java
│   ├── Customer.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── ShoppingCart.java
│   ├── CartItem.java
│   ├── Payment.java
│   │
│   ├── ProductCategory.java
│   ├── ProductStatus.java
│   ├── OrderStatus.java
│   ├── PaymentMethod.java
│   └── PaymentStatus.java
│
├── repository
│   ├── CustomerRepository.java
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   ├── OrderItemRepository.java
│   ├── ShoppingCartRepository.java
│   ├── CartItemRepository.java
│   └── PaymentRepository.java
│
├── service
│   ├── CustomerService.java
│   ├── ProductService.java
│   ├── OrderService.java
│   ├── OrderItemService.java
│   ├── ShoppingCartService.java
│   ├── CartItemService.java
│   └── PaymentService.java
│
├── service/impl
│   ├── CustomerServiceImpl.java
│   ├── ProductServiceImpl.java
│   ├── OrderServiceImpl.java
│   ├── OrderItemServiceImpl.java
│   ├── ShoppingCartServiceImpl.java
│   ├── CartItemServiceImpl.java
│   └── PaymentServiceImpl.java
│
├── exception
│   ├── ApiError.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   ├── InsufficientStockException.java
│   ├── InvalidOrderException.java
│   └── GlobalExceptionHandler.java
│
├── scheduler
│   ├── OrderScheduler.java
│   └── CartScheduler.java
│
└── util
    └── FileStorageUtil.java
```

---

# 8. Maven Dependencies

The project uses:

```text
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation
spring-boot-starter-actuator
mysql-connector-j
springdoc-openapi-starter-webmvc-ui (2.8.17)
lombok (1.18.38)
spring-boot-devtools (runtime, optional)
h2 (test scope)
spring-boot-starter-test
```

---

# 9. Database Configuration

Database:

```text
ecommerce_db
```

Create it using:

```sql
CREATE DATABASE ecommerce_db;
```

Verify:

```sql
SHOW DATABASES;
```

Select:

```sql
USE ecommerce_db;
```

Example configuration:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_db
    username: root
    password: root

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

Change the username and password according to your local MySQL installation.

---

# 10. Base Entity and Auditing

All major entities extend:

```java
BaseEntity
```

The base entity contains:

```text
id
createdAt
updatedAt
```

JPA auditing is enabled using:

```java
@EnableJpaAuditing
```

The application uses:

```java
@CreatedDate
@LastModifiedDate
```

to automatically maintain audit timestamps.

---

# 11. Enum Classes

## ProductCategory

```text
ELECTRONICS
FASHION
HOME
BOOKS
BEAUTY
SPORTS
GROCERY
```

## ProductStatus

```text
ACTIVE
INACTIVE
```

## OrderStatus

```text
PENDING
CONFIRMED
SHIPPED
DELIVERED
CANCELLED
```

## PaymentMethod

```text
CASH_ON_DELIVERY
CARD
UPI
NET_BANKING
```

## PaymentStatus

```text
PENDING
SUCCESS
FAILED
REFUNDED
```

---

# 12. DTO Architecture

Entities are not directly exposed through REST APIs.

## Request

```text
Client
  ↓
Request DTO
  ↓
Controller
  ↓
Service
  ↓
Entity
```

## Response

```text
Entity
  ↓
Service
  ↓
Response DTO
  ↓
Controller
  ↓
Client
```

Example:

```java
public record ProductRequestDto(
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    ProductCategory category
) {}
```

Response:

```java
public record ProductResponseDto(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    ProductCategory category
) {}
```

---

# 13. Validation

The application uses Jakarta Validation.

Examples:

```java
@NotBlank
@NotNull
@Email
@Pattern
@Min
@DecimalMin
```

Controllers use:

```java
@Valid
@RequestBody
```

Example:

```json
{
  "name": "",
  "price": -100,
  "stock": -5
}
```

Expected response:

```text
400 Bad Request
```

---

# 14. Custom Exceptions

The project contains:

### ResourceNotFoundException

Used when a resource does not exist.

Example:

```text
Product with ID 100 not found
```

### DuplicateResourceException

Used for duplicate resources.

Example:

```text
Customer email already exists
```

### InsufficientStockException

Used when requested quantity exceeds available stock.

### InvalidOrderException

Used for invalid order operations.

Example:

```text
Cannot cancel a delivered order
```

---

# 15. Global Exception Handling

The application uses:

```java
@RestControllerAdvice
```

to centrally handle exceptions.

Supported responses include:

```text
400 Bad Request
404 Not Found
409 Conflict
500 Internal Server Error
```

Example:

```json
{
  "timestamp": "2026-08-17T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found: 100",
  "path": "/api/v1/products/100"
}
```

---

# 16. Repository Layer

Repositories extend:

```java
JpaRepository<Entity, Long>
```

Example:

```java
public interface ProductRepository
        extends JpaRepository<Product, Long> {
}
```

Spring Data JPA provides:

```text
save()
findAll()
findById()
delete()
deleteById()
count()
existsById()
```

---

# 17. Query Types

The project demonstrates three query approaches.

## JPQL

```java
@Query("""
    SELECT p
    FROM Product p
    WHERE p.price BETWEEN :min AND :max
""")
```

JPQL uses entity names and entity fields.

---

## Native SQL

```java
@Query(
    value = "SELECT * FROM products WHERE stock > :minimumStock",
    nativeQuery = true
)
```

Native SQL uses actual database tables and columns.

---

## Named Query

The `Product` entity contains:

```java
@NamedQuery(
    name = "Product.findActiveByCategoryNamed",
    query = """
        SELECT p
        FROM Product p
        WHERE p.category = :category
        AND p.status =
        com.example.ecommerce.entity.ProductStatus.ACTIVE
    """
)
```

---

# 18. Transaction Management

Checkout is a transactional business operation.

The checkout process includes:

```text
Validate Customer
      ↓
Get Shopping Cart
      ↓
Get Cart Items
      ↓
Validate Products
      ↓
Check Stock
      ↓
Create Order
      ↓
Create Order Items
      ↓
Calculate Total
      ↓
Reduce Product Stock
      ↓
Create Payment
      ↓
Clear Cart
      ↓
Commit Transaction
```

The operation should be wrapped with:

```java
@Transactional
```

If a critical operation fails:

```text
ROLLBACK
```

This prevents partially completed orders.

---

# 19. Scheduler

Spring Scheduling is used for background tasks.

Enable scheduling:

```java
@EnableScheduling
```

Example use cases:

### Expired Order Cancellation

```text
PENDING
   ↓
Expired
   ↓
CANCELLED
```

### Abandoned Cart Processing

```text
Cart not updated
       ↓
Configured period exceeded
       ↓
Process abandoned cart
```

---

# 20. Logging

The project uses Lombok:

```java
@Slf4j
```

Example:

```java
log.info("Creating product: {}", request.name());
```

Recommended levels:

```text
INFO   → Important application events
DEBUG  → Development/debug information
WARN   → Potential problems
ERROR  → Failures
```

Sensitive information such as payment secrets, card numbers and tokens should not be logged.

---

# 21. Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Swagger allows developers to:

* View APIs
* View request bodies
* View response bodies
* Execute APIs
* Test validation
* Test different HTTP methods

---

# 22. API Versioning

The project uses URI-based API versioning.

## V1

```text
/api/v1/customers
/api/v1/products
/api/v1/shopping-carts
/api/v1/cart-items
/api/v1/orders
/api/v1/order-items
/api/v1/payments
```

## V2

```text
/api/v2/products
```

---

# 23. Complete Postman API Reference

Base URL:

```text
http://localhost:8080
```

---

## 23.1 Customer APIs

| Method | URL                      | Purpose            |
| ------ | ------------------------ | ------------------ |
| POST   | `/api/v1/customers`      | Create customer    |
| GET    | `/api/v1/customers`      | Get all customers  |
| GET    | `/api/v1/customers/{id}` | Get customer by ID |
| PUT    | `/api/v1/customers/{id}` | Update customer    |
| DELETE | `/api/v1/customers/{id}` | Delete customer    |

### Full URLs

```text
POST   http://localhost:8080/api/v1/customers
GET    http://localhost:8080/api/v1/customers
GET    http://localhost:8080/api/v1/customers/{id}
PUT    http://localhost:8080/api/v1/customers/{id}
DELETE http://localhost:8080/api/v1/customers/{id}
```

### Create Customer

```json
{
  "name": "Anna Reddy",
  "email": "anna@example.com",
  "phone": "9876543210",
  "address": "Bangalore"
}
```

---

# 24. Product APIs — V1

| Method | URL                                                    | Purpose                 |
| ------ | ------------------------------------------------------ | ----------------------- |
| POST   | `/api/v1/products`                                     | Create product          |
| GET    | `/api/v1/products`                                     | Get all products        |
| GET    | `/api/v1/products/{id}`                                | Get product by ID       |
| PUT    | `/api/v1/products/{id}`                                | Update product          |
| DELETE | `/api/v1/products/{id}`                                | Delete product          |
| POST   | `/api/v1/products/{id}/reduce-stock?quantity=5`        | Reduce stock            |
| POST   | `/api/v1/products/{id}/increase-stock?quantity=5`      | Increase stock          |
| POST   | `/api/v1/products/{id}/image`                          | Upload image            |
| GET    | `/api/v1/products/query/stock?minimumStock=10`         | Query minimum stock     |
| GET    | `/api/v1/products/query/price-range?min=100&max=1000`  | Query price range       |
| GET    | `/api/v1/products/query/category?category=ELECTRONICS` | Query category          |
| GET    | `/api/v1/products/price-range?min=100&max=1000`        | Find by price range     |
| GET    | `/api/v1/products/low-stock/10`                        | Find low-stock products |
| GET    | `/api/v1/products/category/ELECTRONICS`                | Find by category        |

### Full URLs

```text
POST   http://localhost:8080/api/v1/products
GET    http://localhost:8080/api/v1/products
GET    http://localhost:8080/api/v1/products/{id}
PUT    http://localhost:8080/api/v1/products/{id}
DELETE http://localhost:8080/api/v1/products/{id}

POST   http://localhost:8080/api/v1/products/{id}/reduce-stock?quantity=5
POST   http://localhost:8080/api/v1/products/{id}/increase-stock?quantity=5

POST   http://localhost:8080/api/v1/products/{id}/image

GET    http://localhost:8080/api/v1/products/query/stock?minimumStock=10
GET    http://localhost:8080/api/v1/products/query/price-range?min=100&max=1000
GET    http://localhost:8080/api/v1/products/query/category?category=ELECTRONICS

GET    http://localhost:8080/api/v1/products/price-range?min=100&max=1000
GET    http://localhost:8080/api/v1/products/low-stock/10
GET    http://localhost:8080/api/v1/products/category/ELECTRONICS
```

### Create Product

```json
{
  "name": "Mechanical Keyboard",
  "description": "RGB Mechanical Keyboard",
  "price": 2499.00,
  "stock": 50,
  "category": "ELECTRONICS"
}
```

---

# 25. Product APIs — V2

| Method | URL                                               | Purpose          |
| ------ | ------------------------------------------------- | ---------------- |
| POST   | `/api/v2/products`                                | Create product   |
| GET    | `/api/v2/products`                                | Get all products |
| GET    | `/api/v2/products/{id}`                           | Get product      |
| PUT    | `/api/v2/products/{id}`                           | Update product   |
| DELETE | `/api/v2/products/{id}`                           | Delete product   |
| POST   | `/api/v2/products/{id}/reduce-stock?quantity=5`   | Reduce stock     |
| POST   | `/api/v2/products/{id}/increase-stock?quantity=5` | Increase stock   |
| POST   | `/api/v2/products/{id}/image`                     | Upload image     |
| GET    | `/api/v2/products/price-range?min=100&max=1000`   | Price range      |
| GET    | `/api/v2/products/low-stock/10`                   | Low stock        |
| GET    | `/api/v2/products/category/ELECTRONICS`           | Category         |

### Full URLs

```text
POST   http://localhost:8080/api/v2/products
GET    http://localhost:8080/api/v2/products
GET    http://localhost:8080/api/v2/products/{id}
PUT    http://localhost:8080/api/v2/products/{id}
DELETE http://localhost:8080/api/v2/products/{id}

POST   http://localhost:8080/api/v2/products/{id}/reduce-stock?quantity=5
POST   http://localhost:8080/api/v2/products/{id}/increase-stock?quantity=5

POST   http://localhost:8080/api/v2/products/{id}/image

GET    http://localhost:8080/api/v2/products/price-range?min=100&max=1000
GET    http://localhost:8080/api/v2/products/low-stock/10
GET    http://localhost:8080/api/v2/products/category/ELECTRONICS
```

---

# 26. Shopping Cart APIs

The actual controller uses:

```text
/api/v1/shopping-carts
```

**Note:** This is the correct path. Do not use `/api/v1/carts`.

| Method | URL                                            | Purpose             |
| ------ | ---------------------------------------------- | ------------------- |
| POST   | `/api/v1/shopping-carts`                       | Create cart         |
| GET    | `/api/v1/shopping-carts`                       | Get all carts       |
| GET    | `/api/v1/shopping-carts/{id}`                  | Get cart            |
| GET    | `/api/v1/shopping-carts/customer/{customerId}` | Get customer's cart |
| DELETE | `/api/v1/shopping-carts/{id}`                  | Delete cart         |
| DELETE | `/api/v1/shopping-carts/{id}/clear`            | Clear cart          |

### Full URLs

```text
POST   http://localhost:8080/api/v1/shopping-carts
GET    http://localhost:8080/api/v1/shopping-carts
GET    http://localhost:8080/api/v1/shopping-carts/{id}

GET    http://localhost:8080/api/v1/shopping-carts/customer/{customerId}

DELETE http://localhost:8080/api/v1/shopping-carts/{id}

DELETE http://localhost:8080/api/v1/shopping-carts/{id}/clear
```

---

# 27. Cart Item APIs

| Method | URL                                | Purpose            |
| ------ | ---------------------------------- | ------------------ |
| POST   | `/api/v1/cart-items`               | Add cart item      |
| GET    | `/api/v1/cart-items`               | Get all cart items |
| GET    | `/api/v1/cart-items/{id}`          | Get cart item      |
| GET    | `/api/v1/cart-items/cart/{cartId}` | Get items by cart  |
| PUT    | `/api/v1/cart-items/{id}`          | Update quantity    |
| DELETE | `/api/v1/cart-items/{id}`          | Delete cart item   |

### Full URLs

```text
POST   http://localhost:8080/api/v1/cart-items
GET    http://localhost:8080/api/v1/cart-items
GET    http://localhost:8080/api/v1/cart-items/{id}

GET    http://localhost:8080/api/v1/cart-items/cart/{cartId}

PUT    http://localhost:8080/api/v1/cart-items/{id}
DELETE http://localhost:8080/api/v1/cart-items/{id}
```

---

# 28. Order APIs

| Method | URL                                    | Purpose                |
| ------ | -------------------------------------- | ---------------------- |
| POST   | `/api/v1/orders`                       | Create order           |
| GET    | `/api/v1/orders`                       | Get all orders         |
| GET    | `/api/v1/orders/{id}`                  | Get order              |
| PUT    | `/api/v1/orders/{id}`                  | Update order           |
| DELETE | `/api/v1/orders/{id}`                  | Delete order           |
| POST   | `/api/v1/orders/{id}/confirm`          | Confirm order          |
| POST   | `/api/v1/orders/{id}/cancel`           | Cancel order           |
| POST   | `/api/v1/orders/checkout`              | Checkout shopping cart |
| GET    | `/api/v1/orders/status/{status}`       | Get orders by status   |
| GET    | `/api/v1/orders/customer/{customerId}` | Get customer orders    |

### Full URLs

```text
POST   http://localhost:8080/api/v1/orders
GET    http://localhost:8080/api/v1/orders
GET    http://localhost:8080/api/v1/orders/{id}
PUT    http://localhost:8080/api/v1/orders/{id}
DELETE http://localhost:8080/api/v1/orders/{id}

POST   http://localhost:8080/api/v1/orders/{id}/confirm
POST   http://localhost:8080/api/v1/orders/{id}/cancel

POST   http://localhost:8080/api/v1/orders/checkout

GET    http://localhost:8080/api/v1/orders/status/PENDING
GET    http://localhost:8080/api/v1/orders/customer/{customerId}
```

Valid statuses:

```text
PENDING
CONFIRMED
SHIPPED
DELIVERED
CANCELLED
```

---

# 29. Order Item APIs

| Method | URL                                   | Purpose             |
| ------ | ------------------------------------- | ------------------- |
| POST   | `/api/v1/order-items`                 | Create order item   |
| GET    | `/api/v1/order-items`                 | Get all order items |
| GET    | `/api/v1/order-items/{id}`            | Get order item      |
| GET    | `/api/v1/order-items/order/{orderId}` | Get items by order  |
| PUT    | `/api/v1/order-items/{id}`            | Update order item   |
| DELETE | `/api/v1/order-items/{id}`            | Delete order item   |

### Full URLs

```text
POST   http://localhost:8080/api/v1/order-items
GET    http://localhost:8080/api/v1/order-items
GET    http://localhost:8080/api/v1/order-items/{id}

GET    http://localhost:8080/api/v1/order-items/order/{orderId}

PUT    http://localhost:8080/api/v1/order-items/{id}
DELETE http://localhost:8080/api/v1/order-items/{id}
```

---

# 30. Payment APIs

| Method | URL                                | Purpose                |
| ------ | ---------------------------------- | ---------------------- |
| POST   | `/api/v1/payments`                 | Create payment         |
| GET    | `/api/v1/payments`                 | Get all payments       |
| GET    | `/api/v1/payments/{id}`            | Get payment            |
| PUT    | `/api/v1/payments/{id}`            | Update payment         |
| DELETE | `/api/v1/payments/{id}`            | Delete payment         |
| POST   | `/api/v1/payments/{id}/process`    | Process payment        |
| GET    | `/api/v1/payments/status/{status}` | Get payments by status |
| GET    | `/api/v1/payments/order/{orderId}` | Get payment by order   |

### Full URLs

```text
POST   http://localhost:8080/api/v1/payments
GET    http://localhost:8080/api/v1/payments
GET    http://localhost:8080/api/v1/payments/{id}
PUT    http://localhost:8080/api/v1/payments/{id}
DELETE http://localhost:8080/api/v1/payments/{id}

POST   http://localhost:8080/api/v1/payments/{id}/process

GET    http://localhost:8080/api/v1/payments/status/PENDING
GET    http://localhost:8080/api/v1/payments/order/{orderId}
```

Valid payment statuses:

```text
PENDING
SUCCESS
FAILED
REFUNDED
```

Valid payment methods:

```text
CASH_ON_DELIVERY
CARD
UPI
NET_BANKING
```

---

# 31. Correct Postman End-to-End Testing Sequence

The recommended primary business flow is:

```text
1. Create Customer
       ↓
2. Create Product
       ↓
3. Create Shopping Cart
       ↓
4. Add Cart Item
       ↓
5. Get Cart
       ↓
6. Checkout
       ↓
7. Get Order
       ↓
8. Get Payment / Process Payment
       ↓
9. Confirm Order
```

## Step 1 — Create Customer

```text
POST
http://localhost:8080/api/v1/customers
```

Example:

```json
{
  "name": "Anna Reddy",
  "email": "anna@example.com",
  "phone": "9876543210",
  "address": "Bangalore"
}
```

Save the returned:

```text
customerId
```

---

## Step 2 — Create Product

```text
POST
http://localhost:8080/api/v1/products
```

Example:

```json
{
  "name": "Mechanical Keyboard",
  "description": "RGB Mechanical Keyboard",
  "price": 2499.00,
  "stock": 50,
  "category": "ELECTRONICS"
}
```

Save:

```text
productId
```

---

## Step 3 — Create Shopping Cart

```text
POST
http://localhost:8080/api/v1/shopping-carts
```

Use the customer ID created earlier.

Example request will depend on the actual `ShoppingCartRequestDto`.

Save:

```text
cartId
```

---

## Step 4 — Add Cart Item

```text
POST
http://localhost:8080/api/v1/cart-items
```

Use:

```text
cartId
productId
quantity
```

Example request will depend on your `CartItemRequestDto`.

Save:

```text
cartItemId
```

---

## Step 5 — Get Cart

```text
GET
http://localhost:8080/api/v1/shopping-carts/{cartId}
```

Example:

```text
GET http://localhost:8080/api/v1/shopping-carts/1
```

Verify:

```text
Customer
Cart
Cart Items
Product
Quantity
```

---

## Step 6 — Checkout

```text
POST
http://localhost:8080/api/v1/orders/checkout
```

Use your `CheckoutRequestDto`.

The checkout operation should perform:

```text
Validate Customer
       ↓
Get Cart
       ↓
Get Cart Items
       ↓
Validate Stock
       ↓
Create Order
       ↓
Create Order Items
       ↓
Calculate Total
       ↓
Reduce Stock
       ↓
Create Payment
       ↓
Clear Cart
```

Save:

```text
orderId
```

and, if returned:

```text
paymentId
```

---

## Step 7 — Get Order

```text
GET
http://localhost:8080/api/v1/orders/{orderId}
```

Example:

```text
GET http://localhost:8080/api/v1/orders/1
```

Verify:

```text
Customer
Order Date
Status
Shipping Address
Total Amount
Order Items
Payment
```

---

## Step 8 — Process Payment

If checkout creates a payment:

```text
POST
http://localhost:8080/api/v1/payments/{paymentId}/process
```

Example:

```text
POST http://localhost:8080/api/v1/payments/1/process
```

Then verify:

```text
Payment Status = SUCCESS
```

and the associated order should be updated according to your service implementation.

---

## Step 9 — Confirm Order

```text
POST
http://localhost:8080/api/v1/orders/{orderId}/confirm
```

Example:

```text
POST http://localhost:8080/api/v1/orders/1/confirm
```

Expected:

```text
Order Status = CONFIRMED
```

---

# 32. Product Image Upload

Endpoint:

```text
POST
http://localhost:8080/api/v1/products/{id}/image
```

In Postman:

```text
Body
  ↓
form-data
  ↓
Key: file
  ↓
Type: File
  ↓
Select image
```

Example:

```text
POST http://localhost:8080/api/v1/products/1/image
```

The controller expects:

```text
@RequestParam("file") MultipartFile file
```

---

# 33. Query Testing

## Minimum Stock

Native SQL / stock query:

```text
GET http://localhost:8080/api/v1/products/query/stock?minimumStock=10
```

---

## Price Range

```text
GET http://localhost:8080/api/v1/products/query/price-range?min=1000&max=5000
```

---

## Category

```text
GET http://localhost:8080/api/v1/products/query/category?category=ELECTRONICS
```

---

## Price Range Service API

```text
GET http://localhost:8080/api/v1/products/price-range?min=1000&max=5000
```

---

## Low Stock

```text
GET http://localhost:8080/api/v1/products/low-stock/10
```

---

## Category Path Variable

```text
GET http://localhost:8080/api/v1/products/category/ELECTRONICS
```

---

# 34. Stock Management

## Reduce Stock

```text
POST
http://localhost:8080/api/v1/products/1/reduce-stock?quantity=5
```

## Increase Stock

```text
POST
http://localhost:8080/api/v1/products/1/increase-stock?quantity=5
```

---

# 35. Actuator

Health:

```text
GET http://localhost:8080/actuator/health
```

Info:

```text
GET http://localhost:8080/actuator/info
```

Metrics:

```text
GET http://localhost:8080/actuator/metrics
```

Loggers:

```text
GET http://localhost:8080/actuator/loggers
```

Example health response:

```json
{
  "status": "UP"
}
```

---

# 36. HTTP Status Codes

| Status | Meaning               |
| ------ | --------------------- |
| 200    | OK                    |
| 201    | Created               |
| 204    | No Content            |
| 400    | Bad Request           |
| 404    | Not Found             |
| 409    | Conflict              |
| 500    | Internal Server Error |

---

# 37. Example Successful Product Response

```json
{
  "id": 1,
  "name": "Mechanical Keyboard",
  "description": "RGB Mechanical Keyboard",
  "price": 2499.00,
  "stock": 50,
  "category": "ELECTRONICS"
}
```

---

# 38. Example Error Response

```json
{
  "timestamp": "2026-08-17T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found: 100",
  "path": "/api/v1/products/100"
}
```

---

# 39. Running the Application

## Step 1 — Verify Java

```bash
java -version
```

Expected:

```text
Java 25
```

## Step 2 — Verify Maven

```bash
mvn -version
```

## Step 3 — Create Database

```sql
CREATE DATABASE ecommerce_db;
```

## Step 4 — Configure MySQL

Update:

```yaml
spring:
  datasource:
    username: root
    password: root
```

## Step 5 — Build

```bash
mvn clean install
```

Or using the Maven wrapper (no Maven installation required):

```bash
./mvnw clean install
```

## Step 6 — Run

```bash
mvn spring-boot:run
```

Or using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or run directly from your IDE:

```text
EcommerceApplication.java
```

---

# 40. Application URLs

Base URL:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

Actuator:

```text
http://localhost:8080/actuator/health
```

---

# 41. Testing Strategy

The project can be tested using:

```text
Postman
Swagger
JUnit
Mockito
Integration Tests
```

Recommended basic sequence:

```text
Customer
   ↓
Product
   ↓
Shopping Cart
   ↓
Cart Item
   ↓
Checkout
   ↓
Order
   ↓
Payment
```

---

# 42. Error Testing

## Invalid Product ID

```text
GET http://localhost:8080/api/v1/products/99999
```

Expected:

```text
404 Not Found
```

---

## Invalid Product

```text
POST http://localhost:8080/api/v1/products
```

```json
{
  "name": "",
  "price": -100,
  "stock": -5
}
```

Expected:

```text
400 Bad Request
```

---

## Duplicate Customer

Create the same customer email twice.

Expected:

```text
409 Conflict
```

---

# 43. Complete Checkout Example

Suppose:

```text
Customer ID = 1
```

Cart contains:

```text
Product A
Quantity = 2
Price = ₹500

Product B
Quantity = 1
Price = ₹1000
```

Calculation:

```text
Product A
2 × ₹500 = ₹1000

Product B
1 × ₹1000 = ₹1000

Total = ₹2000
```

Expected order:

```text
Order Status = PENDING
Total Amount = ₹2000
```

After successful payment and confirmation:

```text
Payment Status = SUCCESS
Order Status = CONFIRMED
```

---

# 44. API Version Testing

## V1

```text
GET http://localhost:8080/api/v1/products
```

## V2

```text
GET http://localhost:8080/api/v2/products
```

V2 allows the API contract to evolve while existing clients continue using V1.

---

# 45. Git Branch Structure

Recommended branches:

```text
main
develop

feature/customer
feature/product
feature/order
feature/cart
feature/payment
feature/swagger
feature/scheduler
feature/file-upload
```

Example:

```bash
git checkout -b feature/product
git add .
git commit -m "Implement product CRUD APIs"
git push -u origin feature/product
```

---

# 46. Useful Maven Commands

```bash
# Standard Maven
mvn clean
mvn compile
mvn test
mvn package
mvn clean package
mvn spring-boot:run

# Maven Wrapper (no installation required)
./mvnw clean
./mvnw compile
./mvnw test
./mvnw package
./mvnw clean package
./mvnw spring-boot:run
```

---

# 47. Production Improvements

For a production-ready system, consider adding:

```text
Spring Security
JWT Authentication
Role-Based Authorization
Pagination
Sorting
Filtering
Search
Flyway / Liquibase
Docker
Docker Compose
Redis
AWS S3
Payment Gateway
Email Notifications
Kafka / RabbitMQ
Caching
Unit Tests
Integration Tests
CI/CD
Environment-specific configuration
Secrets Management
API Rate Limiting
Database Indexing
```

---

# 48. Important Design Rules

### Rule 1

Do not expose JPA entities directly from controllers.

Use:

```text
Request DTO
Response DTO
```

### Rule 2

Business logic belongs in the Service layer.

### Rule 3

Database operations belong in the Repository layer.

### Rule 4

Controllers should mainly handle:

```text
HTTP Request
HTTP Response
Validation
Routing
```

### Rule 5

Use transactions around business operations such as checkout.

### Rule 6

Use custom exceptions for expected business errors.

### Rule 7

Use enums for fixed business states.

### Rule 8

Use logging instead of:

```java
System.out.println()
```

---

# 49. Final Project Flow

```text
                     E-COMMERCE REST API
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
      Customer             Product             Order
          │                   │                   │
          │                   │              OrderItem
          │                   │                   │
          │                   └───────────────────┘
          │
     ShoppingCart
          │
       CartItem
          │
       Product

        Order
          │
       Payment
```

Application architecture:

```text
                     Client
                       │
                       ▼
                  Controller
                       │
                       ▼
                  Request DTO
                       │
                       ▼
                    Service
                       │
              ┌────────┴────────┐
              │                 │
       Business Logic       Transaction
              │                 │
              └────────┬────────┘
                       ▼
                  Repository
                       │
                       ▼
                     JPA
                       │
                       ▼
                    MySQL
```

Supporting components:

```text
Scheduler
Swagger / OpenAPI
Actuator
Logging
Global Exception Handler
File Storage
```

---

# 50. Recommended Development Order

```text
1. Project Setup
2. Database Configuration
3. Entity Classes
4. Entity Relationships
5. Enum Classes
6. Repository Layer
7. Customer CRUD
8. Product CRUD
9. ShoppingCart
10. CartItem
11. Order CRUD
12. OrderItem
13. Payment
14. Request DTOs
15. Response DTOs
16. Validation
17. Custom Exceptions
18. Global Exception Handler
19. JPQL Queries
20. Native SQL Queries
21. Named Queries
22. Auditing
23. Transaction Management
24. Checkout Transaction
25. Scheduler
26. Logging
27. Swagger
28. API Versioning
29. Actuator
30. Image Upload
31. Postman Testing
32. Unit Tests
33. Integration Tests
34. Final Documentation
```

---

# 51. Final Postman Checklist

```text
Customer
[ ] Create Customer
[ ] Get All Customers
[ ] Get Customer By ID
[ ] Update Customer
[ ] Delete Customer

Product V1
[ ] Create Product
[ ] Get All Products
[ ] Get Product
[ ] Update Product
[ ] Delete Product
[ ] Reduce Stock
[ ] Increase Stock
[ ] Upload Image
[ ] Minimum Stock Query
[ ] Price Range Query
[ ] Category Query
[ ] Low Stock
[ ] Category Path API

Product V2
[ ] Create Product
[ ] Get Products
[ ] Get Product
[ ] Update Product
[ ] Delete Product
[ ] Stock APIs
[ ] Image Upload
[ ] Price Range
[ ] Low Stock
[ ] Category

Shopping Cart
[ ] Create Cart
[ ] Get All Carts
[ ] Get Cart
[ ] Get Cart By Customer
[ ] Clear Cart
[ ] Delete Cart

Cart Items
[ ] Add Cart Item
[ ] Get All Cart Items
[ ] Get Cart Item
[ ] Get Items By Cart
[ ] Update Quantity
[ ] Delete Cart Item

Orders
[ ] Create Order
[ ] Get All Orders
[ ] Get Order
[ ] Update Order
[ ] Delete Order
[ ] Confirm Order
[ ] Cancel Order
[ ] Checkout
[ ] Orders By Status
[ ] Orders By Customer

Order Items
[ ] Create Order Item
[ ] Get All Order Items
[ ] Get Order Item
[ ] Get Items By Order
[ ] Update Order Item
[ ] Delete Order Item

Payments
[ ] Create Payment
[ ] Get All Payments
[ ] Get Payment
[ ] Update Payment
[ ] Delete Payment
[ ] Process Payment
[ ] Payments By Status
[ ] Payment By Order

Supporting Features
[ ] Validation
[ ] Exception Handling
[ ] JPQL
[ ] Native Query
[ ] Named Query
[ ] Auditing
[ ] Transactions
[ ] Scheduler
[ ] Logging
[ ] Swagger
[ ] API Versioning
[ ] Actuator
[ ] Image Upload
```

---

# 52. Final Goal

The completed project demonstrates a complete Spring Boot e-commerce backend using:

```text
Java
+
Spring Boot
+
Spring Web
+
Spring Data JPA
+
Hibernate
+
MySQL
+
DTO
+
Validation
+
Exception Handling
+
JPQL
+
Native SQL
+
Named Query
+
JPA Relationships
+
Enums
+
Auditing
+
Transactions
+
Scheduler
+
Swagger
+
Logging
+
API Versioning
+
Actuator
+
Lombok
+
File Upload
+
Postman Testing
```

The standard development pattern for new modules is:

```text
Entity
   ↓
Repository
   ↓
Request DTO
   ↓
Response DTO
   ↓
Service Interface
   ↓
Service Implementation
   ↓
Controller
   ↓
Exception Handling
   ↓
Swagger
   ↓
Postman Testing
   ↓
README Update
```

---

# 53. Maintenance Notes

Whenever the project is modified:

1. Update the entity if required.
2. Update database relationships if required.
3. Update Request DTO.
4. Update Response DTO.
5. Update Repository queries.
6. Update Service interface.
7. Update Service implementation.
8. Update Controller.
9. Update Swagger documentation.
10. Update validation.
11. Update exception handling.
12. Add/update Postman tests.
13. Update this README.

This README should be maintained as the **living documentation** of the project.

---

# 54. Version History

## Version 1.0.0

Initial E-commerce REST API implementation.

**Stack:** Java 25 · Spring Boot 3.4.13 · MySQL

Included:

* Customer
* Product
* Shopping Cart
* Cart Item
* Order
* Order Item
* Payment
* CRUD APIs
* DTO architecture
* Validation
* Exception handling
* JPQL
* Native queries
* Named queries
* Entity relationships
* Enum classes
* JPA auditing
* Transaction management
* Checkout
* Scheduler
* Logging
* Swagger / OpenAPI (SpringDoc 2.8.17)
* API versioning (V1 & V2)
* Actuator
* Product image upload
* Spring Boot DevTools (hot reload)
* H2 database (test scope)
* Postman API testing documentation

---

# 55. Author / Project Information

**Project:** E-commerce REST API

**Backend:** Spring Boot

**Language:** Java

**Database:** MySQL

**Architecture:** Layered REST Architecture

**API Style:** RESTful API

**Documentation:** Swagger / OpenAPI

**Build Tool:** Maven

---

## License

This project is available for educational and learning purposes.

---

## Contact & Support

For questions or support, refer to the project documentation or open an issue in the repository.
