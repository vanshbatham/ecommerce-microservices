package com.ecommerce.user_service.service;

import com.ecommerce.user_service.entity.dto.request.UserRequest;
import com.ecommerce.user_service.entity.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    void deleteUser(Long id);

    String loginUser(String email);
}