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
 * @(#)CsvTypedRecordComparator.java	1.90	2013/08/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ssac.aadl.runtime.util.internal.FSDateTimeFormats;

/**
 * データ型に応じて、CSV レコードの指定フィールドの値で比較するコンパレータ。
 * 
 * @version 1.90	2013/08/02
 * @since 1.90
 */
public class CsvTypedRecordComparator implements CsvRecordComparator
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static public final int	DATATYPE_STRING	= 0;
	static public final int	DATATYPE_DECIMAL = 1;
	static public final int	DATATYPE_DATETIME = 2;
	static public final int	DATATYPE_UNKNOWN = (-1);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected final ArrayList<SortCondItem>	_condlist;
	protected final FSDateTimeFormats		_dtformat;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public CsvTypedRecordComparator(FSDateTimeFormats dtformat, String condString) {
		this._condlist = parseSortConditionString(condString);
		if (_condlist.isEmpty()) {
			throw new IllegalArgumentException("Sort condition is not specified.");
		}
		if (dtformat == null) {
			this._dtformat = new FSDateTimeFormats();
		} else {
			this._dtformat = dtformat;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public FSDateTimeFormats getDateTimeFormats() {
		return _dtformat;
	}

	public String getConditionString() {
		StringBuilder sb = new StringBuilder();
		if (!_condlist.isEmpty()) {
			Iterator<SortCondItem> it = _condlist.iterator();
			sb.append(it.next().toString());
			while (it.hasNext()) {
				sb.append(",");
				sb.append(it.next().toString());
			}
		}
		return sb.toString();
	}

	public void setConditionString(String condString) {
		List<SortCondItem> items = parseSortConditionString(condString);
		if (items.isEmpty()) {
			throw new IllegalArgumentException("Sort condition is not specified.");
		}
		_condlist.clear();
		_condlist.ensureCapacity(items.size());
		_condlist.addAll(items);
		_condlist.trimToSize();
		items = null;
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
				Collections.sort(records.subList(skipHeaderRows, len), this);
			}
		}
	}

	//------------------------------------------------------------
	// Implement java.util.Comparator interfaces
	//------------------------------------------------------------

	public int compare(List<String> list1, List<String> list2) {
		for (SortCondItem item : _condlist) {
			int col = item.column();
			String val1 = (col < list1.size() ? list1.get(col) : null);
			String val2 = (col < list2.size() ? list2.get(col) : null);
			int cmp = compareFieldValue(item.dataType(), item.isAscending(), val1, val2);
			if (cmp != 0) {
				return cmp;
			}
		}
		
		return 0;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected int compareFieldValue(int datatype, boolean ascending, String val1, String val2) {
		// null check
		if (val1 == null) {
			if (val2 != null) {
				if (ascending) {
					// ascending : val1 < val2
					return (-1);
				} else {
					// descending : val1 > val2
					return (1);
				}
			}
			else {
				// both null
				return 0;
			}
		}
		else if (val2 == null) {
			// val1 != null
			if (ascending) {
				// ascending : val1 > val2
				return (1);
			} else {
				// descending : val1 < val2
				return (-1);
			}
		}

		int cmp = 0;
		boolean completed = false;
		if (datatype == DATATYPE_DECIMAL) {
			if (!ssac.aadl.runtime.AADLFunctions.isDecimal(val1)) {
				if (ssac.aadl.runtime.AADLFunctions.isDecimal(val2)) {
					// (val1:String < val2:Decimal)
					cmp = (-1);
					completed = true;
				}
				// (val1:String ; val2:String) -> compare by string
			}
			else if (!ssac.aadl.runtime.AADLFunctions.isDecimal(val2)) {
				// (val1:Decimal > val2:String)
				cmp = 1;
				completed = true;
			}
			else {
				// (val1:Decimal ; val2:Decimal)
				java.math.BigDecimal d1 = ssac.aadl.runtime.AADLFunctions.toDecimal(val1);
				java.math.BigDecimal d2 = ssac.aadl.runtime.AADLFunctions.toDecimal(val2);
				cmp = d1.compareTo(d2);
				completed = true;
			}
		}
		else if (datatype == DATATYPE_DATETIME) {
			Calendar cal1 = _dtformat.parse(val1);
			Calendar cal2 = _dtformat.parse(val2);
			if (cal1 == null) {
				if (cal2 != null) {
					// (val1:String < val2:DateTime)
					cmp = (-1);
					completed = true;
				}
				// (val1:String ; val2:String) -> compare by string
			}
			else if (cal2 == null) {
				// (val1:DateTime > val2:String)
				cmp = 1;
				completed = true;
			}
			else {
				// (val1:DateTime ; val2:DateTime)
				cmp = cal1.compareTo(cal2);
				completed = true;
			}
		}

		if (!completed) {
			// compare by string
			cmp = val1.compareTo(val2);
		}
		
		if (!ascending) {
			cmp = -cmp;
		}
		
		return cmp;
	}
	
	static protected ArrayList<SortCondItem> parseSortConditionString(String input) {
		ArrayList<SortCondItem> itemlist = new ArrayList<SortCondItem>();
		
		if (input == null) {
			return itemlist;
		}
		
		int len = input.length();
		int pos = 0;
		int stat = 0;
		java.math.BigDecimal dColumn = null;
		int dtype = DATATYPE_STRING;
		boolean ascending = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			char ch = input.charAt(i);
			if (ch==' ') {
				// skip while space
				for (++i; (i < len) && (ch=input.charAt(i))==' '; i++);
				if (i >= len) {
					break;
				}
			}
			pos = i;
			
			if (stat == 1) {
				// data type phase
				for (; (ch!=',' && ch!='=');) {
					sb.append(ch);
					++i;
					if (i < len) {
						ch = input.charAt(i);
					} else {
						ch = ',';
						break;
					}
				}
				
				String strType = sb.toString().trim();
				sb.setLength(0);
				
				if (strType.length() > 0) {
					dtype = parseDataType(strType);
					if (dtype < 0) {
						// illegal data type
						throw new IllegalArgumentException("Illegal data type : pos=" + (pos+1) + " : \"" + strType + "\"");
					}
				}
				
				if (ch=='=') {
					stat = 2;	// next data type phase
				} else {
					// regist column number
					addCondition(itemlist, dColumn, dtype, ascending);
					
					// next column number phase
					stat = 0;
					dColumn = null;
					dtype = DATATYPE_STRING;
					ascending = true;
				}
			}
			else if (stat == 2) {
				// order phase
				for (; ch!=',';) {
					sb.append(ch);
					++i;
					if (i < len) {
						ch = input.charAt(i);
					} else {
						ch = ',';
						break;
					}
				}
				
				String strAsc = sb.toString().trim();
				sb.setLength(0);
				
				if (strAsc.length() > 0) {
					Boolean asc = parseAscending(strAsc);
					if (asc == null) {
						// illegal ascending flag
						throw new IllegalArgumentException("Illegal order : pos=" + (pos+1) + " : \"" + strAsc + "\"");
					}
					ascending = asc;
				}
				
				// regist column number
				addCondition(itemlist, dColumn, dtype, ascending);
				
				// next column number phase
				stat = 0;
				dColumn = null;
				dtype = DATATYPE_STRING;
				ascending = true;
			}
			else {
				// column number phase
				for (; (ch!=',' && ch!=':' && ch!='=');) {
					sb.append(ch);
					++i;
					if (i < len) {
						ch = input.charAt(i);
					} else {
						ch = ',';
						break;
					}
				}
				
				String strColumn = sb.toString().trim();
				sb.setLength(0);
				
				if (strColumn.length() > 0) {
					if (!ssac.aadl.runtime.AADLFunctions.isDecimal(strColumn)) {
						throw new IllegalArgumentException("Field number is not digit : pos=" + (pos+1) + " : " + String.valueOf(strColumn));
					}
					java.math.BigDecimal val = ssac.aadl.runtime.AADLFunctions.toDecimal(strColumn);
					if (!isColumnNumber(val)) {
						throw new IllegalArgumentException("Please set greater equal 1 for Field number : pos=" + (pos+1) + " : " + String.valueOf(strColumn));
					}
					dColumn = val;
					if (ch==':') {
						stat = 1;	// next data type phase
					} else if (ch=='=') {
						stat = 2;	// next order phase
					} else {
						// regist column number
						addCondition(itemlist, dColumn, dtype, ascending);
						
						// next column number phase
						stat = 0;
						dColumn = null;
						dtype = DATATYPE_STRING;
						ascending = true;
					}
				}
				else if (ch==':' || ch=='=') {
					throw new IllegalArgumentException("Field number is not digit : pos=" + (pos+1));
				}
				else {
					// skip field
					dColumn = null;
					dtype = DATATYPE_STRING;
					ascending = true;
				}
			}
		}
		
		if (dColumn != null) {
			addCondition(itemlist, dColumn, dtype, ascending);
		}
		
		return itemlist;
	}
	
	static protected void addCondition(ArrayList<SortCondItem> itemlist, java.math.BigDecimal colNumber, int datatype, boolean ascending)
	{
		if (colNumber != null) {
			Integer iColumn;
			try {
				iColumn = colNumber.intValueExact() - 1;
			} catch (Throwable ex) {
				iColumn = null;
			}
			if (iColumn != null && iColumn >= 0) {
				SortCondItem item = new SortCondItem(iColumn, datatype, ascending);
				itemlist.add(item);
			}
		}
	}
	
	static protected boolean isColumnNumber(java.math.BigDecimal val) {
		try {
			val.toBigIntegerExact();
			if (java.math.BigDecimal.ONE.compareTo(val) > 0) {
				return false;
			} else {
				return true;
			}
		} catch (Throwable ex) {
			return false;
		}
	}
	
	static protected Boolean parseAscending(String input) {
		input = input.toLowerCase();
		if ("asc".equals(input)) {
			return Boolean.TRUE;
		}
		else if ("desc".equals(input)) {
			return Boolean.FALSE;
		}
		else {
			return null;
		}
	}
	
	static protected int parseDataType(String input) {
		input = input.toLowerCase();
		if ("string".equals(input)) {
			// 'string' data type
			return DATATYPE_STRING;
		}
		else if ("decimal".equals(input)) {
			// 'decimal' data type
			return DATATYPE_DECIMAL;
		}
		else if ("datetime".equals(input)) {
			// 'datetime' data type
			return DATATYPE_DATETIME;
		}
		else {
			// unknown data type
			return DATATYPE_UNKNOWN;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class SortCondItem
	{
		private final int		_column;
		private final int		_datatype;
		private final boolean	_ascending;
		
		public SortCondItem(int column, int datatype, boolean ascending)
		{
			this._column = column;
			this._datatype = datatype;
			this._ascending = ascending;
		}
		
		public int column() {
			return _column;
		}
		
		public int dataType() {
			return _datatype;
		}
		
		public boolean isAscending() {
			return _ascending;
		}

		@Override
		public int hashCode() {
			int h = _column;
			h = 31 * h + _datatype;
			h = 31 * h + (_ascending ? 1231 : 1237);
			return h;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj instanceof SortCondItem) {
				SortCondItem item = (SortCondItem)obj;
				if ((item._column==this._column) && (item._datatype==this._datatype) && (item._ascending==this._ascending)) {
					return true;
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(_column);
			sb.append(':');
			switch (_datatype) {
				case DATATYPE_STRING :
					sb.append("string");
					break;
				case DATATYPE_DECIMAL :
					sb.append("decimal");
					break;
				case DATATYPE_DATETIME :
					sb.append("datetime");
					break;
				default:
					sb.append("Unknown");
			}
			sb.append('=');
			if (_ascending)
				sb.append("ASC");
			else
				sb.append("DESC");
			return sb.toString();
		}
	}
}
