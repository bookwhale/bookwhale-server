package com.teamherb.bookstoreback.user.service;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.orders.domain.OrderRepository;
import com.teamherb.bookstoreback.orders.domain.OrderStatus;
import com.teamherb.bookstoreback.orders.domain.Orders;
import com.teamherb.bookstoreback.orders.dto.PurchaseOrder;
import com.teamherb.bookstoreback.orders.dto.SaleOrder;
import com.teamherb.bookstoreback.post.domain.Book;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostRepository;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.SalePostResponse;
import com.teamherb.bookstoreback.purchase.domain.Purchase;
import com.teamherb.bookstoreback.purchase.domain.PurchaseRepository;
import com.teamherb.bookstoreback.purchase.dto.PurchaseResponse;
import com.teamherb.bookstoreback.sale.domain.Sale;
import com.teamherb.bookstoreback.sale.domain.SaleRepository;
import com.teamherb.bookstoreback.sale.dto.SaleResponse;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private PurchaseRepository purchaseRepository;

  @Mock
  private SaleRepository saleRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private PostRepository postRepository;

  UserService userService;

  User user;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, purchaseRepository, saleRepository,
        passwordEncoder, orderRepository, postRepository);

    user = User.builder()
        .identity("highright96")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .address("서울")
        .build();
  }

  @DisplayName("회원가입을 한다.")
  @Test
  void createUser_success() {
    when(userRepository.existsByIdentity(any())).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn("1234");
    when(userRepository.save(any())).thenReturn(user);

    userService.createUser(new SignUpRequest());

    verify(userRepository).existsByIdentity(any());
    verify(passwordEncoder).encode(any());
    verify(userRepository).save(any());
  }

  @DisplayName("회원가입을 할 때 중복된 아이디면 예외가 발생한다.")
  @Test
  void createUser_duplicatedIdentity_failure() {
    when(userRepository.existsByIdentity(any())).thenReturn(true);
    assertThatThrownBy(() -> userService.createUser(new SignUpRequest())).
        isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.DUPLICATED_USER_IDENTITY.getMessage());
  }

  @DisplayName("내 정보를 수정한다.")
  @Test
  void updateMyInfo_success() {
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        .name("주호세")
        .phoneNumber("010-0000-0000")
        .address("경기")
        .build();

    userService.updateMyInfo(user, userUpdateRequest);

    Assertions.assertAll(
        () -> assertThat(user.getName()).isEqualTo(userUpdateRequest.getName()),
        () -> assertThat(user.getAddress()).isEqualTo(userUpdateRequest.getAddress()),
        () -> assertThat(user.getPhoneNumber()).isEqualTo(userUpdateRequest.getPhoneNumber())
    );
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


  @Test
  @DisplayName("판매자 주문 정보를 조회한다.")
  public void findSaleOrders() {
    User user2 = User.builder()
        .identity("luckyday")
        .name("김첨지")
        .email("kim@sul.com")
        .phoneNumber("010-1234-1234")
        .address("조선")
        .build();

    Book book = Book.builder()
        .bookThumbnail("설렁탕")
        .bookTitle("설렁탕맛잇게 끊이는 법")
        .build();

    Post post = Post.builder()
        .book(book)
        .price("10000")
        .title("설렁탕 비법서 팔아요")
        .build();

    Orders orders = Orders.builder()
        .id(1L)
        .post(post)
        .orderStatus(OrderStatus.ACCEPT)
        .purchaser(user)
        .seller(user2)
        .orderStatus(OrderStatus.ACCEPT)
        .build();

    when(orderRepository.findAllBySellerOrderByCreatedDate(any())).thenReturn(
        of(orders));

    List<SaleOrder> saleOrders = userService.findSaleOrders(user);
    assertAll(
        () -> assertThat(saleOrders.get(0).getBookTitle()).isEqualTo(
            orders.getPost().getBook().getBookTitle()),
        () -> assertThat(saleOrders.get(0).getPostTitle()).isEqualTo(
            orders.getPost().getTitle()),
        () -> assertThat(saleOrders.get(0).getOrderStatus()).isEqualTo(
            orders.getOrderStatus().name()),
        () -> assertThat(saleOrders.get(0).getPurchaserIdentity()).isEqualTo(
            orders.getPurchaser().getIdentity())

    );
  }

  @Test
  @DisplayName("user2가 등록한 게시글을 조회한다.")
  public void findSalePosts() {
    User user2 = User.builder()
        .identity("luckyday")
        .name("김첨지")
        .email("kim@sul.com")
        .phoneNumber("010-1234-1234")
        .address("조선")
        .build();

    Book book = Book.builder()
        .bookThumbnail("설렁탕")
        .bookTitle("설렁탕맛잇게 끊이는 법")
        .build();

    Post post = Post.builder()
        .id(1L)
        .book(book)
        .seller(user2)
        .price("10000")
        .title("설렁탕 비법서 팔아요")
        .postStatus(PostStatus.SALE)
        .build();

    when(postRepository.findAllBySellerOrderByCreatedDate(any())).thenReturn(
        of(post));

    List<SalePostResponse> saleOrders = userService.findSalePosts(user);
    assertAll(
        () -> assertThat(saleOrders.get(0).getId()).isEqualTo(
            post.getId()),
        () -> assertThat(saleOrders.get(0).getBookTitle()).isEqualTo(
            post.getBook().getBookTitle()),
        () -> assertThat(saleOrders.get(0).getPostTitle()).isEqualTo(
            post.getTitle()),
        () -> assertThat(saleOrders.get(0).getBookThumbnail()).isEqualTo(
            post.getBook().getBookThumbnail()),
        () -> assertThat(saleOrders.get(0).getPostStatus()).isEqualTo(
            post.getPostStatus().name())

    );
  }


}
