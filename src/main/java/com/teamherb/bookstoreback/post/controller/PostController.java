package com.teamherb.bookstoreback.post.controller;

import com.teamherb.bookstoreback.common.utils.bookapi.dto.SearchBook;
import com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiXmlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiXmlService.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final BookApiXmlService bookapiXmlService;

    @PostMapping("/api/bookrequest")
    public ArrayList<SearchBook> Bookapirequest(@RequestBody Search search) {

        String XmlString = bookapiXmlService.BookApixmlRequest(search);
        if (XmlString != null) //정상적으로 Api를 호출하여 Xml String을 받아왔다면
            return bookapiXmlService.XmlToBooks(XmlString);
        else
            return null;

    }
}
