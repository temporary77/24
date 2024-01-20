var A = Math.random()*5;
var B = Math.random()*5;
var C = Math.random()*5;
var D = Math.random()*5;
var PARENTHESES = ["((oxo)xo)xo","(ox(oxo))xo","(oxo)x(oxo)","ox((oxo)xo)","ox(ox(oxo))"];
var OPERATORS = ["+","-","*","/"];

function writeExpression(name, a, b, c, d) {
	var mp = {};
	var st = [a,b,c,d];
	for (var i = 0; i < 4; ++i) {
		for (var j = 0; j < 4; ++j) {
			for (var k = 0; k < 4; ++k) {
				for (var l = 0; l < 4; ++l) {
					if (i == j || i == k || i == l || j == k || j == l || k == l)continue;
					mp[st[i]+","+st[j]+","+st[k]+","+st[l]] = true;
				}
			}
		}
	}

	var valmp = {};
	for (var i = 0; i < PARENTHESES.length; ++i) {
		var parenthese = PARENTHESES[i];
		for (var set1 in mp) {
			var arr = set1.split(",");
			var positions = parenthese;
			for (var j = 0; j < 4; ++j) {
				positions = positions.replace("o",arr[j]);
			}
			for (var j of OPERATORS) {
				for (var k of OPERATORS) {
					for (var l of OPERATORS) {
						var expression = positions;
						expression = expression.replace("x",j);
						expression = expression.replace("x",k);
						expression = expression.replace("x",l);
						var valuation = expression;
						valuation = valuation.split("a").join(A);
						valuation = valuation.split("b").join(B);
						valuation = valuation.split("c").join(C);
						valuation = valuation.split("d").join(D);
						valuation = eval(valuation);
						if (!isFinite(valuation)) {
							continue;
						}
						var key = "_"+Math.floor(valuation*1e9+0.5);
						if (key in valmp) {
			              	var olddivs = valmp[key].split("/").length;
			            	var newdivs = expression.split("/").length;
							if (newdivs < olddivs) {
								valmp[key] = expression;
							}
						} else {
							valmp[key] = expression;
						}
					}
				}
			}
		}
	}
	var expressions = [];
	for (var value in valmp) {
		expressions.push(valmp[value]);
	}
	document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] "+name+" = {<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\""+expressions.join("\",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"")+"\"<br>&nbsp;&nbsp;&nbsp;&nbsp;};<br><br>");
}

document.write("public class Expressions3 {<br>");

// pure numbers
// writeExpression("EXP_0000","0","0","0","0"); // 0+0+0+0 == 0-0-0-0 // a+a+a+a != a-a-a-a

// 0 0 0 a generalized

// writeExpression("EXP_0011","0","0","1","1"); // 0+0+1*1 == 0+0+1/1 // 0+0+a*a != 0+0+a/a
// writeExpression("EXP_0111","0","1","1","1"); // 0+1+1*1 == 0+1+1/1 // 0+a+a*a != 0+a+a/a
// writeExpression("EXP_1111","1","1","1","1"); // 1*1*1*1 == 1/1/1/1

// writeExpression("EXP_0022","0","0","2","2"); // 0+0+2+2 == 0+0+2*2
// writeExpression("EXP_0112","0","1","1","2");
// writeExpression("EXP_0122","0","1","2","2"); // 2+2/1 = 2*2-1
// writeExpression("EXP_0222","0","2","2","2");  // 2*2-2 = (2*2)*0+2
// writeExpression("EXP_1112","1","1","1","2");
// writeExpression("EXP_1122","1","1","2","2");
// writeExpression("EXP_1222","1","2","2","2");
// writeExpression("EXP_2222","2","2","2","2");

// writeExpression("EXP_1113","1","1","1","3"); // 1*1+1+3 = 3*(1+1)-1
// writeExpression("EXP_2223","2","2","2","3"); // 3*2*2-2 = 3+2+2/2
// writeExpression("EXP_3333","3","3","3","3"); // 

// writeExpression("EXP_0024","0","0","2","4"); // 0*4+2 = 4/2+0
// writeExpression("EXP_0124","0","1","2","4"); // 4*0+1+2 = 4/2+1
// writeExpression("EXP_0224","0","2","2","4"); // 4*2/2+0 = 4*0+2+2
// writeExpression("EXP_0244","0","2","4","4"); // 4+4-2-0 = 2*0+4+2
// writeExpression("EXP_1124","1","1","2","4"); // 4*2+1+1 = (4+1)*2*1
// writeExpression("EXP_1224","1","2","2","4"); // (2+1)*(4-2) = 6, 4+(2/2)+1 = 6
// writeExpression("EXP_1244","1","2","4","4"); // (4-2-1)*4 = 4, (4*2)-(1*4) = 4
// writeExpression("EXP_2224","2","2","2","4"); // (4-2/2)*2 = 6, (4*2/2)+2 = 6
// writeExpression("EXP_2244","2","2","4","4"); // 4*4-2*2 = 12, 2*(4*2-2) = 12
// writeExpression("EXP_2444","2","4","4","4"); // (4+4/4)*2 = 10, 4*4-4-2 = 10

document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] EXP_AB = {\"a+b\",\"a*b\"};<br><br>");
document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] EXP_AA = {\"a+a\",\"a*a\"};<br><br>");
document.write("}");