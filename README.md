# BACK-OFFICE (Spring Boot + Maven + PostgreSQL)

Initialisation du backend avec:
- Spring Boot 3
- Maven
- PostgreSQL

## Prerequis
- Java 17+
- Maven 3.9+
- Docker (optionnel, pour lancer PostgreSQL rapidement)

## Demarrage rapide

1. Lancer PostgreSQL via Docker:

```bash
docker compose up -d postgres
```

2. Lancer l'application:

```bash
mvn spring-boot:run
```

3. Verifier l'etat de l'application:

```bash
curl http://localhost:8080/actuator/health
```

## Configuration DB

Les variables d'environnement suivantes peuvent surcharger les valeurs par defaut:

- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/backoffice_db`)
- `SPRING_DATASOURCE_USERNAME` (default: `backoffice_user`)
- `SPRING_DATASOURCE_PASSWORD` (default: `backoffice_pwd`)
- `SERVER_PORT` (default: `8080`)

## Build

```bash
mvn clean package
```

## Prochaine etape suggeree

Creer les modules metier (auth, users, roles, dossiers visa, etc.) selon le besoin fonctionnel.
