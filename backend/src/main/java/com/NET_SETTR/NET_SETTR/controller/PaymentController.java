package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.service.PaymentService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> data) {
        try {
            double amount = Double.parseDouble(data.get("amount").toString());
            String currency = data.getOrDefault("currency", "INR").toString();
            String receipt = data.getOrDefault("receipt", "NETSETTR_" + System.currentTimeMillis()).toString();

            Order order = paymentService.createOrder(amount, currency, receipt);

            return Map.of(
                    "orderId", order.get("id"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency"),
                    "status", order.get("status")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Failed to create Razorpay order");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");
            String phoneNo = data.get("phone_no");
            String courseId = data.get("course_id");
            String amount = data.get("amount");

            boolean isValid = paymentService.verifySignature(orderId, paymentId, signature);

            if (isValid) {
                paymentService.savePayment(orderId, paymentId, signature, phoneNo, courseId, Double.parseDouble(amount));
                return ResponseEntity.ok(Map.of("status", "success", "message", "Payment verified and saved"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "failed", "message", "Invalid payment signature"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }



}
