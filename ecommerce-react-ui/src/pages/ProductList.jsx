import { useEffect, useState } from "react";
import productService from "../services/productService";

function ProductList() {

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        loadProducts();
    }, []);

    const loadProducts = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await productService.getAllProducts();

            setProducts(data);

        } catch (error) {

            console.error(error);

            setError(
                "Unable to load products. Please check the Spring Boot backend."
            );

        } finally {

            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="container mt-5">
                <h3>Loading products...</h3>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container mt-5">
                <div className="alert alert-danger">
                    {error}
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-5">

            <div className="d-flex justify-content-between align-items-center mb-4">

                <h2>Products</h2>

                <button
                    className="btn btn-primary"
                    onClick={loadProducts}
                >
                    Refresh
                </button>

            </div>

            {products.length === 0 ? (

                <div className="alert alert-info">
                    No products found.
                </div>

            ) : (

                <div className="row">

                    {products.map((product) => (

                        <div
                            className="col-md-4 mb-4"
                            key={product.id}
                        >

                            <div className="card h-100 shadow-sm">

                                <div className="card-body">

                                    <h5 className="card-title">
                                        {product.name}
                                    </h5>

                                    <p className="card-text">
                                        {product.description}
                                    </p>

                                    <p>
                                        <strong>
                                            Price:
                                        </strong>{" "}
                                        ₹{product.price}
                                    </p>

                                    <p>
                                        <strong>
                                            Stock:
                                        </strong>{" "}
                                        {product.stock}
                                    </p>

                                    <span className="badge bg-secondary">
                                        {product.category}
                                    </span>

                                </div>

                            </div>

                        </div>

                    ))}

                </div>

            )}

        </div>
    );
}

export default ProductList;