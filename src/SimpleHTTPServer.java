import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
public class SimpleHTTPServer {
    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Listening on port 8080...");
            while (true) {
                //run the server forever
                final Socket clientSocket = serverSocket.accept();
                //1. Read HTTP request from client
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (!line.isEmpty()) {
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }
                //2. Prepare HTTP response
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n Hi, this is my Java server and today's date is " + java.time.LocalDate.now();
                //3. Send HTTP response to client
                clientSocket.getOutputStream()
                    .write(httpResponse.getBytes("UTF-8"));
                //4. Close client socket 
                clientSocket.close();
            }
        }
    }
}
