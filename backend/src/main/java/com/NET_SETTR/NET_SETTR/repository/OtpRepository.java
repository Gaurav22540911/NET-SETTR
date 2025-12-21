package com.NET_SETTR.NET_SETTR.repository;

import com.NET_SETTR.NET_SETTR.ENUM.OtpPurpose;
import com.NET_SETTR.NET_SETTR.model.OtpVerification;
import com.NET_SETTR.NET_SETTR.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByUserAndPurposeOrderByCreatedAtDesc(
            User user,
            OtpPurpose purpose
    );

    @Modifying
    @Transactional
    void deleteByUserAndPurpose(User user, OtpPurpose purpose);
}
