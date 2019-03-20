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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Validations.java	2.0.0	2014/03/18 : move 'ssac.util' to 'ssac.aadl.macro.util' 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Validations.java	1.00	2008/10/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util;

/**
 * 条件による検査ユーティリティ。
 * 
 * @version 2.0.0	2014/03/18
 *
 * @since 1.00
 */
public final class Validations
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
	
	private Validations() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <code>expression</code> が <tt>false</tt> の場合、詳細メッセージを
	 * 持たない <code>IllegalArgumentException</code> をスローする。
	 * 
	 * @param expression	評価する式
	 * 
	 * @throws IllegalArgumentException	<code>expression</code> が <tt>false</tt> の場合
	 */
	static public void validArgument(boolean expression) {
		if (!expression) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * <code>expression</code> が <tt>false</tt> の場合、指定された詳細メッセージを
	 * 持つ <code>IllegalArgumentException</code> をスローする。
	 * 
	 * @param expression	評価する式
	 * @param message		詳細メッセージ
	 * 
	 * @throws IllegalArgumentException	<code>expression</code> が <tt>false</tt> の場合
	 */
	static public void validArgument(boolean expression, Object message) {
		if (!expression) {
			throw new IllegalArgumentException(String.valueOf(message));
		}
	}

	/**
	 * <code>expression</code> が <tt>false</tt> の場合、指定された詳細メッセージを
	 * 持つ <code>IllegalArgumentException</code> をスローする。
	 * 
	 * @param expression	評価する式
	 * @param messageFormat	詳細メッセージの書式文字列
	 * 							({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * @param messageArgs		書式文字列の書式指定子により参照される引数
	 * 							({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * 
	 * @throws IllegalArgumentException	<code>expression</code> が <tt>false</tt> の場合
	 */
	static public void validArgument(boolean expression, String messageFormat, Object...messageArgs) {
		if (!expression) {
			throw new IllegalArgumentException(String.format(messageFormat, messageArgs));
		}
	}

	/**
	 * <code>expression</code> が <tt>false</tt> の場合、詳細メッセージを
	 * 持たない <code>IllegalStateException</code> をスローする。
	 * 
	 * @param expression	評価する式
	 * 
	 * @throws IllegalStateException	<code>expression</code> が <tt>false</tt> の場合
	 */
	static public void validState(boolean expression) {
		if (!expression) {
			throw new IllegalStateException();
		}
	}

	/**
	 * <code>expression</code> が <tt>false</tt> の場合、詳細メッセージを
	 * 持つ <code>IllegalStateException</code> をスローする。
	 * 
	 * @param expression	評価する式
	 * @param message		詳細メッセージ
	 * 
	 * @throws IllegalStateException	<code>expression</code> が <tt>false</tt> の場合
	 */
	static public void validState(boolean expression, Object message) {
		if (!expression) {
			throw new IllegalStateException(String.valueOf(message));
		}
	}

	/**
	 * <code>expression</code> が <tt>false</tt> の場合、詳細メッセージを
	 * 持つ <code>IllegalStateException</code> をスローする。
	 * 
	 * @param expression	評価する式
	 * @param messageFormat	詳細メッセージの書式文字列
	 * 							({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * @param messageArgs		書式文字列の書式指定子により参照される引数
	 * 							({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * 
	 * @throws IllegalStateException	<code>expression</code> が <tt>false</tt> の場合
	 */
	static public void validState(boolean expression, String messageFormat, Object...messageArgs) {
		if (!expression) {
			throw new IllegalStateException(String.format(messageFormat, messageArgs));
		}
	}

	/**
	 * <code>reference</code> が <tt>null</tt> の場合、詳細メッセージを
	 * 持たない <code>NullPointerException</code> をスローする。
	 * 
	 * @param reference	評価するオブジェクト
	 * @return	<code>reference</code> に指定された値を返す。
	 * 
	 * @throws NullPointerException	<code>reference</code> が <tt>null</tt> の場合
	 */
	static public <T> T validNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}

	/**
	 * <code>reference</code> が <tt>null</tt> の場合、詳細メッセージを
	 * 持つ <code>NullPointerException</code> をスローする。
	 * 
	 * @param reference	評価するオブジェクト
	 * @param message		詳細メッセージ
	 * @return	<code>reference</code> に指定された値を返す。
	 * 
	 * @throws NullPointerException	<code>reference</code> が <tt>null</tt> の場合
	 */
	static public <T> T validNotNull(T reference, Object message) {
		if (reference == null) {
			throw new NullPointerException(String.valueOf(message));
		}
		return reference;
	}

	/**
	 * <code>reference</code> が <tt>null</tt> の場合、詳細メッセージを
	 * 持つ <code>NullPointerException</code> をスローする。
	 * 
	 * @param reference	評価するオブジェクト
	 * @param messageFormat	詳細メッセージの書式文字列
	 * 							({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * @param messageArgs		書式文字列の書式指定子により参照される引数
	 * 							({@link java.lang.String#format(String, Object[])} の引数と同じ)
	 * @return	<code>reference</code> に指定された値を返す。
	 * 
	 * @throws NullPointerException	<code>reference</code> が <tt>null</tt> の場合
	 */
	static public <T> T validNotNull(T reference, String messageFormat, Object...messageArgs) {
		if (reference == null) {
			throw new NullPointerException(String.format(messageFormat, messageArgs));
		}
		return reference;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
