import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class DbManager {

    static String makeConfigPath(String configName) {
        String workingDirectory = System.getProperty("user.dir");
        String settingsDir = "settings";
        return workingDirectory + File.separator
                + settingsDir + File.separator + configName;
    }


    private static Connection createConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        String pgConfigPath = makeConfigPath("pg.conf");
        String host;
        String username;
        String password;
        String driver;
        try {
            prop.load(new FileInputStream(pgConfigPath));
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
        return connection;
    }


    static ResultSet executeMyQuery(String query) {
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

    static void addRecordToTable(String record, String tableName) throws SQLException {
        ArrayList<String> columns = getTablesColumns(tableName);
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
        executeMyQuery(query);
    }

    static void updateField(
            String columnToUpdate, String  newValue, String columnWhereUpdate,
            String rowWhereUpdate, String tableName
    ) {
        String query = String.format("%s%s\n%s%s%s%s%s%s\n%s\n%s%s%s%s%s",
                "UPDATE ", tableName,
                "SET ", columnToUpdate, " = ", "'", newValue, "'",
                "WHERE ",
                columnWhereUpdate, " = ", "'", rowWhereUpdate, "';"
        );
        executeMyQuery(query);
    }

    static String selectRecord(
            String columnWhichSelect, String columnWhereSelect,
            String rowWhereSelect,  String tableName
    ) {
        String record = "";
        String query = String.format("%s%s\n%s%s\n%s\n%s%s%s%s%s",
                "SELECT ", columnWhichSelect,
                "FROM ", tableName,
                "WHERE ",
                columnWhereSelect, " = ", "'", rowWhereSelect, "';"
        );
        ResultSet rs = executeMyQuery(query);
        try {
            rs.next();
            record = rs.getString(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return record;
    }

    static ArrayList<String> selectColumn(String columnWhichSelect, String tableName) {
        ArrayList<String> records = new ArrayList<String>();

        String query = String.format("%s%s\n%s%s%s",
                "SELECT ", columnWhichSelect,
                "FROM ", tableName, ";"
        );

        ResultSet rs = executeMyQuery(query);

        try {
            while (rs.next())
                records.add(rs.getString(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return records;
    }
}
