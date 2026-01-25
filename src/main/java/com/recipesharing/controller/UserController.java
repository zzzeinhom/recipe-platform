package com.recipesharing.controller;

import com.recipesharing.dto.request.UpdateUserProfileRequest;
import com.recipesharing.dto.response.PagedResponse;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.dto.response.UserProfileResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.FavoriteService;
import com.recipesharing.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User Profile", description = "User & chef profile management")
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final FavoriteService favoriteService;

    public UserController(UserService userService, FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfileByUsername(username));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getMyProfile(currentUser));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(userService.updateMyProfile(request, currentUser));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateAvatar(
            @RequestParam MultipartFile avatar,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(userService.updateAvatar(avatar, currentUser));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/me/avatar")
    public ResponseEntity<Void> deleteAvatar(
            @AuthenticationPrincipal User currentUser
    ) {
        userService.deleteAvatar(currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/favorites")
    @PreAuthorize("isAuthenticated()")
    public Page<RecipeListResponse> myFavorites(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return favoriteService.getUserFavorites(currentUser, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.listUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateUserAsAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateUserAsAdmin(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/promote/chef")
    public ResponseEntity<UserProfileResponse> promoteToChef(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToChef(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/promote/admin")
    public ResponseEntity<UserProfileResponse> promoteToAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToAdmin(id));
    }
}