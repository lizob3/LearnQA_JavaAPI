package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test checks error message when trying to delete user while being non-authorized")
    @DisplayName("Delete user being non-authorized")
    @Test
    public void testDeleteUserUnauthorized() {
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequestNoAuth("https://playground.learnqa.ru/api/user/2");

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error", "Auth token not supplied");
    }

    @Description("This test successfully delete authorized user")
    @DisplayName("Delete user")
    @Test
    public void testDeleteUser() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/",
                header,
                cookie,
                userId);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);
        Assertions.assertJsonByName(responseDeleteUser, "success", "!");

        //GET DATA
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/"+userId,
                header,
                cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Description("This test checks error message when trying to delete user while being authorized as another user")
    @DisplayName("Delete another user")
    @Test
    public void testDeleteAnotherUser() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/",
                header,
                cookie,
                "167587");

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error", "This user can only delete their own account.");
    }
}
