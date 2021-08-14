package com.teamherb.bookstoreback.orders.service;

import com.teamherb.bookstoreback.orders.domain.Orders;
import com.teamherb.bookstoreback.orders.dto.PurchaseHistory;
import com.teamherb.bookstoreback.orders.dto.SellHistory;
import com.teamherb.bookstoreback.orders.repository.OrdersRepository;
import com.teamherb.bookstoreback.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class OrdersService {

    private final OrdersRepository ordersRepository;



    public List<PurchaseHistory> GetPurchaseHistory(User user){
        List<PurchaseHistory> histories= new ArrayList<>();
        List<Orders> PurchaseHistories = ordersRepository.findAllByPurchaserOrderByCompletedate(user);
        if(!PurchaseHistories.isEmpty()) {

            for (Orders ord : PurchaseHistories) {
                PurchaseHistory ph = PurchaseHistory.builder()
                        .bookTitle(ord.getPost().getBook().getBookTitle())
                        .author(ord.getPost().getBook().getBookAuthor())
                        .seller(ord.getSeller().getIdentity())
                        .publisher(ord.getPost().getBook().getBookPublisher())
                        .deliveryPlace(ord.getDeliveryInfo().getDeliveryAddress())
                        .purchaseDate(ord.getCompletedate())
                        .price(ord.getPost().getPrice())
                        .build();

                histories.add(ph);

            }
        }
        return histories;
    }

    public List<SellHistory> GetSellHistory(User user){
        List<SellHistory> histories = new ArrayList<>();
        List<Orders> SellHistories = ordersRepository.findAllBySellerOrderByCompletedate(user);
        if(!SellHistories.isEmpty()) {
            for (Orders ord : SellHistories) {
                SellHistory sh = SellHistory.builder()
                        .bookTitle(ord.getPost().getBook().getBookTitle())
                        .author(ord.getPost().getBook().getBookAuthor())
                        .purchaser(ord.getSeller().getIdentity())
                        .publisher(ord.getPost().getBook().getBookPublisher())
                        .sellDate(ord.getCompletedate())
                        .price(ord.getPost().getPrice())
                        .build();

                histories.add(sh);

            }
        }
        return histories;
    }
}
