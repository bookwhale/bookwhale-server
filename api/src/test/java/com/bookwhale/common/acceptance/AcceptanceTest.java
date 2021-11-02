package com.bookwhale.common.acceptance;

import com.bookwhale.common.DatabaseCleanUp;
import com.bookwhale.user.domain.Role;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import com.bookwhale.user.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

  @LocalServerPort
  int port;

  @Autowired
  UserRepository userRepository;

  @Autowired
  DatabaseCleanUp databaseCleanUp;

  @Autowired
  private PasswordEncoder passwordEncoder;

  protected ObjectMapper objectMapper;

  protected User user;

  protected User anotherUser;

  protected LoginRequest loginRequest;

  protected LoginRequest anotherLoginRequest;

  @BeforeEach
  public void setUp() {
    if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
      RestAssured.port = port;
    }

    databaseCleanUp.afterPropertiesSet();
    databaseCleanUp.cleanUp();

    loginRequest = new LoginRequest("highright96", "1234");
    anotherLoginRequest = new LoginRequest("hose12", "1234");
    user = createUser();
    anotherUser = createAnotherUser();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  private User createUser() {
    User user = User.builder()
        .identity("highright96")
        .password(passwordEncoder.encode("1234"))
        .name("남상우")
        .email("highright96@gmail.com")
        .phoneNumber("010-1234-1234")
        .role(Role.ROLE_USER)
        .build();
    userRepository.save(user);
    return user;
  }

  private User createAnotherUser() {
    User user = User.builder()
        .identity("hose12")
        .password(passwordEncoder.encode("1234"))
        .name("주호세")
        .email("hose12@email.com")
        .phoneNumber("010-5678-5678")
        .role(Role.ROLE_USER)
        .build();
    userRepository.save(user);
    return user;
  }
}
