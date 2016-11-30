Feature: Providing book suggestions

  @level_0_high_level @nominal_case @valid
  Scenario: providing book suggestions
    Given a user
    When we ask for suggestions
    Then the suggestions are popular and available books adpated to the age of the user

  @level_1_specification @nominal_case @valid
  Scenario: suggestions of popular and available books adpated to the age of the user
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |

  @level_1_specification @nominal_case @valid
  Scenario: limit the number of suggestions
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    When we ask for "2" suggestions
    Then the suggestions are
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |

  @level_1_specification @nominal_case @valid
  Scenario: the user have never booked the suggestions
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName |
      | cat1       | Coloriage    |
    And the available books for categories "cat1" are
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
      | b12    | Colorier les vaches | cat1       |
    And the user has already booked the following books
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle           | categoryId |
      | b12    | Colorier les vaches | cat1       |

  @level_1_specification @nominal_case @valid
  Scenario: the books are comming from different categories
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b12    | Colorier les vaches   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b22    | Comptines du loup     | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |

  @level_1_specification @limit_case @valid
  Scenario: not enough suggestions, the books can come from the same categories
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And the available books for categories "cat1,cat2,cat3" are
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b12    | Colorier les vaches   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b22    | Comptines du loup     | cat2       |
    And the user has already booked the following books
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
    When we ask for "3" suggestions
    Then the suggestions are
      | bookId | bookTitle             | categoryId |
      | b12    | Colorier les vaches   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b22    | Comptines du loup     | cat2       |

  @level_1_specification @limit_case @valid
  Scenario: unknown user, no suggestion
    Given the user "Lise"
    And he is unknown
    When we ask for "3" suggestions
    Then there is no suggestions

  @level_1_specification @error_case @valid
  Scenario: one service on which the suggestion system depends on is down
    Given the user "Tim"
    And impossible to get information on the user
    When we ask for "3" suggestions
    Then the system is temporary unavaiable

  @level_2_technical_details @nominal_case @valid
  Scenario: suggestions of popular and available books adpated to the age of the user, he have never booked the suggestions
    Given the user from http://my.library.com/user/Tim
      | field  | value |
      | userId | Tim   |
      | age    | 4     |
    And the categories from http://my.library.com/category?popular=true&age=4
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And the books from http://my.library.com/search?categories=cat1,cat2,cat3&available=true
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    And the books from http://my.library.com/user/Tim/books
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
    When we call http://localhost:9998/suggestions?userId=Tim&maxResults=3
    Then the http code is "200"
    Then the suggestions are
      | bookId | bookTitle             | categoryId |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |

  @level_2_technical_details @limit_case @valid
  Scenario: unknown user, no suggestion
    Given the user from http://my.library.com/user/Lise return http status "404"
    When we call http://localhost:9998/suggestions?userId=Lise&maxResults=3
    Then the http code is "404"

  @level_2_technical_details @error_case @valid
  Scenario: one service on which the suggestion system is down
    Given the user from http://my.library.com/user/Lise return http status "500"
    When we call http://localhost:9998/suggestions?userId=Lise&maxResults=3
    Then the http code is "503"
