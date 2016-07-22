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


  @level_1_specification @limit_case @future
  Scenario: not enough suggestions,
            the books can come from the same categories
            

    Given the user "user1"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName  |
      | cat1       | categoryName1 |
      | cat2       | categoryName2 |
      | cat3       | categoryName3 |
    And the search results for categories "cat1,cat2,cat3" are
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
      | b12    | book12    | cat1       |
      | b21    | book22    | cat2       |
      | b22    | book22    | cat2       |
    And the user has already reserved the following books
      | bookId | bookTitle | categoryId |
      | b11    | book11    | cat1       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle | categoryId |
      | b12    | book12    | cat1       |
      | b21    | book22    | cat2       |
      | b22    | book22    | cat2       |
      
      