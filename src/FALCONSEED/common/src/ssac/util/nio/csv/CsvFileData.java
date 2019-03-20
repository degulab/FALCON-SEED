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
 * @(#)CsvFileData.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileData.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ssac.util.nio.IndexedTextFileData;

/**
 * CSVファイルの情報を保持するデータクラス。
 * 
 * @version 1.17	2010/11/19
 * @since 1.16
 */
public class CsvFileData extends IndexedTextFileData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** CSVパラメータ **/
	private final CsvParameters	_csvParams;

	/** CSVフィールド属性のリスト **/
	private List<CsvFieldAttr>	_fieldAttrList = Collections.emptyList();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileData(File indexFile, File file, Charset encoding) {
		super(indexFile, file, encoding);
		this._csvParams = new CsvParameters();
	}
	
	public CsvFileData(File indexFile, File file, Charset encoding, CsvParameters params) {
		super(indexFile, file, encoding);
		this._csvParams = new CsvParameters(params);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 第１行をヘッダ行として使用するかどうかを取得する。
	 * @return	第１行がヘッダ行に指定されている場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean getUseHeaderLine() {
		return _csvParams.getUseHeaderLine();
	}

	/**
	 * 読み込み時にデータ型の自動判別を行うかどうかの設定を取得する。
	 * @return	データ型の自動判別を行う場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean getAutoDetectDataType() {
		return _csvParams.getAutoDetectDataType();
	}

	/**
	 * デコードにおいて、クオートによるエスケープ処理が有効かどうかを取得する。
	 * デフォルトでは、クオートによるエスケープ処理は有効に設定されている。
	 * @return	クオートによるエスケープ処理が有効であれば <tt>true</tt>、
	 * 			無効であれば <tt>false</tt> を返す。
	 */
	public boolean isQuoteEscapeEnabled() {
		return _csvParams.isQuoteEscapeEnabled();
	}

	/**
	 * デコードにおいて、複数行フィールドが許可されているかどうかを取得する。
	 * デフォルトでは、複数行フィールドは許可されている。
	 * <p>
	 * 複数行フィールドは、改行文字を含むフィールドであり、{@link #isQuoteEscapeEnabled()} が
	 * <tt>true</tt> を返す場合のみ、複数行フィールドとしてデコードされる。
	 * @return	複数行フィールドが許可されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isAllowMutiLineField() {
		return _csvParams.isAllowMutiLineField();
	}

	/**
	 * 現在のフィールド区切り文字を取得する。
	 * デフォルトでは、カンマ(,)に設定されている。
	 * @return	現在のフィールド区切り文字
	 */
	public char getDelimiterChar() {
		return _csvParams.getDelimiterChar();
	}

	/**
	 * 現在のクオート文字を取得する。
	 * デフォルトでは、ダブルクオーテーション(&quot;)に設定されている。
	 * @return	現在のクオート文字
	 */
	public char getQuoteChar() {
		return _csvParams.getQuoteChar();
	}

	/**
	 * CSVフィールドの最大数を取得する。
	 * @return	最大のCSVフィールド数
	 */
	public int getMaxFieldSize() {
		return _fieldAttrList.size();
	}

	/**
	 * CSVフィールドに設定されている名前を取得する。
	 * @param index	取得するフィールドの位置を示す 0 から始まるインデックス
	 * @return	CSVフィールド名を返す。
	 * 			フィールド名が設定されていない場合は <tt>null</tt> を返す。
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 * @since 1.17
	 */
	public String getFieldName(int index) {
		return _fieldAttrList.get(index).getFieldName();
	}

	/**
	 * CSVフィールドに設定されているデータ型を取得する。
	 * このデータ型は、CSV読み込み時に自動判別されたデータ型であり、
	 * 判別不可能な場合は <code>String</code> クラスが返される。
	 * @param index	取得するフィールドの位置を示す 0 から始まるインデックス
	 * @return	設定されているデータ型を返す。
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 * @since 1.17
	 */
	public Class<?> getFieldDataType(int index) {
		return _fieldAttrList.get(index).getDataType();
	}

	/**
	 * CSVフィールド属性の新しい配列を取得する。
	 * このメソッドは、CSVフィールド属性リストの要素をすべて格納する新しい配列を返す。
	 * @return	CSVフィールド属性を要素に持つ配列を返す。
	 * 			CSVフィールドが存在しない場合、要素が空の配列を返す。
	 * @since 1.17
	 */
	public CsvFieldAttr[] getFieldListArray() {
		if (!_fieldAttrList.isEmpty()) {
			return _fieldAttrList.toArray(new CsvFieldAttr[_fieldAttrList.size()]);
		} else {
			return new CsvFieldAttr[0];
		}
	}

	/**
	 * CSVフィールド属性のリストを返す。
	 * @return	CSVフィールド属性を要素に持つリストを返す。
	 * 			CSVフィールド属性が設定されていない場合は <tt>null</tt> を返す。
	 * @since 1.17
	 */
	public List<CsvFieldAttr> getFieldAttrList() {
		return _fieldAttrList;
	}

	/**
	 * 新しいCSVフィールド属性を設定する。
	 * @param attrs	設定するCSVフィールド属性の配列
	 * @since 1.17
	 */
	public void setFieldAttrs(CsvFieldAttr...attrs) {
		setFieldAttrs(attrs==null ? (Collection<? extends CsvFieldAttr>)null : Arrays.asList(attrs));
	}

	/**
	 * 新しいCSVフィールド属性を設定する。
	 * @param attrs	設定するCSVフィールド属性のコレクション
	 * @since 1.17
	 */
	public void setFieldAttrs(Collection<? extends CsvFieldAttr> attrs) {
		if (attrs == null) {
			_fieldAttrList = new ArrayList<CsvFieldAttr>(0);
		} else {
			_fieldAttrList = new ArrayList<CsvFieldAttr>(attrs);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected CsvParameters getCsvParameters() {
		return _csvParams;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
