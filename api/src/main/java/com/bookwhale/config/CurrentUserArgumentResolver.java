package com.bookwhale.config;

import com.bookwhale.auth.domain.CurrentUser;
import com.bookwhale.auth.dto.OAuthTokenExtractor;
import com.bookwhale.auth.service.OauthService;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final OauthService oauthService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(
            HttpServletRequest.class);
        if (httpServletRequest == null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        String apiToken = OAuthTokenExtractor.extract(httpServletRequest);
        return oauthService.getUserFromApiToken(apiToken);
    }
}
