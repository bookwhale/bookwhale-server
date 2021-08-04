package com.teamherb.bookstoreback.common.utils.bookapi;


import com.teamherb.bookstoreback.common.utils.bookapi.service.BookapiServiceJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@DisplayName("책 api 통합 테스트")
@SpringBootTest
public class BookapiTest {

    @Autowired
    BookapiServiceJson bookapiServiceJson;

    @Test
    @DisplayName("책 정보를 가져온다.")
    void BookapiTest(){
        String Search = "9788960773431";
        bookapiServiceJson.BookapiRequest(Search);
    }
}
