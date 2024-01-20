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
        FILTER0.add("(0*"); // replace // location matters
        FILTER0.add("*0)"); // replace // location matters
        FILTER0.add("(0/"); // replaced // location matters

        FILTER1.add("*1)"); // moved // multiple locations
        FILTER1.add("(1*"); // moved // multiple locations
        FILTER1.add("/1)"); // moved // multiple locations

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

    public static String callcode(String expression) {
        return "_"+expression.replace('(','L').replace(')','R').replace('+','A')
        .replace('-','S').replace('*','M').replace('/','D');
    }
}