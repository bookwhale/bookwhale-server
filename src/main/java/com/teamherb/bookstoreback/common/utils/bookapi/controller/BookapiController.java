package com.teamherb.bookstoreback.common.utils.bookapi.controller;



import com.teamherb.bookstoreback.common.utils.bookapi.service.BookapiServiceXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.teamherb.bookstoreback.common.utils.bookapi.service.BookapiServiceXml.*;


@RestController
public class BookapiController {

    @Autowired
    private BookapiServiceXml bookapiServiceXml;

    @PostMapping("/requestbookapi")
    public ArrayList<SearchBook> Bookapirequest(@RequestBody Search search){


        return bookapiServiceXml.BookapixmlRequest(search);
    }
}
