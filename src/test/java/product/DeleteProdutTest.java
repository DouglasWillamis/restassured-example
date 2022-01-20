package product;

import base.TestBase;
import constant.messages.ProductMessages;
import io.restassured.response.Response;
import models.Product;
import models.User;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import requests.LoginEndpoints;
import requests.ProductEndpoints;
import requests.UserEndpoints;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;

final public class DeleteProdutTest extends TestBase {

    private User validUser;
    private User invalidUser;
    private Product validProduct;
    private Product invalidProduct;

    @BeforeClass
    public void generateData() {

        this.validUser = new User(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(6, 8),
                "true");

        UserEndpoints.postUserRequest(SPEC, this.validUser);
        LoginEndpoints.postloginResponse(SPEC, this.validUser);

        this.invalidUser = new User(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(6, 8),
                "false");

        UserEndpoints.postUserRequest(SPEC, this.invalidUser);
        LoginEndpoints.postloginResponse(SPEC, this.invalidUser);

        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        this.validProduct = new Product(
                faker.commerce().productName(),
                faker.number().randomDigitNotZero(),
                faker.lorem().characters(),
                faker.number().randomDigitNotZero()
        );

        this.invalidProduct = new Product(
                faker.random().hex(16),
                faker.commerce().productName(),
                faker.number().randomDigitNotZero(),
                faker.lorem().characters(),
                faker.number().randomDigitNotZero()
        );

        ProductEndpoints.postProductRequest(SPEC, this.validProduct);
        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test
    public void shouldReturnSucessMessageAndStatusCodeOk() {
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        Response deleteProductResponse = ProductEndpoints.deleteProductRequest(SPEC, this.validProduct.get_id());

        deleteProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.entityDeleteMessage));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test
    public void shouldReturnNotDeleteMessageAndStatusCodeOk() {
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        Response deleteProductResponse = ProductEndpoints.deleteProductRequest(SPEC, this.invalidProduct.get_id());

        deleteProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.entityNotFoundDeleteMessage));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test
    public void shouldReturnErroMessageAndStatusCodeUnauthorized() {
        Response deleteProductResponse = ProductEndpoints.deleteProductRequest(SPEC, this.invalidProduct.get_id());

        deleteProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.requestWithoutToken));
    }

    @Test
    public void shouldReturnErroMessageAndStatusCodeForbidden() {
        SPEC.header("Authorization", this.invalidUser.getTokenAuthentication());

        Response deleteProductResponse = ProductEndpoints.deleteProductRequest(SPEC, this.invalidProduct.get_id());

        deleteProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_FORBIDDEN).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.invalidUser));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @AfterClass
    public void tearDown () {
        UserEndpoints.deleteUserRequest(SPEC, this.validUser.get_id());
        UserEndpoints.deleteUserRequest(SPEC, this.invalidUser.get_id());
    }
}
