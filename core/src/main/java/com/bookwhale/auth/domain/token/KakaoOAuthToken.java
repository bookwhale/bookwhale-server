package com.bookwhale.auth.domain.token;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoOAuthToken implements OAuthToken {

    private String tokenType;
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private Integer refreshTokenExpiresIn;
    private String scope;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("tokenType", tokenType)
            .append("accessToken", accessToken)
            .append("expiresIn", expiresIn)
            .append("refreshToken", refreshToken)
            .append("refreshTokenExpiresIn", refreshTokenExpiresIn)
            .append("scope", scope)
            .toString();
    }
}
