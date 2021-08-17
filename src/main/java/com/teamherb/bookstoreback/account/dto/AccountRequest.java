package com.teamherb.bookstoreback.account.dto;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.user.domain.User;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountRequest {

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountBank;

    @NotBlank
    private String accountOwner;

    @Builder
    public AccountRequest(String accountNumber, String accountBank, String accountOwner) {
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.accountOwner = accountOwner;
    }

    public Account toAccount(User user) {
        return Account.builder()
            .user(user)
            .accountBank(accountBank)
            .accountNumber(accountNumber)
            .accountOwner(accountOwner)
            .build();
    }
}
