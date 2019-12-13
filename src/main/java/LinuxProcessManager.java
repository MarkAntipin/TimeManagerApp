import java.util.Arrays;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class LinuxProcessManager {

    private static String executeLinuxCommand(String command) {
        Process process;
        String commandOutput = "";
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            commandOutput = br.readLine();
            process.waitFor();
            process.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return commandOutput;
    }

    private static String getPidOfProgram(String program) {
        return executeLinuxCommand("pgrep " + program);
    }

    private static String[] getTimeOfProgram(String program) {
        String pidProgram =  getPidOfProgram(program);
        String time = "";
        try {
            Process process = Runtime.getRuntime().exec("ps -eo pid,lstart");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            while ((time = br.readLine()) != null) {
                if (time.contains(pidProgram)) {
                    break;
                }
            }
            process.waitFor();
            process.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String[] timeArr = time.split(" ");
        return Arrays.copyOfRange(timeArr, 2, timeArr.length);
    }


    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(getTimeOfProgram("pycharm")));
//        getTimeOfProgram("pycharm");

    }
}
