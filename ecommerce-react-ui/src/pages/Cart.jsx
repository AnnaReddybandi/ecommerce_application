import { useEffect, useState } from "react";
import cartService from "../services/cartService";
import cartItemService from "../services/cartItemService";
import customerService from "../services/customerService";
import productService from "../services/productService";
import orderService from "../services/orderService";
import Loading from "../components/Loading";
import ErrorMessage from "../components/ErrorMessage";

const paymentMethods = ["CARD", "UPI", "CASH_ON_DELIVERY", "NET_BANKING"];

function Cart() {
    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [cart, setCart] = useState(null);
    const [selectedCustomerId, setSelectedCustomerId] = useState("");
    const [selectedProductId, setSelectedProductId] = useState("");
    const [quantity, setQuantity] = useState(1);
    const [shippingAddress, setShippingAddress] = useState("");
    const [paymentMethod, setPaymentMethod] = useState("CARD");
    const [notes, setNotes] = useState("");
    const [loading, setLoading] = useState(true);
    const [working, setWorking] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [editingItemId, setEditingItemId] = useState(null);
    const [editingQuantity, setEditingQuantity] = useState(1);

    const loadSetup = async () => {
        try {
            setLoading(true);
            setError("");
            const [customerResponse, productResponse] = await Promise.all([
                customerService.getAllCustomers(),
                productService.getAllProducts(),
            ]);
            const customerData = Array.isArray(customerResponse) ? customerResponse : [];
            const productData = Array.isArray(productResponse) ? productResponse : [];
            setCustomers(customerData);
            setProducts(productData.filter((product) => product.status !== "INACTIVE" && product.stock > 0));
            if (customerData.length > 0) setSelectedCustomerId(String(customerData[0].id));
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "Unable to load customers and products.");
        } finally {
            setLoading(false);
        }
    };

    const loadCart = async () => {
        if (!selectedCustomerId) {
            setCart(null);
            return;
        }
        try {
            setWorking(true);
            const loadedCart = await cartService.getCartByCustomerId(selectedCustomerId);
            setCart(loadedCart);
            const customer = customers.find((item) => String(item.id) === String(selectedCustomerId));
            if (customer && !shippingAddress) setShippingAddress(customer.address || "");
        } catch (err) {
            if (err.response?.status === 404) setCart(null);
            else setError(err.response?.data?.message || "Unable to load this customer cart.");
        } finally {
            setWorking(false);
        }
    };

    useEffect(() => { loadSetup(); }, []);
    useEffect(() => { loadCart(); }, [selectedCustomerId, customers]);

    const ensureCart = async () => {
        if (cart) return cart;
        const createdCart = await cartService.createCart({ customerId: Number(selectedCustomerId) });
        setCart(createdCart);
        return createdCart;
    };

    const addProduct = async (event) => {
        event.preventDefault();
        try {
            setWorking(true);
            setError("");
            setSuccess("");
            const activeCart = await ensureCart();
            await cartItemService.createCartItem({
                cartId: activeCart.id,
                productId: Number(selectedProductId),
                quantity: Number(quantity),
            });
            await loadCart();
            setSuccess("Product added to the cart.");
            setQuantity(1);
        } catch (err) {
            setError(err.response?.data?.message || "Unable to add product. Check stock and quantity.");
        } finally {
            setWorking(false);
        }
    };

    const updateItem = async (item) => {
        try {
            setWorking(true);
            await cartItemService.updateCartItem(item.id, {
                cartId: item.cartId,
                productId: item.productId,
                quantity: Number(editingQuantity),
            });
            setEditingItemId(null);
            await loadCart();
        } catch (err) {
            setError(err.response?.data?.message || "Unable to update quantity.");
        } finally {
            setWorking(false);
        }
    };

    const removeItem = async (itemId) => {
        try {
            setWorking(true);
            await cartItemService.deleteCartItem(itemId);
            await loadCart();
        } catch (err) {
            setError(err.response?.data?.message || "Unable to remove product from cart.");
        } finally {
            setWorking(false);
        }
    };

    const checkout = async (event) => {
        event.preventDefault();
        try {
            setWorking(true);
            setError("");
            setSuccess("");
            const order = await orderService.checkout({
                customerId: Number(selectedCustomerId),
                shippingAddress,
                paymentMethod,
                notes,
            });
            setCart({ ...cart, items: [] });
            setSuccess(`Order #${order.id} created. Payment is pending and ready to process.`);
        } catch (err) {
            setError(err.response?.data?.message || "Checkout failed. Make sure the cart has items and enough stock.");
        } finally {
            setWorking(false);
        }
    };

    if (loading) return <Loading message="Loading checkout setup..." />;
    if (error && !customers.length) return <ErrorMessage message={error} onRetry={loadSetup} />;

    const items = cart?.items || [];
    const total = items.reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0);
    const selectedCustomer = customers.find((item) => String(item.id) === String(selectedCustomerId));

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Shopping Cart</h1>
                    <p>Add products, review the cart, and complete checkout.</p>
                </div>
                {working && <span className="working-state">Saving...</span>}
            </div>

            {error && <div className="action-error">{error}</div>}
            {success && <div className="action-success">{success}</div>}

            <section className="workflow-grid">
                <div className="action-panel workflow-panel">
                    <div className="panel-heading"><span className="step-number">1</span><div><h2>Choose customer</h2><p>The cart is loaded by customer.</p></div></div>
                    <select value={selectedCustomerId} onChange={(event) => { setSelectedCustomerId(event.target.value); setShippingAddress(""); setSuccess(""); }}>
                        <option value="">Select customer</option>
                        {customers.map((customer) => <option key={customer.id} value={customer.id}>{customer.name} · {customer.email}</option>)}
                    </select>
                </div>

                <form className="action-panel workflow-panel" onSubmit={addProduct}>
                    <div className="panel-heading"><span className="step-number">2</span><div><h2>Add product</h2><p>Only active products with stock appear.</p></div></div>
                    <select value={selectedProductId} onChange={(event) => setSelectedProductId(event.target.value)} required>
                        <option value="">Select product</option>
                        {products.map((product) => <option key={product.id} value={product.id}>{product.name} · ₹{Number(product.price).toLocaleString("en-IN")} · {product.stock} left</option>)}
                    </select>
                    <div className="quantity-row">
                        <label>Quantity<input type="number" min="1" value={quantity} onChange={(event) => setQuantity(event.target.value)} required /></label>
                        <button type="submit" className="primary-button" disabled={!selectedCustomerId || working}>Add to cart</button>
                    </div>
                </form>
            </section>

            <section className="cart-summary-panel">
                <div className="section-heading">
                    <div><h2>{cart ? `Cart for ${selectedCustomer?.name || "customer"}` : "No cart yet"}</h2><p>{items.length} product line{items.length === 1 ? "" : "s"}</p></div>
                    <strong>₹{total.toLocaleString("en-IN")}</strong>
                </div>
                {items.length === 0 ? (
                    <div className="empty-state"><h3>{cart ? "Your cart is empty" : "Select a customer to begin"}</h3><p>Add a product above to create or fill the customer cart.</p></div>
                ) : (
                    <div className="cart-items-list">
                        {items.map((item) => (
                            <div className="cart-item-row" key={item.id}>
                                <div><strong>{item.productName}</strong><span>₹{Number(item.price).toLocaleString("en-IN")} each</span></div>
                                <div className="cart-item-controls">
                                    {editingItemId === item.id ? (
                                        <><input className="inline-number" type="number" min="1" value={editingQuantity} onChange={(event) => setEditingQuantity(event.target.value)} /><button className="edit-button" onClick={() => updateItem(item)}>Save</button><button className="secondary-button" onClick={() => setEditingItemId(null)}>Cancel</button></>
                                    ) : (
                                        <><span className="quantity-badge">Qty {item.quantity}</span><button className="edit-button" onClick={() => { setEditingItemId(item.id); setEditingQuantity(item.quantity); }}>Edit quantity</button></>
                                    )}
                                    <button className="delete-button" onClick={() => removeItem(item.id)}>Remove</button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>

            <form className="checkout-panel" onSubmit={checkout}>
                <div className="panel-heading"><span className="step-number">3</span><div><h2>Checkout and payment</h2><p>Checkout creates the order and its pending payment together.</p></div></div>
                <div className="checkout-grid">
                    <label>Shipping address<textarea value={shippingAddress} onChange={(event) => setShippingAddress(event.target.value)} required placeholder="Delivery address" /></label>
                    <label>Payment method<select value={paymentMethod} onChange={(event) => setPaymentMethod(event.target.value)}>{paymentMethods.map((method) => <option key={method} value={method}>{method.replaceAll("_", " ")}</option>)}</select></label>
                    <label>Notes<input value={notes} onChange={(event) => setNotes(event.target.value)} placeholder="Optional order notes" /></label>
                </div>
                <button type="submit" className="primary-button checkout-button" disabled={!selectedCustomerId || items.length === 0 || working}>Checkout ₹{total.toLocaleString("en-IN")}</button>
            </form>
        </div>
    );
}

export default Cart;
