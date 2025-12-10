package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.example.constants.ApiEndpoint;
import org.example.pojo.UserCreateAndEditRequest;
import org.example.pojo.UserLoginResponse;

import static io.restassured.RestAssured.given;

public class UserSteps {

    public static RequestSpecification requestSpecification() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(ApiEndpoint.BASE_URL);
    }

    @Step("Создание нового пользователя")
    public ValidatableResponse userCreate(UserCreateAndEditRequest userCreateAndEditRequest) {
        return requestSpecification()
                .body(userCreateAndEditRequest)
                .post(ApiEndpoint.USER_CREATE)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse userLogin(UserCreateAndEditRequest userLoginRequest) {
        return requestSpecification()
                .body(userLoginRequest)
                .post(ApiEndpoint.USER_LOGIN)
                .then();
    }

    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse userEdit(UserCreateAndEditRequest userCreateAndEditRequest) {
        return requestSpecification()
                .body(userCreateAndEditRequest)
                .patch(ApiEndpoint.USER)
                .then();
    }

    @Step("Изменение данных пользователя после авторизации")
    public ValidatableResponse userEditAfterLogin(UserCreateAndEditRequest userLoginRequest, UserCreateAndEditRequest userCreateAndEditRequest) {
        Response response = userLogin(userLoginRequest)
                .extract().response();
        UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            return requestSpecification()
                    .header("Authorization", accessToken)
                    .body(userCreateAndEditRequest)
                    .patch(ApiEndpoint.USER)
                    .then();
        } else {
            throw new RuntimeException("Access token is null or empty");
        }
    }

    @Step("Удаление пользователя после авторизации")
    public ValidatableResponse userDeleteAfterLogin(UserCreateAndEditRequest userLoginRequest) {
        try {
            Response response = userLogin(userLoginRequest)
                    .extract().response();
            UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
            String accessToken = userLoginResponse.getAccessToken();
            if (accessToken != null && !accessToken.isEmpty()) {
                return requestSpecification()
                        .header("Authorization", accessToken)
                        .delete(ApiEndpoint.USER)
                        .then();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}

