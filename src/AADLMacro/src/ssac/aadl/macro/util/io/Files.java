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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Files.java	2.0.0	2014/03/18 : move 'ssac.util.io' to 'ssac.aadl.macro.util.io' 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Files.java	1.11	2010/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Files.java	1.00	2008/10/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package ssac.aadl.macro.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * ファイル操作に関する補助機能を提供するユーティリティクラス。
 * 
 * @version 2.0.0	2014/03/18
 * 
 * @since 1.00
 *
 */
public final class Files
{
	private Files() {}
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int defBufferSize = 2048;
	
	static public final char  CommonSeparatorChar = '/';
	static public final String CommonSeparator = "" + CommonSeparatorChar;
	static public final char  CommonPathSeparatorChar = ';';
	static public final String CommonPathSeparator = "" + CommonPathSeparatorChar;
	
	private static final char WINDOWS_FILE_SEPARATOR_CHAR = '\\';
	private static final char LINUX_FILE_SEPARATOR_CHAR = '/';
	
	//private static final String WINDOWS_FILE_SEPARATOR = "" + WINDOWS_FILE_SEPARATOR_CHAR;
	//private static final String LINUX_FILE_SEPARATOR = "" + LINUX_FILE_SEPARATOR_CHAR;
	
	private static final String CUR_DIR = ".";
	private static final String UP_DIR = "..";

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定のストリームをクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param iStream クローズする <code>InputStream</code> オブジェクト
	 */
	static public void closeStream(InputStream iStream) {
		if (iStream != null) {
			try {
				iStream.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定の <code>Reader</code> をクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param iReader クローズする <code>Reader</code> オブジェクト
	 */
	static public void closeStream(Reader iReader) {
		if (iReader != null) {
			try {
				iReader.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定のストリームをクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param oStream クローズする <code>OutputStream</code> オブジェクト
	 */
	static public void closeStream(OutputStream oStream) {
		if (oStream != null) {
			try {
				oStream.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定の <code>Writer</code> をクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param oWriter クローズする <code>Writer</code> オブジェクト
	 */
	static public void closeStream(Writer oWriter) {
		if (oWriter != null) {
			try {
				oWriter.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定されたファイルのパスの先頭に存在する区切り文字数をカウントする。
	 * カウント対象の区切り文字は、バックスラッシュおよびスラッシュのどちらも対象とする。
	 * 
	 * @param file	対象のファイル
	 * @return	パス先頭に含まれる区切り文字数
	 * 
	 * @since 1.11
	 */
	static int countOfStartsSeparator(File file) {
		return (file==null ? 0 : countOfStartsSeparator(file.getPath()));
	}

	/**
	 * 指定されたパス文字列の先頭に存在する区切り文字数をカウントする。
	 * カウント対象の区切り文字は、バックスラッシュおよびスラッシュのどちらも対象とする。
	 * 
	 * @param path	対象のパス文字列
	 * @return	パス先頭に含まれる区切り文字数
	 * 
	 * @since 1.11
	 */
	static int countOfStartsSeparator(String path) {
		if (path == null)
			return 0;
		
		int len = path.length();
		int cnt = 0;
		for (; cnt < len; cnt++) {
			char c = path.charAt(cnt);
			if (c!=WINDOWS_FILE_SEPARATOR_CHAR && c!=LINUX_FILE_SEPARATOR_CHAR) {
				break;
			}
		}
		
		return cnt;
	}

	/**
	 * 指定されたファイルのパスの終端に存在する区切り文字数をカウントする。
	 * カウント対象の区切り文字は、バックスラッシュおよびスラッシュのどちらも対象とする。
	 * 
	 * @param file	対象のファイル
	 * @return	パス終端に含まれる区切り文字数
	 * 
	 * @since 1.11
	 */
	static int countOfEndsSeparator(File file) {
		return (file==null ? 0 : countOfEndsSeparator(file.getPath()));
	}

	/**
	 * 指定されたパス文字列の終端に存在する区切り文字数をカウントする。
	 * カウント対象の区切り文字は、バックスラッシュおよびスラッシュのどちらも対象とする。
	 * 
	 * @param path	対象のパス文字列
	 * @return	パス終端に含まれる区切り文字数
	 * 
	 * @since 1.11
	 */
	static int countOfEndsSeparator(String path) {
		if (path == null)
			return 0;

		int cnt = 0;
		for (int i = path.length()-1; i >= 0; i--, cnt++) {
			char c = path.charAt(i);
			if (c!=WINDOWS_FILE_SEPARATOR_CHAR && c!=LINUX_FILE_SEPARATOR_CHAR) {
				break;
			}
		}
		
		return cnt;
	}

	/**
	 * 指定された抽象パスがディレクトリの場合、その下位に存在するすべてのファイルならびに
	 * ディレクトリの総数を返す。指定された抽象パスがディレクトリがファイルの場合は 1 を返す。
	 * 抽象パスがディレクトリでもファイルでもない場合は 0 を返す。
	 * 
	 * @param file	カウントする基準パス
	 * @return	ファイルならびにディレクトリの総数
	 * @since 1.11
	 */
	static public int countFiles(File file) {
		if (file.isFile()) {
			return 1;
		}
		else if (file.isDirectory()) {
			int numFiles = 1;	// 指定されたファイル分
			File[] flist = file.listFiles();
			if (flist != null && flist.length > 0) {
				for (File f : flist) {
					numFiles += countFiles(f);
				}
			}
			return numFiles;
		}
		else {
			return 0;
		}
	}

	/**
	 * <em>name</em> がファイル名として適切かを判定する。
	 * この判定には、ファイル名として使用できない文字として <em>illegalChars</em> が
	 * 利用される。
	 * また、.(ドットのみ) や、..(2 つの連続ドットのみ) のファイル名は許可しない。
	 * @param name			検証する文字列
	 * @param allowSideDot	文字列の先頭や終端に.(ドット) を許可する場合は <tt>true</tt> を指定する。
	 * @param illegalChars	使用禁止文字の配列。<tt>null</tt> も指定可能。
	 * @return	正当と判定された場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			<em>name</em> が <tt>null</tt> もしくは空文字列の場合も <tt>false</tt> を返す。
	 * @since 1.11
	 */
	static public boolean verifyFilename(String name, boolean allowSideDot, char[] illegalChars) {
		if (name == null)
			return false;
		int slen = name.length();
		if (slen <= 0)
			return false;
		
		// 使用禁止文字のチェック
		if (illegalChars != null && illegalChars.length > 0) {
			for (int i = 0; i < slen; i++) {
				char c = name.charAt(i);
				for (int j = 0; j < illegalChars.length; j++) {
					if (c == illegalChars[j]) {
						return false;
					}
				}
			}
		}
		
		// .(ドット) のチェック
		if (CUR_DIR.equals(name) || UP_DIR.equals(name))
			return false;
		
		// 前後.(ドット) のチェック
		if (!allowSideDot && ('.'==name.charAt(0) || '.'==name.charAt(slen-1)))
			return false;
		
		// 正当
		return true;
	}

	/**
	 * 指定されたファイルのパスを区切り文字で分割する。分割された文字列は、
	 * ルートに近い名前から配列に格納される。分割するパスが含まれていない場合は、
	 * 要素を持たない配列を返す。
	 * 
	 * @param file	分割するパスを含むファイル
	 * @return	パスを分割した文字列配列
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 1.11
	 */
	static public String[] splitPath(File file) {
		return splitPath(file.getPath());
	}
	
	/**
	 * 指定されたファイルのパスを区切り文字で分割する。分割された文字列は、
	 * ルートに近い名前から配列に格納される。分割するパスが含まれていない場合は、
	 * 要素を持たない配列を返す。
	 * 
	 * @param path	分割するパス文字列
	 * @return	パスを分割した文字列配列
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 1.11
	 */
	static public String[] splitPath(String path) {
		ArrayList<String> bufs = new ArrayList<String>();
		int len = path.length();
		int start = -1;
		
		for (int idx = 0; idx < len; idx++) {
			char c = path.charAt(idx);
			if (c==WINDOWS_FILE_SEPARATOR_CHAR || c==LINUX_FILE_SEPARATOR_CHAR) {
				if (start >= 0) {
					String str = path.substring(start, idx);
					bufs.add(str);
					start = -1;
				}
			}
			else if (start < 0) {
				start = idx;
			}
		}
		
		if (start >= 0) {
			String str = path.substring(start, len);
			bufs.add(str);
		}
		
		if (bufs.isEmpty())
			return new String[0];
		else
			return bufs.toArray(new String[bufs.size()]);
	}

	/**
	 * 指定されたファイルのパスに、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> が
	 * 含まれている場合や、指定された区切り文字と異なる名前区切り文字が含まれている場合は
	 * 正規化されていないものとみなす。
	 * <code>separator</code> がバックスラッシュもしくはスラッシュの場合、指定された文字を
	 * 区切り文字として使用する。<code>separator</code> がバックスラッシュ、スラッシュ以外の
	 * 場合はプラットフォーム依存の区切り文字が適用される。
	 * 
	 * @param path			判定するパス文字列
	 * @param separator		判定に使用する名前区切り文字
	 * @return	正規化済の場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>path</em> が <tt>null</tt> の場合
	 * @since 1.11
	 */
	static public boolean isNormalized(String path, char separator) {
		int len = path.length();
		if (len <= 0)
			return true;	// 空文字列ならカレントとみなす。
		
		// パス区切り文字を指定
		if (separator!=WINDOWS_FILE_SEPARATOR_CHAR && separator!=LINUX_FILE_SEPARATOR_CHAR)
			separator = File.separatorChar;

		int start = -1;
		for (int idx = 0; idx < len; idx++) {
			char c = path.charAt(idx);
			if (c==WINDOWS_FILE_SEPARATOR_CHAR || c==LINUX_FILE_SEPARATOR_CHAR) {
				if (c != separator)
					return false;
				if (start >= 0) {
					String name = path.substring(start, idx);
					if (CUR_DIR.equals(name))
						return false;
					if (UP_DIR.equals(name))
						return false;
					start = -1;
				}
			}
			else if (start < 0) {
				start = idx;
			}
		}
		
		if (start >= 0) {
			String name = path.substring(start, len);
			if (CUR_DIR.equals(name))
				return false;
			if (UP_DIR.equals(name))
				return false;
		}
		
		return true;
	}
	
	/**
	 * 指定された抽象パスに、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> が
	 * 含まれている場合や、指定された区切り文字と異なる名前区切り文字が含まれている場合は
	 * 正規化されていないものとみなす。
	 * <code>separator</code> がバックスラッシュもしくはスラッシュの場合、指定された文字を
	 * 区切り文字として使用する。<code>separator</code> がバックスラッシュ、スラッシュ以外の
	 * 場合はプラットフォーム依存の区切り文字が適用される。
	 * 
	 * @param path			判定する抽象パス
	 * @param separator		判定に使用する名前区切り文字
	 * @return	正規化済の場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>path</em> が <tt>null</tt> の場合
	 * @since 1.11
	 */
	static public boolean isNormalized(File path, char separator) {
		return isNormalized(path.getPath(), separator);
	}
	
	/**
	 * 指定されたファイルのパスに、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> が
	 * 含まれている場合は正規化されていないものとみなす。
	 * このメソッドでは名前区切り文字は判定対象としない。
	 * 
	 * @param path			判定するパス文字列
	 * @return	正規化済の場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>path</em> が <tt>null</tt> の場合
	 * @since 1.11
	 */
	static public boolean isNormalized(String path) {
		int len = path.length();
		if (len <= 0)
			return true;	// 空文字列ならカレントとみなす。

		int start = -1;
		for (int idx = 0; idx < len; idx++) {
			char c = path.charAt(idx);
			if (c==WINDOWS_FILE_SEPARATOR_CHAR || c==LINUX_FILE_SEPARATOR_CHAR) {
				if (start >= 0) {
					String name = path.substring(start, idx);
					if (CUR_DIR.equals(name))
						return false;
					if (UP_DIR.equals(name))
						return false;
					start = -1;
				}
			}
			else if (start < 0) {
				start = idx;
			}
		}
		
		if (start >= 0) {
			String name = path.substring(start, len);
			if (CUR_DIR.equals(name))
				return false;
			if (UP_DIR.equals(name))
				return false;
		}
		
		return true;
	}
	
	/**
	 * 指定された抽象パスに、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> が
	 * 含まれている場合は正規化されていないものとみなす。
	 * このメソッドでは名前区切り文字は判定対象としない。
	 * 
	 * @param path			判定する抽象パス
	 * @return	正規化済の場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>path</em> が <tt>null</tt> の場合
	 * @since 1.11
	 */
	static public boolean isNormalized(File path) {
		return isNormalized(path.getPath());
	}

	/**
	 * 指定されたファイルのパスから、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> を
	 * 取り除いたパスを返す。指定されたファイルのパスが相対パスの場合でも正規化可能。
	 * <code>separator</code> がバックスラッシュもしくはスラッシュの場合、指定された文字を
	 * 区切り文字として使用する。<code>separator</code> がバックスラッシュ、スラッシュ以外の
	 * 場合はプラットフォーム依存の区切り文字が適用される。
	 * 
	 * @param file	正規化するファイル
	 * @param separator	正規化したパスに適用する区切り文字
	 * @return	正規化された新しいファイル
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 1.11
	 */
	static public File normalizeFile(File file, char separator) {
		String path = normalizePath(file.getPath(), separator);
		return new File(path);
	}

	/**
	 * 指定されたファイルパスから、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> を
	 * 取り除いたパスを返す。指定されたファイルのパスが相対パスの場合でも正規化可能。
	 * <code>separator</code> がバックスラッシュもしくはスラッシュの場合、指定された文字を
	 * 区切り文字として使用する。<code>separator</code> がバックスラッシュ、スラッシュ以外の
	 * 場合はプラットフォーム依存の区切り文字が適用される。
	 * 
	 * @param path	正規化対象のパスを表す文字列
	 * @param separator	正規化したパスに適用する区切り文字
	 * @return	正規化された新しいパス文字列
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 1.11
	 */
	static public String normalizePath(String path, char separator) {
		// 空文字列ならそのまま返す
		int len = path.length();
		if (len < 1)
			return path;
		
		// パス区切り文字を指定
		if (separator!=WINDOWS_FILE_SEPARATOR_CHAR && separator!=LINUX_FILE_SEPARATOR_CHAR)
			separator = File.separatorChar;
		
		// 先頭区切り文字を生成
		String startsSeparator = null;
		int numStartsSeparator = countOfStartsSeparator(path);
		if (numStartsSeparator == 1)
			startsSeparator = String.valueOf(separator);
		else if (numStartsSeparator > 1) {
			StringBuilder sb = new StringBuilder(numStartsSeparator);
			for (int i = 0; i < numStartsSeparator; i++)
				sb.append(separator);
			startsSeparator = sb.toString();
		}
		//--- 区切り文字しか含まれていないパスか、チェック
		if (len == numStartsSeparator) {
			return startsSeparator;		// 新しい区切り文字のみを含むパス文字列を返す
		}
		
		// 終端区切り文字を生成
		String endsSeparator = null;
		int numEndsSeparator = countOfEndsSeparator(path);
		if (numEndsSeparator == 1)
			endsSeparator = String.valueOf(separator);
		else if (numEndsSeparator > 1) {
			StringBuilder sb = new StringBuilder(numEndsSeparator);
			for (int i = 0; i < numEndsSeparator; i++)
				sb.append(separator);
			endsSeparator = sb.toString();
		}
		
		// パス文字列を区切り文字で分解
		String[] names = splitPath(path);
		
		// パスを整形
		ArrayList<String> nameList = new ArrayList<String>(names.length);
		for (String name : names) {
			if (CUR_DIR.equals(name)) {
				continue;	// skip
			}
			else if (UP_DIR.equals(name)) {
				int tailIndex = nameList.size() - 1;
				if (!nameList.isEmpty() && !UP_DIR.equals(nameList.get(tailIndex))) {
					//--- 1階層UPするので、直前の名前は破棄
					nameList.remove(tailIndex);
				}
				else {
					//--- 1階層UPはそのまま保存
					nameList.add(name);
				}
			}
			else {
				nameList.add(name);
			}
		}
		if (nameList.isEmpty()) {
			//--- 有効な名前が存在しないため、無名の相対パスとみなす
			return "";
		}
		
		StringBuilder sb = new StringBuilder(len);
		//--- 先頭区切り文字
		if (startsSeparator != null)
			sb.append(startsSeparator);
		//--- 先頭の名前
		sb.append(nameList.get(0));
		//--- ２番目以降の名前
		for (int i = 1; i < nameList.size(); i++) {
			sb.append(separator);
			sb.append(nameList.get(i));
		}
		//--- 終端区切り文字
		if (endsSeparator != null)
			sb.append(endsSeparator);
		
		return sb.toString();
	}

	/**
	 * 指定されたファイルのパスから、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> を
	 * 取り除いたパスを返す。指定されたファイルのパスが相対パスの場合でも正規化可能。
	 * パス区切り文字は、プラットフォーム依存の区切り文字に変換される。
	 * 
	 * @param file	正規化するファイル
	 * @return	正規化された新しいファイル
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 1.11
	 */
	static public File normalizeFile(File file) {
		return normalizeFile(file, '\0');
	}

	/**
	 * 指定されたファイルパスから、<code>&quot;..&quot;</code> や <code>&quot;.&quot;</code> を
	 * 取り除いたパスを返す。指定されたファイルのパスが相対パスの場合でも正規化可能。
	 * パス区切り文字は、プラットフォーム依存の区切り文字に変換される。
	 * 
	 * @param path	正規化対象のパスを表す文字列
	 * @return	正規化された新しいパス文字列
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 1.11
	 */
	static public String normalizePath(String path) {
		return normalizePath(path, '\0');
	}

	//------------------------------------------------------------
	// Text File I/O interfaces
	//------------------------------------------------------------
	
	/**
	 * テキストファイルを単一の文字列として読み込む。
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとしてデフォルトの
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>String</code> インスタンスを返す。
	 * <br>
	 * ファイルに改行文字が含まれている場合、ファイルに記録されている改行文字の
	 * まま読み込む。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * 
	 * @return テキストファイルの内容を保持する <code>String</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	static public String stringFromTextFile(File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedReader br = null;
		String strret;
		
		try {
			br = new BufferedReader(new FileReader(txtFile));
			
			strret = readStringFromReader(br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
		}
		
		return strret;
	}
	
	/**
	 * 指定された文字セットで、テキストファイルを単一の文字列として読み込む。
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとして、指定の
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>String</code> インスタンスを返す。
	 * <br>
	 * ファイルに改行文字が含まれている場合、ファイルに記録されている改行文字の
	 * まま読み込む。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return テキストファイルの内容を保持する <code>String</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public String stringFromTextFile(File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		String strret;
		
		try {
			isr = new InputStreamReader(new FileInputStream(txtFile), charsetName);
			br = new BufferedReader(isr);
			
			strret = readStringFromReader(br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
			if (isr != null) {
				closeStream(isr);
				isr = null;
			}
		}
		
		return strret;
	}

	/**
	 * 指定の <code>Reader</code> オブジェクトから、全てのテキストを
	 * 読み込み、読み込んだテキストを格納する
	 * <code>String</code> インスタンスを返す。
	 * 
	 * @param reader 読み込む <code>Reader</code> オブジェクト
	 * @return 読み込んだテキストを保持する <code>String</code> インスタンスを返す。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	static protected String readStringFromReader(Reader reader)
		throws IOException
	{
		char[] cbuf = new char[defBufferSize];
		StringBuilder sb = new StringBuilder();
		
		int len = 0;
		while ((len = reader.read(cbuf)) >= 0) {
			if (len > 0) {
				sb.append(cbuf, 0, len);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * テキストファイルを文字列のリストとして読み込む。
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとしてデフォルトの
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * <br>テキストファイルを読み込む際、改行文字を要素の区切りとして使用する。
	 * このとき、要素の区切りとして使用した改行文字は破棄される。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * 
	 * @return テキストファイルの内容を保持する <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	static public ArrayList<String> stringListFromTextFile(File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedReader br = null;
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(txtFile));

			readStringListFromReader(ret, br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
		}
		
		return ret;
	}
	
	/**
	 * 指定された文字セットで、テキストファイルを文字列のリストとして読み込む。
	 * <p>
	 * <p>
	 * このメソッドは、指定されたファイルをテキストファイルとして、指定の
	 * 文字エンコーディングで全て読み込み、読み込んだテキストを格納する
	 * <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * <br>テキストファイルを読み込む際、改行文字を要素の区切りとして使用する。
	 * このとき、要素の区切りとして使用した改行文字は破棄される。
	 * 
	 * @param txtFile 読み込むテキストファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return テキストファイルの内容を保持する <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public ArrayList<String> stringListFromTextFile(File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		ArrayList<String> ret = new ArrayList<String>();
		
		try {
			isr = new InputStreamReader(new FileInputStream(txtFile), charsetName);
			br = new BufferedReader(isr);
			
			readStringListFromReader(ret, br);
		}
		finally {
			if (br != null) {
				closeStream(br);
				br = null;
			}
			if (isr != null) {
				closeStream(isr);
				isr = null;
			}
		}
		
		return ret;
	}

	/**
	 * 指定の <code>Reader</code> オブジェクトから、全てのテキストを
	 * 読み込み、読み込んだテキストを格納する
	 * <code>ArrayList&lt;String&gt;</code> インスタンスを返す。
	 * <br>テキストファイルを読み込む際、改行文字を要素の区切りとして使用する。
	 * このとき、要素の区切りとして使用した改行文字は破棄される。
	 * 
	 * @param dist 文字列のリストを格納する <code>ArrayList&lt;String&gt;</code> オブジェクト
	 * @param reader 読み込む <code>BufferedReader</code> オブジェクト
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	static protected void readStringListFromReader(ArrayList<String> dist, BufferedReader reader)
		throws IOException
	{
		String strline;
		while ((strline = reader.readLine()) != null) {
			dist.add(strline);
		}
	}
	
	/**
	 * 文字列を、デフォルトの文字エンコーディングでファイルに出力する。
	 * <br>
	 * 改行文字は、指定の文字列に含まれる文字のまま出力される。
	 * 
	 * @param source ファイルに出力する文字列
	 * @param txtFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	static public void stringToTextFile(String source, File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(txtFile));
			pw = new PrintWriter(bw);
			
			pw.print(source);
			pw.flush();
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
		}
	}
	
	/**
	 * 文字列を、指定の文字エンコーディングでファイルに出力する。
	 * <br>
	 * 改行文字は、指定の文字列に含まれる文字のまま出力される。
	 * 
	 * @param source ファイルに出力する文字列
	 * @param txtFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public void stringToTextFile(String source, File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			osw = new OutputStreamWriter(new FileOutputStream(txtFile), charsetName);
			bw = new BufferedWriter(osw);
			pw = new PrintWriter(bw);
			
			pw.print(source);
			pw.flush();
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
			if (osw != null) {
				closeStream(osw);
				osw = null;
			}
		}
	}
	
	/**
	 * 文字列のリストを、デフォルトの文字エンコーディングでファイルに出力する。
	 * ファイル出力の際、文字列リストの要素と要素の間に、自動的に改行文字を出力する。
	 * 要素の区切りとして出力される改行文字は、実行するプラットフォームに依存する。
	 * <br>
	 * なお、文字列の要素に改行文字が含まれている場合、そのままファイルに出力される。
	 * 
	 * @param source 文字列のコレクション
	 * @param txtFile	出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 */
	static public void stringListToTextFile(Collection<String> source, File txtFile)
		throws FileNotFoundException, IOException
	{
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(txtFile));
			pw = new PrintWriter(bw);
			
			writeStringListToWriter(source, pw);
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
		}
	}
	
	/**
	 * 文字列のリストを、指定の文字エンコーディングでファイルに出力する。
	 * ファイル出力の際、文字列リストの要素と要素の間に、自動的に改行文字を出力する。
	 * 要素の区切りとして出力される改行文字は、実行するプラットフォームに依存する。
	 * <br>
	 * なお、文字列の要素に改行文字が含まれている場合、そのままファイルに出力される。
	 * 
	 * @param source 文字列のコレクション
	 * @param txtFile	出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 */
	static public void stringListToTextFile(Collection<String> source, File txtFile, String charsetName)
		throws FileNotFoundException, IOException, UnsupportedEncodingException
	{
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			osw = new OutputStreamWriter(new FileOutputStream(txtFile), charsetName);
			bw = new BufferedWriter(osw);
			pw = new PrintWriter(bw);
			
			writeStringListToWriter(source, pw);
		}
		finally {
			if (pw != null) {
				closeStream(pw);
				pw = null;
			}
			if (bw != null) {
				closeStream(bw);
				bw = null;
			}
			if (osw != null) {
				closeStream(osw);
				osw = null;
			}
		}
	}

	/**
	 * 文字列のリストを、指定の <code>Writer</code> オブジェクトに出力する。
	 * 出力の際、文字列リストの要素と要素の間に、自動的に改行文字を出力する。
	 * 要素の区切りとして出力される改行文字は、実行するプラットフォームに依存する。
	 * <br>
	 * なお、文字列の要素に改行文字が含まれている場合、そのままファイルに出力される。
	 * 
	 * @param source	文字列のコレクション
	 * @param writer	書き込む <code>PrintWriter</code> オブジェクト
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	static protected void writeStringListToWriter(Collection<String> source, PrintWriter writer)
		throws IOException
	{
		if (!source.isEmpty()) {
			Iterator<String> it = source.iterator();
			if (it.hasNext()) {
				writer.print(it.next());
				
				while (it.hasNext()) {
					writer.println();
					writer.flush();
					writer.print(it.next());
				}
				
				writer.flush();
			}
		}
	}
}
