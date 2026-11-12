package lib;

import groovy.lang.GString;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    private final String baseUrl;

    public ApiCoreRequests (String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String path, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(baseUrl+path)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String path, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(baseUrl+path)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String path, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(baseUrl+path)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String path, Map<String,String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(baseUrl+path)
                .andReturn();
    }

    @Step("Make a PUT-request")
    public Response makePutRequestNoAuth(String path, Map<String,String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .put(baseUrl+path)
                .andReturn();
    }

    @Step("Make a POST-request with token and auth cookie")
    public Response makePutRequest(String path, Map<String,String> userData, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(userData)
                .put(baseUrl+path)
                .andReturn();
    }

    @Step("Make a DELETE-request")
    public Response makeDeleteRequestNoAuth(String path) {
        return given()
                .filter(new AllureRestAssured())
                .delete(baseUrl+path)
                .andReturn();
    }

    @Step("Make a DELETE-request with token and auth cookie")
    public Response makeDeleteRequest(String path, String token, String cookie, String userId) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .delete(baseUrl+path+userId)
                .andReturn();
    }

}
