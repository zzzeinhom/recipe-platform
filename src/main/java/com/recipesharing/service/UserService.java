package com.recipesharing.service;

import com.recipesharing.dto.request.UpdateUserProfileRequest;
import com.recipesharing.dto.response.UserProfileResponse;
import com.recipesharing.dto.mapper.UserMapper;
import com.recipesharing.entity.User;
import com.recipesharing.entity.UserRole;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // Public profile by username
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.toUserProfileResponse(user);
    }

    // My profile (current user)
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(User currentUser) {
        // currentUser is already loaded by security; return mapped DTO
        return userMapper.toUserProfileResponse(currentUser);
    }

    // Update my profile (only affects currentUser)
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateUserProfileRequest request, User currentUser) {
        userMapper.updateUserFromRequest(request, currentUser);
        User updated = userRepository.save(currentUser);
        return userMapper.toUserProfileResponse(updated);
    }

    // Admin: update any user profile (partial)
    @Transactional
    public UserProfileResponse updateUserAsAdmin(Long userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userMapper.updateUserFromRequest(request, user);
        return userMapper.toUserProfileResponse(userRepository.save(user));
    }

    // List users (admin)
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserProfileResponse);
    }

    // Admin: promote user to CHEF
    @Transactional
    public UserProfileResponse promoteToChef(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (user.getRole() == UserRole.CHEF) {
            throw new BadRequestException("User is already a chef");
        }
        user.setRole(UserRole.CHEF);
        return userMapper.toUserProfileResponse(userRepository.save(user));
    }

    // Admin: promote user to ADMIN
    @Transactional
    public UserProfileResponse promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (user.getRole() == UserRole.ADMIN) {
            throw new BadRequestException("User is already an admin");
        }
        user.setRole(UserRole.ADMIN);
        return userMapper.toUserProfileResponse(userRepository.save(user));
    }
}
