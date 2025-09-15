Feature: Book CRUD operations
  As a user
  I want to manage books
  So that I can create, read, and delete books

  Scenario: Create a new book
    Given a book with title "Cucumber BDD" and author "Tester"
    When the user creates the book
    Then the book should be created with title "Cucumber BDD" and author "Tester"

  Scenario: Get all books
    When the user requests all books
    Then the list of books should be returned

  Scenario: Get a book by id
    Given a book with title "Cucumber BDD" and author "Tester" exists
    When the user requests the book by id
    Then the book should be returned with title "Cucumber BDD" and author "Tester"

  Scenario: Delete a book
    Given a book with title "Cucumber BDD" and author "Tester" exists
    When the user deletes the book by id
    Then the book should not be found by id
