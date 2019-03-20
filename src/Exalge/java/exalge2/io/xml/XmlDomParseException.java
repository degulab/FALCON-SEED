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
 *  Copyright 2007-2008  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)XmlDomParseException.java	0.95	2008/10/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)XmlDomParseException.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.xml;

import org.w3c.dom.Element;

import exalge2.io.ExalgeIOException;

/**
 * 交換代数コア・パッケージの XML 入力で解析エラーが発生した
 * 場合にスローされる。
 * <p>
 * この例外は、エラー発生の要因、ならびに、エラー発生ノード情報(XMLドキュメント、タグ、属性名、値)を保持する。
 * 
 * @version 0.95	2008/10/29
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 *
 */
public class XmlDomParseException extends ExalgeIOException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * エラー要因が特定されないエラーであることを示す。
	 */
	public static final int NO_FACTOR = -1;

	/**
	 * XML のタグ名が許可されていないものであることを示す。
	 * <br>この要因では、エラーとなったタグが保持される。
	 */
	public static final int ILLEGAL_ELEMENT_NAME = 1;
	/**
	 * 必要な XML のタグが存在しないことを示す。
	 * <br>この要因では、必要なタグ名が値情報に保持される。
	 */
	public static final int ELEMENT_NOT_FOUND = 2;
	/**
	 * XML の値解析において、数値であるはずのものが数値に変換できないことを示す。
	 * <br>この要因では、エラーとなったタグ、属性名、値が保持される。
	 */
	public static final int NUMBER_FORMAT_EXCEPTION = 3;
	/**
	 * 基底キー名として不正な文字が使用されている、もしくは基底キー名として正しくないことを示す。
	 * <br>この要因では、エラーとなったタグ、属性名、値が保持される。
	 */
	public static final int ILLEGAL_BASEKEY_PATTERN = 4;
	/**
	 * 変換テーブルにおいて、"from" を示す行が存在しないことを示す。
	 * <br>この要因では、エラーとなったタグが保持される。
	 */
	public static final int DIR_FROM_NOT_EXIST = 5;
	/**
	 * 変換テーブルにおいて、"to" を示す行が存在しないことを示す。
	 * <br>この要因では、エラーとなったタグが保持される。
	 */
	public static final int DIR_TO_NOT_EXIST = 6;
	/**
	 * 変換テーブルにおいて、同一の変換元基底パターンがすでに存在していることを示す。
	 * <br>この要因では、エラーとなったタグ、重複している基底パターンが値として保持される。
	 */
	public static final int DIR_FROM_ALREADY_EXIST = 7;
	/**
	 * 按分変換テーブルの変換先按分比率テーブルにおいて、同一の変換先基底パターンが
	 * すでに存在していることを示す。
	 * <br>この要因では、エラーとなったタグ、重複している基底パターンが値として保持される。
	 */
	public static final int DIR_TO_ALREADY_EXIST = 8;
	/**
	 * 変換先基底パターンにおいて、基底キーにワイルドカードとワイルドカード以外の文字が
	 * 組み合わされていることを示す。
	 * <br>この要因では、エラーとなったタグ、属性名、値が保持される。
	 */
	public static final int ILLEGAL_DIR_TO_WILDCARD_PATTERN = 9;
	/**
	 * 変換テーブルにおいて、按分比率が 0 未満であることを示す。
	 * <br>この要因では、エラーとなったタグ、属性名、値が保持される。
	 */
	public static final int ILLEGAL_DIVIDE_RATIO = 10;
	/**
	 * 変換元基底パターンを示すタグが複数出現したことを示す。
	 * このエラーは、単一の基底パターンのみ許可されている場合に発生する。
	 * <br>
	 * この要因では、エラーとなったタグが保持される。
	 */
	public static final int DIR_FROM_EXIST_MULTIPLE = 11;
	/**
	 * 変換先基底パターンを示すタグが複数出現したことを示す。
	 * このエラーは、単一の基底パターンのみ許可されている場合に発生する。
	 * <br>この要因では、エラーとなったタグが保持される。
	 */
	public static final int DIR_TO_EXIST_MULTIPLE = 12;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * エラー要因
	 */
	private int factor;
	/**
	 * エラー発生の XML ドキュメント
	 */
	private XmlDocument failDom;
	/**
	 * エラー発生のエレメント(タグ)
	 */
	private Element failElem;
	/**
	 * エラー発生の属性名
	 */
	private String failAttr;
	/**
	 * エラー発生の値
	 */
	private String failValue;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細情報を持たない新規例外を構築する
	 */
	public XmlDomParseException() {
		super();
		initialize(NO_FACTOR, null, null, null, null);
	}

	/**
	 * 詳細メッセージのみを持つ新規例外を構築する
	 * 
	 * @param message 詳細メッセージ
	 */
	public XmlDomParseException(String message) {
		super(message);
		initialize(NO_FACTOR, null, null, null, null);
	}

	/**
	 * エラーの詳細情報を持つ新規例外を構築する
	 * 
	 * @param factor エラー要因
	 * @param failDom エラーが発生した {@link XmlDocument} インスタンス
	 * @param failElem エラーが発生した {@link org.w3c.dom.Element} インスタンス
	 * @param failAttr エラーが発生した属性の属性名
	 * @param failValue エラー発生の原因となった値
	 */
	public XmlDomParseException(int factor, XmlDocument failDom, Element failElem,
								 String failAttr, String failValue)
	{
		super();
		initialize(factor, failDom, failElem, failAttr, failValue);
	}

	/**
	 * 詳細メッセージとエラー詳細情報を持つ新規例外を構築する
	 * 
	 * @param message 詳細メッセージ
	 * @param factor エラー要因
	 * @param failDom エラーが発生した {@link XmlDocument} インスタンス
	 * @param failElem エラーが発生した {@link org.w3c.dom.Element} インスタンス
	 * @param failAttr エラーが発生した属性の属性名
	 * @param failValue エラー発生の原因となった値
	 */
	public XmlDomParseException(String message, int factor, XmlDocument failDom,
								 Element failElem, String failAttr, String failValue)
	{
		super(message);
		initialize(factor, failDom, failElem, failAttr, failValue);
	}
	
	private void initialize(int factor, XmlDocument failDom, Element failElem,
							  String failAttr, String failValue)
	{
		this.factor = factor;
		this.failDom = failDom;
		this.failElem = failElem;
		this.failAttr = failAttr;
		this.failValue = failValue;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 設定されているエラー要因を返す。
	 * 
	 * @return エラー要因
	 */
	public int getIllegalFactor() {
		return this.factor;
	}

	/**
	 * エラーが発生した XML ドキュメントが保持されていれば <tt>true</tt> を返す。
	 * 
	 * @return XML ドキュメントが保持されていれば <tt>true</tt>
	 */
	public boolean hasFailedDocument() {
		return (this.failDom != null);
	}

	/**
	 * エラー発生の原因となった XML タグが保持されていれば <tt>true</tt> を返す。
	 * 
	 * @return XML タグが保持されていれば <tt>true
	 */
	public boolean hasFailedElement() {
		return (this.failElem != null);
	}

	/**
	 * エラー発生の原因となった XML タグの属性名が保持されていれば <tt>true</tt> を返す。
	 * @return XML タグの属性名が保持されていれば <tt>true</tt>
	 */
	public boolean hasFailedAttributeName() {
		return (this.failAttr != null);
	}

	/**
	 * エラー発生の原因となった値が保持されていれば <tt>true</tt> を返す。
	 * 
	 * @return 値が保持されていれば <tt>true</tt>
	 */
	public boolean hasFailedValue() {
		return (this.failValue != null);
	}

	/**
	 * エラーが発生した XML ドキュメント({@link XmlDocument})を返す。
	 * <br>保持されていない場合は <tt>null</tt> を返す。
	 * 
	 * @return エラー発生ドキュメント
	 */
	public XmlDocument getFailedDocument() {
		return this.failDom;
	}

	/**
	 * エラー発生の原因となった XML タグ({@link org.w3c.dom.Element})を返す。
	 * <br>保持されていない場合は <tt>null</tt> を返す。
	 * 
	 * @return エラー発生原因の {@link org.w3c.dom.Element} インスタンス
	 */
	public Element getFailedElement() {
		return this.failElem;
	}

	/**
	 * エラー発生の原因となった XML タグ内の属性名を返す。
	 * <br>保持されていない場合は <tt>null</tt> を返す。
	 * 
	 * @return エラー発生原因の属性名
	 */
	public String getFailedAttributeName() {
		return this.failAttr;
	}

	/**
	 * エラー発生の原因となった値を返す。
	 * <br>保持されていない場合は <tt>null</tt> を返す。
	 * 
	 * @return エラー発生原因となった値
	 */
	public String getFailedValue() {
		return this.failValue;
	}
}
