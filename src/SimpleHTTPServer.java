import java.net.ServerSocket;
import java.net.Socket;
public class SimpleHTTPServer {
    public static void main(String[] args) throws Exception {
        final ServerSocket server = new ServerSocket(8080);
        System.out.println("Listening on port 8080...");
        while (true) {
           //run the server forever
           final Socket client = server.accept();
           //1. Read HTTP request from client
           //2. Prepare HTTP response
           //3. Send HTTP response to client
           //4. Close client socket 
        }
    }
}
