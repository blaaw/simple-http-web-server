# Simple Web Server

A minimal web server written in Java, built to learn the basics of **network programming**, the use of **reader objects** (`InputStreamReader`, `BufferedReader`), and how a server **routes a request to a static file**.

> This is a learning project. It is intentionally small.

## What it does

The server listens for incoming HTTP connections on port `8080`. For every request it:

1. **Reads** the client's HTTP request line-by-line using reader objects.
2. **Parses** the request line to find out which path the client wants.
3. **Resolves** that path to a file inside the `public/` folder.
4. **Serves** that file as the response (or `404.html` if it does not exist).
5. **Closes** the client socket and keeps running to accept the next connection.

## The reader objects

The interesting part of this project is how the incoming request is read. Raw bytes arrive through the socket's `InputStream`, and they are wrapped in two reader objects that turn those bytes into characters and make line-by-line reading convenient:

```java
InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
String line = bufferedReader.readLine();
```

- **`InputStreamReader`** — a *bridge* between byte streams and character streams. It reads bytes from the socket and decodes them into characters.
- **`BufferedReader`** — wraps the `InputStreamReader` and provides buffered, character-based reading. Its `readLine()` method is what makes it easy to consume the HTTP request one line at a time, until an empty line (the end of the headers) is reached.

## The socket and the server socket

Everything above happens inside a connection, so before any reading can take place the server has to open one. That is the job of two classes: `ServerSocket` listens for clients, and `Socket` represents each accepted connection.

```java
final ServerSocket serverSocket = new ServerSocket(8080);
final Socket clientSocket = serverSocket.accept();
```

- **`ServerSocket`** — the server-side listener. It is bound to a port (here `8080`) and waits for incoming connections. It does not carry any data itself; it just knows how to accept clients.
- **`accept()`** — a blocking call. The server stops here until a client connects, then returns a `Socket` for that connection. This is why it is called inside the `while (true)` loop: each call handles one client, and the loop keeps the server alive for the next one.
- **`Socket`** — one established connection to a single client. From it the server gets the input stream to read the request and the output stream to write the response.

The relationship is one-to-many: a single `ServerSocket` can produce many `Socket` instances over its lifetime, one per client. In this project each `Socket` is used to read the request and send the response, and then closed with `clientSocket.close()`.

## Parsing the request

The first line of an HTTP request is called the *request line*, and it is what tells the server what the client wants:

```
GET /about.html HTTP/1.1
│    │           └─ HTTP version
│    └─ the path the client is asking for
└─ the method
```

The server reads that first line, splits it by spaces, and keeps the path, which is the part that decides which page to serve.

## Serving static files

The requested URL is turned into a file inside the `public/` folder, which acts as the document root:

```java
Path rootPath = Paths.get("public");
if (path.equals("/")) path = "/index.html";
Path file = rootPath.resolve(path.substring(1)).normalize();
```

- `/` is mapped to `public/index.html`.
- `/about.html` is mapped to `public/about.html`.
- `.resolve(...)` joins the root with the requested path, and `.normalize()` cleans up any `.` or `..` in it.
- The body of the response is read from the file with `Files.readAllBytes(file)`.

The folder layout is:

```
public/
├── index.html      →  /
├── about.html      →  /about.html
└── 404.html        →  any missing route
```

If the requested file does not exist, the server responds with `404.html` instead of crashing.

## Building the response

The response is a status line, a few headers, a blank line, and then the body:

```java
byte[] responseBody = Files.readAllBytes(file);
String responseHead = "HTTP/1.1 " + status + "\r\n"
    + "Content-Type: text/html; charset=UTF-8\r\n"
    + "Content-Length: " + responseBody.length + "\r\n"
    + "\r\n";
```

- **`HTTP/1.1 200 OK`** — the status line. The space between the code and the reason phrase matters: a malformed status line such as `200OK` is rejected by browsers, which then show nothing.
- **`Content-Type`** — tells the browser that the body is HTML.
- **`Content-Length`** — tells the browser how many bytes to expect, so it knows where the body ends.

The head and the body are written to the socket's output stream, and the socket is closed.

## Running it

Compile and run the server from the project root (the `public/` folder is relative to the directory where `java` is launched):

```bash
javac -d bin src/SimpleHTTPServer.java
java -cp bin SimpleHTTPServer
```

Then open a browser or use `curl`:

```bash
curl http://localhost:8080/            # index.html
curl http://localhost:8080/about.html  # about.html
curl http://localhost:8080/noexiste    # 404.html
```
