package com.teamherb.bookstoreback.user.controller;

import com.teamherb.bookstoreback.Interest.dto.InterestRequest;
import com.teamherb.bookstoreback.Interest.dto.InterestResponse;
import com.teamherb.bookstoreback.security.CurrentUser;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.dto.PasswordUpdateRequest;
import com.teamherb.bookstoreback.user.dto.ProfileResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import com.teamherb.bookstoreback.user.service.UserService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<Void> signup(@Valid @RequestBody SignUpRequest signUpRequest)
      throws URISyntaxException {
    userService.createUser(signUpRequest);
    return ResponseEntity.created(new URI("/api/user/login")).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMyInfo(@CurrentUser User user) {
    return ResponseEntity.ok(UserResponse.of(user));
  }

  @PatchMapping("/me")
  public ResponseEntity<Void> updateMyInfo(@CurrentUser User user,
      @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
    userService.updateMyInfo(user, userUpdateRequest);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/password")
  public ResponseEntity<Void> updatePassword(@CurrentUser User user,
      @Valid @RequestBody PasswordUpdateRequest request) {
    userService.updatePassword(user, request);
    return ResponseEntity.ok().build();
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

  @GetMapping("/me/interests")
  public ResponseEntity<List<InterestResponse>> findInterests(@CurrentUser User user) {
    return ResponseEntity.ok(userService.findInterests(user));
  }

  @PostMapping("/me/interest")
  public ResponseEntity<Void> addInterest(@CurrentUser User user,
      @Valid @RequestBody InterestRequest request) {
    userService.addInterest(user, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/me/interest/{interestId}")
  public ResponseEntity<Void> deleteInterest(@CurrentUser User user,
      @PathVariable Long interestId) {
    userService.deleteInterest(user, interestId);
    return ResponseEntity.ok().build();
  }
}
