import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserInfoTests extends BaseTest{

    @Before
    public void createUser(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest);//создать пользователя
        userResponseBody = userResponse.extract().body().as(UserResponse.class);
    }

    @Test
    @DisplayName("Изменить данные пользователя с авторизацией")
    public void testUpdateUserInfoWithAuthIsSuccessful(){
        ValidatableResponse response = baseHttpClient.patchRequestWithAuth(userResponseBody.getAccessToken(), USER_ENDPOINT, updateUserRequestBody);//изменить данные
        UserResponse updateUserResponseBody = response.extract().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(200);//проверить код ответа
        assertThat(updateUserResponseBody.getUser().getEmail()).isEqualTo(updateUserRequestBody.getEmail());//проверить, что поля имя и почта обновились
        assertThat(updateUserResponseBody.getUser().getName()).isEqualTo(updateUserRequestBody.getName());
        //проверить, что обновилось поле пароля через авторизацию в сиситеме с новым паролем
        ValidatableResponse passwordResponse = baseHttpClient.postRequest(LOGIN_USER_ENDPOINT, updateUserRequestBody);
        passwordResponse.assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменить данные пользователя без авторизации")
    public void testUpdateUserInfoWithoutAuthReturnsError(){
        ValidatableResponse response = baseHttpClient.patchRequest(USER_ENDPOINT, updateUserRequestBody);
        UserResponse updateUserResponseBody = response.extract().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(401);//проверить код ответа
        assertThat(updateUserResponseBody.getSuccess()).isEqualTo(false);
        assertThat(updateUserResponseBody.getMessage()).isEqualTo("You should be authorised");
    }
}
