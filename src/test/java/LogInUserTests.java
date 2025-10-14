import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogInUserTests extends BaseTest{

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void testLoginValidUserIsSuccessful(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest); //создать пользователя
        userResponseBody = userResponse.extract().body().as(UserResponse.class);
        ValidatableResponse response = baseHttpClient.postRequest(LOGIN_USER_ENDPOINT, loginUserRequest); // авторизация пользователя
        UserResponse responseBody = response.extract().body().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(200);//проверить код ответа
        assertThat(responseBody.getUser().getEmail()).isEqualTo(userResponseBody.getUser().getEmail());//проверить, что поля ответа совпадают с полями запроса
        assertThat(responseBody.getUser().getName()).isEqualTo(userResponseBody.getUser().getName());
        assertThat(responseBody.getSuccess()).isEqualTo(true);//проверить остальные поля
        assertThat(responseBody.getAccessToken()).isNotNull();
        assertThat(responseBody.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("Логин с пустым логином и паролем")
    public void testEmptyLoginReturnsError(){
        ValidatableResponse response = baseHttpClient.postRequest(LOGIN_USER_ENDPOINT, emptyLoginUserRequest);
        UserResponse responseBody = response.extract().body().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(401);//проверить код ответа
        assertThat(responseBody.getMessage()).isEqualTo("email or password are incorrect"); //проверить сообщение об ошибке
    }

    @Test
    @DisplayName("Логин с пустым паролем")
    public void testNoPasswordLoginReturnsError(){
        ValidatableResponse response = baseHttpClient.postRequest(LOGIN_USER_ENDPOINT, noPasswordLoginUserRequest);
        UserResponse responseBody = response.extract().body().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(401);//проверить код ответа
        assertThat(responseBody.getMessage()).isEqualTo("email or password are incorrect"); //проверить сообщение об ошибке
    }

    @Test
    @DisplayName("Логин с пустым email")
    public void testNoEmailLoginReturnsError(){
        ValidatableResponse response = baseHttpClient.postRequest(LOGIN_USER_ENDPOINT, noEmailLoginUserRequest);
        UserResponse responseBody = response.extract().body().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(401);//проверить код ответа
        assertThat(responseBody.getMessage()).isEqualTo("email or password are incorrect"); //проверить сообщение об ошибке
    }
}
