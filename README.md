
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
