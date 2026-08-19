# ShopAdmin Frontend

ShopAdmin is the React administration application for the E-Commerce REST API. It provides a practical operator workflow for maintaining customers and products, building customer carts, checking out orders, processing payments, and printing successful payment bills.

The frontend is intentionally an administration UI. It does not replace authentication, authorization, a payment gateway, or customer-facing storefront behavior.

## Stack

- React 19
- React Router
- Axios
- Vite
- CSS modules through `src/index.css`

## What The Application Does

- Displays resilient dashboard metrics and operational alerts.
- Creates, edits, searches, and deletes customers.
- Creates, edits, searches, deletes, and manages product images.
- Creates a customer cart automatically when the first item is added.
- Adds products to carts, edits quantities, and removes cart items.
- Checks out a cart through the backend transaction.
- Reviews orders and changes valid order statuses.
- Processes pending demo payments and prints a success bill.

## Requirements

- Node.js 18 or newer
- npm
- Spring Boot backend running at `http://localhost:8080` unless another API URL is configured

## Install and Run

From this directory:

```powershell
npm install
npm run dev
```

Vite prints the local development URL, normally:

```text
http://localhost:5173
```

Production build:

```powershell
npm run build
npm run preview
```

Lint:

```powershell
npm run lint
```

The `npm run build` command is the required pre-deployment check. The current project uses `npm.cmd` on Windows if PowerShell blocks the `npm` script shim:

```powershell
npm.cmd install
npm.cmd run build
```

## API Configuration

The Axios client is configured in `src/services/api.js`.

Default:

```text
http://localhost:8080/api/v1
```

To use another backend URL, create `.env.local`:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

Restart Vite after changing environment variables.

The backend must allow the frontend origin through CORS. The current backend allows `http://localhost:5173` and `http://127.0.0.1:5173`.

## Application Routes

| Route | Purpose |
| --- | --- |
| `/` | Dashboard metrics, recent orders, and stock attention |
| `/products` | Product catalog create, edit, delete, and search |
| `/customers` | Customer create, edit, and delete |
| `/cart` | Add products to a customer cart and checkout |
| `/orders` | Review, confirm, cancel, and delete orders |
| `/payments` | Review payments, process pending payments, and print successful bills |

## Screen Guide

### Dashboard

The dashboard requests products, customers, orders, carts, and payments independently. It displays:

- Product, customer, order, cart, and payment counts.
- Revenue from payments with `SUCCESS` status.
- Number of pending payments.
- Five most recent orders.
- Active products with stock at or below ten units.
- A refresh button and partial-data error message.

One failed endpoint should not hide successful metrics from the other endpoints.

### Products

Use **Add Product** to enter the backend-required fields:

- Name
- Description
- Price greater than zero
- Stock zero or greater
- Product category
- Optional image URL
- Optional image file

For a file upload, the UI first saves the product, then calls `POST /products/{id}/image`. The backend stores the file under its configured upload directory and returns a path such as `/uploads/products/{file}`. The product card resolves that path against the backend host.

### Customers

Customer forms validate the backend rules before submission:

- Name is required.
- Email must be valid.
- Phone must contain ten digits.
- Address is required.

Duplicate email and backend validation errors remain visible in the form.

### Shopping Cart

The cart screen follows the real backend ownership model:

1. Select a customer.
2. Select an active product with available stock.
3. Enter a quantity and click **Add to cart**.
4. Edit a cart item quantity or remove it.
5. Enter or confirm the shipping address.
6. Select a payment method.
7. Click **Checkout**.

The user does not need to type cart IDs or product IDs. The UI loads the cart by customer and creates it automatically when required.

### Orders

Orders created by checkout start as `PENDING`. The page displays order totals and status. Valid status actions are sent to the backend; failed actions remain visible without replacing the table.

### Payments And Bill

Checkout creates one payment with status `PENDING`. The Payments page lists those records. Clicking **Process** calls the demo processing endpoint, which changes the payment to `SUCCESS` and the related order to `CONFIRMED`. The UI then loads the order and displays:

- Order ID
- Payment ID
- Transaction ID
- Customer
- Payment method
- Success status
- Total amount
- Shipping address

The **Print Bill** action uses the browser print dialog and can save a PDF. Email is not implemented because the backend has no mail provider or SMTP configuration.

## Real-World Purchase Flow

The UI follows the backend transaction flow:

```text
Select customer
  -> Select active product
  -> Add product to cart
  -> Edit quantity or remove item
  -> Enter shipping address
  -> Select payment method
  -> Checkout
  -> Backend creates order, order items, and pending payment
  -> Payments page: Process
  -> Payment SUCCESS and order CONFIRMED
  -> Print Bill
```

### Adding a Product to a Cart

1. Open **Shopping Cart**.
2. Select a customer.
3. Select an active product with available stock.
4. Select the quantity.
5. Click **Add to cart**.
6. Review the cart summary.

