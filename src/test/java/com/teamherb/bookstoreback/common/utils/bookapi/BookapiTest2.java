package com.teamherb.bookstoreback.common.utils.bookapi;

import com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiServiceXml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiServiceXml.Search;

@DisplayName("책 api 통합 테스트(xml)")
@SpringBootTest
public class BookapiTest2 {

    @Autowired
    BookApiServiceXml bookapiServiceXml;


    @Test
    @DisplayName("책 정보를 가져온다.")
    void BookapiTest(){

        Search search = Search.builder()
                .title("기억")
                .isbn(null)
                .build();

    }
}
