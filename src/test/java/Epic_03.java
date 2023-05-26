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

public class Epic_03 {
    String schoolId = "6390f3207a3bcb6a7ac977f9";
    String documentName;
    String[] stage = {"DISMISSAL"};
    String documentID;


    Faker faker = new Faker();
    RequestSpecification recSpec;

    @BeforeClass
    public void Setup() {
        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies = given().contentType(ContentType.JSON).body(userCredential)

                .when().post("/auth/login")

                .then().log().all().statusCode(200).extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).addCookies(cookies).build();
    }

    @Test
    public void createDocument() {


        Map<String, Object> document = new HashMap<>();
        documentName = faker.name().username();
        document.put("description","asdasd");
        document.put("name",documentName);
        document.put("attachmentStages",stage);
        document.put("schoolId",schoolId);

        documentID =
                given()
                        .spec(recSpec).
                        body(document)
                        .log()
                        .body()

                        .when().
                        post("/school-service/api/attachments/create")

                        .then().
                        log().all()

                        .statusCode(201)
                        .extract()
                        .path("id");

        System.out.println("documentId = " + documentID);

    }

    @Test(dependsOnMethods = "createDocument")
    public void createDocumentNegative() {
        Map<String, Object> document = new HashMap<>();
        documentName = faker.name().username();
        document.put("description","asdasd");
        document.put("name",documentName);
        document.put("attachmentStages",stage);
        document.put("schoolId",schoolId);

        given()
                .spec(recSpec)
                .body(document)
                .log().body()

                .when()
                .post("/school-service/api/attachments/create")

                .then()
                .log().body()
                .statusCode(201)
        // .body("message", equalTo("null"))
        ;

    }

    @Test(dependsOnMethods = "createDocumentNegative")
    public void updateDocument() {


        Map<String, Object> document = new HashMap<>();
        document.put("id",documentID);
        document.put("schoolId",schoolId);

        documentName="kral reis"+faker.number().digits(5);
        document.put("name",documentName);
        document.put("attachmentStages",stage);

        documentID =
                given()
                        .spec(recSpec)
                        .body(document)
                        .log().all()

                        .when()
                        .put("/school-service/api/attachments")

                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .path("id");
        //   .body("name", equalTo(documentName))
        ;
    }

    @Test(dependsOnMethods = "updateDocument")
    public void deleteDocument() {
        given()
                .spec(recSpec)
                .pathParam("documentID",documentID)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/{documentID}")

                .then()
                .log().body()
                .statusCode(200)
        ;


    }

    @Test(dependsOnMethods = "deleteDocument")
    public void deleteDocumentNegative() {
        given()
                .spec(recSpec)
                .pathParam("documentID",documentID)
                //  .log().all()

                .when()
                .delete("/school-service/api/attachments/{documentID}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400)
                .body("message",equalTo("Attachment Type not found"))
        ;

    }

}
