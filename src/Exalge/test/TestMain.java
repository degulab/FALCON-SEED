import java.math.BigDecimal;
import java.math.MathContext;
import java.util.regex.Pattern;

import exalge2.ExtendedKeyID;

/**
 * 
 */

/**
 * @author ishizuka
 *
 */
public class TestMain {
	public static void main(String[] args) {
		System.out.println("<<< Start test! >>>");
		System.out.println("  args[" + args.toString() + "]");
		
		// BigDecimal の挙動確認
		/*---
		BigDecimal src = new BigDecimal(1);
		BigDecimal div = new BigDecimal(10);
		BigDecimal dst1 = src.divide(div);
		System.out.println("1 / 10 = " + dst1.toString());
		BigDecimal dst2 = dst1.divide(div);
		System.out.println(dst1.toString() + " / 10 = " + dst2.toString());
		BigDecimal dst3 = dst2.divide(div);
		System.out.println(dst2.toString() + " / 10 = " + dst3.toString());
		BigDecimal dst4 = dst3.divide(div);
		System.out.println(dst3.toString() + " / 10 = " + dst4.toString());
		
		BigDecimal div2 = new BigDecimal(3);
		BigDecimal dst5 = src.divide(div2);
		System.out.println(src.toString() + " / " + div2.toString() + " = " + dst5.toString());
		BigDecimal srcOne = new BigDecimal(1);
		BigDecimal divZero = new BigDecimal(0);
		BigDecimal dst = srcOne.divide(divZero);
		---*/
		
		// BigDecimal の精度確認
		BigDecimal src = new BigDecimal(1);
		BigDecimal div = new BigDecimal(3);
		//---
		BigDecimal r1 = src.divide(div, BigDecimal.ROUND_CEILING);
		System.out.println("[CEILING  ]" + src.toString() + " / " + div.toString() + " = " + r1.toString());
		BigDecimal r2 = src.divide(div, BigDecimal.ROUND_DOWN);
		System.out.println("[DOWN     ]" + src.toString() + " / " + div.toString() + " = " + r2.toString());
		BigDecimal r3 = src.divide(div, BigDecimal.ROUND_FLOOR);
		System.out.println("[FLOOR    ]" + src.toString() + " / " + div.toString() + " = " + r3.toString());
		BigDecimal r4 = src.divide(div, BigDecimal.ROUND_HALF_DOWN);
		System.out.println("[HALF_DOWN]" + src.toString() + " / " + div.toString() + " = " + r4.toString());
		BigDecimal r5 = src.divide(div, BigDecimal.ROUND_HALF_EVEN);
		System.out.println("[HALF_EVEN]" + src.toString() + " / " + div.toString() + " = " + r5.toString());
		BigDecimal r6 = src.divide(div, BigDecimal.ROUND_HALF_UP);
		System.out.println("[HALF_UP  ]" + src.toString() + " / " + div.toString() + " = " + r6.toString());
		BigDecimal r7 = src.divide(div, BigDecimal.ROUND_UP);
		System.out.println("[UP       ]" + src.toString() + " / " + div.toString() + " = " + r7.toString());
		//BigDecimal r8 = src.divide(div, BigDecimal.ROUND_UNNECESSARY);
		//System.out.println("[UNNECESSA]" + src.toString() + " / " + div.toString() + " = " + r8.toString());
		//---
		BigDecimal d1 = src.divide(div, MathContext.DECIMAL128);
		System.out.println("[MathContext.DECIMAL128]" + src.toString() + " / " + div.toString() + " = " + d1.toString());
		BigDecimal d2 = src.divide(div, MathContext.DECIMAL64);
		System.out.println("[MathContext.DECIMAL64 ]" + src.toString() + " / " + div.toString() + " = " + d2.toString());
		BigDecimal d3 = src.divide(div, MathContext.DECIMAL32);
		System.out.println("[MathContext.DECIMAL32 ]" + src.toString() + " / " + div.toString() + " = " + d3.toString());
		//BigDecimal d4 = src.divide(div, MathContext.UNLIMITED);
		//System.out.println("[MathContext.UNLIMITED ]" + src.toString() + " / " + div.toString() + " = " + d4.toString());
		//---
		for (int i = 1; i <= 10; i++) {
			BigDecimal idiv = new BigDecimal(i);
			BigDecimal idst = src.divide(idiv, MathContext.DECIMAL128);
			System.out.println("[MathContext.DECIMAL128]" + src.toString() + " / " + idiv.toString() + " = " + idst.toString());
			BigDecimal odst = src.divide(idiv, 34, BigDecimal.ROUND_HALF_EVEN);
			System.out.println("[ scale=34 / HALF_EVEN ]" + src.toString() + " / " + idiv.toString() + " = " + odst.toString());
		}
		
		// String#split テスト
		String delim = "-";
		String strNull = null;
		//String[] ret1 = strNull.split(delim);
		String[] ret2 = "".split(delim);
		System.out.println(showStringArray("\"\".split(" + delim + ")", ret2));
		String[] ret3 = "name".split(delim);
		System.out.println(showStringArray("\"name\".split(" + delim + ")", ret3));
		String[] ret4 = "name--unit".split(delim);
		System.out.println(showStringArray("\"name--unit\".split(" + delim + ")", ret4));
		
		// Enum テスト
		ExtendedKeyID enumUnit = ExtendedKeyID.UNIT;
		ExtendedKeyID enumTime = ExtendedKeyID.TIME;
		ExtendedKeyID enumSubject = ExtendedKeyID.SUBJECT;
		System.out.println("[ExtendedKeyID.UNIT] = " + enumUnit.toString());
		System.out.println("[ExtendedKeyID.TIME] = " + enumTime.toString());
		System.out.println("[ExtendedKeyID.SUBJECT] = " + enumSubject.toString());
		
		// Pattern テスト
		final char delimChar = '-';
		Pattern pat = Pattern.compile("\\*+");
		String str0 = "名前-HAT-$-Y2000M01-C";
		String str1 = "名前-HAT-$-Y2000M01-A";
		String str2 = "名前-NO_HAT-yen-Y2000-BA";
		String str3 = "-----Y2000-A";
		String pat0 = "\\A**-*-****-Y2000*-*A***\\z";
		String pat1 = pat.matcher(pat0).replaceAll("[^-]*");
		boolean rb0 = str0.matches(pat1);
		boolean rb1 = str1.matches(pat1);
		boolean rb2 = str2.matches(pat1);
		boolean rb3 = str3.matches(pat1);
		System.out.println("Pat[" + pat1 + "] matches(" + str0 + ") = " + rb0);
		System.out.println("Pat[" + pat1 + "] matches(" + str1 + ") = " + rb1);
		System.out.println("Pat[" + pat1 + "] matches(" + str2 + ") = " + rb2);
		System.out.println("Pat[" + pat1 + "] matches(" + str3 + ") = " + rb3);
		
		// Pattern テスト２
		String strPattern = "\\A[^\\s<>\\-+/=\\^,~%&?|@'\"*]+\\z";
		String strMatchers1 = "!\"#$%&'()-=^~|@`;+:*[{]},<.>/?\\_";
		String strMatchers2 = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Pattern pat2 = Pattern.compile(strPattern);
		System.out.println();
		System.out.println("Pattern test - \"" + strPattern + "\"");
		System.out.println("  [ ] " + pat2.matcher(" ").matches());
		System.out.println("  [\\t] " + pat2.matcher("\t").matches());
		System.out.println("  [\\n] " + pat2.matcher("\n").matches());
		System.out.println("  [\\f] " + pat2.matcher("\f").matches());
		System.out.println("  [\\r] " + pat2.matcher("\r").matches());
		System.out.println("  [\\x0B] " + pat2.matcher("\u000B").matches());
		showMachingChar(pat2, strMatchers1);
		showMachingChar(pat2, strMatchers2);
		System.out.println("  [] " + pat2.matcher("").matches());
		System.out.println("  [a^b] " + pat2.matcher("a^b").matches());
		System.out.println("  [123] " + pat2.matcher("123").matches());
		System.out.println("fin - Pattern test");
		
		// Pattern テスト３
		String strHatPattern = "\\A(HAT|NO_HAT|\\^)?\\z";
		Pattern pat3 = Pattern.compile(strHatPattern, Pattern.CASE_INSENSITIVE);
		System.out.println();
		System.out.println("HAT Pattern test - \"" + strHatPattern + "\"");
		System.out.println("  [HAT] " + pat3.matcher("HAT").matches());
		System.out.println("  [hat] " + pat3.matcher("hat").matches());
		System.out.println("  [HaT] " + pat3.matcher("HaT").matches());
		System.out.println("  [No_hAT] " + pat3.matcher("No_hAT").matches());
		System.out.println("  [No-Hat] " + pat3.matcher("No-Hat").matches());
		System.out.println("  [^] " + pat3.matcher("^").matches());
		System.out.println("  [^HAT] " + pat3.matcher("^HAT").matches());
		System.out.println("  [HatA]" + pat3.matcher("HatA").matches());
		System.out.println("  [Ha*]" + pat3.matcher("Ha*").matches());
		System.out.println("  [*] " + pat3.matcher("*").matches());
		System.out.println("  [#] " + pat3.matcher("#").matches());
		System.out.println("  [] " + pat3.matcher("").matches());
		System.out.println("fin - HAT Pattern test");
		
		System.out.println("<<< End of test! >>>");
	}
	
	private static void showMachingChar(Pattern pat, String chars) {
		for (int i = 0; i < chars.length(); i++) {
			String match = String.valueOf(chars.charAt(i));
			String result = String.valueOf(pat.matcher(match).matches());
			System.out.println("  [" + match + "] " + result);
		}
	}
	
	private static String showStringArray(String desc, String[] val) {
		StringBuffer sb = new StringBuffer();
		sb.append(desc);
		sb.append(" = ");
		sb.append(val.length);
		sb.append(" : {");
		for (int i = 0; i < val.length; i++) {
			if (i > 0) {
				sb.append(" ,");
			}
			sb.append("\"");
			sb.append(val[i]);
			sb.append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
}
