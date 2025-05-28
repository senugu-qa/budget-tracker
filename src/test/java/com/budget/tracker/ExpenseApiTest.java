package com.budget.tracker;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExpenseApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void testAddAndGetExpense() {
        // Create expense JSON body
        String expenseJson = """
            {
              "description": "Lunch",
              "amount": 12.50,
              "date": "2025-05-27",
              "category": "Food"
            }
            """;

        // POST expense and validate response
        int expenseId = given()
                .contentType(ContentType.JSON)
                .body(expenseJson)
                .when()
                .post("/expenses")
                .then()
                .statusCode(201)
                .body("description", equalTo("Lunch"))
                .body("amount", equalTo(12.50f))
                .extract()
                .path("id");

        // GET expense by id and validate
        given()
                .when()
                .get("/expenses/" + expenseId)
                .then()
                .statusCode(200)
                .body("id", equalTo(expenseId))
                .body("description", equalTo("Lunch"));
    }

    @Test
    public void testGetAllExpenses() {
        given()
                .when()
                .get("/expenses")
                .then()
                .statusCode(200)
                .body("$", not(empty()));  // List should not be empty
    }
}