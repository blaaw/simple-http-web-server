# Simple HTTP Server

A minimal HTTP server written in Java, built to learn the basics of **network programming** and the use of **reader objects** (`InputStreamReader`, `BufferedReader`).

> This is a learning project. It is intentionally small.

## What it does

The server listens for incoming HTTP connections on port `8080`. For every request it:

1. **Reads** the client's HTTP request line-by-line using reader objects.
2. **Prints** each header line to the console.
3. **Responds** with a simple `HTTP/1.1 200 OK` message that includes the current date.
4. **Closes** the client socket and keeps running to accept the next connection.

## The reader objects

The interesting part of this project is how the incoming request is read. Raw bytes arrive through the socket's `InputStream`, and they are wrapped in two reader objects that turn those bytes into characters and make line-by-line reading convenient:

```java
InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
String line = bufferedReader.readLine();
```

- **`InputStreamReader`** — a *bridge* between byte streams and character streams. It reads bytes from the socket and decodes them into characters.
- **`BufferedReader`** — wraps the `InputStreamReader` and provides buffered, character-based reading. Its `readLine()` method is what makes it easy to consume the HTTP request one line at a time, until an empty line (the end of the headers) is reached.

## Running it

Compile and run the server:

```bash
javac -d bin src/SimpleHTTPServer.java
java -cp bin SimpleHTTPServer
```

Then open a browser or use `curl` to send a request:

```bash
curl http://localhost:8080
```

You should see a response like:

```
Hi, this is my Java server and today's date is 2026-10-15
```
