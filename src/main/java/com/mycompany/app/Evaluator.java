import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;

import java.text.DecimalFormat;

import java.lang.reflect.Field;

class EvalTask extends Task {
	int evalorder;
	double score;

	public EvalTask(int order, int evalorder, int[] numbers, double trgt, double time, double score) {
        super(order, numbers, trgt, time);
        this.evalorder = evalorder;
        this.score = score;
    }
}

class EvalTaskComp implements Comparator<EvalTask> {
    @Override
    public int compare(EvalTask task1, EvalTask task2) {
    	if (task1.score != task2.score) {
       		return Double.compare(task1.score,task2.score);
       	} else {
       		return Double.compare(task1.score,task2.score);
       		// return Integer.compare(task1.order,task2.order);
       	}
    }
}

class NormalTaskComp implements Comparator<EvalTask> {
    @Override
    public int compare(EvalTask task1, EvalTask task2) {
       	return Double.compare(task1.time,task2.time);
    }
}

class Pair {
	Task task;
	EqnPackage eqnpckge;

	public Pair(Task task, EqnPackage eqnpckge) {
		this.task = task;
		this.eqnpckge = eqnpckge;
	}
}

public class Evaluator {

	public static void main(String[] args) {

		long t = System.currentTimeMillis();
		int ttlqueries = ScoreData.__SCRDATA.length;

		// for (double i = 200; i <= 1000; i += 20) {

		List<Pair> eqnlist = new ArrayList<>();

		for (Task task : ScoreData.__SCRDATA) {
			EqnPackage rawsols = Solver.solve4(task.numbers,Constants.trgt);
			eqnlist.add(new Pair(task,rawsols));
		}

		// for (double i = 0; i <= 5; i += 0.11) {
			// i = Math.floor(i*10)/10;

		double min1 = Double.MAX_VALUE;
		double ideal = -1;

		// for (double i = 0; i <= 100; i += 0.11) {
			// i = Math.floor(i*10)/10;

		for (double i = 0; i <= 5; i += 0.011) {
			i = Math.floor(i*100)/100;

			List<EvalTask> actual = new ArrayList<>();
			List<EvalTask> computed = new ArrayList<>();

			// Constants.ONETWOWEIGHTS[3] = i; String display = "OTW3";
			// Constants.ABCD_WEIGHT = i; String display = "ABCD";
			// Constants.FINALWEIGHT[1] = i;
			// String display = "FINALWEIGHT[1]";

			for (Pair pair : eqnlist) {
				EqnPackage rawsols = pair.eqnpckge;
				Task task = pair.task;
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
				// System.out.println(Arrays.toString(task.numbers)+" {"+task.order+"} ("+task.evalorder+") \n"+task.score);
				actual.add(task);
			}
			Collections.sort(actual, new NormalTaskComp());
			double rmse = 0;
			// int idx = 0;
			for (EvalTask task : actual) {
				// task.order = ++idx;
				// System.out.println(Arrays.toString(task.numbers)+" {"+task.order+"} ("+task.evalorder+")");
				// System.out.println(task.time+" "+task.score);
				rmse += Math.pow(Math.floor((task.time-task.score+0.0001)*10000)/10000,2);
			}
			rmse /= ttlqueries;
			rmse = Math.sqrt(rmse);
			System.out.println(display+": "+i+" RMSE: "+rmse);
			if (rmse < min1) {
				min1 = rmse;
				ideal = i;
			}
		}
	// 	System.out.println();
		System.out.println("min1: "+min1);
		System.out.println("ideal: "+ideal);
	// }
		System.out.println("done in "+(System.currentTimeMillis()-t)+" ms");
	}
}