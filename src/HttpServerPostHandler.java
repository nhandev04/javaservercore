import java.io.*;
import java.net.*;
import java.util.*;

public class HttpServerPostHandler {
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

            Map<String, String> headers = new HashMap<>();
            String line;
            int contentLength = 0;

            // Đọc headers
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(":");
                if (idx != -1) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    headers.put(key, value);
                    if (key.equalsIgnoreCase("Content-Length")) {
                        contentLength = Integer.parseInt(value);
                    }
                }
            }

            String body = "";
            if (method.equals("POST") && contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars);
                body = new String(bodyChars);
                System.out.println("📦 Body: " + body);
            }

            // Route
            if (method.equals("POST") && path.equals("/api/login")) {
                Map<String, String> parsed = parseFormBody(body);
                String username = parsed.getOrDefault("username", "unknown");
                String password = parsed.getOrDefault("password", "???");

                String response = String.format("Hello %s, your password is %s", username, password);
                sendResponse(out, 200, "text/plain", response);
            } else {
                sendResponse(out, 404, "text/plain", "404 - Not Found");
            }

            socket.close();
        } catch (IOException e) {
            System.err.println("❗ Error: " + e.getMessage());
        }
    }

    /**
     * Parse x-www-form-urlencoded body (eg: username=abc&password=123)
     */
    private static Map<String, String> parseFormBody(String body) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                result.put(decode(kv[0]), decode(kv[1]));
            }
        }
        return result;
    }

    private static String decode(String s) {
        return s.replace("+", " ").replace("%40", "@"); // simple decode
    }

    private static void sendResponse(BufferedWriter out, int status, String contentType, String body) throws IOException {
        String headers = "HTTP/1.1 " + status + " OK\r\n" +
                         "Content-Type: " + contentType + "; charset=UTF-8\r\n" +
                         "Content-Length: " + body.getBytes().length + "\r\n" +
                         "Connection: close\r\n" +
                         "\r\n";

        out.write(headers + body);
        out.flush();
    }
}
