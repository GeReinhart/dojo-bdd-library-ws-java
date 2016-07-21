Feature: admin offer suggestions

  Background:  
    Given we are working on "fr"

  @level_0_high_level @nominal_case @valid
  Scenario: admin For a new user without any query, suggest offers from the most popular category
    Given a new user without any query
    When the suggestion service is called
    Then offers from the most popular category are returned
    And the user is not a new user anymore

  @level_1_specification @nominal_case @valid
  Scenario: admin For a new user without any query, suggest offers from the most popular category
    Given the user "user1"
    And the userHistory web service have no history on "user1"
    And the query is ""
    And the most popular category returned by userHistory web service is category "124901"
    And the search results for categoryId "124901" is
      | offerId | offerTitle  |
      |       1 | offerTitle1 |
      |       2 | offerTitle2 |
      |       3 | offerTitle3 |
    When we call suggestions service "/Country/fr/Suggestions"
    Then the suggestions service call is a success
    Then the suggestions are
      | offerId | offerTitle  |
      |       1 | offerTitle1 |
      |       2 | offerTitle2 |
      |       3 | offerTitle3 |
    And userHistory web service has been called to create the user "user1"

  @level_1_specification @limit_case @valid
  Scenario: admin For a new user without any query, suggest offers from the most popular category.
     As there are no offers in the most popular, the system use the second most popular category as a fallback.

    Given the user "user1"
    And the userHistory web service have no history on "user1"
    And the query is ""
    And the most popular categories returned by userHistory web service are
      | 124901 |
      | 100001 |
    And there is no result from the search for categoryId "124901"
    And the search results for categoryId "100001" is
      | offerId | offerTitle  |
      |       4 | offerTitle4 |
      |       5 | offerTitle5 |
    When we call suggestions service "/Country/fr/Suggestions"
    Then the suggestions service call is a success
    Then the suggestions are
      | offerId | offerTitle  |
      |       4 | offerTitle4 |
      |       5 | offerTitle5 |
    And userHistory web service has been called to create the user "user1"

  @level_0_high_level @nominal_case @valid
  Scenario: admin For a known user, suggest the offers corresponding to his last query
    Given a known user
    When the suggestion service is called
    Then offers from his last query are returned

  @level_1_specification @nominal_case @valid
  Scenario: admin For a known user, suggest the offers corresponding to his last query
    Given the user "user1"
    And the userHistory web service have history on "user1"
    And the query is ""
    And the last query of "user1" returned by userHistory web service is "sophie la girafe"
    And the search results for the query "sophie la girafe" are
      | offerId | offerTitle  |
      |       7 | offerTitle7 |
      |       8 | offerTitle8 |
      |       9 | offerTitle9 |
    When we call suggestions service "/Country/fr/Suggestions"
    Then the suggestions service call is a success
    Then the suggestions are
      | offerId | offerTitle  |
      |       7 | offerTitle7 |
      |       8 | offerTitle8 |
      |       9 | offerTitle9 |
    And userHistory web service has been called to create the user "user1"

  @level_1_specification @error_case @valid
  Scenario: admin As the search is failing, the system answer that the service is unavailable.
    Given the search is failing
    And the userHistory web service is working properly
    When we call suggestions service "/Country/fr/Suggestions"
    Then the suggestions service answer that the service is not available

 
