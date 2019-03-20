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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)TreeFileData.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * 抽象パスを保持するファイルモデル。
 * このオブジェクトは、基本的に単一の抽象パスを保持する。
 * このオブジェクトは不変オブジェクトである。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class TreeFileData extends AbTreeFileData
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
	
	public TreeFileData(VirtualFile file) {
		super(file);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモデルに関連付けられているファイルオブジェクトを、
	 * 指定された抽象パスに置き換えた、新しいモデルを生成する。
	 * 基本的に、抽象パス以外の情報は複製される。
	 * @param newFile	新しい抽象パス
	 * @return	生成された <code>ITreeFileData</code> オブジェクト
	 */
	public ITreeFileData replaceFile(VirtualFile newFile) {
		return new TreeFileData(newFile);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected TreeFileData createFileData(VirtualFile file) {
		return new TreeFileData(file);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
