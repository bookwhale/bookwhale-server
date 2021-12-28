package com.bookwhale.common.acceptance;

import com.bookwhale.auth.domain.JWT;
import com.bookwhale.auth.service.OauthService;
import com.bookwhale.common.DatabaseCleanUp;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OauthService oauthService;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @Autowired
    protected JWT jwt;

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
            .nickname("남상우")
            .email("highright96@gmail.com")
            .build();
        userRepository.save(user);
        return user;
    }

    private User createAnotherUser() {
        User user = User.builder()
            .nickname("hose12")
            .email("hose12@email.com")
            .build();
        userRepository.save(user);
        return user;
    }
}
