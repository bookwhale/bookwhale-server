package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.common.domain.BaseEntity;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    private String accountNumber;

    private String accountBank;

    private String accountOwner;

    private String title;

    private String price;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Embedded
    private Book book;

    @Builder
    public Post(Long id, User seller, String accountNumber, String accountBank,
        String accountOwner, String title, String price, String description,
        PostStatus postStatus, BookStatus bookStatus, Book book) {
        this.id = id;
        this.seller = seller;
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.accountOwner = accountOwner;
        this.title = title;
        this.price = price;
        this.description = description;
        this.postStatus = postStatus;
        this.bookStatus = bookStatus;
        this.book = book;
    }

    public static Post create(User user, PostRequest req) {
        return Post.builder()
            .seller(user)
            .title(req.getTitle())
            .price(req.getPrice())
            .postStatus(PostStatus.SALE)
            .bookStatus(BookStatus.valueOf(req.getBookStatus()))
            .description(req.getDescription())
            .accountBank(req.getAccountRequest().getAccountBank())
            .accountOwner(req.getAccountRequest().getAccountOwner())
            .accountNumber(req.getAccountRequest().getAccountNumber())
            .book(Book.create(req.getBookRequest()))
            .build();
    }

    public boolean isMyPost(User user) {
        return this.seller.getId().equals(user.getId());
    }
}
