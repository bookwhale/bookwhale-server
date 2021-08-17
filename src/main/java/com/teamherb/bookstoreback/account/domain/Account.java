package com.teamherb.bookstoreback.account.domain;

import com.teamherb.bookstoreback.account.dto.AccountUpdateRequest;
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
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String accountNumber;

    private String accountBank;

    private String accountOwner;

    @Builder
    public Account(Long id, User user, String accountNumber, String accountBank,
        String accountOwner) {
        this.id = id;
        this.user = user;
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.accountOwner = accountOwner;
    }

    public void update(AccountUpdateRequest req) {
        this.accountNumber = req.getAccountNumber();
        this.accountBank = req.getAccountBank();
        this.accountOwner = req.getAccountOwner();
    }
}
