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

final public class GetProductTest extends TestBase {
    private User validUser;
    private Product validProduct;
    private Product invalidProduct;

    @BeforeClass
    public void generateData() {

        this.validUser = new User(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(6, 8),
                faker.random().nextBoolean().toString());

        UserEndpoints.postUserRequest(SPEC, this.validUser);
        LoginEndpoints.postloginResponse(SPEC, this.validUser);

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

        ProductEndpoints.postProductRequest(SPEC, validProduct);

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @Test
    public void shouldReturnProductAndStatusCodeOk() {
        Response getProductResponse = ProductEndpoints.getProductRequest(SPEC, this.validProduct.get_id());

        getProductResponse.
                then().
                    assertThat().
                    statusCode(HttpStatus.SC_OK).
                    body(matchesJsonSchemaInClasspath("schemas/products/getProduct-schema.json")).
                    body("nome", is(this.validProduct.getNome())).
                    body("preco", is(this.validProduct.getPreco())).
                    body("descricao", is(this.validProduct.getDescricao())).
                    body("quantidade", is(this.validProduct.getQuantidade())).
                    body("_id", is(this.validProduct.get_id()));
    }

    @Test
    public void shouldReturnMessageErroAndStatusCodeBadRequest() {
        Response getProductResponse = ProductEndpoints.getProductRequest(SPEC, this.invalidProduct.get_id());

        getProductResponse.
                then().
                assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body(matchesJsonSchemaInClasspath("schemas/genericMessageResponse-schema.json")).
                body("message", is(ProductMessages.productNotFound));
    }

    @AfterClass
    public void tearDown () {
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        ProductEndpoints.deleteProductRequest(SPEC, this.validProduct.get_id());

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");

        UserEndpoints.deleteUserRequest(SPEC, validUser.get_id());
    }
}
