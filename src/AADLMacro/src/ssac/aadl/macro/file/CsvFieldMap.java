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
 * @(#)CsvFieldMap.java	2.0.0	2014/03/18 : move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFieldMap.java	1.10	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.file;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ssac.aadl.macro.util.io.CsvReader;
import ssac.aadl.macro.util.io.CsvReader.CsvField;

/**
 * CSV読み込み時の文字列キーとフィールドの対応を保持するクラス。
 * このマップは一つのキーに複数のフィールドを関連付けられる。
 * また、フィールドは、ファイル内のインデックスで昇順ソートされている。
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.10
 */
public class CsvFieldMap
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final Map<String, FieldSet> _map = new HashMap<String, FieldSet>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFieldMap() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		_map.clear();
	}
	
	public boolean isEmpty() {
		return _map.isEmpty();
	}
	
	public int getNumKeys() {
		return _map.size();
	}
	
	public int getNumFields() {
		int numFields = 0;
		for (FieldSet fset : _map.values()) {
			numFields += fset.size();
		}
		return numFields;
	}
	
	public boolean containsKey(Object key) {
		return _map.containsKey(key);
	}
	
	public boolean containsField(CsvReader.CsvField field) {
		if (_map.isEmpty())
			return false;
		
		for (Map.Entry<String, FieldSet> entry : _map.entrySet()) {
			if (entry.getValue().contains(field)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean contains(String key, CsvReader.CsvField field) {
		FieldSet fset = _map.get(key);
		if (fset != null) {
			return fset.contains(field);
		} else {
			return false;
		}
	}
	
	public boolean add(String key, CsvReader.CsvField field) {
		if (key == null)
			throw new IllegalArgumentException("'key' argument is null.");
		if (field == null)
			throw new IllegalArgumentException("'field' argument is null.");
		
		FieldSet fset = _map.get(key);
		if (fset == null) {
			fset = new FieldSet();
			fset.add(field);
			_map.put(key, fset);
			return true;
		}
		
		return fset.add(field);
	}
	
	public boolean remove(String key, CsvReader.CsvField field) {
		FieldSet fset = _map.get(key);
		boolean ret = false;
		if (fset != null) {
			ret = fset.remove(field);
			if (fset.isEmpty()) {
				_map.remove(key);
			}
		}
		return ret;
	}
	
	public Set<String> keys() {
		return _map.keySet();
	}
	
	public Set<CsvReader.CsvField> getFields(String key) {
		FieldSet fset = _map.get(key);
		if (fset == null) {
			return Collections.emptySet();
		} else {
			return fset;
		}
	}
	
	public CsvReader.CsvField getFirstField(String key) {
		FieldSet fset = _map.get(key);
		if (fset != null && !fset.isEmpty()) {
			return fset.first();
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final class FieldSet extends TreeSet<CsvReader.CsvField>
	{

		public FieldSet() {
			super();
		}

		public FieldSet(Collection<? extends CsvField> c) {
			super(c);
		}

		public FieldSet(Comparator<? super CsvField> c) {
			super(c);
		}

		public FieldSet(SortedSet<CsvField> s) {
			super(s);
		}
	}
}
