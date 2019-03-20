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
 * @(#)IOutputWriter.java	3.0.0	2014/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.Closeable;
import java.io.Flushable;
import java.util.Locale;

/**
 * プロセスにより出力される標準出力、エラー出力の内容を書き込むためのインタフェース。
 * 
 * @version 3.0.0	2014/03/24
 */
public interface IOutputWriter extends Closeable, Flushable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このインタフェースへのアクセス時に使用するロックオブジェクトを返す。
	 * 利用者は、このロックオブジェクトにより、メソッド呼び出しよりも前に
	 * 同一のロックを獲得するために、このロックオブジェクトを使用する。
	 * @return	ロックオブジェクト、もしくは <tt>null</tt>
	 */
	public Object getLockObject();

	/**
	 * 現在の状態をフラッシュし、そのエラー状況を確認する。
	 * 基本となる出力ストリームが <code>InterruptedIOException</code> ではなく <code>IOException</code> をスローした場合、
	 * および <code>setError</code> メソッドが呼び出された場合に、内部エラー状態は <tt>true</tt> に設定される。
	 * 基本となる出力ストリームのオペレーションが <code>InterruptedIOException</code> をスローすると、
	 * 次の操作によって例外を割り込みに戻す。
	 * <blockquote>
	 * <code>Thread.currentThread().interrupt();</code>
	 * </blockquote>
	 * @return	エラー状態であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean checkError();

	/**
	 * バッファにデータがあれば、その内容を出力する。
	 */
	public void flush();

	/**
	 * オブジェクトをクローズする。
	 */
	public void close();

	/**
	 * 単一の文字を出力する。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param c			出力する文字
	 */
	public void print(boolean isError, char c);

	/**
	 * 文字列を出力する。
	 * 引数が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * <tt>null</tt> でない場合は、指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力する文字列
	 */
	public void print(boolean isError, String str);
	
	/**
	 * 指定されたオブジェクトを出力する。
	 * オブジェクトは {@link java.lang.String#valueOf(Object)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力するオブジェクト
	 */
	public void print(boolean isError, Object obj);

	/**
	 * 指定された書式付き文字列を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printf(boolean isError, String format, Object...args);

	/**
	 * 指定された書式付き文字列を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(Locale, String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printf(boolean isError, Locale l, String format, Object...args);

	/**
	 * 単一の文字を出力し、プラットフォーム固有の改行文字を出力する。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param c			出力する文字
	 */
	public void println(boolean isError, char c);
	
	/**
	 * 文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * 引数が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * <tt>null</tt> でない場合は、指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力する文字列
	 */
	public void println(boolean isError, String str);
	
	/**
	 * 指定されたオブジェクトを出力し、プラットフォーム固有の改行文字を出力する。
	 * オブジェクトは {@link java.lang.String#valueOf(Object)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param str		出力するオブジェクト
	 */
	public void println(boolean isError, Object obj);
	
	/**
	 * 指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printfln(boolean isError, String format, Object...args);
	
	/**
	 * 指定された書式付き文字列を出力し、プラットフォーム固有の改行文字を出力する。
	 * 書式付き文字列は、{@link java.lang.String#format(Locale, String, Object...)} によって文字列に変換され、
	 * 指定された文字エンコーディングに従ってバイト変換され、ファイルに書き込まれる。
	 * @param isError	標準出力の内容とする場合は <tt>true</tt>、標準エラー出力の内容とする場合は <tt>false</tt>
	 * @param l			書式設定時に適用する <code>Locale</code>。<tt>null</tt> の場合、ローカリゼーションは適用されない。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 */
	public void printfln(boolean isError, Locale l, String format, Object...args);

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
