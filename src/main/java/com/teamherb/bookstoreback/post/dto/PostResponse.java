package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.account.dto.AccountResponse;
import com.teamherb.bookstoreback.image.domain.Image;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostResponse {

    private AccountResponse accountResponse;
    private BookResponse bookResponse;
    private Long postId;
    private String title;
    private String price;
    private String description;
    private BookStatus bookStatus;
    private PostStatus postStatus;
    private List<String> images;
    private boolean isMyPost;

    @Builder
    public PostResponse(AccountResponse accountResponse, BookResponse bookResponse,
        Long postId, String title, String price, String description,
        BookStatus bookStatus, PostStatus postStatus, List<String> images, boolean isMyPost) {
        this.accountResponse = accountResponse;
        this.bookResponse = bookResponse;
        this.postId = postId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.bookStatus = bookStatus;
        this.postStatus = postStatus;
        this.images = images;
        this.isMyPost = isMyPost;
    }

    public static PostResponse of(Post post, List<Image> images, boolean isMyPost) {
        AccountResponse accountResponse = AccountResponse.builder()
            .accountBank(post.getAccountBank())
            .accountNumber(post.getAccountNumber())
            .accountOwner(post.getAccountOwner())
            .build();

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

        List<String> imageResponse = images.stream().map(Image::getPath)
            .collect(Collectors.toList());

        return PostResponse.builder()
            .accountResponse(accountResponse)
            .bookResponse(bookResponse)
            .postId(post.getId())
            .title(post.getTitle())
            .price(post.getPrice())
            .description(post.getDescription())
            .bookStatus(post.getBookStatus())
            .postStatus(post.getPostStatus())
            .images(imageResponse)
            .isMyPost(isMyPost)
            .build();
    }
}
