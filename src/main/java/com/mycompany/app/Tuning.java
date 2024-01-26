import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Comparator;

import java.text.DecimalFormat;

import java.lang.reflect.Field;

public class Tuning {

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

		List<String> fields = new ArrayList<>();
		fields.add("DEFAULT_SCORE");
		fields.add("CNT_WEIGHT_DET");
		fields.add("INIT_DET");
		fields.add("ABCD_WEIGHT");
		fields.add("AABC_WEIGHT");
		fields.add("AABB_WEIGHT");
		fields.add("AAAB_WEIGHT");
		fields.add("AAAA_WEIGHT");
		fields.add("CONSECUTIVEWEIGHT");
		fields.add("ONETWOWEIGHT");
		fields.add("ALTERWEIGHT");
		fields.add("PAIRSWEIGHT");
		fields.add("LARGEWEIGHT");
		fields.add("LARGERANGE");
		fields.add("FRACTWEIGHT");
		fields.add("USELESS1");
		fields.add("DEAD1");
		fields.add("DEAD0");
		fields.add("SUM_WEIGHT");
		fields.add("DISTRIBUTION_WEIGHT");

		for (int j = 2; j >= 0; j -= 1) {
			for (int ii = 0; ii < 10; ++ii) {
				for (String fieldName : fields) {
					try {
						Field field = Constants.class.getField(fieldName);
						int a = 0, b = 1;

						if (field.getType().isArray()) {
							b = ((double[])field.get(null)).length;
						}

						for (int k = a; k < b; ++k) {
							double w;
							if (field.getType().isArray()) {
								double[] cur = (double[])field.get(null);
								w = cur[k];
							} else {
								w = (double)field.get(null);
							}
							double x = w-0.01, y = w+0.01;
							double z = 0.00011;
							if (j == 0) {
								x = w-0.01;
								y = w+0.01;
								z = 0.00011;
							} else if (j == 1) {
								x = w-0.1;
								y = w+0.1;
								z = 0.0011;
							} else if (j == 2) {
								x = w-1;
								y = w+1;
								z = 0.011;
							}
							if (fieldName == "DEFAULT_SCORE") {
								if (j == 0) {
									x = w-0.1;
									y = w+0.1;
									z = 0.0011;
								} else if (j == 1) {
									x = w-1;
									y = w+1;
									z = 0.011;
								} else if (j == 2) {
									x = w-10;
									y = w+10;
									z = 0.11;
								}
							}
							if (fieldName == "LARGERANGE") {
								if (j == 0) {
									x = w-1;
									y = w+1;
									z = 0.011;
								} else if (j == 1) {
									x = w-10;
									y = w+10;
									z = 0.11;
								} else if (j == 2) {
									x = w-100;
									y = w+100;
									z = 1;
								}
							}
							String display = fieldName;
							if (field.getType().isArray()) {
								display = display+"["+k+"]";
							}
							while (true) {
								double min1 = Double.MAX_VALUE;
								double ideal = -1;
								double mavge = Double.MAX_VALUE;

								// for (double i = 0; i <= 100; i += 0.11) {
									// i = Math.floor(i*10)/10;
								double ymax = y;
									if (z == 0.11)ymax = y+0.01;
									else if (z == 0.011)ymax = y+0.001;
									else if (z == 0.0011)ymax = y+0.0001;
									else if (z == 0.00011)ymax = y+0.00001;
								for (double i = x; i <= ymax; i += z) {
									if (z == 0.11)i = Math.floor(i*10)/10;
									else if (z == 0.011)i = Math.floor(i*100)/100;
									else if (z == 0.0011)i = Math.floor(i*1000)/1000;
									else if (z == 0.00011)i = Math.floor(i*10000)/10000;
									else if (z == 1)i = Math.floor(i);
									List<EvalTask> actual = new ArrayList<>();
									List<EvalTask> computed = new ArrayList<>();

									if (field.getType().isArray()) {
										double[] cur = (double[])field.get(null);
										cur[k] = i;
										field.set(null,cur);
									} else {
										field.set(null,i);
									}

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
									double avge = 0;
									int idx = 0;
									for (EvalTask task : actual) {
										task.order = ++idx;
										// System.out.println(Arrays.toString(task.numbers)+" {"+task.order+"} ("+task.evalorder+")");
										// System.out.println(task.time+" "+task.score);
										rmse += Math.pow(Math.floor((task.time-task.score+0.0001)*10000)/10000,2);
										avge += Math.abs(Math.floor((task.time-task.score+0.0001)*10000)/10000);
									}
									rmse /= ttlqueries;
									avge /= ttlqueries;
									rmse = Math.sqrt(rmse);
									// System.out.println(display+": "+i+" RMSE: "+rmse);
									if (rmse < min1) {
										min1 = rmse;
										ideal = i;
										mavge = avge;
									}
								}
							// 	System.out.println();
								System.out.println("min1: "+min1);
								System.out.println("ideal: "+ideal);
								System.out.println("mavge: "+mavge);
							// }
								System.out.println("done in "+(System.currentTimeMillis()-t)+" ms");
								if (ideal <= x || ideal >= y) {
									if (ideal <= x) {
										if (j == 0) {
											x -= 0.02;
										} else if (j == 1) {
											x -= 0.2;
										} else if (j == 2) {
											x -= 2;
										}
									} else {
										if (j == 0) {
											y += 0.02;
										} else if (j == 1) {
											y += 0.2;
										} else if (j == 2) {
											y += 2;
										}
									}
									System.out.println("\njank\n");
								} else {
									if (field.getType().isArray()) {
										double[] cur = (double[])field.get(null);
										cur[k] = ideal;
										field.set(null,cur);
									} else {
										field.set(null,ideal);
									}
									double value;
									if (field.getType().isArray()) {
										double[] cur = (double[])field.get(null);
										value = cur[k];
									} else {
										value = (double)field.get(null);
									}
									System.out.println("\nSET "+display+" = "+value+"\n");
									break;
								}
							}
						}
					} catch (NoSuchFieldException | IllegalAccessException e) {
		           		e.printStackTrace();
		        	}
		        }
			}
		}
	}
}