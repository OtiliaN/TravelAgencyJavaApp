## Real-time Updates with WebSockets

WebSockets are used to implement the Observer pattern between the server and connected web clients.

- WebSockets were enabled in the Spring Boot application by creating a `WebSocketConfig` class that opens a communication channel.
- A `FlightWebSocketHandler` was created:
  - The server keeps a list of all clients connected via WebSocket.
  - Whenever a change occurs (add, delete, or update), the server sends a message to all connected clients.
- The REST controller (`FlightController`) was updated to send a WebSocket message after each operation that modifies flights.
- The React client was also updated:
  - When a message is received from the server, it automatically calls `fetchFlights()` to reload the list of flights in real-time.

All users of the web application can see changes to the flight data in real-time, without needing to manually refresh the page.
