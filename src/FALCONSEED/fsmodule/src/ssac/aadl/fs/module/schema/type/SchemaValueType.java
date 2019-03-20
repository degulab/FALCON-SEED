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
 * @(#)SchemaValueType.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaValueType.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;


/**
 * 汎用フィルタ定義における、値のデータ型を示す基本クラス。
 * このオブジェクトならびに派生クラスは、基本的に不変オブジェクトとする。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public abstract class SchemaValueType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** データ型を示す文字列 **/
	private final String	_typeName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたタイプ名を持つ、補助パラメータを保持しないデータ型オブジェクトを生成する。
	 * @param typeName	タイプ名
	 * @throws NullPointerException	タイプ名が <tt>null</tt> の場合
	 */
	protected SchemaValueType(String typeName) {
		_typeName  = typeName.intern();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

//	/**
//	 * 二つのデータ型について、タイプ名が同一かを判定する。
//	 * このメソッドでは、引数のどちらも <tt>null</tt> の場合は、<tt>true</tt> が返される。
//	 * @param type1	データ型の一方
//	 * @param type2	データ型のもう一方
//	 * @return	タイプ名が同じ場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	static public boolean equalTypeName(SchemaValueType type1, SchemaValueType type2) {
//		if (type1 == type2)
//			return true;
//		return (type1 != null && type2 != null && type1._typeName == type2._typeName);
//	}

	/**
	 * このデータ型のタイプ名を取得する。
	 * @return	タイプ名
	 */
	public String getTypeName() {
		return _typeName;
	}

	/**
	 * このデータ型に相当する JAVA クラスを取得する。
	 * @return	クラスオブジェクト
	 */
	public abstract Class<?> getJavaClass();

	/**
	 * 指定された文字列から、このデータ型に応じた値に変換する。
	 * @param value	値を示す文字列
	 * @return	文字列から変換された値のオブジェクト
	 * @throws SchemaValueFormatException	変換に失敗した場合
	 */
	public abstract Object convertFromString(String value) throws SchemaValueFormatException;

	/**
	 * 指定されたオブジェクトを、このデータ型に応じた文字列形式に変換する。
	 * このデータ型に応じたオブジェクト型ではない場合、{@link java.lang.Object#toString()} メソッドによって文字列形式に変換する。
	 * <em>value</em> が <tt>null</tt> の場合、このメソッドは <tt>null</tt> を返す。
	 * @param value	文字列へ変換する値	
	 * @return	変換された文字列、<em>value</em> が <tt>null</tt> の場合は <tt>null</tt>
	 * @since 3.2.1
	 */
	public String convertToString(Object value) {
		if (value == null)
			return null;
		else
			return ssac.aadl.runtime.AADLFunctions.toString(value);
	}

	/**
	 * 補助パラメータの保持が許可されているデータ型かを返す。
	 * @return	パラメータ保持が許可されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean getAllowSubParameter() {
		return false;
	}

	/**
	 * 補助パラメータが保持されているかを判定する。
	 * @return	補助パラメータが保持されている場合は <tt>true</tt>、保持されていない場合は <tt>false</tt>
	 */
	public boolean hasSubParameter() {
		return false;
	}

	/**
	 * 保持されている補助パラメータを取得する。
	 * @return	保持されている補助パラメータ、保持されていない場合は <tt>null</tt>
	 */
	public Object getSubParameter() {
		return null;
	}

	/**
	 * このオブジェクトのハッシュ値を返す。
	 * @return	ハッシュ値
	 */
	public int hashCode() {
		return (_typeName.hashCode());
	}

	/**
	 * このオブジェクトの正確なハッシュ値を返す。
	 * @return	正確な状態を示すハッシュ値
	 */
	public int exactlyHashCode() {
		return hashCode();
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを判定する。
	 * @param obj	判定対象のオブジェクト
	 * @return	等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj != null && obj.getClass().equals(this.getClass())) {
			SchemaValueType aType = (SchemaValueType)obj;
			if (_typeName.equals(aType._typeName)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが正確に等しいかを判定する。
	 * @param obj	判定対象のオブジェクト
	 * @return	正確に等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean exactlyEquals(Object obj) {
		return equals(obj);
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * 基本的に、オブジェクトのタイプ名を返す。
	 * @return	このオブジェクトの文字列表現
	 */
	public String toString() {
		return _typeName;
	}

	/**
	 * このオブジェクトの正確な状態を示す文字列表現を返す。
	 * 基本的に、オブジェクトを復元可能な文字列表現とする。
	 * @return	このオブジェクトの正確な文字列表現
	 */
	public String toExactlyString() {
		return _typeName;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
