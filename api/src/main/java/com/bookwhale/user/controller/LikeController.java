package com.bookwhale.user.controller;

import com.bookwhale.security.CurrentUser;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.LikeRequest;
import com.bookwhale.user.dto.LikeResponse;
import com.bookwhale.user.service.LikeService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/me")
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  @PostMapping("/like")
  public ResponseEntity<Void> addLike(@CurrentUser User user,
      @Valid @RequestBody LikeRequest request) {
    likeService.addLike(user, request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/likes")
  public ResponseEntity<List<LikeResponse>> findMyLikes(@CurrentUser User user) {
    return ResponseEntity.ok(likeService.findAllLikes(user));
  }

  @DeleteMapping("like/{likeId}")
  public ResponseEntity<Void> deleteLike(@CurrentUser User user,
      @PathVariable Long likeId) {
    likeService.deleteLike(user, likeId);
    return ResponseEntity.ok().build();
  }
}
