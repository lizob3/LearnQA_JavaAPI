import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @BeforeAll
    static void setUpSsl() {
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void testRestAssured() {
        Map<String,String> params = new HashMap<>();
        params.put("name", "liza");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }
}
