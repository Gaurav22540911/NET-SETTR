package com.NET_SETTR.NET_SETTR.repository;

import com.NET_SETTR.NET_SETTR.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNo(String phoneNo);

    Optional<User> findByEmailOrPhoneNo(String email, String phoneNo);
}
