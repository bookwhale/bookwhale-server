package com.teamherb.bookstoreback.common.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;

public class AcceptanceUtils {

    public static Long getIdFromResponse(ExtractableResponse<Response> response) {
        String[] split = response.header(HttpHeaders.LOCATION).split("/");
        return Long.parseLong(split[split.length - 1]);
    }
}
