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
 * @(#)ProcessUtil.java	2.0.0	2014/03/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.process;

import java.lang.reflect.Field;
import java.util.List;

import ssac.aadl.macro.util.Strings;

/**
 * {@link java.lang.Process} オブジェクト用ユーティリティー。
 * 
 * @version 2.0.0	2014/03/18
 * @since 2.0.0
 */
public class ProcessUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * Windows プラットフォームにおいて、ダブルクオートによる
	 * エンクオートが必要な文字
	 */
	static public final char[] DEFAULT_WINDOWS_ENQUOTECHARS	= {'*', '?', '\"'};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private ProcessUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたプロセスが実行中かどうかを判定する。
	 * 実行中の判定は、{@link java.lang.Process#exitValue()} が例外をスローした場合を実行中とする。
	 * @param process	対象プロセス
	 * @return	実行中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public final boolean isProcessAlive(final Process process) {
		try {
			process.exitValue();
			return false;
		}
		catch (IllegalThreadStateException ex) {
			return true;
		}
	}

	/**
	 * 指定されたプロセスの識別コード(プロセスIDとは限らない)を取得する。
	 * @param process	対象プロセス
	 * @return	プロセス識別コード
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws UnsupportedOperationException	プロセス識別コードが取得できない場合
	 */
	static public final long getProcessHandle(final Process process) {
		if (process == null)
			throw new NullPointerException("Target process object is null.");

		long retValue;
		try {
			if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
				// get Process ID
				Field f = process.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				retValue = f.getInt(process);
			} else {
				// get Process handle
				Field f = process.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				retValue = f.getLong(process);
			}
		} catch (Throwable ex) {
			throw new UnsupportedOperationException("Could not get Process ID or handle.", ex);
		}
		return retValue;
	}

	/**
	 * コマンド文字列リストのエンクオート。
	 * <p>Windows プラットフォームにおいてのみ、エンクオートが必要なコマンド文字列(アスタリスク(*)、クエスチョン(?)、ダブルクオート(&quot;))が含まれている場合、
	 * エンクオート(ダブルクオートで囲む処理)を行う。
	 * また、空文字の場合もダブルクオートで囲む。
	 * 
	 * @param command	エンクオートするコマンド文字列リスト
	 * @return	エンクオートが行われた場合に <tt>true</tt> を返す。
	 */
	static public boolean enquoteCommand(List<String> command) {
		boolean enquoted = false;
		if (System.getProperty("os.name").indexOf("Windows") >= 0) {
			// Windows の場合は、enquote
			for (int idx = 0; idx < command.size(); idx++) {
				String cmd = command.get(idx);
				if (Strings.isNullOrEmpty(cmd)) {
					command.set(idx, "\"\"");
					enquoted = true;
				}
				else if (Strings.contains(cmd, DEFAULT_WINDOWS_ENQUOTECHARS)) {
					cmd = "\"" + cmd.replaceAll("\"", "\\\\\"") + "\"";
					command.set(idx, cmd);
					enquoted = true;
				}
			}
		}
		return enquoted;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
