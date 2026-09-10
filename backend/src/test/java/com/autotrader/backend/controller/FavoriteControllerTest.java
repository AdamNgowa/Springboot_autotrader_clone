package com.autotrader.backend.controller;

import com.autotrader.backend.dto.favorite.FavoriteResponse;
import com.autotrader.backend.exception.ListingNotFoundException;
import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtService;
import com.autotrader.backend.service.FavoriteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void addFavorite_returnsNoContent() throws Exception {

        doNothing().when(favoriteService).addFavorite(1L);

        mockMvc.perform(post("/favorites/{listingId}", 1L))
                .andExpect(status().isNoContent());

        verify(favoriteService).addFavorite(1L);
    }

    @Test
    void addFavorite_whenListingNotFound_returnsNotFound() throws Exception {

        doThrow(new ListingNotFoundException("Listing not found"))
                .when(favoriteService).addFavorite(1L);

        mockMvc.perform(post("/favorites/{listingId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Listing not found"));
    }

    @Test
    void removeFavorite_returnsNoContent() throws Exception {

        doNothing().when(favoriteService).removeFavorite(1L);

        mockMvc.perform(delete("/favorites/{listingId}", 1L))
                .andExpect(status().isNoContent());

        verify(favoriteService).removeFavorite(1L);
    }

    @Test
    void getCurrentUserFavorites_returnsOkResponseWithFavoritesList() throws Exception {

        FavoriteResponse response = new FavoriteResponse();
        response.setId(5L);

        when(favoriteService.getCurrentUserFavorites())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5));

        verify(favoriteService).getCurrentUserFavorites();
    }

    @Test
    void getFavoriteStatus_whenFavorited_returnsTrue() throws Exception {

        when(favoriteService.isFavorite(1L)).thenReturn(true);

        mockMvc.perform(get("/favorites/{listingId}/status", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true));

        verify(favoriteService).isFavorite(1L);
    }

    @Test
    void getFavoriteStatus_whenNotFavorited_returnsFalse() throws Exception {

        when(favoriteService.isFavorite(1L)).thenReturn(false);

        mockMvc.perform(get("/favorites/{listingId}/status", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(false));

        verify(favoriteService).isFavorite(1L);
    }
}