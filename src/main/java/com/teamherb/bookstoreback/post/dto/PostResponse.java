package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostStatus;
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
  private BookStatus bookStatus;
  private PostStatus postStatus;
  private List<String> images;
  private BookResponse bookResponse;
  private boolean isMyPost;
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;

  @Builder
  public PostResponse(Long sellerId, String sellerIdentity, String sellerProfileImage,
      Long postId, String title, String price, String description,
      BookStatus bookStatus, PostStatus postStatus, List<String> images,
      BookResponse bookResponse, boolean isMyPost, LocalDateTime createdDate,
      LocalDateTime lastModifiedDate) {
    this.sellerId = sellerId;
    this.sellerIdentity = sellerIdentity;
    this.sellerProfileImage = sellerProfileImage;
    this.postId = postId;
    this.title = title;
    this.price = price;
    this.description = description;
    this.bookStatus = bookStatus;
    this.postStatus = postStatus;
    this.images = images;
    this.bookResponse = bookResponse;
    this.isMyPost = isMyPost;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
  }

  public static PostResponse of(Post post, List<Image> images, boolean isMyPost) {
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

    List<String> imageResponse = images.stream().map(Image::getPath).collect(Collectors.toList());

    return PostResponse.builder()
        .bookResponse(bookResponse)
        .sellerId(post.getSeller().getId())
        .sellerIdentity(post.getSeller().getIdentity())
        .sellerProfileImage(post.getSeller().getProfileImage())
        .postId(post.getId())
        .title(post.getTitle())
        .price(post.getPrice())
        .description(post.getDescription())
        .bookStatus(post.getBookStatus())
        .postStatus(post.getPostStatus())
        .images(imageResponse)
        .isMyPost(isMyPost)
        .createdDate(post.getCreatedDate())
        .lastModifiedDate(post.getLastModifiedDate())
        .build();
  }
}
