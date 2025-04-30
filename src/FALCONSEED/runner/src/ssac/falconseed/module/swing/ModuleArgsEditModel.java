/*
 * @(#)ModuleArgsEditModel.java	4.0.0	2021/08/29 : for Java11
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgsEditModel.java	2.0.0	2012/10/11
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgsEditModel.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.io.VirtualFile;

/**
 * モジュール引数の編集データモデル。
 * 一つのモジュールについて、編集に関連するデータを保持する。
 * 
 * @version 4.0.0
 * @since 1.22
 */
public class ModuleArgsEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 引数値を編集可能とする場合は <tt>true</tt> **/
	private final boolean		_argsEditable;
	/** 引数値編集時に引数履歴を利用可能とする場合は <tt>true</tt> **/
	private final boolean		_argsHistoryEnabled;
	/** モジュール引数値定義情報 **/
	private ModuleRuntimeData	_datamodel;
	/** モジュール実行定義の引数履歴 **/
	private MExecDefHistory	_argshistory;
	/** モジュール実行定義内のローカルファイルルートディレクトリ **/
	private VirtualFile		_vfFilesRootDir;
	/** パス整形用フォーマッター **/
	private VirtualFilePathFormatterList	_vfFormatter;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgsEditModel() {
		this(null, false, false);
	}
	
	public ModuleArgsEditModel(boolean argsEditable, boolean argsHistoryEnabled) {
		this(null, argsEditable, argsHistoryEnabled);
	}
	
	public ModuleArgsEditModel(ModuleRuntimeData data, boolean argsEditable, boolean argsHistoryEnabled) {
		this._argsEditable = argsEditable;
		this._argsHistoryEnabled = argsHistoryEnabled;
		setData(data);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数値の編集が可能であれば <tt>true</tt> を返す。
	 */
	public boolean isArgsEditable() {
		return _argsEditable;
	}

	/**
	 * 引数履歴情報が利用可能であれば <tt>true</tt> を返す。
	 */
	public boolean isArgsHistoryEnabled() {
		return (_argsEditable && _argsHistoryEnabled);
	}

	/**
	 * モジュール実行定義の名前を取得する。
	 * @return	モジュール実行定義が存在していればその名前、それ以外の場合は <tt>null</tt>
	 */
	public String getName() {
		return (_datamodel==null ? null : _datamodel.getName());
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
		return (_datamodel==null ? null : _datamodel.getExecDefParentDirectory());
	}

	/**
	 * モジュール実行定義のルートディレクトリを取得する。
	 * @return	モジュール実行定義が存在していればその抽象パス、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFile getExecDefDirectory() {
		return (_datamodel==null ? null : _datamodel.getExecDefDirectory());
	}

	/**
	 * モジュール実行定義内のローカルファイルルートディレクトリを取得する。
	 * @return	モジュール実行定義が存在していれば対応する抽象パス、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFile getExecDefLocalFileRootDirectory() {
		return _vfFilesRootDir;
	}

	/**
	 * パス整形用フォーマッターを取得する。
	 * @return	モジュール実行定義が存在していれば対応するオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFilePathFormatterList getFormatter() {
		return _vfFormatter;
	}

	/**
	 * モジュール実行設定情報を取得する。
	 * @return	モジュール実行設定情報が登録されていればそのオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public ModuleRuntimeData getData() {
		return _datamodel;
	}

	/**
	 * モジュール実行設定情報を設定する。
	 * @param newData	設定するモジュール実行設定情報
	 * @throws IllegalArgumentException	指定されたモジュール実行設定情報に対応するモジュール実行定義が存在しない場合
	 */
	public void setData(final ModuleRuntimeData newData) {
		if (newData == _datamodel)
			return;		// no changes
		
		this._datamodel = newData;
		
		if (newData == null) {
			this._argshistory = null;
			this._vfFilesRootDir = null;
			this._vfFormatter = null;
			return;
		}
		
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
			if (vfHistory != null && vfHistory.exists()) {
				// 読み込みエラー回避の為、引数履歴ファイルが存在する場合のみ読み込む : @since 4.0.0
				_argshistory.loadForTarget(vfHistory);
			}
			_argshistory.ensureArgsTypes(newData);	// 引数型定義にあわない履歴を除去
			_argshistory.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());	// 履歴の最大数を反映
		}
		else {
			_argshistory = null;
		}
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
			if (vfHistory != null && vfHistory.exists()) {
				// 読み込みエラー回避の為、引数履歴ファイルが存在する場合のみ読み込む : @since 4.0.0
				hist.loadForTarget(vfHistory);
			}
			hist.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());
		}
		return hist;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
