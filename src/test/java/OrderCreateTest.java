import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.OrderCreateRequest;
import org.example.pojo.UserCreateAndEditRequest;
import org.example.steps.OrderSteps;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.isA;

public class OrderCreateTest {

    public String testEmail = "orders_" + System.nanoTime() + "@burger.shop";
    public String testPassword = "OrderPass#2025";
    public String testName = "Order Maker";
    public List<String> orderItems = new ArrayList<>();

    private boolean skipAccountRemoval = false;

    @After
    public void removeTestAccount() {
        UserSteps userSteps = new UserSteps();
        UserCreateAndEditRequest request = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        userSteps.userDeleteAfterLogin(request);
    }

    @After
    public void clearItems() {
        if (!skipAccountRemoval) {
            orderItems.clear();
        }
    }

    @Test
    @DisplayName("Создать заказ с авторизацией и выбранными ингредиентами")
    @Description("Авторизованный пользователь может создать заказ с ингредиентами")
    public void placeOrderWhenAuthorizedWithItems() {
        UserCreateAndEditRequest userRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        orderItems.add("61c0c5a71d1f82001bdaaa6d");
        OrderCreateRequest orderRequest = new OrderCreateRequest(orderItems);
        UserSteps userSteps = new UserSteps();
        OrderSteps orderSteps = new OrderSteps();

        userSteps.userCreate(userRequest);
        orderSteps.orderCreateAfterLogin(userRequest, orderRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("order.owner.email", equalTo(testEmail))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создать заказ без авторизации с ингредиентами")
    @Description("Неавторизованный пользователь может создать анонимный заказ")
    public void placeOrderWithoutAuthWithItems() {
        UserCreateAndEditRequest userRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        orderItems.add("61c0c5a71d1f82001bdaaa6d");
        OrderCreateRequest orderRequest = new OrderCreateRequest(orderItems);
        UserSteps userSteps = new UserSteps();
        OrderSteps orderSteps = new OrderSteps();

        userSteps.userCreate(userRequest);
        orderSteps.orderCreate(orderRequest)
                .assertThat().body("success", equalTo(true))
                .and()
                .assertThat().body("order.number", isA(Integer.class))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Заказ без ингредиентов не может быть создан")
    @Description("Попытка создать заказ без ингредиентов должна быть отклонена")
    public void rejectOrderWithoutItems() {
        skipAccountRemoval = true;

        UserCreateAndEditRequest userRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        OrderCreateRequest emptyOrder = new OrderCreateRequest(orderItems);
        UserSteps userSteps = new UserSteps();
        OrderSteps orderSteps = new OrderSteps();

        userSteps.userCreate(userRequest);
        orderSteps.orderCreateAfterLogin(userRequest, emptyOrder)
                .assertThat().body("success", equalTo(false))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Заказ с невалидным ингредиентом отклоняется")
    @Description("Попытка создать заказ с неправильным ID ингредиента должна вернуть ошибку")
    public void rejectOrderWithInvalidIngredient() {
        UserCreateAndEditRequest userRequest = new UserCreateAndEditRequest(testEmail, testPassword, testName);
        orderItems.add("61c0c5a71d1f82001bdaaa6d");
        orderItems.add("invalid_hash_xyz123");
        OrderCreateRequest badOrder = new OrderCreateRequest(orderItems);
        UserSteps userSteps = new UserSteps();
        OrderSteps orderSteps = new OrderSteps();

        userSteps.userCreate(userRequest);
        orderSteps.orderCreateAfterLogin(userRequest, badOrder)
                .assertThat().statusCode(500);
    }
}
