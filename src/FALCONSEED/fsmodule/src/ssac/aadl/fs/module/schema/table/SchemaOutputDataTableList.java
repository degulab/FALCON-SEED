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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SchemaOutputDataTableList	3.2.1	2015/07/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaOutputDataTableList	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import ssac.aadl.fs.module.schema.SchemaObjectList;

/**
 * 出力テーブルデータスキーマのリスト。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaOutputDataTableList extends SchemaObjectList<SchemaOutputCsvDataTable>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4746422679953809315L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のパラメータで、要素が空の新しいインスタンスを生成する。
	 */
	public SchemaOutputDataTableList() {
		super();
	}

	/**
	 * 標準のパラメータで、指定された容量を持つ、要素が空の新しいインスタンスを生成する。
	 * @param initialCapacity	初期容量(0 以上)
	 * @throws IllegalArgumentException	<em>initialCapacity</em> が負の場合
	 */
	public SchemaOutputDataTableList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param copyListElements	コレクション要素をコピーする場合は <tt>true</tt>、コピーしない場合は <tt>false</tt>
	 * @param src	コピー元とするオブジェクト
	 * @throws NullPointerException	コピー元オブジェクトが <tt>null</tt> の場合
	 */
	public SchemaOutputDataTableList(boolean copyListElements, final SchemaOutputDataTableList src) {
		super(copyListElements, src);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
