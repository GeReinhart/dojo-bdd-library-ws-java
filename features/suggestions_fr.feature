Feature: Fournir des suggestions de livres

  @level_0_high_level @nominal_case @valid
  Scenario: fournir des suggestions de livres
    Given un utilisateur
    When on demande suggestions
    Then les suggestions proposées sont populaires, disponibles et adaptées à l age de l utilisateur

  @level_1_specification @nominal_case @valid
  Scenario: suggestions of popular and available books adpated to the age of the user
    Given l utilisateur "Tim"
    And il a "4" ans
    And les catégories populaires pour cet age sont
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And les livres disponibles pour les catégories "cat1,cat2,cat3" sont
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    When on demande "3" suggestions
    Then les suggestions sont
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |

  @level_1_specification @nominal_case @valid
  Scenario: limiter le nombre de suggestions
    Given l utilisateur "Tim"
    And il a "4" ans
    And les catégories populaires pour cet age sont
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And les livres disponibles pour les catégories "cat1,cat2,cat3" sont
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    When on demande "2" suggestions
    Then les suggestions sont
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |

  @level_1_specification @nominal_case @valid
  Scenario: l utilisateur n a jamais reservé les livres qu on lui suggère
    Given l utilisateur "Tim"
    And il a "4" ans
    And les catégories populaires pour cet age sont
      | categoryId | categoryName |
      | cat1       | Coloriage    |
    And les livres disponibles pour les catégories "cat1" sont
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
      | b12    | Colorier les vaches | cat1       |
    And l utilisateur a déja reservé les livres suivants
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
    When on demande "3" suggestions
    Then les suggestions sont
      | bookId | bookTitle           | categoryId |
      | b12    | Colorier les vaches | cat1       |

  @level_1_specification @nominal_case @valid
  Scenario: les livres suggerés proviennent de catégories différentes
    Given l utilisateur "Tim"
    And il a "4" ans
    And les catégories populaires pour cet age sont
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And les livres disponibles pour les catégories "cat1,cat2,cat3" sont
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b12    | Colorier les vaches   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b22    | Comptines du loup     | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    When on demande "3" suggestions
    Then les suggestions sont
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |

  @level_1_specification @limit_case @valid
  Scenario: s il n y a pas assez de suggestions, on propose des livres de même catégories
    Given l utilisateur "Tim"
    And il a "4" ans
    And les catégories populaires pour cet age sont
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And les livres disponibles pour les catégories "cat1,cat2,cat3" sont
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b12    | Colorier les vaches   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b22    | Comptines du loup     | cat2       |
    And l utilisateur a déja reservé les livres suivants
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
    When on demande "3" suggestions
    Then les suggestions sont
      | bookId | bookTitle             | categoryId |
      | b12    | Colorier les vaches   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b22    | Comptines du loup     | cat2       |

  @level_1_specification @limit_case @valid
  Scenario: pas de suggestion pour les utilisateurs inconnus
    Given l utilisateur "Lise"
    And il est inconnu 
    When on demande "3" suggestions
    Then il n y a pas de suggestions

  @level_1_specification @error_case @valid
  Scenario: un service pour lequel le systeme dépend est indisponible
    Given l utilisateur "Tim"
    And impossible de récupérer les informations de l utilisateur
    When on demande "3" suggestions
    Then le système est temporairement indisponible

  @level_2_technical_details @nominal_case @valid
  Scenario: les livres proposés sont populaires, disponibles, adaptés à l age de l utilisateur et celui ci ne les a jamais reservés
    Given l utilisateur depuis le web service http://my.library.com/user/Tim
      | field  | value |
      | userId | Tim   |
      | age    | 4     |
    And les catégories depuis le web service http://my.library.com/category?popular=true&age=4
      | categoryId | categoryName           |
      | cat1       | Coloriage              |
      | cat2       | Comptines              |
      | cat3       | Histoires pour le dodo |
    And les livres depuis le web service http://my.library.com/search?categories=cat1,cat2,cat3&available=true
      | bookId | bookTitle             | categoryId |
      | b11    | Colorier les poules   | cat1       |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |
    And les livres depuis le web service http://my.library.com/user/Tim/books
      | bookId | bookTitle           | categoryId |
      | b11    | Colorier les poules | cat1       |
    When on appelle http://localhost:9998/suggestions?userId=Tim&maxResults=3
    Then le code http retourné est  "200"
    Then les suggestions sont
      | bookId | bookTitle             | categoryId |
      | b21    | Comptines de la ferme | cat2       |
      | b31    | Histoires de la mer   | cat3       |

  @level_2_technical_details @limit_case @valid
  Scenario: pas de suggestion pour les utilisateurs inconnus
    Given l utilisateur depuis le web service http://my.library.com/user/Lise retourne un code http "404"
    When on appelle http://localhost:9998/suggestions?userId=Lise&maxResults=3
    Then le code http retourné est  "404"

  @level_2_technical_details @error_case @valid
  Scenario: one service on which the suggestion system is down
    Given l utilisateur depuis le web service http://my.library.com/user/Lise retourne un code http "500"
    When on appelle http://localhost:9998/suggestions?userId=Lise&maxResults=3
    Then le code http retourné est  "503"
