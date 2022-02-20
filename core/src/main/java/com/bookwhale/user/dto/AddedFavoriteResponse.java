package com.bookwhale.user.dto;

import com.bookwhale.favorite.domain.Favorite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddedFavoriteResponse {

    private Long favoriteId;

    public static AddedFavoriteResponse of(Favorite favorite) {
        return new AddedFavoriteResponse(favorite.getId());
    }

    @Override
    public String toString() {
        return "AddedFavoriteResponse{" +
            "favoriteId=" + favoriteId +
            '}';
    }
}
