# Documentation Angular - Visa Front

## 1. Objectif du projet

Ce frontend Angular sert a:
- rechercher des demandes par numero (passeport ou demande),
- afficher la liste des demandes trouvees,
- afficher les details d'une demande,
- afficher une page QR de suivi.

Le backend Spring expose les APIs sous `http://localhost:8080/api/demandes`.

## 2. Initialisation du projet Angular

Le projet `visa-front` est initialise avec Angular moderne (standalone components):
- pas de `NgModule` applicatif principal,
- routage via `provideRouter(...)`,
- bootstrap via `bootstrapApplication(...)`.

Fichiers de base importants:
- `src/main.ts`: point d'entree frontend,
- `src/app/app.ts`: composant racine,
- `src/app/app.config.ts`: providers globaux,
- `src/app/app.routes.ts`: routes.

## 3. Structure mise en place

Dans `src/app/components/`:
- `demande-search/`: page de recherche (page d'accueil),
- `demande-list/`: liste des demandes,
- `demande-details/`: details d'une demande,
- `demande-qr/`: page QR frontend.

Dans `src/app/services/`:
- `demande.ts`: appels HTTP backend.

## 4. Configuration du routage

Routes configurees dans `src/app/app.routes.ts`:
- `/` -> recherche,
- `/demandes` -> liste,
- `/details/:id` -> details,
- `/qr/:id` -> page QR.

Exemple:
- `http://localhost:4200/qr/2`

## 5. Service API

Service `DemandeService`:
- `getDemandesByNumero(numero)` -> `GET /api/demandes/{numero}`
- `getDemandeDetails(id)` -> `GET /api/demandes/details/{id}`

Ce service centralise la communication HTTP avec le backend.

## 6. Flux ecran par ecran

### 6.1 Page Recherche

Composant: `demande-search`.

Logique:
1. L'utilisateur saisit un numero.
2. Au clic sur Rechercher, navigation vers `/demandes?numero=...`.

### 6.2 Page Liste

Composant: `demande-list`.

Logique:
1. Lecture du query param `numero`.
2. Appel API `getDemandesByNumero(numero)`.
3. Affichage du tableau avec actions:
   - Voir details,
   - QR code.

### 6.3 Page Details

Composant: `demande-details`.

Logique:
1. Lecture de `id` dans l'URL.
2. Appel API `getDemandeDetails(id)`.
3. Affichage des informations demande/demandeur/statuts.

### 6.4 Page QR (frontend)

Composant: `demande-qr`.

Logique:
1. Lecture de `id`.
2. Appel `getDemandeDetails(id)` pour recuperer l'historique.
3. Construction d'un texte QR (date + statut).
4. Affichage d'une image QR (service image externe) + contenu texte.

## 7. Comment le QR code est genere

Deux usages QR existent:

### 7.1 QR pour le suivi frontend (`/qr/:id`) - cote backend Thymeleaf

Dans le backend, une page intermediaire `forms/demande-qr.html` affiche un QR code image.

Principe:
1. Construire l'URL cible frontend: `APP_FRONTEND_BASE_URL + /qr/{id}`.
2. Encoder cette URL.
3. Construire l'URL image:
   - `https://api.qrserver.com/v1/create-qr-code/?size=260x260&data=<url_encodee>`
4. Afficher l'image dans la page.

Resultat:
- scanner le QR ouvre directement la page Angular `http://...:4200/qr/id`.

### 7.2 QR contenu statut (page Angular `demande-qr`)

Dans le composant Angular, on genere aussi un contenu texte base sur l'historique des statuts.

Exemple de contenu:
- `27/04/2026 : Dossier cree`
- `...`

Ce contenu est passe a un generateur d'image QR pour affichage.

## 8. Redirection backend apres creation d'une nouvelle demande

Flux ajoute cote backend:
1. Creation d'une nouvelle demande (`POST /demandes/nouvelle`).
2. Recuperation de l'ID de la demande creee.
3. Redirection vers `/demandes/qr/{id}` (page QR intermediaire).
4. Cette page redirige automatiquement vers `/demandes/liste` apres quelques secondes.

Ainsi, l'utilisateur voit d'abord le QR puis revient a la liste.

## 9. Bouton QR dans la liste backend

Dans `templates/lists/demande-liste.html`, section Actions:
- ajout d'un bouton `QR code` pointant vers `/demandes/qr/{id}`.

Ce bouton ouvre la page backend qui affiche le QR de redirection frontend.

## 10. Compatibilite mobile du QR

Pour que le QR fonctionne sur mobile, `localhost` ne suffit pas (localhost du telephone != localhost du PC).

Utiliser une URL frontend accessible sur le reseau local:
- variable: `APP_FRONTEND_BASE_URL`
- exemple: `http://192.168.1.10:4200`

Ensuite, le QR encode cette URL reseau, donc un mobile peut l'ouvrir.

## 11. Commandes utiles

Frontend:
- `npm install`
- `npm start`

Backend:
- `mvn spring-boot:run`

## 12. Resume final du travail

Depuis le debut, le flux complet est:
1. Initialisation Angular et composants standalone.
2. Creation des pages recherche/liste/details/qr.
3. Connexion du front aux APIs backend.
4. Correction CORS backend pour autoriser le front.
5. Enrichissement des statuts cote API (`statusLabel`).
6. Mise en place QR backend intermediaire apres creation.
7. Ajout bouton QR dans la liste.
8. Support mobile via URL frontend configurable.

Ce setup te donne une base claire pour apprendre Angular progressivement:
- routing,
- services HTTP,
- composants standalone,
- navigation entre pages,
- integration avec un backend Spring.
