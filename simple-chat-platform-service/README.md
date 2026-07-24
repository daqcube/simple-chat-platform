# Simple Chat Platform Service

## Overview

The Simple Chat Platform Service is a Spring Boot backend application responsible for managing chat rooms and real-time messaging.

The backend provides:

- REST APIs for chat room operations.
- WebSocket endpoints for real-time communication.

---

# Technology Stack

- Java 21
- Spring Boot 3
- Spring WebSocket
- Spring Messaging
- Maven
- JUnit 5
- Mockito

---

# Backend Architecture

## Controllers

### ChatRoomController

Handles REST operations:

- Joining chat rooms
- Listing chat rooms

### ChatMessageController

Handles WebSocket operations:

- Sending messages
- Leaving chat rooms

---

## Service Layer

### ChatRoomService

Contains business logic for:

- Creating chat rooms
- Managing users
- Publishing messages
- Managing room membership

---

## Storage

The application uses in-memory storage for:

- Active chat rooms
- Connected users
- Room membership

No database is required.

---

# Prerequisites

Install:

- Java 21+
- Maven 3.9+

---

# Build Application

Using Maven Wrapper:

```bash
./mvnw clean install
```

or:

```bash
mvn clean install
```

---

# Run Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

or:

```bash
mvn spring-boot:run
```

---

The backend runs on:

```text
http://localhost:8080
```

WebSocket endpoint:

```text
ws://localhost:8080/ws
```

---

# Running Tests

```bash
./mvnw test
```

or:

```bash
mvn test
```

---

# Configuration

Application configuration:

```text
src/main/resources/application.yml
```

---