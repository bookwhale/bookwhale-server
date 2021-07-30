package com.teamherb.bookstoreback.account.dto;


import com.teamherb.bookstoreback.account.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private String accountNumber;
    private String accountBank;
    private String accountOwner;

    private static AccountResponse of(Account account) {
        return new AccountResponse(
            account.getAccountNumber(),
            account.getAccountBank(),
            account.getAccountOwner()
        );
    }
}
