import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;

import static io.restassured.RestAssured.given;


public class BaseTest {
    BaseHttpClient baseHttpClient = new BaseHttpClient();
    Faker faker = new Faker();
    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    protected static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    protected static final String LOGIN_USER_ENDPOINT = "/api/auth/login";
    protected static final String USER_ENDPOINT = "/api/auth/user";
    protected static final String ORDER_ENDPOINT = "/api/orders";
    protected static final String GET_INGREDIENTS_ENDPOINT = "/api/ingredients";


    ValidatableResponse userResponse;
    ValidatableResponse orderResponse;
    UserResponse userResponseBody;


    //тест данные
    UserRequest userRequest = new UserRequest(faker.internet().emailAddress(), faker.internet().password(), faker.name().username());
    UserRequest updateUserRequestBody = new UserRequest(faker.internet().emailAddress(), faker.internet().password(), faker.name().username());
    UserRequest noNameUserRequest = UserRequest.builder().email(faker.internet().emailAddress()).password(faker.internet().password()).build();
    UserRequest noEmailUserRequest = UserRequest.builder().password(faker.internet().password()).name(faker.name().username()).build();
    UserRequest noPasswordUserRequest = UserRequest.builder().email(faker.internet().emailAddress()).name(faker.name().username()).build();
    UserRequest loginUserRequest = UserRequest.builder().email(userRequest.getEmail()).password(userRequest.getPassword()).build();
    UserRequest emptyLoginUserRequest = UserRequest.builder().email("").password("").build();
    UserRequest noEmailLoginUserRequest = UserRequest.builder().email("").password(userRequest.getPassword()).build();
    UserRequest noPasswordLoginUserRequest = UserRequest.builder().email(userRequest.getEmail()).password("").build();

    String ingredientsJson = "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa72\", "
                                            + "\"61c0c5a71d1f82001bdaaa73\", "
                                            + "\"61c0c5a71d1f82001bdaaa74\", "
                                            + "\"61c0c5a71d1f82001bdaaa6d\"]}";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Создать уникального пользователя и вернуть ответ")
    public ValidatableResponse createUniqueUserAndReturnAsResponse(UserRequest request){
        return baseHttpClient.postRequest(CREATE_USER_ENDPOINT, request);
    }

    @Step ("Получить Json строку ингедиентов")
    public String getIngredients(){
        String ingredients = given().get(BASE_URL + GET_INGREDIENTS_ENDPOINT).then().extract().path("data._id").toString();
        String cleanedIngredients = ingredients.replace("[", "").replace("]", "").replace(" ", ""); //убрать скобки и пробелы
        String[] elements = cleanedIngredients.split(","); //разделить по запятой

        for (int i = 0; i < elements.length; i++) { //добавить кавычки
            elements[i] = "\"" + elements[i] + "\"";
        }

        String output = String.join(", ", elements); //соединить обратно в строку через запятую
        return ingredientsJson = "{\"ingredients\": [" + output + "]}";
    }


    @Step("Создать заказ без авторизации")
    public ValidatableResponse createOrderWithNoAuth(String ingredientsJson){
            return baseHttpClient.postRequestJsonString(ORDER_ENDPOINT, ingredientsJson);
    }

    @Step("Создать заказ с авторизацией")
    public ValidatableResponse createOrderWithAuth(String token, String ingredientsJson){
        return baseHttpClient.postRequestJsonStringWithAuth(token, ORDER_ENDPOINT, ingredientsJson);
    }

    @Step("Удалить пользователя")
    public void deleteUser(String accessToken){
        try {
            baseHttpClient.deleteRequestWithAuth(accessToken, USER_ENDPOINT);
        } catch (Exception e) {
            System.err.println("Failed to delete user data: " + e.getMessage());
        }
    }

    @After
    public void cleanUp(){
        if(userResponse != null && userResponse.extract().statusCode() == 200){
            int statusCode = userResponse.extract().statusCode();
            if (statusCode == 200){
                deleteUser(userResponseBody.getAccessToken());
                System.out.println("User was deleted.");
            } else {
                System.out.println("User creation failed with status code: " + statusCode);
            }
        } else {
            System.out.println("No user was created. Skipping user deletion.");
        }
    }
}
