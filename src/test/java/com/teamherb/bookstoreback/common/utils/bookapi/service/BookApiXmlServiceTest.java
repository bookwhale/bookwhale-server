package com.teamherb.bookstoreback.common.utils.bookapi.service;



import com.teamherb.bookstoreback.common.utils.bookapi.dto.SearchBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiXmlService.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
public class BookApiXmlServiceTest {


    private BookApiXmlService bookApiXmlService;

    @BeforeEach
    void setUp() {
        bookApiXmlService = new BookApiXmlService();


    }
    @DisplayName("NAVER 책 API를 호출한다.")
    @Test
    void apitest(){
        Search search = Search.builder()
                .title("기억")
                .build();
        String result = bookApiXmlService.BookApixmlRequest(search);
        System.out.println("result = " + result);
        ArrayList<SearchBook> searchBooks = bookApiXmlService.XmlToBooks(result);
        for (SearchBook searchBook : searchBooks) {
            System.out.println("searchBook = " + searchBook);
        }

    }
}
