package com.teamherb.bookstoreback.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.teamherb.bookstoreback.purchase.domain.PurchaseRepository;
import com.teamherb.bookstoreback.purchase.dto.PurchaseResponse;
import com.teamherb.bookstoreback.sale.domain.Sale;
import com.teamherb.bookstoreback.sale.domain.SaleRepository;
import com.teamherb.bookstoreback.sale.dto.SaleResponse;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
@JdbcTest
public class UserServiceTest {

    @Spy
    private UserRepository userRepository;

    @Spy
    private PurchaseRepository purchaseRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private SaleRepository saleRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    private Sale sale;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, purchaseRepository, saleRepository, passwordEncoder);

        user = User.builder()
            .id(1L)
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
            .build();

        sale =Sale.builder()
                .seller(user)
                .bookTitle("테스트")
                .purchaserIdentity("test123")
                .bookThumbnail("test")
                .purchaserIdentity("기모찌")
                .postPrice("1000")
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
    @DisplayName("구매내역 테스트")
    public void purchaseResponseTest(){

        userRepository.save(user);
        List<PurchaseResponse> purchaseHistories = userService.findPurchaseHistories(user);
        for (PurchaseResponse purchaseHistory : purchaseHistories) {
            System.out.println("purchaseHistory = " + purchaseHistory);
        }
    }


    @Test
    @DisplayName("판매내역 테스트")
    public void saleResponseTest(){
        User saveduser = userRepository.save(user);
        Sale saved = saleRepository.save(sale);
        System.out.println("saved = " + saved);
        List<SaleResponse> saleHistories = userService.findSaleHistories(user);
        if(saleHistories!= null){
            for (SaleResponse saleHistory : saleHistories) {
                System.out.println("saleHistory = " + saleHistory);
            }
        }

    }
}
