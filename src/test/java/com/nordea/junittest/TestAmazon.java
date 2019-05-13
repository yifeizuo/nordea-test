package com.nordea.junittest;

import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;

public class TestAmazon {
    private WebDriver driver;

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

    @Ignore
    @Test
    public void testNikon() throws Exception {
        driver.manage().window().maximize();
        driver.get("https://www.amazon.com/");

        WebElement searchBox;
        searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys("Nikon");
        searchBox.submit();

        // find the "sort by" dropDown select by it's ID
        // select the "price: High to low" by it's string value
        Select sortByDropDown = new Select(driver.findElement(By.id("s-result-sort-select")));
        sortByDropDown.selectByValue("price-desc-rank");

        // find the sorted result
        WebElement sortedResult = driver.findElement(By.className("s-result-list"));
        WebElement secondItem = sortedResult.findElement(By.xpath("//div[@data-index=\"1\"]"));
        secondItem.click();

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

