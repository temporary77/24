import java.io.File;
import java.io.IOException;

public class Executor {
    public static void main(String[] args) {
        try {
            String currentDirectory = System.getProperty("user.dir");

            String command = "cmd /c start cmd.exe /K \"java -cp . Interactive && exit\"";

            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));

            processBuilder.directory(new File(currentDirectory));

            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            System.out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
