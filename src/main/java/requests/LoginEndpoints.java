package requests;

import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.User;

import static io.restassured.RestAssured.given;

final public class LoginEndpoints extends RequestBase {

    public static Response postloginResponse(RequestSpecification spec, User user) {

        Response postLoginResponse =
            given().
                    spec(spec).
                    header("Content-Type", "application/json").
                    and().
                    body(user.getCredencial()).
            when().
                    post("/login");

        user.setTokenAuthentication(getStringValueFromResponse(postLoginResponse, "authorization"));

        return postLoginResponse;
    }
}
