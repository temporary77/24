public class Sandbox {
	public static void main(String[] args) {
		// String str = "aaa/0aaa";
		// System.out.println(str.substring(3,5).equals("/0"));
		// System.out.println(SolveAll.funnyvariable);

		// EqnPackage rawsols = Solver.solve4(new int[]{6,6,6,6}, Constants.trgt);
		// double score = 0;
		// int cnt = rawsols.solutions.size();
		// for (Equation eqn : rawsols.solutions) {
		// 	Solution sol = Solver.Convert(eqn,Constants.largedet);
		// 	score += sol.score;
		// }
		// score /= cnt;
		// score *= Math.pow(cnt,Constants.CNT_WEIGHT_DET);
		// System.out.println(score);
		String str = "(1+1)*((910+1399)+10)";
		System.out.println(str.replaceAll("\\b\\d{2,}\\b|[^1\\D]","o"));
	}
}