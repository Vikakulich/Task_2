import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.UserCreateAndEditRequest;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserEditTest {

    public String originalEmail = "profile_" + System.nanoTime() + "@burgers.app";
    public String originalPassword = "InitialPass@99";
    public String originalName = "Profile Owner";

    public String updatedEmail = "updated_" + System.nanoTime() + "@burgers.app";
    public String updatedPassword = "UpdatedPass@88";
    public String updatedName = "Updated Owner";

    @After
    public void cleanupProfile() {
        UserSteps userSteps = new UserSteps();
        UserCreateAndEditRequest request = new UserCreateAndEditRequest(originalEmail, originalPassword, originalName);
        userSteps.userDeleteAfterLogin(request);
    }

    @Test
    @DisplayName("Изменение email при авторизации")
    @Description("Авторизованный пользователь может изменить свой email")
    public void updateEmailWhenAuthorized() {
        UserCreateAndEditRequest createRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, originalName);
        UserCreateAndEditRequest updateRequest = new UserCreateAndEditRequest(updatedEmail, originalPassword, originalName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(createRequest);
        userSteps.userEditAfterLogin(createRequest, updateRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("user.email", equalTo(updatedEmail))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Невозможно изменить email без авторизации")
    @Description("Неавторизованный пользователь не может изменить email")
    public void preventEmailChangeWithoutAuth() {
        UserCreateAndEditRequest createRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, originalName);
        UserCreateAndEditRequest updateRequest = new UserCreateAndEditRequest(updatedEmail, originalPassword, originalName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(createRequest);
        userSteps.userEdit(updateRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Изменение пароля при авторизации")
    @Description("Авторизованный пользователь может изменить свой пароль")
    public void updatePasswordWhenAuthorized() {
        UserCreateAndEditRequest createRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, originalName);
        UserCreateAndEditRequest updateRequest = new UserCreateAndEditRequest(originalEmail, updatedPassword, originalName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(createRequest);
        userSteps.userEditAfterLogin(createRequest, updateRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        // Проверяем что новый пароль работает
        UserCreateAndEditRequest loginWithNewPass = new UserCreateAndEditRequest(originalEmail, updatedPassword, originalName);
        userSteps.userLogin(loginWithNewPass)
                .assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Невозможно изменить пароль без авторизации")
    @Description("Неавторизованный пользователь не может изменить пароль")
    public void preventPasswordChangeWithoutAuth() {
        UserCreateAndEditRequest createRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, originalName);
        UserCreateAndEditRequest updateRequest = new UserCreateAndEditRequest(originalEmail, updatedPassword, originalName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(createRequest);
        userSteps.userEdit(updateRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Изменение имени при авторизации")
    @Description("Авторизованный пользователь может изменить свое имя")
    public void updateNameWhenAuthorized() {
        UserCreateAndEditRequest createRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, originalName);
        UserCreateAndEditRequest updateRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, updatedName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(createRequest);
        userSteps.userEditAfterLogin(createRequest, updateRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("user.name", equalTo(updatedName))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Невозможно изменить имя без авторизации")
    @Description("Неавторизованный пользователь не может изменить имя")
    public void preventNameChangeWithoutAuth() {
        UserCreateAndEditRequest createRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, originalName);
        UserCreateAndEditRequest updateRequest = new UserCreateAndEditRequest(originalEmail, originalPassword, updatedName);
        UserSteps userSteps = new UserSteps();

        userSteps.userCreate(createRequest);
        userSteps.userEdit(updateRequest)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
}
