package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {

    public static String getConnectionString(String fileName) {
        Properties props = new Properties();
        String connStr = null;

        try (FileInputStream fis = new FileInputStream(fileName)) {
            props.load(fis);
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connStr = url + "?user=" + user + "&password=" + password;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return connStr;
    }
}
