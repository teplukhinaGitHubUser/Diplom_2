import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import restClient.UserClient;

public class UserTest {
    UserClient userClient;
    Faker faker = new Faker();
    String token;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Создание юзера.")
    @Description("Проверяем, что юзер успешно создан")
    public void createUser_returns_success(){
        User user = new User(faker.bothify("??????@gmail.com"),faker.name().username(),"12345");
        ValidatableResponse response=userClient.createUser(user);
        response.statusCode(200);
        Assertions.assertEquals(user.getEmail(),response.extract().path("user.email"));
        Assertions.assertEquals(user.getName(),response.extract().path("user.name"));
        Assertions.assertNotNull(response.extract().path("accessToken"));
        Assertions.assertNotNull(response.extract().path("refreshToken"));
        token=response.extract().path("accessToken");

    }

    @Test
    @DisplayName("Создание дубля юзера.")
    @Description("Проверяем, что юзер не создан")
    public void createUser_returns_false_when_user_already_exists(){
        User user = new User(faker.bothify("??????@gmail.com"),faker.name().username(),"12345");
        userClient.createUser(user);
        ValidatableResponse response=userClient.createUser(user);
        response.statusCode(403);
        Assertions.assertEquals("User already exists",response.extract().path("message"));
    }

    @ParameterizedTest
    @CsvSource({
            "test1@gmail.com,username1,",
            ",username2,12345",
            "test2@gmail.com,,12345"
    })
    @DisplayName("Создание юзера с незаполненными параметрами")
    @Description("Проверяем, что юзер не создан")
    public void createUser_returns_false_when_password_is_null(String email,String username,String password){
        User user = new User(email,username,password);
        ValidatableResponse response=userClient.createUser(user);
        response.statusCode(403);
        Assertions.assertEquals("Email, password and name are required fields",response.extract().path("message"));
    }

    @AfterEach
    public void tearDown(){
        if(token!=null) {
            userClient.delete(token);
        }
    }
}
