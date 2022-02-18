package restClient;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseTest {

    @Step("GET запрос /api/ingredients")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(init())
                .when()
                .get(EndPoints.getIngredients)
                .then();
    }

    @Step("POST запрос /api/orders")
    public ValidatableResponse createOrder(Order order, String token) {
        return given()
                .spec(init())
                .header("Authorization", token)
                .body(order)
                .when()
                .post(EndPoints.createOrder)
                .then();
    }

    @Step("GET запрос /api/orders")
    public ValidatableResponse getOrders(String token) {
        return given()
                .spec(init())
                .header("Authorization", token)
                .when()
                .get(EndPoints.getOrders)
                .then();
    }

}
