package com.bookwhale.auth.dto.provider;

import com.bookwhale.article.dto.NaverBookRequest;
import com.bookwhale.auth.domain.token.NaverOAuthToken;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.google.common.hash.Hashing;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component("NaverOAuthProvider")
public class NaverOAuthProvider implements OAuthProvider {

    @Value("${external-api.auth.naver.client-id}")
    private String clientId;
    @Value("${external-api.auth.naver.client-key}")
    private String clientSecret;
    @Value("${external-api.auth.naver.base-url}")
    private String baseRequestURL;
    @Value("${external-api.auth.naver.callback-url}")
    private String callbackURL;
    @Value("${external-api.auth.naver.state}")
    private String state;
    @Value("${external-api.auth.naver.access-token-url}")
    private String accessTokenRequestURL;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getOAuthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackURL);
        params.put("state", Hashing.sha512().hashString(state, StandardCharsets.UTF_8).toString());

        String parameterString = params.entrySet().stream()
            .map(x -> x.getKey() + "=" + x.getValue()).collect(Collectors.joining("&"));

        return baseRequestURL + "?" + parameterString;
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("code", accessCode);
        requestBody.add("state",
            Hashing.sha512().hashString(state, StandardCharsets.UTF_8).toString());

        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        var requestEntity = new HttpEntity<>(requestBody, requestHeader);

        return restTemplate.exchange(accessTokenRequestURL, HttpMethod.POST, requestEntity,
            String.class);
    }

    public ResponseEntity<String> getUserInfoFromProvider(NaverOAuthToken oAuthToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccessToken());

        HttpEntity<Map<String, String>> request = new HttpEntity(headers);

        String url = "https://openapi.naver.com/v1/nid/me";

        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    public String getBookInfoFromBookSearch(NaverBookRequest req) {
        String apiURL, text, result;
        String pageUrl = "&display=" + req.getDisplay() + "&start=" + req.getStart();
        try {
            String apiBaseURL = "https://openapi.naver.com/v1/search/book_adv.xml";
            if (req.getTitle() != null) {
                text = URLEncoder.encode(req.getTitle(), StandardCharsets.UTF_8);
                apiURL = apiBaseURL + "?d_titl=" + text;
            } else if (req.getIsbn() != null) {
                text = URLEncoder.encode(req.getIsbn(), StandardCharsets.UTF_8);
                apiURL = apiBaseURL + "?d_isbn=" + text;
            } else {
                text = URLEncoder.encode(req.getAuthor(), StandardCharsets.UTF_8);
                apiURL = apiBaseURL + "?d_auth=" + text;
            }
            apiURL += pageUrl;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
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
        return result;
    }


}
