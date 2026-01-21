package com.recipesharing.service;

import com.recipesharing.dto.mapper.UserMapper;
import com.recipesharing.dto.request.UpdateUserProfileRequest;
import com.recipesharing.entity.User;
import com.recipesharing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getProfileByUsername_success() {

        User user = User.builder().username("john").build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        userService.getProfileByUsername("john");

        verify(userRepository).findByUsername("john");
    }

    @Test
    void updateMyProfile_updatesCurrentUser() {

        User currentUser = User.builder().id(1L).build();
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();

        userService.updateMyProfile(request, currentUser);

        verify(userRepository).save(currentUser);
    }
}
