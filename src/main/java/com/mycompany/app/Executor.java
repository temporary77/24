import java.io.File;
import java.io.IOException;

public class Executor {
    public static void main(String[] args) {
        try {
            // Get the current working directory
            String currentDirectory = System.getProperty("user.dir");

            // Command to open Windows Terminal and execute another Java file
            // String fpath = "C:\\Users\\Asus\\Desktop\\24\\src\\main\\java\\com\\mycompany\\app\\Interactive.java";

            String command = "cmd /c start cmd.exe /K \"java -cp . Interactive && exit\"";

            // Create a ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));

            // Set the working directory to the current directory
            processBuilder.directory(new File(currentDirectory));

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete (optional)
            int exitCode = process.waitFor();

            // Print the exit code (optional)
            System.out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
