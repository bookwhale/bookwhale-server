package com.bookwhale.user.controller;

import com.bookwhale.auth.domain.CurrentUser;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.UserPushSettingResponse;
import com.bookwhale.user.dto.UserResponse;
import com.bookwhale.user.dto.UserUpdateRequest;
import com.bookwhale.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@CurrentUser User user) {
        return ResponseEntity.ok(userService.getUserInfo(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@CurrentUser User user,
        @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateMyInfo(user, userUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/push-setting")
    public ResponseEntity<UserPushSettingResponse> getMyPushSetting(@CurrentUser User user) {
        return ResponseEntity.ok(userService.getUserPushSetting(user));
    }

    @PatchMapping("/me/push-setting")
    public ResponseEntity<UserPushSettingResponse> toggleMyPushSetting(@CurrentUser User user) {
        return ResponseEntity.ok(userService.updatePushSetting(user));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ProfileResponse> uploadProfileImage(@CurrentUser User user,
        @RequestParam MultipartFile profileImage) {
        return ResponseEntity.ok(userService.uploadProfileImage(user, profileImage));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfileImage(@CurrentUser User user) {
        userService.deleteProfileImage(user);
        return ResponseEntity.ok().build();
    }
}
