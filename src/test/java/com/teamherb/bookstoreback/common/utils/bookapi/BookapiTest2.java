package com.teamherb.bookstoreback.common.utils.bookapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.teamherb.bookstoreback.common.utils.bookapi.BookapiService2.Keyword;

@DisplayName("책 api 통합 테스트(xml)")
@SpringBootTest
public class BookapiTest2 {

    @Autowired
    BookapiService2 bookapiService2;


    @Test
    @DisplayName("책 정보를 가져온다.")
    void BookapiTest(){

        Keyword keyword = Keyword.builder()
                .title("기억")
                .isbn(null)
                .build();
        bookapiService2.BookapiRequest(keyword);
    }
}
