package com.bookwhale.auth.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@AllArgsConstructor
public class OAuthRefreshLoginRequest {
    @NotBlank(message = "API token은 필수 입력입니다.")
    private final String apiToken;
    @NotBlank(message = "refresh token은 필수 입력입니다.")
    private String refreshToken;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("apiToken", apiToken)
            .append("refreshToken", refreshToken)
            .toString();
    }
}
