package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.request.LoginRequest;
import com.ecommerce.user_service.dto.request.RegisterRequest;
import com.ecommerce.user_service.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse registerUser(RegisterRequest request);

    String loginUser(LoginRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    void deleteUser(Long id);

}