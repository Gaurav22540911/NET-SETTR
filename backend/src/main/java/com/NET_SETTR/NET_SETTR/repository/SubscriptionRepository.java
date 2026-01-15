package com.NET_SETTR.NET_SETTR.repository;

import com.NET_SETTR.NET_SETTR.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Integer courseId);

    //Optional<Subscription> findByUserIdAndCourseId(Long userId, Integer courseId);

    Optional<Subscription> findTopByUserIdAndCourseIdOrderByCreatedAtDesc(
            Long userId,
            Integer courseId
    );
}
