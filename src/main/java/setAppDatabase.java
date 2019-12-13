import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class setAppDatabase {
    private final static String createProcessTable = String.format("%s%s\n%s\n%s\n%s\n%s\n%s\n%s",
            "CREATE TABLE ", "Process (",
            "id  SERIAL PRIMARY KEY,",
            "name varchar(255) NOT NULL UNIQUE,",
            "last_start_time varchar(255),",
            "full_time int NOT NULL,",
            "type varchar(255) NOT NULL",
            ");"
    );

    private static void setupFromJsonConfig(String configName){
        try {
            String jsonConfigPath = DbManager.makeConfigPath(configName);
            Object obj = new JSONParser().parse(new FileReader(jsonConfigPath));
            JSONObject jsonObj = (JSONObject) obj;
            List<String> types = new ArrayList<String>(jsonObj.keySet());


            for (String type: types) {
                JSONArray processes = (JSONArray) jsonObj.get(type);
                for (Object process: processes) {
                    StringBuilder record = new StringBuilder();
                    record.append("'").append(process.toString()).append("'");
                    record.append(", ").append("null ");
                    record.append(", ").append("0 ");
                    record.append(", ").append("'").append(type).append("'");
                    DbManager.addRecordToTable(record.toString(), "process");
                }
            }
        } catch (IOException | ParseException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DbManager.executeMyQuery(createProcessTable);
        setupFromJsonConfig("setup.json");
    }
}
