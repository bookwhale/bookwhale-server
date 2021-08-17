package com.teamherb.bookstoreback.account.controller;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.account.dto.AccountResponse;
import com.teamherb.bookstoreback.account.dto.AccountUpdateRequest;
import com.teamherb.bookstoreback.account.service.AccountService;
import com.teamherb.bookstoreback.security.CurrentUser;
import com.teamherb.bookstoreback.user.domain.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Void> createAccount(@CurrentUser User user,
        @Valid @RequestBody AccountRequest accountRequest)
        throws URISyntaxException {
        accountService.createAccount(user, accountRequest);
        return ResponseEntity.created(new URI("/api/account")).build();
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAccounts(@CurrentUser User user) {
        List<AccountResponse> accounts = accountService.findAccounts(user);
        return ResponseEntity.ok(accounts);
    }

    @PatchMapping
    public ResponseEntity<Void> findAccounts(@CurrentUser User user,
        AccountUpdateRequest accountUpdateRequest) {
        accountService.updateAccount(user, accountUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@CurrentUser User user,
        @PathVariable Long accountId) {
        accountService.deleteAccount(user, accountId);
        return ResponseEntity.ok().build();
    }
}
