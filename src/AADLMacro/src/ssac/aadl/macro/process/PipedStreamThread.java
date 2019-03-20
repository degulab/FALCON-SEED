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
 * @(#)PipedStreamThread.java	2.0.0	2014/03/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.process;

import java.io.IOException;
import java.io.InputStream;

import ssac.aadl.macro.util.io.ReportPrinter;

/**
 * 入力ストリームの内容を出力ストリームにコピーもしくは破棄するスレッド。
 * 
 * @version 2.0.0	2014/03/18
 * @since 2.0.0
 */
public class PipedStreamThread extends Thread
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 入力ストリーム */
	private final InputStream	_inStream;
	/** 出力ストリーム */
	private final ReportPrinter	_outPrinter;
	/** 処理終了時に入力ストリームを閉じることを示すフラグ */
	private final boolean		_closeInStream;
	/** 処理終了時に出力ストリームを閉じることを示すフラグ */
	private final boolean		_closeOutPrinter; 

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * <em>outStream</em> に <tt>null</tt> を指定した場合、入力ストリームの内容は読み込まれるが、読み込まれた内容は破棄される。
	 * @param inStream		読み込み対象の入力ストリーム
	 * @param outPrinter	出力先とする出力ストリームとなる <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param autoCloseInStream		処理終了時に入力ストリームを閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @param autoCloseOutPrinter	処理終了時に出力ストリームを閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @throws NullPointerException	<em>inStream</em> が <tt>null</tt> の場合
	 */
	public PipedStreamThread(InputStream inStream, ReportPrinter outPrinter, boolean autoCloseInStream, boolean autoCloseOutPrinter) {
		super("PipedStreamThread");
		_closeInStream  = autoCloseInStream;
		_closeOutPrinter = autoCloseOutPrinter;
		if (inStream == null)
			throw new NullPointerException("Input stream object is null.");
		_inStream  = inStream;
		_outPrinter = outPrinter;
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * <em>outStream</em> に <tt>null</tt> を指定した場合、入力ストリームの内容は読み込まれるが、読み込まれた内容は破棄される。
	 * <p>このコンストラクタでは、入力ストリームおよび出力ストリームは処理終了後も閉じられない。
	 * @param inStream	読み込み対象の入力ストリーム
	 * @param outPrinter	出力先とする出力ストリームとなる <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @throws NullPointerException	<em>inStream</em> が <tt>null</tt> の場合
	 */
	public PipedStreamThread(InputStream inStream, ReportPrinter outPrinter) {
		this(inStream, outPrinter, false, false);
	}

	/**
	 * 指定されたパラメータで、入力ストリームの内容を破棄する新しいインスタンスを生成する。
	 * <p>このコンストラクタでは、入力ストリームの内容は破棄される。
	 * @param inStream	読み込み対象の入力ストリーム
	 * @param autoCloseInStream		処理終了時に入力ストリームを閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @throws NullPointerException	<em>inStream</em> が <tt>null</tt> の場合
	 */
	public PipedStreamThread(InputStream inStream, boolean autoCloseInStream) {
		this(inStream, null, autoCloseInStream, false);
	}

	/**
	 * 指定されたパラメータで、入力ストリームの内容を破棄する新しいインスタンスを生成する。
	 * <p>このコンストラクタでは、入力ストリームの内容は破棄される。また、入力ストリームは処理終了後も閉じられない。
	 * @param inStream	読み込み対象の入力ストリーム
	 * @throws NullPointerException	<em>inStream</em> が <tt>null</tt> の場合
	 */
	public PipedStreamThread(InputStream inStream) {
		this(inStream, null, false, false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 入力ストリームが処理終了時に自動的に閉じられるかどうかの設定を取得する。
	 * @return	自動的に閉じられる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean autoCloseInputStream() {
		return _closeInStream;
	}
	
	/**
	 * 出力ストリームが処理終了時に自動的に閉じられるかどうかの設定を取得する。
	 * @return	自動的に閉じられる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean autoCloseOutputPrinter() {
		return _closeOutPrinter;
	}

	/**
	 * このオブジェクトに設定されている入力ストリームを取得する。
	 * @return	入力ストリーム
	 */
	public InputStream getInputStream() {
		return _inStream;
	}

	/**
	 * このオブジェクトに設定されている <code>ReportPrinter</code> オブジェクトを取得する。
	 * @return	設定されている <code>ReportPrinter</code> オブジェクト、設定されていない場合は <tt>null</tt>
	 */
	public ReportPrinter getOutputPrinter() {
		return _outPrinter;
	}

	//------------------------------------------------------------
	// java.lang.Thread interfaces
	//------------------------------------------------------------
	
	public void run()
	{
		byte[] buffer = new byte[2048];
		
		try {
			if (_outPrinter == null) {
				// drop stream data
				for (; _inStream.read(buffer) >= 0;);
			}
			else {
				// transfer stream data
				int r = _inStream.read(buffer);
				for (; r >= 0; ) {
					_outPrinter.write(buffer, 0, r);
					r = _inStream.read(buffer);
				}
			}
		}
		catch (Exception ex) {}
		finally {
			//--- auto-close output stream
			if (_outPrinter != null && _closeOutPrinter) {
				try {
					_outPrinter.close();
				} catch (IOException ignoreEx) {}
			}
			//--- auto-close input stream
			if (_closeInStream) {
				try {
					_inStream.close();
				} catch (IOException ignoreEx) {}
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
