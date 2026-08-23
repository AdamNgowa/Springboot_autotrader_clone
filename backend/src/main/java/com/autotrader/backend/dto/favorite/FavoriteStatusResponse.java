package com.autotrader.backend.dto.favorite;

public class FavoriteStatusResponse {

    private boolean favorite;

    public FavoriteStatusResponse() {
    }

    public FavoriteStatusResponse(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}