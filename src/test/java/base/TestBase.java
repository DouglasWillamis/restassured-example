package base;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;

public abstract class TestBase {

     public final RequestSpecification SPEC = new RequestSpecBuilder()
                .addHeader("accept", "application/json")
                .setBaseUri("http://localhost:3000").build();

     public final Faker faker = new Faker(new Locale("pt-BR"));
}
