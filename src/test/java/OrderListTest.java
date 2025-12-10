import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.UserCreateAndEditRequest;
import org.example.steps.OrderSteps;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

public class OrderListTest {

    public String testEmail = "history_" + System.nanoTime() + "@burger.track";
    public String testPassword = "HistoryPass@456";
    public String testName = "History Tracker";

    private boolean skipAccountDeletion = false;

    @After
    public void removeAccount() {
        if (!skipAccountDeletion) {
            UserSteps userSteps = new UserSteps();
            UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, testPassword, testName);
            userSteps.userDeleteAfterLogin(request);
        }
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может получить историю заказов")
    @Description("Запрос списка заказов без авторизации должен вернуть ошибку 401")
    public void rejectAccessToOrderHistoryWithoutAuth() {
        skipAccountDeletion = true;

        OrderSteps orderSteps = new OrderSteps();

        orderSteps.orderList()
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Авторизованный пользователь может получить свою историю заказов")
    @Description("Авторизованный пользователь получает список своих заказов")
    public void retrieveOrderHistoryWhenAuthorized() {
        UserCreateAndEditRequest userRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        UserSteps userSteps = new UserSteps();
        OrderSteps orderSteps = new OrderSteps();

        userSteps.userCreate(userRequest);
        orderSteps.orderListAfterLogin(userRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("orders", instanceOf(List.class))
                .and()
                .statusCode(200);
    }
}
