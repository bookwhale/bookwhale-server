package com.bookwhale.auth.dto.provider;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class OAuthProviderTypeConverter implements Converter<String, OAuthProviderType> {

    @Override
    public OAuthProviderType convert(String parameter) {
        return OAuthProviderType.valueOf(parameter.toUpperCase());
    }
}
