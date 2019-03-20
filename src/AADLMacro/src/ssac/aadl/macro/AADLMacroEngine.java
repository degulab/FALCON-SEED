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
 * @(#)AADLMacroEngine.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMacroEngine.java	2.0.0	2014/03/23 : modified and move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMacroEngine.java	1.11	2010/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMacroEngine.java	1.10	2009/12/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMacroEngine.java	1.00	2008/12/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ssac.aadl.macro.util.Classes;
import ssac.aadl.macro.util.Strings;
import ssac.aadl.macro.util.io.ReportPrinter;
import ssac.aadl.macro.util.io.ReportStream;
import ssac.aadl.macro.util.io.ReportWriter;

/**
 * AADLマクロ実行エンジン。
 * 
 * @version 2.1.0	2014/05/29
 * @since 1.00
 */
public class AADLMacroEngine
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 出力用ヘッダ文字列
	 */
	static public enum PrintHeader {
		NONE(""),
		INFO("INFO : "),
		WARN("WARNING : "),
		ERROR("ERROR : "),
		DEBUG("DEBUG : ");
		
		private final String header;
		private PrintHeader(String str) {
			header = str;
		}
		public String toString() {
			return header;
		}
	}

	/**
	 * AADLマクロ実行正常時の終了コード
	 */
	static public final int EXITCODE_SUCCEEDED   = 0;
	/**
	 * AADLマクロ起動失敗時の終了コード。
	 * このコードは、起動引数不正、マクロファイルフォーマット不正など、
	 * AADLマクロ実行に障害が発生したときに報告される。
	 */
	static public final int EXITCODE_FATAL_ERROR = 255;
	/**
	 * AADLマクロ実行失敗時の終了コード。
	 * このコードは、AADLマクロ要素のプロセス起動に障害が発生したときに
	 * 報告される。
	 */
	static public final int EXITCODE_CANNOT_EXEC = 254;
	/**
	 * プロセス中断要求による処理中断時の終了コード。
	 * このコードは、AADLマクロ実行プロセスが外部からの中断要求に応じて
	 * 中断されたときに報告される。
	 */
	static public final int EXITCODE_TERMINATE_EXEC	= 99;

	/**
	 * AADLマクロ実行エンジンのバージョン番号
	 */
	static public final String VERSION = "2.1.0.20140529";
	/**
	 * AADLマクロ実行エンジンのバージョン情報
	 */
	static public final String VERSION_TEXT = "AADL Macro Engine version " + VERSION;

	/**
	 * AADL実行時のクラスパス環境変数名
	 */
	static public final String ENV_AADL_LIBPATH = "AADLCLASSPATH";

	/**
	 * マクロ定義用クラスパスの区切り文字。この区切り文字は固定文字であり、
	 * プラットフォームは考慮しない。
	 */
	static public final String MACRO_CLASSPATH_SEPARATOR = ";";

	/**
	 * 標準のマクロCSVファイル・エンコーディング
	 */
	static private final String DEF_CSV_ENCODING = "MS932";

	/**
	 * ログ出力時の時刻フォーマット
	 */
	static private final SimpleDateFormat dateFormatter = new SimpleDateFormat("'['yyyy/MM/dd HH:mm:ss.SSS'] '");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * AADL実行時の環境変数クラスパスリスト(絶対パス)
	 */
	static private List<String> aadlLibPathList = null;
	/**
	 * AADL実行時の環境変数クラスパス(絶対パス)
	 */
	static private String aadlLibPathString = null;
	/**
	 * このクラスを実行中のJAVAコマンド
	 */
	static private String       defJavaCommand  = null;
	/**
	 * AADLマクロ実行エンジンのクラスパス
	 */
	static private String       macroEnginePath = null;

	/**
	 * 標準出力用プリンタ
	 */
	private final ReportPrinter out;
	/**
	 * エラー出力用プリンタ
	 */
	private final ReportPrinter err;

	/**
	 * 実行時のコマンドライン引数
	 */
	private final CommandLineParser cmdLine = new CommandLineParser();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * <code>AADLMacroEngine</code> の新しいインスタンスを生成する。
	 * このメソッドは、出力用プリンタに {@link java.lang.System#out}、{@link java.lang.System#err} を適用する。
	 */
	public AADLMacroEngine() {
		this((ReportPrinter)null, (ReportPrinter)null);
	}

	/**
	 * <code>AADLMacroEngine</code> の新しいインスタンスを生成する。
	 * このメソッドは、出力用プリンタに、指定された <code>outputStream</code> を適用する。
	 * 
	 * @param outputStream	標準ならびにエラー出力先となるストリーム
	 */
	public AADLMacroEngine(PrintStream outputStream) {
		this(outputStream != null ? new ReportStream(outputStream) : null);
	}

	/**
	 * <code>AADLMacroEngine</code> の新しいインスタンスを生成する。
	 * このメソッドは、出力用プリンタに、指定された <code>outputStream</code>、<code>errorStream</code> を適用する。
	 * 
	 * @param outputStream	標準出力先となるストリーム
	 * @param errorStream	エラー出力先となるストリーム
	 */
	public AADLMacroEngine(PrintStream outputStream, PrintStream errorStream) {
		this(outputStream != null ? new ReportStream(outputStream) : null, errorStream != null ? new ReportStream(errorStream) : null);
	}

	/**
	 * <code>AADLMacroEngine</code> の新しいインスタンスを生成する。
	 * このメソッドは、出力用プリンタに、指定された <code>outputWriter</code> を適用する。
	 * 
	 * @param outputWriter	標準ならびにエラー出力先となる <code>Writer</code> オブジェクト
	 */
	public AADLMacroEngine(PrintWriter outputWriter) {
		this(outputWriter != null ? new ReportWriter(outputWriter) : null);
	}

	/**
	 * <code>AADLMacroEngine</code> の新しいインスタンスを生成する。
	 * このメソッドは、出力用プリンタに、指定された <code>outputWriter</code>、<code>errorWriter</code> を適用する。
	 * 
	 * @param outputWriter	標準出力先となる <code>Writer</code> オブジェクト
	 * @param errorWriter	エラー出力先となる <code>Writer</code> オブジェクト
	 */
	public AADLMacroEngine(PrintWriter outputWriter, PrintWriter errorWriter) {
		this(outputWriter != null ? new ReportWriter(outputWriter) : null, errorWriter != null ? new ReportWriter(errorWriter) : null);
	}

	/**
	 * <code>AADLMacroEngine</code> の新しいインスタンスを生成する。
	 * このメソッドは、出力用プリンタに、指定された <code>outputPrinter</code> を使用する。
	 * 
	 * @param outputPrinter	標準ならびにエラー出力用プリンタ
	 */
	protected AADLMacroEngine(ReportPrinter outputPrinter) {
		this(outputPrinter, outputPrinter);
	}

	/**
	 * <code>AADLMacroEngine</code> の新しいインスタンスを生成する。
	 * このメソッドは、出力用プリンタに、指定された <code>outputPrinter</code>、<code>errorPrinter</code> を使用する。
	 * 
	 * @param outputPrinter	標準出力用プリンタ
	 * @param errorPrinter	エラー出力用プリンタ
	 */
	protected AADLMacroEngine(ReportPrinter outputPrinter, ReportPrinter errorPrinter) {
		//--- 標準出力
		if (outputPrinter == null)
			this.out = new ReportStream(System.out);
		else
			this.out = outputPrinter;
		//--- エラー出力
		if (errorPrinter == null)
			this.err = new ReportStream(System.err);
		else
			this.err = errorPrinter;
	}

	//------------------------------------------------------------
	// Main entry
	//------------------------------------------------------------
	
	/**
	 * 指定されたAADLマクロを実行する。
	 * このメソッドは、呼び出し元プロセスでマクロ実行を制御するため、
	 * シャットダウンフックは実行されない。
	 * <br>
	 * このメソッドは、このマクロ実行エンジンのクラスファイルが配置されている場所、もしくはその親ディレクトリに
	 * &quot;aadlrt&quot; ディレクトリが存在する場合、そのディレクトリ内の全てのJarファイル(拡張子が &quot;.jar&quot;)を
	 * マクロ実行時のクラスパスに追加する。
	 * デフォルトのファイルエンコーディングは、全て &quot;MS932&quot; とする。
	 * <br>
	 * <b>(注)</b>
	 * <blockquote>
	 * マクロ内で実行されるマクロコマンドは、別プロセスで実行される。
	 * </blockquote>
	 * 
	 * @param memSize			コマンド実行時のヒープサイズをMB単位で指定
	 * @param macroFilename		実行するマクロ定義ファイルのパス名
	 * @return	マクロ実行エンジンの終了コードを返す。正常終了した場合は 0 を返す。
	 * 
	 * @throws NullPointerException	<em>macroFilename</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>macroFilename</em> が存在しないか、ファイルではない場合
	 * 
	 * @since 1.11
	 */
	public static int exec(String memSize, String macroFilename) {
		return exec(memSize, macroFilename, null);
	}

	/**
	 * 指定されたAADLマクロを実行する。
	 * このメソッドは、呼び出し元プロセスでマクロ実行を制御するため、
	 * シャットダウンフックは実行されない。
	 * <br>
	 * このメソッドは、このマクロ実行エンジンのクラスファイルが配置されている場所、もしくはその親ディレクトリに
	 * &quot;aadlrt&quot; ディレクトリが存在する場合、そのディレクトリ内の全てのJarファイル(拡張子が &quot;.jar&quot;)を
	 * マクロ実行時のクラスパスに追加する。
	 * デフォルトのファイルエンコーディングは、全て &quot;MS932&quot; とする。
	 * また、このメソッドではデバッグオプションを付加して実行する。
	 * <br>
	 * <b>(注)</b>
	 * <blockquote>
	 * マクロ内で実行されるマクロコマンドは、別プロセスで実行される。
	 * </blockquote>
	 * 
	 * @param memSize			コマンド実行時のヒープサイズをMB単位で指定
	 * @param macroFilename		実行するマクロ定義ファイルのパス名
	 * @return	マクロ実行エンジンの終了コードを返す。正常終了した場合は 0 を返す。
	 * 
	 * @throws NullPointerException	<em>macroFilename</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>macroFilename</em> が存在しないか、ファイルではない場合
	 * 
	 * @since 1.11
	 */
	public static int execForDebug(String memSize, String macroFilename) {
		return execForDebug(memSize, macroFilename, null);
	}

	/**
	 * 指定されたAADLマクロを実行する。
	 * このメソッドは、呼び出し元プロセスでマクロ実行を制御するため、
	 * シャットダウンフックは実行されない。
	 * <br>
	 * このメソッドは、このマクロ実行エンジンのクラスファイルが配置されている場所、もしくはその親ディレクトリに
	 * &quot;aadlrt&quot; ディレクトリが存在する場合、そのディレクトリ内の全てのJarファイル(拡張子が &quot;.jar&quot;)を
	 * マクロ実行時のクラスパスに追加する。
	 * デフォルトのファイルエンコーディングは、全て &quot;MS932&quot; とする。
	 * <br>
	 * <b>(注)</b>
	 * <blockquote>
	 * マクロ内で実行されるマクロコマンドは、別プロセスで実行される。
	 * </blockquote>
	 * 
	 * @param memSize			コマンド実行時のヒープサイズをMB単位で指定
	 * @param macroFilename		実行するマクロ定義ファイルのパス名
	 * @param macroArgs			マクロの実行時引数となる文字列のリスト
	 * @return	マクロ実行エンジンの終了コードを返す。正常終了した場合は 0 を返す。
	 * 
	 * @throws NullPointerException	<em>macroFilename</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>macroFilename</em> が存在しないか、ファイルではない場合
	 * 
	 * @since 1.11
	 */
	public static int exec(String memSize, String macroFilename, List<? extends String> macroArgs) {
		// ライブラリ・パスの収集
		String libPaths = collectLibPaths();
		
		// マクロ実行(デバッグオプション)
		return exec(memSize, libPaths, "MS932", "MS932", "MS932", null, macroFilename, macroArgs);
	}

	/**
	 * 指定されたAADLマクロを実行する。
	 * このメソッドは、呼び出し元プロセスでマクロ実行を制御するため、
	 * シャットダウンフックは実行されない。
	 * <br>
	 * このメソッドは、このマクロ実行エンジンのクラスファイルが配置されている場所、もしくはその親ディレクトリに
	 * &quot;aadlrt&quot; ディレクトリが存在する場合、そのディレクトリ内の全てのJarファイル(拡張子が &quot;.jar&quot;)を
	 * マクロ実行時のクラスパスに追加する。
	 * デフォルトのファイルエンコーディングは、全て &quot;MS932&quot; とする。
	 * また、このメソッドではデバッグオプションを付加して実行する。
	 * <br>
	 * <b>(注)</b>
	 * <blockquote>
	 * マクロ内で実行されるマクロコマンドは、別プロセスで実行される。
	 * </blockquote>
	 * 
	 * @param memSize			コマンド実行時のヒープサイズをMB単位で指定
	 * @param macroFilename		実行するマクロ定義ファイルのパス名
	 * @param macroArgs			マクロの実行時引数となる文字列のリスト
	 * @return	マクロ実行エンジンの終了コードを返す。正常終了した場合は 0 を返す。
	 * 
	 * @throws NullPointerException	<em>macroFilename</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>macroFilename</em> が存在しないか、ファイルではない場合
	 * 
	 * @since 1.11
	 */
	public static int execForDebug(String memSize, String macroFilename, List<? extends String> macroArgs) {
		// ライブラリ・パスの収集
		String libPaths = collectLibPaths();
		
		// マクロ実行(デバッグオプション)
		return exec(memSize, libPaths, "MS932", "MS932", "MS932", "-verbose -debug", macroFilename, macroArgs);
	}

	/**
	 * 指定されたAADLマクロを実行する。
	 * このメソッドは、呼び出し元プロセスでマクロ実行を制御するため、
	 * シャットダウンフックは実行されない。
	 * <br>
	 * <b>(注)</b>
	 * <blockquote>
	 * マクロ内で実行されるマクロコマンドは、別プロセスで実行される。
	 * </blockquote>
	 * 
	 * @param memSize			コマンド実行時のヒープサイズをMB単位で指定
	 * @param libPaths			コマンド実行時に適用されるライブラリのパス(クラスパス)を指定する。
	 * 							複数のパスを指定する場合はセミコロンで区切る。
	 * @param macroEncoding		マクロ定義ファイル読み込み時に適用するエンコーディングとする文字セット名を指定する。
	 * 							<tt>null</tt> もしくは空文字列の場合は、プラットフォーム標準のエンコーディングが適用される。
	 * @param csvEncoding		マクロ実行時のCSVファイル入出力に適用するエンコーディングとする文字セット名を指定する。
	 * 							<tt>null</tt> もしくは空文字列の場合は、プラットフォーム標準のエンコーディングが適用される。
	 * @param txtEncoding		マクロ実行時のテキストファイル入出力に適用するエンコーディングとする文字セット名を指定する。
	 * 							<tt>null</tt> もしくは空文字列の場合は、プラットフォーム標準のエンコーディングが適用される。
	 * @param anotherOptions	マクロ実行エンジンの複数のオプションを空白区切りで指定する。
	 * 							オプションの値に空白を含む場合は、ダブルクオーテーションで
	 * 							囲むこと。
	 * @param macroFilename		実行するマクロ定義ファイルのパス名
	 * @param macroArgs			マクロの実行時引数となる文字列のリスト
	 * @return	マクロ実行エンジンの終了コードを返す。正常終了した場合は 0 を返す。
	 * 
	 * @throws NullPointerException	<em>macroFilename</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>macroFilename</em> が存在しないか、ファイルではない場合
	 * 
	 * @since 1.11
	 */
	public static int exec(String memSize, String libPaths, String macroEncoding, String csvEncoding, String txtEncoding,
							 String anotherOptions, String macroFilename, List<? extends String> macroArgs)
	{
		ArrayList<String> cmdlist = new ArrayList<String>();
		
		// メモリサイズの解析
		if (memSize != null && memSize.length() > 0) {
			//--- 数字として適正かは、ここではチェックしない
			cmdlist.add("-heapmax");
			cmdlist.add(memSize + "m");
		}
		
		// ライブラリのパス
		if (libPaths != null && libPaths.length() > 0) {
			//--- パスが適正かは、ここではチェックしない
			cmdlist.add("-libpath");
			cmdlist.add(libPaths);
		}
		
		// マクロ定義ファイル・エンコーディング
		if (!Strings.isNullOrEmpty(macroEncoding)) {
			cmdlist.add("-macroencoding");
			cmdlist.add(macroEncoding);
		}
		
		// CSVファイル・エンコーディング
		if (!Strings.isNullOrEmpty(csvEncoding)) {
			cmdlist.add("-csvencoding");
			cmdlist.add(csvEncoding);
		}
		
		// テキストファイル・エンコーディング
		if (!Strings.isNullOrEmpty(txtEncoding)) {
			cmdlist.add("-txtencoding");
			cmdlist.add(txtEncoding);
		}
		
		// オプションの解析
		if (anotherOptions != null && anotherOptions.length() > 0) {
			String[] macroOptions = Strings.splitCommandLineWithDoubleQuoteEscape(anotherOptions);
			if (macroOptions != null && macroOptions.length > 0) {
				for (String arg : macroOptions) {
					cmdlist.add(arg);
				}
			}
		}
		
		// マクロファイル名のチェック
		if (macroFilename == null)
			throw new NullPointerException("AADL macro filename is null.");
		File macrofile = new File(macroFilename);
		if (!macrofile.exists()) {
			throw new IllegalArgumentException("AADL macro file not found : \"" + macrofile.getAbsolutePath() + "\"");
		}
		else if (!macrofile.isFile()) {
			throw new IllegalArgumentException("AADL macro file is not a file : \"" + macrofile.getAbsolutePath() + "\"");
		}
		cmdlist.add(macrofile.getAbsolutePath());
		
		// 実行時引数の収集
		if (macroArgs != null && !macroArgs.isEmpty()) {
			for (String arg : macroArgs) {
				cmdlist.add(arg);
			}
		}
		
		// マクロ実行
		//--- インスタンス生成
		AADLMacroEngine engine = new AADLMacroEngine();
		//--- 実行
		int retCode;
		try {
			retCode = engine.execMacro(cmdlist.toArray(new String[cmdlist.size()]));
		}
		catch (Throwable ex) {
			engine.printError(ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : ex.toString());
			engine.err.printStackTrace(ex);
			retCode = EXITCODE_FATAL_ERROR;
		}
		
		// 終了
		return retCode;
	}

	/**
	 * AADLマクロ実行エンジンのエントリ・メソッド
	 * 
	 * @param args	AADLマクロ実行エンジンの起動引数
	 */
	public static void main(String[] args) {
		//--- add shutdown hook
		MacroShutdownHook.initShutdownHook();
		//--- インスタンス生成
		AADLMacroEngine engine = new AADLMacroEngine();
		//--- 実行
		int retCode;
		try {
			retCode = engine.execMacro(args);
		}
		catch (Throwable ex) {
			engine.printError(ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : ex.toString());
			engine.err.printStackTrace(ex);
			retCode = EXITCODE_FATAL_ERROR;
		}
		//--- 終了
		engine.out.println();
		System.exit(retCode);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された引数により、AADLマクロを実行する。
	 * 
	 * @param args	AADLマクロ実行エンジンの起動引数
	 * @return 終了コード
	 */
	public int execMacro(String[] args) {
		// コマンドライン解析
		try {
			cmdLine.parse(args);
		}
		catch (IllegalArgumentException ex) {
			//--- バージョン情報を表示
			if (cmdLine.isVersion()) {
				CommandLineParser.showVersion(out);
			}
			printError(ex.getLocalizedMessage());
			return EXITCODE_FATAL_ERROR;
		}
		
		// デバッグ表示
		if (cmdLine.isDebug()) {
			StringBuilder sb = new StringBuilder();
			if (args != null && args.length > 0) {
				sb.append("AADLMacroEngine command-line arguments(" + args.length + "):\n");
				for (int i = 0; i < args.length; i++) {
					sb.append("    [");
					sb.append(i);
					sb.append("] ");
					sb.append(args[i]);
					sb.append("\n");
				}
			} else {
				sb.append("AADLMacroEngine command-line arguments: none.\n");
			}
			printDebug(sb.toString());
		}
		
		// バージョン情報表示
		if (cmdLine.isVersion()) {
			CommandLineParser.showVersion(out);
		}
		
		// ヘルプ表示
		if (cmdLine.isHelp()) {
			CommandLineParser.showHelp(out);
			//--- ヘルプは表示してプログラム終了
			return EXITCODE_SUCCEEDED;
		}
		
		// マクロファイル取得
		File sourceFile = new File(cmdLine.getSourceFilename()).getAbsoluteFile();
		try {
			sourceFile = sourceFile.getCanonicalFile();
		} catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		
		// マクロ実行
		MacroSequencer sequencer = new MacroSequencer(this, sourceFile);
		//--- add sequencer to shutdown hook
		MacroShutdownHook.setVerbose(cmdLine.isDebug());
		MacroShutdownHook.addSequencer(sequencer);
		return sequencer.play();
	}

	/**
	 * AADLマクロ実行エンジン起動時のコマンドライン情報を取得する。
	 * 
	 * @return <code>CommandLineParser</code> オブジェクト
	 */
	public CommandLineParser getCommandLine() {
		return cmdLine;
	}

	/**
	 * AADLマクロ実行時引数の総数を返す。
	 * @return	実行時引数の総数
	 */
	public int getNumMacroArguments() {
		return cmdLine.getMacroArguments().size();
	}

	/**
	 * AADLマクロ実行時引数を取得する。
	 * <em>argID</em> に対応する引数が存在しない場合は <tt>null</tt> を返す。
	 * @param argID	取得する引数の位置を表す ID を指定する。
	 * 				この ID は 1 から始まる数値であり、ID=1 は インデックス=0 に
	 * 				対応する。
	 * @return	<em>argID</em> に対応する引数を返す。
	 * 			<em>argID</em> に対応する引数が存在しない場合は <tt>null</tt> を返す。
	 */
	public String getMacroArgument(int argID) {
		List<String> arglist = cmdLine.getMacroArguments();
		if (arglist.isEmpty())
			return null;
		if (argID < 1 || argID > arglist.size())
			return null;
		return arglist.get(argID-1);
	}

	/**
	 * 標準出力用プリンタを取得する。
	 * @return	標準出力用プリンタ
	 */
	public final ReportPrinter getOutputPrinter() {
		return out;
	}

	/**
	 * エラー出力用プリンタを取得する。
	 * @return	エラー出力用プリンタ
	 */
	public final ReportPrinter getErrorPrinter() {
		return err;
	}

	/**
	 * デバッグモードフラグを取得する。
	 * 
	 * @return	デバッグモードなら <tt>true</tt> を返す。
	 */
	public boolean isDebugMode() {
		return (cmdLine != null ? cmdLine.isDebug() : false);
	}

	/**
	 * 詳細表示モードフラグを取得する。
	 * 
	 * @return	詳細表示モードなら <tt>true</tt> を返す。
	 */
	public boolean isVerboseMode() {
		return (cmdLine != null ? cmdLine.isVerbose() : false);
	}

	/**
	 * AADLマクロファイルの有効なエンコーディングを取得する。
	 * 
	 * @return マクロファイルエンコーディングとなる文字セット名
	 */
	public String getAvailableMacroEncoding() {
		if (cmdLine != null && cmdLine.hasMacroEncoding())
			return cmdLine.getMacroEncoding();
		else
			return getDefaultMacroEncoding();
	}

	/**
	 * AADLマクロファイルの標準エンコーディングを取得する。
	 * 現時点では、このメソッドが返す値は &quot;MS932&quot; となる。
	 * 
	 * @return &quot;MS932&quo; を返す。
	 */
	static public final synchronized String getDefaultMacroEncoding() {
		return DEF_CSV_ENCODING;
	}

	/**
	 * AADLモジュール実行に必要なライブラリパス(クラスパス)を取得する。
	 * AADLマクロ実行エンジン引数にパスが指定されている場合は、AADLマクロ
	 * 実行エンジン引数に指定されたパスを返す。
	 * AADLマクロ実行エンジン引数にパスが指定されていない場合、環境変数に
	 * 設定されたパスを返す。どちらにも設定されていない場合は、空文字列を返す。
	 * このメソッドが返すクラスパスは絶対パスとプラットフォーム固有のパス区切り
	 * 文字に正規化されている。
	 * 
	 * @return	有効なクラスパス文字列
	 */
	public String getAvailableLibPath() {
		if (cmdLine != null && cmdLine.hasLibClassPath())
			return cmdLine.getLibClassPath();
		else
			return getDefaultLibPathString();
	}

	/**
	 * AADL実行時の標準クラスパスを取得する。
	 * このメソッドは、環境変数&quot;AADLCLASSPATH&quot; の値を取得し、
	 * 絶対パスのクラスパス文字列リストとして返す。定義されていない場合は、
	 * 要素が空のリストを返す。
	 * 
	 * @return クラスパス文字列リスト
	 */
	static public final synchronized List<String> getDefaultLibPathList() {
		if (aadlLibPathList == null) {
			String strClassPath = System.getenv(ENV_AADL_LIBPATH);
			if (strClassPath != null && strClassPath.length() > 0) {
				aadlLibPathList = Classes.toAbsoluteClassPathList(strClassPath, MACRO_CLASSPATH_SEPARATOR);
			} else {
				aadlLibPathList = Collections.emptyList();
			}
		}
		return aadlLibPathList;
	}

	/**
	 * AADL実行時の標準クラスパスを取得する。
	 * このメソッドは、環境変数&quot;AADLCLASSPATH&quot; の値を取得し、
	 * 絶対パスの適切なクラスパス文字列として返す。クラスパスの区切り文字は、
	 * プラットフォーム固有の区切り文字を使用する。定義されていない場合は、
	 * 空文字列を返す。
	 * 
	 * @return	クラスパス文字列
	 */
	static public final synchronized String getDefaultLibPathString() {
		if (aadlLibPathString == null) {
			aadlLibPathString = Classes.toClassPathString(null, getDefaultLibPathList());
		}
		return aadlLibPathString;
	}

	/**
	 * JAVAコマンドのパスを取得する。このパスは、基本的にこのプログラムが
	 * 実行されたJavaコマンドを取得する。
	 * 
	 * @return Javaコマンドのパス
	 */
	static public final synchronized String getJavaCommandPath() {
		if (defJavaCommand == null) {
			// プロパティ取得
			String path = System.getProperty("java.home");
			if (path != null && path.length() > 0) {
				File cmdPath = new File(path, "bin");
				File cmdFile;
				if (Strings.startsWithIgnoreCase(System.getProperty("os.name"), "Windows"))
					cmdFile = new File(cmdPath, "java.exe");
				else
					cmdFile = new File(cmdPath, "java");
				if (cmdFile.exists() && cmdFile.isFile()) {
					defJavaCommand = cmdFile.getAbsolutePath();
				}
			}
			// 取得できない場合は、デフォルトのコマンド文字列を設定
			if (defJavaCommand == null) {
				defJavaCommand = "java";
			}
		}
		return defJavaCommand;
	}

	/**
	 * AADLマクロ実行エンジンのクラスパスを取得する。
	 * 
	 * @return AADLマクロ実行エンジンのクラスパス
	 */
	static public final synchronized String getMacroEnginePath() {
		if (macroEnginePath == null) {
			File file = Classes.getClassSource(AADLMacroEngine.class);
			macroEnginePath = file.getAbsolutePath();
		}
		return macroEnginePath;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private static void collectJarFilePaths(File pathname, FileFilter filter, List<File> jarFiles) {
		File[] files = pathname.listFiles(filter);
		if (files != null && files.length > 0) {
			for (File f : files) {
				if (f.isDirectory()) {
					collectJarFilePaths(f, filter, jarFiles);
				}
				else if (f.isFile()) {
					jarFiles.add(f);
				}
			}
		}
	}
	
	private static String collectLibPaths() {
		final String libdir = "aadlrt";
		
		// ライブラリのパスを収集
		String libPaths = null;
		File thisClassPath = null;
		try {
			thisClassPath = Classes.getClassSource(AADLMacroEngine.class);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			thisClassPath = null;
		}
		if (thisClassPath != null) {
			// 親フォルダ内を検索
			File targetDir = null;
			File dir = thisClassPath.getParentFile();
			if (dir != null) {
				File path = new File(dir, libdir);
				if (path.exists() && path.isDirectory()) {
					targetDir = path;
				}
				else if ((dir = dir.getParentFile()) != null) {
					path = new File(dir, libdir);
					if (path.exists() && path.isDirectory()) {
						targetDir = path;
					}
				}
			}
			if (targetDir != null) {
				// フォルダ内の jar ファイルを収集
				ArrayList<File> jarfiles = new ArrayList<File>();
				final File ignoreFile = thisClassPath;
				FileFilter filter = new FileFilter(){
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) {
							return true;
						}
						else if (pathname.isFile() && Strings.endsWithIgnoreCase(pathname.getName(), ".jar")) {
							if (!pathname.equals(ignoreFile)) {
								return true;
							}
						}
						return false;
					}
				};
				collectJarFilePaths(targetDir, filter, jarfiles);
				if (!jarfiles.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					Iterator<File> it = jarfiles.iterator();
					sb.append(it.next().getAbsolutePath());
					while (it.hasNext()) {
						sb.append(";");
						sb.append(it.next().getAbsolutePath());
					}
					libPaths = sb.toString();
				}
			}
		}
		
		return libPaths;
	}

	/**
	 * 現在時刻の文字列表現を取得する。
	 * 
	 * @return フォーマットされた現在時刻を表す文字列
	 */
	static protected final String getCurrentTimeString() {
		return dateFormatter.format(new Date());
	}
	
	static protected void msgPrint(final ReportPrinter printer, final PrintHeader header, String message) {
		printer.print(header.toString() + message);
	}
	
	static protected void msgPrint(final ReportPrinter printer, final PrintHeader header, String format, Object...args) {
		printer.print(header.toString() + String.format(format, args));
	}
	
	static protected void msgPrintln(final ReportPrinter printer, final PrintHeader header, String message) {
		printer.println(header.toString() + message);
	}
	
	static protected void msgPrintln(final ReportPrinter printer, final PrintHeader header, String format, Object...args) {
		printer.println(header.toString() + String.format(format, args));
	}
	
	static protected void logPrint(final ReportPrinter printer, final PrintHeader header, String message) {
		printer.print(header.toString() + getCurrentTimeString() + message);
	}
	
	static protected void logPrint(final ReportPrinter printer, final PrintHeader header, String format, Object...args) {
		printer.print(header.toString() + getCurrentTimeString() + String.format(format, args));
	}
	
	static protected void logPrintln(final ReportPrinter printer, final PrintHeader header, String message) {
		printer.println(header.toString() + getCurrentTimeString() + message);
	}
	
	static protected void logPrintln(final ReportPrinter printer, final PrintHeader header, String format, Object...args) {
		printer.println(header.toString() + getCurrentTimeString() + String.format(format, args));
	}
	
	private void printLog(final ReportPrinter printer, final PrintHeader header, String message) {
		if (cmdLine.isNoTime())
			msgPrintln(printer, header, message);
		else
			logPrintln(printer, header, message);
	}
	
	private void printLog(final ReportPrinter printer, final PrintHeader header, String format, Object...args) {
		if (cmdLine.isNoTime())
			msgPrintln(printer, header, format, args);
		else
			logPrintln(printer, header, format, args);
	}
	
	protected void printMessage(String message) {
		printLog(out, PrintHeader.NONE, message);
	}
	
	protected void printMessage(String format, Object...args) {
		printLog(out, PrintHeader.NONE, format, args);
	}
	
	protected void printDebug(String message) {
		if (cmdLine.isDebug())
			printLog(err, PrintHeader.DEBUG, message);
	}
	
	protected void printDebug(String format, Object...args) {
		if (cmdLine.isDebug())
			printLog(err, PrintHeader.DEBUG, format, args);
	}
	
	protected void printTrace(String message) {
		if (cmdLine.isVerbose())
			printLog(out, PrintHeader.INFO, message);
	}
	
	protected void printTrace(String format, Object...args) {
		if (cmdLine.isVerbose())
			printLog(out, PrintHeader.INFO, format, args);
	}
	
	protected void printWarn(String message) {
		if (!cmdLine.isNoWarn())
			printLog(err, PrintHeader.WARN, message);
	}
	
	protected void printWarn(String format, Object...args) {
		if (!cmdLine.isNoWarn())
			printLog(err, PrintHeader.WARN, format, args);
	}
	
	protected void printError(String message) {
		printLog(err, PrintHeader.ERROR, message);
	}
	
	protected void printError(String format, Object...args) {
		printLog(err, PrintHeader.ERROR, format, args);
	}
	
	protected void printStackTrace(Throwable ex) {
		if (cmdLine.isDebug())
			err.printStackTrace(ex);
	}
}
