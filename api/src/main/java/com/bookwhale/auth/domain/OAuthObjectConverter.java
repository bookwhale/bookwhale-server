package com.bookwhale.auth.domain;

import com.bookwhale.auth.domain.info.UserInfo;
import com.bookwhale.auth.domain.info.UserInfoFromGoogle;
import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.domain.token.GoogleOAuthToken;
import com.bookwhale.auth.domain.token.OAuthToken;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

@Slf4j
public class OAuthObjectConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static OAuthToken getTokenFromResponse(ResponseEntity<String> response,
        OAuthProviderType providerType) {
        OAuthToken oAuthToken = null;
        try {
            String responseBody = response.getBody();
            if (StringUtils.isEmpty(responseBody)) {
                throw new CustomException(ErrorCode.INFORMATION_NOT_FOUND);
            }

            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                String body = responseBody.replaceAll("\\R", "");
                oAuthToken = objectMapper.readValue(body, GoogleOAuthToken.class);
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                // TODO naver token 변환 추가
            }
        } catch (JsonProcessingException e) {
            log.debug("token converting failed.", e);
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return oAuthToken;
    }

    public static UserInfo getUserInfoFromProvider(ResponseEntity<String> response,
        OAuthProviderType providerType) {
        UserInfo userInfo = null;

        try {
            String responseBody = response.getBody();
            if (StringUtils.isEmpty(responseBody)) {
                throw new CustomException(ErrorCode.INFORMATION_NOT_FOUND);
            }

            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                String body = responseBody.replaceAll("\\R", "");
                userInfo = objectMapper.readValue(body, UserInfoFromGoogle.class);
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                // TODO naver user정보 변환 추가
            }
        } catch (JsonProcessingException e) {
            log.debug("token converting failed.", e);
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return userInfo;
    }
}
