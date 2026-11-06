package lib;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {

    @BeforeAll
    static void setUpSsl() {
        RestAssured.useRelaxedHTTPSValidation();
    }

    protected String getHeader(Response response, String name) {
        Headers headers = response.getHeaders();

        assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name " + name);
        return headers.getValue(name);
    }

    protected String getCookie(Response response, String name) {
        Map<String,String> cookies = response.getCookies();

        assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name " + name);
        return cookies.get(name);
    }

    protected int getIntFromJson(Response response, String name) {
        response.then().assertThat().body("$",hasKey(name));
        return response.jsonPath().getInt(name);
    }
}
