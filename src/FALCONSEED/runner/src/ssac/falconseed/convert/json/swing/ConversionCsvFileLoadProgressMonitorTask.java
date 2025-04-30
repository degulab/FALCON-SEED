/*
 * @(#)ConversionCsvFileLoadProgressMonitorTask.java	3.4.0	2020/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ssac.util.nio.FileUtil;
import ssac.util.nio.csv.CsvFileTokenizer;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.nio.csv.CsvUtil;
import ssac.util.swing.ProgressMonitorTask;

/**
 * CSV ファイル読み込み用のプログレスモニタータスク。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionCsvFileLoadProgressMonitorTask extends ProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected final File				_targetCsvFile;
	protected final Charset				_targetEncoding;
	protected final CsvParameters		_targetCsvParams;
	
	protected List<ConversionStructureCsvItem>	_result;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConversionCsvFileLoadProgressMonitorTask(String title, String desc, final File csvFile, final Charset encoding, final CsvParameters csvParams)
	{
		super(title, desc, null, 0, 0, 100);
		this._targetCsvFile = csvFile;
		this._targetEncoding = encoding;
		this._targetCsvParams = csvParams;
		setMinimum(0);
		setMaximum(100+4);
		setValue(0);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public List<ConversionStructureCsvItem> getResultData() {
		return _result;
	}
	
	@Override
	public void processTask() throws Throwable
	{
		CsvFileTokenizer csvTokenizer = null;

		try {
			// Open CSV file by CsvFileTokenizer
			csvTokenizer = new CsvFileTokenizer(_targetCsvFile, _targetEncoding);
			//--- begin progress
			final long filesize = _targetCsvFile.length();
			incrementValue();	// value = 1
			if (isTerminateRequested()) {
				return;
			}
			//--- end progress
			csvTokenizer.setCsvParameters(_targetCsvParams);
			//--- begin progress
			incrementValue();	// value = 2
			long recordEnd = 0L;
			final int baseProgressValue = getValue();
			//--- end progress

			// create CsvFieldAttr empty list
			List<ConversionStructureCsvItem> attrlist = new ArrayList<ConversionStructureCsvItem>();
			String[] csvFields;
			long cntHeaderRecords = 0L;
			
			// read header record
			if (_targetCsvParams.getUseHeaderLine()) {
				for (int hi = 0; hi < _targetCsvParams.getHeaderLineCount(); hi++) {
					csvFields = csvTokenizer.nextRecord();
					if (csvFields == null)
						break;
					//--- begin progress
					if (isTerminateRequested()) {
						return;
					}
					recordEnd = csvTokenizer.getRecordEndIndex();
					//--- end progress
					cntHeaderRecords = csvTokenizer.getRecordCount();
					for (int fi = 0; fi < csvFields.length; fi++) {
						ConversionStructureCsvItem item;
						if (fi < attrlist.size()) {
							item = attrlist.get(fi);
						} else {
							item = new ConversionStructureCsvItem();
							attrlist.add(item);
						}
						item.addHeaderName(csvFields[fi]);
					}
					//--- begin progress
					setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
					//--- end progress
				}
			}
			
			// read CSV records
			if (_targetCsvParams.getAutoDetectDataType()) {
				//--- データ型を判別する
				csvFields = csvTokenizer.nextRecord();
				for (; csvFields != null; ) {
					//--- begin progress
					if (isTerminateRequested()) {
						return;
					}
					recordEnd = csvTokenizer.getRecordEndIndex();
					//--- end progress
					// auto detect data type
					for (int fi = 0; fi < csvFields.length; fi++) {
						ConversionStructureCsvItem item;
						if (fi < attrlist.size()) {
							item = attrlist.get(fi);
						} else {
							item = new ConversionStructureCsvItem();
							//--- 新規作成の場合は、列番号をヘッダー名とする
							item.addHeaderName(String.format("[%d]", fi+1));
							attrlist.add(item);
						}
						item.incrementDataRecordCount();
						Class<?> detectedType = CsvUtil.detectDataType(csvFields[fi]);
						if (detectedType != null) {
							item.addDataType(detectedType);
						}
					}
					//--- begin progress
					setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
					//--- end progress
					// read next record
					csvFields = csvTokenizer.nextRecord();
				}
			} else {
				//--- データ型の自動判別は行わない
				csvFields = csvTokenizer.nextRecord();
				for (; csvFields != null; ) {
					//--- begin progress
					if (this.isTerminateRequested()) {
						return;
					}
					recordEnd = csvTokenizer.getRecordEndIndex();
					//--- end progress
					for (int fi = 0; fi < csvFields.length; fi++) {
						ConversionStructureCsvItem item;
						if (fi < attrlist.size()) {
							item = attrlist.get(fi);
						} else {
							item = new ConversionStructureCsvItem();
							//--- 新規作成の場合は、列番号をヘッダー名とする
							item.addHeaderName(String.format("[%d]", fi+1));
							attrlist.add(item);
						}
						item.incrementDataRecordCount();
					}
					//--- begin progress
					setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
					//--- end progress
					// read next record
					csvFields = csvTokenizer.nextRecord();
				}
			}
			
			//--- begin progress
			setValue(baseProgressValue + 100);
			//--- value = 2 + 100 = 102
			//--- end progress
			_result = attrlist;
		}
		finally {
			if (csvTokenizer != null) {
				FileUtil.closeStream(csvTokenizer);
				csvTokenizer = null;
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
