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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Files.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Files.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Files.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Files.java	1.00	2008/10/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package ssac.aadl.runtime.io;

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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ssac.aadl.runtime.util.ArrayHelper;
import ssac.aadl.runtime.util.Strings;

/**
 * ファイル操作に関する補助機能を提供するユーティリティクラス。
 * 
 * @version 1.16	2010/09/27
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
	
	private static final String WINDOWS_FILE_SEPARATOR = "" + WINDOWS_FILE_SEPARATOR_CHAR;
	private static final String LINUX_FILE_SEPARATOR = "" + LINUX_FILE_SEPARATOR_CHAR;
	
	private static final String CUR_DIR = ".";
	private static final String UP_DIR = "..";

	/**
	 * ファイル名として使用できない標準的な文字の配列。
	 * この配列に含まれる文字は、次の通り<br>&nbsp
	 * ;&nbsp;<b>~</b>
	 * ;&nbsp;<b>#</b>
	 * ;&nbsp;<b>%</b>
	 * ;&nbsp;<b>&amp;</b>
	 * ;&nbsp;<b>*</b>
	 * ;&nbsp;<b>{</b>
	 * ;&nbsp;<b>}</b>
	 * ;&nbsp;<b>\</b>
	 * ;&nbsp;<b>:</b>
	 * ;&nbsp;<b>;</b>
	 * ;&nbsp;<b>&lt;</b>
	 * ;&nbsp;<b>&gt;</b>
	 * ;&nbsp;<b>?</b>
	 * ;&nbsp;<b>/</b>
	 * ;&nbsp;<b>|</b>
	 * ;&nbsp;<b>&quot;</b>
	 */
	static public final char[] DefaultIllegalFilenameCharacters = {
		'~', '#', '&', '*', '{', '}', '/', '\\', ':', ';', '<', '>', '?', '|', '"',
	};
	
	//private static final String UP_DIR_PATH_WINDOWS = UP_DIR + WINDOWS_FILE_SEPARATOR;
	//private static final String UP_DIR_PATH_LINUX   = UP_DIR + LINUX_FILE_SEPARATOR;
	//private static final String UP_DIR_PATH_NATIVE  = UP_DIR + File.separator;
	//private static final String CUR_DIR_PATH_WINDOWS = CUR_DIR + WINDOWS_FILE_SEPARATOR;
	//private static final String CUR_DIR_PATH_LINUX   = CUR_DIR + LINUX_FILE_SEPARATOR;

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
	 * 指定の <code>FileChannel</code> をクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param filechannel	クローズする <code>FileChannel</code> オブジェクト
	 * 
	 * @since 1.16
	 */
	static public void closeStream(FileChannel filechannel) {
		if (filechannel != null) {
			try {
				filechannel.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定された文字セット名に対応する文字セットを返す。
	 * 文字セット名に対応する文字セットがエンコード可能な文字セットではない場合、
	 * このメソッドは例外をスローする。<br>
	 * <em>charsetName</em> に <tt>null</tt> が指定された場合、プラットフォーム標準の
	 * 文字セットを返す。
	 * @param charsetName	文字セット名
	 * @return	文字セット名に対応する <code>Charset</code> オブジェクト
	 * @throws UnsupportedEncodingException	指定された文字セット名がサポートされていない場合、
	 * 											もしくはエンコード可能ではない場合
	 * @since 1.16
	 */
	static public Charset getEncodingByName(String charsetName) throws UnsupportedEncodingException
	{
		Charset newEncoding = null;
		if (charsetName != null) {
			try {
				newEncoding = Charset.forName(charsetName);
			}
			catch (IllegalCharsetNameException ex) {
				throw new UnsupportedEncodingException("Illegal Character-set name : '" + charsetName + "'");
			}
			catch (UnsupportedCharsetException ex) {
				throw new UnsupportedEncodingException("The specified Character-set name is not supported : '" + charsetName + "'");
			}
			if (!newEncoding.canEncode()) {
				throw new UnsupportedEncodingException("Cannot encode by the specified Character-set : '" + charsetName + "'");
			}
		} else {
			newEncoding = getDefaultEncoding();
		}
		return newEncoding;
	}

	/**
	 * プラットフォーム標準のテキストエンコーディングとなる文字セットを返す。
	 * @return	<code>Charset</code> オブジェクト
	 * 
	 * @since 1.16
	 */
	static public Charset getDefaultEncoding() {
		return Charset.defaultCharset();
	}

	/**
	 * 指定された文字列を指定された文字セットでエンコードした結果となる
	 * バイト配列を返す。指定された文字列が <tt>null</tt> もしくは空文字列の
	 * 場合は、空の配列を返す。
	 * @param str	エンコードする文字列
	 * @param encoding	エンコードに使用する文字セット
	 * @return	エンコード結果のバイト配列
	 * @since 1.16
	 */
	static public byte[] encodeString(String str, Charset encoding) {
		if (str != null && str.length() > 0) {
			return encodeCharBuffer(CharBuffer.wrap(str), encoding);
		} else {
			return ArrayHelper.EMPTY_BYTE_ARRAY;
		}
	}
	
	/**
	 * 指定された文字を指定された文字セットでエンコードした結果となる
	 * バイト配列を返す。
	 * @param ch	エンコードする文字
	 * @param encoding	エンコードに使用する文字セット
	 * @return	エンコード結果のバイト配列
	 * @since 1.16
	 */
	static public byte[] encodeChar(char ch, Charset encoding) {
		char[] chary = new char[]{ch};
		return encodeCharBuffer(CharBuffer.wrap(chary), encoding);
	}
	
	/**
	 * 指定された文字バッファの内容を指定された文字セットでエンコードした結果となる
	 * バイト配列を返す。
	 * @param cbuf	エンコードする文字を保持するバッファ
	 * @param encoding	エンコードに使用する文字セット
	 * @return	エンコード結果のバイト配列
	 * @since 1.16
	 */
	static protected byte[] encodeCharBuffer(CharBuffer cbuf, Charset encoding) {
		ByteBuffer bbuf = encoding.encode(cbuf);
		int size = bbuf.limit() - bbuf.position();
		if (size > 0) {
			byte[] ary = new byte[size];
			bbuf.get(ary);
			return ary;
		} else {
			return ArrayHelper.EMPTY_BYTE_ARRAY;
		}
	}

	/**
	 * 指定されたファイルのパスの先頭に存在する区切り文字数をカウントする。
	 * カウント対象の区切り文字は、バックスラッシュおよびスラッシュのどちらも対象とする。
	 * 
	 * @param file	対象のファイル
	 * @return	パス先頭に含まれる区切り文字数
	 * 
	 * @since 1.10
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
	 * @since 1.10
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
	 * @since 1.10
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
	 * @since 1.10
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
	 * @since 1.14
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
	 * @since 1.14
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
	 * @since 1.10
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
	 * @since 1.10
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
	 * @since 1.14
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
	 * @since 1.14
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
	 * @since 1.14
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
	 * @since 1.14
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
	 * @since 1.10
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
	 * @since 1.10
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
	 * @since 1.10
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
	 * @since 1.10
	 */
	static public String normalizePath(String path) {
		return normalizePath(path, '\0');
	}

	/**
	 * 指定された基準ディレクトリと対象ファイルから、基準ディレクトリからの相対パスと
	 * なるファイルに変換する。指定する基準ディレクトリと対象ファイルは、共に絶対パスで
	 * 表されている必要がある。なお、基準ディレクトリと対象ファイルのルートが異なる場合、
	 * 絶対パスのまま返す。
	 * <code>separator</code> がバックスラッシュもしくはスラッシュの場合、指定された文字を
	 * 区切り文字として使用する。<code>separator</code> がバックスラッシュ、スラッシュ以外の
	 * 場合はプラットフォーム依存の区切り文字が適用される。
	 * 同一のパスであれば、空文字列を返す。
	 * 
	 * @param baseDir		相対パスの基準となるディレクトリ
	 * @param targetFile	相対パスに変換するファイル
	 * @param separator		新しいパスに適用する区切り文字
	 * @return	相対パスを表す文字列を返す。基準ディレクトリと同一パスの場合は空文字列を返す。
	 * 
	 * @throws NullPointerException	<code>baseDir</code> もしくは <code>targetFile</code> が <tt>null</tt> の場合
	 * 
	 * @since 1.10
	 */
	static public String convertAbsoluteToRelativePath(File baseDir, File targetFile, char separator) {
		// 正規化
		String nrmBaseDir = normalizePath(baseDir.getAbsolutePath(), separator);
		String nrmTarget  = normalizePath(targetFile.getAbsolutePath(), separator);
		String targetPath = nrmTarget;
		
		// パス区切り文字を指定
		if (separator!=WINDOWS_FILE_SEPARATOR_CHAR && separator!=LINUX_FILE_SEPARATOR_CHAR)
			separator = File.separatorChar;
		
		// OS判定
		String osname = System.getProperty("os.name");
		boolean isWindows = (osname.indexOf("Windows") >= 0);
		if (isWindows) {
			//--- 比較対象を小文字に変換
			nrmBaseDir = nrmBaseDir.toLowerCase();
			nrmTarget = nrmTarget.toLowerCase();
		}
		
		// 同一パスかの判定
		if (nrmTarget.equals(nrmBaseDir)) {
			return "";
		}
		
		// 絶対パスの先頭比較
		String baseTopName = getTopName(nrmBaseDir);
		String targetTopName = getTopName(nrmTarget);
		if (baseTopName == null || !baseTopName.equals(targetTopName)) {
			//--- ルートが異なるため、絶対パスを返す。
			return targetPath;
		}
		
		// 先頭の一致する箇所を取り除く
		int baseLen = nrmBaseDir.length();
		int targetLen = nrmTarget.length();
		int maxLen = Math.min(baseLen, targetLen);
		int lastSeparator = -1;
		int start = 0;
		for (; start < maxLen; start++) {
			char cBase = nrmBaseDir.charAt(start);
			char cTarget = nrmTarget.charAt(start);
			if (cBase != cTarget) {
				break;
			}
			else if (cBase==separator) {
				lastSeparator = start;
			}
		}
		if (baseLen == start) {
			if (targetLen > start) {
				if (nrmTarget.charAt(start) == separator) {
					start = start + 1;
				} else {
					start = lastSeparator + 1;
				}
			} else {
				start = lastSeparator + 1;
			}
		}
		else if (targetLen == start) {
			if (baseLen > start) {
				if (nrmBaseDir.charAt(start) == separator) {
					start = start + 1;
				} else {
					start = lastSeparator + 1;
				}
			} else {
				start = lastSeparator + 1;
			}
		}
		else {
			start = lastSeparator + 1;
		}
		//--- base
		if (start < nrmBaseDir.length())
			nrmBaseDir = nrmBaseDir.substring(start);
		else
			nrmBaseDir = null;
		//--- target
		if (start < targetPath.length())
			targetPath = targetPath.substring(start);
		else
			targetPath = null;
		
		// 相対パス生成
		//--- 基準ディレクトリの階層数を反映
		StringBuilder relPath = new StringBuilder();
		if (nrmBaseDir != null) {
			String[] baseNames = splitPath(nrmBaseDir);
			for (int i = 0; i < baseNames.length; i++) {
				relPath.append(UP_DIR);
				relPath.append(separator);
			}
		}
		//--- 対象パスを反映
		if (targetPath != null) {
			relPath.append(targetPath);
		}
		
		return relPath.toString();
	}

	/**
	 * <em>ancestor</em> が <em>target</em> の上位階層にある場合に <tt>true</tt> を返す。
	 * @param target	上位階層を確認する <code>File</code> オブジェクト
	 * @param ancestor	上位階層に位置するかを判定する <code>File</code> オブジェクト
	 * @return	<em>ancestor</em> が <em>target</em> の上位階層にある場合は <tt>true</tt> を返す。
	 * 			<em>ancestor</em> と <em>target</em> が等しいパスの場合にも <tt>true</tt> を返す。
	 * @since 1.14
	 */
	static public boolean isDescendingFrom(File target, File ancestor) {
		File file = target;
		
		do {
			if (file.equals(ancestor)) {
				return true;
			}
		} while ((file = file.getParentFile()) != null);
		
		return false;
	}


	/**
	 * 指定されたファイルが表すパスの先頭に位置する名前を取得する。
	 * 絶対パス、相対パスのどちらが指定された場合でも、先頭の区切り文字以外の名称のみを
	 * 取得する。名称が取得できない場合は <tt>null</tt> を返す。
	 * @param file	対象のファイル
	 * @return	取得した名前を返す。名前が取得できない場合は <tt>null</tt> を返す。
	 * 
	 * @since 1.10
	 */
	static public String getTopName(File file) {
		return (file==null ? null : getTopName(file.getPath()));
	}

	/**
	 * 指定されたパス文字列の先頭に位置する名前を取得する。
	 * 絶対パス、相対パスのどちらが指定された場合でも、先頭の区切り文字以外の名称のみを
	 * 取得する。名称が取得できない場合は <tt>null</tt> を返す。
	 * @param path	対象のパス文字列
	 * @return	取得した名前を返す。名前が取得できない場合は <tt>null</tt> を返す。
	 * 
	 * @since 1.10
	 */
	static public String getTopName(String path) {
		if (path == null)
			return null;
		
		int len = path.length();
		int end = len;
		int start = -1;
		for (int i = 0; i < len; i++) {
			char c = path.charAt(i);
			if (c==WINDOWS_FILE_SEPARATOR_CHAR || c==LINUX_FILE_SEPARATOR_CHAR) {
				if (start >= 0) {
					end = i;
					break;
				}
			}
			else if (start < 0) {
				start = i;
			}
		}
		
		if (start < 0) {
			return null;
		} else {
			return path.substring(start, end);
		}
	}

	/**
	 * パス文字列から、ファイル名のみを取得する。
	 * ファイル名区切り文字は、Windows(\)またはWindows以外(/)を識別する。
	 * 
	 * @param targetPath パスを表す文字列
	 * @return ファイル名
	 */
	static public String getFilename(String targetPath) {
		String filename = targetPath;
		if (targetPath != null && targetPath.length() > 0) {
			int wsep = targetPath.lastIndexOf(WINDOWS_FILE_SEPARATOR_CHAR);
			int lsep = targetPath.lastIndexOf(LINUX_FILE_SEPARATOR_CHAR);
			int sep = Math.max(wsep, lsep);
			if (sep >= 0) {
				filename = targetPath.substring(sep + 1);
			}
		}
		return filename;
	}

	/**
	 * パスを含まないファイル名から、拡張子のみを取得する。
	 * このメソッドでは、文字列の終端に最も近い'.'以降の文字列を返す。
	 * 拡張子が存在しない場合は null を返す。
	 * 
	 * @param filename パスを含まないファイル名
	 * @return '.'を除く拡張子を返す。拡張子が見つからない場合は null を返す。
	 */
	static protected String getExtensionFromFilename(String filename) {
		String ext = null;
		if (filename != null) {
			int iext = filename.lastIndexOf('.');
			if (iext >= 0 && (iext+1) < filename.length()) {
				ext = filename.substring(iext+1);
			}
		}
		return ext;
	}

	/**
	 * パス文字列から、ファイルの拡張子を取得する。
	 * このメソッドでは、ファイル名の文字列の終端に最も近い'.'以降の文字列を返す。
	 * 拡張子が存在しない場合は null を返す。
	 * 
	 * @param targetPath パスを表す文字列
	 * @return '.'を除く拡張子を返す。拡張子が見つからない場合は null を返す。
	 */
	static public String getExtension(String targetPath) {
		String filename = getFilename(targetPath);
		return getExtensionFromFilename(filename);
	}

	/**
	 * パス文字列の終端に、指定の拡張子を付加したパス文字列を返す。
	 * すでに同一の拡張子が付加されている場合は、入力のパス文字列を返す。
	 * 付加する拡張子に'.'がなければ自動的に付加する。
	 * 
	 * @param targetPath パスを表す文字列
	 * @param ext 付加する拡張子文字列
	 * @return 拡張子を付加したパス文字列
	 * 
	 * @throws NullPointerException 引数が null の場合
	 */
	static public String addExtension(String targetPath, String ext) {
		if (targetPath == null)
			throw new NullPointerException("targetPath");
		if (ext == null)
			throw new NullPointerException("ext");
		
		if (!ext.startsWith(".")) {
			ext = "." + ext;
		}
		if (Strings.endsWithIgnoreCase(targetPath, ext)) {
			return targetPath;
		} else {
			return (targetPath + ext);
		}
	}

	/**
	 * パス文字列の拡張子を除去する。
	 * 拡張子が存在しない場合は、入力のパス文字列を返す。
	 * 
	 * @param targetPath パスを表す文字列
	 * @return 拡張子を取り除いたパス文字列
	 */
	static public String removeExtension(String targetPath) {
		String retPath = targetPath;
		if (targetPath != null && targetPath.length() > 0) {
			String filename = getFilename(targetPath);
			String path = targetPath.substring(0, (targetPath.length() - filename.length()));
			int iext = filename.lastIndexOf('.');
			if (iext >= 0) {
				retPath = path + filename.substring(0, iext);
			}
		}
		return retPath;
	}

	/**
	 * パス文字列の拡張子を、指定の拡張子に置き換える。
	 * ext が null もしくは長さ 0 の文字列の場合は、パス文字列から拡張子を除去する。
	 * 
	 * @param targetPath パスを表す文字列
	 * @param ext 置き換える拡張子
	 * @return 拡張子置き換え後のパス文字列
	 * 
	 * @throws NullPointerException targetPath が null の場合
	 */
	static public String replaceExtension(String targetPath, String ext) {
		if (targetPath == null)
			throw new NullPointerException("targetPath");
		if (ext == null || ext.length() <= 0)
			return removeExtension(targetPath);
		
		String newPath = removeExtension(targetPath);
		return addExtension(newPath, ext);
	}

	/**
	 * 先頭のファイル名区切り文字を除去する。
	 * ファイル名区切り文字は、Windows(\)またはWindows以外(/)の1文字を除去する。
	 * 
	 * @param targetPath パスを表す文字列
	 * @return 区切り文字を取り除いた文字列
	 */
	static public String removeStartsFileSeparator(String targetPath) {
		if (targetPath != null) {
			if (targetPath.startsWith(WINDOWS_FILE_SEPARATOR)) {
				return targetPath.substring(WINDOWS_FILE_SEPARATOR.length());
			}
			else if (targetPath.startsWith(LINUX_FILE_SEPARATOR)) {
				return targetPath.substring(LINUX_FILE_SEPARATOR.length());
			}
			else {
				return targetPath;
			}
		}
		else {
			return targetPath;
		}
	}

	/**
	 * 終端のファイル名区切り文字を除去する。
	 * ファイル名区切り文字は、Windows(\)またはWindows以外(/)の1文字を除去する。
	 * 
	 * @param targetPath パスを表す文字列
	 * @return 区切り文字を取り除いた文字列
	 */
	static public String removeEndsFileSeparator(String targetPath) {
		if (targetPath != null && targetPath.length() > 0) {
			if (targetPath.endsWith(WINDOWS_FILE_SEPARATOR)) {
				return targetPath.substring(0, targetPath.length() - WINDOWS_FILE_SEPARATOR.length());
			}
			else if (targetPath.endsWith(LINUX_FILE_SEPARATOR)) {
				return targetPath.substring(0, targetPath.length() - LINUX_FILE_SEPARATOR.length());
			}
			else {
				return targetPath;
			}
		}
		else {
			return targetPath;
		}
	}
	
	static public File ensureDirectory(String path)
		throws IOException
	{
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				String msg = "the specified directory is a file";
				throw new IOException(msg);
			} else if (file.isDirectory()) {
				return file;
			} else {
				String msg = "the specified directory is not known type";
				throw new IOException(msg);
			}
		} else {
			if (file.mkdir()) {
				return file;
			} else {
				String msg = "failed to create the specified directory";
				throw new IOException(msg);
			}
		}
	}
	
	static public void ensureDirectory(File file)
		throws IOException
	{
		if (file.exists()) {
			if (file.isFile()) {
				String msg = "the specified directory=" + file.getAbsolutePath()
						+ " is a file";
				throw new IOException(msg);
			} else if (file.isDirectory()) {
				return;
			} else {
				String msg = "the specified directory=" + file.getAbsolutePath()
						+ " is not known type";
				throw new IOException(msg);
			}
		} else {
			if (!file.mkdirs()) {
				String msg = "failed to create the specified directory="
						+ file.getAbsolutePath();
				throw new IOException(msg);
			}
		}
	}

	/**
	 * 指定のパラメータが設定された {@link javax.swing.JFileChooser} のインスタンスを取得する。
	 * 
	 * @param selectionMode		表示されるファイルの種類
     * <ul>
     * <li>JFileChooser.FILES_ONLY
     * <li>JFileChooser.DIRECTORIES_ONLY
     * <li>JFileChooser.FILES_AND_DIRECTORIES
     * </ul>
     * @param multiSelection	複数のファイルを選択できる場合は <tt>true</tt>
     * @param initialFile		初期選択ディレクトリを指定するためのファイルオブジェクト。このオブジェクトが
     * 							ファイルを指す場合は、そのファイルが存在するディレクトリが初期ディレクトリとなる。
     * 							<tt>null</tt> の場合は、現在のカレントディレクトリとなる。
     * @param filters			ファイルフィルタ
     * 
     * @return {@link javax.swing.JFileChooser} のインスタンス
	 */
	static public synchronized JFileChooser getFileChooser(int selectionMode, boolean multiSelection,
														File initialFile, FileFilter...filters)
	{
		// setup chooser
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(selectionMode);
		chooser.setMultiSelectionEnabled(multiSelection);
		
		// setup filters
		for (int i = filters.length - 1; i >= 0; i--) {
			chooser.setFileFilter(filters[i]);
		}
		
		// setup initial file
		File initDir = null;
		if (initialFile != null) {
			try {
				if (initialFile.isDirectory()) {
					initDir = initialFile;
				}
				else {
					File parentDir = initialFile.getParentFile();
					if (parentDir != null && parentDir.isDirectory()) {
						initDir = parentDir;
					}
				}
			}
			catch (Throwable ex) {
				// no implement
			}
		}
		if (initDir == null) {
			initDir = new File("");
		}
		chooser.setCurrentDirectory(initDir);
		
		return chooser;
	}

	//------------------------------------------------------------
	// File Helper
	//------------------------------------------------------------

	/**
	 * テンポラリとしてユニークな抽象パスを生成する。
	 * このメソッドは、<em>directory</em> ディレクトリの下に、重複しない
	 * 新しい名前を持つ抽象パスを返す。
	 * なお、<em>directory</em> が <tt>null</tt> ではないとき、ディレクトリでは
	 * ない形式で存在している場合は例外をスローする。
	 * 
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。3 文字以上の長さが必要である
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @param directory	生成するテンポラリファイル名の親ディレクトリ
	 * @return	生成された抽象パス
	 * @throws NullPointerException		<em>prefix</em> もしくは <em>directory</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>prefix</em> 引数が 3 文字に満たない場合、
	 * 										もしくは <em>directory</em> がディレクトリではない状態で存在する場合
	 */
	static public File generateTempFilename(String prefix, String suffix, File directory) {
		if (prefix == null)
			throw new NullPointerException("'prefix' argument is null.");
		if (directory == null)
			throw new NullPointerException("'directory' argument is null.");
		if (prefix.length() < 3)
			throw new IllegalArgumentException("Prefix string too short");
		if (directory.exists() && !directory.isDirectory())
			throw new IllegalArgumentException("'directory' is not directory.");
		if (Strings.isNullOrEmpty(suffix)) {
			suffix = ".tmp";
		}

		File f = null;
		do {
			long n = LazySecureRandome.random.nextInt();
			if (n == Long.MIN_VALUE) {
				n = 0;
			} else {
				n = Math.abs(n);
			}
			f = new File(directory, prefix + Long.toString(n) + suffix);
		} while (f.exists());
		
		return f;
	}
	
	static private class LazySecureRandome {
		static final SecureRandom random = new SecureRandom();
	}

	private static int BUF_SIZE = 50000;
	private static byte[] BUF = new byte[BUF_SIZE];

	/**
	 * <em>source</em> ファイルを <em>target</em> で指定されたパスに移動する。
	 * このメソッドは単一ファイルの移動を行うものであり、<em>source</em> や <em>target</em> が
	 * ディレクトリの場合はエラーとなる。
	 * 
	 * @param source	移動するファイルを示す抽象パス
	 * @param target	移動先を示す抽象パス。このパスが既存のファイルを示す場合は、そのファイルを上書きする。
	 * @param buffer	移動に使用するバッファ
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws SecurityException	セキュリティマネージャがエラーを通知した場合
	 * @since 1.14
	 */
	static public void moveToFile(File source, File target, byte[] buffer)
	throws IOException
	{
		if (!source.exists() || !source.isFile())
			throw new Error("Source file does not exists or is not file : \"" + source.getAbsolutePath() + "\"");

		// copy(rename) to
		if (target.exists()) {
			// << exists >>
			if (target.isDirectory()) {
				throw new Error("Target file is not a file : \"" + target.getAbsolutePath() + "\"");
			}
			//--- copy source to target
			copy(source, target, buffer);
		}
		else {
			// << does not exists >>
			try {
				if (!source.renameTo(target)) {
					target.createNewFile();
					copy(source, target, buffer);
				}
			}
			catch (IOException ex) {
				try {
					target.delete();
				} catch (Throwable ignore){}
				throw ex;
			}
			catch (SecurityException ex) {
				try {
					target.delete();
				} catch (Throwable ignore){}
				throw ex;
			}
		}
		
		// delete source
		source.delete();
	}

	/**
	 * 指定された抽象パスが示すファイルまたはディレクトリを削除する。
	 * ディレクトリの場合は、そのディレクトリに含まれるすべてのファイルと
	 * ディレクトリを削除する。
	 * @param toDelete	削除するファイルもしくはディレクトリを示す抽象パス
	 * @throws FileAccessException	削除に失敗した場合、もしくは、セキュリティマネージャが存在し、
	 * 								セキュリティマネージャの
	 * 								<code>{@link java.lang.SecurityManager#checkDelete}</code>
	 * 								メソッドがファイルへの削除アクセスを許可しない場合
	 * @since 1.14
	 */
	static public void deleteRecursive(File toDelete) throws FileAccessException
	{
		if (toDelete.isDirectory()) {
			File[] flist = toDelete.listFiles();
			for (int i = 0; i < flist.length; i++) {
				deleteRecursive(flist[i]);
			}
		}
		try {
			if (!toDelete.delete() && toDelete.exists()) {
				throw new FileAccessException(toDelete, "Cannot delete.");
			}
		} catch (SecurityException ex) {
			throw new FileAccessException(toDelete, ex);
		}
	}

	/**
	 * 指定されたファイルを削除する。ディレクトリの場合は、そのディレクトリ含まれる
	 * すべてのファイルとディレクトリを削除する。
	 * @param toRemove	削除するファイルを示す抽象パス
	 */
	static public void removeRecursive(File toRemove) {
		if (toRemove.isDirectory()) {
			File flist[] = toRemove.listFiles();
			for (int i = 0; i < flist.length; i++) {
				removeRecursive(flist[i]);
			}
		}
		toRemove.delete();
	}

	static public void moveRecursive(File source, File target) throws IOException
	{
		byte[] buf = new byte[BUF_SIZE];
		moveRecursive(source, target, buf);
	}

	static void moveRecursive(File source, File target, byte[] buffer) throws IOException
	{
		if (source.isDirectory()) {
			if (!target.exists()) {
				target.mkdirs();
			}
			if (target.isDirectory()) {
				File[] files = source.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					File targetFile = new File(target, file.getName());
					if (file.isFile()) {
						if (targetFile.exists()) {
							targetFile.delete();
						}
						if (!file.renameTo(targetFile)) {
							copy(file, targetFile, buffer);
							file.delete();
						}
					}
					else {
						if (!targetFile.exists()) {
							if (!targetFile.mkdirs()) {
								throw new IOException("Could not create target directory: " + targetFile);
							}
						}
						moveRecursive(file, targetFile);
					}
				}
				source.delete();
			}
		}
		else {
			if (!target.isDirectory()) {
				copy(source, target, buffer);
				source.delete();
			}
		}
	}
	
	static public void copyRecursive(File source, File target) throws IOException
	{
		byte[] buf = new byte[BUF_SIZE];
		copyRecursive(source, target, buf);
	}
	
	static void copyRecursive(File source, File target, byte[] buffer) throws IOException
	{
		if (source.isDirectory()) {
			if (!target.exists()) {
				target.mkdirs();
			}
			if (target.isDirectory()) {
				File[] files = source.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					File targetFile = new File(target, file.getName());
					if (file.isFile()) {
						if (targetFile.exists()) {
							targetFile.delete();
						}
						copy(file, targetFile, buffer);
					}
					else {
						targetFile.mkdirs();
						copyRecursive(file, targetFile);
					}
				}
			}
		}
		else {
			if (!target.isDirectory()) {
				if (!target.exists()) {
					File dir = target.getParentFile();
					if(!dir.exists() && !dir.mkdirs()) {
						throw new IOException("Could not create target directory: " + dir);
					}
					if (!target.createNewFile()) {
						throw new IOException("Could not create target file: " + target);
					}
				}
				copy(source, target, buffer);
			}
		}
	}
	
	static public long copy(File source, File target) throws IOException
	{
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			long ret = copy(fis, target);
			//--- set attribute by java.io.File
			if (!source.canWrite())
				target.setReadOnly();
			target.setLastModified(source.lastModified());
			return ret;
		}
		finally {
			if (fis != null) {
				closeStream(fis);
				fis = null;
			}
		}
	}
	
	static public long copy(File source, File target, byte[] buffer) throws IOException
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			long ret = copy(fis, fos, buffer);
			//--- set attribute by java.io.File
			if (!source.canWrite())
				target.setReadOnly();
			target.setLastModified(source.lastModified());
			return ret;
		}
		finally {
			if (fis != null) {
				closeStream(fis);
				fis = null;
			}
			if (fos != null) {
				closeStream(fos);
				fos = null;
			}
        }
	}
	
	static public long copy(InputStream inStream, File target) throws IOException
	{
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(target);
			return copy(inStream, fos);
		}
		finally {
			if (fos != null) {
				closeStream(fos);
				fos = null;
			}
		}
	}
	
	static public long copy(InputStream inStream, OutputStream outStream) throws IOException
	{
		byte[] buf = new byte[BUF_SIZE];
		return copy(inStream, outStream, buf);
	}
	
	static public long globalBufferCopy(InputStream inStream, OutputStream outStream) throws IOException
	{
		synchronized (BUF) {
			return copy(inStream, outStream, BUF);
		}
	}
	
	static public long copy(InputStream inStream, OutputStream outStream, byte[] buffer) throws IOException
	{
		long bytesCopied = 0;
		int read = -1;

		while ((read = inStream.read(buffer, 0, buffer.length)) != -1) {
			outStream.write(buffer, 0, read);
			bytesCopied += read;
		}
		return bytesCopied;
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

	//------------------------------------------------------------
	// inner classes
	//------------------------------------------------------------
}
