package com.bookwhale.auth.domain;

import com.bookwhale.common.domain.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token extends BaseEntity {

    @Id
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String tokenValue;

    @Builder
    public Token(String email, String tokenValue) {
        this.email = email;
        this.tokenValue = tokenValue;
    }

    public static Token create(String email, String tokenValue) {
        return Token.builder()
            .email(email)
            .tokenValue(tokenValue)
            .build();
    }

    @Override
    public String toString() {
        return "Token{" +
            "email='" + email + '\'' +
            ", tokenValue='" + tokenValue + '\'' +
            '}';
    }
}
