import java.util.ArrayList;

public class CollectStatistics {

    static void collectStat(String process) {
        String timeFromDb = DbManager.selectRecord(
                "last_start_time",
                "name",
                process,
                "process"
        );
        String timeFromPid = LinuxProcessManager.getTimeOfProgram(process);
        if (timeFromDb.equals(timeFromPid) && !timeFromDb.equals(" ")) {
            String record = DbManager.selectRecord(
                    "full_time",
                    "name",
                    process,
                    "process"
            );
            int updatedTime = Integer.parseInt(record) + 5;
            DbManager.updateField(
                    "full_time",
                    Integer.toString(updatedTime),
                    "name",
                    process,
                    "process"
            );
        } else {
            DbManager.updateField(
                "last_start_time",
                timeFromPid,
                "name",
                    process,
                "process"
            );
        }
    }

    public static void main(String[] args) {
        ArrayList<String> records = DbManager.selectColumn("name","process");
        for (String record: records) {
            collectStat(record);
        }
    }
}
