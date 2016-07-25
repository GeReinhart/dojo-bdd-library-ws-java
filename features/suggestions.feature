Feature: Providing book suggestions

  @level_1_specification @nominal_case @valid
  Scenario: suggestions of popular and available books adpated to the age of the user

    Given the user "user1"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName  |
      | cat1       | categoryName1 |
      | cat2       | categoryName2 |
      | cat3       | categoryName3 |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b21    | book21    | cat2       |
      | b31    | book31    | cat3       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b21    | book21    | cat2       |
      | b31    | book31    | cat3       |
      
  @level_1_specification @nominal_case @valid
  Scenario: limit the number of suggestions

    Given the user "user1"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName  |
      | cat1       | categoryName1 |
      | cat2       | categoryName2 |
      | cat3       | categoryName3 |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b21    | book21    | cat2       |
      | b31    | book31    | cat3       |
    When we ask for "2" suggestions
    Then the suggestions are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b21    | book21    | cat2       |
      
  @level_1_specification @nominal_case @valid
  Scenario: the user have never booked the suggestions

    Given the user "user1"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName  |
      | cat1       | categoryName1 |
    And the available books for categories "cat1" are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b12    | book12    | cat1       |
    And the user has already booked the following books
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle | categoryId |
      | b12    | book12    | cat1       |


  @level_1_specification @nominal_case @valid
  Scenario: the books are comming from different categories

    Given the user "user1"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName  |
      | cat1       | categoryName1 |
      | cat2       | categoryName2 |
      | cat3       | categoryName3 |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b12    | book12    | cat1       |
      | b21    | book21    | cat2       |
      | b22    | book22    | cat2       |
      | b31    | book31    | cat3       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b21    | book21    | cat2       |
      | b31    | book31    | cat3       |


  @level_1_specification @limit_case @valid
  Scenario: not enough suggestions, the books can come from the same categories

    Given the user "user1"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName  |
      | cat1       | categoryName1 |
      | cat2       | categoryName2 |
      | cat3       | categoryName3 |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b12    | book12    | cat1       |
      | b21    | book21    | cat2       |
      | b22    | book22    | cat2       |
    And the user has already booked the following books
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle | categoryId |
      | b12    | book12    | cat1       |
      | b21    | book21    | cat2       |
      | b22    | book22    | cat2       |
    
  @level_1_specification @limit_case @valid
  Scenario: unknown user, no suggestion

    Given the user "user2"
    And he is unknown
    When we ask for "3" suggestions
    Then there is no suggestions
      
  @level_2_technical_details @nominal_case @valid
  Scenario: suggestions of popular and available books adpated to the age of the user

    Given the user from http://localhost:8080/user/user1
      | field   | value  |
      | userId  | user1  |
      | age     | 4      |
    And the categories from http://localhost:8081/category?popular=true&age=4
      | categoryId | categoryName  |
      | cat1       | categoryName1 |
      | cat2       | categoryName2 |
      | cat3       | categoryName3 |
    And the books from http://localhost:8082/search?categories=cat1,cat2,cat3&available=true 
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b21    | book21    | cat2       |
      | b31    | book31    | cat3       |
    When we call http://localhost:9998/suggestions?userId=user1&maxResults=3
    Then the http code is "200"
    Then the suggestions are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b21    | book21    | cat2       |
      | b31    | book31    | cat3       |
      

  @level_2_technical_details @nominal_case @valid
  Scenario: unknown user, no suggestion

    Given the user from http://localhost:8080/user/user2 return http status "404" 
    When we call http://localhost:9998/suggestions?userId=user2&maxResults=3
    Then the http code is "404"
      