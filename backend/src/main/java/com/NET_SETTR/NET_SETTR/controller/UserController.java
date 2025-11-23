package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    // POST: Register a new user
    @PostMapping("/signup")
    public User signUpUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // GET: Fetch user by phone number
    @GetMapping("/{phoneNo}")
    public User getUser(@PathVariable String phoneNo) {
        return userService.getUserByPhone(phoneNo);
    }
}
