package com.kupujemprodajem;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

public class AppTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @BeforeClass
    public void setUp() {
        // Initialize WebDriver (Chrome) and maximize window
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Initialize explicit wait
        wait = new WebDriverWait(driver, TIMEOUT);
        // Navigate to KupujemProdajem home page
        driver.get("https://www.kupujemprodajem.com/");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            // Quit the browser after tests
            driver.quit();
        }
    }

    @Test
    public void testSearchFunction() {
        try {
            // Open detailed search panel
            driver.findElement(By.xpath("//span[text()='Pretražite detaljno ']")).click();

            // Select category "Odeća | Ženska"
            WebElement kategorija = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("react-select-categoryId-input"))
            );
            kategorija.sendKeys("Odeća | Ženska");
            kategorija.sendKeys(Keys.ENTER);

            // Select group "Bluze"
            WebElement grupa = driver.findElement(By.id("react-select-groupId-input"));
            grupa.sendKeys("Bluze");
            grupa.sendKeys(Keys.ENTER);

            // Set minimum price to 100
            WebElement cenaOd = driver.findElement(By.id("priceFrom"));
            cenaOd.sendKeys("100");
            // Select currency "din"
            driver.findElement(By.xpath("//input[@type='radio' and @value='rsd']")).click();
            // Enable "Samo sa cenom" filter
            driver.findElement(By.xpath("//span[text()='Samo sa cenom']")).click();

            // Open "Stanje" dropdown and select options
            WebElement stanje = driver.findElement(By.xpath("//div[@id='react-select-condition-placeholder' and text()='Stanje']"));
            stanje.click();
            
            // Select "Novo"
            WebElement novo = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Novo']"))
            );
            novo.click();
            // Select "Nekorišćeno (polovno)"
            WebElement nekorisceno = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Nekorišćeno (polovno)']"))
            );
            nekorisceno.click();

            // Apply all filters
            driver.findElement(By.xpath("//button[@aria-label='Primeni filtere']")).click();

            // Wait for the analytics JSON-LD script to be present
            Thread.sleep(2000); 

            // Extract analytics JSON-LD script content
            WebElement jsonScript = driver.findElement(By.xpath("//script[@type='application/ld+json']"));
            String jsonText = jsonScript.getAttribute("innerHTML");

            // Parse JSON and retrieve the total result count text
            JSONObject obj = new JSONObject(jsonText);
            JSONArray itemList = obj.getJSONArray("itemListElement");
            JSONObject lastItem = itemList.getJSONObject(itemList.length() - 1);
            JSONObject item = lastItem.getJSONObject("item");
            String rezultatText = item.getString("name");

            // Extract numeric value and assert it's greater than 1000
            int actualResult = Integer.parseInt(rezultatText.replaceAll("[^\\d]", ""));
            int expectedResult = 1000;
            Assert.assertTrue(actualResult > expectedResult, "Broj rezultata nije veći od 1000");

            // Click the first advertisement in the results
            WebElement firstAd = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("(//section[contains(@class, 'AdItem_adOuterHolder__lACeh')])[1]//a"))
            );
            firstAd.click();

            // Click "Dodaj u adresar" button
            WebElement adresar = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Dodaj u adresar']"))
            );
            adresar.click();

            // Verify login modal appears
            WebElement loginModal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("LoginModal_modal__2g1Dz"))
            );
            Assert.assertTrue(loginModal.isDisplayed(), "Login forma nije prikazana.");

        } catch (InterruptedException e) {
            // Handle sleep interruption
            Thread.currentThread().interrupt();
            Assert.fail("Test interrupted: " + e.getMessage());
        } catch (Exception e) {
            // Fail the test on any unexpected exception
            Assert.fail("Test encountered an exception: " + e.toString());
        }
    }
}