package com.example.AuthService.repository;

import com.example.AuthService.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    User findUserById(Long id);
}
