package com.bookwhale.user.dto;

import com.bookwhale.user.domain.User;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

    @NotBlank
    private String nickname;

    @Builder
    public UserUpdateRequest(String nickname) {
        this.nickname = nickname;
    }

    public User toEntity() {
        return User.builder()
            .nickname(nickname)
            .build();
    }
}
