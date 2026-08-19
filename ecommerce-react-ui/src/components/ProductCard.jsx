import { useState } from "react";

function ProductCard({ product, onDelete, onEdit }) {
    const [imageError, setImageError] = useState(false);

    const productId = product?.id ?? product?.productId;
    const productName =
        product?.name ?? product?.productName ?? "Product";

    const price = product?.price ?? 0;

    const stock =
        product?.stockQuantity ??
        product?.stock ??
        product?.quantity ??
        0;

    const storedImageUrl =
        product?.imageUrl ??
        product?.image ??
        product?.imagePath;
    const apiOrigin = (import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1").replace(/\/api\/v1\/?$/, "");
    const imageUrl = storedImageUrl && storedImageUrl.startsWith("/")
        ? `${apiOrigin}${storedImageUrl}`
        : storedImageUrl;

    return (
        <div className="product-card">
            <div className="product-image-container">
                {imageUrl && !imageError ? (
                    <img
                        src={imageUrl}
                        alt={productName}
                        className="product-image"
                        onError={() => setImageError(true)}
                    />
                ) : (
                    <div className="product-image-placeholder">
                        📦
                    </div>
                )}
            </div>

            <div className="product-card-content">
                <h3 className="product-name">
                    {productName}
                </h3>

                {product?.category && (
                    <span className="product-category">
                        {product.category}
                    </span>
                )}

                <div className="product-card-details">
                    <strong>
                        ₹{Number(price).toLocaleString("en-IN")}
                    </strong>

                    <span
                        className={
                            stock <= 10
                                ? "stock-low"
                                : "stock-available"
                        }
                    >
                        Stock: {stock}
                    </span>
                </div>

                <div className="product-card-actions">
                    <button
                        className="edit-button"
                        onClick={() => onEdit?.(product)}
                    >
                        Edit
                    </button>

                    <button
                        className="delete-button"
                        onClick={() => onDelete?.(productId)}
                    >
                        Delete
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ProductCard;