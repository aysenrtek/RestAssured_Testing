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

public class Epic_04 {

    Faker faker=new Faker();
    String fieldsName;
    String fieldsID;
    RequestSpecification recSpec;
    String schoolId="6390f3207a3bcb6a7ac977f9";

    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createFields()  {

        Map<String,Object > fields=new HashMap<>();
        fieldsName=faker.name().fullName();
        fields.put("name",faker.name().fullName());
        fields.put("code",faker.number().digits(3));
        fields.put("type","DATE");
        fields.put("schoolId",schoolId);

        fieldsID=
                given()
                        .spec(recSpec)
                        .body(fields)
                        .log().body()

                        .when()
                        .post("/school-service/api/entity-field")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
        ;

        System.out.println("fieldsID = " + fieldsID);
    }

    @Test(dependsOnMethods = "createFields")
    public void updateFields()  {

        Map<String,Object> fields =new HashMap<>();
        fields.put("id",fieldsID);

        fieldsName="ayse"+ faker.number().digits(2);
        fields.put("name",fieldsName);
        fields.put("code",faker.number().digits(3));
        fields.put("type","DATE");
        fields.put("schoolId",schoolId);

        given()
                .spec(recSpec)
                .body(fields) // giden body
                //.log().body() // giden body yi log olarak göster

                .when()
                .put("/school-service/api/entity-field")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
                .body("name", equalTo(fieldsName))
        ;
    }

    @Test(dependsOnMethods = "updateFields")
    public void deleteFields()  {

        given()
                .spec(recSpec)
                .pathParam("fieldsID", fieldsID)
                .log().uri()

                .when()
                .delete("/school-service/api/entity-field/{fieldsID}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(204)
        ;

    }




}
