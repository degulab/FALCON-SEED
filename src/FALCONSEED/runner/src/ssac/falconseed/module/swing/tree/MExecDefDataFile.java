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
 * @(#)MExecDefDataFile.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefDataFile.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefDataFile.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import ssac.falconseed.module.MExecDefFileManager;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * モジュール実行定義の抽象パスを保持するファイルモデル。
 * このオブジェクトは、モジュール実行定義のルートディレクトリのパスと、
 * モジュール実行定義ファイルのパスを保持する。
 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
 * 基準とし、大文字小文字を区別しないでチェックする。
 * 
 * @version 3.2.1
 */
public class MExecDefDataFile extends AbMExecDefFile
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** モジュール実行定義のデータファイルを示す抽象パス **/
	private final VirtualFile	_prefsFile;
	/**
	 *  フィルタマクロ定義ファイルを示す抽象パス
	 *  @since 2.0.0
	 */
	private final VirtualFile	_prefsFilterMacro;
	/**
	 * 汎用フィルタ定義ファイルを示す抽象パス
	 * @since 3.2.1
	 */
	private final VirtualFile	_prefsGenericFilter;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefDataFile(VirtualFile file) {
		super(file);
		this._prefsFile = MExecDefFileManager.getModuleExecDefDataFile(file);
		this._prefsFilterMacro = MExecDefFileManager.getMacroFilterDefDataFile(file);
		this._prefsGenericFilter = MExecDefFileManager.getGenericFilterDefDataFile(file);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFile getDataFile() {
		return _prefsFile;
	}
	
	public VirtualFile getFilterMacroDataFile() {
		return _prefsFilterMacro;
	}
	
	public VirtualFile getGenericFilterDataFile() {
		return _prefsGenericFilter;
	}

	@Override
	public boolean exists() {
		return _prefsFile.exists();
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	/**
	 * このモデルに関連付けられているファイルの最終更新時刻を取得する。
	 * この実装では、ファイルがモジュール実行定義ディレクトリを示す場合、
	 * モジュール実行定義情報ファイルの最終更新時刻を取得する。
	 * @return	ファイルが最後に変更された時刻を表す <code>long</code> 値。
	 * 			エポック (1970 年 1 月 1 日 0 時 0 分 0 秒、グリニッジ標準時) からミリ秒単位で測定。
	 * 			ファイルが存在しないか、入出力エラーが発生した場合は 0L
	 * @since 2.0.0
	 */
	@Override
	public long lastModified() {
		if (isExecDefData()) {
			return _prefsFile.lastModified();
		} else {
			return super.lastModified();
		}
	}

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリかどうかを判定する。
	 * これは、ディレクトリ直下にモジュール実行定義の情報ファイルが含まれている場合に、
	 * モジュール実行定義を示すディレクトリ（モジュール実行定義データ）と見なされる。
	 * @return	ファイルが存在し、さらにそれがモジュール実行定義情報を直下に持つディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isExecDefData() {
		return (_prefsFile.exists() && _prefsFile.isFile());
	}

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリであり、
	 * フィルタマクロであるかどうかを判定する。
	 * これは、ディレクトリ直下にフィルタマクロ定義情報ファイルが含まれている場合に、
	 * フィルタマクロを示すディレクトリ（フィルタマクロ定義データ）と見なされる。
	 * @return	ファイルが存在し、さらにそれがフィルタマクロ定義情報を直下に持つディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean isExecDefFilterMacro() {
		return (_prefsFilterMacro.exists() && _prefsFilterMacro.isFile());
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
		return (_prefsGenericFilter.exists() && _prefsGenericFilter.isFile());
	}
	
	public IMExecDefFile replaceFile(VirtualFile newFile) {
		return new MExecDefDataFile(newFile);
	}

	public IMExecDefFile[] listFiles() {
		return null;
	}

	public IMExecDefFile[] listFiles(VirtualFileFilter filter) {
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
