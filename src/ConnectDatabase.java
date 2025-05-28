import java.sql.*;

public class ConnectDatabase {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=demodb;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "123456789";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✅ Driver loaded!");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to SQL Server!");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
