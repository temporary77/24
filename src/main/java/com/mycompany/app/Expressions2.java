import java.util.List;
import java.util.ArrayList;

public class Expressions2 {   

    public static final List<String> FILTER0 = new ArrayList<>();

    public static final List<String> FILTER1 = new ArrayList<>();

    public static final List<String> FILTER2 = new ArrayList<>();

    public static final List<String> FILTER4 = new ArrayList<>();

    static {
        FILTER0.add("+0)"); // moved // multiple locations
        FILTER0.add("(0+"); // moved // multiple locations
        FILTER0.add("-0)"); // moved // multiple locations
        FILTER0.add("(0-"); // all negative sols will have inverse positive sols
                            // even (0-o)-o)-o will have -((0+o)+o)+o) (not in scope)
                            // so they will have sols without using the 0
                            // so negative sols should not have impact on any statistics
        FILTER0.add("(0*"); // replace // location matters
        FILTER0.add("*0)"); // replace // location matters
        FILTER0.add("(0/"); // replaced // location matters

        FILTER1.add("*1)"); // moved // multiple locations
        FILTER1.add("(1*"); // moved // multiple locations
        FILTER1.add("/1)"); // moved // multiple locations
        // 1/ may be useless when every operator is * or /, but it could filter out fract sols
        // so filtering it will have its own function

        FILTER2.add("(2*2)"); // replaced // location matters
        FILTER2.add("(2*(2*"); // replaced // location matters
        FILTER2.add("*2)*2)"); // replaced // location matters

        FILTER4.add("(4/2)"); // replaced // location matters
        FILTER4.add("*4)/2)"); // replaced // location matters
    }

    public static List<String> DIFF1(int x, int y) {
        List<String> res = new ArrayList<>();
        res.add("*("+x+"-"+y+")"); // moved // multiple locations
        res.add("("+x+"-"+y+")*"); // moved // multiple locations
        res.add("/("+x+"-"+y+")"); // moved // multiple locations
        res.add("*("+y+"-"+x+")"); // moved // multiple locations
        res.add("("+y+"-"+x+")*"); // moved // multiple locations
        res.add("/("+y+"-"+x+")"); // moved // multiple locations

        res.add("("+x+"-"+y+")/");
        res.add("("+y+"-"+x+")/");
        // fractionals are not possible since they would have to be resolved right after
        // (if target isnt fractional) (limitation)
        // 
        return res;
    }

    public static List<String> DIFF2(int x1, char m1, int y1, int x2, char m2, int y2) {
        List<String> res = new ArrayList<>();
        String pattern1, pattern2, pattern3;
        pattern1 = "("+x1+m1+y1+")";
        pattern2 = "("+x2+m2+y2+")";
        if (m1 != 'X') {
            // + - /
            // 1*2 useless 1 
            res.add("(2*"+pattern1);
            res.add(pattern1+"*2)");
        }
        if (m2 != 'X') {
            // + - /
            // 1*2 useless 1 
            res.add("(2*"+pattern2);
            res.add(pattern2+"*2)");
        }
        if (m1 != 'X' && m2 != 'X') {
            res.add(pattern1+"*"+pattern2);
            res.add(pattern2+"*"+pattern1);
        }
        return res;
    }

    public static List<String> DIFF4(int x1, char m1, int y1, int x2, char m2, int y2) {
        List<String> res = new ArrayList<>();
        String pattern1, pattern2;
        pattern1 = "("+x1+m1+y1+")";
        pattern2 = "("+x2+m2+y2+")";
        if (m1 != 'X') {
            // + - /
            // 2*2 not considered only 2+2
            res.add(pattern1+"/2)");
        }
        if (m2 != 'X') {
            // + - /
            // 1*2 useless 1 
            res.add("(4/"+pattern2);
        }
        if (m1 != 'X' && m2 != 'X') {
            res.add(pattern1+"/"+pattern2);
        }
        return res;
    }

    public static List<String> CLONES(int[] arr) {
        List<String> patterns = new ArrayList<>();
        int cur = 1;
        for (int i = 1; i < arr.length; ++i) {
            if (arr[i-1] == arr[i]) {
                ++cur;
                int x = arr[i];
                if (cur == 2) {
                    patterns.add("((2*"+x+")-"+x+")");
                    patterns.add("(("+x+"*2)-"+x+")");
                } 
                if (cur == 3) {
                    patterns.add("(((3*"+x+")-"+x+")-"+x+")");
                    patterns.add("((("+x+"*3)-"+x+")-"+x+")");
                }
            } else {
                cur = 1;
            }
        }
        return patterns;
    }

