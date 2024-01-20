import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;

import java.text.DecimalFormat;

class EvalTask extends Task {
	int evalorder;
	double score;

	public EvalTask(int order, int evalorder, int[] numbers, double time, double score) {
        super(order, numbers, time);
        this.evalorder = evalorder;
        this.score = score;
    }
}

class TaskComp implements Comparator<Task> {
    @Override
    public int compare(EvalTask task1, EvalTask task2) {
    	if (task1.score != task2.score) {
       		return Integer.compare(task1.score,task2.score);
       	} else {
       		return Integer.compare(task1.order,task2.order);
       	}
    }
}

public class Evaluator {

	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");

	private static <K,V> void put(TreeMap<K,List<V>> mp, K key, V value) {
		List<V> values = mp.get(key);
		if (values == null) {
			values = new ArrayList<>();
			mp.put(key, values);
		}
		values.add(value);
	}

	public static void main(String[] args) {

		TreeMap<Integer,List<Answer>> countMap = new TreeMap<>();
		TreeMap<Double,List<Answer>> scoreMap = new TreeMap<>();
		TreeMap<Double,List<Answer>> largestMap = new TreeMap<>();
		TreeMap<Integer,List<Answer>> fractMap = new TreeMap<>();
		TreeMap<Integer,List<Answer>> partialMap = new TreeMap<>();
		TreeMap<Integer,List<Answer>> partialOnlyMap = new TreeMap<>();
		int ttlqueries = 0;
		int fractcnt = 0;
		int partcnt = 0;

		long t = System.currentTimeMillis();

		for (int i = start; i <= end; ++i) {
			// System.out.println("solving i = "+i+" ...");
			for (int j = i; j <= end; ++j) {
				for (int k = j; k <= end; ++k) {
					for (int l = k; l <= end; ++l) {
						// if ((i == j && j == k) || (j == k && k == l))continue;
						// if (i == 0 && j == 0)continue;
						++ttlqueries;
						Answer ans = new Answer();
						ans.numbers = new int[]{i,j,k,l};
						EqnPackage rawsols = Solver.solve4(ans.numbers,trgt);
						ans.solutions = new ArrayList<>();
						ans.partials = new ArrayList<>();
						ans.minlarge = Double.MAX_VALUE;
						ans.score = 0;
						for (Equation eqn : rawsols.solutions) {
							Solution sol = Solver.Convert(eqn);
							ans.solutions.add(sol);
							ans.score += sol.score;
							ans.minlarge = Math.min(ans.minlarge,sol.largest);
						}
						for (Equation eqn : rawsols.partials) {
							Solution sol = Solver.Convert(eqn);
							ans.partials.add(sol);
						}
						if (ans.score == 0)ans.minlarge = -1;
						put(countMap,Integer.valueOf(ans.solutions.size()),ans);
						put(scoreMap,Double.valueOf(ans.score),ans);
						put(largestMap,Double.valueOf(ans.minlarge),ans);
						if (ans.score > 0 && ans.score == ans.solutions.size()*0.2) {
							put(fractMap,Integer.valueOf(ans.solutions.size()),ans);
							++fractcnt;
						}
						put(partialMap,Integer.valueOf(ans.partials.size()),ans);
						if (ans.solutions.size() == 0 && ans.partials.size() > 0) {
							put(partialOnlyMap,Integer.valueOf(ans.partials.size()),ans);
							++partcnt;
						}
					}
				}
			}
			// System.out.println("done in "+(System.currentTimeMillis()-t)+" ms");
		}
	}
}