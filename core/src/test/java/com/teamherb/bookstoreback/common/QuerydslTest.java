package com.teamherb.bookstoreback.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamherb.bookstoreback.user.domain.QUser;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Querydsl 테스트")
public class QuerydslTest {

  @Autowired
  EntityManager em;

  @Autowired
  UserRepository userRepository;

  @Autowired
  DatabaseCleanUp databaseCleanUp;

  JPAQueryFactory queryFactory;

  User user;

  @BeforeEach
  void setUp() {
    databaseCleanUp.afterPropertiesSet();
    databaseCleanUp.cleanUp();

    queryFactory = new JPAQueryFactory(em);

    user = User.builder()
        .identity("highright96")
        .password("1234")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .build();
    userRepository.save(user);
  }

  @DisplayName("Q 타입 클래스가 생성된다.")
  @Test
  void createQType() {
    List<User> users = queryFactory
        .selectFrom(QUser.user)
        .fetch();

    Assertions.assertAll(
        () -> assertThat(users.size()).isEqualTo(1),
        () -> assertThat(users.get(0).getIdentity()).isEqualTo(user.getIdentity())
    );
  }
}
