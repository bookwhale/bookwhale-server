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
public class GoogleOAuthToken implements OAuthToken {

    private String accessToken;
    private String expiresIn;
    private String idToken;
    private String refreshToken;
    private String scope;
    private String tokenType;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("tokenType", tokenType)
            .append("expiresIn", expiresIn)
            .append("accessToken", accessToken)
            .append("refreshToken", refreshToken)
            .toString();
    }
}
