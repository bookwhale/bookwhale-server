package com.bookwhale.user.controller;

import com.bookwhale.post.dto.PostsResponse;
import com.bookwhale.security.CurrentUser;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.*;
import com.bookwhale.user.service.LikeService;
import com.bookwhale.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final LikeService likeService;

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

  @PostMapping("/me/like")
  public ResponseEntity<Void> addLike(@CurrentUser User user,
      @Valid @RequestBody LikeRequest request) {
    likeService.addLike(user, request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/me/likes")
  public ResponseEntity<List<LikeResponse>> findMyLikes(@CurrentUser User user) {
    return ResponseEntity.ok(likeService.findAllLikes(user));
  }

  @DeleteMapping("/me/like/{likeId}")
  public ResponseEntity<Void> deleteLike(@CurrentUser User user,
      @PathVariable Long likeId) {
    likeService.deleteLike(user, likeId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/me/post")
  public ResponseEntity<List<PostsResponse>> findMyPosts(@CurrentUser User user) {
    List<PostsResponse> response = userService.findMyPost(user);
    return ResponseEntity.ok(response);
  }
}
