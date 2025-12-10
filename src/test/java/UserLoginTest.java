import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.UserCreateAndEditRequest;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    public String testEmail = "auth_" + System.nanoTime() + "@burger.dev";
    public String testPassword = "AuthTest@123";
    public String testName = "Auth Tester";

    @After
    public void cleanupAccount() {
        UserSteps userSteps = new UserSteps();
        UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        userSteps.userDeleteAfterLogin(request);
    }

    @Test
    @DisplayName("Вход с корректными учетными данными")
    @Description("Проверяем успешный вход в систему с правильным email и паролем")
    public void successfulAuthentication() {
        UserCreateAndEditRequest registerRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(registerRequest);
        userSteps.userLogin(registerRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Вход с неверным email отклоняется")
    @Description("Попытка входа с неправильным email должна вернуть ошибку 401")
    public void rejectLoginWithInvalidEmail() {
        UserCreateAndEditRequest registerRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        UserCreateAndEditRequest wrongEmailRequest = new UserCreateAndEditRequest("fake@burger.dev", testPassword, testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(registerRequest);
        userSteps.userLogin(wrongEmailRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Вход с неверным паролем отклоняется")
    @Description("Попытка входа с неправильным паролем должна вернуть ошибку 401")
    public void rejectLoginWithInvalidPassword() {
        UserCreateAndEditRequest registerRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        UserCreateAndEditRequest wrongPasswordRequest = new UserCreateAndEditRequest(testEmail, "WrongPass123", testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(registerRequest);
        userSteps.userLogin(wrongPasswordRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Вход с неверными email и паролем отклоняется")
    @Description("Попытка входа с обоими неправильными данными должна вернуть 401")
    public void rejectLoginWithBothIncorrect() {
        UserCreateAndEditRequest registerRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        UserCreateAndEditRequest wrongRequest = new UserCreateAndEditRequest("wrong@burger.dev", "WrongPass123", testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(registerRequest);
        userSteps.userLogin(wrongRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
}
