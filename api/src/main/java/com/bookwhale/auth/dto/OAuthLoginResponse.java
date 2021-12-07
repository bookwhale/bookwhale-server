package com.bookwhale.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OAuthLoginResponse {

    private String accessToken;

}
