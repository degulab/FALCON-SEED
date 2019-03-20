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
 * @(#)IEditorDocument.java	1.17	2011/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IEditorDocument.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IEditorDocument.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IEditorDocument.java	1.10	2009/01/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.document;

import java.io.File;
import java.io.IOException;

import ssac.aadl.editor.build.ExecutorFactory;
import ssac.aadl.editor.plugin.IComponentManager;
import ssac.aadl.editor.view.JEncodingComboBox;
import ssac.aadl.module.setting.ExecSettings;
import ssac.util.Validations;
import ssac.util.process.CommandExecutor;

/**
 * Jarモジュールを保持するドキュメント。
 * <p>
 * このドキュメントモデルは、Jarファイルのみの実行を制御するための
 * 特殊なドキュメントである。
 * 
 * @version 1.17	2011/02/02
 * @since 1.10
 */
public class JarModuleDocument implements IEditorDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 設定情報
	 */
	private final ExecSettings settings = new ExecSettings();

	/**
	 * ドキュメントの対象ファイル
	 */
	private File targetFile;
	/**
	 * ドキュメント対象ファイルのロード時の最終更新日時
	 * @since 1.17
	 */
	private long	_lastModifiedWhenLoadingTargetFile = 0L;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * このドキュメントの新しいインスタンスを生成する。
	 * 
	 * @param 実行対象のファイル
	 * @throws NullPointerException <code>targetFile</code> が <tt>null</tt> の場合
	 */
	public JarModuleDocument(File targetFile) {
		this.targetFile = Validations.validNotNull(targetFile);
		initDocument();
	}
	
	private void initDocument() {
		settings.loadForTarget(this.targetFile);
		settings.setTargetFile(this.targetFile.getAbsoluteFile());
		updateLastModifiedTimeWhenLoadingTargetFile();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

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
	
	/**
	 * このドキュメントのパスを含まないファイル名を返す。
	 * @return	ファイル名
	 */
	public String getTitle() {
		return targetFile.getName();
	}
	
	/**
	 * このドキュメントのファイルを返す。
	 * @return	ドキュメントファイル
	 */
	public File getTargetFile() {
		return targetFile;
	}
	
	/**
	 * 常に <tt>true</tt> を返す。
	 * @return	<tt>true</tt>
	 */
	public boolean hasTargetFile() {
		return true;
	}
	
	/**
	 * このドキュメントの保存先を、指定されたパスに設定する。
	 * このメソッドでは、保存先の正当性検証は行わず、ターゲットを指定されたパスに
	 * 設定するのみとなる。編集状態なども変更されない。
	 * @param newTarget	新しい保存先となる抽象パス
	 * @since 1.14
	 */
	public void setTargetFile(File newTarget) {
		this.targetFile = Validations.validNotNull(targetFile);
		settings.setPropertyTarget(this.targetFile);
		settings.setTargetFile(this.targetFile.getAbsoluteFile());
		updateLastModifiedTimeWhenLoadingTargetFile();
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
		return this.targetFile.lastModified();
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を、
	 * 保存先ファイルの現在の更新日時で更新する。
	 * @since 1.17
	 */
	public void updateLastModifiedTimeWhenLoadingTargetFile() {
		_lastModifiedWhenLoadingTargetFile = this.targetFile.lastModified();
	}
	
	/**
	 * このドキュメントはコンポーネントマネージャを持たないため、常に <tt>null</tt> を返す。
	 * @return	<tt>null</tt>
	 */
	public IComponentManager getManager() {
		return null;
	}

	public boolean canMoveTargetFile() {
		return true;
	}

	/**
	 * 常に <tt>false</tt> を返す。
	 * @return	<tt>false</tt>
	 */
	public boolean isNewDocument() {
		return false;
	}
	
	/**
	 * 常に <tt>false</tt> を返す。
	 * @return	<tt>false</tt>
	 */
	public boolean isModified() {
		return false;
	}
	
	/**
	 * プラットフォーム標準のエンコーディング名を返す。
	 */
	public String getLastEncodingName() {
		return JEncodingComboBox.getDefaultEncoding();
	}
	
	/**
	 * このドキュメントでは、ドキュメント編集不可能なため、このメソッドの
	 * 設定内容は無視される。
	 * @param modified	無効
	 */
	public void setModifiedFlag(boolean modified) {}
	
	/**
	 * このメソッドはサポートされない。
	 * @param targetFile	無効
	 * @throws UnsupportedOperationException 常にこの例外をスローする
	 */
	public void save(File targetFile) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 常に <tt>false</tt> を返す。
	 * @return	<tt>false</tt>
	 */
	public boolean isCompilable() {
		return false;
	}
	
	/**
	 * 常に <tt>true</tt> を返す。
	 * @return	<tt>true</tt>
	 */
	public boolean isExecutable() {
		return true;
	}
	
	/**
	 * このドキュメントに関連付けられている設定情報を、最新の情報に更新する。
	 * @return	最新の情報に更新された場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean refreshSettings() {
		return settings.refresh();
	}
	
	/**
	 * このドキュメントに関連付けられている設定情報を取得する。
	 * @return	ドキュメントの設定情報を返す。
	 */
	public ExecSettings getSettings() {
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
		if (targetFile != null && targetFile.exists())
			return targetFile;
		else
			return null;
	}
	
	/**
	 * 常に <tt>null</tt> を返す。
	 * @return	<tt>null</tt>
	 */
	public CommandExecutor createCompileExecutor() {
		return null;
	}
	
	/**
	 * このドキュメントに関連するモジュールを実行するための実行情報を生成する。
	 * @return	生成された {@link CommandExecutor} オブジェクトを返す。
	 */
	public CommandExecutor createModuleExecutor() {
		return ExecutorFactory.createJarExecutor(settings);
	}
	
	/**
	 * このメソッドはサポートされない。
	 * @throws UnsupportedOperationException 常にこの例外をスローする
	 */
	public void onCompileFinished() {
		throw new UnsupportedOperationException();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
