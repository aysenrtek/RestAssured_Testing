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

public class Epic_10 {
    Faker faker=new Faker();

    String gradeId;
    String gradename;
    String shortName2;
    String  nextGradeLevel;
    String order;

    RequestSpecification recspec;
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
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        recspec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }
    {
    }
    @Test
    public void Create(){
        Map<String,String> Grade=new HashMap<>();
        gradename=faker.name().fullName();
        Grade.put("name",gradename);

        shortName2=faker.name().lastName();
        Grade.put("shortName",shortName2);

        nextGradeLevel=null;
        Grade.put("nextGradeLevel", nextGradeLevel);

        Grade.put("order","2");



        gradeId=
                given()
                        .spec(recspec)
                        .body(Grade)
                        .log().body()

                        .when()
                        .post("https://test.mersys.io/school-service/api/grade-levels")

                        .then()
                        .log().body()
                        .statusCode(201).extract().path("id");
        System.out.println("gradeId = " + gradeId);

    }
    @Test(dependsOnMethods = "Create")
    public void CreateNegative(){
        Map<String,String> Grade=new HashMap<>();
        Grade.put("name",gradename);
        Grade.put("shortName",shortName2);
        Grade.put("nextGradeLevel", null);
        Grade.put("order","2");

        given()
                .spec(recspec)
                .body(Grade) // giden body
                .log().body() // giden body yi log olarak göster

                .when()
                .post("/school-service/api/grade-levels")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400)
                .body("message", containsString("already"))  // gelen body deki...
        ;
    }
    @Test(dependsOnMethods = "CreateNegative")
    public void Update(){

        Map<String,String> Grade=new HashMap<>();
        Grade.put("id",gradeId);

        gradename=faker.name().fullName();
        Grade.put("name",gradename);

        shortName2=faker.name().lastName();
        Grade.put("shortName",shortName2);

        nextGradeLevel=null;
        Grade.put("nextGradeLevel", nextGradeLevel);

        Grade.put("order","2");

        given()
                .spec(recspec)
                .body(Grade) // giden body
                //.log().body() // giden body yi log olarak göster

                .when()
                .put("/school-service/api/grade-levels")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
        ;

    }
    @Test(dependsOnMethods = "Update")
    public void Delete(){

        given()
                .spec(recspec)
                .pathParam("gradeId", gradeId)
                .log().uri()

                .when()
                .delete("/school-service/api/grade-levels/{gradeId}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "Delete")
    public void DeleteNegative(){
        given()
                .spec(recspec)
                .pathParam("gradeId", gradeId)
                .log().uri()

                .when()
                .delete("/school-service/api/grade-levels/{gradeId}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400) // jenkins için hatalı test için 500 yapıldı normalde 400

        ;
    }
}
