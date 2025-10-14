import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTests extends BaseTest{

    @Test
    @DisplayName("Create unique user")
    public void testCreateUniqueUserIsSuccessful(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest);
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(200);//checking response code
        assertThat(userResponseBody.getUser().getEmail()).isEqualTo(userRequest.getEmail());//checking response mathches the request
        assertThat(userResponseBody.getUser().getName()).isEqualTo(userRequest.getName());
        assertThat(userResponseBody.getSuccess()).isEqualTo(true);//checking the rest of the fields are not empty
        assertThat(userResponseBody.getAccessToken()).isNotNull();
        assertThat(userResponseBody.getRefreshToken()).isNotNull();
    }


    @Test
    @DisplayName("Create user that's already registered")
    public void testCreateDuplicateUserReturnsError(){

        userResponse = createUniqueUserAndReturnAsResponse(userRequest);
        userResponseBody = userResponse.extract().body().as(UserResponse.class);
        ValidatableResponse userResponse1 = createUniqueUserAndReturnAsResponse(userRequest);//create user with data that alredy exists
        UserResponse duplicateUserResponse = userResponse1.extract().as(UserResponse.class);

        assertThat(userResponse1.extract().statusCode()).isEqualTo(403);
        assertThat(duplicateUserResponse.getSuccess()).isEqualTo(false);
        assertThat(duplicateUserResponse.getMessage()).isEqualTo("User already exists");
    }

    @Test
    @DisplayName("Create user with no name")
    public void testCreateUserWithoutNameReturnsError(){
        userResponse = createUniqueUserAndReturnAsResponse(noNameUserRequest); //create user with no name
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(403);//check response code
        assertThat(userResponseBody.getSuccess()).isEqualTo(false);
        assertThat(userResponseBody.getMessage()).isEqualTo("Email, password and name are required fields");//check error message
    }

    @Test
    @DisplayName("Create user without email")
    public void testCreateUserWithoutEmailReturnsError(){
        userResponse = createUniqueUserAndReturnAsResponse(noEmailUserRequest); //create user with no email
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(403);//check response code
        assertThat(userResponseBody.getSuccess()).isEqualTo(false);
        assertThat(userResponseBody.getMessage()).isEqualTo("Email, password and name are required fields");//check error message
    }

    @Test
    @DisplayName("Create user without password")
    public void testCreateUserWithoutPasswordReturnsError(){
        userResponse= createUniqueUserAndReturnAsResponse(noPasswordUserRequest); //create user with no password
        userResponseBody = userResponse.extract().body().as(UserResponse.class);

        assertThat(userResponse.extract().statusCode()).isEqualTo(403);//check response code
        assertThat(userResponseBody.getSuccess()).isEqualTo(false);
        assertThat(userResponseBody.getMessage()).isEqualTo("Email, password and name are required fields");//check error message
    }
}
