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
 * @(#)PropertiesUtil.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.properties;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Properties;

import ssac.util.Strings;

/**
 * <code>Properties</code> オブジェクトを操作するためのユーティリティ群。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class PropertiesUtil
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
	
	private PropertiesUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたキーのプロパティを除去する。
	 * @param prop	<code>Properties</code> オブジェクト
	 * @param key	除去するプロパティのキー
	 */
	static public void clearPropery(Properties prop, Object key) {
		prop.remove(key);
	}

	/**
	 * 指定された配列に含まれる全てのキーに対応するプロパティを除去する。
	 * @param prop	<code>Properties</code> オブジェクト
	 * @param keys	除去するプロパティのキーを格納する配列
	 */
	static public void clearProperties(Properties prop, Object...keys) {
		if (keys != null && keys.length > 0) {
			for (Object key : keys) {
				prop.remove(key);
			}
		}
	}

	/**
	 * 指定されたコレクションに含まれる全てのキーに対応するプロパティを除去する。
	 * @param prop	<code>Properties</code> オブジェクト
	 * @param keys	除去するプロパティのキーを格納するコレクション
	 */
	static public void clearProperties(Properties prop, Collection<?> keys) {
		if (keys != null && !keys.isEmpty()) {
			for (Object key : keys) {
				prop.remove(key);
			}
		}
	}
	
	//
	// 'get' interfaces
	//

	/**
	 * 指定されたキーに対応するプロパティの値を取得し、<em>clazz</em> に指定されたオブジェクトに変換する。
	 * キーもしくは値が存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param clazz			変換後の型を示す <code>Class</code> オブジェクト
	 * @param defaultValue	デフォルト値
	 * @return	変換後のオブジェクトを返す。キーもしくは値が存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public <T> T getPropertyValue(Properties prop, String key, Class<T> clazz, T defaultValue) {
		T retval = null;
		String strValue = prop.getProperty(key);
		if (strValue != null && strValue.length() > 0) {
			retval = PropertyConverter.toValue(clazz, strValue);
		}
		return (retval != null ? retval : defaultValue);
	}

	/**
	 * 指定されたキーに対応するプロパティの値を取得し、<em>clazz</em> に指定されたオブジェクトの配列に変換する。
	 * キーもしくは値が存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param clazz			変換後の型を示す <code>Class</code> オブジェクト
	 * @param defaultValues	デフォルト値
	 * @return	変換後のオブジェクトを格納する配列を返す。キーもしくは値が存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public <T> T[] getPropertyValueArray(Properties prop, String key, Class<T> clazz, T[] defaultValues) {
		T[] retary = defaultValues;
		String sval = prop.getProperty(key);
		if (sval != null) {
			retary = PropertyConverter.toValueArray(clazz, sval, PropertyConverter.DefaultValueDelimiterChar);
		}
		return retary;
	}
	
	//
	// 'get' for Objects
	//

	/**
	 * 指定されたキーに対応するプロパティ値を文字列として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 */
	static public String getString(Properties prop, String key, String defaultValue) {
		String sval = prop.getProperty(key);
		return (sval != null) ? sval : defaultValue;
	}

	/**
	 * 指定されたキーに対応するプロパティ値を取得し、カンマを区切り文字として分割した文字列配列として返す。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した文字列の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 */
	static public String[] getStringArray(Properties prop, String key, String[] defaultValues) {
		String[] retval = defaultValues;
		String sval = prop.getProperty(key);
		if (sval != null) {
			retval = Strings.parseCsvLine(sval);
		}
		return retval;
	}

	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Boolean</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 */
	static public Boolean getBoolean(Properties prop, String key, Boolean defaultValue) {
		String strValue = prop.getProperty(key);
		Boolean retval = (strValue != null ? new Boolean(strValue) : null);
		return (retval != null ? retval : defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Boolean</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 */
	static public Boolean[] getBooleanArray(Properties prop, String key, Boolean[] defaultValues) {
		return getPropertyValueArray(prop, key, Boolean.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Short</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Short getShort(Properties prop, String key, Short defaultValue) {
		return getPropertyValue(prop, key, Short.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Short</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Short[] getShortArray(Properties prop, String key, Short[] defaultValues) {
		return getPropertyValueArray(prop, key, Short.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Integer</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Integer getInteger(Properties prop, String key, Integer defaultValue) {
		return getPropertyValue(prop, key, Integer.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Integer</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Integer[] getIntegerArray(Properties prop, String key, Integer[] defaultValues) {
		return getPropertyValueArray(prop, key, Integer.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Long</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Long getLong(Properties prop, String key, Long defaultValue) {
		return getPropertyValue(prop, key, Long.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Long</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Long[] getLongArray(Properties prop, String key, Long[] defaultValues) {
		return getPropertyValueArray(prop, key, Long.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Float</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Float getFloat(Properties prop, String key, Float defaultValue) {
		return getPropertyValue(prop, key, Float.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Float</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Float[] getFloatArray(Properties prop, String key, Float[] defaultValues) {
		return getPropertyValueArray(prop, key, Float.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Double</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Double getDouble(Properties prop, String key, Double defaultValue) {
		return getPropertyValue(prop, key, Double.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Double</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Double[] getDoubleArray(Properties prop, String key, Double[] defaultValues) {
		return getPropertyValueArray(prop, key, Double.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigInteger</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public BigInteger getBigInteger(Properties prop, String key, BigInteger defaultValue) {
		return getPropertyValue(prop, key, BigInteger.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigInteger</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public BigInteger[] getBigIntegerArray(Properties prop, String key, BigInteger[] defaultValues) {
		return getPropertyValueArray(prop, key, BigInteger.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigDecimal</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public BigDecimal getBigDecimal(Properties prop, String key, BigDecimal defaultValue) {
		return getPropertyValue(prop, key, BigDecimal.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigDecimal</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public BigDecimal[] getBigDecimalArray(Properties prop, String key, BigDecimal[] defaultValues) {
		return getPropertyValueArray(prop, key, BigDecimal.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Point</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Point getPoint(Properties prop, String key, Point defaultValue) {
		Point retval = defaultValue;
		String sval = prop.getProperty(key);
		if (sval != null) {
			Integer[] values = PropertyConverter.toValueArray(Integer.class, sval, PropertyConverter.DefaultValueDelimiterChar);
			if (values.length==2 && values[0]!=null && values[1]!=null) {
				retval = new Point(values[0], values[1]);
			}
		}
		return retval;
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Dimension</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Dimension getDimension(Properties prop, String key, Dimension defaultValue) {
		Dimension retval = defaultValue;
		String sval = prop.getProperty(key);
		if (sval != null) {
			Integer[] values = PropertyConverter.toValueArray(Integer.class, sval, PropertyConverter.DefaultValueDelimiterChar);
			if (values.length==2 && values[0]!=null && values[1]!=null) {
				retval = new Dimension(values[0], values[1]);
			}
		}
		return retval;
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Rectangle</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public Rectangle getRectangle(Properties prop, String key, Rectangle defaultValue) {
		Rectangle retval = defaultValue;
		String sval = prop.getProperty(key);
		if (sval != null) {
			Integer[] values = PropertyConverter.toValueArray(Integer.class, sval, PropertyConverter.DefaultValueDelimiterChar);
			if (values.length==4 && values[0]!=null && values[1]!=null && values[2]!=null && values[3]!=null) {
				retval = new Rectangle(values[0], values[1], values[2], values[3]);
			}
		}
		return retval;
	}
	
	//
	// 'get' for Primitives
	//
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>boolean</code> 型の値として取得する。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <tt>false</tt> を返す。
	 */
	static public boolean getBooleanValue(Properties prop, String key) {
		return getBooleanValue(prop, key, false);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>boolean</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 */
	static public boolean getBooleanValue(Properties prop, String key, boolean defaultValue) {
		Boolean pv = getBoolean(prop, key, Boolean.valueOf(defaultValue));
		return pv.booleanValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>short</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public short getShortValue(Properties prop, String key, short defaultValue) {
		Short pv = getShort(prop, key, defaultValue);
		return pv.shortValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>int</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public int getIntegerValue(Properties prop, String key, int defaultValue) {
		Integer pv = getInteger(prop, key, defaultValue);
		return pv.intValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>long</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public long getLongValue(Properties prop, String key, long defaultValue) {
		Long pv = getLong(prop, key, defaultValue);
		return pv.longValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>float</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public float getFloatValue(Properties prop, String key, float defaultValue) {
		Float pv = getFloat(prop, key, defaultValue);
		return pv.floatValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>double</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	static public double getDoubleValue(Properties prop, String key, double defaultValue) {
		Double pv = getDouble(prop, key, defaultValue);
		return pv.doubleValue();
	}
	
	//
	// 'set' interfaces
	//

	/**
	 * <code>Number</code> クラスの派生オブジェクトの配列を、指定されたキーのプロパティ値に設定する。
	 * 配列が <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 */
	static public <T extends Number> void setPropertyValueArray(Properties prop, String key, T...values) {
		prop.setProperty(key, PropertyConverter.valueArrayToPropertyString(values));
	}
	
	/**
	 * <code>Number</code> クラスの派生オブジェクトのコレクションを、指定されたキーのプロパティ値に設定する。
	 * コレクションが <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 */
	static public <T extends Number> void setPropertyValueList(Properties prop, String key, Collection<T> values) {
		prop.setProperty(key, PropertyConverter.valueListToPropertyString(values));
	}
	
	//
	// 'set' for Objects
	//

	/**
	 * 文字列を指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setString(Properties prop, String key, String value) {
		prop.setProperty(key, value);
	}
	
	/**
	 * 文字列の配列を、指定されたキーのプロパティ値に設定する。
	 * 配列が <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 */
	static public void setStringArray(Properties prop, String key, String...values) {
		if (values != null && values.length > 0) {
			prop.setProperty(key, Strings.buildCsvLine(values));
		} else {
			prop.setProperty(key, null);
		}
	}
	
	/**
	 * 文字列のコレクションを、指定されたキーのプロパティ値に設定する。
	 * コレクションが <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 */
	static public void setStringList(Properties prop, String key, Collection<? extends String> values) {
		if (values != null && !values.isEmpty()) {
			prop.setProperty(key, Strings.buildCsvLine(values));
		} else {
			prop.setProperty(key, null);
		}
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setBooleanValue(Properties prop, String key, boolean value) {
		setPropertyValue(prop, key, Boolean.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setShortValue(Properties prop, String key, short value) {
		setPropertyValue(prop, key, Short.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setIntegerValue(Properties prop, String key, int value) {
		setPropertyValue(prop, key, Integer.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setLongValue(Properties prop, String key, long value) {
		setPropertyValue(prop, key, Long.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setFloatValue(Properties prop, String key, float value) {
		setPropertyValue(prop, key, Float.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setDoubleValue(Properties prop, String key, double value) {
		setPropertyValue(prop, key, Double.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setBoolean(Properties prop, String key, Boolean value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * <code>Boolean</code> オブジェクトの配列を、指定されたキーのプロパティ値に設定する。
	 * 配列が <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 */
	static public void setBooleanArray(Properties prop, String key, Boolean...values) {
		prop.setProperty(key, PropertyConverter.booleanArrayToPropertyString(values));
	}
	
	/**
	 * <code>Boolean</code> オブジェクトのコレクションを、指定されたキーのプロパティ値に設定する。
	 * コレクションが <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 */
	static public void setBooleanList(Properties prop, String key, Collection<? extends Boolean> values) {
		prop.setProperty(key, PropertyConverter.booleanListToPropertyString(values));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setShort(Properties prop, String key, Short value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setInteger(Properties prop, String key, Integer value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setLong(Properties prop, String key, Long value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setFloat(Properties prop, String key, Float value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setDouble(Properties prop, String key, Double value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setBigInteger(Properties prop, String key, BigInteger value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setBigDecimal(Properties prop, String key, BigDecimal value) {
		setPropertyValue(prop, key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setPoint(Properties prop, String key, Point value) {
		if (value != null) {
			prop.setProperty(key, String.format("%d%c%d", value.x, PropertyConverter.DefaultValueDelimiterChar, value.y));
		} else {
			prop.setProperty(key, null);
		}
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setDimension(Properties prop, String key, Dimension value) {
		if (value != null) {
			prop.setProperty(key, String.format("%d%c%d", value.width, PropertyConverter.DefaultValueDelimiterChar, value.height));
		} else {
			prop.setProperty(key, null);
		}
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static public void setRectangle(Properties prop, String key, Rectangle value) {
		if (value != null) {
			prop.setProperty(key, String.format("%d%c%d%c%d%c%d", value.x,
					PropertyConverter.DefaultValueDelimiterChar, value.y,
					PropertyConverter.DefaultValueDelimiterChar, value.width,
					PropertyConverter.DefaultValueDelimiterChar, value.height));
		} else {
			prop.setProperty(key, null);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param prop		<code>Properties</code> オブジェクト
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	static protected void setPropertyValue(Properties prop, String key, Object value) {
		if (value != null) {
			prop.setProperty(key, value.toString());
		} else {
			prop.remove(key);
		}
	}
}
