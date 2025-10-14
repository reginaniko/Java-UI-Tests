
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.equalTo;

public class GetUserOrdersTests extends BaseTest{

    @Test
    @DisplayName("Get orders of authorized user")
    public void testGetOrdersOfAuthUserIsSuccessful(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest); //create user
        userResponseBody = userResponse.extract().body().as(UserResponse.class);
        createOrderWithAuth(userResponseBody.getAccessToken(), ingredientsJson); //create order
         //get the list of orders
        baseHttpClient.getRequestWithAuth(userResponseBody.getAccessToken(), ORDER_ENDPOINT)
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Get orders of unauthorized user")
    public void testGetOrdersOfNoAuthUserReturnsError(){
        baseHttpClient.getRequest(ORDER_ENDPOINT)
                .assertThat().statusCode(401)
                .and().body("message", equalTo("You should be authorised"));
    }
}
