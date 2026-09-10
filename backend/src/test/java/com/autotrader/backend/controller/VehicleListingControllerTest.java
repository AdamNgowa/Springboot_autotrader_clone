package com.autotrader.backend.controller;

import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.UpdateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.exception.ListingNotFoundException;
import com.autotrader.backend.exception.UnauthorizedListingAccessException;
import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtService;
import com.autotrader.backend.service.VehicleListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleListingController.class)
// addFilters = false stops the security filter chain from running on
// requests in this slice test. Controller-layer tests care about mapping,
// validation, and delegation to the service — not authentication.
@AutoConfigureMockMvc(addFilters = false)
class VehicleListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // VehicleListingService is mocked so the controller can be tested in
    // isolation from real listing business logic.
    @MockitoBean
    private VehicleListingService vehicleListingService;

    /*
     * Even with addFilters = false, @WebMvcTest still auto-registers any
     * Filter-typed bean it finds, so JwtAuthenticationFilter still gets
     * constructed (just never invoked). These two mocks satisfy that
     * filter's constructor dependencies so the context can load.
     */
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createListing_withValidRequest_returnsCreatedResponse() throws Exception {

        // Arrange: create the request body that a real client would send.
        String requestBody = """
                {
                    "title": "2019 Toyota Corolla 1.8 Hybrid",
                    "description": "Well maintained, accident free, full service history.",
                    "price": 1850000,
                    "year": 2019,
                    "make": "Toyota",
                    "model": "Corolla",
                    "mileage": 65000,
                    "fuelType": "PETROL",
                    "transmission": "AUTOMATIC",
                    "bodyType": "SEDAN",
                    "city": "Nairobi"
                }
                """;

        VehicleListingResponse response = new VehicleListingResponse(
                1L,
                "2019 Toyota Corolla 1.8 Hybrid",
                BigDecimal.valueOf(1850000),
                "Toyota",
                "Corolla",
                2019,
                "Nairobi",
                false
        );

        when(vehicleListingService.createListing(any(CreateListingRequest.class)))
                .thenReturn(response);

        // Act + Assert: verify the 201 status, Location header, and body.
        mockMvc.perform(post("/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/listings/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("2019 Toyota Corolla 1.8 Hybrid"));

        verify(vehicleListingService).createListing(any(CreateListingRequest.class));
    }

    @Test
    void createListing_withInvalidRequest_returnsBadRequestAndDoesNotCallService()
            throws Exception {

        // Arrange: omit required fields to trigger Bean Validation failures.
        String requestBody = """
                {
                    "title": "",
                    "description": "",
                    "make": "",
                    "model": "",
                    "mileage": -5,
                    "city": ""
                }
                """;

        mockMvc.perform(post("/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());

        verify(vehicleListingService, never()).createListing(any(CreateListingRequest.class));
    }

    @Test
    void getListings_returnsOkResponseWithPagedListings() throws Exception {

        VehicleListingResponse response = new VehicleListingResponse(
                1L, "2019 Toyota Corolla", BigDecimal.valueOf(1850000),
                "Toyota", "Corolla", 2019, "Nairobi", false
        );

        Page<VehicleListingResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        // @ModelAttribute always builds a criteria object (never null), so
        // any() matches it regardless of which query params were supplied.
        when(vehicleListingService.getListings(any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"));

        verify(vehicleListingService).getListings(any(), any(Pageable.class));
    }

    @Test
    void getCurrentUserListings_returnsOkResponseWithPagedListings() throws Exception {

        VehicleListingResponse response = new VehicleListingResponse(
                2L, "My Honda Civic", BigDecimal.valueOf(1200000),
                "Honda", "Civic", 2020, "Mombasa", false
        );

        Page<VehicleListingResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(vehicleListingService.getCurrentUserListings(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/listings/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2));

        verify(vehicleListingService).getCurrentUserListings(any(Pageable.class));
    }

    @Test
    void getListingById_whenListingExists_returnsOkResponse() throws Exception {

        VehicleListingResponse response = new VehicleListingResponse(
                1L, "2019 Toyota Corolla", BigDecimal.valueOf(1850000),
                "Toyota", "Corolla", 2019, "Nairobi", false
        );

        when(vehicleListingService.getListingById(1L)).thenReturn(response);

        mockMvc.perform(get("/listings/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(vehicleListingService).getListingById(1L);
    }

    @Test
    void getListingById_whenListingNotFound_returnsNotFound() throws Exception {

        when(vehicleListingService.getListingById(1L))
                .thenThrow(new ListingNotFoundException("Listing not found"));

        mockMvc.perform(get("/listings/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Listing not found"));
    }

    @Test
    void updateListing_withValidRequest_returnsOkResponse() throws Exception {

        String requestBody = """
                {
                    "title": "2019 Toyota Corolla 1.8 Hybrid",
                    "description": "Well maintained, accident free, full service history.",
                    "price": 1750000,
                    "year": 2019,
                    "make": "Toyota",
                    "model": "Corolla",
                    "mileage": 70000,
                    "fuelType": "PETROL",
                    "transmission": "AUTOMATIC",
                    "bodyType": "SEDAN",
                    "city": "Nairobi"
                }
                """;

        VehicleListingResponse response = new VehicleListingResponse(
                1L, "2019 Toyota Corolla 1.8 Hybrid", BigDecimal.valueOf(1750000),
                "Toyota", "Corolla", 2019, "Nairobi", false
        );

        when(vehicleListingService.updateListing(eq(1L), any(UpdateListingRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/listings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1750000));

        verify(vehicleListingService).updateListing(eq(1L), any(UpdateListingRequest.class));
    }

    @Test
    void updateListing_withInvalidRequest_returnsBadRequestAndDoesNotCallService()
            throws Exception {

        String requestBody = """
                {
                    "title": "",
                    "description": "",
                    "make": "",
                    "model": "",
                    "mileage": -5,
                    "city": ""
                }
                """;

        mockMvc.perform(put("/listings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());

        verify(vehicleListingService, never())
                .updateListing(any(), any(UpdateListingRequest.class));
    }

    @Test
    void updateListing_whenNotOwner_returnsForbidden() throws Exception {

        String requestBody = """
                {
                    "title": "2019 Toyota Corolla 1.8 Hybrid",
                    "description": "Well maintained, accident free, full service history.",
                    "price": 1750000,
                    "year": 2019,
                    "make": "Toyota",
                    "model": "Corolla",
                    "mileage": 70000,
                    "fuelType": "PETROL",
                    "transmission": "AUTOMATIC",
                    "bodyType": "SEDAN",
                    "city": "Nairobi"
                }
                """;

        when(vehicleListingService.updateListing(eq(1L), any(UpdateListingRequest.class)))
                .thenThrow(new UnauthorizedListingAccessException(
                        "You are not allowed to modify this listing"));

        mockMvc.perform(put("/listings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to modify this listing"));
    }

    @Test
    void deleteListing_returnsNoContent() throws Exception {

        doNothing().when(vehicleListingService).deleteListing(1L);

        mockMvc.perform(delete("/listings/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(vehicleListingService).deleteListing(1L);
    }

    @Test
    void deleteListing_whenListingNotFound_returnsNotFound() throws Exception {

        doThrow(new ListingNotFoundException("Listing not found"))
                .when(vehicleListingService).deleteListing(1L);

        mockMvc.perform(delete("/listings/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Listing not found"));
    }
}