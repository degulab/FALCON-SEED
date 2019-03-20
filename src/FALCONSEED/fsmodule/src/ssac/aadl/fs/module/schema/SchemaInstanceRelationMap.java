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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SchemaReferenceMap.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaReferenceMap.java	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * スキーマオブジェクトの参照関係を保持するクラス。
 * このクラスでは、スキーマオブジェクトのインスタンスIDをキーとして関連付けを保持する。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaInstanceRelationMap
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 参照される側をキーとするマップ、複数から参照される **/
	protected MultiInstanceMap	_precedentMap;
	/** 参照する側をキーとするマップ、複数を参照する場合もある **/
	protected MultiInstanceMap	_dependentMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaInstanceRelationMap() {
		_precedentMap = new MultiInstanceMap();
		_dependentMap  = new MultiInstanceMap();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return _dependentMap.isEmpty();
	}
	
	public void clear() {
		_dependentMap .clear();
		_precedentMap.clear();
	}
	
	public int getDependentCount() {
		return _dependentMap.size();
	}
	
	public int getPrecedentCount() {
		return _precedentMap.size();
	}
	
	public int getReferenceCount() {
		int num = 0;
		for (Map.Entry<Integer, ElementMap> entry : _precedentMap.entrySet()) {
			num += entry.getValue().size();
		}
		return num;
	}
	
	public boolean containsReference(SchemaObject dependent, SchemaObject precedent) {
		if (dependent != null && precedent != null) {
			return _dependentMap.containsPair(dependent.getInstanceId(), precedent.getInstanceId());
		}
		return false;
	}
	
	public boolean containsDependent(SchemaObject dependent) {
		return (dependent != null && _dependentMap.containsKey(dependent.getInstanceId()));
	}
	
	public boolean containsPrecedent(SchemaObject precedent) {
		return (precedent != null && _precedentMap.containsKey(precedent.getInstanceId()));
	}
	
	public Collection<SchemaObject> getAllPrecedents(SchemaObject dependent) {
		if (dependent == null) {
			return Collections.emptyList();
		}
		else {
			ElementMap emap = _dependentMap.get(dependent.getInstanceId());
			if (emap == null) {
				return Collections.emptyList();
			} else {
				return Collections.unmodifiableCollection(emap.values());
			}
		}
	}
	
	public Collection<SchemaObject> getAllDependents(SchemaObject precedent) {
		if (precedent == null) {
			return Collections.emptyList();
		}
		else {
			ElementMap emap = _precedentMap.get(precedent.getInstanceId());
			if (emap == null) {
				return Collections.emptyList();
			} else {
				return Collections.unmodifiableCollection(emap.values());
			}
		}
	}

	public boolean setReference(SchemaObject dependent, SchemaObject precedent) {
		int precedentid = precedent.getInstanceId();
		int dependentid = dependent.getInstanceId();
		
		if (!_dependentMap.putPair(dependentid, precedentid, precedent)) {
			// already exists
			return false;
		}
		
		// modified
		_precedentMap.putPair(precedentid, dependentid, dependent);
		return true;
	}
	
	public boolean replaceReference(SchemaObject dependent, SchemaObject oldPrecedent, SchemaObject newPrecedent) {
		int oldPrecedentid = oldPrecedent.getInstanceId();
		int newPrecedentid = newPrecedent.getInstanceId();
		int dependentid = dependent.getInstanceId();
		
		// remove old reference
		boolean dremoved = _dependentMap.removePair(dependentid, oldPrecedentid);
		boolean premoved = _precedentMap.removePair(oldPrecedentid, dependentid);
		
		// append new reference
		boolean appended;
		if (_dependentMap.putPair(dependentid, newPrecedentid, newPrecedent)) {
			//--- modified
			appended = true;
			_precedentMap.putPair(newPrecedentid, dependentid, dependent);
		} else {
			//--- unmodified
			appended = false;
		}
		
		return (dremoved || premoved || appended);
	}
	
	public boolean removeReference(SchemaObject dependent, SchemaObject precedent) {
		if (dependent == null || precedent == null)
			return false;
		
		int dependentid = dependent.getInstanceId();
		int precedentid = precedent.getInstanceId();
		
		boolean dremoved = _dependentMap.removePair(dependentid, precedentid);
		boolean premoved = _precedentMap.removePair(precedentid, dependentid);
		
		return (dremoved || premoved);
	}
	
	public boolean removeDependent(SchemaObject dependent) {
		if (dependent == null)
			return false;

		int dependentid = dependent.getInstanceId();
		ElementMap emap = _dependentMap.remove(dependentid);
		if (emap == null)
			return false;
		
		for (Map.Entry<Integer,SchemaObject> entry : emap.entrySet()) {
			_precedentMap.removePair(entry.getKey(), dependentid);
		}
		return true;
	}
	
	public boolean removePrecedent(SchemaObject precedent) {
		if (precedent == null)
			return false;
		
		int precedentid = precedent.getInstanceId();
		ElementMap emap = _precedentMap.remove(precedentid);
		if (emap == null)
			return false;
		
		for (Map.Entry<Integer,SchemaObject> entry : emap.entrySet()) {
			_dependentMap.removePair(entry.getKey(), precedentid);
		}
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 複数の要素を持つマルチマップ。
	 * @version 3.2.1
	 * @since 3.2.1
	 */
	static protected class MultiInstanceMap extends TreeMap<Integer,ElementMap>
	{
		private static final long serialVersionUID = 1L;

		public MultiInstanceMap() {
			super();
		}

		public MultiInstanceMap(Map<? extends Integer, ? extends ElementMap> m) {
			super(m);
		}
		
		public boolean containsPair(SchemaObject key, SchemaObject value) {
			return containsPair(key.getInstanceId(), value.getInstanceId());
		}
		
		public boolean containsPair(Integer key, SchemaObject value) {
			return containsPair(key, value.getInstanceId());
		}

		public boolean containsPair(Integer key, Integer value) {
			ElementMap emap = get(key);
			if (emap != null && !emap.isEmpty()) {
				return emap.containsKey(value);
			} else {
				return false;
			}
		}
		
		public boolean putPair(SchemaObject key, SchemaObject value) {
			return putPair(key.getInstanceId(), value.getInstanceId(), value);
		}
		
		public boolean putPair(Integer key, SchemaObject value) {
			return putPair(key, value.getInstanceId(), value);
		}
		
		public boolean putPair(Integer key, Integer valueKey, SchemaObject valueObject) {
			ElementMap emap = get(key);
			if (emap == null) {
				// new
				emap = new ElementMap();
				emap.put(valueKey, valueObject);
				put(key, emap);
				return true;
			}
			
			// modified
			return (emap.put(valueKey, valueObject) == null);
		}
		
		public boolean removePair(SchemaObject key, SchemaObject value) {
			return removePair(key.getInstanceId(), value.getInstanceId());
		}
		
		public boolean removePair(Integer key, SchemaObject value) {
			return removePair(key, value.getInstanceId());
		}
		
		public boolean removePair(Integer key, Integer value) {
			ElementMap emap = get(key);
			if (emap == null) {
				// not exists
				return false;
			}
			
			boolean removed = (emap.remove(value) != null);
			
			if (emap.isEmpty()) {
				remove(key);
			}
			
			return removed;
		}
	}

	/**
	 * 内部の要素マップ。
	 * @version 3.2.0
	 * @since 3.2.0
	 */
	static protected class ElementMap extends TreeMap<Integer,SchemaObject>
	{
		private static final long serialVersionUID = 1L;

		public ElementMap() {
			super();
		}

		public ElementMap(Map<? extends Integer, ? extends SchemaObject> m) {
			super(m);
		}
	}
}
