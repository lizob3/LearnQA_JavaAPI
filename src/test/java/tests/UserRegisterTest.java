package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests("https://playground.learnqa.ru/api_dev");

    @Test
    public void testCreateUserWithExistingEmail () {

        String email = "vinkotov@example.com";

        Map<String,String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api_dev/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully () {

        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api_dev/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
        System.out.println(responseCreateAuth.asString());
    }

    @Description("This test checks error message when trying to register with invalid email")
    @DisplayName("Register with invalid email")
    @Test
    public void testCreateUserWithInvalidEmail() {

        String email = "vinkotovexample.com";

        Map<String,String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response resposeCreateAuth = apiCoreRequests.makePostRequest(
                "/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(resposeCreateAuth, 400);
        Assertions.assertResponseTextEquals(resposeCreateAuth, "Invalid email format");
    }

    @Description("This test checks error message when trying to register without necessary field")
    @DisplayName("Register without necessary field")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutNecessaryField(String field) {

        Map<String,String> userData = DataGenerator.getRegistrationDataWithoutNecessaryField(field);
        Response resposeCreateAuth = apiCoreRequests.makePostRequest(
                "/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(resposeCreateAuth, 400);
        Assertions.assertResponseTextEquals(resposeCreateAuth, "The following required params are missed: " + field);
    }

    @Description("This test checks error message when trying to register with 1 symbol username")
    @DisplayName("Register with 1 symbol username")
    @Test
    public void testCreateUserWithShortName() {

        String username = "a";

        Map<String,String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response resposeCreateAuth = apiCoreRequests.makePostRequest(
                "/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(resposeCreateAuth, 400);
        Assertions.assertResponseTextEquals(resposeCreateAuth, "The value of 'username' field is too short");
    }

    @Description("This test checks error message when trying to register with more than 250 symbols username")
    @DisplayName("Register with more than 250 symbols username")
    @Test
    public void testCreateUserWithLongName() {

        String username = "Et. Dictum integer imperdiet morbi justo et sed imperdiet amet, dictum pellentesque nec pulvinar imperdiet mattis et. Nulla aenean risus in vestibulum vestibulum sed amet dolor faucibus. Nisi et. Dapibus amet, nulla ultricies. Quam, in in morbi ultric";

        Map<String,String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response resposeCreateAuth = apiCoreRequests.makePostRequest(
                "/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(resposeCreateAuth, 400);
        Assertions.assertResponseTextEquals(resposeCreateAuth, "The value of 'username' field is too long");
    }


}
