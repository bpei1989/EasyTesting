Feature: HttpClient Testing
  Scenario: Get resource
    Given I have a website
    When I use it to get resource
    Then I should get it