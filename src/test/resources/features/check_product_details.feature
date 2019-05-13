Feature: Check product details
  The product details of the sorted search results is correctly rendered

  Scenario: Second product title contains D4S
    When open url https://www.amazon.com
    And search Nikon
    And sort by Price high to low
    And click second product for details
    Then the product title from details contains Nikon D4S