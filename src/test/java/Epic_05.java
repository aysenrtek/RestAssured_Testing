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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


public class Epic_05 {


    Faker faker = new Faker();
    RequestSpecification recSpec;

    String positionName;
    String positionShortName;
    String positionID;
    String tenantId = "6390ef53f697997914ec20c2";


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
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createEmployeePosition() {


        Map<String, String> position = new HashMap<>();

        positionName = faker.job().title();
        positionShortName = faker.job().position();
        position.put("name", positionName);
        position.put("shortName", positionShortName);
        position.put("tenantId", tenantId);


        positionID =
                given()
                        .spec(recSpec)
                        .body(position)

                        .when()
                        .post("/school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("positionId:" + positionID);

    }

    @Test(dependsOnMethods = "createEmployeePosition")
    public void createEmployeePositionNegative() {


        Map<String, String> position = new HashMap<>();
        position.put("name", positionName);
        position.put("shortName", positionShortName);
        position.put("tenantId", tenantId);

        given()
                .spec(recSpec)
                .body(position)
                .log().body()

                .when()
                .post("/school-service/api/employee-position")
                .then()

                .log().body()
                .statusCode(400)
                .body("message", containsString("already"));


    }

    @Test(dependsOnMethods = "createEmployeePositionNegative")
    public void updateEmployeePosition() {

        Map<String, String> position = new HashMap<>();
        position.put("id", positionID);
        position.put("name", positionName);
        position.put("shortName", positionShortName);
        position.put("tenantId", tenantId);


        given()
                .spec(recSpec)
                .body(position)

                .log().body()

                .when()
                .put("/school-service/api/employee-position")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionName));

    }

    @Test(dependsOnMethods = "updateEmployeePosition")
    public void deleteEmployeePosition() {

        given()
                .spec(recSpec)
                .pathParam("positionID", positionID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteEmployeePosition")
    public void deleteEmployeePositionNegative()  {

        given()
                .spec(recSpec)
                .pathParam("positionID", positionID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionID}")

                .then()
                .log().body()
                .statusCode(204)

        ;

    }


}







