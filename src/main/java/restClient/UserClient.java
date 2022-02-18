package restClient;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseTest {

    @Step("POST запрос /api/auth/register")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(init())
                .body(user)
                .when()
                .post(EndPoints.createUser)
                .then();

    }
    @Step("POST запрос /api/auth/login")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(init())
                .body(userCredentials)
                .when()
                .post(EndPoints.login)
                .then();
    }
    @Step("PATCH запрос /api/auth/user")
    public ValidatableResponse update(User user, String token) {
        return given()
                .spec(init())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(EndPoints.updateUser)
                .then();

    }

    @Step("DELETE запрос /api/auth/user")
    public ValidatableResponse delete(String token) {
        return given()
                .spec(init())
                .header("Authorization", token)
                .when()
                .delete(EndPoints.deleteUser)
                .then();
    }
}
