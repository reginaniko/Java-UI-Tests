import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTests extends BaseTest{

    @Test
    @DisplayName("Создать уникального пользователя")
    public void testCreateUniqueUserIsSuccessful(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest);
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(200);//проверить код ответа
        assertThat(userResponseBody.getUser().getEmail()).isEqualTo(userRequest.getEmail());//проверить, что поля ответа совпадают с полями запроса
        assertThat(userResponseBody.getUser().getName()).isEqualTo(userRequest.getName());
        assertThat(userResponseBody.getSuccess()).isEqualTo(true);//проверить, что остальные поля не пустые
        assertThat(userResponseBody.getAccessToken()).isNotNull();
        assertThat(userResponseBody.getRefreshToken()).isNotNull();
    }


    @Test
    @DisplayName("Cоздать пользователя, который уже зарегистрирован")
    public void testCreateDuplicateUserReturnsError(){

        userResponse = createUniqueUserAndReturnAsResponse(userRequest);
        userResponseBody = userResponse.extract().body().as(UserResponse.class);
        ValidatableResponse userResponse1 = createUniqueUserAndReturnAsResponse(userRequest);//создать пользователя с теми же данными
        UserResponse duplicateUserResponse = userResponse1.extract().as(UserResponse.class);

        assertThat(userResponse1.extract().statusCode()).isEqualTo(403);
        assertThat(duplicateUserResponse.getSuccess()).isEqualTo(false);
        assertThat(duplicateUserResponse.getMessage()).isEqualTo("User already exists");
    }

    @Test
    @DisplayName("Создать пользователя без имени")
    public void testCreateUserWithoutNameReturnsError(){
        userResponse = createUniqueUserAndReturnAsResponse(noNameUserRequest); //создать пользователя без имени
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(403);//проверить код ответа
        assertThat(userResponseBody.getSuccess()).isEqualTo(false);
        assertThat(userResponseBody.getMessage()).isEqualTo("Email, password and name are required fields");//проверить сообщение об ошибке
    }

    @Test
    @DisplayName("Создать пользователя без почты")
    public void testCreateUserWithoutEmailReturnsError(){
        userResponse = createUniqueUserAndReturnAsResponse(noEmailUserRequest); //создать пользователя без пароля
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(403);//проверить код ответа
        assertThat(userResponseBody.getSuccess()).isEqualTo(false);
        assertThat(userResponseBody.getMessage()).isEqualTo("Email, password and name are required fields");//проверить сообщение об ошибке
    }

    @Test
    @DisplayName("Создать пользователя без пароля")
    public void testCreateUserWithoutPasswordReturnsError(){
        userResponse= createUniqueUserAndReturnAsResponse(noPasswordUserRequest);
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(403);//проверить код ответа
        assertThat(userResponseBody.getSuccess()).isEqualTo(false);
        assertThat(userResponseBody.getMessage()).isEqualTo("Email, password and name are required fields");//проверить сообщение об ошибке
    }
}
