package requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;

public abstract class RequestBase {

    public static Gson gsonWithExposeAttribute = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static String getStringValueFromResponse(Response response, String key) {
        return response
                .then()
                    .extract()
                    .path(key);
    }

    public static void removeQueryParameterFromRequestSpecification(RequestSpecification spec, String queryParameter){
        FilterableRequestSpecification requestSpecificationspecification = (FilterableRequestSpecification) spec;
        requestSpecificationspecification.removeQueryParam(queryParameter);
    }

    public static void removeHeaderFromRequestSpecification(RequestSpecification spec, String header) {
        FilterableRequestSpecification requestSpecificationspecification = (FilterableRequestSpecification) spec;
        requestSpecificationspecification.removeHeader(header);
    }
}
