import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.UserCreateAndEditRequest;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreateTest {

    public String testEmail = "user_" + System.nanoTime() + "@stellar.test";
    public String testPassword = "SecurePass#2025";
    public String testName = "Stellar User";

    private boolean skipCleanup = false;

    @After
    public void cleanupUser() {
        if (!skipCleanup) {
            UserSteps userSteps = new UserSteps();
            UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, testPassword, testName);
            userSteps.userDeleteAfterLogin(request);
        }
    }

    @Test
    @DisplayName("Успешная регистрация нового юзера")
    @Description("Проверяем, что новый пользователь может зарегистрироваться в системе")
    public void registerNewAccount() {
        UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(request)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Ошибка при попытке зарегистрировать существующий аккаунт")
    @Description("Попытка регистрации с уже существующим email должна вернуть ошибку")
    public void preventDuplicateRegistration() {
        UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(request);
        userSteps.userCreate(request)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Регистрация без электронной почты невозможна")
    @Description("Попытка создать аккаунт без email должна быть отклонена")
    public void rejectRegistrationWithoutEmail() {
        skipCleanup = true;

        UserCreateAndEditRequest request = new UserCreateAndEditRequest(null, testPassword, testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(request)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Регистрация без пароля невозможна")
    @Description("Попытка создать аккаунт без пароля должна быть отклонена")
    public void rejectRegistrationWithoutPassword() {
        skipCleanup = true;

        UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, null, testName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(request)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Регистрация без имени невозможна")
    @Description("Попытка создать аккаунт без имени пользователя должна быть отклонена")
    public void rejectRegistrationWithoutName() {
        skipCleanup = true;

        UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, testPassword, null);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(request)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }
}
