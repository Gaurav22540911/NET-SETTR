package com.NET_SETTR.NET_SETTR.repository;

import com.NET_SETTR.NET_SETTR.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Integer courseId);

   // Optional<Subscription> findByUserIdAndCourseIdAndStatus(Long userId, Integer courseId, Subscription.Status status);
}
