import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.steps.UserSteps;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

public class ChangeUserDataTests {

    private UserSteps userSteps = new UserSteps ();
    private String accessToken;

    String email = RandomStringUtils.randomAlphabetic (10) + "@ya.ru";
    String password = RandomStringUtils.randomAlphabetic (10);
    String name = RandomStringUtils.randomAlphabetic (10);

    String changeEmail = RandomStringUtils.randomAlphabetic (10) + "@yandex.com";
    String changeName = RandomStringUtils.randomAlphabetic (10);


    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    public void changeNameDataUserWithAuthorizationTest() {

        userSteps
                .createUser (email, password, name);

        accessToken = userSteps
                .loginUser (email, password)
                .statusCode (200)
                .extract ().jsonPath ().getString ("accessToken");

        userSteps
                .getData (accessToken)
                .statusCode (200)
                .body ("success", is (true));

        userSteps
                .changeDataToken (email, password, changeName, accessToken)
                .statusCode (200)
                .body ("success", is (true))
                .body ("user.name", equalTo (changeName));

        userSteps
                .getData (accessToken)
                .statusCode (200)
                .body ("success", is (true));
    }

    @Test
    @DisplayName("Изменение e-mail пользователя с авторизацией")
    public void changeEmailDataUserWithAuthorizationTest() {

        userSteps
                .createUser (email, password, name);

        accessToken = userSteps
                .loginUser (email, password)
                .statusCode (200)
                .extract ().jsonPath ().getString ("accessToken");

        userSteps
                .getData (accessToken)
                .statusCode (200)
                .body ("success", is (true));

        userSteps
                .changeDataToken (changeEmail, password, name, accessToken)
                .statusCode (200)
                .body ("success", is (true));

        userSteps
                .getData (accessToken)
                .statusCode (200)
                .body ("success", is (true));
    }

    @Test
    @DisplayName("Ошибка при изменение имени и e-mail пользователя без авторизации")
    public void changeDataUserWithoutAuthorizationTest() {

        userSteps
                .createUser (email, password, name)
                .statusCode (200);

        userSteps
                .loginUser (email, password)
                .statusCode (200);

        userSteps
                .changeData (changeEmail, password, changeName)
                .statusCode (401)
                .body ("message", is ("You should be authorised"));
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


