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
 * @(#)AADLTerminatedException.java	2.0.0	2014/03/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

/**
 * AADL実行時にプロセスが中断要求を受け付けたときにスローされる例外クラス。
 * 
 * @version 2.0.0	2014/03/22
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 2.0.0
 */
public class AADLTerminatedException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String DEFAULT_MESSAGE = "@@@ AADL process was terminated by termination request.";
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 詳細メッセージを持たない <code>AADLTerminatedException</code> を構築する。
	 */
	public AADLTerminatedException() {
		super(DEFAULT_MESSAGE);
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
	public AADLTerminatedException(Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
