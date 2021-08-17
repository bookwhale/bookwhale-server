package com.teamherb.bookstoreback.user.dto;

import com.teamherb.bookstoreback.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String identity;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    public static UserResponse of(User user) {
        return new UserResponse(
            user.getIdentity(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getAddress()
        );
    }
}
