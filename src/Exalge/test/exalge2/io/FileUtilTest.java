/**
 * 
 */
package exalge2.io;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author ishizuka
 *
 */
public class FileUtilTest extends TestCase {
	
	static protected final File testdir = new File("testdata/txt");
	
	static protected final String[] testdata = new String[]{
		"文字列の\r入出力テスト。",
		"",
		"文字列をテキストファイルに",
		"出力する",
		"メソッドを追加したので、\rその動作を",
		"\"\"テスト,テスト,テスト\"\"する。",
		"",
	};
	
	static protected String testString1 = null;
	static protected String testString2 = null;
	static protected ArrayList<String> testList1 = null;
	static protected ArrayList<String> resultList1 = null;
	
	static protected String getTestString1() {
		if (testString1 == null) {
			StringBuilder sb = new StringBuilder();
			for (String s : testdata) {
				sb.append(s);
			}
			testString1 = sb.toString();
		}
		return testString1;
	}
	
	static protected String getTestString2() {
		if (testString2 == null) {
			int i = 0;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.print(testdata[i++]);
			for (; i < testdata.length; i++) {
				pw.println();
				pw.print(testdata[i]);
			}
			pw.flush();
			testString2 = sw.toString();
		}
		return testString2;
	}
	
	static protected ArrayList<String> getTestList1() {
		if (testList1 == null) {
			testList1 = new ArrayList<String>(Arrays.asList(testdata));
		}
		return testList1;
	}
	
	static protected ArrayList<String> getResultList1() {
		if (resultList1 == null) {
			ArrayList<String> ary = new ArrayList<String>();
			for (String s : testdata) {
				String[] ss = s.split("\\r\\n|\\r|\\n|\\u0085|\\u2028|\\u2029");
				if (ss.length > 0) {
					ary.addAll(Arrays.asList(ss));
				} else {
					ary.add(s);
				}
			}
			while (!ary.isEmpty()) {
				int idx = ary.size() - 1;
				if (ary.get(idx).length() > 0) {
					break;
				}
				ary.remove(idx);
			}
			resultList1 = ary;
		}
		return resultList1;
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		// テスト・ディレクトリの作成
		if (!testdir.exists() || !testdir.isDirectory()) {
			testdir.mkdir();
		}
	}

	/**
	 * {@link exalge2.io.FileUtil#stringFromTextFile(java.io.File)} のためのテスト・メソッド。
	 * {@link exalge2.io.FileUtil#stringToTextFile(java.lang.String, java.io.File)} のためのテスト・メソッド。
	 */
	public void testStringFromToTextFileFile() {
		// test-1
		String str1 = getTestString1();
		File file1 = new File(testdir, "testString1.txt");
		//--- 出力
		try {
			FileUtil.stringToTextFile(str1, file1);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringToTextFile(str1, file1) -- " + ex.getMessage());
		}
		//--- 入力
		String ret1 = null;
		try {
			ret1 = FileUtil.stringFromTextFile(file1);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringFromTextFile(file1) -- " + ex.getMessage());
		}
		//--- 比較
		if (ret1 != null) {
			assertEquals(ret1, str1);
		}
		
		// test-2
		String str2 = getTestString2();
		File file2 = new File(testdir, "testString2.txt");
		//--- 出力
		try {
			FileUtil.stringToTextFile(str2, file2);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringToTextFile(str2, file2) -- " + ex.getMessage());
		}
		//--- 入力
		String ret2 = null;
		try {
			ret2 = FileUtil.stringFromTextFile(file2);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringFromTextFile(file2) -- " + ex.getMessage());
		}
		//--- 比較
		if (ret2 != null) {
			assertEquals(ret2, str2);
		}
	}

