# Simple Chat Platform UI

## Overview

The Simple Chat Platform UI is an Angular frontend application that provides the user interface for the real-time chat platform.

The frontend communicates with the backend using:

- REST APIs
- WebSocket/STOMP communication

---

# Technology Stack

- Angular
- TypeScript
- RxJS
- STOMP.js
- SockJS

---

# Features

The application provides:

- Joining chat rooms
- Sending messages
- Receiving real-time messages
- Viewing connected users

---

# Prerequisites

Install:

- Node.js 22+
- npm

---

# Install Dependencies

Run:

```bash
npm install
```

---

# Run Application

Start development server:

```bash
npm start
```

or:

```bash
ng serve
```

---

The frontend runs on:

```text
http://localhost:4200
```

---

# Application Structure

Main components:

- Join Room Component
- Chat Window Component
- User List Component

Services:

- WebSocket/STOMP connection management
- Chat communication
- Backend API integration

---

# Running Tests

```bash
npm test
```

---

# Configuration

Environment configuration:

```text
src/environments/
```

---
