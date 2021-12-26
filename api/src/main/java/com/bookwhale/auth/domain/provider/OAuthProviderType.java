package com.bookwhale.auth.domain.provider;

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
