package com.recipesharing.controller;

import com.recipesharing.dto.request.UpdateUserProfileRequest;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.dto.response.UserProfileResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.FavoriteService;
import com.recipesharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // GET /api/users/{username} - public profile
    @Operation(summary = "Get user profile by username", description = "Retrieves public profile information of a user by their username. Public endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfileByUsername(username));
    }

    // GET /api/users/me - my profile
    @Operation(summary = "Get current user profile", description = "Retrieves the profile information of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getMyProfile(currentUser));
    }

    // PUT /api/users/me - update my profile
    @Operation(summary = "Update current user profile", description = "Updates the profile information of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(userService.updateMyProfile(request, currentUser));
    }

    @Operation(summary = "Get user favorite recipes", description = "Retrieves paginated list of recipes marked as favorites by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite recipes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping("/me/favorites")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public Page<RecipeListResponse> myFavorites(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        return favoriteService.getUserFavorites(currentUser, pageable);
    }


    // Admin endpoints:

    // GET /api/users - list all users (admin)
    @Operation(summary = "List all users (Admin)", description = "Retrieves a paginated list of all users in the system. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(pageable));
    }

    // PUT /api/users/{id} - update user as admin (partial update)
    @Operation(summary = "Update user profile (Admin)", description = "Updates a user's profile as an admin. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateUserAsAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateUserAsAdmin(id, request));
    }

    // PUT /api/users/{id}/promote/chef
    @Operation(summary = "Promote user to chef (Admin)", description = "Promotes a user to CHEF role. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User promoted to chef successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/promote/chef")
    public ResponseEntity<UserProfileResponse> promoteToChef(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToChef(id));
    }

    // PUT /api/users/{id}/promote/admin
    @Operation(summary = "Promote user to admin (Admin)", description = "Promotes a user to ADMIN role. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User promoted to admin successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/promote/admin")
    public ResponseEntity<UserProfileResponse> promoteToAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToAdmin(id));
    }
}
