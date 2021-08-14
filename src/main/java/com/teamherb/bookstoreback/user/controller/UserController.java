package com.teamherb.bookstoreback.user.controller;

import com.teamherb.bookstoreback.purchase.dto.PurchaseResponse;
import com.teamherb.bookstoreback.security.CurrentUser;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.service.UserService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/access-test")
    public ResponseEntity<UserResponse> accessTest(@CurrentUser User user) {
        return ResponseEntity.ok(UserResponse.of(user));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        Long userId = userService.createUser(signUpRequest);
        return ResponseEntity.created(URI.create("/api/user/me" + userId)).build();
    }

    @GetMapping("/purchase-history")
    public ResponseEntity<List<PurchaseResponse>> findPurchaseHistories(
        @CurrentUser User user) {
        List<PurchaseResponse> res = userService.findPurchaseHistories(user);
        return ResponseEntity.ok(res);
    }
}
