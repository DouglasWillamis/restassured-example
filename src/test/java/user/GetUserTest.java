package user;

import base.TestBase;
import constant.messages.UserMessages;
import io.restassured.response.Response;
import models.User;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requests.UserEndpoints;

import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

final public class GetUserTest extends TestBase {
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
                faker.random().nextBoolean().toString(),
                faker.random().hex(16));

        UserEndpoints.postUserRequest(SPEC, this.validUser);
    }

    @Test
    public void shouldReturnUserAndStatusCodeOk() {
        Response getUserResponse = UserEndpoints.getUserRequest(SPEC, this.validUser.get_id());

        getUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_OK).
                    body(matchesJsonSchemaInClasspath("schemas/users/getUser-schema.json")).
                    body("nome", is(this.validUser.getNome())).
                    body("email", is(this.validUser.getEmail())).
                    body("password", is(this.validUser.getPassword())).
                    body("administrador", is(this.validUser.getAdministrador())).
                    body("_id", is(this.validUser.get_id()));
    }

    @Test
    public void shouldReturnMessageErroAndStatusCodeBadRequest() {
        Response getUserResponse = UserEndpoints.getUserRequest(SPEC, this.invalidUser.get_id());

        getUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_BAD_REQUEST).
                    body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                    body("message", is(UserMessages.UserNotFound));
    }

    @AfterClass
    public void tearDown () {
        UserEndpoints.deleteUserRequest(SPEC, this.validUser.get_id());
    }
}
