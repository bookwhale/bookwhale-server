package com.bookwhale.user.dto;

import com.bookwhale.user.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {

    private Long userId;
    private String nickName;
    private String email;
    private String profileImage;

    @Builder
    public UserResponse(Long userId, String nickName, String email, String profileImage) {
        this.userId = userId;
        this.nickName = nickName;
        this.email = email;
        this.profileImage = profileImage;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
            .userId(user.getId())
            .nickName(user.getNickname())
            .email(user.getEmail())
            .profileImage(user.getProfileImage())
            .build();
    }
}
