package com.teamherb.bookstoreback.user.dto;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    private String identity;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    private AccountRequest accountRequest;

    @Builder
    public SignUpRequest(String identity, String password, String name, String email,
        AccountRequest accountRequest) {
        this.identity = identity;
        this.password = password;
        this.name = name;
        this.email = email;
        this.accountRequest = accountRequest;
    }
}
