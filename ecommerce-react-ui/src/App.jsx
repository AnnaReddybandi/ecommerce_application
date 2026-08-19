import { BrowserRouter, Route, Routes } from "react-router-dom";

import AdminLayout from "./layouts/AdminLayout";

import Dashboard from "./pages/Dashboard";
import Products from "./pages/Products";
import Customers from "./pages/Customers";
import Cart from "./pages/Cart";
import Orders from "./pages/Orders";
import Payments from "./pages/Payments";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<AdminLayout />}>
                    <Route path="/" element={<Dashboard />} />

                    <Route
                        path="/products"
                        element={<Products />}
                    />

                    <Route
                        path="/customers"
                        element={<Customers />}
                    />

                    <Route
                        path="/cart"
                        element={<Cart />}
                    />

                    <Route
                        path="/orders"
                        element={<Orders />}
                    />

                    <Route
                        path="/payments"
                        element={<Payments />}
                    />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;