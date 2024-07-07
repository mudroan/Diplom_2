import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import ru.praktikum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {

    private UserSteps userSteps  = new UserSteps ();

    String email = RandomStringUtils.randomAlphabetic(10)+"@ya.ru";
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);

    @Test
    @DisplayName("Авторизация пользователя")
    public void loginUserTest() {
        userSteps
                .createUser (email, password, name);

        userSteps
                .loginUser (email, password)
                .statusCode(200)
                .body("success", is(true));
    }

}
