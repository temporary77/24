import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;

import java.util.Scanner;

public class Extract {
	public static void main(String[] args) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter("ScoreData.java")) {
			File file = new File("ScoreData.java");
			// System.out.println(file.exists());
			// System.out.println(file.isDirectory());
			// System.out.println(file.canRead());
			out.println("public class ScoreData {");
			out.println("	public static final Task[] __SCRDATA = {");
			Scanner scanner = new Scanner(System.in);
			int idx = 1;
			String scanin;
			int order;
			int[] numbers = new int[4];
			double score;
			while (idx <= 1362) {
				order = scanner.nextInt();
				for (int i = 0; i < 4; ++i) {
					numbers[i] = scanner.nextInt();
				}
				score = scanner.nextDouble();
				for (int i = 0; i < 3; ++i) {
					scanin = scanner.next();
				}
				out.println("		new Task("+order+", new int[]"+
					Arrays.toString(numbers).replaceAll("\\[","{").replaceAll("\\]","}")+
					", "+score+"),");
				++idx;
			}
			out.print("	}\n}");
		}
	}
}