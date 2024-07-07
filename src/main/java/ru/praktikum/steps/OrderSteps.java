package ru.praktikum.steps;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.praktikum.dto.CreateOrderRequest;
import java.util.ArrayList;
import static io.restassured.RestAssured.given;
import static ru.praktikum.config.RestConfig.HOST;
import static ru.praktikum.model.constants.ApiURI.*;


public class OrderSteps {

    @Step("Получение списка ингредиентов")
    public ValidatableResponse getIngredients () {

        return given ()
                .contentType (ContentType.JSON)
                .baseUri (HOST)
                .when ()
                .get (INGREDIENTS_ENDPOINT)
                .then ();
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder (ArrayList<String> ingredients) {

        CreateOrderRequest createOrderRequest = new CreateOrderRequest ();
        createOrderRequest.setIngredients (ingredients);

        return given ()
                .contentType (ContentType.JSON)
                .baseUri (HOST)
                .body (createOrderRequest)
                .when ()
                .post (CREATE_ORDER)
                .then ();
    }

    @Step("Получение списка заказов авторизованного пользователя")
    public ValidatableResponse getOrdersToken (String accessToken) {

        return given ()
                .contentType (ContentType.JSON)
                .baseUri (HOST)
                .header("Authorization", accessToken)
                .get (GET_ORDERS_USER)
                .then ();
    }

    @Step("Получение списка заказов неавторизованного пользователя")
    public ValidatableResponse getOrdersUnauthorizedUser  () {

        return given ()
                .contentType (ContentType.JSON)
                .baseUri (HOST)
                .when ()
                .get (GET_ORDERS_USER)
                .then ();
    }
}
