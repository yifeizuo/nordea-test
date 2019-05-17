package com.nordea.cucumbertest;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

public class Stepdefs {
    private WebDriver driver;

    /*
     * Setup first try to launch with set system property Chrome, then Firefox.
     * If failed, first try to add Chrome WebDriver, then Firefox WebDriver.
     * If failed, it means no Chrome or Firefox browser installed, then throw exception
     */
    @Before
    public void setUp() {
        if (!StringUtils.isEmpty(System.getProperty("webdriver.chrome.driver"))) {
            driver = new ChromeDriver();
        } else if (!StringUtils.isEmpty(System.getProperty("webdriver.gecko.driver"))) {
            driver = new FirefoxDriver();
        } else {
            // no system property set, attempt to use provided drivers for Linux only
            try {
                File chromeDriver = new File(getClass().getClassLoader().getResource("chromedriver").getFile());
                chromeDriver.setExecutable(true);
                System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
                driver = new ChromeDriver();
                return;
            } catch (IllegalStateException e) {
                System.out.println("Cannot start chrome: " + e);
            }

            try {
                File firefoxDriver = new File(getClass().getClassLoader().getResource("geckodriver").getFile());
                firefoxDriver.setExecutable(true);
                System.setProperty("webdriver.gecko.driver", firefoxDriver.getAbsolutePath());
                driver = new FirefoxDriver();
                return;
            } catch (IllegalStateException e) {
                System.out.println("Cannot start firefox: " + e);
            }

            throw new IllegalStateException("Chrome or Firefox browser and their web driver are not found!");
        }
    }

    @When("open url {}")
    public void openURL(String url) {
        /*
         * window().maximize() might cause side effect if other tests assert window is not maximized
         * the consideration here is for the convenience of taking screenshots for example
         */
        //driver.manage().window().maximize();
        driver.get(url);
    }

    @And("^search Nikon$")
    public void searchNikon() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5).getSeconds());
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));

        searchBox.sendKeys("Nikon");
        searchBox.submit();
    }

    @And("^sort by Price high to low$")
    public void sortByPriceHighToLow() {
        /*
         * find the "sort by" dropDown select by it's ID
         * select the "price: High to low" by it's string value
         */
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5).getSeconds());
        WebElement sortElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("s-result-sort-select")));
        Select sortByDropDown = new Select(sortElement);

        sortByDropDown.selectByValue("price-desc-rank");
    }

    @And("^click second product for details$")
    public void clickSecondProductForDetails() {
        // find the sorted result
        WebElement sortedResult = driver.findElement(By.className("s-result-list"));
        WebElement secondItem = sortedResult.findElement(By.xpath("//div[@data-index=\"1\"]//a[contains(@class, \"a-text-normal\")]"));
        secondItem.click();
    }

    @Then("^the product title from details contains Nikon D4S$")
    public void theProductTitleFromDetailsContainsNikonDS() {
        WebElement productTitle = driver.findElement(By.id("productTitle"));
        String productTitleText = productTitle.getText();
        System.out.println(productTitleText);

        Assert.assertTrue(productTitleText.contains("Nikon D4S"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
