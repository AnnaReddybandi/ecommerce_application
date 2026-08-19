import api from "./api";

const productService = {
    getAllProducts: async () => {
        const response = await api.get("/products");
        return response.data;
    },

    getProductById: async (id) => {
        const response = await api.get(`/products/${id}`);
        return response.data;
    },

    createProduct: async (product) => {
        const response = await api.post("/products", product);
        return response.data;
    },

    updateProduct: async (id, product) => {
        const response = await api.put(`/products/${id}`, product);
        return response.data;
    },

    deleteProduct: async (id) => {
        const response = await api.delete(`/products/${id}`);
        return response.data;
    },

    searchByCategory: async (category) => {
        const response = await api.get(`/products/category/${category}`);
        return response.data;
    },

    getProductsByPriceRange: async (minPrice, maxPrice) => {
        const response = await api.get("/products/price-range", {
            params: { min: minPrice, max: maxPrice },
        });

        return response.data;
    },

    getLowStockProducts: async (threshold = 10) => {
        const response = await api.get(`/products/low-stock/${threshold}`);

        return response.data;
    },

    reduceStock: async (id, quantity) => {
        const response = await api.post(
            `/products/${id}/reduce-stock`,
            null,
            {
                params: { quantity },
            }
        );

        return response.data;
    },

    increaseStock: async (id, quantity) => {
        const response = await api.post(
            `/products/${id}/increase-stock`,
            null,
            {
                params: { quantity },
            }
        );

        return response.data;
    },

    uploadImage: async (id, file) => {
        const formData = new FormData();
        formData.append("file", file);

        const response = await api.post(
            `/products/${id}/image`,
            formData,
            {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            }
        );

        return response.data;
    },
};

export default productService;