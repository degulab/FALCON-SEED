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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FilterArgUtil.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.common.gui.util;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.swing.FilterArgEditModel;
import ssac.falconseed.module.swing.FilterArgReferValueEditModel;

/**
 * フィルタ定義引数に関するユーティリティ群。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class FilterArgUtil
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
	
	private FilterArgUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数番号を、フィルタマクロの引数参照を示す文字列に整形する。
	 * @param argno	引数番号
	 * @return	整形した文字列
	 */
	static public String formatLinkArgNo(int argno) {
		return String.format("${%d}", argno);
	}

	/**
	 * 指定された引数情報を、フィルタマクロの引数参照を示す文字列に整形する。
	 * @param referArg	引数情報
	 * @return	整形した文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.1.0
	 */
	static public String formatLinkArgNo(IModuleArgConfig referArg) {
		if (referArg instanceof FilterArgReferValueEditModel) {
			// フィルタマクロ定義引数の参照
			FilterArgReferValueEditModel refDefArg = (FilterArgReferValueEditModel)referArg;
			return String.format("%s${%d}%s", refDefArg.getPrefix(), refDefArg.getArgNo(), refDefArg.getSuffix());
		} else {
			// その他の引数参照
			return String.format("${%d}", referArg.getArgNo());
		}
	}

	/**
	 * 指定されたインデックスを、引数番号を示す文字列に整形する。
	 * @param rowIndex	引数の位置を示すインデックス
	 * @return	整形した文字列
	 */
	static public String formatArgNo(int rowIndex) {
		return String.format("($%d)", rowIndex+1);
	}

	/**
	 * 指定されたインデックスと引数属性を、表示形式となる文字列に整形する。
	 * @param rowIndex	引数の位置を示すインデックス
	 * @param type		引数属性
	 * @return	整形した文字列
	 * @throws NullPointerException	<em>type</em> が <tt>null</tt> の場合
	 */
	static public String formatArgType(int rowIndex, ModuleArgType type) {
		return String.format("($%d)%s", rowIndex+1, type.toString());
	}
	
	static public String formatArgType(FilterArgEditModel argModel) {
		return String.format("($%d)%s", argModel.getArgNo(), argModel.getType());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
