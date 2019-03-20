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
 * @(#)VerboseStream.java	2.0.0	2014/03/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.process;

import java.io.PrintStream;

/**
 * 実行時メッセージを出力するインタフェースの実装。
 * システムプロパティ 'aadl.verbose' の値によって、出力レベルを指定できる。
 * <p>'aadl.verbose=info' が指定されている場合、<code>info</code> レベルのメッセージのみが出力される。
 * <p>'aadl.verbose=debug' が指定されている場合、<code>info</code> および <code>debug</code> レベルのメッセージが出力される。
 * <p>'aadl.verbose=trace' が指定されている場合、すべてのレベルのメッセージが出力される。
 * <p>プロパティの値が上記以外、もしくはプロパティが指定されていない場合、どのレベルのメッセージも出力されない。
 * <p>メッセージの出力先の初期値は {@link System#out} に設定されている。
 * 
 * @version 2.0.0	2014/03/18
 * @since 2.0.0
 */
public class VerboseStream
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** プロセス起動時に指定される実行メッセージ出力用プロパティ名 **/
	static public final String PROPKEY_AADL_VERBOSE	= "aadl.verbose";

	/** 実行メッセージ出力レベル : trace **/
	static public final String PROPVALUE_AADL_VERBOSE_TRACE = "trace";
	/** 実行メッセージ出力レベル : debug **/
	static public final String PROPVALUE_AADL_VERBOSE_DEBUG = "debug";
	/** 実行メッセージ出力レベル : info **/
	static public final String PROPVALUE_AADL_VERBOSE_INFO  = "info";

	/** 実行メッセージ出力レベル値 : 出力なし **/
	static public final int	VERBOSELEVEL_NONE	= 0;
	/** 実行メッセージ出力レベル値 : info **/
	static public final int	VERBOSELEVEL_INFO	= 1;
	/** 実行メッセージ出力レベル値 : debug **/
	static public final int	VERBOSELEVEL_DEBUG	= 3;
	/** 実行メッセージ出力レベル値 : trace **/
	static public final int	VERBOSELEVEL_TRACE	= 7;
	
	static private final int MASK_LEVEL_INFO	= 1;
	static private final int MASK_LEVEL_DEBUG	= 2;
	static private final int MASK_LEVEL_TRACE	= 4;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 現在のメッセージ出力レベル **/
	static private volatile int			s_verboseLevel	= getVerboseLevelFromProperty();
	/** 現在の出力ストリーム **/
	static private volatile PrintStream	s_outVerbose	= System.out;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private VerboseStream() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * Java コマンド引数に指定するプロパティオプション用文字列を取得する。
	 * @return	有効な出力レベルが設定されている場合はオプション文字列、それ以外の場合は <tt>null</tt>
	 */
	static public final String getVerboseOption() {
		int vl = s_verboseLevel;
		switch (vl) {
			case VERBOSELEVEL_INFO :
				return "-D".concat(PROPKEY_AADL_VERBOSE).concat("=").concat(PROPVALUE_AADL_VERBOSE_INFO);
			case VERBOSELEVEL_DEBUG :
				return "-D".concat(PROPKEY_AADL_VERBOSE).concat("=").concat(PROPVALUE_AADL_VERBOSE_DEBUG);
			case VERBOSELEVEL_TRACE :
				return "-D".concat(PROPKEY_AADL_VERBOSE).concat("=").concat(PROPVALUE_AADL_VERBOSE_TRACE);
			default :
				return null;
		}
	}

	/**
	 * {@link #PROPKEY_AADL_VERBOSE} で定義されているプロパティの値を取得する。
	 * @return	プロパティの値
	 */
	static public final String getVerboseProperty() {
		return System.getProperty(PROPKEY_AADL_VERBOSE);
	}

	/**
	 * プロパティからメッセージ出力レベルを取得する。
	 * @return	次の出力レベルを返す。
	 * <dl>
	 * <dt>{@link #VERBOSELEVEL_INFO}</dt><dd>'aadl.verbose' プロパティの値に &quot;info&quote; が指定されている。</dd>
	 * <dt>{@link #VERBOSELEVEL_DEBUG}</dt><dd>'aadl.verbose' プロパティの値に &quot;debug&quote; が指定されている。</dd>
	 * <dt>{@link #VERBOSELEVEL_TRACE}</dt><dd>'aadl.verbose' プロパティの値に &quot;trace&quote; が指定されている。</dd>
	 * <dt>{@link #VERBOSELEVEL_NONE}</dt><dd>'aadl.verbose' プロパティの値が上記以外の場合、もしくはプロパティが存在しない場合</dd>
	 * </dl>
	 */
	static public final int getVerboseLevelFromProperty() {
		String strValue = System.getProperty(PROPKEY_AADL_VERBOSE);
		if (strValue != null) {
			if (PROPVALUE_AADL_VERBOSE_INFO.equalsIgnoreCase(strValue)) {
				return VERBOSELEVEL_INFO;
			}
			else if (PROPVALUE_AADL_VERBOSE_DEBUG.equalsIgnoreCase(strValue)) {
				return VERBOSELEVEL_DEBUG;
			}
			else if (PROPVALUE_AADL_VERBOSE_TRACE.equalsIgnoreCase(strValue)) {
				return VERBOSELEVEL_TRACE;
			}
		}
		// no verbose
		return VERBOSELEVEL_NONE;
	}

	/**
	 * 現在のプロパティで、メッセージ出力レベルを更新する。
	 */
	static public void updateVerboseLevelByProperty() {
		s_verboseLevel = getVerboseLevelFromProperty();
	}

	/**
	 * 現在のメッセージ出力レベルを、指定されたレベルに設定する。
	 * このメソッドでは、システムプロパティの内容も更新する。
	 * @param level	メッセージ出力レベル値
	 */
	static public void setVerboseLebel(int level) {
		switch (level) {
			case VERBOSELEVEL_INFO :
				System.setProperty(PROPKEY_AADL_VERBOSE, PROPVALUE_AADL_VERBOSE_INFO);
				break;
			case VERBOSELEVEL_DEBUG :
				System.setProperty(PROPKEY_AADL_VERBOSE, PROPVALUE_AADL_VERBOSE_DEBUG);
				break;
			case VERBOSELEVEL_TRACE :
				System.setProperty(PROPKEY_AADL_VERBOSE, PROPVALUE_AADL_VERBOSE_TRACE);
				break;
			default :
				System.clearProperty(PROPKEY_AADL_VERBOSE);
		}
		updateVerboseLevelByProperty();
	}

	/**
	 * 現在のメッセージ出力先となるストリームを返す。
	 * @return	{@link java.io.PrintStream} オブジェクト
	 */
	static public PrintStream getOut() {
		return s_outVerbose;
	}

	/**
	 * メッセージ出力先とするストリームを設定する。
	 * <em>newStream</em> に <tt>null</tt> を指定した場合、メッセージ出力先ストリームには {@link System#out} が設定される。
	 * @param newStream	{@link java.io.PrintStream} オブジェクト
	 */
	static public void setOut(PrintStream newStream) {
		if (newStream == null) {
			s_outVerbose = System.out;
		} else {
			s_outVerbose = newStream;
		}
	}

	/**
	 * 現在の出力レベルが <code>info</code> レベルかどうかを判定する。
	 * @return	出力レベルが <code>info</code> であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public final boolean isInfo() {
		return ((s_verboseLevel & MASK_LEVEL_INFO) != 0);
	}
	
	/**
	 * 現在の出力レベルが <code>debug</code> レベルかどうかを判定する。
	 * @return	出力レベルが <code>debug</code> であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public final boolean isDebug() {
		return ((s_verboseLevel & MASK_LEVEL_DEBUG) != 0);
	}
	
	/**
	 * 現在の出力レベルが <code>trace</code> レベルかどうかを判定する。
	 * @return	出力レベルが <code>trace</code> であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public final boolean isTrace() {
		return ((s_verboseLevel & MASK_LEVEL_TRACE) != 0);
	}

	/**
	 * <code>info</code> レベルのメッセージが出力可能な場合に、指定されたメッセージを出力し、行を終了する。
	 * 引数が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * @param message	出力するメッセージ
	 */
	static public final void info(String message) {
		if (isInfo()) {
			s_outVerbose.println("[Info] ".concat(message==null ? "null" : message));
		}
	}
	
	/**
	 * <code>info</code> レベルのメッセージが出力可能な場合に、指定されたカテゴリとメッセージを出力し、行を終了する。
	 * カテゴリは、出力レベルの次に &lt;&gt; で囲まれて出力される。
	 * <em>message</em> が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * @param category	出力するカテゴリ
	 * @param message	出力するメッセージ
	 */
	static public final void categoryInfo(String category, String message) {
		if (isInfo()) {
			s_outVerbose.println("[Info] <".concat(category==null ? "" : category).concat("> ").concat(message==null ? "null" : message));
		}
	}

	/**
	 * <code>info</code> レベルのメッセージが出力可能な場合に、指定された書式付き文字列を出力し、行を終了する。
	 * このメソッドでは、{@link String#format(String, Object...)} によって書式付き文字列を展開する。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see String#format(String, Object...)
	 */
	static public final void formatInfo(String format, Object...args) {
		if (isInfo()) {
			s_outVerbose.println("[Info] ".concat(String.format(format, args)));
		}
	}
	
	/**
	 * <code>info</code> レベルのメッセージが出力可能な場合に、指定されたカテゴリと書式付き文字列を出力し、行を終了する。
	 * カテゴリは、出力レベルの次に &lt;&gt; で囲まれて出力される。
	 * このメソッドでは、{@link String#format(String, Object...)} によって書式付き文字列を展開する。
	 * @param category	出力するカテゴリ
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see String#format(String, Object...)
	 */
	static public final void formatCategoryInfo(String category, String format, Object...args) {
		if (isInfo()) {
			s_outVerbose.println("[Info] <".concat(category==null ? "" : category).concat("> ").concat(String.format(format, args)));
		}
	}
	
	/**
	 * <code>debug</code> レベルのメッセージが出力可能な場合に、指定されたメッセージを出力し、行を終了する。
	 * 引数が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * @param message	出力するメッセージ
	 */
	static public final void debug(String message) {
		if (isDebug()) {
			s_outVerbose.println("[Debug] ".concat(message==null ? "null" : message));
		}
	}
	
	/**
	 * <code>debug</code> レベルのメッセージが出力可能な場合に、指定されたカテゴリとメッセージを出力し、行を終了する。
	 * カテゴリは、出力レベルの次に &lt;&gt; で囲まれて出力される。
	 * <em>message</em> が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * @param category	出力するカテゴリ
	 * @param message	出力するメッセージ
	 */
	static public final void categoryDebug(String category, String message) {
		if (isInfo()) {
			s_outVerbose.println("[Debug] <".concat(category==null ? "" : category).concat("> ").concat(message==null ? "null" : message));
		}
	}
	
	/**
	 * <code>debug</code> レベルのメッセージが出力可能な場合に、指定された書式付き文字列を出力し、行を終了する。
	 * このメソッドでは、{@link String#format(String, Object...)} によって書式付き文字列を展開する。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see String#format(String, Object...)
	 */
	static public final void formatDebug(String format, Object...args) {
		if (isDebug()) {
			s_outVerbose.println("[Debug] ".concat(String.format(format, args)));
		}
	}
	
	/**
	 * <code>debug</code> レベルのメッセージが出力可能な場合に、指定されたカテゴリと書式付き文字列を出力し、行を終了する。
	 * カテゴリは、出力レベルの次に &lt;&gt; で囲まれて出力される。
	 * このメソッドでは、{@link String#format(String, Object...)} によって書式付き文字列を展開する。
	 * @param category	出力するカテゴリ
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see String#format(String, Object...)
	 */
	static public final void formatCategoryDebug(String category, String format, Object...args) {
		if (isInfo()) {
			s_outVerbose.println("[Debug] <".concat(category==null ? "" : category).concat("> ").concat(String.format(format, args)));
		}
	}
	
	/**
	 * <code>trace</code> レベルのメッセージが出力可能な場合に、指定されたメッセージを出力し、行を終了する。
	 * 引数が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * @param message	出力するメッセージ
	 */
	static public final void trace(String message) {
		if (isTrace()) {
			s_outVerbose.println("[Trace] ".concat(message==null ? "null" : message));
		}
	}
	
	/**
	 * <code>trace</code> レベルのメッセージが出力可能な場合に、指定されたカテゴリとメッセージを出力し、行を終了する。
	 * カテゴリは、出力レベルの次に &lt;&gt; で囲まれて出力される。
	 * <em>message</em> が <tt>null</tt> の場合は、文字列「null」が出力される。
	 * @param category	出力するカテゴリ
	 * @param message	出力するメッセージ
	 */
	static public final void categoryTrace(String category, String message) {
		if (isInfo()) {
			s_outVerbose.println("[Trace] <".concat(category==null ? "" : category).concat("> ").concat(message==null ? "null" : message));
		}
	}
	
	/**
	 * <code>trace</code> レベルのメッセージが出力可能な場合に、指定された書式付き文字列を出力し、行を終了する。
	 * このメソッドでは、{@link String#format(String, Object...)} によって書式付き文字列を展開する。
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see String#format(String, Object...)
	 */
	static public final void formatTrace(String format, Object...args) {
		if (isTrace()) {
			s_outVerbose.println("[Trace] ".concat(String.format(format, args)));
		}
	}
	
	/**
	 * <code>trace</code> レベルのメッセージが出力可能な場合に、指定されたカテゴリと書式付き文字列を出力し、行を終了する。
	 * カテゴリは、出力レベルの次に &lt;&gt; で囲まれて出力される。
	 * このメソッドでは、{@link String#format(String, Object...)} によって書式付き文字列を展開する。
	 * @param category	出力するカテゴリ
	 * @param format	書式文字列
	 * @param args		引数
	 * @throws NullPointerException	<em>format</em> が <tt>null</tt> の場合
	 * @see String#format(String, Object...)
	 */
	static public final void formatCategoryTrace(String category, String format, Object...args) {
		if (isInfo()) {
			s_outVerbose.println("[Trace] <".concat(category==null ? "" : category).concat("> ").concat(String.format(format, args)));
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
