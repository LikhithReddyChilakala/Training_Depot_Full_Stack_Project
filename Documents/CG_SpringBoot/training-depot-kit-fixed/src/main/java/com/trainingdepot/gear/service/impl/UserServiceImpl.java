package com.trainingdepot.gear.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trainingdepot.gear.model.Role;
import com.trainingdepot.gear.model.User;
import com.trainingdepot.gear.repository.UserRepository;
import com.trainingdepot.gear.service.EmailService;
import com.trainingdepot.gear.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public User register(String username, String rawPassword, String email, String phoneNumber) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("That depot username is already taken");
        }
        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setEmail(email.trim());
        user.setPhoneNumber(phoneNumber);
        user.setRole(Role.USER);
        User saved = userRepository.save(user);
        emailService.sendWelcomeEmail(saved);
        return saved;
    }

    @Override
    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    @Override
    public User getById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No depot member with id " + id));
    }
}
