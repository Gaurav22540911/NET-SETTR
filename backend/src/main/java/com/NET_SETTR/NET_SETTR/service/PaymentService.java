package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.model.Payment;
import com.NET_SETTR.NET_SETTR.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    @Autowired
    private PaymentRepository paymentRepository;

    public Order createOrder(Double amount, String currency, String receipt) throws Exception {
        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // convert to paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);

        return razorpay.orders.create(orderRequest);
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) throws Exception {
        String data = orderId + "|" + paymentId;
        return Utils.verifySignature(data, signature, keySecret);
    }


    public void savePayment(String orderId, String paymentId, String signature, String phoneNo, String courseId, double amount) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setOrderId(orderId);
        payment.setPhoneNo(phoneNo);
        payment.setCourseId(Integer.parseInt(courseId));
        payment.setAmount(amount);
        payment.setCurrency("INR");
        payment.setStatus("paid");
        payment.setRazorpaySignature(signature);
        paymentRepository.save(payment);
    }
}
