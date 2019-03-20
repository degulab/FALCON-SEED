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
 * @(#)AADLFunctions.java	2.2.0	2015/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLFunctions.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLFunctions.java	2.0.0	2014/03/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLFunctions.java	1.90	2013/08/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLFunctions.java	1.80	2012/06/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLFunctions.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ssac.aadl.runtime.mqtt.MqttCsvParameter;
import ssac.aadl.runtime.util.range.DecimalRange;
import ssac.aadl.runtime.util.range.NaturalNumberDecimalRange;
import ssac.aadl.runtime.util.range.Range;
import ssac.aadl.runtime.util.range.SimpleDecimalRange;
import dtalge.DtAlgeSet;
import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.DtStringThesaurus;
import dtalge.Dtalge;
import exalge2.ExAlgeSet;
import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.ExTransfer;
import exalge2.Exalge;
import exalge2.TransDivideRatios;
import exalge2.TransMatrix;
import exalge2.TransTable;

/**
 * AADL組み込み関数
 * 
 * @version 2.2.0	2015/05/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class AADLFunctions
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** テキストファイルのデフォルトエンコーディング用プロパティキー **/
	static public final String PROPKEY_AADL_TXT_ENCODING = "aadl.txt.encoding";
	/** CSVファイルのデフォルトエンコーディング用プロパティキー **/
	static public final String PROPKEY_AADL_CSV_ENCODING = "aadl.csv.encoding";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLFunctions() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトが <tt>null</tt> 値であれば <tt>true</tt> を返します。
	 * @param obj	半定数rのオブジェクト
	 * @return	指定されたオブジェクトが <tt>null</tt> 値であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.30
	 */
	static public boolean isNull(Object obj) {
		return (obj == null);
	}

	/**
	 * 指定された文字列が空文字列(長さ 0 の文字列)かを判定します。
	 * @param str	文字列
	 * @return	空文字列もしくは <tt>null</tt> の場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.30
	 */
	static public boolean isEmpty(String str) {
		return (str==null || str.length()<=0);
	}
	
	/**
	 * 交換代数元(<em>alge</em>)の要素が空である場合に <tt>true</tt> を返します。
	 * @param alge 交換代数元
	 * @return	交換代数元に要素が存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#isEmpty()
	 * 
	 * @since 1.00
	 */
	static public boolean isEmpty(Exalge alge) {
		return alge.isEmpty();
	}

	/**
	 * 振替変換テーブル(<em>table</em>)の要素が空である場合に <tt>true</tt> を返します。
	 * @param table	振替変換テーブル
	 * @return	振替変換テーブルに要素が存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransTable#isEmpty()
	 * 
	 * @since 1.22
	 */
	static public boolean isEmpty(TransTable table) {
		return table.isEmpty();
	}

	/**
	 * 按分変換テーブル(<em>matrix</em>)の要素が空である場合に <tt>true</tt> を返します。
	 * @param matrix	按分変換テーブル
	 * @return	按分変換テーブルに要素が存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransMatrix#isEmpty()
	 * 
	 * @since 1.22
	 */
	static public boolean isEmpty(TransMatrix matrix) {
		return matrix.isEmpty();
	}
	
	/**
	 * 変換テーブル(<em>transfer</em>)の要素が空である場合に <tt>true</tt> を返します。
	 * @param transfer	変換テーブル
	 * @return	変換テーブルに要素が存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#isEmpty()
	 * 
	 * @since 1.23
	 */
	static public boolean isEmpty(ExTransfer transfer) {
		return transfer.isEmpty();
	}

	/**
	 * 指定されたコレクションの要素が空である場合に <tt>true</tt> を返します。
	 * @param c 検査するコレクション
	 * @return コレクションに要素が存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see java.util.Collection#isEmpty()
	 * 
	 * @since 1.00
	 */
	static public boolean isEmpty(Collection<?> c) {
		return c.isEmpty();
	}
	
	/*
	 * ExBase
	 */

	/**
	 * 交換代数基底(<em>base</em>)がハットなし基底の場合に <tt>true</tt> を返します。
	 * @param base	交換代数基底
	 * @return	ハットなし基底であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#isNoHat()
	 * 
	 * @since 1.10
	 */
	static public boolean isNoHat(ExBase base) {
		return base.isNoHat();
	}

	/**
	 * 交換代数基底(<em>base</em>)がハット基底の場合に <tt>true</tt> を返します。
	 * @param base	交換代数基底
	 * @return	ハット基底であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#isHat()
	 * 
	 * @since 1.10
	 */
	static public boolean isHat(ExBase base) {
		return base.isHat();
	}

	/**
	 * 交換代数基底(<em>base</em>)から、名前キー文字列を取得します。
	 * @param base	交換代数基底
	 * @return 名前キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#getNameKey()
	 * 
	 * @since 1.10
	 */
	static public String getNameKey(ExBase base) {
		return base.getNameKey();
	}
	
	/**
	 * 交換代数基底(<em>base</em>)から、単位キー文字列を取得します。
	 * @param base	交換代数基底
	 * @return 単位キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#getUnitKey()
	 * 
	 * @since 1.10
	 */
	static public String getUnitKey(ExBase base) {
		return base.getUnitKey();
	}
	
	/**
	 * 交換代数基底(<em>base</em>)から、時間キー文字列を取得します。
	 * @param base	交換代数基底
	 * @return 時間キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#getTimeKey()
	 * 
	 * @since 1.10
	 */
	static public String getTimeKey(ExBase base) {
		return base.getTimeKey();
	}
	
	/**
	 * 交換代数基底(<em>base</em>)から、主体キー文字列を取得します。
	 * @param base	交換代数基底
	 * @return 主体キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#getSubjectKey()
	 * 
	 * @since 1.10
	 */
	static public String getSubjectKey(ExBase base) {
		return base.getSubjectKey();
	}

	/**
	 * 交換代数元(<em>alge</em>)に含まれる全ての基底の集合を返します。
	 * 交換代数元が要素を持たない場合、要素が空の交換代数基底集合を返します。
	 * @param alge 交換代数元
	 * @return 交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#getBases()
	 * 
	 * @since 1.10
	 */
	static public ExBaseSet getBases(Exalge alge) {
		return alge.getBases();
	}

	/**
	 * 交換代数集合(<em>algeset</em>)に含まれる全ての基底の集合を返します。
	 * 異なる交換代数元に同じ基底が含まれている場合でも、返される交換代数基底集合内では
	 * 基底は重複しません。交換代数集合の要素が空の場合、要素が空の交換代数基底集合を返します。
	 * @param algeset 交換代数集合
	 * @return 交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#getBases()
	 * 
	 * @since 1.10
	 */
	static public ExBaseSet getBases(ExAlgeSet algeset) {
		return algeset.getBases();
	}

	/**
	 * 交換代数元(<em>alge</em>)から、基底を 1 つだけ抽出します。
	 * 交換代数元に要素が複数含まれている場合は、どの基底を取得するかは指定できません。
	 * 交換代数元の要素が空の場合は例外をスローします。
	 * @param alge	交換代数元
	 * @return	交換代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws java.util.NoSuchElementException	交換代数元の要素が空の場合
	 * @see exalge2.Exalge#getOneBase()
	 * 
	 * @since 1.22
	 */
	static public ExBase getOneBase(Exalge alge) {
		return alge.getOneBase();
	}

	/**
	 * 交換代数基底(<em>base</em>)と同じ基底キーを持つ、新しいハットなし基底を返します。
	 * @param base	交換代数基底
	 * @return	ハットなし交換代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#removeHat()
	 * 
	 * @since 1.22
	 */
	static public ExBase removeHat(ExBase base) {
		return base.removeHat();
	}

	/**
	 * 交換代数基底集合(<em>baseset</em>)に含まれる全てのハット基底をハットなし基底に
	 * 置き換えた、新しい交換代数基底集合を返します。
	 * @param baseset	交換代数基底集合
	 * @return	ハットなし基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBaseSet#removeHat()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet removeHat(ExBaseSet baseset) {
		return baseset.removeHat();
	}

	/**
	 * 交換代数基底(<em>base</em>)と同じ基底キーを持つ、新しいハット基底を返します。
	 * @param base	交換代数基底
	 * @return	ハット交換代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBase#setHat()
	 * 
	 * @since 1.22
	 */
	static public ExBase setHat(ExBase base) {
		return base.setHat();
	}

	/**
	 * 交換代数基底集合(<em>baseset</em>)に含まれる全てのハットなし基底をハット基底に
	 * 置き換えた、新しい交換代数基底集合を返します。
	 * @param baseset	交換代数基底集合
	 * @return	ハット基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBaseSet#setHat()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet setHat(ExBaseSet baseset) {
		return baseset.setHat();
	}

	/**
	 * 交換代数基底集合(<em>baseset</em>)から、全てのハットなし基底を取得します。
	 * @param baseset	交換代数基底集合
	 * @return	ハットなし基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBaseSet#getNoHatBases()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getNoHatBases(ExBaseSet baseset) {
		return baseset.getNoHatBases();
	}
	
	/**
	 * 交換代数元(<em>alge</em>)から、全てのハットなし基底を取得します。
	 * @param alge	交換代数元
	 * @return	ハットなし基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#getNoHatBases()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getNoHatBases(Exalge alge) {
		return alge.getNoHatBases();
	}
	
	/**
	 * 交換代数集合(<em>algeset</em>)から、全てのハットなし基底を取得します。
	 * @param algeset	交換代数集合
	 * @return	ハットなし基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#getNoHatBases()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getNoHatBases(ExAlgeSet algeset) {
		return algeset.getNoHatBases();
	}
	
	/**
	 * 交換代数基底集合(<em>baseset</em>)から、全てのハット基底を取得します。
	 * @param baseset	交換代数基底集合
	 * @return	ハット基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBaseSet#getHatBases()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getHatBases(ExBaseSet baseset) {
		return baseset.getHatBases();
	}
	
	/**
	 * 交換代数元(<em>alge</em>)から、全てのハット基底を取得します。
	 * @param alge	交換代数元
	 * @return	ハット基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#getHatBases()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getHatBases(Exalge alge) {
		return alge.getHatBases();
	}
	
	/**
	 * 交換代数集合(<em>algeset</em>)から、全てのハット基底を取得します。
	 * @param algeset	交換代数集合
	 * @return	ハット基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#getHatBases()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getHatBases(ExAlgeSet algeset) {
		return algeset.getHatBases();
	}

	/**
	 * 交換代数元(<em>alge</em>)に含まれる全てのハット基底をハットなし基底に置き換えた、
	 * 新しい交換代数基底集合を返します。
	 * @param alge	交換代数元
	 * @return	ハットなし基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#getBasesWithRemoveHat()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getBasesWithRemoveHat(Exalge alge) {
		return alge.getBasesWithRemoveHat();
	}
	
	/**
	 * 交換代数集合(<em>algeset</em>)に含まれる全てのハット基底をハットなし基底に置き換えた、
	 * 新しい交換代数基底集合を返します。
	 * @param algeset	交換代数集合
	 * @return	ハットなし基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#getBasesWithRemoveHat()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getBasesWithRemoveHat(ExAlgeSet algeset) {
		return algeset.getBasesWithRemoveHat();
	}
	
	/**
	 * 交換代数元(<em>alge</em>)に含まれる全てのハットなし基底をハット基底に置き換えた、
	 * 新しい交換代数基底集合を返します。
	 * @param alge	交換代数元
	 * @return	ハット基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#getBasesWithSetHat()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getBasesWithSetHat(Exalge alge) {
		return alge.getBasesWithSetHat();
	}
	
	/**
	 * 交換代数集合(<em>algeset</em>)に含まれる全てのハットなし基底をハット基底に置き換えた、
	 * 新しい交換代数基底集合を返します。
	 * @param algeset	交換代数集合
	 * @return	ハット基底のみを含む交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#getBasesWithSetHat()
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet getBasesWithSetHat(ExAlgeSet algeset) {
		return algeset.getBasesWithSetHat();
	}
	
	/*
	 * ExBasePattern
	 */

	/**
	 * 指定された名前キーを持つ、新しい基底パターンオブジェクトを生成します。
	 * 指定されない単位キー、時間キー、主体キー、ハットキーは、ワイルドカード記号('*')となります。
	 * @param name		名前キー
	 * @return	交換代数基底パターン
	 * @see exalge2.ExBasePattern#ExBasePattern(String, String)
	 * 
	 * @since 1.30
	 */
	static public ExBasePattern newExBasePattern(String name) {
		return new ExBasePattern(name, ExBasePattern.WILDCARD);
	}

	/**
	 * 指定された名前キー、単位キーを持つ、新しい基底パターンオブジェクトを生成します。
	 * 指定されない時間キー、主体キー、ハットキーは、ワイルドカード記号('*')となります。
	 * @param name		名前キー
	 * @param unit		単位キー
	 * @return	交換代数基底パターン
	 * @see exalge2.ExBasePattern#ExBasePattern(String, String, String)
	 * 
	 * @since 1.30
	 */
	static public ExBasePattern newExBasePattern(String name, String unit) {
		return new ExBasePattern(name, ExBasePattern.WILDCARD, unit);
	}

	/**
	 * 指定された名前キー、単位キー、時間キーを持つ、新しい基底パターンオブジェクトを生成します。
	 * 指定されない主体キー、ハットキーは、ワイルドカード記号('*')となります。
	 * @param name		名前キー
	 * @param unit		単位キー
	 * @param time		時間キー
	 * @return	交換代数基底パターン
	 * @see exalge2.ExBasePattern#ExBasePattern(String, String, String, String)
	 * 
	 * @since 1.30
	 */
	static public ExBasePattern newExBasePattern(String name, String unit, String time) {
		return new ExBasePattern(name, ExBasePattern.WILDCARD, unit, time);
	}

	/**
	 * 指定された名前キー、単位キー、時間キー、主体キーを持つ、新しい基底パターンオブジェクトを生成します。
	 * 指定されないハットキーは、ワイルドカード記号('*')となります。
	 * @param name		名前キー
	 * @param unit		単位キー
	 * @param time		時間キー
	 * @param subject	主体キー
	 * @return	交換代数基底パターン
	 * @see exalge2.ExBasePattern#ExBasePattern(String, String, String, String, String)
	 * 
	 * @since 1.30
	 */
	static public ExBasePattern newExBasePattern(String name, String unit, String time, String subject) {
		return new ExBasePattern(name, ExBasePattern.WILDCARD, unit, time, subject);
	}

	/**
	 * 指定された文字列リスト(<em>keys</em>)に格納されている文字列を基底キーとする、
	 * 新しい基底パターンオブジェクトを生成します。文字列リストと基底キーとの対応は、
	 * 文字列リストの先頭から、名前キー、単位キー、時間キー、主体キーとなります。
	 * 文字列リストの長さが 4 未満の場合、不足した基底キーはワイルドカード記号('*')となります。
	 * @param keys		名前キー、単位キー、時間キー、主体キー の順に指定された文字列リスト
	 * @return	基底パターン
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 1.30
	 */
	static public ExBasePattern newExBasePattern(List<String> keys) {
		int len = keys.size();
		String[] basekeys = new String[Math.max(2, (len+1))];
		basekeys[0] = (len > 0 ? keys.get(0) : ExBasePattern.WILDCARD);
		basekeys[1] = ExBasePattern.WILDCARD;
		for (int i = 1; i < len; i++) basekeys[i+1] = keys.get(i);
		return new ExBasePattern(basekeys);
	}

	/**
	 * 交換代数基底パターン(<em>pattern</em>)から、名前キー文字列を取得します。
	 * @param pattern	交換代数基底パターン
	 * @return 名前キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBasePattern#getNameKey()
	 * 
	 * @since 1.30
	 */
	static public String getNameKey(ExBasePattern pattern) {
		return pattern.getNameKey();
	}
	
	/**
	 * 交換代数基底パターン(<em>pattern</em>)から、単位キー文字列を取得します。
	 * @param pattern	交換代数基底パターン
	 * @return 単位キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBasePattern#getUnitKey()
	 * 
	 * @since 1.30
	 */
	static public String getUnitKey(ExBasePattern pattern) {
		return pattern.getUnitKey();
	}
	
	/**
	 * 交換代数基底パターン(<em>pattern</em>)から、時間キー文字列を取得します。
	 * @param pattern	交換代数基底パターン
	 * @return 時間キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBasePattern#getTimeKey()
	 * 
	 * @since 1.30
	 */
	static public String getTimeKey(ExBasePattern pattern) {
		return pattern.getTimeKey();
	}
	
	/**
	 * 交換代数基底パターン(<em>pattern</em>)から、主体キー文字列を取得します。
	 * @param pattern	交換代数基底パターン
	 * @return 主体キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBasePattern#getSubjectKey()
	 * 
	 * @since 1.30
	 */
	static public String getSubjectKey(ExBasePattern pattern) {
		return pattern.getSubjectKey();
	}
	
	/**
	 * 交換代数基底(<em>base</em>)の名前キー、単位キー、時間キー、主体キーを持つ、
	 * 新しい基底パターンオブジェクトを生成します。この基底パターンのハットキーは、
	 * ワイルドカード記号('*')となります。
	 * @param base	交換代数基底
	 * @return 交換代数基底パターン
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBasePattern#ExBasePattern(ExBase, boolean)
	 * 
	 * @since 1.30
	 */
	static public ExBasePattern toExBasePattern(ExBase base) {
		return new ExBasePattern(base, true);
	}

	/**
	 * 交換代数基底集合(<em>bases</em>)に含まれるすべての基底について、
	 * 基底の名前キー、単位キー、時間キー、主体キーを持つ、
	 * 新しい基底パターンオブジェクトを生成します。この基底パターンのハットキーは、
	 * ワイルドカード記号('*')となります。
	 * 生成されたすべての基底パターンは、新しい基底パターン集合に格納されます。
	 * @param bases	交換代数基底集合
	 * @return	生成された交換代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBasePatternSet#ExBasePatternSet(ExBaseSet, boolean)
	 * 
	 * @since 1.30
	 */
	static public ExBasePatternSet toExBasePattern(ExBaseSet bases) {
		return new ExBasePatternSet(bases, true);
	}

	/**
	 * 交換代数基底パターン(<em>pattern</em>)の名前キー、単位キー、時間キー、主体キーを持つ、
	 * 新しいハットなし交換代数基底オブジェクトを生成します。
	 * 生成される基底は、必ずハットなし基底となります。
	 * @param pattern	交換代数基底パターン
	 * @return	交換代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 1.30
	 */
	static public ExBase toExBase(ExBasePattern pattern) {
		return new ExBase(pattern.getNameKey(), ExBase.NO_HAT, pattern.getUnitKey(), pattern.getTimeKey(), pattern.getSubjectKey());
	}

	/**
	 * 交換代数基底パターン集合(<em>patterns</em>)に含まれるすべての基底パターンについて、
	 * 基底パターンの名前キー、単位キー、時間キー、主体キーを持つ、
	 * 新しいハットなし交換代数基底オブジェクトを生成します。
	 * 生成される基底は、必ずハットなし基底となります。
	 * 生成されたすべての基底は、新しい交換代数基底集合に格納されます。
	 * @param patterns	交換代数基底パターン集合
	 * @return	交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 1.30
	 */
	static public ExBaseSet toExBase(ExBasePatternSet patterns) {
		ExBaseSet retBases = new ExBaseSet(patterns.size());
		for (ExBasePattern pat : patterns) {
			retBases.add(new ExBase(pat.getNameKey(), ExBase.NO_HAT, pat.getUnitKey(), pat.getTimeKey(), pat.getSubjectKey()));
		}
		return retBases;
	}

	/*
	 * Exalge
	 */

	/**
	 * 交換代数元(<em>alge</em>)のすべての要素の値を逆数に変換した交換代数元を返します。
	 * 要素の値が 0 の場合、逆数は演算されず、結果の交換代数元には含まれません。
	 * @param alge 対象の交換代数元
	 * @return 逆数を要素の値として持つ交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#inverse()
	 * 
	 * @since 1.10
	 */
	static public Exalge inverse(Exalge alge) {
		return alge.inverse();
	}

	/**
	 * 交換代数元(<em>alge</em>)の逆元を求め、その結果を返します。
	 * @param alge	対象の交換代数元
	 * @return	交換代数元の逆元となる、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#invElement()
	 * 
	 * @since 1.22
	 */
	static public Exalge invElement(Exalge alge) {
		return alge.invElement();
	}

	/**
	 * 交換代数元(<em>alge</em>)から値 0 の要素を除いた、新しい交換代数元を返します。
	 * @param alge 対象の交換代数元
	 * @return 値 0 の要素を除いた、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#normalization()
	 * 
	 * @since 1.10
	 */
	static public Exalge normalize(Exalge alge) {
		return alge.normalization();
	}

	/**
	 * 交換代数元(<em>alge</em>)に含まれる全要素の値の総和を返します。
	 * @param alge 対象の交換代数元
	 * @return 交換代数元のノルム値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#norm()
	 * 
	 * @since 1.00
	 */
	static public BigDecimal norm(Exalge alge) {
		return alge.norm();
	}

	/**
	 * 交換代数集合(<em>algeset</em>)に含まれる全要素の総和を返します。
	 * @param algeset	対象の交換代数集合
	 * @return 交換代数元の総和
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#sum()
	 * @see exalge2.Exalge#sum(Collection)
	 * 
	 * @since 1.00
	 */
	static public Exalge sum(ExAlgeSet algeset) {
		return Exalge.sum(algeset);
	}

	/**
	 * <code>BigDecimal</code> オブジェクトのコレクション(<em>c</em>)に含まれる
	 * 全要素の値の総和を返します。
	 * @param c 対象のコレクション
	 * @return 全要素の値の総和
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 1.00
	 */
	static public BigDecimal sum(Collection<? extends BigDecimal> c) {
		BigDecimal ret = BigDecimal.ZERO;
		for (BigDecimal ev : c) {
			ret = ret.add(ev);
		}
		return ret;
	}

	/**
	 * 振替変換テーブル(<em>table</em>)による交換代数元(<em>alge</em>)の振替変換を行います。
	 * 振替変換は交換代数元に含まれる基底のうち、振替変換テーブルの振替元パターンに一致する
	 * 基底を振替先基底パターンに従い変換します。この変換では、振替元パターンに一致する基底を
	 * <code>&lt;e1&gt;</code>、振替先基底を <code>&lt;e2&gt;</code> とした場合、交換代数は
	 * 次のように変換されます。
	 * <blockquote>
	 * <code>10&lt;e1&gt; + 30^&lt;e3&gt; → 10&lt;e1&gt; + 30^&lt;e3&gt; + 10^&lt;e1&gt; + 10&lt;e2&gt;</code>
	 * </blockquote>
	 * これは振替変換によって、もとの基底をハット演算したものと振替先基底に置き換えられた値を、
	 * もとの交換代数元に加算していることを意味しています。振替変換ではバー演算は行われません。
	 * 複数の変換対象要素の基底が 1 つの基底に変換された場合も、変換後の要素は加算されます。
	 * また、1 つの変換対象要素の基底が複数の異なる基底パターンにマッチする場合でも、
	 * 変換対象要素の基底は最初にマッチしたパターンの定義によって一度だけ変換されます。
	 * 他のマッチするパターンの定義による変換は行われませんので、パターン作成の際はご注意下さい。
	 * @param alge 対象の交換代数元
	 * @param table 振替変換テーブル
	 * @return 振替変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#aggreTransfer(TransTable)
	 * 
	 * @since 1.00
	 */
	static public Exalge aggreTransfer(Exalge alge, TransTable table) {
		return alge.aggreTransfer(table);
	}

	/**
	 * 指定された(基底パターンと見なす)基底による振替変換を行います。
	 * この変換は、振替元基底パターン(<em>fromBase</em>)に一致する交換代数元(<em>alge</em>)に
	 * 含まれる基底を、振替先基底パターン(<em>toBase</em>)に従い変換します。
	 * 基底パターンは、ハットキー以外の基底キーに含まれているアスタリスク文字('*')を 0 文字以上の
	 * 任意の文字にマッチするワイルドカードとみなし、ハットキー以外のすべての基底キーが一致した場合を
	 * パターン一致と判定します。基底の変換は、振替先基底パターンの基底キーにワイルドカードが含まれている場合、
	 * 変換対象基底の基底キーがそのまま利用されます。ワイルドカードが含まれていない場合は、
	 * 振替先基底パターンの基底キーに置き換えられます。
	 * 変換結果には、変換対象基底をハット演算した結果と、変換後の基底によって、変換対象基底の
	 * 要素の値とともに加算されます。この振る舞いは次のようなものです。
	 * <blockquote>
	 * <code>x = a^&lt;e1&gt; + b&lt;e1&gt;</code><br>
	 * <code><i>aggreTransfer</i>(x, &lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e1&gt; + a&lt;e1&gt; + a^&lt;e3&gt;</code>
	 * </blockquote>
	 * 複数の変換対象要素の基底が一つの基底に変換された場合も、変換後の要素は加算されます。
	 * なお、振替元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromBase	振替元基底パターンとみなす交換代数基底
	 * @param toBase	振替先基底パターンとみなす交換代数基底
	 * @return	振替変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#aggreTransfer(ExBase, ExBase)
	 * 
	 * @since 1.22
	 */
	static public Exalge aggreTransfer(Exalge alge, ExBase fromBase, ExBase toBase) {
		return alge.aggreTransfer(fromBase, toBase);
	}
	
	/**
	 * 指定された基底パターンによる振替変換を行います。
	 * この変換は、振替元基底パターン(<em>fromPattern</em>)に一致する交換代数元(<em>alge</em>)に
	 * 含まれる基底を、振替先基底パターン(<em>toPattern</em>)に従い変換します。
	 * 基底の変換は、振替先基底パターンの基底キーにワイルドカードが含まれている場合、
	 * 変換対象基底の基底キーがそのまま利用されます。ワイルドカードが含まれていない場合は、
	 * 振替先基底パターンの基底キーに置き換えられます。
	 * 変換結果には、変換対象基底をハット演算した結果と、変換後の基底によって、変換対象基底の
	 * 要素の値とともに加算されます。この振る舞いは次のようなものです。
	 * <blockquote>
	 * <code>x = a^&lt;e1&gt; + b&lt;e1&gt;</code><br>
	 * <code><i>aggreTransfer</i>(x, &lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e1&gt; + a&lt;e1&gt; + a^&lt;e3&gt;</code>
	 * </blockquote>
	 * 複数の変換対象要素の基底が一つの基底に変換された場合も、変換後の要素は加算されます。
	 * なお、振替元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromPattern	振替元基底パターン
	 * @param toPattern	振替先基底パターン
	 * @return	振替変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#aggreTransfer(ExBasePattern, ExBasePattern)
	 * 
	 * @since 1.30
	 */
	static public Exalge aggreTransfer(Exalge alge, ExBasePattern fromPattern, ExBasePattern toPattern) {
		return alge.aggreTransfer(fromPattern, toPattern);
	}
	
	/**
	 * 指定された(基底パターンと見なす)基底による振替変換を行います。
	 * この変換は、振替元基底パターン集合(<em>fromBases</em>)に含まれる基底パターンのどれか 1 つに一致する交換代数元(<em>alge</em>)に
	 * 含まれる基底を、振替先基底パターン(<em>toBase</em>)に従い変換します。
	 * 基底パターンは、ハットキー以外の基底キーに含まれているアスタリスク文字('*')を 0 文字以上の
	 * 任意の文字にマッチするワイルドカードとみなし、ハットキー以外のすべての基底キーが一致した場合を
	 * パターン一致と判定します。基底の変換は、振替先基底パターンの基底キーにワイルドカードが含まれている場合、
	 * 変換対象基底の基底キーがそのまま利用されます。ワイルドカードが含まれていない場合は、
	 * 振替先基底パターンの基底キーに置き換えられます。
	 * 変換結果には、変換対象基底をハット演算した結果と、変換後の基底によって、変換対象基底の
	 * 要素の値とともに加算されます。この振る舞いは次のようなものです。
	 * <blockquote>
	 * <code>x = a^&lt;e1&gt; + b&lt;e1&gt;</code><br>
	 * <code><i>aggreTransfer</i>(x, [&lt;e1&gt;, &lt;e5&gt;], &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e1&gt; + a&lt;e1&gt; + a^&lt;e3&gt;</code>
	 * </blockquote>
	 * 複数の変換対象要素の基底が一つの基底に変換された場合も、変換後の要素は加算されます。
	 * また、1 つの変換対象要素の基底が複数の異なる基底パターンにマッチする場合でも、変換対象要素の
	 * 基底は最初にマッチしたパターンの定義のよって一度だけ変換されます。他のマッチするパターンの
	 * 定義による変換は行われませんので、パターン作成の際はご注意下さい。
	 * なお、振替元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromBases	振替元基底パターンとみなす交換代数基底の集合
	 * @param toBase	振替先基底パターンとみなす交換代数基底
	 * @return	振替変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#aggreTransfer(ExBaseSet, ExBase)
	 * 
	 * @since 1.22
	 */
	static public Exalge aggreTransfer(Exalge alge, ExBaseSet fromBases, ExBase toBase) {
		return alge.aggreTransfer(fromBases, toBase);
	}
	
	/**
	 * 指定された基底パターンによる振替変換を行います。
	 * この変換は、振替元基底パターン集合(<em>fromPatterns</em>)に含まれる基底パターンのどれか 1 つに一致する交換代数元(<em>alge</em>)に
	 * 含まれる基底を、振替先基底パターン(<em>toPattern</em>)に従い変換します。
	 * 基底の変換は、振替先基底パターンの基底キーにワイルドカードが含まれている場合、
	 * 変換対象基底の基底キーがそのまま利用されます。ワイルドカードが含まれていない場合は、
	 * 振替先基底パターンの基底キーに置き換えられます。
	 * 変換結果には、変換対象基底をハット演算した結果と、変換後の基底によって、変換対象基底の
	 * 要素の値とともに加算されます。この振る舞いは次のようなものです。
	 * <blockquote>
	 * <code>x = a^&lt;e1&gt; + b&lt;e1&gt;</code><br>
	 * <code><i>aggreTransfer</i>(x, [&lt;e1&gt;, &lt;e5&gt;], &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e1&gt; + a&lt;e1&gt; + a^&lt;e3&gt;</code>
	 * </blockquote>
	 * 複数の変換対象要素の基底が一つの基底に変換された場合も、変換後の要素は加算されます。
	 * また、1 つの変換対象要素の基底が複数の異なる基底パターンにマッチする場合でも、変換対象要素の
	 * 基底は最初にマッチしたパターンの定義のよって一度だけ変換されます。他のマッチするパターンの
	 * 定義による変換は行われませんので、パターン作成の際はご注意下さい。
	 * なお、振替元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromPatterns	振替元基底パターンの集合
	 * @param toPattern	振替先基底パターン
	 * @return	振替変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#aggreTransfer(ExBasePatternSet, ExBasePattern)
	 * 
	 * @since 1.30
	 */
	static public Exalge aggreTransfer(Exalge alge, ExBasePatternSet fromPatterns, ExBasePattern toPattern) {
		return alge.aggreTransfer(fromPatterns, toPattern);
	}

	/**
	 * 指定された(基底パターンとみなす)基底(<em>fromBase</em>)に一致する交換代数元(<em>alge</em>)に含まれる基底を、
	 * (基底パターンとみなす)変換先基底(<em>toBase</em>)で示される基底に置換します。
	 * 基底パターンは、ハットキー以外の基底キーに含まれているアスタリスク文字('*')を 0 文字以上の任意の文字に
	 * マッチするワイルドカードとみなし、ハットキー以外のすべての基底キーが一致した場合をパターン一致と判定します。
	 * 基底の変換は、変換先基底パターンの基底キーにワイルドカードが含まれている場合、変換対象基底の基底キーが
	 * そのまま利用されます。ワイルドカードが含まれていない場合は、変換先基底パターンの基底キーに置き換えられます。
	 * この変換操作は基底の置換となり、変換対象となったもとの基底は結果に含まれません。
	 * なお、変換元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromBase	変換元基底パターンとみなす交換代数基底
	 * @param toBase	変換先基底パターンとみなす交換代数基底
	 * @return	変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#transform(ExBase, ExBase)
	 * 
	 * @since 1.22
	 */
	static public Exalge transform(Exalge alge, ExBase fromBase, ExBase toBase) {
		return alge.transform(fromBase, toBase);
	}
	
	/**
	 * 指定された基底パターン(<em>fromPattern</em>)に一致する交換代数元(<em>alge</em>)に含まれる基底を、
	 * 変換先基底パターン(<em>toPattern</em>)で示される基底に置換します。
	 * 基底の変換は、変換先基底パターンの基底キーにワイルドカードが含まれている場合、変換対象基底の基底キーが
	 * そのまま利用されます。ワイルドカードが含まれていない場合は、変換先基底パターンの基底キーに置き換えられます。
	 * この変換操作は基底の置換となり、変換対象となったもとの基底は結果に含まれません。
	 * なお、変換元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromPattern	変換元基底パターン
	 * @param toPattern	変換先基底パターン
	 * @return	変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#transform(ExBasePattern, ExBasePattern)
	 * 
	 * @since 1.30
	 */
	static public Exalge transform(Exalge alge, ExBasePattern fromPattern, ExBasePattern toPattern) {
		return alge.transform(fromPattern, toPattern);
	}
	
	/**
	 * 指定された変換元基底パターン集合(<em>fromBases</em>)に含まれる基底パターンのどれか 1 つに一致する交換代数元(<em>alge</em>)に含まれる基底を、
	 * (基底パターンとみなす)変換先基底(<em>toBase</em>)で示される基底に置換します。
	 * 基底パターンは、ハットキー以外の基底キーに含まれているアスタリスク文字('*')を 0 文字以上の任意の文字に
	 * マッチするワイルドカードとみなし、ハットキー以外のすべての基底キーが一致した場合をパターン一致と判定します。
	 * 基底の変換は、変換先基底パターンの基底キーにワイルドカードが含まれている場合、変換対象基底の基底キーが
	 * そのまま利用されます。ワイルドカードが含まれていない場合は、変換先基底パターンの基底キーに置き換えられます。
	 * この変換操作は基底の置換となり、変換対象となったもとの基底は結果に含まれません。
	 * なお、変換元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromBases	変換元基底パターンとみなす交換代数基底の集合
	 * @param toBase	変換先基底パターンとみなす交換代数基底
	 * @return	変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#transform(ExBaseSet, ExBase)
	 * 
	 * @since 1.22
	 */
	static public Exalge transform(Exalge alge, ExBaseSet fromBases, ExBase toBase) {
		return alge.transform(fromBases, toBase);
	}
	
	/**
	 * 指定された変換元基底パターン集合(<em>fromPatterns</em>)に含まれる基底パターンのどれか 1 つに一致する交換代数元(<em>alge</em>)に含まれる基底を、
	 * 変換先基底パターン(<em>toPattern</em>)で示される基底に置換します。
	 * 基底の変換は、変換先基底パターンの基底キーにワイルドカードが含まれている場合、変換対象基底の基底キーが
	 * そのまま利用されます。ワイルドカードが含まれていない場合は、変換先基底パターンの基底キーに置き換えられます。
	 * この変換操作は基底の置換となり、変換対象となったもとの基底は結果に含まれません。
	 * なお、変換元基底パターンに一致する基底が存在しない場合は、指定された交換代数元を返します。
	 * @param alge		対象の交換代数元
	 * @param fromPatterns	変換元基底パターンの集合
	 * @param toPattern	変換先基底パターン
	 * @return	変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#transform(ExBasePatternSet, ExBasePattern)
	 * 
	 * @since 1.30
	 */
	static public Exalge transform(Exalge alge, ExBasePatternSet fromPatterns, ExBasePattern toPattern) {
		return alge.transform(fromPatterns, toPattern);
	}

	/**
	 * 按分変換テーブル(<em>matrix</em>)による交換代数元(<em>alge</em>)の按分変換を行います。
	 * 按分変換は、交換代数元に含まれる基底のうち、按分変換テーブルの按分元パターンに一致する基底を、
	 * 按分比率と按分先基底パターンに従い変換します。
	 * この変換では、按分元パターンに一致する基底を <code>&lt;e1&gt;</code>、按分先基底を <code>&lt;e01&gt;</code>、
	 * <code>&lt;e02&gt;</code> とし、その比率をそれぞれ 0.5、0.5 とした場合、交換代数元は次のように変換されます。
	 * <blockquote>
	 * <code>10&lt;e1&gt; + 30^&lt;e3&gt; → 10&lt;e1&gt; + 30^&lt;e3&gt; + 10^&lt;e1&gt; + 5&lt;e01&gt; + 5&lt;e02&gt;</code>
	 * </blockquote>
	 * また、按分比率をそれぞれ 0.7、0.3 とした場合、交換代数元は次のように変換されます。
	 * <blockquote>
	 * <code>10&lt;e1&gt; + 30^&lt;e3&gt; → 10&lt;e1&gt; + 30^&lt;e3&gt; + 10^&lt;e1&gt; + 7&lt;e01&gt; + 3&lt;e02&gt;</code>
	 * </blockquote>
	 * これは按分変換によって、もとの基底をハット演算したものと按分比率によって按分先基底に置き換えられた値を、
	 * もとの交換代数元に加算していることを意味しています。按分変換ではバー演算は行われません。
	 * 複数の変換対象要素の基底が 1 つの基底に変換された場合も、変換後の要素は加算されます。
	 * また、1 つの変換対象要素の基底が複数の異なる基底パターンにマッチする場合でも、変換対象要素の基底は
	 * 最初にマッチしたパターンの定義によって一度だけ変換されます。他のマッチするパターンの定義による変換は
	 * 行われませんので、パターン作成の際はご注意下さい。
	 * @param alge 対象の交換代数元
	 * @param matrix 按分変換テーブル
	 * @return	変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#divideTransfer(TransMatrix)
	 * 
	 * @since 1.00
	 */
	static public Exalge divideTransfer(Exalge alge, TransMatrix matrix) {
		return alge.divideTransfer(matrix);
	}

	/**
	 * 変換テーブル(<em>transfer</em>)の変換定義による交換代数元(<em>alge</em>)の変換を行います。
	 * 変換テーブルの定義については、{@link exalge2.ExTransfer} を参照して下さい。
	 * 変換結果には、変換対象基底をハット演算した結果と、変換後の基底によって、
	 * 変換対象基底の要素の値とともに加算されます。この変換ではバー演算は行われません。
	 * 複数の変換対象要素の基底が 1 つの基底に変換された場合も、変換後の要素は加算されます。
	 * 変換テーブルは、1 つの変換定義が 1 つの変換元基底パターンを持ちます。変換定義の属性名によって
	 * 変換方法が異なりますが、<code>ratio</code> や <code>multiply</code> など、複数の変換定義において
	 * 同一の変換元基底パターンを持つものも定義できます。このとき、変換対象の要素の基底にマッチした
	 * 変換元基底パターンと同一の変換元基底パターンを持つすべての変換定義により、変換対象の要素が変換されます。
	 * ただし、1 つの変換対象要素の基底が複数の異なる基底パターンにマッチする場合は、変換対象要素の基底は
	 * 最初にマッチしたパターンを変換元基底パターンとするすべての変換定義によって一度だけ変換されます。
	 * 他のマッチするパターンの定義による変換は行われませんので、パターン作成の際はご注意下さい。
	 * @param alge	対象の交換代数元
	 * @param transfer	変換テーブル
	 * @return	変換後の、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#transfer(ExTransfer)
	 * 
	 * @since 1.23
	 */
	static public Exalge transfer(Exalge alge, ExTransfer transfer) {
		return alge.transfer(transfer);
	}
	
	/*
	 * TransTable
	 */

	/**
	 * 振替変換テーブル(<em>table</em>)の定義に基づき、交換代数基底(<em>base</em>)を変換した結果を返します。
	 * 指定された基底が、振替変換テーブルの振替元基底パターンのどれにも一致しない場合、
	 * 指定された基底をそのまま返します。
	 * 指定された基底に一致する、異なる振替元基底パターンが複数存在する場合、最初に一致した振替元基底パターンに
	 * 関連付けられている振替先基底パターンによってのみ変換されます。
	 * @param table	振替変換テーブル
	 * @param base	対象の交換代数基底
	 * @return	変換後の交換代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransTable#transform(ExBase)
	 * 
	 * @since 1.22
	 */
	static public ExBase transform(TransTable table, ExBase base) {
		return table.transform(base);
	}
	
	/**
	 * 振替変換テーブル(<em>table</em>)の定義に基づき、交換代数基底集合(<em>bases</em>)に含まれる
	 * すべての基底を変換した結果を返します。
	 * 指定された基底集合に含まれる基底のうち振替変換テーブルの振替元基底パターンのどれにも一致しない基底は、
	 * 変換されずに結果の基底集合に格納されます。
	 * 指定された基底に一致する、異なる振替元基底パターンが複数存在する場合、最初に一致した振替元基底パターンに
	 * 関連付けられている振替先基底パターンによってのみ変換されます。
	 * @param table	振替変換テーブル
	 * @param bases	対象の交換代数基底の集合
	 * @return	変換後の交換代数基底を格納する、新しい交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransTable#transform(ExBaseSet)
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet transform(TransTable table, ExBaseSet bases) {
		return table.transform(bases);
	}
	
	/**
	 * 振替変換テーブル(<em>transfer</em>)に格納されているすべての変換元基底パターンを格納する、
	 * 新しい基底パターン集合を返します。変換定義が存在しない場合は、空の基底パターン集合を返します。
	 * @param transfer	振替変換テーブル
	 * @return	すべての振替元基底パターンを格納する、新しい交換代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransTable#transFromPatterns()
	 * @since 1.30
	 */
	static public ExBasePatternSet getTransferFromPatterns(TransTable transfer) {
		return new ExBasePatternSet(transfer.transFromPatterns());
	}

	/**
	 * 振替変換テーブル(<em>transfer</em>)に格納されているすべての変換先基底パターンを格納する、
	 * 新しい基底パターン集合を返します。変換定義が存在しない場合は、空の基底パターン集合を返します。
	 * @param transfer	振替変換テーブル
	 * @return	すべての振替先基底パターンを格納する、新しい交換代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransTable#transToPatterns()
	 * @since 1.30
	 */
	static public ExBasePatternSet getTransferToPatterns(TransTable transfer) {
		return new ExBasePatternSet(transfer.transToPatterns());
	}
	
	/*
	 * TransMatrix
	 */
	
	/**
	 * 按分変換テーブル(<em>matrix</em>)の定義に基づき、交換代数基底(<em>base</em>)を変換した結果を格納する、
	 * 新しい基底集合を返します。
	 * 指定された基底が、按分変換テーブルの按分元基底パターンのどれにも一致しない基底は、
	 * 変換されずに結果の基底集合に格納されます。
	 * 指定された基底に一致する、異なる按分元基底パターンが複数存在する場合、最初に一致した按分元基底パターンに
	 * 関連付けられている按分先基底パターンによってのみ変換されます。
	 * @param matrix	按分変換テーブル
	 * @param base		対象の交換代数基底
	 * @return	変換後の交換代数基底を格納する、新しい交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransMatrix#transform(ExBase)
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet transform(TransMatrix matrix, ExBase base) {
		return matrix.transform(base);
	}
	
	/**
	 * 按分変換テーブル(<em>matrix</em>)の定義に基づき、交換代数基底集合(<em>bases</em>)に含まれる
	 * すべての基底を変換した結果を格納する、新しい基底集合を返します。
	 * 指定された基底集合に含まれる基底のうち按分変換テーブルの按分元基底パターンのどれにも一致しない基底は、
	 * 変換されずに結果の基底集合に格納されます。
	 * 指定された基底に一致する、異なる按分元基底パターンが複数存在する場合、最初に一致した按分元基底パターンに
	 * 関連付けられている按分先基底パターンによってのみ変換されます。
	 * @param matrix	按分変換テーブル
	 * @param bases		対象の交換代数基底の集合
	 * @return	変換後の交換代数基底を格納する、新しい交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransMatrix#transform(ExBaseSet)
	 * 
	 * @since 1.22
	 */
	static public ExBaseSet transform(TransMatrix matrix, ExBaseSet bases) {
		return matrix.transform(bases);
	}
	
	/**
	 * 按分変換テーブル(<em>transfer</em>)に格納されているすべての変換元基底パターンを格納する、
	 * 新しい基底パターン集合を返します。変換定義が存在しない場合は、空の基底パターン集合を返します。
	 * @param transfer	按分変換テーブル
	 * @return	すべての按分元基底パターンを格納する、新しい交換代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransMatrix#transFromPatterns()
	 * @since 1.30
	 */
	static public ExBasePatternSet getTransferFromPatterns(TransMatrix transfer) {
		return new ExBasePatternSet(transfer.transFromPatterns());
	}

	/**
	 * 按分変換テーブル(<em>transfer</em>)に格納されているすべての変換先基底パターンを格納する、
	 * 新しい基底パターン集合を返します。変換定義が存在しない場合は、空の基底パターン集合を返します。
	 * @param transfer	按分変換テーブル
	 * @return	すべての按分先基底パターンを格納する、新しい交換代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.30
	 */
	static public ExBasePatternSet getTransferToPatterns(TransMatrix transfer) {
		ExBasePatternSet retset = new ExBasePatternSet();
		Collection<TransDivideRatios> ratios = transfer.transToRatios();
		for (TransDivideRatios toRatio : ratios) {
			if (toRatio != null && !toRatio.isEmpty())
				retset.addAll(toRatio.patterns());
		}
		return retset;
	}
	
	/*
	 * ExTransfer
	 */

	/**
	 * 変換テーブル(<em>transfer</em>)の定義に基づき、交換代数基底(<em>base</em>)を変換した結果を格納する、
	 * 新しい基底集合を返します。
	 * 指定された基底が、変換テーブルの変換元基底パターンのどれにも一致しない基底は、
	 * 変換されずに結果の基底集合に格納されます。
	 * 指定された基底に一致する、異なる変換元基底パターンが複数存在する場合、最初に一致した基底パターンを
	 * 変換元基底パターンとするすべての変換定義によってのみ変換されます。
	 * @param transfer	変換テーブル
	 * @param base		対象の交換代数基底
	 * @return	変換後の交換代数基底を格納する、新しい交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#transform(ExBase)
	 * 
	 * @since 1.23
	 */
	static public ExBaseSet transform(ExTransfer transfer, ExBase base) {
		return transfer.transform(base);
	}

	/**
	 * 変換テーブル(<em>matrix</em>)の定義に基づき、交換代数基底集合(<em>bases</em>)に含まれる
	 * すべての基底を変換した結果を格納する、新しい基底集合を返します。
	 * 指定された基底集合に含まれる基底のうち変換テーブルの按分元基底パターンのどれにも一致しない基底は、
	 * 変換されずに結果の基底集合に格納されます。
	 * 指定された基底に一致する、異なる按分元基底パターンが複数存在する場合、最初に一致した按分元基底パターンに
	 * 関連付けられている按分先基底パターンによってのみ変換されます。
	 * 指定された基底に一致する、異なる変換元基底パターンが複数存在する場合、最初に一致した基底パターンを
	 * 変換元基底パターンとするすべての変換定義によってのみ変換されます。
	 * @param transfer	変換テーブル
	 * @param bases		対象の交換代数基底の集合
	 * @return	変換後の交換代数基底を格納する、新しい交換代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#transform(ExBaseSet)
	 * 
	 * @since 1.23
	 */
	static public ExBaseSet transform(ExTransfer transfer, ExBaseSet bases) {
		return transfer.transform(bases);
	}

	/**
	 * 変換テーブル(<em>transfer</em>)に格納されているすべての変換元基底パターンを格納する、
	 * 新しい基底パターン集合を返します。変換定義が存在しない場合は、空の基底パターン集合を返します。
	 * @param transfer	変換テーブル
	 * @return	すべての変換元基底パターンを格納する、新しい交換代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#fromPatterns()
	 * @since 1.30
	 */
	static public ExBasePatternSet getTransferFromPatterns(ExTransfer transfer) {
		return transfer.fromPatterns();
	}

	/**
	 * 変換テーブル(<em>transfer</em>)に格納されているすべての変換先基底パターンを格納する、
	 * 新しい基底パターン集合を返します。変換定義が存在しない場合は、空の基底パターン集合を返します。
	 * @param transfer	変換テーブル
	 * @return	すべての変換先基底パターンを格納する、新しい交換代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#toPatterns()
	 * @since 1.30
	 */
	static public ExBasePatternSet getTransferToPatterns(ExTransfer transfer) {
		return transfer.toPatterns();
	}

	/**
	 * 変換テーブル(<em>transfer</em>)から、変換元基底パターン(<em>from</em>)に関連付けられている
	 * すべての変換定義の属性値の合計を取得します。
	 * @param transfer	変換テーブル
	 * @param from		変換元基底パターン
	 * @return	指定された変換元基底パターンが定義されている場合、そのパターンに関連付けられている属性値の合計値を返す。
	 * 			指定された変換元基底パターンが存在しない場合は 0 を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#getTotalValue(ExBasePattern)
	 * 
	 * @since 1.30
	 */
	static public BigDecimal getTransferTotalValue(ExTransfer transfer, ExBasePattern from) {
		BigDecimal totalValue = transfer.getTotalValue(from);
		return (totalValue==null ? BigDecimal.ZERO : totalValue);
	}

	/**
	 * 変換テーブル(<em>transfer</em>)から、変換元基底パターン(<em>from</em>)と変換先基底パターン(<em>to</em>)との
	 * 組み合わせに関連付けられている属性の属性値を取得します。
	 * @param transfer	変換テーブル
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @return	指定された変換定義が存在する場合は、その定義に設定されている属性値を返す。
	 * 			指定された変換定義が存在しない場合は 0 を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#getValue(ExBasePattern, ExBasePattern)
	 * 
	 * @since 1.30
	 */
	static public BigDecimal getTransferValue(ExTransfer transfer, ExBasePattern from, ExBasePattern to) {
		BigDecimal attrValue = transfer.getValue(from, to);
		return (attrValue==null ? BigDecimal.ZERO : attrValue);
	}

	/**
	 * 変換テーブル(<em>transfer</em>)から、変換元基底パターン(<em>from</em>)と変換先基底パターン(<em>to</em>)との
	 * 組み合わせに関連付けられている属性の属性名を取得します。
	 * @param transfer	変換テーブル
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された変換定義が存在する場合は、その定義に設定されている属性名を返す。
	 * 			指定された変換定義が存在しない場合は、空文字列(長さ 0 の文字列)を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#getAttribute(ExBasePattern, ExBasePattern)
	 * 
	 * @since 1.30
	 */
	static public String getTransferAttribute(ExTransfer transfer, ExBasePattern from, ExBasePattern to) {
		String attrName = transfer.getAttribute(from, to);
		return (attrName==null ? "" : attrName);
	}
	
	/*
	 * Math
	 */

	/**
	 * 指定された数値(<em>value</em>)にスケール(<em>scale</em>)を適用した数値を返します。
	 * スケールは小数点以下の桁数であり、整数値で設定します。指定されたスケール値により
	 * 元の数値のスケールが減らされる場合、スケール設定のための除算により、値が変わる可能性があります。
	 * このとき、四捨五入によって値が丸められます。
	 * この関数の詳細は動作は、{@link java.math.BigDecimal#setScale(int, RoundingMode)} の動作に依存します。
	 * 実際には、次のコードを実行します。
	 * <blockquote>
	 * <code>value.setScale(scale.intValue(), RoundingMode.HALF_UP)</code>
	 * </blockquote>
	 * @param value	スケールを設定する元の値
	 * @param scale	スケール値
	 * @return スケール適用後の数値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see java.math.BigDecimal#setScale(int, RoundingMode)
	 * 
	 * @since 1.10
	 */
	static public BigDecimal setDecimalScale(BigDecimal value, BigDecimal scale) {
		return value.setScale(scale.intValue(), RoundingMode.HALF_UP);
	}

	/**
	 * 指定された数値(<em>value</em>)の現在のスケール値を返します。
	 * @param value	スケールを取得する元の値
	 * @return 現在のスケール値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see java.math.BigDecimal#scale()
	 * 
	 * @since 1.10
	 */
	static public BigDecimal getDecimalScale(BigDecimal value) {
		return new BigDecimal(value.scale());
	}

	/**
	 * 指定された値(<em>value</em>)を、JAVA の 64-bit 浮動小数点(double)型に変換可能か検証します。
	 * @param value	検証する値
	 * @return	変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see java.math.BigDecimal#doubleValue()
	 * @see java.math.BigDecimal#valueOf(double)
	 * 
	 * @since 1.30
	 */
	static public boolean canConvertToDoubleValue(BigDecimal value) {
		try {
			BigDecimal.valueOf(value.doubleValue());
			return true;
		} catch (Throwable ignoreEx) {
			return false;
		}
	}

	/**
	 * 指定された値(<em>value</em>)を、JAVA の 64-bit 浮動小数点(double)型で表現される値に変換します。
	 * 表現可能な値に変換できない場合、もしくは、変換結果が <code>BigDecimal</code> の数値として
	 * 適切ではない場合、このメソッドは例外をスローします。
	 * この関数が返す値は、JAVA の 64-bit 浮動小数点(double)型と同等の精度となります。
	 * @param value	変換する値
	 * @return java 倍精度浮動小数点数(double)に変換した値を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws NumberFormatException	<code>BigDecimal</code> の数値として適切ではない場合
	 * @see java.math.BigDecimal#doubleValue()
	 * @see java.math.BigDecimal#valueOf(double)
	 * 
	 * @since 1.30
	 */
	static public BigDecimal doubleValue(BigDecimal value) {
		return BigDecimal.valueOf(value.doubleValue());
	}

	/**
	 * <em>v</em> の <em>s</em> 乗を計算した結果を返します。
	 * パラメータ <em>s</em> は、0 から 999999999 の範囲に収まっている必要があります。
	 * パラメータ <em>s</em> が小数の場合、小数部は切り捨てられます。
	 * パラメータ <em>s</em> が範囲外の場合は、例外がスローされます。
	 * <em>v</em> と <em>s</em> がどちらも 0 の場合、この関数は 1 を返します。
	 * @param v 元の値
	 * @param s 累乗(0 から 999999999 の範囲内の整数)
	 * @return <em>v</em> の <em>s</em> 乗の計算結果
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ArithmeticException		<em>s</em> の値が適切ではない場合
	 * @see BigDecimal#pow(int)
	 * 
	 * @since 1.30
	 */
	static public BigDecimal bigpow(BigDecimal v, BigDecimal s) {
		return v.pow(s.intValue());
	}

	/**
	 * <em>v</em> を <em>s</em> で累乗した結果を返します。
	 * <em>s</em> が 0 の場合、この関数は 1 を返します。
	 * 演算結果が <code>BigDecimal</code> の数値として適切ではない場合、このメソッドは例外をスローします。
	 * この演算結果は、JAVA の 64-bit 浮動小数点(double)型と同等の精度となります。
	 * この関数では、内部で JAVA の 64-bit 浮動小数点数(double)型に変換してから演算が行われます。
	 * @param v 基数
	 * @param s 指数
	 * @return <em>v</em> の <em>s</em> 乗の計算結果
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ArithmeticException		演算結果が <code>BigDecimal</code> の値として適切ではない場合
	 * @see java.lang.Math#pow(double, double)
	 * @see java.math.BigDecimal#doubleValue()
	 * @see java.math.BigDecimal#valueOf(double)
	 * 
	 * @since 1.00
	 */
	static public BigDecimal pow(BigDecimal v, BigDecimal s) {
		double r = Math.pow(v.doubleValue(), s.doubleValue());
		return BigDecimal.valueOf(r);
	}

	/**
	 * 指定された数値(<em>v</em>) の正の平方根を返します。
	 * 演算結果が <code>BigDecimal</code> の数値として適切ではない場合、このメソッドは例外をスローします。
	 * この演算結果は、JAVA の 64-bit 浮動小数点(double)型と同等の精度となります。
	 * この関数では、内部で JAVA の 64-bit 浮動小数点数(double)型に変換してから演算が行われます。
	 * @param v 元の値
	 * @return 正の平方根
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ArithmeticException		演算結果が <code>BigDecimal</code> の値として適切ではない場合
	 * @see java.lang.Math#sqrt(double)
	 * @see java.math.BigDecimal#doubleValue()
	 * @see java.math.BigDecimal#valueOf(double)
	 * 
	 * @since 1.00
	 */
	static public BigDecimal sqrt(BigDecimal v) {
		double r = Math.sqrt(v.doubleValue());
		return BigDecimal.valueOf(r);
	}

	/**
	 * 指定された数値の絶対値を返します。
	 * @param v 元の値
	 * @return 絶対値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see java.math.BigDecimal#abs()
	 * 
	 * @since 1.00
	 */
	static public BigDecimal abs(BigDecimal v) {
		return v.abs();
	}

	/**
	 * 指定された数値(<em>a</em>)と数値(<em>b</em>)の最小値を返します。
	 * @param a 最小値の計算に使用する数値
	 * @param b 最小値の計算に使用する数値のもう一方
	 * @return 最小値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see java.math.BigDecimal#min(BigDecimal)
	 * 
	 * @since 1.00
	 */
	static public BigDecimal min(BigDecimal a, BigDecimal b) {
		return a.min(b);
	}

	/**
	 * 指定された数値(<em>a</em>)と数値(<em>b</em>)の最大値を返します。
	 * @param a 最大値の計算に使用する数値
	 * @param b 最大値の計算に使用する数値のもう一方
	 * @return 最大値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see java.math.BigDecimal#max(BigDecimal)
	 * 
	 * @since 1.00
	 */
	static public BigDecimal max(BigDecimal a, BigDecimal b) {
		return a.max(b);
	}

	/**
	 * 正の無限大に近い整数値を返します。この関数が返す値のスケール値は 0 となります。
	 * @param v 元の値
	 * @return 正の無限大に近い整数値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 1.00
	 */
	static public BigDecimal ceil(BigDecimal v) {
		return v.setScale(0, RoundingMode.CEILING);
	}

	/**
	 * 負の無限大に近い整数値を返します。この関数が返す値のスケール値は 0 となります。
	 * @param v 元の値
	 * @return 負の無限大に近い整数値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 1.00
	 */
	static public BigDecimal floor(BigDecimal v) {
		return v.setScale(0, RoundingMode.FLOOR);
	}

	/*
	 * Utilities
	 */

	/**
	 * 指定された文字列が数値(<code>BigDecimal</code> オブジェクト)に変換可能な場合に <tt>true</tt> を返します。
	 * 変換可能な文字列表現は <code>BigDecimal</code> の文字列表現となります。
	 * @param str 検証する文字列
	 * @return 数値に変換可能な文字列であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @see java.math.BigDecimal#BigDecimal(String)
	 * @since 1.10
	 */
	static public boolean isDecimal(String str) {
		boolean ret;
		try {
			new BigDecimal(str);
			ret = true;
		} catch (Throwable ex) {
			ret = false;
		}
		return ret;
	}
	
	/**
	 * 指定された文字列を数値(<code>BigDecimal</code> オブジェクト)に変換します。
	 * 変換可能な文字列表現は <code>BigDecimal</code> の文字列表現となります。
	 * 文字列表現が数値として有効ではない場合、この関数は例外をスローします。
	 * @param str 変換する文字列
	 * @return 変換後の数値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws NumberFormatException	変換できない場合
	 * @see java.math.BigDecimal#BigDecimal(String)
	 * 
	 * @since 1.10
	 */
	static public BigDecimal toDecimal(String str) {
		return new BigDecimal(str);
	}

	/**
	 * 指定された値を <code>BigDecimal</code> オブジェクトに変換します。
	 * @param value		<code>byte</code> 型の値
	 * @return	<code>BigDecimal</code> オブジェクト
	 * @see java.math.BigDecimal#BigDecimal(int)
	 * @since 1.30
	 */
	static public BigDecimal toDecimal(byte value) {
		return new BigDecimal((int)value);
	}
	
	/**
	 * 指定された値を <code>BigDecimal</code> オブジェクトに変換します。
	 * @param value		<code>short</code> 型の値
	 * @return	<code>BigDecimal</code> オブジェクト
	 * @see java.math.BigDecimal#BigDecimal(int)
	 * @since 1.30
	 */
	static public BigDecimal toDecimal(short value) {
		return new BigDecimal((int)value);
	}
	
	/**
	 * 指定された値を <code>BigDecimal</code> オブジェクトに変換します。
	 * @param value		<code>int</code> 型の値
	 * @return	<code>BigDecimal</code> オブジェクト
	 * @see java.math.BigDecimal#BigDecimal(int)
	 * @since 1.30
	 */
	static public BigDecimal toDecimal(int value) {
		return new BigDecimal(value);
	}
	
	/**
	 * 指定された値を <code>BigDecimal</code> オブジェクトに変換します。
	 * @param value		<code>long</code> 型の値
	 * @return	<code>BigDecimal</code> オブジェクト
	 * @see java.math.BigDecimal#valueOf(long)
	 * @since 1.30
	 */
	static public BigDecimal toDecimal(long value) {
		return BigDecimal.valueOf(value);
	}
	
	/**
	 * 指定された値を <code>BigDecimal</code> オブジェクトに変換します。
	 * @param value		<code>float</code> 型の値
	 * @return	<code>BigDecimal</code> オブジェクト
	 * @throws NumberFormatException	変換不可能な値の場合
	 * @see java.math.BigDecimal#valueOf(double)
	 * @since 1.30
	 */
	static public BigDecimal toDecimal(float value) {
		return BigDecimal.valueOf((double)value);
	}
	
	/**
	 * 指定された値を <code>BigDecimal</code> オブジェクトに変換します。
	 * @param value		<code>double</code> 型の値
	 * @return	<code>BigDecimal</code> オブジェクト
	 * @throws NumberFormatException	変換不可能な値の場合
	 * @see java.math.BigDecimal#valueOf(double)
	 * @since 1.30
	 */
	static public BigDecimal toDecimal(double value) {
		return BigDecimal.valueOf(value);
	}

	/**
	 * 指定されたオブジェクトの値を文字列に変換します。
	 * この関数は、基本的に {@link String#valueOf(Object)} メソッドにより文字列に
	 * 変換します。ただし、指定のオブジェクトが <code>BigDecimal</code> オブジェクトの
	 * 場合は、{@link java.math.BigDecimal#stripTrailingZeros()} 適用後に、
	 * {@link java.math.BigDecimal#toPlainString()} によって文字列に変換します。
	 * @param obj 対象のオブジェクト
	 * @return 文字列
	 * @see String#valueOf(Object)
	 * @see java.math.BigDecimal#stripTrailingZeros()
	 * @see java.math.BigDecimal#toPlainString()
	 * 
	 * @since 1.00
	 */
	static public String toString(Object obj) {
		if (obj instanceof BigDecimal) {
			return ((BigDecimal)obj).stripTrailingZeros().toPlainString();
		} else {
			return String.valueOf(obj);
		}
	}

	/**
	 * 指定されたオブジェクトの値を、文字列として標準出力へ出力します。
	 * 指定されたオブジェクトは {@link #toString(Object)} 関数によって文字列に変換されます。
	 * @param obj 対象のオブジェクト
	 * @return 出力に成功した場合は <tt>true</tt>
	 * @see #toString(Object)
	 * @see java.io.PrintStream#print(String)
	 * 
	 * @since 1.00
	 */
	static public boolean print(Object obj) {
	    try {
	        System.out.print(toString(obj));
	        System.out.flush();
	        return true;
	    } catch (Exception ex) {
	        return false;
	    }
	}

	/**
	 * 指定されたオブジェクトの値を、文字列として標準出力へ出力し、最後に改行を出力します。
	 * 指定されたオブジェクトは {@link #toString(Object)} 関数によって文字列に変換されます。
	 * @param obj 対象のオブジェクト
	 * @return 出力に成功した場合は <tt>true</tt>
	 * @see #toString(Object)
	 * @see java.io.PrintStream#println(String)
	 * 
	 * @since 1.10
	 */
	static public boolean println(Object obj) {
		try {
			System.out.println(toString(obj));
			System.out.flush();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 改行のみを標準出力へ出力します。
	 * @return 出力に成功した場合は <tt>true</tt>
	 * @see java.io.PrintStream#println()
	 * 
	 * @since 1.30
	 */
	static public boolean println() {
		try {
			System.out.println();
			System.out.flush();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 指定されたオブジェクトの値を、文字列として標準エラー出力へ出力します。
	 * 指定されたオブジェクトは {@link #toString(Object)} 関数によって文字列に変換されます。
	 * @param obj 対象のオブジェクト
	 * @return 出力に成功した場合は <tt>true</tt>
	 * @see #toString(Object)
	 * @see java.io.PrintStream#print(String)
	 * 
	 * @since 1.30
	 */
	static public boolean errorprint(Object obj) {
	    try {
	        System.err.print(toString(obj));
	        System.err.flush();
	        return true;
	    } catch (Exception ex) {
	        return false;
	    }
	}

	/**
	 * 指定されたオブジェクトの値を、文字列として標準エラー出力へ出力し、最後に改行を出力します。
	 * 指定されたオブジェクトは {@link #toString(Object)} 関数によって文字列に変換されます。
	 * @param obj 対象のオブジェクト
	 * @return 出力に成功した場合は <tt>true</tt>
	 * @see #toString(Object)
	 * @see java.io.PrintStream#println(String)
	 * 
	 * @since 1.30
	 */
	static public boolean errorprintln(Object obj) {
		try {
			System.err.println(toString(obj));
			System.err.flush();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 改行のみを標準エラー出力へ出力します。
	 * @return 出力に成功した場合は <tt>true</tt>
	 * @see java.io.PrintStream#println()
	 * 
	 * @since 1.30
	 */
	static public boolean errorprintln() {
		try {
			System.err.println();
			System.err.flush();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 指定された XML ファイル(<em>xmlFilename</em>)を、XSLT ファイル(<em>xsltFilename</em>)の定義に従い変換します。
	 * 変換後のドキュメントは指定されたファイル(<em>destFilename</em>)に出力されます。
	 * 指定されたファイルが見つからない場合や、変換に失敗した場合は、例外がスローされます。
	 * <em>verbose</em> に <tt>true</tt> を指定した場合、変換中に発生した警告や詳細なエラー情報などが標準出力／標準エラー出力に出力されます。
	 * @param xmlFilename	変換元のXMLファイル名
	 * @param xsltFilename	変換に使用するスタイルシートのファイル名
	 * @param destFilename		変換先のXMLファイル名
	 * @param verbose			詳細な情報を標準出力に表示する場合は <tt>true</tt> を指定する。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	変換に失敗した場合
	 * 
	 * @since 1.20
	 */
	static public void xmlTransform(String xmlFilename, String xsltFilename, String destFilename, Boolean verbose) {
		XsltTransformer transformer = new XsltTransformer(xmlFilename, xsltFilename, destFilename, verbose);
		transformer.transform();
	}

	/**
	 * 現在の作業ディレクトリ上で、指定のコマンドを別プロセスとして実行します。
	 * コマンド文字列は、空白によって区切られ、文字列リストに変換されます。
	 * このメソッドは、コマンドの実行が完了するまで(正確には、起動した別プロセスが
	 * 終了するまで)制御を戻しません。
	 * <p>この関数は、{@link ProcessBuilder} を使用して別プロセスでのコマンド実行を行います。
	 * @param command	コマンドを表す文字列
	 * @return	終了コード
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	コマンドが実行できない場合
	 * @see #exec(String, String)
	 */
	static public int exec(String command) {
		return exec(command, null);
	}

	/**
	 * 指定された作業ディレクトリ上で、指定のコマンドを別プロセスとして実行します。
	 * コマンド文字列は、空白によって区切られ、文字列リストに変換されます。
	 * このメソッドは、コマンドの実行が完了するまで(正確には、起動した別プロセスが
	 * 終了するまで)制御を戻しません。
	 * <p>この関数は、{@link ProcessBuilder} を使用して別プロセスでのコマンド実行を行います。
	 * @param command	コマンドを表す文字列
	 * @param workdir	作業ディレクトリのパスを示す文字列
	 * @return 終了コード
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	コマンドが実行できない場合
	 * @see #exec(List, String)
	 */
	static public int exec(String command, String workdir) {
		if (command.length() < 1)
			throw new IllegalArgumentException("exec : Empty command");
		String[] cmdarray = _splitCommandLineWithDoubleQuoteEscape(command);
		return exec(Arrays.asList(cmdarray), workdir);
	}

	/**
	 * 現在の作業ディレクトリ上で、指定のコマンドを別プロセスとして実行します。
	 * このメソッドは、コマンドの実行が完了するまで(正確には、起動した別プロセスが
	 * 終了するまで)制御を戻しません。
	 * <p>この関数は、{@link ProcessBuilder} を使用して別プロセスでのコマンド実行を行います。
	 * @param commands	コマンドの文字列を格納する文字列リスト
	 * @return	終了コード
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	コマンドが実行できない場合
	 * @see #exec(List, String)
	 */
	static public int exec(List<String> commands) {
		return exec(commands, null);
	}

	/**
	 * 指定された作業ディレクトリ上で、指定のコマンドを別プロセスとして実行します。
	 * このメソッドは、コマンドの実行が完了するまで(正確には、起動した別プロセスが
	 * 終了するまで)制御を戻しません。
	 * <p>この関数は、{@link ProcessBuilder} を使用して別プロセスでのコマンド実行を行います。
	 * @param commands	コマンドの文字列を格納する文字列リスト
	 * @param workdir	作業ディレクトリのパスを示す文字列
	 * @return	終了コード
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	コマンドが実行できない場合
	 * @see ProcessBuilder#ProcessBuilder(List)
	 */
	static public int exec(List<String> commands, String workdir) {
		if (commands.isEmpty())
			throw new IllegalArgumentException("Empty commands");
		ProcessBuilder pb = new ProcessBuilder(commands);
		if (workdir != null && workdir.length() > 0) {
			pb.directory(new java.io.File(workdir));
		}
		_enquoteCommandList(pb.command());
		// start process
		Process cmdProc = null;
		try {
			cmdProc = pb.start();
		} catch (java.io.IOException ex) {
			throw new AADLRuntimeException(ex);
		}
		// start output handler thread
		(new ProcessStreamHandler(cmdProc.getInputStream(), System.out)).start();
		(new ProcessStreamHandler(cmdProc.getErrorStream(), System.err)).start();
		try {
			cmdProc.waitFor();
		} catch (InterruptedException ex) { ex=null; }
		cmdProc.destroy();
		//
		try{cmdProc.getInputStream().close();}catch(java.io.IOException ignoreEx){}
		try{cmdProc.getOutputStream().close();}catch(java.io.IOException ignoreEx){}
		try{cmdProc.getErrorStream().close();}catch(java.io.IOException ignoreEx){}
		return cmdProc.exitValue();
	}
	
	//********************************************************************
	// Registered functions for dtalge package.
	// @sincle 1.40
	//********************************************************************

	/**
	 * データ代数元(<em>alge</em>)の要素が空である場合に <tt>true</tt> を返します。
	 * @param alge	データ代数元
	 * @return 要素が存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.Dtalge#isEmpty()
	 * 
	 * @since 1.40
	 */
	static public boolean isEmpty(Dtalge alge) {
		return alge.isEmpty();
	}

	/**
	 * シソーラス定義(<em>thes</em>)の要素が空である場合に <tt>true</tt> を返します。
	 * @param thes	シソーラス定義
	 * @return 要素が存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtStringThesaurus#isEmpty()
	 * 
	 * @since 1.40
	 */
	static public boolean isEmpty(DtStringThesaurus thes) {
		return thes.isEmpty();
	}

	/**
	 * データ代数元(<em>alge</em>)に含まれる全てのデータ代数基底の集合を返します。
	 * データ代数元が要素を持たない場合、要素が空のデータ代数基底集合を返します。
	 * @param alge データ代数元
	 * @return データ代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.Dtalge#getBases()
	 * 
	 * @since 1.40
	 */
	static public DtBaseSet getBases(Dtalge alge) {
		return alge.getBases();
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)に含まれる全てのデータ代数基底の集合を返します。
	 * 異なるデータ代数元に同じ基底が含まれている場合でも、返されるデータ代数基底集合内では
	 * 基底は重複しません。データ代数集合の要素が空の場合、要素が空のデータ代数基底集合を返します。
	 * @param algeset データ代数集合
	 * @return データ代数基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#getBases()
	 * 
	 * @since 1.40
	 */
	static public DtBaseSet getBases(DtAlgeSet algeset) {
		return algeset.getBases();
	}
	
	/**
	 * データ代数元(<em>alge</em>)から、データ代数基底を 1 つだけ抽出します。
	 * データ代数元に要素が複数含まれている場合は、どの基底を取得するかは指定できません。
	 * データ代数元の要素が空の場合は例外をスローします。
	 * @param alge	データ代数元
	 * @return	データ代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws java.util.NoSuchElementException	データ代数元の要素が空の場合
	 * @see dtalge.Dtalge#getOneBase()
	 * 
	 * @since 1.40
	 */
	static public DtBase getOneBase(Dtalge alge) {
		return alge.getOneBase();
	}

	/**
	 * データ代数元(<em>alge</em>)から、要素の値を 1 つだけ抽出します。
	 * データ代数元に要素が複数含まれている場合は、どの値を取得するかは指定できません。
	 * データ代数元の要素が空の場合は例外をスローします。
	 * @param alge	データ代数元
	 * @return	データ代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws java.util.NoSuchElementException	データ代数元の要素が空の場合
	 * @see dtalge.Dtalge#getOneValue()
	 * 
	 * @since 1.40
	 */
	static public Object getOneValue(Dtalge alge) {
		return alge.getOneValue();
	}
	
	/**
	 * データ代数基底(<em>base</em>)から、名前キー文字列を取得します。
	 * @param base	データ代数基底
	 * @return 名前キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBase#getNameKey()
	 * 
	 * @since 1.40
	 */
	static public String getNameKey(DtBase base) {
		return base.getNameKey();
	}
	
	/**
	 * データ代数基底(<em>base</em>)から、データ型キー文字列を取得します。
	 * @param base	データ代数基底
	 * @return データ型キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBase#getTypeKey()
	 * 
	 * @since 1.40
	 */
	static public String getTypeKey(DtBase base) {
		return base.getTypeKey();
	}
	
	/**
	 * データ代数基底(<em>base</em>)から、属性キー文字列を取得します。
	 * @param base	データ代数基底
	 * @return 属性キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBase#getAttributeKey()
	 * 
	 * @since 1.40
	 */
	static public String getAttributeKey(DtBase base) {
		return base.getAttributeKey();
	}
	
	/**
	 * データ代数基底(<em>base</em>)から、主体キー文字列を取得します。
	 * @param base	データ代数基底
	 * @return 主体キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBase#getSubjectKey()
	 * 
	 * @since 1.40
	 */
	static public String getSubjectKey(DtBase base) {
		return base.getSubjectKey();
	}
	
	/**
	 * データ代数基底パターン(<em>pattern</em>)から、名前キー文字列を取得します。
	 * @param pattern	データ代数基底パターン
	 * @return 名前キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBasePattern#getNameKey()
	 * 
	 * @since 1.40
	 */
	static public String getNameKey(DtBasePattern pattern) {
		return pattern.getNameKey();
	}
	
	/**
	 * データ代数基底パターン(<em>pattern</em>)から、データ型キー文字列を取得します。
	 * @param pattern	データ代数基底パターン
	 * @return データ型キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBasePattern#getTypeKey()
	 * 
	 * @since 1.40
	 */
	static public String getTypeKey(DtBasePattern pattern) {
		return pattern.getTypeKey();
	}
	
	/**
	 * データ代数基底パターン(<em>pattern</em>)から、属性キー文字列を取得します。
	 * @param pattern	データ代数基底パターン
	 * @return 属性キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBasePattern#getAttributeKey()
	 * 
	 * @since 1.40
	 */
	static public String getAttributeKey(DtBasePattern pattern) {
		return pattern.getAttributeKey();
	}
	
	/**
	 * データ代数基底パターン(<em>pattern</em>)から、主体キー文字列を取得します。
	 * @param pattern	データ代数基底パターン
	 * @return 主体キー
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBasePattern#getSubjectKey()
	 * 
	 * @since 1.40
	 */
	static public String getSubjectKey(DtBasePattern pattern) {
		return pattern.getSubjectKey();
	}

	/**
	 * 指定された名前キーを持つ、新しいデータ代数基底パターンオブジェクトを生成します。
	 * 指定されないデータ型キー、属性キー、主体キーは、ワイルドカード記号('*')となります。
	 * @param name		名前キー
	 * @return	データ代数基底パターン
	 * @see dtalge.DtBasePattern#newPattern(String, String)
	 * 
	 * @since 1.40
	 */
	static public DtBasePattern newDtBasePattern(String name) {
		return DtBasePattern.newPattern(name, DtBasePattern.WILDCARD);
	}

	/**
	 * 指定された名前キー、データ型キーを持つ、新しいデータ代数基底パターンオブジェクトを生成します。
	 * 指定されない属性キー、主体キーは、ワイルドカード記号('*')となります。
	 * @param name		名前キー
	 * @param type		データ型キー
	 * @return	データ代数基底パターン
	 * @see dtalge.DtBasePattern#newPattern(String, String)
	 * 
	 * @since 1.40
	 */
	static public DtBasePattern newDtBasePattern(String name, String type) {
		return DtBasePattern.newPattern(name, type);
	}

	/**
	 * 指定された名前キー、データ型キー、属性キーを持つ、新しいデータ代数基底パターンオブジェクトを生成します。
	 * 指定されない主体キーは、ワイルドカード記号('*')となります。
	 * @param name		名前キー
	 * @param type		データ型キー
	 * @param attr		属性キー
	 * @return	データ代数基底パターン
	 * @see dtalge.DtBasePattern#newPattern(String, String, String)
	 * 
	 * @since 1.40
	 */
	static public DtBasePattern newDtBasePattern(String name, String type, String attr) {
		return DtBasePattern.newPattern(name, type, attr);
	}

	/**
	 * 指定された名前キー、データ型キー、属性キー、主体キーを持つ、新しいデータ代数基底パターンオブジェクトを生成します。
	 * @param name		名前キー
	 * @param type		データ型キー
	 * @param attr		属性キー
	 * @param subject	主体キー
	 * @return	データ代数基底パターン
	 * @see dtalge.DtBasePattern#newPattern(String, String, String, String)
	 * 
	 * @since 1.40
	 */
	static public DtBasePattern newDtBasePattern(String name, String type, String attr, String subject) {
		return DtBasePattern.newPattern(name, type, attr, subject);
	}

	/**
	 * 指定された文字列リスト(<em>keys</em>)に格納されている文字列を基底キーとする、
	 * 新しいデータ代数基底パターンオブジェクトを生成します。文字列リストと基底キーとの対応は、
	 * 文字列リストの先頭から、名前キー、データ型キー、属性キー、主体キーとなります。
	 * 文字列リストの長さが 4 未満の場合、不足した基底キーはワイルドカード記号('*')となります。
	 * @param keys		名前キー、データ型キー、属性キー、主体キー の順に指定された文字列リスト
	 * @return	データ代数基底パターン
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBasePattern#newPattern(String[])
	 * 
	 * @since 1.40
	 */
	static public DtBasePattern newDtBasePattern(List<String> keys) {
		int len = keys.size();
		if (len > 0) {
			String[] basekeys = keys.toArray(new String[len]);
			return DtBasePattern.newPattern(basekeys);
		} else {
			return DtBasePattern.newPattern(DtBasePattern.WILDCARD, DtBasePattern.WILDCARD);
		}
	}
	
	/**
	 * データ代数基底(<em>base</em>)の名前キー、データ型キー、属性キー、主体キーを持つ、
	 * 新しいデータ代数基底パターンオブジェクトを生成します。
	 * @param base	データ代数基底
	 * @return データ代数基底パターン
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBasePattern#newPattern(DtBase, boolean)
	 * 
	 * @since 1.40
	 */
	static public DtBasePattern toDtBasePattern(DtBase base) {
		return DtBasePattern.newPattern(base, false);
	}

	/**
	 * データ代数基底集合(<em>bases</em>)に含まれるすべてのデータ代数基底について、
	 * 基底の名前キー、データ型キー、属性キー、主体キーを持つ、
	 * 新しいデータ代数基底パターンオブジェクトを生成します。
	 * 生成されたすべての基底パターンは、新しいデータ代数基底パターン集合に格納されます。
	 * @param bases	データ代数基底集合
	 * @return	生成されたデータ代数基底パターン集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtBasePatternSet#DtBasePatternSet(DtBaseSet, boolean)
	 * 
	 * @since 1.40
	 */
	static public DtBasePatternSet toDtBasePattern(DtBaseSet bases) {
		return new DtBasePatternSet(bases, false);
	}

	/**
	 * データ代数元(<em>alge</em>)から、値が <tt>null</tt> の要素を除いた、
	 * 新しいデータ代数元を返します。
	 * @param alge 対象のデータ代数元
	 * @return 値 <tt>null</tt> の要素を除いた、新しいデータ代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.Dtalge#normalization()
	 * 
	 * @since 1.40
	 */
	static public Dtalge normalize(Dtalge alge) {
		return alge.normalization();
	}

	/**
	 * データ代数元(<em>alge</em>)に含まれる要素のうち、データ代数基底(<em>base</em>)に対応する
	 * 要素の値が振替元の値(<em>from</em>)と等しい場合のみ、その値を振替先の値(<em>to</em>)に
	 * 置き換えたデータ代数元を生成します。
	 * 指定されたデータ代数基底(<em>base</em>)がデータ代数元(<em>alge</em>)に存在しない場合や、
	 * データ代数基底(<em>base</em>)に対応する要素の値が
	 * 振替元の値(<em>from</em>)と等しくない場合は、指定されたデータ代数元(<em>alge</em>)を
	 * そのまま返します。振替元の値(<em>from</em>)と振替先の値(<em>to</em>)のデータ型が
	 * データ代数基底(<em>base</em>)のデータ型キーと異なる場合は、例外がスローされます。
	 * @param alge	対象のデータ代数元
	 * @param base	振替対象とするデータ代数基底
	 * @param from	振替元の値
	 * @param to	振替先の値
	 * @return	振替後の要素を格納する、新しいデータ代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws dtalge.exception.IllegalValueOfDataTypeException	指定された振替先の値が基底のデータ型と異なる場合
	 * @see dtalge.Dtalge#thesconv(DtBase, Object, Object)
	 * 
	 * @since 1.40
	 */
	static public Dtalge thesconv(Dtalge alge, DtBase base, Object from, Object to) {
		return alge.thesconv(base, from, to);
	}

	/**
	 * データ代数元(<em>alge</em>)に含まれる要素のうち、データ代数基底(<em>base</em>)に対応する
	 * 要素の値を、シソーラス定義(<em>thes</em>)に従い、分類集合(<em>values</em>)の値に振り替えた
	 * データ代数元を生成します。
	 * シソーラス定義に基づく分類集合への値振替では、データ代数基底(<em>base</em>)に対応する要素の値が
	 * シソーラス定義(<em>thes</em>)に属し、分類集合(<em>values</em>)に含まれる値よりも小さい場合、
	 * 分類集合内の関係する値へ振り替えられます。
	 * データ代数基底(<em>base</em>)に対応する要素の値が分類集合のどの値とも関係を持たない場合は、
	 * 振替不可能とみなし <tt>null</tt> を返します。
	 * データ代数基底(<em>base</em>)がデータ代数元(<em>alge</em>)に存在しない場合や、
	 * データ代数基底(<em>base</em>)のデータ型キーが文字列型ではない場合、例外がスローされます。
	 * @param alge		振替対象のデータ代数元
	 * @param base		振替対象とするデータ代数基底
	 * @param thes		シソーラス定義
	 * @param values	振替先とする値の分類集合
	 * @return	振替に成功した場合は、振替後の要素を格納するデータ代数元を返す。
	 * 			振替不可能の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が文字列型ではない場合、
	 * 										もしくは、指定された語句の集合が分類集合ではない場合
	 * @throws dtalge.exception.DtBaseNotFoundException	指定された基底がデータ代数元に含まれていない場合
	 * @see dtalge.Dtalge#thesconv(DtBase, DtStringThesaurus, Collection)
	 * 
	 * @since 1.40
	 */
	static public Dtalge thesconv(Dtalge alge, DtBase base, DtStringThesaurus thes, List<String> values) {
		return alge.thesconv(base, thes, values);
	}

	/**
	 * <tt>null</tt> 値が交換代数元(<em>alge</em>)の要素に含まれている場合に <tt>true</tt> を返します。
	 * @param alge	交換代数元
	 * @return	<tt>null</tt> 値が要素に含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#containsNull()
	 * 
	 * @since 1.40
	 */
	static public boolean containsNull(Exalge alge) {
		return alge.containsNull();
	}

	/**
	 * <tt>null</tt> 値がデータ代数元(<em>alge</em>)の要素に含まれている場合に <tt>true</tt> を返します。
	 * @param alge	データ代数元
	 * @return	<tt>null</tt> 値が要素に含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.Dtalge#containsNull()
	 * 
	 * @since 1.40
	 */
	static public boolean containsNull(Dtalge alge) {
		return alge.containsNull();
	}

	/**
	 * 交換代数元(<em>alge</em>)から、値が <tt>null</tt> の要素のみを含む交換代数元を生成します。
	 * 値が <tt>null</tt> の要素が存在しない場合は、要素が空の交換代数元を返します。
	 * @param alge	交換代数元
	 * @return	値が <tt>null</tt> の要素のみを含む、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#nullProjection()
	 * 
	 * @since 1.40
	 */
	static public Exalge nullProj(Exalge alge) {
		return alge.nullProjection();
	}

	/**
	 * 交換代数集合(<em>alges</em>)から、値が <tt>null</tt> の要素のみを含む交換代数元の集合を生成します。
	 * 生成される交換代数集合は、指定された交換代数集合(<em>alges</em>)のすべての要素(<code>Exalge</code>)に対し
	 * {@link #nullProj(Exalge)} を実行した結果となる交換代数元の集合であり、要素が空の交換代数元は含まれません。
	 * 値が <tt>null</tt> の要素がどの交換代数元にも存在しない場合は、要素が空の交換代数集合を返します。
	 * @param alges	交換代数集合
	 * @return	値が <tt>null</tt> の要素のみを含む交換代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#nullProjection()
	 * 
	 * @since 1.40
	 */
	static public ExAlgeSet nullProj(ExAlgeSet alges) {
		return alges.nullProjection();
	}

	/**
	 * データ代数元(<em>alge</em>)から、値が <tt>null</tt> の要素のみを含むデータ代数元を生成します。
	 * 値が <tt>null</tt> の要素が存在しない場合は、要素が空のデータ代数元を返します。
	 * @param alge	データ代数元
	 * @return	値が <tt>null</tt> の要素のみを含む、新しいデータ代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.Dtalge#nullProjection()
	 * 
	 * @since 1.40
	 */
	static public Dtalge nullProj(Dtalge alge) {
		return alge.nullProjection();
	}

	/**
	 * データ代数集合(<em>alges</em>)から、値が <tt>null</tt> の要素のみを含むデータ代数元の集合を生成します。
	 * 生成されるデータ代数集合は、指定されたデータ代数集合(<em>alges</em>)のすべての要素(<code>Dtalge</code>)に対し
	 * {@link #nullProj(Dtalge)} を実行した結果となるデータ代数元の集合であり、要素が空のデータ代数元は含まれません。
	 * 値が <tt>null</tt> の要素がどのデータ代数元にも存在しない場合は、要素が空のデータ代数集合を返します。
	 * @param alges	データ換代数集合
	 * @return	値が <tt>null</tt> の要素のみを含むデータ代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#nullProjection()
	 * 
	 * @since 1.40
	 */
	static public DtAlgeSet nullProj(DtAlgeSet alges) {
		return alges.nullProjection();
	}

	/**
	 * 交換代数元(<em>alge</em>)から、値が <tt>null</tt> ではない要素のみを含む交換代数元を生成します。
	 * 値が <tt>null</tt> ではない要素が存在しない場合は、要素が空の交換代数元を返します。
	 * @param alge	交換代数元
	 * @return	値が <tt>null</tt> ではない要素のみを含む、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#nonullProjection()
	 * 
	 * @since 1.40
	 */
	static public Exalge nonullProj(Exalge alge) {
		return alge.nonullProjection();
	}

	/**
	 * 交換代数集合(<em>alges</em>)から、値が <tt>null</tt> ではない要素のみを含む交換代数元の集合を生成します。
	 * 生成される交換代数集合は、指定された交換代数集合(<em>alges</em>)のすべての要素(<code>Exalge</code>)に対し
	 * {@link #nonullProj(Exalge)} を実行した結果となる交換代数元の集合であり、要素が空の交換代数元は含まれません。
	 * 値が <tt>null</tt> ではない要素がどの交換代数元にも存在しない場合は、要素が空の交換代数集合を返します。
	 * @param alges	交換代数集合
	 * @return	値が <tt>null</tt> ではない要素のみを含む交換代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#nonullProjection()
	 * 
	 * @since 1.40
	 */
	static public ExAlgeSet nonullProj(ExAlgeSet alges) {
		return alges.nonullProjection();
	}

	/**
	 * データ代数元(<em>alge</em>)から、値が <tt>null</tt> ではない要素のみを含むデータ代数元を生成します。
	 * 値が <tt>null</tt> ではない要素が存在しない場合は、要素が空のデータ代数元を返します。
	 * @param alge	データ代数元
	 * @return	値が <tt>null</tt> ではない要素のみを含む、新しいデータ代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.Dtalge#nonullProjection()
	 * 
	 * @since 1.40
	 */
	static public Dtalge nonullProj(Dtalge alge) {
		return alge.nonullProjection();
	}

	/**
	 * データ代数集合(<em>alges</em>)から、値が <tt>null</tt> ではない要素のみを含むデータ代数元の集合を生成します。
	 * 生成されるデータ代数集合は、指定されたデータ代数集合(<em>alges</em>)のすべての要素(<code>Dtalge</code>)に対し
	 * {@link #nonullProj(Dtalge)} を実行した結果となるデータ代数元の集合であり、要素が空のデータ代数元は含まれません。
	 * 値が <tt>null</tt> ではない要素がどのデータ代数元にも存在しない場合は、要素が空のデータ代数集合を返します。
	 * @param alges	データ換代数集合
	 * @return	値が <tt>null</tt> ではない要素のみを含むデータ代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#nonullProjection()
	 * 
	 * @since 1.40
	 */
	static public DtAlgeSet nonullProj(DtAlgeSet alges) {
		return alges.nonullProjection();
	}

	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param alge	出力対象のデータ代数元
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCSV(File)
	 * @see dtalge.Dtalge#toTableCSV(File, String)
	 * 
	 * @since 1.40
	 */
	static public void writeTableCsvFile(Dtalge alge, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCSV(new File(filename));
			} else {
				alge.toTableCSV(new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}

	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param alge	出力対象のデータ代数元
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCSV(File, String)
	 * 
	 * @since 1.40
	 */
	static public void writeTableCsvFile(Dtalge alge, String filename, String charsetName) {
		try {
			alge.toTableCSV(new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param alge	出力対象のデータ代数集合
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCSV(File)
	 * @see dtalge.DtAlgeSet#toTableCSV(File, String)
	 * 
	 * @since 1.40
	 */
	static public void writeTableCsvFile(DtAlgeSet alge, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCSV(new File(filename));
			} else {
				alge.toTableCSV(new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param alge	出力対象のデータ代数集合
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCSV(File, String)
	 * 
	 * @since 1.40
	 */
	static public void writeTableCsvFile(DtAlgeSet alge, String filename, String charsetName) {
		try {
			alge.toTableCSV(new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}

	//------------------------------------------------------------
	// Public interfaces since 1.60
	//------------------------------------------------------------

	/**
	 * AADL/ADDL のオブジェクト比較において、2 つのオブジェクトが同値かを検証します。
	 * 基本的に、<code>Comparable</code> インタフェースを実装しているもの同士は、
	 * 一方のオブジェクトにキャスト可能な場合のみ <code>compareTo</code> メソッドを呼び出します。
	 * それ以外の場合は <code>equals</code> メソッドを呼び出します。
	 * @param obj1	検査するオブジェクトの一方
	 * @param obj2	検査するオブジェクトのもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.60
	 */
	@SuppressWarnings("unchecked")
	static public boolean isEqual(Object obj1, Object obj2) {
		if (obj1 == obj2) {
			return true;	// same instance
		}
		if (obj1==null || obj2 == null) {
			return false;	// (null)!=(not null)
		}
		
		if ((obj1 instanceof Comparable) && (obj2 instanceof Comparable)) {
			if (obj1.getClass().isAssignableFrom(obj2.getClass())) {
				return (0 == ((Comparable)obj1).compareTo(obj2));
			}
			else if (obj2.getClass().isAssignableFrom(obj1.getClass())) {
				return (0 == ((Comparable)obj2).compareTo(obj1));
			}
			else {
				return obj1.equals(obj2);
			}
		}
		else {
			return obj1.equals(obj2);
		}
	}

	/**
	 * シソーラス定義(<em>thes</em>)から、指定された語句(<em>word</em>)の直接の親となるすべての語句を取得します。
	 * @param thes	シソーラス定義
	 * @param word	基準となる語句
	 * @return	親となるすべての語句を格納する文字列リストを返す。
	 * 			語句が登録されていない場合や、親となる語句が未定義の場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtStringThesaurus#getThesaurusParents(String)
	 * @since 1.60
	 */
	static public List<String> getThesaurusParents(DtStringThesaurus thes, String word) {
		return thes.getThesaurusParents(word);
	}

	/**
	 * シソーラス定義(<em>thes</em>)から、指定された語句(<em>word</em>)の直接の子となるすべての語句を取得します。
	 * @param thes	シソーラス定義
	 * @param word	基準となる語句
	 * @return	子となるすべての語句を格納する文字列リストを返す。
	 * 			語句が登録されていない場合や、子となる語句が未定義の場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtStringThesaurus#getThesaurusChildren(String)
	 * @since 1.60
	 */
	static public List<String> getThesaurusChildren(DtStringThesaurus thes, String word) {
		return thes.getThesaurusChildren(word);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に含まれるすべてのデータ代数元を結合します。
	 * 異なるデータ代数元に同じデータ代数基底が含まれている場合、その基底に関連付けられている値は、
	 * 内部的な結合の順序で上書きされます。その際、数値的な加算等の演算は行われません。
	 * @param algeset	データ代数集合
	 * @return 結合結果となるデータ代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#sum()
	 * @see dtalge.Dtalge#sum(Collection)
	 * 
	 * @since 1.60
	 */
	static public Dtalge sum(DtAlgeSet algeset) {
		return Dtalge.sum(algeset);
	}

	/**
	 * データ代数集合<em>algeset</em>)から、データ代数基底(<em>base</em>)に割り当てられているすべての値を抽出します。
	 * データ代数基底(<em>base</em>)のデータ型が文字列型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられたすべての値を格納する文字列リストを返す。
	 * 			基底に関連付けられた値が一つも存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が文字列型ではない場合
	 * @see dtalge.DtAlgeSet#toStringList(DtBase)
	 * @since 1.60
	 */
	static public List<String> toStringList(DtAlgeSet algeset, DtBase base) {
		return algeset.toStringList(base);
	}

	/**
	 * データ代数集合<em>algeset</em>)から、データ代数基底(<em>base</em>)に割り当てられている
	 * すべての重複しない(同値ではない)値を抽出します。
	 * データ代数基底(<em>base</em>)のデータ型が文字列型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられたすべての重複しない(同値ではない)値を格納する文字列リストを返す。
	 * 			基底に関連付けられた値が一つも存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が文字列型ではない場合
	 * @see dtalge.DtAlgeSet#toDistinctStringList(DtBase)
	 * @since 1.60
	 */
	static public List<String> toDistinctStringList(DtAlgeSet algeset, DtBase base) {
		return algeset.toDistinctStringList(base);
	}
	
	/**
	 * データ代数集合<em>algeset</em>)から、データ代数基底(<em>base</em>)に割り当てられているすべての値を抽出します。
	 * データ代数基底(<em>base</em>)のデータ型が実数値型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられたすべての値を格納する実数値リストを返す。
	 * 			基底に関連付けられた値が一つも存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が実数値型ではない場合
	 * @see dtalge.DtAlgeSet#toDecimalList(DtBase)
	 * @since 1.60
	 */
	static public List<BigDecimal> toDecimalList(DtAlgeSet algeset, DtBase base) {
		return algeset.toDecimalList(base);
	}
	
	/**
	 * データ代数集合<em>algeset</em>)から、データ代数基底(<em>base</em>)に割り当てられている
	 * すべての重複しない(同値ではない)値を抽出します。
	 * データ代数基底(<em>base</em>)のデータ型が実数値型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられたすべての重複しない(同値ではない)値を格納する実数値リストを返す。
	 * 			基底に関連付けられた値が一つも存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が実数値型ではない場合
	 * @see dtalge.DtAlgeSet#toDistinctDecimalList(DtBase)
	 * @since 1.60
	 */
	static public List<BigDecimal> toDistinctDecimalList(DtAlgeSet algeset, DtBase base) {
		return algeset.toDistinctDecimalList(base);
	}
	
	/**
	 * データ代数集合<em>algeset</em>)から、データ代数基底(<em>base</em>)に割り当てられているすべての値を抽出します。
	 * データ代数基底(<em>base</em>)のデータ型が真偽値型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられたすべての値を格納する真偽値リストを返す。
	 * 			基底に関連付けられた値が一つも存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が真偽値型ではない場合
	 * @see dtalge.DtAlgeSet#toBooleanList(DtBase)
	 * @since 1.60
	 */
	static public List<Boolean> toBooleanList(DtAlgeSet algeset, DtBase base) {
		return algeset.toBooleanList(base);
	}
	
	/**
	 * データ代数集合<em>algeset</em>)から、データ代数基底(<em>base</em>)に割り当てられている
	 * すべての重複しない(同値ではない)値を抽出します。
	 * データ代数基底(<em>base</em>)のデータ型が真偽値型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられたすべての重複しない(同値ではない)値を格納する真偽値リストを返す。
	 * 			基底に関連付けられた値が一つも存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が真偽値型ではない場合
	 * @see dtalge.DtAlgeSet#toDistinctBooleanList(DtBase)
	 * @since 1.60
	 */
	static public List<Boolean> toDistinctBooleanList(DtAlgeSet algeset, DtBase base) {
		return algeset.toDistinctBooleanList(base);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に含まれる、データ代数基底(<em>base</em>)に関連付けられている値のうち、
	 * <tt>null</tt> ではない最小値を取得します。
	 * 比較可能な値の場合や、指定されたデータ代数基底を持つデータ代数元が一つも存在しない場合は <tt>null</tt> を返します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられている値の最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#minValue(DtBase)
	 * @since 1.60
	 */
	static public Object minValue(DtAlgeSet algeset, DtBase base) {
		return algeset.minValue(base);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)に含まれる、データ代数基底(<em>base</em>)に関連付けられている値のうち、
	 * <tt>null</tt> ではない最大値を取得します。
	 * 比較可能な値の場合や、指定されたデータ代数基底を持つデータ代数元が一つも存在しない場合は <tt>null</tt> を返します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられている値の最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#maxValue(DtBase)
	 * @since 1.60
	 */
	static public Object maxValue(DtAlgeSet algeset, DtBase base) {
		return algeset.maxValue(base);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に含まれる、データ代数基底(<em>base</em>)に関連付けられている値のうち、
	 * <tt>null</tt> ではない最小の実数値を取得します。
	 * 比較可能な値が一つも存在しない場合は <tt>null</tt> を返します。
	 * データ代数基底(<em>base</em>)のデータ型が実数値型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられている値の最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が実数値型ではない場合
	 * @see dtalge.DtAlgeSet#minDecimal(DtBase)
	 * @since 1.60
	 */
	static public BigDecimal minDecimal(DtAlgeSet algeset, DtBase base) {
		return algeset.minDecimal(base);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)に含まれる、データ代数基底(<em>base</em>)に関連付けられている値のうち、
	 * <tt>null</tt> ではない最大の実数値を取得します。
	 * 比較可能な値が一つも存在しない場合は <tt>null</tt> を返します。
	 * データ代数基底(<em>base</em>)のデータ型が実数値型ではない場合、この関数は例外をスローします。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @return	指定されたデータ代数基底に関連付けられている値の最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が実数値型ではない場合
	 * @see dtalge.DtAlgeSet#maxDecimal(DtBase)
	 * @since 1.60
	 */
	static public BigDecimal maxDecimal(DtAlgeSet algeset, DtBase base) {
		return algeset.maxDecimal(base);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に含まれるすべての要素を、データ代数基底(<em>base</em>)に関連付けられている値で並べ替えられた、
	 * 新しいデータ代数集合を生成します。
	 * 値の並べ替えにおいて、値が等しい要素の順序は維持されます。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられ、基底が存在しない要素は <tt>null</tt> 値よりも小さい要素として並べ替えられます。
	 * @param algeset		データ代数集合
	 * @param base			データ代数基底
	 * @param ascending		昇順に並べ替える場合は <tt>true</tt>、降順に並べ替える場合は <tt>false</tt>
	 * @return	指定された順序で要素が並べ替えられた、新しいデータ代数集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#sortedAlgesByValue(DtBase, boolean)
	 * @since 1.60
	 */
	static public DtAlgeSet sortedAlgesByValue(DtAlgeSet algeset, DtBase base, boolean ascending) {
		return algeset.sortedAlgesByValue(base, ascending);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に含まれるすべての要素を、データ代数基底(<em>base</em>)に関連付けられている値で並べ替えます。
	 * 値の並べ替えにおいて、値が等しい要素の順序は維持されます。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられ、基底が存在しない要素は <tt>null</tt> 値よりも小さい要素として並べ替えられます。
	 * <p><b>この関数は破壊型関数であり、指定されたデータ代数集合(<em>algeset</em>)そのものが書き換えられます。</b>
	 * @param algeset		データ代数集合
	 * @param base			データ代数基底
	 * @param ascending		昇順に並べ替える場合は <tt>true</tt>、降順に並べ替える場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#sortAlgesByValue(DtBase, boolean)
	 * @since 1.60
	 */
	static public void sortAlgesByValue(DtAlgeSet algeset, DtBase base, boolean ascending) {
		algeset.sortAlgesByValue(base, ascending);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)と等しいデータ代数元が存在するかどうかを判定します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	指定された値がデータ代数元のどれか一つに存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>algeset</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#containsValue(DtBase, Object)
	 * @since 1.60
	 */
	static public boolean containsValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.containsValue(base, value);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定されたコレクション(<em>values</em>)に含まれる値のどれか一つと等しいデータ代数元が存在するかどうかを判定します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param values	判定する値のコレクション
	 * @return	指定されたコレクションに含まれる値のどれか一つが、データ代数元のどれか一つに存在する場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>algeset</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#containsAnyValues(DtBase, Collection)
	 * @since 1.60
	 */
	static public boolean containsAnyValues(DtAlgeSet algeset, DtBase base, Collection<?> values) {
		return algeset.containsAnyValues(base, values);
	}

	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)と等しいデータ代数元のみを抽出します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#selectEqualValue(DtBase, Object)
	 * @since 1.60
	 */
	static public DtAlgeSet selectEqualValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.selectEqualValue(base, value);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)と等しくないデータ代数元のみを抽出します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#selectNotEqualValue(DtBase, Object)
	 * @since 1.60
	 */
	static public DtAlgeSet selectNotEqualValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.selectNotEqualValue(base, value);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)よりも小さいデータ代数元のみを抽出します。
	 * 指定された値(<em>value</em>)と比較不可能な要素は、結果に含まれません。
	 * 値(<em>value</em>)に <tt>null</tt> を指定した場合、この関数は要素が空のデータ代数集合を返します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#selectLessThanValue(DtBase, Object)
	 * @since 1.60
	 */
	static public DtAlgeSet selectLessThanValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.selectLessThanValue(base, value);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)よりも小さい、もしくは等しいデータ代数元のみを抽出します。
	 * 指定された値(<em>value</em>)と比較不可能な要素は、結果に含まれません。
	 * 値(<em>value</em>)に <tt>null</tt> を指定した場合、この関数は要素が空のデータ代数集合を返します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#selectLessEqualValue(DtBase, Object)
	 * @since 1.60
	 */
	static public DtAlgeSet selectLessEqualValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.selectLessEqualValue(base, value);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)よりも大きいデータ代数元のみを抽出します。
	 * 指定された値(<em>value</em>)と比較不可能な要素は、結果に含まれません。
	 * 値(<em>value</em>)に <tt>null</tt> を指定した場合、この関数は要素が空のデータ代数集合を返します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#selectGreaterThanValue(DtBase, Object)
	 * @since 1.60
	 */
	static public DtAlgeSet selectGreaterThanValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.selectGreaterThanValue(base, value);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)よりも大きい、もしくは等しいデータ代数元のみを抽出します。
	 * 指定された値(<em>value</em>)と比較不可能な要素は、結果に含まれません。
	 * 値(<em>value</em>)に <tt>null</tt> を指定した場合、この関数は要素が空のデータ代数集合を返します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#selectGreaterEqualValue(DtBase, Object)
	 * @since 1.60
	 */
	static public DtAlgeSet selectGreaterEqualValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.selectGreaterEqualValue(base, value);
	}

	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)と等しいデータ代数元が、指定されたデータ代数元(<em>newAlge</em>)に置き換えられた、
	 * 新しいデータ代数集合を生成します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	指定された値と等しい要素を持つデータ代数元が置き換えられた、新しいデータ代数集合を返す。
	 * 			指定された値と等しい要素を持つデータ代数元が一つも存在しない場合は、指定されたデータ代数集合と同じものを返す。
	 * @throws NullPointerException	<em>algeset</em>、<em>base</em>、<em>newAlge</em> のどれかが <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#replaceEqualValue(DtBase, Object, Dtalge)
	 * @since 1.60
	 */
	static public DtAlgeSet replaceEqualValue(DtAlgeSet algeset, DtBase base, Object value, Dtalge newAlge) {
		return algeset.replaceEqualValue(base, value, newAlge);
	}

	/**
	 * データ代数集合(<em>algeset</em>)の、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)と等しいデータ代数元を、指定されたデータ代数元(<em>newAlge</em>)に置き換えます。
	 * <p><b>この関数は破壊型関数であり、指定されたデータ代数集合(<em>algeset</em>)そのものが書き換えられます。</b>
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	データ代数元が置き換えられた場合は <tt>true</tt>、データ代数集合の内容が変更されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	<em>algeset</em>、<em>base</em>、<em>newAlge</em> のどれかが <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#updateEqualValue(DtBase, Object, Dtalge)
	 * @since 1.60
	 */
	static public boolean updateEqualValue(DtAlgeSet algeset, DtBase base, Object value, Dtalge newAlge) {
		return algeset.updateEqualValue(base, value, newAlge);
	}

	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)と等しいデータ代数元を取り除いた、新しいデータ代数集合を生成します。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	指定された値と等しい要素を持つデータ代数元を含まない、新しいデータ代数集合を返す。
	 * 			指定された値と等しい要素を持つデータ代数元が一つも存在しない場合は、指定されたデータ代数集合と同じものを返す。
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#removeEqualValue(DtBase, Object)
	 * @since 1.60
	 */
	static public DtAlgeSet removeEqualValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.removeEqualValue(base, value);
	}

	/**
	 * データ代数集合(<em>algeset</em>)から、データ代数基底(<em>base</em>)に関連付けられている値が
	 * 指定された値(<em>value</em>)と等しいデータ代数元を削除します。
	 * <p><b>この関数は破壊型関数であり、指定されたデータ代数集合(<em>algeset</em>)そのものが書き換えられます。</b>
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @return	データ代数元が削除された場合は <tt>true</tt>、データ代数集合の内容が変更されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	<em>algeset</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @see dtalge.DtAlgeSet#deleteEqualValue(DtBase, Object)
	 * @since 1.60
	 */
	static public boolean deleteEqualValue(DtAlgeSet algeset, DtBase base, Object value) {
		return algeset.deleteEqualValue(base, value);
	}

	/**
	 * データ代数集合(<em>algeset</em>)に含まれる、データ代数基底(<em>base</em>)に関連付けられている値のうち、
	 * シソーラス定義(<em>thes</em>)に基づく極大値となるデータ代数元のみを抽出します。
	 * この関数は、指定された基底(<em>base</em>)に関連付けられている値をシソーラス定義(<em>thes</em>)に基づいてすべて比較し、
	 * シソーラス定義において比較可能な値の中から最大の値を持つデータ代数元を取り出します。
	 * この関数が返すデータ代数集合に含まれる値は、シソーラス定義においてそれぞれ比較不可能もしくは同値となります。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param thes		シソーラス定義
	 * @return	指定されたデータ代数基底に関連付けられた値がシソーラス定義に基づく極大値となるデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			極大値を持つデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が文字列型ではない場合
	 * @see dtalge.DtAlgeSet#selectThesaurusMax(DtBase, DtStringThesaurus)
	 * @since 1.60
	 */
	static public DtAlgeSet selectThesaurusMax(DtAlgeSet algeset, DtBase base, DtStringThesaurus thes) {
		return algeset.selectThesaurusMax(base, thes);
	}
	
	/**
	 * データ代数集合(<em>algeset</em>)に含まれる、データ代数基底(<em>base</em>)に関連付けられている値のうち、
	 * シソーラス定義(<em>thes</em>)に基づく極小値となるデータ代数元のみを抽出します。
	 * この関数は、指定された基底(<em>base</em>)に関連付けられている値をシソーラス定義(<em>thes</em>)に基づいてすべて比較し、
	 * シソーラス定義において比較可能な値の中から最小の値を持つデータ代数元を取り出します。
	 * この関数が返すデータ代数集合に含まれる値は、シソーラス定義においてそれぞれ比較不可能もしくは同値となります。
	 * @param algeset	データ代数集合
	 * @param base		データ代数基底
	 * @param thes		シソーラス定義
	 * @return	指定されたデータ代数基底に関連付けられた値がシソーラス定義に基づく極小値となるデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			極小値を持つデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底のデータ型が文字列型ではない場合
	 * @see dtalge.DtAlgeSet#selectThesaurusMin(DtBase, DtStringThesaurus)
	 * @since 1.60
	 */
	static public DtAlgeSet selectThesaurusMin(DtAlgeSet algeset, DtBase base, DtStringThesaurus thes) {
		return algeset.selectThesaurusMin(base, thes);
	}

	//------------------------------------------------------------
	// Public interfaces since 1.70
	//------------------------------------------------------------
	
	/**
	 * 範囲先頭=0、範囲終端=<em>last</em> の、範囲終端を含まない数値範囲オブジェクトを生成します。
	 * (0 &lt;= <em>last</em>) の場合は、数値間隔=1 が設定されます。
	 * (0 &gt <em>last</em>) の場合は、数値間隔=(-1) が設定されます。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となります。
	 * <ul>
	 * <li>終端を含まない数値範囲で (範囲終端 == 0) の場合
	 * </ul>
	 * @param last			範囲終端の値
	 * @return	数値範囲オブジェクト
	 * @throws NullPointerException	<em>last</em> が <tt>null</tt> の場合
	 * @since 1.70
	 */
	static public SimpleDecimalRange range(BigDecimal last) {
		return new SimpleDecimalRange(last);
	}
	
	/**
	 * 範囲先頭=0、範囲終端=<em>last</em> の、数値範囲オブジェクトを生成します。
	 * (0 &lt;= <em>last</em>) の場合は、数値間隔=1 が設定されます。
	 * (0 &gt; <em>last</em>) の場合は、数値間隔=(-1) が設定されます。
	 * <em>includeLast</em> に <tt>true</tt> が指定された場合は、範囲終端を含む数値範囲オブジェクトとなります。
	 * <em>includeLast</em> に <tt>false</tt> が指定された場合は、範囲終端を含まない数値範囲オブジェクトとなります。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となります。
	 * <ul>
	 * <li>終端を含まない数値範囲で (範囲終端 == 0) の場合
	 * </ul>
	 * @param last			範囲終端の値
	 * @param includeLast	範囲終端を含める場合は <tt>true</tt>、範囲終端を含めない場合は <tt>false</tt>
	 * @return	数値範囲オブジェクト
	 * @throws NullPointerException	<em>last</em> が <tt>null</tt> の場合
	 * @since 1.70
	 */
	static public SimpleDecimalRange range(BigDecimal last, boolean includeLast) {
		return new SimpleDecimalRange(last, includeLast);
	}
	
	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em> の、範囲終端を含まない数値範囲オブジェクトを生成します。
	 * (<em>first</em> &lt;= <em>last</em>) の場合、数値間隔=1 が設定されます。
	 * (<em>first</em> &gt; <em>last</em>) の場合、数値間隔=(-1) が設定されます。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となります。
	 * <ul>
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @return	数値範囲オブジェクト
	 * @throws NullPointerException	<em>first</em>、<em>last</em> のどれかが <tt>null</tt> の場合
	 * @since 1.70
	 */
	static public SimpleDecimalRange range(BigDecimal first, BigDecimal last) {
		return new SimpleDecimalRange(first, last);
	}
	
	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em> の、数値範囲オブジェクトを生成します。
	 * (<em>first</em> &lt;= <em>last</em>) の場合、数値間隔=1 が設定されます。
	 * (<em>first</em> &gt; <em>last</em>) の場合、数値間隔=(-1) が設定されます。
	 * <em>includeLast</em> に <tt>true</tt> が指定された場合は、範囲終端を含む数値範囲オブジェクトとなります。
	 * <em>includeLast</em> に <tt>false</tt> が指定された場合は、範囲終端を含まない数値範囲オブジェクトとなります。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となります。
	 * <ul>
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @param includeLast	範囲終端を含める場合は <tt>true</tt>、範囲終端を含めない場合は <tt>false</tt>
	 * @return	数値範囲オブジェクト
	 * @throws NullPointerException	<em>first</em>、<em>last</em> のどれかが <tt>null</tt> の場合
	 * @since 1.70
	 */
	static public SimpleDecimalRange range(BigDecimal first, BigDecimal last, boolean includeLast) {
		return new SimpleDecimalRange(first, last, includeLast);
	}
	
	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em>、数値間隔=<em>step</em> の、範囲終端を含まない数値範囲オブジェクトを生成します。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となります。
	 * <ul>
	 * <li>数値間隔が 0 の場合
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @param step			範囲の数値間隔
	 * @return	数値範囲オブジェクト
	 * @throws NullPointerException	<em>first</em>、<em>last</em>、<em>step</em> のどれかが <tt>null</tt> の場合
	 * @since 1.70
	 */
	static public SimpleDecimalRange range(BigDecimal first, BigDecimal last, BigDecimal step) {
		return new SimpleDecimalRange(first, last, step);
	}
	
	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em>、数値間隔=<em>step</em> の、数値範囲オブジェクトを生成します。
	 * <em>includeLast</em> に <tt>true</tt> が指定された場合は、範囲終端を含む数値範囲オブジェクトとなります。
	 * <em>includeLast</em> に <tt>false</tt> が指定された場合は、範囲終端を含まない数値範囲オブジェクトとなります。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となります。
	 * <ul>
	 * <li>数値間隔が 0 の場合
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @param step			範囲の数値間隔
	 * @param includeLast	範囲終端を含める場合は <tt>true</tt>、範囲終端を含めない場合は <tt>false</tt>
	 * @return	数値範囲オブジェクト
	 * @throws NullPointerException	<em>first</em>、<em>last</em>、<em>step</em> のどれかが <tt>null</tt> の場合
	 * @since 1.70
	 */
	static public SimpleDecimalRange range(BigDecimal first, BigDecimal last, BigDecimal step, boolean includeLast) {
		return new SimpleDecimalRange(first, last, step, includeLast);
	}
	
	/**
	 * 指定された数値範囲定義文字列から、デフォルトの上限(最大値)までの数値範囲を生成します。
	 * 数値範囲定義文字列は、0 以上の自然数によって指定される不連続の数値範囲であり、
	 * 連続する数値範囲をハイフンで、連続ではない数値範囲をカンマで区切って記述します。
	 * 例えば、1、3、5～10、12～15、という数値範囲の場合、&quot;1,3,5-10,12-15&quot; というように記述します。
	 * ハイフンの前を省略した場合は 1 から指定された数値までの範囲とみなします。また、ハイフンの後を
	 * 省略した場合は指定された数値から最大値までの範囲と見なします。
	 * <p>生成される数値範囲オブジェクトは、数値範囲終端の値を含みます。また、数値間隔は、連続、不連続に
	 * 関わらず、常に 1 を返します。
	 * 数値範囲指定の文字列に、ハイフン、カンマ、数字、空白以外の文字が含まれている
	 * 場合や、数字を空白で区切って記述した場合、ハイフンの前後どちらかに数値が指定
	 * されていない場合は例外をスローします。<br>
	 * このコンストラクタで生成された数値範囲の上限(最大値)は、{@link java.lang.Long#MAX_VALUE} となります。
	 * @param format	数値範囲定義文字列
	 * @return	数値範囲オブジェクト
	 * @throws IllegalArgumentException	数値範囲定義文字列の書式が不正な場合
	 * @since 1.70
	 */
	static public NaturalNumberDecimalRange naturalNumberRange(String format) {
		return new NaturalNumberDecimalRange(format);
	}
	
	/**
	 * 指定された数値範囲定義文字列から、指定された上限(最大値)までの数値範囲を生成します。
	 * 数値範囲定義文字列は、0 以上の自然数によって指定される不連続の数値範囲であり、
	 * 連続する数値範囲をハイフンで、連続ではない数値範囲をカンマで区切って記述します。
	 * 例えば、1、3、5～10、12～15、という数値範囲の場合、&quot;1,3,5-10,12-15&quot; というように記述します。
	 * ハイフンの前を省略した場合は 1 から指定された数値までの範囲とみなします。また、ハイフンの後を
	 * 省略した場合は指定された数値から最大値までの範囲と見なします。
	 * <p>生成される数値範囲オブジェクトは、数値範囲終端の値を含みます。また、数値間隔は、連続、不連続に
	 * 関わらず、常に 1 を返します。
	 * 数値範囲指定の文字列に、ハイフン、カンマ、数字、空白以外の文字が含まれている
	 * 場合や、数字を空白で区切って記述した場合、ハイフンの前後どちらかに数値が指定
	 * されていない場合は例外をスローします。<br>
	 * <em>maxValue</em> に <tt>null</tt> を指定した場合の上限(最大値)は、{@link java.lang.Long#MAX_VALUE} となります。
	 * @param format	数値範囲定義文字列
	 * @param maxValue		範囲の上限とする値(0 以上の整数のみ)
	 * @return	数値範囲オブジェクト
	 * @throws IllegalArgumentException	数値範囲定義文字列の書式が不正な場合、
	 * 										<em>maxValue</em> が負の値、もしくは整数ではない場合
	 * @since 1.70
	 */
	static public NaturalNumberDecimalRange naturalNumberRange(String format, BigDecimal maxValue) {
		return new NaturalNumberDecimalRange(format, maxValue);
	}

	/**
	 * 指定された文字列が、数値範囲定義文字列として正しい書式であるかを判定します。
	 * このメソッドは、書式のみを判定するものであり、その記述内容から生成される数値範囲が
	 * 有効か無効かは判定には含まれません。従って、指定された文字列が <tt>null</tt>、もしくは空文字列でも、
	 * このメソッドは <tt>true</tt> を返します。
	 * @param format	判定する文字列
	 * @return	数値範囲定義文字列として正等なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.70
	 */
	static public boolean isNaturalNumberRangeFormat(final String format) {
		boolean ret;
		try {
			new NaturalNumberDecimalRange(format);
			ret = true;
		}
		catch (IllegalArgumentException ex) {
			ret = false;
		}
		return ret;
	}
	
	/**
	 * 指定された数値範囲オブジェクトの数値範囲が無効である場合に <tt>true</tt> を返します。
	 * @param range		判定する数値範囲オブジェクト
	 * @return	数値範囲が無効であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.Range#isEmpty()
	 * @since 1.70
	 */
	static public boolean isEmpty(final Range<?> range) {
		return range.isEmpty();
	}
	
	/**
	 * 指定された数値範囲が、範囲終端の値を含むよう指定されている場合に <tt>true</tt> を返します。
	 * 範囲の指定によっては、このメソッドが <tt>true</tt> を返す場合でも、範囲終端が含まれない場合があります。
	 * @param range		判定する数値範囲オブジェクト
	 * @return	範囲終端の値を含むよう指定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.Range#isIncludeRangeLast()
	 * @since 1.70
	 */
	static public boolean isIncludeRangeLast(final Range<?> range) {
		return range.isIncludeRangeLast();
	}
	
	/**
	 * 指定された数値範囲が増加方向か減少方向かを返します。
	 * @param range		判定する数値範囲オブジェクト
	 * @return	増加方向の数値範囲の場合は <tt>true</tt>、減少方向の数値範囲の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.Range#isIncremental()
	 * @since 1.70
	 */
	static public boolean isIncremental(final Range<?> range) {
		return range.isIncremental();
	}
	
	/**
	 * 指定された値が、指定の数値範囲内の要素と等しいかどうかを判定します。
	 * ただし、数値範囲が無効である場合、このメソッドは常に <tt>false</tt> を返します。
	 * @param range		数値範囲オブジェクト
	 * @param value		判定する値
	 * @return	数値範囲内の要素と等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			数値範囲が無効の場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>range</em> が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.Range#containsValue(Object)
	 * @since 1.70
	 */
	static public boolean containsValue(final Range<?> range, final Object value) {
		return range.containsValue(value);
	}
	
	/**
	 * 指定された数値範囲の範囲先頭として設定された値を返します。
	 * @param range		数値範囲オブジェクト
	 * @return	範囲先頭
	 * @throws NullPointerException	<em>range</em> が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.DecimalRange#rangeFirst()
	 * @since 1.70
	 */
	static public BigDecimal rangeFirst(final DecimalRange range) {
		return range.rangeFirst();
	}
	
	/**
	 * 指定された数値範囲の範囲終端として設定された値を返します。
	 * @param range		数値範囲オブジェクト
	 * @return	範囲終端
	 * @throws NullPointerException	<em>range</em> が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.DecimalRange#rangeLast()
	 * @since 1.70
	 */
	static public BigDecimal rangeLast(final DecimalRange range) {
		return range.rangeLast();
	}
	
	/**
	 * 指定された数値範囲の数値間隔として設定された値を返します。
	 * @param range		数値範囲オブジェクト
	 * @return	数値間隔
	 * @throws NullPointerException	<em>range</em> が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.DecimalRange#rangeStep()
	 * @since 1.70
	 */
	static public BigDecimal rangeStep(final DecimalRange range) {
		return range.rangeStep();
	}
	
	/**
	 * 指定された数値範囲の要素の中で最小の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @param range		数値範囲オブジェクト
	 * @return	数値範囲の要素の最小値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>range</em> が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.DecimalRange#rangeMin()
	 * @since 1.70
	 */
	static public BigDecimal rangeMin(final DecimalRange range) {
		return range.rangeMin();
	}
	
	/**
	 * 指定された数値範囲の要素の中で最大の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @param range		数値範囲オブジェクト
	 * @return	数値範囲の要素の最大値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>range</em> が <tt>null</tt> の場合
	 * @see ssac.aadl.runtime.util.range.DecimalRange#rangeMax()
	 * @since 1.70
	 */
	static public BigDecimal rangeMax(final DecimalRange range) {
		return range.rangeMax();
	}

	//------------------------------------------------------------
	// Public Helper functions
	//------------------------------------------------------------

	/**
	 * AADL/ADDL のテキストファイル入出力において、エンコーディングに使用する
	 * 文字セット名が指定されない場合の、デフォルトのエンコーディングとなる
	 * 文字セット名を返します。
	 * この関数は、システムプロパティ &quot;aadl.txt.encoding&quot; の内容を取得します。
	 * @return	デフォルトの文字セット名。設定されていない場合は <tt>null</tt>。
	 * 
	 * @since 1.70
	 */
	static public String getDefaultTextEncoding() {
		String name = System.getProperty(PROPKEY_AADL_TXT_ENCODING);
		return (name!=null && name.length()>0 ? name : null);
	}

	/**
	 * AADL/ADDL の CSV ファイル入出力において、エンコーディングに使用する
	 * 文字セット名が指定されない場合の、デフォルトのエンコーディングとなる
	 * 文字セット名を返します。
	 * この関数は、システムプロパティ &quot;aadl.csv.encoding&quot; の内容を取得します。
	 * @return	デフォルトの文字セット名。設定されていない場合は <tt>null</tt>。
	 * 
	 * @since 1.70
	 */
	static public String getDefaultCsvEncoding() {
		String name = System.getProperty(PROPKEY_AADL_CSV_ENCODING);
		return (name!=null && name.length()>0 ? name : null);
	}
	
	/**
	 * AADL/ADDL のテキストファイル入出力における、デフォルトのエンコーディングとなる
	 * 文字セット名を設定します。<em>charsetName</em> に <tt>null</tt> もしくは空文字列を
	 * 指定した場合は設定解除となります。
	 * この関数は、システムプロパティ &quot;aadl.txt.encoding&quot; の内容を変更します。
	 * @param charsetName	設定する文字セット名
	 * @throws AADLRuntimeException	指定された文字セットがサポートされていない場合
	 * 
	 * @since 1.70
	 */
	static public void setDefaultTextEncoding(String charsetName) {
		_setDefaultEncodingProperty(PROPKEY_AADL_TXT_ENCODING, charsetName);
	}
	
	/**
	 * AADL/ADDL の CSV ファイル入出力における、デフォルトのエンコーディングとなる
	 * 文字セット名を設定します。<em>charsetName</em> に <tt>null</tt> もしくは空文字列を
	 * 指定した場合は設定解除となります。
	 * この関数は、システムプロパティ &quot;aadl.csv.encoding&quot; の内容を変更します。
	 * @param charsetName	設定する文字セット名
	 * @throws AADLRuntimeException	指定された文字セットがサポートされていない場合
	 * 
	 * @since 1.70
	 */
	static public void setDefaultCsvEncoding(String charsetName) {
		_setDefaultEncodingProperty(PROPKEY_AADL_CSV_ENCODING, charsetName);
	}

	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、交換代数元を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数元
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.Exalge#fromCSV(File)
	 * @see exalge2.Exalge#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public Exalge newExalgeFromCsvFile(String filepath) {
		if (filepath == null)
			throw new NullPointerException("'filepath' argument is null.");
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return Exalge.fromCSV(new File(filepath));
			else
				return Exalge.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}

	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、交換代数元を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成された交換代数元
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.Exalge#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public Exalge newExalgeFromCsvFile(String filepath, String charsetName) {
		if (filepath == null)
			throw new NullPointerException("'filepath' argument is null.");
		if (charsetName == null)
			throw new NullPointerException("'charsetName' argument is null.");
		try {
			return Exalge.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}

	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数集合の XML 標準形として読み込み、
	 * 交換代数元を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数元
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.Exalge#fromXML(File)
	 * @since 1.70		
	 */
	static public Exalge newExalgeFromXmlFile(String filepath) {
		if (filepath == null)
			throw new NullPointerException("'filepath' argument is null.");
		try {
			return Exalge.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、交換代数集合を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExAlgeSet#fromCSV(File)
	 * @see exalge2.ExAlgeSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExAlgeSet newExAlgeSetFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return ExAlgeSet.fromCSV(new File(filepath));
			else
				return ExAlgeSet.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、交換代数集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成された交換代数集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExAlgeSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExAlgeSet newExAlgeSetFromCsvFile(String filepath, String charsetName) {
		try {
			return ExAlgeSet.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数集合の XML 標準形として読み込み、
	 * 交換代数集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExAlgeSet#fromXML(File)
	 * @since 1.70		
	 */
	static public ExAlgeSet newExAlgeSetFromXmlFile(String filepath) {
		try {
			return ExAlgeSet.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数基底集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、交換代数基底集合を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数基底集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBaseSet#fromCSV(File)
	 * @see exalge2.ExBaseSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExBaseSet newExBaseSetFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return ExBaseSet.fromCSV(new File(filepath));
			else
				return ExBaseSet.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数基底集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、交換代数基底集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成された交換代数基底集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBaseSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExBaseSet newExBaseSetFromCsvFile(String filepath, String charsetName) {
		try {
			return ExBaseSet.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数基底集合の XML 標準形として読み込み、
	 * 交換代数基底集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数基底集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBaseSet#fromXML(File)
	 * @since 1.70		
	 */
	static public ExBaseSet newExBaseSetFromXmlFile(String filepath) {
		try {
			return ExBaseSet.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数基底パターン集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、交換代数基底パターン集合を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数基底パターン集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBasePatternSet#fromCSV(File)
	 * @see exalge2.ExBasePatternSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExBasePatternSet newExBasePatternSetFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return ExBasePatternSet.fromCSV(new File(filepath));
			else
				return ExBasePatternSet.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数基底パターン集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、交換代数基底パターン集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成された交換代数基底パターン集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBasePatternSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExBasePatternSet newExBasePatternSetFromCsvFile(String filepath, String charsetName) {
		try {
			return ExBasePatternSet.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、交換代数基底パターン集合の XML 標準形として読み込み、
	 * 交換代数基底パターン集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された交換代数基底パターン集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBasePatternSet#fromXML(File)
	 * @since 1.70		
	 */
	static public ExBasePatternSet newExBasePatternSetFromXmlFile(String filepath) {
		try {
			return ExBasePatternSet.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、振替変換テーブルの CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、振替変換テーブルを生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された振替変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransTable#fromCSV(File)
	 * @see exalge2.TransTable#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public TransTable newTransTableFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return TransTable.fromCSV(new File(filepath));
			else
				return TransTable.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、振替変換テーブルの CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、振替変換テーブルを生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成された振替変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransTable#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public TransTable newTransTableFromCsvFile(String filepath, String charsetName) {
		try {
			return TransTable.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、振替変換テーブルの XML 標準形として読み込み、
	 * 振替変換テーブルを生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された振替変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransTable#fromXML(File)
	 * @since 1.70		
	 */
	static public TransTable newTransTableFromXmlFile(String filepath) {
		try {
			return TransTable.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、按分変換テーブルの CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、按分変換テーブルを生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された按分変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransMatrix#fromCSV(File)
	 * @see exalge2.TransMatrix#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public TransMatrix newTransMatrixFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return TransMatrix.fromCSV(new File(filepath));
			else
				return TransMatrix.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、按分変換テーブルの CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、按分変換テーブルを生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成された按分変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransMatrix#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public TransMatrix newTransMatrixFromCsvFile(String filepath, String charsetName) {
		try {
			return TransMatrix.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、按分変換テーブルの XML 標準形として読み込み、
	 * 按分変換テーブルを生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された按分変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransMatrix#fromXML(File)
	 * @since 1.70		
	 */
	static public TransMatrix newTransMatrixFromXmlFile(String filepath) {
		try {
			return TransMatrix.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、変換テーブルの CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、変換テーブルを生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExTransfer#fromCSV(File)
	 * @see exalge2.ExTransfer#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExTransfer newExTransferFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return ExTransfer.fromCSV(new File(filepath));
			else
				return ExTransfer.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、変換テーブルの CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、変換テーブルを生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成された変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExTransfer#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public ExTransfer newExTransferFromCsvFile(String filepath, String charsetName) {
		try {
			return ExTransfer.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、変換テーブルの XML 標準形として読み込み、
	 * 変換テーブルを生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成された変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExTransfer#fromXML(File)
	 * @since 1.70		
	 */
	static public ExTransfer newExTransferFromXmlFile(String filepath) {
		try {
			return ExTransfer.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、データ代数元を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数元
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.Dtalge#fromCSV(File)
	 * @see dtalge.Dtalge#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public Dtalge newDtalgeFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return Dtalge.fromCSV(new File(filepath));
			else
				return Dtalge.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、データ代数元を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成されたデータ代数元
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.Dtalge#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public Dtalge newDtalgeFromCsvFile(String filepath, String charsetName) {
		try {
			return Dtalge.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数集合の XML 標準形として読み込み、
	 * データ代数元を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数元
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.Dtalge#fromXML(File)
	 * @since 1.70		
	 */
	static public Dtalge newDtalgeFromXmlFile(String filepath) {
		try {
			return Dtalge.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、データ代数集合を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtAlgeSet#fromCSV(File)
	 * @see dtalge.DtAlgeSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtAlgeSet newDtAlgeSetFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return DtAlgeSet.fromCSV(new File(filepath));
			else
				return DtAlgeSet.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、データ代数集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成されたデータ代数集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtAlgeSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtAlgeSet newDtAlgeSetFromCsvFile(String filepath, String charsetName) {
		try {
			return DtAlgeSet.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数集合の XML 標準形として読み込み、
	 * データ代数集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtAlgeSet#fromXML(File)
	 * @since 1.70		
	 */
	static public DtAlgeSet newDtAlgeSetFromXmlFile(String filepath) {
		try {
			return DtAlgeSet.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数基底集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、データ代数基底集合を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数基底集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtBaseSet#fromCSV(File)
	 * @see dtalge.DtBaseSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtBaseSet newDtBaseSetFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return DtBaseSet.fromCSV(new File(filepath));
			else
				return DtBaseSet.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数基底集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、データ代数基底集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成されたデータ代数基底集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtBaseSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtBaseSet newDtBaseSetFromCsvFile(String filepath, String charsetName) {
		try {
			return DtBaseSet.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数基底集合の XML 標準形として読み込み、
	 * データ代数基底集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数基底集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtBaseSet#fromXML(File)
	 * @since 1.70		
	 */
	static public DtBaseSet newDtBaseSetFromXmlFile(String filepath) {
		try {
			return DtBaseSet.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数基底パターン集合の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、データ代数基底パターン集合を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数基底パターン集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtBasePatternSet#fromCSV(File)
	 * @see dtalge.DtBasePatternSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtBasePatternSet newDtBasePatternSetFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return DtBasePatternSet.fromCSV(new File(filepath));
			else
				return DtBasePatternSet.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数基底パターン集合の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、データ代数基底パターン集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成されたデータ代数基底パターン集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtBasePatternSet#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtBasePatternSet newDtBasePatternSetFromCsvFile(String filepath, String charsetName) {
		try {
			return DtBasePatternSet.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、データ代数基底パターン集合の XML 標準形として読み込み、
	 * データ代数基底パターン集合を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたデータ代数基底パターン集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtBasePatternSet#fromXML(File)
	 * @since 1.70		
	 */
	static public DtBasePatternSet newDtBasePatternSetFromXmlFile(String filepath) {
		try {
			return DtBasePatternSet.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、シソーラス定義の CSV 標準形として
	 * デフォルト・エンコーディングで読み込み、シソーラス定義を生成します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたシソーラス定義
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtStringThesaurus#fromCSV(File)
	 * @see dtalge.DtStringThesaurus#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtStringThesaurus newDtStringThesaurusFromCsvFile(String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				return DtStringThesaurus.fromCSV(new File(filepath));
			else
				return DtStringThesaurus.fromCSV(new File(filepath), _defEncoding);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、シソーラス定義の CSV 標準形として
	 * 指定されたエンコーディング(<em>charsetName</em>)で読み込み、シソーラス定義を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @return	生成されたシソーラス定義
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtStringThesaurus#fromCSV(File, String)
	 * @since 1.70		
	 */
	static public DtStringThesaurus newDtStringThesaurusFromCsvFile(String filepath, String charsetName) {
		try {
			return DtStringThesaurus.fromCSV(new File(filepath), charsetName);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定されたファイル(<em>filepath</em>)を、シソーラス定義の XML 標準形として読み込み、
	 * シソーラス定義を生成します。
	 * @param filepath	読み込み対象のファイルのパスを示す文字列
	 * @return	生成されたシソーラス定義
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see dtalge.DtStringThesaurus#fromXML(File)
	 * @since 1.70		
	 */
	static public DtStringThesaurus newDtStringThesaurusFromXmlFile(String filepath) {
		try {
			return DtStringThesaurus.fromXML(new File(filepath));
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数元(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象の交換代数元
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.Exalge#toCSV(File)
	 * @see exalge2.Exalge#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(Exalge obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数元(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象の交換代数元
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.Exalge#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(Exalge obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数元(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象の交換代数元
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.Exalge#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(Exalge obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象の交換代数集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExAlgeSet#toCSV(File)
	 * @see exalge2.ExAlgeSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExAlgeSet obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象の交換代数集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExAlgeSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExAlgeSet obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数集合(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象の交換代数集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExAlgeSet#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(ExAlgeSet obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数基底集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象の交換代数基底集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExBaseSet#toCSV(File)
	 * @see exalge2.ExBaseSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExBaseSet obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数基底集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象の交換代数基底集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExBaseSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExBaseSet obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数基底集合(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象の交換代数基底集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExBaseSet#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(ExBaseSet obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数基底パターン集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象の交換代数基底パターン集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExBasePatternSet#toCSV(File)
	 * @see exalge2.ExBasePatternSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExBasePatternSet obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数基底パターン集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象の交換代数基底パターン集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExBasePatternSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExBasePatternSet obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数基底パターン集合(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象の交換代数基底パターン集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExBasePatternSet#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(ExBasePatternSet obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 振替変換テーブル(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象の振替変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.TransTable#toCSV(File)
	 * @see exalge2.TransTable#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(TransTable obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 振替変換テーブル(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象の振替変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.TransTable#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(TransTable obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 振替変換テーブル(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象の振替変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.TransTable#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(TransTable obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 按分変換テーブル(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象の按分変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.TransMatrix#toCSV(File)
	 * @see exalge2.TransMatrix#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(TransMatrix obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 按分変換テーブル(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象の按分変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.TransMatrix#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(TransMatrix obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 按分変換テーブル(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象の按分変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.TransMatrix#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(TransMatrix obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 変換テーブル(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象の変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExTransfer#toCSV(File)
	 * @see exalge2.ExTransfer#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExTransfer obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 変換テーブル(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象の変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExTransfer#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(ExTransfer obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 変換テーブル(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象の変換テーブル
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see exalge2.ExTransfer#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(ExTransfer obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象のデータ代数元
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toCSV(File)
	 * @see dtalge.Dtalge#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(Dtalge obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象のデータ代数元
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(Dtalge obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象のデータ代数元
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(Dtalge obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象のデータ代数集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toCSV(File)
	 * @see dtalge.DtAlgeSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtAlgeSet obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象のデータ代数集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtAlgeSet obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象のデータ代数集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(DtAlgeSet obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * データ代数基底集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象のデータ代数基底集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtBaseSet#toCSV(File)
	 * @see dtalge.DtBaseSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtBaseSet obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数基底集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象のデータ代数基底集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtBaseSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtBaseSet obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数基底集合(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象のデータ代数基底集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtBaseSet#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(DtBaseSet obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * データ代数基底パターン集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象のデータ代数基底パターン集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtBasePatternSet#toCSV(File)
	 * @see dtalge.DtBasePatternSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtBasePatternSet obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数基底パターン集合(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象のデータ代数基底パターン集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtBasePatternSet#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtBasePatternSet obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数基底パターン集合(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象のデータ代数基底パターン集合
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtBasePatternSet#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(DtBasePatternSet obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * シソーラス定義(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * @param obj	出力対象のシソーラス定義
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtStringThesaurus#toCSV(File)
	 * @see dtalge.DtStringThesaurus#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtStringThesaurus obj, String filepath) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				obj.toCSV(new File(filepath));
			} else {
				obj.toCSV(new File(filepath), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * シソーラス定義(<em>obj</em>)の内容を、CSV 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * @param obj	出力対象のシソーラス定義
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtStringThesaurus#toCSV(File, String)
	 * @since 1.70		
	 */
	static public void writeCsvFile(DtStringThesaurus obj, String filepath, String charsetName) {
		try {
			obj.toCSV(new File(filepath), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * シソーラス定義(<em>obj</em>)の内容を、XML 標準形のファイルとして、
	 * 指定された出力先(<em>filepath</em>)に出力します。
	 * @param obj	出力対象のシソーラス定義
	 * @param filepath	出力先ファイルのパスを示す文字列
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtStringThesaurus#toXML(File)
	 * @since 1.70		
	 */
	static public void writeXmlFile(DtStringThesaurus obj, String filepath) {
		try {
			obj.toXML(new File(filepath));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	//------------------------------------------------------------
	// Public interfaces since 1.80
	//------------------------------------------------------------
	
	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * <p>
	 * 基底のない要素のフィールドは空欄となります。また、基底に対応する値が <tt>null</tt> もしくは空文字列の場合、
	 * その値は出力されません。特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>出力される基底の順序は、データ代数に格納されている基底の順序となります。
	 * 
	 * @param alge	出力対象のデータ代数元
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCsvWithoutNull(File)
	 * @see dtalge.Dtalge#toTableCsvWithoutNull(File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithoutNull(Dtalge alge, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCsvWithoutNull(new File(filename));
			} else {
				alge.toTableCsvWithoutNull(new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * <p>
	 * 基底のない要素のフィールドは空欄となります。また、基底に対応する値が <tt>null</tt> もしくは空文字列の場合、
	 * その値は出力されません。特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>出力される基底の順序は、データ代数に格納されている基底の順序となります。
	 * 
	 * @param alge	出力対象のデータ代数元
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCsvWithoutNull(File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithoutNull(Dtalge alge, String filename, String charsetName) {
		try {
			alge.toTableCsvWithoutNull(new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * <p>
	 * 基底のない要素のフィールドは空欄となります。また、基底に対応する値が <tt>null</tt> もしくは空文字列の場合、
	 * その値は出力されません。特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>出力される基底の順序は、データ代数に格納されている基底の順序となります。
	 * 
	 * @param alge	出力対象のデータ代数集合
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCsvWithoutNull(File)
	 * @see dtalge.DtAlgeSet#toTableCsvWithoutNull(File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithoutNull(DtAlgeSet alge, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCsvWithoutNull(new File(filename));
			} else {
				alge.toTableCsvWithoutNull(new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * <p>
	 * 基底のない要素のフィールドは空欄となります。また、基底に対応する値が <tt>null</tt> もしくは空文字列の場合、
	 * その値は出力されません。特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>出力される基底の順序は、データ代数に格納されている基底の順序となります。
	 * 
	 * @param alge	出力対象のデータ代数集合
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCsvWithoutNull(File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithoutNull(DtAlgeSet alge, String filename, String charsetName) {
		try {
			alge.toTableCsvWithoutNull(new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * 
	 * @param alge	出力対象のデータ代数元
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File)
	 * @see dtalge.Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithBaseOrder(Dtalge alge, DtBaseSet order, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCsvWithBaseOrder(order, new File(filename));
			} else {
				alge.toTableCsvWithBaseOrder(order, new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * 
	 * @param alge	出力対象のデータ代数元
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithBaseOrder(Dtalge alge, DtBaseSet order, String filename, String charsetName) {
		try {
			alge.toTableCsvWithBaseOrder(order, new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * 
	 * @param alge	出力対象のデータ代数集合
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File)
	 * @see dtalge.DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithBaseOrder(DtAlgeSet alge, DtBaseSet order, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCsvWithBaseOrder(order, new File(filename));
			} else {
				alge.toTableCsvWithBaseOrder(order, new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * 
	 * @param alge	出力対象のデータ代数集合
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFileWithBaseOrder(DtAlgeSet alge, DtBaseSet order, String filename, String charsetName) {
		try {
			alge.toTableCsvWithBaseOrder(order, new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力されません。
	 * 
	 * @param alge	出力対象のデータ代数元
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCSV(DtBaseSet, boolean, File)
	 * @see dtalge.Dtalge#toTableCSV(DtBaseSet, boolean, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFile(Dtalge alge, DtBaseSet order, boolean withoutNull, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCSV(order, withoutNull, new File(filename));
			} else {
				alge.toTableCSV(order, withoutNull, new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数元(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力されません。
	 * 
	 * @param alge	出力対象のデータ代数元
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.Dtalge#toTableCSV(DtBaseSet, boolean, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFile(Dtalge alge, DtBaseSet order, boolean withoutNull, String filename, String charsetName) {
		try {
			alge.toTableCSV(order, withoutNull, new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)にデフォルト・エンコーディングで出力します。
	 * デフォルト・エンコーディングは、{@link #getDefaultCsvEncoding()} が返す文字セットが適用されます。
	 * {@link #getDefaultCsvEncoding()} が <tt>null</tt> を返す場合は、プラットフォーム
	 * 標準の文字セットが適用されます。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力されません。
	 * 
	 * @param alge	出力対象のデータ代数集合
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param filename	出力先ファイルのパス名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCSV(DtBaseSet, boolean, File)
	 * @see dtalge.DtAlgeSet#toTableCSV(DtBaseSet, boolean, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFile(DtAlgeSet alge, DtBaseSet order, boolean withoutNull, String filename) {
		try {
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null) {
				alge.toTableCSV(order, withoutNull, new File(filename));
			} else {
				alge.toTableCSV(order, withoutNull, new File(filename), _defEncoding);
			}
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * データ代数集合(<em>alge</em>)の内容を、テーブル形式の CSV ファイルとして、
	 * 指定された出力先(<em>filename</em>)に指定されたエンコーディング(<em>charsetName</em>)で出力します。
	 * <p>
	 * 基底のない要素のフィールドは空欄となり、<tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力されます。
	 * 特殊記号で始まる値は、特殊記号でエスケープされます。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力されます。
	 * 順序指定用基底集合に含まれない基底は、行の終端にデータ代数に格納されている順序で出力されます。
	 * 順序が指定されていない場合は、データ代数に格納されている基底の順序で出力されます。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力されません。
	 * 
	 * @param alge	出力対象のデータ代数集合
	 * @param order			順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param filename	出力先ファイルのパス名
	 * @param charsetName	出力時のエンコーディングとする文字セット名
	 * @throws NullPointerException	<em>order</em> 以外の引数のどれかが <tt>null</tt> の場合
	 * @throws AADLRuntimeException	出力に失敗した場合
	 * @see dtalge.DtAlgeSet#toTableCSV(DtBaseSet, boolean, File, String)
	 * 
	 * @since 1.80
	 */
	static public void writeTableCsvFile(DtAlgeSet alge, DtBaseSet order, boolean withoutNull, String filename, String charsetName) {
		try {
			alge.toTableCSV(order, withoutNull, new File(filename), charsetName);
		} catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}

	//------------------------------------------------------------
	// Public interfaces since 1.90
	//------------------------------------------------------------
	
	/**
	 * 指定された文字列を真偽値に変換します。
	 * 指定された文字列が大文字小文字に関係なく &quot;true&quot; の場合は <tt>true</tt>、
	 * それ以外の場合は <tt>false</tt> に変換されます。
	 * @param str 変換する文字列
	 * @return 変換後の真偽値
	 * @since 1.90
	 */
	static public boolean toBoolean(String str) {
		return ((str != null) && str.equalsIgnoreCase("true"));
	}

	/**
	 * 指定された文字列が大文字小文字に関係なく、&quot;true&quot; もしくは &quot;false&quot; なら <tt>true</tt> を返します。
	 * @param str	判定する。
	 * @return	&quot;true&quot; もしくは &quot;false&quot; なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 1.90
	 */
	static public boolean isBooleanExactly(String str) {
		return ((str != null) && (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false")));
	}

	/**
	 * 指定された真偽値リストから、指定されたインデックスに対応する真偽値を返す。
	 * インデックスが範囲外の場合は <tt>null</tt> を返す。
	 * 真偽値リストの要素の値として <tt>null</tt> が返される場合もある。
	 * @param list		真偽値リスト
	 * @param index	要素の位置を示すインデックス
	 * @return	インデックスに対応する位置の要素を返す。インデックスが範囲外の場合は <tt>null</tt> を返す。
	 * @since 1.90
	 */
	static public Boolean getBooleanValueByIndex(List<Boolean> list, BigDecimal index) {
		if (index.compareTo(BigDecimal.ZERO) >= 0 && index.compareTo(toDecimal(list.size())) < 0) {
			return list.get(index.intValue());
		} else {
			return null;
		}
	}

	/**
	 * 指定された真偽値リストから、指定された番号に対応する真偽値を返す。
	 * 番号が範囲外の場合は <tt>null</tt> を返す。
	 * 真偽値リストの要素の値として <tt>null</tt> が返される場合もある。
	 * @param list		真偽値リスト
	 * @param number	要素の位置を示す 1 から始まる番号
	 * @return	番号に対応する位置の要素を返す。番号が範囲外の場合は <tt>null</tt> を返す。
	 * @since 1.90
	 */
	static public Boolean getBooleanValueByNumber(List<Boolean> list, BigDecimal number) {
		if (number.compareTo(BigDecimal.ONE) >= 0 && number.compareTo(toDecimal(list.size())) <= 0) {
			return list.get(number.intValue()-1);
		} else {
			return null;
		}
	}

	/**
	 * 指定された数値リストから、指定されたインデックスに対応する数値を返す。
	 * インデックスが範囲外の場合は <tt>null</tt> を返す。
	 * 数値リストの要素の値として <tt>null</tt> が返される場合もある。
	 * @param list		数値リスト
	 * @param index	要素の位置を示すインデックス
	 * @return	インデックスに対応する位置の要素を返す。インデックスが範囲外の場合は <tt>null</tt> を返す。
	 * @since 1.90
	 */
	static public BigDecimal getDecimalValueByIndex(List<? extends BigDecimal> list, BigDecimal index) {
		if (index.compareTo(BigDecimal.ZERO) >= 0 && index.compareTo(toDecimal(list.size())) < 0) {
			return list.get(index.intValue());
		} else {
			return null;
		}
	}

	/**
	 * 指定された数値リストから、指定された番号に対応する数値を返す。
	 * 番号が範囲外の場合は <tt>null</tt> を返す。
	 * 数値リストの要素の値として <tt>null</tt> が返される場合もある。
	 * @param list		数値リスト
	 * @param number	要素の位置を示す 1 から始まる番号
	 * @return	番号に対応する位置の要素を返す。番号が範囲外の場合は <tt>null</tt> を返す。
	 * @since 1.90
	 */
	static public BigDecimal getDecimalValueByNumber(List<? extends BigDecimal> list, BigDecimal number) {
		if (number.compareTo(BigDecimal.ONE) >= 0 && number.compareTo(toDecimal(list.size())) <= 0) {
			return list.get(number.intValue()-1);
		} else {
			return null;
		}
	}

	/**
	 * 指定された文字列リストから、指定されたインデックスに対応する文字列を返す。
	 * インデックスが範囲外の場合は <tt>null</tt> を返す。
	 * 文字列リストの要素の値として <tt>null</tt> が返される場合もある。
	 * @param list		文字列リスト
	 * @param index	要素の位置を示すインデックス
	 * @return	インデックスに対応する位置の要素を返す。インデックスが範囲外の場合は <tt>null</tt> を返す。
	 * @since 1.90
	 */
	static public String getStringValueByIndex(List<String> list, BigDecimal index) {
		if (index.compareTo(BigDecimal.ZERO) >= 0 && index.compareTo(toDecimal(list.size())) < 0) {
			return list.get(index.intValue());
		} else {
			return null;
		}
	}

	/**
	 * 指定された文字列リストから、指定された番号に対応する文字列を返す。
	 * 番号が範囲外の場合は <tt>null</tt> を返す。
	 * 文字列リストの要素の値として <tt>null</tt> が返される場合もある。
	 * @param list		文字列リスト
	 * @param number	要素の位置を示す 1 から始まる番号
	 * @return	番号に対応する位置の要素を返す。番号が範囲外の場合は <tt>null</tt> を返す。
	 * @since 1.90
	 */
	static public String getStringValueByNumber(List<String> list, BigDecimal number) {
		if (number.compareTo(BigDecimal.ONE) >= 0 && number.compareTo(toDecimal(list.size())) <= 0) {
			return list.get(number.intValue()-1);
		} else {
			return null;
		}
	}

	/**
	 * 指定されたファイルまたはディレクトリを削除します。指定されたファイルがディレクトリの場合、そのディレクトリが空でない場合は削除できません。
	 * @param filename	ファイルのパスを示す文字列
	 * @return	削除できた場合、もしくはファイルが存在しない場合は <tt>true</tt>、削除できなかった場合は <tt>false</tt>
	 * @since 1.90
	 */
	static public boolean deleteFile(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}

	/**
	 * 指定されたファイルまたはディレクトリを削除します。
	 * 指定されたファイルがディレクトリの場合、<em>recursive</em> に指定した値によって動作が異なります。
	 * <em>recursive</em> に <tt>true</tt> を指定した場合は、そのディレクトリに含まれるすべてのファイルまたはディレクトリを削除します。
	 * <em>recursive</em> に <tt>false</tt> を指定した場合は、そのディレクトリが空の場合のみ削除します。
	 * @param filename	ファイルのパスを示す文字列
	 * @param recursive	指定されたディレクトリ内のすべてのファイルまたはディレクトリを削除する場合は <tt>true</tt>
	 * @return	削除できた場合、もしくはファイルが存在しない場合は <tt>true</tt>、削除できなかった場合は <tt>false</tt>
	 * @since 1.90
	 */
	static public boolean deleteFile(String filename, boolean recursive) {
		if (recursive) {
			return deleteFileRecursive(new File(filename));
		} else {
			return deleteFile(filename);
		}
	}

	/**
	 * 指定された接頭辞と接尾辞をファイル名の生成に使用して、デフォルトの一時ファイルディレクトリに空のファイルを生成します。
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。3 文字以上の長さが必要である
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @return	新規作成された空のファイルを示す抽象パス名
	 * @throws IllegalArgumentException	<em>prefix</em> が 3 文字に満たない場合
	 * @throws RuntimeException	ファイルが生成できなかった場合
	 * @since 1.90
	 */
	static public String createTemporaryFile(String prefix, String suffix) {
		return createTemporaryFile(null, prefix, suffix);
	}

	/**
	 * 指定された接頭辞と接尾辞をファイル名の生成に使用して、指定されたディレクトリに空のファイルを生成します。
	 * <em>directory</em> が <tt>null</tt> の場合、システムに依存するデフォルトの一時ファイルディレクトリが使用されます。
	 * @param directory	ファイルが生成されるディレクトリ。デフォルトの一時ファイルディレクトリを使用する場合は <tt>null</tt>
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。3 文字以上の長さが必要である
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @return	新規作成された空のファイルを示す抽象パス名
	 * @throws IllegalArgumentException	<em>prefix</em> が 3 文字に満たない場合
	 * @throws RuntimeException	ファイルが生成できなかった場合
	 * @since 1.90
	 */
	static public String createTemporaryFile(String directory, String prefix, String suffix) {
		try {
			File dir = (directory==null ? null : new File(directory));
			File tmpfile = File.createTempFile(prefix, suffix, dir);
			tmpfile.deleteOnExit();
			return tmpfile.getPath();
		} catch (IOException ex) {
			throw new RuntimeException("Failed to create Temporary file in \"" + String.valueOf(directory) + "\"", ex);
		} catch (SecurityException ex) {
			throw new RuntimeException("Failed to create Temporary file in \"" + String.valueOf(directory) + "\"", ex);
		}
	}
	
	/**
	 * 指定された接頭辞と接尾辞をファイル名の生成に使用して、デフォルトの一時ファイルディレクトリに空のディレクトリを生成します。
	 * なお、ここで作成されたディレクトリは、自動的には削除されません。
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。3 文字以上の長さが必要である
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @return	新規作成された空のディレクトリを示す抽象パス名
	 * @throws IllegalArgumentException	<em>prefix</em> が 3 文字に満たない場合
	 * @throws RuntimeException	ディレクトリが生成できなかった場合
	 * @since 1.90
	 */
	static public String createTemporaryDirectory(String prefix, String suffix) {
		return createTemporaryDirectory(null, prefix, suffix);
	}
	
	/**
	 * 指定された接頭辞と接尾辞をファイル名の生成に使用して、指定されたディレクトリに空のディレクトリを生成します。
	 * <em>directory</em> が <tt>null</tt> の場合、システムに依存するデフォルトの一時ファイルディレクトリが使用されます。
	 * @param directory	ファイルが生成されるディレクトリ。デフォルトの一時ファイルディレクトリを使用する場合は <tt>null</tt>
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。3 文字以上の長さが必要である
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @return	新規作成された空のディレクトリを示す抽象パス名
	 * @throws IllegalArgumentException	<em>prefix</em> が 3 文字に満たない場合
	 * @throws RuntimeException	ディレクトリが生成できなかった場合
	 * @since 1.90
	 */
	static public String createTemporaryDirectory(String directory, String prefix, String suffix) {
		try {
			File dir = (directory==null ? null : new File(directory));
			File tmpfile = File.createTempFile(prefix, suffix, dir);
			tmpfile.delete();
			Thread.yield();
			if (dir.mkdir()) {
				return tmpfile.getPath();
			}
			throw new RuntimeException("Filaed to create Temporary directory in \"" + String.valueOf(directory) + "\"");
		} catch (IOException ex) {
			throw new RuntimeException("Failed to create Temporary directory in \"" + String.valueOf(directory) + "\"", ex);
		} catch (SecurityException ex) {
			throw new RuntimeException("Failed to create Temporary directory in \"" + String.valueOf(directory) + "\"", ex);
		}
	}

	//------------------------------------------------------------
	// Public interfaces since 2.0.0
	//------------------------------------------------------------
	
	/**
	 * このプロセスに中断要求が発生している場合、その中断要求に対し応答を開始したことを示す内部ステータスをセットし、
	 * 中断を示す実行時例外({@link ssac.aadl.runtime.AADLTerminatedException})をスローする。
	 * 中断要求が発生していない場合、このメソッドは何も行わない。
	 * <p>中断要求が発生していた場合、このメソッドを呼び出したスレッドの割り込みステータスもクリアされる。
	 * @throws AADLTerminatedException
	 * 
	 * @since 2.0.0
	 */
	static public void checkAndAcceptTerminateRequest() {
		if (acceptTerminateRequest()) {
			throw new AADLTerminatedException();
		}
	}

	/**
	 * このプロセスに中断要求が発生している場合、その中断要求に対し応答を開始したことを示す内部ステータスをセットする。
	 * 中断要求が発生していない場合、このメソッドは何も行わずに <tt>false</tt> を返す。
	 * <p>このメソッドが <tt>true</tt> を返す場合、このメソッドを呼び出したスレッドの割り込みステータスもクリアされる。
	 * なお、このメソッドが <tt>true</tt> を返す間は、このメソッド呼び出しの度に、このメソッドを呼び出したスレッドの
	 * 割り込みステータスがクリアされる。
	 * @return	中断要求があった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * 
	 * @since 2.0.0
	 */
	static public boolean acceptTerminateRequest() {
		try {
			Class<?> clazz = Class.forName("ssac.aadl.macro.process.InterruptibleJavaMainProcessImpl");
			Method mt = clazz.getMethod("acceptTerminateRequest");
			Boolean result = (Boolean)mt.invoke(null);
			return result.booleanValue();
		}
		catch (LinkageError ex) {
			return false;
		}
		catch (ClassNotFoundException ex) {
			return false;
		}
		catch (SecurityException ex) {
			return false;
		}
		catch (NoSuchMethodException ex) {
			return false;
		}
		catch (IllegalArgumentException ex) {
			return false;
		}
		catch (IllegalAccessException ex) {
			return false;
		}
		catch (InvocationTargetException ex) {
			return false;
		}
	}
	
	/**
	 * @deprecated	このメソッドは互換性のためにのみ定義されています。
	 * このプロセスに中断要求が発生している場合、その中断要求に対し応答を開始したことを示す内部ステータスをセットする。
	 * 中断要求が発生していない場合、このメソッドは何も行わずに <tt>false</tt> を返す。
	 * <p>このメソッドが <tt>true</tt> を返す場合、このメソッドを呼び出したスレッドの割り込みステータスもクリアされる。
	 * なお、このメソッドが <tt>true</tt> を返す間は、このメソッド呼び出しの度に、このメソッドを呼び出したスレッドの
	 * 割り込みステータスがクリアされる。
	 * @return	中断要求があった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @see #acceptTerminateRequest()
	 * 
	 * @since 2.0.0
	 */
	@Deprecated
	static protected boolean acceptTerminateReqest() {
		return acceptTerminateRequest();
	}

	/**
	 * このプロセスに中断要求があったかどうかを判定する。
	 * このメソッドは、中断要求があったかどうかを判定するのみであり、中断要求に対する応答は行わない。<br>
	 * このメソッドが <tt>true</tt> を返した場合、{@link #acceptTerminateRequest()} を呼び出して
	 * 中断要求への応答を開始したことを示す必要がある。
	 * 中断要求発生からおよそ 10 秒以内に応答を行わなかった場合、このプロセスは強制的に停止される。
	 * <p>このメソッド呼び出しでは、このメソッドを呼び出したスレッドの割り込みステータスはクリアされない。
	 * @return	中断要求があった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * 
	 * @since 2.0.0
	 */
	static public boolean isTerminateRequested() {
		try {
			Class<?> clazz = Class.forName("ssac.aadl.macro.process.InterruptibleJavaMainProcessImpl");
			Method mt = clazz.getMethod("isTerminateRequested");
			Boolean result = (Boolean)mt.invoke(null);
			return result.booleanValue();
		}
		catch (LinkageError ex) {
			return false;
		}
		catch (ClassNotFoundException ex) {
			return false;
		}
		catch (SecurityException ex) {
			return false;
		}
		catch (NoSuchMethodException ex) {
			return false;
		}
		catch (IllegalArgumentException ex) {
			return false;
		}
		catch (IllegalAccessException ex) {
			return false;
		}
		catch (InvocationTargetException ex) {
			return false;
		}
	}

	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATと
	 * し、整合の結果の値が 0 になる基底は、要素から削除される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @param alge	演算対象の交換代数元
	 * @return 演算結果の交換代数
	 * 
	 * @since 2.0.0
	 */
	static public Exalge strictBar(Exalge alge) {
		return alge.strictBar();
// modified @since 2.1.0
//		Map<ExBase, BigDecimal> rmap = new LinkedHashMap<ExBase, BigDecimal>();
//		ExBaseSet baseset = alge.getBases();
//		
//		for (ExBase exbase : baseset) {
//			ExBase hatbase = exbase.hat();
//			
//			// NO_HATの基底を基準とし、対応するHAT基底との演算結果を
//			// 新しいインスタンスに代入する。
//			// 対応するペアが存在しないものは、そのまま新しいインスタンスに
//			// 代入する
//			
//			if (exbase.isNoHat()) {
//				// NO_HAT基底への処理
//				//    exbase  -- NO_HAT基底
//				//    hatbase -- HAT基底
//				BigDecimal value = alge.get(exbase);
//				if (alge.containsBase(hatbase)) {
//					//-- 対応ペアあり
//					value = value.subtract(alge.get(hatbase));
//					//--- 0 以外なら代入
//					if (value.compareTo(BigDecimal.ZERO) != 0) {
//						rmap.put(exbase, value);
//					}
//				}
//				else {
//					// 対応ペアなしなら、そのまま代入
//					rmap.put(exbase, value);
//				}
//			}
//			else {
//				// HAT基底への処理
//				//    exbase  -- HAT基底
//				//    hatbase -- NO_HAT基底
//				if (!alge.containsBase(hatbase)) {
//					//-- 対応ペアなしなら、そのまま代入
//					rmap.put(exbase, alge.get(exbase));
//				}
//				// 対応ペアありの場合は、NO_HAT基底検知時に演算を行うため、
//				// ここでの処理は不要
//			}
//		}
//		
//		// 結果の生成
//		return new Exalge(rmap);
	}
	
	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATとし、整合の結果の値が 0 になる基底は、
	 * <em>leaveAsNoHat</em> が <tt>true</tt> であればハットなし基底、
	 * <em>leaveAsNoHat</em> が <tt>false</tt> であればハット基底として結果に出力される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @param alge	演算対象の交換代数元
	 * @param leaveAsNoHat	バー演算の結果、値が 0 となる要素の基底をハットなし基底とする場合は <tt>true</tt>、ハット基底とする場合は <tt>false</tt>
	 * @return 演算結果の交換代数
	 * 
	 * @since 2.0.0
	 */
	static public Exalge strictBarLeaveZero(Exalge alge, boolean leaveAsNoHat) {
		return alge.strictBarLeaveZero(leaveAsNoHat);
// modified @since 2.1.0
//		Map<ExBase, BigDecimal> rmap = new LinkedHashMap<ExBase, BigDecimal>();
//		ExBaseSet baseset = alge.getBases();
//		
//		for (ExBase exbase : baseset) {
//			ExBase hatbase = exbase.hat();
//			
//			// NO_HATの基底を基準とし、対応するHAT基底との演算結果を
//			// 新しいインスタンスに代入する。
//			// 対応するペアが存在しないものは、そのまま新しいインスタンスに
//			// 代入する
//			
//			if (exbase.isNoHat()) {
//				// NO_HAT基底への処理
//				//    exbase  -- NO_HAT基底
//				//    hatbase -- HAT基底
//				BigDecimal value = alge.get(exbase);
//				if (alge.containsBase(hatbase)) {
//					//-- 対応ペアあり
//					value = value.subtract(alge.get(hatbase));
//					if (value.compareTo(BigDecimal.ZERO) != 0) {
//						//--- 0 以外なら代入
//						rmap.put(exbase, value);
//					}
//					else {
//						//--- 0 なら、
//						//--- leaveAsNoHat=true -> NO_HAT基底を残す
//						//--- leaveAsNoHat=false -> HAT基底を残す
//						if (leaveAsNoHat)
//							rmap.put(exbase, BigDecimal.ZERO);
//						else
//							rmap.put(hatbase, BigDecimal.ZERO);
//					}
//				}
//				else {
//					// 対応ペアなしなら、そのまま代入
//					rmap.put(exbase, value);
//				}
//			}
//			else {
//				// HAT基底への処理
//				//    exbase  -- HAT基底
//				//    hatbase -- NO_HAT基底
//				if (!alge.containsBase(hatbase)) {
//					//-- 対応ペアなしなら、そのまま代入
//					rmap.put(exbase, alge.get(exbase));
//				}
//				// 対応ペアありの場合は、NO_HAT基底検知時に演算を行うため、
//				// ここでの処理は不要
//			}
//		}
//		
//		// 結果の生成
//		return new Exalge(rmap);
	}

	//------------------------------------------------------------
	// Public interfaces since 2.1.0
	//------------------------------------------------------------

	/**
	 * 交換代数元(<em>alge</em>)から、値が 0 の要素のみを含む交換代数元を生成します。
	 * 値が 0 の要素が存在しない場合は、要素が空の交換代数元を返します。
	 * @param alge	交換代数元
	 * @return	値が 0 の要素のみを含む、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#zeroProjection()
	 * 
	 * @since 2.1.0
	 */
	static public Exalge zeroProj(Exalge alge) {
		return alge.zeroProjection();
	}

	/**
	 * 交換代数集合(<em>alges</em>)から、値が 0 の要素のみを含む交換代数元の集合を生成します。
	 * 生成される交換代数集合は、指定された交換代数集合(<em>alges</em>)のすべての要素(<code>Exalge</code>)に対し
	 * {@link #zeroProj(Exalge)} を実行した結果となる交換代数元の集合であり、要素が空の交換代数元は含まれません。
	 * 値が 0 の要素がどの交換代数元にも存在しない場合は、要素が空の交換代数集合を返します。
	 * @param alges	交換代数集合
	 * @return	値が 0 の要素のみを含む交換代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#zeroProjection()
	 * 
	 * @since 2.1.0
	 */
	static public ExAlgeSet zeroProj(ExAlgeSet alges) {
		return alges.zeroProjection();
	}

	/**
	 * 交換代数元(<em>alge</em>)から、値が 0 ではない要素のみを含む交換代数元を生成します。
	 * 値が 0 ではない要素が存在しない場合は、要素が空の交換代数元を返します。
	 * @param alge	交換代数元
	 * @return	値が 0 ではない要素のみを含む、新しい交換代数元
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#notzeroProjection()
	 * 
	 * @since 2.1.0
	 */
	static public Exalge notzeroProj(Exalge alge) {
		return alge.notzeroProjection();
	}

	/**
	 * 交換代数集合(<em>alges</em>)から、値が 0 ではない要素のみを含む交換代数元の集合を生成します。
	 * 生成される交換代数集合は、指定された交換代数集合(<em>alges</em>)のすべての要素(<code>Exalge</code>)に対し
	 * {@link #notzeroProj(Exalge)} を実行した結果となる交換代数元の集合であり、要素が空の交換代数元は含まれません。
	 * 値が 0 ではない要素がどの交換代数元にも存在しない場合は、要素が空の交換代数集合を返します。
	 * @param alges	交換代数集合
	 * @return	値が 0 ではない要素のみを含む交換代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#notzeroProjection()
	 * 
	 * @since 2.1.0
	 */
	static public ExAlgeSet notzeroProj(ExAlgeSet alges) {
		return alges.notzeroProjection();
	}

	/**
	 * 指定された文字列を、交換代数集合の CSV 標準形として読み込み、交換代数元を生成します。
	 * @param csvString	読み込む文字列
	 * @return	生成された交換代数元
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.Exalge#fromCsvString(String)
	 * @since 2.1.0		
	 */
	static public Exalge newExalgeFromCsvString(String csvString) {
		try {
			return Exalge.fromCsvString(csvString);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された文字列を、交換代数集合の CSV 標準形として読み込み、交換代数集合を生成します。
	 * @param csvString	読み込む文字列
	 * @return	生成された交換代数集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExAlgeSet#fromCsvString(String)
	 * @since 2.1.0		
	 */
	static public ExAlgeSet newExAlgeSetFromCsvString(String csvString) {
		try {
			return ExAlgeSet.fromCsvString(csvString);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された文字列を、交換代数基底集合の CSV 標準形として読み込み、交換代数基底集合を生成します。
	 * @param csvString	読み込む文字列
	 * @return	生成された交換代数基底集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBaseSet#fromCsvString(String)
	 * @since 2.1.0		
	 */
	static public ExBaseSet newExBaseSetFromCsvString(String csvString) {
		try {
			return ExBaseSet.fromCsvString(csvString);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された文字列を、交換代数基底パターン集合の CSV 標準形として読み込み、交換代数基底パターン集合を生成します。
	 * @param csvString	読み込む文字列
	 * @return	生成された交換代数基底パターン集合
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExBasePatternSet#fromCsvString(String)
	 * @since 2.1.0		
	 */
	static public ExBasePatternSet newExBasePatternSetFromCsvString(String csvString) {
		try {
			return ExBasePatternSet.fromCsvString(csvString);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された文字列を、振替変換テーブルの CSV 標準形として読み込み、振替変換テーブルを生成します。
	 * @param csvString	読み込む文字列
	 * @return	生成された振替変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransTable#fromCsvString(String)
	 * @since 2.1.0		
	 */
	static public TransTable newTransTableFromCsvString(String csvString) {
		try {
			return TransTable.fromCsvString(csvString);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された文字列を、按分変換テーブルの CSV 標準形として読み込み、按分変換テーブルを生成します。
	 * @param csvString	読み込む文字列
	 * @return	生成された按分変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.TransMatrix#fromCsvString(String)
	 * @since 2.1.0		
	 */
	static public TransMatrix newTransMatrixFromCsvString(String csvString) {
		try {
			return TransMatrix.fromCsvString(csvString);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 指定された文字列を、変換テーブルの CSV 標準形として読み込み、変換テーブルを生成します。
	 * @param csvString	読み込む文字列
	 * @return	生成された変換テーブル
	 * @throws AADLRuntimeException	読み込みに失敗した場合
	 * @see exalge2.ExTransfer#fromCsvString(String)
	 * @since 2.1.0		
	 */
	static public ExTransfer newExTransferFromCsvString(String csvString) {
		try {
			return ExTransfer.fromCsvString(csvString);
		}
		catch (Exception ex) {
			throw new AADLRuntimeException(ex);
		}
	}
	
	/**
	 * 交換代数元(<em>obj</em>)の内容を、CSV 標準形の文字列として出力します。
	 * @param obj	出力対象の交換代数元
	 * @return	CSV 標準形の文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.Exalge#toCsvString()
	 * @since 2.1.0		
	 */
	static public String toCsvString(Exalge obj) {
		return obj.toCsvString();
	}
	
	/**
	 * 交換代数集合(<em>obj</em>)の内容を、CSV 標準形の文字列として出力します。
	 * @param obj	出力対象の交換代数集合
	 * @return	CSV 標準形の文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExAlgeSet#toCsvString()
	 * @since 2.1.0		
	 */
	static public String toCsvString(ExAlgeSet obj) {
		return obj.toCsvString();
	}
	
	/**
	 * 交換代数基底集合(<em>obj</em>)の内容を、CSV 標準形の文字列として出力します。
	 * @param obj	出力対象の交換代数基底集合
	 * @return	CSV 標準形の文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBaseSet#toCsvString()
	 * @since 2.1.0		
	 */
	static public String toCsvString(ExBaseSet obj) {
		return obj.toCsvString();
	}
	
	/**
	 * 交換代数基底パターン集合(<em>obj</em>)の内容を、CSV 標準形の文字列として出力します。
	 * @param obj	出力対象の交換代数基底パターン集合
	 * @return	CSV 標準形の文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExBasePatternSet#toCsvString()
	 * @since 2.1.0		
	 */
	static public String toCsvString(ExBasePatternSet obj) {
		return obj.toCsvString();
	}
	
	/**
	 * 振替変換テーブル(<em>obj</em>)の内容を、CSV 標準形の文字列として出力します。
	 * @param obj	出力対象の振替変換テーブル
	 * @return	CSV 標準形の文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransTable#toCsvString()
	 * @since 2.1.0		
	 */
	static public String toCsvString(TransTable obj) {
		return obj.toCsvString();
	}
	
	/**
	 * 按分変換テーブル(<em>obj</em>)の内容を、CSV 標準形の文字列として出力します。
	 * @param obj	出力対象の按分変換テーブル
	 * @return	CSV 標準形の文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.TransMatrix#toCsvString()
	 * @since 2.1.0		
	 */
	static public String toCsvString(TransMatrix obj) {
		return obj.toCsvString();
	}
	
	/**
	 * 変換テーブル(<em>obj</em>)の内容を、CSV 標準形の文字列として出力します。
	 * @param obj	出力対象の変換テーブル
	 * @return	CSV 標準形の文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @see exalge2.ExTransfer#toCsvString()
	 * @since 2.1.0		
	 */
	static public String toCsvString(ExTransfer obj) {
		return obj.toCsvString();
	}

	/**
	 * デフォルトの <code>QoS</code> を 1 として、CSV 形式で記述された MQTT 接続パラメータを読み込み、MQTT 接続パラメータを格納するオブジェクトを生成します。
	 * これはモジュール引数に MQTT 接続パラメータを使用したときの、サポート関数として利用できます。
	 * MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述します。トピックは記述を省略できます。<br>
	 * 接続先サーバー情報は、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できます。接続オプションは省略できますが、値のみの省略は許可されません。
	 * また、接続オプションの名前は、大文字小文字は区別されません。指定可能な接続オプションは、次の通りです。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定します。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントIDを指定します。</dd>
	 * </dl>
	 * <p>この関数は、次の場合にエラーとなります。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、トピックにはワイルドカードも指定できます。
	 * 
	 * @param csvString			CSV 形式の MQTT 接続パラメータを示す文字列
	 * @return	MQTT 接続パラメータを格納するオブジェクト
	 * 
	 * @throws NullPointerException	<em>csvString</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合
	 */
	static public MqttCsvParameter parseMqttCsvParameter(String csvString) {
		return new MqttCsvParameter(csvString);
	}
	
	/**
	 * デフォルトの <code>QoS</code> を 1 として、CSV 形式で記述された MQTT 接続パラメータを読み込み、MQTT 接続パラメータを格納するオブジェクトを生成します。
	 * これはモジュール引数に MQTT 接続パラメータを使用したときの、サポート関数として利用できます。
	 * MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述します。トピックは記述を省略できます。<br>
	 * 接続先サーバー情報は、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できます。接続オプションは省略できますが、値のみの省略は許可されません。
	 * また、接続オプションの名前は、大文字小文字は区別されません。指定可能な接続オプションは、次の通りです。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定します。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントIDを指定します。</dd>
	 * </dl>
	 * <p>この関数は、次の場合にエラーとなります。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、<em>allowTopicWildcard</em> に <tt>false</tt> を指定した場合、トピックにワイルドカードが含まれている場合にもエラーとなります。
	 * 
	 * @param csvString			CSV 形式の MQTT 接続パラメータを示す文字列
	 * @param allowTopicWildcard	トピックにワイルドカードの指定を許可する場合は <tt>true</tt>
	 * @return	MQTT 接続パラメータを格納するオブジェクト
	 * 
	 * @throws NullPointerException	<em>csvString</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合
	 * 
	 * @since 2.1.0
	 */
	static public MqttCsvParameter parseMqttCsvParameter(String csvString, boolean allowTopicWildcard) {
		return new MqttCsvParameter(csvString, allowTopicWildcard);
	}

	/**
	 * CSV 形式で記述された MQTT 接続パラメータを読み込み、MQTT 接続パラメータを格納するオブジェクトを生成します。
	 * これはモジュール引数に MQTT 接続パラメータを使用したときの、サポート関数として利用できます。
	 * MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述します。トピックは記述を省略できます。<br>
	 * 接続先サーバー情報は、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できます。接続オプションは省略できますが、値のみの省略は許可されません。
	 * また、接続オプションの名前は、大文字小文字は区別されません。指定可能な接続オプションは、次の通りです。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定します。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントIDを指定します。</dd>
	 * </dl>
	 * <p>この関数は、次の場合にエラーとなります。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、トピックにはワイルドカードも指定できます。
	 * 
	 * @param csvString			CSV 形式の MQTT 接続パラメータを示す文字列
	 * @param defaultQOS			接続オプションの <code>QoS</code> が省略された場合の <code>QoS</code> のデフォルト値
	 * @return	MQTT 接続パラメータを格納するオブジェクト
	 * 
	 * @throws NullPointerException	<em>csvString</em> もしくは <em>defaultQOS</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合、もしくは <em>defaultQOS</em> が 0、1、2 以外の場合
	 * 
	 * @since 2.1.0
	 */
	static public MqttCsvParameter parseMqttCsvParameter(String csvString, BigDecimal defaultQOS) {
		int defqos;
		try {
			defqos = defaultQOS.intValueExact();
		}
		catch (ArithmeticException ex) {
			throw new IllegalArgumentException("Default QoS is not 0, 1 or 2 : " + defaultQOS.stripTrailingZeros().toPlainString(), ex);
		}
		return new MqttCsvParameter(csvString, defqos);
	}

	/**
	 * CSV 形式で記述された MQTT 接続パラメータを読み込み、MQTT 接続パラメータを格納するオブジェクトを生成します。
	 * これはモジュール引数に MQTT 接続パラメータを使用したときの、サポート関数として利用できます。
	 * MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述します。トピックは記述を省略できます。<br>
	 * 接続先サーバー情報は、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できます。接続オプションは省略できますが、値のみの省略は許可されません。
	 * また、接続オプションの名前は、大文字小文字は区別されません。指定可能な接続オプションは、次の通りです。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定します。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントIDを指定します。</dd>
	 * </dl>
	 * <p>この関数は、次の場合にエラーとなります。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、<em>allowTopicWildcard</em> に <tt>false</tt> を指定した場合、トピックにワイルドカードが含まれている場合にもエラーとなります。
	 * 
	 * @param csvString			CSV 形式の MQTT 接続パラメータを示す文字列
	 * @param allowTopicWildcard	トピックにワイルドカードの指定を許可する場合は <tt>true</tt>
	 * @param defaultQOS			接続オプションの <code>QoS</code> が省略された場合の <code>QoS</code> のデフォルト値
	 * @return	MQTT 接続パラメータを格納するオブジェクト
	 * 
	 * @throws NullPointerException	<em>csvString</em> もしくは <em>defaultQOS</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合、もしくは <em>defaultQOS</em> が 0、1、2 以外の場合
	 * 
	 * @since 2.1.0
	 */
	static public MqttCsvParameter parseMqttCsvParameter(String csvString, boolean allowTopicWildcard, BigDecimal defaultQOS) {
		int defqos;
		try {
			defqos = defaultQOS.intValueExact();
		}
		catch (ArithmeticException ex) {
			throw new IllegalArgumentException("Default QoS is not 0, 1 or 2 : " + defaultQOS.stripTrailingZeros().toPlainString(), ex);
		}
		return new MqttCsvParameter(csvString, allowTopicWildcard, defqos);
	}
	
	/**
	 * 指定された MQTT 接続パラメータから、サーバー URL を取得します。
	 * @return	サーバーURL を示す文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.1.0
	 */
	static public String getMqttServerURI(MqttCsvParameter param) {
		return param.getServerURI();
	}
	
	/**
	 * 指定された MQTT 接続パラメータから、クライアントIDを取得します。
	 * クライアントIDが指定されていな場合は <tt>null</tt> を返します。
	 * @return	クライアントIDが指定されていればその文字列、なければ <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.1.0
	 */
	static public String getMqttClientID(MqttCsvParameter param) {
		return param.getClientID();
	}

	/**
	 * 指定された MQTT 接続パラメータから、利用可能なクライアントIDを取得します。
	 * クライアントIDが指定されていない場合、ランダムに生成されたクライアントIDを返します。
	 * @return	クライアントID を示す文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.1.0
	 */
	static public String getAvailableMqttClientID(MqttCsvParameter param) {
		return param.getAvailableClientID();
	}

	/**
	 * 指定された MQTT 接続パラメータから、<code>QoS</code> を取得します。
	 * @return	<code>QoS</code>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.1.0
	 */
	static public BigDecimal getMqttQOS(MqttCsvParameter param) {
		return toDecimal(param.getQOS());
	}
	
	/**
	 * 指定された MQTT 接続パラメータから、トピックのリストを取得します。
	 * トピックが指定されていない場合は、要素が空のリストを返します。
	 * なお、このメソッドが返すリストは、変更できません。
	 * @return	変更不可能なトピックのリスト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.1.0
	 */
	static public List<String> getMqttTopicList(MqttCsvParameter param) {
		return param.getTopics();
	}

	/**
	 * このオブジェクトのパラメータを、CSV 形式の文字列として出力します。
	 * <code>QoS</code> の値も出力に含まれます。
	 * @return	CSVレコードフォーマットの文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.1.0
	 */
	static public String toCsvString(MqttCsvParameter param) {
		return param.toCsvString();
	}

	//------------------------------------------------------------
	// Public interfaces since 2.2.0
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトのコレクションを、典型的な CSV 形式（カンマ区切り）の単一レコード文字列に変換する。
	 * コレクションの要素が <tt>null</tt> の場合も空文字のフィールドとする。
	 * なお、<em>withRecordDelimiter</em> に <tt>false</tt> を指定したとき、
	 * コレクションの要素が一つのみでその要素が <tt>null</tt> もしくは空文字列となるものの場合には、
	 * 単一の空のフィールドが存在することを示すため、2 つのダブルクオートのみが含まれた文字列を返す。
	 * @param c	変換対象のオブジェクトのコレクション
	 * @param withRecordDelimiter	終端にレコード区切り文字を追加する場合は <tt>true</tt>、追加しない場合は <tt>false</tt>
	 * @return	レコード区切り文字を含まない CSV 形式の単一レコードを表す文字列、コレクションが空の場合は空文字列
	 * @throws NullPointerException	<em>c</em> が <tt>null</tt> の場合
	 * @since 2.2.0
	 */
	static public String toCsvString(Collection<?> c, boolean withRecordDelimiter) {
		if (c.isEmpty()) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = c.iterator();
		
		// first entry
		Object obj = it.next();
		_appendTypicalCsvFieldStringWithEnquoteAsNeeded(sb, obj);
		// other entries
		for (; it.hasNext(); ) {
			sb.append(',');
			obj = it.next();
			_appendTypicalCsvFieldStringWithEnquoteAsNeeded(sb, obj);
		}
		
		// append record delimiter as needed
		if (withRecordDelimiter) {
			sb.append("\r\n");
		}
		
		// check empty
		//--- コレクションの要素が存在しながら文字列バッファが空の場合、
		//--- フィールドは存在するものとして、"" のみを文字列バッファに格納する。
		if (sb.length() == 0) {
			sb.append("\"\"");
		}
		
		// completed
		return sb.toString();
	}

	/**
	 * 指定されたオブジェクトを文字列に変換し、典型的な CSV フィールドの文字列として、文字列バッファに追加する。
	 * オブジェクトから変換した文字列に、カンマ、改行文字、ダブルクオートが含まれている場合、ダブルクオートで囲まれた
	 * CSV フィールド文字列に変換する。
	 * なお、オブジェクトが <tt>null</tt> の場合も、空のフィールドとして追加する。
	 * @param buffer	CSV フィールド文字列を格納するバッファ
	 * @param obj		変換対象のオブジェクト
	 * @since 2.2.0
	 */
	static protected void _appendTypicalCsvFieldStringWithEnquoteAsNeeded(StringBuilder buffer, Object obj) {
		if (obj == null)
			return;
		
		String str = toString(obj);
		int len = str.length();
		
		// check to enquote as needed
		boolean asNeed = false;
		for (int i = 0; i < len; ++i) {
			char ch = str.charAt(i);
			if (ch == ',' || ch == '\"' || ch == '\r' || ch == '\n') {
				asNeed = true;
				break;
			}
		}
		if (!asNeed) {
			buffer.append(str);
			return;
		}
		
		// enquote
		buffer.append('\"');
		for (int i = 0; i < len; ++i) {
			char ch = str.charAt(i);
			if (ch == '\"') {
				buffer.append("\"\"");
			} else {
				buffer.append(ch);
			}
		}
		buffer.append('\"');
	}

	/**
	 * 典型的な CSV 文字列（カンマ区切り、改行文字によるレコード区切り）から、フィールド値のリストを生成する。
	 * 複数のレコードが含まれる場合も、連続したフィールドのリストとする。
	 * なお、リストに含まれるフィールド値は、ダブルクオートで囲まれたもの（フィールドの先頭がダブルクオートのもの）に
	 * ついては、ダブルクオートを除去したフィールド値としてリストに格納する。
	 * @param csvString	CSV 形式の文字列
	 * @return	フィールド値のリスト、<em>csvString</em> が <tt>null</tt> もしくは空文字の場合は要素が空のリスト
	 * @since 2.2.0
	 */
	static public ArrayList<String> newStringListFromCsvString(String csvString) {
		if (csvString == null || csvString.isEmpty()) {
			return new ArrayList<String>(0);
		}
		
		// decode
		ArrayList<String> list = new ArrayList<String>();
		int len = csvString.length();
		StringBuilder buf = new StringBuilder();
		StringBuilder sbField = buf;
		for (int i = 0; i < len; ) {
			char ch = csvString.charAt(i);
			// check first char
			if (ch == '\"') {
				// quote started
				if (sbField == null)
					sbField = buf;
				i = _extractCharUntilCsvQuote(sbField, csvString, i+1, len);
			}
			else if (ch == ',') {
				// フィールド区切り
				if (sbField == null) {
					// 前のフィールドは空、次のフィールドは有り
					list.add("");
					sbField = buf;
				}
				else {
					// 前のフィールドは文字列、次のフィールドは有り
					list.add(sbField.toString());
					sbField.setLength(0);
				}
				++i;	// next char
			}
			else if (ch == '\r' || ch == '\n') {
				// レコード区切り
				if (sbField == null) {
					// 前のフィールドは空、次のフィールドは無し
					list.add("");
					sbField = null;
				}
				else {
					// 前のフィールドは文字列、次にフィールドは無し
					list.add(sbField.toString());
					sbField.setLength(0);
					sbField = null;
				}
				++i;	// next char
				//--- CRLFチェック
				if (ch == '\r') {
					if (i < len) {
						ch = csvString.charAt(i);
						if (ch == '\n') {
							//--- skip LF
							++i;
						}
					}
				}
			}
			else {
				// extract characters until delimiters
				if (sbField == null)
					sbField = buf;
				i = _extractCharUntilCsvDelimiter(sbField, csvString, i, len);
			}
		}
		
		// 直前のフィールドが存在する場合は、リストに追加
		if (sbField != null) {
			list.add(sbField.toString());
		}
		
		// 完了
		return list;
	}

	/**
	 * 指定された文字列から、<em>offset</em> からフィールド区切りもしくはレコード区切りまでを、 CSV フィールド値として取得する。
	 * @param buffer	抽出した文字列を格納するバッファ
	 * @param csvString	対象の文字列
	 * @param offset	値の取得開始位置を示すインデックス
	 * @param strlen	文字列の長さ
	 * @return	フィールド区切りもしくはレコード区切りの位置を示すインデックス
	 * @since 2.2.0
	 */
	static protected int _extractCharUntilCsvDelimiter(StringBuilder buffer, String csvString, int offset, int strlen) {
		for (int i = offset; i < strlen; ++i) {
			char ch = csvString.charAt(i);
			if (ch == ',' || ch == '\r' || ch == '\n') {
				// フィールド区切り、もしくはレコード区切り
				return i;
			}
			else {
				// CSV フィールドの文字
				buffer.append(ch);
			}
		}
		//--- 文字列終端に到達
		return strlen;
	}

	/**
	 * 指定された文字列から、<em>offset</em> から終端のダブルクオートまでを、ダブルクオートで囲まれた CSV フィールド値として取得する。
	 * なお、<em>offset</em> は、先頭のダブルクオートの次の文字を指すインデックスとする。
	 * @param buffer	抽出した文字列を格納するバッファ
	 * @param csvString	対象の文字列
	 * @param offset	値の取得開始位置を示すインデックス
	 * @param strlen	文字列の長さ
	 * @return	終端ダブルクオートの次の位置を示すインデックス
	 * @since 2.2.0
	 */
	static protected int _extractCharUntilCsvQuote(StringBuilder buffer, String csvString, int offset, int strlen) {
		for (int i = offset; i < strlen; ++i) {
			char ch = csvString.charAt(i);
			if (ch == '\"') {
				// 次の文字をチェック
				int ni = i + 1;
				if (ni < strlen) {
					ch = csvString.charAt(ni);
					if (ch == '\"') {
						// ダブルクオートのエスケープ
						i = ni;
						buffer.append(ch);
						continue;
					}
				}
				//--- 終端ダブルクオート
				return ni;
			}
			else {
				// CSV フィールドの文字
				buffer.append(ch);
			}
		}
		//--- 文字列終端に到達
		return strlen;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final boolean deleteFileRecursive(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] filelist = file.listFiles();
				if (filelist != null && filelist.length > 0) {
					for (File subfile : filelist) {
						if (!deleteFileRecursive(subfile)) {
							// cannot delete
							return false;
						}
					}
				}
				return file.delete();
			} else {
				return file.delete();
			}
		} else {
			return true;
		}
	}
	
	static protected final void _setDefaultEncodingProperty(String key, String charsetName) {
		if (charsetName != null && charsetName.length() > 0) {
			boolean canEncoding = false;
			try {
				Charset cs = Charset.forName(charsetName);
				canEncoding = cs.canEncode();
			} catch (Exception ex) {
				throw new AADLRuntimeException(String.format("\"%s\" charset is not supported.", charsetName), ex);
			}
			if (!canEncoding) {
				throw new AADLRuntimeException(String.format("\"%s\" charset is not supported.", charsetName));
			}
			System.setProperty(key, charsetName);
		} else {
			System.clearProperty(key);
		}
	}
	
	/**
	 * 比較可能(Comparable)な要素を持つリストの内容を昇順にソートする。
	 * このメソッドは、リストの内容を変更する破壊型メソッドである。
	 * @param list	ソートするリスト
	 * 
	 * @throws ClassCastException	リストに「相互に比較可能」でない要素がある場合
	 * @throws UnsupportedOperationException	指定されたリストのリスト反復子が <code>set</code> オペレーションをサポートしない場合
	 * 
	 * @since 1.40
	 */
	static protected <T extends Comparable<? super T>> void _sort(List<T> list) {
		Collections.sort(list);
	}

	/**
	 * 編集可能なリストオブジェクトを生成する。
	 * @param <T>	データ型
	 * @param a		データの可変長引数
	 * @return	ArrayList オブジェクト
	 * 
	 * @since 1.30
	 */
	static protected <T> ArrayList<T> createArrayList(T...a) {
		return new ArrayList<T>(Arrays.asList(a));
	}

	/**
	 * マップオブジェクトを生成する。
	 * ※未使用
	 * 
	 * @param <K> マップのキーの型
	 * @param <V> マップの値の型
	 * @param keys キーの配列
	 * @param values 値の配列
	 * @return マップ
	 * 
	 * @since 1.00
	 */
	static protected <K,V> Map<K,V> createLinkedHashMap(K[] keys, V[] values) {
		// Check
		if (keys.length != values.length) {
			throw new IllegalArgumentException("keys.length not equal values.length");
		}
		// create Map
		Map<K,V> newMap = new LinkedHashMap<K,V>();
		for (int i = 0; i < keys.length; i++) {
			newMap.put(keys[i], values[i]);
		}
		// result
		return newMap;
	}

	// 互換性のためのみに残している
	static protected final String _getTextEncoding() {
		return getDefaultTextEncoding();
	}
	
	// 互換性のためのみに残している
	static protected final String _getCsvEncoding() {
		return getDefaultCsvEncoding();
	}
	
	static protected final String[] _splitCommandLineWithDoubleQuoteEscape(String str) {
		final String delimiters = " \t\n\r\f";
		final int maxPosition = str.length();
		final StringBuilder sb = new StringBuilder(maxPosition);
		final ArrayList<String> args = new ArrayList<String>();

		int curPosition = 0;
		while (curPosition < maxPosition) {
			char c = str.charAt(curPosition);
			if (c == '\"') {
				curPosition++;
				while (curPosition < maxPosition) {
					c = str.charAt(curPosition);
					if (c == '\"') {
						int nextPosition = curPosition + 1;
						if (nextPosition < maxPosition) {
							if (str.charAt(nextPosition) == '\"') {
								sb.append(c);
								curPosition = nextPosition;
							} else {
								break;
							}
						} else {
							break;
						}
					} else {
						sb.append(c);
					}
					curPosition++;
				}
			}
			else if (delimiters.indexOf(c) >= 0) {
				args.add(sb.toString());
				sb.setLength(0);
				//--- skip delimiters
				int newPosition = curPosition+1;
				for (; (newPosition < maxPosition) && (delimiters.indexOf(str.charAt(newPosition)) >= 0); newPosition++);
				curPosition = newPosition - 1;
			}
			else {
				sb.append(c);
			}
			curPosition++;
		}
		if (sb.length() > 0) {
			args.add(sb.toString());
		}
		
		return args.toArray(new String[args.size()]);
	}
	
	static protected final boolean _containsCharsInString(final String target, char...cs) {
		boolean exist = false;
		int len;
		if (target != null && (len = target.length()) > 0) {
			for (int i = 0; i < len; i++) {
				char tc = target.charAt(i);
				for (char cc : cs) {
					if (cc == tc) return true;
				}
			}
		}
		return exist;
	}
	
	static protected final boolean _enquoteCommandList(List<String> command) {
		final char[] enquoteChars = {'*', '?', '\"'};
		boolean isWindows = (System.getProperty("os.name").indexOf("Windows") >= 0);
		if (!isWindows) return false; // Don't enquote not Windows platforms.
		boolean enquoted = false;
		for (int idx = 0; idx < command.size(); idx++) {
			String cmd = command.get(idx);
			if (cmd!=null && cmd.length()>0 && _containsCharsInString(cmd, enquoteChars)) {
				cmd = "\"" + cmd.replaceAll("\"", "\\\\\"") + "\"";
				command.set(idx, cmd);
				enquoted = true;
			}
		}
		return enquoted;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected final class ProcessStreamHandler extends Thread {
		static private final int DEFAULT_BUFFER_SIZE = 8192;
		private final java.io.InputStream inStream;
		private final java.io.PrintStream outStream;
		public ProcessStreamHandler(java.io.InputStream ins, java.io.PrintStream outs) {
			super();
			inStream = ins;
			outStream = outs;
		}
		public void run() {
			int read;
			final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			if (outStream != null) {
				try {
					while ((read = inStream.read(buffer)) >= 0) {
						outStream.write(buffer, 0, read);
					}
				} catch (java.io.IOException ex) { ex=null; }
			} else {
				try {
					while ((read = inStream.read(buffer)) >= 0) ;
				} catch (java.io.IOException ex) { ex=null; }
			}
		}
	}
	
	static protected final class ProcessOutputHandler extends Thread {
		static private final int DEFAULT_BUFFER_SIZE = 8192;
		private volatile boolean isRunning = true;
		private final java.io.InputStream stdout;
		private final java.io.InputStream stderr;
		private final byte[] byteBuffer = new byte[DEFAULT_BUFFER_SIZE];
		// Constructor
		public ProcessOutputHandler(Process proc) {
			super();
			stdout = proc.getInputStream();
			stderr = proc.getErrorStream();
		}
		// Buffering
		private boolean storeBytes(final java.io.InputStream target, final java.io.PrintStream printer) {
			boolean flgRead = false;
			try {
				int size = target.available();
				if (size > 0) {
					int len = target.read(byteBuffer);
					printer.write(byteBuffer, 0, len);
					flgRead = true;
				}
			} catch (java.io.IOException ex) {
				ex = null;
				flgRead = false;
			}
			return flgRead;
		}
		private boolean storeBytes() {
			return (storeBytes(stdout, System.out) || storeBytes(stderr, System.err));
		}
		// stop
		public void terminate() {
			isRunning = false;
		}
		// run
		public void run() {
			final long sleepTime = 100L;
			while (isRunning) {
				if (!storeBytes()) {
					try { sleep(sleepTime); }
					catch (InterruptedException ex) { ex=null; }
				}
			}
			while (storeBytes());
		}
	}
	
	static protected final class XsltTransformer implements javax.xml.transform.ErrorListener
	{
		private final java.io.File _fileXML;
		private final java.io.File _fileXSL;
		private final java.io.File _fileOUT;
		private final boolean     _verbose;
		
		// Constructor
		public XsltTransformer(String pathXML, String pathXSL, String pathOUT, boolean verbose) {
			this._fileXML = new java.io.File(pathXML);
			this._fileXSL = new java.io.File(pathXSL);
			this._fileOUT = new java.io.File(pathOUT);
			this._verbose = verbose;
		}
		
		// Transform
		public void transform() {
			if (_verbose) {
				System.out.println("<<< XML transformation >>>");
				System.out.print("[Source] \"");
				System.out.print(_fileXML.getName());
				System.out.println("\"");
				System.out.print("[Stylesheet] \"");
				System.out.print(_fileXSL.getName());
				System.out.println("\"");
				System.out.print("[Destination] \"");
				System.out.print(_fileOUT.getName());
				System.out.println("\"");
			}
			try {
				// XSLTのストリーム・ソース
				javax.xml.transform.stream.StreamSource xsltSrc = new javax.xml.transform.stream.StreamSource(_fileXSL);
				// ソース・ツリーのストリーム・ソース
				javax.xml.transform.stream.StreamSource source  = new javax.xml.transform.stream.StreamSource(_fileXML);
				// 結果ツリーのストリーム・リザルト
				javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(_fileOUT);

				// XSLTプロセッサのファクトリの生成
				javax.xml.transform.TransformerFactory tFactory = javax.xml.transform.TransformerFactory.newInstance();
				if (!_verbose) {
					//--- エラー情報の詳細を標準エラー出力に出力しない場合、オリジナルの ErrorListener を使用する。
					//--- エラー詳細情報を出力する場合は、Transformer インスタンスの ErrorListener を使用する。
					tFactory.setErrorListener(XsltTransformer.this);
				}
				// XSLTプロセッサの生成
				javax.xml.transform.Transformer transformer = tFactory.newTransformer(xsltSrc);
				// 変換と結果ツリーの出力
				transformer.transform(source, result);
				
				// 結果表示
				if (_verbose) {
					System.out.println("...completed!");
				}
			} catch (Exception e) {
				if (_verbose) {
					System.out.println("...failed!");
				}
				throw new AADLRuntimeException(e);
			}
		}
		
		// XML Transform error
		public void error(javax.xml.transform.TransformerException e) throws javax.xml.transform.TransformerException
		{
			//--- through an Error
			throw e;
		}

		// XML Transform fatal error
		public void fatalError(javax.xml.transform.TransformerException e) throws javax.xml.transform.TransformerException
		{
			//--- through an Error
			throw e;
		}

		// XML Transform warning
		public void warning(javax.xml.transform.TransformerException e) throws javax.xml.transform.TransformerException
		{
			// Ignore warning
		}
	}
}
