import api from "./api";

const cartItemService = {
    getAllCartItems: async () => {
        const response = await api.get("/cart-items");
        return response.data;
    },

    getCartItemById: async (id) => {
        const response = await api.get(`/cart-items/${id}`);
        return response.data;
    },

    getCartItemsByCartId: async (cartId) => {
        const response = await api.get(`/cart-items/cart/${cartId}`);
        return response.data;
    },

    createCartItem: async (cartItem) => {
        const response = await api.post("/cart-items", cartItem);
        return response.data;
    },

    updateCartItem: async (id, cartItem) => {
        const response = await api.put(`/cart-items/${id}`, cartItem);
        return response.data;
    },

    deleteCartItem: async (id) => {
        const response = await api.delete(`/cart-items/${id}`);
        return response.data;
    },

    deleteCartItemsByCartId: async (cartId) => {
        const response = await api.delete(`/cart-items/cart/${cartId}`);
        return response.data;
    },
};

export default cartItemService;