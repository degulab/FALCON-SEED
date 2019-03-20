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
 * @(#)TextFileReader.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ssac.aadl.runtime.io.internal.AbTextFileReader;

/**
 * テキストファイルを 1 行ずつ読み込むリーダークラス。
 * <p>このクラスは、自身がイテレータであり、その反復子を利用して
 * テキストファイルから 1 行ずつ文字列として取得します。<br>
 * このクラスの {@link #next()} メソッドが返す文字列には、
 * 行終端文字は含まれません。
 * <p>
 * このクラスのインスタンスが生成されると、指定されたファイルをオープンし、
 * 読み込み待機状態となります。反復子がファイルの最後まで到達するか、
 * 読み込みエラーが発生した場合、このファイルは自動的に閉じられます。
 * ファイルが最後まで読み込まれない状態で処理を中断した場合は、
 * {@link #close()} メソッドを呼び出してファイルを閉じてください。
 * 
 * @version 1.50	2010/09/29
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
public class TextFileReader extends AbTextFileReader<String>
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
	
	/**
	 * プラットフォーム標準のエンコーディングで指定されたファイルを読み込む、
	 * <code>TextFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public TextFileReader(File file) throws FileNotFoundException
	{
		super(file);
	}
	
	/**
	 * 指定されたエンコーディングで指定されたファイルを読み込む、
	 * <code>TextFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public TextFileReader(File file, String charsetName) throws FileNotFoundException, UnsupportedEncodingException
	{
		super(file, charsetName);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 新しいレコードを読み込み、読み込み済みレコードを更新します。
	 * このメソッドは、読み込み済みレコードの有無に関わらず、
	 * 新しいレコードを読み込みます。
	 * @return	新しいレコードが読み込まれた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException	読み込みエラーが発生した場合
	 */
	@Override
	protected boolean readNextRecord() throws IOException {
		_nextRecord = _reader.readLine();
		return (_nextRecord != null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
