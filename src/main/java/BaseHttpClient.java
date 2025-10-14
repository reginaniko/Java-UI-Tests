import io.restassured.response.ValidatableResponse;

//import java.util.List;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;

public class BaseHttpClient {
    private final String JSON = "application/json";

    public ValidatableResponse getRequest(String url){
        return given().config(config)
                .header("Content-Type", JSON)
                .get(url).then().log().all();
    }

    public ValidatableResponse getRequestWithAuth(String token, String url){
        return given().config(config)
                .header("Content-Type", JSON)
                .header("Authorization", token)
                .get(url).then().log().all();
    }
    public ValidatableResponse postRequest(String url, Object body) {
        return given()
                .header("Content-type", JSON)
                .body(body).log().all()
                .when().post(url).then().log().all();
    }
    public ValidatableResponse postRequestJsonString(String url, String json) {
        return given()
                .header("Content-type", JSON)
                .body(json).log().all()
                .when().post(url).then().log().all();
    }
    public ValidatableResponse postRequestJsonStringWithAuth(String token, String url, String json) {
        return given()
                .header("Content-type", JSON)
                .header("Authorization", token)
                .body(json).log().all()
                .when().post(url).then().log().all();
    }
    public ValidatableResponse deleteRequestWithAuth(String token, String url) {
        return given()
                .header("Content-Type", JSON)
                .header("Authorization", token)
                .delete(url).then().log().all();
    }

    public ValidatableResponse patchRequestWithAuth(String token, String url, Object body){
        return given()
                .header("Content-Type", JSON)
                .header("Authorization", token)
                .body(body).log().all()
                .when().patch(url).then().log().all();
    }

    public ValidatableResponse patchRequest( String url, Object body){
        return given()
                .header("Content-Type", JSON)
                .body(body).log().all()
                .when().patch(url).then().log().all();
    }

}
