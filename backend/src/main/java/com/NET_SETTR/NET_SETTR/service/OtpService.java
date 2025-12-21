package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.ENUM.OtpPurpose;
import com.NET_SETTR.NET_SETTR.model.OtpVerification;
import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpService(OtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public void generateAndSendOtp(User user, OtpPurpose purpose) {

        // invalidate previous OTPs
        otpRepository.deleteByUserAndPurpose(user, purpose);

        String otp = String.valueOf(
                new Random().nextInt(900000) + 100000
        );

        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setUser(user);
        otpEntity.setOtp(otp);
        otpEntity.setPurpose(purpose);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);
        otpEntity.setAttempts(0);

        otpRepository.save(otpEntity);

        // send email (reuse existing infra)
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public boolean verifyOtp(User user, String otp, OtpPurpose purpose) {

        OtpVerification record = otpRepository
                .findTopByUserAndPurposeOrderByCreatedAtDesc(user, purpose)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (record.isVerified()) return false;
        if (record.getAttempts() >= 3) return false;
        if (record.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        record.setAttempts(record.getAttempts() + 1);

        if (!record.getOtp().equals(otp)) {
            otpRepository.save(record);
            return false;
        }

        record.setVerified(true);
        otpRepository.save(record);

        return true;
    }

    public void ensureOtpVerified(User user, OtpPurpose purpose) {

        OtpVerification record = otpRepository
                .findTopByUserAndPurposeOrderByCreatedAtDesc(user, purpose)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!record.isVerified()) {
            throw new RuntimeException("OTP not verified");
        }
    }

    @Transactional
    public void invalidateOtp(User user, OtpPurpose purpose) {
        otpRepository.deleteByUserAndPurpose(user, purpose);
    }


}
