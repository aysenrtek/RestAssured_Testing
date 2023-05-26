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
public class Epic_11 {
    Faker faker=new Faker();
    String cod;
    String descripption;
    String discountid;
    String priority;
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
        Map<String,String> Discount=new HashMap<>();

        descripption=faker.country().countryCode2();
        Discount.put("description",descripption);

        cod=faker.name().bloodGroup();
        Discount.put("code",cod);

        priority=faker.number().digits(3);
        Discount.put("priority",priority);



        discountid=
                given()
                        .spec(recspec)
                        .body(Discount)
                        .log().body()

                        .when()
                        .post("https://test.mersys.io/school-service/api/discounts")

                        .then()
                        .log().body()
                        .statusCode(201).extract().path("id");
        System.out.println("discountid = " + discountid);

    }
    @Test(dependsOnMethods = "Create")
    public void CreateNegative(){
        Map<String,String> Discount=new HashMap<>();

        Discount.put("description",descripption);
        Discount.put("code",cod);
        Discount.put("priority",priority);


        given()
                .spec(recspec)
                .body(Discount) // giden body
                .log().body() // giden body yi log olarak göster

                .when()
                .post("https://test.mersys.io/school-service/api/discounts")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400)
                .body("message", containsString("already")) ; // gelen body deki...


    }
    @Test(dependsOnMethods = "CreateNegative")
    public void Update(){

        Map<String,String> Discount=new HashMap<>();
        Discount.put("id",discountid);
        descripption=faker.country().countryCode2();
        Discount.put("description",descripption);

        cod=faker.name().bloodGroup();
        Discount.put("code",cod);

        priority=faker.number().digits(3);
        Discount.put("priority",priority);


        given()
                .spec(recspec)
                .body(Discount) // giden body
                .log().body() // giden body yi log olarak göster

                .when()
                .put("https://test.mersys.io/school-service/api/discounts")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
        // gelen body deki...
        ;
    }
    @Test(dependsOnMethods = "Update")
    public void Delete(){
        given()
                .spec(recspec)
                .pathParam("discountid", discountid)
                .log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountid}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "Delete")
    public void DeleteNegative(){
        given()
                .spec(recspec)
                .pathParam("discountid", discountid)
                .log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountid}")

                .then()
                .log().body() // gelen body yi log olarak göster
                .statusCode(400);
    }
}





