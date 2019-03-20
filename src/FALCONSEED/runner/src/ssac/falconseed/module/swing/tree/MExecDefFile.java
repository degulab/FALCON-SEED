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
 * @(#)MExecDefFile.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFile.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFile.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.util.ArrayList;

import ssac.falconseed.module.MExecDefFileManager;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * モジュール実行定義の抽象パスを保持するファイルモデル。
 * このオブジェクトは、基本的に単一の抽象パスを保持する。
 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
 * 基準とし、大文字小文字を区別しないでチェックする。
 * 
 * @version 3.2.1
 */
public class MExecDefFile extends AbMExecDefFile
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
	
	public MExecDefFile(VirtualFile file) {
		super(file);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリかどうかを判定する。
	 * これは、ディレクトリ直下にモジュール実行定義の情報ファイルが含まれている場合に、
	 * モジュール実行定義を示すディレクトリ（モジュール実行定義データ）と見なされる。
	 * @return	この実装では、常に <tt>false</tt> を返す。
	 */
	public boolean isExecDefData() {
		return false;
	}

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリであり、
	 * フィルタマクロであるかどうかを判定する。
	 * これは、ディレクトリ直下にフィルタマクロ定義情報ファイルが含まれている場合に、
	 * フィルタマクロを示すディレクトリ（フィルタマクロ定義データ）と見なされる。
	 * @return	この実装では、常に <tt>false</tt> を返す。
	 * @since 2.0.0
	 */
	public boolean isExecDefFilterMacro() {
		return false;
	}

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリであり、
	 * 汎用フィルタであるかどうかを判定する。
	 * これは、ディレクトリ直下に汎用フィルタ定義情報ファイルが含まれている場合に、
	 * 汎用フィルタを示すディレクトリ（汎用フィルタ定義データ)とみなされる。
	 * @return	ファイルが存在し、さらにそれが汎用フィルタ定義情報を直下に持つディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean isExecDefGenericFilter() {
		return false;
	}

	public IMExecDefFile replaceFile(VirtualFile newFile) {
		return new MExecDefFile(newFile);
	}

	public IMExecDefFile[] listFiles() {
		return listFiles(null);
	}

	public IMExecDefFile[] listFiles(VirtualFileFilter filter) {
		VirtualFile[] files;
		if (filter != null) {
			files = getFile().listFiles(filter);
		} else {
			files = getFile().listFiles();
		}
		if (files == null) {
			return null;
		}
		if (files.length <= 0) {
			return EMPTY_ARRAY;
		}
		
		ArrayList<IMExecDefFile> list = new ArrayList<IMExecDefFile>(files.length);
		for (VirtualFile f : files) {
			if (f.isDirectory()) {
				VirtualFile prefs = MExecDefFileManager.getModuleExecDefDataFile(f);
				if (prefs.exists() && prefs.isFile()) {
					// this directory is Module Execution Definition Data
					list.add(new MExecDefDataFile(f));
				} else {
					// this directory is normal
					list.add(new MExecDefFile(f));
				}
			} else {
				list.add(new MExecDefFile(f));
			}
		}
		
		return list.toArray(new IMExecDefFile[list.size()]);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
