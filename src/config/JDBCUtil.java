package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class JDBCUtil {

    private static final Map<String, String> JDBC_URL_BY_MCN = new HashMap<>();
    private static final String DEFAULT_URL = buildSqlServerUrl("DESKTOP-0G7OJFQ\\MANHDUNG1", "quanlycuahangdongho");
    private static volatile String currentMcn;

    static {
        JDBC_URL_BY_MCN.put("CN1", buildSqlServerUrl("DESKTOP-0G7OJFQ\\MANHDUNG2", "quanlycuahangdongho"));
        JDBC_URL_BY_MCN.put("CN2", buildSqlServerUrl("DESKTOP-0G7OJFQ\\MANHDUNG3", "quanlycuahangdongho"));
        JDBC_URL_BY_MCN.put("CN3", buildSqlServerUrl("DESKTOP-0G7OJFQ\\MANHDUNG4", "quanlycuahangdongho"));
    }

    public static void registerJdbcUrl(String mcn, String jdbcUrl) {
        if (mcn == null || mcn.isBlank() || jdbcUrl == null || jdbcUrl.isBlank()) {
            return;
        }
        JDBC_URL_BY_MCN.put(mcn.trim().toUpperCase(), jdbcUrl.trim());
    }

    public static void setCurrentMcn(String mcn) {
        currentMcn = mcn == null ? null : mcn.trim().toUpperCase();
    }

    public static String buildSqlServerUrl(String instanceName, String databaseName) {
        return "jdbc:sqlserver://" + instanceName
                + ";databaseName=" + databaseName
                + ";encrypt=false;trustServerCertificate=true";
    }

    public static Connection getConnection(String mcn) {
        String key = mcn == null ? null : mcn.trim().toUpperCase();
        String url = key != null && JDBC_URL_BY_MCN.containsKey(key)
                ? JDBC_URL_BY_MCN.get(key)
                : DEFAULT_URL;
        return openConnection(url, key);
    }

    public static Connection getConnection() {
        if (currentMcn != null && JDBC_URL_BY_MCN.containsKey(currentMcn)) {
            return openConnection(JDBC_URL_BY_MCN.get(currentMcn), currentMcn);
        }
        return openConnection(DEFAULT_URL, currentMcn);
    }

    private static Connection openConnection(String url, String branch) {
        Connection result = null;
        try {
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            String userName = "sa";
            String password = "cc123123";
            result = DriverManager.getConnection(url, userName, password);
            String serverInfo = extractServerInfo(url);
            String dbInfo = extractDatabaseName(url);
            String branchInfo = branch != null && !branch.isBlank() ? branch : "DEFAULT";
            System.out.println("[DB] OPEN | branch=" + branchInfo + " | server=" + serverInfo + " | db=" + dbInfo + " | thread=" + Thread.currentThread().getName());
            logConnectedServer(result);
        } catch (Exception e) {
            System.out.println("[DB] ERROR | branch=" + (branch != null ? branch : "DEFAULT") + " | " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu !", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    private static void logConnectedServer(Connection connection) {
        if (connection == null) {
            return;
        }
        String sql = "SELECT @@SERVERNAME AS SERVER_NAME, DB_NAME() AS DB_NAME";
        try (PreparedStatement pst = connection.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String serverName = rs.getString("SERVER_NAME");
                String databaseName = rs.getString("DB_NAME");
                System.out.println("[DB] ACTIVE | server=" + serverName + " | db=" + databaseName);
            }
        } catch (Exception e) {
            System.out.println("[DB] WARN | cannot read server identity | " + e.getMessage());
        }
    }

    private static String extractServerInfo(String url) {
        String prefix = "jdbc:sqlserver://";
        if (url == null || !url.startsWith(prefix)) {
            return url;
        }
        String remainder = url.substring(prefix.length());
        int semicolonIndex = remainder.indexOf(';');
        return semicolonIndex >= 0 ? remainder.substring(0, semicolonIndex) : remainder;
    }

    private static String extractDatabaseName(String url) {
        if (url == null) {
            return "";
        }
        String marker = "databaseName=";
        int startIndex = url.indexOf(marker);
        if (startIndex < 0) {
            return "";
        }
        startIndex += marker.length();
        int endIndex = url.indexOf(';', startIndex);
        return endIndex >= 0 ? url.substring(startIndex, endIndex) : url.substring(startIndex);
    }

    public static void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
                System.out.println("[DB] CLOSE | thread=" + Thread.currentThread().getName());
            }
        } catch (Exception e) {
            System.out.println("[DB] WARN | close failed | " + e.getMessage());
            e.printStackTrace();
        }
    }
}