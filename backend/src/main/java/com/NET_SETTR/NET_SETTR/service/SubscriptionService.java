package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.dto.SubscriptionDetailsResponse;
import com.NET_SETTR.NET_SETTR.model.Subscription;
import com.NET_SETTR.NET_SETTR.repository.SubscriptionRepository;
import com.NET_SETTR.NET_SETTR.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public SubscriptionDetailsResponse getSubscriptionDetails(Long userId, Integer courseId) {

        Optional<Subscription> subOpt =
                subscriptionRepository.findTopByUserIdAndCourseIdOrderByCreatedAtDesc(userId, courseId);

        if (subOpt.isEmpty()) {
            return new SubscriptionDetailsResponse(false);
        }

        Subscription sub = subOpt.get();

        boolean expired = sub.getEndDate().isBefore(LocalDateTime.now());

        // 🔁 Lazy expiry (best practice)
        if (expired && sub.getStatus() == Subscription.Status.ACTIVE) {
            sub.setStatus(Subscription.Status.EXPIRED);
            subscriptionRepository.save(sub);
        }

        return new SubscriptionDetailsResponse(
                true,
                expired ? Subscription.Status.EXPIRED.name() : sub.getStatus().name(),
                sub.getStartDate(),
                sub.getEndDate(),
                expired
        );
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
