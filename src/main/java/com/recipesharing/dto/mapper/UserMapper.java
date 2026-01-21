package com.recipesharing.dto.mapper;

import com.recipesharing.dto.request.UpdateUserProfileRequest;
import com.recipesharing.dto.response.UserProfileResponse;
import com.recipesharing.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    UserProfileResponse toUserProfileResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true) // optionally allow admin to change email via separate flow
    @Mapping(target = "password", ignore = true)
    void updateUserFromRequest(UpdateUserProfileRequest request, @MappingTarget User user);
}
