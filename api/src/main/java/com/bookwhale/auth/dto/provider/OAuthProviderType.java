package com.bookwhale.auth.dto.provider;

public enum OAuthProviderType {
    GOOGLE("google"),
    NAVER("naver");

    private final String providerName;

    OAuthProviderType(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }
}
