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
 * @(#)TextFileWriter.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import ssac.aadl.runtime.io.internal.AbTextFileWriter;

/**
 * テキストファイルに任意の文字列を出力するライタークラス。
 * <p>
 * このクラスのインスタンスが生成されると、指定されたファイルをオープンし、
 * 書き込み待機状態となります。<br>
 * <b>このクラスでは、{@link #close()} メソッドが呼び出されるまでファイルは
 * 閉じられません。ファイルへの書き込みが完了したときは、
 * 必ず {@link #close()} メソッドを呼び出してファイルを閉じてください。</b>
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
public class TextFileWriter extends AbTextFileWriter
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
	 * プラットフォーム標準のエンコーディングで指定されたファイルへ書き込む、
	 * <code>TextFileWriter</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを拒否する場合
	 */
	public TextFileWriter(File file) throws FileNotFoundException
	{
		super(file);
	}
	
	/**
	 * 指定されたエンコーディングで指定されたファイルへ書き込む、
	 * <code>TextFileWriter</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @param charsetName	エンコーディングとする文字セット名。
	 * 						<tt>null</tt> の場合は、プラットフォーム標準のエンコーディングとなる。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを拒否する場合
	 */
	public TextFileWriter(File file, String charsetName) throws FileNotFoundException, UnsupportedEncodingException
	{
		super(file, charsetName);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 改行文字を書き込みます。このメソッドが出力する改行文字は、Java 標準の改行文字となります。
	 * 通常、プラットフォーム標準の改行文字が出力されます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean newLine() {
		return writeRecordSeparator();
	}

	/**
	 * 改行文字を書き込みます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean println() {
		return writeRecordSeparator();
	}
	
	/**
	 * 指定されたオブジェクトを、{@link String#valueOf(Object)} メソッドにより文字列に変換し、
	 * その文字列を書き込みます。
	 * 引数が <code>BigDecimal</code> オブジェクトの場合、
	 * 基本的に指数フィールドのない、極力短い文字列表現として出力します。
	 * 引数が <tt>null</tt> の場合は &quot;null&quot; が出力されます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @param obj	出力するオブジェクト
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean print(Object obj) {
		if (obj == null) {
			return write("null");
		}
		else if (obj instanceof BigDecimal) {
			BigDecimal val = (BigDecimal)obj;
			if (BigDecimal.ZERO.compareTo(val) == 0) {
				return write(BigDecimal.ZERO.toPlainString());
			} else {
				return write(val.stripTrailingZeros().toPlainString());
			}
		}
		else if (obj instanceof String) {
			return write((String)obj);
		}
		else {
			return write(String.valueOf(obj));
		}
	}
	
	/**
	 * 指定されたオブジェクトを、{@link String#valueOf(Object)} メソッドにより文字列に変換し、
	 * その文字列を書き込み、さらに改行文字を書き込みます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @param obj	出力するオブジェクト
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean println(Object obj) {
		if (print(obj)) {
			return newLine();
		} else {
			return false;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
