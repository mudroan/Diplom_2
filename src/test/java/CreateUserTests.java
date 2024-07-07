import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import ru.praktikum.steps.UserSteps;
import static org.hamcrest.CoreMatchers.is;
import static ru.praktikum.model.constants.MissingData.*;

public class CreateUserTests {

    private UserSteps userSteps  = new UserSteps ();
    private String accessToken;

    String email = RandomStringUtils.randomAlphabetic(10)+"@ya.ru";
    String password = RandomStringUtils.randomAlphabetic(10);
    String name = RandomStringUtils.randomAlphabetic(10);


    @Test
    @DisplayName("Создание пользователя")
    public void createUserTest() {

        userSteps
                .createUser (email, password, name)
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя, который уже зарегистрирован")
    public void createDuplicateUserTest() {
        userSteps
                .createUser (email, password, name);

        userSteps
                .createUser (email, password, name)
                .statusCode(403)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя c незаполненным email")
    public void createMissingEmailUserTest() {
        userSteps
                .createUser (MISSING_EMAIL, password, name)
                .statusCode(403)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя c незаполненным password")
    public void createMissingPasswordUserTest() {
        userSteps
                .createUser (email, MISSING_PASSWORD, name)
                .statusCode(403)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя c незаполненным name")
    public void createMissingNameUserTest() {
        userSteps
                .createUser (email, password, MISSING_NAME)
                .statusCode(403)
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
