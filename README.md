#  Sprint Framework - ETU002756

Sprint Framework est un micro-framework Java basé sur l'API Servlet (compatible avec Tomcat 9), conçu pour simplifier le développement d'applications web et d'API REST. Il offre une structure modulaire avec des fonctionnalités telles que le routage, la validation, l'authentification, la gestion de sessions et la gestion des erreurs, inspiré de Spring MVC mais optimisé pour la simplicité et la flexibilité.

---

##  Table des matières
- [Introduction](#introduction)
- [Intégration dans un projet](#intégration-dans-un-projet)
- [Dépendances](#dépendances)
- [Configuration](#configuration)
- [Annotations](#annotations)
- [Validation](#validation)
- [Authentification](#authentification)
- [Gestion des erreurs](#gestion-des-erreurs)
- [Journalisation](#journalisation)
- [Aperçu des sprints](#aperçu-des-sprints)
- [Exemples](#exemples)
- [Architecture](#architecture)
- [Améliorations futures](#améliorations-futures)
- [Auteur](#auteur)

---

##  Introduction

Sprint Framework est conçu pour rationaliser le développement web en Java en offrant une alternative légère mais robuste aux frameworks plus lourds. Il utilise des annotations Java pour la configuration, prend en charge les modèles MVC traditionnels et les API RESTful, et inclut des fonctionnalités intégrées pour la validation des formulaires, la gestion des sessions et la gestion des erreurs. Le framework utilise SLF4J avec Logback pour la journalisation, garantissant des capacités détaillées de débogage et de surveillance.

**Caractéristiques principales :**
- Contrôleurs et routage basés sur des annotations
- Mappage automatique des formulaires aux objets
- Gestion des sessions
- Support des API REST avec sérialisation JSON
- Validation et gestion des erreurs complètes
- Architecture modulaire pour l'extensibilité

---

##  Intégration dans un projet

Pour utiliser Sprint Framework, deux options sont disponibles :

1. **Intégrer le code source** :
   - Copiez le package `mg.sprint.framework` dans votre projet.
   - Assurez-vous que toutes les dépendances sont incluses dans votre outil de build (Maven/Gradle).

2. **Exporter en JAR** :
   - Utilisez les scripts fournis pour empaqueter le framework :
     - `Jat.bat` pour Windows
     - `Jar.sh` pour Linux
   - Incluez le JAR généré dans le classpath de votre projet.

---

##  Dépendances

Le framework nécessite les dépendances suivantes, à ajouter dans votre `pom.xml` (Maven) ou configuration équivalente :

```xml
<dependencies>
    <!-- Servlet API -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>4.0.1</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- Gson pour le support JSON -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.9</version>
    </dependency>
    
    <!-- Paranamer pour la résolution des noms de paramètres -->
    <dependency>
        <groupId>com.thoughtworks.paranamer</groupId>
        <artifactId>paranamer</artifactId>
        <version>2.8</version>
    </dependency>
    
    <!-- SLF4J et Logback pour la journalisation -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.32</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.6</version>
    </dependency>
</dependencies>
```

**Note** : Assurez-vous que votre projet est compilé avec l'option `-parameters` (ou `-g` pour les informations de débogage) pour supporter la résolution des noms de paramètres par Paranamer, utilisée dans `ArgumentResolver`.

---

##  Configuration

### web.xml

Configurez le servlet `FrontController` et le filtre `PreviousUrlFilter` dans votre `web.xml` :

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!-- Servlet -->
    <servlet>
        <servlet-name>FrontController</servlet-name>
        <servlet-class>mg.sprint.framework.servlet.FrontController</servlet-class>
        <init-param>
            <param-name>base-package</param-name>
            <param-value>mg.sprint.test.controllers</param-value>
        </init-param>
        <init-param>
            <param-name>model-package</param-name>
            <param-value>mg.sprint.test.models</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>FrontController</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!-- Filtre -->
    <filter>
        <filter-name>PreviousUrlFilter</filter-name>
        <filter-class>mg.sprint.framework.filter.PreviousUrlFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>PreviousUrlFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```

**Notes** :
- Le paramètre `base-package` indique où se trouvent les classes de contrôleurs.
- Le paramètre `model-package` indique où se trouvent les classes de modèles avec l'annotation `@ClassLevel`.
- L'annotation `@MultipartConfig` sur `FrontController` active le support des uploads de fichiers.

### sprint.properties

Créez un fichier `sprint.properties` dans `src/main/resources` pour configurer l'authentification et la journalisation :

```properties
# Clé pour le niveau d'authentification en session
key.authentification=userLevel

# Niveau de journalisation pour le framework
logging.level.mg.sprint=DEBUG
```

---

##  Annotations

Sprint Framework utilise des annotations pour définir les contrôleurs, les routes, les paramètres, la validation et l'authentification.

### Annotations des contrôleurs
- **`@Controller`** : Marque une classe comme contrôleur (`mg.sprint.framework.annotation.controller.Controller`).
- **`@BaseUrl(path="/prefix")`** : Définit un préfixe d'URL pour toutes les méthodes d'un contrôleur.
- **`@AuthController(level=x)`** : Sécurise un contrôleur avec un niveau d'authentification requis.

### Annotations des méthodes
- **`@Url(path="/route")`** : Spécifie le chemin d'URL pour une méthode de contrôleur.
- **`@Get`**, **`@Post`** : Définit le verbe HTTP (GET ou POST) pour une méthode.
- **`@RestAPI`** : Indique que la méthode retourne des données JSON.
- **`@AuthMethod(level=x)`** : Sécurise une méthode avec un niveau d'authentification requis.

### Annotations des paramètres
- **`@RequestParam("name")`** : Lie un paramètre de requête à un argument de méthode.
- **`@RequestObject(name="prefix")`** : Mappe les paramètres de formulaire à un objet, en utilisant un préfixe pour les noms de champs.

### Annotations de validation
- **`@Required`** : Assure qu'un champ n'est pas vide.
- **`@Numeric`** : Valide qu'un champ contient uniquement des chiffres.
- **`@Decimal`** : Valide un nombre décimal (par exemple, `12.34` ou `-12.34`).
- **`@Min(value=x)`** : Assure qu'un champ numérique est ≥ la valeur spécifiée.
- **`@Max(value=x)`** : Assure qu'un champ numérique est ≤ la valeur spécifiée.
- **`@Size(min=x, max=y)`** : Valide la longueur d'un champ texte.
- **`@Regex(pattern="...")`** : Valide un champ contre une expression régulière.
- **`@In({"val1", "val2"})`** : Restreint un champ à un ensemble de valeurs autorisées.
- **`@Phone`** : Valide un numéro de téléphone (7 à 15 chiffres, préfixe `+` optionnel).
- **`@BooleanField`** : Assure qu'un champ est `true` ou `false`.
- **`@DateFormat(pattern="yyyy-MM-dd")`** : Valide une date selon un format spécifié.
- **`@Email`** : Valide une adresse email (par exemple, `user@domain.com`).
- **`@FormName("name")`** : Remplace le nom par défaut d'un champ pour le mappage de formulaires.

---

##  Validation

Le framework fournit une validation robuste via la classe `ValidationUtil`, qui traite les annotations de validation sur les objets modèles. Les erreurs de validation sont collectées dans une instance de `ValidationManager`, qui suit les erreurs et les valeurs des champs.

### Processus détaillé
- **`ValidationUtil.validate(Object obj)`** : Parcourt les champs, vérifie les annotations, et remplit `ValidationManager` avec les erreurs (par exemple, pour `@Required`, vérifie si la valeur est vide) et les valeurs.
- Les erreurs sont indexées par nom de champ (par exemple, "username") et incluent des messages comme "Le champ 'username' est requis.".
- Dans les contrôleurs, injectez `ValidationManager` pour vérifier `hasErrors()` et agir en conséquence.

### Exemple
```java
public class User {
    @Required
    @Size(min=3, max=20)
    private String username;
    
    @Email
    private String email;
    
    @DateFormat(pattern="dd/MM/yyyy")
    private String birthDate;
}
```

Dans un contrôleur :
```java
@Post
@Url("/save-user")
public String saveUser(@RequestObject(name="user") User user, ValidationManager validation) {
    if (validation.hasErrors()) {
        return "user-form.jsp"; // Retourne au formulaire avec les erreurs définies comme attributs de requête (error_username, etc.)
    }
    // Traiter les données valides
    return "success.jsp";
}
```

Le `ValidationManager` stocke les erreurs (indexées par nom de champ) et les valeurs, qui sont ajoutées à la requête pour affichage dans la vue. Les erreurs sont accessibles via les attributs `error_fieldName`. La journalisation dans `ValidationUtil` trace chaque étape de validation, avec des avertissements pour les échecs.

---

##  Authentification

L'authentification est gérée via des niveaux d'utilisateur basés sur la session, avec un support pour les restrictions au niveau des classes et des méthodes.

### Authentification au niveau des modèles
- **`@ClassLevel(value=x)`** : Définit le niveau d'authentification pour une classe modèle, utilisé pour valider les niveaux d'accès des contrôleurs et des méthodes pendant l'initialisation des routes.

### Authentification au niveau des contrôleurs
- **`@AuthController(level=x)`** : Exige un niveau minimum d'utilisateur pour toutes les méthodes du contrôleur. Vérifié dans `RequestHandler.checkAuthorization()`.

### Authentification au niveau des méthodes
- **`@AuthMethod(level=x)`** : Exige un niveau minimum d'utilisateur pour une méthode spécifique. Le niveau le plus élevé entre le contrôleur et la méthode est appliqué.

### Gestion des sessions
- La classe `MySession` encapsule `HttpSession` pour une gestion facile des attributs (`add`, `get`, `delete`).
- Le niveau d'authentification est stocké dans la session sous la clé définie dans `sprint.properties` (par défaut : `userLevel`).
- Dans `RequestHandler`, le niveau utilisateur est récupéré de la session et comparé au niveau requis ; s'il est insuffisant, une `UnauthorizedException` est levée.

### Exemple
```java
@ClassLevel(2)
public class Admin {
    // Classe modèle définissant le niveau 2
}

@Controller
@AuthController(level=2)
@BaseUrl("/admin")
public class AdminController {
    @Get
    @Url("/dashboard")
    @AuthMethod(level=1)
    public ModelView dashboard() {
        ModelView mv = new ModelView("dashboard.jsp");
        mv.addData("message", "Bienvenue sur le tableau de bord Admin");
        return mv;
    }
}

@Post
@Url("/login")
public String login(String username, String password, MySession session) {
    if (authenticate(username, password)) {
        session.add("userLevel", 2); // Définir le niveau utilisateur
        return "redirect:/admin/dashboard";
    }
    return "login.jsp";
}
```

Si le niveau de la session de l'utilisateur est insuffisant, une `UnauthorizedException` est levée, entraînant une page d'erreur 403. La journalisation dans `RequestHandler` débogue les vérifications d'autorisation et signale les échecs en ERROR.

---

##  Gestion des erreurs

Le framework gère les erreurs via la classe `Error`, qui génère des pages d'erreur HTML pour :
- **404** : URL non trouvée (`Mapping` non trouvé dans `RouteRegistry`).
- **403** : Accès non autorisé (`UnauthorizedException`).
- **405** : Méthode HTTP non supportée (`NoSuchMethodException` dans `Mapping.getMethodByVerb()`).
- **500** : Erreurs serveur générales (capturées dans `FrontController.handleRequest()`).

### Processus détaillé
- Dans `FrontController`, les exceptions sont capturées et passées à `Error.displayErrorPage()`, qui définit le statut de la réponse et écrit une page HTML avec le code d'erreur, le titre, le message et des boutons (Réessayer/Retour).
- `PreviousUrlFilter` préserve le contexte pour les redirections potentielles en cas d'erreur.
- Les gestionnaires d'erreurs personnalisés peuvent être implémentés en étendant la logique d'erreur.

### Exemple de gestion d'erreur personnalisée
```java
public class CustomErrorHandler {
    public void handle(HttpServletResponse resp, Exception e) throws IOException {
        // Rendu personnalisé de la page d'erreur, par exemple, journaliser e.getMessage() et rendre une erreur JSON pour les API
    }
}
```

Les erreurs sont journalisées via SLF4J dans `FrontController` et d'autres composants, avec un niveau ERROR pour les problèmes critiques.

---

##  Journalisation


### Niveaux de journalisation par défaut
- **DEBUG** : Traces détaillées, par exemple, résolution des arguments dans `ArgumentResolver`, validation des champs dans `ValidationUtil`.
- **INFO** : Événements clés, par exemple, enregistrement des routes dans `RouteInitializer`, traitement des requêtes dans `RequestHandler`.
- **WARN** : Problèmes comme les échecs de validation dans `ValidationUtil`, avertissements de configuration dans `RequestHandler`.
- **ERROR** : Erreurs critiques, par exemple, exceptions dans `FrontController`, conflits de routes dans `RouteValidator`.

### Exemple de configuration Logback
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/sprint.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
    
    <logger name="mg.sprint" level="DEBUG" />
</configuration>
```

### Détails de l'implémentation
- Chaque classe utilise `private static final Logger logger = LoggerFactory.getLogger(ClassName.class);`.
- Exemples :
  - `ValidationUtil` : Journalise le début/fin de la validation, avertit pour chaque champ invalide.
  - `RequestHandler` : Journalise le chemin/méthode de la requête, les vérifications d'autorisation, les erreurs.
  - `RouteInitializer` : Journalise les classes scannées, les routes enregistrées, les erreurs de validation.
- Cela garantit une traçabilité pour le débogage et la surveillance.

---

##  Aperçu des sprints

Le framework a été développé de manière itérative à travers 20 sprints (0 à 19), chaque sprint se concentrant sur des fonctionnalités incrémentielles. Voici une description très détaillée, avec des références aux éléments de code spécifiques, classes, méthodes et logique.

### Sprint 0 : Réception des requêtes
- **Objectif** : Établir le servlet principal pour gérer les requêtes HTTP entrantes.
- **Implémentations clés** :
  - Création de la classe `FrontController` héritant de `HttpServlet`, annotée avec `@WebServlet(name = "FrontController", urlPatterns = "/*", loadOnStartup = 1)` et `@MultipartConfig` pour le support futur des fichiers.
  - Surcharge de `init(ServletConfig config)` pour lire les paramètres `base-package` et `model-package`, levant une `ServletException` si manquants.
  - Implémentation des méthodes `doGet` et `doPost`, déléguant toutes deux à `handleRequest(req, resp)` pour un traitement unifié.
  - Dans `handleRequest`, capture des exceptions et délégation à un gestionnaire d'erreurs (initialement basique, étendu plus tard).
  - Journalisation basique : Ajout d'un logger SLF4J pour journaliser les URI des requêtes en mode DEBUG.
- **Défis relevés** : Interception de toutes les requêtes (`/*`) ; traitement uniforme GET/POST.
- **Test** : Déploiement sur Tomcat, accès aux URLs, vérification des logs console.

### Sprint 1 : Détection des contrôleurs
- **Objectif** : Scanner les classes de contrôleurs avec des annotations.
- **Implémentations clés** :
  - Introduction de l'annotation `@Controller` dans `mg.sprint.framework.annotation.controller`.
  - Création de la classe `RouteScanner` avec la méthode `scan(String basePackage, ClassLoader classLoader)` : Convertit le package en chemin, utilise `classLoader.getResource()` pour trouver le répertoire, scanne récursivement les fichiers `.class`, charge les classes, et collecte celles annotées avec `@Controller`.
  - Dans `RouteInitializer.initializeRoutes()`, appel de `RouteScanner.scan()` sur `base-package` et `model-package`.
  - Gestion des exceptions comme `ClassNotFoundException` si le package est introuvable.
  - Journalisation : INFO pour le début du scan, DEBUG pour chaque classe trouvée, ERROR si package manquant.
- **Défis relevés** : Scan récursif sans bibliothèques externes ; traitement uniquement des classes annotées.
- **Test** : Ajout de contrôleurs de test ; vérification que le scan les liste correctement.

### Sprint 2 : Mapping des URLs
- **Objectif** : Associer les URLs aux méthodes des contrôleurs.
- **Implémentations clés** :
  - Ajout des annotations `@Url(path)` et `@Get`.
  - Création de la classe `Mapping` avec `controllerClass`, `verbActions` (liste de `VerbAction(verb, methodName)`), et `controllerAuthLevel`.
  - Dans `RouteInitializer.processControllerClass()`, pour chaque méthode annotée avec `@Url`, construction de l'URL complète (base + chemin), détermination du verbe ("GET" par défaut), création/mise à jour de `Mapping`, et enregistrement dans `RouteRegistry` (un `HashMap<String, Mapping>` statique).
  - Ajout des méthodes `RouteRegistry.register()` et `get()`.
  - Journalisation : INFO pour chaque route enregistrée avec chemin, contrôleur, verbe.
- **Défis relevés** : Support de plusieurs méthodes par URL (via verbes) ; prévention des enregistrements en double.
- **Test** : Vérification que `RouteRegistry` contient les mappings corrects.

### Sprint 3 : Exécution dynamique
- **Objectif** : Invoquer les méthodes des contrôleurs à l'exécution.
- **Implémentations clés** :
  - Dans `RequestHandler.processRequest()`, extraction du chemin de la requête, récupération du `Mapping` depuis `RouteRegistry`, obtention de la méthode via `mapping.getMethodByVerb(req.getMethod())`.
  - Instanciation du contrôleur avec `mapping.getControllerClass().getDeclaredConstructor().newInstance()`.
  - Invocation de la méthode avec `method.invoke(controllerInstance, args)` (args initialement vide).
  - Gestion de `NoSuchMethodException` pour les erreurs 405.
  - Journalisation : DEBUG pour l'invocation de méthode, ERROR pour les méthodes manquantes.
- **Défis relevés** : Invocation basée sur la réflexion ; gestion basique des arguments.
- **Test** : Appel de méthodes simples retournant des chaînes ; vérification de la sortie.

### Sprint 4 : Passage de données à la vue
- **Objectif** : Supporter le transfert de données aux vues.
- **Implémentations clés** :
  - Création de la classe `ModelView` avec `url`, `data` (HashMap<String, Object>), `setUrl()`, `addData()`.
  - Dans `ResponseHandler.handleWebResponse()`, si le résultat est `ModelView`, définition des attributs sur la requête et transfert à `mv.getUrl()` via `req.getRequestDispatcher().forward()`.
  - Support des retours de chaînes comme sorties directes ou redirections.
  - Journalisation : TRACE pour les données ajoutées, INFO pour les transferts.
- **Défis relevés** : Différenciation entre les transferts de vue et les réponses directes.
- **Test** : Retour de `ModelView` avec données ; vérification que la JSP reçoit les attributs.

### Sprint 5 : Gestion d'exceptions
- **Objectif** : Gestion robuste des erreurs.
- **Implémentations clés** :
  - Création de la classe `Error` avec `displayErrorPage(resp, e)` : Définit le type de contenu, détermine le code/titre d'erreur selon le type d'exception (par exemple, 404 pour une Exception générale avec "URL non trouvée"), écrit une page HTML avec des boutons.
  - Dans `FrontController.handleRequest()`, capture de toutes les exceptions et appel de l'affichage d'erreur.
  - Ajout des classes `UnauthorizedException` et `ValidationException`.
  - Journalisation : ERROR pour les exceptions capturées avec traces.
- **Défis relevés** : Pages d'erreur personnalisées sans JSP ; messages adaptés à l'i18n.
- **Test** : Induction d'erreurs 404/500 ; vérification de la sortie HTML.

### Sprint 6 : Formulaire → Contrôleur
- **Objectif** : Lier les paramètres de formulaire.
- **Implémentations clés** :
  - Ajout de l'annotation `@RequestParam(value)`.
  - Dans `ArgumentResolver`, utilisation de Paranamer pour obtenir les noms des paramètres, résolution des paramètres simples avec `req.getParameter(name)`, conversion via `ConvertUtil.convertValue()`.
  - Levée d'une `IllegalArgumentException` si le paramètre est manquant.
  - Journalisation : TRACE pour les valeurs résolues.
- **Défis relevés** : Résolution des noms de paramètres sans Java 8+.
- **Test** : Méthodes avec paramètres string/int ; soumission de formulaires.

### Sprint 7 : Paramètre objet
- **Objectif** : Mapper les formulaires aux objets.
- **Implémentations clés** :
  - Ajout de `@RequestObject(name="prefix")`.
  - Dans `ArgumentResolver.resolveRequestObject()`, instanciation de l'objet, définition des champs à partir de `req.getParameter(prefix + "." + fieldName)`, utilisation de `@FormName` pour les noms personnalisés.
  - Intégration de la validation de base (étendue plus tard).
  - Journalisation : DEBUG pour la création/définition d'objets.
- **Défis relevés** : Définition de champs basée sur la réflexion ; conversion de types.
- **Test** : Soumission de formulaires avec paramètres préfixés ; vérification du peuplement de l'objet.

### Sprint 8 : Gestion de session
- **Objectif** : Gérer les sessions facilement.
- **Implémentations clés** :
  - Création de `MySession` encapsulant `HttpSession`, avec `get(key)`, `add(key, obj)`, `delete(key)`.
  - Dans `ArgumentResolver`, si le type du paramètre est `MySession`, retour de `new MySession(req.getSession())`.
  - Journalisation : TRACE pour les récupérations, DEBUG pour les ajouts/suppressions.
- **Défis relevés** : API de session simplifiée ; injection automatique.
- **Test** : Définition/récupération d'attributs dans les contrôleurs.

### Sprint 9 : API REST
- **Objectif** : Supporter les API JSON.
- **Implémentations clés** :
  - Ajout de l'annotation `@RestAPI`.
  - Dans `RequestHandler`, vérification de `@RestAPI` ; dans `ResponseHandler.handleRestApiResponse()`, définition du type de contenu JSON, sérialisation du résultat/`ModelView.data` avec Gson.
  - Journalisation : DEBUG pour les réponses JSON.
- **Défis relevés** : Différenciation des réponses web/REST.
- **Test** : Retour de listes/objets ; vérification de la sortie JSON.

### Sprint 10 : Gestion des verbes HTTP
- **Objectif** : Support complet des verbes.
- **Implémentations clés** :
  - Ajout de `@Post` ; mise à jour de `Mapping` pour ajouter `VerbAction` pour chaque verbe.
  - Dans `RouteInitializer.determineHttpVerb()`, vérification des annotations.
  - Dans `Mapping.getMethodByVerb()`, recherche de la méthode par verbe.
  - Journalisation : TRACE pour la détermination du verbe.
- **Défis relevés** : Même URL, verbes différents.
- **Test** : POST/GET sur la même URL.

### Sprint 11 : Affichage des erreurs
- **Objectif** : Erreurs conviviales.
- **Implémentations clés** :
  - Amélioration de `Error.displayErrorPage()` avec HTML stylé, messages spécifiques pour 403/404/405.
  - Ajout de boutons pour réessayer/retour.
  - Journalisation : INFO pour les affichages de pages d'erreur.
- **Défis relevés** : Pas de CSS externe ; interactivité basique.
- **Test** : Visualisation des pages d'erreur.

### Sprint 12 : Upload de fichier
- **Objectif** : Gérer les fichiers.
- **Implémentations clés** :
  - `@MultipartConfig` sur `FrontController`.
  - Dans `ArgumentResolver`, si le paramètre est `Part`, obtention de `req.getPart(name)`.
  - Levée d'une exception si manquant.
  - Journalisation : DEBUG pour les parties de fichier.
- **Défis relevés** : Requêtes multipart.
- **Test** : Upload de fichiers via formulaires.

### Sprint 13 : Validation (v1)
- **Objectif** : Validation basique des champs.
- **Implémentations clés** :
  - Ajout des annotations comme `@Required`, `@Numeric`, `@Email`, `@DateFormat`.
  - Dans `ValidationUtil.validate()`, vérification de chaque annotation, levée de `ValidationException` en cas d'échec.
  - Intégration dans `ArgumentResolver` pour `@RequestObject`.
  - Journalisation : WARN pour les échecs.
- **Défis relevés** : Correspondance regex/motifs.
- **Test** : Données invalides levant des exceptions.

### Sprint 14 : Validation (v2)
- **Objectif** : Validation sans exception.
- **Implémentations clés** :
  - Création de `ValidationManager` avec `addError(field, msg)`, `addValue(field, val)`, `hasErrors()`.
  - Mise à jour de `ValidationUtil` pour remplir le manager au lieu de lever des exceptions.
  - Dans `RequestHandler`, injection du manager, vérification des erreurs, ajout à la requête comme `error_field`, transfert à l'URL précédente.
  - Journalisation : DEBUG pour les erreurs/valeurs ajoutées.
- **Défis relevés** : Collecte/affichage des erreurs sans arrêt.
- **Test** : Formulaires avec erreurs affichées dans la vue.

### Sprint 15 : Authentification (Méthode)
- **Objectif** : Sécuriser les méthodes.
- **Implémentations clés** :
  - Ajout de `@AuthMethod(level)`.
  - Dans `RequestHandler.checkAuthorization()`, obtention du niveau de la méthode, comparaison avec `userLevel` de la session, levée de `UnauthorizedException` si insuffisant.
  - Journalisation : DEBUG pour les vérifications, ERROR pour les refus.
- **Défis relevés** : Vérifications basées sur la session.
- **Test** : Accès avec/sans niveau suffisant.

### Sprint 16 : Authentification (Classe)
- **Objectif** : Sécuriser les classes/modèles.
- **Implémentations clés** :
  - Ajout de `@AuthController(level)`, `@ClassLevel(value)`.
  - Dans `RouteInitializer`, scan des modèles pour les niveaux, stockage dans `validLevels`, validation des niveaux des contrôleurs/méthodes.
  - Dans `Mapping`, stockage de `controllerAuthLevel` ; vérification du max entre classe/méthode.
  - Journalisation : DEBUG pour la validation des niveaux.
- **Défis relevés** : Validation inter-packages.
- **Test** : Niveaux invalides levant des exceptions à l'init.

### Sprint 17 : Utilitaires
- **Objectif** : Fonctionnalités d'aide.
- **Implémentations clés** :
  - Ajout de `@BaseUrl`, concaténé dans `RouteInitializer`.
  - Création de `StringUtil` avec méthodes comme `removeLastAt`, `addInBeginIfDont` (utilisées en interne).
  - Ajout de `ConvertUtil` pour les conversions de types.
  - Journalisation : TRACE pour les appels d'utilitaires.
- **Défis relevés** : Manipulations de chaînes pour URLs/params.
- **Test** : URLs de base dans les routes.

### Sprint 18 : Suivi de l'URL précédente
- **Objectif** : Préserver le contexte de navigation et  Journalisation complète.
- **Implémentations clés** :
  - Création de `PreviousUrlFilter` implémentant `Filter` : Dans `doFilter`, mise à jour de la session avec `previousUrl`, `previousMethod`, `currentUrl`, `currentMethod` ; ignoré pour les ressources statiques/AJAX.
  - Ajout de `SessionManager` pour gérer les mises à jour de l'URL précédente.
  - Utilisation dans la gestion des erreurs pour transférer à l'URL précédente en cas d'erreurs de validation.
  - Journalisation : INFO pour les URLs précédentes, DEBUG pour les mises à jour.
   - Intégration de SLF4J/Logback dans toutes les classes (par exemple, `logger = LoggerFactory.getLogger(...)`).
  - Ajout de logs : DEBUG pour les traces (par exemple, définition de champs), INFO pour les événements (par exemple, init des routes), WARN pour les problèmes (par exemple, données invalides), ERROR pour les échecs (par exemple, exceptions).
  - Chargement de la configuration depuis `sprint.properties` (niveau de journalisation).
  - Support de `logback.xml` pour les appenders avancés.
  - Journalisation : Auto-journalisation de l'initialisation.
- **Défis relevés** : Suivi basé sur un filtre sans interférer avec les ressources statiques et  Niveaux granulaires sans impact sur les performances.
- **Test** : Navigation entre pages ; vérification des attributs de session ,  Déploiement,vérification des logs pour les requêtes/validations/erreurs.

##  Exemples

### Contrôleur basique
```java
@Controller
@BaseUrl("/products")
public class ProductController {
    
    @Get
    @Url("/list")
    public ModelView listProducts() {
        ModelView mv = new ModelView("products/list.jsp");
        mv.addData("products", productService.getAll());
        return mv;
    }
    
    @Post
    @Url("/save")
    public String saveProduct(@RequestObject(name="product") Product product, 
                             ValidationManager validation) {
        if (validation.hasErrors()) {
            return "products/form.jsp";
        }
        productService.save(product);
        return "redirect:/products/list";
    }
}
```

### API REST
```java
@Controller
@BaseUrl("/api/users")
public class UserApiController {
    
    @Get
    @Url("/")
    @RestAPI
    public List<User> getAllUsers() {
        return userService.getAll();
    }
    
    @Post
    @Url("/")
    @RestAPI
    public User createUser(@RequestObject User user) {
        return userService.create(user);
    }
}
```

---

##  Architecture

Le framework suit une architecture MVC modulaire :

```
FrontController (Servlet)
├── RouteInitializer
│   ├── RouteScanner (scan des packages)
│   └── RouteValidator (vérifications des conflits)
├── RequestHandler
│   ├── ArgumentResolver (liaison des paramètres)
│   ├── ResponseHandler (rendu vue/JSON)
│   └── SessionManager (gestion des URLs précédentes)
├── ValidationManager (collecte des erreurs)
├── Error (pages d'erreur HTML)
└── MySession (encapsulation de session)
```

### Flux de requête
1. `PreviousUrlFilter` met à jour le contexte de la session.
2. `FrontController` délègue à `RequestHandler`.
3. Correspondance de la route, résolution des arguments, validation, autorisation, invocation de la méthode, gestion de la réponse.
4. Les erreurs sont rendues via `Error`.

La journalisation traverse toutes les couches.

---

##  Améliorations futures

Planifiées :
- Support des WebSockets.
- Mise en cache.
- Internationalisation (i18n).
- Tests automatisés.
- Documentation Swagger.

---

## Auteur

Développé par **ETU002756**