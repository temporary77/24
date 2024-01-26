import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.util.Scanner;

public class QuickSolve {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String cmd;
		double target = Constants.trgt;
		double largedet = Constants.largedet;
		int[] numbers = {-1,-1,-1,-1};
		for (int i = 0; i < 4; ++i) {
			cmd = scanner.next();
			numbers[i] = Integer.valueOf(cmd);
		}
		EqnPackage rawsols = Solver.solve4(numbers,target);
		List<Solution> normalsols = new ArrayList<>();
		List<Solution> partials = new ArrayList<>();
		List<Solution> largesols = new ArrayList<>();
		List<Solution> fractsols = new ArrayList<>();
		List<Solution> insanesols = new ArrayList<>();
		double minlarge = Double.MAX_VALUE;
		for (Equation eqn : rawsols.solutions) {
			Solution sol = Solver.Convert(eqn,largedet);
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
			Solution sol = Solver.Convert(eqn,largedet);
			partials.add(sol);
		}
		if (rawsols.solutions.size() == 0)minlarge = -1;
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
		return;
	}
}