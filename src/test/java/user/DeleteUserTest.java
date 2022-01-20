package user;

import base.TestBase;
import constant.messages.UserMessages;
import io.restassured.response.Response;
import models.User;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requests.UserEndpoints;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;

final public class DeleteUserTest extends TestBase {

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
    public void shouldReturnSucessMessageAndStatusCodeOk() {
        Response deleteUserResponse = UserEndpoints.deleteUserRequest(SPEC, this.validUser.get_id());

        deleteUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_OK).
                    body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                    body("message", is(UserMessages.entityDeleteMessage));
    }

    @Test
    public void shouldReturnNotDeleteMessageAndStatusCodeOk() {
        Response deleteUserResponse = UserEndpoints.deleteUserRequest(SPEC, this.invalidUser.get_id());

        deleteUserResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(UserMessages.entityNotFoundDeleteMessage));
    }
}
