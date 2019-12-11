package DataBase;

import java.lang.String.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import java.sql.ResultSet;

public class PgConnection {
    private final static String createTypeTable = String.format("%s%s\n%s\n%s\n%s",
        "CREATE TABLE ", "Types (",
        "id SERIAL PRIMARY KEY,",
        "type varchar(255) NOT NULL UNIQUE",
        ");"
    );
    private final static String createProcessTable = String.format("%s%s\n%s\n%s\n%s\n%s\n%s",
        "CREATE TABLE ", "Process (",
        "id  SERIAL PRIMARY KEY,",
        "name varchar(255) NOT NULL UNIQUE,",
        "time date NOT NULL,",
        "type_id int FOREIGN KEY REFERENCES Types(id)",
        ");"
    );

    public PgConnection() {
    }


    public static Connection createConnection() throws IOException, ClassNotFoundException, SQLException {
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
        System.out.println("host: " + host + "\nusername: " + username + "\npassword: " + password + "\ndriver: " + driver);
        Class.forName(driver);
        Connection connection = DriverManager.getConnection(host, username, password);
        System.out.println("Connected to database!");
        return connection;
    }

    private static void getTablesColumns(String tableName) {
        String query = String.format("%s\n%s%s%s",
                "SELECT COLUMN_NAME FROM information_schema.COLUMNS",
                "WHERE TABLE_NAME = ", tableName, ";"
        );
//        PreparedStatement pst = con.prepareStatement("SELECT * FROM authors");
//        ResultSet rs = executeQuery(query)
    }

    private static void executeQuery(String query) {
        try {
            Statement statement = createConnection().createStatement();
            statement.execute(query);
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void addRecordToTable(String record, String tableName) {
        String query = String.format("%s%s\n%s%s%s",
                "INSERT INTO ", tableName,
                "VALUES", record, " ;"
                );
        executeQuery(query);
    }



    public static void main(String[] args) {
//        executeQuery(createTypeTable);
        addRecordToTable("('work')", "types");
        addRecordToTable("('study')", "types");
        addRecordToTable("('fun')", "types");
    }
}
