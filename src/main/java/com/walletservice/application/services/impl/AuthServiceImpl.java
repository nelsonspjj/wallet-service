package com.walletservice.application.services.impl;

import com.walletservice.application.services.AuthService;
import com.walletservice.domain.model.User;
import com.walletservice.infrastructure.repository.UserRepository;
import com.walletservice.infrastructure.security.JwtService;
import com.walletservice.domain.dtos.UserDTO;
import com.walletservice.shared.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public String register(UserDTO userDTO) {
        log.info("Attempting to register user: " + userDTO.getUsername());
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = new User(userDTO.getUsername(), userDTO.getPassword());
        log.info("Registering user: " + user.getUsername());
        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public String login(UserDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user == null || !user.getPassword().equals(userDTO.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return jwtService.generateToken(user);
    }
}