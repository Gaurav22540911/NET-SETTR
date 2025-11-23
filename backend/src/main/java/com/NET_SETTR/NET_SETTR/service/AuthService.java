package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.model.EmailOtp;
import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.repository.EmailOtpRepository;
import com.NET_SETTR.NET_SETTR.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailOtpRepository otpRepo;

    @Autowired
    private EmailService emailService;

    // --------------------------
    // SIGNUP
    // --------------------------
    public String signup(User user) {

        Optional<User> emailUser = userRepo.findByEmail(user.getEmail());
        Optional<User> phoneUser = userRepo.findByPhoneNo(user.getPhoneNo());
        User existing = null;

        // Case 1: Email exists
        if (emailUser.isPresent()) {
            existing = emailUser.get();
            if (existing.isVerified()) return "Email already exists";

            existing.setFullName(user.getFullName());
            existing.setPhoneNo(user.getPhoneNo());
            existing.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            user = userRepo.save(existing);
        }
        // Case 2: Phone exists
        else if (phoneUser.isPresent()) {
            existing = phoneUser.get();
            if (existing.isVerified()) return "Phone number already exists";

            existing.setFullName(user.getFullName());
            existing.setEmail(user.getEmail());
            existing.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            user = userRepo.save(existing);
        }
        // Case 3: New user
        else {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            user.setVerified(false);
            user = userRepo.save(user);
        }

        // OTP generation
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(user.getEmail());
        emailOtp.setOtpCode(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpRepo.save(emailOtp);

        emailService.sendEmail(
                user.getEmail(),
                "NET-SETTR Email Verification",
                "Your OTP is: " + otp + " (valid for 5 minutes)"
        );

        return "OTP sent to email";
    }

    // --------------------------
    // VERIFY OTP
    // --------------------------
    public String verifyOtp(String email, String otp) {

        EmailOtp saved = otpRepo.findById(email).orElse(null);
        if (saved == null) return "OTP not found";
        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) return "OTP expired";
        if (!saved.getOtpCode().equals(otp)) return "Invalid OTP";

        User user = userRepo.findByEmail(email).get();
        user.setVerified(true);
        userRepo.save(user);

        otpRepo.deleteById(email);

        return "Verification successful!";
    }

    // --------------------------
    // LOGIN
    // --------------------------
    public String login(String loginId, String password) {

        Optional<User> userOpt;

        if (loginId.contains("@"))
            userOpt = userRepo.findByEmail(loginId);
        else
            userOpt = userRepo.findByPhoneNo(loginId);

        if (userOpt.isEmpty()) return "User not found";

        User user = userOpt.get();

        if (!BCrypt.checkpw(password, user.getPassword()))
            return "Invalid password";

        if (!user.isVerified())
            return "User not verified";

        user.setLastLogin(LocalDateTime.now());
        userRepo.save(user);

        return "Login successful";
    }
}
