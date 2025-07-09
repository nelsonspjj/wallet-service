package com.walletservice.application.services.impl;

import com.walletservice.domain.dtos.UserDTO;
import com.walletservice.domain.model.User;
import com.walletservice.infrastructure.repository.UserRepository;
import com.walletservice.infrastructure.security.JwtService;
import com.walletservice.shared.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Test
    public void register_ShouldRegisterUser_WhenUserDoesNotExist() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(null);

        String result = authService.register(userDTO);

        assertEquals("User registered successfully", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void register_ShouldThrowException_WhenUserAlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpassword");

        User existingUser = new User("testuser", "somePassword");
        when(userRepository.findByUsername("testuser")).thenReturn(existingUser);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(userDTO);
        });
        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void login_ShouldReturnToken_WhenCredentialsAreValid() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpassword");

        User user = new User("testuser", "testpassword");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mockedToken");

        String token = authService.login(userDTO);

        assertEquals("mockedToken", token);
        verify(jwtService).generateToken(user);
    }

    @Test
    public void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("wrongPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(new User("testuser", "testpassword"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(userDTO);
        });
        assertEquals("Invalid username or password", exception.getMessage());
    }
}