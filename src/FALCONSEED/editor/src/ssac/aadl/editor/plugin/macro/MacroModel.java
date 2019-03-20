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
 * @(#)MacroModel.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroModel.java	1.17	2011/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroModel.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroModel.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroModel.java	1.10	2009/01/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.macro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.build.ClassPaths;
import ssac.aadl.editor.build.ExecutorFactory;
import ssac.aadl.editor.document.IEditorTableDocument;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.JEncodingComboBox;
import ssac.aadl.macro.AADLMacroEngine;
import ssac.aadl.macro.command.CommandActionNode;
import ssac.aadl.macro.command.CommandParser;
import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.command.RecognitionException;
import ssac.aadl.macro.file.CsvFormatException;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.setting.MacroExecSettings;
import ssac.util.Classes;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.CsvReader;
import ssac.util.io.CsvWriter;
import ssac.util.io.Files;
import ssac.util.process.CommandExecutor;
import ssac.util.swing.table.SpreadSheetModel;

/**
 * AADLマクロのテーブルモデル。
 * 
 * @version 3.1.0	2014/05/12
 *
 * @since 1.10
 */
public class MacroModel extends SpreadSheetModel implements IEditorTableDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/**
	 * テーブルの初期容量(行)
	 */
	static private final int INITIAL_ROW_CAPACITY = 256;
	
	/**
	 * 最大カラム数
	 */
	static private final int maxColumnCount = 99*2+7;
	/**
	 * このモデルの最小行数
	 */
	static private final int MINIMUM_ROW_COUNT = 256;
	/**
	 * セルの無効なデータ
	 */
	static private String EMPTY_CELL_TEXT = "";
	
//	static public final String ARGATTR_IN  = "[IN]";
//	static public final String ARGATTR_OUT = "[OUT]";
//	static public final String ARGATTR_STR = "[STR]";
	static public final String ARGATTR_IN  = ModuleArgType.IN.toString();
	static public final String ARGATTR_OUT = ModuleArgType.OUT.toString();
	static public final String ARGATTR_STR = ModuleArgType.STR.toString();
	static public final String ARGATTR_PUB = ModuleArgType.PUB.toString();
	static public final String ARGATTR_SUB = ModuleArgType.SUB.toString();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * カラム名定義
	 */
	static private ArrayList<Object> defColumnNames = null;

	/**
	 * このドキュメントのマネージャインスタンス
	 */
	private final MacroComponentManager manager;
	/** マクロ実行設定情報 **/
	private final MacroExecSettings settings = new MacroExecSettings();

	/**
	 * このドキュメントが新規ドキュメントであることを示すフラグ
	 */
	private boolean flgNew;
	/**
	 * ドキュメント変更状態を示すフラグ
	 */
	private boolean flgModified;
	/**
	 * ソースドキュメントの対象ファイル
	 */
	private File targetFile;
	/**
	 * ドキュメント対象ファイルのロード時の最終更新日時
	 * @since 1.17
	 */
	private long	_lastModifiedWhenLoadingTargetFile = 0L;
	/**
	 * 最後に適用したファイルエンコーディング
	 */
	private String	_lastEncoding = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準的な新しいインスタンスを生成する。
	 */
	private MacroModel(final MacroComponentManager manager) {
		super(INITIAL_ROW_CAPACITY);
		this.manager = Validations.validNotNull(manager, "'manager' is null.");
		this._lastEncoding = manager.getFileEncoding();
		this.setColumnIdentifiers(getDefaultColumnNames(), maxColumnCount);
	}
	
	/**
	 * このマクロモデルの新しいインスタンスを生成する。
	 * このコンストラクタは、指定されたファイルを保存先とする、
	 * 新規ドキュメントを生成する。指定されたファイルが存在していても、
	 * ファイルから読み込まない。
	 * ここで生成される新規ドキュメントは、<em>templateData</em> が有効な文字列の場合、
	 * CSVフォーマットの文字列として読み込み、このモデルの初期データとする。
	 * @param manager	このドキュメントが所属するコンポーネントマネージャ
	 * @param templateData	新規ドキュメントの初期データとするCSVフォーマットの文字列
	 * @param targetFile	新規ドキュメントの保存先ファイル
	 * @throws NullPointerException <code>manager</code> もしくは <code>targetFile</code> が <tt>null</tt> の場合
	 * @throws IOException	<em>templateData</em> のフォーマットが正しくない場合
	 * @since 1.14
	 */
	public MacroModel(final MacroComponentManager manager, String templateData, File targetFile)
	throws IOException
	{
		this(manager);
		this.targetFile = Validations.validNotNull(targetFile, "'targetFile' is null.");
		updateLastModifiedTimeWhenLoadingTargetFile();
		this.settings.loadForTarget(targetFile);
		if (Strings.isNullOrEmpty(templateData)) {
			setupNewModel();
		} else {
			setupModelFromString(templateData);
		}
		this.flgNew = true;
		setModifiedFlag(true);
	}

	/**
	 * このマクロモデルの新しいインスタンスを生成する。
	 * このコンストラクタは、指定されたファイルを読み込みモデルを構築する。
	 * @param manager	このドキュメントが所属するコンポーネントマネージャ
	 * @param targetFile	ドキュメントの内容を読み出すファイル
	 * @throws NullPointerException <code>manager</code> もしくは <code>targetFile</code> が <tt>null</tt> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public MacroModel(final MacroComponentManager manager, File targetFile)
	throws IOException
	{
		this(manager);
		this.targetFile = Validations.validNotNull(targetFile, "'targetFile' is null.");
		updateLastModifiedTimeWhenLoadingTargetFile();
		this.settings.loadForTarget(targetFile);
		setupModelFromFile(targetFile, manager.getFileEncoding());
		this.flgNew = false;
		setModifiedFlag(false);
	}

	/**
	 * このマクロモデルの新しいインスタンスを生成する。
	 * このコンストラクタは、指定されたファイルを読み込みモデルを構築する。
	 * 
	 * @param manager	このドキュメントが所属するコンポーネントマネージャ
	 * @param targetFile	ドキュメントの内容を読み出すファイル
	 * @param csvEncoding	ファイル読み込み時のテキストファイルエンコーディング名を指定する。
	 * 						<tt>null</tt> の場合は、デフォルトのエンコーディングで読み込む。
	 * @throws NullPointerException <code>manager</code> もしくは <code>targetFile</code> が <tt>null</tt> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public MacroModel(final MacroComponentManager manager,
			File targetFile, String csvEncoding)
	throws IOException
	{
		this(manager);
		this.targetFile = Validations.validNotNull(targetFile, "'targetFile' is null.");
		updateLastModifiedTimeWhenLoadingTargetFile();
		this.settings.loadForTarget(targetFile);
		
		setupModelFromFile(targetFile, csvEncoding);
		this.flgNew = false;
		setModifiedFlag(false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された列インデックスが、マクロコマンド列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return マクロコマンド列の列インデックスなら <tt>true</tt>
	 */
	public boolean isCommandColumn(int columnIndex) {
		return (columnIndex == CsvMacroFiles.FIELD_COMMAND);
	}

	/**
	 * 指定された列インデックスが、プロセス名列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	プロセス名列の列インデックスなら <tt>true</tt>
	 */
	public boolean isProcessNameColumn(int columnIndex) {
		return (columnIndex == CsvMacroFiles.FIELD_PROCNAME);
	}

	/**
	 * 指定された列インデックスが、コメント列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	コメント列の列インデックスなら <tt>true</tt>
	 */
	public boolean isCommentColumn(int columnIndex) {
		return (columnIndex == CsvMacroFiles.FIELD_COMMENT);
	}

	/**
	 * 指定された列インデックスが、実行モジュール名列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	実行モジュール名列の列インデックスなら <tt>true</tt>
	 */
	public boolean isModuleNameColumn(int columnIndex) {
		return (columnIndex == CsvMacroFiles.FIELD_JARNAME);
	}

	/**
	 * 指定された列インデックスが、クラスパス列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	クラスパス列の列インデックスなら <tt>true</tt>
	 */
	public boolean isClassPathColumn(int columnIndex) {
		return (columnIndex == CsvMacroFiles.FIELD_CLASSPATH);
	}

	/**
	 * 指定された列インデックスが、クラス名列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	クラスパス列の列インデックスなら <tt>true</tt>
	 */
	public boolean isMainClassColumn(int columnIndex) {
		return (columnIndex == CsvMacroFiles.FIELD_MAINCLASS);
	}

	/**
	 * 指定された列インデックスが、Javaパラメータ列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	Javaパラメータ列の列インデックスなら <tt>true</tt>
	 */
	public boolean isJavaParametersColumn(int columnIndex) {
		return (columnIndex == CsvMacroFiles.FIELD_PARAMETERS);
	}

	/**
	 * 指定された列インデックスが、引数(属性ならびに値)列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	引数列の列インデックスなら <tt>true</tt>
	 */
	public boolean isArgumentsColumn(int columnIndex) {
		return (columnIndex >= CsvMacroFiles.FIELD_ARG_TYPE1);
	}

	/**
	 * 指定された列インデックスが、引数属性列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	引数属性列の列インデックスなら <tt>true</tt>
	 */
	public boolean isArgumentTypeColumn(int columnIndex) {
		if (columnIndex >= CsvMacroFiles.FIELD_ARG_TYPE1) {
			return (((columnIndex - CsvMacroFiles.FIELD_ARG_TYPE1) % 2) == 0);
		}
		return false;
	}

	/**
	 * 指定された列インデックスが、引数値列であるかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	引数値列の列インデックスなら <tt>true</tt>
	 */
	public boolean isArgumentValueColumn(int columnIndex) {
		if (columnIndex >= CsvMacroFiles.FIELD_ARG_TYPE1) {
			return (((columnIndex - CsvMacroFiles.FIELD_ARG_TYPE1) % 2) != 0);
		}
		return false;
	}

	/**
	 * 指定された列インデックスが、ファイル名を記述する列であるかを判定する。
	 * ファイル名を記述する列は、実行モジュール名列と引数値列の場合とする。
	 * @param columnIndex	判定する列インデックス
	 * @return	ファイル名を記述する列の列インデックスなら <tt>true</tt>
	 */
	public boolean isFileNameColumn(int columnIndex) {
		if (isModuleNameColumn(columnIndex)) {
			return true;
		}
		else if (isArgumentValueColumn(columnIndex)) {
			return true;
		}
		
		return false;
	}

	@Override
	public void fireTableCellUpdated(int rowIndex, int columnIndex) {
		if (columnIndex == CsvMacroFiles.FIELD_COMMAND) {
			// update row for color
			super.fireTableRowsUpdated(rowIndex, rowIndex);
		} else if (columnIndex >= CsvMacroFiles.FIELD_ARG_TYPE1 && ((columnIndex-CsvMacroFiles.FIELD_ARG_TYPE1)%2)==0) {
			// update argument type for back color
			super.fireTableRowsUpdated(rowIndex, rowIndex);
		} else {
			super.fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	/**
	 * 現在の行数が最小行数を下回る場合のみ、行数を標準行数に合わせる。
	 */
	public void adjustRowCount() {
		adjustMinimumRows(MINIMUM_ROW_COUNT);
	}

	//------------------------------------------------------------
	// Implement IEditorDocument interfaces
	//------------------------------------------------------------
	/**
	 * 表示に関するリソースを開放する。
	 * このドキュメントがコンパイルもしくは実行可能なドキュメントの場合、
	 * このメソッドの実行によってコンパイルもしくは実行に影響があってはならない。
	 * @since 1.16
	 */
	public void releaseViewResources() {
		// No implement.
	}
	
	public String getTitle() {
		return targetFile.getName();
	}
	
	public boolean hasTargetFile() {
		return (targetFile != null);
	}
	
	public File getTargetFile() {
		return targetFile;
	}
	
	/**
	 * このドキュメントの保存先を、指定されたパスに設定する。
	 * このメソッドでは、保存先の正当性検証は行わず、ターゲットを指定されたパスに
	 * 設定するのみとなる。編集状態なども変更されない。
	 * @param newTarget	新しい保存先となる抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.14
	 */
	public void setTargetFile(File newTarget) {
		this.targetFile = Validations.validNotNull(newTarget);
		updateLastModifiedTimeWhenLoadingTargetFile();
		this.settings.setPropertyTarget(newTarget);
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	ドキュメントが読み込まれた時点での、最終更新日時
	 * @since 1.17
	 */
	public long lastModifiedTimeWhenLoadingTargetFile() {
		return _lastModifiedWhenLoadingTargetFile;
	}
	
	/**
	 * このドキュメントの保存先ファイルの、現在の最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	最終更新日時
	 * @since 1.17
	 */
	public long lastModifiedTimeWhenCurrentTargetFile() {
		if (hasTargetFile()) {
			return getTargetFile().lastModified();
		} else {
			return 0L;
		}
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を、
	 * 保存先ファイルの現在の更新日時で更新する。
	 * @since 1.17
	 */
	public void updateLastModifiedTimeWhenLoadingTargetFile() {
		if (hasTargetFile()) {
			_lastModifiedWhenLoadingTargetFile = getTargetFile().lastModified();
		}
	}
	
	public MacroComponentManager getManager() {
		return manager;
	}
	
	public boolean isNewDocument() {
		return flgNew;
	}
	
	public boolean isModified() {
		return flgModified;
	}
	
	public boolean canMoveTargetFile() {
		// ターゲットファイルが指定されていなければ、移動不可
		if (hasTargetFile()) {
			return false;
		}
		
		// 編集中のドキュメントなら、移動不可
		if (isModified()) {
			return false;
		}
		
		// 移動を許可
		return true;
	}
	
	/**
	 * 現在のドキュメントに関連付けられたファイルのエンコーディング名を返す。
	 * このメソッドが返すエンコーディング名は、読み込み時もしくは保存時に
	 * 適用されたものとなる。
	 * @return	エンコーディング名(<tt>null</tt> 以外)
	 */
	public String getLastEncodingName() {
		if (_lastEncoding == null) {
			return JEncodingComboBox.getDefaultEncoding();
		} else {
			return _lastEncoding;
		}
	}
	
	public void setModifiedFlag(boolean modified) {
		this.flgModified = modified;
	}
	
	/**
	 * このドキュメントに関連付けられている設定情報を、最新の情報に更新する。
	 * @return	最新の情報に更新された場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean refreshSettings() {
		if (settings != null) {
			return settings.refresh();
		} else {
			return false;
		}
	}
	
	/**
	 * マクロ実行設定情報のインスタンスを返す。
	 * @return	<code>MacroExecSettings</code> のインスタンス(<tt>null</tt> 以外)
	 */
	public MacroExecSettings getSettings() {
		return settings;
	}
	
	/**
	 * このドキュメントにおける実行可能ファイルの有無を判定する。
	 * @return	ドキュメントにおける実行可能ファイルが存在している場合は <tt>true</tt>
	 */
	public boolean hasExecutableFile() {
		return (targetFile != null && targetFile.exists());
	}
	
	/**
	 * このドキュメントにおける実行可能ファイルを取得する。
	 * このメソッドがファイルを返した場合、そのファイルは必ず存在している。
	 * @return	実行可能ファイルが存在していればそのファイルオブジェクトを返す。
	 * 			それ以外の場合は <tt>null</tt> を返す。
	 */
	public File getExecutableFile() {
		if (hasExecutableFile())
			return targetFile;
		else
			return null;
	}
	
	public boolean isCompilable() {
		return false;
	}
	
	public boolean isExecutable() {
		return true;
	}
	
	public CommandExecutor createCompileExecutor() {
		return null;
	}
	
	public CommandExecutor createModuleExecutor() {
		// check exist target
		File targetFile = getTargetFile();
		if (targetFile == null)
			throw new IllegalArgumentException("Target file is nothing.");
		if (!targetFile.exists())
			throw new IllegalArgumentException("Target file is not found.\n[File] " + targetFile.getAbsolutePath());
		if (!targetFile.isFile())
			throw new IllegalArgumentException("Target is not file.\n[File] " + targetFile.getAbsolutePath());

		// create command
		Vector<String> cmdList = new Vector<String>();
		//--- java command
		ExecutorFactory.addJavaCommandPath(cmdList,
				AppSettings.getInstance().getCurrentJavaCommandPath());
		//--- java VM arguments
		String maxMemorySize = AADLEditor.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(settings.getJavaVMArgs());
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			ExecutorFactory.addJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			ExecutorFactory.addJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Macro engine class-path
		ClassPaths pathList = new ClassPaths();
		File macroClass = Classes.getClassSource(AADLMacroEngine.class);
		pathList.addPath(macroClass.getAbsoluteFile());
		ExecutorFactory.addClassPath(cmdList, pathList);
		
		// Macro main class
		ExecutorFactory.addMainClassName(cmdList, AADLMacroEngine.class.getName());
		
		// Macro options
		//--- debug
		if (AADLEditor.isDebugEnabled()) {
			cmdList.add("-debug");
			cmdList.add("-verbose");
		}
		//--- encoding
		String encoding = getManager().getFileEncoding();
		if (!Strings.isNullOrEmpty(encoding)) {
			cmdList.add("-macroencoding");
			cmdList.add(encoding);
		}
		encoding = AppSettings.getInstance().getAadlCsvEncodingName();
		if (!Strings.isNullOrEmpty(encoding)) {
			cmdList.add("-csvencoding");
			cmdList.add(encoding);
		}
		encoding = AppSettings.getInstance().getAadlTxtEncodingName();
		if (!Strings.isNullOrEmpty(encoding)) {
			cmdList.add("-txtencoding");
			cmdList.add(encoding);
		}
		//--- heapmax
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			cmdList.add("-heapmax");
			cmdList.add(maxMemorySize);
		}
		//--- Class path setting for AADL module
		String[] libs = AppSettings.getInstance().getExecLibraries();
		if (libs != null && libs.length > 0) {
			StringBuilder sb = new StringBuilder(libs[0]);
			for (int i = 1; i < libs.length; i++) {
				sb.append(AADLMacroEngine.MACRO_CLASSPATH_SEPARATOR);
				sb.append(libs[i]);
			}
			cmdList.add("-libpath");
			cmdList.add(sb.toString());
		}
		//--- VM options
		if (!Strings.isNullOrEmpty(strVmArgs)) {
			cmdList.add("-javavm");
			cmdList.add(strVmArgs);
		}
		
		// Target macro file
		cmdList.add(targetFile.getAbsolutePath());
		
		// Module arguments
		String[] args = settings.getProgramArgs();
		if (args != null && args.length > 0) {
			for (String arg : args) {
				if (Strings.isNullOrEmpty(arg)) {
					cmdList.add("");
				} else {
					cmdList.add(arg);
				}
			}
		}
		
		// create Executor
		CommandExecutor executor = new CommandExecutor(cmdList);
		
		// Working directory
		executor.setWorkDirectory(targetFile.getAbsoluteFile().getParentFile());
		
		// completed
		return executor;
	}

	public void onCompileFinished() {
		// No implements.
	}
	
	public void save(File targetFile) throws IOException {
		Validations.validNotNull(targetFile);
		saveDocument(targetFile, manager.getFileEncoding());
		flgNew = false;
		setModifiedFlag(false);
	}

	/**
	 * 指定されたファイルのパスを、このドキュメントが格納される位置からの
	 * 相対パスに変換する。相対パスとはならない場合、絶対パスを返す。
	 * 
	 * @param file	変換するファイル
	 * @return	相対パスを示す文字列
	 */
	public String convertRelativePath(File file) {
		File parentFile = targetFile.getAbsoluteFile().getParentFile();
		String relPath = Files.convertAbsoluteToRelativePath(parentFile, file, '/');	// プラットフォーム依存をなくす
		if (relPath.length() < 1) {
			//--- 相対パスが空文字の場合、カレントを表す "." とする。
			relPath = ".";
		}
		return relPath;
	}

	/**
	 * 指定されたパス文字列を、このドキュメントが格納される位置からの
	 * 相対パスとみなし、絶対パスに変換する。
	 * 
	 * @param path	変換するパス文字列
	 * @return	変換後の絶対パスを示すファイル
	 */
	public File convertAbsolutePath(String path) {
		// 空文字列の場合は、このドキュメントそのものの位置を返す。
		if (Strings.isNullOrEmpty(path)) {
			return getTargetFile();
		}
		
		// 絶対パスの場合は、そのまま返す
		File file = new File(path);
		if (file.isAbsolute()) {
			return file;
		}
		
		// 絶対パスに変換する
		File parentFile = targetFile.getAbsoluteFile().getParentFile();
		file = new File(parentFile, path);
		return Files.normalizeFile(file);
	}

	//------------------------------------------------------------
	// Implement SpreadSheetModel interfaces
	//------------------------------------------------------------

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (rowIndex == 0) {
			// 第１行目は MagicWord とするため、編集不可
			return false;
		}
		else {
			// ２行目以降は編集可
			return true;
		}
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void addColumn(Object columnName, ColumnDataModel columnData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void addColumn(Object columnName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このクラスでは、常に文字列クラスを返す。
	 * 
	 * @param columnIndex	列インデックス
	 * 
	 * @return	マクロセルクラス
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return MacroCell.class;
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void setData(List<List<?>> dataList, Collection<?> columnNames) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void setData(Object[][] dataList, Collection<?> columnNames) {
		throw new UnsupportedOperationException();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private ArrayList<Object> getDefaultColumnNames() {
		if (defColumnNames == null) {
			defColumnNames = new ArrayList<Object>(maxColumnCount);
			defColumnNames.add(MacroMessages.getInstance().colNameCommand);
			defColumnNames.add(MacroMessages.getInstance().colNameProcName);
			defColumnNames.add(MacroMessages.getInstance().colNameComment);
			defColumnNames.add(MacroMessages.getInstance().colNameJarName);
			defColumnNames.add(MacroMessages.getInstance().colNameClassPath);
			defColumnNames.add(MacroMessages.getInstance().colNameMainClass);
			defColumnNames.add(MacroMessages.getInstance().colNameJavaParams);
			for (int i = 1; i < 100; i++) {
				defColumnNames.add("($"+i+") " + MacroMessages.getInstance().colNameArgAttr);
				defColumnNames.add("($"+i+") " + MacroMessages.getInstance().colNameArgValue);
			}
		}
		return defColumnNames;
	}
	
	private void setupNewModel() {
		//setRows(DEFAULT_ROW_COUNT);	// MagicWord と空行１を初期状態とする。
		setRows(MINIMUM_ROW_COUNT);	// MagicWord と空行１を初期状態とする。
		setValueAt(CsvMacroFiles.CsvMacroFileHeader, 0, 0);
	}

	/**
	 * 指定された <em>reader</em> で読み込まれたデータで、このモデルをセットアップする。
	 * @param reader	読み込みに使用する <code>CsvReader</code> オブジェクト
	 * @throws IOException	読み込みエラーが発生した場合
	 * @since 1.14
	 */
	private void setupModelByCsvReader(CsvReader reader) throws IOException
	{
		MacroElementModel rowData;
		
		// MagicWord 確認(第1行目、第1列が MagicWord かチェック)
		//****************************************************************
		// Excel が生成するCSVは、最大列数分カンマをパディングするので、
		// 先頭行第１列のみのチェックとする
		//****************************************************************
		CsvReader.CsvRecord rec = reader.readRecord();
		if (rec == null || !rec.hasFields() || !CsvMacroFiles.isCsvMacroFileHeader(rec.getField(0).getValue())) {
			// mismatched csv macro file header
			throw new CsvFormatException("Mismatched CSV header expecting \"" + CsvMacroFiles.CsvMacroFileHeader + "\".", 1, 1);
		}
		rowData = createRowDataModel(new Object[]{CsvMacroFiles.CsvMacroFileHeader});
		rowData.updateCommandAction();
		addRow(rowData);
		
		// 読み込み
		while ((rec = reader.readRecord()) != null) {
			int lim = Math.min(rec.getNumFields(), getColumnCount());
			rowData = null;
			if (lim > 0) {
				rowData = createRowDataModel(lim);
				for (int i = 0; i < lim; i++) {
					String value = rec.getField(i).getValue();
					MacroCell cell = new MacroCell(value);
					rowData.add(cell);
				}
				rowData.updateCommandAction();
			}
			addRow(rowData);
		}
	}

	/**
	 * <em>macroFile</em> を保存先とするドキュメントを生成する。
	 * ドキュメントは指定された文字列をCSVテキストとして読み込む。
	 * @param modelValue	ドキュメントデータ
	 * @since 1.14
	 */
	private void setupModelFromString(String modelValue) throws IOException
	{
		CsvReader reader = new CsvReader(new BufferedReader(new StringReader(modelValue)));
		
		try {
			setupModelByCsvReader(reader);
		}
		finally {
			reader.close();
		}

		//--- 行数の調整
		adjustRowCount();
	}
	
	private void setupModelFromFile(File macroFile, String csvEncoding) throws IOException
	{
		CsvReader reader;
		if (csvEncoding != null && csvEncoding.length() > 0) {
			this._lastEncoding = csvEncoding;
			reader = new CsvReader(macroFile, csvEncoding);
		} else {
			this._lastEncoding = JEncodingComboBox.getDefaultEncoding();
			reader = new CsvReader(macroFile);
		}

		try {
			//@@@ 2009/10/29 : Y.Ishizuka : modified @@@
			setupModelByCsvReader(reader);
			/*--- 2009/10/29 : old codes
			MacroElementModel rowData;
			
			// MagicWord 確認(第1行目、第1列が MagicWord かチェック)
			//****************************************************************
			// Excel が生成するCSVは、最大列数分カンマをパディングするので、
			// 先頭行第１列のみのチェックとする
			//****************************************************************
			CsvReader.CsvRecord rec = reader.readRecord();
			if (rec == null || !rec.hasFields() || !CsvMacroFiles.isCsvMacroFileHeader(rec.getField(0).getValue())) {
				// mismatched csv macro file header
				throw new CsvFormatException("Mismatched CSV header expecting \"" + CsvMacroFiles.CsvMacroFileHeader + "\".", 1, 1);
			}
			rowData = createRowDataModel(new Object[]{CsvMacroFiles.CsvMacroFileHeader});
			rowData.updateCommandAction();
			addRow(rowData);
			
			// 読み込み
			while ((rec = reader.readRecord()) != null) {
				int lim = Math.min(rec.getNumFields(), getColumnCount());
				rowData = null;
				if (lim > 0) {
					rowData = createRowDataModel(lim);
					for (int i = 0; i < lim; i++) {
						String value = rec.getField(i).getValue();
						MacroCell cell = new MacroCell(value);
						rowData.add(cell);
					}
					rowData.updateCommandAction();
				}
				addRow(rowData);
			}
			**--- 2009/10/29 : end of old codes */
			//@@@ 2009/10/29 : Y.Ishizuka : end of modified @@@
		}
		finally {
			reader.close();
		}

		//--- 行数の調整
		adjustRowCount();
	}

	private void saveDocument(File targetFile, String csvEncoding) throws IOException
	{
		File saveFile = (targetFile != null ? targetFile : this.targetFile);
		int maxRows = getValidRowCount();
		
		CsvWriter writer;
		if (csvEncoding != null && csvEncoding.length() > 0) {
			writer = new CsvWriter(saveFile, csvEncoding);
			this._lastEncoding = csvEncoding;
		} else {
			writer = new CsvWriter(saveFile);
			this._lastEncoding = JEncodingComboBox.getDefaultEncoding();
		}
		
		try {
			// MagicWord 書き込み(第１行目)
			//--- 読み込み時には、第１行目第１列の MagicWord のみをチェックし、
			//--- 内容は破棄しているため、書き出し時も同様
			writer.writeLine(CsvMacroFiles.CsvMacroFileHeader);
			
			// エントリ書き込み(２行目から)
			for (int row = 1; row < maxRows; row++) {
				int maxCols = getValidColumnCount(row);
				if (maxCols == 1) {
					Object value = getValueAt(row, 0);
					writer.writeLine(value == null ? "" : value.toString());
				}
				else if (maxCols > 0) {
					int fincol = maxCols - 1;
					Object value;
					for (int col = 0; col < fincol; col++) {
						value = getValueAt(row, col);
						writer.writeField(false, (value==null ? "" : value.toString()));
					}
					value = getValueAt(row, fincol);
					writer.writeField(true, (value==null ? "" : value.toString()));
				}
				else {
					// no entry in line
					writer.writeBlankLine();
				}
			}
		}
		finally {
			writer.close();
			if (Objects.isEqual(saveFile, this.targetFile)) {
				updateLastModifiedTimeWhenLoadingTargetFile();
			}
		}
		
		// 設定情報の永続化
		if (!Objects.isEqual(saveFile, this.targetFile)) {
			// 保存結果と設定内容が異なる場合のみ、設定情報を永続化
			this.targetFile = saveFile;
			updateLastModifiedTimeWhenLoadingTargetFile();
			this.settings.saveForTarget(saveFile);
		}
	}

	@Override
	protected boolean isValidValue(Object value) {
		if (value != null) {
			String strValue = value.toString();
			return (!Strings.isNullOrEmpty(strValue));
		} else {
			return false;
		}
	}

	@Override
	protected boolean storeElementAt(Object aValue, int rowIndex, int columnIndex) {
		boolean result;
		if (aValue instanceof MacroCell) {
			result = super.storeElementAt(aValue, rowIndex, columnIndex);
		} else {
			MacroCell cell = new MacroCell(aValue.toString());
			result = super.storeElementAt(cell, rowIndex, columnIndex);
		}
		if (result && columnIndex == CsvMacroFiles.FIELD_COMMAND) {
			MacroElementModel row = (MacroElementModel)getRow(rowIndex);
			row.updateCommandAction();
		}
		return result;
	}

	@Override
	protected MacroElementModel createRowDataModel(int initialCapacity) {
		return new MacroElementModel(initialCapacity);
	}
	
	@Override
	protected MacroElementModel createRowDataModel(Collection<? extends Object> c) {
		MacroElementModel row;
		if (c instanceof List) {
			List<? extends Object> list = (List<? extends Object>)c;
			//--- 実サイズを検証
			int upperBound = -1;
			for (int i = list.size() - 1; i >= 0; i--) {
				if (isValidValue(list.get(i))) {
					upperBound = i;
					break;
				}
			}
			//--- 必要なサイズの領域を生成する
			int cap = Math.max((upperBound+1), INITIAL_COL_CAPACITY);
			row = new MacroElementModel(cap);
			//--- 値を設定
			for (int i = 0; i <= upperBound; i++) {
				Object val = list.get(i);
				if (val == null) {
					row.add(null);
				}
				else if (val instanceof MacroCell) {
					row.add(val);
				}
				else {
					String str = val.toString();
					if (isValidValue(str))
						row.add(new MacroCell(str));
					else
						row.add(null);
				}
			}
		}
		else {
			//--- 実サイズを検証
			int upperBound = -1;
			int index = 0;
			for (Object val : c) {
				if (isValidValue(val)) {
					upperBound = index;
				}
				index++;
			}
			//--- 必要なサイズの領域を生成する
			int cap = Math.max((upperBound+1), INITIAL_COL_CAPACITY);
			row = new MacroElementModel(cap);
			//--- 値を設定
			index = 0;
			for (Object val : c) {
				if (val == null) {
					row.add(null);
				}
				else if (val instanceof MacroCell) {
					row.add(val);
				}
				else {
					String str = val.toString();
					if (isValidValue(str))
						row.add(new MacroCell(str));
					else
						row.add(null);
				}
			}
		}
		// 情報を更新
		row.updateCommandAction();
		return row;
	}

	@Override
	protected MacroElementModel createRowDataModel(Object[] c) {
		MacroElementModel row;
		if (c != null && c.length > 0) {
			//--- 実サイズを検証
			int upperBound = -1;
			for (int i = c.length-1; i >= 0; i--) {
				if (isValidValue(c[i])) {
					upperBound = i;
					break;
				}
			}
			//--- 必要なサイズの領域を生成する
			int cap = Math.max((upperBound+1), INITIAL_COL_CAPACITY);
			row = new MacroElementModel(cap);
			//--- 値を設定
			for (int i = 0; i <= upperBound; i++) {
				Object val = c[i];
				if (val == null) {
					row.add(null);
				}
				else if (val instanceof MacroCell) {
					row.add(val);
				}
				else {
					String str = val.toString();
					if (isValidValue(str))
						row.add(new MacroCell(str));
					else
						row.add(null);
				}
			}
		}
		else {
			row = new MacroElementModel(INITIAL_COL_CAPACITY);
		}
		// 情報を更新
		row.updateCommandAction();
		return row;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected class MacroElementModel extends RowDataModel
	{
		private CommandActionNode cmdNode = null;
		
		public MacroElementModel() {
			super();
		}

		public MacroElementModel(int initialCapacity) {
			super(initialCapacity);
		}
		
		public MacroAction getMacroAction() {
			return (cmdNode==null ? null : cmdNode.getAction());
		}
		
		public boolean updateCommandAction() {
			if (isEmpty())
				return false;
			
			MacroCell cmd = (MacroCell)get(0);
			if (cmd == null || Strings.isNullOrEmpty(cmd.getText())) {
				boolean ret = (cmdNode != null);
				cmdNode = null;
				return ret;
			}
			
			CommandParser parser = new CommandParser();
			try {
				cmdNode = (CommandActionNode)parser.parseCommand(cmd.getText());
				cmd.setErrorFlag(false);
				cmd.setTooltipText(null);
			}
			catch (RecognitionException ex) {
				cmdNode = null;
				cmd.setErrorFlag(true);
				cmd.setTooltipText(ex.getLocalizedMessage());
			}
			return true;	// updated
		}
	}

	static protected class MacroColumnModel extends ColumnDataModel
	{
		public MacroColumnModel() {
			super();
		}
		
		public MacroColumnModel(int initialCapacity) {
			super(initialCapacity);
		}
	}

	/**
	 * マクロデータのセルデータ
	 */
	static protected class MacroCell implements Cloneable
	{
		/**
		 * このデータがエラーであることを示す
		 */
		private boolean flgError;
		/**
		 * このセルのツールチップテキスト
		 */
		private String tipText;
		/**
		 * このセルの文字列データ
		 */
		private String text;
		
		public MacroCell() {
			flgError = false;
			tipText = null;
			text = EMPTY_CELL_TEXT;
		}
		
		public MacroCell(String text) {
			flgError = false;
			tipText = null;
			setText(text);
		}
		
		public boolean isError() {
			return flgError;
		}
		
		public void setErrorFlag(boolean isError) {
			this.flgError = isError;
		}
		
		public String getTooltipText() {
			return tipText;
		}
		
		public void setTooltipText(String tooltip) {
			if (!Strings.isNullOrEmpty(tooltip))
				this.tipText = tooltip;
			else
				this.tipText = null;
		}
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			if (text != null)
				this.text = text;
			else
				this.text = EMPTY_CELL_TEXT;
		}

		@Override
		protected MacroCell clone() throws CloneNotSupportedException {
			try {
				return (MacroCell)super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new InternalError();
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj instanceof MacroCell) {
				if (((MacroCell)obj).text.equals(this.text)) {
					return true;
				}
			}
			
			return false;
		}

		@Override
		public int hashCode() {
			return text.hashCode();
		}

		@Override
		public String toString() {
			return text;
		}
	}
}
