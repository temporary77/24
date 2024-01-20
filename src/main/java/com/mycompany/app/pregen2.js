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

document.write("public class Expressions2 {<br>");

// 2 + 2 == 2 * 2
writeExpression("EXP_22AB","2","2","a","b");
writeExpression("EXP_22AA","2","2","a","a");
// writeExpression("EXP_222A","2","2","2","a"); // (2*a-2)/2 = a-2+(2/2)

// writeExpression("EXP_022A","0","2","2","a"); // a*(2/2)+0 = a+0*(2+2)

// a * 1 == a / 1 && 2 + 2 == 2 * 2
// writeExpression("EXP_122A","1","2","2","a"); // (a*2)*(2-1) = (a+1)*2-2

// 2 * a - a == (a + a) / 2
writeExpression("EXP_2AAB","2","a","a","b");
writeExpression("EXP_2AAA","2","a","a","a");

writeExpression("EXP_02AA","0","2","a","a");
// 0 0 2 a is accounted for with 0 0 a b

// a * 1 == a / 1 && 2 * a - a == (a + a) / 2
writeExpression("EXP_12AA","1","2","a","a");
// writeExpression("EXP_112A","1","1","2","a"); // (2-1*1) = (1+1)/2

// 3 * a - a - a == (a + a + a) / 3 
writeExpression("EXP_3AAA","3","a","a","a");

// a,a+1 has useless ((a+1)-a) = 1

// 2,a,a+2 has duplicates ((a+2)-a) = 2

// 4 - 2 == 4 / 2
writeExpression("EXP_24AB","2","4","a","b");
writeExpression("EXP_24AA","2","4","a","a");
// writeExpression("EXP_024A","0","2","4","a"); 0*4+2 = 4/2+0
// writeExpression("EXP_124A","1","2","4","a"); // 4*2-1 = 4+2+1
// writeExpression("EXP_224A","2","2","4","a"); // (2+4)/2 = 4-(2/2) 
// writeExpression("EXP_244A","2","4","4","a"); // (4*a-4)/2 = (a-4/4)*2 = 2a-2

document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] EXP_AB = {\"a+b\",\"a*b\",\"a-b\",\"b-a\",\"a/b\",\"b/a\"};<br><br>");
document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] EXP_AA = {\"a+a\",\"a*a\",\"a-a\",\"a/a\"};<br><br>");
document.write("}");