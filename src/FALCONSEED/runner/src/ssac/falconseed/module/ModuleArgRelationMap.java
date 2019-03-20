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
 * @(#)ModuleArgRelationMap.java	2.0.0	2012/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgRelationMap.java	1.22	2012/08/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ssac.aadl.module.ModuleArgType;

/**
 * モジュール実行定義引数の <code>source</code> と <code>destination</code> の関連マップ。
 * このリレーションは単方向であり、一つの <code>source</code> に対して複数の <code>destination</code> の関連付けを保持する。
 * ここで、<code>source</code> とは関連付けされる側、<code>destination</code> とは関連付けする側であり、
 * 引数の参照は <code>destination</code> において <code>source</code> の引数の値を参照するという意味となる。
 * 基本的には、[OUT] 引数に関連付けられた [IN] 引数とのリレーションを保持するが、引数属性は特に限定しない。
 * このマップは、<code>destination</code> に関連付けられた唯一の <code>source</code> 引数と、
 * <code>source</code> 引数に関連付けられた複数の <code>destination</code> 引数のマップを保持する。
 * <b>注意：</b>
 * <blockquote>
 * 引数の関連付けにおいて、同一のフィルタIDに属する引数の関連付けは許可しない。
 * </blockquote>
 * 
 * @version 2.0.0	2012/10/15
 * @since 1.22
 */
public class ModuleArgRelationMap implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected HashMap<ModuleArgID, ModuleArgID>		_destmap;	// _inmap
	protected HashMap<ModuleArgID, Set<ModuleArgID>>	_srcmap;	// _outmap

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgRelationMap() {
		this._destmap  = new HashMap<ModuleArgID, ModuleArgID>();
		this._srcmap = new HashMap<ModuleArgID, Set<ModuleArgID>>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 関連付けが一つも定義されていない場合に <tt>true</tt> を返す。
	 */
	public boolean isEmpty() {
		return _destmap.isEmpty();
	}

	/**
	 * 関連付けの総数を返す。
	 * @return	関連付けの総数
	 */
	public int size() {
		return _destmap.size();
	}

	/**
	 * 関連付けをすべて破棄する。
	 */
	public void clear() {
		_destmap.clear();
		for (Set<ModuleArgID> inset : _srcmap.values()) {
			if (inset != null) {
				inset.clear();
			}
		}
		_srcmap.clear();
	}

	/**
	 * 関連付けの <code>source</code> に、指定された引数に相当する引数キーが定義されている場合に <tt>true</tt> を返す。
	 * @param data		モジュール定義情報
	 * @param argno		引数番号
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsSource(final ModuleRuntimeData data, final int argno) {
		if (data != null && argno > 0) {
			return containsSource(new ModuleArgID(data, argno));
		}
		return false;
	}

	/**
	 * 関連付けの <code>source</code> に、指定された引数キーが定義されている場合に <tt>true</tt> を返す。
	 * @param aid	引数キー
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsSource(final ModuleArgID aid) {
		return _srcmap.containsKey(aid);
	}

	/**
	 * 関連付けの <code>destination</code> に、指定された引数に相当する引数キーが定義されている場合に <tt>true</tt> を返す。
	 * @param data		モジュール定義情報
	 * @param argno		引数番号
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsDestination(final ModuleRuntimeData data, final int argno) {
		if (data != null && argno > 0) {
			return containsDestination(new ModuleArgID(data, argno));
		}
		return false;
	}
	
	/**
	 * 関連付けの <code>destination</code> に、指定された引数キーが定義されている場合に <tt>true</tt> を返す。
	 * @param aid	引数キー
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsDestination(final ModuleArgID aid) {
		return _destmap.containsKey(aid);
	}

	/**
	 * 関連付けに、指定された引数に相当する引数キーが定義されている場合に <tt>true</tt> を返す。
	 * @param data		モジュール定義情報
	 * @param argno		引数番号
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean contains(final ModuleRuntimeData data, final int argno) {
		return contains(new ModuleArgID(data, argno));
	}
	
	/**
	 * 関連付けに、指定された引数キーが定義されている場合に <tt>true</tt> を返す。
	 * @param aid	引数キー
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean contains(final ModuleArgID aid) {
		if (_srcmap.containsKey(aid)) {
			return true;
		}
		
		return _destmap.containsKey(aid);
	}

	/** @deprecated **/
	public boolean containsIN(final ModuleRuntimeData data, final int argno) {
		return containsIN(new ModuleArgID(data, argno));
	}
	
	/** @deprecated **/
	public boolean containsIN(final ModuleArgID aid) {
		return _destmap.containsKey(aid);
	}
	
	/** @deprecated **/
	public boolean containsOUT(final ModuleRuntimeData data, final int argno) {
		return containsOUT(new ModuleArgID(data, argno));
	}
	
	/** @deprecated **/
	public boolean containsOUT(final ModuleArgID aid) {
		return _srcmap.containsKey(aid);
	}
	
	/** @deprecated **/
	public boolean contains(final ModuleArgType type, final ModuleArgID aid) {
		if (ModuleArgType.OUT == type) {
			return _srcmap.containsKey(aid);
		}
		else if (ModuleArgType.IN == type) {
			return _destmap.containsKey(aid);
		}
		else {
			return false;
		}
	}

	/**
	 * 指定された <code>source</code> と <code>destination</code> の関係が定義されている場合に <tt>true</tt> を返す。
	 * @param srcRunNo		<code>source</code> の実行番号
	 * @param srcArgNo		<code>source</code> の引数番号
	 * @param destRunNo		<code>destination</code> の実行番号
	 * @param destArgNo		<code>destination</code> の引数番号
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsRelation(final long srcRunNo, final int srcArgNo, final long destRunNo, final int destArgNo) {
		for (Map.Entry<ModuleArgID, ModuleArgID> entry : _destmap.entrySet()) {
			ModuleArgID argid = entry.getKey();
			if (destRunNo == argid.runNo() && destArgNo == argid.argNo()) {
				argid = entry.getValue();
				if (srcRunNo == argid.runNo() && srcArgNo == argid.argNo()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 指定された <code>source</code> と <code>destination</code> の関係が定義されている場合に <tt>true</tt> を返す。
	 * @param srcArgID		<code>source</code> の引数キー
	 * @param destArgID		<code>destination</code> の引数キー
	 * @return	関連付けが定義されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsRelation(final ModuleArgID srcArgID, final ModuleArgID destArgID) {
		ModuleArgID existSrcArgID = _destmap.get(destArgID);
		if (srcArgID != null && existSrcArgID != null) {
			return srcArgID.equals(existSrcArgID);
		} else {
			return false;
		}
	}
	
	/** @deprecated **/
	public ModuleArgID findOutArgID(final long outRunNo, final int outArgNo) {
		for (ModuleArgID outkey : _srcmap.keySet()) {
			if (outRunNo == outkey.runNo() && outArgNo == outkey.argNo()) {
				return outkey;
			}
		}
		return null;
	}
	
	/** @deprecated **/
	public ModuleArgID findInArgID(final long inRunNo, final int inArgNo) {
		for (ModuleArgID inkey : _destmap.keySet()) {
			if (inRunNo == inkey.runNo() && inArgNo == inkey.argNo()) {
				return inkey;
			}
		}
		return null;
	}

	/**
	 * <code>source</code> の引数キーから、指定された実行番号と引数番号を持つ引数キーを検索する。
	 * @param runNo		実行番号
	 * @param argno		引数番号
	 * @return	条件に一致した <code>source</code> の引数キーを返す。一致する引数キーが存在しない場合は <tt>null</tt>
	 */
	public ModuleArgID findSourceArgID(final long runNo, final int argno) {
		for (ModuleArgID srckey : _srcmap.keySet()) {
			if (runNo == srckey.runNo() && argno == srckey.argNo()) {
				return srckey;
			}
		}
		return null;
	}
	
	/**
	 * <code>destination</code> の引数キーから、指定された実行番号と引数番号を持つ引数キーを検索する。
	 * @param runNo		実行番号
	 * @param argno		引数番号
	 * @return	条件に一致した <code>destination</code> の引数キーを返す。一致する引数キーが存在しない場合は <tt>null</tt>
	 */
	public ModuleArgID findDestinationArgID(final long runNo, final int argno) {
		for (ModuleArgID destkey : _destmap.keySet()) {
			if (runNo == destkey.runNo() && argno == destkey.argNo()) {
				return destkey;
			}
		}
		return null;
	}

	/**
	 * すべての引数キーから、指定された実行番号と引数番号を持つ引数キーを検索する。
	 * @param runNo		実行番号
	 * @param argNo		引数番号
	 * @return	条件に一致した引数キーを返す。一致する引数キーが存在しない場合は <tt>null</tt>
	 */
	public ModuleArgID findArgID(final long runNo, final int argNo) {
		for (Map.Entry<ModuleArgID, ModuleArgID> entry : _destmap.entrySet()) {
			ModuleArgID argid = entry.getKey();
			if (runNo == argid.runNo() && argNo == argid.argNo()) {
				return argid;
			}
			argid = entry.getValue();
			if (runNo == argid.runNo() && argNo == argid.argNo()) {
				return argid;
			}
		}
		return null;
	}

	/**
	 * 指定された <code>destination</code> 引数キーに関連付けられた、<code>source</code> 引数キーを取得する。
	 * @param destArgID		<code>destination</code> 引数キー
	 * @return	関連付けられた <code>source</code> 引数キーを返す。関連付けられていない場合は <tt>null</tt>
	 */
	public ModuleArgID getSourceArgID(final ModuleArgID destArgID) {
		return _destmap.get(destArgID);
	}
	
	/**
	 * 指定された <code>source</code> 引数キーに関連付けられた、すべての <code>destination</code> 引数キーのセットを取得する。
	 * @param srcArgID		<code>source</code> 引数キー
	 * @return	関連付けられた <code>destination</code> 引数キーをすべて保持するセットを返す。関連付けられていない場合は <tt>null</tt>
	 */
	public Set<ModuleArgID> getDestinationArgIDs(final ModuleArgID srcArgID) {
		Set<ModuleArgID> inset = _srcmap.get(srcArgID);
		if (inset != null && !inset.isEmpty()) {
			return Collections.unmodifiableSet(inset);
		}
		
		// no relation
		return null;
	}
	
	/** @deprecated **/
	public ModuleArgID getOutArgID(final ModuleArgID inArgID) {
		return _destmap.get(inArgID);
	}
	
	/** @deprecated **/
	public Set<ModuleArgID> getInArgIDs(final ModuleArgID outArgID) {
		Set<ModuleArgID> inset = _srcmap.get(outArgID);
		if (inset != null && !inset.isEmpty()) {
			return Collections.unmodifiableSet(inset);
		}
		
		// no relation
		return null;
	}

	/**
	 * 新しいリレーションを設定する
	 * @param srcArgID	<code>source</code> 引数キー
	 * @param destArgID	<code>destination</code> 引数キー
	 * @return	リレーションが変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数のモジュール実行番号が同じ場合
	 */
	public boolean put(final ModuleArgID srcArgID, final ModuleArgID destArgID) {
		if (srcArgID == null)
			throw new NullPointerException("srcArgID is null.");
		if (destArgID == null)
			throw new NullPointerException("destArgID is null.");
		if (srcArgID.runNo() == destArgID.runNo())
			throw new IllegalArgumentException("srcArgID's runNo(" + srcArgID.runNo() + ") equals destArgID's runNo(" + destArgID.runNo() + ")");
		
		// put IN->OUT map
		ModuleArgID oldSrcArgID = _destmap.put(destArgID, srcArgID);
		if (oldSrcArgID != null) {
			// inArgID に別の outArgID が関連付けられている
			if (srcArgID.equals(oldSrcArgID)) {
				// このリレーションはすでに存在する
				return false;
			}
			
			// 古いリレーションを削除
			removeSrcArgRelation(oldSrcArgID, destArgID);
		}
		
		// put OUT->IN map
		Set<ModuleArgID> destset = _srcmap.get(srcArgID);
		if (destset == null) {
			destset = createDestArgsSet();
			_srcmap.put(srcArgID, destset);
		}
		destset.add(destArgID);
		
		return true;
	}

	/**
	 * 指定された引数キーを持つ全ての関連付けを除去する。
	 * @param aid	引数キー
	 * @return	関連付けが除去された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeRelations(final ModuleArgID aid) {
		ModuleArgID srcArgID = _destmap.get(aid);
		if (srcArgID != null) {
			_destmap.remove(aid);
			removeSrcArgRelation(srcArgID, aid);
			return true;
		}
		
		Set<ModuleArgID> destset = _srcmap.get(aid);
		if (destset != null) {
			_srcmap.remove(aid);
			for (ModuleArgID destArgID : destset) {
				_destmap.remove(destArgID);
			}
			destset.clear();
			return true;
		}
		
		return false;
	}

	/**
	 * 指定された関連付けを除去する。
	 * @param srcArgID		<code>source</code> 引数キー
	 * @param destArgID		<code>destination</code> 引数キー
	 * @return	関連付けが除去された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeRelation(final ModuleArgID srcArgID, final ModuleArgID destArgID) {
		ModuleArgID existSrcArgID = _destmap.get(destArgID);
		if (existSrcArgID != null && existSrcArgID.equals(srcArgID)) {
			_destmap.remove(destArgID);
			removeSrcArgRelation(srcArgID, destArgID);
			return true;
		}
		
		// no relation
		return false;
	}

	/**
	 * このオブジェクトのクローンを生成する。
	 * このクローン生成は、ディープコピーとなる。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ModuleArgRelationMap clone() {
		try {
			ModuleArgRelationMap map = (ModuleArgRelationMap)super.clone();
			map._destmap = (HashMap<ModuleArgID, ModuleArgID>)this._destmap.clone();
			map._srcmap = (HashMap<ModuleArgID, Set<ModuleArgID>>)this._srcmap.clone();
			for (Map.Entry<ModuleArgID, Set<ModuleArgID>> entry : map._srcmap.entrySet()) {
				Set<ModuleArgID> orgset = entry.getValue();
				if (orgset != null) {
					entry.setValue(createDestArgsSet(orgset));
				}
			}
			return map;
		}
		catch (CloneNotSupportedException ex) {
		    throw new InternalError("Clone not supported!");
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean removeSrcArgRelation(final ModuleArgID srcArgID, final ModuleArgID destArgID) {
		boolean modified = false;
		Set<ModuleArgID> inset = _srcmap.get(srcArgID);
		if (inset != null) {
			modified = inset.remove(destArgID);
			if (inset.isEmpty()) {
				_srcmap.remove(srcArgID);
			}
		}
		return modified;
	}
	
	protected Set<ModuleArgID> createDestArgsSet() {
		return new HashSet<ModuleArgID>();
	}
	
	protected Set<ModuleArgID> createDestArgsSet(Collection<? extends ModuleArgID> values) {
		return new HashSet<ModuleArgID>(values);
	}

//	/** @deprecated **/
//	protected boolean removeOutArgRelation(final ModuleArgID outArgID, final ModuleArgID inArgID) {
//		boolean modified = false;
//		Set<ModuleArgID> inset = _srcmap.get(outArgID);
//		if (inset != null) {
//			modified = inset.remove(inArgID);
//			if (inset.isEmpty()) {
//				_srcmap.remove(outArgID);
//			}
//		}
//		return modified;
//	}
//	
//	/** @deprecated **/
//	protected Set<ModuleArgID> createInArgsSet() {
//		return new HashSet<ModuleArgID>();
//	}
//	
//	/** @deprecated **/
//	protected Set<ModuleArgID> createInArgsSet(Collection<? extends ModuleArgID> values) {
//		return new HashSet<ModuleArgID>(values);
//	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
