package product;

import base.TestBase;
import io.restassured.response.Response;
import models.Product;
import models.User;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import requests.LoginEndpoints;
import requests.ProductEndpoints;
import requests.UserEndpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

final public class GetProductsTest extends TestBase {

    private User validUser;
    private final List<Product> products = new ArrayList<>();
    private int totalProducts = 2;

    @BeforeClass
    public void generateData() {

        this.validUser = new User(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(6, 8),
                "true");

        UserEndpoints.postUserRequest(SPEC, this.validUser);
        LoginEndpoints.postloginResponse(SPEC, this.validUser);

        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        for (int i = 0; i < 5; i++) {
            Product validProduct = new Product(
                    faker.commerce().productName(),
                    faker.number().randomDigitNotZero(),
                    faker.lorem().characters(),
                    faker.number().randomDigitNotZero()
            );

            ProductEndpoints.postProductRequest(SPEC, validProduct);

            this.products.add(validProduct);
            this.totalProducts += 1;
        }

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");
    }

    @DataProvider(name = "userQueryData")
    public Object[][] createQueryData() {
        Random random = new Random();

        return new Object[][] {
                {"nome", products.get(this.products.size() - 1).getNome()},
                {"descricao", products.get(random.nextInt(this.products.size() - 1)).getDescricao()},
        };
    }

    @Test
    public void shouldReturnAllUsersAndStatusCodeOk() {
        Response getUsersRequest = ProductEndpoints.getProductsRequest(SPEC);

        getUsersRequest.
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body(matchesJsonSchemaInClasspath("schemas/products/getProducts-schema.json")).
                body("quantidade", is(this.totalProducts)).
                body("quantidade", instanceOf(Integer.class)).
                body("produtos", notNullValue()).
                body("produtos", instanceOf(List.class));
    }

    @Test(dataProvider = "userQueryData")
    public void shouldReturnUserQueryAndStatusCodeOk(String query, String queryValue) {
        SPEC.queryParam(query, queryValue);

        Response getUsersRequest = ProductEndpoints.getProductsRequest(SPEC);

        getUsersRequest.
                then().
                assertThat().
                statusCode(HttpStatus.SC_OK).
                body(matchesJsonSchemaInClasspath("schemas/products/getProducts-schema.json")).
                body("quantidade", is(1)).
                body("quantidade", instanceOf(Integer.class)).
                body("produtos", notNullValue()).
                body("produtos", instanceOf(List.class));

        ProductEndpoints.removeQueryParameterFromRequestSpecification(SPEC, query);
    }

    @AfterClass
    public void tearDown () {
        SPEC.header("Authorization", this.validUser.getTokenAuthentication());

        for (Product product: products) {
            ProductEndpoints.deleteProductRequest(SPEC, product.get_id());
        }

        ProductEndpoints.removeHeaderFromRequestSpecification(SPEC,"Authorization");

        UserEndpoints.deleteUserRequest(SPEC, validUser.get_id());
    }
}
