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
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)DefaultDataTable.java	2.1.0	2013/07/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.util.ArrayList;
import java.util.Collection;

/**
 * メモリ上のデータテーブルの実装。
 * このクラスはメモリ上に展開されたデータを保持するため、
 * レコード数ならびにフィールド数の上限は、<code>int</code> 型の正の最大値となる。
 * 
 * @version 2.1.0	2013/07/18
 * @since 2.1.0
 */
public class DefaultDataTable extends AbDataTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** フィールド数 **/
	protected int	_numFields;
	/** ヘッダレコード **/
	protected ArrayList<DataRecord>	_headers;
	/** データレコード **/
	protected ArrayList<DataRecord>	_records;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DefaultDataTable() {
		_headers = new ArrayList<DataRecord>();
		_records = new ArrayList<DataRecord>();
	}
	
	public DefaultDataTable(int records, int fields) {
		this(records, fields, 0);
	}
	
	public DefaultDataTable(int records, int fields, int headers) {
		if (fields < 0)
			throw new IllegalArgumentException("'fields' is negative : " + fields);
		if (records < 0)
			throw new IllegalArgumentException("'records' is negative : " + records);
		if (headers < 0)
			throw new IllegalArgumentException("'headers' is negative : " + headers);
		
		// create headers
		_headers = new ArrayList<DataRecord>(headers);
		for (int i = 0; i < headers; ++i) {
			_headers.add(new DataRecord(fields));
		}
		for (int i = 0; i < records; ++i) {
			_records.add(new DataRecord(fields));
		}
		_numFields = fields;
	}

	//------------------------------------------------------------
	// Implement ssac.util.swing.plot.IDataTable interfaces
	//------------------------------------------------------------

	/**
	 * 総レコード数を返す。
	 * レコード数は、ヘッダレコード数とデータレコード数の合計となる。
	 */
	public long getRecordCount() {
		return ((long)_headers.size() + (long)_records.size());
	}
	
	/**
	 * ヘッダとなるレコード数を返す。
	 */
	public long getHeaderRecordCount() {
		return _headers.size();
	}
	
	/**
	 * 実データの総レコード数を返す。
	 */
	public long getDataRecordCount() {
		return _records.size();
	}
	/**
	 * 最大フィールド数を返す。
	 */
	public int getFieldCount() {
		return _numFields;
	}
	
	/**
	 * 指定された位置のヘッダ値を返す。
	 * @param headerRecordIndex	レコードの位置を示すインデックス(<code>getHeaderRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	ヘッダ値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getHeaderValue(long headerRecordIndex, int fieldIndex) {
		validHeaderRecordIndex(headerRecordIndex, _headers.size());
		validFieldIndex(fieldIndex, _numFields);
		
		DataRecord rec = _headers.get((int)headerRecordIndex);
		return rec.getValue(fieldIndex);
	}
	
	/**
	 * 指定された位置の実値を返す。
	 * @param dataRecordIndex	レコードの位置を示すインデックス(<code>getDataRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	実値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getValue(long dataRecordIndex, int fieldIndex) {
		validDataRecordIndex(dataRecordIndex, _records.size());
		validFieldIndex(fieldIndex, _numFields);
		
		DataRecord rec = _records.get((int)dataRecordIndex);
		return rec.getValue(fieldIndex);
	}

	/**
	 * 指定されたヘッダ位置に、新しい値を設定する。
	 * <em>allowGrowing</em> に <tt>true</tt> を指定した場合、テーブルのサイズを自動的に拡張する。
	 * <em>allowGrowing</em> に <tt>false</tt> であり、現在のレコード数とフィールド数を超える位置を指定した場合、
	 * このメソッドは例外をスローする。
	 * @param headerRecordIndex	レコードの位置を示すインデックス
	 * @param fieldIndex		フィールドの位置を示すインデックス
	 * @param value				新しい値
	 * @param allowGrowing		テーブルのサイズを自動的に拡張する場合は <tt>true</tt>
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが負の値の場合、	
	 * 							または、<em>allowGrowing</em> が <tt>true</tt> であり、レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public void setHeaderValue(long headerRecordIndex, int fieldIndex, Object value, boolean allowGrowing) {
		DataRecord rec;
		if (allowGrowing) {
			validHeaderRecordIndex(headerRecordIndex, Integer.MAX_VALUE);
			validFieldIndex(fieldIndex, Integer.MAX_VALUE);
			
			fillEmptyRecords(_headers, (int)headerRecordIndex + 1);
			rec = _headers.get((int)headerRecordIndex);
		}
		else {
			validHeaderRecordIndex(headerRecordIndex, _headers.size());
			validFieldIndex(fieldIndex, _numFields);
			
			rec = _headers.get((int)headerRecordIndex);
		}
		
		fillNullFields(rec, fieldIndex + 1);
		rec.set(fieldIndex, value);
	}

	/**
	 * 指定された位置に、新しい値を設定する。
	 * <em>allowGrowing</em> に <tt>true</tt> を指定した場合、テーブルのサイズを自動的に拡張する。
	 * <em>allowGrowing</em> に <tt>false</tt> であり、現在のレコード数とフィールド数を超える位置を指定した場合、
	 * このメソッドは例外をスローする。
	 * @param dataRecordIndex	レコードの位置を示すインデックス
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @param value				新しい値
	 * @param allowGrowing		テーブルのサイズを自動的に拡張する場合は <tt>true</tt>
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが負の値の場合、	
	 * 							または、<em>allowGrowing</em> が <tt>true</tt> であり、レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public void setValue(long dataRecordIndex, int fieldIndex, Object value, boolean allowGrowing) {
		DataRecord rec;
		if (allowGrowing) {
			validDataRecordIndex(dataRecordIndex, Integer.MAX_VALUE);
			validFieldIndex(fieldIndex, Integer.MAX_VALUE);
			
			fillEmptyRecords(_records, (int)dataRecordIndex + 1);
			rec = _records.get((int)dataRecordIndex);
		}
		else {
			validDataRecordIndex(dataRecordIndex, _records.size());
			validFieldIndex(fieldIndex, _numFields);
			
			rec = _records.get((int)dataRecordIndex);
		}
		
		fillNullFields(rec, fieldIndex + 1);
		rec.set(fieldIndex, value);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void fillEmptyRecords(ArrayList<DataRecord> list, int maxRecords) {
		for (; list.size() < maxRecords; ) {
			list.add(new DataRecord());
		}
	}
	
	protected void fillNullFields(DataRecord rec, int maxFields) {
		for (; rec.size() < maxFields; ) {
			rec.add(null);
		}
		
		if (_numFields < maxFields) {
			_numFields = maxFields;
		}
	}
	
	protected int countOfFields() {
		int fields = 0;
		
		// header
		for (DataRecord rec : _headers) {
			int num = rec.size();
			if (num > fields) {
				fields = num;
			}
		}
		
		// data
		for (DataRecord rec : _records) {
			int num = rec.size();
			if (num > fields) {
				fields = num;
			}
		}
		
		return fields;
	}
	
	static protected class DataRecord extends ArrayList<Object>
	{
		private static final long serialVersionUID = -6732338081655464090L;

		public DataRecord() {
			super();
		}

		public DataRecord(Collection<? extends Object> c) {
			super(c);
		}

		public DataRecord(int initialCapacity) {
			super(initialCapacity);
		}
		
		public Object getValue(int fieldIndex) {
			if (fieldIndex < size())
				return get(fieldIndex);
			else
				return null;
		}
	}
}
