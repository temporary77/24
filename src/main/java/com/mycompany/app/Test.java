import java.util.List;
import java.util.Arrays;

public class Test {

	public static final int trgt = 24;

	public static void test(int a, int b, int c, int d, int ans) {
		int[] arr = {a,b,c,d};
		SolPackage solutions = Solver.solve4(arr,trgt);
		// System.out.println(Arrays.toString(arr)+"\n  has "+ans+"\nfound "+solutions.solutions.size());
		for (Equation eqn : solutions.solutions) {
			System.out.println(eqn.exp+"=24");
		}
	}

	public static void main(String[] args) {
		Solver.solve4(new int[] {1, 1, 1, 1},trgt);
		long t = System.currentTimeMillis();
		test(1,3,4,6,0);
		test(2,3,5,12,0);
		test(1,4,5,6,0);
		test(2,5,5,10,0);
		test(3,5,7,13,0);
		test(3,3,8,8,0);
		test(1,6,6,8,0);
		test(2,4,10,10,0);
		test(3,6,6,11,0);
		test(2,4,7,12,0);
		test(2,9,13,13,0);
		test(3,7,9,13,0);
		test(3,3,5,7,0);
		test(1,8,12,12,0);
		test(9,11,12,13,0);
		test(2,3,8,13,0);
		test(2,2,10,11,0);
		test(7,8,10,11,0);
		test(5,5,7,11,0);
		test(5,7,7,11,0);
		test(4,8,8,11,0);
		test(3,3,7,13,0);
		test(3,8,8,10,0);
		test(2,7,7,10,0);
		test(4,8,8,13,0);
		test(2,5,7,8,0);
		test(7,8,8,13,0);
		test(6,9,9,10,0);
		test(5,6,9,11,0);
		test(1,4,7,11,0);
		test(2,2,6,13,0);
		test(2,7,8,9,0);
		test(5,6,8,13,0);
		test(7,10,12,13,0);
		test(2,2,13,13,0);
		test(2,4,11,12,0);
		test(6,11,12,12,0);
		test(6,7,8,12,0);
		test(5,8,9,13,0);
		test(6,12,12,13,0);
		test(5,7,10,11,0);
		test(4,5,6,11,0);
		test(3,4,6,13,0);
		test(3,5,8,12,0);
		test(4,7,11,13,0);
		test(4,4,10,10,0);
		test(4,6,7,9,0);
		test(4,5,6,12,0);
		test(5,7,9,12,0);
		test(4,4,7,7,0);
		test(2,2,11,11,0);
		test(1,3,9,10,0);
		test(4,6,6,10,0);
		test(1,6,11,13,0);
		test(2,5,6,9,0);
		test(8,9,10,12,0);
		test(3,5,8,13,0);
		test(3,3,7,7,0);
		test(6,7,7,11,0);
		test(5,10,10,13,0);
		test(1,5,5,5,0); // first triple
		test(5,9,10,11,0);
		// a = b = c = d
		// test(3, 3, 3, 3, 1);
		// // a = b = c < d
		// test(1, 1, 1, 12, 1);
		// test(2, 2, 2, 8, 3);
		// test(3, 3, 3, 5, 1);
		// test(3, 3, 3, 4, 2);
		// // a = b < c = d
		// test(1, 1, 5, 5, 1);
		// test(2, 2, 3, 3, 2);
		// test(3, 3, 5, 5, 1);
		// test(4, 4, 5, 5, 3);
		// // a = b < c < d
		// test(1, 1, 4, 5, 2);
		// test(2, 2, 4, 5, 4);
		// test(2, 2, 3, 12, 6);
		// test(3, 3, 5, 6, 7);
		// test(3, 3, 4, 8, 3);
		// // a < b = c = d
		// test(1, 8, 8, 8, 1);
		// test(2, 3, 3, 3, 2);
		// // a < b = c < d
		// test(1, 2, 2, 6, 2);
		// test(1, 3, 3, 4, 3);
		// test(2, 6, 6, 8, 5);
		// test(2, 3, 3, 8, 3);
		// test(2, 12, 12, 13, 2);
		// test(2, 3, 3, 4, 0);
		// test(3, 6, 6, 9, 4);
		// test(3, 4, 4, 6, 5);
		// test(4, 6, 6, 7, 2);
		// test(5, 6, 6, 7, 4);
		// // a < b < c = d
		// test(1, 3, 6, 6, 3);
		// test(1, 2, 12, 12, 2);
		// test(2, 4, 6, 6, 5);
		// test(2, 3, 12, 12, 4);
		// test(2, 11, 12, 12, 2);
		// test(2, 3, 4, 4, 3);
		// test(4, 6, 8, 8, 4);
		// test(3, 4, 12, 12, 2);
		// test(3, 7, 8, 8, 3);
		// test(11, 12, 13, 13, 2);
		// // a < b < c < d
		// test(1, 4, 6, 8, 3);
		// test(1, 2, 3, 8, 4);
		// test(2, 4, 6, 12, 10);
		// test(3, 4, 11, 13, 4);
		// test(3, 5, 6, 8, 3);
		// test(4, 6, 9, 10, 4);
		// test(3, 4, 5, 8, 6);
		// test(3, 8, 9, 10, 3);
		// test(2, 3, 7, 8, 4);
		// test(5, 6, 7, 8, 3);
		System.out.println(System.currentTimeMillis() - t + "ms");
	}
}