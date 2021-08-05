package com.teamherb.bookstoreback.user.dto;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

    private String name;
    private String phoneNumber;
    private String address;
    private List<AccountRequest> accounts;

    @Builder
    public UserUpdateRequest(String name, String phoneNumber, String address,
        List<AccountRequest> accounts) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.accounts = accounts;
    }

    public List<Account> toAccounts(User user) {
        return accounts.stream().map(v -> v.toAccount(user)).collect(Collectors.toList());
    }
}
