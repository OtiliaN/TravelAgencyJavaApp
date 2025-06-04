# Travel Agency Java App

A modular travel agency system implemented in Java, built step-by-step to explore and experiment with various software development technologies and architectural patterns. The project evolved from basic client-server communication to a modern RESTful architecture with a web frontend and real-time updates via WebSockets

## Overview

Tourism agencies use a central flight booking system to purchase tickets for their clients. Each employee uses a desktop or web client to:
- Log in to the system
- Search for available flights by destination and departure date
- Book tickets for clients
- View updated flight information in real-time after bookings
- Log out

## Tech Stack

- **Java (Gradle)** – main backend and client logic
- **JavaFX** – desktop GUI
- **SQLite** – relational database
- **Hibernate (ORM)** – data persistence
- **Spring Boot** – REST API layer
- **React** – web client UI
- **WebSockets** – real-time communication
- **Protocol Buffers (ProtoBuf)** – for cross-platform communication (Java client & C# server)

## Project Phases

### Phase 1 – Java Client-Server with Custom RPC
- Implemented client-server architecture using a custom RPC protocol
- Structured project into multiple Gradle modules (Client, Networking, Server, Services, Persistence, Model)

### Phase 2 – Cross-Platform Architecture (Java Client + C# Server)
- Reimplemented communication layer using **gRPC with Protocol Buffers**
- Java client communicates with a C# server
- [C# Server Repo](https://github.com/OtiliaN/TravelAgencyCSharpApp)

### Phase 3 – ORM with Hibernate
- Introduced Hibernate ORM for `Flight` and `Agent` entities
- Replaced classic repositories with `HibernateRepositoryImpl`
- Created and populated new tables via entity mapping

### Phase 4 – REST API (Spring Boot)
- Exposed CRUD operations for `Flight` entity:
  - `GET /org/flights`
  - `GET /org/flights/{id}`
  - `POST /org/flights`
  - `PUT /org/flights/{id}`
  - `DELETE /org/flights/{id}`
- Tested with:
  - Postman
  - Java test client
  - C# test client using `HttpClient` 

### Phase 5 – React Client for REST Services
- Implemented a web-based UI using React
- Features:
  - Display all flights
  - Add, update, and delete flights via REST calls

### Phase 6 – Real-Time Updates with WebSockets
- Integrated WebSocket support to notify all clients when flight data is updated
- Implemented the **Observer** pattern between server and all connected clients

>  This is the current version on the `main` branch.

## What I Learned

- Structuring large Java projects into modules
- Implementing layered architecture (MVC + Services + Persistence)
- Working with REST APIs and asynchronous communication
- Creating cross-platform systems with ProtoBuf
- Using WebSockets for real-time client updates
- Building responsive UIs with JavaFX and React
