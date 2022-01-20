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

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

final public class PutUserTest extends TestBase {
    private User validUser;
    private User newUser;
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

        this.newUser = new User(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(6, 8),
                faker.random().nextBoolean().toString(),
                faker.random().hex(16));

        UserEndpoints.postUserRequest(SPEC, this.validUser);
    }

    @Test
    public void shouldReturnSucessMessageAndStatusCodeOk() {
        this.validUser.setEmail(faker.internet().safeEmailAddress());
        this.validUser.setPassword(faker.internet().password(6, 8));
        this.validUser.setNome(faker.name().fullName());
        this.validUser.setAdministrador(faker.random().nextBoolean().toString());

        Response putUserResponse = UserEndpoints.putUserRequest(SPEC, this.validUser);

        putUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_OK).
                    body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                    body("message", is(UserMessages.entityUpdateMessage));
    }

    @Test
    public void shouldReturnSucessMessageAndStatusCodeCreated() {
        Response putUserResponse = UserEndpoints.putUserRequest(SPEC, this.newUser);

        putUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_CREATED).
                    body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                    body("message", is(UserMessages.entityCreatedMessage)).
                    body("_id", notNullValue());

        this.newUser.set_id(UserEndpoints.getStringValueFromResponse(putUserResponse, "_id"));
    }

    @Test(dependsOnMethods = {"shouldReturnSucessMessageAndStatusCodeOk"})
    public void shouldReturnEmailInvalidMessageAndStatusCodeBadRequest() {
        this.invalidUser.setEmail(this.validUser.getEmail());

        Response putUserResponse = UserEndpoints.putUserRequest(SPEC, this.invalidUser);

        putUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_BAD_REQUEST).
                    body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                    body("message", is(UserMessages.UserEmailExistErro));
    }

    @AfterClass
    public void tearDown() {
        UserEndpoints.deleteUserRequest(SPEC, this.validUser.get_id());
        UserEndpoints.deleteUserRequest(SPEC, this.newUser.get_id());
    }

}
