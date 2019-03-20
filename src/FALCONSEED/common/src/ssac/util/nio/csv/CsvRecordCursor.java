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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFileReader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;

import ssac.util.Validations;
import ssac.util.nio.FileUtil;
import ssac.util.nio.ICloseable;

/**
 * CSVファイルのレコード位置情報を保持するカーソル
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class CsvRecordCursor implements ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final CsvFileData	_fileData;
	
	private transient CsvFileReader	_recordReader;
	private transient String[]		_cachedField;
	
	private long	_lastIndex;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvRecordCursor(CsvFileData fileData) {
		this._fileData = Validations.validNotNull(fileData);
		setLastRecordIndex(-1L);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CsvFileData getFileData() {
		return _fileData;
	}
	
	public long getRecordSize() {
		return getFileData().getRecordSize();
	}
	
	/**
	 * ストリームが開いている状態の場合に <tt>true</tt> を返す。
	 */
	public boolean isOpen() {
		return (_recordReader != null && _recordReader.isOpen());
	}

	/**
	 * このストリームを閉じ、関連付けられている全てのシステムリソースを開放する。
	 * ストリームがすでに閉じている場合、このメソッドを呼び出しても何も行われない。
	 * 
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void close()// throws IOException
	{
		final CsvFileReader reader = _recordReader;
		if (reader != null) {
			FileUtil.closeStream(reader);
			this._recordReader = null;
			setLastRecordIndex(-1L);
		}
	}
	
	public String[] getRecord(long index) throws IOException
	{
		final String[] fields = getCachedFields();
		if (getLastRecordIndex() == index) {
			return fields;
		}
		
		final CsvFileReader reader = getRecordReader();
		Arrays.fill(fields, null);
		reader.readFields(fields, index);
		setLastRecordIndex(index);
		return fields;
	}
	
	public ListView getList() {
		return new ListView(this);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void finalize() throws Throwable {
		this.clone();
		super.finalize();
	}
	
	protected CsvFileReader createRecordReader() throws IOException
	{
		return new CsvFileReader(getFileData());
	}
	
	protected CsvFileReader getRecordReader() throws IOException
	{
		if (_recordReader == null) {
			_recordReader = createRecordReader();
		}
		return _recordReader;
	}
	
	protected String[] createFieldCashe() {
		return new String[getFileData().getMaxFieldSize()];
	}
	
	protected String[] getCachedFields() {
		if (_cachedField == null) {
			_cachedField = createFieldCashe();
		}
		return _cachedField;
	}
	
	protected void setCachedFields(String[] newFields) {
		this._cachedField = newFields;
	}
	
	protected long getLastRecordIndex() {
		return _lastIndex;
	}
	
	protected void setLastRecordIndex(long lastIndex) {
		this._lastIndex = lastIndex;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class ListView extends AbstractList<Object>
	{
		final CsvRecordCursor	_cursor;
		
		public ListView(CsvRecordCursor cursor) {
			this._cursor = cursor;
		}
		
		public CsvRecordCursor getCursor() {
			return _cursor;
		}

		@Override
		public Object get(int index) {
			try {
				return _cursor.getRecord(index);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		
		public int size() {
			return (int)_cursor.getRecordSize();
		}
	}
}
