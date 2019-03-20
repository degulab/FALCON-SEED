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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleFileManager.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleFileManager.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.Icon;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.setting.AadlJarProfile;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.aadl.module.setting.CompileSettings;
import ssac.aadl.module.setting.MacroExecSettings;
import ssac.aadl.module.setting.ModuleSettings;
import ssac.aadl.module.setting.PackageSettings;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.DefaultFileFactory;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileManager;

/**
 * モジュールファイルが存在するファイルシステムを管理する、
 * アプリケーション唯一のマネージャクラス。
 * 
 * @version 1.17	2010/11/19
 * @since 1.14
 */
public class ModuleFileManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** モジュールパッケージの設定ファイル名 **/
	static public final String PACK_PREFS_FILENAME = ".aadl_modulepack.prefs";
	/** プロジェクトの設定ファイル名 **/
	static public final String PROJECT_PREFS_FILENAME = ".aadl_project.prefs";
	/** AADLソースファイルの拡張子 **/
	static public final String EXT_AADL_SRC	= ".aadl";
	/** AADLマクロファイルの拡張子 **/
	static public final String EXT_AADL_MACRO	= ".amf";
	/** AADL実行モジュール(JARモジュール)の拡張子 **/
	static public final String EXT_JARMODULE	= ".jar";
	/** 設定ファイルの拡張子 **/
	static public final String EXT_FILE_PREFS	= ".prefs";
	/** CSVファイルの拡張子 **/
	static public final String EXT_FILE_CSV	= ".csv";
	/** XMLファイルの拡張子 **/
	static public final String EXT_FILE_XML	= ".xml";
	/** AADLソース設定ファイルの拡張子 **/
	static public final String EXT_PREFS_AADL_SRC		= EXT_AADL_SRC + EXT_FILE_PREFS;
	/** AADLマクロ設定ファイルの拡張子 **/
	static public final String EXT_PREFS_AADL_MACRO	= EXT_AADL_MACRO + EXT_FILE_PREFS;
	/** AADL実行モジュール(JARモジュール)設定ファイルの拡張子 **/
	static public final String EXT_PREFS_JARMODULE	= EXT_JARMODULE + EXT_FILE_PREFS;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** アプリケーション唯一のモジュールファイルマネージャ **/
	static private ModuleFileManager	_instance;
	/** ファイルファクトリ・マネージャ **/
	private final VirtualFileManager	_fileManager = new VirtualFileManager();

	//------------------------------------------------------------
	// Singleton
	//------------------------------------------------------------
	
	static public void initialize() {
		if (_instance != null)
			throw new AssertionError("TestModuleFileManager already initialized!");
		
		_instance = new ModuleFileManager();
	}
	
	static protected ModuleFileManager getInstance() {
		if (_instance == null)
			throw new AssertionError("TestModuleFileManager not initialized!");
		return _instance;
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private ModuleFileManager() {
		// サポートするファクトリの登録
		_fileManager.registerFactory(DefaultFileFactory.getInstance(), CommonResources.ICON_DISKUNIT);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <em>file</em> のファイル名に設定ファイルの拡張子を付加した
	 * 新しい抽象パスを返す。
	 * <em>file</em> の拡張子が設定ファイルの拡張子の場合、
	 * 拡張子は付加されない。
	 * @return	設定ファイルの拡張子が付加された抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public VirtualFile addPrefsExtension(VirtualFile file) {
		if (Strings.endsWithIgnoreCase(file.getName(), EXT_FILE_PREFS)) {
			return file;
		} else {
			return file.getFactory().newFile(file.getPath() + EXT_FILE_PREFS);
		}
	}
	
	/**
	 * ファイル拡張子を、'.aadl' に置き換えた新しい抽象パスを返す。
	 * @param file	対象ファイル
	 * @return	新しい抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public VirtualFile replaceExtensionToAadlSource(VirtualFile file) {
		String newPath = Files.replaceExtension(file.getPath(), EXT_AADL_SRC);
		return file.getFactory().newFile(newPath);
	}

	/**
	 * <em>file</em> のファイル名がプロジェクト設定ファイル名であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isProjectPrefsFile(VirtualFile file) {
		return file.getName().equalsIgnoreCase(PROJECT_PREFS_FILENAME);
	}

	/**
	 * <em>file</em> のファイル名がモジュールパッケージ設定ファイル名であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isModulePackagePrefsFile(VirtualFile file) {
		return file.getName().equalsIgnoreCase(PACK_PREFS_FILENAME);
	}

	/**
	 * <em>file</em> のファイル名の拡張子が AADL ソースファイルのものであれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isAadlSourceFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_AADL_SRC);
	}
	
	/**
	 * <em>file</em> のファイル名の拡張子が AADL ソースの設定ファイルのものであれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isAadlSourcePrefsFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_PREFS_AADL_SRC);
	}

	/**
	 * <em>file</em> のファイル名の拡張子が AADL マクロファイルのものであれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isAadlMacroFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_AADL_MACRO);
	}
	
	/**
	 * <em>file</em> のファイル名の拡張子が AADL マクロの設定ファイルのものであれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isAadlMacroPrefsFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_PREFS_AADL_MACRO);
	}

	/**
	 * <em>file</em> のファイル名の拡張子が &quot;.jar&quot; であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isJarFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_JARMODULE);
	}

	/**
	 * <em>file</em> のファイル名の拡張子が &quot;.jar.prefs&quot; であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isJarPrefsFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_PREFS_JARMODULE);
	}
	
	/**
	 * <em>file</em> のファイル名の拡張子が設定ファイルのものであれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isPrefsFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_FILE_PREFS);
	}
	
	/**
	 * <em>file</em> のファイル名の拡張子が &quot;.csv&quot; であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isCsvFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_FILE_CSV);
	}
	
	/**
	 * <em>file</em> のファイル名の拡張子が &quot;.xml&quot; であれば <tt>true</tt> を返す。
	 * このメソッドは、大文字小文字を区別しない。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isXmlFile(VirtualFile file) {
		return Strings.endsWithIgnoreCase(file.getName(), EXT_FILE_XML);
	}

	/**
	 * 指定された抽象パスがプロジェクト・ルートであるかを判定する。
	 * プロジェクトルートとは、その直下にプロジェクト設定ファイルが
	 * 存在するディレクトリを指す。
	 * このメソッドは、指定された抽象パスがディレクトリであり、
	 * 直下にプロジェクト設定ファイルが存在する場合に <tt>true</tt> を返す。
	 * @param file	判定対象の抽象パス
	 * @return	指定された抽象パスがプロジェクト・ルートの場合に <tt>true</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 * @throws SecurityException		ファイルにアクセスできなかった場合
	 */
	static public boolean isProjectRoot(VirtualFile file) {
		if (file.isDirectory()) {
			VirtualFile fPrefs = getProjectPrefsFile(file);
			return (fPrefs.exists() && fPrefs.isFile());
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された抽象パスがモジュールパッケージ・ルートであるかを判定する。
	 * モジュールパッケージルートとは、その直下にパッケージ設定ファイルが
	 * 存在するディレクトリを指す。
	 * このメソッドは、指定された抽象パスがディレクトリであり、
	 * 直下にパッケージ設定ファイルが存在する場合に <tt>true</tt> を返す。
	 * @param file	判定対象の抽象パス
	 * @return	指定された抽象パスがモジュールパッケージ・ルートの場合に <tt>true</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 * @throws SecurityException		ファイルにアクセスできなかった場合
	 */
	static public boolean isModulePackageRoot(VirtualFile file) {
		if (file.isDirectory()) {
			VirtualFile fPrefs = getModulePackagePrefsFile(file);
			return (fPrefs.exists() && fPrefs.isFile());
		} else {
			return false;
		}
	}

	/**
	 * プロジェクト設定ファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @return <em>parent</em> を親とするプロジェクト設定ファイルの抽象パス
	 * @throws NullPointerException	<em>parent</em> が <tt>null</tt> の場合
	 */
	static public VirtualFile getProjectPrefsFile(VirtualFile parent) {
		return parent.getFactory().newFile(parent, PROJECT_PREFS_FILENAME);
	}
	
	/**
	 * モジュールパッケージ設定ファイルの抽象パスを生成する。
	 * このメソッドは、<em>parent</em> の正当性については検査しない。
	 * @return <em>parent</em> を親とするモジュールパッケージ設定ファイルの抽象パス
	 * @throws NullPointerException	<em>parent</em> が <tt>null</tt> の場合
	 */
	static public VirtualFile getModulePackagePrefsFile(VirtualFile parent) {
		return parent.getFactory().newFile(parent, PACK_PREFS_FILENAME);
	}

	/**
	 * 指定されたファイルに設定ファイル拡張子を付加した抽象パスを取得する。
	 * @param file	拡張子を付加する抽象パス
	 * @return	拡張子が付加された抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public VirtualFile getPrefsFile(VirtualFile file) {
		return file.getFactory().newFile(Files.addExtension(file.getPath(), EXT_FILE_PREFS));
	}

	/**
	 * 指定されたファイルの親を辿り、最上位に存在するプロジェクト設定ファイルへの
	 * 抽象パスを返す。このメソッドは、<em>rootFile</em> までパスを辿る。
	 * <em>rootFile</em> が <tt>null</tt> の場合や、<em>file</em> の上位に <em>rootFile</em> が
	 * 存在しない場合は、<em>file</em> の親がなくなるまでパスを辿る。
	 * @param file		判定する抽象パス
	 * @param rootFile	上限とする抽象パス
	 * @return	最上位に存在するプロジェクト設定ファイルへの抽象パスを返す。
	 * 			プロジェクト設定ファイルが見つからない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 */
	static public VirtualFile getTopProjectPrefsFile(VirtualFile file, VirtualFile rootFile) {
		return getTopPrefsFile(PROJECT_PREFS_FILENAME, file, rootFile);
	}
	
	/**
	 * 指定されたファイルの親を辿り、最上位に存在するモジュールパッケージ設定ファイルへの
	 * 抽象パスを返す。このメソッドは、<em>rootFile</em> までパスを辿る。
	 * <em>rootFile</em> が <tt>null</tt> の場合や、<em>file</em> の上位に <em>rootFile</em> が
	 * 存在しない場合は、<em>file</em> の親がなくなるまでパスを辿る。
	 * @param file		判定する抽象パス
	 * @param rootFile	上限とする抽象パス
	 * @return	最上位に存在するモジュールパッケージ設定ファイルへの抽象パスを返す。
	 * 			モジュールパッケージ設定ファイルが見つからない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 */
	static public VirtualFile getTopModulePackagePrefsFile(VirtualFile file, VirtualFile rootFile) {
		return getTopPrefsFile(PACK_PREFS_FILENAME, file, rootFile);
	}
	
	/**
	 * 指定されたファイルの親を辿り、最上位に存在する設定ファイルへの
	 * 抽象パスを返す。このメソッドは、<em>rootFile</em> までパスを辿る。
	 * <em>rootFile</em> が <tt>null</tt> の場合や、<em>file</em> の上位に <em>rootFile</em> が
	 * 存在しない場合は、<em>file</em> の親がなくなるまでパスを辿る。
	 * @param prefsFilename	判定するパスを含まない完全設定ファイル名
	 * @param file		判定する抽象パス
	 * @param rootFile	上限とする抽象パス
	 * @return	最上位に存在する設定ファイルへの抽象パスを返す。
	 * 			設定ファイルが見つからない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>prefsFilename</em> が有効な文字列ではない場合
	 */
	static protected VirtualFile getTopPrefsFile(String prefsFilename, VirtualFile file, VirtualFile rootFile) {
		if (Strings.isNullOrEmpty(prefsFilename))
			throw new IllegalArgumentException("'prefsFilename' argument is null or empty.");
		VirtualFile foundPrefs = null;
		VirtualFile parent = (file.isDirectory() ? file : file.getParentFile());
		do {
			VirtualFile prefs = parent.getChildFile(prefsFilename);
			if (prefs != null && prefs.exists() && prefs.isFile()) {
				foundPrefs = prefs;
			}
			if (parent.equals(rootFile)) {
				break;
			}
		} while ((parent = parent.getParentFile()) != null);
		return foundPrefs;
	}

	/**
	 * 指定されたモジュールアイテムを、アプリケーション共通の URI に変換する。
	 * このメソッドが返す URI は、このマネージャの により、<code>VirtualFile</code> オブジェクトに
	 * 復元できる。
	 * @param file	URI に変換する <code>VirtualFile</code> オブジェクト
	 * @return 変換できた場合は、その URI を返す。変換できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<code>file</code> が <tt>null</tt> のとき
	 */
	static public URI toURI(VirtualFile file) {
		return file.toURI();
	}

	/**
	 * 指定された URI から、<code>VirtualFile</code> オブジェクトを生成する。
	 * @param uri	<code>VirtualFile</code> を生成する URI
	 * @return	生成できた場合は、その <code>VirtualFile</code> オブジェクトを返す。
	 * 			サポートされていない URI の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<code>uri</code> が <tt>null</tt> のとき
	 */
	static public VirtualFile fromURI(URI uri) {
		return getInstance()._fileManager.fromURI(uri);
	}
	
	/**
	 * 指定された <code>File</code> オブジェクトから、<code>VirtualFile</code> オブジェクトを生成する。
	 * このメソッドは、ローカルファイルシステム用のオブジェクトを返す。
	 * @param file	<code>File</code> オブジェクト
	 * @return	生成されたオブジェクト
	 * @throws NullPointerException	<code>file</code> が <tt>null</tt> のとき
	 */
	static public VirtualFile fromJavaFile(File file) {
		return new DefaultFile(file);
	}

	/**
	 * 指定されたオブジェクトから、JAVA 標準の <code>File</code> オブジェクトを取得する。
	 * <em>file</em> が <code>File</code> オブジェクトなら、そのまま返す。
	 * <em>file</em> が <code>DefaultFile</code> オブジェクトなら、取得した <code>File</code> オブジェクトを返す。
	 * <code>File</code> オブジェクトが取得不可能な場合は <tt>null</tt> を返す。 
	 * @param file	対象のオブジェクト
	 * @return	取得した <code>File</code> オブジェクトを返す。取得できない場合は <tt>null</tt> を返す。
	 * @since 1.17
	 */
	static public File toJavaFile(Object file) {
		if (file instanceof File) {
			return (File)file;
		}
		else if (file instanceof DefaultFile) {
			return ((DefaultFile)file).getJavaFile();
		}
		else {
			return null;
		}
	}

	/**
	 * 指定された URI がこのマネージャでサポートされているかを判定する。
	 * サポートされている URI の場合は、{@link #fromURI(URI)} により
	 * <code>VirtualFile</code> を生成することが可能となる。
	 * @param uri	検証する URI
	 * @return	サポートされている場合に <tt>true</tt> を返す。
	 * @throws NullPointerException	<code>uri</code> が <tt>null</tt> のとき
	 */
	static public boolean isSupported(URI uri) {
		return getInstance()._fileManager.isSupportedURI(uri);
	}
	
	static public Icon getJavaFileSystemDisplayIcon() {
		return CommonResources.ICON_DISKUNIT;
	}

	/**
	 * 指定されたファイルオブジェクトのファイルシステムを表すアイコンを返す。
	 * アイコンが設定されていない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 */
	static public Icon getSystemDisplayIcon(VirtualFile file) {
		Icon icon = (Icon)getInstance()._fileManager.getFactoryUserObject(file.getFactory());
		return icon;
	}
	
	/**
	 * 指定されたファイル名の拡張子やパラメータから、表示用アイコンを取得する。
	 * アイコンが設定されていない場合は <tt>null</tt> を返す。
	 * @param filename		対象のファイル名
	 * @param isProjectRoot	プロジェクトのルートディレクトリの場合は <tt>true</tt> を指定する。
	 * @param isModulePackageRoot	モジュールパッケージのルートディレクトリの場合は <tt>true</tt> を指定する。
	 * @param isDirectory	ディレクトリの場合は <tt>true</tt> を指定する。
	 * @param isFile		ファイルの場合は <tt>true</tt> を指定する。
	 * @param expanded		展開アイコンを要求する場合は <tt>true</tt>、
	 * 						収縮アイコンを要求する場合は <tt>false</tt> を指定する。
	 * 						このパラメータを指定しても同じアイコンを返す場合もある。
	 * @param withMark		マークつきアイコンを要求する場合は <tt>true</tt>、
	 * 						マークなしアイコンを要求する場合は <tt>false</tt> を指定する。		
	 * @return	アイコンが設定されている場合は <code>Icon</code> オブジェクトを返す。
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<code>item</code> が <tt>null</tt> のとき
	 */
	static public Icon getDisplayIcon(String filename,
										boolean isProjectRoot, boolean isModulePackageRoot,
										boolean isDirectory, boolean isFile,
										boolean expanded, boolean withMark)
	{
		// Directory
		if (isDirectory) {
			// for Project
			if (isProjectRoot) {
				if (withMark) {
					if (expanded)
						return CommonResources.ICON_MARK_OPENPROJECT;
					else
						return CommonResources.ICON_MARK_PROJECT;
				} else {
					if (expanded)
						return CommonResources.ICON_OPENPROJECT;
					else
						return CommonResources.ICON_PROJECT;
				}
			}
			
			// for ModulePackage
			if (isModulePackageRoot) {
				if (withMark) {
					if (expanded)
						return CommonResources.ICON_MARK_PACK_OPEN;
					else
						return CommonResources.ICON_MARK_PACK_CLOSE;
				} else {
					if (expanded)
						return CommonResources.ICON_PACK_OPEN;
					else
						return CommonResources.ICON_PACK_CLOSE;
				}
			}
			
			// for normal Directory
			if (withMark) {
				if (expanded)
					return CommonResources.ICON_MARK_DIR_OPEN;
				else
					return CommonResources.ICON_MARK_DIR_CLOSE;
			} else {
				if (expanded)
					return CommonResources.ICON_DIR_OPEN;
				else
					return CommonResources.ICON_DIR_CLOSE;
			}
		}

		// File
		if (isFile) {
			// for JAR module
			if (Strings.endsWithIgnoreCase(filename, EXT_JARMODULE)) {
				//--- expanded は無視
				if (withMark)
					return CommonResources.ICON_MARK_JARMODULE;
				else
					return CommonResources.ICON_JARMODULE;
			}
			
			// for AADL source
			if (Strings.endsWithIgnoreCase(filename, EXT_AADL_SRC)) {
				//--- expanded は無視
				if (withMark)
					return CommonResources.ICON_MARK_AADLSRCFILE;
				else
					return CommonResources.ICON_AADLSRCFILE;
			}
			
			// for AADL macro
			if (Strings.endsWithIgnoreCase(filename, EXT_AADL_MACRO)) {
				//--- expanded は無視
				if (withMark)
					return CommonResources.ICON_MARK_MACROFILE;
				else
					return CommonResources.ICON_MACROFILE;
			}
			
			// for CSV
			if (Strings.endsWithIgnoreCase(filename, EXT_FILE_CSV)) {
				//--- expanded は無視
				if (withMark)
					return CommonResources.ICON_MARK_CSVFILE;
				else
					return CommonResources.ICON_CSVFILE;
			}
			
			// for XML
			if (Strings.endsWithIgnoreCase(filename, EXT_FILE_XML)) {
				//--- expanded は無視
				if (withMark)
					return CommonResources.ICON_MARK_XMLFILE;
				else
					return CommonResources.ICON_XMLFILE;
			}
			
			// for .prefs
			if (Strings.endsWithIgnoreCase(filename, EXT_FILE_PREFS)) {
				//--- expanded は無視
				if (withMark)
					return CommonResources.ICON_MARK_FILE;
				else
					return CommonResources.ICON_FILE;
			}
			
			// for normal File
			//--- expanded は無視
			if (withMark)
				return CommonResources.ICON_MARK_FILE;
			else
				return CommonResources.ICON_FILE;
		}
		
		// no Icon
		return null;
	}
	
	static public void appendDisplayJarFilePropertyBlock(StringBuilder sb, VirtualFile file, AadlJarProfile profile) {
		sb.append("<dl>");
		appendLastModifiedPropertyBlock(sb, file.lastModified());
		if (profile != null) {
			String strCompilerVer = profile.getCompilerVersion();
			//--- main-class
			appendJarMainClassPropertyBlock(sb, profile.getMainClass());
			//--- properties
			if (profile.hasProperties()) {
				//--- title
				appendTitlePropertyBlock(sb, profile.getTitle());
				//--- description
				appendDescriptionPropertyBlock(sb, profile.getDescription());
				//--- argument details
				appendArgumentDetailsPropertyBlock(sb, profile.getArgumentDetails());
				//--- note
				appendNotePropertyBlock(sb, profile.getNote());
				//--- revision(現在は表示しない)
				//appendJarRevisionPropertyBlock(sb, profile.getRevision());
				//--- Compiler version
				appendJarCompilerVersionPropertyBlock(sb, strCompilerVer);
			}
			else if (strCompilerVer != null) {
				//--- Compiler version
				appendJarCompilerVersionPropertyBlock(sb, strCompilerVer);
			}
		}
		sb.append("</dl>");
	}
	
	static public void appendDisplayFilePropertyBlock(StringBuilder sb, VirtualFile file, AbstractSettings settings) {
		sb.append("<dl>");
		appendLastModifiedPropertyBlock(sb, file.lastModified());
		if (settings instanceof ModuleSettings) {
			ModuleSettings ms = (ModuleSettings)settings;
			//--- title
			appendTitlePropertyBlock(sb, ms.getTitle());
			//--- description
			appendDescriptionPropertyBlock(sb, ms.getDescription());
			//--- argument details
			appendArgumentDetailsPropertyBlock(sb, ms.getArgumentDetails());
			//--- note
			appendNotePropertyBlock(sb, ms.getNote());
		}
		else if (settings instanceof PackageSettings) {
			PackageSettings ps = (PackageSettings)settings;
			//--- title
			appendTitlePropertyBlock(sb, ps.getTitle());
			//--- description
			appendDescriptionPropertyBlock(sb, ps.getDescription());
			//--- main module
			VirtualFile vfMain = ps.getMainModuleFile();
			if (vfMain == null) {
				appendMainModulePropertyBlock(sb, null);
			} else {
				appendMainModulePropertyBlock(sb, vfMain.relativePathFrom(ps.getVirtualPropertyFile().getParentFile(), Files.CommonSeparatorChar));
			}
		}
		sb.append("</dl>");
	}
	
	/**
	 * 指定されたファイルに関連付けられる設定情報をファイルから読み込み、
	 * その設定情報オブジェクトを返す。このメソッドが取得する設定情報ファイルは
	 * 次の通り。
	 * <ul>
	 * <li>AADLソースファイル</li>
	 * <li>AADLマクロファイル</li>
	 * <li>JARモジュールファイル</li>
	 * </ul>
	 * <p>このメソッドは、設定情報ファイルが存在しなくても、
	 * 設定情報を取得可能なファイルやディレクトリの場合は、新規の
	 * 設定情報オブジェクトを返す。
	 * @param file	設定情報を取得する対象となる抽象パス
	 * @return	設定情報オブジェクトを返す。設定情報オブジェクトが
	 * 			取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public AbstractSettings getModuleFileSettings(VirtualFile file) {
		//--- ファイル拡張子で判定
		if (ModuleFileManager.isAadlSourceFile(file)) {
			VirtualFile fPrefs = ModuleFileManager.addPrefsExtension(file);
			CompileSettings setting = new CompileSettings();
			setting.loadForTarget(fPrefs);
			return setting;
		}
		else if (ModuleFileManager.isAadlMacroFile(file)) {
			VirtualFile fPrefs = ModuleFileManager.addPrefsExtension(file);
			MacroExecSettings setting = new MacroExecSettings();
			setting.loadForTarget(fPrefs);
			return setting;
		}
		else if (ModuleFileManager.isJarFile(file)) {
			VirtualFile fAadlSrc = ModuleFileManager.replaceExtensionToAadlSource(file);
			if (fAadlSrc.exists()) {
				VirtualFile fPrefs = ModuleFileManager.addPrefsExtension(fAadlSrc);
				CompileSettings setting = new CompileSettings();
				setting.loadForTarget(fPrefs);
				return setting;
			}
		}
		
		// 該当なし
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private String encodeHtmlFromString(String value) {
		if (value==null)
			return "";
		
		StringBuilder sb = new StringBuilder();
		int len = value.length();
		char c;
		boolean skipLF = false;
		for (int i = 0; i < len; i++) {
			c = value.charAt(i);
			
			if (skipLF) {
				skipLF = false;
				if (c == '\n') {
					continue;
				} else {
					sb.append("<br>");
				}
			}
			
			switch (c) {
				case ' ' :
					sb.append("&nbsp;");
					break;
				case '&' :
					sb.append("&amp;");
					break;
				case '"' :
					sb.append("&quot;");
					break;
				case '<' :
					sb.append("&lt;");
					break;
				case '>' :
					sb.append("&gt;");
					break;
				case '\n' :
					sb.append("<br>");
					break;
				case '\r' :
					skipLF = true;
					break;
				case '\t' :
					sb.append("&nbsp;");
					break;
				case 0x0B :
					sb.append("<br>");
					break;
				default :
					sb.append(c);
			}
		}
		
		if (skipLF) {
			sb.append("<br>");
		}
		
		return sb.toString();
	}
	
	static private void appendDetailListElement(StringBuilder sb, String title, String description) {
		sb.append("<dt><b>");
		sb.append(encodeHtmlFromString(title));
		sb.append("</b></dt><dd>");
		sb.append(encodeHtmlFromString(description));
		sb.append("</dd>");
	}
	
	static private void appendLastModifiedPropertyBlock(StringBuilder sb, long lastModified) {
		DateFormat   frmDate = DateFormat.getDateTimeInstance();
		Date dtModified = new Date(lastModified);
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_lastmodified,
								frmDate.format(dtModified));
	}
	
	static private void appendTitlePropertyBlock(StringBuilder sb, String title) {
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_title,
								title);
	}
	
	static private void appendDescriptionPropertyBlock(StringBuilder sb, String description) {
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_description,
								description);
	}
	
	static private void appendNotePropertyBlock(StringBuilder sb, String note) {
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_note,
								note);
	}
	
	static private void appendMainModulePropertyBlock(StringBuilder sb, String pathname) {
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_MainModule,
								pathname);
	}
	
	static private void appendJarMainClassPropertyBlock(StringBuilder sb, String mainclass) {
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_MainClass,
								mainclass);
	}
	
	static private void appendJarCompilerVersionPropertyBlock(StringBuilder sb, String compilerVer) {
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_compiler,
								compilerVer);
	}

	/*--- 現時点では使用しない
	static private void appendJarRevisionPropertyBlock(StringBuilder sb, int revision) {
		appendDetailListElement(sb, CommonMessages.getInstance().ModuleInfoLabel_revision,
								Integer.toString(revision));
	}
	---*/
	
	static private void appendArgumentDetailsPropertyBlock(StringBuilder sb, ModuleArgDetail[] details) {
		sb.append("<dt><b>");
		sb.append(CommonMessages.getInstance().ModuleInfoLabel_arguments);
		sb.append("</b></dt><dd>");
		sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">");
		sb.append("<tr><th>&nbsp;</th><th>");
		sb.append(CommonMessages.getInstance().ModuleInfoLabel_args_attr);
		sb.append("</th><th>");
		sb.append(CommonMessages.getInstance().ModuleInfoLabel_args_desc);
		sb.append("</th></tr>");
		if (details != null && details.length > 0) {
			ModuleArgDetail arg;
			String argType;
			String argDesc;
			for (int i = 0; i < details.length; i++) {
				arg = details[i];
				argType = (arg.type()==null ? null : arg.type().typeName());
				argDesc = arg.description();
				sb.append("<tr><th>$");
				sb.append(Integer.toString(i+1));
				sb.append("</th><td>");
				sb.append(Strings.isNullOrEmpty(argType) ? "&nbsp;" : encodeHtmlFromString(argType));
				sb.append("</td><td>");
				sb.append(Strings.isNullOrEmpty(argDesc) ? "&nbsp;" : encodeHtmlFromString(argDesc));
				sb.append("</td></tr>");
			}
		}
		sb.append("</table>");
		sb.append("</dd>");
	}
}
