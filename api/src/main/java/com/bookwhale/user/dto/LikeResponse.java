package com.bookwhale.user.dto;

import com.bookwhale.like.domain.Like;
import com.bookwhale.post.domain.Post;
import com.bookwhale.post.dto.PostsResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LikeResponse {

  private Long likeId;

  private PostsResponse postsResponse;

  public LikeResponse(Long likeId,
                      PostsResponse postsResponse) {
    this.likeId = likeId;
    this.postsResponse = postsResponse;
  }

  public static List<LikeResponse> listOf(List<Like> likes) {
    LocalDateTime cur = LocalDateTime.now();
    return likes.stream().map(likedPost -> {
      Post post = likedPost.getPost();
      return new LikeResponse(likedPost.getId(),
          PostsResponse.of(post, post.getImages().getFirstImageUrl(), cur));
    }).collect(Collectors.toList());
  }
}
