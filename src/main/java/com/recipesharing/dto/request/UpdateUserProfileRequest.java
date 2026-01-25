package com.recipesharing.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequest {
    @Size(max = 100)
    private String fullName;

    @Size(max = 500)
    private String bio;
}
