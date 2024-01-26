import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.text.DecimalFormat;

public class GetData {

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


		long t = System.currentTimeMillis();

		for (int ii = 0; ii <= 48; ++ii) {
			Constants.trgt = ii;

			TreeMap<Integer,List<Answer>> countMap = new TreeMap<>();
			TreeMap<Double,List<Answer>> scoreMap = new TreeMap<>();
			TreeMap<Double,List<Answer>> largestMap = new TreeMap<>();
			TreeMap<Integer,List<Answer>> fractMap = new TreeMap<>();
			TreeMap<Integer,List<Answer>> partialMap = new TreeMap<>();
			TreeMap<Integer,List<Answer>> partialOnlyMap = new TreeMap<>();
			int fractcnt = 0;
			int partcnt = 0;
			
			double taskscore = 0;
			int ttlqueries = 0;
			int goodqueries = 0;
			for (int i = Constants.start; i <= Constants.end; ++i) {
				// System.out.println("solving i = "+i+" ...");
				for (int j = i; j <= Constants.end; ++j) {
					for (int k = j; k <= Constants.end; ++k) {
						for (int l = k; l <= Constants.end; ++l) {
							// if ((i == j && j == k) || (j == k && k == l))continue;
							// if (i == 0 && j == 0)continue;
							++ttlqueries;
							Answer ans = new Answer();
							ans.numbers = new int[]{i,j,k,l};
							ans.trgt = Constants.trgt;
							EqnPackage rawsols = Solver.solve4(ans.numbers,Constants.trgt);
							ans.solutions = new ArrayList<>();
							ans.partials = new ArrayList<>();
							ans.minlarge = Double.MAX_VALUE;
							ans.score = 0;
							int cnt = rawsols.solutions.size();
							if (cnt == 0)continue;
							++goodqueries;
							int fracts = 0;
							for (Equation eqn : rawsols.solutions) {
								Solution sol = Solver.Convert(eqn,Constants.largedet);
								ans.solutions.add(sol);
								ans.minlarge = Math.min(ans.minlarge,sol.largest);
								ans.score += Math.pow(sol.score,Constants.INIT_DET);
								if (sol.isfract)++fracts;
							}
							ans.score /= cnt;
							ans.score = Math.pow(ans.score,1/Constants.INIT_DET);
							ans.score *= Math.pow(cnt,Constants.CNT_WEIGHT_DET);
							taskscore += ans.score;
							// System.out.println(i+" "+j+" "+k+" "+l+">> "+ans.score);
							for (Equation eqn : rawsols.partials) {
								Solution sol = Solver.Convert(eqn,Constants.largedet);
								ans.partials.add(sol);
							}
							if (ans.minlarge == Double.MAX_VALUE)ans.minlarge = -1;
							// if (ans.score == 0)ans.minlarge = -1;
							put(countMap,Integer.valueOf(cnt),ans);
							put(scoreMap,Double.valueOf(ans.score),ans);
							put(largestMap,Double.valueOf(ans.minlarge),ans);
							if (fracts == cnt) {
								put(fractMap,Integer.valueOf(cnt),ans);
								++fractcnt;
							}
							put(partialMap,Integer.valueOf(ans.partials.size()),ans);
							if (cnt == 0 && ans.partials.size() > 0) {
								put(partialOnlyMap,Integer.valueOf(ans.partials.size()),ans);
								++partcnt;
							}
						}
					}
				}
				// System.out.println("done in "+(System.currentTimeMillis()-t)+" ms");
			}
			taskscore /= goodqueries;
			DecimalFormat sf = new DecimalFormat("###0.00000");
			// System.out.println("target = "+ii);
			// System.out.println("\nout of all "+ttlqueries+" queries");
			// System.out.println(ttlqueries-countMap.firstEntry().getValue().size()+" have solutions,");
			// System.out.println(countMap.firstEntry().getValue().size()+" dont");
			// System.out.printf("%.3f\n",(ttlqueries-countMap.firstEntry().getValue().size())/(double)ttlqueries*100);
			System.out.printf("%.3f\n",taskscore);
			// System.out.println("% have full solutions\n");
		}
		// System.out.printf("%.3f",(ttlqueries-countMap.firstEntry().getValue().size()+partcnt)/(double)ttlqueries*100);
		// System.out.println("% atleast have partial or full solutions\n");
		// for (Answer ans : countMap.lastEntry().getValue()) {
		// 	System.out.println(Arrays.toString(ans.numbers)+" has "+ans.solutions.size()+" solution(s)");
		// 	for (Solution sol : ans.solutions) {
		// 		System.out.println(sol.exp+"="+Constants.trgt);
		// 	}
		// }
		// System.out.println();
		// if (scoreMap.size() > 1) {
		// 	for (Answer ans : scoreMap.higherEntry(0.0).getValue()) {
		// 		String fscr = sf.format(ans.score);
		// 		System.out.println(Arrays.toString(ans.numbers)+" has "+fscr+" score");
		// 		for (Solution sol : ans.solutions) {
		// 			System.out.println(sol.exp+"="+Constants.trgt);
		// 		}
		// 	}
		// 	System.out.println();
		// }
		// for (Integer cnt : countMap.keySet()) {
		// 	System.out.println(countMap.get(cnt).size()+" queries have "+cnt+" solution(s)");
		// }
		// System.out.println();
		// for (Double scr : scoreMap.keySet()) {
		// 	String fscr = sf.format(scr);
		// 	System.out.println(scoreMap.get(scr).size()+" queries have "+fscr+" score");
		// }
		// System.out.println();
		// System.out.println(countMap.get(0).size()+" queries have no solution(s)");
		// for (Answer ans : countMap.firstEntry().getValue()) {
		// 	System.out.println(Arrays.toString(ans.numbers));
		// }
		// System.out.println();
		// Map.Entry<Integer,List<Answer>> entry = countMap.higherEntry(0);
		// System.out.println(entry.getValue().size()+" queries have "+entry.getKey()+" solution(s)");
		// for (Answer ans : entry.getValue()) {
		// 	System.out.println(Arrays.toString(ans.numbers)+" has "+entry.getKey()+" solution(s)");
		// 	for (Solution sol : ans.solutions) {
		// 		System.out.println(sol.exp+"="+Constants.trgt);
		// 	}
		// }
		// System.out.println(fractcnt+" queries have fractional solutions only");
		// for (Integer cnt : fractMap.keySet()) {
		// 	System.out.println(fractMap.get(cnt).size()+" queries have "+cnt+" solution(s)");
		// 	for (Answer ans : fractMap.get(cnt)) {
		// 		System.out.println(Arrays.toString(ans.numbers));//+" have "+cnt+" solution(s)");
		// 		for (Solution sol : ans.solutions) {
		// 			System.out.println(sol.exp+"="+Constants.trgt);
		// 		}
		// 	}
		// }
		// System.out.println();
		// for (Double k : largestMap.descendingKeySet()) {
		// 	if (k <= Constants.largedet) {
		// 		break;
		// 	}
		// 	for (Answer ans : largestMap.get(k)) {
		// 		System.out.println(Arrays.toString(ans.numbers)+" approaches "+k);
		// 		for (Solution sol : ans.solutions) {
		// 			System.out.println(sol.exp+"="+Constants.trgt);
		// 		}
		// 	}
		// }
		// System.out.println(partcnt+" queries have partial solutions only");
		// for (Integer cnt : partialOnlyMap.keySet()) {
		// 	System.out.println(partialOnlyMap.get(cnt).size()+" queries have "+cnt+" partial solution(s)");
		// 	for (Answer ans : partialOnlyMap.get(cnt)) {
		// 		System.out.println(Arrays.toString(ans.numbers));//+" have "+cnt+" solution(s)");
		// 		for (Solution sol : ans.partials) {
		// 			System.out.println(sol.exp+"="+sol.result);
		// 		}
		// 	}
		// }
	}
}