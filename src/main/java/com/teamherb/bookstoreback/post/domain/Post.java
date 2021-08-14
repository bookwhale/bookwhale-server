package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.common.domain.BaseEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String title;

    private String image;

    private Long price;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Embedded
    private Book book;

    @Builder
    public Post(Long id, User seller, Account account, String title, String image, Long price, String description, PostStatus postStatus, BookStatus bookStatus, Book book) {
        this.id = id;
        this.seller = seller;
        this.account = account;
        this.title = title;
        this.image = image;
        this.price = price;
        this.description = description;
        this.postStatus = postStatus;
        this.bookStatus = bookStatus;
        this.book = book;
    }
}
