import java.io.*;
import java.net.*;


public class HttpServer {
    public static void main (String[] args) {
        int PORT = 8080; // Default port

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening http://localhost:" + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    
    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            // Đọc dòng đầu tiên của HTTP request (ví dụ: GET / HTTP/1.1)
            String requestLine = in.readLine();
            System.out.println("📥 Request: " + requestLine);

            // Bỏ qua các dòng header tiếp theo (đơn giản hóa)
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
               System.out.println("📄 Header: " + line);
            }

            // Tạo HTTP response thủ công
            String responseBody = "Hello from Java";
            String response = "HTTP/1.1 200 OK\r\n" +
                              "Content-Type: text/plain\r\n" +
                              "Content-Length: " + responseBody.length() + "\r\n" +
                              "\r\n" +
                              responseBody;

            // Gửi response
            out.write(response);
            out.flush();

            socket.close(); // Đóng kết nối
        } catch (IOException e) {
            System.err.println("❗ Error handling client: " + e.getMessage());
        }
    }
}