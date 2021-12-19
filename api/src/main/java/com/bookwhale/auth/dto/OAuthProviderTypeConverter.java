package com.bookwhale.auth.dto;

import com.bookwhale.auth.domain.provider.OAuthProviderType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class OAuthProviderTypeConverter implements Converter<String, OAuthProviderType> {

    @Override
    public OAuthProviderType convert(String parameter) {
        return OAuthProviderType.valueOf(parameter.toUpperCase());
    }
}
