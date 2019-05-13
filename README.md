## Test Case automation requirements
Create Selenium Web browser tests using Java language for amazon.com with following details:

- Search Nikon and sort results from highest price to slowest.
- Select second product and click it for details.
- From details check (verify with assert) that product topic contains text “Nikon D3X”.

Bonus points:

- Creating Cucumber scenario which is used for test execution/test step mapping.
- Implementing the webpage opening step so that url is parameter.
- The test is implemented as Maven project and the test can be executed from command line using command: mvn clean test.
Inspector will set suitable path to the Chrome/Firefox drivers.


## Introduction
The tests are implemented as a maven project with dependencies of Selenium library and Cucumber testing framework,
which fully achieved basic requirements and all the bonus points given above. Inspectors can invoke tests with either
chrome or firefox browser. Example usage can refer to the [below](#Usage).


## Prerequisites
- Java 8
- Maven
- Chrome browser or Firefox browser 
- Optional: Chrome web driver or Firefox web driver


## Local resources
- Chrome web driver
- Firefox web driver


## Usage
Example usage of invoking test with customize chrome driver:
```bash
mvn clean test -Dwebdriver.chrome.driver=<your-chrome-driver-path>
```

Example usage of invoking test with customize firefox driver:
```bash
mvn clean test -Dwebdriver.gecko.driver=<your-firefox-driver-path>
```

Example usage of invoking test with project provided web drivers (Linux only):
```bash
mvn clean test
```


## Troubleshooting with firefox web driver
The problem was that when testing with firefox web driver, attempt to click on one of the sort dropdown option always fails.
Example error log as below:
```bash
 Element <select id="s-result-sort-select" class="a-native-dropdown" name="s"> is not clickable at point (1197,127) because another element <span class="a-button-text a-declarative"> obscures it
```

It indicated that the select element (or its option element) is not clickable due to another span element obscuring on top.
However, chrome web driver is working correctly leading to passing test. I've tried with quite a few approaches summarized below to mitigate it but in vain.

1. Using selenium interaction library e.g.
```java
Actions action = new Actions(driver);
action.moveToElement(driveri.findElement(By.id("s-result-sort-select"))).click().perform();
```
This didn't work since actions underlying still tries to click on the located element, while it's obscured according to firefox web driver.

2. Search for its parent element and try to click it before selecting the target dropdown option
```java
WebElement parentElement = element.findElement(By.xpath("./.."));
// or
WebElement span = driver.findElement(By.xpath("//span[contains(@class,\"a-dropdown-container\")]"));
// or
WebElement form = driver.findElement(By.xpath("//form[@method=\"get\" and @action=\"/s\"]"));
```
This didn't work since the sort select element's parent element is similarly obscured, while the container form submit
is not leading to correctly rendering the dropdown options.

3. Inject codes to hide those elements which are reported obscuring the target select element
```java
List<WebElement> overlayElements = driver.findElements(
   By.xpath("//span[" +
       "contains(@class,\"a-dropdown-prompt\")" +
       " or contains(@class, \"a-button-text\")" +
       " or contains(@class, \"a-button-inner\")" +
       " or contains(@class, \"a-button\")" +
       " or contains(@class, \"a-dropdown-container\")" +  // this is the parent container holding the select
   "]"));
for (WebElement overlayElement : overlayElements) {
   ((JavascriptExecutor) driver).executeScript("arguments[0].style.visibility='hidden'", overlayElement);
}
```
This didn't work since even the parent container holding the select element is reported obscuring the select element, while
removing the parent container will obviously lose the select element as well.

There are other dead ends too, e.g. try with different selenium select option API, use javascript executor to inject click
element code and run, waiting for the overlay spans to hide etc. but none of them is viable.

Thus, my assumption is that the native dropdown clicking capability is not properly provided by firefox web driver, especially
in a more complicated context with automated spans and other different tags. Optimally an issue should be created in https://github.com/mozilla/geckodriver/releases
and other browsers e.g. chrome should be preferred for testing such case.


## Junit tests
The project also includes a junit test case implementation, which is ignored because the cucumber test case implementation is preferred.
