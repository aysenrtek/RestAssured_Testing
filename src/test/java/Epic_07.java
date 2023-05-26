import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Epic_07 {

    Faker faker = new Faker();
    RequestSpecification recSpec;

    String name;
    String shortName;
    String school="6390f3207a3bcb6a7ac977f9";
    String locationID;
    String type="CLASS";



    @BeforeClass

    public void SetUp() {
        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");
        Cookies cookies =

                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                     //   .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }



    @Test
    public void createLocations() {


        Map<String, String> location = new HashMap<>();

        name = faker.name().username();
        shortName= faker.name().firstName();
        location.put("name",name);
        location.put("school",school);
        location.put("shortName",shortName);
        location.put("type",type);


        locationID =
                given()
                        .spec(recSpec)
                        .body(location)

                        .when()
                        .post("/school-service/api/location")

                        .then()
                     //   .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("locationId:" + locationID);

    }

    @Test(dependsOnMethods = "createLocations")
    public void updateLocations() {

        Map<String, String> location = new HashMap<>();
        location.put("id", locationID);
        location.put("name",name);
        location.put("shortName",shortName);
        location.put("school", school);
        location.put("type",type);


        given()
                .spec(recSpec)
                .body(location)

             //   .log().body()

                .when()
                .put("/school-service/api/location")
                .then()
            //    .log().body()
                .statusCode(200)
                .body("name", equalTo(name));

    }

    @Test(dependsOnMethods = "updateLocations")
    public void deleteLocations() {

        given()
                .spec(recSpec)
                .pathParam("locationID", locationID)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{locationID}")

                .then()
           //     .log().body()
                .statusCode(200)
        ;
    }

}











