
import io.qameta.allure.junit4.DisplayName;

import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import java.lang.String;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderTests extends BaseTest {
    String ingredientsJson;

    @Test
    @DisplayName("Create order with no ingredients without authorization")
    public void testCreateEmptyOrderWithNoAuth(){
        orderResponse = createOrderWithNoAuth("");

        orderResponse.assertThat().statusCode(400);
        orderResponse.assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with ingredients without authorization")
    public void testCreateOrderWithNoAuth(){

        ingredientsJson = getIngredients();
        orderResponse = createOrderWithNoAuth(ingredientsJson);

        orderResponse.assertThat().statusCode(200);
        orderResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create order with no ingredients with authorization")
    public void testCreateEmptyOrderWithAuth(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest);//создать пользователя
        userResponseBody = userResponse.extract().as(UserResponse.class);//получть токен
        ValidatableResponse orderResponse = createOrderWithAuth(userResponseBody.getAccessToken(), ""); // создать заказ

        orderResponse.assertThat().statusCode(400);
        orderResponse.assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with ingredients with authorization")
    public void testCreateOrderWithAuth(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest);//создать пользователя
        userResponseBody = userResponse.extract().as(UserResponse.class);//получть токен
        ValidatableResponse orderResponse = createOrderWithAuth(userResponseBody.getAccessToken(), getIngredients()); // создать заказ

        orderResponse.assertThat().statusCode(200);
        orderResponse.assertThat().body("success", equalTo(true));
    }
}
