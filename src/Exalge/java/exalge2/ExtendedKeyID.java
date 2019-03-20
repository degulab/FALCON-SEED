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
 * @(#)ExtendedKeyID.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExtendedKeyID.java	0.90	2007/07/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2;

import java.lang.reflect.Field;

/**
 * 交換代数の拡張基底キーを識別するキーIDクラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)であり、
 * 定数オブジェクトのみ参照可能となっている。
 * 交換代数の拡張基底キーを識別するキーIDとして使用する。
 * 
 * @version 0.91 2007/07/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.90
 */
public class ExtendedKeyID
{
	/**
	 * 交換代数拡張基底キーの単位キーを示すキーID。
	 */
	static public final ExtendedKeyID UNIT    = new ExtendedKeyID(0);
	/**
	 * 交換代数拡張基底キーの時間キーを示すキーID。
	 */
	static public final ExtendedKeyID TIME    = new ExtendedKeyID(1);
	/**
	 * 交換代数拡張基底キーのサブジェクトキーを示すキーID。
	 */
	static public final ExtendedKeyID SUBJECT = new ExtendedKeyID(2);

	/**
	 * 交換代数拡張基底キーの総数。<br>
	 * この定数は、交換代数基底クラスの内部で使用される。
	 */
	static protected final int NUM_KEYS = 3;

	/**
	 * 拡張基底キーID値。
	 */
	private final int keyid;

	/**
	 * 拡張基底キーIDの新しいインスタンスを生成する。<br>
	 * このコンストラクタは、内部処理用。
	 * 
	 * @param value 拡張基底キーID値
	 */
	private ExtendedKeyID(int value) {
		this.keyid = value;
	}

	/**
	 * 拡張基底キーID値を取得する。<br>
	 * このメソッドは、交換代数基底クラスの内部で使用される。
	 * 
	 * @return 拡張基底キーID値
	 */
	protected int intValue() {
		return this.keyid;
	}

	/**
	 * 拡張基底キーの総数を返す。
	 * 
	 * @return 拡張基底キー総数
	 */
	static public int getNumKeys() {
		return NUM_KEYS;
	}

	/**
	 * 拡張基底キーIDを示す文字列を返す。
	 * 
	 * @return 拡張基底キーIDを示す文字列
	 */
	public String toString() {
		Field[] fields = this.getClass().getFields();
		String retText = "Unknown";
		for (int i = 0; i < fields.length; i++) {
			Class clsInst = fields[i].getType();
			if (clsInst == this.getClass()) {
				String strName = fields[i].getName();
				ExtendedKeyID enumInst;
				try {
					Object objInst = fields[i].get(null);
					enumInst = (ExtendedKeyID)objInst;
				}
				catch (Exception ex) {
					enumInst = null;
				}
				if (enumInst != null && enumInst == this) {
					retText = strName;
					break;
				}
			}
		}
		return retText;
	}
}
