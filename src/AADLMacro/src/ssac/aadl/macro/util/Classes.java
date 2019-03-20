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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Classes.java	2.0.0	2014/03/18 : move 'ssac.util' to 'ssac.aadl.macro.util' 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Classes.java	1.11	2010/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Classes.java	1.00	2008/11/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ssac.aadl.macro.util.io.Files;

/**
 * クラスに関するユーティリティ。
 * 
 * @version 2.0.0	2014/03/18
 *
 * @since 1.00
 */
public final class Classes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたパス文字列を指定の区切り文字で分割し、絶対パスに変換した
	 * リストを生成する。
	 * 
	 * @param paths				パス文字列
	 * @param pathSeparator		正規表現のパス区切り文字。空文字列もしくは<tt>null</tt> の場合は、';' もしくは ':'
	 * 
	 * @return 絶対パスのリスト
	 */
	static public final List<String> toAbsoluteClassPathList(final String paths, final String pathSeparator) {
		// パスの分割
		String ps;
		if (pathSeparator != null && pathSeparator.length() > 0)
			ps = pathSeparator;
		else
			ps = "[\\;\\:]";
		String[] aryPath;
		if (paths != null && paths.length() > 0)
			aryPath = paths.split(ps);
		else
			aryPath = new String[0];
		
		// 絶対パスリストの生成
		if (aryPath.length > 0) {
			ArrayList<String> pathList = new ArrayList<String>(aryPath.length);
			for (String path : aryPath) {
				if (path != null && path.length() > 0) {
					File file = new File(path);
					pathList.add(file.getAbsolutePath());
				}
			}
			return pathList;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * 指定された文字列配列から、プラットフォーム固有の区切り文字で連結した
	 * クラスパス文字列を生成する。
	 * 
	 * @param pathSeparator	任意のパス区切り文字を指定する。<tt>null</tt> の場合はプラットフォーム固有の区切り文字を使用する。
	 * @param paths	パス文字列の配列
	 * @return	パス区切り文字で連結されたクラスパス文字列
	 */
	static public final String toClassPathString(final String pathSeparator, final String...paths) {
		return toClassPathString(pathSeparator, Arrays.asList(paths));
	}

	/**
	 * 指定されたパスリストから、プラットフォーム固有の区切り文字で連結した
	 * クラスパス文字列を生成する。
	 * 
	 * @param pathSeparator	任意のパス区切り文字を指定する。<tt>null</tt> の場合はプラットフォーム固有の区切り文字を使用する。
	 * @param paths	パス文字列のリスト
	 * @return	パス区切り文字で連結されたクラスパス文字列
	 */
	static public final String toClassPathString(final String pathSeparator, final List<String> paths) {
		if (paths.isEmpty()) {
			return "";
		} else {
			String ps;
			if (pathSeparator != null && pathSeparator.length() > 0)
				ps = pathSeparator;
			else
				ps = File.pathSeparator;
			StringBuilder sb = new StringBuilder();
			Iterator<String> it = paths.iterator();
			sb.append(it.next());
			while (it.hasNext()) {
				sb.append(ps);
				sb.append(it.next());
			}
			return sb.toString();
		}
	}

	/**
	 * 指定されたクラスのリソースが格納されているクラスパスを
	 * 取得する。このメソッドが返すパスは絶対パスとなる。
	 * 
	 * @param clazz		対象クラス
	 * @return	クラスリソースが格納されているクラスパス
	 * 
	 * @throws IllegalArgumentException	クラスが見つからない場合
	 */
	static public final File getClassPath(String className) {
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (Throwable ex) {
			throw new IllegalArgumentException(ex);
		}
		return getClassSource(clazz);
	}

	/**
	 * 指定したクラスのロード元となるファイルを取得する
	 * 
	 * @param clazz 元ファイルを取得するクラス
	 * @return 取得したファイルを返す。取得できない場合は null を返す。
	 */
	static public final File getClassSource(final Class<?> clazz) {
		String clazzResource = clazz.getName().replace('.','/') + ".class";
		return getResourceSource(clazz.getClassLoader(), clazzResource);
	}

	/**
	 * クラスローダーから、指定したリソースのファイルを取得する。
	 * 
	 * @param classLoader クラスローダー
	 * @param resource リソース
	 * @return 取得したファイルを返す。取得できない場合は null を返す。
	 */
	static public final File getResourceSource(final ClassLoader classLoader, final String resource) {
		ClassLoader cl = (classLoader == null ? Classes.class.getClassLoader() : classLoader); 
		
		URL url = null;
		if (cl == null) {
			url = ClassLoader.getSystemResource(resource);
			//--- for Debug
			//System.err.println("[Debug] ssac.util.Classes#getResourceSource<getSystemResource> : \"" + String.valueOf(url) + "\"");
			//--- end of Debug
		} else {
			url = cl.getResource(resource);
			//--- for Debug
			//System.err.println("[Debug] ssac.util.Classes#getResourceSource<getResource> : \"" + String.valueOf(url) + "\"");
			//--- end of Debug
		}
		if (url != null) {
			String strurl = url.toString();
			if (strurl.startsWith("jar:file:")) {
				int pling = strurl.indexOf("!");
				String jarName = strurl.substring(4, pling);
				return new File(getPathFromURI(jarName));
			}
			else if (strurl.startsWith("file:")) {
				int tail = strurl.indexOf(resource);
				String dirName = strurl.substring(0, tail);
				return new File(getPathFromURI(dirName));
			}
		}
		// not found!
		return null;
	}
	
	/**
	 * <code>&quot;file:&quot;</code> で始まる URI から、ファイルパスを生成する。
	 * ファイルパスの生成において、<code>'?'</code> で始まるURLパラメータは無視する。
	 * 
	 * @param strURI	対象のURI
	 * @return ファイルパス
	 * 
	 * @throws IllegalArgumentException	URIが正しくない場合、もしくは'file:'スキーマではない場合
	 */
	static public final String getPathFromURI(final String strURI) {
		//--- for Debug
		//System.err.println("[Debug] ssac.util.Classes#getPathFromURI argument : \"" + String.valueOf(strURI) + "\"");
		//--- end of Debug
		if (strURI == null) {
			throw new NullPointerException("strURI is null!");
		}
		URI uri = null;
		try {
			uri = new URI(strURI);
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException("Cannot create URI instance by \"" + strURI + "\".", ex);
		}
		
		if (uri == null || !("file".equals(uri.getScheme()))) {
			throw new IllegalArgumentException("Can only handle valid file: URIs");
		}
		//--- for Debug
		//System.err.println("[Debug] ssac.util.Classes#getPathFromURI URI information\n    uri.getHost()=\"" + String.valueOf(uri.getHost()) + "\"\n    uri.getPath()=\"" + String.valueOf(uri.getPath()) + "\"");
		//--- end of Debug

		// パスの取得
		String strPath;
		if (uri.isOpaque()) {
			// スラッシュ(/)で始まらないスキーマ
			strPath = uri.getSchemeSpecificPart();
			//--- URLパラメータの除外
			int pos = strPath.indexOf('?');
			if (pos >= 0) {
				strPath = strPath.substring(0, pos);
			}
			//--- 名前区切り文字の変換
			strPath = strPath.replace('/', File.separatorChar);
			//--- 絶対パスに変換
			strPath = (new File("").getAbsolutePath() + File.separatorChar + strPath);
			
		} else {
			// スラッシュ(/)で始まるスキーマ
			StringBuffer sb = new StringBuffer();
			//--- ホスト名の抽出
			String strHost = uri.getHost();
			if (strHost != null && strHost.length() > 0) {
				sb.append(File.separatorChar);
				sb.append(File.separatorChar);
				sb.append(strHost);
			}
			//--- URLパラメータの除外
			strPath = uri.getPath();
			int pos = strPath.indexOf('?');
			sb.append((pos < 0) ? strPath : strPath.substring(0, pos));
			//--- ファイルパスの抽出
			strPath = sb.toString().replace('/', File.separatorChar);
			if (File.pathSeparatorChar == ';' && strPath.startsWith("\\") && strPath.length() > 2 &&
				Character.isLetter(strPath.charAt(1)) && strPath.charAt(2) == ':')
			{
				// 絶対パスのドライブ文字の前に File.separatorChar がある場合は、削除(Windows)
				strPath = strPath.substring(1);
			}
		}
		
		return Files.normalizePath(strPath);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
