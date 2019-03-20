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
 * @(#)IFilterEditModel.java	3.1.0	2014/05/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.util.io.VirtualFile;

/**
 * フィルタ編集データモデルの共通インタフェース。
 * 
 * @version 3.1.0	2014/05/16
 * @since 3.1.0
 */
public interface IFilterEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * フィルタが編集中であれば <tt>true</tt> を返す。
	 */
	public boolean isEditing();

	/**
	 * パス整形用フォーマッターを取得する。
	 * @return	フォーマッターが存在していればそのオブジェクト、存在しない場合は <tt>null</tt>
	 */
	public VirtualFilePathFormatterList getFormatter();

	/**
	 * パスフォーマット用の基準パスを返す。
	 * @return	基準パスを示すオブジェクト、基準パスが存在しない場合は <tt>null</tt>
	 */
	public VirtualFile getBasePath();
}
