package com.bookwhale.post.dto;

import com.bookwhale.post.domain.Post;
import com.bookwhale.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PostsResponse {

    private Long postId;
    private String postImage;
    private String postTitle;
    private String postPrice;
    private String postStatus;
    private String sellingLocation;
    private String description;
    private Long viewCount;
    private Long likeCount;
    private String beforeTime;

    @Builder
    public PostsResponse(Long postId, String postImage, String postTitle, String postPrice,
        String postStatus, String sellingLocation, String description, Long viewCount,
        Long likeCount, String beforeTime) {
        this.postId = postId;
        this.postImage = postImage;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.postStatus = postStatus;
        this.sellingLocation = sellingLocation;
        this.description = description;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.beforeTime = beforeTime;
    }

    public static PostsResponse of(Post post, String postImage, LocalDateTime currentTime) {
        return PostsResponse.builder()
            .postId(post.getId())
            .postImage(postImage)
            .postTitle(post.getTitle())
            .postPrice(post.getPrice())
            .postStatus(post.getPostStatus().getName())
            .sellingLocation(post.getSellingLocation().get().getName())
            .description(post.getDescription())
            .viewCount(post.getViewCount())
            .likeCount(post.getLikeCount())
            .beforeTime(TimeUtils.BeforeTime(currentTime, post.getCreatedDate()))
            .build();
    }

    public static List<PostsResponse> listOf(List<Post> posts) {
        LocalDateTime cur = LocalDateTime.now();
        return posts.stream()
            .map(p -> PostsResponse.of(p, p.getImages().getFirstImageUrl(), cur))
            .collect(Collectors.toList());
    }
}
