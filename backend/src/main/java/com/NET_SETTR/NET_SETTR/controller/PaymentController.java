package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.repository.UserRepository;
import com.NET_SETTR.NET_SETTR.service.PaymentService;
import com.NET_SETTR.NET_SETTR.service.SubscriptionService;
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

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-order")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> data) {
        try {
            double amount = Double.parseDouble(data.get("amount").toString());
            String currency = data.getOrDefault("currency", "INR").toString();
            String receipt = "NETSETTR_" + System.currentTimeMillis();

            var order = paymentService.createOrder(amount, currency, receipt);

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
            String loginId = data.get("loginId");   // email/number
            Integer courseId = Integer.parseInt(data.get("course_id"));
            double amount = Double.parseDouble(data.get("amount"));

            User user;
            // fetch user using loginId (email)
            if (loginId.contains("@")) {
                user = userRepository.findByEmail(loginId)
                        .orElseThrow(() -> new RuntimeException("User not found with email"));
            }
            // 🔹 Else treat it as phone number
            else {
                user = userRepository.findByPhoneNo(loginId)
                        .orElseThrow(() -> new RuntimeException("User not found with phone number"));
            }

            Long userId = user.getUserId();
            String phoneNo = user.getPhoneNo();  // NOW we have phone number

            boolean isValid = paymentService.verifySignature(orderId, paymentId, signature);

            String status;
            if (!isValid) {
                status = "FAILED";
                paymentService.savePayment(orderId, paymentId, signature, userId, phoneNo, courseId, amount, status);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "failed", "message", "Verification failed"));
            }

            status = "SUCCESS";
            paymentService.savePayment(orderId, paymentId, signature, userId, phoneNo, courseId, amount, status);

            subscriptionService.createSubscription(userId, courseId, paymentId);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Payment verified & subscription activated"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}
