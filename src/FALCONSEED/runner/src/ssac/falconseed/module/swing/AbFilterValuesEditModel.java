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
 * @(#)AbFilterValuesEditModel.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbFilterValuesEditModel.java	2.0.0	2012/10/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.io.File;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.module.FilterDataError;
import ssac.falconseed.module.FilterError;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;

/**
 * フィルタ実行時引数値を保持するデータモデルの共通実装。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public abstract class AbFilterValuesEditModel implements IFilterValuesEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** イベントリスナーのリスト **/
	protected EventListenerList	_listenerList = new EventListenerList();
	
	/** モジュール引数値定義情報 **/
	protected ModuleRuntimeData	_moduleData;
	/** モジュール実行定義の引数履歴 **/
	protected MExecDefHistory	_argshistory;
	/** モジュール実行定義内のローカルファイルルートディレクトリ **/
	protected VirtualFile		_vfFilesRootDir;
	/** パス整形用フォーマッター **/
	protected VirtualFilePathFormatterList	_vfFormatter;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbFilterValuesEditModel() {
		
	}
	
	public AbFilterValuesEditModel(final ModuleRuntimeData data) {
		initModuleData(data);
	}
	
	private void initModuleData(final ModuleRuntimeData newData) {
		_moduleData = newData;
		if (newData == null) {
			this._argshistory = null;
			this._vfFilesRootDir = null;
			this._vfFormatter = null;
		}
		else {
			// パスの取得
			VirtualFile vfMExecDefDir = newData.getExecDefDirectory();
			if (vfMExecDefDir == null)
				throw new IllegalArgumentException("MExecDef directory is null.");
			if (!newData.isExistExecDefDirectory())
				throw new IllegalArgumentException("MExecDef directory is not exists : \"" + vfMExecDefDir.toString() + "\"");
			_vfFilesRootDir = vfMExecDefDir.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
			
			// create path formatter
			_vfFormatter = new VirtualFilePathFormatterList();
			_vfFormatter.add(new DefaultVirtualFilePathFormatter(_vfFilesRootDir, null));
			_vfFormatter.add(new DefaultVirtualFilePathFormatter(vfMExecDefDir, null));
			
			// 引数履歴の取得
			if (isArgsHistoryEnabled()) {
				_argshistory = new MExecDefHistory();
				VirtualFile vfHistory = vfMExecDefDir.getChildFile(MExecDefFileManager.MEXECDEF_HISTORY_FILENAME);
				_argshistory.loadForTarget(vfHistory);
				_argshistory.ensureArgsTypes(newData);	// 引数型定義にあわない履歴を除去
				_argshistory.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());	// 履歴の最大数を反映
			}
			else {
				_argshistory = null;
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数値の編集が可能であれば <tt>true</tt> を返す。
	 */
	abstract public boolean isArgsEditable();

	/**
	 * 引数履歴情報が利用可能であれば <tt>true</tt> を返す。
	 */
	abstract public boolean isArgsHistoryEnabled();

	/**
	 * モジュール実行定義の名前を取得する。
	 * @return	モジュール実行定義が存在していればその名前、それ以外の場合は <tt>null</tt>
	 */
	public String getName() {
		return (_moduleData==null ? null : _moduleData.getName());
	}

	/**
	 * モジュール実行定義の説明を取得する。
	 * @return	モジュール実行定義が存在していればその説明、それ以外の場合は <tt>null</tt>
	 */
	public String getDescription() {
		return (_moduleData==null ? null : _moduleData.getDescription());
	}

	/**
	 * 引数履歴情報を取得する。
	 * @return	引数履歴情報が利用可能であればそのオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public MExecDefHistory getArgsHistory() {
		return _argshistory;
	}

	/**
	 * モジュール実行定義のルートディレクトリが配置されているディレクトリを取得する。
	 * @return	モジュール実行定義が存在していればその親の抽象パス、それ以外の場合は <tt>null</tt>
	 * @since 2.0.0
	 */
	public VirtualFile getExecDefParentDirectory() {
		return (_moduleData==null ? null : _moduleData.getExecDefParentDirectory());
	}

	/**
	 * モジュール実行定義のルートディレクトリを取得する。
	 * @return	モジュール実行定義が存在していればその抽象パス、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFile getExecDefDirectory() {
		return (_moduleData==null ? null : _moduleData.getExecDefDirectory());
	}

	/**
	 * モジュール実行定義内のローカルファイルルートディレクトリを取得する。
	 * @return	モジュール実行定義が存在していれば対応する抽象パス、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFile getExecDefLocalFileRootDirectory() {
		return _vfFilesRootDir;
	}

	/**
	 * パスフォーマット用の基準パスを返す。
	 * @return	基準パスを示すオブジェクト、基準パスが存在しない場合は <tt>null</tt>
	 */
	public VirtualFile getBasePath() {
		return getExecDefDirectory();
	}

	/**
	 * パス整形用フォーマッターを取得する。
	 * @return	モジュール実行定義が存在していれば対応するオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFilePathFormatterList getFormatter() {
		return _vfFormatter;
	}

	/**
	 * 指定された抽象パスを、表示用パス文字列に整形する。
	 * 整形できないパスの場合はパス文字列そのものを返す。
	 * @return	整形後のパス文字列
	 */
	public String formatPathString(VirtualFile file) {
		VirtualFilePathFormatter vfFormatter = getFormatter();
		if (vfFormatter != null) {
			String path = vfFormatter.formatPath(file);
			if (path != null)
				return path;
		}

		VirtualFile vfBasePath = getBasePath();
		if (vfBasePath != null && file.isDescendingFrom(vfBasePath)) {
			return file.relativePathFrom(vfBasePath, Files.CommonSeparatorChar);
		} else {
			return file.toString();
		}
	}

	/**
	 * モジュール実行設定情報を取得する。
	 * @return	モジュール実行設定情報が登録されていればそのオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public ModuleRuntimeData getModuleData() {
		return _moduleData;
	}

	/**
	 * モジュール実行設定情報を設定する。
	 * @param newData	設定するモジュール実行設定情報
	 * @return データが変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalArgumentException	指定されたモジュール実行設定情報に対応するモジュール実行定義が存在しない場合
	 */
	public boolean setModuleData(final ModuleRuntimeData newData) {
		if (newData == _moduleData)
			return false;		// no changes

		ModuleRuntimeData oldData = _moduleData;
		initModuleData(newData);

		onChangedData(false, oldData, newData);
		return true;
	}

	/**
	 * モジュールの引数が存在しない場合に <tt>true</tt> を返す。
	 * @return	引数が存在しない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEmptyArgument() {
		return (_moduleData==null ? false : _moduleData.isEmptyArgument());
	}

	/**
	 * モジュール定義の引数の数を取得する。
	 * @return	引数の数
	 */
	public int getArgumentCount() {
		return (_moduleData==null ? 0 : _moduleData.getArgumentCount());
	}

	/**
	 * 指定されたインデックスに対応する引数オブジェクトを返す。
	 * @param argIndex	引数インデックス
	 * @return	引数オブジェクト
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public ModuleArgConfig getArgument(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex);
	}

	/**
	 * 指定されたインデックスに対応する引数の属性を返す。
	 * @param argIndex	引数インデックス
	 * @return 引数属性
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public ModuleArgType getArgumentType(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).getType();
	}

	/**
	 * 指定されたインデックスに対応する引数の説明を返す。
	 * @param argIndex	引数インデックス
	 * @return	引数の説明
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public String getArgumentDescription(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).getDescription();
	}

	/**
	 * 指定されたインデックスに対応する引数の説明を設定する。
	 * @param argIndex	引数インデックス
	 * @param newDesc	新しい説明の文字列
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean setArgumentDescription(int argIndex, String newDesc) {
		ensureArgument();
		return setArgumentDescription(argIndex, false, newDesc);
	}

	/**
	 * 指定されたインデックスに対応する引数に固定値が設定済みであれば <tt>true</tt> を返す。
	 * @param argIndex	引数インデックス
	 * @return	固定値設定済みの引数であれば <tt>true</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean isArgumentFixedValue(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).isFixedValue();
	}

	/**
	 * 指定されたインデックスに対応する引数定義のパラメータ種別を返す。
	 * @param argIndex	引数インデックス
	 * @return	可変引数の場合は <code>IMExecArgParam</code> オブジェクト、固定の場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public IMExecArgParam getArgumentParameterType(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).getParameterType();
	}

	/**
	 * 指定されたインデックスに対応する引数に値が設定されていれば <tt>true</tt> を返す。
	 * @param argIndex	引数インデックス
	 * @return	引数値が <tt>null</tt> ではない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean hasArgumentValue(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).hasValue();
	}

	/**
	 * 指定されたインデックスに対応する引数の値を取得する。
	 * @param argIndex	引数インデックス
	 * @return	引数値が <tt>null</tt> ではない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public Object getArgumentValue(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).getValue();
	}

	/**
	 * 指定されたインデックスに対応する引数の値を、ファイルの抽象パスとして取得する。
	 * このメソッドでは、引数の値がファイル抽象パスではない場合、基本的に <tt>null</tt> を返す。
	 * ただし、引数の値が他の引数へのリンクの場合、リンク先の引数値を取得する。
	 * @param argIndex	引数インデックス
	 * @return	引数値がファイル抽象パスの場合は <code>VirtualFile</code> オブジェクト、それ以外の場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public VirtualFile getArgumentFileValue(int argIndex) {
		ensureArgument();
		
		VirtualFile retFile = null;
		Object val = _moduleData.getArgument(argIndex).getValue();
		if (val instanceof VirtualFile) {
			retFile = (VirtualFile)val;
		}
		else if (val instanceof File) {
			retFile = ModuleFileManager.fromJavaFile((File)val);
		}
		else if (val instanceof ModuleArgID) {
			// 他引数へのリンク
			ModuleArgID argid = (ModuleArgID)val;
			val = argid.getValue();
			if (val instanceof VirtualFile) {
				retFile = (VirtualFile)val;
			}
			else if (val instanceof File) {
				retFile = ModuleFileManager.fromJavaFile((File)val);
			}
		}
		
		return retFile;
	}

	/**
	 * 指定されたインデックスに対応する引数の値を設定する。
	 * @param argIndex	引数インデックス
	 * @param newValue	新しい引数の値
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 * @throws IllegalStateException		フィルタ引数が固定値の場合
	 */
	public boolean setArgumentValue(int argIndex, Object newValue) {
		ensureArgument();
		return setArgumentValue(argIndex, false, newValue);
	}

	/**
	 * 指定されたインデックスに対応する引数のテンポラリ出力設定を取得する。
	 * @param argIndex	引数インデックス
	 * @return	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean getArgumentOutToTempEnabled(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).getOutToTempEnabled();
	}

	/**
	 * 指定されたインデックスに対応する引数に、テンポラリファイルへの出力を設定する。
	 * @param argIndex	引数インデックス
	 * @param toEnable	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 * @throws IllegalStateException		フィルタ引数が固定値の場合
	 */
	public boolean setArgumentOutToTempEnabled(int argIndex, boolean toEnable) {
		ensureArgument();
		return setArgumentOutToTempEnable(argIndex, false, toEnable);
	}

	/**
	 * 指定されたインデックスに対応する引数に、テンポラリファイルのプレフィックスが指定されているかを判定する。
	 * @param argIndex	引数インデックス
	 * @return	プレフィックスが指定されている場合は <tt>true</tt>、指定されていない場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean hasArgumentTempFilePrefix(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).hasTempFilePrefix();
	}

	/**
	 * 指定されたインデックスに対応する引数のテンポラリファイル用プレフィックスを取得する。
	 * @param argIndex	引数インデックス
	 * @return	プレフィックスが指定されている場合はその文字列、指定されていない場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public String getArgumentTempFilePrefix(int argIndex) {
		ensureArgument();
		return _moduleData.getArgument(argIndex).getTempFilePrefix();
	}

	/**
	 * 指定されたインデックスに対応する引数に、テンポラリファイル用プレフィックスを設定する。
	 * @param argIndex	引数インデックス
	 * @param newPrefix	テンポラリファイルのプレフィックス、指定しない場合は <tt>null</tt> もしくは長さが 0 の文字列
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean setArgumentTempFilePrefix(int argIndex, String newPrefix) {
		ensureArgument();
		return setArgumentTempFilePrefix(argIndex, false, newPrefix);
	}

// removed @since 3.1.0
//	/**
//	 * このモデルに格納されたデータにエラーが存在するかを判定する。
//	 * @return	エラーが存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean hasModuleError() {
//		if (_moduleData == null) {
//			return false;
//		}
//		
//		return (_moduleData.getUserData() instanceof FilterConfigError);
//	}

// removed @since 3.1.0
//	/**
//	 * このモデルのモジュールデータに設定されたエラーオブジェクトを取得する。
//	 * @return	エラーオブジェクト、なければ <tt>null</tt>
//	 */
//	public FilterDataError getModuleError() {
//		if (_moduleData == null)
//			return null;
//		
//		Object ud = _moduleData.getUserData();
//		if (ud instanceof FilterConfigError) {
//			return (FilterConfigError)ud;
//		} else {
//			return null;
//		}
//	}

// removed @since 3.1.0
//	public void setModuleError(FilterConfigError err) {
//		if (_moduleData != null) {
//			Object ud = _moduleData.getUserData();
//			if (!Objects.isEqual(ud, err)) {
//				_moduleData.setUserData(err);
//				fireDataChanged();
//			}
//		}
//	}
	
	/**
	 * このモデルに格納されたデータに実行順序リンクエラーが存在するかを判定する。
	 * @return	エラーが存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	public boolean hasRunOrderLinkError() {
		return (_moduleData==null ? false : _moduleData.hasModuleError());
	}

	/**
	 * このモデルのモジュールに設定された実行順序リンクエラーオブジェクトを取得する。
	 * @return	エラーオブジェクト、なければ <tt>null</tt>
	 * @since 3.1.0
	 */
	public FilterDataError getRunOrderLinkError() {
		return (_moduleData==null ? null : _moduleData.getModuleError());
	}

	/**
	 * このモデルに格納されたモジュールの実行順序リンクエラーをクリアする。
	 * @return	エラー状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	public boolean clearRunOrderLinkError() {
		return clearRunOrderLinkError(false);
	}

	/**
	 * このモジュールに対して、指定された実行順序リンクエラーオブジェクトを設定する。
	 * @param err	エラーオブジェクト、または <tt>null</tt>
	 * @return エラー状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalStateException	<em>err</em> が <tt>null</tt> ではなく、モジュールデータが <tt>null</tt> の場合
	 * @since 3.1.0
	 */
	public boolean setRunOrderLinkError(FilterDataError err) {
		return setRunOrderLinkError(false, err);
	}

	/**
	 * 指定されたインデックスに対応する引数にエラーが存在するかを判定する。
	 * @param argIndex	引数インデックス
	 * @return	エラーが存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean hasArgumentError(int argIndex) {
		ensureArgument();
		return (_moduleData.getArgument(argIndex).getUserData() instanceof FilterError);
	}
	
	public FilterError getArgumentError(int argIndex) {
		ensureArgument();
		Object ud = _moduleData.getArgument(argIndex).getUserData();
		if (ud instanceof FilterError) {
			return (FilterError)ud;
		} else {
			return null;
		}
	}
	
	/**
	 * 指定されたインデックスに対応する引数のエラーをクリアする。
	 * @param argIndex		引数インデックス
	 * @return	エラー状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean clearArgumentError(int argIndex) {
		ensureArgument();
		return clearArgumentError(argIndex, false);
	}

	/**
	 * 指定されたインデックスに対応する引数にエラーオブジェクトを設定する。
	 * @param argIndex	引数インデックス
	 * @param err	エラーオブジェクト、または <tt>null</tt>
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean setArgumentErrorObject(int argIndex, Object err) {
		ensureArgument();
		return setArgumentErrorObject(argIndex, false, err);
	}

	/**
	 * 指定されたインデックスに対応する引数に、指定されたメッセージを格納するエラーオブジェクトを設定する。
	 * <em>errmsg</em> が <tt>null</tt> もしくは空文字列の場合、エラーはクリアされる。
	 * @param argIndex	引数インデックス
	 * @param errmsg	エラーメッセージ
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	public boolean setArgumentErrorString(int argIndex, String errmsg) {
		ensureArgument();
		return setArgumentErrorString(argIndex, false, errmsg);
	}

	/**
	 * 指定された引数インデックスに対応する引数値の正当性をチェックする。
	 * このメソッドでは、正当性チェックによるイベントも発行される。
	 * @param argIndex	引数インデックス
	 * @return	引数値が正当であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public boolean verifyArgument(int argIndex) {
		ensureArgument();
//		ModuleArgConfig argdata = getArgument(argIndex);
//		switch (argdata.getType()) {
//			case IN :
//				return verifyInputArgumentValue(argIndex, false, argdata);
//			case OUT :
//				return verifyOutputArgumentValue(argIndex, false, argdata);
//			default :
//				return verifyStringArgumentValue(argIndex, false, argdata);
//		}
		return verifyArgument(argIndex, false);	// withoutEvent=false
	}

	/**
	 * 指定されたモジュール実行設定情報に対応する、引数履歴オブジェクトを取得する。
	 * このメソッドでは、モジュール実行定義のディレクトリ有無を判定し、履歴オブジェクトを取得する。
	 * このとき、引数型定義のチェックは行わない。
	 * @param data	モジュール実行設定情報
	 * @return	取得できた場合はそのオブジェクト、それ以外の場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public MExecDefHistory getHistory(final ModuleRuntimeData data) {
		MExecDefHistory hist = null;
		if (data.isExistExecDefDirectory()) {
			VirtualFile vfHistory = data.getExecDefDirectory().getChildFile(MExecDefFileManager.MEXECDEF_HISTORY_FILENAME);
			hist = new MExecDefHistory();
			hist.loadForTarget(vfHistory);
			hist.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());
		}
		return hist;
	}

	/**
	 * データモデルが変更されるたびに通知されるリストにリスナーを追加する。
	 * @param l		追加するリスナーオブジェクト
	 */
	public void addDataModelListener(IFilterValuesEditModelListener l) {
		_listenerList.add(IFilterValuesEditModelListener.class, l);
	}

	/**
	 * データモデルが変更されるたびに通知されるリストからリスナーを削除する。
	 * @param l		削除するリスナーオブジェクト
	 */
	public void removeDataModelListener(IFilterValuesEditModelListener l) {
		_listenerList.remove(IFilterValuesEditModelListener.class, l);
	}

	/**
	 * このモデルに登録された、すべてのデータモデルリスナーから成る配列を返す。
	 * @return	このモデルの <code>IFilterValuesEditModelListener</code> 全部。データモデルリスナーが現在登録されていない場合は空の配列
	 * @see #addDataModelListener(IFilterValuesEditModelListener)
	 * @see #removeDataModelListener(IFilterValuesEditModelListener)
	 */
	public IFilterValuesEditModelListener[] getDataModelListeners() {
		return (IFilterValuesEditModelListener[])_listenerList.getListeners(IFilterValuesEditModelListener.class);
	}

	/**
	 * この <code>AbFilterValuesEditModel</code> に <code><em>Foo</em>Listener</code> として現在登録されているすべてのオブジェクトの配列を返す。
	 * <code><em>Foo</em>Listener</code> は、<code>add<em>Foo</em>Listener</code> メソッドを使用して登録する。
	 * <code><em>Foo</em>Listener.class</code> といったクラスリテラルを使用して、<em>listenerType</em> 引数を指定できる。
	 * 該当するリスナーがない場合は空の配列を返す。
	 * @param listenerType	要求されるリスナーの型。<code>java.util.EventListener</code> の下位インタフェースを指定
	 * @return	コンポーネントに <code><em>Foo</em>Listener</code> として登録されているすべてのオブジェクトの配列。リスナーが登録されていない場合は空の配列を返す
	 * @throws ClassCastException	<em>listenerType</em> が <code>java.util.EventListener</code> を実装するクラスまたはインタフェースを指定しない場合
	 * @see #getDataModelListeners()
	 */
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return _listenerList.getListeners(listenerType);
	}

	/**
	 * 指定されたインデックスに対応する引数の属性が変更されている可能性があることを、すべてのリスナーに通知する。
	 * @param argIndex	引数インデックス
	 */
	public void fireUpdateArgumentType(int argIndex) {
		fireDataChanged(new FilterValuesEditModelEvent(this, argIndex, argIndex, FilterValuesEditModelEvent.UPDATE_ARGUMENT_TYPE));
	}

	/**
	 * <code>[firstArgIndex, lastArgIndex]</code>(どちらの値も含む)で、引数の属性が変更されていることを、すべてのリスナーに通知する。
	 * @param firstArgIndex		最初の引数を示すインデックス
	 * @param lastArgIndex		最後の引数を示すインデックス
	 */
	public void fireUpdateArgumentTypes(int firstArgIndex, int lastArgIndex) {
		fireDataChanged(new FilterValuesEditModelEvent(this, firstArgIndex, lastArgIndex, FilterValuesEditModelEvent.UPDATE_ARGUMENT_TYPE));
	}

	/**
	 * 指定されたインデックスに対応する引数の説明が変更されている可能性があることを、すべてのリスナーに通知する。
	 * @param argIndex	引数インデックス
	 */
	public void fireUpdateArgumentDescription(int argIndex) {
		fireDataChanged(new FilterValuesEditModelEvent(this, argIndex, argIndex, FilterValuesEditModelEvent.UPDATE_ARGUMENT_DESC));
	}
	
	/**
	 * <code>[firstArgIndex, lastArgIndex]</code>(どちらの値も含む)で、引数の説明が変更されていることを、すべてのリスナーに通知する。
	 * @param firstArgIndex		最初の引数を示すインデックス
	 * @param lastArgIndex		最後の引数を示すインデックス
	 */
	public void fireUpdateArgumentDescriptions(int firstArgIndex, int lastArgIndex) {
		fireDataChanged(new FilterValuesEditModelEvent(this, firstArgIndex, lastArgIndex, FilterValuesEditModelEvent.UPDATE_ARGUMENT_DESC));
	}
	
	/**
	 * 指定されたインデックスに対応する引数の値が変更されている可能性があることを、すべてのリスナーに通知する。
	 * @param argIndex	引数インデックス
	 */
	public void fireUpdateArgumentValue(int argIndex) {
		fireDataChanged(new FilterValuesEditModelEvent(this, argIndex, argIndex, FilterValuesEditModelEvent.UPDATE_ARGUMENT_VALUE));
	}
	
	/**
	 * <code>[firstArgIndex, lastArgIndex]</code>(どちらの値も含む)で、引数の値が変更されていることを、すべてのリスナーに通知する。
	 * @param firstArgIndex		最初の引数を示すインデックス
	 * @param lastArgIndex		最後の引数を示すインデックス
	 */
	public void fireUpdateArgumentValues(int firstArgIndex, int lastArgIndex) {
		fireDataChanged(new FilterValuesEditModelEvent(this, firstArgIndex, lastArgIndex, FilterValuesEditModelEvent.UPDATE_ARGUMENT_VALUE));
	}
	
	/**
	 * すべての引数の説明が変更されている可能性があることを、すべてのリスナーに通知する。
	 * @param argIndex	引数インデックス
	 */
	public void fireUpdateAllArgumentValues() {
		fireDataChanged(new FilterValuesEditModelEvent(this));
	}

	/**
	 * 実行順序リンクの状態が変更されていることを、すべてのリスナーに通知する。
	 * @since 3.1.0
	 */
	public void fireRunOrderLinkChanged() {
		// エラー情報変更の為、引数インデックスは無効とする
		fireDataChanged(new FilterValuesEditModelEvent(this,
				FilterValuesEditModelEvent.INVALID_ARGUMENT_INDEX,
				FilterValuesEditModelEvent.INVALID_ARGUMENT_INDEX,
				FilterValuesEditModelEvent.UPDATE_RUNORDER_LINK));
	}

	/**
	 * フィルタの定義が変更されていることを、すべてのリスナーに通知する。
	 */
	public void fireDataChanged() {
		fireDataChanged(new FilterValuesEditModelEvent(FilterValuesEditModelEvent.UPDATE_DEFINITION, this));
	}

	/**
	 * このデータモデルのリスナーとして登録されているすべてのリスナーに、指定された通知イベントを転送する。
	 * @param e		転送されるイベント
	 */
	public void fireDataChanged(FilterValuesEditModelEvent e) {
		Object[] listeners = _listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==IFilterValuesEditModelListener.class) {
				((IFilterValuesEditModelListener)listeners[i+1]).dataChanged(e);
			}
		}
	}

	//------------------------------------------------------------
	// Internal events
	//------------------------------------------------------------

	/**
	 * モジュールデータそのものが変更されたときに呼び出される内部イベントハンドラ。
	 * <em>withoutEvent</em> が <tt>true</tt> の場合のみ、変更イベントを発行する。
	 * @param withoutEvent	イベントを発行する場合は <tt>true</tt>
	 * @param oldData	変更前のデータ
	 * @param newData	変更後のデータ
	 */
	protected void onChangedData(boolean withoutEvent, ModuleRuntimeData oldData, ModuleRuntimeData newData) {
		//System.out.println("AbFilterValueEditModel#onChangedData : withoutEvent=" + withoutEvent + ", oldData=" + String.valueOf(oldData) + ", newData=" + String.valueOf(newData));
		if (!withoutEvent) {
			fireDataChanged();
		}
	}

	/**
	 * 引数説明が変更されたときに呼び出される内部イベントハンドラ。
	 * <em>withoutEvent</em> が <tt>true</tt> の場合のみ、変更イベントを発行する。
	 * @param withoutEvent	イベントを発行する場合は <tt>true</tt>
	 * @param argIndex	対象の引数位置を示すインデックス(0～)
	 * @param arg		対象引数オブジェクト
	 * @param oldDesc	変更前のデータ
	 * @param newDesc	変更後のデータ
	 */
	protected void onChangedArgumentDescription(boolean withoutEvent, int argIndex, ModuleArgConfig arg, String oldDesc, String newDesc) {
		//System.out.println("AbFilterValueEditModel#onChangedArgumentDescription : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldDesc=" + String.valueOf(oldDesc) + ", newDesc=" + String.valueOf(newDesc));
		if (!withoutEvent) {
			fireUpdateArgumentDescription(argIndex);
		}
	}

	/**
	 * 引数値が変更されたときに呼び出される内部イベントハンドラ。
	 * <em>withoutEvent</em> が <tt>true</tt> の場合のみ、変更イベントを発行する。
	 * @param withoutEvent	イベントを発行する場合は <tt>true</tt>
	 * @param argIndex	対象の引数位置を示すインデックス(0～)
	 * @param arg		対象引数オブジェクト
	 * @param oldValue	変更前のデータ
	 * @param newValue	変更後のデータ
	 */
	protected void onChangedArgumentValue(boolean withoutEvent, int argIndex, ModuleArgConfig arg, Object oldValue, Object newValue) {
		//System.out.println("AbFilterValueEditModel#onChangedArgumentValue : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldValue=" + String.valueOf(oldValue) + ", newValue=" + String.valueOf(newValue));
		if (!withoutEvent) {
			fireUpdateArgumentValue(argIndex);
		}
	}

	/**
	 * 引数に対するテンポラリ出力指定が変更されたときに呼び出される内部イベントハンドラ。
	 * <em>withoutEvent</em> が <tt>true</tt> の場合のみ、変更イベントを発行する。
	 * @param withoutEvent	イベントを発行する場合は <tt>true</tt>
	 * @param argIndex	対象の引数位置を示すインデックス(0～)
	 * @param arg		対象引数オブジェクト
	 * @param oldFlag	変更前の状態
	 * @param newFlag	変更後の状態
	 */
	protected void onChangedArgumentOutToTempEnabled(boolean withoutEvent, int argIndex, ModuleArgConfig arg, boolean oldFlag, boolean newFlag) {
		//System.out.println("AbFilterValueEditModel#onChangedArgumentOutToTempEnabled : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldFlag=" + oldFlag + ", newFlag=" + newFlag);
		if (!withoutEvent) {
			fireUpdateArgumentValue(argIndex);
		}
	}

	/**
	 * 引数に対するテンポラリ出力用プレフィックスが変更されたときに呼び出される内部イベントハンドラ。
	 * <em>withoutEvent</em> が <tt>true</tt> の場合のみ、変更イベントを発行する。
	 * @param withoutEvent	イベントを発行する場合は <tt>true</tt>
	 * @param argIndex	対象の引数位置を示すインデックス(0～)
	 * @param arg		対象引数オブジェクト
	 * @param oldPrefix	変更前のデータ
	 * @param newPrefix	変更後のデータ
	 */
	protected void onChangedArgumentTempFilePrefix(boolean withoutEvent, int argIndex, ModuleArgConfig arg, String oldPrefix, String newPrefix) {
		//System.out.println("AbFilterValueEditModel#onChangedArgumentTempFilePrefix : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldPrefix=" + String.valueOf(oldPrefix) + ", newPrefix=" + String.valueOf(newPrefix));
		if (!withoutEvent) {
			fireUpdateArgumentValue(argIndex);
		}
	}

	/**
	 * 引数のエラー状態が変更されたときに呼び出される内部イベントハンドラ。
	 * <em>withoutEvent</em> が <tt>true</tt> の場合のみ、変更イベントを発行する。
	 * @param withoutEvent	イベントを発行する場合は <tt>true</tt>
	 * @param argIndex	対象の引数位置を示すインデックス(0～)
	 * @param arg		対象引数オブジェクト
	 * @param oldError	変更前のエラー状態
	 * @param newError	変更後のエラー状態
	 */
	protected void onChangedArgumentError(boolean withoutEvent, int argIndex, ModuleArgConfig arg, Object oldError, Object newError) {
		//System.out.println("AbFilterValueEditModel#onChangedArgumentError : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldError=" + String.valueOf(oldError) + ", newError=" + String.valueOf(newError));
		if (!withoutEvent) {
			fireUpdateArgumentValue(argIndex);
		}
	}

	/**
	 * このモジュールの実行順序リンクエラー状態が変更されたときに呼び出される内部イベントハンドラ。
	 * <em>withoutEvent</em> が <tt>true</tt> の場合のみ、変更イベントを発行する。
	 * @param withoutEvent	イベントを発行する場合は <tt>true</tt>
	 * @param oldError	変更前のエラー状態
	 * @param newError	変更後のエラー状態
	 * @since 3.1.0
	 */
	protected void onChangedRunOrderLinkError(boolean withoutEvent, Object oldError, Object newError) {
		//System.out.println("AbFilterValueEditModel#onChangedRunOrderLinkError : withoutEvent=" + withoutEvent + ", oldError=" + String.valueOf(oldError) + ", newError=" + String.valueOf(newError));
		if (!withoutEvent) {
			fireRunOrderLinkChanged();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定された引数インデックスに対応する引数値の正当性をチェックする。
	 * @param argIndex	引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @return	引数値が正当であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	protected boolean verifyArgument(int argIndex, boolean withoutEvent) {
		ModuleArgConfig argdata = getArgument(argIndex);
		switch (argdata.getType()) {
			case IN :
				return verifyInputArgumentValue(argIndex, withoutEvent, argdata);
			case OUT :
				return verifyOutputArgumentValue(argIndex, withoutEvent, argdata);
			case PUB :
				return verifyPublishArgumentValue(argIndex, withoutEvent, argdata);
			case SUB :
				return verifySubscribeArgumentValue(argIndex, withoutEvent, argdata);
			default :
				return verifyStringArgumentValue(argIndex, withoutEvent, argdata);
		}
	}

	/**
	 * 文字列属性の引数値が正当かを判定する。
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	abstract protected boolean verifyStringArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata);

	/**
	 * 入力ファイル属性の引数値が正当かを判定する。
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	abstract protected boolean verifyInputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata);
	
	/**
	 * 出力ファイル属性の引数値が正当かを判定する。
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	abstract protected boolean verifyOutputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata);
	
	/**
	 * [PUB]属性の引数値が正当かを判定する。
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	abstract protected boolean verifyPublishArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata);
	
	/**
	 * [SUB]属性の引数値が正当かを判定する。
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	abstract protected boolean verifySubscribeArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata);
	
	/**
	 * 指定されたインデックスに対応する引数の説明を設定する。
	 * @param argIndex		引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param newDesc		新しい説明の文字列
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	protected boolean setArgumentDescription(int argIndex, boolean withoutEvent, String newDesc) {
		boolean modified = false;
		ModuleArgConfig arg = _moduleData.getArgument(argIndex);
		String oldDesc = arg.getDescription();
		if (!Objects.isEqual(oldDesc, (newDesc==null ? "" : newDesc))) {
			modified = true;
			arg.setDescription(newDesc);
			onChangedArgumentDescription(withoutEvent, argIndex, arg, oldDesc, newDesc);
		}
		return modified;
	}
	
	/**
	 * 指定されたインデックスに対応する引数の値を設定する。
	 * @param argIndex		引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param newValue		新しい引数の値
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 * @throws IllegalStateException		フィルタ引数が固定値の場合
	 */
	protected boolean setArgumentValue(int argIndex, boolean withoutEvent, Object newValue) {
		boolean modified = false;
		ModuleArgConfig arg = _moduleData.getArgument(argIndex);
		Object oldValue = arg.getValue();
		if (!Objects.isEqual(oldValue, newValue)) {
			modified = true;
			arg.setValue(newValue);
			onChangedArgumentValue(withoutEvent, argIndex, arg, oldValue, newValue);
		}
		return modified;
	}
	
	/**
	 * 指定されたインデックスに対応する引数に、テンポラリファイルへの出力を設定する。
	 * @param argIndex		引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param toEnable		テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 * @throws IllegalStateException		フィルタ引数が固定値の場合
	 */
	protected boolean setArgumentOutToTempEnable(int argIndex, boolean withoutEvent, boolean toEnable) {
		boolean modified = false;
		ModuleArgConfig arg = _moduleData.getArgument(argIndex);
		boolean oldFlag = arg.getOutToTempEnabled();
		if (oldFlag != toEnable) {
			modified = true;
			arg.setOutToTempEnabled(toEnable);
			onChangedArgumentOutToTempEnabled(withoutEvent, argIndex, arg, oldFlag, toEnable);
		}
		return modified;
	}
	
	/**
	 * 指定されたインデックスに対応する引数に、テンポラリファイル用プレフィックスを設定する。
	 * @param argIndex		引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param newPrefix		テンポラリファイルのプレフィックス、指定しない場合は <tt>null</tt> もしくは長さが 0 の文字列
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	protected boolean setArgumentTempFilePrefix(int argIndex, boolean withoutEvent, String newPrefix) {
		boolean modified = false;
		ModuleArgConfig arg = _moduleData.getArgument(argIndex);
		String oldPrefix = arg.getTempFilePrefix();
		if (!Objects.isEqual(oldPrefix, newPrefix)) {
			modified = true;
			arg.setTempFilePrefix(newPrefix);
			onChangedArgumentTempFilePrefix(withoutEvent, argIndex, arg, oldPrefix, newPrefix);
		}
		return modified;
	}

	/**
	 * このモジュールに対する実行順序リンクエラーをクリアする。
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @return エラー状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	protected boolean clearRunOrderLinkError(boolean withoutEvent) {
		return setRunOrderLinkError(withoutEvent, null);
	}

	/**
	 * このモジュールに対して、指定された実行順序リンクエラーオブジェクトを設定する。
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param err	エラーオブジェクト、または <tt>null</tt>
	 * @return エラー状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalStateException	<em>err</em> が <tt>null</tt> ではなく、モジュールデータが <tt>null</tt> の場合
	 * @since 3.1.0
	 */
	protected boolean setRunOrderLinkError(boolean withoutEvent, FilterDataError err) {
		if (err != null) {
			if (_moduleData == null)
				throw new IllegalStateException("'_moduleData' object is null.");
			FilterDataError oldErr = _moduleData.getModuleError();
			if (_moduleData.setModuleError(err)) {
				onChangedRunOrderLinkError(withoutEvent, oldErr, err);
				return true;
			}
		}
		else if (_moduleData != null) {
			// err==null のため、エラークリア
			FilterDataError oldErr = _moduleData.getModuleError();
			if (_moduleData.clearModuleError()) {
				onChangedRunOrderLinkError(withoutEvent, oldErr, null);
				return true;
			}
		}
		
		// 変更なし
		return true;
	}

	/**
	 * 指定されたインデックスに対応する引数のエラーをクリアする。
	 * @param argIndex		引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @return	エラー状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	protected boolean clearArgumentError(int argIndex, boolean withoutEvent) {
		return setArgumentErrorObject(argIndex, withoutEvent, null);
	}

	/**
	 * 指定されたインデックスに対応する引数にエラーオブジェクトを設定する。
	 * @param argIndex	引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param err	エラーオブジェクト、または <tt>null</tt>
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	protected boolean setArgumentErrorObject(int argIndex, boolean withoutEvent, Object err) {
		boolean modified = false;
		ModuleArgConfig arg = _moduleData.getArgument(argIndex);
		Object oldErr = arg.getUserData();
		if (!Objects.isEqual(oldErr, err)) {
			modified = true;
			_moduleData.getArgument(argIndex).setUserData(err);
			onChangedArgumentError(withoutEvent, argIndex, arg, oldErr, err);
		}
		return modified;
	}

	/**
	 * 指定されたインデックスに対応する引数に、指定されたメッセージを格納するエラーオブジェクトを設定する。
	 * <em>errmsg</em> が <tt>null</tt> もしくは空文字列の場合、エラーはクリアされる。
	 * @param argIndex	引数インデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param errmsg	エラーメッセージ
	 * @return 値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	引数インデックスが適切ではない場合
	 */
	protected boolean setArgumentErrorString(int argIndex, boolean withoutEvent, String errmsg) {
		FilterError err;
		if (Strings.isNullOrEmpty(errmsg)) {
			err = null;
		} else {
			err = new FilterError(errmsg);
		}
		return setArgumentErrorObject(argIndex, withoutEvent, err);
	}

	/**
	 * フィルタデータが存在しない場合に例外をスローする。
	 * @throws IllegalStateException	フィルタデータが <tt>null</tt> の場合
	 */
	protected void ensureData() {
		if (_moduleData == null) {
			throw new IllegalStateException("No such data.");
		}
	}

	/**
	 * フィルタデータが存在しない場合に例外をスローする。
	 * @throws IndexOutOfBoundsException	フィルタデータが <tt>null</tt> の場合
	 */
	protected void ensureArgument() {
		if (_moduleData == null) {
			throw new IndexOutOfBoundsException("No such data.");
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
