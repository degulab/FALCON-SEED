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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)INodeLocation.java	1.00	2008/11/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.data;

/**
 * AADLマクロ要素の定義位置を取得するインタフェース。
 * <p>
 * ファイルやテキストのフォーマットに合わせ、マクロ要素が定義されている
 * 位置の情報を取得するためのインタフェースとなる。
 * 
 * @version 1.00	2008/11/17
 *
 * @since 1.00
 */
public interface INodeLocation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * マクロ要素の定義開始位置を表す値を取得する。
	 * この値の定義は、実装により異なる。
	 * 
	 * @return マクロ要素の定義開始位置
	 */
	public int getStartPosition();

	/**
	 * マクロ要素の定義終了位置を表す値を取得する。
	 * この値の定義は、実装により異なる。
	 * 
	 * @return	マクロ要素の定義終了位置
	 */
	public int getEndPosition();

	/**
	 * このインスタンスのハッシュコード値を返す。
	 * このメソッドは、このクラスの異なるインスタンスにおいて、
	 * 同値の場合は同じハッシュコード値を返す。
	 * 
	 * @return ハッシュコード値
	 */
	public int hashCode();

	/**
	 * 指定されたオブジェクトとこのインスタンスが、同値であるかを判定する。
	 * 基本的に同値とは、同じ位置を表す場合を指す。
	 * 
	 * @param obj	比較するオブジェクト
	 * @return	同値であれば <tt>true</tt>
	 */
	public boolean equals(Object obj);

	/**
	 * 位置情報の文字列表現を取得する。
	 * <p>
	 * この文字列表現は、AADLマクロ定義の種類に応じた位置情報を表す
	 * 文字列表現となる。
	 * 
	 * @return 位置情報の文字列表現。
	 */
	public String toString();
}
