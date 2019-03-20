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
 * @(#)UnsupportedFormatException.java	0.93	2007/09/03
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io;

/**
 * 交換代数コア・パッケージにおいて、指定されたファイル、もしくは XML ドキュメントが
 * サポートされていないフォーマットと判断された場合にスローされる。
 * 
 * @version 0.93 2007/09/03
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.93
 *
 */
public class UnsupportedFormatException extends ExalgeIOException
{
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細メッセージを持たない <code>UnsupportedFormatException</code> を構築する。
	 */
	public UnsupportedFormatException() {
		super();
	}

	/**
	 * 指定された詳細メッセージを持つ <code>UnsupportedFormatException</code> を構築する。
	 * 
	 * @param message 詳細メッセージ
	 */
	public UnsupportedFormatException(String message) {
		super(message);
	}

	/**
	 * <code>(cause==null ? null : cause.toString())</code> の指定された原因および
	 * 詳細メッセージを使用して新規例外を構築する。
	 * 
	 * <p>通常 <tt>(cause==null ? null : cause.toString())</tt> には、<tt>cause</tt> の
	 * クラスおよび詳細メッセージが含まれる。
	 * <br>
	 * このコンストラクタは、例外が他のスロー可能オブジェクトのラッパーである場合に有用である。
	 * 
	 * @param cause 原因。あとで {@link Throwable#getCause()} メソッドで取得するために
	 * 				 保存される。<tt>null</tt> が許可されており、原因が存在しないか不明で
	 * 				 あることを示す。
	 */
	public UnsupportedFormatException(Throwable cause) {
		super(cause);
	}

	/**
	 * 指定された詳細メッセージおよび原因を使用して新規例外を構築する。
	 * 
	 * <p><code>cause</code> と関連付けられた詳細メッセージが、この例外の
	 * 詳細メッセージに自動的に統合されることはない。
	 * 
	 * @param message 詳細メッセージ。{@link Throwable#getMessage()} メソッドによる
	 * 				   取得用に保存される。
	 * @param cause 原因。あとで {@link Throwable#getCause()} メソッドで取得するために
	 * 				 保存される。<tt>null</tt> が許可されており、原因が存在しないか不明で
	 * 				 あることを示す。
	 */
	public UnsupportedFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
