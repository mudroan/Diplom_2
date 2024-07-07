import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import ru.praktikum.steps.OrderSteps;
import ru.praktikum.steps.UserSteps;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;

public class CreateOrderTests {

    private String accessToken;
    private ArrayList<String> ingredients;
    private ArrayList<String> incorrectIdIngredients = new ArrayList<>();

    private UserSteps userSteps  = new UserSteps ();
    private OrderSteps orderSteps  = new OrderSteps ();

    String email = RandomStringUtils.randomAlphabetic(10)+"@ya.ru";
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);


    @Test //можно удалить
    @DisplayName("Получение списка ингредиентов")
    public void getListIngredientsTest() {

        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());

        orderSteps
                .getIngredients ()
                .statusCode(200);
    }

    @Test //ошибка, тест возвращзает успешный ответ, но по ОР не должен создаваться
    @DisplayName("Создание заказа без авторизации")
    public void  createOrderWithOutAuthorizationTest() {
        //удалить логирование
        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());

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
        //удалить логирование
        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());

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
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    public void  createOrderWithAuthorizationWithOutIngredientsTest() {
        //удалить логирование
        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());

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
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void  createOrderWithOutAuthorizationWithOutIngredientsTest() {
        //удалить логирование
        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());

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
    @DisplayName("Создание заказа с невалидным хеш ингредиентов")
    public void  createOrderIncorrectIdTest() {
        //удалить логирование
        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());

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

        incorrectIdIngredients.add("Text, https://code.s3.yandex.net/react/code/salad-mobile.png");

        orderSteps
                .createOrder(incorrectIdIngredients)
                .statusCode (500);
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    public void  getOrderUserListTest() {
        //удалить логирование
        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());

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
                .getOrders (accessToken)
                .statusCode(200)
                .body("success", is (true));
    }
}
