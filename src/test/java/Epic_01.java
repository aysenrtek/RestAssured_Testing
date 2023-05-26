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

public class Epic_01 {

    Faker faker = new Faker();

    RequestSpecification recSpec;
    String positionCategoryName;
    String positionCategoryId;


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
    public void addPositionCategory() {


        Map<String, String> positionCategory = new HashMap<>();
        positionCategoryName = faker.name().fullName();
        positionCategory.put("name", positionCategoryName);

        positionCategoryId =
                given()
                        .spec(recSpec)
                        .body(positionCategory)

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        .log().all()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("positionCategoryId = " + positionCategoryId);

    }

    @Test(dependsOnMethods = "addPositionCategory")
    public void editPositionCategory() {

        Map<String, String> positionCategory = new HashMap<>();
        positionCategory.put("id", positionCategoryId);

        positionCategoryName = "Osip Senkovskiy" + faker.number().digits(2);
        positionCategory.put("name", positionCategoryName);

        given()
                .spec(recSpec)
                .body(positionCategory)


                .when()
                .put("/school-service/api/position-category")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionCategoryName));

    }

    @Test(dependsOnMethods = "editPositionCategory")
    public void deletePositionCategory() {

        given()
                .spec(recSpec)
                .pathParam("positionCategoryId", positionCategoryId)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionCategoryId}")

                .then()
                .log().body()
                .statusCode(204)
        ;


    }
}
