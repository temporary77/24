import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;

import java.text.DecimalFormat;

public class Final {

	public static void main(String[] args) {

		long t = System.currentTimeMillis();
		int ttlqueries = ScoreData.__SCRDATA.length;

		List<EvalTask> actual = new ArrayList<>();
		List<EvalTask> computed = new ArrayList<>();

		for (Task task : ScoreData.__SCRDATA) {
			EqnPackage rawsols = Solver.solve4(task.numbers,Constants.trgt);
			double score = 0;
			int cnt = rawsols.solutions.size();
			for (Equation eqn : rawsols.solutions) {
				Solution sol = Solver.Convert(eqn,Constants.largedet);
				score += Math.pow(sol.score,Constants.INIT_DET);
			}
			score /= cnt;
			score = Math.pow(score,1/Constants.INIT_DET);
			score *= Math.pow(cnt,Constants.CNT_WEIGHT_DET);
			// score *= 1+(Math.log(cnt)/Math.log(Constants.CNT_WEIGHT_DET));
			// score = score/(Math.pow(cnt,Constants.CNT_WEIGHT_DET));
			// score = score/(1+Math.log(cnt)/Math.log(Constants.CNT_WEIGHT_DET));
			// System.out.println(cnt+" "+Math.log(cnt)/Math.log(Constants.CNT_WEIGHT_DET));
			computed.add(new EvalTask(task.order,-1,task.numbers,task.trgt,task.time,score));
		}
		Collections.sort(computed, new EvalTaskComp());
		int evalorder = 0;
		for (EvalTask task : computed) {
			task.evalorder = ++evalorder;
			EqnPackage rawsols = Solver.solve4(task.numbers,Constants.trgt);
			System.out.println(Arrays.toString(task.numbers)+" {"+task.order+"} ("+task.evalorder+")");
			System.out.println(task.time+","+task.score+"\n"+(Math.floor((task.time-task.score+0.0001)*1000)/1000)
				+","+Math.pow(Math.floor((task.time-task.score+0.0001)*10000)/10000,2));
			actual.add(task);
		}
		Collections.sort(actual, new NormalTaskComp());
		double rmse = 0;
		double avge = 0;
		// int idx = 0;
		for (EvalTask task : actual) {
			// task.order = ++idx;
			// System.out.println(Arrays.toString(task.numbers)+" {"+task.order+"} ("+task.evalorder+")");
			// System.out.println(task.time+" "+task.score);
			rmse += Math.pow(Math.floor((task.time-task.score+0.0001)*10000)/10000,2);
			avge += Math.abs(Math.floor((task.time-task.score+0.0001)*10000)/10000);
		}
		rmse /= ttlqueries;
		avge /= ttlqueries;
		rmse = Math.sqrt(rmse);
		System.out.println("RMSE: "+rmse);
		System.out.println("AVGE: "+avge);
		System.out.println("done in "+(System.currentTimeMillis()-t)+" ms");
	}
}