The frontend calls:

```text
GET  /api/v1/customers
GET  /api/v1/products
GET  /api/v1/shopping-carts/customer/{customerId}
POST /api/v1/shopping-carts
POST /api/v1/cart-items
PUT  /api/v1/cart-items/{id}
DELETE /api/v1/cart-items/{id}
```

A cart is created automatically for the selected customer when needed.

### Checkout and Payment

Checkout calls:

```text
POST /api/v1/orders/checkout
```

Request example:

```json
{
  "customerId": 1,
  "shippingAddress": "12 Main Street",
  "paymentMethod": "CARD",
  "notes": "Leave at reception"
}
```

The backend creates the payment as `PENDING`. Open **Payments** and click **Process**. On success, the UI fetches the confirmed order and displays a printable bill.

Email notifications are not currently included because the backend has no email provider or SMTP configuration. The bill can be printed or saved as PDF from the browser.

## Error Handling

- Initial page loading displays a loading state.
- Failed initial data loading displays a retry action.
- Action failures remain visible on the current page instead of replacing the table or form.
- Backend validation messages are displayed when available.
- Dashboard requests are independent, so one failed metric endpoint does not hide all other metrics.

Backend error responses normally contain `message` and, for validation failures, an `errors` object. The UI prefers the backend message and falls back to a user-readable local message.

## Frontend To Backend Map

| Frontend service | Backend base path | Main responsibility |
| --- | --- | --- |
| `customerService.js` | `/customers` | Customer CRUD |
| `productService.js` | `/products` | Product CRUD, stock, image upload, queries |
| `cartService.js` | `/shopping-carts` | Cart lookup, create, clear, delete |
| `cartItemService.js` | `/cart-items` | Add, update, list, and remove cart items |
| `orderService.js` | `/orders` | Orders, checkout, confirmation, cancellation |
| `paymentService.js` | `/payments` | Payment history, processing, and deletion |

The most important request is:

```http
POST /api/v1/orders/checkout
```

```json
{
  "customerId": 1,
  "shippingAddress": "12 Main Street",
  "paymentMethod": "CARD",
  "notes": "Leave at reception"
}
```

The backend reads the selected customer's cart and creates the order, order items, payment, stock changes, and cart clearing in one transaction.

## Project Structure

```text
src/
  components/     Shared forms, cards, navigation, loading, and errors
  layouts/        Admin shell and outlet layout
  pages/          Dashboard, CRUD pages, cart, orders, and payments
  services/       Axios-backed API service modules
  App.jsx         Routes
  index.css       Shared responsive design system
```

Important files:

```text
src/App.jsx                         Application routes
src/layouts/AdminLayout.jsx         Sidebar, navbar, and page outlet
src/pages/Dashboard.jsx             Operational metrics and alerts
src/pages/Products.jsx              Product management
src/pages/Customers.jsx             Customer management
src/pages/Cart.jsx                  Cart, checkout, and payment selection
src/pages/Orders.jsx                Order status management
src/pages/Payments.jsx              Payment processing and printable bill
src/services/api.js                 Axios base URL and error logging
src/index.css                       Shared layout and responsive styles
```

## Backend Prerequisites

Start the backend before using data-driven pages:

```powershell
cd ..\ecommerce-rest-api
mvn spring-boot:run
```

Confirm:

```text
http://localhost:8080/actuator/health
```

If the UI shows network errors, check that the backend is running, MySQL is available, and `VITE_API_BASE_URL` points to the correct API version.

## Troubleshooting

### Products or customers do not load

1. Confirm the backend is running on port 8080.
2. Open `http://localhost:8080/actuator/health`.
3. Confirm MySQL is running and the configured database exists.
4. Check the browser developer console for the failed request.

### Images do not appear

- For uploaded images, confirm the backend is running and the upload request succeeds.
- Confirm the returned `imageUrl` starts with `/uploads/products/`.
- Confirm the image URL opens from the backend host, for example `http://localhost:8080/uploads/products/file.jpg`.
- For remote images, confirm the URL is publicly reachable and permits browser loading.

### Checkout fails

- The selected customer must exist.
- The customer must have a cart with at least one item.
- Each item quantity must be within available stock.
- Shipping address is required.
- Payment method must be one of `CARD`, `UPI`, `CASH_ON_DELIVERY`, or `NET_BANKING`.

### Payment processing fails

- Only `PENDING` payments can be processed.
- The related order must still be available.
- This project uses demo payment processing; no external gateway is connected.

## Production Notes

This is an administration UI, not an authentication system. Before production deployment, add authenticated sessions, role-based access, CSRF protection where applicable, server-side pagination, audit logging, structured frontend logging, environment-specific API configuration, image CDN/storage, and a real payment provider integration.
