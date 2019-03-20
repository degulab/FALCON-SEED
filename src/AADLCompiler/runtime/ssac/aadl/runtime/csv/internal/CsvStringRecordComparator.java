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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvTypedRecordComparator.java	1.90	2013/08/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CSV レコードを文字列として比較するコンパレータ。
 * このコンパレータは、基本的にレコード内の全フィールドを比較対象とする。
 * 
 * @version 1.90	2013/08/07
 * @since 1.90
 */
public class CsvStringRecordComparator implements CsvRecordComparator
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private Set<Integer>	_ignoreFieldIndexSet;
	private boolean			_ascending;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 全フィールドを比較対象とする、昇順のコンパレータを生成する。
	 */
	public CsvStringRecordComparator() {
		this(true, null);
	}

	/**
	 * 全フィールドを比較対象とする、指定オーダーのコンパレータを生成する。
	 * @param ascending	昇順とする場合は <tt>true</tt>、降順とする場合は <tt>false</tt>
	 */
	public CsvStringRecordComparator(boolean ascending) {
		this(ascending, null);
	}

	/**
	 * 指定されたフィールドインデックスを比較から除外する、昇順のコンパレータを生成する。
	 * @param ignoreFieldIndices	除外するフィールドインデックスの配列
	 */
	public CsvStringRecordComparator(int...ignoreFieldIndices) {
		this(true, ignoreFieldIndices);
	}
	
	/**
	 * 指定されたフィールドインデックスを比較から除外する、指定オーダーのコンパレータを生成する。
	 * @param ascending	昇順とする場合は <tt>true</tt>、降順とする場合は <tt>false</tt>
	 * @param ignoreFieldIndices	除外するフィールドインデックスの配列
	 */
	public CsvStringRecordComparator(boolean ascending, int...ignoreFieldIndices) {
		_ascending = ascending;
		if (ignoreFieldIndices != null && ignoreFieldIndices.length > 0) {
			_ignoreFieldIndexSet = new HashSet<Integer>();
			for (int index : ignoreFieldIndices) {
				if (index >= 0) {
					_ignoreFieldIndexSet.add(index);
				}
			}
		} else {
			_ignoreFieldIndexSet = Collections.emptySet();
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public int compareValue(String val1, String val2) {
		// null check
		if (val1 == null) {
			if (val2 != null) {
				// ascending : val1 < val2
				return (-1);
			}
			else {
				// both null
				return 0;
			}
		}
		else if (val2 == null) {
			// val1 != null
			// ascending : val1 > val2
			return (1);
		}
		
		// both not null (ascending)
		return val1.compareTo(val2);
	}

	//------------------------------------------------------------
	// Implement CsvRecordComparator interfaces
	//------------------------------------------------------------

	public void sort(List<List<String>> records) {
		Collections.sort(records, this);
	}
	
	public void sort(int skipHeaderRows, List<List<String>> records) {
		if (skipHeaderRows < 1) {
			sort(records);
		} else {
			int len = records.size();
			if (len > skipHeaderRows) {
				java.util.Collections.sort(records.subList(skipHeaderRows, len), this);
			}
		}
	}

	//------------------------------------------------------------
	// Implement java.util.Comparator interfaces
	//------------------------------------------------------------

	public int compare(java.util.List<String> list1, java.util.List<String> list2) {
		boolean ascending = _ascending;
		int len1 = list1.size();
		int len2 = list2.size();
		int maxlen = Math.max(len1, len2);
		if (maxlen == 0) {
			// 要素なし
			return 0;
		}
		else if (len1 == 0) {
			// list1 の要素なし
			return (ascending ? -1 : 1);
		}
		else if (len2 == 0) {
			// list2 の要素なし
			return (ascending ? 1 : -1);
		}
		
		// 比較
		if (_ignoreFieldIndexSet.isEmpty()) {
			// 全フィールドを比較
			if (ascending) {
				// 昇順
				for (int index = 0; index < maxlen; ++index) {
					String val1 = (index < len1 ? list1.get(index) : null);
					String val2 = (index < len2 ? list2.get(index) : null);
					int cmp = compareValue(val1, val2);
					if (cmp != 0) {
						return cmp;
					}
				}
			} else {
				// 降順
				for (int index = 0; index < maxlen; ++index) {
					String val1 = (index < len1 ? list1.get(index) : null);
					String val2 = (index < len2 ? list2.get(index) : null);
					int cmp = compareValue(val2, val1);
					if (cmp != 0) {
						return cmp;
					}
				}
			}
		} else {
			// 除外フィールドを考慮
			if (ascending) {
				// 昇順
				for (int index = 0; index < maxlen; ++index) {
					if (!_ignoreFieldIndexSet.contains(index)) {
						String val1 = (index < len1 ? list1.get(index) : null);
						String val2 = (index < len2 ? list2.get(index) : null);
						int cmp = compareValue(val1, val2);
						if (cmp != 0) {
							return cmp;
						}
					}
				}
			} else {
				// 降順
				for (int index = 0; index < maxlen; ++index) {
					if (!_ignoreFieldIndexSet.contains(index)) {
						String val1 = (index < len1 ? list1.get(index) : null);
						String val2 = (index < len2 ? list2.get(index) : null);
						int cmp = compareValue(val2, val1);
						if (cmp != 0) {
							return cmp;
						}
					}
				}
			}
		}
		
		return 0;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
