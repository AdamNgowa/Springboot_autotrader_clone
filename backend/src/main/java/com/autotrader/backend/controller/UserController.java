package com.autotrader.backend.controller;

import com.autotrader.backend.dto.user.UserResponse;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.mapper.UserMapper;
import com.autotrader.backend.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(
        name = "Users",
        description = "Endpoints for authenticated user information"
)
public class UserController {

    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    public UserController(
            CurrentUserService currentUserService,
            UserMapper userMapper) {

        this.currentUserService = currentUserService;
        this.userMapper = userMapper;
    }

    @Operation(
            summary = "Get the authenticated user",
            description = """
                    Returns the profile information of the currently
                    authenticated user based on the supplied JWT.
                    """
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        User user = currentUserService.getAuthenticatedUser();

        UserResponse response = userMapper.toResponse(user);

        return ResponseEntity.ok(response);
    }
}   