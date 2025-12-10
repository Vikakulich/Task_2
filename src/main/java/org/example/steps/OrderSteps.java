package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.example.constants.ApiEndpoint;
import org.example.pojo.OrderCreateRequest;
import org.example.pojo.UserCreateAndEditRequest;
import org.example.pojo.UserLoginResponse;

import static org.example.steps.UserSteps.requestSpecification;

public class OrderSteps {

    @Step("Создание нового заказа без авторизации")
    public ValidatableResponse orderCreate(OrderCreateRequest orderCreateRequest) {
        return requestSpecification()
                .body(orderCreateRequest)
                .post(ApiEndpoint.ORDERS)
                .then();
    }

    @Step("Создание нового заказа после авторизации")
    public ValidatableResponse orderCreateAfterLogin(UserCreateAndEditRequest userLoginRequest, OrderCreateRequest orderCreateRequest) {
        UserSteps userSteps = new UserSteps();
        Response response = userSteps.userLogin(userLoginRequest)
                .extract().response();
        UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            return requestSpecification()
                    .header("Authorization", accessToken)
                    .body(orderCreateRequest)
                    .post(ApiEndpoint.ORDERS)
                    .then();
        } else {
            throw new RuntimeException("Access token is null or empty");
        }
    }

    @Step("Получение заказов без авторизации")
    public ValidatableResponse orderList() {
        return requestSpecification()
                .get(ApiEndpoint.ORDERS)
                .then();
    }

    @Step("Получение заказов после авторизации")
    public ValidatableResponse orderListAfterLogin(UserCreateAndEditRequest userLoginRequest) {
        UserSteps userSteps = new UserSteps();
        Response response = userSteps.userLogin(userLoginRequest)
                .extract().response();
        UserLoginResponse userLoginResponse = response.as(UserLoginResponse.class);
        String accessToken = userLoginResponse.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            return requestSpecification()
                    .header("Authorization", accessToken)
                    .get(ApiEndpoint.ORDERS)
                    .then();
        } else {
            throw new RuntimeException("Access token is null or empty");
        }
    }
}

