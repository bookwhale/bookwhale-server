package com.bookwhale.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OAuthLoginResponse {

    private String accessToken;

}
