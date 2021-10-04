package com.teamherb.bookstoreback.Interest.dto;

import com.teamherb.bookstoreback.Interest.domain.Interest;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.dto.PostsResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InterestResponse {

  private Long interestId;

  private PostsResponse postsResponse;

  public InterestResponse(Long interestId,
      PostsResponse postsResponse) {
    this.interestId = interestId;
    this.postsResponse = postsResponse;
  }

  public static List<InterestResponse> listOf(List<Interest> interests) {
    LocalDateTime cur = LocalDateTime.now();
    return interests.stream().map(v -> {
      Post post = v.getPost();
      return new InterestResponse(v.getId(),
          PostsResponse.of(post, post.getImages().getFirstImageUrl(), cur));
    }).collect(Collectors.toList());
  }
}
