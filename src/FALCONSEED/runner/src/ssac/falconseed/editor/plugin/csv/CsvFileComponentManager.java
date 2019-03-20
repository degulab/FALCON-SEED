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
 * @(#)CsvFileComponentManager.java	2.0.0	2012/10/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileComponentManager.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileComponentManager.java	1.21	2012/06/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileComponentManager.java	1.13	2012/02/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileComponentManager.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.swing.CsvFileSaveConfigDialog;
import ssac.falconseed.editor.plugin.AbComponentManager;
import ssac.falconseed.editor.view.IEditorView;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.nio.FileUtil;
import ssac.util.nio.array.LongStoredArrayWriter;
import ssac.util.nio.csv.CsvBufferedWriter;
import ssac.util.nio.csv.CsvFieldAttr;
import ssac.util.nio.csv.CsvFileData;
import ssac.util.nio.csv.CsvFileDataFactory;
import ssac.util.nio.csv.CsvFileTokenizer;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.nio.csv.CsvRecordCursor;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.ProgressMonitorTask;
import ssac.util.swing.SwingTools;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * CSVファイル閲覧用のドキュメントとビューを管理するマネージャ。
 * 
 * @version 2.0.0	2012/10/02
 */
public class CsvFileComponentManager extends AbComponentManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** このプラグインの ID **/
	static public final String PluginID = "CSVFile";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileComponentManager() {
		super();
		setSaveFileFilters(FileChooserManager.getCsvFileFilter());
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
		return openDocument(parentComponent, targetFile, null, null);
	}
	
	public CsvFileView openDocument(Component parentComponent, File targetFile, String charsetName, CsvParameters csvParams)
	throws IOException
	{
		// file encoding
		if (Strings.isNullOrEmpty(charsetName)) {
			charsetName = getFileEncoding();
		}
		
		// CSV parameters
		if (csvParams == null) {
			// CSVパラメータ生成
			csvParams = new CsvParameters();
			//--- ダブルクオートによるエスケープを許可する
			csvParams.setQuoteEscapeEnabled(true);
			//--- 改行文字もエスケープする
			csvParams.setAllowMultiLineField(true);
		}
		
		// 指定ファイルの読み込み
		CsvFileModel doc = createDocument(parentComponent, targetFile, charsetName, csvParams);
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

	public CsvFileModel createDocument(Component parentComponent, final File csvFile,
										final String charsetName, final CsvParameters csvParams)
	throws IOException
	{
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
				ModuleRunner.showWarningMessage(parentComponent, msg);
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
	
	public CsvFileModel saveDocument(Component parentComponent, final CsvFileModel csvModel,
										final File csvFile, final String charsetName, final CsvParameters csvParams)
	throws IOException
	{
		// CSVファイル書き込みタスクの実行
		FileSaveProgressMonitorTask task = new FileSaveProgressMonitorTask(this, csvModel, csvFile, charsetName, csvParams);
		boolean result = task.execute(parentComponent);
		if (result) {
			return task.getNewModel();
		}
		
		// CSVファイル書き込み/読み込みエラーの詳細取得
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
	
	@Override
	public boolean onSaveAsComponent(Component parentComponent, IEditorView targetView) {
		Validations.validArgument((targetView instanceof CsvFileView), "'targetView' is not CsvFileView instance.");
		return saveAsComponent(parentComponent, (CsvFileView)targetView, false);
	}
	
	public boolean saveAsComponent(Component parentComponent, CsvFileView targetView, boolean withConfig)
	{
		// setup base directory
		//--- テンポラリファイルが指定されたときのディレクトリを、テンポラリ以外のディレクトリに変更する。
		//--- この場合は、データファイルツリーのユーザールートディレクトリとする。
		File orgRecFile = FileChooserManager.getRecommendedDirectory();
		FileChooserManager.setRecommendedDirectory(targetView.getFrame().getDataFileUserRootDirectory());
		setLastSelectedFile(FileChooserManager.getInitialDocumentFile(targetView.getDocumentFile()));
		FileChooserManager.setRecommendedDirectory(orgRecFile);
		
		// choose target file
		File targetFile = chooseSaveFile(parentComponent, getFileChooserTitleForSave());
		if (targetFile == null) {
			// not selected
			return false;
		}
		FileChooserManager.setLastChooseDocumentFile(targetFile);
		
		// check read only
		if (((RunnerFrame)targetView.getFrame()).isReadOnlyFile(targetFile)) {
			String errmsg = String.format(CommonMessages.getInstance().msgCannotWriteCauseReadOnly, targetFile.getName());
			AppLogger.warn("AbComponentManager#onSaveAsComponent(\"" + targetFile + "\") : " + errmsg);
			Application.showWarningMessage(targetView.getFrame(), errmsg);
			return false;
		}
		
		// configuration
		String csvEncoding;
		CsvParameters csvParams;
		if (withConfig) {
			// 保存時のCSV設定を確認
			CsvFileSaveConfigDialog dlg;
			Window win = SwingTools.getWindowForComponent(parentComponent);
			if (win instanceof Frame) {
				dlg = new CsvFileSaveConfigDialog((Frame)win, targetFile);
			} else {
				dlg = new CsvFileSaveConfigDialog((Dialog)win, targetFile);
			}
			dlg.setConfiguration(AppSettings.CSVFILESAVECONFIG_DLG, AppSettings.getInstance().getConfiguration());
			dlg.initialComponent();
			dlg.setVisible(true);
			dlg.dispose();
			if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
				// user canceled
				return false;
			}
			//--- パラメータを取得
			csvEncoding = dlg.getEncodingCharsetName();
			csvParams = dlg.getCsvParameters();
		} else {
			// 標準のCSV設定
			csvParams = new CsvParameters();
			// ヘッダー行を使用していれば、ヘッダー行も出力する
			csvParams.setUseHeaderLine(targetView.getDocument().getCsvParameters().getUseHeaderLine());
			// 保存時のエンコードは、デフォルト設定のCSVファイル・エンコード
			csvEncoding = getFileEncoding();
		}
		
		// save
		try {
			// ドキュメントを保存し、保存先のファイルで新しいドキュメントを生成
			final CsvFileModel oldModel = targetView.getDocument();
			CsvFileModel newModel = saveDocument(targetView.getFrame(), oldModel, targetFile, csvEncoding, csvParams);
			if (newModel == null) {
				return false;		// user canceled
			}
			
			// 表示可能行数のチェック
			if (newModel.getRowCount() != newModel.getRealRowCount()) {
				// 表示行数と読み込み行数が異なる。
				NumberFormat numformat = NumberFormat.getNumberInstance();
				String msg = String.format(CsvFileMessages.getInstance().msgLimitOverCsvRecords, numformat.format(newModel.getRealRowCount()));
				AppLogger.warn(msg);
				ModuleRunner.showWarningMessage(parentComponent, msg);
			}
			
			// 新しいドキュメントを適用
			removeDocument(oldModel);
			putDocumentView(newModel, targetView);
			targetView.setDocument(newModel);
			oldModel.releaseViewResources();
			
			// 開きなおした場合は、左上カラムを表示
			targetView.getTableComponent().scrollToVisibleCell(0, 0);
			targetView.refreshEditingStatus();
			return true;
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(
					CommonMessages.MessageID.ERR_FILE_NOTFILE, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(
					CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (IOException ex) {
			String errmsg = CommonMessages.getErrorMessage(
					CommonMessages.MessageID.ERR_FILE_WRITE, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
	}
	
	public boolean exportDtalgeAsComponent(Component parentComponent, CsvFileView targetView) {
		// get document model
		CsvFileModel docModel = targetView.getDocument();
		Validations.validNotNull(docModel, "The specified Target Editor View has no document.");
		
		// check record count
		if (docModel.getFileData().getRecordSize() <= 0) {
			// There is no record can be exported.
			String errmsg = CsvFileMessages.getInstance().msgExportDtalgeNoRecords;
			AppLogger.error(errmsg);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		
		// export setting
		ExportDtalgeDialog dlg;
		Window win = SwingTools.getWindowForComponent(parentComponent);
		if (win instanceof Frame) {
			dlg = new ExportDtalgeDialog((Frame)win, docModel);
		} else {
			dlg = new ExportDtalgeDialog((Dialog)win, docModel);
		}
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return false;
		}
		
		// create CSV parameter
		String csvEncoding = getFileEncoding();
		CsvParameters csvParams = new CsvParameters();	// use default parameters
		
		// export to Dtalge CSV table format
		File targetFile = dlg.getSelectedFile();
		DtalgeHeaderTableModel dtHeader = dlg.getTableModel();
		ExportDtalgeProgressMonitorTask task = new ExportDtalgeProgressMonitorTask(this, docModel, targetFile, csvEncoding, csvParams, dtHeader);
		try {
			if (task.execute(parentComponent)) {
				// 処理成功
				return true;
			}
			Throwable taskex = task.getErrorCause();
			if (taskex instanceof IOException) {
				throw (IOException)taskex;
			}
			else if (taskex instanceof OutOfMemoryError) {
				throw (OutOfMemoryError)taskex;
			}
			else if (taskex != null) {
				throw new RuntimeException(taskex);
			}
			// 処理中断
			return false;
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(
					CommonMessages.MessageID.ERR_FILE_NOTFILE, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(
					CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (IOException ex) {
			String errmsg = CommonMessages.getErrorMessage(
					CommonMessages.MessageID.ERR_FILE_WRITE, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
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

	/**
	 * 指定のCSVモデルをデータ代数のテーブル形式CSVファイルにエクスポートするプログレスタスク。
	 * 現在の実装では出力のみとし、出力したファイルの読み込みは行わない。
	 */
	static protected class ExportDtalgeProgressMonitorTask extends FileSaveProgressMonitorTask
	{
		/**
		 * 基底キーの省略記号文字
		 */
		static protected final String BASEKEY_OMITTED = "#";
		/**
		 * DtAlgeSet テーブル形式のCSVファイルフォーマットの第1行目のキーワード
		 */
		static protected final String CSV_TABLE_KEYWORD_V2	= "#DtalgebraTable2";

		static protected final String CSV_VALUE_EMPTY		= "";
		static protected final String CSV_COMMAND_PREFIX	= "!%";
		static protected final String CSV_COMMAND_NULL	= CSV_COMMAND_PREFIX + "N";
		
		protected final DtalgeHeaderTableModel	_dtalgeHeaderModel;
		
		public ExportDtalgeProgressMonitorTask(final CsvFileComponentManager manager,
													final CsvFileModel srcModel, final File dtalgeFile, final String charsetName,
													final CsvParameters csvParams, final DtalgeHeaderTableModel headerModel)
		{
			this(CsvFileMessages.getInstance().progressExportDtalge_title,
				  CsvFileMessages.getInstance().progressExportDtalge_desc,
				  manager, srcModel, dtalgeFile, charsetName, csvParams, headerModel);
		}
		
		protected ExportDtalgeProgressMonitorTask(String title, String desc, final CsvFileComponentManager manager,
													final CsvFileModel srcModel, final File dtalgeFile, final String charsetName,
													final CsvParameters csvParams, final DtalgeHeaderTableModel headerModel)
		{
			super(title, desc, manager, srcModel, dtalgeFile, charsetName, csvParams);
			this._dtalgeHeaderModel = headerModel;
			setMaximum(109);
		}

		@Override
		protected void readProgressTask() throws Throwable
		{
			// 読み込み処理は行わない
		}
		
		protected String convertDtBaseKeyString(String value) {
			if (value == null) {
				return BASEKEY_OMITTED;
			}
			else {
				value = value.trim();
				if (value.length() > 0) {
					return value;
				} else {
					return BASEKEY_OMITTED;
				}
			}
		}

		@Override
		protected boolean writeCsvHeader(CsvBufferedWriter writer, CsvParameters csvParams, CsvFileModel csvModel, CsvRecordCursor readCursor)
		throws IOException
		{
			final DtalgeHeaderTableModel headerModel = _dtalgeHeaderModel;
			final int numRows = headerModel.getRowCount();
			
			// データ代数テーブル形式のキーワードを出力する
			writer.addField(CSV_TABLE_KEYWORD_V2);
			writer.newRecordEnsureFieldCount(numRows);
			//--- begin progress
			incrementValue();	// value = 1;
			if (isTerminateRequested()) {
				return false;
			}
			//--- end progress
			int row;
			
			// name key
			for (row = 0; row < numRows; row++) {
				writer.addField(convertDtBaseKeyString(headerModel.getNameKey(row)));
				//--- begin progress
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
			}
			writer.newRecordEnsureFieldCount(numRows);
			//--- begin progress
			incrementValue();	// value = 2;
			//--- end progress
			
			// data type key
			for (row = 0; row < numRows; row++) {
				writer.addField(convertDtBaseKeyString(headerModel.getTypeKey(row).toString()));
				//--- begin progress
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
			}
			writer.newRecordEnsureFieldCount(numRows);
			//--- begin progress
			incrementValue();	// value = 3;
			//--- end progress
			
			// attribute key
			for (row = 0; row < numRows; row++) {
				writer.addField(convertDtBaseKeyString(headerModel.getAttributeKey(row)));
				//--- begin progress
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
			}
			writer.newRecordEnsureFieldCount(numRows);
			//--- begin progress
			incrementValue();	// value = 4;
			//--- end progress
			
			// subject key
			for (row = 0; row < numRows; row++) {
				writer.addField(convertDtBaseKeyString(headerModel.getSubjectKey(row)));
				//--- begin progress
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
			}
			writer.newRecordEnsureFieldCount(numRows);
			//--- begin progress
			incrementValue();	// value = 5;
			//--- end progress
			
			// 完了
			return true;
		}

		@Override
		protected boolean writeCsvRecords(CsvBufferedWriter writer, CsvParameters csvParams, CsvFileModel csvModel, CsvRecordCursor readCursor)
		throws IOException
		{
			final DtalgeHeaderTableModel headerModel = _dtalgeHeaderModel;
			final int baseProgressValue = getValue();
			
			// レコードを読み込み、その内容を出力する
			final int numColumns = csvModel.getColumnCount();
			final long numRecords = readCursor.getRecordSize();
			for (long recIndex = 0L; recIndex < numRecords;) {
				String[] fields = readCursor.getRecord(recIndex++);
				for (int colIndex = 0; colIndex < fields.length; colIndex++) {
					String value = fields[colIndex];
					if (Strings.isNullOrEmpty(value)) {
						// the field is null ro empty.
						if (headerModel.getNullState(colIndex)) {
							// Empty value to write NULL command
							value = CSV_COMMAND_NULL;
						} else {
							// Empty value to write empty
							value = null;
						}
					}
					else if (value.startsWith(CSV_COMMAND_PREFIX)) {
						// need to escape command prefix pattern
						value = CSV_COMMAND_PREFIX.concat(value);
					}
					writer.addField(value);
				}
				writer.newRecordEnsureFieldCount(numColumns);
				//--- begin progress
				setValue(baseProgressValue + (int)(((double)recIndex / (double)numRecords) * 100.0));
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
			}
			
			//--- begin progress
			setValue(baseProgressValue + 100);
			//--- end progress
			return true;
		}
	}

	/**
	 * 指定のCSVモデルを新しいファイルに出力し、その内容を新しいCSVモデルとして読み込む、プログレスタスク。
	 */
	static protected class FileSaveProgressMonitorTask extends FileOpenProgressMonitorTask
	{
		protected final CsvFileModel		_sourceModel;
		
		public FileSaveProgressMonitorTask(final CsvFileComponentManager manager, final CsvFileModel srcModel,
											final File csvFile, final String charsetName, final CsvParameters csvParams)
		{
			this(CsvFileMessages.getInstance().chooserTitleSave, CommonMessages.getInstance().Progress_Writing,
					manager, srcModel, csvFile, charsetName, csvParams);
		}
		
		protected FileSaveProgressMonitorTask(String title, String desc, final CsvFileComponentManager manager, final CsvFileModel srcModel,
												final File csvFile, final String charsetName, final CsvParameters csvParams)
		{
			super(title, desc, manager, csvFile, charsetName, csvParams);
			this._sourceModel = srcModel;
			setMaximum(207);
		}

		/**
		 * ヘッダー行を出力する。
		 * {@link ssac.util.nio.csv.CsvParameters#getUseHeaderLine()} が <tt>false</tt> を返す場合、
		 * このメソッドは何も行わない。
		 * @param writer		ライター
		 * @param csvParams		<code>CsvParameters</code> オブジェクト
		 * @param csvModel		<code>CsvFileModel</code> オブジェクト
		 * @param readCursor	<code>CsvRecordCursor</code> オブジェクト
		 * @return	処理が最後まで完了した場合は <tt>true</tt>、処理中止リクエストが発生した場合は <tt>false</tt>
		 * @throws IOException	入出力エラーが発生した場合
		 */
		protected boolean writeCsvHeader(final CsvBufferedWriter writer, final CsvParameters csvParams,
											final CsvFileModel csvModel, final CsvRecordCursor readCursor)
		throws IOException
		{
			if (csvParams.getUseHeaderLine()) {
				final int numColumns = csvModel.getColumnCount();
				for (int i = 0; i < numColumns; i++) {
					writer.addField(_sourceModel.getColumnName(i));
				}
				writer.newRecordEnsureFieldCount(numColumns);
			}
			
			return true;
		}

		/**
		 * CSVレコードをすべて出力する。
		 * このメソッドは、指定されたカーソルの先頭からレコード終端まで、指定されたライターに出力する。
		 * プログレスの位置は、このメソッドが呼び出された位置を 0 とし、完了で 100 が加算された状態となる。
		 * @param writer		ライター
		 * @param csvParams		<code>CsvParameters</code> オブジェクト
		 * @param csvModel		<code>CsvFileModel</code> オブジェクト
		 * @param readCursor	<code>CsvRecordCursor</code> オブジェクト
		 * @return	処理が最後まで完了した場合は <tt>true</tt>、処理中止リクエストが発生した場合は <tt>false</tt>
		 * @throws IOException	入出力エラーが発生した場合
		 */
		protected boolean writeCsvRecords(final CsvBufferedWriter writer, final CsvParameters csvParams,
											final CsvFileModel csvModel, final CsvRecordCursor readCursor)
		throws IOException
		{
			final int baseProgressValue = getValue();
			
			// レコードを読み込み、その内容を出力する
			final int numColumns = csvModel.getColumnCount();
			final long numRecords = readCursor.getRecordSize();
			for (long recIndex = 0L; recIndex < numRecords;) {
				writer.addAllFields(readCursor.getRecord(recIndex++));
				writer.newRecordEnsureFieldCount(numColumns);
				//--- begin progress
				setValue(baseProgressValue + (int)(((double)recIndex / (double)numRecords) * 100.0));
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
			}
			
			//--- begin progress
			setValue(baseProgressValue + 100);
			//--- end progress
			return true;
		}
		
		/**
		 * 指定されたファイルに、指定されたCSVモデルの全レコードを出力する。
		 * @param writer		ライター
		 * @param csvParams		<code>CsvParameters</code> オブジェクト
		 * @param csvModel		<code>CsvFileModel</code> オブジェクト
		 * @param readCursor	<code>CsvRecordCursor</code> オブジェクト
		 * @return	処理が最後まで完了した場合は <tt>true</tt>、処理中止リクエストが発生した場合は <tt>false</tt>
		 * @throws IOException	入出力エラーが発生した場合
		 */
		protected boolean writeCsvFile(final File outFile, final String csvEncoding, final CsvParameters csvParams, final CsvFileModel csvModel)
		throws IOException
		{
			// 出力設定
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			CsvBufferedWriter writer = null;
			
			// 書き込み
			try {
				// ストリームを開く
				fos = new FileOutputStream(outFile);
				osw = new OutputStreamWriter(new FileOutputStream(outFile), csvEncoding);
				writer = new CsvBufferedWriter(osw, csvParams);
				
				// 読み込み用カーソルを取得
				CsvRecordCursor readCursor = csvModel.getSearchCursor();
				//--- begin progress
				incrementValue();
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
				
				// ヘッダー行を出力する
				if (!writeCsvHeader(writer, csvParams, csvModel, readCursor)) {
					return false;
				}
				//--- begin progress
				incrementValue();
				if (isTerminateRequested()) {
					return false;
				}
				//--- end progress
				
				// レコード行を出力する
				if (!writeCsvRecords(writer, csvParams, csvModel, readCursor)) {
					return false;
				}
				
				// 出力完了
				writer.flush();
			}
			finally {
				if (writer != null) {
					FileUtil.closeStream(writer);
					writer = null;
				}
				if (osw != null) {
					Files.closeStream(osw);
					osw = null;
				}
				if (fos != null) {
					Files.closeStream(fos);
					fos = null;
				}
			}
			
			// 完了
			return true;
		}

		/**
		 * 指定されたファイルを削除する。
		 * このメソッドはエラーを返さない。
		 */
		protected void removeCsvFile(File outFile) {
			try {
				if (outFile.exists()) {
					outFile.delete();
				}
			}
			catch (Throwable ignoreEx) {}
		}

		/**
		 * ターゲットファイルを読み込み、新しいCSVモデルを生成する。
		 * このメソッドは、スーパークラスである {@link FileOpenProgressMonitorTask#processTask()} を実行する。
		 * @throws Throwable	処理中に例外が発生した場合
		 */
		protected void readProgressTask() throws Throwable
		{
			//--- 新しいCSVファイルの読み込み
			setDescription(CommonMessages.getInstance().Progress_Reading);
			super.processTask();
		}
		
		@Override
		public void processTask() throws Throwable
		{
			// テンポラリファイルを作成
			File outFile;
			if (_targetCsvFile.exists()) {
				outFile = File.createTempFile(".CsvSave", ".csv", _targetCsvFile.getParentFile());
			} else {
				// 出力先が存在しないので、出力名で作成
				outFile = _targetCsvFile;
			}
			//--- begin progress
			if (isTerminateRequested()) {
				//--- この時点でキャンセルされた場合は、作成したファイルを削除
				removeCsvFile(outFile);
				return;
			}
			incrementValue();	// value = 1
			//--- end progress
			
			// 出力
			try {
				writeCsvFile(outFile, _encodingCharsetName, _targetCsvParams, _sourceModel);
				//--- value = 103
			}
			catch (Throwable ex) {
				// 例外が発生した場合は、作成したファイルを削除
				removeCsvFile(outFile);
				throw ex;
			}
			
			// 出力先へリネーム
			//--- begin progress
			if (isTerminateRequested()) {
				//--- この時点でキャンセルされた場合は、作成したファイルを削除
				removeCsvFile(outFile);
				return;
			}
			//--- end progress
			if (_targetCsvFile != outFile) {
				try {
					//--- 既存のファイルを消す
					if (_targetCsvFile.exists()) {
						if (!_targetCsvFile.delete()) {
							throw new IOException("Cannot rename to target file.");
						}
					}
					//--- 名前変更
					if (!outFile.renameTo(_targetCsvFile)) {
						throw new IOException("Cannot rename to target file.");
					}
				}
				catch (Throwable ex) {
					// 例外が発生した場合は、作成したファイルを削除
					removeCsvFile(outFile);
					throw ex;
				}
			}
			//--- begin progress
			incrementValue();	// value = 104
			//--- end progress
			
			//--- 新しいCSVファイルの読み込み
			readProgressTask();
			//--- value = 104 + 103 = 207
		}
	}

	/**
	 * CSV ファイルを読み込み、新しいCSVモデルを生成するプログレスタスク。
	 */
	static protected class FileOpenProgressMonitorTask extends ProgressMonitorTask
	{
		protected final File				_targetCsvFile;
		protected final String			_encodingCharsetName;
		protected final CsvParameters		_targetCsvParams;
		protected final CsvFileComponentManager	_componentManager;
		
		private CsvFileModel	_newModel;
		
		public FileOpenProgressMonitorTask(final CsvFileComponentManager manager,
											final File csvFile, final String charsetName, final CsvParameters csvParams)
		{
			this(CsvFileMessages.getInstance().chooserTitleOpen, CommonMessages.getInstance().Progress_Reading, manager,
					csvFile, charsetName, csvParams);
		}
		
		protected FileOpenProgressMonitorTask(String title, String desc, final CsvFileComponentManager manager,
												final File csvFile, final String charsetName, final CsvParameters csvParams)
		{
			super(title, desc, null, 0, 0, 100);
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
			final CsvFileDataFactory factory = new CsvFileDataFactoryTask();
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
					FileOpenProgressMonitorTask.this.incrementValue();	// value = 1
					if (FileOpenProgressMonitorTask.this.isTerminateRequested()) {
						return null;
					}
					//--- end progress
					csvTokenizer.setCsvParameters(getCsvParameters());
					// Open Index file writer
					indexWriter = new LongStoredArrayWriter(indexFile);
					//--- begin progress
					FileOpenProgressMonitorTask.this.incrementValue();	// value = 2
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
								//attrlist.add(new CsvFieldAttr(strField, Object.class));
								//--- ヘッダーでは、型情報を確定しない
								attrlist.add(new CsvFieldAttr(strField, null));
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
					//--- value = 2 + 100 = 102
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
