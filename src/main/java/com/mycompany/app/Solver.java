import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.TreeMap;

class Equation {
	String exp;
	double result;

	public Equation(String exp, double result) {
		this.exp = exp;
		this.result = result;
	}
}

class EqnPackage {
	List<Equation> solutions;
	List<Equation> partials;
}

class Solution extends Equation {
	double score;
	double largest;
	boolean isfract;

    public Solution(String exp, double result, double score, double largest, boolean isfract) {
        super(exp, result);
        this.score = score;
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

	private static EqnPackage filteradd(EqnPackage bucket, List<String> patterns) {
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		boolean taken = false;
		String exclude = "placeholder";
		String failed = "placeholder";
		for (Equation expression : bucket.solutions) {
			boolean pass = true;
			for (String pattern : patterns) {
				if (expression.exp.indexOf(pattern) >= 0 && (!taken || (taken && pattern != exclude))) {
					pass = false;
					failed = pattern;
					break;
				}
			}
			if (!pass) {
				if (!taken) {
					res.solutions.add(expression);
					exclude = failed;
					taken = true;
				}
				continue;
			}
			res.solutions.add(expression);
		}
		TreeMap<Double, String> mp = new TreeMap<>();
		for (Equation expression : bucket.partials) {
			boolean pass = true;
			taken = mp.containsKey(expression.result);
			for (String pattern : patterns) {
				if (expression.exp.indexOf(pattern) >= 0 && (!taken || (taken && pattern != mp.get(expression.result)))) {
					pass = false;
					failed = pattern;
					break;
				}
			}
			if (!pass) {
				if (!taken) {
					res.partials.add(expression);
					mp.put(expression.result,failed);
				}
				continue;
			}
			res.partials.add(expression);
		}
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

	public static boolean isInteger(double x) {
		return Math.abs(x-Math.floor(x)) < Constants.EPSILON || (Math.abs(x-Math.ceil(x)) < Constants.EPSILON);
	}

	public static boolean fsEquals(double x, double y) {
		return Math.abs(x-y) < Constants.EPSILON;
	}

	private static List<Equation> filter1div3(List<Equation> bucket) {
		List<Equation> res = new ArrayList<>();
		for (Equation equation : bucket) {
			int left1 = equation.exp.indexOf('(',1);
			int right1 = equation.exp.lastIndexOf(')',equation.exp.length()-2);
			int[] opi = {-1,-1};
			char[] oper = {'X','X'};
			int[] opparity = {-1,-1};
			int idx = 0;
			String exp = equation.exp;
			for (int i = 0; i < equation.exp.length(); ++i) {
				char c = equation.exp.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/') {
					oper[idx] = c;
					if (c == '+' || c == '-') {
						opparity[idx] = 0;
					} else {
						opparity[idx] = 1;
					}
					// opparity[idx] = (c == '+' || c == '-') ? 0 : 1;
					opi[idx++] = i;
				}
			}
			int x = -1, y = -1, z = -1;
			double p = Double.MIN_VALUE;
			double q = equation.result;
			if (left1 == 1) {
				// (oxo)xo
				x = Integer.parseInt(equation.exp.substring(left1+1,opi[0]));
				y = Integer.parseInt(equation.exp.substring(opi[0]+1,right1));
				z = Integer.parseInt(equation.exp.substring(opi[1]+1,equation.exp.length()-1));
				p = compute(x,oper[0],y);
				// q = compute(p,oper[1],z);
				if (x == 1 && oper[0] == '/') {
					if (oper[1] == '*') {
						// (1/x)* is always useless, but (1/x)/ never is
						continue;
					}
				}
				if (fsEquals(p,0) && oper[1] == '/')continue;
			} else if (right1 == equation.exp.length()-2) {
				// ox(oxo)
				x = Integer.parseInt(equation.exp.substring(1,opi[0]));
				y = Integer.parseInt(equation.exp.substring(left1+1,opi[1]));
				z = Integer.parseInt(equation.exp.substring(opi[1]+1,right1));
				p = compute(y,oper[1],z);
				// q = compute(x,oper[0],p);
				if (y == 1 && oper[1] == '/') {
					if (opparity[0] == 1) {
						// *(1/x), /(1/x) is always useless
						continue;
					}
				}
				if (x == 1 && oper[0] == '/') {
					if (oper[1] == '/') {
					// 1/(x/y) is always useless, but 1/(x*y) never is
						continue;
					}
				}
			} else {
				System.out.println("impossible!!!");
			}
			res.add(equation);
		}
		return res;
	}

	private static List<Equation> filter1div4(List<Equation> bucket) {
		List<Equation> res = new ArrayList<>();
		for (Equation equation : bucket) {
			int left1 = equation.exp.indexOf('(',1);
			int left2 = equation.exp.indexOf('(',left1+1);
			int right2 = equation.exp.lastIndexOf(')',equation.exp.length()-2);
			int right1 = equation.exp.lastIndexOf(')',right2-1);
			int[] opi = {-1,-1,-1};
			char[] oper = {'X','X','X'};
			int[] opparity = {-1,-1,-1};
			int idx = 0;
			String exp = equation.exp;
			for (int i = 0; i < equation.exp.length(); ++i) {
				char c = equation.exp.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/') {
					oper[idx] = c;
					if (c == '+' || c == '-') {
						opparity[idx] = 0;
					} else {
						opparity[idx] = 1;
					}
					// opparity[idx] = (c == '+' || c == '-') ? 0 : 1;
					opi[idx++] = i;
				}
			}
			int w = -1, x = -1, y = -1, z = -1;
			double r = equation.result;
			if (right1 < left2) {
				// (oxo)x(oxo)
				if (w == 1 && oper[0] == '/') {
					if (oper[1] == '*') {
						// (1/x)*z is always useless, but (1/x)/z never is
						continue;
					}
				}
				if (y == 1 && oper[2] == '/') {
					if (opparity[1] == 1) {
						// *(1/x) , /(1/x) is always useless
						continue;
					}
				}
			} else if (left1 == 1 && left2 == 2) {
				// ((oxo)xo)xo
				w = Integer.parseInt(equation.exp.substring(left2+1,opi[0]));
				x = Integer.parseInt(equation.exp.substring(opi[0]+1,right1));
				y = Integer.parseInt(equation.exp.substring(opi[1]+1,right2));
				z = Integer.parseInt(equation.exp.substring(opi[2]+1,equation.exp.length()-1));
				double p = compute(w,oper[0],x);
				double q = compute(p,oper[1],y);
				if (w == 1 && oper[0] == '/') {
					if (oper[1] == '*' || (oper[1] == '/' && oper[2] == '*')) {
					// ((1/x)*y) always useless
					// (((1/x)/y)*z), (((1/x)*y)*z), (((1/x)*y)/z) is always useless,
					//	but (((1/x)/y)/z) never is
						continue;
					}
				}
				if (fsEquals(p,0) && oper[1] == '/')continue;
				if (fsEquals(q,0) && oper[2] == '/')continue;
			} else if (right2 == equation.exp.length()-2 && right1 == equation.exp.length()-3) {
				// ox(ox(oxo))
				w = Integer.parseInt(equation.exp.substring(1,opi[0]));
				x = Integer.parseInt(equation.exp.substring(left1+1,opi[1]));
				y = Integer.parseInt(equation.exp.substring(left2+1,opi[2]));
				z = Integer.parseInt(equation.exp.substring(opi[2]+1,right1));
				double p = compute(y,oper[2],z);
				double q = compute(x,oper[1],p);
				if (y == 1 && oper[2] == '/') {
					if (opparity[1] == 1) {
						// *(1/x), /(1/x) is always useless
						continue;
					}
					// +(1/x), -(1/x) is always not
				}
				if (x == 1 && oper[1] == '/') {
					if (oper[2] == '/') {
						// 1/(x/y) is always useless, but 1/(x*y) never is
						continue;
					}
					if (opparity[0] == 1) {
						// *(1/(z)), /(1/(z)) is always useless
						continue;
					}
					if (fsEquals(p,1)) {
						continue;
					}
				}
				if (w == 1 && oper[0] == '/') {
					if (oper[1] == '/' || (oper[1] == '*' && oper[2] == '/')) {
						// 1/(x/w) = w/x
						// 1/(x*(y*z)) never useless
						// 1/(x/(y*z)) = y*z/x
						// 1/(x*(y/z)) = z/(x*y)
						// 1/(x/(y/z)) = y/(x*z)
						continue;
					}
					if (fsEquals(q,1)) {
						continue;
					}
				}
			} else if (left1 == 1) {
				// (ox(oxo))xo
				w = Integer.parseInt(equation.exp.substring(left1+1,opi[0]));
				x = Integer.parseInt(equation.exp.substring(left2+1,opi[1]));
				y = Integer.parseInt(equation.exp.substring(opi[1]+1,right1));
				z = Integer.parseInt(equation.exp.substring(opi[2]+1,equation.exp.length()-1));
				double p = compute(x,oper[1],y);
				double q = compute(w,oper[0],p);
				if (x == 1 && oper[1] == '/') {
					if (opparity[0] == 1) {
						// *(1/x), /(1/x) always useless
						continue;
					}
					// +(1/x), -(1/x) is always not
				}
				if (w == 1 && oper[0] == '/') {
					if (oper[1] == '/') {
						// 1/(x/y) is always useless, but 1/(x*y) never is
						continue;
					}
					if (oper[2] == '*') {
						// (1/(z))*x is always useless, but (1/(z))/x never is
						continue;
					}
					if (fsEquals(p,1)) {
						continue;
					}
				}
				if (fsEquals(q,0) && oper[2] == '/')continue;
			} else if (right2 == equation.exp.length()-2) {
				// ox((oxo)xo)
				w = Integer.parseInt(equation.exp.substring(1,opi[0]));
				x = Integer.parseInt(equation.exp.substring(left2+1,opi[1]));
				y = Integer.parseInt(equation.exp.substring(opi[1]+1,right1));
				z = Integer.parseInt(equation.exp.substring(opi[2]+1,right2));
				double p = compute(x,oper[1],y);
				double q = compute(p,oper[2],z);
				if (x == 1 && oper[1] == '/') {
					if (oper[2] == '*' || (oper[2] == '/' && opparity[0] == 1)) {
						// (1/x)*y is always useless, but (1/x)/y never is
						// z*((1/x)/y) = z/(x*y);
						// z/((1/x)/y) = x*y*z
						continue;
					}
				}
				if (w == 1 && oper[0] == '/') {
					if (oper[2] == '/' || (oper[2] == '*' && oper[1] == '/')) {
						// 1/((x*y)*z) never useless
						// 1/((x/y)*z) = y/(x*z)
						// 1/((x*y)/z) = z/(x*y)
						// 1/((x/y)/z) = (y*z)/x
						// 1/((w)/z) = z/w
						continue;
					}
					if (fsEquals(q,1)) {
						continue;
					}
				}
				if (fsEquals(p,0) && oper[2] == '/')continue;
			} else {
				System.out.println("impossible!!!");
			}
			res.add(equation);
		}
		return res;
	}

