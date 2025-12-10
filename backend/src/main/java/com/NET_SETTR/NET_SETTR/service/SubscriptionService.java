package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.model.Subscription;
import com.NET_SETTR.NET_SETTR.repository.SubscriptionRepository;
import com.NET_SETTR.NET_SETTR.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    public boolean isUserSubscribed(Long userId, Integer courseId) {
        return subscriptionRepository.existsByUserIdAndCourseId(userId, courseId);
    }


    public Subscription createSubscription(Long userId, Integer courseId, String paymentId) {
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setCourseId(courseId);
        subscription.setPaymentId(paymentId);
        subscription.setStartDate(java.time.LocalDateTime.now());
        subscription.setEndDate(java.time.LocalDateTime.now().plusMonths(6));
        subscription.setStatus(Subscription.Status.ACTIVE);

        return subscriptionRepository.save(subscription);
    }
}
