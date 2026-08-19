import api from "./api";

const orderService = {
    getAllOrders: async () => {
        const response = await api.get("/orders");
        return response.data;
    },

    getOrderById: async (id) => {
        const response = await api.get(`/orders/${id}`);
        return response.data;
    },

    getOrdersByCustomerId: async (customerId) => {
        const response = await api.get(`/orders/customer/${customerId}`);
        return response.data;
    },

    createOrder: async (order) => {
        const response = await api.post("/orders", order);
        return response.data;
    },

    checkout: async (checkoutRequest) => {
        const response = await api.post("/orders/checkout", checkoutRequest);
        return response.data;
    },

    updateOrder: async (id, order) => {
        const response = await api.put(`/orders/${id}`, order);
        return response.data;
    },

    deleteOrder: async (id) => {
        const response = await api.delete(`/orders/${id}`);
        return response.data;
    },

    confirmOrder: async (id) => {
        const response = await api.post(`/orders/${id}/confirm`);
        return response.data;
    },

    cancelOrder: async (id) => {
        const response = await api.post(`/orders/${id}/cancel`);

        return response.data;
    },
};

export default orderService;