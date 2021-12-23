package com.bookwhale.auth.domain;

import com.bookwhale.auth.domain.info.UserInfo;
import com.bookwhale.auth.domain.info.UserInfoFromGoogle;
import com.bookwhale.auth.domain.info.UserInfoFromNaver;
import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.domain.token.GoogleOAuthToken;
import com.bookwhale.auth.domain.token.NaverOAuthToken;
import com.bookwhale.auth.domain.token.OAuthToken;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
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
            String body = responseBody.replaceAll("\\R", "");
            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                oAuthToken = objectMapper.readValue(body, GoogleOAuthToken.class);
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                oAuthToken = objectMapper.readValue(body, NaverOAuthToken.class);
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
            String body = responseBody.replaceAll("\\R", "");

            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                userInfo = objectMapper.readValue(body, UserInfoFromGoogle.class);
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                Map<String, Object> map = objectMapper.readValue(body, Map.class);
                // key = "response"에서 유효한 정보 확인 가능
                var infoMap = (LinkedHashMap<String, String>) map.get("response");

                userInfo = objectMapper.convertValue(infoMap, UserInfoFromNaver.class);
            }
        } catch (JsonProcessingException e) {
            log.debug("token converting failed.", e);
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        } catch (Exception e) {
            log.debug("unexpected exception occurred.", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return userInfo;
    }
}
