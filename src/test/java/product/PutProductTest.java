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

public class PutProductTest extends TestBase {
    private User validUser;
    private User invalidUser;
    private Product validProduct;
    private Product newProduct;
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

        this.newProduct = new Product(
                faker.random().hex(16),
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
        this.validProduct.setNome(faker.commerce().productName());
        this.validProduct.setPreco(faker.number().randomDigitNotZero());
        this.validProduct.setDescricao(faker.lorem().characters());
        this.validProduct.setQuantidade(faker.number().randomDigitNotZero());

        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        Response putProductResponse = ProductEndpoints.putProductRequest(SPEC, this.validProduct);

        putProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.entityUpdateMessage));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test
    public void shouldReturnSucessMessageAndStatusCodeCreated(){
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        Response putProductResponse = ProductEndpoints.putProductRequest(SPEC, this.newProduct);

        putProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_CREATED).
                body(matchesJsonSchemaInClasspath("schemas/genericCreatedResponse-schema.json")).
                body("message", is(ProductMessages.entityCreatedMessage)).
                body("_id", instanceOf(String.class));

        this.newProduct.set_id(ProductEndpoints.getStringValueFromResponse(putProductResponse, "_id"));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test(dependsOnMethods = {"shouldReturnSucessMessageAndStatusCodeOk"})
    public void shouldReturnNomeInvalidMessageAndStatusCodeBadRequest() {
        this.invalidProduct.setNome(this.validProduct.getNome());

        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        Response putProductResponse = ProductEndpoints.putProductRequest(SPEC, this.invalidProduct);

        putProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.productExistErro));

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test
    public void shouldReturnMissingTokenErroMessageAndStatusCodeUnauthorized() {
        Response putProductResponse = ProductEndpoints.putProductRequest(SPEC, this.invalidProduct);

        putProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.requestWithoutToken));
    }

    @Test
    public void shouldReturnInvalidUserErroMessageAndStatusCodeForbidden() {
        SPEC.header("Authorization", this.invalidUser.getTokenAuthentication());

        Response putProductResponse = ProductEndpoints.putProductRequest(SPEC, this.invalidProduct);

        putProductResponse.
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
        ProductEndpoints.deleteProductRequest(SPEC, this.newProduct.get_id());

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");

        UserEndpoints.deleteUserRequest(SPEC, this.validUser.get_id());
        UserEndpoints.deleteUserRequest(SPEC, this.invalidUser.get_id());
    }
}
