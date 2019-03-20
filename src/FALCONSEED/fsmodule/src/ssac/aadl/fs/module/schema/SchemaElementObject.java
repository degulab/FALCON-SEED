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
 * @(#)SchemaElementValue.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

/**
 * 汎用フィルタ定義における要素の位置情報を保持するオブジェクトのインタフェース。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public interface SchemaElementObject extends SchemaObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 無効な要素番号 **/
	static public final int	INVALID_ELEMENT_NO	= (-1);

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトに設定された要素番号が有効かどうかを判定する。
	 * @return	要素番号が無効の場合は <tt>false</tt>
	 */
	public boolean isValidElementNo();
	
	/**
	 * このオブジェクトの要素番号を取得する。
	 * @return	正の要素番号、要素番号が無効の場合は (-1)
	 */
	public int getElementNo();

	/**
	 * このオブジェクトに要素番号を設定する。
	 * @param newElemNo	新しい要素番号、無効とする場合は負の値
	 */
	public void setElementNo(int newElemNo);

	/**
	 * このオブジェクトに要素番号を設定する。
	 * @param newElemNo	新しい要素番号、無効とする場合は負の値
	 * @return	要素番号が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean updateElementNo(int newElemNo);

	/**
	 * この要素の親となるオブジェクトが設定されているかを判定する。
	 * @return	要素の親オブジェクトが設定されている場合は <tt>true</tt>
	 */
	public boolean hasParentObject();

	/**
	 * 設定されているこの要素の親オブジェクトを取得する。
	 * @return	設定されている親オブジェクト、設定されていな場合は <tt>null</tt>
	 */
	public SchemaObject getParentObject();

	/**
	 * このオブジェクトに親オブジェクトを設定する。
	 * @param newParent	新しい親オブジェクト
	 */
	public void setParentObject(SchemaObject newParent);

	/**
	 * このオブジェクトに親オブジェクトを設定する。
	 * このメソッドでは、親オブジェクトが同一かどうか（インスタンスが同じかどうか)を判定し、
	 * 異なるオブジェクトであれば更新する。
	 * @param newParent	新しい親オブジェクト
	 * @return	親オブジェクトのインスタンスが変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean updateParentObject(SchemaObject newParent);
}
