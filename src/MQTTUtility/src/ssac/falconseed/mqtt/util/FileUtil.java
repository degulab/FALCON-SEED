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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FileUtil.java	1.0.0	2013/02/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 * ファイル操作に関する補助機能を提供するユーティリティクラス。
 * 
 * @version 1.0.0	2013/02/28
 */
public class FileUtil
{
	private FileUtil() {}
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//static private final int defBufferSize = 2048;
	
	static public final char  CommonSeparatorChar = '/';
	static public final String CommonSeparator = "" + CommonSeparatorChar;
	static public final char  CommonPathSeparatorChar = ';';
	static public final String CommonPathSeparator = "" + CommonPathSeparatorChar;
	
	//private static final char WINDOWS_FILE_SEPARATOR_CHAR = '\\';
	//private static final char LINUX_FILE_SEPARATOR_CHAR = '/';
	
	//private static final String WINDOWS_FILE_SEPARATOR = "" + WINDOWS_FILE_SEPARATOR_CHAR;
	//private static final String LINUX_FILE_SEPARATOR = "" + LINUX_FILE_SEPARATOR_CHAR;
	
	//private static final String CUR_DIR = ".";
	//private static final String UP_DIR = "..";

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
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
