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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import exalge2.util.ExQuarterTimeKey;

/**
 * <code>ExBase</code> の速度チェック
 */
public class SpeedTestExBase extends TestCase {
	
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
	static private final int MAX_NAMES = 100;
	static private final int MAX_SUBJECTS = 100;
	static private final int MAX_TIMES = 100;

	private Random[] randForName;
	private Random   randForHat;
	private String[] baseNames;
	private String[] baseTimes;
	private String[] baseSubjects;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
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
	}

	/**
	 * <code>ExBase</code> の新しいインスタンスを、基底キーのチェックなしに生成する。
	 * このときの実行速度を計測する。
	 */
	public void testNewExBaseWithoutCheck() {
		System.out.println();
		System.out.println("<< Performance check for newing ExBase without check base keys >>");
		long count = 0L;
		long tcStart = System.currentTimeMillis();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					new ExBaseWithoutCheck(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
				}
			}
		}
		long tcEnd = System.currentTimeMillis();
		System.out.println("    - created " + count + " bases.");
		System.out.println("    - creating time span : " + (tcEnd - tcStart) + " ms");
	}

	/**
	 * <code>ExBase</code> の新しいインスタンスを、Java正規表現クラスを利用した
	 * 基底キーチェックとともに生成する。このときの実行速度を計測する。
	 *
	 */
	public void testNewExBaseWithRegexpCheck() {
		System.out.println();
		System.out.println("<< Performance check for newing ExBase with check base keys by Regexp >>");
		long count = 0L;
		long tcStart = System.currentTimeMillis();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					new ExBaseWithRegexpCheck(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
				}
			}
		}
		long tcEnd = System.currentTimeMillis();
		System.out.println("    - created " + count + " bases.");
		System.out.println("    - creating time span : " + (tcEnd - tcStart) + " ms");
	}

	/**
	 * <code>ExBase</code> の新しいインスタンスを、単純な文字比較による基底キー
	 * チェックとともに生成する。このときの実行速度を計測する。
	 *
	 */
	public void testNewExBaseWithStringCheck() {
		System.out.println();
		System.out.println("<< Performance check for newing ExBase with check base keys by char compare >>");
		long count = 0L;
		long tcStart = System.currentTimeMillis();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					new ExBaseWithStringCheck(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
				}
			}
		}
		long tcEnd = System.currentTimeMillis();
		System.out.println("    - created " + count + " bases.");
		System.out.println("    - creating time span : " + (tcEnd - tcStart) + " ms");
	}

	/**
	 * <code>ExBase</code> の新しいインスタンスを、単純な文字比較による基底キー
	 * チェックとともに生成する。このときの実行速度を計測する。
	 *
	 */
	public void testNewExBaseWithBinarySearchCheck() {
		System.out.println();
		System.out.println("<< Performance check for newing ExBase with check base keys by Binary search >>");
		long count = 0L;
		long tcStart = System.currentTimeMillis();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					new ExBaseWithBinarySearchCheck(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
				}
			}
		}
		long tcEnd = System.currentTimeMillis();
		System.out.println("    - created " + count + " bases.");
		System.out.println("    - creating time span : " + (tcEnd - tcStart) + " ms");
	}

	/**
	 * <code>ExBase</code> の新しいインスタンスを、単純な文字比較による基底キー
	 * チェックとともに生成する。このときの実行速度を計測する。
	 *
	 */
	public void testNewExBaseWithMapCheck() {
		System.out.println();
		System.out.println("<< Performance check for newing ExBase with check base keys by HashSet >>");
		long count = 0L;
		long tcStart = System.currentTimeMillis();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					new ExBaseWithMapCheck(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
				}
			}
		}
		long tcEnd = System.currentTimeMillis();
		System.out.println("    - created " + count + " bases.");
		System.out.println("    - creating time span : " + (tcEnd - tcStart) + " ms");
	}

	/**
	 * 内包記法を想定した、<code>Exalge</code> インスタンスの生成パフォーマンスを計測する。
	 * 基本的に、<code>ExBase</code> の生成、<code>Exalge</code> の生成、<code>ExAlgeSet</code> への
	 * 格納を行う。
	 * <code>ExBase</code> の生成に関しては、生成コストを除外したパフォーマンスチェックとする。
	 *
	 */
	public void testNewAlgeOnIntensions() {
		long tcSpanNewBases = 0L;
		
		System.out.println();
		System.out.println("<< Performance check for newing ExBase ... current implementation with Regexp check >>");
		long count = 0L;
		long tcStart = System.currentTimeMillis();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					new ExBase(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
				}
			}
		}
		long tcEnd = System.currentTimeMillis();
		tcSpanNewBases = tcEnd - tcStart;
		System.out.println("    - created " + count + " bases.");
		System.out.println("    - creating time span : " + tcSpanNewBases + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for creating Exalge by default initial capacity >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		ExAlgeSet set1 = new ExAlgeSet();
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					ExBase base = new ExBase(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
					BigDecimal value = new BigDecimal(count);
					Exalge alge = new Exalge(base, value);
					set1.add(alge);
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		long tcSpan = tcEnd - tcStart;
		System.out.println("    - created " + set1.size() + " alges.");
		System.out.println("    - creating time span : " + tcSpan + " ms");
		System.out.println("    - creating time span without creating ExBase : " + (tcSpan - tcSpanNewBases) + " ms");
		set1 = null;
		
		System.out.println();
		System.out.println("<< Performance check for creating Exalge by specified initial capacity >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		int initCap = baseNames.length * baseTimes.length * baseSubjects.length;
		set1 = new ExAlgeSet(initCap);
		for (String strName : baseNames) {
			for (String strTime : baseTimes) {
				for (String strSubject : baseSubjects) {
					ExBase base = new ExBase(strName, ExBase.NO_HAT, ExBase.OMITTED, strTime, strSubject);
					count++;
					BigDecimal value = new BigDecimal(count);
					Exalge alge = new Exalge(base, value);
					set1.add(alge);
				}
			}
		}
		tcEnd = System.currentTimeMillis();
		tcSpan = tcEnd - tcStart;
		System.out.println("    - created " + set1.size() + " alges.");
		System.out.println("    - creating time span : " + tcSpan + " ms");
		System.out.println("    - creating time span without creating ExBase : " + (tcSpan - tcSpanNewBases) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for ExAlgeSet#getBases() function >>");
		tcStart = System.currentTimeMillis();
		ExBaseSet bases = set1.getBases();
		tcEnd = System.currentTimeMillis();
		System.out.println("    - ExAlgeSet in " + set1.size() + " alges.");
		System.out.println("    - ExBaseSet in " + bases.size() + " bases.");
		System.out.println("    - ExAlgeSet#getBases time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for ExAlgeSet#sum() function >>");
		tcStart = System.currentTimeMillis();
		Exalge alge = set1.sum();
		tcEnd = System.currentTimeMillis();
		System.out.println("    - ExAlgeSet in " + set1.size() + " alges.");
		System.out.println("    - Exalge in " + alge.getNumElements() + " elements.");
		System.out.println("    - ExAlgeSet#sum time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for Exalge#getBases() function >>");
		tcStart = System.currentTimeMillis();
		bases = alge.getBases();
		tcEnd = System.currentTimeMillis();
		System.out.println("    - Exalge in " + alge.getNumElements() + " elements.");
		System.out.println("    - ExBaseSet in " + bases.size() + " bases.");
		System.out.println("    - Exalge#getBases time span : " + (tcEnd - tcStart) + " ms");
		
		System.out.println();
		System.out.println("<< Performance check for Exalge#projection() function >>");
		count = 0L;
		tcStart = System.currentTimeMillis();
		for (ExBase base : bases) {
			alge.projection(base);
			count++;
		}
		tcEnd = System.currentTimeMillis();
		System.out.println("    - Exalge in " + alge.getNumElements() + " elements.");
		System.out.println("    - ExBaseSet in " + bases.size() + " bases.");
		System.out.println("    - Call projection count : " + count);
		System.out.println("    - Exalge#projection total time span : " + (tcEnd - tcStart) + " ms");
	}
	
	static class ExBaseWithoutCheck extends AbExBase {
		public ExBaseWithoutCheck(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
			//initialize(new String[]{nameKey.intern(), hatKey.intern(), unitKey.intern(), timeKey.intern(), subjectKey.intern()});
		}
		
		private void initialize(String[] keys) {
			// Check basic keys
			if (keys.length < NUM_BASIC_KEYS) {
				if (keys.length < 1)
					throw new IllegalArgumentException("No name key");
				else
					throw new IllegalArgumentException("No hat key");
			}
			//--- name key
			if (keys[KEY_NAME] == null || keys[KEY_NAME].length() <= 0)
				throw new IllegalArgumentException("No name key");
			//--- hat key
			if (keys[KEY_HAT] == null || !isValidHatKey(keys[KEY_HAT]))
				throw new IllegalArgumentException("No hat key");
			
			// create keys, default key is omitted('#')
			initializeBaseKeys(keys, ExBase.OMITTED);
		}

		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}
	}
	
	static class ExBaseWithRegexpCheck extends AbExBase {
		public ExBaseWithRegexpCheck(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
		}
		
		private void initialize(String[] keys) {
			// Check basic keys
			if (keys.length < NUM_BASIC_KEYS) {
				if (keys.length < 1)
					throw new IllegalArgumentException("No name key");
				else
					throw new IllegalArgumentException("No hat key");
			}
			//--- name key
			if (keys[KEY_NAME] == null || keys[KEY_NAME].length() <= 0)
				throw new IllegalArgumentException("No name key");
			//--- hat key
			if (keys[KEY_HAT] == null || !isValidHatKey(keys[KEY_HAT]))
				throw new IllegalArgumentException("No hat key");
			
			// 基底キー文字列が有効文字列かを検証する
			int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
			for (int i = 0; i < maxKeys; i++) {
				if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					if (!ExBase.Util.getAllowedBaseKeyPattern().matcher(keys[i]).matches()) {
						String msg = "Illegal character is included in " + getKeyName(i) + ".";
						throw new IllegalArgumentException(msg);
					}
				}
			}
			
			// create keys, default key is omitted('#')
			initializeBaseKeys(keys, ExBase.OMITTED);
		}

		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}
	}
	
	static class ExBaseWithStringCheck extends AbExBase {
		static private char[] IllegalBaseChars = new char[]{' ','\t','\n','\u000B','\f','\r','<','>','-',',','^','%','&','?','|','@','\'','\"'};
		//static private String IllegalBaseString = " \t\n\u000B\f\r<>-,^%&?|@'\"";
		
		public ExBaseWithStringCheck(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
		}
		
		private void initialize(String[] keys) {
			// Check basic keys
			if (keys.length < NUM_BASIC_KEYS) {
				if (keys.length < 1)
					throw new IllegalArgumentException("No name key");
				else
					throw new IllegalArgumentException("No hat key");
			}
			//--- name key
			if (keys[KEY_NAME] == null || keys[KEY_NAME].length() <= 0)
				throw new IllegalArgumentException("No name key");
			//--- hat key
			if (keys[KEY_HAT] == null || !isValidHatKey(keys[KEY_HAT]))
				throw new IllegalArgumentException("No hat key");
			
			// 基底キー文字列が有効文字列かを検証する
			int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
			for (int i = 0; i < maxKeys; i++) {
				if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					String strkey = keys[i];
					int len = strkey.length();
					for (int idx = 0; idx < len; idx++) {
						char c = strkey.charAt(idx);
						/*
						if (IllegalBaseString.indexOf(c) >= 0) {
							//--- 使用禁止文字発見
							String msg = "Illegal character is included in " + getKeyName(i) + ".";
							throw new IllegalArgumentException(msg);
						}
						*/
						/**/
						for (char ic : IllegalBaseChars) {
							if (c == ic) {
								//--- 使用禁止文字発見
								String msg = "Illegal character is included in " + getKeyName(i) + ".";
								throw new IllegalArgumentException(msg);
							}
						}
						/**/
					}
				}
			}
			
			// create keys, default key is omitted('#')
			initializeBaseKeys(keys, ExBase.OMITTED);
		}

		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}
	}
	
	static class ExBaseWithBinarySearchCheck extends AbExBase {
		static private char[] IllegalBaseChars = new char[]{' ','\t','\n','\u000B','\f','\r','<','>','-',',','^','%','&','?','|','@','\'','\"'};
		static private final char minIllegalChar;
		static private final char maxIllegalChar;
		static {
			Arrays.sort(IllegalBaseChars);
			minIllegalChar = IllegalBaseChars[0];
			maxIllegalChar = IllegalBaseChars[IllegalBaseChars.length-1];
		}
		
		public ExBaseWithBinarySearchCheck(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
		}
		
		private void initialize(String[] keys) {
			// Check basic keys
			if (keys.length < NUM_BASIC_KEYS) {
				if (keys.length < 1)
					throw new IllegalArgumentException("No name key");
				else
					throw new IllegalArgumentException("No hat key");
			}
			//--- name key
			if (keys[KEY_NAME] == null || keys[KEY_NAME].length() <= 0)
				throw new IllegalArgumentException("No name key");
			//--- hat key
			if (keys[KEY_HAT] == null || !isValidHatKey(keys[KEY_HAT]))
				throw new IllegalArgumentException("No hat key");
			
			// 基底キー文字列が有効文字列かを検証する
			int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
			for (int i = 0; i < maxKeys; i++) {
				if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					String strkey = keys[i];
					int len = strkey.length();
					for (int idx = 0; idx < len; idx++) {
						char c = strkey.charAt(idx);
						if (minIllegalChar <= c && c <= maxIllegalChar) {
							if (Arrays.binarySearch(IllegalBaseChars, c) >= 0) {
								//--- 使用禁止文字発見
								String msg = "Illegal character is included in " + getKeyName(i) + ".";
								throw new IllegalArgumentException(msg);
							}
						}
					}
				}
			}
			
			// create keys, default key is omitted('#')
			initializeBaseKeys(keys, ExBase.OMITTED);
		}

		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}
	}
	
	static class ExBaseWithMapCheck extends AbExBase {
		static private Set<Character> IllegalBaseCharSet = new HashSet<Character>(Arrays.asList(' ','\t','\n','\u000B','\f','\r','<','>','-',',','^','%','&','?','|','@','\'','\"'));
		
		public ExBaseWithMapCheck(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
			super();
			initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
		}
		
		private void initialize(String[] keys) {
			// Check basic keys
			if (keys.length < NUM_BASIC_KEYS) {
				if (keys.length < 1)
					throw new IllegalArgumentException("No name key");
				else
					throw new IllegalArgumentException("No hat key");
			}
			//--- name key
			if (keys[KEY_NAME] == null || keys[KEY_NAME].length() <= 0)
				throw new IllegalArgumentException("No name key");
			//--- hat key
			if (keys[KEY_HAT] == null || !isValidHatKey(keys[KEY_HAT]))
				throw new IllegalArgumentException("No hat key");
			
			// 基底キー文字列が有効文字列かを検証する
			int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
			for (int i = 0; i < maxKeys; i++) {
				if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					String strkey = keys[i];
					int len = strkey.length();
					for (int idx = 0; idx < len; idx++) {
						char c = strkey.charAt(idx);
						if (IllegalBaseCharSet.contains(c)) {
							//--- 使用禁止文字発見
							String msg = "Illegal character is included in " + getKeyName(i) + ".";
							throw new IllegalArgumentException(msg);
						}
					}
				}
			}
			
			// create keys, default key is omitted('#')
			initializeBaseKeys(keys, ExBase.OMITTED);
		}

		private boolean isValidHatKey(String hatKey) {
			if (ExBase.HAT.equals(hatKey))
				return true;	// 正等
			else if (ExBase.NO_HAT.equals(hatKey))
				return true;	// 正等
			// 不正
			return false;
		}
	}
}
