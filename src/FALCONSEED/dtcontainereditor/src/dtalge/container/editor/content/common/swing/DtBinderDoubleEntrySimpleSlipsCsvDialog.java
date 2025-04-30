/*
 * @(#)DtBinderDoubleEntrySimpleSlipsCsvExportDialog.java	2.0.0	2025/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.swing;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.setting.DtContainerEditorSettings;
import dtalge.container.editor.view.dialog.DtContainerFileChooserManager;
import ssac.aadl.common.CommonResources;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.Files;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.JCharsetComboBox;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.StaticTextComponent;

/**
 * 複式記述簡易データ伝票のインポート／エクスポート設定ダイアログ
 * 
 * @version 2.0.0
 * @since 2.0.0
 */
public class DtBinderDoubleEntrySimpleSlipsCsvDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	/** このダイアログの最小サイズ **/
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 400);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	// file chooser
	private JFileChooser	_chooser;

	/** インポートなら <code>false</code>、エクスポートなら <code>true</code> **/
	private final boolean	_flgExport;
	
	// 貸借科目定義 CSV
	private File				_fileDefTableCsv;
	/** 貸借科目定義テーブルCSVファイルの親ディレクトリのパス **/
	private JTextComponent		_defTableCsvFileDir;
	/** 貸借科目定義テーブルCSVファイルのパスを含まない名前 **/
	private JTextComponent		_defTableCsvFileName;
	/** 貸借科目定義テーブルCSVファイルの文字コードを選択するコンボボックス **/
	private JCharsetComboBox	_cmbDefTableCsvEncoding;
	/** 貸借科目定義CSVファイルの選択ボタン **/
	private JButton				_btnChooseDefTableFile;
	
	// 複式記述簡易データ伝票 CSV
	private File				_fileSlipsCsv;
	/** 簡易データ伝票 CSV ファイルの親ディレクトリのパス **/
	private JTextComponent		_slipsCsvFileDir;
	/** 簡易データ伝票 CSV ファイルのパスを含まない名前 **/
	private JTextComponent		_slipsCsvFileName;
	/** 簡易データ伝票 CSV ファイルの文字コード選択コンボボックス **/
	private JCharsetComboBox	_cmbSlipsCsvEncoding;
	/** 簡易データ伝票 CSV ファイルの選択ボタン **/
	private JButton				_btnChooserSlipsCsvFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 複式記述簡易データ伝票のインポート／エクスポート設定ダイアログのインスタンスを生成する。
	 * @param owner		オーナー
	 * @param toExport	エクスポート用なら <code>true</code>、インポート用なら <code>false</code>
	 */
	public DtBinderDoubleEntrySimpleSlipsCsvDialog(Frame owner, boolean toExport)
	{
		super(owner, DtContainerEditorMessages.getInstance().ContentExportDlg_title, true);
		_flgExport = toExport;
		constructDtBinderDoubleEntrySimpleSlipsCsvDialog();
	}
	
	/**
	 * 複式記述簡易データ伝票のインポート／エクスポート設定ダイアログのインスタンスを生成する。
	 * @param owner		オーナー
	 * @param toExport	エクスポート用なら <code>true</code>、インポート用なら <code>false</code>
	 */
	public DtBinderDoubleEntrySimpleSlipsCsvDialog(Dialog owner, boolean toExport)
	{
		super(owner, DtContainerEditorMessages.getInstance().ContentExportDlg_title, true);
		_flgExport = toExport;
		constructDtBinderDoubleEntrySimpleSlipsCsvDialog();
	}
	
	private final void constructDtBinderDoubleEntrySimpleSlipsCsvDialog()
	{
		// Dialog title
		if (_flgExport)
			setTitle(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_title_export);
		else
			setTitle(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_title_import);

		setConfiguration(DtContainerEditorSettings.DE_SIMPLESLIPS_CSV_DLG, DtContainerEditorSettings.getInstance().getConfiguration());
	}
	
	@Override
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// initial component
		super.initialComponent();
		
		// restore
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isExportOperation() {
		return _flgExport;
	}
	
	public String getDefTableCsvEncoding() {
		return _cmbDefTableCsvEncoding.getSelectedCharsetName();
	}
	
	public File getDefTableCsvFile() {
		return _fileDefTableCsv;
	}
	
	public String getTargetCsvEncoding() {
		return _cmbSlipsCsvEncoding.getSelectedCharsetName();
	}
	
	public File getTargetCsvFile() {
		return _fileSlipsCsv;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	/**
	 * [OK] ボタンが押下された後に呼び出されるイベントハンドラ。
	 * @return	ダイアログを閉じる場合は {@code true}、それ以外の場合は {@code false}
	 */
	@Override
	protected boolean doOkAction() {
		// ファイルの指定を確認
		
		if (_fileDefTableCsv == null) {
			Application.showErrorMessage(this, DtContainerEditorMessages.getInstance().msgUnselectedDebitCreditDefTableCsvFile);
			return false;
		}
		
		if (_fileSlipsCsv == null) {
			Application.showErrorMessage(this, DtContainerEditorMessages.getInstance().msgUnselectedDoubleEntrySimpleSlipsCsvFile);
			return false;
		}
		
		// エクスポートの場合は、上書き確認
		if (_flgExport && _fileSlipsCsv.exists()) {
			String msgTitle = getTitle();
			String msgDesc = String.format(DtContainerEditorMessages.getInstance().confirmOverwriteFileParam1, _fileSlipsCsv.getName());
			int ret = Application.showConfirmMessageBox(this, msgTitle, msgDesc, JOptionPane.YES_NO_OPTION);
			if (ret != JOptionPane.YES_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 完了
		return true;
	}
	
	protected File chooseCsvFile(String title, boolean toSave)
	{
		_chooser.setDialogTitle(title);
		
		File lastFile = _fileDefTableCsv;
		if (lastFile == null) {
			lastFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.DOCUMENT);
		}
		if (lastFile != null) {
			FileFilter curFilter = _chooser.getFileFilter();
			if (curFilter instanceof ExtensionFileFilter) {
				if (curFilter.accept(lastFile)) {
					_chooser.setSelectedFile(lastFile);
				} else {
					// 拡張子を削除
					lastFile = new File(Files.removeExtension(lastFile.getPath()));
				}
			}
			_chooser.setSelectedFile(lastFile);
		}
		int ret;
		if (toSave)
			ret = _chooser.showSaveDialog(this);
		else
			ret = _chooser.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		lastFile = _chooser.getSelectedFile();
		FileFilter curFilter = _chooser.getFileFilter();
		if (curFilter instanceof ExtensionFileFilter) {
			if (!curFilter.accept(lastFile)) {
				lastFile = new File(((ExtensionFileFilter)curFilter).appendExtension(lastFile.getPath()));
			}
		}
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.DOCUMENT, lastFile);
		return lastFile;
	}

	/**
	 * 貸借科目定義テーブルCSVファイルの選択ボタン。
	 * 選択されたファイルを読み込む。
	 */
	protected void onClickedChooseDefTableCsvFileButton() {
		File selectedFile = chooseCsvFile(DtContainerEditorMessages.getInstance().DebitCreditDefinitionCsvFIle_title_FileChooser, false);	// open
		if (selectedFile == null) {
			// user canceled
			return;
		}
		
		// ファイル名を保存
		_fileDefTableCsv = selectedFile;
		refreshDefTableCsvFileInfo();
	}

	/**
	 * 複式記述簡易データ伝票CSVファイルの出力先選択ボタン。
	 */
	protected void onClickedChooseSlipsCsvFileButton() {
		boolean toSaveFile = _flgExport;
		String chooserTitle;
		if (toSaveFile) {
			// to save
			chooserTitle = DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_title_SaveFileChooser;
		}
		else {
			// to open
			chooserTitle = DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_title_OpenFileChooser;
		}
		File selectedFile = chooseCsvFile(chooserTitle, toSaveFile);
		if (selectedFile == null) {
			// user canceled
			return;
		}
		
		// パスを保存
		_fileSlipsCsv = selectedFile;
		refreshSlipsCsvFileInfo();
	}
	
	/**
	 * ダイアログが閉じられる直前に呼び出されるイベントハンドラ。
	 */
	protected void dialogClose(int result) {
		// 編集状態を解除
		
		// ダイアログを閉じる
		super.dialogClose(result);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void refreshDefTableCsvFileInfo() {
		if (_fileDefTableCsv != null) {
			_defTableCsvFileDir.setText(_fileDefTableCsv.getParentFile().getPath());
			_defTableCsvFileName.setText(_fileDefTableCsv.getName());
		}
		else {
			_defTableCsvFileDir.setText(null);
			_defTableCsvFileName.setText(null);
		}
	}
	
	protected void refreshSlipsCsvFileInfo() {
		if (_fileSlipsCsv != null) {
			_slipsCsvFileDir.setText(_fileSlipsCsv.getParentFile().getPath());
			_slipsCsvFileName.setText(_fileSlipsCsv.getName());
		}
		else {
			_slipsCsvFileDir.setText(null);
			_slipsCsvFileName.setText(null);
		}
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();

		setStoreLocation(false);	// ダイアログの位置を保存しない
		
		this.setResizable(true);	// ダイアログのサイズ変更を許可する
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);	// ダイアログの最小サイズを維持する
		
		// 親コンポーネントの中央に表示
		setLocationRelativeTo(getParent());
	}
	
	/**
	 * このダイアログのコンポーネントを生成する。
	 */
	protected void createContentComponents() {
		// CSV file choose
		_chooser = DtContainerFileChooserManager.createCsvFileOnlyChooser(true, null);
		
		// 貸借科目定義 CSV
		_defTableCsvFileDir = createStaticFileDirTextComponent();
		_defTableCsvFileName = createStaticFileNameTextComponent();
		_cmbDefTableCsvEncoding = new JCharsetComboBox();
		_cmbDefTableCsvEncoding.setSelectedCharsetName(DtContainerEditorSettings.getInstance().getAadlCsvEncodingName());
		_btnChooseDefTableFile = new JButton(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_btn_OpenFileChooser);
		_btnChooseDefTableFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedChooseDefTableCsvFileButton();
			}
		});
		
		// 複式記述簡易データ伝票 CSV
		_slipsCsvFileDir = createStaticFileDirTextComponent();
		_slipsCsvFileName = createStaticFileNameTextComponent();
		_cmbSlipsCsvEncoding = new JCharsetComboBox();
		_cmbSlipsCsvEncoding.setSelectedCharsetName(DtContainerEditorSettings.getInstance().getAadlCsvEncodingName());
		if (_flgExport)
			_btnChooserSlipsCsvFile = new JButton(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_btn_SaveFileChooser);
		else
			_btnChooserSlipsCsvFile = new JButton(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_btn_OpenFileChooser);
		_btnChooserSlipsCsvFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedChooseSlipsCsvFileButton();
			}
		});
	}
	
	private JTextComponent createStaticFileDirTextComponent() {
		return new JStaticMultilineTextPane();
	}
	
	private JTextComponent createStaticFileNameTextComponent() {
		return new StaticTextComponent();
	}
	
	/**
	 * このダイアログのメインコンテンツを初期化する。
	 * このメソッドは、{@link #createContentComponents()} よりも後に呼び出される。
	 */
	@Override
	protected void setupMainContents() {
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		Insets spaceInsets = new Insets(0, 0, 3, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.insets = spaceInsets;
		gbc.gridy = 0;
		//--- content info
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(createDefTableCsvFilePanel(), gbc);
		gbc.gridy++;
		//--- dest file
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(createSlipsCsvFilePanel(), gbc);
		
		// add main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	private JPanel createDefTableCsvFilePanel() {
		// labels
		JLabel lblFilePath = new JLabel(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_lbl_FileDir + ": ");
		JLabel lblFileName = new JLabel(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_lbl_FileName + ": ");
		JLabel lblEncoding = new JLabel(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_lbl_charset + ": ");
		
		// panel
		JPanel panel = new JPanel(new GridBagLayout());
		Border bd = BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(DtContainerEditorMessages.getInstance().DebitCreditDefinitionCsvFile_caption),
						CommonResources.DIALOG_CONTENT_BORDER
				);
		panel.setBorder(bd);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		Insets spaceInsets = new Insets(0, 0, 3, 0);
		Insets filedirLabelInsets = new Insets(3, 0, 3, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = spaceInsets;
		gbc.gridy = 0;
		//--- file path
		gbc.insets = filedirLabelInsets;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		panel.add(lblFilePath, gbc);
		gbc.gridx++;
		gbc.insets = spaceInsets;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_defTableCsvFileDir, gbc);
		gbc.gridy++;
		//--- file name
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblFileName, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_defTableCsvFileName, gbc);
		gbc.gridy++;
		//--- encoding
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblEncoding, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_cmbDefTableCsvEncoding, gbc);
		gbc.gridy++;
		//--- file chooser button
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_btnChooseDefTableFile, gbc);
		gbc.gridy++;
		
		return panel;
	}
	
	private JPanel createSlipsCsvFilePanel() {
		// labels
		JLabel lblFilePath = new JLabel(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_lbl_FileDir + ": ");
		JLabel lblFileName = new JLabel(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_lbl_FileName + ": ");
		JLabel lblEncoding = new JLabel(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvDlg_lbl_charset + ": ");
		
		// panel
		JPanel panel = new JPanel(new GridBagLayout());
		Border bd = BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(DtContainerEditorMessages.getInstance().DoubleEntrySimpleSlipsCsvFile_caption),
						CommonResources.DIALOG_CONTENT_BORDER
				);
		panel.setBorder(bd);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		Insets spaceInsets = new Insets(0, 0, 3, 0);
		Insets filedirLabelInsets = new Insets(3, 0, 3, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = spaceInsets;
		gbc.gridy = 0;
		//--- file path
		gbc.insets = filedirLabelInsets;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		panel.add(lblFilePath, gbc);
		gbc.gridx++;
		gbc.insets = spaceInsets;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_slipsCsvFileDir, gbc);
		gbc.gridy++;
		//--- file name
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblFileName, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_slipsCsvFileName, gbc);
		gbc.gridy++;
		//--- encoding
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblEncoding, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_cmbSlipsCsvEncoding, gbc);
		gbc.gridy++;
		//--- file chooser button
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_btnChooserSlipsCsvFile, gbc);
		gbc.gridy++;
		
		return panel;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
