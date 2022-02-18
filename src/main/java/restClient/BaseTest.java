package restClient;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;


public class BaseTest {
public RequestSpecification init(){
    return new RequestSpecBuilder()
            .setContentType("application/json")
            .setBaseUri("https://stellarburgers.nomoreparties.site/")
            .build();
}

}
