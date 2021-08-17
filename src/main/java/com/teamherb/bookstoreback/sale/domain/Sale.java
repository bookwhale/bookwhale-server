package com.teamherb.bookstoreback.sale.domain;

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
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    private String purchaserIdentity;

    private String purchaserName;

    private String postTitle;

    private String postPrice;

    private String bookTitle;

    private String bookThumbnail;

    @Builder
    public Sale(Long id, User seller, String purchaserIdentity, String purchaserName, String postTitle, String postPrice, String bookTitle, String bookThumbnail) {
        this.id = id;
        this.seller = seller;
        this.purchaserIdentity = purchaserIdentity;
        this.purchaserName = purchaserName;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.bookTitle = bookTitle;
        this.bookThumbnail = bookThumbnail;
    }
}
