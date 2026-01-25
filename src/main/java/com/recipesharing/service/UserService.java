package com.recipesharing.service;

import com.recipesharing.dto.image.ProcessedImage;
import com.recipesharing.dto.request.UpdateUserProfileRequest;
import com.recipesharing.dto.response.UserProfileResponse;
import com.recipesharing.dto.mapper.UserMapper;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.entity.UserRole;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.UserRepository;
import com.recipesharing.util.ImageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ImageProcessingService imageProcessingService;
    private final LocalFileStorageService fileStorageService;
    private final ImageUtil imageUtil;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper, ImageProcessingService imageProcessingService, LocalFileStorageService fileStorageService, ImageUtil imageUtil
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.imageProcessingService = imageProcessingService;
        this.fileStorageService = fileStorageService;
        this.imageUtil = imageUtil;
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
        return userMapper.toUserProfileResponse(currentUser);
    }

    // Update my profile
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateUserProfileRequest request, User currentUser) {
        userMapper.updateUserFromRequest(request, currentUser);

        User updated = userRepository.save(currentUser);
        return userMapper.toUserProfileResponse(updated);
    }

    // Update my profile avatar
    @Transactional
    public UserProfileResponse updateAvatar(MultipartFile avatar, User currentUser) {

        ProcessedImage processed = imageProcessingService.process(avatar);

        String dir = "avatars/" + currentUser.getUsername();
        String mainFilename = "image." + processed.format();

        String mainUrl = fileStorageService.store(processed.mainImage(), dir, mainFilename);

        imageUtil.cleanupUserImageFiles(currentUser);

        currentUser.setProfileImage(mainUrl);

        User updated = userRepository.save(currentUser);
        return userMapper.toUserProfileResponse(updated);
    }

    @Transactional
    public void deleteAvatar(User currentUser) {
        currentUser.setProfileImage(null);
        userRepository.save(currentUser);

        imageUtil.cleanupUserImageFiles(currentUser);
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
