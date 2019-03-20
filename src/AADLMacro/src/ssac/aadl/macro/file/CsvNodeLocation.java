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
 * @(#)CsvNodeLocation.java	2.0.0	2014/03/18 : move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvNodeLocation.java	1.00	2008/11/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.file;

import static ssac.aadl.macro.util.Validations.validArgument;
import ssac.aadl.macro.data.INodeLocation;

/**
 * CSVフォーマットにおけるAADLマクロ要素の定義位置を保持するクラス。
 * <p>
 * 定義位置の値は、CSVフォーマットの先頭行を 1 とする行番号を表す。
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
public class CsvNodeLocation implements INodeLocation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final int startLineNo;
	private final int endLineNo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvNodeLocation(int lineNo) {
		this(lineNo, lineNo);
	}
	
	public CsvNodeLocation(int startLineNo, int endLineNo) {
		validArgument((startLineNo > 0), "start line no less than 1.");
		validArgument((endLineNo > 0), "end line no less than 1.");
		validArgument((startLineNo <= endLineNo), "start line no greater than end line no.");
		this.startLineNo = startLineNo;
		this.endLineNo   = endLineNo;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	/**
	 * マクロ要素の定義開始位置を表す値を取得する。
	 * この値は、マクロ要素の定義開始位置の行番号となる。
	 * 
	 * @return マクロ要素の定義開始位置
	 */
	public int getStartPosition() {
		return startLineNo;
	}

	/**
	 * マクロ要素の定義終了位置を表す値を取得する。
	 * この値は、マクロ要素定義終了位置の行番号となる。
	 * 
	 * @return	マクロ要素の定義終了位置
	 */
	public int getEndPosition() {
		return endLineNo;
	}

	/**
	 * このインスタンスのハッシュコード値を返す。
	 * このメソッドは、このクラスの異なるインスタンスにおいて、
	 * 同値の場合は同じハッシュコード値を返す。
	 * 
	 * @return ハッシュコード値
	 */
	public int hashCode() {
		return (startLineNo * endLineNo + startLineNo + endLineNo);
	}

	/**
	 * 指定されたオブジェクトとこのインスタンスが、同値であるかを判定する。
	 * 基本的に同値とは、同じ位置を表す場合を指す。
	 * 
	 * @param obj	比較するオブジェクト
	 * @return	同値であれば <tt>true</tt>
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof CsvNodeLocation) {
			CsvNodeLocation al = (CsvNodeLocation)obj;
			if (al.startLineNo == this.startLineNo && al.endLineNo == this.endLineNo) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 位置情報の文字列表現を取得する。
	 * <p>
	 * この文字列表現は、次のようになる。<br>
	 * &nbsp;&nbsp;&nbsp;[line:<i>開始位置</i>]<br>
	 * この<i>開始位置</i>が、マクロ定義開始位置の行番号となる。
	 * 
	 * @return 位置情報の文字列表現。
	 */
	public String toString() {
		return ("[line:" + startLineNo + "]");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
