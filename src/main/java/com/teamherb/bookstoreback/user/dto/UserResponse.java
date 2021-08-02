package com.teamherb.bookstoreback.user.dto;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.dto.AccountResponse;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String identity;
    private String name;
    private String email;
    private List<AccountResponse> accountResponse;

    public static UserResponse of(User user, List<Account> accounts) {
        return new UserResponse(
            user.getIdentity(),
            user.getName(),
            user.getEmail(),
            AccountResponse.listOf(accounts)
        );
    }
}
