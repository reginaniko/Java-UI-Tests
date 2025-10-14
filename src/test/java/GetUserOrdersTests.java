
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.equalTo;

public class GetUserOrdersTests extends BaseTest{

    @Test
    @DisplayName("Получить заказы авторизованного пользователя")
    public void testGetOrdersOfAuthUserIsSuccessful(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest); //создать пользователя
        userResponseBody = userResponse.extract().body().as(UserResponse.class);
        createOrderWithAuth(userResponseBody.getAccessToken(), ingredientsJson); //создать заказ
         //получить список заказов
        baseHttpClient.getRequestWithAuth(userResponseBody.getAccessToken(), ORDER_ENDPOINT)
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получить заказы неавторизованного пользователя")
    public void testGetOrdersOfNoAuthUserReturnsError(){
        baseHttpClient.getRequest(ORDER_ENDPOINT)
                .assertThat().statusCode(401)
                .and().body("message", equalTo("You should be authorised"));
    }
}
