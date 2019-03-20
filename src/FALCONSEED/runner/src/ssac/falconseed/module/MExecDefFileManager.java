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
 * @(#)MExecDefFileManager.java	3.2.0	2015/06/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFileManager.java	2.0.0	2012/10/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFileManager.java	1.20	2012/03/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFileManager.java	1.10	2011/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFileManager.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.File;

import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.io.VirtualFile;

/**
 * モジュール実行定義ファイルの管理機能。
 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
 * 基準とし、大文字小文字を区別しないでチェックする。
 * 
 * @version 3.2.0
 */
public class MExecDefFileManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** モジュール実行定義情報を格納する定義ファイル名 **/
	static public final String MEXECDEF_PREFS_FILENAME	= ".mexecdef.prefs";
	/** モジュール実行定義の実行時引数の履歴を格納する定義ファイル名 **/
	static public final String MEXECDEF_HISTORY_FILENAME	= ".mexecdef.history";
	/** モジュール実行定義の実行時引数の履歴を格納する定義ファイル名 **/
	static public final String MACROFILTER_PREFS_FILENAME		= ".macrofilter.prefs";
	/** モジュール実行定義の汎用フィルタ専用スキーマ定義ファイル **/
	static public final String GENERICFILTER_SCHEMA_FILENAME	= "GenericFilterSchema.xml";
	/** モジュール実行定義エクスポートファイルの拡張子 **/
	static public final String EXT_FILE_MEXECDEF	= RunnerMessages.getInstance().extMExecDefFile;

	/** モジュール実行定義のローカルデータディレクトリディレクトリ名 **/
	static public final String MEXECDEF_FILES_ROOT_DIRNAME	= "files";
	/** モジュール実行定義のローカルデータ作業ディレクトリ名 **/
	static public final String MEXECDEF_FILES_WORK_DIRNAME	= ".$files";

	/** モジュール実行定義の作業ディレクトリ名 **/
	static public final String MEXECDEF_WORKING_DIRNAME		= "$work";
	/** モジュール実行定義ディレクトリ直下の固定引数値格納ディレクトリ名 **/
	static public final String MEXECDEF_MODULEARG_DIRNAME		= "args";
	/**
	 * モジュール実行定義の引数固定値を格納するサブディレクトリプレフィックス。
	 * 通常、ディレクトリは <code>MEXECDEF_MODULEARG_DIRNAME</code> に連番付きで作成される。
	 */
	static public final String MEXECDEF_MODULEARG_SUBDIR_PREFIX	= "a";
	
	/** モジュール実行定義ディレクトリ直下のマクロフィルタ専用マクロ格納ディレクトリ名 **/
	static public final String MACROFILTER_MACRO_ROOT_DIRNAME		= "macro";
	/** マクロフィルタ専用のAADLマクロ定義ファイルの拡張子を除くファイル名 **/
	static public final String MACROFILTER_AADLMACRO_BASENAME		= "FilterMacro";
	/** マクロフィルタ専用のAADLマクロ定義ファイル名 **/
	static public final String MACROFILTER_AADLMACRO_FILENAME		= MACROFILTER_AADLMACRO_BASENAME + ModuleFileManager.EXT_AADL_MACRO;
	/** マクロフィルタ専用のサブフィルタ格納用ディレクトリ名 **/
	static public final String MACROFILTER_SUBFILTER_DIRNAME_		= "filters";
	/**
	 *  マクロフィルタ専用のサブフィルタ格納用ディレクトリ名。
	 *  通常、ディレクトリは <code>MACROFILTER_SUBFILTER_DIRNAME</code> に連番付きで作成される。
	 */
	static public final String MACROFILTER_SUBFILTER_SUBDIR_PREFIX	= "f";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private MExecDefFileManager() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <em>file</em> のファイル名がモジュール実行定義情報ファイル名であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isModuleExecDefPrefsFile(VirtualFile file) {
		return file.getName().equalsIgnoreCase(MEXECDEF_PREFS_FILENAME);
	}

	/**
	 * <em>file</em> のファイル名がマクロフィルタ定義ファイル名であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @param file	判定する抽象パス
	 * @return	マクロフィルタ定義ファイルの抽象パスであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	static public boolean isMacroFilterPrefsFile(VirtualFile file) {
		return file.getName().equalsIgnoreCase(MACROFILTER_PREFS_FILENAME);
	}

	/**
	 * <em>file</em> のファイル名が汎用フィルタ定義ファイル名であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @param file	判定する抽象パス
	 * @return	汎用フィルタ定義ファイルの抽象パスであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.0
	 */
	static public boolean isGenericFilterPrefsFile(VirtualFile file) {
		return file.getName().equalsIgnoreCase(GENERICFILTER_SCHEMA_FILENAME);
	}

	/**
	 * 指定された抽象パスが、1 つのモジュール実行定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下にモジュール実行定義情報ファイルが
	 * 存在している場合、指定された抽象パスをモジュール実行定義のデータディレクトリと見なす。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	モジュール実行定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean isModuleExecDefData(VirtualFile file) {
		if (file.isDirectory()) {
			VirtualFile prefs = file.getChildFile(MEXECDEF_PREFS_FILENAME);
			return (prefs.exists() && prefs.isFile());
		}
		return false;
	}

	/**
	 * 指定された抽象パスが、1 つのマクロフィルタ定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下にマクロフィルタ定義情報ファイルが
	 * 存在している場合、指定された抽象パスをマクロフィルタ定義のデータディレクトリとみなす。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	マクロフィルタ定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	static public boolean isMacroFilterDefData(VirtualFile file) {
		if (file.isDirectory()) {
			VirtualFile prefs = file.getChildFile(MACROFILTER_PREFS_FILENAME);
			return (prefs.exists() && prefs.isFile());
		}
		return false;
	}

	/**
	 * 指定された抽象パスが、1 つの汎用フィルタ定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下に汎用フィルタ定義情報ファイルが
	 * 存在している場合、指定された抽象パスを汎用フィルタ定義のデータディレクトリとみなす。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	汎用フィルタ定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.0
	 */
	static public boolean isGenericFilterDefData(VirtualFile file) {
		if (file.isDirectory()) {
			VirtualFile prefs = file.getChildFile(GENERICFILTER_SCHEMA_FILENAME);
			return (prefs.exists() && prefs.isFile());
		}
		return false;
	}
	
	/**
	 * 指定された抽象パスが、1 つのモジュール実行定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下にモジュール実行定義情報ファイルが
	 * 存在している場合、指定された抽象パスをモジュール実行定義のデータディレクトリと見なす。
	 * <p>このメソッドは、指定された抽象パスがディレクトリかどうかは判定しない。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	モジュール実行定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	static public boolean isModuleExecDefDataDirectory(VirtualFile file) {
		VirtualFile prefs = file.getChildFile(MEXECDEF_PREFS_FILENAME);
		return (prefs.exists() && prefs.isFile());
	}

	/**
	 * 指定された抽象パスが、1 つのマクロフィルタ定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下にマクロフィルタ定義情報ファイルが
	 * 存在している場合、指定された抽象パスをマクロフィルタ定義のデータディレクトリと見なす。
	 * <p>このメソッドは、指定された抽象パスがディレクトリかどうかは判定しない。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	マクロフィルタ定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	static public boolean isMacroFilterDefDataDirectory(VirtualFile file) {
		VirtualFile prefs = file.getChildFile(MACROFILTER_PREFS_FILENAME);
		return (prefs.exists() && prefs.isFile());
	}
	
	/**
	 * 指定された抽象パスが、1 つの汎用フィルタ定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下に汎用フィルタ定義情報ファイルが
	 * 存在している場合、指定された抽象パスを汎用フィルタ定義のデータディレクトリと見なす。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	汎用フィルタ定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.0
	 */
	static public boolean isGenericFilterDefDataDirectory(VirtualFile file) {
		VirtualFile prefs = file.getChildFile(GENERICFILTER_SCHEMA_FILENAME);
		return (prefs.exists() && prefs.isFile());
	}
	
	/**
	 * 指定された抽象パスが、1 つのモジュール実行定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下にモジュール実行定義情報ファイルが
	 * 存在している場合、指定された抽象パスをモジュール実行定義のデータディレクトリと見なす。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	モジュール実行定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	static public boolean isModuleExecDefData(File file) {
		if (file.isDirectory()) {
			File prefs = new File(file, MEXECDEF_PREFS_FILENAME);
			return (prefs.exists() && prefs.isFile());
		}
		return false;
	}
	
	/**
	 * 指定された抽象パスが、1 つのマクロフィルタ定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下にマクロフィルタ定義情報ファイルが
	 * 存在している場合、指定された抽象パスをマクロフィルタ定義のデータディレクトリと見なす。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	マクロフィルタ定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	static public boolean isMacroFilterDefData(File file) {
		if (file.isDirectory()) {
			File prefs = new File(file, MACROFILTER_PREFS_FILENAME);
			return (prefs.exists() && prefs.isFile());
		}
		return false;
	}
	
	/**
	 * 指定された抽象パスが、1 つの汎用フィルタ定義を示すデータディレクトリであれば <tt>true</tt> を返す。
	 * 指定された抽象パスがディレクトリであり、そのディレクトリ直下に汎用フィルタ定義情報ファイルが
	 * 存在している場合、指定された抽象パスを汎用フィルタ定義のデータディレクトリと見なす。
	 * @param file	判定するディレクトリを示す抽象パス
	 * @return	汎用フィルタ定義のデータディレクトリなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.0
	 */
	static public boolean isGenericFilterDefData(File file) {
		if (file.isDirectory()) {
			File prefs = new File(file, GENERICFILTER_SCHEMA_FILENAME);
			return (prefs.exists() && prefs.isFile());
		}
		return false;
	}

	/**
	 * モジュール実行定義データファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @param parent	親とする抽象パス
	 * @return	<em>parent</em> を親とするモジュール実行定義データファイルの抽象パス
	 */
	static public VirtualFile getModuleExecDefDataFile(VirtualFile parent) {
		return parent.getChildFile(MEXECDEF_PREFS_FILENAME);
	}
	
	/**
	 * マクロフィルタ定義データファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @param parent	親とする抽象パス
	 * @return	<em>parent</em> を親とするマクロフィルタ定義データファイルの抽象パス
	 * @since 2.0.0
	 */
	static public VirtualFile getMacroFilterDefDataFile(VirtualFile parent) {
		return parent.getChildFile(MACROFILTER_PREFS_FILENAME);
	}
	
	/**
	 * 汎用フィルタ定義データファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @param parent	親とする抽象パス
	 * @return	<em>parent</em> を親とする汎用フィルタ定義データファイルの抽象パス
	 * @since 3.2.0
	 */
	static public VirtualFile getGenericFilterDefDataFile(VirtualFile parent) {
		return parent.getChildFile(GENERICFILTER_SCHEMA_FILENAME);
	}
	
	/**
	 * モジュール実行定義データファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @param parent	親とする抽象パス
	 * @return	<em>parent</em> を親とするモジュール実行定義データファイルの抽象パス
	 * @since 1.10
	 */
	static public File getModuleExecDefDataFile(File parent) {
		return new File(parent, MEXECDEF_PREFS_FILENAME);
	}
	
	/**
	 * マクロフィルタ定義データファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @param parent	親とする抽象パス
	 * @return	<em>parent</em> を親とするマクロフィルタ定義データファイルの抽象パス
	 * @since 1.10
	 */
	static public File getMacroFitlerDefDataFile(File parent) {
		return new File(parent, MACROFILTER_PREFS_FILENAME);
	}
	
	/**
	 * 汎用フィルタ定義データファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @param parent	親とする抽象パス
	 * @return	<em>parent</em> を親とする汎用フィルタ定義データファイルの抽象パス
	 * @since 3.2.0
	 */
	static public File getGenericFilterDefDataFile(File parent) {
		return new File(parent, GENERICFILTER_SCHEMA_FILENAME);
	}

	/**
	 * 指定された抽象パスが、1 つのモジュール実行定義を示すデータディレクトリであれば、
	 * そのモジュール実行定義の最終更新日時を表す値を取得する。このメソッドは、モジュール実行定義に
	 * 含まれる設定ファイルの最終更新日時を、モジュール実行定義の最終更新日時とする。
	 * @param file	モジュール実行定義のデータディレクトリを示す抽象パス
	 * @return	モジュール実行定義の最終更新日時を返す。指定された抽象パスがモジュール実行定義では
	 * 			ない場合や、指定された抽象パスのディレクトリが存在しない場合は 0L を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.10
	 */
	static public long lastModifiedModuleExecDefData(VirtualFile file) {
		long lm = 0L;
		if (file.isDirectory()) {
			VirtualFile prefs = file.getChildFile(MEXECDEF_PREFS_FILENAME);
			if (prefs.exists() && prefs.isFile()) {
				lm = prefs.lastModified();
			}
		}
		return lm;
	}
	
	/**
	 * 指定された抽象パスが、1 つのマクロフィルタ定義を示すデータディレクトリであれば、
	 * そのマクロフィルタ定義の最終更新日時を表す値を取得する。このメソッドは、マクロフィルタ定義に
	 * 含まれる設定ファイルの最終更新日時を、マクロフィルタ定義の最終更新日時とする。
	 * @param file	マクロフィルタ定義のデータディレクトリを示す抽象パス
	 * @return	マクロフィルタ定義の最終更新日時を返す。指定された抽象パスがマクロフィルタ定義では
	 * 			ない場合や、指定された抽象パスのディレクトリが存在しない場合は 0L を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.0.0
	 */
	static public long lastModifiedMacroFilterDefData(VirtualFile file) {
		long lm = 0L;
		if (file.isDirectory()) {
			VirtualFile prefs = file.getChildFile(MACROFILTER_PREFS_FILENAME);
			if (prefs.exists() && prefs.isFile()) {
				lm = prefs.lastModified();
			}
		}
		return lm;
	}
	
	/**
	 * 指定された抽象パスが、1 つの汎用フィルタ定義を示すデータディレクトリであれば、
	 * その汎用フィルタ定義の最終更新日時を表す値を取得する。このメソッドは、汎用フィルタ定義に
	 * 含まれる設定ファイルの最終更新日時を、汎用フィルタ定義の最終更新日時とする。
	 * @param file	汎用フィルタ定義のデータディレクトリを示す抽象パス
	 * @return	汎用フィルタ定義の最終更新日時を返す。指定された抽象パスが汎用フィルタ定義では
	 * 			ない場合や、指定された抽象パスのディレクトリが存在しない場合は 0L を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.2.0
	 */
	static public long lastModifiedGenericFilterDefData(VirtualFile file) {
		long lm = 0L;
		if (file.isDirectory()) {
			VirtualFile prefs = file.getChildFile(GENERICFILTER_SCHEMA_FILENAME);
			if (prefs.exists() && prefs.isFile()) {
				lm = prefs.lastModified();
			}
		}
		return lm;
	}
	
	/**
	 * 指定された抽象パスが、1 つのモジュール実行定義を示すデータディレクトリであれば、
	 * そのモジュール実行定義の最終更新日時を表す値を取得する。このメソッドは、モジュール実行定義に
	 * 含まれる設定ファイルの最終更新日時を、モジュール実行定義の最終更新日時とする。
	 * @param file	モジュール実行定義のデータディレクトリを示す抽象パス
	 * @return	モジュール実行定義の最終更新日時を返す。指定された抽象パスがモジュール実行定義では
	 * 			ない場合や、指定された抽象パスのディレクトリが存在しない場合は 0L を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.10
	 */
	static public long lastModifiedModuleExecDefData(File file) {
		long lm = 0L;
		if (file.isDirectory()) {
			File prefs = new File(file, MEXECDEF_PREFS_FILENAME);
			if (prefs.exists() && prefs.isFile()) {
				lm = prefs.lastModified();
			}
		}
		return lm;
	}
	
	/**
	 * 指定された抽象パスが、1 つのマクロフィルタ定義を示すデータディレクトリであれば、
	 * そのマクロフィルタ定義の最終更新日時を表す値を取得する。このメソッドは、マクロフィルタ定義に
	 * 含まれる設定ファイルの最終更新日時を、マクロフィルタ定義の最終更新日時とする。
	 * @param file	マクロフィルタ定義のデータディレクトリを示す抽象パス
	 * @return	マクロフィルタ定義の最終更新日時を返す。指定された抽象パスがマクロフィルタ定義では
	 * 			ない場合や、指定された抽象パスのディレクトリが存在しない場合は 0L を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.0.0
	 */
	static public long lastModifiedMacroFilterDefData(File file) {
		long lm = 0L;
		if (file.isDirectory()) {
			File prefs = new File(file, MACROFILTER_PREFS_FILENAME);
			if (prefs.exists() && prefs.isFile()) {
				lm = prefs.lastModified();
			}
		}
		return lm;
	}
	
	/**
	 * 指定された抽象パスが、1 つの汎用フィルタ定義を示すデータディレクトリであれば、
	 * その汎用フィルタ定義の最終更新日時を表す値を取得する。このメソッドは、汎用フィルタ定義に
	 * 含まれる設定ファイルの最終更新日時を、汎用フィルタ定義の最終更新日時とする。
	 * @param file	汎用フィルタ定義のデータディレクトリを示す抽象パス
	 * @return	汎用フィルタ定義の最終更新日時を返す。指定された抽象パスが汎用フィルタ定義では
	 * 			ない場合や、指定された抽象パスのディレクトリが存在しない場合は 0L を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.2.0
	 */
	static public long lastModifiedGenericFilterDefData(File file) {
		long lm = 0L;
		if (file.isDirectory()) {
			File prefs = new File(file, GENERICFILTER_SCHEMA_FILENAME);
			if (prefs.exists() && prefs.isFile()) {
				lm = prefs.lastModified();
			}
		}
		return lm;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
