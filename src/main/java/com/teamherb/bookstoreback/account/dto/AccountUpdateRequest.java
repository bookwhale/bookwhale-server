package com.teamherb.bookstoreback.account.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountUpdateRequest {

    @NotBlank
    private Long accountId;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountBank;

    @NotBlank
    private String accountOwner;

    @Builder
    public AccountUpdateRequest(Long accountId, String accountNumber, String accountBank,
        String accountOwner) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.accountOwner = accountOwner;
    }
}
