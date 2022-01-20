package login;

import base.TestBase;
import constant.messages.LoginMessages;
import io.restassured.response.Response;
import models.User;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requests.LoginEndpoints;
import requests.UserEndpoints;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

final public class PostLoginTest extends TestBase {

    private User validUser;
    private User invalidUser;

    @BeforeClass
    public void generateData() {
        this.validUser = new User(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(6, 8),
                faker.random().nextBoolean().toString());

        this.invalidUser = new User(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(6, 8),
                faker.random().nextBoolean().toString());

        UserEndpoints.postUserRequest(SPEC, this.validUser);
    }

    @Test
    public void shouldReturnSuccessMessageAndStatusCodeOk() {
        Response loginUserResponse = LoginEndpoints.postloginResponse(SPEC, this.validUser);

        loginUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_OK).
                    body(matchesJsonSchemaInClasspath("schemas/login/postLoginSuccess-schema.json")).
                    body("message", is(LoginMessages.successLogin)).
                    body("authorization", notNullValue()).
                    body("authorization", instanceOf(String.class));
    }

    @Test
    public void shouldReturnInvalidEmailErroMessageAndStatusCodeBadRequest() {
        this.invalidUser.setEmail(faker.random().hex(10));

        Response loginUserResponse = LoginEndpoints.postloginResponse(SPEC, this.invalidUser);

        loginUserResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body(matchesJsonSchemaInClasspath("schemas/login/postLoginInvalidEmail-schema.json")).
                body("email", is(LoginMessages.invalidEmail));

        this.invalidUser.setEmail(faker.internet().safeEmailAddress());
    }

    @Test
    public void shouldReturnBlankEmailErroMessageAndStatusCodeBadRequest() {
        this.invalidUser.setEmail("");

        Response loginUserResponse = LoginEndpoints.postloginResponse(SPEC, this.invalidUser);

        loginUserResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body(matchesJsonSchemaInClasspath("schemas/login/postLoginInvalidEmail-schema.json")).
                body("email", is(LoginMessages.blankEmail));

        this.invalidUser.setEmail(faker.internet().safeEmailAddress());
    }

    @Test
    public void shouldReturnInvalidPasswordErroMessageAndStatusCodeBadRequest() {
        this.invalidUser.setPassword("");

        Response loginUserResponse = LoginEndpoints.postloginResponse(SPEC, this.invalidUser);

        loginUserResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body(matchesJsonSchemaInClasspath("schemas/login/PostLoginInvalidPassword-schema.json")).
                body("password", is(LoginMessages.invalidPassword));

        this.invalidUser.setPassword(faker.internet().password(6, 8));
    }

    @Test
    public void shouldReturnInvalidUserErroMessageAndStatusCodeUnauthorized() {
        Response loginUserResponse = LoginEndpoints.postloginResponse(SPEC, this.invalidUser);

        loginUserResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(LoginMessages.invalidUser));
    }


    @AfterClass
    public void tearDown() {
        UserEndpoints.deleteUserRequest(SPEC, validUser.get_id());
    }
}
