package com.bookwhale.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiUser {

    private String name;
    private String image;
    private String email;

    @Override
    public String toString() {
        return "ApiUser{" +
            "name='" + name + '\'' +
            ", image='" + image + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
