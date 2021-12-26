package com.bookwhale.auth.domain.info;

import com.bookwhale.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Builder
@Getter
@AllArgsConstructor
public class UserInfoFromToken implements UserInfo {

    private String name;
    private String email;
    private String picture;

    public static UserInfoFromToken of(User user) {
        return UserInfoFromToken.builder()
            .name(user.getNickname())
            .email(user.getEmail())
            .picture(user.getProfileImage())
            .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("name", name)
            .append("email", email)
            .append("picture", picture)
            .toString();
    }
}
