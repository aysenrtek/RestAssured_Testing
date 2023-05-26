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

public class Epic_08 {
    Faker faker = new Faker();
    RequestSpecification requestSpec;

    String departmentID;

    @BeforeClass
    @Test
    public void setup() {
        baseURI = "https://test.mersys.io";

        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "turkeyts");
        loginData.put("password", "TechnoStudy123");
        loginData.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(loginData)
                        .when()
                        .post("/auth/login")
                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createDepartment() {
        String code = Long.toString(faker.number().randomNumber());
        String name = faker.company().name();
        Map<String, String> department = new HashMap<>();
        department.put("name", name);
        department.put("code", code);
        department.put("active", "true");
        department.put("school", "6390f3207a3bcb6a7ac977f9");


        departmentID =
                given()
                        .spec(requestSpec)
                        .body(department)
                        .when()
                        .post("/school-service/api/department")
                        .then()
                        .statusCode(201)
                        .extract().path("id");

    }

    @Test(dependsOnMethods = "createDepartment")
    public void updateDepartment() {
        String code = Long.toString(faker.number().randomNumber());
        String name = faker.company().name();
        Map<String, Object> department = new HashMap<>();
        department.put("name", name);
        department.put("code", code);
        department.put("active", false);
        department.put("school", "6390f3207a3bcb6a7ac977f9");
        department.put("id", departmentID);


        given()
                .spec(requestSpec)
                .body(department)
                .when()
                .put("/school-service/api/department")
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "updateDepartment")
    public void deleteDepartment()  {

        given()
                .pathParam("departmentID",departmentID)
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/department/{departmentID}")
                .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteDepartment")
    public void deleteDepartmentNegative() {

        given()
                .pathParam("departmentID",departmentID)
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/department/{departmentID}")
                .then()
                .statusCode(204);
    }
}

