package com.teamherb.bookstoreback.common.utils.bookapi;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@DisplayName("책 api 통합 테스트")
@SpringBootTest
public class BookapiTest {

    @Autowired
    BookapiService bookapiService;

    @Test
    @DisplayName("책 정보를 가져온다.")
    void BookapiTest(){
        String keyword = "베르나르 베르베르";
        bookapiService.bookapirequest(keyword);
    }
}
