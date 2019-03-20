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
 * @(#)AADLMessages.java	1.90	2013/08/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMessages.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.io.File;

/**
 * AADL実行時のメッセージリソース
 * 
 * @version 1.90	2013/08/07
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 1.50
 */
public class AADLMessages
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
	 * ファイルが存在しないことを示すエラーメッセージを返す。
	 * @param file	存在しなかったファイルを示す抽象パス
	 * @return	エラーメッセージ
	 */
	static public final String formatFileNotFoundMessage(File file) {
		return formatFileNotFoundMessage(file==null ? (String)null : file.getAbsolutePath());
	}

	/**
	 * ファイルが存在しないことを示すエラーメッセージを返す。
	 * @param filename	存在しなかったファイルのパスを示す文字列
	 * @return	エラーメッセージ
	 */
	static public final String formatFileNotFoundMessage(String filename) {
		if (filename == null) {
			return "File not found: unknown file.";
		} else {
			return "File not found: \"" + filename + "\"";
		}
	}

	/**
	 * 文字コードがサポートされていないことを示すエラーメッセージを返す。
	 * @param charsetName	サポートされていなかった文字コード名
	 * @return	エラーメッセージ
	 */
	static public final String formatUnsupportedEncodingMessage(String charsetName) {
		if (charsetName == null) {
			return "Unsupported encoding: unknown Character-Set name.";
		} else {
			return "Unsupported encoding: " + charsetName;
		}
	}

	/**
	 * ファイルの読み込みに失敗したことを示すエラーメッセージを返す。
	 * @param file	読み込みに失敗したファイルを示す抽象パス
	 * @return	エラーメッセージ
	 */
	static public final String formatFailedReadMessage(File file) {
		return formatFailedReadMessage(file==null ? (String)null : file.getAbsolutePath());
	}

	/**
	 * ファイルの読み込みに失敗したことを示すエラーメッセージを返す。
	 * @param filename	読み込みに失敗したファイルをパスを示す文字列
	 * @return	エラーメッセージ
	 */
	static public final String formatFailedReadMessage(String filename) {
		if (filename == null) {
			return "Failed to read from file.";
		} else {
			return "Failed to read from file: \"" + filename + "\"";
		}
	}

	/**
	 * ファイルの書き込みに失敗したことを示すエラーメッセージを返す。
	 * @param file	書き込みに失敗したファイルを示す抽象パス
	 * @return	エラーメッセージ
	 * @since 1.90
	 */
	static public final String formatFailedWriteMessage(File file) {
		return formatFailedWriteMessage(file==null ? (String)null : file.getAbsolutePath());
	}

	/**
	 * ファイルの書き込みに失敗したことを示すエラーメッセージを返す。
	 * @param filename	書き込みに失敗したファイルのパスを示す文字列
	 * @return	エラーメッセージ
	 * @since 1.90
	 */
	static public final String formatFailedWriteMessage(String filename) {
		if (filename == null) {
			return "Failed to write to file.";
		} else {
			return "Failed to write to file: \"" + filename + "\"";
		}
	}

	/**
	 * ファイルのクローズに失敗したことを示すエラーメッセージを返す。
	 * @param file	クローズに失敗したファイルを示す抽象パス
	 * @return	エラーメッセージ
	 * @since 1.90
	 */
	static public final String formatFailedCloseMessage(File file) {
		return formatFailedCloseMessage(file==null ? (String)null : file.getAbsolutePath());
	}

	/**
	 * ファイルのクローズに失敗したことを示すエラーメッセージを返す。
	 * @param filename	クローズに失敗したファイルのパスを示す文字列
	 * @return	エラーメッセージ
	 * @since 1.90
	 */
	static public final String formatFailedCloseMessage(String filename) {
		if (filename == null) {
			return "Failed to close file.";
		} else {
			return "Failed to close file: \"" + filename + "\"";
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
