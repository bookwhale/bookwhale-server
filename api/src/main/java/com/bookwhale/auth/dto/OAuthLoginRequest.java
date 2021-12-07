package com.bookwhale.auth.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@AllArgsConstructor
public class OAuthLoginRequest {

    @NotBlank(message = "OAuth 공급자는 필수 입력입니다.")
    private String provider;
    @NotBlank(message = "인증 코드는 필수 입력입니다.")
    private String authCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OAuthLoginRequest that = (OAuthLoginRequest) o;

        return new EqualsBuilder().append(provider, that.provider)
            .append(authCode, that.authCode).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(provider).append(authCode).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("provider", provider)
            .append("authCode", authCode)
            .toString();
    }
}
