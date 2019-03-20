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
 * @(#)PropertyConverter.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.properties;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ssac.util.Strings;

/**
 * プロパティの値を変換するユーティリティ。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class PropertyConverter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 複数の数値をプロパティに格納する際の、標準区切り文字 **/
	static public char DefaultValueDelimiterChar = ',';
	
	/** Constant for the prefix of hex numbers.*/
	private static final String HEX_PREFIX = "0x";

	/** Constant for the radix of hex numbers.*/
	private static final int HEX_RADIX = 16;

	/** Constant for the argument classes of the Number constructor that takes a String. */
	private static final Class<?>[] CONSTR_ARGS = {String.class};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private PropertyConverter() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 文字列を指定された型の値に変換する。
	 * @param	clazz	変換後の型
	 * @param	value	値に変換する文字列
	 * @return	変換された値を返す。<em>value</em> が <tt>null</tt> の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException		<em>clazz</em> が <tt>null</tt> の場合
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public <T> T toValue(Class<T> clazz, String value) {
		if (value == null)
			return null;
		
		String strValue = value;
		if (Number.class.isAssignableFrom(clazz)) {
			// Number
			if (strValue.startsWith(HEX_PREFIX)) {
				try {
					BigInteger bi = new BigInteger(strValue.substring(HEX_PREFIX.length()), HEX_RADIX);
					strValue = bi.toString();
				}
				catch (Throwable ex) {
					throw new PropertyConversionException(getConversionErrorMessage(value, clazz, ex), ex);
				}
			}
		}
		
		try {
			Constructor<T> cs = clazz.getConstructor(CONSTR_ARGS);
			return cs.newInstance(new Object[]{strValue});
		}
		catch (InvocationTargetException ex) {
			throw new PropertyConversionException(getConstructErrorMessage(clazz, ex.getTargetException()), ex.getTargetException());
		}
		catch (Throwable ex) {
			throw new PropertyConversionException(getConstructErrorMessage(clazz, ex), ex);
		}
	}

	/**
	 * 指定された文字列を指定された区切り文字で分割し、各文字列を指定された型の値に変換する。
	 * 変換された値は、指定された型の配列として返す。
	 * @param clazz			変換後の型(配列要素の型)
	 * @param value			値に変換する文字列
	 * @param delimiter		文字列を分割する区切り文字
	 * @return	変換された値を格納する配列を返す。<em>value</em> が <tt>null</tt> の場合、空の配列を返す。
	 * @throws NullPointerException		<em>clazz</em> が <tt>null</tt> の場合
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	@SuppressWarnings("unchecked")
	static public <T> T[] toValueArray(Class<T> clazz, String value, char delimiter) {
		String[] sval = splitByDelimiter(value, delimiter);
		T[] retval;
		if (sval != null) {
			retval = (T[])java.lang.reflect.Array.newInstance(clazz, sval.length);
			for (int i = 0; i < sval.length; i++) {
				retval[i] = toValue(clazz, sval[i]);
			}
		}
		else {
			retval = (T[])java.lang.reflect.Array.newInstance(clazz, 0);
		}
		return retval;
	}

	/**
	 * 指定された文字列を指定された区切り文字で分割し、各文字列を指定された型の値に変換する。
	 * 変換された値は、{@link java.util.ArrayList} オブジェクトに格納される。
	 * @param clazz			変換後の型
	 * @param value			値に変換する文字列
	 * @param delimiter		文字列を分割する区切り文字
	 * @return	変換された値を格納する {@link java.util.ArrayList} オブジェクトを返す。
	 * 			<em>value</em> が <tt>null</tt> の場合、要素が空の {@link java.util.ArrayList} オブジェクトを返す。
	 * @throws NullPointerException		<em>clazz</em> が <tt>null</tt> の場合
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public <T> List<T> toValueList(Class<T> clazz, String value, char delimiter) {
		if (clazz == null)
			throw new NullPointerException("'clazz' argument is null.");
		
		String[] sval = splitByDelimiter(value, delimiter);
		ArrayList<T> retval;
		if (sval != null) {
			retval = new ArrayList<T>(sval.length);
			for (String s : sval) {
				retval.add(toValue(clazz, s));
			}
		}
		else {
			retval = new ArrayList<T>();
		}
		return retval;
	}
	
	/**
	 * 指定された文字列を指定された区切り文字で分割し、各文字列を指定された型の値に変換する。
	 * 変換された値は、<em>buffer</em> の終端に追加される。
	 * @param buffer		変換後の値を格納するバッファ
	 * @param clazz			変換後の型
	 * @param value			値に変換する文字列
	 * @param delimiter		文字列を分割する区切り文字
	 * @return	<em>buffer</em> に指定されたオブジェクト
	 * @throws NullPointerException		<em>buffer</em> もしくは <em>clazz</em> が <tt>null</tt> の場合
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public <T> List<T> toValueList(List<T> buffer, Class<T> clazz, String value, char delimiter) {
		if (buffer == null)
			throw new NullPointerException("'buffer' argument is null.");
		if (clazz == null)
			throw new NullPointerException("'clazz' argument is null.");
		
		String[] sval = splitByDelimiter(value, delimiter);
		if (sval != null) {
			for (String s : sval) {
				buffer.add(toValue(clazz, s));
			}
		}
		return buffer;
	}

	/**
	 * 指定された文字列を、指定された区切り文字で分割する。<br>
	 * このメソッドは、指定された文字列が有効(長さが 1 以上の文字列)な場合、
	 * {@link java.lang.String#split(String)} を呼び出す。
	 * @param value			分割する文字列
	 * @param delimiter		区切り文字
	 * @return	分割した文字列を格納する配列を返す。
	 * 			<em>value</em> が <tt>null</tt> の場合は <tt>null</tt> を返す。
	 * 			<em>value</em> が空文字列(長さが 0 の文字列)の場合は、空の配列を返す。
	 */
	static public String[] splitByDelimiter(String value, char delimiter) {
		if (value == null) {
			return null;
		}
		else if (value.length() > 0) {
			return value.split("\\" + delimiter);
		}
		else {
			return new String[0];
		}
	}
	
	/**
	 * <code>Number</code> クラスの派生オブジェクトの配列を、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static public <T extends Number> String valueArrayToPropertyString(T...values) {
		return objectArrayToPropertyString(values);
	}
	
	/**
	 * <code>Number</code> クラスの派生オブジェクトのコレクションを、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトのコレクション
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static public <T extends Number> String valueListToPropertyString(Collection<T> values) {
		return objectListToPropertyString(values);
	}
	
	/**
	 * <code>Boolean</code> オブジェクトの配列を、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static public String booleanArrayToPropertyString(Boolean...values) {
		return objectArrayToPropertyString(values);
	}
	
	/**
	 * <code>Boolean</code> オブジェクトのコレクションを、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトのコレクション
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static public String booleanListToPropertyString(Collection<? extends Boolean> values) {
		return objectListToPropertyString(values);
	}
	
	/**
	 * 文字列の配列を、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素の文字列をカンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static public String stringArrayToPropertyString(String...values) {
		return (values!=null ? Strings.buildCsvLine(values) : null);
	}
	
	/**
	 * 文字列のコレクションを、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素の文字列をカンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトのコレクション
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static public String stringListToPropertyString(Collection<? extends String> values) {
		return (values!=null ? Strings.buildCsvLine(values) : null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * オブジェクトの配列を、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static protected <T> String objectArrayToPropertyString(T...values) {
		String retValue = null;
		if (values != null) {
			if (values.length > 0) {
				StringBuilder sb = new StringBuilder();
				if (values[0] != null) {
					sb.append(values[0]);
				}
				for (int i = 1; i < values.length; i++) {
					sb.append(DefaultValueDelimiterChar);
					if (values[i] != null) {
						sb.append(values[i]);
					}
				}
				retValue = sb.toString();
			} else {
				retValue = "";
			}
		}
		return retValue;
	}
	
	/**
	 * オブジェクトのコレクションを、プロパティ値として格納可能な文字列に変換する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列を生成する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトのコレクション
	 * @return	変換結果の文字列を返す。<em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>values</em> の要素が空の場合は空文字列を返す。
	 */
	static protected <T> String objectListToPropertyString(Collection<T> values) {
		String retValue = null;
		if (values != null) {
			if (!values.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				Iterator<T> it = values.iterator();
				T val = it.next();
				if (val != null) {
					sb.append(val);
				}
				while (it.hasNext()) {
					sb.append(DefaultValueDelimiterChar);
					val = it.next();
					if (val != null) {
						sb.append(val);
					}
				}
				retValue = sb.toString();
			} else {
				retValue = "";
			}
		}
		return retValue;
	}

	/**
	 * プロパティの値変換時に発生したエラーの要因を示すエラーメッセージを生成する。
	 * @param value	プロパティから取得した値を示す文字列
	 * @param clazz	変換先のデータ型となる <code>Class</code> オブジェクト
	 * @param ex	変換時に発生した例外。<tt>null</tt> の場合は例外の情報を含めない
	 * @return	エラーメッセージ
	 * @throws NullPointerException	<em>value</em> もしくは <em>clazz</em> が <tt>null</tt> の場合
	 */
	static protected String getConversionErrorMessage(String value, Class<?> clazz, Throwable ex) {
		StringBuilder sb = new StringBuilder();
		sb.append("Failed to convert from \"");
		sb.append(value);
		sb.append("\" to '");
		sb.append(clazz.getName());
		sb.append("' class");
		String exmsg = ex.getMessage();
		if (exmsg != null) {
			sb.append(", cause ");
			sb.append(exmsg);
		}
		sb.append(".");
		return sb.toString();
	}

	/**
	 * 変換先データ型のオブジェクト生成時に発生したエラーの要因を示すエラーメッセージを生成する。
	 * @param clazz	変換先のデータ型となる <code>Class</code> オブジェクト
	 * @param ex	オブジェクト生成時に発生した例外。<tt>null</tt> の場合は例外の情報を含めない
	 * @return	エラーメッセージ
	 * @throws NullPointerException	<em>clazz</em> が <tt>null</tt> の場合
	 */
	static protected String getConstructErrorMessage(Class<?> clazz, Throwable ex) {
		StringBuilder sb = new StringBuilder();
		sb.append("Failed to construct '");
		sb.append(clazz.getName());
		sb.append("' class");
		String exmsg = ex.getMessage();
		sb.append(ex.getMessage());
		if (exmsg != null) {
			sb.append(", cause ");
			sb.append(exmsg);
		}
		sb.append(".");
		return sb.toString();
	}
}
