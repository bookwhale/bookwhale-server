package com.bookwhale.post.dto;

import com.bookwhale.image.domain.Image;
import com.bookwhale.post.domain.Post;
import com.bookwhale.utils.TimeUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostResponse {

    private Long sellerId;
    private String sellerIdentity;
    private String sellerProfileImage;
    private Long postId;
    private String title;
    private String price;
    private String description;
    private String bookStatus;
    private String postStatus;
    private String sellingLocation;
    private List<String> images;
    private BookResponse bookResponse;
    private boolean isMyPost;
    private boolean isMyLike;
    private Long viewCount;
    private Long likeCount;
    private String beforeTime;

    @Builder
    public PostResponse(Long sellerId, String sellerIdentity, String sellerProfileImage,
        Long postId, String title, String price, String description, String bookStatus,
        String postStatus, String sellingLocation, List<String> images,
        BookResponse bookResponse, boolean isMyPost, boolean isMyLike, Long viewCount,
        Long likeCount, String beforeTime) {
        this.sellerId = sellerId;
        this.sellerIdentity = sellerIdentity;
        this.sellerProfileImage = sellerProfileImage;
        this.postId = postId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.bookStatus = bookStatus;
        this.postStatus = postStatus;
        this.sellingLocation = sellingLocation;
        this.images = images;
        this.bookResponse = bookResponse;
        this.isMyPost = isMyPost;
        this.isMyLike = isMyLike;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.beforeTime = beforeTime;
    }

    public static PostResponse of(Post post, boolean isMyPost,
        boolean isMyLike) {
        BookResponse bookResponse = BookResponse.builder()
            .bookAuthor(post.getBook().getBookAuthor())
            .bookIsbn(post.getBook().getBookIsbn())
            .bookListPrice(post.getBook().getBookListPrice())
            .bookPubDate(post.getBook().getBookPubDate())
            .bookPublisher(post.getBook().getBookPublisher())
            .bookSummary(post.getBook().getBookSummary())
            .bookThumbnail(post.getBook().getBookThumbnail())
            .bookTitle(post.getBook().getBookTitle())
            .build();

        List<String> imageResponse = post.getImages().getImages().stream().map(Image::getUrl)
            .collect(Collectors.toList());

        return PostResponse.builder()
            .bookResponse(bookResponse)
            .sellerId(post.getSeller().getId())
            .sellerIdentity(post.getSeller().getIdentity())
            .sellerProfileImage(post.getSeller().getProfileImage())
            .postId(post.getId())
            .title(post.getTitle())
            .price(post.getPrice())
            .description(post.getDescription())
            .bookStatus(post.getBookStatus().getName())
            .postStatus(post.getPostStatus().getName())
            .sellingLocation(post.getSellingLocation().isPresent() ?
                post.getSellingLocation().get().getName() : "")
            .images(imageResponse)
            .isMyPost(isMyPost)
            .isMyLike(isMyLike)
            .viewCount(post.getViewCount())
            .likeCount(post.getLikeCount())
            .beforeTime(TimeUtils.BeforeTime(LocalDateTime.now(), post.getCreatedDate()))
            .build();
    }
}
