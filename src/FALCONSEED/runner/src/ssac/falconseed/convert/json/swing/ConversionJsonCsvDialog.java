/*
 * @(#)ConversionJsonCsvDialog.java	3.4.0	2020/03/17
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.JSONReader;
import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.logging.AppLogger;
import ssac.util.nio.csv.CsvBufferedWriter;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.ProgressMonitorTask;

/**
 * JSONファイルをCSVファイルに変換するダイアログ。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionJsonCsvDialog extends AbBasicDialog implements ConversionJsonCsvConfigHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(800, 600);
	
	static private final String	SUBKEY_SRC_FILE_REPFIX	= ".src";
	static private final String SUBKEY_DST_FILE_PREFIX	= ".dst";
	
	static private final String	CMD_SRC_OPEN		= "src.open";
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
	
	/** JSON-CSV 変換設定 **/
	private ConversionJsonCsvConfigPanel	_pnlJsonCsvConfig;
	
	/** JSON-CSV 変換設定における JSON ソース構造モデルのルートドキュメント構造 **/
	private LinkedHashMap<String, ConversionStructureJsonItem>					_srcRootJsonMap;
	/** JSON-CSV 変換設定における JSON ソース構造モデル **/
	private ConversionStructureSourceTableModel<ConversionStructureJsonItem>	_srcTableModel;
	/** JSON-CSV 変換設定における JSON ソース構造パネル **/
	private ConversionStructureSourcePanel<ConversionStructureJsonItem>			_srcTablePane;
	
	/** JSON-CSV 変換設定における任意の CSV 出力構造モデル **/
	private ConversionStructureDestTableModel<ConversionStructureCsvItem>		_dstCustomTableModel;
	/** JSON-CSV 変換設定における出力構造パネル **/
	private ConversionStructureDestPanel<ConversionStructureCsvItem>			_dstTablePane;
	
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
	
	public ConversionJsonCsvDialog(Frame owner, boolean modal) {
		super(owner, RunnerMessages.getInstance().ConversionJson2CsvDlg_title, modal);
		setConfiguration(AppSettings.CONVERT_JSON2CSV_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ConversionJsonCsvDialog(Dialog owner, boolean modal) {
		super(owner, RunnerMessages.getInstance().ConversionJson2CsvDlg_title, modal);
		setConfiguration(AppSettings.CONVERT_JSON2CSV_DLG, AppSettings.getInstance().getConfiguration());
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
		File targetFile = FileChooserManager.chooseOpenFile(this, RunnerMessages.getInstance().ConversionJson2CsvDlg_openfile_title,
															false, initFile, FileChooserManager.getJsonFileFilter());
		if (targetFile == null) {
			// user canceled
			requestFocusInWindow();
			return;
		}
		//--- store last file
		storeSourceFile(targetFile);
		
		// ソースファイルを開く
		// JSON ファイル読み込みタスクの実行
		String title = RunnerMessages.getInstance().ConversionJson2CsvDlg_loadsrc_title + " [" + targetFile.getName() + "]";
		JsonFileLoadProgressMonitorTask task = new JsonFileLoadProgressMonitorTask(title, null, targetFile);
		boolean result = task.execute(this);
		// check error
		if (!result) {
			String errmsg = null;
			Throwable taskex = task.getErrorCause();
			if (taskex instanceof OutOfMemoryError) {
				errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			}
			else if (taskex != null) {
				errmsg = RunnerMessages.getInstance().ConversionJson2CsvDlg_err_load_json;
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
		
		// 読み込み成功
		//--- フルパスを表示
		_srcFile = targetFile;
		_lblSrcFileName.setText(targetFile.getAbsolutePath());
		//--- 全データクリア
		_dstCustomTableModel.clear();
		_srcTableModel.clear();
		//--- JSON モデルの更新(子孫要素もすべて展開)
		_srcRootJsonMap = task.getResultData();
		_srcTableModel.clear();
		if (_srcRootJsonMap != null && !_srcRootJsonMap.isEmpty()) {
			int itemindex = 0;
			//--- マップ要素
			for (Map.Entry<String, ConversionStructureJsonItem> entry : _srcRootJsonMap.entrySet()) {
				ConversionStructureJsonItem item = entry.getValue();
				item.setParent(null);	// root から削除
				item.setItemIndex(itemindex++);
				item.refreshAllDescendantsDisplayValues();
				// JSON モデルへ追加
				addJsonItemToSourceTableModel(item);
			}
		}
		//--- 表示を更新
		refreshDestFunctionButtons();
		refreshSourceFunctionButtons();
		requestFocusInWindow();
	}
	
	/**
	 * 指定されたソースアイテムが、CSV へ変換可能な要素かを判定する。
	 * 基本的に、JSON コレクション要素のみの場合は、変換対象にはしない。
	 * ただし、JSON コレクション要素以外に、プリミティブ要素も含まれている場合は、変換対象とするが、JSON コレクションは変換されない。
	 * @param item	判定対象の JSON 要素
	 * @return	変換可能な場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean isAllowConversion(ConversionStructureJsonItem item) {
		return (item.isPrimitiveData() || !item.isCollectionType());
	}
	
	protected void onClickedSourceAddToDestButton() {
		if (!_srcTablePane.getTableComponent().hasSelectedCells())
			return;
		int[] sels = _srcTablePane.getTableComponent().getSelectedRows();
		if (sels.length == 0)
			return;	// 念のため
		
		//
		// JSON コレクション要素以外を出力対象へ追加
		//
		ArrayList<ConversionStructureCsvItem> dstItems = new ArrayList<ConversionStructureCsvItem>(sels.length);
		for (int i = 0; i < sels.length; i++) {
			ConversionStructureJsonItem srcItem = _srcTableModel.getItem(_srcTablePane.getTableComponent().convertRowIndexToModel(sels[i]));
			if (isAllowConversion(srcItem)) {
				ConversionStructureCsvItem dstItem = new ConversionStructureCsvItem();
				dstItem.setAttachedItem(srcItem);
				//dstItem.setItemIndex(index++);
				dstItem.setItemIndex(-1);
				dstItem.setDefaultName(srcItem.getDefaultName());
				dstItem.refreshDisplayDataTypeString();
				dstItems.add(dstItem);
			}
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
				ConversionStructureCsvItem prevItem = _dstCustomTableModel.getItem(prevrow);
				ConversionStructureCsvItem curItem  = _dstCustomTableModel.getItem(selrow);
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
				ConversionStructureCsvItem nextItem = _dstCustomTableModel.getItem(nextrow);
				ConversionStructureCsvItem curItem  = _dstCustomTableModel.getItem(selrow);
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
	
	protected void refreshSourceFunctionButtons() {
//		if (_pnlJsonCsvConfig.isSelectedDataModelCustom()) {
			boolean toEnable = false;
			if (_srcTablePane.getSelectedRowCount() > 0) {
				// JSON コレクション要素のみの場合は、出力不可
				int[] selrows = _srcTablePane.getTableComponent().getSelectedRows();
				if (selrows != null && selrows.length > 0) {
					for (int i = 0; i < selrows.length; i++) {
						ConversionStructureJsonItem item = _srcTableModel.getItem(selrows[i]);
						if (isAllowConversion(item)) {
							// 変換可能なアイテムが含まれていれば、ボタン有効
							toEnable = true;
						}
					}
				}
			}
			_btnAddToDest.setEnabled(toEnable);
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
	
//	protected String getErrorEmptyNameKey(int rowIndex, String columnName) {
//		return String.format(CsvFileMessages.getInstance().msgDtBaseNameEmpty, (rowIndex+1), columnName);
//	}
//	
//	protected String getErrorEmptyDataTypeKey(int rowIndex, String columnName) {
//		return String.format(CsvFileMessages.getInstance().msgDtBaseTypeEmpty, (rowIndex+1), columnName);
//	}
//	
//	protected String getErrorIllegalBaseKey(int rowIndex, String columnName) {
//		String msg = String.format(CsvFileMessages.getInstance().msgDtBaseIllegalBaseKeyChars, (rowIndex+1), columnName);
//		msg = msg + "\n    " + CsvFileMessages.getInstance().ExportDtalgeDlg_illegalBaseKeyChars;
//		return msg; 
//	}
//	
//	protected String getErrorIllegalDataTypeKey(int rowIndex, String columnName) {
//		return String.format(CsvFileMessages.getInstance().msgDtBaseIllegalTypeKey, (rowIndex+1), columnName);
//	}
//	
//	protected String getErrorDtBaseMultiple(int existRowIndex, int rowIndex) {
//		return String.format(CsvFileMessages.getInstance().msgDtBaseMultiple, (existRowIndex+1), (rowIndex+1));
//	}
//	
//	protected String getErrorFailedToAutoDetect() {
//		return CsvFileMessages.getInstance().msgFailedToAutoDetect;
//	}
//	
//	protected String getErrorEmptySelectedFile() {
//		return CsvFileMessages.getInstance().msgOutputFileEmpty;
//	}

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
		btn.setText(RunnerMessages.getInstance().ConversionJson2CsvDlg_btn_convert);
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
	
	protected void addJsonItemToSourceTableModel(ConversionStructureJsonItem jsonitem) {
		// 自身を登録
		_srcTableModel.addItem(jsonitem);
		
		// マップ要素を登録
		if (jsonitem.isKeyValueType()) {
			LinkedHashMap<String, ConversionStructureJsonItem> jsonmap = jsonitem.ensureJsonMap();
			for (Map.Entry<String, ConversionStructureJsonItem> entry : jsonmap.entrySet()) {
				addJsonItemToSourceTableModel(entry.getValue());
			}
		}
		
		// リスト要素を登録
		if (jsonitem.isListType()) {
			ArrayList<ConversionStructureJsonItem> jsonlist = jsonitem.ensureJsonList();
			for (ConversionStructureJsonItem subitem : jsonlist) {
				addJsonItemToSourceTableModel(subitem);
			}
		}
	}

	/**
	 * CSV ファイルへのコンバートを実行する。
	 */
	@Override
	protected void doApplyAction() {
		// JSON ファイルへコンバート
		File initFile = restoreDestinationFile();
		if (initFile == null) {
			initFile = FileChooserManager.getRecommendedDirectory();
		}
		File targetFile = FileChooserManager.chooseSaveFileAndCheckOverwrite(this, RunnerMessages.getInstance().ConversionJson2CsvDlg_btn_convert,
																			initFile, FileChooserManager.getCsvFileFilter());
		if (targetFile == null) {
			// user canceled
			requestFocusInWindow();
			return;
		}
		//--- store last file
		storeDestinationFile(targetFile);
		
		// CSV パラメータ作成
		Charset encoding = _pnlJsonCsvConfig.getSelectedCsvFileEncoding();
		CsvParameters csvParams = _pnlJsonCsvConfig.getCurrentCsvParameters();
		
		// convert to CSV
		String desc = String.format("%s -> %s", _srcFile.getName(), targetFile.getName());
		ConversionCsvFileSaveProgressMonitorTask task = new ConversionCsvFileSaveProgressMonitorTask(
															RunnerMessages.getInstance().ConversionJson2CsvDlg_btn_convert,	// title
															desc,
															_srcFile,
															targetFile,
															encoding,
															csvParams,
															_srcRootJsonMap,
															_dstCustomTableModel);
		boolean result;
		try {
			result = task.execute(this);
		}
		catch (Throwable ex) {
			String msg = RunnerMessages.getInstance().ConversionJson2CsvDlg_err_conversion;
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
				errmsg = RunnerMessages.getInstance().ConversionJson2CsvDlg_err_conversion;
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
		_btnOpenSrcFile = CommonResources.createIconButton(CommonResources.ICON_BROWSE, RunnerMessages.getInstance().ConversionJson2CsvDlg_tooltip_jsonfile);
		
		_pnlJsonCsvConfig = new ConversionJsonCsvConfigPanel(false, false, true);
		_pnlJsonCsvConfig.setConfiguration(getConfigurationPrefix(), getConfiguration());
		_pnlJsonCsvConfig.initialComponent();
		
		_srcTableModel = new ConversionStructureSourceTableModel<ConversionStructureJsonItem>();
		_srcTablePane  = new ConversionStructureSourcePanel<ConversionStructureJsonItem>(_srcTableModel);
		_srcTablePane.initialComponent();
		
		_dstCustomTableModel = new ConversionStructureDestTableModel<ConversionStructureCsvItem>();
		_dstTablePane = new ConversionStructureDestPanel<ConversionStructureCsvItem>(_dstCustomTableModel);
		_dstTablePane.initialComponent();
		
		_btnAddToDest = CommonResources.createIconButton(CommonResources.ICON_ARROW_RIGHT, RunnerMessages.getInstance().ConversionJson2CsvDlg_tooltip_addToDest);
		_btnDestMoveUp = CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, RunnerMessages.getInstance().ConversionStructureButton_tooltip_destMoveUp);
		_btnDestMoveDown = CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, RunnerMessages.getInstance().ConversionStructureButton_tooltip_destMoveDown);
		_btnDestDelete = CommonResources.createIconButton(CommonResources.ICON_DELETE, RunnerMessages.getInstance().ConversionStructureButton_tooltip_destDelete);
	}
	
	protected JStaticMultilineTextPane createStaticLabel() {
		return new JStaticMultilineTextPane();
	}
	
	protected JPanel createInputFilePanel() {
		JLabel lblCaption = new JLabel(RunnerMessages.getInstance().ConversionJson2CsvDlg_label_inputfile+":");
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
		pnl.add(_btnOpenSrcFile, gbc);
		
		return pnl;
	}
	
	protected JPanel createJsonCsvConfigPanel() {
		Border bd = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(RunnerMessages.getInstance().ConversionJson2CsvDlg_label_csvconfig),
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
		pnl.add(new JLabel(RunnerMessages.getInstance().ConversionStructureTable_label_json+":"), gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnl.add(new JLabel(), gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(new JLabel(RunnerMessages.getInstance().ConversionStructureTable_label_csv+":"), gbc);
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
	
	static protected Document parseKeyValueObject(JSONReader jreader) throws IOException
	{
		JSONEventType jtype;
		Document doc = new Document();
		String lastName = "";
		while ((jtype = jreader.next()) != null) {
			if (jtype == JSONEventType.END_OBJECT) {
				break;
			}
			else if (jtype == JSONEventType.NAME) {
				lastName = jreader.getString();
			}
			else {
				doc.append(lastName, jreader.getValue(Object.class));
			}
		}
		doc = Document.parse(doc.toJson());
		return doc;
	}
	
	/**
	 * ソースファイル読み込み用プログレスモニタータスク。
	 * 
	 * @version 3.4.0
	 * @since 3.4.0
	 */
	static protected class JsonFileLoadProgressMonitorTask extends ProgressMonitorTask
	{
		/** ソースとなる JSON ファイル **/
		protected File		_srcJsonFile;
		/** 読み込み結果 **/
		protected LinkedHashMap<String, ConversionStructureJsonItem>	_jsonDocMap;
		
		public JsonFileLoadProgressMonitorTask(String title, String desc, File srcFile)
		{
			super(title, desc, null, 0, 0, 100);
			_srcJsonFile = srcFile;
			setValue(0);
			_jsonDocMap = new LinkedHashMap<String, ConversionStructureJsonItem>();
		}
		
		public LinkedHashMap<String, ConversionStructureJsonItem> getResultData() {
			return _jsonDocMap;
		}
		
		protected void parseJsonDocument(Document jsondoc) {
			for (Map.Entry<String, Object> entry : jsondoc.entrySet()) {
				String jsonKey = entry.getKey();
				// KeyValue 要素
				ConversionStructureJsonItem item = _jsonDocMap.get(jsonKey);
				if (item == null) {
					// 新規作成
					item = new ConversionStructureJsonItem();
					item.setJsonName(jsonKey);
					_jsonDocMap.put(jsonKey, item);
				}
				// 値をパース
				item.parseJsonValue(entry.getValue());
			}
		}
		
		@Override
		public void processTask() throws Throwable
		{
			// データサイズ
//			long flen = _srcJsonFile.length();
//			long cntRead  = 0L;	// 読込ドキュメントバイト数
//			long cntWrite = 0L;	// 書き込み済みドキュメント数
			
			// 読み込み
			FileInputStream fis = null;
			InputStreamReader isr = null;
			JSONReader jreader = null;
			try {
				fis = new FileInputStream(_srcJsonFile);
				isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				jreader = new JSON().getReader(isr);
				
				// JSONオブジェクトを読み込む
				JSONEventType jtype;
				while (!isTerminateRequested() && (jtype = jreader.next()) != null) {
					if (jtype == JSONEventType.START_ARRAY) {
						// リスト型
						while (!isTerminateRequested() && (jtype = jreader.next()) != null) {
							if (jtype == JSONEventType.END_ARRAY) {
								break;
							}
							else if (jtype == JSONEventType.START_OBJECT) {
								Document doc = parseKeyValueObject(jreader);
								// オブジェクトの読み込み
								parseJsonDocument(doc);
							}
							else if (jtype == JSONEventType.NULL) {
								// 最上位JSON配列要素の NULL 値は無視
							}
							else {
								throw new RuntimeException(RunnerMessages.getInstance().MongoToolDlg_err_jsonTopListElemNotKeyVal);
							}
						}
					}
					else if (jtype == JSONEventType.START_OBJECT) {
						// マップ型
						Document doc = parseKeyValueObject(jreader);
						// オブジェクトの読み込み
						parseJsonDocument(doc);
					}
					else {
						// その他のデータ型
						throw new RuntimeException(RunnerMessages.getInstance().MongoToolDlg_err_jsonStartPrimitive);
					}
				}
			}
			finally {
				ssac.util.io.Files.closeStream(isr);
				ssac.util.io.Files.closeStream(fis);
				jreader = null;
				isr = null;
				fis = null;
			}
		}
	}

	/**
	 * JSON ファイルを CSV ファイルに変換する、プログレスタスク。
	 * JSON の階層が不定のため、ヘッダー行を出力する場合は単一行のみとする。
	 */
	static protected class ConversionCsvFileSaveProgressMonitorTask extends ProgressMonitorTask
	{
		protected final File			_targetJsonFile;
		protected final File			_targetCsvFile;
		protected final Charset			_targetEncoding;
		protected final CsvParameters	_targetCsvParams;

		protected final LinkedHashMap<String, ConversionStructureJsonItem>					_srcRootJsonMap;
		protected final ConversionStructureDestTableModel<ConversionStructureCsvItem>		_dstModel;
		
		protected ConversionCsvFileSaveProgressMonitorTask(String title, String desc, final File jsonFile, final File csvFile,
															final Charset encoding, final CsvParameters csvParams,
															final LinkedHashMap<String, ConversionStructureJsonItem> srcJsonMap,
															final ConversionStructureDestTableModel<ConversionStructureCsvItem> dstModel)
		{
			super(title, desc, null, 0, 0, 100);
			this._targetJsonFile = jsonFile;
			this._targetCsvFile = csvFile;
			this._targetEncoding = encoding;
			this._targetCsvParams = csvParams;
			this._srcRootJsonMap = srcJsonMap;
			this._dstModel = dstModel;
			setMinimum(0);
			setMaximum(100+4);
			setValue(0);
		}
		
		protected void clearAllDescendantsJsonData() {
			for (Map.Entry<String, ConversionStructureJsonItem> entry : _srcRootJsonMap.entrySet()) {
				entry.getValue().clearThisAndAllDescendantsUserData();
			}
		}
		
		protected void parseAndSetJsonValue(ConversionStructureJsonItem targetItem, Object jsonValue) {
			//--- データ型判定
			if (ConversionStructureJsonItem.isJsonObject(jsonValue)) {
				// JSON-Map
				targetItem.setUserData(jsonValue);	// 念のため、保存
				Map<String,ConversionStructureJsonItem> srcmap = targetItem.getJsonMap();	// null ではないはず
				@SuppressWarnings("unchecked")
				Map<String,?> jsonmap = (Map<String,?>)jsonValue;
				for (Map.Entry<String, ?> entry : jsonmap.entrySet()) {
					parseAndSetJsonValue(srcmap.get(entry.getKey()), entry.getValue());
				}
			}
			else if (ConversionStructureJsonItem.isJsonArray(jsonValue)) {
				// JSON-List
				targetItem.setUserData(jsonValue);	// 念のため、保存
				List<ConversionStructureJsonItem> srclist = targetItem.getJsonList();	// null ではないはず
				List<?> jsonary = (List<?>)jsonValue;
				int limit = jsonary.size();
				for (int index = 0; index < limit; index++) {
					parseAndSetJsonValue(srclist.get(index), jsonary.get(index));
				}
			}
			else if (jsonValue != null) {
				// 値
				if (org.bson.types.Decimal128.class.isInstance(jsonValue)) {
					// BigDecimal へ変換
					BigDecimal dval = ((org.bson.types.Decimal128)jsonValue).bigDecimalValue();
					if (BigDecimal.ZERO.compareTo(dval) == 0) {
						// 0 は "0"
						targetItem.setUserData("0");
					} else {
						targetItem.setUserData(dval.stripTrailingZeros().toPlainString());
					}
				}
				else if (BigDecimal.class.isInstance(jsonValue)) {
					// BigDecimal は余分な 0 を除去したプレーンなテキストとして出力
					BigDecimal dval = (BigDecimal)jsonValue;
					if (BigDecimal.ZERO.compareTo(dval) == 0) {
						// 0 は "0"
						targetItem.setUserData("0");
					} else {
						targetItem.setUserData(dval.stripTrailingZeros().toPlainString());
					}
				}
				else {
					// 上記以外は、出力用の文字列として値を保存
					targetItem.setUserData(String.valueOf(jsonValue));
				}
			}
			else {
				// 念のため、null を登録
				targetItem.setUserData(null);
			}
		}
		
		protected void writeCsvRecordByJsonDocument(CsvBufferedWriter writer, final int numFields, Document jsondoc) throws Throwable
		{
			// JSON データをすべて収集
			clearAllDescendantsJsonData();
			for (Map.Entry<String, Object> entry : jsondoc.entrySet()) {
				String jsonKey = entry.getKey();
				//--- 同じ JSON ファイルからソースモデルを構築しているので、名前に対応するデータは存在するはず
				parseAndSetJsonValue(_srcRootJsonMap.get(jsonKey), entry.getValue());
			}
			
			// データ行の出力
			int itemlen = _dstModel.getItemCount();
			for (int index = 0; index < itemlen; index++) {
				ConversionStructureCsvItem csvItem = _dstModel.getItem(index);
				ConversionStructureJsonItem jsonItem = (ConversionStructureJsonItem)csvItem.getAttachedItem();
				Object jsonValue = jsonItem.getUserData();
				writer.addField(jsonValue==null ? null : jsonValue.toString());
			}
			writer.newRecordEnsureFieldCount(numFields);
		}

		@Override
		public void processTask() throws Throwable
		{
			// 出力設定
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			CsvBufferedWriter writer = null;
			
			// 読み込み
			FileInputStream fis = null;
			InputStreamReader isr = null;
			JSONReader jreader = null;
			try {
				// JSON ファイルを開く
				fis = new FileInputStream(_targetJsonFile);
				isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				jreader = new JSON().getReader(isr);
				
				// 出力先 CSV ファイルを開く
				fos = new FileOutputStream(_targetCsvFile);
				osw = new OutputStreamWriter(fos, _targetEncoding);
				writer = new CsvBufferedWriter(osw, _targetCsvParams);
				
				// フィールド数
				final int numFields = _dstModel.getItemCount();
				
				// ヘッダー行の出力
				if (_targetCsvParams.getUseHeaderLine()) {
					int itemlen = _dstModel.getItemCount();
					for (int index = 0; index < itemlen; index++) {
						ConversionStructureCsvItem csvItem = _dstModel.getItem(index);
						writer.addField(csvItem.getDisplayName());
					}
					writer.newRecordEnsureFieldCount(numFields);
				}
				
				// JSON オブジェクトを読み込む
				JSONEventType jtype;
				while (!isTerminateRequested() && (jtype = jreader.next()) != null) {
					if (jtype == JSONEventType.START_ARRAY) {
						// リスト型
						while (!isTerminateRequested() && (jtype = jreader.next()) != null) {
							if (jtype == JSONEventType.END_ARRAY) {
								break;
							}
							else if (jtype == JSONEventType.START_OBJECT) {
								Document doc = parseKeyValueObject(jreader);
								// オブジェクトの出力
								writeCsvRecordByJsonDocument(writer, numFields, doc);
							}
							else if (jtype == JSONEventType.NULL) {
								// 最上位JSON配列要素の NULL 値は無視
							}
							else {
								throw new RuntimeException(RunnerMessages.getInstance().MongoToolDlg_err_jsonTopListElemNotKeyVal);
							}
						}
					}
					else if (jtype == JSONEventType.START_OBJECT) {
						// マップ型
						Document doc = parseKeyValueObject(jreader);
						// オブジェクトの出力
						writeCsvRecordByJsonDocument(writer, numFields, doc);
					}
					else {
						// その他のデータ型
						throw new RuntimeException(RunnerMessages.getInstance().MongoToolDlg_err_jsonStartPrimitive);
					}
				}
			}
			finally {
				// 出力ファイルを閉じる
				if (writer != null) {
					try {
						writer.close();
					} catch (Throwable ignoreEx) {}
					writer = null;
				}
				ssac.util.io.Files.closeStream(osw);
				ssac.util.io.Files.closeStream(fos);
				osw = null;
				fos = null;
				// 入力ファイルを閉じる
				ssac.util.io.Files.closeStream(isr);
				ssac.util.io.Files.closeStream(fis);
				jreader = null;
				isr = null;
				fis = null;
				
				// ソースモデルのデータから、ユーザーデータを除去しておく
				clearAllDescendantsJsonData();
			}
		}
	}
}
