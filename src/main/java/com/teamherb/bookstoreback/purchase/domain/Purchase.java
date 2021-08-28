package com.teamherb.bookstoreback.purchase.domain;

import com.teamherb.bookstoreback.common.domain.BaseEntity;
import com.teamherb.bookstoreback.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Purchase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchaser_id")
    private User purchaser;

    private String sellerIdentity;

    private String sellerName;

    private String postTitle;

    private String postPrice;

    private String bookTitle;

    private String bookThumbnail;

    @Builder
    public Purchase(Long id, User purchaser, String sellerIdentity, String sellerName,
        String postTitle, String postPrice, String bookTitle, String bookThumbnail) {
        this.id = id;
        this.purchaser = purchaser;
        this.sellerIdentity = sellerIdentity;
        this.sellerName = sellerName;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.bookTitle = bookTitle;
        this.bookThumbnail = bookThumbnail;
    }
}
