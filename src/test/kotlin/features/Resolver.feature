Feature: CRUD Operations with GraphQL Resolver

  Scenario: Creating an Author
    Given a user wants to create an author
    When they provide the author details:
      | name      | age |
      | John Doe  | 30  |
    Then the author is created

  Scenario: Creating a Tutorial
    Given a user wants to create a tutorial
    When they provide the tutorial details:
      | title           | description         | authorId |
      | GraphQL Basics  | Introduction to...  | 1        |
    And they provide corresponding author details:
      | id  | name         | age |
      | 1   | John Doe     | 35  |
    Then the tutorial is created

  Scenario: Deleting a Tutorial
    Given a user wants to delete a tutorial
    When they provide the tutorial ID 1
    Then the tutorial is deleted

  Scenario: Updating a Tutorial
    Given a user wants to update a tutorial
    When they provide the updated tutorial details:
      | id  | title          | description       | authorId |
      | 1   | Updated Title  | Updated desc...   | 1        |
    And they provide corresponding author details for update tutorial:
      | id  | name         | age |
      | 1   | John Doe     | 35  |
    Then the tutorial is updated

  Scenario: Updating an Author
    Given a user wants to update an author
    When they provide the updated author details:
      | id  | name         | age |
      | 1   | Updated Name | 35  |
    Then the author is updated

  Scenario: Finding all Authors
    Given a user wants to find all authors
    Then a list of authors is returned

  Scenario: Finding all Tutorials
    Given a user wants to find all tutorials
    Then a list of tutorials is returned
