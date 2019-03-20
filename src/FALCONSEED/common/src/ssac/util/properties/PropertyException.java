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
 * @(#)PropertyException.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.properties;

/**
 * プロパティの操作に失敗したときにスローされる例外。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class PropertyException extends RuntimeException
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
	 * 詳細メッセージに <tt>null</tt> を使用して、実行時例外を構築する。
	 * 原因は初期化されず、その後 {@link Throwable#initCause(Throwable)} を
	 * 呼び出すことで初期化される。
	 */
	protected PropertyException() {
		super();
	}

	/**
	 * 指定された詳細メッセージを使用して、新規例外を構築する。
	 * 原因は初期化されず、その後 {@link Throwable#initCause(Throwable)} を
	 * 呼び出すことで初期化される。
	 * @param message	詳細メッセージ（あとで {@link Throwable#getMessage()} メソッドで取得するために保存される）
	 */
	protected PropertyException(String message) {
		super(message);
	}

	/**
	 * <code>(cause==null ? null : cause.toString())</code> の指定された原因および
	 * 詳細メッセージを使用して新しい実行時例外を構築する。
	 * 通常、<code>(cause==null ? null : cause.toString())</code> には、<code>cause</code> の
	 * クラスおよび詳細メッセージが含まれる。このコンストラクタは、実行時例外が他の
	 * スロー可能オブジェクトのラッパーである場合に有用である。
	 * @param cause	原因（あとで {@link Throwable#getCause()} メソッドで取得するために保存される）。
	 * 				<tt>null</tt> 値が許可されており、原因が存在しないか不明であることを示す。
	 */
	protected PropertyException(Throwable cause) {
		super(cause);
	}

	/**
	 * 指定された詳細メッセージおよび原因を使用して新しい実行時例外を構築する。
	 * <code>cause</code> と関連付けられた詳細メッセージが、この実行時例外の詳細メッセージに
	 * 自動的に統合されることはない。
	 * 
	 * @param message	詳細メッセージ（あとで {@link Throwable#getMessage()} メソッドで取得するために保存される）
	 * @param cause	原因（あとで {@link Throwable#getCause()} メソッドで取得するために保存される）。
	 * 				<tt>null</tt> 値が許可されており、原因が存在しないか不明であることを示す。
	 */
	protected PropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
