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
 * @(#)AbModuleConfig.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbModuleConfig.java	2.0.0	2012/10/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbModuleConfig.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleFileType;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.MExecArgsModel;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileManager;

/**
 * モジュール情報とモジュール引数設定値を保持する抽象クラス。
 * 
 * @version 3.1.0	2014/05/12
 * @since 1.22
 */
public abstract class AbModuleConfig<T extends IModuleArgConfig> implements IModuleConfig<T>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** モジュール実行番号 **/
	private long		_rno;

	/** 機能説明 **/
	private String		_desc;
	
	/**
	 * モジュール固有のコメント
	 * @since 3.1.0
	 */
	private String		_comment = "";

	/** モジュール実行定義情報の格納ディレクトリ(絶対パス) **/
	private VirtualFile	_vfTargetDir;
	/** モジュール実行定義ルートディレクトリの親ディレクトリ(絶対パス) **/
	private VirtualFile	_vfTargetParentDir;
	/** 実行対象のモジュール(絶対パス) **/
	private VirtualFile	_vfModule;
	/** 実行対象モジュールの種別 **/
	private ModuleFileType	_moduleType;
	/** 実行対象モジュールのメインクラス **/
	private String			_mainClass;
	/** JAVA固有の引数 **/
	private String			_javaVMArgs;

	/** モジュールファイルのサイズ **/
	private long			_lModuleFileLength;
	/** モジュールファイルの最終更新日時 **/
	private long			_lModuleLastModified;
	
	/** モジュール実行時引数 **/
	protected ArrayList<T>			_args;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbModuleConfig() {
		setDescription(null);
		this._vfTargetDir = null;
		this._vfTargetParentDir = null;
		this._vfModule    = null;
		this._moduleType  = null;
		this._mainClass   = null;
		this._javaVMArgs  = null;
		this._lModuleFileLength   = 0;
		this._lModuleLastModified = 0;
		this._args = createArgsList();
	}
	
	public AbModuleConfig(final MExecDefSettings settings) {
		setDescription(settings.getDescription());
		this._vfTargetDir = settings.getExecDefDirectory();
		this._vfTargetParentDir = (_vfTargetDir==null ? null : _vfTargetDir.getParentFile());
		this._vfModule    = settings.getModuleFile();
		this._moduleType  = settings.getModuleFileType();
		this._mainClass   = settings.getModuleMainClass();
		this._javaVMArgs  = settings.getJavaVMArgs();
		if (_vfModule != null) {
			this._lModuleFileLength   = _vfModule.length();
			this._lModuleLastModified = _vfModule.lastModified();
		} else {
			this._lModuleFileLength   = 0;
			this._lModuleLastModified = 0;
		}
		this._args = createArgsList(settings);
	}

	/**
	 * 互換性のためのコンストラクタ
	 * @param argsmodel	モジュール実行時引数のデータモデル
	 */
	public AbModuleConfig(final MExecArgsModel argsmodel) {
		final MExecDefSettings settings = argsmodel.getSettings();
		setDescription(settings.getDescription());
		this._vfTargetDir = settings.getExecDefDirectory();
		this._vfTargetParentDir = (_vfTargetDir==null ? null : _vfTargetDir.getParentFile());
		this._vfModule    = settings.getModuleFile();
		this._moduleType  = settings.getModuleFileType();
		this._mainClass   = settings.getModuleMainClass();
		this._javaVMArgs  = settings.getJavaVMArgs();
		if (_vfModule != null) {
			this._lModuleFileLength   = _vfModule.length();
			this._lModuleLastModified = _vfModule.lastModified();
		} else {
			this._lModuleFileLength   = 0;
			this._lModuleLastModified = 0;
		}
		this._args = createArgsList(argsmodel);
	}
	
	/**
	 * コピーコンストラクタ
	 */
	public AbModuleConfig(final IModuleConfig<? extends IModuleArgConfig> src) {
		this._rno         = src.getRunNo();
		this._desc        = src.getDescription();
		this._comment     = src.getComment();
		this._vfTargetDir = src.getExecDefDirectory();
		this._vfTargetParentDir = (_vfTargetDir==null ? null : _vfTargetDir.getParentFile());
		this._vfModule    = src.getModuleFile();
		this._moduleType  = src.getModuleType();
		this._mainClass   = src.getModuleMainClass();
		this._javaVMArgs  = src.getJavaVMArgs();
		if (_vfModule != null) {
			this._lModuleFileLength   = _vfModule.length();
			this._lModuleLastModified = _vfModule.lastModified();
		} else {
			this._lModuleFileLength   = 0;
			this._lModuleLastModified = 0;
		}
		this._args = createArgsList(src);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモジュールデータの基準パスを更新する。
	 * このモジュールに含まれるファイルは新しい基準パス下のパスに変換される。
	 * @param newTarget		新しい基準位置を示す抽象パス
	 * @param withoutArguments	引数のファイル抽象パスを変換対象から除外する場合は <tt>true</tt>
	 * @throws NullPointerException	<em>newTarget</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>newTarget</em> がファイルシステム上のルートの場合
	 * @since 2.0.0
	 */
	public void updateMExecDefDirectory(VirtualFile newTarget, boolean withoutArguments) {
		VirtualFile vfParent = newTarget.getParentFile();
		if (vfParent == null)
			throw new IllegalArgumentException("Target file has no parent : \"" + newTarget.toString() + "\"");

		VirtualFile vfOldTarget = _vfTargetDir;
		_vfTargetDir = newTarget;
		_vfTargetParentDir = vfParent;
		if (vfOldTarget == null)
			return;		// no more process
		
		if (_vfModule != null && _vfModule.isDescendingFrom(vfOldTarget)) {
			_vfModule = newTarget.getChildFile(_vfModule.relativePathFrom(vfOldTarget));
		}
		if (withoutArguments || _args.isEmpty())
			return;		// no more process
		
		for (T arg : _args) {
			VirtualFile vfFile = VirtualFileManager.toVirtualFile(arg.getValue());
			if (vfFile != null && vfFile.isDescendingFrom(vfOldTarget)) {
				arg.setValue(newTarget.getChildFile(vfFile.relativePathFrom(vfOldTarget)));
			}
		}
	}

	/**
	 * 指定されたモジュール実行定義の定義情報で、このオブジェクトを更新する。
	 * この更新では、モジュール実行定義の位置、モジュールファイルの情報、
	 * 引数の説明のみが更新される。なお、引数の説明更新は、引数の番号と
	 * 属性が一致する場合のみ更新される。
	 * @param settings	モジュール実行定義の定義情報
	 */
	public void updateModuleConfig(final MExecDefSettings settings) {
		setDescription(settings.getDescription());
		this._vfTargetDir = settings.getExecDefDirectory();
		this._vfTargetParentDir = (_vfTargetDir==null ? null : _vfTargetDir.getParentFile());
		this._vfModule    = settings.getModuleFile();
		this._moduleType  = settings.getModuleFileType();
		this._mainClass   = settings.getModuleMainClass();
		this._javaVMArgs  = settings.getJavaVMArgs();
		if (_vfModule != null) {
			this._lModuleFileLength   = _vfModule.length();
			this._lModuleLastModified = _vfModule.lastModified();
		} else {
			this._lModuleFileLength   = 0;
			this._lModuleLastModified = 0;
		}
		
		int arglen = Math.min(_args.size(), settings.getNumArguments());
		for (int i = 0; i < arglen; i++) {
			IModuleArgConfig dstarg = _args.get(i);
			ModuleArgData srcarg = settings.getArgument(i);
			if (dstarg.getType() == srcarg.getType()) {
				dstarg.setDescription(srcarg.getDescription());
			}
		}
	}

	/**
	 * 指定されたモジュール実行定義の定義情報で、このオブジェクトを更新する。
	 * 基本的に、モジュール情報、引数の情報などがすべて更新される。
	 * @param settings	モジュール実行定義の定義情報
	 * @since 2.0.0
	 */
	public void setData(final MExecDefSettings settings) {
		setDescription(settings.getDescription());
		this._vfTargetDir = settings.getExecDefDirectory();
		this._vfTargetParentDir = (_vfTargetDir==null ? null : _vfTargetDir.getParentFile());
		this._vfModule    = settings.getModuleFile();
		this._moduleType  = settings.getModuleFileType();
		this._mainClass   = settings.getModuleMainClass();
		this._javaVMArgs  = settings.getJavaVMArgs();
		if (_vfModule != null) {
			this._lModuleFileLength   = _vfModule.length();
			this._lModuleLastModified = _vfModule.lastModified();
		} else {
			this._lModuleFileLength   = 0;
			this._lModuleLastModified = 0;
		}
		this._args = createArgsList(settings);
	}

	/**
	 * モジュール実行番号を取得する。
	 * 実行番号は、モジュール実行時に割り当てられる識別番号である。
	 * @return	実行番号
	 */
	public long getRunNo() {
		return _rno;
	}

	/**
	 * モジュール実行番号を設定する。
	 * 実行番号は、モジュール実行時に割り当てる識別番号である。
	 * @param rno	新しい実行番号
	 */
	public void setRunNo(long rno) {
		this._rno = rno;
	}

	/**
	 * モジュール実行定義の名前を取得する。
	 * @return	モジュール実行定義名
	 */
	public String getName() {
		return _vfTargetDir.getName();
	}

	/**
	 * 実行番号とモジュール実行定義名を組み合わせた文字列を返す。
	 * この文字列は、
	 * <blockquote>
	 * <em>実行番号</em>:<em>モジュール実行定義名</em>
	 * </blockquote>
	 * として出力される。
	 * @return	生成された文字列
	 * @since 2.0.0
	 */
	public String getRunNoAndFilterName() {
		String name = getName();
		return String.format("%d:%s", getRunNo(), (name==null ? "" : name));
	}

	/**
	 * モジュール実行定義の説明を返す。
	 * @return	説明
	 */
	public String getDescription() {
		return _desc;
	}

	/**
	 * モジュール実行定義の説明を設定する。
	 * @param desc	説明を示す文字列
	 */
	public void setDescription(String desc) {
		_desc = (desc==null ? "" : desc);
	}

	/**
	 * このモジュールのコメントを取得する。
	 * @return	<tt>null</tt> ではない文字列
	 * @since 3.1.0
	 */
	public String getComment() {
		return _comment;
	}

	/**
	 * このモジュールにコメントを設定する。
	 * @param comment	コメントとする文字列、または <tt>null</tt>
	 * @since 3.1.0
	 */
	public void setComment(String comment) {
		_comment = (comment==null ? "" : comment);
	}

	/**
	 * モジュール実行定義のディレクトリが存在しているかを判定する。
	 * @return	存在していれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isExistExecDefDirectory() {
		try {
			if (_vfTargetDir.exists() && _vfTargetDir.isDirectory()) {
				return true;
			}
		} catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		return false;
	}

	/**
	 * モジュール実行定義の設定ファイルが存在しているかを判定する。
	 * @return	存在していれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isExistExecDefPrefsFile() {
		try {
			VirtualFile vfPrefs = MExecDefFileManager.getModuleExecDefDataFile(_vfTargetDir);
			if (vfPrefs.exists() && vfPrefs.isFile()) {
				return true;
			}
		} catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		return false;
	}

	/**
	 * 実行対象のモジュールファイルが存在しているかを判定する。
	 * @return	存在していれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isExistModuleFile() {
		try {
			if (_vfModule.exists() && _vfModule.isFile()) {
				return true;
			}
		} catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		return false;
	}

	/**
	 * モジュール実行定義のディレクトリを取得する。
	 * @return	モジュール実行定義のルートディレクトリの位置を示す抽象パス
	 */
	public VirtualFile getExecDefDirectory() {
		return _vfTargetDir;
	}

	/**
	 * モジュール実行定義ルートディレクトリの親ディレクトリを取得する。
	 * @return	モジュール実行定義ルートディレクトリの親ディレクトリを示す抽象パス
	 * @since 2.0.0
	 */
	public VirtualFile getExecDefParentDirectory() {
		return _vfTargetParentDir;
	}

	/**
	 * モジュール実行定義の設定ファイルを取得する。
	 * @return	モジュール実行定義の設定ファイルの位置を示す抽象パス
	 */
	public VirtualFile getExecDefPrefsFile() {
		VirtualFile vfPrefs = null;
		if (_vfTargetDir != null) {
			vfPrefs = MExecDefFileManager.getModuleExecDefDataFile(_vfTargetDir);
		}
		return vfPrefs;
	}

	/**
	 * 実行対象のモジュールファイルを取得する。
	 * @return	モジュールファイルの位置を示す抽象パス
	 */
	public VirtualFile getModuleFile() {
		return _vfModule;
	}

	/**
	 * 実行対象のモジュールファイルの、現在のサイズを取得する。
	 * @return	モジュールファイルのバイト数
	 */
	public long getModuleFileLength() {
		return (_vfModule==null ? 0 : _vfModule.length());
	}

	/**
	 * 実行対象のモジュールファイルの、現在の最終更新日時を取得する。
	 * このメソッドでは、モジュールファイルが存在しない場合は 0 を返す。
	 * @return	モジュールファイルの最終更新日時
	 */
	public long getModuleFileLastModified() {
		return (_vfModule==null ? 0 : _vfModule.lastModified());
	}

	/**
	 * 実行対象のモジュールファイルが変更されているかを判定する。
	 * このメソッドでは、モジュールファイルが存在しない場合は <tt>false</tt> を返す。
	 * @return	変更されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isModifiedModuleFile() {
		try {
			long len = _vfModule.length();
			long lm  = _vfModule.lastModified();
			if (len==_lModuleFileLength && lm==_lModuleLastModified) {
				return false;
			}
		} catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		return true;
	}

	/**
	 * 実行対象のモジュールが実行された時点での、モジュールファイルのサイズを取得する。
	 * 実行前の場合は、現在のモジュールファイルのサイズを返す。
	 * @return	モジュール実行時点での、モジュールファイルのバイト数
	 */
	public long getModuleFileLengthAtRuntime() {
		return _lModuleFileLength;
	}

	/**
	 * 実行対象のモジュールが実行された時点での、モジュールファイルのサイズを取得する。
	 * 実行前の場合は、現在のモジュールファイルの最終更新日時を返す。
	 * @return	モジュール実行時点での、モジュールファイルの最終更新日時
	 */
	public long getModuleFileLastModifiedAtRuntime() {
		return _lModuleLastModified;
	}

	/**
	 * モジュールの種別を取得する。
	 * @return	モジュール種別を示す <code>ModuleFileType</code> オブジェクト
	 */
	public ModuleFileType getModuleType() {
		return _moduleType;
	}

	/**
	 * モジュールのメインクラスを取得する。
	 * @return	モジュールのメインクラス名
	 */
	public String getModuleMainClass() {
		return _mainClass;
	}

	/**
	 * モジュールに指定された Java VM オプションを取得する。
	 * @return	オプションが指定されている場合はその文字列、指定されていない場合は <tt>null</tt>
	 */
	public String getJavaVMArgs() {
		return _javaVMArgs;
	}

	/**
	 * モジュールの引数が存在しない場合に <tt>true</tt> を返す。
	 * @return	引数が存在しない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEmptyArgument() {
		return _args.isEmpty();
	}

	/**
	 * モジュール定義の引数の数を取得する。
	 * @return	引数の数
	 */
	public int getArgumentCount() {
		return _args.size();
	}

	/**
	 * 引数定義情報を取得する。
	 * @param index	取得する引数のインデックス。
	 * @return	引数定義情報の新しいインスタンス
	 */
	public ModuleArgData getArgDefinition(int index) {
		T arg = getArgument(index);
		ModuleArgData argdata;
		if (arg.isFixedValue()) {
			argdata = new ModuleArgData(arg.getType(), arg.getDescription(), arg.getValue());
		} else {
			argdata = new ModuleArgData(arg.getType(), arg.getDescription(), arg.getParameterType());
		}
		return argdata;
	}

	/**
	 * 実行時引数の値を取得する。
	 * @param index	取得する引数のインデックス
	 * @return	実行時引数の値を保持する <code>IRuntimeMExecArgValue</code> オブジェクト
	 * @throws ArrayIndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public T getArgument(int index) {
		return _args.get(index);
	}

	public Iterator<T> iterator() {
		List<T> l = Collections.unmodifiableList(_args);
		return l.iterator();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	abstract protected ArrayList<T> createArgsList();
	abstract protected ArrayList<T> createArgsList(final MExecDefSettings settings);
	abstract protected ArrayList<T> createArgsList(final MExecArgsModel argsmodel);
	abstract protected ArrayList<T> createArgsList(final IModuleConfig<? extends IModuleArgConfig> src);

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
