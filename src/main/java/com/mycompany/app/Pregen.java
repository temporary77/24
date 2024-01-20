import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;
import java.lang.reflect.Field;

public class Pregen {
	public static void main(String[] args) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter("Methods.java")) {
			File file = new File("Methods.java");
			// System.out.println(file.exists());
			// System.out.println(file.isDirectory());
			// System.out.println(file.canRead());
			out.println("public class Methods {");
			for (Field field : Expressions.class.getFields()) {
				String fieldName = field.getName();
				if (!fieldName.startsWith("EXP_")) {
					continue;
				}
				for (String expression : (String[]) field.get(null)) {
					out.println("\tpublic static double "+Expressions2.callcode(expression)
						+"(double a, double b, double c, double d) {return "
						+expression.replace("0","0.0").replace("1","1.0")
						.replace("2","2.0").replace("3","3.0").replace("4","4.0")+";}");
				}
			}
			out.print("}");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}