	/**
	 * {@link exalge2.io.FileUtil#stringFromTextFile(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 * {@link exalge2.io.FileUtil#stringToTextFile(java.lang.String, java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testStringFromToTextFileFileString() {
		final String encoding = "UTF-8";
		final String suffix   = "UTF8";
		
		// test-1
		String str1 = getTestString1();
		File file1 = new File(testdir, "testString1_" + suffix + ".txt");
		//--- 出力
		try {
			FileUtil.stringToTextFile(str1, file1, encoding);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringToTextFile(str1, file1, \"" + encoding + "\") -- " + ex.getMessage());
		}
		//--- 入力
		String ret1 = null;
		try {
			ret1 = FileUtil.stringFromTextFile(file1, encoding);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringFromTextFile(file1, \"" + encoding + "\") -- " + ex.getMessage());
		}
		//--- 比較
		if (ret1 != null) {
			assertEquals(ret1, str1);
		}
		
		// test-2
		String str2 = getTestString2();
		File file2 = new File(testdir, "testString2_" + suffix + ".txt");
		//--- 出力
		try {
			FileUtil.stringToTextFile(str2, file2, encoding);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringToTextFile(str2, file2, \"" + encoding + "\") -- " + ex.getMessage());
		}
		//--- 入力
		String ret2 = null;
		try {
			ret2 = FileUtil.stringFromTextFile(file2, encoding);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringFromTextFile(file2, \"" + encoding + "\") -- " + ex.getMessage());
		}
		//--- 比較
		if (ret2 != null) {
			assertEquals(ret2, str2);
		}
	}

	/**
	 * {@link exalge2.io.FileUtil#stringListFromTextFile(java.io.File)} のためのテスト・メソッド。
	 * {@link exalge2.io.FileUtil#stringListToTextFile(java.util.Collection, java.io.File)} のためのテスト・メソッド。
	 */
	public void testStringListFromToTextFileFile() {
		// test-1
		ArrayList<String> ary1 = getTestList1();
		File file1 = new File(testdir, "testStringList1.txt");
		//--- 出力
		try {
			FileUtil.stringListToTextFile(ary1, file1);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringListToTextFile(ary1, file1) -- " + ex.getMessage());
		}
		//--- 入力(String)
		String strret1 = null;
		try {
			strret1 = FileUtil.stringFromTextFile(file1);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringFromTextFile(file1) -- " + ex.getMessage());
		}
		//--- 比較(String)
		if (strret1 != null) {
			assertEquals(strret1, getTestString2());
		}
		//--- 入力(List)
		ArrayList<String> ret1 = null;
		try {
			ret1 = FileUtil.stringListFromTextFile(file1);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringListFromTextFile(file1) -- " + ex.getMessage());
		}
		//--- 比較(List)
		if (ret1 != null) {
			assertEquals(ret1, getResultList1());
		}
	}

	/**
	 * {@link exalge2.io.FileUtil#stringListFromTextFile(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 * {@link exalge2.io.FileUtil#stringListToTextFile(java.util.Collection, java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testStringListFromToTextFileFileString() {
		final String encoding = "UTF-8";
		final String suffix   = "UTF8";
		
		// test-1
		ArrayList<String> ary1 = getTestList1();
		File file1 = new File(testdir, "testStringList1_" + suffix + ".txt");
		//--- 出力
		try {
			FileUtil.stringListToTextFile(ary1, file1, encoding);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringListToTextFile(ary1, file1, \"" + encoding + "\") -- " + ex.getMessage());
		}
		//--- 入力(String)
		String strret1 = null;
		try {
			strret1 = FileUtil.stringFromTextFile(file1, encoding);
			assertTrue(true);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringFromTextFile(file1, \"" + encoding + "\") -- " + ex.getMessage());
		}
		//--- 比較(String)
		if (strret1 != null) {
			assertEquals(strret1, getTestString2());
		}
		//--- 入力(List)
		ArrayList<String> ret1 = null;
		try {
			ret1 = FileUtil.stringListFromTextFile(file1, encoding);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("FileUtil.stringListFromTextFile(file1, \"" + encoding + "\") -- " + ex.getMessage());
		}
		//--- 比較(List)
		if (ret1 != null) {
			assertEquals(ret1, getResultList1());
		}
	}

}
