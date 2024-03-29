var A = Math.random()*5;
var B = Math.random()*5;
var C = Math.random()*5;
var D = Math.random()*5;
// var PARENTHESES4 = ["ox(ox(oxo))","ox((oxo)xo)","(oxo)x(oxo)","(ox(oxo))xo","((oxo)xo)xo"];
// var PARENTHESES3 = ["ox(oxo)","(oxo)xo"];
var PARENTHESES4 = ["((oxo)xo)xo","(ox(oxo))xo","(oxo)x(oxo)","ox((oxo)xo)","ox(ox(oxo))"];
var PARENTHESES3 = ["(oxo)xo","ox(oxo)"];
var OPERATORS = ["+","-","*","/"];

function writeExpression4(name, a, b, c, d) {
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
	for (var i = 0; i < PARENTHESES4.length; ++i) {
		var parenthese = PARENTHESES4[i];
		for (var j of OPERATORS) {
			for (var k of OPERATORS) {
				for (var l of OPERATORS) {
					for (var set1 in mp) {
						var arr = set1.split(",");
						var positions = parenthese;
						for (var m = 0; m < 4; ++m) {
							positions = positions.replace("o",arr[m]);
						}
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

function writeExpression3(name, a, b, c) {
	var mp = {};
	var st = [a,b,c];
	for (var i = 0; i < 4; ++i) {
		for (var j = 0; j < 4; ++j) {
			for (var k = 0; k < 4; ++k) {
				if (i == j || i == k || j == k)continue;
				mp[st[i]+","+st[j]+","+st[k]] = true;
			}
		}
	}

	var valmp = {};
	for (var i = 0; i < PARENTHESES3.length; ++i) {
		var parenthese = PARENTHESES3[i];
		for (var j of OPERATORS) {
			for (var k of OPERATORS) {
				for (var set1 in mp) {
					var arr = set1.split(",");
					var positions = parenthese;
					for (var m = 0; m < 3; ++m) {
						positions = positions.replace("o",arr[m]);
					}
					var expression = positions;
					expression = expression.replace("x",j);
					expression = expression.replace("x",k);
					var valuation = expression;
					valuation = valuation.split("a").join(A);
					valuation = valuation.split("b").join(B);
					valuation = valuation.split("c").join(C);
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
	var expressions = [];
	for (var value in valmp) {
		expressions.push(valmp[value]);
	}
	document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] "+name+" = {<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\""+expressions.join("\",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"")+"\"<br>&nbsp;&nbsp;&nbsp;&nbsp;};<br><br>");
}

document.write("public class Expressions extends Expressions2 {<br>");

writeExpression4("EXP_ABCD","a","b","c","d");
writeExpression4("EXP_AABC","a","a","b","c");
writeExpression4("EXP_AABB","a","a","b","b");
writeExpression4("EXP_AAAB","a","a","a","b");
writeExpression4("EXP_AAAA","a","a","a","a");

writeExpression3("EXP_ABC","a","b","c");
writeExpression3("EXP_AAB","a","a","b");
writeExpression3("EXP_AAA","a","a","a");

document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] EXP_AB = {\"a+b\",\"a*b\",\"a-b\",\"b-a\",\"a/b\",\"b/a\"};<br><br>");
document.write("&nbsp;&nbsp;&nbsp;&nbsp;public static final String[] EXP_AA = {\"a+a\",\"a*a\",\"a-a\",\"a/a\"};<br><br>");
document.write("}");