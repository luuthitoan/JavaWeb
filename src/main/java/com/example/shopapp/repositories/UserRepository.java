package com.example.shopapp.repositories;

import com.example.shopapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUserIdentifier(String userIdentifier);
    Optional<User> findByUserIdentifier(String userIdentifier);
    Optional<User> findById(Long id);
}
