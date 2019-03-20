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
 * @(#)MacroSubFilterValuesEditModel.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSubFilterValuesEditModel.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.module.FilterDataError;
import ssac.falconseed.module.FilterErrorType;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.swing.table.AbMacroSubFilterArgValueTableModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Objects;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileManager;

/**
 * マクロフィルタを構成するサブフィルタの実行時引数値を保持するデータモデル。
 * このモデルでは、テンポラリ出力はフラグを使用せず、引数値に <code>MExecArgTempFile</code> インスタンスを
 * 格納する。
 * また、編集時情報として、実行順序リンクのエラー情報についても保持する。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public class MacroSubFilterValuesEditModel extends AbFilterValuesEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * マクロフィルタのデータモデル
	 */
	private final MacroFilterEditModel	_baseModel;
	/**
	 * マクロフィルタのサブフィルタ引数値編集テーブル用モデルオブジェクト
	 */
	private final MacroSubFilterArgValueTableModel	_tableModel;
	
	//
	// このフィルタ(this)から関連付けられているフィルタ(定義含む)のマップ
	//

	/**
	 * このフィルタから参照されている他フィルタ(key)と、
	 * その参照を保持するこのフィルタの引数位置を示すインデックス集合(value)のマップ。
	 * フィルタ定義の引数参照の場合、キーは <tt>_baseModel</tt> となる。
	 */
	private final Map<ModuleRuntimeData, ReferArgIndices>	_mapReferencedFilters;
	/**
	 * 参照となる他フィルタの値(key)と、
	 * その参照を保持するこのフィルタの引数位置を示すインデックス集合(value)のマップ。
	 */
	private final Map<Object, ReferArgIndices>		_mapReferencedValues;
	/** このサブフィルタが、フィルタ定義外であることを示すフラグ **/
	private boolean	_externalModule = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroSubFilterValuesEditModel(MacroFilterEditModel baseModel, ModuleRuntimeData targetData)
	{
		super(targetData);
		if (baseModel == null)
			throw new NullPointerException("'baseModel' is null.");
		this._baseModel = baseModel;
		this._tableModel = new MacroSubFilterArgValueTableModel();
		this._mapReferencedFilters = new HashMap<ModuleRuntimeData, ReferArgIndices>();
		this._mapReferencedValues  = new HashMap<Object, ReferArgIndices>();
		initModuleData(targetData);
	}
	
	private void initModuleData(final ModuleRuntimeData newData) {
		if (newData != null) {
			for (ModuleArgConfig argdata : newData) {
				if (!argdata.isFixedValue()) {
					if (argdata.getOutToTempEnabled()) {
						argdata.setValue(MExecArgTempFile.instance);
					}
					else if (argdata.getValue() instanceof MExecArgTempFile) {
						argdata.setOutToTempEnabled(true);
					}
				}
			}
			setupReferencedMap();
			if (newData != null && newData.getExecDefDirectory() != null) {
				_externalModule = !_baseModel.isIncludedMExecDefWithoutSubFilters(newData.getExecDefDirectory());
			} else {
				_externalModule = false;
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このサブフィルタが、フィルタ定義外に存在する外部ファイルの場合に <tt>true</tt> を返す。
	 */
	public boolean isExternalSubFilter() {
		return _externalModule;
	}

	/**
	 * モジュール実行定義の名前を取得する。
	 * このメソッドでは、外部ファイルの場合に、名前の先頭にアスタリスクを付加する。
	 * @return	モジュール実行定義が存在していればその名前、それ以外の場合は <tt>null</tt>
	 */
	@Override
	public String getName() {
		String name = super.getName();
		if (name != null && isExternalSubFilter()) {
			name = "*".concat(name);
		}
		return name;
	}

	/**
	 * このモデルが管理するフィルタの引数値をクリアし、関連マップをクリアする。
	 * なお、このメソッドでは、値変更のイベントは発行されない。
	 */
	public void cleanupWithoutEvents() {
		for (ModuleArgConfig argData : _moduleData) {
			argData.setUserData(null);
			argData.setOutToTempEnabled(false);
			if (!argData.isFixedValue()) {
				argData.setValue(null);
			}
		}
		_moduleData.clearModuleError();
		clearReferencedMap();
	}

	/**
	 * 指定された抽象パスが、このフィルタ定義内に含まれる場合に <tt>true</tt> を返す。
	 * @param file	判定対象のパス
	 * @return	このフィルタ内のファイルであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean isFilterFile(final VirtualFile file) {
		return file.isDescendingFrom(getExecDefDirectory());
	}

	/**
	 * このフィルタ以外のサブフィルタで、指定された抽象パスが属するサブフィルタを返す。
	 * @param file	判定対象のパス
	 * @return	指定されたファイルが属するサブフィルタを返す。どのサブフィルタにも属さない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public ModuleRuntimeData getFileHavingAnotherFilter(final VirtualFile file) {
		for (ModuleRuntimeData amodule : _baseModel.getSubFilterList()) {
			if (amodule != _moduleData && file.isDescendingFrom(amodule.getExecDefDirectory())) {
				return amodule;
			}
		}
		return null;
	}

	/**
	 * 指定された抽象パスが属するサブフィルタを返す。
	 * このメソッドでは、自身も含まれる。
	 * @param file	判定対象のパス
	 * @return	指定されたファイルが属するサブフィルタを返す。どのサブフィルタにも属さない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public ModuleRuntimeData getFileHavingSubFilter(final VirtualFile file) {
		for (ModuleRuntimeData amodule : _baseModel.getSubFilterList()) {
			if (file.isDescendingFrom(amodule.getExecDefDirectory())) {
				return amodule;
			}
		}
		return null;
	}

	/**
	 * フィルタが編集中であれば <tt>true</tt> を返す。
	 */
	@Override
	public boolean isEditing() {
		return _baseModel.isEditing();
	}

	@Override
	public boolean isArgsEditable() {
		// モジュールデータが存在しない場合は、編集不可
		return (getModuleData()==null ? false : _baseModel.isEditing());
	}

	@Override
	public boolean isArgsHistoryEnabled() {
		// 引数履歴は使用しない
		return false;
	}
	
	public MacroFilterEditModel getEditModel() {
		return _baseModel;
	}
	
	public MacroSubFilterArgValueTableModel getEditorTableModel() {
		return _tableModel;
	}

	/**
	 * このモジュールの実行順序オーダーが正当かを判定する。
	 * 次の条件に一致した場合に正当と判定する。
	 * <ul>
	 * <li>このモジュールが待機するモジュールの実行番号が、このモジュールの実行番号より小さい。
	 * </ul>
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	public boolean verifyRunOrderLink(boolean withoutEvent) {
		// 待機モジュールを格納するモジュールデータではない場合は、正当とみなす
		if (!(_moduleData instanceof MacroSubModuleRuntimeData)) {
			clearRunOrderLinkError(withoutEvent);
			return true;
		}
		
		// 待機モジュールの実行番号をチェック
		MacroSubModuleRuntimeData subModule = (MacroSubModuleRuntimeData)_moduleData;
		long thisRunNo = subModule.getRunNo();
		for (ModuleRuntimeData waitModule : subModule.getWaitModuleSet()) {
			if (waitModule.getRunNo() >= thisRunNo) {
				// 待機モジュールの実行番号は自身の実行番号以上のため、エラー
				FilterDataError err = new FilterDataError(FilterErrorType.FILTER_WAITFILTERS,
														RunnerMessages.getInstance().msgMacroSubFilterWiatTargetInvalidSelected);
				setRunOrderLinkError(withoutEvent, err);
				return false;
			}
		}
		
		// 待機モジュールの実行番号は正当
		clearRunOrderLinkError(withoutEvent);
		return true;
	}

	/**
	 * このサブフィルタの引数値の正当性をチェックする。
	 * @return	すべての引数値が正当であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean verifyAllArguments() {
		if (isEmptyArgument()) {
			return true;		// 引数なし
		}

		boolean available = true;
		for (int index = 0; index < getArgumentCount(); index++) {
			if (!verifyArgument(index)) {
				available = false;
			}
		}
		
		return available;
	}

	/**
	 * 指定された値がこのサブフィルタから参照されている値の場合に <tt>true</tt> を返す。
	 * @param refValue	判定する引数値
	 * @return	参照値であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isReferencedValue(final Object refValue) {
		if (refValue instanceof FilterArgReferValueEditModel) {
			//--- フィルタマクロ定義引数参照（prefix/suffix付き）の場合は、参照元で判定する。
			return _mapReferencedValues.containsKey(((FilterArgReferValueEditModel)refValue).getReferencedArgument());
		} else {
			return _mapReferencedValues.containsKey(refValue);
		}
	}

	/**
	 * 指定された値を参照している、このサブフィルタの引数インデックスをすべて取得する。
	 * @param refValue	判定する引数値
	 * @return	このサブフィルタにおいて値を参照している引数インデックスを保持するオブジェクト、なければ <tt>null</tt>
	 * @since 3.1.0
	 */
	protected ReferArgIndices getReferArgIndicesByArgumentValue(final Object refValue) {
		if (refValue instanceof FilterArgReferValueEditModel) {
			//--- フィルタマクロ定義引数参照（prefix/suffix付き）の場合は、参照元を取得する。
			return _mapReferencedValues.get(((FilterArgReferValueEditModel)refValue).getReferencedArgument());
		} else {
			return _mapReferencedValues.get(refValue);
		}
	}

	/**
	 * 指定された値がこのサブフィルタから参照されている値の場合に、
	 * その引数値をこのフィルタから除去する。
	 * @param refValue		削除する引数値
	 * @return	引数値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeReferencedValue(final Object refValue) {
		boolean modified = false;
		ReferArgIndices ref = getReferArgIndicesByArgumentValue(refValue);
		if (ref != null) {
			for (int argIndex : ref.getIndices()) {
				if (setArgumentValue(argIndex, null)) {
					modified = true;
				}
			}
		}
		return modified;
	}

	/**
	 * 指定された値がこのサブフィルタから参照されている値の場合に、その引数の表示を更新する。
	 * @param refValue			更新する値
	 * @param withoutVerify		引数値正当性チェックを行わない場合は <tt>true</tt>
	 */
	public void updateReferencedValue(final Object refValue, boolean withoutVerify) {
		ReferArgIndices ref = getReferArgIndicesByArgumentValue(refValue);
		if (ref != null) {
			for (int argIndex : ref.getIndices()) {
				if (!withoutVerify) {
					// エラーチェック(イベント発行しない)
					verifyArgument(argIndex, true);
				}
				//--- 表示更新
				fireUpdateArgumentValue(argIndex);
			}
		}
	}

//	/**
//	 * 指定されたフィルタ定義引数が、このサブフィルタから参照されている場合に <tt>true</tt> を返す。
//	 * @param defArgData	判定するフィルタ定義引数データ
//	 * @return	このサブフィルタから参照された定義引数の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean isReferedToMExecDefArgument(final IModuleArgConfig defArgData) {
//		return _mapReferencedValues.containsKey(defArgData);
//	}

	/**
	 * 指定されたフィルタ内のデータ(ファイルや引数)が、このフィルタから参照されている場合に <tt>true</tt> を返す。
	 * @param targetModule	被参照かを判定するフィルタ
	 * @return	参照している場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isReferedSubFilterData(final ModuleRuntimeData targetModule) {
		return _mapReferencedFilters.containsKey(targetModule);
	}
	
	/**
	 * 指定されたフィルタ集合に含まれるフィルタ内のデータ(ファイルや引数)が、
	 * このフィルタから参照されている場合に <tt>true</tt> を返す。
	 * @param targetModule	被参照かを判定するフィルタ
	 * @return	参照している場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isReferedSubFilterData(final Set<? extends ModuleRuntimeData> moduleSet) {
		if (moduleSet != null && !moduleSet.isEmpty() && !isEmptyArgument()) {
			if (moduleSet.size() > _mapReferencedFilters.size()) {
				//--- _mapReferencedFilters.size() < moduleSet.size()
				for (ModuleRuntimeData refdata : _mapReferencedFilters.keySet()) {
					if (moduleSet.contains(refdata)) {
						return true;
					}
				}
			} else {
				//--- moduleSet.size() <= _mapreferencedFilters.size()
				for (ModuleRuntimeData refdata : moduleSet) {
					if (_mapReferencedFilters.containsKey(refdata)) {
						return true;
					}
				}
			}
		}
		return false;
	}

// removed @since 3.1.0
//	/**
//	 * 指定されたフィルタ内のデータ(ファイルや引数)がこのフィルタから参照されている場合、
//	 * その引数値をこのフィルタから除去する。
//	 * @param targetModule	被参照フィルタ
//	 * @return	引数値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean removeReferedSubFilterData(final ModuleRuntimeData targetModule) {
//		boolean modified = false;
//		if (targetModule != null && !isEmptyArgument()) {
//			for (int argIndex = 0; argIndex < getArgumentCount(); argIndex++) {
//				IModuleArgConfig argData = getArgument(argIndex);
//				ReferArgIndices ref = _mapReferencedValues.get(argData.getValue());
//				if (ref != null && ref.refModule() == targetModule) {
//					//--- 引数値をクリア
//					modified = true;
//					setArgumentValue(argIndex, null);	// イベントも発行
//				}
//			}
//		}
//		return modified;
//	}

	/**
	 * 指定された集合に含まれるフィルタ内のデータ(ファイルや引数)がこのフィルタから参照されている場合、
	 * その引数値をこのフィルタから除去する。
	 * @param moduleSet		被参照フィルタの集合
	 * @return	引数値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeReferedSubFilterData(final Set<? extends ModuleRuntimeData> moduleSet) {
		boolean modified = false;
		if (moduleSet != null && !moduleSet.isEmpty() && !isEmptyArgument()) {
			for (int argIndex = 0; argIndex < getArgumentCount(); argIndex++) {
				IModuleArgConfig argData = getArgument(argIndex);
				ReferArgIndices ref = getReferArgIndicesByArgumentValue(argData.getValue());
				if (ref != null && moduleSet.contains(ref.refModule())) {
					//--- 引数値をクリア
					modified = true;
					setArgumentValue(argIndex, null);	// イベントも発行
				}
			}
		}
		return modified;
	}
	
//	/**
//	 * 指定されたフィルタの引数を参照している場合に <tt>true</tt> を返す。
//	 * @param targetModule		参照されているかを検証するフィルタ
//	 * @return	参照している引数がある場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean isReferedSubFilterArgument(final ModuleRuntimeData targetModule) {
//		if (targetModule != null && !isEmptyArgument()) {
//			for (ModuleArgConfig argdata : _moduleData) {
//				if (argdata.getValue() instanceof ModuleArgID) {
//					ModuleArgID argid = (ModuleArgID)argdata.getValue();
//					if (targetModule == argid.getData()) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 指定されたフィルタ集合に含まれるフィルタの引数を参照している場合に <tt>true</tt> を返す。
//	 * @param moduleSet		参照されているかを検証するフィルタの集合
//	 * @return	参照している引数がある場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean isReferedSubFilterArgument(final Set<ModuleRuntimeData> moduleSet) {
//		if (moduleSet != null && !moduleSet.isEmpty() && !isEmptyArgument()) {
//			for (ModuleArgConfig argdata : _moduleData) {
//				if (argdata.getValue() instanceof ModuleArgID) {
//					ModuleArgID argid = (ModuleArgID)argdata.getValue();
//					if (moduleSet.contains(argid.getData())) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}

//	/**
//	 * 引数値がサブフィルタ参照の場合に、その引数値を除去する。
//	 * この場合、引数値には <tt>null</tt> が格納される。
//	 * @return	内容が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean removeAllArgumentValuesReferedSubFilter() {
//		boolean modified = false;
//		if (!isEmptyArgument()) {
//			for (int argIndex = 0; argIndex < _moduleData.getArgumentCount(); argIndex++) {
//				ModuleArgConfig argdata = _moduleData.getArgument(argIndex);
//				if (argdata.getValue() instanceof ModuleArgID) {
//					//--- イベントも発行
//					modified = true;
//					setArgumentValue(argIndex, null);
//				}
//			}
//		}
//		return modified;
//	}
//
//	/**
//	 * 指定されたフィルタの引数を参照している引数の場合、その引数値を除去する。
//	 * この場合、引数値には <tt>null</tt> が格納される。
//	 * @param moduleSet		除去対象とする参照先フィルタ
//	 * @return	内容が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean removeArgumentValueReferSubFilter(final ModuleRuntimeData targetModule) {
//		boolean modified = false;
//		if (!isEmptyArgument() && targetModule != null) {
//			for (int argIndex = 0; argIndex < _moduleData.getArgumentCount(); argIndex++) {
//				ModuleArgConfig argdata = _moduleData.getArgument(argIndex);
//				if (argdata.getValue() instanceof ModuleArgID) {
//					ModuleArgID argid = (ModuleArgID)argdata.getValue();
//					if (targetModule == argid.getData()) {
//						//--- イベントも発行
//						modified = true;
//						setArgumentValue(argIndex, null);
//					}
//				}
//			}
//		}
//		return modified;
//	}
//
//	/**
//	 * 指定されたフィルタ集合に含まれるフィルタの引数を参照している引数の場合、その引数値を除去する。
//	 * この場合、引数値には <tt>null</tt> が格納される。
//	 * @param moduleSet		除去対象とする参照先フィルタの集合
//	 * @return	内容が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean removeArgumentValueReferSubFilter(final Set<ModuleRuntimeData> moduleSet) {
//		boolean modified = false;
//		if (!isEmptyArgument() && moduleSet != null && !moduleSet.isEmpty()) {
//			for (int argIndex = 0; argIndex < _moduleData.getArgumentCount(); argIndex++) {
//				ModuleArgConfig argdata = _moduleData.getArgument(argIndex);
//				if (argdata.getValue() instanceof ModuleArgID) {
//					ModuleArgID argid = (ModuleArgID)argdata.getValue();
//					if (moduleSet.contains(argid.getData())) {
//						//--- イベントも発行
//						modified = true;
//						setArgumentValue(argIndex, null);
//					}
//				}
//			}
//		}
//		return modified;
//	}

	/**
	 * 引数値がサブフィルタ参照の場合に、その引数の表示を更新する。
	 * @param withoutVerify		引数値正当性チェックを行わない場合は <tt>true</tt>
	 */
	public void refreshAllArgumentValuesReferSubFilter(boolean withoutVerify) {
		refreshArgumentValueReferSubFilter(null, withoutVerify);
	}

	/**
	 * 指定されたフィルタ集合に含まれるフィルタ引数を参照している引数の場合、その引数の表示を更新する。
	 * <em>moduleSet</em> が <tt>null</tt> の場合のみ、サブフィルタを参照しているすべての引数値の表示を更新する。
	 * @param moduleSet			参照先フィルタの集合
	 * @param withoutVerify		引数値正当性チェックを行わない場合は <tt>true</tt>
	 */
	public void refreshArgumentValueReferSubFilter(final Collection<? extends ModuleRuntimeData> moduleSet, boolean withoutVerify) {
		if (!isEmptyArgument()) {
			if (moduleSet != null) {
				// 選択的更新
				for (Map.Entry<Object,ReferArgIndices> entry : _mapReferencedValues.entrySet()) {
					//--- 他サブモジュールの引数参照を更新
					if (entry.getKey() instanceof ModuleArgID) {
						ModuleArgID argid = (ModuleArgID)entry.getKey();
						if (moduleSet.contains(argid.getData())) {
							//--- 集合内のフィルタデータに一致するもののみ
							for (int argIndex : entry.getValue().getIndexList()) {
								if (!withoutVerify) {
									// エラーチェック
									verifyArgument(argIndex, true);	// イベントは発行しない
								}
								//--- 表示更新
								fireUpdateArgumentValue(argIndex);
							}
						}
					}
				}
			}
			else {
				// 全更新
				for (Map.Entry<Object,ReferArgIndices> entry : _mapReferencedValues.entrySet()) {
					if (entry.getKey() instanceof ModuleArgID) {
						for (int argIndex : entry.getValue().getIndexList()) {
							if (!withoutVerify) {
								// エラーチェック
								verifyArgument(argIndex, true);	// イベントは発行しない
							}
							//--- 表示更新
							fireUpdateArgumentValue(argIndex);
						}
					}
				}
			}
		}
	}

	/**
	 * 指定された引数インデックスに対応する表示を更新する
	 * @param argIndex	引数インデックス
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public void refreshDisplayArgumentValue(int argIndex) {
		_tableModel.fireArgumentValueUpdated(argIndex);
	}

	@Override
	public VirtualFile getBasePath() {
		if (_baseModel != null) {
			return _baseModel.getAvailableFilterRootDirectory();
		} else {
			return super.getBasePath();
		}
	}

	@Override
	public VirtualFilePathFormatterList getFormatter() {
		if (_baseModel != null) {
			return _baseModel.getPathFormatter();
		} else {
			return super.getFormatter();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * フィルタ引数値の関連付け情報を初期化する。
	 */
	protected void setupReferencedMap() {
		for (int argIndex = 0; argIndex < getArgumentCount(); argIndex++) {
			addToReferencedMap(argIndex, getArgument(argIndex));
		}
	}
	
	protected void clearReferencedMap() {
		if (_mapReferencedFilters != null) {
			for (Map.Entry<ModuleRuntimeData, ReferArgIndices> entry : _mapReferencedFilters.entrySet()) {
				ReferArgIndices ref = entry.getValue();
				if (ref != null) {
					ref.clearIndices();
				}
			}
			for (Map.Entry<Object, ReferArgIndices> entry : _mapReferencedValues.entrySet()) {
				ReferArgIndices ref = entry.getValue();
				if (ref != null) {
					ref.clearIndices();
				}
			}
			_mapReferencedFilters.clear();
			_mapReferencedValues.clear();
		}
	}

	/**
	 * 指定された引数の値が、マクロフィルタ定義引数の参照、もしくは他サブフィルタの引数参照の場合、
	 * このフィルタを主とした引数参照関係マップに追加する。
	 * @param argIndex	対象引数のこのフィルタにおける位置を示すインデックス
	 * @param argData	対象引数(このフィルタの引数定義)
	 * @return	参照関係マップが更新された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean addToReferencedMap(int argIndex, ModuleArgConfig argData) {
		Object value = argData.getValue();
		if (value instanceof IModuleArgConfig) {
			//--- フィルタ定義の引数参照
			return addRelationToReferencedMap(argIndex, value, _baseModel.getModuleData());
		}
		else if (value instanceof ModuleArgID) {
			//--- 他フィルタの引数参照
			return addRelationToReferencedMap(argIndex, value, ((ModuleArgID)value).getData());
		}
		else if ((value instanceof VirtualFile) || (value instanceof File)) {
			VirtualFile vfFile = VirtualFileManager.toVirtualFile(value);
			if (!isFilterFile(vfFile)) {
				// 自サブフィルタには属さないファイル
				ModuleRuntimeData aModule = getFileHavingAnotherFilter(vfFile);
				if (aModule != null) {
					//--- 他サブフィルタに属するファイル
					return addRelationToReferencedMap(argIndex, value, aModule);
				}
				else if (_baseModel.isIncludedMExecDefWithoutSubFilters(vfFile)) {
					//--- マクロフィルタ定義に属するファイル
					return addRelationToReferencedMap(argIndex, value, _baseModel.getModuleData());
				}
			}
		}
		// no change
		return false;
	}

	/**
	 * 指定されたこの引数データの値から、他フィルタとの関連付けがある値の場合に、
	 * 被参照フィルタマップに登録する。
	 * @param argIndex		このフィルタの引数インデックス
	 * @param argValue		このフィルタの引数値
	 * @param refModule		引数値の被参照フィルタのインスタンス
	 * @return	登録された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean addRelationToReferencedMap(int argIndex, final Object argValue, final ModuleRuntimeData refModule) {
		boolean modified = false;
		
		Object refValue = argValue;
		if (argValue instanceof FilterArgReferValueEditModel) {
			//--- フィルタ定義の引数参照(Prefix/Suffix付)
			refValue = ((FilterArgReferValueEditModel)argValue).getReferencedArgument();
		}

		// 値と引数インデックス、被参照モジュールの登録
		ReferArgIndices ref = _mapReferencedValues.get(refValue);
		if (ref != null) {
			//--- インデックスの登録
			assert (ref.refModule() == refModule);
			if (ref.addIndex(argIndex)) {
				modified = true;
			}
		}
		else {
			//--- 新規登録
			modified = true;
			ref = new ReferArgIndices(refModule);
			ref.addIndex(argIndex);
			_mapReferencedValues.put(refValue, ref);
		}
		
		// 被参照モジュールと値の登録
		ref = _mapReferencedFilters.get(refModule);
		if (ref != null) {
			//--- インデックスの登録
			assert (ref.refModule() == refModule);
			if (ref.addIndex(argIndex)) {
				modified = true;
			}
		}
		else {
			//--- 新規登録
			modified = true;
			ref = new ReferArgIndices(refModule);
			ref.addIndex(argIndex);
			_mapReferencedFilters.put(refModule, ref);
		}
		
		return modified;
	}

	/**
	 * 指定された引数値に関連する被参照フィルタを被参照フィルタマップから除外する。
	 * @param argIndex	このフィルタの引数値が保持されている引数いんでっくす
	 * @param argValue	関連付けを除外する、このフィルタの引数値
	 * @return	除外できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean removeRelationFromReferencedMap(int argIndex, Object argValue) {
		Object refValue = argValue;
		if (argValue instanceof FilterArgReferValueEditModel) {
			//--- フィルタ定義の引数参照(Prefix/Suffix付)
			refValue = ((FilterArgReferValueEditModel)argValue).getReferencedArgument();
		}
		
		ReferArgIndices ref = _mapReferencedValues.get(refValue);
		if (ref != null) {
			boolean removed = false;
			//--- remove value
			if (ref.removeIndex(argIndex)) {
				removed = true;
				if (ref.isIndicesEmpty()) {
					//--- remove value entry
					_mapReferencedValues.remove(refValue);
				}
			}
			//--- remove module
			ModuleRuntimeData refModule = ref.refModule();
			ref = _mapReferencedFilters.get(refModule);
			if (ref != null && ref.removeIndex(argIndex)) {
				removed = true;
				if (ref.isIndicesEmpty()) {
					//--- remove module entry
					_mapReferencedFilters.remove(refModule);
				}
			}
			return removed;
		}
		// no change
		return false;
	}

	/**
	 * 文字列属性の引数値が正当かを判定する。
	 * 次の条件に一致した場合に正当と判定する。
	 * <ul>
	 * <li>参照引数が存在し、引数属性が一致している。
	 * <li>文字列が空ではでない。
	 * </ul>
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean verifyStringArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		Object value = argdata.getValue();

		// 引数の有無をチェック
		if (value == null) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredString);
			return false;
		}

		// 参照引数のチェック
		if (value instanceof IModuleArgConfig) {
			// 参照引数の正当性チェック
			IModuleArgConfig refarg = (IModuleArgConfig)value;
			if (refarg.getType() != argdata.getType()) {
				// 引数属性が異なる
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgTypeIsNotSame);
				return false;
			}
			if (refarg.getArgNo() < 1 || refarg.getArgNo() > _baseModel.getMExecDefArgumentCount()) {
				// 引数番号が不正
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgNotFound);
				return false;
			}
			//--- 正当
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// 文字列に変換
		String strValue = value.toString();
		if (strValue.length() > 0) {
			clearArgumentError(argIndex, withoutEvent);
			return true;
		} else {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredString);
			return false;
		}
	}

	/**
	 * 入力ファイル属性の引数値が正当かを判定する。
	 * 次の条件のどれかに一致した場合に正当と判定する。
	 * <ul>
	 * <li>参照引数が存在し、引数属性が一致している。
	 * <li>この引数より前に実行されるフィルタの[OUT]引数が指定されている。
	 * <li>テンポラリファイル指定である。
	 * <li>ファイルのパスが指定されており、そのファイルが存在している。
	 * </ul>
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean verifyInputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		Object value = argdata.getValue();

		// 引数の有無をチェック
		if (value == null) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredInputFile);
			return false;
		}

		// 参照引数のチェック
		if (value instanceof IModuleArgConfig) {
			// 参照引数の正当性チェック
			IModuleArgConfig refarg = (IModuleArgConfig)value;
			if (refarg.getType() != argdata.getType()) {
				// 引数属性が異なる
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgTypeIsNotSame);
				return false;
			}
			if (refarg.getArgNo() < 1 || refarg.getArgNo() > _baseModel.getMExecDefArgumentCount()) {
				// 引数番号が不正
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgNotFound);
				return false;
			}
			
			// 正当
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// リンク引数のチェック
		if (value instanceof ModuleArgID) {
			// リンク引数の正当性チェック
			ModuleArgID argid = (ModuleArgID)value;
			if (argid.getData().getRunNo() >= getModuleData().getRunNo()) {
				// このフィルタ以後に実行されるフィルタの引数が参照されている場合は、エラー
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorLinkedOutArgNoExecuted);
				return false;
			}
			if (argid.getArgument().getType() != ModuleArgType.OUT) {
				// [OUT] 属性ではない引数が参照されている場合は、エラー
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorLinkedOutArgNoExecuted);
				return false;
			}
			
			// 正当
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// ファイルかチェック
		VirtualFile vfFile = VirtualFileManager.toVirtualFile(value);
		if (vfFile == null) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredInputFile);
			return false;
		}
		
		// ファイルがローカルに配置されたファイルかチェック
		if (!_baseModel.isIncludedMExecDef(vfFile)) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorInputFileIsExternal);
			return false;
		}
		
		// ファイルの有無をチェック
		if (!vfFile.exists()) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorInputFileNotFound);
			return false;
		}
		
		// エラーなし
		clearArgumentError(argIndex, withoutEvent);
		return true;
	}
	
	/**
	 * 出力ファイル属性の引数値が正当かを判定する。
	 * 次の条件のどれかに一致した場合に正当と判定する。
	 * <ul>
	 * <li>参照引数が存在し、引数属性が一致している。
	 * <li>テンポラリファイル指定である。
	 * <li>ファイルのパスが指定されている。
	 * </ul>
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean verifyOutputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		// テンポラリ出力をチェック
		if (argdata.getOutToTempEnabled()) {
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}

		// 引数の有無をチェック
		Object value = argdata.getValue();
		if (value == null) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredOutputFile);
			return false;
		}

		// 参照引数のチェック
		if (value instanceof IModuleArgConfig) {
			// 参照引数の正当性チェック
			IModuleArgConfig refarg = (IModuleArgConfig)value;
			if (refarg.getType() != argdata.getType()) {
				// 引数属性が異なる
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgTypeIsNotSame);
				return false;
			}
			if (refarg.getArgNo() < 1 || refarg.getArgNo() > _baseModel.getMExecDefArgumentCount()) {
				// 引数番号が不正
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgNotFound);
				return false;
			}
			
			// 正当
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// テンポラリ出力のチェック
		if (value instanceof MExecArgTempFile) {
			// エラーなし
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// ファイルかチェック
		VirtualFile vfFile = VirtualFileManager.toVirtualFile(value);
		if (vfFile == null) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredOutputFile);
			return false;
		}
		
		// ファイルがローカルに配置されたファイルかチェック
		if (!_baseModel.isIncludedMExecDef(vfFile)) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorOutputFileIsExternal);
			return false;
		}
		
		// エラーなし
		clearArgumentError(argIndex, withoutEvent);
		return true;
	}

	/**
	 * [PUB]属性の引数値が正当かを判定する。
	 * 次の条件に一致した場合に正当と判定する。
	 * <ul>
	 * <li>参照引数が存在し、引数属性が一致している。
	 * <li>文字列が空ではでない。
	 * </ul>
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	protected boolean verifyPublishArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		Object value = argdata.getValue();

		// 引数の有無をチェック
		if (value == null) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredMqttPubAddr);
			return false;
		}

		// 参照引数のチェック
		if (value instanceof IModuleArgConfig) {
			// 参照引数の正当性チェック
			IModuleArgConfig refarg = (IModuleArgConfig)value;
			if (refarg.getType() != argdata.getType()) {
				// 引数属性が異なる
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgTypeIsNotSame);
				return false;
			}
			if (refarg.getArgNo() < 1 || refarg.getArgNo() > _baseModel.getMExecDefArgumentCount()) {
				// 引数番号が不正
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgNotFound);
				return false;
			}
			//--- 正当
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// 文字列に変換
		String strValue = value.toString();
		if (strValue.length() > 0) {
			clearArgumentError(argIndex, withoutEvent);
			return true;
		} else {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredMqttPubAddr);
			return false;
		}
	}

	/**
	 * [SUB]属性の引数値が正当かを判定する。
	 * 次の条件のどれかに一致した場合に正当と判定する。
	 * <ul>
	 * <li>参照引数が存在し、引数属性が一致している。
	 * <li>この引数より前に実行されるフィルタの[PUB]引数が指定されている。
	 * <li>文字列が空ではでない。
	 * </ul>
	 * @param argIndex	対象引数のインデックス
	 * @param withoutEvent	イベントを発行しない場合は <tt>true</tt>
	 * @param argdata	対象引数データ
	 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	protected boolean verifySubscribeArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		Object value = argdata.getValue();

		// 引数の有無をチェック
		if (value == null) {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredMqttSubAddr);
			return false;
		}

		// 参照引数のチェック
		if (value instanceof IModuleArgConfig) {
			// 参照引数の正当性チェック
			IModuleArgConfig refarg = (IModuleArgConfig)value;
			if (refarg.getType() != argdata.getType()) {
				// 引数属性が異なる
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgTypeIsNotSame);
				return false;
			}
			if (refarg.getArgNo() < 1 || refarg.getArgNo() > _baseModel.getMExecDefArgumentCount()) {
				// 引数番号が不正
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorReferArgNotFound);
				return false;
			}
			
			// 正当
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// リンク引数のチェック
		if (value instanceof ModuleArgID) {
			// リンク引数の正当性チェック
			ModuleArgID argid = (ModuleArgID)value;
			if (argid.getData().getRunNo() >= getModuleData().getRunNo()) {
				// このフィルタ以後に実行されるフィルタの引数が参照されている場合は、エラー
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorLinkedPubArgNoExecuted);
				return false;
			}
			if (argid.getArgument().getType() != ModuleArgType.PUB) {
				// [PUB] 属性ではない引数が参照されている場合は、エラー
				setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorLinkedPubArgNoExecuted);
				return false;
			}
			
			// 正当
			clearArgumentError(argIndex, withoutEvent);
			return true;
		}
		
		// 文字列に変換
		String strValue = value.toString();
		if (strValue.length() > 0) {
			clearArgumentError(argIndex, withoutEvent);
			return true;
		} else {
			setArgumentErrorString(argIndex, withoutEvent, RunnerMessages.getInstance().msgErrorRequiredMqttSubAddr);
			return false;
		}
	}

	@Override
	protected void onChangedData(boolean withoutEvent, ModuleRuntimeData oldData, ModuleRuntimeData newData) {
		//System.out.println("MacroSubFilterValuesEditModel#onChangedData : withoutEvent=" + withoutEvent + ", oldData=" + String.valueOf(oldData) + ", newData=" + String.valueOf(newData));
		clearReferencedMap();
		//--- データの整形
		initModuleData(newData);
		super.onChangedData(withoutEvent, oldData, newData);
	}

	@Override
	protected void onChangedArgumentError(boolean withoutEvent, int argIndex, ModuleArgConfig arg, Object oldError, Object newError) {
		//System.out.println("MacroSubFilterValuesEditModel#onChangedArgumentError : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldError=" + String.valueOf(oldError) + ", newError=" + String.valueOf(newError));
		// イベント発行
		if (!withoutEvent) {
			_tableModel.fireArgumentValueUpdated(argIndex);
		}
		super.onChangedArgumentError(withoutEvent, argIndex, arg, oldError, newError);
	}

	@Override
	protected void onChangedArgumentOutToTempEnabled(boolean withoutEvent, int argIndex, ModuleArgConfig arg, boolean oldFlag, boolean newFlag) {
		//System.out.println("MacroSubFilterValuesEditModel#onChangedArgumentOutToTempEnabled : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldFlag" + oldFlag + ", newFlag=" + newFlag);
		//--- 引数値を調整
		Object oldValue = arg.getValue();
		Object newValue = (newFlag ? MExecArgTempFile.instance : null);
		if (!Objects.isEqual(oldValue, newValue)) {
			removeRelationFromReferencedMap(argIndex, oldValue);
			arg.setValue(newValue);
		}
		//--- 引数のエラーチェック
		verifyArgument(argIndex, true);	// イベント発行しない
		// イベント発行
		if (!withoutEvent) {
			_tableModel.fireArgumentValueUpdated(argIndex);
		}
		super.onChangedArgumentOutToTempEnabled(withoutEvent, argIndex, arg, oldFlag, newFlag);
	}

	@Override
	protected void onChangedArgumentValue(boolean withoutEvent, int argIndex, ModuleArgConfig arg, Object oldValue, Object newValue) {
		//System.out.println("MacroSubFilterValuesEditModel#onChangedArgumentValue : withoutEvent=" + withoutEvent + ", argIndex=" + argIndex + ", arg=" + String.valueOf(arg) + ", oldValue=" + String.valueOf(oldValue) + ", newValue=" + String.valueOf(newValue));
		//--- テンポラリ出力フラグを調整
		arg.setOutToTempEnabled(newValue instanceof MExecArgTempFile);
		//--- 参照関係を破棄
		removeRelationFromReferencedMap(argIndex, oldValue);
		//--- 新しい参照関係を追加
		addToReferencedMap(argIndex, arg);
		//--- 引数のエラーチェック
		verifyArgument(argIndex, true);	// イベント発行しない
		// イベント発行
		if (!withoutEvent) {
			_tableModel.fireArgumentValueUpdated(argIndex);
		}
		super.onChangedArgumentValue(withoutEvent, argIndex, arg, oldValue, newValue);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	public class MacroSubFilterArgValueTableModel extends AbMacroSubFilterArgValueTableModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (!isArgsEditable()) {
				//--- 引数値編集不可
				return false;
			}
			
			if (columnIndex == valueColumnIndex()) {
				ModuleArgType type = getArgumentAttr(rowIndex);
				if (ModuleArgType.IN == type || ModuleArgType.OUT == type) {
					// ファイル引数の場合は編集不可
					return false;
				}
				else {
					// 文字列なら、実行時可変の場合のみ直接編集可能
					return !isArgumentFixedValue(rowIndex);
				}
			}
			else {
				// 値列以外は編集不可
				return false;
			}
		}

		@Override
		public int getRowCount() {
			return MacroSubFilterValuesEditModel.this.getArgumentCount();
		}

		@Override
		public ModuleArgConfig getArgument(int rowIndex) {
			return MacroSubFilterValuesEditModel.this.getModuleData().getArgument(rowIndex);
		}

		@Override
		public ModuleArgConfig getArgumentValue(int rowIndex) {
			// この呼び出しによる取得される値は、引数の値オブジェクトではなく、値を保持する引数定義そのものである必要がある
			return getArgument(rowIndex);
		}

		@Override
		public void setArgumentValue(int rowIndex, Object newValue) {
			if (MacroSubFilterValuesEditModel.this.setArgumentValue(rowIndex, newValue)) {
				// modified
				fireTableCellUpdated(rowIndex, valueColumnIndex());
			}
		}
		
		public void fireArgumentValueUpdated(int rowIndex) {
			fireTableCellUpdated(rowIndex, valueColumnIndex());
		}
	}
	
	static protected class ReferArgIndices
	{
		/** このフィルタから参照しているフィルタデータ **/
		private final ModuleRuntimeData	_refModule;
		private final List<Integer>		_thisArgIndices = new ArrayList<Integer>(2);
		
		protected ReferArgIndices(final ModuleRuntimeData refModule) {
			_refModule = refModule;
		}
		
		public ModuleRuntimeData refModule() {
			return _refModule;
		}
		
		public void clearIndices() {
			_thisArgIndices.clear();
		}
		
		public boolean isIndicesEmpty() {
			return _thisArgIndices.isEmpty();
		}
		
		public boolean containsIndex(int argIndex) {
			return _thisArgIndices.contains(argIndex);
		}
		
		public Integer[] getIndices() {
			return _thisArgIndices.toArray(new Integer[_thisArgIndices.size()]);
		}
		
		public List<Integer> getIndexList() {
			return _thisArgIndices;
		}

		/**
		 * 引数インデックスを登録する
		 * @param argIndex	登録するインデックス
		 * @return	新たにインデックスが登録された場合は <tt>true</tt>、すでに登録済みの場合は <tt>false</tt>
		 */
		public boolean addIndex(int argIndex) {
			boolean modified = false;
			if (!_thisArgIndices.contains(argIndex)) {
				modified = true;
				_thisArgIndices.add(argIndex);
			}
			return modified;
		}

		/**
		 * 引数インデックスを削除する
		 * @param argIndex	削除するインデックス
		 * @return	削除された場合は <tt>true</tt>、インデックスが未登録の場合は <tt>false</tt>
		 */
		public boolean removeIndex(int argIndex) {
			int pos = _thisArgIndices.indexOf(argIndex);
			if (pos < 0) {
				// no entry
				return false;
			} else {
				// remove
				_thisArgIndices.remove(pos);
				return true;
			}
		}
	}
}
