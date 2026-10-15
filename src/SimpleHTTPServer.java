import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

                String[] partsRequest = line.split(" ");
                //String method = partsRequest[0];
                String path = partsRequest[1];

                while (!line.isEmpty()) {
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }
                //2. Prepare HTTP response
                Path rootPath = Paths.get("public");
                if (path.equals("/")) path = "/index.html";

                Path file = rootPath.resolve(path.substring(1)).normalize();

                String status;
                if (Files.isRegularFile(file)) {
                    status = "200 OK";
                } else {
                    status = "404 Not Found";
                    file = rootPath.resolve("/404.html".substring(1)).normalize();
                }

                byte[] responseBody = Files.readAllBytes(file);
                String responseHead = "HTTP/1.1 "+ status + "\r\n"
                    + "Content-Type: text/html; charset=UTF-8\r\n"
                    + "Content-length: " + responseBody.length + "\r\n"
                    + "\r\n";
                //3. Send HTTP response to client
                 clientSocket.getOutputStream()
                    .write(responseHead.getBytes(StandardCharsets.UTF_8));
                clientSocket.getOutputStream()
                    .write(responseBody);
                //4. Close client socket 
                clientSocket.close();
            }
        }
    }
}
