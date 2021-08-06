package com.teamherb.bookstoreback.common.utils.bookapi.service;

import com.teamherb.bookstoreback.common.utils.bookapi.dto.SearchBook;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BookApiServiceXml {

    public String BookApixmlRequest(Search search) {
        String clientId = "JhuTF7N1bKBh_QeQzii5";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "f3rZzZ9BSQ";//애플리케이션 클라이언트 시크릿값";
        String apiURL;
        String reuslt;

        ArrayList<SearchBook> searchBooks = new ArrayList<>();
        try {

            String text;
            if (search.title != null){
                text = URLEncoder.encode(search.title, StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_titl="+text;
            }
            else if(search.isbn!= null){
                text = URLEncoder.encode(search.isbn, StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_isbn="+text;
            }
            else{
                text = URLEncoder.encode(search.author, StandardCharsets.UTF_8);
                apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_auth="+text;
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
            con.disconnect();
            reuslt = response.toString();



        }catch (IOException e) {
            System.out.println(e);
            reuslt = null;

        }
        return reuslt;
    }


    public ArrayList<SearchBook> XmlToBooks(String XmlString){

        ArrayList<SearchBook> searchBooks = new ArrayList<>();

        try {
            // xml을 파싱해주는 객체를 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();

            // xml 문자열은 InputStream으로 변환
            InputStream is = new ByteArrayInputStream(XmlString.getBytes());
            // 파싱 시작
            Document doc = documentBuilder.parse(is);
            // 최상위 노드 찾기
            Element element = doc.getDocumentElement();
            // 원하는 태그 데이터 찾아오기
            NodeList items = element.getElementsByTagName("item");

            for(int i=0;i<items.getLength();i++) {

                SearchBook searchBook = new SearchBook();
                NodeList childNodes = items.item(i).getChildNodes();
                for (int j = 0; j < childNodes.getLength(); ++j) {

                    switch (childNodes.item(j).getNodeName()) {
                        case "title":
                            searchBook.setTitle(childNodes.item(j).getTextContent());
                            break;
                        case "price":
                            searchBook.setPrice(childNodes.item(j).getTextContent());
                            break;
                        case "isbn":
                            searchBook.setIsbn(childNodes.item(j).getTextContent());
                            break;
                        case "publisher":
                            searchBook.setPublisher(childNodes.item(j).getTextContent());
                            break;
                        case "pubdate":
                            searchBook.setPubdate(childNodes.item(j).getTextContent());
                            break;
                        case "author":
                            searchBook.setAuthor(childNodes.item(j).getTextContent());
                            break;
                        case "description":
                            searchBook.setDescription(childNodes.item(j).getTextContent());
                            break;
                        case "image":
                            searchBook.setImage(childNodes.item(j).getTextContent());
                            break;
                    }

                }
                searchBooks.add(searchBook);

            }


        }catch (Exception e){
            System.out.println(e);
        }
        return searchBooks;
    }


    @Data
    @NoArgsConstructor
    public static class Search {

        private String title;
        private String isbn;
        private String author;



        @Builder
        public Search(String isbn, String title, String author) {
            this.title = title;
            this.isbn = isbn;
            this.author = author;
        }
    }


}
