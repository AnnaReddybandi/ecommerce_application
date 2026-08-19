# E-Commerce REST API Backend

Spring Boot REST API for the ShopAdmin application. It manages customers, products, shopping carts, cart items, orders, order items, and payments.

## Stack
- Java 25
- Spring Boot 3.4.13
- Spring Web and Spring Data JPA
- Hibernate
- MySQL
- Maven
- Lombok
- Jakarta Validation
- SpringDoc OpenAPI / Swagger
- Spring Boot Actuator

## Requirements

- JDK 25 installed
- `JAVA_HOME` configured
- MySQL running on `localhost:3306`
- Database named `ecommerce_db`

The default development database settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
server.port=8080
```

Change the credentials before using this outside local development.

## Run

From this directory:

```powershell
mvn spring-boot:run
```

If PowerShell blocks the wrapper, use:

```powershell
mvn.cmd spring-boot:run
```

The API base URL is:

```text
http://localhost:8080/api/v1
```

## Verify

```powershell
mvn test
mvn clean package
```

Health check:

```text
http://localhost:8080/actuator/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Business Workflow

The supported purchase flow is:

```text
Customer
  -> Shopping Cart
  -> Cart Item
  -> Checkout
  -> Order + Order Items + Pending Payment
  -> Process Payment
  -> Successful Payment + Confirmed Order
```

### 1. Customer

`CustomerRequestDto` requires:

```json
{
  "name": "Mohan",
  "email": "mohan@example.com",
  "phone": "9876543210",
  "address": "12 Main Street"
}
```

Endpoints:

- `POST /customers`
- `GET /customers`
- `GET /customers/{id}`
- `PUT /customers/{id}`
- `DELETE /customers/{id}`

### 2. Product

`ProductRequestDto` requires a non-empty name, price greater than zero, non-negative stock, and a valid `ProductCategory`.

Endpoints:

- `POST /products`
- `GET /products`
- `GET /products/{id}`
- `PUT /products/{id}`
- `DELETE /products/{id}`
- `POST /products/{id}/reduce-stock?quantity=...`
- `POST /products/{id}/increase-stock?quantity=...`
- `POST /products/{id}/image`

### 3. Shopping Cart

Create a cart for a customer:

```json
{ "customerId": 1 }
```

Endpoints:

- `POST /shopping-carts`
- `GET /shopping-carts`
- `GET /shopping-carts/customer/{customerId}`
- `DELETE /shopping-carts/{id}/clear`
- `DELETE /shopping-carts/{id}`

### 4. Cart Items

Add a product to a cart:

```json
{
  "cartId": 1,
  "productId": 5,
  "quantity": 2
}
```

Endpoints:

- `POST /cart-items`
- `GET /cart-items/cart/{cartId}`
- `PUT /cart-items/{id}`
- `DELETE /cart-items/{id}`

The service validates available product stock and merges repeated additions of the same product into one cart item.

### 5. Checkout

Checkout uses the customer ID to find that customer's cart. It does not accept cart items in the request.

```json
{
  "customerId": 1,
  "shippingAddress": "12 Main Street",
  "paymentMethod": "CARD",
  "notes": "Leave at reception"
}
```

Endpoint:

```text
POST /orders/checkout
```

Checkout transactionally:

1. Validates the customer and cart.
2. Rejects an empty cart.
3. Validates stock for every cart item.
4. Creates the order and order items.
5. Reduces product stock.
6. Creates one pending payment for the order.
7. Generates a transaction ID for non-cash methods.
8. Clears the cart.

### 6. Payment Processing

Checkout creates the payment with status `PENDING`. Process it after the payment provider or demo payment step succeeds:

```text
POST /payments/{id}/process
```

Processing changes:

```text
Payment: PENDING -> SUCCESS
Order:   PENDING -> CONFIRMED
```

Payment methods:

- `CARD`
- `UPI`
- `CASH_ON_DELIVERY`
- `NET_BANKING`

Payment endpoints:

- `GET /payments`
- `GET /payments/{id}`
- `GET /payments/order/{orderId}`
- `POST /payments/{id}/process`
- `DELETE /payments/{id}`

## Error Responses

Validation and business errors use a JSON response similar to:

```json
{
  "timestamp": "2026-08-19T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/orders/checkout",
  "errors": {
    "shippingAddress": "Shipping address is required"
  }
}
```

The global exception handler also covers missing resources, duplicate resources, insufficient stock, invalid orders, illegal state, and database constraint errors.

## Configuration Notes

- Product uploads use `file.upload.dir=uploads/products/`.
- Logs are written to `logs/ecommerce-rest-api.log`.
- Scheduler jobs clean old carts and cancel eligible orders according to the configured rates.
- This project is not production-secure yet: add authentication, authorization, secret management, database migrations, and a real payment provider before deployment.
