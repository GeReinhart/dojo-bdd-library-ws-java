Feature: Fournir des suggestions de livres

  # Etape 1 : Implémenter le cas minimal de niveau 2  

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
    When on appelle http://localhost:9998/suggestions?userId=Tim&maxResults=3
    Then le code http retourné est  "200"
    And les données retournées sont
    """
     <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     <suggestions>
         <suggestions bookId="b11" bookTitle="Colorier les poules" categoryId="cat1"/>
         <suggestions bookId="b21" bookTitle="Comptines de la ferme" categoryId="cat2"/>
         <suggestions bookId="b31" bookTitle="Histoires de la mer" categoryId="cat3"/>
     </suggestions>
    """
      
  # Etape 2 : Implémenter le cas minimal de niveau 1 correspondant  réutilisation de phrase exécutable de niveau d'abstraction inférieur  

  @level_1_specification @nominal_case @valid
  Scenario: les suggestions proposées sont populaires, disponibles et adaptées à l age de l utilisateur
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

  # Etape 3 : Implémenter les autres cas nominaux de niveau 1  => réutilisation de phrase exécutable de mếme niveau d abstraction  

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


      # Plus de lisibilité du scénario en générer des données dans les steps  

  @level_1_specification @nominal_case @valid
  Scenario: limiter le nombre de suggestions
    Given l utilisateur "Tim"
    And il a "4" ans
    And "3" livres  sont disponibles pour les catégories populaires pour cet age
    When on demande "2" suggestions
    Then "2" suggestions sont proposées parmi les livres précédents

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

  # Etape 4 : Implémenter les autres cas de niveau 2 pour avoir toutes les phrases exécutables de base 

  @level_2_technical_details @limit_case @valid
  Scenario: pas de suggestion pour les utilisateurs inconnus
    Given l utilisateur depuis le web service http://my.library.com/user/Lise retourne un code http "404"
    When on appelle http://localhost:9998/suggestions?userId=Lise&maxResults=3
    Then le code http retourné est  "404"

  @level_2_technical_details @error_case @valid
  Scenario: un service pour lequel le systeme dépend est indisponible
    Given l utilisateur depuis le web service http://my.library.com/user/Lise retourne un code http "500"
    When on appelle http://localhost:9998/suggestions?userId=Lise&maxResults=3
    Then le code http retourné est  "503"

  # Etape 5 : Implémenter les autres cas de niveau 1  

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


  # Etape 6 : Implémenter le niveau 0 en générant des données dans les steps 

  @level_0_high_level @nominal_case @valid
  Scenario: fournir des suggestions de livres
    Given un utilisateur
    When on demande suggestions
    Then les suggestions proposées sont populaires, disponibles et adaptées à l age de l utilisateur    

