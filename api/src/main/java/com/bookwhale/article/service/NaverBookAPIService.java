package com.bookwhale.article.service;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.config.AppProperties;
import com.bookwhale.article.dto.BookResponse;
import com.bookwhale.article.dto.NaverBookRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private final AppProperties appProperties;

    public List<BookResponse> getNaverBooks(NaverBookRequest req) {
        String apiURL, text, result;
        String pageUrl = "&display=" + req.getDisplay() + "&start=" + req.getStart();
        try {
            if (req.getTitle() != null) {
                text = URLEncoder.encode(req.getTitle(), StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_titl=" + text;
            } else if (req.getIsbn() != null) {
                text = URLEncoder.encode(req.getIsbn(), StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_isbn=" + text;
            } else {
                text = URLEncoder.encode(req.getAuthor(), StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_auth=" + text;
            }
            apiURL += pageUrl;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", appProperties.getNaverBook().getClientId());
            con.setRequestProperty("X-Naver-Client-Secret",
                appProperties.getNaverBook().getClientSecret());
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            con.disconnect();
            result = response.toString();
        } catch (Exception e) {
            log.error("naverBook error : {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_NAVER_SERVER_ERROR);
        }
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
