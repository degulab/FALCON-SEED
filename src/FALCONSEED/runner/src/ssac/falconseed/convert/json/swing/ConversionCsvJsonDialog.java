/*
 * @(#)CsvJsonConversionDialog.java	3.4.0	2020/03/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.bson.Document;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.logging.AppLogger;
import ssac.util.nio.FileUtil;
import ssac.util.nio.csv.CsvFileTokenizer;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.ProgressMonitorTask;

/**
 * CSVファイルをJSONファイルに変換するダイアログ。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionCsvJsonDialog extends AbBasicDialog implements ConversionJsonCsvConfigHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(800, 600);
	
	static private final String	SUBKEY_SRC_FILE_REPFIX	= ".src";
	static private final String SUBKEY_DST_FILE_PREFIX	= ".dst";
	
	static private final String	CMD_SRC_OPEN		= "src.open";
	static private final String CMD_SRC_RELOAD		= "src.reload";
	static private final String	CMD_SRC_ADD_DEST	= "src.add.dest";
	static private final String	CMD_DST_MOVE_UP		= "dst.move.up";
	static private final String	CMD_DST_MOVE_DOWN	= "dst.move.down";
	static private final String	CMD_DST_DELETE		= "dst.delete";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** ソースファイル **/
	private File			_srcFile;
	
	/** ソースファイル名 **/
	private JStaticMultilineTextPane	_lblSrcFileName;
	/** ソースファイル選択ボタン **/
	private JButton			_btnOpenSrcFile;
	/** ソースファイルの再読み込みボタン **/
	private JButton			_btnReloadSrcFile;
	
	/** JSON-CSV 変換設定 **/
	private ConversionJsonCsvConfigPanel	_pnlJsonCsvConfig;

	/** CSV ソース構造モデル読み込み時のファイルエンコーディング **/
	private Charset			_savedSrcEncoding;
	/** CSV ソース構造モデル読み込み時の CSV パラメータ **/
	private CsvParameters	_savedCsvParams;
	/** CSV-JSON 変換設定における CSV ソース構造モデル **/
	private ConversionStructureSourceTableModel<ConversionStructureCsvItem>	_srcTableModel;
	/** CSV-JSON 変換設定における CSV ソース構造パネル **/
	private ConversionStructureSourcePanel<ConversionStructureCsvItem>		_srcTablePane;
	
	/** CSV-JSON 変換設定における任意の JSON 出力構造モデル **/
	private ConversionStructureDestTableModel<ConversionStructureJsonItem>	_dstCustomTableModel;
	/** CSV-JSON 変換設定における出力構造パネル **/
	private ConversionStructureDestPanel<ConversionStructureJsonItem>		_dstTablePane;
	
	/** ソースから変換結果へ追加するボタン **/
	private JButton			_btnAddToDest;
	/** 変換結果構造の位置を一つ上へ移動するボタン **/
	private JButton			_btnDestMoveUp;
	/** 変換結果構造の位置を一つ下へ移動するボタン **/
	private JButton			_btnDestMoveDown;
	/** 変換結果構造を削除するボタン **/
	private JButton			_btnDestDelete;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConversionCsvJsonDialog(Frame owner, boolean modal) {
		super(owner, RunnerMessages.getInstance().ConversionCsv2JsonDlg_title, modal);
		setConfiguration(AppSettings.CONVERT_CSV2JSON_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ConversionCsvJsonDialog(Dialog owner, boolean modal) {
		super(owner, RunnerMessages.getInstance().ConversionCsv2JsonDlg_title, modal);
		setConfiguration(AppSettings.CONVERT_CSV2JSON_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// initial component
		super.initialComponent();
		
		// restore settings
		restoreConfiguration();
		
		// update display
		
		// setup actions
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonClicked(e);
			}
		};
		//--- Open target file
		_btnOpenSrcFile.setActionCommand(CMD_SRC_OPEN);
		_btnOpenSrcFile.addActionListener(al);
		//--- Reload target file
		_btnReloadSrcFile.setActionCommand(CMD_SRC_RELOAD);
		_btnReloadSrcFile.addActionListener(al);
		//--- Source add to dest
		_btnAddToDest.setActionCommand(CMD_SRC_ADD_DEST);
		_btnAddToDest.addActionListener(al);
		//--- Dest move up
		_btnDestMoveUp.setActionCommand(CMD_DST_MOVE_UP);
		_btnDestMoveUp.addActionListener(al);
		//--- Dest move down
		_btnDestMoveDown.setActionCommand(CMD_DST_MOVE_DOWN);
		_btnDestMoveDown.addActionListener(al);
		//--- Dest delete
		_btnDestDelete.setActionCommand(CMD_DST_DELETE);
		_btnDestDelete.addActionListener(al);
		//--- source structure table
		_srcTablePane.getTableComponent().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSourceStructureTableSelectionChanged(e);
			}
		});
		_srcTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				onSourceTableModelChanged(e);
			}
		});
		//--- dest structure table
		_dstTablePane.getTableComponent().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onDestStructureTableSelectionChanged(e);
			}
		});
		_dstCustomTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				onDestCustomTableModelChanged(e);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このウィンドウリソースを破棄する。
	 */
	public void destroy() {
		if (isDisplayable()) {
			dialogClose(IDialogResult.DialogResult_Cancel);			
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	@Override
	protected void initDialog() {
		super.initDialog();
		
//		// テーブルの幅を設定
//		Rectangle rc = _seriesTable.getVisibleRect();
//		int maxWidth = rc.width;
//		int colWidth = rc.width / 3;
//		TableColumnModel cmodel = _seriesTable.getColumnModel();
//		for (int i = 0; i < cmodel.getColumnCount() - 1; i++) {
//			cmodel.getColumn(i).setPreferredWidth(colWidth);
//			maxWidth -= colWidth;
//		}
//		cmodel.getColumn(cmodel.getColumnCount()-1).setPreferredWidth(maxWidth);
	}

	@Override
	protected boolean doOkAction() {
		
		// 完了
		return true;
	}
	
	@Override
	protected void dialogClose(int result) {
		// ダイアログを閉じる
		super.dialogClose(result);
	}
	
	protected void onButtonClicked(ActionEvent ae) {
		String cmd = ae.getActionCommand();
		switch (cmd) {
			case CMD_SRC_OPEN:
				onClickedSourceFileOpenButton();
				break;
			case CMD_SRC_RELOAD:
				onClickedSourceFileReloadButton();
				break;
			case CMD_SRC_ADD_DEST:
				onClickedSourceAddToDestButton();
				break;
			case CMD_DST_MOVE_UP:
				onClickedDestMoveUpButton();
				break;
			case CMD_DST_MOVE_DOWN:
				onClickedDestMoveDownButton();
				break;
			case CMD_DST_DELETE:
				onClickedDestDeleteButton();
				break;
		}
	}
	
	protected void onClickedSourceFileOpenButton() {
		File initFile = restoreSourceFile();
		if (initFile == null) {
			initFile = FileChooserManager.getRecommendedDirectory();
		}
		File targetFile = FileChooserManager.chooseOpenFile(this, RunnerMessages.getInstance().ConversionCsv2JsonDlg_openfile_title,
															false, initFile, FileChooserManager.getCsvFileFilter());
		if (targetFile == null) {
			// user canceled
			requestFocusInWindow();
			return;
		}
		//--- store last file
		storeSourceFile(targetFile);
		
		// 読込
		reopenSourceFile(targetFile);
	}
	
	protected void onClickedSourceFileReloadButton() {
		if (_srcFile == null)
			return;
		
		reopenSourceFile(_srcFile);
	}
	
	protected void onClickedSourceAddToDestButton() {
		if (!_srcTablePane.getTableComponent().hasSelectedCells())
			return;
		int[] sels = _srcTablePane.getTableComponent().getSelectedRows();
		if (sels.length == 0)
			return;	// 念のため

		//int index = _dstCustomTableModel.getRowCount();
		ArrayList<ConversionStructureJsonItem> dstItems = new ArrayList<ConversionStructureJsonItem>(sels.length);
		for (int i = 0; i < sels.length; i++) {
			ConversionStructureJsonItem dstItem = new ConversionStructureJsonItem();
			ConversionStructureCsvItem srcItem = _srcTableModel.getItem(_srcTablePane.getTableComponent().convertRowIndexToModel(sels[i]));
			dstItem.setAttachedItem(srcItem);
			//dstItem.setItemIndex(index++);
			dstItem.setItemIndex(-1);
			dstItem.setDefaultName(srcItem.getDefaultName());
			dstItem.refreshDisplayDataTypeString();
			dstItems.add(dstItem);
		}
		
		_dstCustomTableModel.addAllItems(dstItems);
		requestFocusInWindow();
	}
	
	protected void onClickedDestMoveUpButton() {
		if (!_dstTablePane.getTableComponent().hasSelectedCells())
			return;
		int[] sels = _dstTablePane.getTableComponent().getSelectedRows();
		if (sels.length == 0)
			return;	// 念のため
		Arrays.sort(sels);
		
		int lastSelectedRow = -1;
		for (int i = 0; i < sels.length; i++) {
			int selrow = sels[i];
			int prevrow = selrow-1;
			if (prevrow > lastSelectedRow) {
				//--- move up
				ConversionStructureJsonItem prevItem = _dstCustomTableModel.getItem(prevrow);
				ConversionStructureJsonItem curItem  = _dstCustomTableModel.getItem(selrow);
				_dstCustomTableModel.setItem(prevrow, curItem);
				_dstCustomTableModel.setItem(selrow, prevItem);
				_dstTablePane.getTableComponent().addRowSelectionInterval(prevrow, prevrow);
				_dstTablePane.getTableComponent().removeRowSelectionInterval(selrow, selrow);
				selrow = prevrow;
				sels[i] = selrow;
			}
			lastSelectedRow = selrow;
		}
		_dstTablePane.getTableComponent().scrollToVisibleCell(sels[0], 0);
		_dstTablePane.getTableComponent().scrollToVisibleCell(sels[sels.length-1], 0);
	}
	
	protected void onClickedDestMoveDownButton() {
		if (!_dstTablePane.getTableComponent().hasSelectedCells())
			return;
		int[] sels = _dstTablePane.getTableComponent().getSelectedRows();
		if (sels.length == 0)
			return;	// 念のため
		Arrays.sort(sels);
		
		int lastSelectedRow = _dstCustomTableModel.getRowCount();
		for (int i = sels.length - 1; i >= 0; i--) {
			int selrow = sels[i];
			int nextrow = selrow+1;
			if (nextrow < lastSelectedRow) {
				//--- move down
				ConversionStructureJsonItem nextItem = _dstCustomTableModel.getItem(nextrow);
				ConversionStructureJsonItem curItem  = _dstCustomTableModel.getItem(selrow);
				_dstCustomTableModel.setItem(nextrow, curItem);
				_dstCustomTableModel.setItem(selrow, nextItem);
				_dstTablePane.getTableComponent().addRowSelectionInterval(nextrow, nextrow);
				_dstTablePane.getTableComponent().removeRowSelectionInterval(selrow, selrow);
				selrow = nextrow;
				sels[i] = selrow;
			}
			lastSelectedRow = selrow;
		}
		_dstTablePane.getTableComponent().scrollToVisibleCell(sels[sels.length-1], 0);
		_dstTablePane.getTableComponent().scrollToVisibleCell(sels[0], 0);
	}
	
	protected void onClickedDestDeleteButton() {
		if (!_dstTablePane.getTableComponent().hasSelectedCells())
			return;
		int[] sels = _dstTablePane.getTableComponent().getSelectedRows();
		if (sels.length == 0)
			return;	// 念のため
		Arrays.sort(sels);
		
		// remove
		for (int i = sels.length-1; i >= 0; i--) {
			_dstCustomTableModel.removeItem(sels[i]);
		}
		requestFocusInWindow();
	}
	
	protected void onSourceStructureTableSelectionChanged(ListSelectionEvent lse) {
		refreshSourceFunctionButtons();
	}
	
	protected void onDestStructureTableSelectionChanged(ListSelectionEvent lse) {
		refreshDestFunctionButtons();
	}
	
	protected void onSourceTableModelChanged(TableModelEvent lse) {
		// row header width
		_srcTablePane.updateTableRowHeaderWidth();
	}
	
	protected void onDestCustomTableModelChanged(TableModelEvent lse) {
		// row header width
		_dstTablePane.updateTableRowHeaderWidth();
		getApplyButtonAction().setEnabled(_dstCustomTableModel.getRowCount() > 0);
	}
	
	protected void refreshSourceFileButtons() {
		_btnOpenSrcFile.setEnabled(true);
		_btnReloadSrcFile.setEnabled(_srcFile != null);
	}
	
	protected void refreshSourceFunctionButtons() {
//		if (_pnlJsonCsvConfig.isSelectedDataModelCustom()) {
			_btnAddToDest.setEnabled(_srcTablePane.getSelectedRowCount() > 0);
//		}
//		else {
//			_btnAddToDest.setEnabled(false);
//		}
	}
	
	protected void refreshDestFunctionButtons() {
//		if (_pnlJsonCsvConfig.isSelectedDataModelCustom()) {
			boolean hasSelection = (_dstTablePane.getSelectedRowCount() > 0);
			_btnDestMoveUp.setEnabled(hasSelection);
			_btnDestMoveDown.setEnabled(hasSelection);
			_btnDestDelete.setEnabled(hasSelection);
//		}
//		else {
//			_btnDestMoveUp.setEnabled(false);
//			_btnDestMoveDown.setEnabled(false);
//			_btnDestDelete.setEnabled(false);
//		}
	}

	//------------------------------------------------------------
	// Implement ConversionJsonCsvConfigHandler interfaces
	//------------------------------------------------------------

	/**
	 * データモデルの選択が変更されたときに呼び出されるイベントハンドラ。
	 * @param selected	新たに選択されたデータモデルを表す文字列(コマンド名)
	 */
	@Override
	public void onSelectionChangedDataModel(String selected) {
		// TODO Auto-generated method stub
		
	}

	//------------------------------------------------------------
	// Error messages
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected File restoreSourceFile() {
		String strPath;
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			strPath = config.getLastFilename(prefix+SUBKEY_SRC_FILE_REPFIX);
		}
		else {
			// CSV
			//--- この場合は、データファイルツリーのユーザールートディレクトリとする。
			strPath = ((RunnerFrame)Application.getApplicationMainFrame()).getDataFileUserRootDirectory().getAbsolutePath();
		}
		return (strPath==null || strPath.isEmpty() ? null : new File(strPath));
	}
	
	protected void storeSourceFile(File lastFile) {
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			config.setLastFilename(prefix+SUBKEY_SRC_FILE_REPFIX, lastFile==null ? null : lastFile.getAbsolutePath());
		}
	}
	
	protected File restoreDestinationFile() {
		String strPath;
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			strPath = config.getLastFilename(prefix+SUBKEY_DST_FILE_PREFIX);
		}
		else {
			// CSV と同じディレクトリ
			strPath = ((RunnerFrame)Application.getApplicationMainFrame()).getDataFileUserRootDirectory().getAbsolutePath();
		}
		return (strPath==null || strPath.isEmpty() ? null : new File(strPath));
	}
	
	protected void storeDestinationFile(File lastFile) {
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			config.setLastFilename(prefix+SUBKEY_DST_FILE_PREFIX, lastFile==null ? null : lastFile.getAbsolutePath());
		}
	}

	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
		
		_pnlJsonCsvConfig.restoreConfiguration();
		
		refreshSourceFileButtons();
		refreshSourceFunctionButtons();
		refreshDestFunctionButtons();
		getApplyButtonAction().setEnabled(_dstCustomTableModel.getRowCount() > 0);
	}

	@Override
	public void storeConfiguration() {
		// TODO Auto-generated method stub
		super.storeConfiguration();
		
		_pnlJsonCsvConfig.storeConfiguration();
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// 変換実行ボタン
		JButton btn = super.createApplyButton();
		btn.setText(RunnerMessages.getInstance().ConversionCsv2JsonDlg_btn_convert);
		return btn;
	}

	@Override
	protected JButton createOkButton() {
		// no OK button
		return null;
	}

	/**
	 * 各ボタンを生成する。
	 * @return	生成されたボタンの配列
	 */
	@Override
	protected JButton[] createButtons() {
		JButton[] btnlist = super.createButtons();
		
		// ボタンを入れ替え
		JButton tmp = btnlist[0];
		btnlist[0] = btnlist[1];
		btnlist[1] = tmp;
		
		return btnlist;
	}

	@Override
	protected JButton createCancelButton() {
		JButton btn = super.createCancelButton();
		btn.setText(CommonMessages.getInstance().Button_Close);
		return btn;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		this.setResizable(true);
		this.setStoreLocation(true);
		this.setStoreSize(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(320, 240);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		setupEscapeKeyBind();
	}

	@Override
	protected void setupMainContents() {
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- input file panel
		mainPanel.add(createInputFilePanel(), gbc);
		gbc.gridy++;
		//--- CSV config panel
		gbc.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(createJsonCsvConfigPanel(), gbc);
		gbc.gridy++;
		//--- Field selection panel
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(createFieldSelectionPanel(), gbc);

		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	protected void reopenSourceFile(File targetFile) {
		// CSV パラメータ作成
		Charset encoding = _pnlJsonCsvConfig.getSelectedCsvFileEncoding();
		CsvParameters csvParams = _pnlJsonCsvConfig.getCurrentCsvParameters();
		
		// CSV ファイル読み込みタスクの実行
		String title = RunnerMessages.getInstance().ConversionCsv2JsonDlg_loadsrc_title + " [" + targetFile.getName() + "]";
		ConversionCsvFileLoadProgressMonitorTask task = new ConversionCsvFileLoadProgressMonitorTask(title, null, targetFile, encoding, csvParams);
		boolean result = task.execute(this);
		// check error
		if (!result) {
			String errmsg = null;
			Throwable taskex = task.getErrorCause();
			if (taskex instanceof OutOfMemoryError) {
				errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			}
			else if (taskex != null) {
				errmsg = RunnerMessages.getInstance().ConversionCsv2JsonDlg_err_load_csv;
				errmsg += "\r\n";
				String exmsg = taskex.getLocalizedMessage();
				errmsg += (exmsg==null || exmsg.isEmpty() ? taskex.toString() : exmsg);
			}
			if (errmsg != null) {
				AppLogger.error(errmsg, taskex);
				Application.showErrorMessage(this, errmsg);
			}
			requestFocusInWindow();
			return;
		}
		
		// フルパスを表示
		if (!targetFile.equals(_srcFile)) {
			// ファイル名表示を更新
			_srcFile = targetFile;
			_lblSrcFileName.setText(targetFile.getAbsolutePath());
		}
		//--- 全データクリア
		_dstCustomTableModel.clear();
		_srcTableModel.clear();
		
		// CSV モデルの更新
		_savedSrcEncoding = encoding;
		_savedCsvParams   = csvParams;
		List<ConversionStructureCsvItem> attrlist = task.getResultData();
		_srcTableModel.clear();
		int index = 0;
		String[] lastHeaderNames = new String[csvParams.getHeaderLineCount() > 0 ? csvParams.getHeaderLineCount() : 0];
		for (ConversionStructureCsvItem item : attrlist) {
			int hnc = item.getHeaderNameCount();
			for (int i = 0; i < hnc; i++) {
				if (hnc < (hnc-1)) {
					// 最終ヘッダー行の名前が空の場合は、列番号を設定
					String strName = item.getHeaderName(i);
					if (strName == null || strName.isEmpty()) {
						item.setHeaderName(i, String.valueOf(index+1));
					}
				}
				else if (i < lastHeaderNames.length) {
					// 最終以外のヘッダー行では、左列の名前を継承
					String strName = item.getHeaderName(i);
					if (strName == null || strName.isEmpty()) {
						String strLastName = lastHeaderNames[i];
						if (strLastName != null && !strLastName.isEmpty()) {
							item.setHeaderName(i, strLastName);
						}
					}
					else {
						lastHeaderNames[i] = strName;
					}
				}
			}
			item.setItemIndex(index++);
			item.refreshDisplayValues();
			_srcTableModel.addItem(item);
		}
		refreshSourceFileButtons();
		refreshDestFunctionButtons();
		requestFocusInWindow();
	}

	/**
	 * JSON ファイルへのコンバートを実行する。
	 */
	@Override
	protected void doApplyAction() {
		// JSON ファイルへコンバート
		File initFile = restoreDestinationFile();
		if (initFile == null) {
			initFile = FileChooserManager.getRecommendedDirectory();
		}
		File targetFile = FileChooserManager.chooseSaveFileAndCheckOverwrite(this, RunnerMessages.getInstance().ConversionCsv2JsonDlg_btn_convert,
																			initFile, FileChooserManager.getJsonFileFilter());
		if (targetFile == null) {
			// user canceled
			requestFocusInWindow();
			return;
		}
		//--- store last file
		storeDestinationFile(targetFile);
		
		// convert to JSON
		String desc = String.format("%s -> %s", _srcFile.getName(), targetFile.getName());
		ConversionJsonFileSaveProgressMonitorTask task = new ConversionJsonFileSaveProgressMonitorTask(
				RunnerMessages.getInstance().ConversionCsv2JsonDlg_btn_convert,	// title
				desc,
				targetFile,
				_srcFile,
				_savedSrcEncoding,
				_savedCsvParams,
				_dstCustomTableModel);
		boolean result;
		try {
			result = task.execute(this);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().ConversionCsv2JsonDlg_err_conversion;
			msg += "\r\n";
			String exmsg = ex.getLocalizedMessage();
			msg += (exmsg==null || exmsg.isEmpty() ? ex.toString() : exmsg);
			AppLogger.error(msg, ex);
			Application.showErrorMessage(this, msg);
			requestFocusInWindow();
			return;
		}
		// check error
		if (!result) {
			String errmsg = null;
			Throwable taskex = task.getErrorCause();
			if (taskex instanceof OutOfMemoryError) {
				errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			}
			else if (taskex != null) {
				errmsg = RunnerMessages.getInstance().ConversionCsv2JsonDlg_err_conversion;
				errmsg += "\r\n";
				String exmsg = taskex.getLocalizedMessage();
				errmsg += (exmsg==null || exmsg.isEmpty() ? taskex.toString() : exmsg);
			}
			if (errmsg != null) {
				AppLogger.error(errmsg, taskex);
				Application.showErrorMessage(this, errmsg);
			}
		}
		requestFocusInWindow();
	}

	//------------------------------------------------------------
	// Create components
	//------------------------------------------------------------

	protected void createContentComponents() {
		_lblSrcFileName = createStaticLabel();
		_btnOpenSrcFile = CommonResources.createIconButton(CommonResources.ICON_BROWSE, RunnerMessages.getInstance().ConversionCsv2JsonDlg_tooltip_csvfile);
		_btnReloadSrcFile = CommonResources.createIconButton(CommonResources.ICON_REFRESH, RunnerMessages.getInstance().ConversionCsv2JsonDlg_tooltip_reload_csv);
		
		_pnlJsonCsvConfig = new ConversionJsonCsvConfigPanel(true, true, true);
		_pnlJsonCsvConfig.setConfiguration(getConfigurationPrefix(), getConfiguration());
		_pnlJsonCsvConfig.initialComponent();
		
		_srcTableModel = new ConversionStructureSourceTableModel<ConversionStructureCsvItem>();
		_srcTablePane  = new ConversionStructureSourcePanel<ConversionStructureCsvItem>(_srcTableModel);
		_srcTablePane.initialComponent();
		
		_dstCustomTableModel = new ConversionStructureDestTableModel<ConversionStructureJsonItem>();
		_dstTablePane = new ConversionStructureDestPanel<ConversionStructureJsonItem>(_dstCustomTableModel);
		_dstTablePane.initialComponent();
		
		_btnAddToDest = CommonResources.createIconButton(CommonResources.ICON_ARROW_RIGHT, RunnerMessages.getInstance().ConversionStructureButton_tooltip_addToDest);
		_btnDestMoveUp = CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, RunnerMessages.getInstance().ConversionStructureButton_tooltip_destMoveUp);
		_btnDestMoveDown = CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, RunnerMessages.getInstance().ConversionStructureButton_tooltip_destMoveDown);
		_btnDestDelete = CommonResources.createIconButton(CommonResources.ICON_DELETE, RunnerMessages.getInstance().ConversionStructureButton_tooltip_destDelete);
	}
	
	protected JStaticMultilineTextPane createStaticLabel() {
		return new JStaticMultilineTextPane();
	}
	
	protected JPanel createInputFilePanel() {
		JLabel lblCaption = new JLabel(RunnerMessages.getInstance().ConversionCsv2JsonDlg_label_inputfile+":");
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(3, 0, 0, 3);
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(lblCaption, gbc);
		
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.insets = new Insets(1, 0, 0, 0);
		pnl.add(_lblSrcFileName, gbc);
		_lblSrcFileName.setText(" ");
		
		gbc.gridx++;
		gbc.insets = new Insets(0, 2, 0, 0);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		Box btnbox = Box.createHorizontalBox();
		{
			btnbox.add(_btnOpenSrcFile);
			btnbox.add(Box.createHorizontalStrut(2));
			btnbox.add(_btnReloadSrcFile);
		}
		pnl.add(btnbox, gbc);
		
		return pnl;
	}
	
	protected JPanel createJsonCsvConfigPanel() {
		Border bd = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(RunnerMessages.getInstance().ConversionCsv2JsonDlg_label_csvconfig),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
		);
		_pnlJsonCsvConfig.setBorder(bd);
		return _pnlJsonCsvConfig;
	}
	
	protected JPanel createFieldSelectionPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		//--- labels
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(new JLabel(RunnerMessages.getInstance().ConversionStructureTable_label_csv+":"), gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnl.add(new JLabel(), gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(new JLabel(RunnerMessages.getInstance().ConversionStructureTable_label_json+":"), gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnl.add(new JLabel(), gbc);
		gbc.gridy++;
		
		//--- source structure table
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		pnl.add(_srcTablePane, gbc);
		
		//--- function buttons for source
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 5, 0, 5);
		pnl.add(_btnAddToDest, gbc);
		
		//--- destination structure table
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0,0,0,0);
		pnl.add(_dstTablePane, gbc);
		
		//--- function buttons for destination
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 2, 0, 0);
		Box dstButtons = Box.createVerticalBox();
		{
			dstButtons.add(_btnDestMoveUp);
			dstButtons.add(Box.createVerticalStrut(3));
			dstButtons.add(_btnDestMoveDown);
			dstButtons.add(Box.createVerticalStrut(3));
			dstButtons.add(_btnDestDelete);
		}
		pnl.add(dstButtons, gbc);
		
		return pnl;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * JSON コンバート用プログレスモニタータスク。
	 * 
	 * @version 3.4.0
	 * @since 3.4.0
	 */
	static protected class ConversionJsonFileSaveProgressMonitorTask extends ProgressMonitorTask
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		protected final File				_targetJsonFile;
		protected final File				_targetCsvFile;
		protected final Charset				_targetEncoding;
		protected final CsvParameters		_targetCsvParams;
		
		protected final ConversionStructureDestTableModel<ConversionStructureJsonItem> _dstModel;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------

		public ConversionJsonFileSaveProgressMonitorTask(String title, String desc, final File jsonFile, final File csvFile,
														final Charset encoding, final CsvParameters csvParams,
														final ConversionStructureDestTableModel<ConversionStructureJsonItem> dstModel)
		{
			super(title, desc, null, 0, 0, 100);
			this._targetJsonFile = jsonFile;
			this._targetCsvFile = csvFile;
			this._targetEncoding = encoding;
			this._targetCsvParams = csvParams;
			this._dstModel = dstModel;
			setMinimum(0);
			setMaximum(100+4);
			setValue(0);
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		protected Object convertToJsonData(String fieldvalue) {
			if (fieldvalue == null || fieldvalue.isEmpty())
				return null;	// null 値として出力
			
			// 真偽値
			if ("true".equalsIgnoreCase(fieldvalue) || "false".equalsIgnoreCase(fieldvalue)) {
				// 真偽値
				return Boolean.parseBoolean(fieldvalue);
			}
			
			// 数値
			BigDecimal decimal;
			try {
				decimal = new BigDecimal(fieldvalue);
			}
			catch (NumberFormatException ignoreEx) {
				decimal = null;
			}
			if (decimal != null) {
				// 整数値か判定
				//--- int 整数
				try {
					int iv = decimal.intValueExact();
					return iv;
				} catch (ArithmeticException ignoreEx) {}
				//--- long 整数
				try {
					long lv = decimal.longValueExact();
					return lv;
				} catch (ArithmeticException ignoreEx) {}
				//--- BigInteger 整数
				try {
					BigInteger bi = decimal.toBigIntegerExact();
					return bi;
				} catch (ArithmeticException ignoreEx) {}
				//--- double
				double di = decimal.doubleValue();
				try {
					if (decimal.compareTo(BigDecimal.valueOf(di)) == 0) {
						return di;	// double value
					}
				} catch (Throwable ignoreEx) {}
				//--- BigDecimal
				return decimal;
			}
			
			// 上記以外は文字列
			return fieldvalue;
		}
		
		protected String serializeJson(Document doc) {
			return com.mongodb.util.JSON.serialize(doc);
		}

		@Override
		public void processTask() throws Throwable
		{
			CsvFileTokenizer csvTokenizer = null;
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			BufferedWriter bw = null;

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

				String[] csvFields;

				// skip header record
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
						//--- begin progress
						setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
						//--- end progress
					}
				}
				
				// open writer
				fos = new FileOutputStream(_targetJsonFile);
				osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
				bw = new BufferedWriter(osw);
				bw.append('[');
				long cntDataRecords = 0L;

				// read CSV records
				//--- データ型は必ず判別する
				Document jsondoc = new Document();
				csvFields = csvTokenizer.nextRecord();
				for (; csvFields != null; csvFields = csvTokenizer.nextRecord()) {
					//--- begin progress
					if (isTerminateRequested()) {
						return;
					}
					recordEnd = csvTokenizer.getRecordEndIndex();
					//--- end progress
					//--- conversion CSV record
					int len = _dstModel.getItemCount();
					jsondoc.clear();
					for (int itemIndex = 0; itemIndex < len; itemIndex++) {
						ConversionStructureJsonItem item = _dstModel.getItem(itemIndex);
						String key = item.getDisplayName();
						int targetFieldIndex = item.getAttachedItem().getItemIndex();
						if (targetFieldIndex < csvFields.length) {
							jsondoc.append(key, convertToJsonData(csvFields[targetFieldIndex]));
						}
						else {
							//--- NULL を出力
							jsondoc.append(key, null);
						}
					}
					//--- JSON 出力
					if (cntDataRecords > 0L) {
						bw.append(',');
					}
					bw.newLine();
					bw.append(serializeJson(jsondoc));
					cntDataRecords++;
					//--- begin progress
					setValue(baseProgressValue + (int)(((double)recordEnd / (double)filesize) * 100.0));
					//--- end progress
				}
				//--- close array
				bw.newLine();
				bw.append(']');
				bw.newLine();
				//--- begin progress
				setValue(baseProgressValue + 100);
				//--- value = 2 + 100 = 102
				//--- end progress
			}
			finally {
				if (csvTokenizer != null) {
					FileUtil.closeStream(csvTokenizer);
					csvTokenizer = null;
				}
				ssac.util.io.Files.closeStream(bw);
				ssac.util.io.Files.closeStream(osw);
				ssac.util.io.Files.closeStream(fos);
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Inner classes
		//------------------------------------------------------------
	}
}
