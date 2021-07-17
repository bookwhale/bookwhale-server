package com.teamherb.bookstoreback.orders.domain;

import javax.persistence.Embeddable;

@Embeddable
public class DeliveryInfo {

    private String deliveryAddress;

    private String receiver;

    private String contact;

    private String deliveryRequest;
}
