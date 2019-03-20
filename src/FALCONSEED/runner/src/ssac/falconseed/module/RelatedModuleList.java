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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)RelatedModuleList.java	2.0.0	2012/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RelatedModuleList.java	1.22	2012/08/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import ssac.aadl.module.ModuleArgType;

/**
 * モジュールの実行時引数値を格納するオブジェクトのリスト。
 * このクラスでは、モジュールの引数関連付け情報を保持し、テンポラリ出力などの
 * 引数値正規化などを行う。
 * 
 * @version 2.0.0	2012/10/15
 * @since 1.22
 */
public class RelatedModuleList implements Iterable<ModuleRuntimeData>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** オブジェクトの状態：モジュール実行定義データが存在しない **/
	static public final int EMPTY			= 0;
	/** オブジェクトの状態：関連付けが行われていないモジュール実行定義データが存在する **/
	static public final int MODIFIED		= 1;
	/** オブジェクトの状態：関連付けが完了している状態。引数値にも対応する引数IDが格納されている。 **/
	static public final int RELATED		= 2;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** モジュール実行定義情報のリスト **/
	private final ArrayList<ModuleRuntimeData>		_datalist;
	/** モジュール引数の関連マップ **/
	private final ModuleArgRelationMap				_argrel;

	/** このオブジェクトの状態 **/
	private int	_state;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public RelatedModuleList() {
		this._datalist = new ArrayList<ModuleRuntimeData>();
		this._argrel = new ModuleArgRelationMap();
		_state = EMPTY;
	}
	
	public RelatedModuleList(int initialCapacity) {
		this._datalist = new ArrayList<ModuleRuntimeData>(initialCapacity);
		this._argrel = new ModuleArgRelationMap();
		_state = EMPTY;
	}
	
	public RelatedModuleList(Collection<? extends ModuleRuntimeData> c) {
		this._datalist = new ArrayList<ModuleRuntimeData>(c.size());
		for (ModuleRuntimeData data : c) {
			if (c == null)
				throw new IllegalArgumentException("ModuleRuntimeData collection included Null!");
			_datalist.add(data);
		}
		this._argrel = new ModuleArgRelationMap();
		this._state = (_datalist.isEmpty() ? EMPTY : MODIFIED);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Iterator<ModuleRuntimeData> iterator() {
		return Collections.unmodifiableList(_datalist).iterator();
	}
	
	public int getStatus() {
		return _state;
	}
	
	public String getStatusString() {
		switch (_state) {
			case EMPTY :
				return "EMPTY";
			case MODIFIED :
				return "MODIFIED";
			case RELATED :
				return "RELATED";
			default :
				return String.format("Unknown(%d)", _state);
		}
	}
	
	public boolean isEmpty() {
		return _datalist.isEmpty();
	}

	public int size() {
		return _datalist.size();
	}
	
	public void clear() {
		undoAllRelationsForInArgValue();
		_argrel.clear();
		_datalist.clear();
		_state = EMPTY;
	}

	/**
	 * すべてのモジュールのうち、最小の引数の数を返す。
	 * @since 2.0.0
	 */
	public int getMinArgumentCount() {
		if (_datalist.isEmpty())
			return 0;
		
		int mincnt = Integer.MAX_VALUE;
		for (ModuleRuntimeData data : _datalist) {
			int cnt = data.getArgumentCount();
			if (mincnt > cnt) {
				mincnt = cnt;
			}
		}
		
		return mincnt;
	}

	/**
	 * すべてのモジュールのうち、最大の引数の数を返す。
	 * @since 2.0.0
	 */
	public int getMaxArgumentCount() {
		if (_datalist.isEmpty())
			return 0;
		
		int maxcnt = 0;
		for (ModuleRuntimeData data : _datalist) {
			int cnt = data.getArgumentCount();
			if (maxcnt < cnt) {
				maxcnt = cnt;
			}
		}
		
		return maxcnt;
	}
	
	public void clearRelations() {
		undoAllRelationsForInArgValue();
		_argrel.clear();
		_state = (_datalist.isEmpty() ? EMPTY : MODIFIED);
	}
	
	public int indexOf(ModuleRuntimeData data) {
		return _datalist.indexOf(data);
	}
	
	public ModuleRuntimeData getData(int index) {
		return _datalist.get(index);
	}
	
	public void add(final ModuleRuntimeData data) {
		if (data == null)
			throw new NullPointerException("'data' argument is null.");
		_datalist.add(data);
		_state = MODIFIED;
	}
	
	public void addAll(Collection<? extends ModuleRuntimeData> c) {
		_datalist.ensureCapacity(_datalist.size() + c.size());
		for (ModuleRuntimeData data : c) {
			if (data == null)
				throw new IllegalArgumentException("ModuleRuntimeData collection included Null!");
			_datalist.add(data);
			_state = MODIFIED;
		}
	}
	
	public boolean remove(final ModuleRuntimeData data) {
		int index = _datalist.indexOf(data);
		if (index < 0)
			return false;
		removeRelation(data);
		_datalist.remove(index);
		return true;
	}
	
	public boolean remove(int index) {
		if (index < 0 || index >= _datalist.size())
			return false;
		ModuleRuntimeData data = _datalist.remove(index);
		removeRelation(data);
		return true;
	}

	/**
	 * 現在のモジュール実行定義情報から、引数値のリレーションを更新する。
	 * このとき、引数値が関連付けられた[OUT]引数の位置を示すIDに置き換えられる。
	 */
	public void updateRelations() {
		_argrel.clear();
		
		// 出力引数のマップを構築
		LinkedHashMap<Object, ModuleArgID> outvaluemap = new LinkedHashMap<Object, ModuleArgID>();
		//--- より近い出力関係とするため、先頭から上書き
		for (ModuleRuntimeData data : _datalist) {
			for (IModuleArgConfig arg : data) {
				if (ModuleArgType.OUT == arg.getType()) {
					Object value = arg.getValue();
					if (value != null) {
						outvaluemap.put(value, new ModuleArgID(data, arg.getArgNo()));
					}
				}
				else if (ModuleArgType.IN == arg.getType() && !arg.isFixedValue()) {
					Object value = arg.getValue();
					if (value instanceof ModuleArgID) {
						//--- すでにリレーションが定義されている[IN]引数は、リレーションを保存
						ModuleArgID outArgID = (ModuleArgID)value;
						ModuleArgID inArgID = new ModuleArgID(data, arg.getArgNo());
						_argrel.put(outArgID, inArgID);
					}
					else if (value != null) {
						ModuleArgID outArgID = outvaluemap.get(value);
						if (outArgID != null) {
							//--- [IN] 引数の値書き換え
							ModuleArgID inArgID = new ModuleArgID(data, arg.getArgNo());
							_argrel.put(outArgID, inArgID);
							arg.setValue(outArgID);
						}
					}
				}
			}
		}
		
		_state = (_datalist.isEmpty() ? EMPTY : RELATED);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean removeRelation(final ModuleRuntimeData data) {
		boolean modified = false;
		for (IModuleArgConfig arg : data) {
			if (ModuleArgType.OUT == arg.getType()) {
				ModuleArgID outArgID = new ModuleArgID(data, arg.getArgNo());
				if (undoRelationBySrcArg(outArgID, arg)) {
					modified = true;
				}
			}
			else if (ModuleArgType.IN == arg.getType() && !arg.isFixedValue()) {
				ModuleArgID inArgID = new ModuleArgID(data, arg.getArgNo());
				if (undoRelationBySrcArg(inArgID, arg)) {
					modified = true;
				}
			}
		}
		return modified;
	}
	
	protected boolean undoRelationByDestArg(ModuleArgID destArgID, IModuleArgConfig destArg) {
		boolean modified = false;
		Object destValue = destArg.getValue();
		if (destValue instanceof ModuleArgID) {
			ModuleArgID srcArgID = (ModuleArgID)destValue;
			undoRelationForDestArgValue(destArg, srcArgID);
			modified = true;
		}
		if (_argrel.removeRelations(destArgID)) {
			modified = true;
		}
		return modified;
	}
	
	protected boolean undoRelationBySrcArg(ModuleArgID srcArgID, IModuleArgConfig srcArg) {
		boolean modified = false;
		Object srcValue = srcArg.getValue();
		Set<ModuleArgID> destset = _argrel.getDestinationArgIDs(srcArgID);
		if (destset != null && !destset.isEmpty()) {
			modified = true;
			for (ModuleArgID destArgID : destset) {
				IModuleArgConfig destArg = destArgID.getData().getArgument(destArgID.argNo()-1);
				Object destValue = destArg.getValue();
				if (destValue instanceof ModuleArgID) {
					destArg.setValue(srcValue);
					modified = true;
				}
			}
		}
		if (_argrel.removeRelations(srcArgID)) {
			modified = true;
		}
		return modified;
	}
	
	protected void undoRelationForDestArgValue(IModuleArgConfig destArg, ModuleArgID srcArgID) {
		Object srcValue = srcArgID.getData().getArgument(srcArgID.argNo()-1).getValue();
		destArg.setValue(srcValue);
	}

	protected void undoAllRelationsForInArgValue() {
		for (ModuleRuntimeData data : _datalist) {
			for (IModuleArgConfig arg : data) {
				if (ModuleArgType.IN == arg.getType() && !arg.isFixedValue()) {
					Object value = arg.getValue();
					if (value instanceof ModuleArgID) {
						undoRelationForDestArgValue(arg, (ModuleArgID)value);
					}
				}
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
