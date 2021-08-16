package com.teamherb.bookstoreback.account.dto;


import com.teamherb.bookstoreback.account.domain.Account;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountResponse {

    private String accountNumber;
    private String accountBank;
    private String accountOwner;

    @Builder
    public AccountResponse(String accountNumber, String accountBank, String accountOwner) {
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.accountOwner = accountOwner;
    }

    public static List<AccountResponse> listOf(List<Account> accounts) {
        return accounts.stream().map(
            account -> AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .accountOwner(account.getAccountBank())
                .accountBank(account.getAccountOwner())
                .build()
        ).collect(Collectors.toList());
    }
}
