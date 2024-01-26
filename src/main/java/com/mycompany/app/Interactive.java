import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.util.Scanner;

import java.util.Collections;
import java.util.Comparator;

import java.util.Random;

import java.text.DecimalFormat;

class Element {
	int[] numbers;
	double score;
}

class ElementComp implements Comparator<Element> {
    @Override
    public int compare(Element ele1, Element ele2) {
    	return Double.compare(ele1.score,ele2.score);
    }
}

public class Interactive {

    private static boolean isStrInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static List<Element> gendata() {
   		List<Element> list = new ArrayList<>();
    	for (int i = Constants.start; i <= Constants.end; ++i) {
			// System.out.println("solving i = "+i+" ...");
			for (int j = i; j <= Constants.end; ++j) {
				// System.out.println("solving j = "+j+" ...");
				for (int k = j; k <= Constants.end; ++k) {
					for (int l = k; l <= Constants.end; ++l) {
						// if ((i == j && j == k) || (j == k && k == l))continue;
						// if (i == 0 && j == 0)continue;
						Element ele = new Element();
						ele.numbers = new int[]{i,j,k,l};
						EqnPackage rawsols = Solver.solve4(ele.numbers,Constants.trgt);
						ele.score = 0;
						int cnt = rawsols.solutions.size();
						if (cnt == 0)continue;
						int fracts = 0;
						for (Equation eqn : rawsols.solutions) {
							Solution sol = Solver.Convert(eqn,Constants.largedet);
							ele.score += Math.pow(sol.score,Constants.INIT_DET);
						}
						ele.score /= cnt;
						ele.score = Math.pow(ele.score,1/Constants.INIT_DET);
						ele.score *= Math.pow(cnt,Constants.CNT_WEIGHT_DET);
						list.add(ele);
					}
				}
			}
			// System.out.println("done in "+(System.currentTimeMillis()-t)+" ms");
		}
		return list;
    }

    private static double calculate(String exp) {
		int left1 = exp.indexOf('(');
		int left2 = exp.indexOf('(',left1+1);
		int right2 = exp.lastIndexOf(')');
		int right1 = exp.lastIndexOf(')',right2-1);
		int[] opi = {-1,-1,-1};
		char[] oper = {'X','X','X'};
		int[] opparity = {-1,-1,-1};
		int idx = 0;
		for (int i = 0; i < exp.length(); ++i) {
			char c = exp.charAt(i);
			if (c == '+' || c == '-' || c == '*' || c == '/') {
				oper[idx] = c;
				opi[idx++] = i;
			}
		}
		int w = -1, x = -1, y = -1, z = -1;
		double p = -1, q = -1, r = Double.MAX_VALUE;
		if (right1 < left2) {
			// (oxo)x(oxo)
			w = Integer.parseInt(exp.substring(left1+1,opi[0]));
			x = Integer.parseInt(exp.substring(opi[0]+1,right1));
			y = Integer.parseInt(exp.substring(left2+1,opi[2]));
			z = Integer.parseInt(exp.substring(opi[2]+1,right2));
			p = Solver.compute(w,oper[0],x);
			q = Solver.compute(y,oper[2],z);
			r = Solver.compute(p,oper[1],q);
		} else if (left1 == 0 && left2 == 1) {
			// ((oxo)xo)xo
			w = Integer.parseInt(exp.substring(left2+1,opi[0]));
			x = Integer.parseInt(exp.substring(opi[0]+1,right1));
			y = Integer.parseInt(exp.substring(opi[1]+1,right2));
			z = Integer.parseInt(exp.substring(opi[2]+1));
			p = Solver.compute(w,oper[0],x);
			q = Solver.compute(p,oper[1],y);
			r = Solver.compute(q,oper[2],z);
		} else if (right2 == exp.length()-1 && right1 == exp.length()-2) {
			// ox(ox(oxo))
			w = Integer.parseInt(exp.substring(0,opi[0]));
			x = Integer.parseInt(exp.substring(left1+1,opi[1]));
			y = Integer.parseInt(exp.substring(left2+1,opi[2]));
			z = Integer.parseInt(exp.substring(opi[2]+1,right1));
			p = Solver.compute(y,oper[2],z);
			q = Solver.compute(x,oper[1],p);
			r = Solver.compute(w,oper[0],q);
		} else if (left1 == 0) {
			// (ox(oxo))xo
			w = Integer.parseInt(exp.substring(left1+1,opi[0]));
			x = Integer.parseInt(exp.substring(left2+1,opi[1]));
			y = Integer.parseInt(exp.substring(opi[1]+1,right1));
			z = Integer.parseInt(exp.substring(opi[2]+1));
			p = Solver.compute(x,oper[1],y);
			q = Solver.compute(w,oper[0],p);
			r = Solver.compute(q,oper[2],z);
		} else if (right2 == exp.length()-1) {
			// ox((oxo)xo)
			w = Integer.parseInt(exp.substring(0,opi[0]));
			x = Integer.parseInt(exp.substring(left2+1,opi[1]));
			y = Integer.parseInt(exp.substring(opi[1]+1,right1));
			z = Integer.parseInt(exp.substring(opi[2]+1,right2));
			p = Solver.compute(x,oper[1],y);
			q = Solver.compute(p,oper[2],z);
			r = Solver.compute(w,oper[0],q);
		}
		return r;
    }

