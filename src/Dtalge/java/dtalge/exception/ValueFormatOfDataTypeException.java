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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)ValueFormatOfDataTypeException.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ValueFormatOfDataTypeException.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.exception;

/**
 * アプリケーションが文字列を指定のデータ型に変換しようとしたとき、
 * 文字列の形式が正しくない場合にスローされる。
 *
 * @version 0.20	2010/02/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class ValueFormatOfDataTypeException extends IllegalValueOfDataTypeException
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
	 * 詳細メッセージを持たない <code>ValueFormatOfDataTypeException</code> を構築する。
	 * 
	 * @param dataTypeName	指定されたデータ型名
	 * @param requiredType	要求されたクラス情報
	 * @param targetValue	指定された値となるオブジェクト
	 */
	public ValueFormatOfDataTypeException(String dataTypeName, Class<?> requiredType, Object targetValue) {
		super(dataTypeName, requiredType, targetValue);
	}

	/**
	 * 指定された詳細メッセージを持つ <code>ValueFormatOfDataTypeException</code> を構築する。
	 * 
	 * @param dataTypeName	指定されたデータ型名
	 * @param requiredType	要求されたクラス情報
	 * @param targetValue	指定された値となるオブジェクト
	 * @param msg			詳細メッセージ
	 */
	public ValueFormatOfDataTypeException(String dataTypeName, Class<?> requiredType,
											Object targetValue, String msg)
	{
		super(dataTypeName, requiredType, targetValue, msg);
	}

	/**
	 * <code>(cause==null ? null : cause.toString())</code> の指定された原因および
	 * 詳細メッセージを使用して <code>ValueFormatOfDataTypeException</code> を構築する。
	 * 
	 * <p>通常 <tt>(cause==null ? null : cause.toString())</tt> には、<tt>cause</tt> の
	 * クラスおよび詳細メッセージが含まれる。
	 * <br>
	 * このコンストラクタは、例外が他のスロー可能オブジェクトのラッパーである場合に有用である。
	 * 
	 * @param dataTypeName	指定されたデータ型名
	 * @param requiredType	要求されたクラス情報
	 * @param targetValue	指定された値となるオブジェクト
	 * @param cause 原因。あとで {@link Throwable#getCause()} メソッドで取得するために
	 * 				 保存される。<tt>null</tt> が許可されており、原因が存在しないか不明で
	 * 				 あることを示す。
	 */
	public ValueFormatOfDataTypeException(String dataTypeName, Class<?> requiredType,
											Object targetValue, Throwable cause)
	{
		super(dataTypeName, requiredType, targetValue, cause);
	}

	/**
	 * 指定された詳細メッセージおよび原因を使用して
	 *  <code>ValueFormatOfDataTypeException</code> を構築する。
	 * 
	 * <p><code>cause</code> と関連付けられた詳細メッセージが、この例外の
	 * 詳細メッセージに自動的に統合されることはない。
	 * 
	 * @param dataTypeName	指定されたデータ型名
	 * @param requiredType	要求されたクラス情報
	 * @param targetValue	指定された値となるオブジェクト
	 * @param msg			詳細メッセージ
	 * @param cause 原因。あとで {@link Throwable#getCause()} メソッドで取得するために
	 * 				 保存される。<tt>null</tt> が許可されており、原因が存在しないか不明で
	 * 				 あることを示す。
	 */
	public ValueFormatOfDataTypeException(String dataTypeName, Class<?> requiredType,
											Object targetValue, String msg, Throwable cause)
	{
		super(dataTypeName, requiredType, targetValue, msg, cause);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このスロー可能オブジェクトの短い記述を返す。
	 * このオブジェクトが非<tt>null<tt>の詳細メッセージ文字列を
	 * 使用して作成された場合、結果は次の文字列を連結したものになる。
	 * <ul>
	 * <li>このオブジェクトの実際のクラス名
	 * <li>"<"
	 * <li>指定されたデータ型名
	 * <li>","
	 * <li>要求されたクラスのクラス名
	 * <li>","
	 * <li>指定された値の実際のクラス名
	 * <li>">"
	 * <li>": " (コロンとスペース)
	 * <li>このオブジェクトに対する {@link #getMessage} メソッドの結果
	 * </ul>
	 * このオブジェクトが <tt>null</tt> の詳細メッセージ文字列を使用して
	 * 作成された場合は、(コロンとスペース)以降の文字列は含まれません。
	 * 
	 * @return このスロー可能オブジェクトの文字列表現
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//--- exception class
		sb.append(getClass().getName());
		sb.append('<');
		//--- data type name
		sb.append(getDataTypeName());
		sb.append(',');
		//--- required class name
		sb.append(getRequiredType() != null ? getRequiredType().getName() : "Undefined class");
		sb.append(',');
		//--- target object class
		sb.append(String.valueOf(getTargetValue()));
		sb.append('>');
		//--- message
        String message = getLocalizedMessage();
		if (message != null) {
			sb.append(": ");
			sb.append(message);
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
