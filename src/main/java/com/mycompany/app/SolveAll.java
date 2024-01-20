import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.text.DecimalFormat;

class Task {
	int order;
	int[] numbers;
	double time;

    public Task(int order, int[] numbers, double time) {
        this.order = order;
        this.numbers = numbers;
        this.time = time;
    }
}


class Answer {
	int[] numbers;
	List<Solution> solutions;
	List<Solution> partials;
	double minlarge;
	double score;
}

public class SolveAll {

	public static final double trgt = 24;

	public static final double largecap = trgt*1.5;

	public static final int start = 1;

	public static final int end = 13;

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
			System.out.println("solving i = "+i+" ...");
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
			System.out.println("done in "+(System.currentTimeMillis()-t)+" ms");
		}
		DecimalFormat sf = new DecimalFormat("###0.00");
		System.out.println("\nout of all "+ttlqueries+" queries");
		System.out.println(ttlqueries-countMap.firstEntry().getValue().size()+" have solutions,");
		System.out.println(countMap.firstEntry().getValue().size()+" dont");
		System.out.printf("%.3f",(ttlqueries-countMap.firstEntry().getValue().size())/(double)ttlqueries*100);
		System.out.println("% have full solutions\n");
		System.out.printf("%.3f",(ttlqueries-countMap.firstEntry().getValue().size()+partcnt)/(double)ttlqueries*100);
		System.out.println("% atleast have partial or full solutions\n");
		for (Answer ans : countMap.lastEntry().getValue()) {
			System.out.println(Arrays.toString(ans.numbers)+" has "+ans.solutions.size()+" solution(s)");
			for (Solution sol : ans.solutions) {
				System.out.println(sol.exp+"="+trgt);
			}
		}
		System.out.println();
		if (scoreMap.size() > 1) {
			for (Answer ans : scoreMap.higherEntry(0.0).getValue()) {
				String fscr = sf.format(ans.score);
				System.out.println(Arrays.toString(ans.numbers)+" has "+fscr+" score");
				for (Solution sol : ans.solutions) {
					System.out.println(sol.exp+"="+trgt);
				}
			}
			System.out.println();
		}
		for (Integer cnt : countMap.keySet()) {
			System.out.println(countMap.get(cnt).size()+" queries have "+cnt+" solution(s)");
		}
		System.out.println();
		for (Double scr : scoreMap.keySet()) {
			String fscr = sf.format(scr);
			System.out.println(scoreMap.get(scr).size()+" queries have "+fscr+" score");
		}
		System.out.println();
		System.out.println(countMap.get(0).size()+" queries have no solution(s)");
		for (Answer ans : countMap.firstEntry().getValue()) {
			System.out.println(Arrays.toString(ans.numbers));
		}
		System.out.println();
		Map.Entry<Integer,List<Answer>> entry = countMap.higherEntry(0);
		System.out.println(entry.getValue().size()+" queries have "+entry.getKey()+" solution(s)");
		for (Answer ans : entry.getValue()) {
			System.out.println(Arrays.toString(ans.numbers)+" has "+entry.getKey()+" solution(s)");
			for (Solution sol : ans.solutions) {
				System.out.println(sol.exp+"="+trgt);
			}
		}
		System.out.println(fractcnt+" queries have fractional solutions only");
		for (Integer cnt : fractMap.keySet()) {
			System.out.println(fractMap.get(cnt).size()+" queries have "+cnt+" solution(s)");
			for (Answer ans : fractMap.get(cnt)) {
				System.out.println(Arrays.toString(ans.numbers));//+" have "+cnt+" solution(s)");
				for (Solution sol : ans.solutions) {
					System.out.println(sol.exp+"="+trgt);
				}
			}
		}
		System.out.println();
		for (Double k : largestMap.descendingKeySet()) {
			if (k <= largecap) {
				break;
			}
			for (Answer ans : largestMap.get(k)) {
				System.out.println(Arrays.toString(ans.numbers)+" approaches "+k);
				for (Solution sol : ans.solutions) {
					System.out.println(sol.exp+"="+trgt);
				}
			}
		}
		System.out.println(partcnt+" queries have partial solutions only");
		for (Integer cnt : partialOnlyMap.keySet()) {
			System.out.println(partialOnlyMap.get(cnt).size()+" queries have "+cnt+" partial solution(s)");
			for (Answer ans : partialOnlyMap.get(cnt)) {
				System.out.println(Arrays.toString(ans.numbers));//+" have "+cnt+" solution(s)");
				for (Solution sol : ans.partials) {
					System.out.println(sol.exp+"="+sol.result);
				}
			}
		}
	}
}