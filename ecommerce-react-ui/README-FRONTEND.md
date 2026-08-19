# ShopAdmin Frontend

React and Vite administration UI for the E-Commerce REST API.

## Stack

- React 19
- React Router
- Axios
- Vite
- CSS modules through `src/index.css`

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

## Application Routes

| Route | Purpose |
| --- | --- |
| `/` | Dashboard metrics, recent orders, and stock attention |
| `/products` | Product catalog create, edit, delete, and search |
| `/customers` | Customer create, edit, and delete |
| `/cart` | Add products to a customer cart and checkout |
| `/orders` | Review, confirm, cancel, and delete orders |
| `/payments` | Review payments, process pending payments, and print successful bills |

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

## Production Notes

This is an administration UI, not an authentication system. Before production deployment, add authenticated sessions, role-based access, CSRF protection where applicable, server-side pagination, audit logging, and a real payment provider integration.
