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

public class Epic_09 {
    Faker faker = new Faker();
    RequestSpecification requestSpec;
    String accountID;

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
    public void createBankAccounts() {
        String iban = Long.toString(faker.number().randomNumber());
        String integrationCode = Integer.toString(faker.number().hashCode());
        Map<String, String> bank = new HashMap<>();
        bank.put("name", faker.name().name());
        bank.put("iban", iban);
        bank.put("integrationCode", integrationCode);
        bank.put("currency", "USD");
        bank.put("schoolId", "6390f3207a3bcb6a7ac977f9");


        accountID =
                given()
                        .spec(requestSpec)
                        .body(bank)
                        .when()
                        .post("/school-service/api/bank-accounts")
                        .then()
                        .statusCode(201)
                        .extract().path("id");

    }

    @Test(dependsOnMethods = "createBankAccounts")
    public void updateBankAccounts() {
        String iban = Long.toString(faker.number().randomNumber());
        String integrationCode = Integer.toString(faker.number().hashCode());
        Map<String, String> bank = new HashMap<>();
        bank.put("name", faker.name().name());
        bank.put("iban", iban);
        bank.put("integrationCode", integrationCode);
        bank.put("currency", "USD");
        bank.put("schoolId", "6390f3207a3bcb6a7ac977f9");
        bank.put("id", accountID);


        given()
                .spec(requestSpec)
                .body(bank)
                .when()
                .put("/school-service/api/bank-accounts")
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "updateBankAccounts")
    public void deleteBankAccounts() {

        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/bank-accounts/" + accountID)
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "deleteBankAccounts")
    public void deleteBankAccountsNegative() {

        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/bank-accounts/" + accountID)
                .then()
                .statusCode(400);
    }

}
