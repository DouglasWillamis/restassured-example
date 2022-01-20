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

final public class PostUserTest extends TestBase {

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
                this.validUser.getEmail(),
                faker.internet().password(6, 8),
                faker.random().nextBoolean().toString());
    }

    @Test
    public void shouldReturnSuccessMessageAndStatusCodeCreated() {
        Response postUserResponse = UserEndpoints.postUserRequest(SPEC, this.validUser);

        postUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_CREATED).
                    body(matchesJsonSchemaInClasspath("schemas/genericCreatedResponse-schema.json")).
                    body("message", is(UserMessages.entityCreatedMessage)).
                    body("_id", notNullValue());
    }

    @Test(dependsOnMethods = "shouldReturnSuccessMessageAndStatusCodeCreated")
    public void shouldReturnInvalidEmailErroMessageAndStatusCodeBadRequest() {
        Response postUserResponse = UserEndpoints.postUserRequest(SPEC, this.invalidUser);

        postUserResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_BAD_REQUEST).
                    body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                    body("message", is(UserMessages.UserEmailExistErro));
    }

    @AfterClass
    public void tearDown() {
        UserEndpoints.deleteUserRequest(SPEC, this.validUser.get_id());
    }
}
