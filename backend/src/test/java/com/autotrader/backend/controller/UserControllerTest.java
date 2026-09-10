package com.autotrader.backend.controller;

import com.autotrader.backend.dto.user.SellerResponse;
import com.autotrader.backend.dto.user.UserResponse;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.entity.Enums.UserRole;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.UserNotFoundException;
import com.autotrader.backend.mapper.UserMapper;
import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtService;
import com.autotrader.backend.service.CurrentUserService;
import com.autotrader.backend.service.UserService;
import com.autotrader.backend.service.VehicleListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrentUserService currentUserService;

    // UserMapper is mocked because entity -> DTO conversion is not part of
    // what this controller test is responsible for verifying.
    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private VehicleListingService vehicleListingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getCurrentUser_returnsOkResponse() throws Exception {

        User authenticatedUser = new User();

        UserResponse response = new UserResponse(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "0712345678",
                UserRole.USER,
                LocalDateTime.now()
        );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(authenticatedUser);

        when(userMapper.toResponse(authenticatedUser))
                .thenReturn(response);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(currentUserService).getAuthenticatedUser();
        verify(userMapper).toResponse(authenticatedUser);
    }

    @Test
    void getSellerProfile_whenSellerExists_returnsOkResponse() throws Exception {

        User seller = new User();

        SellerResponse response =
                new SellerResponse(5L, "Jane", "Smith", "0798765432");

        when(userService.getUserById(5L)).thenReturn(seller);
        when(userMapper.toSellerResponse(seller)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.firstName").value("Jane"));

        verify(userService).getUserById(5L);
        verify(userMapper).toSellerResponse(seller);
    }

    @Test
    void getSellerProfile_whenSellerNotFound_returnsNotFound() throws Exception {

        when(userService.getUserById(5L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/{id}", 5L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void getSellerListings_returnsOkResponseWithPagedListings() throws Exception {

        User seller = new User();

        VehicleListingResponse listingResponse = new VehicleListingResponse(
                10L, "2019 Toyota Corolla", null,
                "Toyota", "Corolla", 2019, "Nairobi", false
        );

        Page<VehicleListingResponse> page =
                new PageImpl<>(List.of(listingResponse), PageRequest.of(0, 10), 1);

        when(userService.getUserById(5L)).thenReturn(seller);
        when(vehicleListingService.getSellerActiveListings(eq(seller), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/users/{id}/listings", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].title").value("2019 Toyota Corolla"));

        verify(vehicleListingService)
                .getSellerActiveListings(eq(seller), any(Pageable.class));
    }
}