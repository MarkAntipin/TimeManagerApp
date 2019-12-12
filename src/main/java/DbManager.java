import java.util.ArrayList;

import java.lang.String.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import java.sql.ResultSet;
import org.postgresql.util.PSQLException;

public class DbManager {
    private final static String createTypeTable = String.format("%s%s\n%s\n%s\n%s",
            "CREATE TABLE ", "Types (",
            "id SERIAL PRIMARY KEY,",
            "type varchar(255) NOT NULL UNIQUE",
            ");"
    );
    private final static String createProcessTable = String.format("%s%s\n%s\n%s\n%s\n%s\n%s\n%s",
            "CREATE TABLE ", "Process (",
            "id  SERIAL PRIMARY KEY,",
            "name varchar(255) NOT NULL UNIQUE,",
            "last_start_time date NOT NULL,",
            "full_time int NOT NULL,",
            "type_id int FOREIGN KEY REFERENCES Types(id)",
            ");"
    );

    private static Connection createConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        String workingDirectory = System.getProperty("user.dir");
        String settingsDir = "settings";
        String filename = "pg.conf";
        String pgConfigPath = workingDirectory + File.separator + settingsDir + File.separator + filename;
        String host;
        String username;
        String password;
        String driver;
        try {
            prop.load(new java.io.FileInputStream(pgConfigPath));
            host = prop.getProperty("host");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
            driver = prop.getProperty("driver");
        } catch (IOException ex) {
            System.out.println("No 'pg.conf' in settings");
            throw new IOException(ex);
        }
        Class.forName(driver);
        Connection connection = DriverManager.getConnection(host, username, password);
        System.out.println("Connected to database!");
        return connection;
    }

//    private static Connection setAppDatabase() {
//
//    }

    private static ResultSet executeMyQuery(String query) {
        ResultSet rs = null;
        try {
            PreparedStatement pst = createConnection().prepareStatement(query);
            rs = pst.executeQuery();
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return rs;
    }

    private static ArrayList<String> getTablesColumns(String tableName) throws SQLException {
        String query = String.format("%s\n%s%s%s%s",
                "SELECT COLUMN_NAME FROM information_schema.COLUMNS",
                "WHERE TABLE_NAME = ", "'", tableName, "';"
        );
        ResultSet rs = executeMyQuery(query);
        ArrayList<String> columns = new ArrayList<String>();
        while (rs.next()) {
            columns.add(rs.getString(1));
        }
        return columns;
    }

    private static void addRecordToTable(String record, String tableName) throws SQLException {
        ArrayList<String> columns = getTablesColumns("types");
        columns.remove(0);
        StringBuilder stringColumns = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            stringColumns.append(columns.get(i));
            if (i < columns.size() - 1) {
                stringColumns.append(", ");
            }
        }

        String query = String.format("%s%s%s%s%s\n%s%s%s",
                "INSERT INTO ", tableName, "(", stringColumns.toString(), ")",
                "VALUES(", record, ");"
        );
        System.out.println(query);
        executeMyQuery(query);
    }





    public static void main(String[] args) throws SQLException {
//        executeMyQuery(createTypeTable);


        addRecordToTable("'work'", "types");
    }
}
