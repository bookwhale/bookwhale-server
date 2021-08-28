package com.teamherb.bookstoreback.orders.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class DeliveryInfo {

    private String deliveryAddress;

    private String receiver;

    private String contact;

    private String deliveryRequest;

    @Builder
    public DeliveryInfo(String deliveryAddress, String receiver, String contact, String deliveryRequest) {
        this.deliveryAddress = deliveryAddress;
        this.receiver = receiver;
        this.contact = contact;
        this.deliveryRequest = deliveryRequest;
    }

    public DeliveryInfo() {

    }
}
