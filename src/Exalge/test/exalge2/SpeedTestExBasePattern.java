/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
package exalge2;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import exalge2.util.ExQuarterTimeKey;

/**
 * <code>ExBase</code> の速度チェック
 */
public class SpeedTestExBasePattern extends TestCase {
	
	static private final String[] TEXTS1 = new String[]{
		"あ", "い", "う", "え", "お",
		"か", "き", "く", "け", "こ",
		"が", "ぎ", "ぐ", "げ", "ご",
		"さ", "し", "す", "せ", "そ",
		"ざ", "じ", "ず", "ぜ", "ぞ",
		"た", "ち", "つ", "て", "と",
		"だ", "ぢ", "づ", "で", "ど",
		"な", "に", "ぬ", "ね", "の",
		"は", "ひ", "ふ", "へ", "ほ",
		"ば", "び", "ぶ", "べ", "ぼ",
		"ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
		"ま", "み", "む", "め", "も",
		"や", "ゆ", "よ",
		"ら", "り", "る", "れ", "ろ",
		"わ",
	};
	
	static private final String[] TEXTS2 = new String[]{
		"あ", "い", "う", "え", "お",
		"か", "き", "く", "け", "こ",
		"が", "ぎ", "ぐ", "げ", "ご",
		"さ", "し", "す", "せ", "そ",
		"ざ", "じ", "ず", "ぜ", "ぞ",
		"た", "ち", "つ", "て", "と", "っ",
		"だ", "ぢ", "づ", "で", "ど",
		"な", "に", "ぬ", "ね", "の",
		"は", "ひ", "ふ", "へ", "ほ",
		"ば", "び", "ぶ", "べ", "ぼ",
		"ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
		"ま", "み", "む", "め", "も",
		"や", "ゆ", "よ", "ゃ", "ゅ", "ょ",
		"ら", "り", "る", "れ", "ろ",
		"わ", "を", "ん",
	};
	
	static private final int MAX_TEXTS = 10;
	static private final long INIT_SEED = 20090209L;
	//static private final int MAX_NAMES = 100;
	//static private final int MAX_SUBJECTS = 100;
	//static private final int MAX_TIMES = 100;
	static private final int MAX_NAMES = 50;
	static private final int MAX_SUBJECTS = 50;
	static private final int MAX_TIMES = 50;

	private Random[] randForName;
	private Random   randForHat;
	private String[] baseNames;
	private String[] baseTimes;
	private String[] baseSubjects;
	
	private ExAlgeSet testAlgeSet;
	private Exalge    testAlge;
	private ExBasePatternSet testPatterns;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		System.out.println();
		System.out.println("@@@ Make test data @@@");
		
		// 乱数生成(HAT)
		randForHat = new Random(INIT_SEED);
		
		// 乱数生成(Names)
		randForName = new Random[MAX_TEXTS];
		for (int i = 0; i < MAX_TEXTS; i++) {
			long seed = randForHat.nextLong();
			randForName[i] = new Random(seed);
		}
		
		// 名前基底キーの生成
		StringBuilder sb = new StringBuilder(MAX_TEXTS);
		baseNames = new String[MAX_NAMES];
		for (int i = 0; i < MAX_NAMES; i++) {
			sb.setLength(0);
			String text = TEXTS1[randForName[0].nextInt(TEXTS1.length)];
			sb.append(text);
			
			for (int j = 1; j < MAX_TEXTS; j++) {
				text = TEXTS2[randForName[j].nextInt(TEXTS2.length)];
				sb.append(text);
			}
			
			baseNames[i] = sb.toString();
		}
		
		// 主体基底キーの生成
		baseSubjects = new String[MAX_SUBJECTS];
		for (int i = 0; i < MAX_SUBJECTS; i++) {
			sb.setLength(0);
			String text = TEXTS1[randForName[0].nextInt(TEXTS1.length)];
			sb.append(text);
			
			for (int j = 1; j < MAX_TEXTS; j++) {
				text = TEXTS2[randForName[j].nextInt(TEXTS2.length)];
				sb.append(text);
			}
			
			baseSubjects[i] = sb.toString();
		}
		
