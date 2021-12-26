package com.bookwhale.auth.dto;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class OAuthTokenExtractor {

    public static String extract(HttpServletRequest request) {
        String result = null;
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            result = bearerToken.substring(7);
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return result;
    }
}
