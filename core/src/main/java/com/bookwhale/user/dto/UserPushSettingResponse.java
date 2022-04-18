package com.bookwhale.user.dto;

import com.bookwhale.common.domain.ActiveYn;
import com.bookwhale.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPushSettingResponse {
    private Long userId;
    @JsonFormat(shape = Shape.STRING)
    private ActiveYn pushActivate;

    @Builder
    public UserPushSettingResponse(Long userId, ActiveYn pushActivate) {
        this.userId = userId;
        this.pushActivate = pushActivate;
    }

    public static UserPushSettingResponse of(User user) {
        return UserPushSettingResponse.builder()
            .userId(user.getId())
            .pushActivate(user.getPushActivate())
            .build();
    }
}
