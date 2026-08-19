import api from "./api";

const cartService = {
    getAllCarts: async () => {
        const response = await api.get("/shopping-carts");
        return response.data;
    },

    getCartById: async (id) => {
        const response = await api.get(`/shopping-carts/${id}`);
        return response.data;
    },

    getCartByCustomerId: async (customerId) => {
        const response = await api.get(`/shopping-carts/customer/${customerId}`);
        return response.data;
    },

    createCart: async (cart) => {
        const response = await api.post("/shopping-carts", cart);
        return response.data;
    },

    updateCart: async () => {
        throw new Error("Shopping carts cannot be updated by the backend API.");
    },

    deleteCart: async (id) => {
        const response = await api.delete(`/shopping-carts/${id}`);
        return response.data;
    },

    clearCart: async (id) => {
        const response = await api.delete(`/shopping-carts/${id}/clear`);
        return response.data;
    },
};

export default cartService;