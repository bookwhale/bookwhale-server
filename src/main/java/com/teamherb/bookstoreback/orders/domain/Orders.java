package com.teamherb.bookstoreback.orders.domain;

import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.common.domain.BaseEntity;
import com.teamherb.bookstoreback.user.domain.User;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchaser_id")
    private User purchaser;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Embedded
    private DeliveryInfo deliveryInfo;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime completedate;

    @Builder
    public Orders(Long id, User purchaser, User seller, Post post, DeliveryInfo deliveryInfo, OrderStatus orderStatus, LocalDateTime completedate) {
        this.id = id;
        this.purchaser = purchaser;
        this.seller = seller;
        this.post = post;
        this.deliveryInfo = deliveryInfo;
        this.orderStatus = orderStatus;
        this.completedate = completedate;
    }
}
