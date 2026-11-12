package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests("https://playground.learnqa.ru/api_dev");

    @Description("This test checks error message when trying to delete user while being non-authorized")
    @DisplayName("Delete user being non-authorized")
    @Link(name="API docs",  url="https://playground.learnqa.ru/api/map/")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Owner("Liza")
    @Test
    public void testDeleteUserUnauthorized() {
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequestNoAuth("/user/2");

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error", "Auth token not supplied");
    }

    @Description("This test successfully delete authorized user")
    @DisplayName("Delete user")
    @Link(name="API docs",  url="https://playground.learnqa.ru/api/map/")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Owner("Liza")
    @Test
    public void testDeleteUser() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "/user/",
                userData
        );
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "/user/",
                header,
                cookie,
                userId);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);
        Assertions.assertJsonByName(responseDeleteUser, "success", "!");

        //GET DATA
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "/user/"+userId,
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Description("This test checks error message when trying to delete user while being authorized as another user")
    @DisplayName("Delete another user")
    @Link(name="API docs",  url="https://playground.learnqa.ru/api/map/")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Issue("ID-123")
    @Owner("Liza")
    @Test
    public void testDeleteAnotherUser() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "/user/",
                userData
        );
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "/user/",
                header,
                cookie,
                "167587");

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error", "This user can only delete their own account.");
    }
}
