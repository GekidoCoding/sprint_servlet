
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
