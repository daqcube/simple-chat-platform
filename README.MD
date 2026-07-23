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

## Start Backend

```bash
cd simple-chat-platform-service
```

Follow:

```text
simple-chat-platform-service/README.md
```

---

## Start Frontend

```bash
cd simple-chat-platform-ui
```

Follow:

```text
simple-chat-platform-ui/README.md
```

---

# Application Flow

1. User enters a username and room ID.
2. Frontend sends a request to join a chat room.
3. Backend creates the room if it does not exist.
4. A WebSocket/STOMP connection is established.
5. User subscribes to the room topic.
6. Messages are exchanged in real time.
7. Backend broadcasts messages to connected users.
8. Leave events are broadcast when users exit.

---
