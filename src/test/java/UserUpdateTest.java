import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredentials;
import org.junit.jupiter.api.*;
import restClient.UserClient;

public class UserUpdateTest {
    UserClient userClient;
    Faker faker = new Faker();
    String token;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Редактирование юзера. Юзер авторизован.")
    @Description("Проверяем, что данные юзера успешно изменены.")
    public void updateUser_returns_success() {
        User user = new User(faker.bothify("??????@gmail.com"), faker.name().username(), "12345");
        UserCredentials userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        token = userClient.createUser(user).extract().path("accessToken");
        userClient.login(userCredentials);
        User updatedUser = new User(faker.bothify("??????@gmail.com"), faker.name().username(), "1234567");
        ValidatableResponse response = userClient.update(updatedUser, token);
        response.statusCode(200);
        Assertions.assertEquals(updatedUser.getEmail(),response.extract().path("user.email"));
        Assertions.assertEquals(updatedUser.getName(),response.extract().path("user.name"));
    }

    @Test
    @DisplayName("Редактирование юзера. Юзер не авторизован.")
    @Description("Проверяем, что нельзя изменить данные неавторизованным юзером.")
    public void updateUser_fails_when_user_not_authorized() {
        User user = new User(faker.bothify("??????@gmail.com"), faker.name().username(), "12345");
        ValidatableResponse response = userClient.update(user, "");
        response.statusCode(401);
        Assertions.assertEquals("You should be authorised",response.extract().path("message"));

    }

    @AfterEach
    public void tearDown(){
        if(token!=null) {
            userClient.delete(token);
        }
    }
}
