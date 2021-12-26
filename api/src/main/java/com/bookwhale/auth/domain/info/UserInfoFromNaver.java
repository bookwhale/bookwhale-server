package com.bookwhale.auth.domain.info;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInfoFromNaver implements UserInfo {
    private String id;
    private String nickname;
    private String name;
    private String email;
    private String profile_image;

    public String getPicture() {
        return profile_image;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("name", name)
            .append("email", email)
            .append("picture", profile_image)
            .toString();
    }
}
