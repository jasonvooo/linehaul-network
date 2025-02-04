# Line Haul Network

## Requirements

Implement an HTTP server with **two endpoints**:

1. **Update the linehaul network** (POST `/update`)
2. **Query travel times** (GET `/route`)

## Architecture

- **Java 21** (Latest LTS release)
- **Spring Boot 3** (For rapid API development using MVC pattern)
- **Junit** (Unit tests)
- **OpenAPI 3** (For API specification and automatic code generation)
- **JGraphT** (For efficient graph processing and Dijkstra's shortest path algorithm)

## Notes

- **Java 21** is used as it is the latest **LTS** release, ensuring long-term support and modern features.
- **Spring Boot** simplifies setting up a web server and enables API development following the **MVC pattern**.
- **OpenAPI** is used to define the API and generate base API and model classes via OpenAPI generators.
- **JGraphT** is leveraged for its built-in **Dijkstra's shortest path algorithm**.
- Since there is no explicit **CRUD functionality** for `/update`, it is assumed that the update operation will
  **replace all existing paths** with those provided in the request payload. This can be modified later to support CRUD
  operations.
- **Data storage is handled in-memory** using a singleton inside `RouteService`, as persistent storage was not a
  requirement.
- Unit tests and integration tests written to cover edge cases with input validation and errors

## How to Run

### **Prerequisites**

- Ensure **Java 21** is installed (`java -version` should output `21`).
- Ensure **Gradle** is installed (`gradle -version`). If Gradle is not installed, you can use the Gradle wrapper (
  `./gradlew`).

### **Run the Application**

```sh
./gradlew bootRun
```

This will start the Spring Boot application on **http://localhost:8080**.

### **Build the Application**

```sh
./gradlew build
```

This will compile and package the application.

### **Run Tests**

```sh
./gradlew test
```

This will execute all unit and integration tests.

---