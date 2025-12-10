package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.repository.UserRepository;
import com.NET_SETTR.NET_SETTR.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin("*")
public class SubscriptionController {

    @Autowired
    private UserRepository userRepository;

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/check")
    public Map<String, Object> checkSubscription(@RequestParam String loginId, @RequestParam Integer courseId) {

        // Convert email to userId
        User user = userRepository.findByEmail(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean subscribed = subscriptionService.isUserSubscribed(user.getUserId(), courseId);

        return Map.of("subscribed", subscribed);
    }

//    @GetMapping("/check")
//    public Map<String, Object> checkSubscription(@RequestParam Long userId, @RequestParam Integer courseId) {
//        boolean subscribed = subscriptionService.isUserSubscribed(userId, courseId);
//
//        return Map.of("subscribed", subscribed);
//    }


}
