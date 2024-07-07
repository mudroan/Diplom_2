import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.steps.OrderSteps;
import ru.praktikum.steps.UserSteps;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;

public class OrderTests {

    private String accessToken;
    private ArrayList<String> ingredients;
    private ArrayList<String> incorrectIdIngredients = new ArrayList<>();

    private UserSteps userSteps  = new UserSteps ();
    private OrderSteps orderSteps  = new OrderSteps ();

    String email = RandomStringUtils.randomAlphabetic(10)+"@ya.ru";
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);


    @Test
    @DisplayName("Получение списка ингредиентов")
    public void getListIngredientsTest() {

        orderSteps
                .getIngredients ()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Issue ("Ошибка. ФР: Тест возвращает успешный ответ, ОР: заказ не должен создаваться без авторизации")
    public void  createOrderWithOutAuthorizationTest() {

        userSteps
                .createUser (email, password, name);

        userSteps
                .loginUser (email, password)
                .statusCode(200);

        ingredients = orderSteps
                .getIngredients ()
                .statusCode(200)
                .extract().path ("data[0,1]._id");

        orderSteps
                .createOrder(ingredients)
                .statusCode (200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void  createOrderWithAuthorizationTest() {

        userSteps
                .createUser (email, password, name);

        accessToken = userSteps
                .loginUser (email, password)
                .statusCode(200)
                .extract().jsonPath().getString("accessToken");

        userSteps
                .getData (accessToken)
                .statusCode(200)
                .body("success", is (true));

        ingredients = orderSteps
                .getIngredients ()
                .statusCode(200)
                .extract().path ("data[0,1]._id");

        orderSteps
                .createOrder(ingredients)
                .statusCode (200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Ошибка при создании заказа с авторизацией без ингредиентов")
    public void  createOrderWithAuthorizationWithOutIngredientsTest() {

        userSteps
                .createUser (email, password, name);

        accessToken = userSteps
                .loginUser (email, password)
                .statusCode(200)
                .extract().jsonPath().getString("accessToken");

        userSteps
                .getData (accessToken)
                .statusCode(200)
                .body("success", is (true));

        orderSteps
                .createOrder(ingredients)
                .statusCode (400)
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка при создании заказа без авторизации и без ингредиентов")
    public void  createOrderWithOutAuthorizationWithOutIngredientsTest() {

        userSteps
                .createUser (email, password, name);

        userSteps
                .loginUser (email, password)
                .statusCode(200);

        orderSteps
                .createOrder(ingredients)
                .statusCode (400)
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка при создании заказа с невалидным хеш ингредиентов")
    public void  createOrderIncorrectIdTest() {

        userSteps
                .createUser (email, password, name);

        accessToken = userSteps
                .loginUser (email, password)
                .statusCode(200)
                .extract().jsonPath().getString("accessToken");

        userSteps
                .getData (accessToken)
                .statusCode(200)
                .body("success", is (true));

        incorrectIdIngredients
                .add("Text, https://code.s3.yandex.net/react/code/salad-mobile.png");

        orderSteps
                .createOrder(incorrectIdIngredients)
                .statusCode (500);
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    public void  getOrderUserListTest() {

        userSteps
                .createUser (email, password, name);

        accessToken = userSteps
                .loginUser (email, password)
                .statusCode(200)
                .extract().jsonPath().getString("accessToken");

        userSteps
                .getData (accessToken)
                .statusCode(200)
                .body("success", is (true));

        ingredients = orderSteps
                .getIngredients ()
                .statusCode(200)
                .extract().path ("data[0,1]._id");

        orderSteps
                .createOrder(ingredients)
                .statusCode (200)
                .body("success", is(true));

        orderSteps
                .getOrdersToken (accessToken)
                .statusCode(200)
                .body("success", is (true));
    }

    @Test
    @DisplayName("Ошибка при получении списка заказов неавторизованного пользователя")
    public void  getOrdersUnauthorizedUserTest() {

        userSteps
                .createUser (email, password, name);

        userSteps
                .loginUser (email, password)
                .statusCode(200);

        ingredients = orderSteps
                .getIngredients ()
                .statusCode(200)
                .extract().path ("data[0,1]._id");

        orderSteps
                .createOrder(ingredients)
                .statusCode (200)
                .body("success", is(true));

        orderSteps
                .getOrdersUnauthorizedUser ()
                .statusCode (401)
                .body("message", is("You should be authorised"));
    }

    @After
    public void tearDown() {

        if (accessToken != null) {
            userSteps
                    .delete (accessToken)
                    .statusCode (202)
                    .body ("message", is ("User successfully removed"));
        }
    }
}
