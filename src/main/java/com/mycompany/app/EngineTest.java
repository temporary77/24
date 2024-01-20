import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EngineTest {
    public static void main(String[] args) {
        // Create a ScriptEngineManager
        ScriptEngineManager manager = new ScriptEngineManager();

        // Get a JavaScript engine by name
        ScriptEngine engine = manager.getEngineByName("javascript");

        if (engine != null) {
            System.out.println("JavaScript engine obtained successfully.");
        } else {
            System.out.println("JavaScript engine not available or not recognized.");
        }
    }
}