    public static double precise(double x) {
    	return Math.floor(x*10000+0.1)/10000;
    }

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Random random = new Random();
		DecimalFormat sf = new DecimalFormat("###0.00000");
		String cmd;
		int[] numbers = {-1,-1,-1,-1};
		List<Element> list = gendata();
		Collections.sort(list,new ElementComp());
		TreeMap<String,Integer> ordermap = new TreeMap<>();
		for (int i = 0; i < list.size(); ++i) {
			ordermap.put(Arrays.toString(list.get(i).numbers),i);
		}
		int ttl = list.size();
		// System.out.println(ordermap.get("[1, 1, 4, 6]"));
		// System.out.println(Arrays.toString(list.get(0).numbers)+list.get(0).score);
		System.out.println("booted");
		while(true) {
			System.out.println("current target: "+Constants.trgt+", current largedet: "+Constants.largedet);
			System.out.println("current range: "+Constants.start+" through "+Constants.end);
			System.out.println("available prompts: setconst, solve, generate, quit");
			cmd = scanner.next();
			if (cmd.equals("quit"))break;
			else if (cmd.equals("setconst")) {
				System.out.println();
				System.out.println("enter new target:");
				// target = scanner.next();
				cmd = scanner.next();
				if (cmd.equals("back")) {
					System.out.println();
					continue;
				}
				Constants.trgt = Double.valueOf(cmd);
				System.out.println(Constants.trgt+" ok");
				System.out.println("enter new largedet:");
				// largedet = scanner.next();
				cmd = scanner.next();
				if (cmd.equals("back")) {
					System.out.println();
					continue;
				}
				Constants.largedet = Double.valueOf(cmd);
				System.out.println(Constants.largedet+" ok");
				System.out.println("enter new start:");
				// largedet = scanner.next();
				cmd = scanner.next();
				if (cmd.equals("back")) {
					System.out.println();
					continue;
				}
				Constants.start = Integer.valueOf(cmd);
				System.out.println(Constants.start+" ok");
				System.out.println("enter new end:");
				// largedet = scanner.next();
				cmd = scanner.next();
				if (cmd.equals("back")) {
					System.out.println();
					continue;
				}
				Constants.end = Integer.valueOf(cmd);
				System.out.println(Constants.end+" ok");

				list = gendata();
				Collections.sort(list,new ElementComp());
				ordermap = new TreeMap<>();
				for (int i = 0; i < list.size(); ++i) {
					ordermap.put(Arrays.toString(list.get(i).numbers),i);
				}
				ttl = list.size();
			}
			else if (cmd.equals("solve")) {
				System.out.println("enter a, b, c, d:");
				boolean exit = false;
				boolean quit = false;
				for (int i = 0; i < 4; ++i) {
					cmd = scanner.next();
					if (cmd.equals("back")) {
						System.out.println();
						exit = true;
						break;
					} else if (cmd.equals("quit")) {
						quit = true;
						break;
					}
					numbers[i] = Integer.valueOf(cmd);
				}
				Arrays.sort(numbers);
				if (exit)continue;
				if (quit)break;
				EqnPackage rawsols = Solver.solve4(numbers,Constants.trgt);
				List<Solution> solutions = new ArrayList<>();
				List<Solution> partials = new ArrayList<>();
				List<Solution> normalsols = new ArrayList<>();
				List<Solution> largesols = new ArrayList<>();
				List<Solution> fractsols = new ArrayList<>();
				List<Solution> insanesols = new ArrayList<>();
				double minlarge = Double.MAX_VALUE;
				double taskscore = 0;
				int cnt = rawsols.solutions.size();
				for (Equation eqn : rawsols.solutions) {
					Solution sol = Solver.Convert(eqn,Constants.largedet);
					taskscore += Math.pow(sol.score,Constants.INIT_DET);
					minlarge = Math.min(minlarge,sol.largest);
					if (sol.largest > Constants.largedet && sol.isfract) {
						insanesols.add(sol);
					} else if (sol.largest > Constants.largedet) {
						largesols.add(sol);
					} else if (sol.isfract) {
						fractsols.add(sol);
					} else {
						normalsols.add(sol);
					}
					solutions.add(sol);
				}
				taskscore /= cnt;
				taskscore = Math.pow(taskscore,1/Constants.INIT_DET);
				taskscore *= Math.pow(cnt,Constants.CNT_WEIGHT_DET);
				for (Equation eqn : rawsols.partials) {
					Solution sol = Solver.Convert(eqn,Constants.largedet);
					partials.add(sol);
				}
				if (rawsols.solutions.size() == 0)minlarge = -1;
				while(true) {
					System.out.println("current task: "+Arrays.toString(numbers)+" = "+Constants.trgt);
					System.out.println("available prompts: rawsols, sols, info, back, quit");
					cmd = scanner.next();
					if (cmd.equals("back")) {
						System.out.println();
						break;
					}
					if (cmd.equals("quit"))return;
					if (cmd.equals("rawsols")) {
						System.out.println();
						System.out.println("there are "+solutions.size()+" solutions");
						for (Solution sol : solutions) {
							System.out.println(sol.exp+"="+precise(sol.result));
						}
						System.out.println();
					} else if (cmd.equals("sols")) {
						System.out.println();
						if (normalsols.size() > 0) {
							System.out.println("there are "+normalsols.size()+" normal solutions");
							for (Solution sol : normalsols) {
								System.out.println(sol.exp+"="+precise(sol.result));
								System.out.println("estimated score: "+sf.format(sol.score));
							}
							System.out.println();
						}
						if (largesols.size() > 0) {
							System.out.println("there are "+largesols.size()+" large solutions");
							for (Solution sol : largesols) {
								System.out.println(sol.exp+"="+precise(sol.result)
									+" which approaches "+sol.largest);
								System.out.println("estimated score: "+sf.format(sol.score));
							}
							System.out.println();
						}
						if (fractsols.size() > 0) {
							System.out.println("there are "+fractsols.size()+" fractional solutions");
							for (Solution sol : fractsols) {
								System.out.println(sol.exp+"="+precise(sol.result));
								System.out.println("estimated score: "+sf.format(sol.score));
							}
							System.out.println();
						}
						if (insanesols.size() > 0) {
							System.out.println("there are "+insanesols.size()+" large fractional solutions");
							for (Solution sol : insanesols) {
								System.out.println(sol.exp+"="+precise(sol.result)
									+" which approaches "+sol.largest);
								System.out.println("estimated score: "+sf.format(sol.score));
							}
							System.out.println();
						}
						if (partials.size() > 0) {
							System.out.println("there are "+partials.size()+" partial solutions");
							for (Solution sol : partials) {
								System.out.println(sol.exp+"="+precise(sol.result));
							}
							System.out.println();
						}
					} else if (cmd.equals("info")) {
						System.out.println();
						System.out.println(numbers[0]+" "+numbers[1]+" "+numbers[2]+" "+numbers[3]+" = "+Constants.trgt);
						System.out.println(solutions.size()+" solutions");
						System.out.println("approaches atleast: "+sf.format(minlarge));
						System.out.println("estimated score: "+sf.format(taskscore));
						System.out.println(partials.size()+" partial solutions");
						System.out.println("is the #"+(ordermap.get(Arrays.toString(numbers))+1)+" easiest task out of all "+list.size()+" possible tasks");
						System.out.println();
					} else System.out.println("\ninvalid prompt\n");
				}
			} else if (cmd.equals("generate")) {
				System.out.println();
				while(true) {
					System.out.println("current target: "+Constants.trgt+", current largedet: "+Constants.largedet);
					System.out.println("current range: "+Constants.start+" through "+Constants.end);
					System.out.println("type \"done\" to continue");
					System.out.println("available prompts: 1, 2, 3, 4, 5, back, quit");
					cmd = scanner.next();
					if (cmd.equals("back")) {
						System.out.println();
						break;
					}
					if (cmd.equals("quit"))return;
					if (isStrInt(cmd)) {
						System.out.println();
						int val = Integer.parseInt(cmd);
						int rand = random.nextInt(100000000);
						int a = (val-1)*ttl/5, b = val*ttl/5;
						int idx = (rand%(b-a))+a;
						// System.out.println(a+" "+b+" "+val+" "+rand+" "+rand%(b-a));
						numbers = (list.get(idx)).numbers;
						EqnPackage rawsols = Solver.solve4(numbers,Constants.trgt);
						List<Solution> solutions = new ArrayList<>();
						List<Solution> partials = new ArrayList<>();
						List<Solution> normalsols = new ArrayList<>();
						List<Solution> largesols = new ArrayList<>();
						List<Solution> fractsols = new ArrayList<>();
						List<Solution> insanesols = new ArrayList<>();
						double minlarge = Double.MAX_VALUE;
						double taskscore = 0;
						int cnt = rawsols.solutions.size();
						for (Equation eqn : rawsols.solutions) {
							Solution sol = Solver.Convert(eqn,Constants.largedet);
							taskscore += Math.pow(sol.score,Constants.INIT_DET);
							minlarge = Math.min(minlarge,sol.largest);
							if (sol.largest > Constants.largedet && sol.isfract) {
								insanesols.add(sol);
							} else if (sol.largest > Constants.largedet) {
								largesols.add(sol);
							} else if (sol.isfract) {
								fractsols.add(sol);
							} else {
								normalsols.add(sol);
							}
							solutions.add(sol);
						}
						taskscore /= cnt;
						taskscore = Math.pow(taskscore,1/Constants.INIT_DET);
						taskscore *= Math.pow(cnt,Constants.CNT_WEIGHT_DET);
						for (Equation eqn : rawsols.partials) {
							Solution sol = Solver.Convert(eqn,Constants.largedet);
							partials.add(sol);
						}
						if (rawsols.solutions.size() == 0)minlarge = -1;
						System.out.println(numbers[0]+" "+numbers[1]+" "+numbers[2]+" "+numbers[3]+" = "+Constants.trgt);
						while(true) {
							cmd = scanner.next();
							System.out.println(">> "+cmd);
							if (cmd.equals("done"))break;
							else {
								double res = calculate(cmd);
								if (Solver.fsEquals(res,Double.MAX_VALUE))continue;
								else {
									System.out.println("= "+precise(res));
									if (Solver.fsEquals(res,Constants.trgt))break;
								}
							}
						}
						while(true) {
							System.out.println("current task: "+Arrays.toString(numbers)+" = "+Constants.trgt);
							System.out.println("available prompts: rawsols, sols, info, back, quit");
							cmd = scanner.next();
							if (cmd.equals("back")) {
								System.out.println();
								break;
							}
							if (cmd.equals("quit"))return;
							if (cmd.equals("rawsols")) {
								System.out.println();
								System.out.println("there are "+solutions.size()+" solutions");
								for (Solution sol : solutions) {
									System.out.println(sol.exp+"="+precise(sol.result));
								}
								System.out.println();
							} else if (cmd.equals("sols")) {
								System.out.println();
								if (normalsols.size() > 0) {
									System.out.println("there are "+normalsols.size()+" normal solutions");
									for (Solution sol : normalsols) {
										System.out.println(sol.exp+"="+precise(sol.result));
										System.out.println("estimated score: "+sf.format(sol.score));
									}
									System.out.println();
								}
								if (largesols.size() > 0) {
									System.out.println("there are "+largesols.size()+" large solutions");
									for (Solution sol : largesols) {
										System.out.println(sol.exp+"="+precise(sol.result)
											+" which approaches "+sol.largest);
										System.out.println("estimated score: "+sf.format(sol.score));
									}
									System.out.println();
								}
								if (fractsols.size() > 0) {
									System.out.println("there are "+fractsols.size()+" fractional solutions");
									for (Solution sol : fractsols) {
										System.out.println(sol.exp+"="+precise(sol.result));
										System.out.println("estimated score: "+sf.format(sol.score));
									}
									System.out.println();
								}
								if (insanesols.size() > 0) {
									System.out.println("there are "+insanesols.size()+" large fractional solutions");
									for (Solution sol : insanesols) {
										System.out.println(sol.exp+"="+precise(sol.result)
											+" which approaches "+sol.largest);
										System.out.println("estimated score: "+sf.format(sol.score));
									}
									System.out.println();
								}
								if (partials.size() > 0) {
									System.out.println("there are "+partials.size()+" partial solutions");
									for (Solution sol : partials) {
										System.out.println(sol.exp+"="+precise(sol.result));
									}
									System.out.println();
								}
							} else if (cmd.equals("info")) {
								System.out.println();
								System.out.println(numbers[0]+" "+numbers[1]+" "+numbers[2]+" "+numbers[3]+" = "+Constants.trgt);
								System.out.println(solutions.size()+" solutions");
								System.out.println("approaches atleast: "+sf.format(minlarge));
								System.out.println("estimated score: "+sf.format(taskscore));
								System.out.println(partials.size()+" partial solutions");
								System.out.println("is the #"+(ordermap.get(Arrays.toString(numbers))+1)+" easiest task out of all "+list.size()+" possible tasks");
								System.out.println();
							} else System.out.println("\ninvalid prompt\n");
						}
					} else System.out.println("\ninvalid prompt\n");
				}
			} else System.out.println("\ninvalid prompt\n");
		}
		return;
	}
}