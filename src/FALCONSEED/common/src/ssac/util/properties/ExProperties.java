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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ExProperties.java	2.0.0	2012/10/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExProperties.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExProperties.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.properties;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import ssac.util.Strings;


/**
 * プロパティテーブル。
 * プロパティ・データモデルをプロパティ・データの実体として保持する。
 * 
 * @version 2.0.0	2012/10/31
 */
public class ExProperties
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//static public char defaultValueDelimiterChar = ',';
	
	protected final IExPropertyModel propModel;
	protected final ExProperties propDefaults;
	
//	protected Properties prop;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExProperties() {
		this(new JavaPropertiesModel());
	}
	
	public ExProperties(ExProperties defaults) {
		this(new JavaPropertiesModel(), null);
	}
	
	public ExProperties(IExPropertyModel model) {
		this(model, null);
	}
	
	public ExProperties(IExPropertyModel model, ExProperties defaults) {
		if (model == null)
			throw new NullPointerException("model");
		
		this.propModel = model;
		this.propDefaults = defaults;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたプロパティと内容が等しい場合に <tt>true</tt> を返す。
	 * <p>このメソッドではデフォルトの内容を比較される。
	 * @param otherProperties	比較するプロパティ
	 * @return 内容が等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.0.0
	 */
	public boolean equalsProperties(ExProperties otherProperties) {
		if (otherProperties == null)
			return false;
		if (this == otherProperties)
			return true;
		
		if (propModel.equals(otherProperties.propModel)) {
			if (propDefaults==otherProperties.propDefaults) {
				return true;
			}
			else if (propDefaults != null && propDefaults.equalsProperties(otherProperties.propDefaults)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	/**
	 * 指定されたプロパティと内容が等しい場合に <tt>true</tt> を返す。
	 * <p>このメソッドでは、デフォルトのプロパティは比較対象には含まない。
	 * @param otherProperties	比較するプロパティ
	 * @return 内容が等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.0.0
	 */
	public boolean equalsPropertiesWithoutDefaults(ExProperties otherProperties) {
		if (otherProperties == null)
			return false;
		if (this == otherProperties)
			return true;
		
		return propModel.equals(otherProperties.propModel);
	}

	/**
	 * 全てのプロパティを除去する。
	 */
	public void clear() {
		propModel.clear();
	}
	
	/**
	 * 指定されたキーのプロパティを除去する。
	 * @param key	除去するプロパティのキー
	 */
	public void clearProperty(String key) {
		propModel.clearProperty(key);
	}

	/**
	 * 指定された配列に含まれる全てのキーに対応するプロパティを除去する。
	 * @param keys	除去するプロパティのキーを格納する配列
	 * @since 1.17
	 */
	public void clearProperties(String...keys) {
		if (keys != null && keys.length > 0) {
			for (String key : keys) {
				propModel.clearProperty(key);
			}
		}
	}

//	/**
//	 * 指定されたコレクションに含まれる全てのキーに対応するプロパティを除去する。
//	 * @param keys	除去するプロパティのキーを格納するコレクション
//	 * @since 1.17
//	 */
//	public void clearProperties(Collection<? extends String> keys) {
//		if (keys != null && !keys.isEmpty()) {
//			for (String key : keys) {
//				//prop.remove(key);
//				propModel.clearProperty(key);
//			}
//		}
//	}

	/**
	 * 指定された文字列がキーの先頭と一致するもののみ、そのプロパティを除去する。
	 * <em>keyPrefix</em> が <tt>null</tt> もしくは空文字列の場合、このメソッドは何も行わない。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドでは、デフォルトのプロパティは変更されない。
	 * </blockquote>
	 * @param keyPrefix		除去するキーのプレフィックス
	 * @since 2.0.0
	 */
	public void clearPropertiesByKeyPrefix(String keyPrefix) {
		Iterator<Object> it = propModel.keySet().iterator();
		
		while (it.hasNext()) {
			Object key = it.next();
			if (key instanceof String) {
				if (((String)key).startsWith(keyPrefix)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * 指定されたキーがプロパティに存在しているかを検証する。
	 * @param key	判定するキー
	 * @return	キーが存在していれば <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean containsKey(String key) {
		return propModel.containsKey(key);
	}

	/**
	 * 指定されたキーに対応する値をそのまま取得する。
	 * 指定されたキーが見つからない場合は、デフォルトのプロパティテーブルから取得する。
	 * @param key	キー
	 * @return	取得したプロパティ値となる文字列を返す。どのプロパティテーブルにもキーが存在しない場合は <tt>null</tt> を返す。
	 */
	public String getProperty(String key) {
		String sval = propModel.getValue(key);
		return ((sval == null) && (propDefaults != null)) ? propDefaults.getProperty(key) : sval;
	}

	/**
	 * 指定されたキーと値を、このプロパティテーブルに設定する。
	 * 設定する値がデフォルトのプロパティテーブルの値と等しい場合、
	 * このプロパティテーブルのみからキー（および値）を除去する。
	 * @param key	キー
	 * @param value	値
	 * @throws NullPointerException	<em>key</em> もしくは <em>value</em> が <tt>null</tt> の場合
	 */
	public void setProperty(String key, String value) {
		if (isDefaultValue(key, value)) {
			// same default value
			propModel.clearProperty(key);
		} else {
			// new value
			propModel.setValue(key, value);
		}
	}

	/**
	 * 指定されたファイルから、このプロパティテーブルにプロパティを読み込む。
	 * @param propFile		読み込むファイル
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void loadFile(File propFile) throws IOException {
		propModel.loadFile(propFile);
	}

	/**
	 * 指定されたファイルへ、このプロパティテーブルの内容を書き込む。
	 * デフォルトのプロパティテーブルの内容は出力されない。
	 * @param propFile		書き込み先のファイル
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void saveFile(File propFile) throws IOException {
		propModel.saveFile(propFile, null);
	}

	/**
	 * 指定されたファイルへ、このプロパティテーブルの内容を書き込む。
	 * デフォルトのプロパティテーブルの内容は出力されない。
	 * @param propFile		書き込み先のファイル
	 * @param comments		コメント
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void saveFile(File propFile, String comments) throws IOException {
		propModel.saveFile(propFile, comments);
	}
	
	/**
	 * 指定されたストリームから、このプロパティテーブルにプロパティを読み込む。
	 * @param inStream		読み込むストリーム
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void loadFromStream(InputStream inStream) throws IOException {
		propModel.loadFromStream(inStream);
	}
	
	/**
	 * 指定されたストリームへ、このプロパティテーブルの内容を書き込む。
	 * デフォルトのプロパティテーブルの内容は出力されない。
	 * @param outStream		書き込み先のストリーム
	 * @param comments		コメント
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void saveToStream(OutputStream outStream, String comments) throws IOException {
		propModel.saveToStream(outStream, comments);
	}
	
	//
	// 'get' interfaces
	//
	
	//
	// 'get' for Objects
	//

	/**
	 * 指定されたキーに対応するプロパティ値を文字列として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 */
	public String getString(String key, String defaultValue) {
		String sval = getProperty(key);
		return (sval != null) ? sval : defaultValue;
	}

	/**
	 * 指定されたキーに対応するプロパティ値を取得し、カンマを区切り文字として分割した文字列配列として返す。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した文字列の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 */
	public String[] getStringArray(String key, String[] defaultValues) {
		String[] retval = defaultValues;
		String sval = getProperty(key);
		if (sval != null) {
			retval = Strings.parseCsvLine(sval);
		}
		return retval;
	}

	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Boolean</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 */
	public Boolean getBoolean(String key, Boolean defaultValue) {
		String strValue = getProperty(key);
		Boolean retval = (strValue != null ? new Boolean(strValue) : null);
		return (retval != null ? retval : defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Boolean</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @since 1.17
	 */
	public Boolean[] getBooleanArray(String key, Boolean[] defaultValues) {
		return getPropertyValueArray(key, Boolean.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Short</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public Short getShort(String key, Short defaultValue) {
		return getPropertyValue(key, Short.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Short</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public Short[] getShortArray(String key, Short[] defaultValues) {
		return getPropertyValueArray(key, Short.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Integer</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		return getPropertyValue(key, Integer.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Integer</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public Integer[] getIntegerArray(String key, Integer[] defaultValues) {
		return getPropertyValueArray(key, Integer.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Long</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public Long getLong(String key, Long defaultValue) {
		return getPropertyValue(key, Long.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Long</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public Long[] getLongArray(String key, Long[] defaultValues) {
		return getPropertyValueArray(key, Long.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Float</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public Float getFloat(String key, Float defaultValue) {
		return getPropertyValue(key, Float.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Float</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public Float[] getFloatArray(String key, Float[] defaultValues) {
		return getPropertyValueArray(key, Float.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Double</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public Double getDouble(String key, Double defaultValue) {
		return getPropertyValue(key, Double.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Double</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public Double[] getDoubleArray(String key, Double[] defaultValues) {
		return getPropertyValueArray(key, Double.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigInteger</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		return getPropertyValue(key, BigInteger.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigInteger</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public BigInteger[] getBigIntegerArray(String key, BigInteger[] defaultValues) {
		return getPropertyValueArray(key, BigInteger.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigDecimal</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		return getPropertyValue(key, BigDecimal.class, defaultValue);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>BigDecimal</code> オブジェクトの配列として取得する。
	 * キーが存在しない場合は、<em>defaultValues</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValues	デフォルト値
	 * @return	取得した値の配列を返す。キーが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public BigDecimal[] getBigDecimalArray(String key, BigDecimal[] defaultValues) {
		return getPropertyValueArray(key, BigDecimal.class, defaultValues);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>Point</code> オブジェクトとして取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public Point getPoint(String key, Point defaultValue) {
		Point retval = defaultValue;
		String sval = getProperty(key);
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
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	public Dimension getDimension(String key, Dimension defaultValue) {
		Dimension retval = defaultValue;
		String sval = getProperty(key);
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
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public Rectangle getRectangle(String key, Rectangle defaultValue) {
		Rectangle retval = defaultValue;
		String sval = getProperty(key);
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
	 * @param key			プロパティのキー
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <tt>false</tt> を返す。
	 */
	public boolean getBooleanValue(String key) {
		return getBooleanValue(key, false);
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>boolean</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param prop			<code>Properties</code> オブジェクト
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 */
	public boolean getBooleanValue(String key, boolean defaultValue) {
		Boolean pv = getBoolean(key, Boolean.valueOf(defaultValue));
		return pv.booleanValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>short</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public short getShortValue(String key, short defaultValue) {
		Short pv = getShort(key, defaultValue);
		return pv.shortValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>int</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public int getIntegerValue(String key, int defaultValue) {
		Integer pv = getInteger(key, defaultValue);
		return pv.intValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>long</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public long getLongValue(String key, long defaultValue) {
		Long pv = getLong(key, defaultValue);
		return pv.longValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>float</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public float getFloatValue(String key, float defaultValue) {
		Float pv = getFloat(key, defaultValue);
		return pv.floatValue();
	}
	
	/**
	 * 指定されたキーに対応するプロパティ値を、<code>double</code> 型の値として取得する。
	 * キーが存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param defaultValue	デフォルト値
	 * @return	取得したプロパティ値を返す。キーが存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 * @since 1.17
	 */
	public double getDoubleValue(String key, double defaultValue) {
		Double pv = getDouble(key, defaultValue);
		return pv.doubleValue();
	}
	
	//
	// 'set' interfaces
	//

	/**
	 * <code>Number</code> クラスの派生オブジェクトの配列を、指定されたキーのプロパティ値に設定する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列として格納する。
	 * <em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * <em>values</em> の要素が空の場合は空文字列を格納する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @since 1.17
	 */
	public <T extends Number> void setPropertyValueArray(String key, T...values) {
		setProperty(key, PropertyConverter.valueArrayToPropertyString(values));
	}
	
	/**
	 * <code>Number</code> クラスの派生オブジェクトのコレクションを、指定されたキーのプロパティ値に設定する。
	 * このメソッドは、要素オブジェクトを文字列に変換し、カンマで連結した文字列として格納する。
	 * <em>values</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * <em>values</em> の要素が空の場合は空文字列を格納する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトのコレクション
	 * @since 1.17
	 */
	public <T extends Number> void setPropertyValueList(String key, Collection<T> values) {
		setProperty(key, PropertyConverter.valueListToPropertyString(values));
	}
	
	//
	// 'set' for Objects
	//

	/**
	 * 文字列を指定されたキーのプロパティ値に設定する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setString(String key, String value) {
		setProperty(key, value);
	}
	
	/**
	 * 文字列の配列を、指定されたキーのプロパティ値に設定する。
	 * 配列が <tt>null</tt>の場合は <tt>null</tt> を設定する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 */
	public void setStringArray(String key, String...values) {
		if (values != null) {
			setProperty(key, Strings.buildCsvLine(values));
		} else {
			setProperty(key, null);
		}
	}
	
	/**
	 * 文字列のコレクションを、指定されたキーのプロパティ値に設定する。
	 * コレクションが <tt>null</tt>の場合は <tt>null</tt> を設定する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @since 1.17
	 */
	public void setStringList(String key, Collection<? extends String> values) {
		if (values != null) {
			setProperty(key, Strings.buildCsvLine(values));
		} else {
			setProperty(key, null);
		}
	}

	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 * @since 1.17
	 */
	public void setBooleanValue(String key, boolean value) {
		setPropertyValue(key, Boolean.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 * @since 1.17
	 */
	public void setShortValue(String key, short value) {
		setPropertyValue(key, Short.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 * @since 1.17
	 */
	public void setIntegerValue(String key, int value) {
		setPropertyValue(key, Integer.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 * @since 1.17
	 */
	public void setLongValue(String key, long value) {
		setPropertyValue(key, Long.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 * @since 1.17
	 */
	public void setFloatValue(String key, float value) {
		setPropertyValue(key, Float.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 * @since 1.17
	 */
	public void setDoubleValue(String key, double value) {
		setPropertyValue(key, Double.valueOf(value));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setBoolean(String key, Boolean value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * <code>Boolean</code> オブジェクトの配列を、指定されたキーのプロパティ値に設定する。
	 * 配列が <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @since 1.17
	 */
	public void setBooleanArray(String key, Boolean...values) {
		setProperty(key, PropertyConverter.booleanArrayToPropertyString(values));
	}
	
	/**
	 * <code>Boolean</code> オブジェクトのコレクションを、指定されたキーのプロパティ値に設定する。
	 * コレクションが <tt>null</tt> もしくは要素が空の場合は、指定されたキーに対応するプロパティを除去する。
	 * @param key			プロパティのキー
	 * @param values		プロパティ値とするオブジェクトの配列
	 * @since 1.17
	 */
	public void setBooleanList(String key, Collection<? extends Boolean> values) {
		setProperty(key, PropertyConverter.booleanListToPropertyString(values));
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setShort(String key, Short value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setInteger(String key, Integer value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setLong(String key, Long value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setFloat(String key, Float value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setDouble(String key, Double value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setBigInteger(String key, BigInteger value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setBigDecimal(String key, BigDecimal value) {
		setPropertyValue(key, value);
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setPoint(String key, Point value) {
		if (value != null) {
			setProperty(key, String.format("%d%c%d", value.x, PropertyConverter.DefaultValueDelimiterChar, value.y));
		} else {
			setProperty(key, null);
		}
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 */
	public void setDimension(String key, Dimension value) {
		if (value != null) {
			setProperty(key, String.format("%d%c%d", value.width, PropertyConverter.DefaultValueDelimiterChar, value.height));
		} else {
			setProperty(key, null);
		}
	}
	
	/**
	 * 指定された値を、指定されたキーのプロパティ値に設定する。
	 * 値が <tt>null</tt> の場合、指定されたキーに対応するプロパティを除去する。
	 * @param key		プロパティのキー
	 * @param value		プロパティの値
	 * @since 1.17
	 */
	public void setRectangle(String key, Rectangle value) {
		if (value != null) {
			setProperty(key, String.format("%d%c%d%c%d%c%d", value.x,
					PropertyConverter.DefaultValueDelimiterChar, value.y,
					PropertyConverter.DefaultValueDelimiterChar, value.width,
					PropertyConverter.DefaultValueDelimiterChar, value.height));
		} else {
			setProperty(key, null);
		}
	}

	//------------------------------------------------------------
	// Public 'get' interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public 'set' interfaces
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたキーに対応するプロパティの値を取得し、<em>clazz</em> に指定されたオブジェクトに変換する。
	 * キーもしくは値が存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param clazz			変換後の型を示す <code>Class</code> オブジェクト
	 * @param defaultValue	デフォルト値
	 * @return	変換後のオブジェクトを返す。キーもしくは値が存在しない場合は <em>defaultValue</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	protected <T> T getPropertyValue(String key, Class<T> clazz, T defaultValue) {
		T retval = null;
		String strValue = getProperty(key);
		if (strValue != null && strValue.length() > 0) {
			retval = PropertyConverter.toValue(clazz, strValue);
		}
		return (retval != null ? retval : defaultValue);
	}

	/**
	 * 指定されたキーに対応するプロパティの値を取得し、<em>clazz</em> に指定されたオブジェクトの配列に変換する。
	 * キーもしくは値が存在しない場合は、<em>defaultValue</em> で指定された値を返す。
	 * @param key			プロパティのキー
	 * @param clazz			変換後の型を示す <code>Class</code> オブジェクト
	 * @param defaultValues	デフォルト値
	 * @return	変換後のオブジェクトを格納する配列を返す。キーもしくは値が存在しない場合は <em>defaultValues</em> を返す。
	 * @throws PropertyConversionException	変換に失敗した場合
	 */
	protected <T> T[] getPropertyValueArray(String key, Class<T> clazz, T[] defaultValues) {
		T[] retary = defaultValues;
		String sval = getProperty(key);
		if (sval != null) {
			retary = PropertyConverter.toValueArray(clazz, sval, PropertyConverter.DefaultValueDelimiterChar);
		}
		return retary;
	}

	protected void setPropertyValue(String key, Object value) {
		setProperty(key, (value != null ? value.toString() : null));
	}
	
	protected boolean isDefaultValue(String key, String value) {
		// has default properties?
		if (propDefaults == null)
			return false;	// no defaults
		
		// compare to default value
		String defval = propDefaults.getProperty(key);
		if (value != null) {
			return value.equals(defval);
		} else {
			return (defval == null);
		}
	}
}
