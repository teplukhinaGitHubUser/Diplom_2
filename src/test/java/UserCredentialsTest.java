import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserCredentials;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import restClient.BaseTest;
import restClient.UserClient;

public class UserCredentialsTest extends BaseTest {
    UserClient userClient;
    Faker faker = new Faker();
    String token;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Логин с корректыми кредами")
    @Description("Проверяем, что юзер успешно залогинился")
    public void login_returns_success(){
        User user = new User(faker.bothify("??????@gmail.com"),faker.name().username(),"12345");
        UserCredentials userCredentials = new UserCredentials(user.getEmail(),user.getPassword());
        token=userClient.createUser(user).extract().path("accessToken");
        ValidatableResponse response = userClient.login(userCredentials);
        response.statusCode(200);
        Assertions.assertEquals(user.getEmail(),response.extract().path("user.email"));
        Assertions.assertEquals(user.getName(),response.extract().path("user.name"));
        Assertions.assertNotNull(response.extract().path("accessToken"));
        Assertions.assertNotNull(response.extract().path("refreshToken"));

    }

    @ParameterizedTest
    @CsvSource({
            "testEmailqq_upd@mail.ru,12345",
            "testEmailqq@mail.ru,123455",
    })
    @DisplayName("Логин с некорректыми кредами")
    @Description("Проверяем, что юзер не залогинился")
    public void login_fails_when_incorrect_login_or_password(String email, String password){
        User user = new User("testEmailqq@mail.ru",faker.name().username(),"12345");
        UserCredentials userCredentials = new UserCredentials(email,password);
        token=userClient.createUser(user).extract().path("accessToken");
        ValidatableResponse response = userClient.login(userCredentials);
        response.statusCode(401);
        Assertions.assertEquals("email or password are incorrect",response.extract().path("message"));
    }

    @AfterEach
    public void tearDown(){
        if(token!=null) {
            userClient.delete(token);
        }
    }
}
