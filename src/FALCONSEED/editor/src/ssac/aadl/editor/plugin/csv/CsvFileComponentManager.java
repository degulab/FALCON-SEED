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
 * @(#)CsvFileComponentManager.java	2.0.0	2012/11/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileComponentManager.java	1.17	2012/02/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileComponentManager.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileComponentManager.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.csv;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.plugin.AbComponentManager;
import ssac.aadl.editor.setting.AppSettings;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.logging.AppLogger;
import ssac.util.nio.FileUtil;
import ssac.util.nio.array.LongStoredArrayWriter;
import ssac.util.nio.csv.CsvFieldAttr;
import ssac.util.nio.csv.CsvFileData;
import ssac.util.nio.csv.CsvFileDataFactory;
import ssac.util.nio.csv.CsvFileTokenizer;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.nio.csv.CsvRecordCursor;
import ssac.util.swing.ProgressMonitorTask;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * CSVファイル閲覧用のドキュメントとビューを管理するマネージャ。
 * 
 * @version 2.0.0	2012/11/07
 *
 * @since 1.16
 */
public class CsvFileComponentManager extends AbComponentManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String PluginID = "CSVFile";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileComponentManager() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
//	public String getDefaultFileEncoding() {
//		return AppSettings.getInstance().getDefaultAadlCsvEncodingName();
//	}
	
	public String getFileEncoding() {
		return AppSettings.getInstance().getAadlCsvEncodingName();
	}

	/*
	public void setFileEncoding(String charsetName) {
		this.csvEncoding = charsetName;
	}
	*/

	//------------------------------------------------------------
	// Implement IComponentManager interfaces
	//------------------------------------------------------------
	
	public String getID() {
		return PluginID;
	}
	
	public String getName() {
		return CsvFileMessages.getInstance().pluginName;
	}
	
	public String getDescription() {
		return CsvFileMessages.getInstance().pluginDesc;
	}
	
	public Icon getDisplayIcon() {
		return CommonResources.ICON_CSVFILE;
	}
	
	public Icon getDisplayNewIcon() {
		return CommonResources.ICON_NEW_CSVFILE;
	}
	
	public Class<CsvFileModel> getSupportedDocumentClass() {
		return CsvFileModel.class;
	}
	
	public ExtensionFileFilter getSupportedFileFilter() {
		return this.forOpenFilters[0];
	}
	
	public boolean isSupportedFileType(File targetFile) {
		// ファイルの拡張子を判定する
		if (!super.isSupportedFileType(targetFile)) {
			// mismatched open file extensions
			return false;
		}
		
		// supported file
		return true;
	}
	
	/**
	 * このプラグインのドキュメントが、新規作成可能なドキュメントかを取得する。
	 * @return	常に <tt>false</tt> を返す。
	 * @since 1.16
	 */
	public boolean isAllowCreateNewDocument() {
		return false;
	}

	/**
	 * AADLエディタの標準フォントを返す。
	 * @return 標準のエディタフォント
	 */
	public Font getDefaultEditorFont() {
		return SpreadSheetTable.getDefaultTableFont();
	}
	
	public CsvFileView newDocument(File targetFile, String templateText) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	public CsvFileView openDocument(Component parentComponent, File targetFile) throws IOException
	{
		// CSVパラメータ生成
		CsvParameters csvParams = new CsvParameters();
		//--- ダブルクオートによるエスケープを許可する
		csvParams.setQuoteEscapeEnabled(true);
		//--- 改行文字もエスケープする
		csvParams.setAllowMultiLineField(true);
		
		// 指定ファイルの読み込み
		CsvFileModel doc = createDocument(parentComponent, targetFile, getFileEncoding(), csvParams);
		if (doc == null) {
			// user canceled
			return null;
		}
		
		// ファイルデータからドキュメント生成
		CsvFileView view = new CsvFileView();
		view.initialComponent(doc);
		putDocumentView(doc, view);
		//view.setEditorFont(getEditorFont());
		return view;
	}
	
	public CsvFileView onNewComponent(Component parentComponent) {
		// サポートしない
		throw new UnsupportedOperationException();
	}
	
	public CsvFileModel createDocument(Component parentComponent, final File csvFile, final String charsetName, final CsvParameters csvParams)
	throws IOException
	{
		/*
		// CSV ファイルを読み込み、インデックスを作成
		final CsvFileDataFactory factory = new CsvFileDataFactory();
		//--- CSV読み込み設定
		factory.setCsvParameters(csvParams);
		//--- 読み込み
		final CsvFileData csvdata = factory.newFileData(csvFile, charsetName);
		
		// CSV カーソルの生成
		final CsvRecordCursor cursor = new CsvRecordCursor(csvdata);
		
		// CSVファイルモデルの生成
		return new CsvFileModel(this, cursor);
		*/
		
		// CSVファイル読み込みタスクの実行
		FileOpenProgressMonitorTask task = new FileOpenProgressMonitorTask(this, csvFile, charsetName, csvParams);
		boolean result = task.execute(parentComponent);
		if (result) {
			CsvFileModel fmodel = task.getNewModel();
			
			// 表示可能行数のチェック
			if (fmodel.getRowCount() != fmodel.getRealRowCount()) {
				// 表示行数と読み込み行数が異なる。
				NumberFormat numformat = NumberFormat.getNumberInstance();
				String msg = String.format(CsvFileMessages.getInstance().msgLimitOverCsvRecords, numformat.format(fmodel.getRealRowCount()));
				AppLogger.warn(msg);
				AADLEditor.showWarningMessage(parentComponent, msg);
			}
			
			return fmodel;
		}
		
		// CSVファイル読み込みエラーの詳細取得
		Throwable taskex = task.getErrorCause();
		if (taskex == null) {
			// user canceled
			return null;
		}
		if (taskex instanceof IOException) {
			throw (IOException)taskex;
		}
		else if (taskex instanceof OutOfMemoryError) {
			throw (OutOfMemoryError)taskex;
		}
		else {
			throw new RuntimeException(taskex);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected String getFileChooserTitleForOpen() {
		return CsvFileMessages.getInstance().chooserTitleOpen;
	}

	@Override
	protected String getFileChooserTitleForSave() {
		return CsvFileMessages.getInstance().chooserTitleSave;
	}

	/**
	 * ファイル・オープン・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンスの初期化に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForOpenFileFilters() {
		return new ExtensionFileFilter[]{
				new ExtensionFileFilter(CsvFileMessages.getInstance().descExtOpen, CsvFileMessages.getInstance().extOpen),
				new ExtensionFileFilter(CsvFileMessages.getInstance().descExtCsv, CsvFileMessages.getInstance().extCsv),
		};
	}

	/**
	 * ファイル・セーブ・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンス初期化時に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForSaveFileFilters() {
		return null;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class FileOpenProgressMonitorTask extends ProgressMonitorTask
	{
		private final File				_targetCsvFile;
		private final String			_encodingCharsetName;
		private final CsvParameters	_targetCsvParams;
		private final CsvFileComponentManager	_componentManager;
		
		private CsvFileModel	_newModel;
		
		public FileOpenProgressMonitorTask(final CsvFileComponentManager manager, final File csvFile, final String charsetName, final CsvParameters csvParams) {
			super(CsvFileMessages.getInstance().chooserTitleOpen, CommonMessages.getInstance().Progress_Reading, null, 0, 0, 100);
			this._targetCsvFile = csvFile;
			this._encodingCharsetName = charsetName;
			this._targetCsvParams = csvParams;
			this._componentManager = manager;
			setMinimum(0);
			setMaximum(100+4);
			setValue(0);
		}
		
		protected CsvFileModel getNewModel() {
			return _newModel;
		}
		
		@Override
		public void processTask() throws Throwable
		{
			// ファクトリクラスの生成
			final CsvFileDataFactoryTask factory = new CsvFileDataFactoryTask();
			//--- CSV読み込み設定
			factory.setCsvParameters(_targetCsvParams);
			
			// CSVファイルの読み込み
			//--- 読み込み
			final CsvFileData csvdata = factory.newFileData(_targetCsvFile, _encodingCharsetName);
			if (isTerminateRequested()) {
				return;		// user canceled
			}
			
			// CSVファイルモデルの生成
			CsvRecordCursor cursor = new CsvRecordCursor(csvdata);
			CsvFileModel newModel = null;
			try {
				newModel = new CsvFileModel(_componentManager, cursor);
				//--- 先頭レコードをキャッシュ
				if (cursor.getRecordSize() > 0) {
					cursor.getRecord(0);
				}
			}
			catch (Throwable ex) {
				cursor.close();
				cursor = null;
				throw ex;
			}
			this._newModel = newModel;
			//--- begin progress
			incrementValue();	// value = 1 + 102 = 103;
			//--- end progress
		}
		
		/**
		 * CSV データを生成するファクトリクラスのプログレスバー対応の実装。
		 */
		protected class CsvFileDataFactoryTask extends CsvFileDataFactory
		{
			@Override
			protected CsvFileData createFileData(final File indexFile, final File textFile, final Charset encoding)
			throws IOException
			{
				CsvFileData csvData = null;
				CsvFileTokenizer csvTokenizer = null;
				LongStoredArrayWriter indexWriter = null;

				try {
					// Open CSV file by CsvFileTokenizer
					csvTokenizer = new CsvFileTokenizer(textFile, encoding);
					//--- begin progress
					final long filesize = textFile.length();
					FileOpenProgressMonitorTask.this.incrementValue();
					if (FileOpenProgressMonitorTask.this.isTerminateRequested()) {
						return null;
					}
					//--- end progress
					csvTokenizer.setCsvParameters(getCsvParameters());
					// Open Index file writer
					indexWriter = new LongStoredArrayWriter(indexFile);
					//--- begin progress
					FileOpenProgressMonitorTask.this.incrementValue();
					long recordEnd = 0L;
					final int baseProgressValue = FileOpenProgressMonitorTask.this.getValue();
					//--- end progress

					// create CsvFieldAttr empty list
					List<CsvFieldAttr> attrlist = new ArrayList<CsvFieldAttr>();
					String[] csvFields;
					long cntHeaderRecords = 0L;
					
					// read header record
					if (getUseHeaderLine()) {
						csvFields = csvTokenizer.nextRecord();
						if (csvFields != null) {
							//--- begin progress
							if (FileOpenProgressMonitorTask.this.isTerminateRequested()) {
								return null;
							}
							recordEnd = csvTokenizer.getRecordEndIndex();
							//--- end progress
							cntHeaderRecords = csvTokenizer.getRecordCount();
							for (String strField : csvFields) {
								attrlist.add(new CsvFieldAttr(strField, Object.class));
							}
							//--- begin progress
							FileOpenProgressMonitorTask.this.setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
							//--- end progress
						}
					}
					
					// read CSV records
					if (getAutoDetectDataType()) {
						//--- データ型を判別する
						csvFields = csvTokenizer.nextRecord();
						for (; csvFields != null; ) {
							//--- begin progress
							if (FileOpenProgressMonitorTask.this.isTerminateRequested()) {
								return null;
							}
							recordEnd = csvTokenizer.getRecordEndIndex();
							//--- end progress
							indexWriter.add(csvTokenizer.getRecordBeginIndex());
							indexWriter.add(csvTokenizer.getRecordEndIndex());
							// auto detect data type
							autoDetectDataTypeForCsvFields(csvFields, attrlist);
							//--- begin progress
							FileOpenProgressMonitorTask.this.setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
							//--- end progress
							// read next record
							csvFields = csvTokenizer.nextRecord();
						}
					} else {
						//--- データ型の自動判別は行わない
						csvFields = csvTokenizer.nextRecord();
						for (; csvFields != null; ) {
							//--- begin progress
							if (FileOpenProgressMonitorTask.this.isTerminateRequested()) {
								return null;
							}
							recordEnd = csvTokenizer.getRecordEndIndex();
							//--- end progress
							indexWriter.add(csvTokenizer.getRecordBeginIndex());
							indexWriter.add(recordEnd);
							//--- begin progress
							FileOpenProgressMonitorTask.this.setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
							//--- end progress
							// read next record
							csvFields = csvTokenizer.nextRecord();
						}
					}
					
					// correct CsvFieldAttr list
					ensureCsvFieldAttrListSize(csvTokenizer.getMaxFieldCount(), attrlist);
					correctCsvFieldDataTypes(attrlist);
					
					// setup CsvFileData
					csvData = new CsvFileData(indexFile, textFile, encoding, getCsvParameters());
					csvData.setRecordSize(csvTokenizer.getRecordCount() - cntHeaderRecords);
					csvData.setMaxRecordByteSize(csvTokenizer.getMaxReocrdByteSize());
					csvData.setMaxRecordCharSize(csvTokenizer.getMaxRecordLength());
					csvData.setFieldAttrs(attrlist);
					
					//--- begin progress
					FileOpenProgressMonitorTask.this.setValue(baseProgressValue + 100);
					//--- end progress
				}
				finally {
					if (indexWriter != null) {
						FileUtil.closeStream(indexWriter);
						indexWriter = null;
					}
					if (csvTokenizer != null) {
						FileUtil.closeStream(csvTokenizer);
						csvTokenizer = null;
					}
				}
				
				return csvData;
			}
		}
	}
}
