package com.trainingdepot.gear.service;

import java.util.Optional;

import com.trainingdepot.gear.model.User;

public interface UserService {

    /** Creates a USER-role account. Throws IllegalArgumentException if the username is taken. */
    User register(String username, String rawPassword, String email, String phoneNumber);

    /** Returns the user only if the username exists and the password matches. */
    Optional<User> authenticate(String username, String rawPassword);

    User getById(int id);
}
