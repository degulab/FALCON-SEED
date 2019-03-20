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
 * @(#)MacroSubModuleRuntimeData.java	3.1.0	2014/05/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.MExecArgsModel;

/**
 * フィルタマクロのサブモジュールについて、実行時のモジュール引数と実行結果を保持するクラス。
 * このクラスは、{@link ModuleRuntimeData} クラスの拡張であり、待機モジュール情報
 * などを保持する。
 * <p>このオブジェクトのクローン時には、ユーザー定義データはシャローコピーとなり、
 * 編集時データはディープコピー、
 * モジュール実行順序は、実行番号順とする設定となる。
 * 
 * @version 3.1.0	2014/05/19
 * @since 1.22
 */
public class MacroSubModuleRuntimeData extends ModuleRuntimeData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final long[] EMPTY_RUNNO_ARRAY	= new long[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * 任意のモジュール実行順序リンクを保持する場合は <tt>true</tt>
	 * モジュールの実行順序を指定する場合は <tt>true</tt>、
	 * 実行番号通りに実行する場合は <tt>false</tt>。
	 * デフォルトは <tt>false</tt>
	 */
	private boolean						_runOrderLinkEnable = false;
	/**
	 * 終了を待機するモジュールのセット
	 */
	private HashSet<MacroSubModuleRuntimeData>	_waitModules;
	/**
	 * このモジュールの実行完了後に実行されるモジュールのセット
	 */
	private HashSet<MacroSubModuleRuntimeData>	_nextModules;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroSubModuleRuntimeData() {
		super();
		//--- デフォルトの編集時データ
		setEditData(createDefaultEditData());
	}
	
	public MacroSubModuleRuntimeData(final MExecDefSettings settings) {
		super(settings);
		//--- デフォルトの編集時データ
		setEditData(createDefaultEditData());
	}

	/**
	 * 互換性のためのコンストラクタ
	 * @param argsmodel	モジュール実行時引数のデータモデル
	 */
	public MacroSubModuleRuntimeData(final MExecArgsModel argsmodel) {
		super(argsmodel);
		//--- デフォルトの編集時データ
		setEditData(createDefaultEditData());
	}
	
	/**
	 * コピーコンストラクタ
	 */
	public MacroSubModuleRuntimeData(final IModuleConfig<? extends IModuleArgConfig> src) {
		super(src);
		//--- 編集時データのクローンが生成されなければ、デフォルトの編集時データ
		if (!hasEditData()) {
			setEditData(createDefaultEditData());
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このインスタンスに、指定された編集時データを設定する。
	 * このメソッド呼び出しでは、<em>editData</em> に <tt>null</tt> を指定した場合、デフォルトの編集時データが設定される。
	 * @param editData	設定する編集時データ、もしくは <tt>null</tt>
	 * @return	編集時データの内容が変更された場合は <tt>true</tt>
	 * @since 3.1.0
	 */
	@Override
	public boolean setEditData(ModuleEditData editData) {
		return super.setEditData(editData==null ? createDefaultEditData() : editData);
	}

	/**
	 * 任意の実行順序を指定するかどうかの設定を取得する。
	 * @return	任意の実行順序指定が有効であれば <tt>true</tt>、無効であれば <tt>false</tt>
	 */
	public boolean getRunOrderLinkEnabled() {
		return _runOrderLinkEnable;
	}

	/**
	 * 任意の実行順序の有効／無効を設定する。
	 * @param toEnable	任意の実行順序指定を有効にするなら <tt>true</tt>、無効にするなら <tt>false</tt>
	 */
	public void setRunOrderLinkEnabled(boolean toEnable) {
		_runOrderLinkEnable = toEnable;
	}

	/**
	 * 実行完了を待機するモジュールの有無を判定する。
	 * @return	待機モジュールが一つもない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEmptyWaitModules() {
		return (_waitModules==null || _waitModules.isEmpty());
	}

	/**
	 * このモジュール実行完了後に実行すべきモジュールの有無を判定する。
	 * @return	次に実行するモジュールが一つもない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEmptyNextModules() {
		return (_nextModules==null || _nextModules.isEmpty());
	}

	/**
	 * 実行完了を待機するモジュールの総数を返す。
	 * @return	待機するモジュール数
	 */
	public int getWaitModuleCount() {
		return (_waitModules==null ? 0 : _waitModules.size());
	}

	/**
	 * このモジュール実行完了後に実行すべきモジュールの総数を返す。
	 * @return	次に実行するモジュール数
	 */
	public int getNextModuleCount() {
		return (_nextModules==null ? 0 : _nextModules.size());
	}

	/**
	 * 実行完了を待機するモジュールのセットにおける反復子が最初に返すモジュールを取得する。
	 * @return	待機モジュールがあればそのオブジェクト、なければ <tt>null</tt>
	 */
	public MacroSubModuleRuntimeData getFirstWaitModule() {
		MacroSubModuleRuntimeData waitModule = null;
		if (!isEmptyWaitModules()) {
			waitModule = _waitModules.iterator().next();
		}
		return waitModule;
	}

	/**
	 * このモジュール実行完了後に実行すべきモジュールのセットにおける反復子が最初に返すモジュールを取得する。
	 * @return	次に実行するモジュールがあればそのオブジェクト、なければ <tt>null</tt>
	 */
	public MacroSubModuleRuntimeData getFirstNextModule() {
		MacroSubModuleRuntimeData nextModule = null;
		if (!isEmptyNextModules()) {
			nextModule = _nextModules.iterator().next();
		}
		return nextModule;
	}

	/**
	 * 実行完了を待機するモジュールの変更不可能なセットを取得する。
	 * @return	待機モジュールの変更不可能なセット
	 */
	public Set<MacroSubModuleRuntimeData> getWaitModuleSet() {
		if (_waitModules != null && !_waitModules.isEmpty()) {
			return Collections.unmodifiableSet(_waitModules);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * このモジュール実行完了後に実行すべきモジュールの変更不可能なセットを取得する。
	 * @return	次に実行するモジュールの変更不可能なセット
	 */
	public Set<MacroSubModuleRuntimeData> getNextModuleSet() {
		if (_nextModules != null && !_nextModules.isEmpty()) {
			return Collections.unmodifiableSet(_nextModules);
		} else {
			return Collections.emptySet();
		}
	}
	
	/**
	 * 実行完了を待機するモジュールの実行番号を取得する。
	 * なお、実行番号は昇順にソートされる。
	 * @return	待機するモジュールがあればその実行番号を格納した配列、なければ空の配列
	 */
	public long[] getWaitModuleRunNumberArray() {
		long[] numbers = EMPTY_RUNNO_ARRAY;
		if (_waitModules != null && !_waitModules.isEmpty()) {
			numbers = new long[_waitModules.size()];
			int index = 0;
			for (MacroSubModuleRuntimeData data : _waitModules) {
				numbers[index++] = data.getRunNo();
			}
			Arrays.sort(numbers);
		}
		return numbers;
	}
	
	/**
	 * このモジュール実行完了後に実行すべきモジュールの実行番号を取得する。
	 * なお、実行番号は昇順にソートされる。
	 * @return	次に実行するモジュールがあればその実行番号を格納した配列、なければ空の配列
	 */
	public long[] getNextModuleRunNumberArray() {
		long[] numbers = EMPTY_RUNNO_ARRAY;
		if (_nextModules != null && !_nextModules.isEmpty()) {
			numbers = new long[_nextModules.size()];
			int index = 0;
			for (MacroSubModuleRuntimeData data : _nextModules) {
				numbers[index++] = data.getRunNo();
			}
			Arrays.sort(numbers);
		}
		return numbers;
	}

	/**
	 * 実行完了を待機するモジュールの実行番号をカンマ区切りの文字列として取得する。
	 * なお、実行番号は昇順にソートされる。
	 * @return	待機するモジュールがあればその実行番号、なければ空文字列
	 */
	public String getWaitModuleRunNumberString() {
		long[] numbers = getWaitModuleRunNumberArray();
		if (numbers.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(numbers[0]);
			for (int i = 1; i < numbers.length; ++i) {
				sb.append(',');
				sb.append(numbers[i]);
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * このモジュール実行完了後に実行すべきモジュールの実行番号をカンマ区切りの文字列として取得する。
	 * なお、実行番号は昇順にソートされる。
	 * @return	次に実行するモジュールがあればその実行番号、なければ空文字列
	 */
	public String getNextModuleRunNumberString() {
		long[] numbers = getNextModuleRunNumberArray();
		if (numbers.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(numbers[0]);
			for (int i = 1; i < numbers.length; ++i) {
				sb.append(',');
				sb.append(numbers[i]);
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * 指定されモジュールが、待機するモジュールとして登録されているかを判定する。
	 * @param aModule	検査するモジュール
	 * @return	登録されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsWaitModule(MacroSubModuleRuntimeData aModule) {
		return (_waitModules==null ? false : _waitModules.contains(aModule));
	}

	/**
	 * 指定されたモジュールが、次に実行するモジュールとして登録されているかを判定する。
	 * @param aModule	検査するモジュール
	 * @return	登録されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsNextModule(MacroSubModuleRuntimeData aModule) {
		return (_nextModules==null ? false : _nextModules.contains(aModule));
	}
	
	/**
	 * 指定されたモジュールを、待機するモジュールとして登録する。
	 * 指定されたモジュールには、このオブジェクトが次に実行されるモジュールとして登録される。
	 * @param aModule	登録するモジュール
	 * @return	登録された場合は <tt>true</tt>、すでに登録済みの場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean connectWaitModule(MacroSubModuleRuntimeData aModule) {
		if (aModule == null)
			throw new NullPointerException();
		boolean result = addWaitModule(aModule);
		if (result) {
			aModule.addNextModule(this);
		}
		return result;
	}
	
	/**
	 * 指定されたモジュールを、次に実行するモジュールとして登録する。
	 * 指定されたモジュールには、このオブジェクトが待機するモジュールとして登録される。
	 * @param aModule	登録するモジュール
	 * @return	登録された場合は <tt>true</tt>、すでに登録済みの場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean connectNextModule(MacroSubModuleRuntimeData aModule) {
		if (aModule == null)
			throw new NullPointerException();
		boolean result = addNextModule(aModule);
		if (result) {
			aModule.addWaitModule(this);
		}
		return result;
	}

	/**
	 * 指定されたモジュールを、待機するモジュールから除外する。
	 * 指定されたモジュールの次に実行するモジュールからも、このオブジェクトが除外される。
	 * @param aModule	除外するモジュール
	 * @return	除外された場合は <tt>true</tt>、登録されていない場合は <tt>false</tt>
	 */
	public boolean disconnectWaitModule(MacroSubModuleRuntimeData aModule) {
		boolean result = removeWaitModule(aModule);
		if (result) {
			aModule.removeNextModule(this);
		}
		return result;
	}

	/**
	 * 指定されたモジュールを、次に実行するモジュールから除外する。
	 * 指定されたモジュールの待機するモジュールからも、このオブジェクトが除外される。
	 * @param aModule	除外するモジュール
	 * @return	除外された場合は <tt>true</tt>、登録されていない場合は <tt>false</tt>
	 */
	public boolean disconnectNextModule(MacroSubModuleRuntimeData aModule) {
		boolean result = removeNextModule(aModule);
		if (result) {
			aModule.removeWaitModule(this);
		}
		return result;
	}

	/**
	 * 待機するモジュールをすべて除外する。
	 * 待機するモジュールとして登録されていたモジュールの次に実行するモジュールからも、このオブジェクトが除外される。
	 * @return	除外された場合は <tt>true</tt>、待機するモジュールが一つもない場合は <tt>false</tt>
	 */
	public boolean disconnectAllWaitModules() {
		if (isEmptyWaitModules())
			return false;
		
		for (Iterator<MacroSubModuleRuntimeData> it = _waitModules.iterator(); it.hasNext(); ) {
			MacroSubModuleRuntimeData aModule = it.next();
			aModule.removeNextModule(this);
			it.remove();
		}
		return true;
	}

	/**
	 * 次に実行するモジュールをすべて除外する。
	 * 次に実行するモジュールとして登録されていたモジュールの待機するモジュールからも、このオブジェクトが除外される。
	 * @return	除外された場合は <tt>true</tt>、次に実行するモジュールが一つもない場合は <tt>false</tt>
	 */
	public boolean disconnectAllNextModules() {
		if (isEmptyNextModules())
			return false;
		
		for (Iterator<MacroSubModuleRuntimeData> it = _nextModules.iterator(); it.hasNext(); ) {
			MacroSubModuleRuntimeData aModule = it.next();
			aModule.removeWaitModule(this);
			it.remove();
		}
		return true;
	}
	
	/**
	 * このオブジェクトの複製を生成する。
	 * 基本的にディープコピー。
	 */
	@Override
	public MacroSubModuleRuntimeData clone() {
		MacroSubModuleRuntimeData newData = (MacroSubModuleRuntimeData)super.clone();
		newData._runOrderLinkEnable = false;	// default
		newData._waitModules = null;			// clear links
		newData._nextModules = null;			// clear links
		return newData;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたモジュールを、待機するモジュールのセットに追加する。
	 * このメソッドでは、セットへの追加のみで、指定されたモジュールは変更しない。
	 * @param module	追加するモジュール
	 * @return	追加された場合は <tt>true</tt>、すでにセットに含まれている場合は <tt>false</tt>
	 */
	protected boolean addWaitModule(MacroSubModuleRuntimeData module) {
		if (_waitModules == null) {
			_waitModules = new HashSet<MacroSubModuleRuntimeData>();
		}
		return _waitModules.add(module);
	}

	/**
	 * 指定されたモジュールを、次にするモジュールのセットに追加する。
	 * このメソッドでは、セットへの追加のみで、指定されたモジュールは変更しない。
	 * @param module	追加するモジュール
	 * @return	追加された場合は <tt>true</tt>、すでにセットに含まれている場合は <tt>false</tt>
	 */
	protected boolean addNextModule(MacroSubModuleRuntimeData module) {
		if (_nextModules == null) {
			_nextModules = new HashSet<MacroSubModuleRuntimeData>();
		}
		return _nextModules.add(module);
	}
	
	/**
	 * 待機するモジュールのセットから、指定されたモジュールを除外する。
	 * このメソッドでは、セットから除外するのみで、指定されたモジュールは変更しない。
	 * @param module	除外するモジュール
	 * @return	除外された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean removeWaitModule(MacroSubModuleRuntimeData module) {
		if (_waitModules == null) {
			return false;
		} else {
			return _waitModules.remove(module);
		}
	}
	
	/**
	 * 次に実行するモジュールのセットから、指定されたモジュールを除外する。
	 * このメソッドでは、セットから除外するのみで、指定されたモジュールは変更しない。
	 * @param module	除外するモジュール
	 * @return	除外された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean removeNextModule(MacroSubModuleRuntimeData module) {
		if (_nextModules == null) {
			return false;
		} else {
			return _nextModules.remove(module);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
