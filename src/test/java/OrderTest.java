import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import restClient.OrderClient;
import restClient.UserClient;

import java.util.List;


public class OrderTest {
    OrderClient orderClient;
    UserClient userClient;
    Faker faker = new Faker();
    static List<String> ingredients;

    {
        orderClient = new OrderClient();
        userClient = new UserClient();
        ingredients=orderClient.getIngredients().extract().path("data._id");
    }


    @Test
    @DisplayName("Создание заказа неавторизованным юзером.")
    @Description("Проверяем, что заказ с ингридиентами неавторизованным юзером успешно создан.")
    public void createOrder_returns_success_when_user_not_authorized() {
        Order order = new Order(ingredients);
        ValidatableResponse response = orderClient.createOrder(order, "");
        response.statusCode(200);
        Assertions.assertNotNull(response.extract().path("order.number"));
    }

    @Test
    @DisplayName("Создание заказа авторизованным юзером.")
    @Description("Проверяем, что заказ с ингридиентами авторизованным юзером успешно создан.")
    public void createOrder_returns_success_when_user_authorized() {
       // List<String> ingredients = orderClient.getIngredients().extract().path("data._id");
        User user = new User(faker.bothify("??????@gmail.com"), faker.name().username(), "12345");
        String token = userClient.createUser(user).extract().path("accessToken");
        Order order = new Order(ingredients);
        ValidatableResponse response = orderClient.createOrder(order, token);
        response.statusCode(200);
        Assertions.assertNotNull(response.extract().path("order.number"));
        Assertions.assertEquals(user.getEmail(), response.extract().path("order.owner.email"));
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов")
    @Description("Проверяем, что нельзя ооздать заказ без ингридиентов")
    public void createOrder_returns_fail_when_order_without_ingredients() {
        Order order = new Order(null);
        ValidatableResponse response = orderClient.createOrder(order, "");
        response.statusCode(400);
        Assertions.assertEquals("Ingredient ids must be provided", response.extract().path("message"));
    }

    @Test
    @DisplayName("Создание заказа с несуществующим ингредиентом")
    @Description("Проверяем, что нельзя ооздать заказ с несуществующим ингредиентом")
    public void createOrder_returns_fail_when_order_contains_incorrect_ingredient() {
        Order order = new Order(List.of("1"));
        ValidatableResponse response = orderClient.createOrder(order, "");
        response.statusCode(500);

    }

    @Test
    @DisplayName("Получение заказа юзера. Юзер авторизован")
    @Description("Проверяем, что заказы юзера получены")
    public void getOrders_returns_success_when_user_authorized() {
       // List<String> ingredients = orderClient.getIngredients().extract().path("data._id");
        User user = new User(faker.bothify("??????@gmail.com"), faker.name().username(), "12345");
        String token = userClient.createUser(user).extract().path("accessToken");
        Order order = new Order(ingredients);
        orderClient.createOrder(order,token);
        ValidatableResponse response = orderClient.getOrders(token);
        response.statusCode(200);
        Assertions.assertNotNull(response.extract().path("orders"));
    }

    @Test
    @DisplayName("Получение заказа юзера. Юзер не авторизован")
    @Description("Проверяем, что нельзя получить заказы неавторизованным юзером")
    public void getOrders_returns_fail_when_user_not_authorized() {
        Order order = new Order(ingredients);
        ValidatableResponse response = orderClient.getOrders("");
        response.statusCode(401);
        Assertions.assertEquals("You should be authorised",response.extract().path("message"));

    }
}
