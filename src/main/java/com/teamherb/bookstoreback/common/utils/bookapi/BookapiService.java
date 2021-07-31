package com.teamherb.bookstoreback.common.utils.bookapi;


import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class BookapiService {

    public void BookapiRequest(String keyword){

        String clientId = "JhuTF7N1bKBh_QeQzii5";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "f3rZzZ9BSQ";//애플리케이션 클라이언트 시크릿값";
        try {
        String text = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String apiURL = "https://openapi.naver.com/v1/search/book.json?query="+text;

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
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String title = obj.getString("title");
            String link = (obj.has("image")) ? obj.getString("image"): null;
            String author = obj.getString("author");
            Integer price = (obj.has("price")) ? obj.getInt("price"): null;
            String isbn = obj.getString("isbn");
            System.out.println("isbn = " + isbn);
            System.out.println("price = " + price);
            System.out.println("title = " + title);
            System.out.println("link = " + link);
            System.out.println("author = " + author);




        }
        //System.out.println(response.toString());
        }catch (Exception e) {
            System.out.println(e);
        }
    }

}
