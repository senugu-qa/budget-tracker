package com.budget.tracker;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExpenseUiTest {

    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Check if running in CI
        String ciEnv = System.getenv("CI");
        if ("true".equalsIgnoreCase(ciEnv)) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--user-data-dir=/tmp/chrome-user-data-" + System.currentTimeMillis());
        }

        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @RegisterExtension
    TestWatcher watcher = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            if (driver instanceof TakesScreenshot) {
                try {
                    TakesScreenshot ts = (TakesScreenshot) driver;
                    File screenshot = ts.getScreenshotAs(OutputType.FILE);
                    try (InputStream is = Files.newInputStream(screenshot.toPath())) {
                        Allure.addAttachment("Screenshot - " + context.getDisplayName(), "image/png", is, ".png");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    };

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

    @Test
    public void testFailExample_UI() throws InterruptedException {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("amount")).sendKeys("-100");  // invalid negative amount
        driver.findElement(By.xpath("//button[text()='Add Expense']")).click();

        Thread.sleep(1000);  // wait for error message to show

        // Now trigger failure to capture screenshot after error message is visible
        Assertions.fail("Intentional failure after UI error displayed");

    }
}