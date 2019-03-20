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
 * @(#)CsvFileSorter.java	1.90	2013/08/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ssac.aadl.runtime.AADLFunctions;
import ssac.aadl.runtime.AADLMessages;
import ssac.aadl.runtime.AADLRuntimeException;
import ssac.aadl.runtime.io.CsvFileReader;
import ssac.aadl.runtime.io.CsvFileWriter;

/**
 * CSVファイルのマージソートを実行するクラス。
 *  
 * @version 1.90	2013/08/05
 * @since 1.90
 */
public class CsvFileSorter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private final int _minBufferedRows = 6;		// このレコード数以上でバッファリングソートを許可
	private final long _reportIntervalTime = 300000L;	// 5分（ミリ秒）

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 入力ファイル **/
	private final File	_inFile;
	/** 最終出力ファイル **/
	private final File	_outFile;
	/** ファイルエンコーディング **/
	private final String	_encoding;

	/** 最初のソート時に、先頭フィールドにレコードID を付加するフラグ **/
	private boolean	_appendRecordIdAtFirst = false;
	/** 最終マージ時に、除外するフィールドのインデックス **/
	private Set<Integer>	_removeFieldIndicesAtLast;

	/** レコードコンパレータ **/
	private final CsvRecordComparator		_comparator;
	/** 処理対象外レコード数 **/
	private final long	_skipRecords;

	/** 最大レコード数 **/
	private long	_maxRecords  = 0L;
	/** 最大フィールド数 **/
	private int		_maxFields   = 0;

	/** ヘッダレコード数 **/
	private long	_headerRecords = 0L;
	/** ソート完了レコード数 **/
	private long	_sortedRecords = 0L;
	/** 制限数 **/
	private long	_numLists      = 0L;
	/** ソート開始時間 **/
	private long	_sortStartTime = 0L;
	/** 最終レポート出力時間 **/
	private long	_lastReportedTime = 0L;
	/** 最大バッファリングレコード数 **/
	private int	_maxBufferedRowCount = 0;
	/** 最小バッファリングレコード数 **/
	private int	_minBufferedRowCount = 0;

	/** 処理対象外レコードを格納したファイル **/
	private File	_headerFile     = null;
	/** 読み込み対象のマージファイル **/
	private File	_readMergeFile  = null;
	/** 書き込み対象のマージファイル **/
	private File	_writeMergeFile = null;
	/** 分割ファイル 1 **/
	private File	_splitFile1     = null;
	/** 分割ファイル 2 **/
	private File	_splitFile2     = null;
	
	private CsvFileWriter _splitWriter1 = null;
	private CsvFileWriter _splitWriter2 = null;
	private CsvFileReader _splitReader[] = new CsvFileReader[2];
	private CsvFileWriter _mergeWriter = null;
	private CsvFileReader _mergeReader = null;

	private List<String>	_nextSplitRow = null;

	/** ソート完了フラグ **/
	private boolean	_sortCompleted = false;
	/** 詳細メッセージ表示フラグ **/
	private boolean	_verbose = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileSorter(File inFile, File outFile, String encoding, BigDecimal skipRecords, CsvRecordComparator sortComparator) {
		this(inFile, outFile, encoding, clipSkipRecordCount(skipRecords), sortComparator);
	}
	
	public CsvFileSorter(File inFile, File outFile, String encoding, long skipRecords, CsvRecordComparator sortComparator) {
		if (inFile == null)
			throw new NullPointerException("The specified 'inFile' argument is null.");
		if (outFile == null)
			throw new NullPointerException("The specified 'outFile' argument is null.");
		if (sortComparator == null)
			throw new NullPointerException("CsvRecordComparator object is null.");
		
		_inFile  = inFile;
		_outFile = outFile;
		_encoding = (encoding != null && encoding.length() > 0 ? encoding : null);
		_comparator = sortComparator;
		_skipRecords = skipRecords;
		
		_removeFieldIndicesAtLast = new TreeSet<Integer>();
	}
	
	static protected long clipSkipRecordCount(BigDecimal skipRecords) {
		long numSkip;
		if (skipRecords != null && skipRecords.signum() > 0) {
			skipRecords = AADLFunctions.floor(skipRecords);
			if (skipRecords.compareTo(new BigDecimal(Long.MAX_VALUE)) < 0) {
				numSkip = skipRecords.longValue();
			} else {
				numSkip = Long.MAX_VALUE;
			}
		}
		else {
			numSkip = 0L;
		}
		return numSkip;
	}
	
	//------------------------------------------------------------
	// Internal methods(java)
	//------------------------------------------------------------

	// Debug mode ?
	// AADL:function isDebug():Boolean
	protected boolean isDebug()
	{
		return __debugFlag;
	}

	// println for Debug
	//AADL:function debugPrintln(obj:Object)
	protected void debugPrintln(Object obj)
	{
		if (isDebug()) {
			AADLFunctions.println("[Debug] " + AADLFunctions.toString(obj));
		}
	}

	static private final boolean __debugFlag = _getDebugFlag();
	
	static private final int __maxBufRows = _getMaxBufferedRows();

	static private final boolean _getDebugFlag() {
		return System.getProperties().containsKey("AADLDEBUG");
	}
	
	static private final int _getMaxBufferedRows() {
		String val = System.getProperty("CSVFILESORT_MAXBUFROWS");
		int ret = Integer.MAX_VALUE - 1;
		if (val != null && val.length() > 0) {
			BigDecimal d;
			try {
				d = new BigDecimal(val);
			} catch (Throwable ex) {
				d = null;
			}
			if (d != null) {
				if (d.signum() > 0) {
					d = d.setScale(0, RoundingMode.FLOOR);
					if (d.compareTo(new BigDecimal(ret)) < 0) {
						ret = d.intValue();
					}
				} else {
					ret = 0;
				}
			}
		}
		return ret;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isVerbose() {
		return _verbose;
	}
	
	public void setVerbose(boolean flag) {
		_verbose = flag;
	}

	/**
	 * ソートによって判明した総レコード数を返す。
	 */
	public long getMaxRecordCount() {
		return _maxRecords;
	}

	/**
	 * ソートによって判明した総フィールド数を返す。
	 */
	public int getMaxFieldCount() {
		return _maxFields;
	}

	/**
	 * ソート完了時のヘッダレコード数を返す。
	 */
	public long getHeaderRecordCount() {
		return _headerRecords;
	}

	/**
	 * ソート完了時のソートしたレコード数を返す。
	 */
	public long getSortedRecordCount() {
		return _sortedRecords;
	}
	
	public boolean getAppendRecordIdAtFirst() {
		return _appendRecordIdAtFirst;
	}
	
	public void setAppendRecordIdAtFirst(boolean toAppend) {
		_appendRecordIdAtFirst = toAppend;
	}
	
	public void clearRemoveFieldIndicesAtLast() {
		_removeFieldIndicesAtLast.clear();
	}
	
	public boolean isEmptyRemoveFieldIndicesAtLast() {
		return _removeFieldIndicesAtLast.isEmpty();
	}
	
	public int getCountOfRemoveFieldIndicesAtLast() {
		return _removeFieldIndicesAtLast.size();
	}
	
	public int[] getRemoveFieldIndicesAtLast() {
		int[] aryIndices = new int[_removeFieldIndicesAtLast.size()];
		int ai = 0;
		for (Integer index : _removeFieldIndicesAtLast) {
			aryIndices[ai++] = index;
		}
		Arrays.sort(aryIndices);
		return aryIndices;
	}
	
	public boolean addRemoveFieldIndexAtLast(int index) {
		if (index < 0)
			throw new IllegalArgumentException("The specified index is negative : " + index);
		return _removeFieldIndicesAtLast.add(index);
	}
	
	public boolean removeRemoveFieldIndexAtLast(int index) {
		return _removeFieldIndicesAtLast.remove(index);
	}
	
	public void setRemoveFieldIndicesAtLast(int...indices) {
		_removeFieldIndicesAtLast.clear();
		if (indices != null && indices.length > 0) {
			for (int index : indices) {
				if (index >= 0) {
					_removeFieldIndicesAtLast.add(index);
				}
			}
		}
	}
	
	public void setRemoveFieldIndicesAtLast(Collection<Integer> indices) {
		_removeFieldIndicesAtLast.clear();
		if (indices != null && indices.size() > 0) {
			for (Integer index : indices) {
				if (index != null && index.intValue() >= 0) {
					_removeFieldIndicesAtLast.add(index);
				}
			}
		}
	}
	
	public void sort() {
		if (_sortCompleted)
			throw new IllegalStateException("ソートは完了しています。");

		// ソート実行と結果出力
		_maxRecords = 0L;
		_maxFields  = 0;
		_headerRecords = 0L;
		_sortedRecords = 0L;
		_numLists = 0L;
		_sortStartTime = System.currentTimeMillis();
		try {
			// ソート実行
			doMergeSort();
			
			// 結果出力
			if (_removeFieldIndicesAtLast.isEmpty()) {
				// 全フィールドを出力
				outputResult();
			} else {
				// 除外フィールドを削除
				outputResultAndRemoveFieldsByIndices();
			}
			
			// sort completed
			printReportSortCompleted();
			_sortCompleted = true;
		}
		finally {
			// テンポラリファイルの削除
			deleteTempFile(_headerFile);
			deleteTempFile(_readMergeFile);
			deleteTempFile(_writeMergeFile);
			deleteTempFile(_splitFile1);
			deleteTempFile(_splitFile2);
			_headerFile = null;
			_readMergeFile = null;
			_writeMergeFile = null;
			_splitFile1 = null;
			_splitFile2 = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void createSplitReaders() {
		_splitReader[0] = _createReader(_splitFile1);
		_splitReader[1] = _createReader(_splitFile2);
	}
	
	private void createSplitWriters() {
		_splitWriter1 = _createWriter(_splitFile1);
		_splitWriter2 = _createWriter(_splitFile2);
	}
	
	private void createMergeReaderWriter() {
		// swap read file and write file
		File tmp = _readMergeFile;
		_readMergeFile = _writeMergeFile;
		_writeMergeFile = tmp;
		// create reader
		_mergeReader = _createReader(_readMergeFile);
		_mergeWriter = _createWriter(_writeMergeFile);
	}
	
	private void closeSplitReaders() {
		_closeReader(_splitReader[0]);
		_closeReader(_splitReader[1]);
		_splitReader[0] = null;
		_splitReader[1] = null;
	}
	
	private void closeSplitWriters() {
		_closeWriter(_splitWriter1);
		_closeWriter(_splitWriter2);
		_splitWriter1 = null;
		_splitWriter2 = null;
	}
	
	private void closeMergeReaderWriter() {
		_closeReader(_mergeReader);
		_closeWriter(_mergeWriter);
		_mergeReader = null;
		_mergeWriter = null;
	}
	
	private void printReportSorting() {
		if (_verbose) {
			long time = System.currentTimeMillis() - _sortStartTime;
			long interval = time - _lastReportedTime;
			if (interval >= _reportIntervalTime) {
				_lastReportedTime = time;
				long lHour = time / 3600000L;
				time = time % 3600000L;
				long lMin  = time / 60000L;
				time = time % 60000L;
				long lSec  = time / 1000L;
				time = time % 1000L;
				double per = (_numLists > 0L ? 100.0/_numLists : 100.0);
				String msg = String.format("... sorting %.1f%%(%d) (%d:%02d:%02d.%03d)", per, _numLists, lHour, lMin, lSec, time);
				System.out.println(msg);
			}
		}
	}
	
	private void printReportSortCompleted() {
		long time = System.currentTimeMillis() - _sortStartTime;
		long lHour = time / 3600000L;
		time = time % 3600000L;
		long lMin  = time / 60000L;
		time = time % 60000L;
		long lSec  = time / 1000L;
		time = time % 1000L;
		String msg = String.format("... sort completed! (%d:%02d:%02d.%03d)", lHour, lMin, lSec, time);
		System.out.println(msg);
	}
	
	private void printBufferedRowCount() {
		String msg = String.format("... buffered record count : %d , %d", _minBufferedRowCount, _maxBufferedRowCount);
		System.out.println(msg);
	}
	
	private void skipHeaderRows(CsvFileReader reader) {
		// create header temp file
		File hfile = createTempFile();
		CsvFileWriter writer = _createWriter(hfile);
		
		long num = 0L;
		try {
			List<String> record;
			for (; reader.hasNext() && num < _skipRecords; ) {
				record = reader.next();
				++_maxRecords;
				if (_appendRecordIdAtFirst) {
					// レコード先頭フィールドに、レコードIDを付加
					record.add(0, String.valueOf(_maxRecords));
				}
				_maxFields = Math.max(_maxFields, record.size());
				writeRecord(record, writer);
				++num;
			}
		}
		finally {
			_closeWriter(writer);
			writer = null;
		}
		
		if (num > 0) {
			_headerFile = hfile;
			_headerRecords = num;
		} else {
			deleteTempFile(hfile);
		}
	}

	// ソート実行部
	private void doMergeSort() {
		// first merge sort
		CsvFileReader inReader = _createReader(_inFile);
		try {
			// check has next row
			if (!inReader.hasNext()) {
				//--- レコードのないファイル
				return;
			}
			
			// skip header rows
			if (_skipRecords > 0L) {
				skipHeaderRows(inReader);
				if (!inReader.hasNext()) {
					//--- sort completed
					_writeMergeFile = _headerFile;
					_headerFile = null;
					return;
				}
			}
			
			// merge sort
			//--- create temp files
			_writeMergeFile = createTempFile();
			_splitFile1 = createTempFile();
			_splitFile2 = createTempFile();
			//--- create merge writer
			_mergeWriter = _createWriter(_writeMergeFile);
			//--- sort
			boolean hasSplit = firstMergeSort(_comparator, inReader, _mergeWriter);
			printReportSorting();
			if (!hasSplit) {
				//--- sort completed
				printReportSorting();
				return;
			}
		}
		finally {
			//--- close readers & writers
			closeMergeReaderWriter();
			closeSplitWriters();
			closeSplitReaders();
			//--- close inReader
			_closeReader(inReader);
			inReader = null;
		}
		
		// create read merge temp file
		_readMergeFile = createTempFile();
		
		// merge sort
		try {
			boolean hasSplit = false;
			do {
				_numLists = 0L;
				// create merge reader & writer
				createMergeReaderWriter();
				
				// sort
				hasSplit = fileMergeSort(_comparator, _mergeReader, _mergeWriter);
				
				// close merge reader & writer
				closeMergeReaderWriter();
				printReportSorting();
			} while (hasSplit);
		}
		finally {
			//--- close readers & writers
			closeMergeReaderWriter();
			closeSplitWriters();
			closeSplitReaders();
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean firstMergeSort(CsvRecordComparator sortComparator, CsvFileReader reader, CsvFileWriter mergeWriter)
	{
		CsvFileWriter writer = null;
		List<String> curRow = null;
		List<String> lastRow = null;
		List<String> mergedLastRow = null;
		long numMergedRows = 0L;
		boolean hasSplit = false;
		_numLists = 0L;
		_maxBufferedRowCount = 1;
		_minBufferedRowCount = 1;
		
		// buffered merge sort
		if (__maxBufRows >= _minBufferedRows) {
			CsvRecordList rowList = new CsvRecordList(4096);
			int len = 0;
			
			// first buffering split
			len = readRowsAsPossibleAtFirst(reader, rowList);
			if (len > 0) {
				_maxBufferedRowCount = len;
				_minBufferedRowCount = len;
				sortComparator.sort(rowList);
				_sortedRecords += len;
				lastRow = rowList.get(len-1);
				if (!reader.hasNext()) {
					// sort completed! All rows writes to mergeWriter.
					for (List<String> row : rowList) {
						writeRecord(row, mergeWriter);
					}
					mergeWriter.flush();
					++_numLists;
					printBufferedRowCount();
					return false;
				}
			}
			
			// write buffered rows to splitWriter1
			createSplitWriters();
			writer = _splitWriter1;
			for (List<String> row : rowList) {
				writeRecord(row, writer);
			}
			
			// buffered merge sort
			for(; len >= _minBufferedRows ;) {	// 指定のレコード以上のバッファリング実績で実行許可
				len = readRowsAsPossibleAtFirst(reader, rowList);
				if (len > 0) {
					_maxBufferedRowCount = Math.max(_maxBufferedRowCount, len);
					_minBufferedRowCount = Math.min(_minBufferedRowCount, len);
					sortComparator.sort(rowList);
					curRow = rowList.get(0);
					if (sortComparator.compare(lastRow, curRow) > 0) {
						// change list
						if (writer == _splitWriter2) {
							// merge
							closeSplitWriters();
							createSplitReaders();
							//--- merge
							Object[] ret = mergeRows(sortComparator, _splitReader, mergeWriter);
							if (mergedLastRow != null && sortComparator.compare(mergedLastRow, (List<String>)ret[0]) > 0) {
								// marged rows have multi lists.
								++_numLists;
								hasSplit = true;
							}
							mergedLastRow = (List<String>)ret[1];
							closeSplitReaders();
							createSplitWriters();
							writer = _splitWriter1;
							lastRow = null;
							numMergedRows = _sortedRecords;
						} else {
							// chage to splitWriter2
							writer = _splitWriter2;
						}
					}
					lastRow = rowList.get(len-1);
					_sortedRecords += len;
					for (List<String> row : rowList) {
						writeRecord(row, writer);
					}
				}
			}
		}
		printBufferedRowCount();
		
		// create split writers, if no split writer.
		if (writer == null) {
			createSplitWriters();
			writer = _splitWriter1;
		}
		
		// no buffering merge sort
		if (lastRow == null) {
			lastRow = reader.next();
			++_maxRecords;
			if (_appendRecordIdAtFirst) {
				lastRow.add(0, String.valueOf(_maxRecords));
			}
			_maxFields = Math.max(_maxFields, lastRow.size());
			++_sortedRecords;
			writeRecord(lastRow, writer);
		}
		for (; reader.hasNext();) {
			curRow = reader.next();
			++_maxRecords;
			if (_appendRecordIdAtFirst) {
				curRow.add(0, String.valueOf(_maxRecords));
			}
			_maxFields = Math.max(_maxFields, curRow.size());
			if (sortComparator.compare(lastRow, curRow) > 0) {
				// change list
				if (writer == _splitWriter2) {
					// merge phase
					closeSplitWriters();
					createSplitReaders();
					//--- merge
					Object[] ret = mergeRows(sortComparator, _splitReader, mergeWriter);
					if (mergedLastRow != null && sortComparator.compare(mergedLastRow, (List<String>)ret[0]) > 0) {
						// marged rows have multi lists.
						++_numLists;
						hasSplit = true;
					}
					mergedLastRow = (List<String>)ret[1];
					closeSplitReaders();
					createSplitWriters();
					writer = _splitWriter1;
					lastRow = null;
					numMergedRows = _sortedRecords;
				} else {
					// chage to splitWriter2
					writer = _splitWriter2;
				}
			}
			lastRow = curRow;
			++_sortedRecords;
			writeRecord(lastRow, writer);
		}
		//--- last merge
		if (numMergedRows < _sortedRecords) {
			// merge phase
			closeSplitWriters();
			createSplitReaders();
			//--- merge
			Object[] ret = mergeRows(sortComparator, _splitReader, mergeWriter);
			if (ret != null && mergedLastRow != null && sortComparator.compare(mergedLastRow, (List<String>)ret[0]) > 0) {
				// marged rows have multi lists.
				hasSplit = true;
			}
		}

		// completed
		++_numLists;
		return hasSplit;
	}
	
	@SuppressWarnings("unchecked")
	private boolean fileMergeSort(CsvRecordComparator sortComparator, CsvFileReader reader, CsvFileWriter mergeWriter)
	{
		List<String> mergedLastRow = null;
		boolean hasSplit = false;
		_numLists = 1L;
		
		for (; reader.hasNext();) {
			// split phase
			//--- create split writers
			createSplitWriters();
			//--- split
			splitRows(sortComparator, reader, _splitWriter1, _splitWriter2);
			//--- close split writers
			closeSplitWriters();
			
			// merge phase
			//--- create split readers
			createSplitReaders();
			//--- merge
			Object[] ret = mergeRows(sortComparator, _splitReader, mergeWriter);
			if (ret != null) {
				if (mergedLastRow != null && sortComparator.compare(mergedLastRow, (List<String>)ret[0]) > 0) {
					// marged rows have multi lists.
					++_numLists;
					hasSplit = true;
				}
				mergedLastRow = (List<String>)ret[1];
			}
			//--- close split readers
			closeSplitReaders();
		}
		if (_nextSplitRow != null) {
			//--- 最後の 1 レコード
			writeRecord(_nextSplitRow, mergeWriter);
			if (mergedLastRow != null && sortComparator.compare(mergedLastRow, _nextSplitRow) > 0) {
				// marged rows have multi lists.
				++_numLists;
				hasSplit = true;
			}
			_nextSplitRow = null;
		}
		
		return hasSplit;
	}
	
	private boolean splitRows(CsvRecordComparator sortComparator, CsvFileReader reader, CsvFileWriter splitWriter1, CsvFileWriter splitWriter2)
	{
		List<String> curRow  = null;
		List<String> lastRow = null;
		
		// first record
		if (_nextSplitRow != null) {
			lastRow = _nextSplitRow;
		} else {
			lastRow = reader.next();
		}
		writeRecord(lastRow, splitWriter1);
		
		// read first list
		boolean changeList = false;
		for (; reader.hasNext();) {
			curRow = reader.next();
			if (sortComparator.compare(lastRow, curRow) > 0) {
				// split list
				changeList = true;
				break;
			}
			writeRecord(curRow, splitWriter1);
			lastRow = curRow;
		}
		splitWriter1.flush();
		if (!changeList) {
			_nextSplitRow = null;
			return false;
		}
		
		// read second list
		writeRecord(curRow, splitWriter2);
		lastRow = curRow;
		changeList = false;
		for (; reader.hasNext();) {
			curRow = reader.next();
			if (sortComparator.compare(lastRow, curRow) > 0) {
				// split list
				changeList = true;
				break;
			}
			writeRecord(curRow, splitWriter2);
			lastRow = curRow;
		}
		splitWriter2.flush();
		if (!changeList) {
			// no more rows
			_nextSplitRow = null;
		} else {
			// remain rows
			_nextSplitRow = curRow;
		}

		// completed
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private Object[] mergeRows(CsvRecordComparator sortComparator, CsvFileReader splitReader[], CsvFileWriter mergeWriter)
	{
		List<String> firstRow = null;
		List<String> curRow;
		List<String> lastRow = null;
		Object nextRow[] = new Object[2];
		
		if (splitReader[0].hasNext() && splitReader[1].hasNext()) {
			int readerIndex;
			
			// first marge
			nextRow[0] = splitReader[0].next();
			nextRow[1] = splitReader[1].next();
			if (sortComparator.compare((List<String>)nextRow[0], (List<String>)nextRow[1]) > 0) {
				//--- nextRow[0] > nextRow[1]
				readerIndex = 1;
			} else {
				//--- nextRow[0] <= nextRow[1]
				readerIndex = 0;
			}
			firstRow = (List<String>)nextRow[readerIndex];
			writeRecord(firstRow, mergeWriter);
			lastRow = firstRow;

			// merge remain all rows
			for ( ; splitReader[readerIndex].hasNext(); ) {
				nextRow[readerIndex] = splitReader[readerIndex].next();
				if (sortComparator.compare((List<String>)nextRow[0], (List<String>)nextRow[1]) > 0) {
					//--- nextRow[0] > nextRow[1]
					readerIndex = 1;
				} else {
					//--- nextRow[0] <= nextRow[1]
					readerIndex = 0;
				}
				curRow = (List<String>)nextRow[readerIndex];
				writeRecord(curRow, mergeWriter);
				lastRow = curRow;
			}
			
			// append remain all rows
			readerIndex = (readerIndex + 1) % 2;
			lastRow = (List<String>)nextRow[readerIndex];
			writeRecord(lastRow, mergeWriter);
			for (; splitReader[readerIndex].hasNext(); ) {
				lastRow = splitReader[readerIndex].next();
				writeRecord(lastRow, mergeWriter);
			}
		}
		else if (splitReader[0].hasNext()) {
			// 分割リストが一つの場合
			firstRow = splitReader[0].next();
			lastRow = firstRow;
			writeRecord(firstRow, mergeWriter);
			for (; splitReader[0].hasNext();) {
				lastRow = splitReader[0].next();
				writeRecord(lastRow, mergeWriter);
			}
		}
		else if (splitReader[1].hasNext()) {
			// 分割リストが一つの場合
			firstRow = splitReader[1].next();
			lastRow = firstRow;
			writeRecord(firstRow, mergeWriter);
			for (; splitReader[1].hasNext();) {
				lastRow = splitReader[1].next();
				writeRecord(lastRow, mergeWriter);
			}
		}
		
		// completed
		if (firstRow != null) {
			mergeWriter.flush();
			nextRow[0] = firstRow;
			nextRow[1] = lastRow;
			return nextRow;
		} else {
			return null;
		}
	}
	
	private int readRowsAsPossibleAtFirst(CsvFileReader reader, CsvRecordList rows)
	{
		// release unused memory
		rows.clear();
		//System.gc();
		Runtime rt = Runtime.getRuntime();
		int limitLen = __maxBufRows;
		long maxMemory = rt.maxMemory();
		long totalMemory = rt.totalMemory();
		long initUsedMemory = totalMemory - rt.freeMemory();
		long limitUsedMemory = (maxMemory - initUsedMemory) / 2 + initUsedMemory;
		
		// 空き領域の半分くらいまで読み込む
		if (initUsedMemory < limitUsedMemory) {
			List<String> record;
			while (reader.hasNext()) {
				record = reader.next();
				++_maxRecords;
				if (_appendRecordIdAtFirst) {
					record.add(0, String.valueOf(_maxRecords));
				}
				_maxFields = Math.max(_maxFields, record.size());
				rows.add(record);
				long usedmem = rt.totalMemory() - rt.freeMemory();
				if ((rows.size() >= limitLen) || (usedmem > limitUsedMemory)) {
					break;	// 制限一杯
				}
			}
		}
		
		return rows.size();
	}
	
	// テンポラリファイルの削除
	private void deleteTempFile(File file) {
		if (file != null) {
			try {
				file.delete();
			} catch (Throwable ignoreEx) { ignoreEx = null; }
		}
	}
	
	private File createTempFile() {
		try {
			File tmpfile = File.createTempFile("tmpSort", ".csv");
			tmpfile.deleteOnExit();
			return tmpfile;
		} catch (Throwable ex) {
			throw new RuntimeException("Failed to create temporary file.", ex);
		}
	}

	// 結果出力(指定インデックスのフィールド削除)
	private void outputResultAndRemoveFieldsByIndices() {
		CsvFileReader reader = null;
		CsvFileWriter writer = null;
		List<String> record;
		_maxFields = 0;
		int[] removeIndices = getRemoveFieldIndicesAtLast();

		try {
			// open destination file
			writer = _createWriter(_outFile);
			
			// Copy header file
			if (_headerFile != null) {
				try {
					// Open header file
					reader = _createReader(_headerFile);
				
					// Copy header file to output file
					while (reader.hasNext()) {
						record = reader.next();
						for (int i = removeIndices.length - 1; i >= 0; --i) {
							int findex = removeIndices[i];
							if (findex < record.size()) {
								record.remove(findex);
							}
						}
						_maxFields = Math.max(_maxFields, record.size());
						//--- write
						writeRecord(record, writer);
					}
				}
				finally {
					_closeReader(reader);
					reader = null;
				}
			}
			
			// Copy result file
			if (_writeMergeFile != null) {
				try {
					// Open result file
					reader = _createReader(_writeMergeFile);
					
					// Copy result file to output file
					while (reader.hasNext()) {
						record = reader.next();
						for (int i = removeIndices.length - 1; i >= 0; --i) {
							int findex = removeIndices[i];
							if (findex < record.size()) {
								record.remove(findex);
							}
						}
						_maxFields = Math.max(_maxFields, record.size());
						//--- write
						writeRecord(record, writer);
					}
				}
				finally {
					_closeReader(reader);
					reader = null;
				}
			}
		}
		finally {
			_closeWriter(writer);
			writer = null;
		}
	}
	
	// 結果出力(ノーマル)
	private void outputResult() {
		FileChannel dstChannel = null;
		FileChannel srcChannel = null;
		try {
			// Open destination file
			try {
				dstChannel = new FileOutputStream(_outFile).getChannel();
			} catch (FileNotFoundException ex) {
				throw new AADLRuntimeException(AADLMessages.formatFileNotFoundMessage(_outFile), ex);
			} catch (SecurityException ex) {
				throw new AADLRuntimeException(formatFailedWriteMessage(_outFile), ex);
			}
			
			// Copy header file
			if (_headerFile != null) {
				// Open header file
				try {
					srcChannel = new FileInputStream(_headerFile).getChannel();
				} catch (FileNotFoundException ex) {
					throw new AADLRuntimeException(AADLMessages.formatFileNotFoundMessage(_headerFile), ex);
				} catch (SecurityException ex) {
					throw new AADLRuntimeException(AADLMessages.formatFailedReadMessage(_headerFile), ex);
				}

				// Copy header file to output file
				try {
					long fsize = srcChannel.size();
					long tsize = 0L;
					for (; tsize < fsize; ) {
						tsize += srcChannel.transferTo(tsize, (fsize-tsize), dstChannel);
					}
				} catch (IOException ex) {
					throw new AADLRuntimeException(formatFailedWriteMessage(_outFile), ex);
				}
			}

			// Copy result file
			if (_writeMergeFile != null) {
				if (srcChannel != null) {
					try {
						srcChannel.close();
					} catch (Throwable ignoreEx) {ignoreEx = null;}
					srcChannel = null;
				}
				
				// Open result file
				try {
					srcChannel = new FileInputStream(_writeMergeFile).getChannel();
				} catch (FileNotFoundException ex) {
					throw new AADLRuntimeException(AADLMessages.formatFileNotFoundMessage(_writeMergeFile), ex);
				} catch (SecurityException ex) {
					throw new AADLRuntimeException(AADLMessages.formatFailedReadMessage(_writeMergeFile), ex);
				}

				// Copy result file to output file
				try {
					long fsize = srcChannel.size();
					long tsize = 0L;
					for (; tsize < fsize; ) {
						tsize += srcChannel.transferTo(tsize, (fsize-tsize), dstChannel);
					}
				} catch (IOException ex) {
					throw new AADLRuntimeException(formatFailedWriteMessage(_outFile), ex);
				}
			}
		}
		finally {
			try {
				if (dstChannel != null) {
					dstChannel.close();
				}
				if (srcChannel != null) {
					srcChannel.close();
				}
			} catch (Throwable ignoreEx) {ignoreEx = null;}
		}
	}
	
	private void writeRecord(List<String> row, CsvFileWriter writer) {
		if (!writer.writeRecord(row)) {
			throw new AADLRuntimeException(formatFailedWriteMessage(writer.getFile()), writer.lastException());
		}
	}
	
	private CsvFileReader _createReader(File file) {
		try {
			CsvFileReader newReader;
			if (_encoding == null)
				newReader = new CsvFileReader(file);
			else
				newReader = new CsvFileReader(file, _encoding);
			return newReader;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private CsvFileWriter _createWriter(File file) {
		try {
			CsvFileWriter newWriter;
			if (_encoding == null)
				newWriter = new CsvFileWriter(file);
			else
				newWriter = new CsvFileWriter(file, _encoding);
			return newWriter;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private void _closeReader(CsvFileReader reader) {
		if (reader != null) {
			reader.close();
		}
	}
	
	private void _closeWriter(CsvFileWriter writer) {
		if (writer != null) {
			writer.close();
		}
	}
	
	static private final String formatFailedWriteMessage(File file) {
		return formatFailedWriteMessage(file==null ? (String)null : file.getAbsolutePath());
	}
	
	static private final String formatFailedWriteMessage(String filename) {
		if (filename == null) {
			return "Failed to write to file.";
		} else {
			return "Failed to write to file: \"" + filename + "\"";
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class CsvRecordList extends ArrayList<List<String>>
	{
		private static final long serialVersionUID = 1L;

		public CsvRecordList() {
			super();
		}
		
		public CsvRecordList(int initialCapacity) {
			super(initialCapacity);
		}
		
		public CsvRecordList(Collection<? extends List<String>> c) {
			super(c);
		}
	}
}
