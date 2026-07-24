# Simple Chat Platform

## Overview

The Simple Chat Platform is a real-time web application that allows users to join chat rooms, exchange messages
instantly, and leave conversations.

The solution consists of two applications:

- **simple-chat-platform-service** - Spring Boot backend exposing REST APIs and WebSocket endpoints.
- **simple-chat-platform-ui** - Angular frontend providing the user interface and real-time chat experience.

---

## Project Structure

```text
simple-chat-platform
|
├── README.md
|
├── postman
|   └── simple-chat-platform.postman_collection.json
|
├── simple-chat-platform-service
|   ├── README.md
|   ├── pom.xml
|   └── src
|
└── simple-chat-platform-ui
    ├── README.md
    ├── package.json
    └── src
```

---

# Solution Architecture

The application follows a client-server architecture with real-time communication over WebSockets using the STOMP
protocol.

```text
+-------------------+
|   Angular Client  |
|-------------------|
| Join Chat Room    |
| Send Messages     |
| Receive Messages  |
+---------+---------+
          |
          |
          | HTTP (Join Room)
          | WebSocket/STOMP
          |
+---------v---------+
|  Spring Boot API  |
|-------------------|
| REST Controller   |
| WebSocket Config  |
| Message Controller|
| ChatRoomService   |
+---------+---------+
          |
          |
+---------v---------+
| In-Memory Storage |
|-------------------|
| Active Rooms      |
| Connected Users   |
| Room Membership   |
+-------------------+
```

---

# Technology Stack

## Backend

- Java 21
- Spring Boot 3
- Spring WebSocket
- Spring Messaging
- Maven
- JUnit 5
- Mockito

## Frontend

- Angular
- TypeScript
- RxJS
- STOMP.js
- SockJS

---

# Running the Application

## Backend

See the backend documentation:

[Simple Chat Platform Service README](./simple-chat-platform-service/README.md)

---

## Frontend

See the frontend documentation:

[Simple Chat Platform UI README](./simple-chat-platform-ui/README.md)

---

# API Testing (Postman)

A Postman collection is provided to test the backend REST APIs.

Download/import the collection:

[Postman Collection](postman/simple-chat-platform.postman_collection.json)

The collection includes:

- Join chat room
- Retrieve available chat rooms

## Import Collection

1. Open Postman.
2. Click **Import**.
3. Select the Postman collection link above.
4. Start the backend service.
5. Execute the requests.

---

# Application Flow

1. User enters a username.

2. Frontend validates that the username is provided.

3. Frontend establishes a WebSocket/STOMP connection.

4. Frontend selects the default chat room (`general`).

5. Frontend sends a REST request to join the default room.

6. Backend validates that the default room exists.

7. Backend adds the user to the room membership.

8. Backend publishes a JOIN event.

9. Backend broadcasts the JOIN event to users subscribed to the room.

10. Users exchange messages through WebSocket/STOMP.

11. Backend broadcasts messages to all users subscribed to the room.

12. When a user leaves, backend removes the user from the room.

13. Backend broadcasts a LEAVE event.
---
