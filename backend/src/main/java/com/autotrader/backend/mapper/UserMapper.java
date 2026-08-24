package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.user.SellerResponse;
import com.autotrader.backend.dto.user.UserResponse;
import com.autotrader.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public SellerResponse toSellerResponse(User user) {
        return new SellerResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber()
        );
    }
}