    public static List<String> FRIENDLYFIRE3(int x, char m, int y, int z, int det) {
        // filter in only 1 form of 0s and 1s
        List<String> res = new ArrayList<>();
        if (m == '+') {
            if (det == 0) {
                res.add(z+"-"+"("+x+"+"+y+")");
                res.add(z+"-"+"("+y+"+"+x+")");
                res.add(x+"+"+"("+y+"-"+z+")");
                res.add(y+"+"+"("+x+"-"+z+")");
                res.add("("+x+"+"+y+")"+"-"+z);
                res.add("("+y+"+"+x+")"+"-"+z);
                res.add("("+z+"-"+y+")"+"-"+x);
                res.add("("+z+"-"+x+")"+"-"+y);
                res.add("("+x+"-"+z+")"+"+"+y);
                res.add("("+y+"-"+z+")"+"+"+x);
            } else if (det == 1) {
                res.add(z+"/"+"("+x+"+"+y+")");
                res.add(z+"/"+"("+y+"+"+x+")");
                res.add("("+x+"+"+y+")"+"/"+z);
                res.add("("+y+"+"+x+")"+"/"+z);
                res.add(x+"/"+"("+z+"-"+y+")");
                res.add(x+"/"+"("+y+"-"+z+")");
                res.add("("+z+"-"+y+")"+"/"+x);
                res.add("("+y+"-"+z+")"+"/"+x);
                res.add(y+"/"+"("+z+"-"+x+")");
                res.add(y+"/"+"("+x+"-"+z+")");
                res.add("("+z+"-"+x+")"+"/"+y);
                res.add("("+x+"-"+z+")"+"/"+y);
            }
        } else if (m == '*') {
            if (det == 1) {
                res.add(z+"/"+"("+x+"*"+y+")");
                res.add(z+"/"+"("+y+"*"+x+")");
                res.add(x+"*"+"("+y+"/"+z+")");
                res.add(y+"*"+"("+x+"/"+z+")");
                res.add("("+x+"*"+y+")"+"/"+z);
                res.add("("+y+"*"+x+")"+"/"+z);
                res.add("("+z+"/"+y+")"+"/"+x);
                res.add("("+z+"/"+x+")"+"/"+y);
                res.add("("+x+"/"+z+")"+"*"+y);
                res.add("("+y+"/"+z+")"+"*"+x);
            } else if (det == 0) {
                res.add(z+"-"+"("+x+"*"+y+")");
                res.add(z+"-"+"("+y+"*"+x+")");
                res.add("("+x+"*"+y+")"+"-"+z);
                res.add("("+y+"*"+x+")"+"-"+z);
                res.add(x+"-"+"("+z+"/"+y+")");
                res.add("("+z+"/"+y+")"+"-"+x);
                res.add(y+"-"+"("+z+"/"+x+")");
                res.add("("+z+"/"+x+")"+"-"+y);
            }
        }
        for (String result : res) {
            result = "("+result+")";
        }
        return res;        
    }

    public static char inverseoper(char oper) {
        if (oper == '+') {
            return '-';
        } else if (oper == '-') {
            return '+';
        } else if (oper == '*') {
            return '/';
        } else if (oper == '/') {
            return '*';
        } else {
            return 'X';
        }
    }

