package requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.User;

import static io.restassured.RestAssured.*;

final public class UserEndpoints extends RequestBase {

    public static String path = "/usuarios";

    public static Response getUsersRequest(RequestSpecification spec) {
        return
            given().
                   spec(spec).
            when().
                  get(path);
    }

    public static Response getUserRequest(RequestSpecification spec, String id) {
        return
                given().
                        spec(spec).
                        pathParam("_id", id).
                        header("Content-Type", "application/json").
                when().
                        get(path + "/{_id}");
    }

    public static Response postUserRequest(RequestSpecification spec, User user) {
        String userJsonRepresentation = gsonWithExposeAttribute.toJson(user);

        Response postUserResponse =
                given().
                        spec(spec).
                        header("Content-Type", "application/json").
                        and().
                        body(userJsonRepresentation).
                when().
                        post(path);

        user.set_id(getStringValueFromResponse(postUserResponse, "_id"));

        return postUserResponse;
    }

    public static Response putUserRequest(RequestSpecification spec, User user) {
        String userJsonRepresentation = gsonWithExposeAttribute.toJson(user);

        return
            given().
                    spec(spec).
                    pathParam("_id", user.get_id()).
                    header("Content-Type", "application/json").
                    and().
                    body(userJsonRepresentation).
            when().
                    put(path + "/{_id}");
    }

    public static Response deleteUserRequest(RequestSpecification spec, String id) {
        return
            given().
                   spec(spec).
                   pathParam("_id", id).
                   header("Content-Type", "application/json").
            when().
                   delete(path + "/{_id}");
    }
}
