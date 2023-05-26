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


public class Epic_12 {

    Faker faker=new Faker();
    String namee;
    String nationalaid;
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
        Map<String,String> Nationalaties=new HashMap<>();
        namee=faker.funnyName().name();
        Nationalaties.put("name",namee);

        nationalaid=
                given()
                        .spec(recspec)
                        .body(Nationalaties)
                        .log().body()

                        .when()
                        .post("https://test.mersys.io/school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(201).extract().path("id");
        System.out.println("nationaliid = " + nationalaid);

    }
    @Test(dependsOnMethods = "Create")
    public void CreateNegative(){
        Map<String,String> Nationalaties=new HashMap<>();
        Nationalaties.put("name",namee);
        given()
                .spec(recspec)
                .body(Nationalaties)
                .log().body()

                .when()
                .post("https://test.mersys.io/school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"));


    }
    @Test(dependsOnMethods = "CreateNegative")
    public void Update(){
        Map<String,String> Nationalaties=new HashMap<>();
        Nationalaties.put("id",nationalaid);
        namee=faker.funnyName().name();
        Nationalaties.put("name",namee);
        given()
                .spec(recspec)
                .body(Nationalaties)
                .log().body()

                .when()
                .put("https://test.mersys.io/school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(200);

    }
    @Test(dependsOnMethods = "Update")
    public void Delete(){

        given()
                .spec(recspec)
                .pathParam("nationalaid", nationalaid)
                .log().uri()

                .when()
                .delete("/school-service/api/nationality/{nationalaid}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
        ;

    }
    @Test(dependsOnMethods = "Delete")
    public void DeleteNegative(){
        given()
                .spec(recspec)
                .pathParam("nationalaid", nationalaid)
                .log().uri()

                .when()
                .delete("/school-service/api/nationality/{nationalaid}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400)
        ;
    }
}




