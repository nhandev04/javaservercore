import java.io.*;
import java.net.*;

public class HttpServerWithRouting {
    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 Server running at http://localhost:" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start(); // chạy đa luồng nhẹ
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            System.out.println("📥 Request: " + requestLine);
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Bỏ qua headers
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                System.out.println("📄 Header: " + line);
            }

            String responseBody;
            String contentType = "text/plain";

            if (method.equals("GET") && path.equals("/")) {
                responseBody = "Welcome to the homepage!";
            } else if (method.equals("GET") && path.equals("/about")) {
                responseBody = "This is the about page.";
            } else if (method.equals("GET") && path.equals("/api/data")) {
                responseBody = """
                    {
                        "name": "Nhân đẹp trai",
                        "message": "Hello from /api/data"
                    }
                    """;
                contentType = "application/json";
            } else {
                responseBody = "404 Not Found";
            }

            String response = "HTTP/1.1 200 OK\r\n" +
                              "Content-Type: " + contentType + "\r\n" +
                              "Content-Length: " + responseBody.getBytes().length + "\r\n" +
                              "\r\n" +
                              responseBody;

            out.write(response);
            out.flush();
            socket.close();
        } catch (IOException e) {
            System.err.println("❗ Error: " + e.getMessage());
        }
    }
}
