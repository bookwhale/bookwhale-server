package com.bookwhale.user.dto;

import com.bookwhale.common.domain.ActiveYn;
import com.bookwhale.user.domain.User;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
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
    @JsonFormat(shape = Shape.STRING)
    private ActiveYn pushActivate;

    @Builder
    public UserResponse(Long userId, String nickName, String email, String profileImage,
        ActiveYn pushActivate) {
        this.userId = userId;
        this.nickName = nickName;
        this.email = email;
        this.profileImage = profileImage;
        this.pushActivate = pushActivate;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
            .userId(user.getId())
            .nickName(user.getNickname())
            .email(user.getEmail())
            .profileImage(user.getProfileImage())
            .pushActivate(user.getPushActivate())
            .build();
    }
}
