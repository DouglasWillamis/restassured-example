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
import static org.hamcrest.Matchers.*;

final public class PostProductTest extends TestBase {

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

        this.validProduct = new Product(
                faker.commerce().productName(),
                faker.number().randomDigitNotZero(),
                faker.lorem().characters(),
                faker.number().randomDigitNotZero()
        );

        this.invalidProduct = new Product(
                this.validProduct.getNome(),
                faker.number().randomDigitNotZero(),
                faker.lorem().characters(),
                faker.number().randomDigitNotZero()
        );
    }

    @Test
    public void shouldReturnSuccessMessageAndProductoIdAndStatusCodeCreated() {
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        Response postProductResponse = ProductEndpoints.postProductRequest(SPEC, this.validProduct);

        postProductResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_CREATED).
                    body(matchesJsonSchemaInClasspath("schemas/genericCreatedResponse-schema.json")).
                    body("message", is(ProductMessages.entityCreatedMessage)).
                    body("_id", instanceOf(String.class));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test(dependsOnMethods = "shouldReturnSuccessMessageAndProductoIdAndStatusCodeCreated")
    public void shouldReturnErroMessageAndStatusCodeBadRequest() {
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        Response postProductResponse = ProductEndpoints.postProductRequest(SPEC, this.invalidProduct);

        postProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.productExistErro));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test(dependsOnMethods = "shouldReturnSuccessMessageAndProductoIdAndStatusCodeCreated")
    public void shouldReturnErroMessageAndStatusCodeUnauthorized() {
        Response postProductResponse = ProductEndpoints.postProductRequest(SPEC, this.invalidProduct);

        postProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.requestWithoutToken));
    }

    @Test(dependsOnMethods = "shouldReturnSuccessMessageAndProductoIdAndStatusCodeCreated")
    public void shouldReturnErroMessageAndStatusCodeForbidden() {
        SPEC.header("Authorization", this.invalidUser.getTokenAuthentication());

        Response postProductResponse = ProductEndpoints.postProductRequest(SPEC, this.invalidProduct);

        postProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_FORBIDDEN).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.invalidUser));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @AfterClass
    public void tearDown () {
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        ProductEndpoints.deleteProductRequest(SPEC, this.validProduct.get_id());

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");

        UserEndpoints.deleteUserRequest(SPEC, validUser.get_id());
        UserEndpoints.deleteUserRequest(SPEC, invalidUser.get_id());
    }
}
