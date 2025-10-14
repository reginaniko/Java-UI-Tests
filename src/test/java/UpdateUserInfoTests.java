import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserInfoTests extends BaseTest{

    @Before
    public void createUser(){
        userResponse = createUniqueUserAndReturnAsResponse(userRequest);//create user
        userResponseBody = userResponse.extract().body().as(UserResponse.class);
    }

    @Test
    @DisplayName("Change user data when authorized")
    public void testUpdateUserInfoWithAuthIsSuccessful(){
        ValidatableResponse response = baseHttpClient.patchRequestWithAuth(userResponseBody.getAccessToken(), USER_ENDPOINT, updateUserRequestBody);//change data
        UserResponse updateUserResponseBody = response.extract().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(200);//check response code
        assertThat(updateUserResponseBody.getUser().getEmail()).isEqualTo(updateUserRequestBody.getEmail());//check if email and name fields changed
        assertThat(updateUserResponseBody.getUser().getName()).isEqualTo(updateUserRequestBody.getName());
        //check that the password field was updated through authorization with the new password
        ValidatableResponse passwordResponse = baseHttpClient.postRequest(LOGIN_USER_ENDPOINT, updateUserRequestBody);
        passwordResponse.assertThat().statusCode(200).and().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Change user data without authorization")
    public void testUpdateUserInfoWithoutAuthReturnsError(){
        ValidatableResponse response = baseHttpClient.patchRequest(USER_ENDPOINT, updateUserRequestBody);
        UserResponse updateUserResponseBody = response.extract().as(UserResponse.class);

        assertThat(response.extract().statusCode()).isEqualTo(401);//check response code
        assertThat(updateUserResponseBody.getSuccess()).isEqualTo(false);
        assertThat(updateUserResponseBody.getMessage()).isEqualTo("You should be authorised");
    }
}
