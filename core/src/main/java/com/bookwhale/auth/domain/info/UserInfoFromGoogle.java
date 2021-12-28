package com.bookwhale.auth.domain.info;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInfoFromGoogle implements UserInfo {

    private String id;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("name", name)
            .append("email", email)
            .append("picture", picture)
            .toString();
    }
}
