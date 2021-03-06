package com.bookwhale.auth.service.provider;

public enum OAuthProviderType {
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    private final String providerName;

    OAuthProviderType(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }
}
