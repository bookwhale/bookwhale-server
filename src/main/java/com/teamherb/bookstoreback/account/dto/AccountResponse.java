package com.teamherb.bookstoreback.account.dto;


import com.teamherb.bookstoreback.account.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private String accountNumber;
    private String accountBank;
    private String accountOwner;

    public static List<AccountResponse> listOf(List<Account> accounts) {
        return accounts.stream().map(
                account -> new AccountResponse(
                        account.getAccountNumber(),
                        account.getAccountBank(),
                        account.getAccountOwner()
                )).collect(Collectors.toList());
    }
}
