package com.budget.tracker;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.openqa.selenium.JavascriptExecutor;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExpenseUiTest {

    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAddExpense() throws InterruptedException {
        driver.get("http://localhost:8080/");

        driver.findElement(By.id("description")).sendKeys("Test Lunch");
        driver.findElement(By.id("amount")).sendKeys("15.75");

        WebElement dateInput = driver.findElement(By.id("date"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", dateInput, "2025-05-27");


        driver.findElement(By.id("category")).sendKeys("Food");

        driver.findElement(By.xpath("//button[text()='Add Expense']")).click();

        Thread.sleep(2000);

        WebElement expensesTable = driver.findElement(By.id("expense-table-body"));
        assertTrue(expensesTable.getText().contains("Test Lunch"));
    }
}