		// 時間基底キーの生成
		baseTimes = new String[MAX_TIMES];
		ExQuarterTimeKey time = new ExQuarterTimeKey(2001, 1);
		for (int i = 0; i < MAX_TIMES; i++) {
			String text = time.toString();
			baseTimes[i] = text;
			time.addUnit(1);
		}
		final int algeTimes = 10;
		String[] baseShapedTimes = new String[algeTimes];
		for (int i = 0; i < baseShapedTimes.length; i++) {
			baseShapedTimes[i] = baseTimes[randForHat.nextInt(baseTimes.length)];
		}
		
		// テスト用交換代数集合の生成
		System.out.print("    - make ExAlgeSet...");
		testAlgeSet = new ExAlgeSet(baseNames.length * baseTimes.length * baseSubjects.length);
		long count = 0L;
		for (String strName : baseNames) {
			for (String strTime : baseShapedTimes) {
				for (String strSubject : baseSubjects) {
					ExBase base = new ExBase(strName, ExBase.HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
					BigDecimal value = new BigDecimal(count);
					Exalge alge = new Exalge(base, value);
					testAlgeSet.add(alge);
				}
			}
		}
		System.out.println("O.K!(" + testAlgeSet.size() + ")");
		
		// テスト用交換代数元の生成
		System.out.print("    - make Exalge...");
		testAlge = testAlgeSet.sum();
		System.out.println("O.K!(" + testAlge.getNumElements() + ")");
		
		// テスト用基底パターンの作成
		System.out.print("    - make ExBasePatternSet...");
		testPatterns = new ExBasePatternSet();
		//--- 固定パターン
		for (int c = 0; c < 15; c++) {
			for (int i = baseNames.length-1; i >= 0; i--) {
				String strName = baseNames[i % baseNames.length];
				String strTime = baseTimes[(i + c) % baseTimes.length];
				String strSubject = baseSubjects[(i + c) % baseSubjects.length];
				ExBasePattern pat = new ExBasePattern(strName, ExBase.HAT, ExBase.OMITTED, strTime, strSubject);
				testPatterns.add(pat);
			}
		}
		//System.err.println(" - testPatterns elements : " + testPatterns.size());
		//--- HAT, 単位キーのみワイルドカード
		for (int c = 1; c <= 15; c++) {
			for (int i = baseNames.length-1; i >= 0; i--) {
				String strName = baseNames[i % baseNames.length];
				String strTime = baseTimes[(baseTimes.length+i-c) % baseTimes.length];
				String strSubject = baseSubjects[(baseSubjects.length+i-c) % baseSubjects.length];
				ExBasePattern pat = new ExBasePattern(strName, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, strTime, strSubject);
				testPatterns.add(pat);
			}
		}
		//System.err.println(" - testPatterns elements : " + testPatterns.size());
		//--- HAT, 名前キーのみワイルドカード
		for (int c = 1; c <= 15; c++) {
			for (int i = baseTimes.length-1; i >= 0; i--) {
				String strTime = baseTimes[i % baseTimes.length];
				String strSubject = baseSubjects[(i+c) % baseSubjects.length];
				ExBasePattern pat = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBase.OMITTED, strTime, strSubject);
				testPatterns.add(pat);
			}
		}
		//System.err.println(" - testPatterns elements : " + testPatterns.size());
		//--- HATキーがワイルドカード、主体キーがワイルドカードを含むパターン
		String[] basePatSubjects;
		{
			StringBuilder subsb = new StringBuilder(100);
			LinkedHashSet<String> subset = new LinkedHashSet<String>();
			for (String strSubject : baseSubjects) {
				subsb.setLength(0);
				subsb.append(strSubject.substring(0, 2));
				subsb.append(ExBasePattern.WILDCARD);
				subsb.append(strSubject.substring(4, 6));
				subsb.append(ExBasePattern.WILDCARD);
				subsb.append(strSubject.substring(8));
				subset.add(subsb.toString());
			}
			basePatSubjects = subset.toArray(new String[subset.size()]);
		}
		for (int c = 1; c <= 15; c++) {
			for (int i = baseNames.length-1; i >= 0; i--) {
				String strName = baseNames[i % baseNames.length];
				String strTime = baseTimes[(baseTimes.length+i-c) % baseTimes.length];
				String strSubject = basePatSubjects[(basePatSubjects.length+i-c-1) % basePatSubjects.length];
				ExBasePattern pat = new ExBasePattern(strName, ExBasePattern.WILDCARD, ExBase.OMITTED, strTime, strSubject);
				testPatterns.add(pat);
			}
		}
		System.out.println("O.K!(" + testPatterns.size() + ")");

