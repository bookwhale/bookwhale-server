package com.teamherb.bookstoreback.user.controller;


import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.basket.domain.Basket;
import com.teamherb.bookstoreback.basket.dto.BasketResponse;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.orders.domain.OrderStatus;
import com.teamherb.bookstoreback.orders.dto.PurchaseOrder;
import com.teamherb.bookstoreback.orders.dto.SaleOrder;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.SalePostResponse;
import com.teamherb.bookstoreback.purchase.dto.PurchaseResponse;
import com.teamherb.bookstoreback.sale.dto.SaleResponse;
import com.teamherb.bookstoreback.user.docs.UserDocumentation;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.dto.LoginRequest;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import com.teamherb.bookstoreback.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.web.context.WebApplicationContext;


@DisplayName("유저 단위 테스트(Controller)")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest extends CommonApiTest {

  @MockBean
  UserService userService;

  User user;

  @BeforeEach
  @Override
  public void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    super.setUp(webApplicationContext, restDocumentation);

    user = User.builder()
        .identity("highright96")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .address("서울")
        .build();
  }

  @DisplayName("유저 회원가입을 한다.")
  @Test
  void createUser() throws Exception {
    AccountRequest accountRequest = AccountRequest.builder()
        .accountBank("국민은행")
        .accountNumber("123-1234-12345")
        .accountOwner("남상우")
        .build();

    SignUpRequest signUpRequest = SignUpRequest.builder()
        .identity("highright96")
        .password("1234")
        .name("남상우")
        .email("highright96@email.com")
        .accountRequest(accountRequest)
        .build();

    doNothing().when(userService).createUser(any());

    mockMvc.perform(post("/api/user/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(UserDocumentation.userSignup());
  }

  @DisplayName("유저 로그인을 한다.")
  @Test
  void login() throws Exception {
    LoginRequest loginRequest = new LoginRequest("highright96", "1234");

    mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andDo(print())
        .andDo(UserDocumentation.userLogin());
  }

  @WithMockCustomUser
  @DisplayName("내 정보를 조회한다.")
  @Test
  void getMyInfo() throws Exception {
    mockMvc.perform(get("/api/user/me")
        .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.identity").value("user"))
        .andExpect(jsonPath("$.name").value("유저"))
        .andExpect(jsonPath("$.email").value("user@email.com"))
        .andDo(print())
        .andDo(UserDocumentation.userMe());
  }

  @WithMockCustomUser
  @DisplayName("내 정보를 수정한다.")
  @Test
  void updateMyInfo() throws Exception {
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        .name("주호세")
        .phoneNumber("010-1122-3344")
        .address("경기")
        .build();

    doNothing().when(userService).updateMyInfo(any(), any());

    mockMvc.perform(patch("/api/user/me")
        .header(HttpHeaders.AUTHORIZATION, "accessToken")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userUpdateRequest)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userUpdateMe());
  }

  @WithMockCustomUser
  @DisplayName("구매내역을 조회한다.")
  @Test
  void findPurchaseHistories() throws Exception {
    PurchaseResponse response = PurchaseResponse.builder()
        .sellerIdentity("highright96")
        .sellerName("남상우")
        .postTitle("책 팝니다.")
        .postPrice("10000")
        .bookTitle("신")
        .bookThumbnail("책 썸네일")
        .createdDate(LocalDateTime.now())
        .build();

    when(userService.findPurchaseHistories(any())).thenReturn(of(response));

    mockMvc.perform(get("/api/user/purchase-history")
        .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.findPurchaseHistories());
  }

  @WithMockCustomUser
  @DisplayName("판매내역을 조회한다.")
  @Test
  void findSaleHistories() throws Exception {
    SaleResponse saleResponse = SaleResponse.builder()
        .purchaserIdentity("hose123")
        .purchaserName("김첨지")
        .postTitle("설렁탕 팝니다.")
        .postPrice("6000")
        .bookTitle("설렁탕")
        .bookThumbnail("설렁탕 썸네일")
        .createdDate(LocalDateTime.now())
        .build();

    when(userService.findSaleHistories(any())).thenReturn(of(saleResponse));

    mockMvc.perform(get("/api/user/sale-history")
        .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.findSaleHistories());
  }

  @WithMockCustomUser
  @DisplayName("판매자 주문정보를 조회한다.")
  @Test
  void findSaleOrders() throws Exception {
    SaleOrder saleOrder = SaleOrder.builder()
        .id(1L)
        .bookPrice("10000")
        .orderStatus(OrderStatus.ACCEPT.name())
        .bookThumbnail("설렁탕사진")
        .bookTitle("설렁탕 요리비법")
        .postTitle("설렁탕 요리책 팔아요")
        .purchaserIdentity("blackcow")
        .build();

    when(userService.findSaleOrders(any())).thenReturn(of(saleOrder));

    mockMvc.perform(get("/api/user/saleOrders")
        .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.findSaleOrders());
  }

  @WithMockCustomUser
  @DisplayName("중고책 게시글을 조회한다.")
  @Test
  void findSalePosts() throws Exception {
    SalePostResponse  salePostResponse= SalePostResponse.builder()
        .id(1L)
        .bookPrice("10000")
        .postStatus(PostStatus.SALE.name())
        .bookThumbnail("설렁탕사진")
        .bookTitle("설렁탕 요리비법")
        .postTitle("설렁탕 요리책 팔아요")
        .build();

    when(userService.findSalePosts(any())).thenReturn(of(salePostResponse));

    mockMvc.perform(get("/api/user/salePosts")
        .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.findSalePosts());
  }

  @WithMockCustomUser
  @DisplayName("관심목록을 조회한다.")
  @Test
  void findBaskets() throws Exception {
    BasketResponse basketResponse= BasketResponse.builder()
        .id(1L)
        .bookPrice("10000")
        .postStatus(PostStatus.SALE.name())
        .bookThumbnail("설렁탕사진")
        .bookTitle("설렁탕 요리비법")
        .postTitle("설렁탕 요리책 팔아요")
        .sellerIdentity("luckyday")
        .build();

    when(userService.findBaskets(any())).thenReturn(of(basketResponse));

    mockMvc.perform(get("/api/user/baskets")
        .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.findBaskets());
  }

  @WithMockCustomUser
  @DisplayName("관심목록을 삭제한다.")
  @Test
  void delBasket() throws Exception {
    Basket basket = Basket.builder()
        .id(1L)
        .build();

    mockMvc.perform(get("/api/user/baskets")
        .param("id","1")
        .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.delBasket());
  }

}
