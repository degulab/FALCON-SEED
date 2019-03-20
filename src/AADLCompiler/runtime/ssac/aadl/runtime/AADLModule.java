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
 * @(#)AADLModule.java	2.2.0	2015/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLModule.java	2.1.0	2014/05/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLModule.java	1.90	2013/08/27 - bug fix
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLModule.java	1.70	2011/07/12 - bug fix
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLModule.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.io.File;
import java.io.InterruptedIOException;

import ssac.aadl.runtime.io.CsvFileReader;
import ssac.aadl.runtime.io.CsvFileWriter;
import ssac.aadl.runtime.io.TextFileReader;
import ssac.aadl.runtime.io.TextFileWriter;
import ssac.aadl.runtime.io.internal.AbTextFileReader;
import ssac.aadl.runtime.io.internal.AbTextFileWriter;
import ssac.aadl.runtime.io.internal.StreamManager;

/**
 * AADLモジュールの基本クラス
 * 
 * @version 2.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public abstract class AADLModule extends AADLFunctions
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String ATTR_AGGRE = "aggre";
	static protected final String ATTR_HAT = "hat";
	static protected final String ATTR_RATIO = "ratio";
	static protected final String ATTR_MULTIPLY = "multiply";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ファイルストリームを管理するオブジェクト **/
	protected final StreamManager _aadl$stream$manager = new StreamManager();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLModule() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	// 有効なAADL行番号を持つ例外を検索する
	protected Throwable _aadl$findExceptionWithAadlLocation(String targetClassName, Throwable ex) {
		Throwable exTarget = ex;
		do {
			StackTraceElement[] traceElements = exTarget.getStackTrace();
			for (StackTraceElement elem : traceElements) {
				if (elem.getClassName().startsWith(targetClassName)) {
					int alno = _aadl$getAADLLineNumber(elem.getLineNumber());
					if (alno > 0) {
						//--- AADL行番号を持つ例外を発見
						return exTarget;
					}
				}
			}
			//--- 次の例外要因を取得
			exTarget = exTarget.getCause();
		} while (exTarget != null);
		
		//--- AADL行番号を持つ例外は、なし
		return null;
	}

	// 例外要因も含めた中断通知メッセージを整形する
	protected String _aadl$fomatTerminationMessage(String targetClassName, Throwable ex) {
		int javaLineNo = -1;
		int aadlLineNo = -1;

		// AADL行番号が取得可能な例外発生位置の検索
		StackTraceElement[] traceElements = ex.getStackTrace();
		for (StackTraceElement elem : traceElements) {
			if (elem.getClassName().startsWith(targetClassName)) {
				int elemLineNo = elem.getLineNumber();
				if (javaLineNo < 0) {
					javaLineNo = elemLineNo;
				}
				int alno = _aadl$getAADLLineNumber(elemLineNo);
				if (alno > 0) {
					javaLineNo = elemLineNo;
					aadlLineNo = alno;
					break;
				}
			}
		}
		
		// 例外情報メッセージの整形
		String exClassName = ex.getClass().getName();
		String exMessage   = ex.getLocalizedMessage();
		StringBuilder sb = new StringBuilder();
		//--- 中断要求による中断処理
		if (!(ex instanceof AADLTerminatedException)) {
			// 例外が AADLTerminatedException 以外なら、中断メッセージに例外情報を付加
			if (exMessage != null && exMessage.length() > 0)
				exMessage = AADLTerminatedException.DEFAULT_MESSAGE + " (" + exMessage + ")";
			else
				exMessage = AADLTerminatedException.DEFAULT_MESSAGE + " (" + ex.toString() + ")";
		}
		sb.append("Terminated[");
		if (exMessage != null && exMessage.length() > 0) {
			// exMessage が空文字列でないなら、例外クラス名は不要
			exClassName = null;
		}
		if (aadlLineNo >= 0) {
			//--- AADL行番号特定
			sb.append(String.format("line:%d (java:%d)", aadlLineNo, javaLineNo));
		}
		else if (javaLineNo >= 0) {
			//--- JAVA行番号表示
			sb.append(String.format("(java:%d)", javaLineNo));
		}
		else {
			//--- 場所不明
			sb.append("Location unknown");
		}
		sb.append("] : ");
		if (exMessage != null) {
			//--- 詳細メッセージあり
			sb.append(exMessage);
			if (exClassName != null) {
				sb.append("(");
				sb.append(exClassName);
				sb.append(")");
			}
		} else if (exClassName != null) {
			//--- 詳細メッセージなし
			sb.append(exClassName);
		}
		
		return sb.toString();
	}

	// 例外要因も含めた例外通知メッセージを整形する
	protected String _aadl$fomatExceptionMessage(String targetClassName, Throwable ex) {
		int javaLineNo = -1;
		int aadlLineNo = -1;

		// AADL行番号が取得可能な例外発生位置の検索
		StackTraceElement[] traceElements = ex.getStackTrace();
		for (StackTraceElement elem : traceElements) {
			if (elem.getClassName().startsWith(targetClassName)) {
				int elemLineNo = elem.getLineNumber();
				if (javaLineNo < 0) {
					javaLineNo = elemLineNo;
				}
				int alno = _aadl$getAADLLineNumber(elemLineNo);
				if (alno > 0) {
					javaLineNo = elemLineNo;
					aadlLineNo = alno;
					break;
				}
			}
		}
		
		// 例外情報メッセージの整形
		String exClassName = ex.getClass().getName();
		String exMessage   = ex.getLocalizedMessage();
		StringBuilder sb = new StringBuilder();
		//--- 通常のエラー
		sb.append("Error[");
		if (aadlLineNo >= 0) {
			//--- AADL行番号特定
			sb.append(String.format("line:%d (java:%d)", aadlLineNo, javaLineNo));
		}
		else if (javaLineNo >= 0) {
			//--- JAVA行番号表示
			sb.append(String.format("(java:%d)", javaLineNo));
		}
		else {
			//--- 場所不明
			sb.append("Location unknown");
		}
		sb.append("] : ");
		if (exMessage != null) {
			//--- 詳細メッセージあり
			sb.append(exMessage);
			if (exClassName != null) {
				sb.append("(");
				sb.append(exClassName);
				sb.append(")");
			}
		} else if (exClassName != null) {
			//--- 詳細メッセージなし
			sb.append(exClassName);
		}
		
		return sb.toString();
	}

	/**
	 * AADL モジュールの実行でスローされた例外の情報を、標準エラー出力に出力する。
	 * @param ex	例外
	 */
	public final void aadlErrorReport(Throwable ex) {
		// 中断要求への応答判定
		boolean requestTerminated = false;
		if (ex instanceof AADLTerminatedException) {
			// 中断要求へ応答したときにスローされる例外
			requestTerminated = true;
		}
		else if ((ex instanceof InterruptedIOException) || (ex.getCause() instanceof InterruptedIOException)) {
			// IO処理への割り込み例外の場合、中断要求の有無を判定
			requestTerminated = acceptTerminateRequest();
		}
		else if ((ex instanceof InterruptedException) || (ex.getCause() instanceof InterruptedException)) {
			// このスレッドへの割り込み発生時、中断要求の有無を判定
			requestTerminated = acceptTerminateRequest();
		}
		
		// AADL行番号取得可能な例外の取得
		String thisClassName = this.getClass().getName();
		Throwable exTarget = _aadl$findExceptionWithAadlLocation(thisClassName, ex);
		//--- AADL行番号取得可能な例外が存在しない場合は、最初の例外をターゲットに設定
		if (exTarget == null) {
			exTarget = ex;
		}
		
		// 例外メッセージの取得
		String errmsg;
		if (requestTerminated) {
			// 中断メッセージの整形
			errmsg = _aadl$fomatTerminationMessage(thisClassName, exTarget);
		}
		else {
			// 例外メッセージの整形
			errmsg = _aadl$fomatExceptionMessage(thisClassName, exTarget);
			
			// 例外要因が指定されている場合は、要因のメッセージを取得
			Throwable exCause = exTarget.getCause();
			for (; exCause != null; ) {
				String causemsg = _aadl$fomatExceptionMessage(thisClassName, exCause);
				errmsg = errmsg + "\nCaused by: " + causemsg;
				//--- 次の例外要因を取得
				exCause = exCause.getCause();
			}
		}
		
		// 例外メッセージのエラー出力
		System.err.println(errmsg);
	}

	/**
	 * AADL モジュールのインスタンスで保持されているリソースを解放します。
	 * 
	 * @since 1.70
	 */
	public void dispose() {
		_aadl$stream$manager.cleanup();
	}

	/**
	 * AADL モジュールのインスタンスが解放される前に、
	 * このクラスが管理するリソースのみを解放します。
	 */
	@Override
	protected void finalize() throws Throwable {
		_aadl$stream$manager.cleanup();
		super.finalize();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * AADLモジュールのエントリメンバメソッド。
	 * このエントリメソッドは、AADLモジュールの実行に必要な引数の数をチェックしません。
	 * 引数の数が AADLプログラム引数番号の最大値よりも小さい場合、不足した引数には
	 * <tt>null</tt> が渡されるので、このメソッド利用時にはご注意下さい。
	 * @param args	AADL プログラム引数とする文字列の配列
	 * @return	AADLモジュールの終了コード
	 */
	protected abstract int aadlRun(String[] args);

	/**
	 * このクラスの Java 行番号を、AADLソースコードの行番号に変換します。
	 * @param javaLineNo	Java ソースコードの行番号
	 * @return	AADLソースコードの行番号
	 */
	protected abstract int _aadl$getAADLLineNumber(int javaLineNo);

	/**
	 * 指定された文字列配列から、AADLプログラム引数番号に対応する引数値を取得します。
	 * AADLプログラム引数番号は、1 から始まる番号で指定します。
	 * @param args		AADLプログラム引数とする文字列配列
	 * @param argNo		AADLプログラム引数番号(1～)
	 * @return	プログラム引数番号に対応する引数値を返す。
	 * 			プログラム引数番号に対応する引数が存在しない場合は <tt>null</tt> を返す。
	 * @throws IndexOutOfBoundsException	AADLプログラム引数番号が 0 以下の場合
	 */
	static protected final String getAADLProgramArgument(String[] args, int argNo) {
		if (args != null && args.length >= argNo) {
			return args[argNo-1];
		} else {
			return null;
		}
	}
	
	//********************************************************************
	// Registered functions for dtalge package.
	// @sincle 1.50
	//********************************************************************
	
	protected boolean _aadl$addToStreamManager(AbTextFileReader<?> reader) {
		return _aadl$stream$manager.add(reader);
	}
	
	protected boolean _aadl$addToStreamManager(AbTextFileWriter writer) {
		return _aadl$stream$manager.add(writer);
	}
	
	protected boolean _aadl$removeFromStreamManager(AbTextFileReader<?> reader) {
		return _aadl$stream$manager.remove(reader);
	}
	
	protected boolean _aadl$removeFromStreamManager(AbTextFileWriter writer) {
		return _aadl$stream$manager.remove(writer);
	}

	/**
	 * 指定されたファイル(<em>filename</em>)をデフォルト・エンコーディングで、
	 * テキスト・ファイルとして読み込むリーダーオブジェクトを生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultTextEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultTextEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * このメソッドで生成されたオブジェクトは、{@link #closeReader(TextFileReader)} を
	 * 呼び出してストリームを閉じてください。 
	 * 
	 * @param filename	ファイルのパス名
	 * @return	生成された <code>TextFileReader</code> オブジェクト
	 * @throws AADLRuntimeException	ファイルが存在しない場合
	 * 
	 * @since 1.50
	 */
	protected final TextFileReader newTextFileReader(String filename) {
		try {
			TextFileReader newReader;
			String _defEncoding = getDefaultTextEncoding();
			if (_defEncoding == null)
				newReader = new TextFileReader(filename==null ? null : new File(filename));
			else
				newReader = new TextFileReader(filename==null ? null : new File(filename), _defEncoding);
			_aadl$addToStreamManager(newReader);
			return newReader;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}

	/**
	 * 指定されたファイル(<em>filename</em>)を、指定されたエンコーディング(<em>encoding</em>)で、
	 * テキスト・ファイルとして読み込むリーダーオブジェクトを生成します。
	 * このメソッドで生成されたオブジェクトは、{@link #closeReader(TextFileReader)} を
	 * 呼び出してストリームを閉じてください。 
	 * @param filename	ファイルのパス名
	 * @param encoding	エンコーディングとする文字セット名
	 * @return	生成された <code>TextFileReader</code> オブジェクト
	 * @throws AADLRuntimeException	ファイルが存在しない場合、もしくはサポートされていない文字セット名が指定された場合
	 * 
	 * @since 1.50
	 */
	protected final TextFileReader newTextFileReader(String filename, String encoding) {
		try {
			TextFileReader newReader = new TextFileReader(filename==null ? null : new File(filename), encoding);
			_aadl$addToStreamManager(newReader);
			return newReader;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された出力先ファイル(<em>filename</em>)にデフォルト・エンコーディングで、
	 * テキスト・ファイルとして書き込むライターオブジェクトを生成します。出力先ファイルがすでに存在
	 * している場合は、新しいファイルとして上書きされます。
	 * デフォルト・エンコーディングは、{@link #getDefaultTextEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultTextEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * このメソッドで生成されたオブジェクトは、{@link #closeWriter(TextFileWriter)} を
	 * 呼び出してストリームを閉じてください。 
	 * @param filename	出力先ファイルのパス名
	 * @return	生成された <code>TextFileWriter</code> オブジェクト
	 * @throws AADLRuntimeException	書き込むファイルが生成できない場合
	 * 
	 * @since 1.50
	 */
	protected final TextFileWriter newTextFileWriter(String filename) {
		try {
			TextFileWriter newWriter;
			String _defEncoding = getDefaultTextEncoding();
			if (_defEncoding == null)
				newWriter = new TextFileWriter(filename==null ? null : new File(filename));
			else
				newWriter = new TextFileWriter(filename==null ? null : new File(filename), _defEncoding);
			_aadl$addToStreamManager(newWriter);
			return newWriter;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された出力先ファイル(<em>filename</em>)に、指定されたエンコーディング(<em>encoding</em>)で、
	 * テキスト・ファイルとして書き込むライターオブジェクトを生成します。出力先ファイルがすでに存在
	 * している場合は、新しいファイルとして上書きされます。
	 * このメソッドで生成されたオブジェクトは、{@link #closeWriter(TextFileWriter)} を
	 * 呼び出してストリームを閉じてください。 
	 * @param filename	出力先ファイルのパス名
	 * @param encoding	出力時のエンコーディングとする文字セット名
	 * @return	生成された <code>TextFileWriter</code> オブジェクト
	 * @throws AADLRuntimeException	書き込むファイルが生成できない場合、もしくはサポートされていない文字セット名が指定された場合
	 * 
	 * @since 1.50
	 */
	protected final TextFileWriter newTextFileWriter(String filename, String encoding) {
		try {
			TextFileWriter newWriter = new TextFileWriter(filename==null ? null : new File(filename), encoding);
			_aadl$addToStreamManager(newWriter);
			return newWriter;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filename</em>)をデフォルト・エンコーディングで、
	 * CSV ファイルとして読み込むリーダーオブジェクトを生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * CSV ファイルの読み込みにおいては、改行文字を CSV レコード区切り、カンマをフィールド区切り文字として
	 * 使用します。
	 * このメソッドで生成されたオブジェクトは、{@link #closeReader(CsvFileReader)} を
	 * 呼び出してストリームを閉じてください。 
	 * 
	 * @param filename	ファイルのパス名
	 * @return	生成された <code>CsvFileReader</code> オブジェクト
	 * @throws AADLRuntimeException	ファイルが存在しない場合
	 * 
	 * @since 1.50
	 */
	protected final CsvFileReader newCsvFileReader(String filename) {
		try {
			CsvFileReader newReader;
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				newReader = new CsvFileReader(filename==null ? null : new File(filename));
			else
				newReader = new CsvFileReader(filename==null ? null : new File(filename), _defEncoding);
			_aadl$addToStreamManager(newReader);
			return newReader;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filename</em>)を、指定されたエンコーディング(<em>encoding</em>)で、
	 * CSV ファイルとして読み込むリーダーオブジェクトを生成します。
	 * CSV ファイルの読み込みにおいては、改行文字を CSV レコード区切り、カンマをフィールド区切り文字として
	 * 使用します。
	 * このメソッドで生成されたオブジェクトは、{@link #closeReader(CsvFileReader)} を
	 * 呼び出してストリームを閉じてください。 
	 * @param filename	ファイルのパス名
	 * @param encoding	エンコーディングとする文字セット名
	 * @return	生成された <code>CsvFileReader</code> オブジェクト
	 * @throws AADLRuntimeException	ファイルが存在しない場合、もしくはサポートされていない文字セット名が指定された場合
	 * 
	 * @since 1.50
	 */
	protected final CsvFileReader newCsvFileReader(String filename, String encoding) {
		try {
			CsvFileReader newReader = new CsvFileReader(filename==null ? null : new File(filename), encoding);
			_aadl$addToStreamManager(newReader);
			return newReader;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された出力先ファイル(<em>filename</em>)にデフォルト・エンコーディングで、
	 * CSV ファイルとして書き込むライターオブジェクトを生成します。出力先ファイルがすでに存在
	 * している場合は、新しいファイルとして上書きされます。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * CSV ファイルの書き込みにおいては、改行文字を CSV レコード区切り、カンマをフィールド区切りとして
	 * 出力します。
	 * このメソッドで生成されたオブジェクトは、{@link #closeWriter(CsvFileWriter)} を
	 * 呼び出してストリームを閉じてください。 
	 * @param filename	出力先ファイルのパス名
	 * @return	生成された <code>CsvFileWriter</code> オブジェクト
	 * @throws AADLRuntimeException	書き込むファイルが生成できない場合
	 * 
	 * @since 1.50
	 */
	protected final CsvFileWriter newCsvFileWriter(String filename) {
		try {
			CsvFileWriter newWriter;
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				newWriter = new CsvFileWriter(filename==null ? null : new File(filename));
			else
				newWriter = new CsvFileWriter(filename==null ? null : new File(filename), _defEncoding);
			_aadl$addToStreamManager(newWriter);
			return newWriter;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された出力先ファイル(<em>filename</em>)に、指定されたエンコーディング(<em>encoding</em>)で、
	 * CSV ファイルとして書き込むライターオブジェクトを生成します。出力先ファイルがすでに存在
	 * している場合は、新しいファイルとして上書きされます。
	 * CSV ファイルの書き込みにおいては、改行文字を CSV レコード区切り、カンマをフィールド区切りとして
	 * 出力します。
	 * このメソッドで生成されたオブジェクトは、{@link #closeWriter(CsvFileWriter)} を
	 * 呼び出してストリームを閉じてください。 
	 * @param filename	出力先ファイルのパス名
	 * @param encoding	出力時のエンコーディングとする文字セット名
	 * @return	生成された <code>CsvFileWriter</code> オブジェクト
	 * @throws AADLRuntimeException	書き込むファイルが生成できない場合、もしくはサポートされていない文字セット名が指定された場合
	 * 
	 * @since 1.50
	 */
	protected final CsvFileWriter newCsvFileWriter(String filename, String encoding) {
		try {
			CsvFileWriter newWriter = new CsvFileWriter(filename==null ? null : new File(filename), encoding);
			_aadl$addToStreamManager(newWriter);
			return newWriter;
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}

	/**
	 * 指定されたストリームを閉じます。
	 * @param reader	リーダー
	 * 
	 * @since 1.50
	 */
	protected final void closeReader(TextFileReader reader) {
		reader.close();
		_aadl$removeFromStreamManager(reader);
	}
	
	/**
	 * 指定されたストリームを閉じます。
	 * @param reader	リーダー
	 * 
	 * @since 1.50
	 */
	protected final void closeReader(CsvFileReader reader) {
		reader.close();
		_aadl$removeFromStreamManager(reader);
	}
	
	/**
	 * 指定されたストリームを閉じます。
	 * @param writer	ライター
	 * 
	 * @since 1.50
	 */
	protected final boolean closeWriter(TextFileWriter writer) {
		boolean result = writer.close();
		if (result) _aadl$removeFromStreamManager(writer);
		return result;
	}
	
	/**
	 * 指定されたストリームを閉じます。
	 * @param writer	ライター
	 * 
	 * @since 1.50
	 */
	protected final boolean closeWriter(CsvFileWriter writer) {
		boolean result = writer.close();
		if (result) _aadl$removeFromStreamManager(writer);
		return result;
	}

	/**
	 * 指定されたストリームが正常に終了しているかを検査する。
	 * @param writer	ライター
	 * @throws RuntimeException	出力エラーが発生していた場合
	 * 
	 * @since 1.90
	 */
	protected final void writerValidSucceeded(TextFileWriter writer) {
		RuntimeException wex = writer.lastException();
		if (wex != null) {
			throw wex;
		}
	}
	

	/**
	 * 指定されたストリームが正常に終了しているかを検査する。
	 * @param writer	ライター
	 * @throws RuntimeException	出力エラーが発生していた場合
	 * 
	 * @since 1.90
	 */
	protected final void writerValidSucceeded(CsvFileWriter writer) {
		RuntimeException wex = writer.lastException();
		if (wex != null) {
			throw wex;
		}
	}
	
	/**
	 * 指定されたストリームを閉じます。
	 * ストリームでエラーが発生していた場合は、例外をスローします。
	 * @param writer	ライター
	 * @throws RuntimeException	出力エラーが発生していた場合
	 * 
	 * @since 1.90
	 */
	protected final void closeWriterValidSucceeded(TextFileWriter writer) {
		if (writer.close()) {
			_aadl$removeFromStreamManager(writer);
		}
		RuntimeException wex = writer.lastException();
		if (wex != null) {
			throw wex;
		}
	}
	
	/**
	 * 指定されたストリームを閉じます。
	 * ストリームでエラーが発生していた場合は、例外をスローします。
	 * @param writer	ライター
	 * @throws RuntimeException	出力エラーが発生していた場合
	 * 
	 * @since 1.90
	 */
	protected final void closeWriterValidSucceeded(CsvFileWriter writer) {
		if (writer.close()) {
			_aadl$removeFromStreamManager(writer);
		}
		RuntimeException wex = writer.lastException();
		if (wex != null) {
			throw wex;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