		/*
		System.err.println();
		System.err.println("<< Show patterns for test >>");
		for (ExBasePattern pat : testPatterns) {
			System.err.println(pat.toString());
		}
		*/

		/*
		// テスト用基底パターン集合の生成
		testPatterns = new ExBasePatternSet(baseNames.length * baseTimes.length * baseSubjects.length);
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					int type = randForHat.nextInt(6);
					ExBasePattern pat;
					switch (type) {
						case 1 :
							pat = new ExBasePattern(ExBasePattern.WILDCARD, ExBase.HAT, ExBase.OMITTED, strTime, strSubject);
							break;
						case 2 :
							pat = new ExBasePattern(strName, ExBasePattern.WILDCARD, ExBase.OMITTED, strTime, strSubject);
							break;
						case 3 :
							pat = new ExBasePattern(strName, ExBase.HAT, ExBasePattern.WILDCARD, strTime, strSubject);
							break;
						case 4 :
							pat = new ExBasePattern(strName, ExBase.HAT, ExBase.OMITTED, ExBasePattern.WILDCARD, strSubject);
							break;
						case 5 :
							pat = new ExBasePattern(strName, ExBase.HAT, ExBase.OMITTED, strTime, ExBasePattern.WILDCARD);
							break;
						default :
							pat = new ExBasePattern(strName, ExBase.HAT, ExBase.OMITTED, strTime, strSubject);
					}
					testPatterns.add(pat);
				}
			}
		}
		*/
	}
	
	public void testNormalProjection() {
		ExBaseSet bases = testAlge.getBases();
		long tcStart, tcEnd;
		
		System.out.println();
		System.out.println("<< Performance check for projection by ExBaseSet from ExAlgeSet>>");
		tcStart = System.currentTimeMillis();
		testAlgeSet.projection(bases);
		tcEnd = System.currentTimeMillis();
		System.out.println("    - projection by ExBaseSet(" + bases.size() + ") from ExAlgeSet(" + testAlgeSet.size() + ")");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for projection by ExBaseSet from Exalge >>");
		tcStart = System.currentTimeMillis();
		testAlge.projection(bases);
		tcEnd = System.currentTimeMillis();
		System.out.println("    - projection by ExBaseSet(" + bases.size() + ") from Exalge(" + testAlge.getNumElements() + ")");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
	}

	/*--- この計算は除外
	public void testDefaultPatternProjection() {
		long count, tcStart, tcEnd;
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePattern in ExAlgeSet >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePattern pat : testPatterns) {
			testAlgeSet.patternProjection(pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in ExAlgeSet.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePattern in Exalge >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePattern pat : testPatterns) {
			testAlge.patternProjection(pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in Exalge.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
	}
	---*/
	
	public void testPatternProjectionWithRegexp() {
		long count, tcStart, tcEnd;
		
		System.out.println();
		System.out.println("<< Performance check for make ExBasePatternWithRegexp >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		LinkedHashSet<ExBasePatternWithRegexp> patterns = new LinkedHashSet<ExBasePatternWithRegexp>(testPatterns.size());
		for (ExBasePattern testpat : testPatterns) {
			ExBasePatternWithRegexp pat = new ExBasePatternWithRegexp(testpat.getNameKey(), testpat.getHatKey(), testpat.getUnitKey(), testpat.getTimeKey(), testpat.getSubjectKey());
			patterns.add(pat);
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - make " + patterns.size() + " patterns.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePatternWithRegexp in ExAlgeSet >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePatternWithRegexp pat : patterns) {
			patternProjection(testAlgeSet, pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in ExAlgeSet.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePatternWithRegexp in Exalge >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePatternWithRegexp pat : patterns) {
			patternProjection(testAlge, pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in Exalge.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
	}
	
	public void testPatternProjectionWithType() {
		long count, tcStart, tcEnd;
		
		System.out.println();
		System.out.println("<< Performance check for make ExBasePatternWithType >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		LinkedHashSet<ExBasePatternWithType> patterns = new LinkedHashSet<ExBasePatternWithType>(testPatterns.size());
		for (ExBasePattern testpat : testPatterns) {
			ExBasePatternWithType pat = new ExBasePatternWithType(testpat.getNameKey(), testpat.getHatKey(), testpat.getUnitKey(), testpat.getTimeKey(), testpat.getSubjectKey());
			patterns.add(pat);
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - make " + patterns.size() + " patterns.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePatternWithType in ExAlgeSet >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePatternWithType pat : patterns) {
			patternProjection(testAlgeSet, pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in ExAlgeSet.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePatternWithType in Exalge >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePatternWithType pat : patterns) {
			patternProjection(testAlge, pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in Exalge.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
	}
	
	public void testPatternProjectionWithStringCompare() {
		long count, tcStart, tcEnd;
		
		System.out.println();
		System.out.println("<< Performance check for make ExBasePatternWithStringCompare >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		LinkedHashSet<ExBasePatternWithStringCompare> patterns = new LinkedHashSet<ExBasePatternWithStringCompare>(testPatterns.size());
		for (ExBasePattern testpat : testPatterns) {
			ExBasePatternWithStringCompare pat = new ExBasePatternWithStringCompare(testpat.getNameKey(), testpat.getHatKey(), testpat.getUnitKey(), testpat.getTimeKey(), testpat.getSubjectKey());
			patterns.add(pat);
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - make " + patterns.size() + " patterns.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePatternWithStringCompare in ExAlgeSet >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePatternWithStringCompare pat : patterns) {
			patternProjection(testAlgeSet, pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in ExAlgeSet.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePatternWithStringCompare in Exalge >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBasePatternWithStringCompare pat : patterns) {
			patternProjection(testAlge, pat);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + count + " times in Exalge.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
	}
	
	public void testPatternProjectionCurrentImplement() {
		long tcStart, tcEnd;
		
		System.out.println();
		System.out.println("<< Performance check for make ExBasePattern >>");
		tcStart = System.currentTimeMillis();
		ExBasePatternSet patterns = new ExBasePatternSet(testPatterns.size());
		for (ExBasePattern testpat : testPatterns) {
			ExBasePattern pat = new ExBasePattern(testpat.getNameKey(), testpat.getHatKey(), testpat.getUnitKey(), testpat.getTimeKey(), testpat.getSubjectKey());
			patterns.add(pat);
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - make " + patterns.size() + " patterns.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePattern in ExAlgeSet >>");
		tcStart = System.currentTimeMillis();
		testAlgeSet.patternProjection(patterns);
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + testAlgeSet.size() + " times in ExAlgeSet.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for pattern matching with ExBasePattern in Exalge >>");
		tcStart = System.currentTimeMillis();
		testAlge.patternProjection(patterns);
		tcEnd = System.currentTimeMillis();
		System.out.println("    - matches for " + patterns.size() + " times in Exalge.");
		System.out.println("    - time span : " + (tcEnd - tcStart) + " ms");
	}

	static private Exalge patternProjection(Exalge targetAlge, ExBasePatternBase pattern) {
		ExBaseSet retBases = new ExBaseSet(targetAlge.getNumElements());
		for (ExBase base : targetAlge.data.keySet()) {
			if (pattern.matches(base)) {
				retBases.add(base);
			}
		}
		return targetAlge.projection(retBases);
	}
	
	static private ExAlgeSet patternProjection(ExAlgeSet targetSet, ExBasePatternBase pattern) {
		ExAlgeSet retSet = new ExAlgeSet(targetSet.size());
		for (Exalge alge : targetSet) {
			Exalge projAlge = patternProjection(alge, pattern);
			if (!projAlge.isEmpty()) {
				retSet.add(projAlge);
			}
		}
		return retSet;
	}
	
	static abstract class ExBasePatternBase extends AbExBase {
		abstract public boolean matches(ExBase exbase);

		public ExBase translate(ExBase exbase) {
			String[] newKeys = new String[NUM_ALL_KEYS];
			for (int i = 0; i < this._baseKeys.length; i++) {
				if (this._baseKeys[i].indexOf(ExBasePattern.WILDCARD) >= 0) {
					// ワイルドカード指定(置き換えない)
					newKeys[i] = exbase._baseKeys[i];
				}
				else {
					// ワイルドカードなし(置き換える)
					newKeys[i] = this._baseKeys[i];
				}
			}
			
			return new ExBase(newKeys);
		}
	}

	/**
	 * JAVA正規表現による基底パターン比較を行う、現在の実装。
	 */
	static class ExBasePatternWithRegexp extends ExBasePatternBase {
		static private final Pattern replaceTermPattern = Pattern.compile("\\\\E");
		static private final String replaceEscapeTerm = "\\\\E\\\\\\\\E\\\\Q";
		static private final Pattern replacePattern = Pattern.compile("\\*+");
		static private final String replaceWildcard = "\\\\E[^-]*\\\\Q";

		private Pattern machingPattern;

		public ExBasePatternWithRegexp(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
		}

		private void initialize(String[] keys) {
			// Check hat keys
			if (keys != null && keys.length >= ExBase.NUM_BASIC_KEYS) {
				// ハットキーが省略されている場合は、チェック対象外(ワイルドカードにする)
				if (keys[KEY_HAT] != null && keys[KEY_HAT].length() > 0 && !isValidHatKey(keys[KEY_HAT])) {
					// ハットキーに有効な文字列が定義されているが、
					// HAT でも NO_HAT でも ワイルドカードでもない
					throw new IllegalArgumentException("hat key is illegal");
				}
			}
			
			// 基底キー文字列が有効文字列かを検証する
			int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
			for (int i = 0; i < maxKeys; i++) {
				if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					if (!ExBasePattern.Util.getAllowedBaseKeyPattern().matcher(keys[i]).matches()) {
						String msg = "Illegal character is included in " + getKeyName(i) + ".";
						throw new IllegalArgumentException(msg);
					}
				}
			}
			
			// create keys, default key is wildcard('*')
			initializeBaseKeys(keys, ExBasePattern.WILDCARD);
			
			// create maching pattern
			updateMatchingPattern();
		}

		private void updateMatchingPattern() {
			// ワイルドカードが含まれているか
			if (key().indexOf(ExBasePattern.WILDCARD) < 0) {
				// ワイルドカードなし
				machingPattern = null;
			}
			else {
				// ワイルドカードあり - マッチングパターン生成
				String strRegexpKey;
				StringBuffer sb = new StringBuffer();
				//@@@ bug fix by Y.Ishizuka : 2008/06/01
				//--- 名前キー
				strRegexpKey = replaceWildcardPattern(this._baseKeys[KEY_NAME]);
				sb.append(strRegexpKey);
				//--- ハットキー＆拡張基底キー
				for (int i = 1; i < this._baseKeys.length; i++) {
					strRegexpKey = replaceWildcardPattern(this._baseKeys[i]);
					sb.append(DELIMITOR);
					sb.append(strRegexpKey);
				}
				//--- パターン生成
				strRegexpKey = "\\A\\Q" + sb.toString() + "\\E\\z";
				/*@@@ old codes.
				//--- 名前キー
				strRegexpKey = replacePattern.matcher(this._baseKeys[KEY_NAME]).replaceAll(replaceWildcard);
				sb.append(strRegexpKey);
				//--- ハットキー＆拡張基底キー
				for (int i = 1; i < this._baseKeys.length; i++) {
					strRegexpKey = replacePattern.matcher(this._baseKeys[i]).replaceAll(replaceWildcard);
					sb.append(DELIMITOR);
					sb.append(strRegexpKey);
				}
				//--- パターン生成
				strRegexpKey = "\\A" + sb.toString() + "\\z";
				**@@@ end of old codes. */
				//@@@ end of bug fix.
				machingPattern = Pattern.compile(strRegexpKey);
			}
		}

		private String replaceWildcardPattern(String key) {
			String strRegexpKey;
			//--- \E を正規表現のエスケープされた文字パターンに変換する
			strRegexpKey = replaceTermPattern.matcher(key).replaceAll(replaceEscapeTerm);
			//--- ワイルドカードを正規表現パターンに変換する
			strRegexpKey = replacePattern.matcher(strRegexpKey).replaceAll(replaceWildcard);
			//--- 完了
			return strRegexpKey;
		}

		public boolean matches(ExBase exbase) {
			if (exbase == null)
				throw new NullPointerException();
			
			if (this.machingPattern != null) {
				// exbase.key() と、正規表現パターンが一致するかを判定する
				return this.machingPattern.matcher(exbase.key()).matches();
			}
			else {
				// 完全一致かを判定する
				return exbase.equals(this);
			}
		}
		
		protected Pattern getMatchingPattern() {
			return this.machingPattern;
		}
		
		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBasePattern.WILDCARD.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}
	}

	/**
	 * 基底パターンのパターン種別を基底ごとに保持するパターン。
	 * この種別は、ワイルドカードのみ、ワイルドカードを含まない文字列、ワイルドカードを含む文字列に
	 * 分けられる。
	 */
	static class ExBasePatternWithType extends ExBasePatternBase {
		private final PatternItem[] _patterns = new PatternItem[NUM_ALL_KEYS];

		public ExBasePatternWithType(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
		}
		
		private void initialize(String[] keys) {
			// Check hat keys
			if (keys != null && keys.length >= ExBase.NUM_BASIC_KEYS) {
				// ハットキーが省略されている場合は、チェック対象外(ワイルドカードにする)
				if (keys[KEY_HAT] != null && keys[KEY_HAT].length() > 0 && !isValidHatKey(keys[KEY_HAT])) {
					// ハットキーに有効な文字列が定義されているが、
					// HAT でも NO_HAT でも ワイルドカードでもない
					throw new IllegalArgumentException("hat key is illegal");
				}
			}
			
			// 基底キー文字列が有効文字列かを検証する
			int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
			for (int i = 0; i < maxKeys; i++) {
				if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					if (!ExBasePattern.Util.getAllowedBaseKeyPattern().matcher(keys[i]).matches()) {
						String msg = "Illegal character is included in " + getKeyName(i) + ".";
						throw new IllegalArgumentException(msg);
					}
				}
			}
			
			// create keys, default key is wildcard('*')
			initializeBaseKeys(keys, ExBasePattern.WILDCARD);
			
			// create maching pattern
			updatePatterns();
		}
		
		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBasePattern.WILDCARD.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}

		public boolean matches(ExBase exbase) {
			if (exbase == null)
				throw new NullPointerException();
			
			for (PatternItem item : _patterns) {
				if (item != null) {
					Object pattern = item.getPattern();
					if (pattern instanceof String) {
						// 文字列とのマッチング
						if (!exbase._baseKeys[item.getKeyIndex()].equals(pattern)) {
							// no match!
							return false;
						}
					}
					else if (pattern instanceof Pattern) {
						// 正規表現マッチング
						Pattern regexp = (Pattern)pattern;
						if (!regexp.matcher(exbase._baseKeys[item.getKeyIndex()]).matches()) {
							// no match !
							return false;
						}
					}
				}
			}
			
			// matched!
			return true;
		}

		private String convertWildcardToRegexpPattern(String strpattern) {
			int len = strpattern.length();
			StringBuffer sb = new StringBuffer(len * 3);
			sb.append("\\A\\Q");	// 入力の先頭を示す
			
			int index = 0;
			while (index < len) {
				char c = strpattern.charAt(index);
				if ('*' == c) {
					// wildcard
					sb.append("\\E.*\\Q");
					//--- 連続したワイルドカードはスキップ
					for (++index; (index < len) && (strpattern.charAt(index) == '*'); index++) ;
				}
				else if ('\\' == c) {
					// \E 文字セット?
					index++;
					if ((index < len) && (strpattern.charAt(index) == 'E')) {
						// 正規表現[\E]パターン
						sb.append("\\E\\\\E\\Q");
						index++;
					} else {
						sb.append(c);
					}
				}
				else {
					sb.append(c);
					index++;
				}
			}
			
			sb.append("\\E\\z");
			return sb.toString();
		}

		private void updatePatterns() {
			// パターンを生成
			for (int i = 0; i < _baseKeys.length; i++) {
				Object patternObj;
				if (ExBasePattern.WILDCARD.equals(_baseKeys[i])) {
					// 全ての文字にマッチする
					patternObj = null;
				}
				else if (_baseKeys[i].indexOf(ExBasePattern.WILDCARD) < 0) {
					// ワイルドカードが含まれていない
					patternObj = _baseKeys[i];
				}
				else {
					// パターンを生成
					String strPattern = convertWildcardToRegexpPattern(_baseKeys[i]);
					patternObj = Pattern.compile(strPattern);
				}
				_patterns[i] = new PatternItem(i, patternObj);
			}
			
			// マッチング順序の並べ替え
			Arrays.sort(_patterns);
		}

		static private class PatternItem implements Comparable<PatternItem> {
			static public final int TYPE_STRING = (-1);
			static public final int TYPE_REGEXP = 0;
			static public final int TYPE_WILDCARD = 1;
			
			private final int keyIndex;
			private final Object pattern;
			
			public PatternItem(int keyIndex, Object pattern) {
				this.keyIndex = keyIndex;
				this.pattern = pattern;
			}
			
			public int getKeyIndex() {
				return keyIndex;
			}
			
			public Object getPattern() {
				return pattern;
			}
			
			public int getPatternType() {
				if (pattern instanceof String) {
					return TYPE_STRING;
				}
				else if (pattern instanceof Pattern) {
					return TYPE_REGEXP;
				}
				else {
					return TYPE_WILDCARD;
				}
			}

			public int compareTo(PatternItem o) {
				int thisType = getPatternType();
				int otherType = o.getPatternType();
				
				if (thisType < otherType) {
					return -1;
				}
				else if (thisType > otherType) {
					return 1;
				}
				else {
					if (this.keyIndex < o.keyIndex)
						return -1;
					else if (this.keyIndex > o.keyIndex)
						return 1;
					else
						return 0;
				}
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof PatternItem) {
					if (((PatternItem)obj).keyIndex == this.keyIndex) {
						return true;
					}
				}
				return false;
			}

			@Override
			public int hashCode() {
				return keyIndex;
			}
			
		}
	}

	/**
	 * シンプルな比較パターンの実装。この実装では、JAVA正規表現を使用しない。
	 */
	static class ExBasePatternWithStringCompare extends ExBasePatternBase {
		private WildcardPattern[] patterns;
		

		public ExBasePatternWithStringCompare(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
		}

		private void initialize(String[] keys) {
			// Check hat keys
			if (keys != null && keys.length >= ExBase.NUM_BASIC_KEYS) {
				// ハットキーが省略されている場合は、チェック対象外(ワイルドカードにする)
				if (keys[KEY_HAT] != null && keys[KEY_HAT].length() > 0 && !isValidHatKey(keys[KEY_HAT])) {
					// ハットキーに有効な文字列が定義されているが、
					// HAT でも NO_HAT でも ワイルドカードでもない
					throw new IllegalArgumentException("hat key is illegal");
				}
			}
			
			// 基底キー文字列が有効文字列かを検証する
			int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
			for (int i = 0; i < maxKeys; i++) {
				if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					if (!ExBasePattern.Util.getAllowedBaseKeyPattern().matcher(keys[i]).matches()) {
						String msg = "Illegal character is included in " + getKeyName(i) + ".";
						throw new IllegalArgumentException(msg);
					}
				}
			}
			
			// create keys, default key is wildcard('*')
			initializeBaseKeys(keys, ExBasePattern.WILDCARD);
			
			// create maching pattern
			updateMatchingPattern();
		}

		private void updateMatchingPattern() {
			WildcardPattern[] tempPatterns = new WildcardPattern[NUM_ALL_KEYS];
			
			boolean existWildcard = false;
			for (int i = 0; i < NUM_ALL_KEYS; i++) {
				String key = _baseKeys[i];
				if (ExBasePattern.WILDCARD.equals(key)) {
					tempPatterns[i] = new WildcardOnly();
					existWildcard = true;
				}
				else {
					int firstWildcardPos = key.indexOf(ExBasePattern.WILDCARD);
					if (firstWildcardPos >= 0) {
						WildcardMixedPattern pat = new WildcardMixedPattern(firstWildcardPos, key);
						if (pat.isWildcardOnly())
							tempPatterns[i] = new WildcardOnly();
						else
							tempPatterns[i] = pat;
						existWildcard = true;
					}
				}
			}
			
			if (existWildcard) {
				patterns = tempPatterns;
			}
			else {
				patterns = null;
			}
		}

		public boolean matches(ExBase exbase) {
			if (exbase == null)
				throw new NullPointerException();
			
			if (patterns != null) {
				// パターンによる一致
				for (int i = 0; i < NUM_ALL_KEYS; i++) {
					WildcardPattern pat = patterns[i];
					if (pat != null) {
						if (!pat.matches(exbase._baseKeys[i])) {
							//--- no matched!
							return false;
						}
					} else {
						if (!exbase._baseKeys[i].equals(this._baseKeys[i])) {
							//--- no matched!
							return false;
						}
					}
				}
				
				//--- matched
				return true;
			}
			else {
				// 固定文字列による同値性
				return exbase.equals(this);
			}
		}
		
		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBasePattern.WILDCARD.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}
		
		static abstract class WildcardPattern {
			abstract public boolean matches(String str);
		}
		
		static class WildcardOnly extends WildcardPattern {
			public boolean matches(String str) {
				return true;
			}
		}
		
		static class WildcardMixedPattern extends WildcardPattern {
			private final String startsString;
			private final String endsString;
			private final String[] midStrings;
			public WildcardMixedPattern(int firstWildcardPos, String strPattern) {
				int lastWildcardPos  = strPattern.lastIndexOf(ExBasePattern.WILDCARD);
				//--- 先頭は固定文字列？
				if (firstWildcardPos > 0) {
					startsString = strPattern.substring(0, firstWildcardPos);
				} else {
					startsString = null;
				}
				//--- 終端は固定文字列？
				if (lastWildcardPos < (strPattern.length()-1)) {
					endsString = strPattern.substring(lastWildcardPos+1);
				} else {
					endsString = null;
				}
				//--- 途中の固定文字列抽出
				if (firstWildcardPos < lastWildcardPos) {
					String mid = strPattern.substring(firstWildcardPos + 1, lastWildcardPos);
					StringTokenizer st = new StringTokenizer(mid, ExBasePattern.WILDCARD);
					int num = st.countTokens();
					if (num > 0) {
						String[] tokens = new String[num];
						int i = 0;
						while (st.hasMoreTokens()) {
							tokens[i++] = st.nextToken();
						}
						midStrings = tokens;
					} else {
						midStrings = null;
					}
				} else {
					midStrings = null;
				}
			}
			
			public boolean isWildcardOnly() {
				return (startsString == null && endsString == null && midStrings == null);
			}
			
			public boolean matches(String str) {
				//--- matches starts
				if (startsString != null) {
					if (!str.startsWith(startsString)) {
						// no matched!
						return false;
					}
					str = str.substring(startsString.length());
				}
				//--- matches mid strings
				if (midStrings != null) {
					for (String mid : midStrings) {
						int findPos = str.indexOf(mid);
						if (findPos < 0) {
							// no matched!
							return false;
						}
						str = str.substring(findPos + mid.length());
					}
				}
				//--- matches ends
				if (endsString != null) {
					if (!str.endsWith(endsString)) {
						// no matched!
						return false;
					}
				}
				
				// pattern matched
				return true;
			}
		}
	}
}
