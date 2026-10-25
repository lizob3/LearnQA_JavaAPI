import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
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
        Map<String,String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();


        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String,String> cookies = new HashMap<>();
        if (responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }


        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();
    }

    @Test
    public void testParseJSON() {
        JsonPath responseJSON = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String secondMessage = responseJSON.get("messages[1].message");
        System.out.println(secondMessage);

    }

    @Test
    public void printRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String LocationHeader = response.getHeader("Location");
        System.out.println(LocationHeader);
    }

    @Test
    public void redirectCycle() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int redirectCount = 0;
        int statusCode;

        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(url)
                    .andReturn();
            url = response.getHeader("Location");
            statusCode = response.getStatusCode();
            if (url != null) {
                System.out.println(url);
                redirectCount++;
            }
        } while (statusCode >= 300 && statusCode <= 399);
        System.out.println(redirectCount);
    }

    @Test
    public void longtimeJob() throws InterruptedException {
        //Step 1 - Create Job
        JsonPath responseCreateJob = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = responseCreateJob.get("token");
        int seconds = responseCreateJob.get("seconds");

        Map<String,String> params = new HashMap<>();
        params.put("token", token);

        //Step 2 - Check status - Job is not ready
        JsonPath waitForJobCompletion = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

            String status = waitForJobCompletion.get("status");
            System.out.println(status);
            Thread.sleep(seconds*1000);

        //Step 3 - Check status - Job is ready
        JsonPath jobIsCompleted = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

            status = jobIsCompleted.get("status");
            System.out.println(status);
            if (status.equals("Job is ready")) {
                String result = jobIsCompleted.get("result");
                System.out.println(result);
            }

    }

}
