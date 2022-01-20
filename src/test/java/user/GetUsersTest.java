package user;

import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import base.TestBase;
import io.restassured.response.Response;
import models.User;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import requests.UserEndpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final public class GetUsersTest extends TestBase {

    private final List<User> users = new ArrayList<>();

    @BeforeClass
    public void generateData() {
        for (int i = 0; i < 5; i++) {
            User validUser = new User(
                    faker.name().fullName(),
                    faker.internet().safeEmailAddress(),
                    faker.internet().password(6, 8),
                    faker.random().nextBoolean().toString());

            UserEndpoints.postUserRequest(SPEC, validUser);
            this.users.add(validUser);
        }
    }

    @DataProvider(name = "userQueryData")
    public Object[][] createQueryData() {
        Random random = new Random();
        return new Object[][] {
                {"nome", users.get(random.nextInt(this.users.size() - 1)).getNome()},
                {"email", users.get(random.nextInt(this.users.size() - 1)).getEmail()},
        };
    }

    @Test
    public void shouldReturnAllUsersAndStatusCodeOk() {
        Response getUsersResponse = UserEndpoints.getUsersRequest(SPEC);
        List<User> jsonListUser = getUsersResponse.then().extract().path("usuarios");

        getUsersResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_OK).
                    body(matchesJsonSchemaInClasspath("schemas/users/getUsers-schema.json")).
                    body("quantidade", is(this.users.size())).
                    body("usuarios", notNullValue());
    }

    @Test(dataProvider = "userQueryData")
    public void shouldReturnUserQueryAndStatusCodeOk(String query, String queryValue) {
        SPEC.queryParam(query, queryValue);

        Response getUsersResponse = UserEndpoints.getUsersRequest(SPEC);

        getUsersResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body(matchesJsonSchemaInClasspath("schemas/users/getUsers-schema.json")).
                body("quantidade", is(1)).
                body("usuarios", notNullValue());

        UserEndpoints.removeQueryParameterFromRequestSpecification(SPEC, query);
    }

    @AfterClass
    public void tearDown () {
        for (User user: users) {
            UserEndpoints.deleteUserRequest(SPEC, user.get_id());
        }
    }
}
