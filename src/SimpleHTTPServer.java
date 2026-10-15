import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
public class SimpleHTTPServer {
    public static void main(String[] args) throws Exception {
        final ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Listening on port 8080...");
        while (true) {
            //run the server forever
            try (Socket clientSocket = serverSocket.accept()) {
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + java.time.LocalDate.now();
                clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            }
            //1. Read HTTP request from client
            //2. Prepare HTTP response
            //3. Send HTTP response to client
            //4. Close client socket 
        }
    }
}
