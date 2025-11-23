package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {

        // Check if phone exists
        if (user.getPhoneNo() != null &&
                userRepository.findByPhoneNo(user.getPhoneNo()).isPresent()) {
            throw new RuntimeException("User with this phone number already exists!");
        }

        // Check if email exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists!");
        }

        return userRepository.save(user);
    }

    public User getUserByPhone(String phoneNo) {
        return userRepository.findByPhoneNo(phoneNo)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
