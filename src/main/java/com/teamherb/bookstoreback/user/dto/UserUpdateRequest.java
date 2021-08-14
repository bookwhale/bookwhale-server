package com.teamherb.bookstoreback.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

    private String name;
    private String phoneNumber;
    private String address;

    @Builder
    public UserUpdateRequest(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
