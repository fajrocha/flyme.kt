# Flyme

## Introduction

Just a small project for me to mess around with reactivity using _Kotlin_ and 
[_Spring WebFlux_](https://docs.spring.io/spring-framework/reference/web/webflux.html).

## Key frameworks and libraries 📕

- [Spring Boot 3](https://spring.io/projects/spring-boot);
- [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) for data access.
- [Flyway](https://github.com/flyway/flyway) for database migrations.
- [Test Containers](https://testcontainers.com/) for integration testing, both for the _PostgreSQL DB_ and to mock the
  [external REST API](https://airportgap.com/) using _MockServer Module_.
- [Kluent](https://markusamshove.github.io/Kluent/) for fluent and readable assertions.

## Setup

### Postgres DB

Run this on the same directory as `docker-compose.yml`:

```shell
docker compose up -d
```

### Setting up the external API token

Get a token on [link](https://airportgap.com/tokens/new).

These credentials should be stored in an `secrets.yml` file side by side with `application.yml` 
(the import is configured on the latter):

```yaml
airport-gap:
  api-token: apiTokenGoesHere
```
