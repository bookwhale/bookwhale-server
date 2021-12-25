package com.bookwhale.auth.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.bookwhale.common.exception.ErrorCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class OAuthLoginResponse {

    private final String apiToken;
    private final String refreshToken;

    public OAuthLoginResponse(String apiToken, String refreshToken) {
        checkArgument(StringUtils.isNotEmpty(apiToken) || StringUtils.isNotEmpty(refreshToken),
            ErrorCode.INVALID_TOKEN.getMessage());
        this.apiToken = apiToken;
        this.refreshToken = refreshToken;
    }
}