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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)XmlErrors.java	0.982	2009/09/13
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.io.xml;

import java.util.Stack;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * XMLエラーに関する機能を提供するクラス。
 * 
 * @version 0.982	2009/09/13
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.982
 */
public final class XmlErrors
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
	
	private XmlErrors() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 文字列スタックの先頭にある文字列を取得する。
	 * スタックに文字列が存在しない場合は、<tt>null</tt> を返す。
	 * 
	 * @param stack 先頭の要素を取得する <code>Stack</code> オブジェクト
	 * @return 要素が存在する場合は先頭の要素、それ以外の場合は <tt>null</tt> を返す。
	 */
	static public String peekStringStack(Stack<String> stack) {
		return (stack.empty() ? null : stack.peek());
	}

	/**
	 * <code>SAXParseException</code> から、エラー発生の位置を含めた
	 * エラーメッセージを取得する。
	 * 
	 * @param ex	メッセージ取得対象の <code>SAXParseException</code>
	 * @return	取得したメッセージ文字列
	 */
	static public String getMessageAndLocation(SAXParseException ex) {
		StringBuilder sbuffer = new StringBuilder();
		String message = ex.getMessage();

		if (null != message) {
			sbuffer.append(message);
		}

		String systemID = ex.getSystemId();
		int    line     = ex.getLineNumber();
		int    column   = ex.getColumnNumber();

		if (null != systemID) {
			sbuffer.append("; SystemID: ");
			sbuffer.append(systemID);
		}

		if (0 != line) {
			sbuffer.append("; Line#: ");
			sbuffer.append(line);
		}

		if (0 != column) {
			sbuffer.append("; Column#: ");
			sbuffer.append(column);
		}

		return sbuffer.toString();
	}

	/**
	 * <code>SAXParseException</code> から、エラー発生の位置情報文字列を
	 * 取得する。
	 * 
	 * @param ex	メッセージ取得対象の <code>SAXParseException</code>
	 * @return	取得したメッセージ文字列
	 */
	public String getLocationAsString(SAXParseException ex) {
		StringBuilder sbuffer  = new StringBuilder();
		String       systemID = ex.getSystemId();
		int          line     = ex.getLineNumber();
		int          column   = ex.getColumnNumber();

		if (null != systemID) {
			sbuffer.append("; SystemID: ");
			sbuffer.append(systemID);
		}

		if (0 != line) {
			sbuffer.append("; Line#: ");
			sbuffer.append(line);
		}

		if (0 != column) {
			sbuffer.append("; Column#: ");
			sbuffer.append(column);
		}

		return sbuffer.toString();
	}

	/**
	 * 不明なエレメントを要因とするメッセージの <code>SAXParseException</code> 例外をスローする。
	 * 
	 * @param locator	エラー発生位置を示す <code>Locator</code>
	 * @param element	不明なエレメント名
	 * @throws SAXParseException	常にスローされる。
	 */
	static public void errorUnknownElement(Locator locator, String element)
		throws SAXParseException
	{
		throw new SAXParseException(String.format("Unknown <%s> element.", element), locator);
	}

	/**
	 * 指定されたメッセージと位置情報を持つ <code>SAXParseException</code> 例外をスローする。
	 * 
	 * @param locator	エラー発生位置を示す <code>Locator</code>
	 * @param format	書式文字列({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * @param args		書式文字列の書式指定子により参照される引数({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * @throws SAXParseException	常にスローされる。
	 * 
	 * @see java.lang.String#format(String, Object[])
	 */
	static public void errorParseException(Locator locator, String format, Object...args)
		throws SAXParseException
	{
		throw new SAXParseException(String.format(format, args), locator);
	}

	/**
	 * 指定された文字列が空文字でないかを検証する。
	 * 空文字もしくは <tt>null</tt> の場合は、例外をスローする。
	 * 
	 * @param locator	エラー発生位置を示す <code>Locator</code>
	 * @param value		検証する文字列
	 * @throws SAXParseException	空文字もしくは <tt>null</tt> の場合
	 */
	static public void validStringValue(Locator locator, String value)
		throws SAXParseException
	{
		if (value == null || value.length() <= 0) {
			// failed
			throw new SAXParseException("The character string of one character or more is necessary.", locator);
		}
	}

	/**
	 * 指定された式(<code>expression</code>)が偽の場合、
	 * 必須の子エレメントが存在しないことを示す例外をスローする。
	 * 
	 * @param expression	評価する式
	 * @param locator		エラー発生位置を示す <code>Locator</code>
	 * @param curElement	エラーメッセージに組み込むエレメント名
	 * @param childElement	エラーメッセージに組み込む、必要な子エレメント名
	 * @throws SAXParseException	評価する式が偽の場合
	 */
	static public void validNeedElement(boolean expression, Locator locator, String curElement, String childElement)
		throws SAXParseException
	{
		if (!expression) {
			// failed
			throw new SAXParseException(String.format("<%s> element must have child <%s>.", curElement, childElement), locator);
		}
	}

	/**
	 * 指定された式(<code>expression</code>)が偽の場合、
	 * エレメントが重複していることを示す例外をスローする。
	 * 
	 * @param expression	評価する式
	 * @param locator		エラー発生位置を示す <code>Locator</code>
	 * @param element		重複したエレメント名
	 * @throws SAXParseException	評価する式が偽の場合
	 */
	static public void validSingleElement(boolean expression, Locator locator, String element)
		throws SAXParseException
	{
		if (!expression) {
			// failed
			throw new SAXParseException(String.format("<%s> element must not be multiple.", String.valueOf(element)), locator);
		}
	}

	/**
	 * 開始エレメント名と終了エレメント名が等しくない場合、例外をスローする。
	 * 
	 * @param locator	エラー発生位置を示す <code>Locator</code>
	 * @param startElement	開始エレメント名
	 * @param endElement	終了エレメント名
	 * @throws SAXParseException	開始エレメント名と終了エレメント名が等しくない場合
	 */
	static public void validEndElement(Locator locator, String startElement, String endElement)
		throws SAXParseException
	{
		if (startElement != null) {
			if (startElement.equals(endElement)) {
				// valid
				return;
			}
		}
		
		// failed
		throw new SAXParseException(String.format("<%s> is not a start of <%s> element.",
				String.valueOf(endElement), String.valueOf(startElement)), locator);
	}

	/**
	 * <code>targetParent</code> に指定された文字列が、<code>needParents</code> に指定した
	 * 文字列の配列に含まれていない場合、例外をスローする。
	 * <code>needParents</code> が要素を持たない場合、<code>targetParent</code> が <tt>null</tt> で
	 * なければ、例外をスローする。
	 * 
	 * @param locator	エラー発生位置を示す <code>Locator</code>
	 * @param target	評価対象のエレメント名
	 * @param targetParent	評価対象の親エレメント名
	 * @param needParents	正しい親エレメント名の配列
	 * @throws SAXParseException	親エレメント名が正しくない場合
	 */
	static public void validChildOfParent(Locator locator, String target, String targetParent, String...needParents)
		throws SAXParseException
	{
		if (needParents.length > 1) {
			// must be child
			for (String p : needParents) {
				if (p.equals(targetParent)) {
					// valid
					return;
				}
			}
		}
		else {
			// must be root
			if (targetParent == null) {
				// valid
				return;
			}
		}
		
		// failed
		throw new SAXParseException(messageChildOfParent(target, targetParent, needParents), locator);
	}

	/**
	 * 親エレメント名が正しくないことを示すメッセージを取得する。
	 * 
	 * @param target	当該エレメント名
	 * @param targetParent	当該親エレメント名
	 * @param needParents	正しい親エレメント名の配列
	 * @return	親エレメント名が正しくないことを示すメッセージ
	 */
	static public String messageChildOfParent(String target, String targetParent, String...needParents) {
		if (needParents.length > 0) {
			StringBuilder sb = new StringBuilder();
			// must be child
			sb.append(String.format("<%s>", needParents[0]));
			if (needParents.length > 1) {
				for (int i = 1; i < (needParents.length-1); i++) {
					sb.append(String.format(",<%s>", needParents[i]));
				}
				sb.append(String.format("or <%s>", needParents[needParents.length-1]));
			}
			return String.format("<%s> element must be child of %s.", String.valueOf(target), sb.toString());
		}
		else {
			// must be root element
			return String.format("<%s> element must be XML document root.", String.valueOf(target));
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
