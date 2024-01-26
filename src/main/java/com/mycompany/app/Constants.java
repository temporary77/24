public class Constants {

	public static double trgt = 24;

	public static double largedet = trgt*1.5;

	public static int start = 0;

	public static int end = 9;

	public static final double EPSILON = 1e-10;

	// SCORE EVAL

	public static double DEFAULT_SCORE = 7.714;

	public static double INIT_DET = -2.1996; // 6.18

	public static double CNT_WEIGHT_DET = -0.2696; // 0.27

	public static double USELESS0 = 0.55; // 5

	public static double DEAD0 = 0.6276; // 12.5

	public static double[] DRAG0 = {1,0.3,0.2,0.1}; // 0,8,16,30

	public static double USELESS1 = 0.5789; // 3

	public static double DEAD1 = 0.6513; // 7.5

	public static double LARGERANGE = 0.06; // 250

	public static double LARGEWEIGHT = 0.1085; // 0.77 // 0.71

	public static double FRACTWEIGHT = 1.9675; // 0.2

	// public static double[] ALTERWEIGHT = {1,1,1,1}; //1.39,1,0.82,1 // 1.22,1,0.86,0.91 // 1.3,1,0.7,1

	public static double[] CONSECUTIVEWEIGHT = {0.8616,0.5381};

	public static double[] ONETWOWEIGHT = {0.8105,1.4008,0.8438,1.2176}; // 0.86, 0.79

	// public static double[] TWOONEWEIGHT = {1,1}; // 1.42, 0.68

	public static double[] ALTERWEIGHT = {1.5912,1.2025};

	public static double[] PAIRSWEIGHT = {1.5946,0.9697}; // 0.73, 1.14

	public static double ABCD_WEIGHT = 1.4023;

	public static double AABC_WEIGHT = 0.91; // 1.44 // 1.8 // 1.75

	public static double AABB_WEIGHT = 0.6867; // 2.3 // 1.88

	public static double AAAB_WEIGHT = 0.7176; // 2.85 // 1.8

	public static double AAAA_WEIGHT = 0.4831; // 3

	public static double SUM_WEIGHT = 0.6547; // 1

	public static double DISTRIBUTION_WEIGHT = 0.412;
}

// 0.5714461415384615

/*
sol cnt
fract cnt
largest
alternations
useless 1s
dead 1s
*/
