import api from "./api";

const paymentService = {
    getAllPayments: async () => {
        const response = await api.get("/payments");
        return response.data;
    },

    getPaymentById: async (id) => {
        const response = await api.get(`/payments/${id}`);
        return response.data;
    },

    getPaymentByOrderId: async (orderId) => {
        const response = await api.get(`/payments/order/${orderId}`);
        return response.data;
    },

    createPayment: async (payment) => {
        const response = await api.post("/payments", payment);
        return response.data;
    },

    updatePayment: async (id, payment) => {
        const response = await api.put(`/payments/${id}`, payment);
        return response.data;
    },

    deletePayment: async (id) => {
        const response = await api.delete(`/payments/${id}`);
        return response.data;
    },

    processPayment: async (id) => {
        const response = await api.post(`/payments/${id}/process`);
        return response.data;
    },
};

export default paymentService;