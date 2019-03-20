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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)IDataField.java	2.1.0	2013/07/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * データフィールドの情報を取得するインタフェース。
 * 
 * @version 2.1.0	2013/07/20
 * @since 2.1.0
 */
public interface IDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * この列が属するデータテーブルを返す。
	 */
	public IDataTable getDataTable();
	
	/**
	 * この列のテーブル上のインデックスを返す。
	 */
	public int getFieldIndex();
	
	/**
	 * 対象データのフィールド名(列名)を返す。
	 */
	public String getFieldName();
	
	/**
	 * このデータの総レコード数を返す。
	 * 総レコード数は、すべてのデータレコードとヘッダレコードの総数となる。
	 */
	public long getRecordCount();
	
	/**
	 * データ数(データレコード数)を返す。
	 */
	public long getDataCount();
	
	/**
	 * 対象データのデータ型を返す。
	 */
	public Class<?> getFieldType();
	
	/**
	 * 指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getFieldValue(long index);
}
