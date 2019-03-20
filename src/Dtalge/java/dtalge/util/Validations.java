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
 * @(#)Validations.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util;

/**
 * 条件による検査ユーティリティ。
 * 
 * @version 0.10	2008/08/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.10
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
