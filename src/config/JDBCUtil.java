package config;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class JDBCUtil {
    public static Connection getConnection() {
        Connection result = null;
        try {
            // Đăng ký SQL Server Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Thông số kết nối SQL Server
            String url = "jdbc:sqlserver://localhost:1433;databaseName=quanlycuahangdongho;encrypt=false";
            String userName = "sa";
            String password = "cc123123"; // mật khẩu SQL Server của bạn
            // Tạo kết nối
            result = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu!\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    public static void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}