import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.text.DecimalFormat;

public class Analyzer {
	public static void main(String[] args) {
		List<EvalTask> actual = new ArrayList<>();
		List<EvalTask> computed = new ArrayList<>();

		for (Task task : ScoreData.__SCRDATA) {
			EqnPackage rawsols = Solver.solve4(task.numbers,Constants.trgt);
			double score = 0;
			int cnt = rawsols.solutions.size();
			for (Equation eqn : rawsols.solutions) {
				Solution sol = Solver.Convert(eqn,Constants.largedet);
				score += sol.score;
			}
			// if (task.numbers[1] == 1) {
			// 	System.out.println(Arrays.toString(task.numbers)+" "+score);
			// } 
			// System.out.println(Arrays.toString(task.numbers)+" "+score);
			score = score/(Math.pow(rawsols.solutions.size(),Constants.CNT_WEIGHT_DET));
			computed.add(new EvalTask(task.order,-1,task.numbers,task.trgt,task.time,score));
		}
		Collections.sort(computed, new EvalTaskComp());
		int evalorder = 0;
		for (EvalTask task : computed) {
			// System.out.println(Arrays.toString(task.numbers)+" {"+task.order+"} ("+task.evalorder+") \n"+task.score);
			task.evalorder = ++evalorder;
			actual.add(task);
		}
		Collections.sort(actual, new NormalTaskComp());
		double rmse = 0;
		int idx = 0;
		for (EvalTask task : actual) {
			task.order = ++idx;
			// System.out.println(Arrays.toString(task.numbers)+" {"+task.order+"} ("+task.evalorder+") \n"+task.score);
			rmse += Math.pow(task.time-task.score,2);

		}
		for (EvalTask task : actual) {
			EqnPackage rawsols = Solver.solve4(task.numbers,Constants.trgt);
			double score = 0;
			double minlarge = Double.MAX_VALUE;
			int cnt = rawsols.solutions.size();
			int fracts = 0;
			for (Equation eqn : rawsols.solutions) {
				Solution sol = Solver.Convert(eqn,Constants.largedet);
				score += sol.score;
				minlarge = Math.min(minlarge,sol.largest);
				if (sol.isfract)++fracts;
			}
			if (minlarge <= 36)minlarge = 0;
			score = score/(Math.pow(rawsols.solutions.size(),Constants.CNT_WEIGHT_DET));
			System.out.printf("%4d [",task.order);
			for (int i = 0; i < 3; ++i)System.out.printf("%2d,",task.numbers[i]);
			System.out.printf("%2d",task.numbers[3]);
			int sum = task.numbers[0]+task.numbers[1]+task.numbers[2]+task.numbers[3];
			System.out.printf("] %2d %6.2f %2d %5.1f %2d%n",sum,task.time,cnt,minlarge,fracts);
			for (Equation eqn : rawsols.solutions) {
				System.out.println(eqn.exp+"="+Constants.trgt);
			}
		}
	}
}