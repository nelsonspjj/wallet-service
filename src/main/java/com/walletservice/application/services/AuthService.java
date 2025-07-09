package com.walletservice.application.services;

import com.walletservice.domain.dtos.UserDTO;

public interface AuthService {
    String register(UserDTO userDTO);
    String login(UserDTO userDTO);
}