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
 * @(#)BaseCsvTableData.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 汎用フィルタ編集用の基準 CSV テーブルデータ。
 * このデータクラスは、CSV テーブルの先頭を指定行数分読み込んだものを保持する。
 * <p>このクラスは、基本的にデータを保持するのみのクラスであり、行や列の詳細な編集インタフェースは持たない。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class BaseCsvTableData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 読み込み対象の CSV ファイルパス **/
	private final File		_csvFile;
	/** 読み込み時のファイルエンコーディングを示す文字セット名、<tt>null</tt> の場合はデフォルト **/
	private final String	_csvEncoding;
	/** CSV テーブルデータ **/
	private ArrayList<List<String>>	_csvData;
	/** 最大フィールド数 **/
	private int		_maxFields;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public BaseCsvTableData() {
		this(null, null);
	}
	
	public BaseCsvTableData(File targetFile) {
		this(targetFile, null);
	}
	
	public BaseCsvTableData(File targetFile, String fileEncoding) {
		_csvFile = targetFile;
		_csvEncoding = fileEncoding;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getFile() {
		return _csvFile;
	}
	
	public String getEncoding() {
		return _csvEncoding;
	}
	
	public ArrayList<List<String>> getData() {
		return _csvData;
	}
	
	public int getRecordCount() {
		return (_csvData==null ? 0 : _csvData.size());
	}
	
	public int getMaxFieldCount() {
		return _maxFields;
	}
	
	public boolean isEmpty() {
		return (_csvData==null ? true : _csvData.isEmpty());
	}
	
	public void clear() {
		_csvData     = null;
		_maxFields   = 0;
	}
	
	public int updateMaxFieldCount() {
		if (_csvData != null && !_csvData.isEmpty()) {
			int maxFieldCount = 0;
			for (List<String> rec : _csvData) {
				maxFieldCount = Math.max(maxFieldCount, rec.size());
			}
			_maxFields = maxFieldCount;
		}
		else {
			// empty
			_maxFields = 0;
		}
		return _maxFields;
	}
	
	public void trimRecords() {
		if (_csvData != null) {
			_csvData.trimToSize();
		}
	}
	
	public List<String> getRecord(int recIndex) {
		if (_csvData == null)
			throw new IndexOutOfBoundsException("Record index out of range : " + recIndex);
		return _csvData.get(recIndex);
	}
	
	public String getCell(int recIndex, int fldIndex) {
		if (_csvData == null)
			throw new IndexOutOfBoundsException("Record index out of range : " + recIndex);
		if (fldIndex < 0 || fldIndex >= _maxFields)
			throw new IndexOutOfBoundsException("Field index out of range : " + fldIndex);

		List<String> rec = _csvData.get(recIndex);
		return (fldIndex < rec.size() ? rec.get(fldIndex) : null);
	}
	
	public void addRecord(List<String> rec) {
		_maxFields = Math.max(_maxFields, rec.size());
		if (_csvData == null) {
			_csvData = new ArrayList<List<String>>();
		}
		_csvData.add(rec);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