    public static List<Equation> FRIENDLYFIRE4(List<Equation> bucket) {
        List<Equation> res = new ArrayList<>();

        // !!! = can do * (for /), can do + (for -)

        // * AND /
        // 0. + - all same
        // (w+x)/(y+z)
        // (w-x)/(y-z)
        // 1. + - different
        // (w+x)/(y-z)
        // (w-x)/(-y-z)
        // (w-x)/(y+z)
        // -(w+x)/(y-z)
        // since equals to 1 XOR -1 => no duplicates undetected (1 and -1 have one each)
        // will either get 1 or -1
        // counting these as same (option)
        // 2. * / all same !!!
        // (w*x)/(y*z)
        // (w/x)/(y/z)
        // (w*x)*(/y/z) NO
        // (w/x)*(z/y) <<
        // 3. * / different !!!
        // (w*x)/(y/z)
        // (w*x)*(z/y) <<
        // (w/y)/(/x/z) NO
        // (w*x*y)/z
        // (w/x)/(y*z)
        // w/(x*y*z)
        // since equals to 1 => same groups of numbers => no duplicates undetected
        // 4. + *
        // (w+x)/(y*z)
        // ((w+x)/z)/y // leads nowhere
        // w/((y*z)-x) // leads nowhere
        // 5. - *
        // (w-x)/(y*z)
        // ((w-x)/z)/y
        // w/((y*z)+x) 
        // 6. + /
        // (w+x)/(y/z) !!!
        // w/((y/z)-x) // leads nowhere
        // ((w+x)*z)/y // leads nowhere
        // 7. - /
        // (w-x)/(y/z) !!!
        // w/((y/z)+x) // leads nowhere
        // ((w-x)*z)/y // leads nowhere
        final boolean[] allow1 = {false,false,true,true,false,false,true,true};

        // + AND -
        // 0. + - all same !!!
        // (w+x)-(y+z)
        // (w-x)-(y-z)
        // 1. + - different !!!
        // (w+x)-(y-z)
        // (w-y)-(-x-z)
        // (w-y)+(x+z)
        // equals to 0 and is basically the same, no duplicates
        // 2. * / all same
        // (w*x)-(y*z)
        // (w/x)-(y/z)
        // 3. * / different
        // (w*x)-(y/z)
        // (w/y)-(/x/z) NO
        // (w*x*y)-z
        // (w/x)-(y*z)
        // w-(x*y*z)
        // 4. + *
        // (w+x)-(y*z)
        // (y*z)+(-w-x) NO
        // ((w+x)/z)-y // leads nowhere
        // w-((y*z)-x) // leads nowhere
        // 5. - * !!!
        // (w-x)-(y*z)
        // (y*z)+(x-w) <<
        // ((w-x)/z)-y
        // w-((y*z)+x) 
        // 6. + /
        // (w+x)-(y/z)
        // w-((y/z)-x) // leads nowhere
        // ((w+x)*z)-y // leads nowhere
        // 7. - / !!!
        // (w-x)-(y/z)
        // w-((y/z)+x) // leads nowhere
        // ((w-x)*z)-y // leads nowhere
        final boolean[] allow0 = {true,true,false,false,false,true,false,true};

        int[][] table = {{0,1,4,6},
                         {1,0,5,7},
                         {4,5,2,3},
                         {6,7,3,2}};

        boolean[] check0 = new boolean[8];
        boolean[] check1 = new boolean[8];
        String exp;
        for (Equation equation : bucket) {
            if (equation.result != 0 && equation.result != 1) {
                res.add(equation);
                continue;
            }
            int left1 = equation.exp.indexOf('(',1);
            int left2 = equation.exp.indexOf('(',left1+1);
            int right2 = equation.exp.lastIndexOf(')',equation.exp.length()-2);
            int right1 = equation.exp.lastIndexOf(')',right2-1);
            int[] opi = {-1,-1,-1};
            char[] oper = {'X','X','X'};
            int[] opparity = {-1,-1,-1};
            int idx = 0;
            exp = equation.exp;
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
            char[] o = {'X','X','X'};
            if (right1 < left2) {
                // (oxo)x(oxo)
                o[0] = oper[0];
                o[1] = oper[1];
                o[2] = oper[2];
            } else if (left1 == 1 && left2 == 2) {
                // ((oxo)xo)xo
                o[0] = oper[0];
                o[1] = oper[2];
                o[2] = inverseoper(oper[1]);
            } else if (right2 == equation.exp.length()-2 && right1 == equation.exp.length()-3) {
                // ox(ox(oxo))
                o[0] = inverseoper(oper[1]);
                o[1] = oper[0];
                o[2] = oper[2]; 
            } else if (left1 == 1) {
                // (ox(oxo))xo
                o[0] = oper[1];
                o[1] = oper[2];
                o[2] = inverseoper(oper[0]);
            } else if (right2 == equation.exp.length()-2) {
                // ox((oxo)xo)
                o[0] = oper[1];
                o[1] = oper[0];
                o[2] = inverseoper(oper[2]);
            } else {
                System.out.println("impossible!!!");
            }
            int x = -1, y = -1;
            if (o[0] == '+')x = 0;
            else if (o[0] == '-')x = 1;
            else if (o[0] == '*')x = 2;
            else if (o[0] == '/')x = 3;
            if (o[2] == '+')y = 0;
            else if (o[2] == '-')y = 1;
            else if (o[2] == '*')y = 2;
            else if (o[2] == '/')y = 3;
            int det = table[x][y];
            if (equation.result == 0) {
                if (o[1] == '+' || o[1] == '-') {
                    if (check0[det])continue;
                    else check0[det] = true;
                }
            } else if (equation.result == 1) {
                if (o[1] == '*' || o[1] == '/') {
                    if (check1[det])continue;
                    else check1[det] = true;
                }
            } else {
                System.out.println("impossible!!!"); 
            }
            res.add(equation);
        }
        return res;
    }

    public static String callcode(String expression) {
        return "_"+expression.replace('(','L').replace(')','R').replace('+','A')
        .replace('-','S').replace('*','M').replace('/','D');
    }
}