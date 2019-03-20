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
 * @(#)SchemaObject.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import javax.xml.stream.XMLStreamException;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;

/**
 * 汎用フィルタ定義の共通インタフェース。
 * 汎用フィルタ定義クラスは、すべてこのインタフェースを実装する。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public interface SchemaObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトのインスタンス ID を取得する。
	 * このメソッドが返す値は、{@link System#identityHashCode(Object)} の戻り値となる。
	 * @return	インスタンス ID
	 */
	public int getInstanceId();

	/**
	 * このオブジェクトの名前を取得する。
	 * このメソッドが返す値は空文字ではない文字列、もしくは <tt>null</tt> である。
	 * @return	設定されている名前、設定されていない場合は <tt>null</tt>
	 */
	public String getName();

	/**
	 * このオブジェクトの名前を設定する。
	 * 新しい名前が空文字の場合、<tt>null</tt> が設定される。
	 * @param newName	新しい名前
	 */
	public void setName(String newName);
	
	/**
	 * このオブジェクトの名前を更新する。
	 * 新しい名前が空文字の場合、<tt>null</tt> が設定される。
	 * @param newName	新しい名前
	 * @return	名前が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean updateName(String newName);

	/**
	 * このオブジェクトの説明を取得する。
	 * このメソッドが返す値は空文字ではない文字列、もしくは <tt>null</tt> である。
	 * @return	設定されている説明、設定されていない場合は <tt>null</tt>
	 */
	public String getDescription();

	/**
	 * このオブジェクトの説明を設定する。
	 * 新しい説明が空文字の場合、<tt>null</tt> が設定される。
	 * @param newDesc	新しい説明
	 */
	public void setDescription(String newDesc);

	/**
	 * このオブジェクトの説明を更新する。
	 * 新しい説明が空文字の場合、<tt>null</tt> が設定される。
	 * @param newDesc	新しい説明
	 * @return	説明が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean updateDescription(String newDesc);

	/**
	 * このオブジェクトの名前を示す文字列を返す。
	 * 名前が設定されていない場合、クラス名とインスタンス ID の組合せとする。
	 * @return	名前を示す文字列
	 */
	public String toNameString();

	/**
	 * このオブジェクトを示す変数名表現を返す。
	 * @return	このオブジェクトを示す変数名表現
	 */
	public String toVariableNameString();

	/**
	 * このオブジェクトのパラメータを示す文字列表現を返す。
	 * @return	パラメータを含む文字列表現
	 */
	public String toParamString();

	//------------------------------------------------------------
	// Public interfaces for XML
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの XML エレメント名を取得する。
	 * @return	このオブジェクトの XML エレメント名
	 */
	public String getXmlElementName();

	/**
	 * 指定されたライターオブジェクトにより、このオブジェクトの XML 表現を出力する。
	 * @param writer	専用のライターオブジェクト
	 * @throws XMLStreamException	XML 出力に失敗した場合
	 */
	public void writeToXml(SchemaConfigWriter writer) throws XMLStreamException;

	/**
	 * 指定されたリーダーオブジェクトの現在の位置から XML 表現を読み込み、このオブジェクトの内容を復元する。
	 * @param reader	専用のリーダーオブジェクト
	 * @throws XMLStreamException	XML 入力に失敗した場合
	 */
	public void readFromXml(SchemaConfigReader reader) throws XMLStreamException;
}
