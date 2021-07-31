package com.teamherb.bookstoreback.common.utils.bookapi;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class BookapiService2 {

    public void BookapiRequest(Keyword keyword){
        String clientId = "JhuTF7N1bKBh_QeQzii5";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "f3rZzZ9BSQ";//애플리케이션 클라이언트 시크릿값";
        String apiURL;
        try {

            String text;
            if (keyword.title != null){
                text = URLEncoder.encode(keyword.title, StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_titl="+text;
            }
            else{
                text = URLEncoder.encode(keyword.isbn, StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_isbn="+text;
            }



            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
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
            String result = response.toString();
            System.out.println("result = " + result);

            // 1. 빌더 팩토리 생성.
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

            // 2. 빌더 팩토리로부터 빌더 생성
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            // 3. 빌더를 통해 XML 문서를 파싱해서 Document 객체로 가져온다.
            Document document = builder.parse(new InputSource(new StringReader(result)));

            NodeList ItemTagList = document.getElementsByTagName("item");

            for (int i = 0; i < ItemTagList.getLength(); ++i) {
                System.out.println("테스트");
                System.out.println(ItemTagList.item(i).getNodeValue());

                NodeList childNodes = ItemTagList.item(i).getChildNodes();
                for (int j = 0; j < childNodes.getLength(); ++j) {

                    if ("title".equals(childNodes.item(j).getNodeName())) {
                        String title = childNodes.item(j).getNodeValue();
                        System.out.println("title = " + title);
                    }

                    if ("price".equals(childNodes.item(j).getNodeName())) {
                        String price = childNodes.item(j).getNodeValue();
                        System.out.println("price = " + price);
                    }


                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    @Data
    @NoArgsConstructor
    public static class Keyword {
        private String isbn;
        private String title;

        @Builder
        public Keyword(String isbn, String title) {
            this.title = title;
            this.isbn = isbn;
        }
    }

}
