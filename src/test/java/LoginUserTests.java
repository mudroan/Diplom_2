import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.steps.UserSteps;
import static org.hamcrest.CoreMatchers.is;


public class LoginUserTests {

    private UserSteps userSteps  = new UserSteps ();
    private String accessToken;

    String email = RandomStringUtils.randomAlphabetic(10)+"@ya.ru";
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);

    @Test
    @DisplayName("Авторизация существуюшего пользователя")
    public void loginUserTest() {

        userSteps
                .createUser (email, password, name);

        userSteps
                .loginUser (email, password)
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным логином")
    public void loginIncorrectUserTest() {

        userSteps
                .createUser (email, password, name);

        userSteps
                .loginUser ("email@example.com", password)
                .statusCode(401)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным паролем")
    public void loginIncorrectPasswordTest() {

        userSteps
                .createUser (email, password, name);

        userSteps
                .loginUser (email, "qwerty1234")
                .statusCode(401)
                .body("success", is(false));
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
