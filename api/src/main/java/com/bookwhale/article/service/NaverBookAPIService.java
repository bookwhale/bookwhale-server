package com.bookwhale.article.service;

import com.bookwhale.article.dto.BookResponse;
import com.bookwhale.article.dto.NaverBookRequest;
import com.bookwhale.auth.service.provider.NaverOAuthProvider;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverBookAPIService {

    private final NaverOAuthProvider naverOAuthProvider;

    public List<BookResponse> getNaverBooks(NaverBookRequest req) {
        String result = naverOAuthProvider.getBookInfoFromBookSearch(req);
        return convertXmlToBookResponses(result);
    }

    private List<BookResponse> convertXmlToBookResponses(String xml) {
        List<BookResponse> bookResponses = new ArrayList<>();
        try {
            // xml 을 파싱해주는 객체를 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();

            // xml 문자열은 InputStream 으로 변환
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            // 파싱 시작
            Document doc = documentBuilder.parse(is);
            // 최상위 노드 찾기
            Element element = doc.getDocumentElement();
            // 원하는 태그 데이터 찾아오기
            NodeList items = element.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                BookResponse bookResponse = new BookResponse();
                NodeList childNodes = items.item(i).getChildNodes();
                for (int j = 0; j < childNodes.getLength(); ++j) {
                    switch (childNodes.item(j).getNodeName()) {
                        case "title":
                            bookResponse.setBookTitle(childNodes.item(j).getTextContent());
                            break;
                        case "price":
                            bookResponse.setBookListPrice(childNodes.item(j).getTextContent());
                            break;
                        case "isbn":
                            bookResponse.setBookIsbn(childNodes.item(j).getTextContent());
                            break;
                        case "publisher":
                            bookResponse.setBookPublisher(childNodes.item(j).getTextContent());
                            break;
                        case "pubDate":
                            bookResponse.setBookPubDate(childNodes.item(j).getTextContent());
                            break;
                        case "author":
                            bookResponse.setBookAuthor(childNodes.item(j).getTextContent());
                            break;
                        case "description":
                            bookResponse.setBookSummary(childNodes.item(j).getTextContent());
                            break;
                        case "image":
                            bookResponse.setBookThumbnail(childNodes.item(j).getTextContent());
                            break;
                    }
                }
                bookResponses.add(bookResponse);
            }
        } catch (Exception e) {
            log.error("convert XML error : {}", e.getMessage());
            throw new CustomException(ErrorCode.FAILED_CONVERT_XML);
        }
        return bookResponses;
    }
}
