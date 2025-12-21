package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.dto.AuthResponse;
import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        return authService.signup(user);
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return authService.verifyOtp(email, otp);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody Map<String, String> loginRequest) {
        String loginId = loginRequest.get("loginId");
        String password = loginRequest.get("password");
        String deviceId = loginRequest.get("deviceId");

        return authService.login(loginId, password, deviceId);
    }

    @PostMapping("/force-device-switch")
    public AuthResponse forceDeviceSwitch(@RequestBody Map<String, String> body) {
        String loginId = body.get("loginId");
        String deviceId = body.get("deviceId");

        authService.updateDevice(loginId, deviceId);
        return new AuthResponse("LOGIN_SUCCESS", "Device switched successfully");
    }

//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
//
//        String loginId = body.get("loginId");
//
//        Optional<User> userOpt;
//        if (loginId.matches("\\d{10}")) {
//            userOpt = userRepository.findByPhoneNo(loginId);
//        } else {
//            userOpt = userRepository.findByEmail(loginId);
//        }
//
//        // SECURITY: do not reveal user existence
//        if (userOpt.isPresent()) {
//            otpService.generateAndSendOtp(
//                    userOpt.get(),
//                    OtpPurpose.RESET_PASSWORD
//            );
//        }
//
//        return ResponseEntity.ok(
//                Map.of("message", "If account exists, OTP has been sent")
//        );
//    }
//
//    @PostMapping("/verify-reset-otp")
//    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> body) {
//
//        String loginId = body.get("loginId");
//        String otp = body.get("otp");
//
//        User user = userService.findByLoginId(loginId);
//
//        boolean valid = otpService.verifyOtp(
//                user,
//                otp,
//                OtpPurpose.RESET_PASSWORD
//        );
//
//        if (!valid) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("message", "Invalid or expired OTP"));
//        }
//
//        return ResponseEntity.ok(
//                Map.of("message", "OTP verified")
//        );
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
//
//        String loginId = body.get("loginId");
//        String newPassword = body.get("newPassword");
//
//        User user = userService.findByLoginId(loginId);
//
//        otpService.ensureOtpVerified(
//                user,
//                OtpPurpose.RESET_PASSWORD
//        );
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//
//        otpService.invalidateOtp(user, OtpPurpose.RESET_PASSWORD);
//
//        return ResponseEntity.ok(
//                Map.of("message", "Password reset successful")
//        );
//    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        authService.forgotPassword(body.get("loginId"));
        return ResponseEntity.ok(Map.of("message", "If account exists, OTP sent"));
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> body) {

        boolean valid = authService.verifyResetOtp(
                body.get("loginId"),
                body.get("otp")
        );

        if (!valid) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired OTP"));
        }

        return ResponseEntity.ok(Map.of("message", "OTP verified"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        authService.resetPassword(
                body.get("loginId"),
                body.get("newPassword")
        );
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }


}
