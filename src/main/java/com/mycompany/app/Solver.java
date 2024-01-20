import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

class Equation {
	String exp;
	double result;
	double score;

	public Equation(String exp, double result, double score) {
		this.exp = exp;
		this.result = result;
		this.score = score;
	}
}

class EqnPackage {
	List<Equation> solutions;
	List<Equation> partials;
}

class Solution extends Equation {
	double largest;
	boolean isfract;

    public Solution(String exp, double result, double score, double largest, boolean isfract) {
        super(exp, result, score); // Call the constructor of the superclass
        this.largest = largest;
        this.isfract = isfract;
    }
}

public class Solver extends Expressions {
	private static HashMap<String, Method> methodMap = new HashMap<>();

	static {
		for (Field field : Expressions.class.getFields()) {
			String fieldName = field.getName();
			if (!fieldName.startsWith("EXP_")) {
				continue;
			}
			try {
				for (String expression : (String[]) field.get(null)) {
					methodMap.put(expression, Methods.class
						.getMethod(callcode(expression),
							double.class, double.class, double.class, double.class));
				}
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static EqnPackage filter(EqnPackage bucket, List<String> patterns) {
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		for (Equation expression : bucket.solutions) {
			boolean pass = true;
			for (String pattern : patterns) {
				if (expression.exp.indexOf(pattern) >= 0) {
					pass = false;
					break;
				}
			}
			if (!pass)continue;
			res.solutions.add(expression);
		}
		for (Equation expression : bucket.partials) {
			boolean pass = true;
			for (String pattern : patterns) {
				if (expression.exp.indexOf(pattern) >= 0) {
					pass = false;
					break;
				}
			}
			if (!pass)continue;
			res.partials.add(expression);
		}
		return res;
	}

	private static EqnPackage append(EqnPackage res, EqnPackage extras, String suffix) {
		for (Equation solution : extras.solutions) {
			res.solutions.add(new Equation("("+solution.exp+suffix+")",solution.result,0));
		}
		for (Equation partsol : extras.partials) {
			res.partials.add(new Equation("("+partsol.exp+suffix+")",partsol.result,0));
		}
		return res;
	}

	private static EqnPackage solve(String[] expressions, double trgt, int... n) {
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		Object[] args = new Object[4];
		for (int i = 0; i < n.length; ++i) {
			args[i] = Double.valueOf(n[i]);
		}
		for (int i = n.length; i < 4; ++i) {
			args[i] = Double.valueOf(0);
		}
		for (String expression : expressions) {
			double val;
			try {
				val = ((Number)methodMap.get(expression).invoke(null,args)).doubleValue();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
			if (Math.abs(val-trgt) < 1.000_001) {
				String value = expression;
				for (int i = 0; i < n.length; ++i) {
					value = value.replace(""+(char)('a'+i),""+n[i]);
				}
				int left = value.lastIndexOf('(');
				int right = value.indexOf(')');
				if (left < right) {
					// System.out.println(n.length+" and "+value.substring(left+2,right));
					if (value.substring(left+2,right).equals("/0"))continue;
					// System.out.println("success");
				}
				if (left == -1 && right == -1) {
					if (value.substring(1,3).equals("/0"))continue;
				}
				if (Math.abs(val-trgt) < .000_001) res.solutions.add(new Equation("("+value+")",val,0));
				else res.partials.add(new Equation("("+value+")",val,0));
				// System.out.println("--- "+value+" gets "+val);
			}
		}
		return res;
	}

	public static int[] flush(int[] arr, int... n) {
		int[] res = new int[arr.length-n.length];
		int ni = 0, idx = 0;
		for (int i = 0; i < arr.length; ++i) {
			if (ni != n.length && arr[i] == n[ni]) {
				++ni;
			} else {
				res[idx++] = arr[i];
			}
		}
		return res;
	}

	public static EqnPackage solve0(int[] arr, double trgt) {
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		if (Math.abs(0-trgt) < .000_001)res.solutions.add(new Equation("",0,0));
		else if (Math.abs(0-trgt) < 1.000_001)res.partials.add(new Equation("",0,0));
		return res;
	}

	public static EqnPackage solve1(int[] arr, double trgt) {
		// System.out.println("s1 "+Arrays.toString(arr)+" "+trgt);
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		if (Math.abs(arr[0]-trgt) < .000_001)res.solutions.add(new Equation(""+arr[0],arr[0],0));
		else if (Math.abs(arr[0]-trgt) < 1.000_001)res.partials.add(new Equation(""+arr[0],arr[0],0));
		// System.out.println("s1 found "+res.size());
		return res;
	}

	public static EqnPackage solve2(int[] arr, double trgt) {
		Arrays.sort(arr);
		int a = arr[0];
		int b = arr[1];
		int cnt[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		++cnt[a];
		++cnt[b];
		// System.out.println("s2 "+Arrays.toString(arr)+" "+trgt);
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		if (a == b) {
			res = solve(EXP_AA,trgt,a);
		} else {
			res = solve(EXP_AB,trgt,a,b);
		}
		if (cnt[0] >= 1) { // 0+0 = 0-0
			res = filter(res,FILTER0);
			append(res,solve1(flush(arr,0),trgt),"+0");
			append(res,solve0(flush(arr,0,b),trgt),"0*"+b);
		}
		if (cnt[1] >= 1) { // 5/1 = 5/1
			res = filter(res,FILTER1);
			append(res,solve1(flush(arr,1),trgt),"*1");
		}
		if (cnt[2] >= 2) { // 2+2 = 2*2
			res = filter(res,FILTER2);
		}
		if (cnt[2] >= 1 && cnt[4] >= 1) { // 4/2 = 4-2
			res = filter(res,FILTER4);
		}
		// System.out.println("s2 found "+res.size());
		return res;
	}
	// ((4-5)*3)+8 , 8-(3*(5-4)) // filtered at pregen
	// 8+(3*(4-5)) , (8-3)*(5-4) //
	// 8-(3*(5-4))

	public static EqnPackage solve3(int[] arr, double trgt) {
		Arrays.sort(arr);
		int a = arr[0];
		int b = arr[1];
		int c = arr[2];
		int cnt[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		++cnt[a];
		++cnt[b];
		++cnt[c];
		// System.out.println("s3 "+Arrays.toString(arr)+" "+trgt);
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		if (a == b) {
			if (b == c) {
				res = solve(EXP_AAA,trgt,a);
			} else {
				res = solve(EXP_AAB,trgt,a,c);
			}
		} else {
			if (b == c) {
				res = solve(EXP_AAB,trgt,b,a);
			} else {
				res = solve(EXP_ABC,trgt,a,b,c);
			}
		}
		if (cnt[0] >= 1) { // (5+0)+0 = (5+0)-0
			res = filter(res,FILTER0);
			append(res,solve2(flush(arr,0),trgt),"+0");
			append(res,solve1(flush(arr,0,b),trgt),"+(0*"+b+")");
			if (c != b)append(res,solve1(flush(arr,0,c),trgt),"+(0*"+c+")");
			append(res,solve0(flush(arr,0,b,c),trgt),"0*("+b+"+"+c+")");
		}
		if (cnt[1] >= 1) {
			res = filter(res,FILTER1);
			append(res,solve2(flush(arr,1),trgt),"*1");
		}
		if (cnt[2] >= 2) {
			res = filter(res,FILTER2);
		}
		if (cnt[2] >= 1 && cnt[4] >= 1) {
			res = filter(res,FILTER4);
		}
		for (int i = 2; i >= 0; --i) {
			for (int j = i-1; j >= 0; --j) {
				if (arr[i]-arr[j] == 1) {
					res = filter(res,DIFF1(arr[i],arr[j]));
					append(res,solve1(flush(arr,arr[j],arr[i]),trgt),"*("+arr[i]+"-"+arr[j]+")");
				}
				char m1 = 'X';
				if (arr[i]+arr[j] == 2)m1 = '+';
				if (arr[j] != 0 && arr[i]/arr[j] == 2)m1 = '/';
				if (arr[i]-arr[j] == 2)m1 = '-';
				if (m1 != 'X') {
					res = filter(res,DIFF2(arr[i],m1,arr[j],0,'X',0));	
					if (m1 == '+') {
						res = filter(res,DIFF2(arr[j],'+',arr[i],0,'X',0));	
					}
				}
				m1 = 'X';
				if (arr[i]+arr[j] == 4)m1 = '+';
				if (arr[j] != 0 && arr[i]/arr[j] == 4)m1 = '/';
				if (arr[i]-arr[j] == 4)m1 = '-';
				if (m1 != 'X') {
					res = filter(res,DIFF4(arr[i],m1,arr[j],0,'X',0));	
					if (m1 == '+') {
						res = filter(res,DIFF4(arr[j],'+',arr[i],0,'X',0));	
					}
				}
			}
		}
		res = filter(res,CLONES(arr));
		// System.out.println("s3 found "+res.size());
		return res;
	}

	public static EqnPackage solve4(int[] arr, double trgt) {
		Arrays.sort(arr);
		int a = arr[0];
		int b = arr[1];
		int c = arr[2];
		int d = arr[3];
		int cnt[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		++cnt[a];
		++cnt[b];
		++cnt[c];
		++cnt[d];
		// System.out.println("s4 "+Arrays.toString(arr)+" "+trgt);
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		if (a == b) {
			if (b == c) {
				if (c == d) {
					res = solve(EXP_AAAA,trgt,a);
				} else {
					res = solve(EXP_AAAB,trgt,a,d);
				}
			} else {
				if (c == d) {
					res = solve(EXP_AABB,trgt,a,c);
				} else {
					res = solve(EXP_AABC,trgt,a,c,d);
				}
			}
		} else {
			if (b == c) {
				if (c == d) {
					res = solve(EXP_AAAB,trgt,b,a);
				} else {
					res = solve(EXP_AABC,trgt,b,a,d);
				}
			} else {
				if (c == d) {
					res = solve(EXP_AABC,trgt,c,a,b);
				} else {
					res = solve(EXP_ABCD,trgt,a,b,c,d);
				}
			}
		}
		if (cnt[0] >= 1) { // (5+0+0)+0 = (5+0+0)-0
			res = filter(res,FILTER0);
			append(res,solve3(flush(arr,0),trgt),"+0");
			append(res,solve2(flush(arr,0,b),trgt),"+(0*"+b+")");
			if (c != b)append(res,solve2(flush(arr,0,c),trgt),"+(0*"+c+")");
			if (d != c)append(res,solve2(flush(arr,0,d),trgt),"+(0*"+d+")");
			append(res,solve1(flush(arr,0,b,c),trgt),"+(0*("+b+"+"+c+"))");
			if (c != b || d != c)append(res,solve1(flush(arr,0,c,d),trgt),"+(0*("+c+"+"+d+"))");
			if (d != c && c != b)append(res,solve1(flush(arr,0,b,d),trgt),"+(0*("+b+"+"+d+"))");
			append(res,solve0(flush(arr,0,b,c,d),trgt),"0*("+b+"+("+c+"+"+d+"))");
		}
		if (cnt[1] >= 1) {
			res = filter(res,FILTER1);
			// System.out.println("filtering *1s");
			append(res,solve3(flush(arr,1),trgt),"*1");
		}
		if (cnt[2] >= 2) {
			res = filter(res,FILTER2);
		}
		if (cnt[2] >= 1 && cnt[4] >= 1) {
			res = filter(res,FILTER4);
		}
		// for (String result : res) {
		// 	System.out.println(result+"=24");
		// }
		for (int i = 3; i >= 0; --i) {
			for (int j = i-1; j >= 0; --j) {
				for (int k = 3; k >= 0; --k) {
					for (int l = k-1; l >= 0; --l) {
						if (i == k || i == l || j == k || j == l)continue;
						if (arr[i]-arr[j] == 1) {
							res = filter(res,DIFF1(arr[i],arr[j]));
							append(res,solve2(flush(arr,arr[j],arr[i]),trgt),"*("+arr[i]+"-"+arr[j]+")");
						}
						char m1 = 'X';
						if (arr[i]+arr[j] == 2)m1 = '+';
						if (arr[j] != 0 && arr[i]/arr[j] == 2)m1 = '/';
						if (arr[i]-arr[j] == 2)m1 = '-';
						if (m1 != 'X') {
							res = filter(res,DIFF2(arr[j],m1,arr[i],0,'X',0));	
							if (m1 == '+') {
								res = filter(res,DIFF2(arr[i],'+',arr[j],0,'X',0));
							}
							char m2 = 'X';
							if (arr[k]+arr[l] == 2)m2 = '+';
							if (arr[l] != 0 && arr[k]/arr[l] == 2)m2 = '/';
							if (arr[k]-arr[l] == 2)m2 = '-';
							if (m2 != 'X') {
								res = filter(res,DIFF2(arr[i],m1,arr[j],arr[k],m2,arr[l]));
								if (m1 == '+') {
									res = filter(res,DIFF2(arr[j],m1,arr[i],arr[k],m2,arr[l]));	
								}
								if (m2 == '+') {
									res = filter(res,DIFF2(arr[i],m1,arr[j],arr[l],m2,arr[k]));	
								}
								if (m1 == '+' && m2 == '+') {
									res = filter(res,DIFF2(arr[j],m1,arr[i],arr[l],m2,arr[k]));	
								}
							}
						}
						m1 = 'X';
						if (arr[i]+arr[j] == 4)m1 = '+';
						if (arr[j] != 0 && arr[i]/arr[j] == 4)m1 = '/';
						if (arr[i]-arr[j] == 4)m1 = '-';
						if (m1 != 'X') {
							res = filter(res,DIFF4(arr[j],m1,arr[i],0,'X',0));	
							if (m1 == '+') {
								res = filter(res,DIFF4(arr[i],'+',arr[j],0,'X',0));
							}
							char m2 = 'X';
							if (arr[k]+arr[l] == 2)m2 = '+';
							if (arr[l] != 0 && arr[k]/arr[l] == 2)m2 = '/';
							if (arr[k]-arr[l] == 2)m2 = '-';
							if (m2 != 'X') {
								res = filter(res,DIFF4(arr[i],m1,arr[j],arr[k],m2,arr[l]));
								if (m1 == '+') {
									res = filter(res,DIFF4(arr[j],m1,arr[i],arr[k],m2,arr[l]));	
								}
								if (m2 == '+') {
									res = filter(res,DIFF4(arr[i],m1,arr[j],arr[l],m2,arr[k]));	
								}
								if (m1 == '+' && m2 == '+') {
									res = filter(res,DIFF4(arr[j],m1,arr[i],arr[l],m2,arr[k]));	
								}
							}
						}
					}
				}
			}
		}
		// System.out.println(">>>>>>>>>");
		// for (String result : res) {
		// 	System.out.println(result+"=24");
		// }
		res = filter(res,CLONES(arr));
		// System.out.println("s4 found "+res.size());
		// if (res.size() > 0) {
		for (int i = 0; i < res.solutions.size(); ++i) {
		    res.solutions.set(i,
		    	new Equation(res.solutions.get(i).exp.substring(1,res.solutions.get(i).exp.length()-1),
		    		res.solutions.get(i).result,0));
		}
		for (int i = 0; i < res.partials.size(); ++i) {
		    res.partials.set(i,
		    	new Equation(res.partials.get(i).exp.substring(1,res.partials.get(i).exp.length()-1),
		    		res.partials.get(i).result,0));
		}
		// }
		return res;
	}

	public static double compute(double x, char oper, double y) {
		if (oper == '+') {
			return x+y;
		} else if (oper == '-') {
			return x-y;
		} else if (oper == '*') {
			return x*y;
		} else if (oper == '/') {
			return x/y;
		}
		return 0;
	}

	public static Solution Convert(Equation solution) {
		int left1 = solution.exp.indexOf('(');
		int left2 = solution.exp.indexOf('(',left1+1);
		int right2 = solution.exp.lastIndexOf(')');
		int right1 = solution.exp.lastIndexOf(')',right2-1);
		int[] opi = {-1,-1,-1};
		char[] oper = {'X','X','X'};
		int idx = 0;
		String exp = solution.exp;
		double result = solution.result;
		for (int i = 0; i < solution.exp.length(); ++i) {
			char c = solution.exp.charAt(i);
			if (c == '+' || c == '-' || c == '*' || c == '/') {
				oper[idx] = c;
				opi[idx++] = i;
			}
		}
		double largest = -1;
		boolean isfract = false;
		double score = 0;
		if (right1 < left2) {
			// (oxo)x(oxo)
			int w = Integer.parseInt(solution.exp.substring(left1+1,opi[0]));
			int x = Integer.parseInt(solution.exp.substring(opi[0]+1,right1));
			int y = Integer.parseInt(solution.exp.substring(left2+1,opi[2]));
			int z = Integer.parseInt(solution.exp.substring(opi[2]+1,right2));
			double p = compute(w,oper[0],x);
			double q = compute(y,oper[2],z);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[0] == '+' || oper[0] == '-') || !(oper[1] == '+' || oper[1] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[2] == '+' || oper[2] == '-') || !(oper[1] == '+' || oper[1] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(p,oper[1],q));
			if (oper[1] == '+' || oper[1] == '-') {
				if ((oper[0] == '/' && w%x != 0) || (oper[2] == '/' && y%z != 0)) {
					isfract = true;
				} else {
					isfract = false;
				}
			} else {
				isfract = false;
			}
		} else if (left1 == 0 && left2 == 1) {
			// ((oxo)xo)xo
			int w = Integer.parseInt(solution.exp.substring(left2+1,opi[0]));
			int x = Integer.parseInt(solution.exp.substring(opi[0]+1,right1));
			int y = Integer.parseInt(solution.exp.substring(opi[1]+1,right2));
			int z = Integer.parseInt(solution.exp.substring(opi[2]+1));
			double p = compute(w,oper[0],x);
			double q = compute(p,oper[1],y);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[0] == '+' || oper[0] == '-') || !(oper[1] == '+' || oper[1] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[2] == '+' || oper[2] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(q,oper[2],z));
		} else if (right2 == solution.exp.length()-1 && right1 == solution.exp.length()-2) {
			// ox(ox(oxo))
			int w = Integer.parseInt(solution.exp.substring(0,opi[0]));
			int x = Integer.parseInt(solution.exp.substring(left1+1,opi[1]));
			int y = Integer.parseInt(solution.exp.substring(left2+1,opi[2]));
			int z = Integer.parseInt(solution.exp.substring(opi[2]+1,right1));
			double p = compute(y,oper[2],z);
			double q = compute(x,oper[1],p);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[2] == '+' || oper[2] == '-') || !(oper[1] == '+' || oper[1] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[0] == '+' || oper[0] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(w,oper[0],q));
		} else if (left1 == 0) {
			// (ox(oxo))xo
			int w = Integer.parseInt(solution.exp.substring(left1+1,opi[0]));
			int x = Integer.parseInt(solution.exp.substring(left2+1,opi[1]));
			int y = Integer.parseInt(solution.exp.substring(opi[1]+1,right1));
			int z = Integer.parseInt(solution.exp.substring(opi[2]+1));
			double p = compute(x,oper[1],y);
			double q = compute(w,oper[0],p);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[0] == '+' || oper[0] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[0] == '+' || oper[0] == '-') || !(oper[2] == '+' || oper[2] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(q,oper[2],z));
		} else if (right2 == solution.exp.length()-1) {
			// ox((oxo)xo)
			int w = Integer.parseInt(solution.exp.substring(0,opi[0]));
			int x = Integer.parseInt(solution.exp.substring(left2+1,opi[1]));
			int y = Integer.parseInt(solution.exp.substring(opi[1]+1,right1));
			int z = Integer.parseInt(solution.exp.substring(opi[2]+1,right2));
			double p = compute(x,oper[1],y);
			double q = compute(p,oper[2],z);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[2] == '+' || oper[2] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[2] == '+' || oper[2] == '-') || !(oper[0] == '+' || oper[0] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(w,oper[0],q));
		}
		if (!(right1 < left2)) {
			int pos = solution.exp.indexOf('/',left2+1);
			if (pos > left2 && pos < right1) {
				int x = Integer.parseInt(solution.exp.substring(left2+1,pos));
				int y = Integer.parseInt(solution.exp.substring(pos+1,right1));
				if (y != 0) {
					if (x%y != 0) {
						isfract = true;
					} else {
						isfract = false;
					}
				} else System.out.println(solution+"impossible!!!");
			} else {
				isfract = false;
			}
		}
		if (Math.abs(Math.ceil(largest)-largest) < .000_001) {
			largest = Math.ceil(largest);
		}
		return new Solution(exp,result,score,largest,isfract);
	}
}