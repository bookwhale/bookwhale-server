package com.bookwhale.auth.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.bookwhale.common.exception.ErrorCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class OAuthLoginResponse {

    private final String apiToken;

    public OAuthLoginResponse(String apiToken) {
        checkArgument(StringUtils.isNotEmpty(apiToken), ErrorCode.INVALID_ACCESS_TOKEN.getMessage());
        this.apiToken = apiToken;
    }
}