	private static EqnPackage append(EqnPackage res, EqnPackage extras, String suffix) {
		for (Equation eqn : extras.solutions) {
			res.solutions.add(new Equation("("+eqn.exp+suffix+")",eqn.result));
		}
		for (Equation eqn : extras.partials) {
			res.partials.add(new Equation("("+eqn.exp+suffix+")",eqn.result));
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
				if (Math.abs(val-trgt) < .000_001) res.solutions.add(new Equation("("+value+")",val));
				else res.partials.add(new Equation("("+value+")",val));
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
		if (Math.abs(0-trgt) < .000_001)res.solutions.add(new Equation("",0));
		else if (Math.abs(0-trgt) < 1.000_001)res.partials.add(new Equation("",0));
		return res;
	}

	public static EqnPackage solve1(int[] arr, double trgt) {
		// System.out.println("s1 "+Arrays.toString(arr)+" "+trgt);
		EqnPackage res = new EqnPackage();
		res.solutions = new ArrayList<>();
		res.partials = new ArrayList<>();
		if (Math.abs(arr[0]-trgt) < .000_001)res.solutions.add(new Equation(""+arr[0],arr[0]));
		else if (Math.abs(arr[0]-trgt) < 1.000_001)res.partials.add(new Equation(""+arr[0],arr[0]));
		// System.out.println("s1 found "+res.size());
		return res;
	}

	public static EqnPackage solve2(int[] arr, double trgt) {
		Arrays.sort(arr);
		int a = arr[0];
		int b = arr[1];
		int[] cnt = new int[Constants.end+1];
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
			// need not apply 1/ filter, as no attempt can make it useless (except 1/1 which is covered)
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
		int[] cnt = new int[Constants.end+1];
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
		res.solutions = filter1div3(res.solutions);
		res.partials = filter1div3(res.partials);
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
		boolean[][] check = new boolean[Constants.end-Constants.start+1][Constants.end-Constants.start+1];
		for (int i = 2; i >= 0; --i) {
			for (int j = i-1; j >= 0; --j) {
				// System.out.println((arr[i]-Constants.start)+" "+(arr[j]-Constants.start));
				// if (arr[i]-Constants.start < 0 || arr[j]-Constants.start < 0)continue;
				if (check[arr[i]-Constants.start][arr[j]-Constants.start])continue;
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
				int k = 3-i-j;
				if (arr[i]+arr[j] == arr[k]) {
					res = filteradd(res,FRIENDLYFIRE3(arr[i],'+',arr[j],arr[k],0));
					res = filteradd(res,FRIENDLYFIRE3(arr[i],'+',arr[j],arr[k],1));
				}
				if (arr[i]*arr[j] == arr[k]) {
					res = filteradd(res,FRIENDLYFIRE3(arr[i],'*',arr[j],arr[k],0));
					res = filteradd(res,FRIENDLYFIRE3(arr[i],'*',arr[j],arr[k],1));
				}
				check[arr[i]-Constants.start][arr[j]-Constants.start] = true;
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
		int[] cnt = new int[Constants.end+1];
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
		res.solutions = filter1div4(res.solutions);
		res.partials = filter1div4(res.partials);
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
		boolean[][] check = new boolean[Constants.end-Constants.start+1][Constants.end-Constants.start+1];
		for (int i = 3; i >= 0; --i) {
			for (int j = i-1; j >= 0; --j) {
				for (int k = 3; k >= 0; --k) {
					for (int l = k-1; l >= 0; --l) {
						if (i == k || i == l || j == k || j == l)continue;
						// System.out.println((arr[i]-Constants.start)+" "+(arr[j]-Constants.start));
						// if (arr[i]-Constants.start < 0 || arr[j]-Constants.start < 0)continue;
						if (check[arr[i]-Constants.start][arr[j]-Constants.start])continue;
						if (arr[i]-arr[j] == 1) {
							res = filter(res,DIFF1(arr[i],arr[j]));
							append(res,solve2(flush(arr,arr[j],arr[i]),trgt),"*("+arr[i]+"-"+arr[j]+")");
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
							res = filter(res,DIFF4(arr[i],m1,arr[j],0,'X',0));	
							if (m1 == '+') {
								res = filter(res,DIFF4(arr[j],'+',arr[i],0,'X',0));
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
						if (arr[i]+arr[j] == arr[k]) {
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'+',arr[j],arr[k],0));
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'+',arr[j],arr[k],1));
						}
						if (arr[i]*arr[j] == arr[k]) {
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'*',arr[j],arr[k],0));
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'*',arr[j],arr[k],1));
						}
						if (arr[i]+arr[j] == arr[l]) {
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'+',arr[j],arr[l],0));
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'+',arr[j],arr[l],1));
						}
						if (arr[i]*arr[j] == arr[l]) {
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'*',arr[j],arr[l],0));
							res = filteradd(res,FRIENDLYFIRE3(arr[i],'*',arr[j],arr[l],1));
						}
						check[arr[i]-Constants.start][arr[j]-Constants.start] = true;
					}
				}
			}
		}
		// System.out.println(">>>>>>>>>");
		// for (String result : res) {
		// 	System.out.println(result+"=24");
		// }
		res = filter(res,CLONES(arr));
		res.solutions = Expressions2.FRIENDLYFIRE4(res.solutions);
		res.partials = Expressions2.FRIENDLYFIRE4(res.partials);
		// System.out.println("s4 found "+res.size());
		// if (res.size() > 0) {
		for (int i = 0; i < res.solutions.size(); ++i) {
		    res.solutions.set(i,
		    	new Equation(res.solutions.get(i).exp.substring(1,res.solutions.get(i).exp.length()-1),
		    		res.solutions.get(i).result));
		}
		for (int i = 0; i < res.partials.size(); ++i) {
		    res.partials.set(i,
		    	new Equation(res.partials.get(i).exp.substring(1,res.partials.get(i).exp.length()-1),
		    		res.partials.get(i).result));
		}
		// }
		return res;
	}

	public static Solution Convert(Equation equation, double largedet) {
		int left1 = equation.exp.indexOf('(');
		int left2 = equation.exp.indexOf('(',left1+1);
		int right2 = equation.exp.lastIndexOf(')');
		int right1 = equation.exp.lastIndexOf(')',right2-1);
		int[] opi = {-1,-1,-1};
		char[] oper = {'X','X','X'};
		int[] opparity = {-1,-1,-1};
		int idx = 0;
		String exp = equation.exp;
		double result = equation.result;
		for (int i = 0; i < equation.exp.length(); ++i) {
			char c = equation.exp.charAt(i);
			if (c == '+' || c == '-' || c == '*' || c == '/') {
				oper[idx] = c;
				if (c == '+' || c == '-') {
					opparity[idx] = 0;
				} else {
					opparity[idx] = 1;
				}
				// opparity[idx] = (c == '+' || c == '-') ? 0 : 1;
				opi[idx++] = i;
			}
		}
		double largest = -1;
		boolean isfract = false;
		double score = Constants.DEFAULT_SCORE;
		int alternate = -1;
		int det = -1;
		// System.out.println("the "+score);
		int w = -1, x = -1, y = -1, z = -1;
		if (right1 < left2) {
			// (oxo)x(oxo)
			w = Integer.parseInt(equation.exp.substring(left1+1,opi[0]));
			x = Integer.parseInt(equation.exp.substring(opi[0]+1,right1));
			y = Integer.parseInt(equation.exp.substring(left2+1,opi[2]));
			z = Integer.parseInt(equation.exp.substring(opi[2]+1,right2));
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
			if (opparity[1] != opparity[0] && opparity[1] != opparity[2]) {
				alternate = 3;
				if (opparity[1] == 0)det = 0;
				else det = 1;
			} else if (opparity[1] != opparity[0] || opparity[1] != opparity[2]) {
				alternate = 1;
				if (opparity[1] == 0)det = 1;
				else det = 0;
			} else {
				alternate = 0;
				det = opparity[1];
			}
			if (oper[0] == '*' && opparity[1] == 0 && oper[2] == '*') {
				if (w == y || w == z || x == y || x == z) {
					score *= Constants.DISTRIBUTION_WEIGHT;
				}
			}
			// if (fsEquals(p,result) || fsEquals(q,result)) {
			// 	if (opparity[1] == 0) {
			// 		score *= Constants.DEAD0;
			// 	} else if (opparity[1] == 1) {
			// 		score *= Constants.DEAD1;
			// 	}
			// }
			// duplicate to old method down below
		} else if (left1 == 0 && left2 == 1) {
			// ((oxo)xo)xo
			w = Integer.parseInt(equation.exp.substring(left2+1,opi[0]));
			x = Integer.parseInt(equation.exp.substring(opi[0]+1,right1));
			y = Integer.parseInt(equation.exp.substring(opi[1]+1,right2));
			z = Integer.parseInt(equation.exp.substring(opi[2]+1));
			double p = compute(w,oper[0],x);
			double q = compute(p,oper[1],y);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[0] == '+' || oper[0] == '-') || !(oper[1] == '+' || oper[1] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[2] == '+' || oper[2] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(q,oper[2],z));
			if (oper[0] == '/') {
				if (x != 0) {
					if (w%x != 0 && opparity[1] == 0) {
						isfract = true;
					} else {
						isfract = false;
					}
				} else System.out.println(equation.exp+"impossible!!!");
			} else {
				isfract = false;
			}
			if (opparity[1] != opparity[0] && opparity[1] != opparity[2]) {
				alternate = 2;
				det = opparity[0];
			} else if (opparity[1] != opparity[0] || opparity[1] != opparity[2]) {
				alternate = 1;
				if (opparity[0] == 0) {
					if (opparity[1] == 0) {
						det = 2;
					} else {
						det = 0;
					}
				} else {
					if (opparity[1] == 0) {
						det = 1;
					} else {
						det = 3;
					}
				}
			} else {
				alternate = 0;
				det = opparity[0];
			}
			if (fsEquals(p,result)) {
				if (opparity[1] == 0 && opparity[2] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[1] == 1 && opparity[2] == 1) {
					score *= Constants.DEAD1;
				}
			}
			if (fsEquals(w,q) || (fsEquals(x,q))) {
				if (opparity[0] == 0 && opparity[1] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[0] == 1 && opparity[1] == 1) {
					score *= Constants.DEAD1;
				}
			}
		} else if (right2 == equation.exp.length()-1 && right1 == equation.exp.length()-2) {
			// ox(ox(oxo))
			w = Integer.parseInt(equation.exp.substring(0,opi[0]));
			x = Integer.parseInt(equation.exp.substring(left1+1,opi[1]));
			y = Integer.parseInt(equation.exp.substring(left2+1,opi[2]));
			z = Integer.parseInt(equation.exp.substring(opi[2]+1,right1));
			double p = compute(y,oper[2],z);
			double q = compute(x,oper[1],p);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[2] == '+' || oper[2] == '-') || !(oper[1] == '+' || oper[1] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[0] == '+' || oper[0] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(w,oper[0],q));
			if (oper[2] == '/') {
				if (z != 0) {
					if (y%z != 0 && opparity[1] == 0) {
						isfract = true;
					} else {
						isfract = false;
					}
				} else System.out.println(equation.exp+"impossible!!!");
			} else {
				isfract = false;
			}
			if (opparity[1] != opparity[0] && opparity[1] != opparity[2]) {
				alternate = 2;
				det = opparity[2];
			} else if (opparity[1] != opparity[0] || opparity[1] != opparity[2]) {
				alternate = 1;
				if (opparity[2] == 0) {
					if (opparity[1] == 0) {
						det = 2;
					} else {
						det = 0;
					}
				} else {
					if (opparity[1] == 0) {
						det = 1;
					} else {
						det = 3;
					}
				}
			} else {
				alternate = 0;
				det = opparity[2];
			}
			if (fsEquals(p,result)) {
				if (opparity[1] == 0 && opparity[0] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[1] == 1 && opparity[0] == 1) {
					score *= Constants.DEAD1;
				}
			}
			if (fsEquals(y,q) || (fsEquals(z,q))) {
				if (opparity[2] == 0 && opparity[1] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[2] == 1 && opparity[1] == 1) {
					score *= Constants.DEAD1;
				}
			}
		} else if (left1 == 0) {
			// (ox(oxo))xo
			w = Integer.parseInt(equation.exp.substring(left1+1,opi[0]));
			x = Integer.parseInt(equation.exp.substring(left2+1,opi[1]));
			y = Integer.parseInt(equation.exp.substring(opi[1]+1,right1));
			z = Integer.parseInt(equation.exp.substring(opi[2]+1));
			double p = compute(x,oper[1],y);
			double q = compute(w,oper[0],p);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[0] == '+' || oper[0] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[0] == '+' || oper[0] == '-') || !(oper[2] == '+' || oper[2] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(q,oper[2],z));
			if (oper[1] == '/') {
				if (y != 0) {
					if (x%y != 0 && opparity[0] == 0) {
						isfract = true;
					} else {
						isfract = false;
					}
				} else System.out.println(equation.exp+"impossible!!!");
			} else {
				isfract = false;
			}
			if (opparity[0] != opparity[1] && opparity[0] != opparity[2]) {
				alternate = 2;
				det = opparity[1];
			} else if (opparity[0] != opparity[1] || opparity[0] != opparity[2]) {
				alternate = 1;
				if (opparity[1] == 0) {
					if (opparity[0] == 0) {
						det = 2;
					} else {
						det = 0;
					}
				} else {
					if (opparity[0] == 0) {
						det = 1;
					} else {
						det = 3;
					}
				}
			} else {
				alternate = 0;
				det = opparity[1];
			}
			if (fsEquals(p,result)) {
				if (opparity[0] == 0 && opparity[2] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[0] == 1 && opparity[2] == 1) {
					score *= Constants.DEAD1;
				}
			}
			if (fsEquals(x,q) || (fsEquals(y,q))) {
				if (opparity[1] == 0 && opparity[0] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[1] == 1 && opparity[0] == 1) {
					score *= Constants.DEAD1;
				}
			}
		} else if (right2 == equation.exp.length()-1) {
			// ox((oxo)xo)
			w = Integer.parseInt(equation.exp.substring(0,opi[0]));
			x = Integer.parseInt(equation.exp.substring(left2+1,opi[1]));
			y = Integer.parseInt(equation.exp.substring(opi[1]+1,right1));
			z = Integer.parseInt(equation.exp.substring(opi[2]+1,right2));
			double p = compute(x,oper[1],y);
			double q = compute(p,oper[2],z);
			largest = Math.max(Math.max(Math.max(w,x),y),z);
			if(!(oper[1] == '+' || oper[1] == '-') || !(oper[2] == '+' || oper[2] == '-'))
				largest = Math.max(largest,p);
			if(!(oper[2] == '+' || oper[2] == '-') || !(oper[0] == '+' || oper[0] == '-'))
				largest = Math.max(largest,q);
			largest = Math.max(largest,compute(w,oper[0],q));
			if (oper[1] == '/') {
				if (y != 0) {
					if (x%y != 0 && opparity[2] == 0) {
						isfract = true;
					} else {
						isfract = false;
					}
				} else System.out.println(equation.exp+"impossible!!!");
			} else {
				isfract = false;
			}
			if (opparity[2] != opparity[1] && opparity[2] != opparity[0]) {
				alternate = 2;
				det = opparity[1];
			} else if (opparity[2] != opparity[1] || opparity[2] != opparity[0]) {
				alternate = 1;
				if (opparity[1] == 0) {
					if (opparity[2] == 0) {
						det = 2;
					} else {
						det = 0;
					}
				} else {
					if (opparity[2] == 0) {
						det = 1;
					} else {
						det = 3;
					}
				}
			} else {
				alternate = 0;
				det = opparity[1];
			}
			if (fsEquals(p,result)) {
				if (opparity[2] == 0 && opparity[0] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[2] == 1 && opparity[0] == 1) {
					score *= Constants.DEAD1;
				}
			}
			if (fsEquals(x,q) || (fsEquals(y,q))) {
				if (opparity[1] == 0 && opparity[2] == 0) {
					score *= Constants.DEAD0;
					// System.out.println("dead0: "+equation.exp);
				} else if (opparity[1] == 1 && opparity[2] == 1) {
					score *= Constants.DEAD1;
				}
			}
		} else {
			System.out.println("impossible!!!");
		}
		if (Math.abs(Math.ceil(largest)-largest) < .000_001) {
			largest = Math.ceil(largest);
		}
		if (largest > Constants.largedet) {
			double largeweight = 1+((largest-largedet)/Constants.LARGERANGE);
			score *= Math.pow(largeweight,Constants.LARGEWEIGHT);
			// System.out.println(largeweight*Constants.LARGEWEIGHT);
		}
		if (isfract)score *= Constants.FRACTWEIGHT;
		// score *= Constants.ALTERWEIGHT[alternate];
		// System.out.println(exp+" "+alternate+" "+score);
		if (w == x) {
			if (x == y) {
				if (y == z) {
					score *= Constants.AAAA_WEIGHT;
				} else {
					score *= Constants.AAAB_WEIGHT;
					// System.out.println(Constants.AAAB_WEIGHT);
				}
			} else {
				if (y == z) {
					score *= Constants.AABB_WEIGHT;
				} else {
					score *= Constants.AABC_WEIGHT;
				}
			}
		} else {
			if (x == y) {
				if (y == z) {
					score *= Constants.AAAB_WEIGHT;
				} else {
					score *= Constants.AABC_WEIGHT;
					// System.out.println(Constants.AAAB_WEIGHT);
				}
			} else {
				if (y == z) {
					score *= Constants.AABC_WEIGHT;
				} else {
					score *= Constants.ABCD_WEIGHT;
				}
			}
		}
		int sum = w+x+y+z;
		score *= (1-Constants.SUM_WEIGHT)*(1-(sum/(double)(Constants.end*4)))+Constants.SUM_WEIGHT;
		// System.out.println((1-Constants.SUM_WEIGHT)*(1-(sum/(double)(Constants.end*4)))+Constants.SUM_WEIGHT);
		if (alternate == 0) {
			score *= Constants.CONSECUTIVEWEIGHT[det];
		} else if (alternate == 1) {
			score *= Constants.ONETWOWEIGHT[det];
		} else if (alternate == 2) {
			score *= Constants.ALTERWEIGHT[det];
		} else if (alternate == 3) {
			score *= Constants.PAIRSWEIGHT[det];
		}
		int it;
		String pattern;
		for (int i = Constants.start; i <= Constants.end; ++i) {
			it = -1;
			pattern = "("+i+"-"+i+")";
			// System.out.println("check: "+equation.exp);
			while(true) {
				it = exp.indexOf(pattern,it+1);
				if (it < 0)break;
				score *= Constants.DEAD0;
				// System.out.println("dead0: "+equation.exp);
			}
		}
		for (int i = Constants.start+1; i <= Constants.end; ++i) {
			it = -1;
			pattern = "("+i+"-"+(i-1)+")";
			while(true) {
				it = exp.indexOf(pattern,it+1);
				if (it < 0)break;
				score *= Constants.DEAD1;
			}
		}
		String exp0 = exp.replaceAll("[1-9]\\d*","o");
		List<String> patterns = new ArrayList<>();
		List<Double> weights = new ArrayList<>();
		patterns.add("+0");
		weights.add(Constants.USELESS0);
		patterns.add("+(0*o)");
		weights.add(Constants.DRAG0[1]);
		patterns.add("+(0*(o+o))");
		weights.add(Constants.DRAG0[2]);
		patterns.add("0*(o+(o+o))");
		weights.add(Constants.DRAG0[3]);
		for (int i = 0; i < weights.size(); ++i) {
			it = -1;
			pattern = patterns.get(i);
			while(true) {
				it = exp0.indexOf(pattern,it+1);
				if (it < 0)break;
				score *= weights.get(i);
			}
		}
		String exp1 = exp.replaceAll("\\b\\d{2,}\\b|[^1\\D]","o");
		it = -1;
		pattern = "*1";
		while(true) {
			it = exp1.indexOf(pattern,it+1);
			if (it < 0)break;
			score *= Constants.USELESS1;
		}
		// System.out.println(exp+" "+alternate+" "+det);
		return new Solution(exp,result,score,largest,isfract);
	}
}

// (a+b)/c, c/(a+b), (c-b)/a, (c-a)/b, a/(c-b), b/(c-a) for a+b = c being the same
// -x+y, *x/y for x = y being the same
// will remain unresolved (for now)
// (implementing it would just be case work)