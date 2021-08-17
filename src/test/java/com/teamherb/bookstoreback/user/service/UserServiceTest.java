package com.teamherb.bookstoreback.user.service;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.purchase.domain.Purchase;
import com.teamherb.bookstoreback.purchase.domain.PurchaseRepository;
import com.teamherb.bookstoreback.purchase.dto.PurchaseResponse;
import com.teamherb.bookstoreback.sale.domain.Sale;
import com.teamherb.bookstoreback.sale.domain.SaleRepository;
import com.teamherb.bookstoreback.sale.dto.SaleResponse;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
public class UserServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private SaleRepository saleRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, purchaseRepository, saleRepository,
            passwordEncoder);

        user = User.builder()
            .id(1L)
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
            .build();
    }

    @DisplayName("회원가입을 한다.")
    @Test
    void createUser() {
        when(userRepository.existsByIdentity(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("1234");
        when(userRepository.save(any())).thenReturn(user);

        userService.createUser(new SignUpRequest());

        verify(userRepository).existsByIdentity(any());
        verify(passwordEncoder).encode(any());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("구매 내역을 조회한다.")
    public void findPurchaseHistories() {
        Purchase purchase = Purchase.builder()
            .bookTitle("신")
            .postTitle("신 판매합니다")
            .build();

        when(purchaseRepository.findAllByPurchaserOrderByCreatedDate(any())).thenReturn(
            of(purchase));

        List<PurchaseResponse> purchases = userService.findPurchaseHistories(user);
        assertAll(
            () -> assertThat(purchases.get(0).getBookTitle()).isEqualTo(
                purchase.getBookTitle()),
            () -> assertThat(purchases.get(0).getPostTitle()).isEqualTo(
                purchase.getPostTitle())
        );
    }


    @Test
    @DisplayName("판매 내역을 조회한다.")
    public void findSaleHistories() {
        Sale sale = Sale.builder()
            .bookTitle("신")
            .postTitle("신 판매합니다")
            .build();

        when(saleRepository.findAllBySellerOrderByCreatedDate(any())).thenReturn(
            of(sale));

        List<SaleResponse> sales = userService.findSaleHistories(user);
        assertAll(
            () -> assertThat(sales.get(0).getBookTitle()).isEqualTo(
                sale.getBookTitle()),
            () -> assertThat(sales.get(0).getPostTitle()).isEqualTo(
                sale.getPostTitle())
        );
    }
}
