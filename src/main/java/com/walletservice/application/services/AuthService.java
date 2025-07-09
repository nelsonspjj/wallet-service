package com.walletservice.application.services;

import com.walletservice.shared.dtos.UserDTO;

public interface AuthService {
    String register(UserDTO userDTO);
    String login(UserDTO userDTO);
}