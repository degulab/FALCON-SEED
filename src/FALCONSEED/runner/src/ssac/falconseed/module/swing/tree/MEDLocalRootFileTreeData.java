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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MEDLocalFileTreeData.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import ssac.falconseed.file.swing.tree.AliasFileTreeData;
import ssac.falconseed.file.swing.tree.IFileTreeData;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * モジュール実行定義ローカルのファイルツリーのルートディレクトリを示す
 * ツリーノードデータ。ファイルが存在しない場合でも、常にディレクトリで
 * 存在する状態にする。
 * 
 * @version 1.20	2012/03/08
 * @since 1.20
 */
public class MEDLocalRootFileTreeData extends AliasFileTreeData
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

	public MEDLocalRootFileTreeData(VirtualFile file, String aliasName) {
		super(file, aliasName);
		// null、存在しないパス、もしくは、ディレクトリ以外は受け付けない
		if (file != null && file.exists() && !file.isDirectory())
			throw new IllegalArgumentException("File is not directory : \"" + file.getAbsolutePath() + "\"");
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void setFile(VirtualFile file) {
		// null、存在しないパス、もしくは、ディレクトリ以外は受け付けない
		if (file != null && file.exists() && !file.isDirectory())
			throw new IllegalArgumentException("File is not directory : \"" + file.getAbsolutePath() + "\"");
		super.setFile(file);
	}

	@Override
	public boolean exists() {
		// 抽象パスが設定されていない場合も true を返す。
		return true;
	}

	@Override
	public boolean isDirectory() {
		// 抽象パスが設定されていない場合も true を返す。
		return true;
	}

	@Override
	public boolean isFile() {
		// 常に false を返す。
		return false;
	}

	@Override
	public IFileTreeData[] listFiles(VirtualFileFilter filter) {
		// 抽象パスが設定されていない場合は、要素が空の配列を返す。
		if (getFile() == null)
			return EMPTY_ARRAY;
		
		// 抽象パスが設定されていれば、通常の処理
		return super.listFiles(filter);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
