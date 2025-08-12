
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