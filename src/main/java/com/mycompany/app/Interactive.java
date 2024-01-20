import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.util.Scanner;

public class Interactive {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String cmd;
		double target = 24;
		double largedet = 36;
		int[] numbers = {-1,-1,-1,-1};
		System.out.println("booted");
		while(true) {
			System.out.println();
			System.out.println("current target: "+target+", current largedet: "+largedet);
			System.out.println("available prompts: setconst, solve, quit");
			cmd = scanner.next();
			if (cmd.equals("quit"))break;
			else if (cmd.equals("setconst")) {
				System.out.println("enter new target:");
				// target = scanner.next();
				cmd = scanner.next();
				if (cmd.equals("back"))continue;
				target = Double.valueOf(cmd);
				System.out.println(target+" ok");
				System.out.println("enter new largedet:");
				// largedet = scanner.next();
				cmd = scanner.next();
				if (cmd.equals("back"))continue;
				largedet = Double.valueOf(cmd);
				System.out.println(largedet+" ok");
			}
			else if (cmd.equals("solve")) {
				System.out.println("enter a, b, c, d:");
				boolean exit = false;
				boolean quit = false;
				for (int i = 0; i < 4; ++i) {
					cmd = scanner.next();
					if (cmd.equals("back")) {
						exit = true;
						break;
					} else if (cmd.equals("quit")) {
						quit = true;
						break;
					}
					numbers[i] = Integer.valueOf(cmd);
				}
				if (exit)continue;
				if (quit)break;
				SolPackage rawsols = Solver.solve4(numbers,target);
				List<Solution> normalsols = new ArrayList<>();
				List<Solution> partials = new ArrayList<>();
				List<Solution> largesols = new ArrayList<>();
				List<Solution> fractsols = new ArrayList<>();
				List<Solution> insanesols = new ArrayList<>();
				double minlarge = Double.MAX_VALUE;
				for (Equation eqn : rawsols.solutions) {
					Solution sol = Solver.Convert(eqn);
					if (sol.largest > largedet && sol.isfract) {
						insanesols.add(sol);
					} else if (sol.isfract) {
						fractsols.add(sol);
					} else if (sol.largest > largedet) {
						largesols.add(sol);
					} else {
						normalsols.add(sol);
					}
					minlarge = Math.min(minlarge,sol.largest);
				}
				for (Equation eqn : rawsols.partials) {
					Solution sol = Solver.Convert(eqn);
					partials.add(sol);
				}
				if (rawsols.solutions.size() == 0)minlarge = -1;
				while(true) {
					System.out.println();
					System.out.println("current task: "+Arrays.toString(numbers)+" = "+target);
					System.out.println("available prompts: rawsols, sols, info, back");
					cmd = scanner.next();
					if (cmd.equals("back"))break;
					if (cmd.equals("rawsols")) {
						for (Solution sol : normalsols) {
							System.out.println(sol.exp+"="+sol.result);
						}
						for (Solution sol : largesols) {
							System.out.println(sol.exp+"="+sol.result);
						}
						for (Solution sol : fractsols) {
							System.out.println(sol.exp+"="+sol.result);
						}
						for (Solution sol : insanesols) {
							System.out.println(sol.exp+"="+sol.result);
						}
					} else System.out.println("invalid prompt");
				}
			} else System.out.println("invalid prompt");
		}
		return;
	}
}