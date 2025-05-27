import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpServerWithJsonHelper {
    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 Server running at http://localhost:" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
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

            String line;
            while ((line = in.readLine()) != null && !line.isEmpty());

            // Routing
            if (method.equals("GET") && path.equals("/")) {
                sendResponse(out, 200, "text/plain", "Welcome to homepage!");
            } else if (method.equals("GET") && path.equals("/api/user")) {
                String json = """
                {
                  "username": "nhan_dep_trai",
                  "role": "founder",
                  "verified": true
                }
                """;
                sendResponse(out, 200, "application/json", json);
            } else {
                sendResponse(out, 404, "text/plain", "404 - Not Found");
            }

            socket.close();
        } catch (IOException e) {
            System.err.println("❗ Error: " + e.getMessage());
        }
    }

    /**
     * Send HTTP response with headers and body.
     */
    private static void sendResponse(BufferedWriter out, int status, String contentType, String body) throws IOException {
        String statusText = switch (status) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };

        String date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                          .format(new Date());

        String headers = "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                         "Content-Type: " + contentType + "; charset=UTF-8\r\n" +
                         "Content-Length: " + body.getBytes().length + "\r\n" +
                         "Connection: close\r\n" +
                         "Access-Control-Allow-Origin: *\r\n" +
                         "Date: " + date + "\r\n" +
                         "\r\n";

        out.write(headers + body);
        out.flush();
    }
}
