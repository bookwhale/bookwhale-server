package com.teamherb.bookstoreback.orders.service;

import com.teamherb.bookstoreback.orders.domain.DeliveryInfo;
import com.teamherb.bookstoreback.orders.domain.Orders;
import com.teamherb.bookstoreback.orders.dto.PurchaseHistory;
import com.teamherb.bookstoreback.orders.dto.SellHistory;
import com.teamherb.bookstoreback.orders.repository.OrdersRepository;

import com.teamherb.bookstoreback.post.domain.Book;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.repository.PostRepository;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime;
import java.util.List;



@ExtendWith(SpringExtension.class)
@DataJpaTest
@DisplayName("OrderService 통합테스트")

class OrdersServiceTest {

    private OrdersService ordersService;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;



    @DisplayName("구매이력, 판미이력 단위테스트")
    @Test
    void getPurchaseHistory() {

        ordersService = new OrdersService(ordersRepository);
        User user = User.builder()
                .identity("kim123")
                .password("1234")
                .name("김첨지")
                .email("kim123@email.com")
                .build();

        User user2 = User.builder()
                .identity("sul123")
                .password("1234")
                .name("설렁탕")
                .email("sul123@email.com")
                .build();

        Book book = Book.builder()
                .bookAuthor("설렁탕")
                .bookTitle("백종원의 요리책")
                .bookPublisher("빽다방")
                .build();


        Post post = Post.builder()
                .price(3000L)
                .book(book)
                .build();

        DeliveryInfo deliveryInfo = DeliveryInfo.builder()
                .deliveryAddress("테스트입니다.")
                .contact("테스트")
                .deliveryRequest("테스트")
                .receiver("테스트")
                .build();

        Orders order1 = Orders.builder()
                .completedate(LocalDateTime.now())
                .post(post)
                .purchaser(user)
                .seller(user2)
                .deliveryInfo(deliveryInfo)
                .build();

        Orders order2 = Orders.builder()
                .completedate(LocalDateTime.now())
                .post(post)
                .purchaser(user2)
                .seller(user)
                .deliveryInfo(deliveryInfo)
                .build();



        User saveduser1 = userRepository.save(user);
        User saveduser2 = userRepository.save(user2);
        postRepository.save(post);
        Orders save1 = ordersRepository.save(order1);
        Orders save2 = ordersRepository.save(order2);
        List<PurchaseHistory> purchaseHistories = ordersService.GetPurchaseHistory(saveduser1);
        for (PurchaseHistory purchaseHistory : purchaseHistories) {
            System.out.println("purchaseHistory = " + purchaseHistory);
        }

        List<SellHistory> sellHistories = ordersService.GetSellHistory(saveduser1);
        for (SellHistory sellHistory : sellHistories) {
            System.out.println("sellHistory = " + sellHistory);
        }


    }
}