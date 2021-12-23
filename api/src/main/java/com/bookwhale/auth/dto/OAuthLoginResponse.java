package com.bookwhale.auth.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import com.bookwhale.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class OAuthLoginResponse {

    private String apiToken;

    public OAuthLoginResponse(String apiToken) {
        checkNotNull(apiToken, ErrorCode.INVALID_ACCESS_TOKEN.getMessage());
        this.apiToken = apiToken;
    }
}