package com.recipesharing.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String bio;
    private String profileImage;
}
