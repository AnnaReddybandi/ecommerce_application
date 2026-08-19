import api from "./api";

const orderItemService = {
    getAll: async () => {
        const response = await api.get("/order-items");
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/order-items/${id}`);
        return response.data;
    },

    getByOrder: async (orderId) => {
        const response = await api.get(
            `/order-items/order/${orderId}`
        );
        return response.data;
    },

    create: async (orderItem) => {
        const response = await api.post(
            "/order-items",
            orderItem
        );
        return response.data;
    },

    update: async (id, orderItem) => {
        const response = await api.put(
            `/order-items/${id}`,
            orderItem
        );
        return response.data;
    },

    remove: async (id) => {
        const response = await api.delete(
            `/order-items/${id}`
        );
        return response.data;
    },
};

export default orderItemService;