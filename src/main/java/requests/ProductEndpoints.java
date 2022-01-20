package requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Product;

import static io.restassured.RestAssured.given;

final public class ProductEndpoints extends RequestBase {

    public static String path = "/produtos";

    public static Response getProductsRequest(RequestSpecification spec) {
        return
                given().
                        spec(spec).
                when().
                        get(path);
    }

    public static Response getProductRequest(RequestSpecification spec, String id) {
        return
                given().
                        spec(spec).
                        pathParam("_id", id).
                        header("Content-Type", "application/json").
                when().
                        get(path + "/{_id}");
    }

    public static Response postProductRequest(RequestSpecification spec, Product product) {
        String productJsonRepresentation = gsonWithExposeAttribute.toJson(product);

        Response postProductResponse =
                given().
                        spec(spec).
                        header("Content-Type", "application/json").
                        and().
                        body(productJsonRepresentation).
               when().
                        post(path);

        product.set_id(getStringValueFromResponse(postProductResponse, "_id"));

        return postProductResponse;
    }

    public static Response putProductRequest(RequestSpecification spec, Product product) {
        String productJsonRepresentation = gsonWithExposeAttribute.toJson(product);

        return
                given().
                        spec(spec).
                        pathParam("_id", product.get_id()).
                        header("Content-Type", "application/json").
                        and().
                        body(productJsonRepresentation).
                when().
                        put(path + "/{_id}");
    }

    public static Response deleteProductRequest(RequestSpecification spec, String id) {
        return
                given().
                        spec(spec).
                        pathParam("_id", id).
                        header("Content-Type", "application/json").
                when().
                        delete(path + "/{_id}");
    }
}
