Cerinta: Notificarea clienților web (observer) folosind websockets când se adaugă/șterge/modifică o resursă. Trebuie să modificați temele 6 și 7 (Servicii REST și Client Web pentru servicii REST)
----
Se folosesc WebSockets si se implementeaza practic patternul Observer intre server si clienti. 
  - Am activat WebSockets in Spring Boot: am creat o configuratie WebSocketConfig care deschide un canal de comunicare;
  - Am creat un FlightWebSocketHandler:
      - serverul pastreaza o lista cu toti clientii conectati prin WebSocket
      - cand apare o modificare, serverul trimite un mesaj la toti clientii
  - Se modifica controllerul REST (FlightController), dupa fiecare operatie serverul trimite un mesaj WebSocket catre toti clientii
  - Actualizam clientul (aplicatia React):
      - când clientul primește un mesaj de la server, refolosește fetchFlights() ca să reîncarce automat lista zborurilor.

Toți utilizatorii aplicației web văd în timp real modificările asupra zborurilor, fără a fi nevoie să reîncarce manual pagina.
