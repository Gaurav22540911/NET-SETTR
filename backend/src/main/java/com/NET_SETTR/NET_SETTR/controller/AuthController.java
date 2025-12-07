package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.dto.AuthResponse;
import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
