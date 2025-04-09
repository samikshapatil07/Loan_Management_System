package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnUtil {

    public static Connection getDBConn() throws Exception {
        // Update this with your actual path if needed
        String fileName = "db.properties";
        String connStr = DBPropertyUtil.getConnectionString(fileName);

        return DriverManager.getConnection(connStr);
    }
}
