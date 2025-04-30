/*
 * @(#)DtContainerContentExportDialog.java	1.0.0	2022/12/16
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
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.DtContainerContentDisplayableType;
import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.DtContainerContentTypesManager;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
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
 * データコンテナ要素のエクスポートダイアログ。
 * 主に、要素のファイル出力などを行うインターフェースとなる。
 * 
 * @version 1.0.0
 */
public class DtContainerContentExportDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	/** このダイアログの最小サイズ **/
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 300);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	// file chooser
	private JFileChooser	_chooser;
	
	/** エクスポートターゲット **/
	protected IDtContainerContentTreeNode		_ndExportTarget;
	protected DtContainerContentDisplayableType	_targetDisplayableType;
	
	/** 出力先ファイル **/
	private File				_dstFile;
	/** JSON 形式での保存が必要なファイルなら {@code true} */
	private final boolean		_flgTargetSlipObject;

	/** コンテンツのパス **/
	private JTextComponent		_srcContentPath;
	/** コンテンツの種類 **/
	private JTextComponent		_srcContentType;
	/** ファイルの親ディレクトリのパス **/
	private JTextComponent		_dstFileDir;
	/** ファイルのパスを含まない名前 **/
	private JTextComponent		_dstFileName;
	/** CSV 形式でのエクスポートを選択するラジオボタン **/
	private JRadioButton		_rdoCsvFile;
	/** XML 形式でのエクスポートを選択するラジオボタン **/
	private JRadioButton		_rdoXmlFile;
	/** ファイルのテキスト形式を選択するラジオボタンのグループ **/
	private ButtonGroup			_bgTextTypes;
	/** CSV ファイルの文字コードを選択するコンボボックス **/
	private JCharsetComboBox	_cmbCsvEncoding;
	/** ファイル選択ボタン **/
	private JButton				_btnChooseFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public DtContainerContentExportDialog(Frame owner, boolean forSlipObject, IDtContainerContentTreeNode exportTarget)
	{
		super(owner, DtContainerEditorMessages.getInstance().ContentExportDlg_title, true);
		_flgTargetSlipObject = forSlipObject;
		constructDtContainerContentExportDialog(exportTarget);
	}
	
	public DtContainerContentExportDialog(Dialog owner, boolean forSlipObject, IDtContainerContentTreeNode exportTarget)
	{
		super(owner, DtContainerEditorMessages.getInstance().ContentExportDlg_title, true);
		_flgTargetSlipObject = forSlipObject;
		constructDtContainerContentExportDialog(exportTarget);
	}
	
	private final void constructDtContainerContentExportDialog(IDtContainerContentTreeNode exportTarget)
	{
		if (exportTarget == null)
			throw new NullPointerException("Export target is null");
		_ndExportTarget = exportTarget;
		switch (_ndExportTarget.getContentType()) {
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				_targetDisplayableType = DtContainerContentTypesManager.getInstance().getDisplayableType(DtContainerContentTypes.ContentDtalge);
				break;
			default:
				_targetDisplayableType = DtContainerContentTypesManager.getInstance().getDisplayableType(_ndExportTarget.getContentType());
		}
		if (_flgTargetSlipObject != DtContainerContentTypesManager.getInstance().isDtSlipObject(exportTarget.getContentType())) {
			throw new IllegalArgumentException("Export target is not content type that is different 'forSlipObject' argument.");
		}
		
		setConfiguration(DtContainerEditorSettings.CONTENT_EXPORT_DLG, DtContainerEditorSettings.getInstance().getConfiguration());
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
	
	public boolean isDtSlipObject() {
		return _flgTargetSlipObject;
	}
	
	public boolean isTextTypeXml() {
		return (!_flgTargetSlipObject && _rdoXmlFile.isSelected());
	}
	
	public boolean isTextTypeCsv() {
		return (!_flgTargetSlipObject && _rdoCsvFile.isSelected());
	}
	
	public String getCsvEncoding() {
		return _cmbCsvEncoding.getSelectedCharsetName();
	}
	
	public File getDestinationFile() {
		return _dstFile;
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
		// ファイルが指定されているか？
		if (_dstFile == null) {
			Application.showErrorMessage(this, DtContainerEditorMessages.getInstance().msgNoDestinationFile);
			return false;
		}
		
		// 上書き確認
		if (_dstFile.exists()) {
			String msgTitle = getTitle();
			String msgDesc = String.format(DtContainerEditorMessages.getInstance().confirmOverwriteFileParam1, _dstFile.getName());
			int ret = Application.showConfirmMessageBox(this, msgTitle, msgDesc, JOptionPane.YES_NO_OPTION);
			if (ret != JOptionPane.YES_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 完了
		return true;
	}
	
	/**
	 * [出力ファイル選択] ボタンが押下された後に呼び出されるイベントハンドラ。
	 */
	protected void onClickedChooseFileButton() {
		File selectedFile;
		if (_flgTargetSlipObject) {
			// JSON
			selectedFile = chooseSlipObjectJsonFile(_dstFile);
		}
		else {
			// CSV/XML
			selectedFile = chooseDataObjectCsvXmlFile(_dstFile);
		}
		if (selectedFile == null) {
			// user canceled
			return;
		}
		_dstFile = selectedFile;
		refreshFileInfo();
	}
	
	protected File chooseDataObjectCsvXmlFile(File initialFile) {
		if (_rdoXmlFile.isSelected()) {
			_chooser.setFileFilter(DtContainerFileChooserManager.getInstance().filterXML);
		}
		else {
			_chooser.setFileFilter(DtContainerFileChooserManager.getInstance().filterCSV);
		}
		
		String title = DtContainerEditorMessages.getInstance().ContentExportDlg_title_FileChooser + " [" + _srcContentType.getText() + "]";
		_chooser.setDialogTitle(title);
		
		File lastFile = initialFile;
		if (lastFile == null) {
			lastFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.CONTENT_EXPORT_PREFS);
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
		int ret = _chooser.showSaveDialog(this);
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
			if (curFilter == DtContainerFileChooserManager.getInstance().filterXML) {
				_rdoXmlFile.setSelected(true);
			} else {
				_rdoCsvFile.setSelected(true);
			}
		}
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.CONTENT_EXPORT_PREFS, lastFile);
		return lastFile;
	}
	
	protected File chooseSlipObjectJsonFile(File initialFile) {
		String title = DtContainerEditorMessages.getInstance().ContentExportDlg_title_FileChooser + " [" + _srcContentType.getText() + "]";
		_chooser.setDialogTitle(title);

		File lastFile = initialFile;
		if (lastFile == null) {
			lastFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.CONTENT_EXPORT_PREFS);
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
		int ret = _chooser.showSaveDialog(this);
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
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.CONTENT_EXPORT_PREFS, lastFile);
		return lastFile;
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

	protected void refreshFileInfo() {
		if (_dstFile != null) {
			_dstFileDir.setText(_dstFile.getParentFile().getPath());
			_dstFileName.setText(_dstFile.getName());
		}
		else {
			_dstFileDir.setText(null);
			_dstFileName.setText(null);
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
		// コンテンツ情報
		_srcContentPath = createStaticObjectPathTextComponent();
		_srcContentPath.setText(_ndExportTarget.getContentPathString());
		_srcContentType = createStaticContentTypeTextComponent();
		_srcContentType.setText(_targetDisplayableType==null ? "" : _targetDisplayableType.toString());
		
		// as JSON or CSV/XML
		if (_flgTargetSlipObject) {
			// JSON
			_chooser = DtContainerFileChooserManager.createFileOnlyChooser(true, null, DtContainerFileChooserManager.getInstance().filterJSON);
		}
		else {
			// CSV/XML
			_chooser = DtContainerFileChooserManager.createCsvXmlFileOnlyChooser(true, null);
		}
		
		// ファイル情報
		_dstFileDir = createStaticFileDirTextComponent();
		_dstFileName = createStaticFileNameTextComponent();
		_btnChooseFile = createChooseFileButton();
		
		// ファイル形式
		_rdoCsvFile = new JRadioButton("CSV");
		_rdoXmlFile = new JRadioButton("XML");
		_bgTextTypes = new ButtonGroup();
		_bgTextTypes.add(_rdoCsvFile);
		_bgTextTypes.add(_rdoXmlFile);
		_rdoCsvFile.setSelected(true);
		_cmbCsvEncoding = new JCharsetComboBox();
		_cmbCsvEncoding.setSelectedCharsetName(DtContainerEditorSettings.getInstance().getAadlCsvEncodingName());
	}
	
	private JTextComponent createStaticContentTypeTextComponent() {
		return new StaticTextComponent();
	}
	
	private JTextComponent createStaticObjectPathTextComponent() {
		return new StaticTextComponent();
	}
	
	private JTextComponent createStaticFileDirTextComponent() {
		return new JStaticMultilineTextPane();
	}
	
	private JTextComponent createStaticFileNameTextComponent() {
		return new StaticTextComponent();
	}
	
	private JButton createChooseFileButton() {
		JButton btn = new JButton(DtContainerEditorMessages.getInstance().ContentExportDlg_btn_ChooseFile);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedChooseFileButton();
			}
		});
		return btn;
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
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(createContentInfoPanel(), gbc);
		gbc.gridy++;
		//--- dest file
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(createDestFilePanel(), gbc);
		
		// add main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	private JPanel createContentInfoPanel() {
		// labels
		JLabel lblContentPath = new JLabel(DtContainerEditorMessages.getInstance().ContentExportDlg_lbl_ObjectPath + ": ");
		JLabel lblContentType = new JLabel(DtContainerEditorMessages.getInstance().ContentExportDlg_lbl_ContentType + ": ");
		
		// panel
		JPanel panel = new JPanel(new GridBagLayout());
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		Insets spaceInsets = new Insets(0, 0, 3, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = spaceInsets;
		gbc.gridy = 0;
		//--- content path
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblContentPath, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_srcContentPath, gbc);
		gbc.gridy++;
		//--- content type
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblContentType, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_srcContentType, gbc);
		gbc.gridy++;

		return panel;
	}
	
	private JPanel createDestFilePanel() {
		// labels
		JLabel lblFilePath = new JLabel(DtContainerEditorMessages.getInstance().ContentExportDlg_lbl_FileDir + ": ");
		JLabel lblFileName = new JLabel(DtContainerEditorMessages.getInstance().ContentExportDlg_lbl_FileName + ": ");
		JLabel lblTextType = new JLabel(DtContainerEditorMessages.getInstance().ContentExportDlg_lbl_FileFormat + ": ");
		
		// text type panel
		JComponent pnlTextTypes;
		if (_flgTargetSlipObject) {
			// JSON only
			pnlTextTypes = new JLabel("JSON");
		}
		else {
			// CSV or XML
			Box box = Box.createHorizontalBox();
			box.add(_rdoCsvFile);
			box.add(Box.createHorizontalStrut(5));
			box.add(_cmbCsvEncoding);
			box.add(Box.createHorizontalStrut(10));
			box.add(_rdoXmlFile);
			box.add(Box.createGlue());
			pnlTextTypes = box;
		}
		
		// panel
		JPanel panel = new JPanel(new GridBagLayout());
		Border bd = BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(""),
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
		panel.add(_dstFileDir, gbc);
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
		panel.add(_dstFileName, gbc);
		gbc.gridy++;
		//--- text type
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblTextType, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(pnlTextTypes, gbc);
		gbc.gridy++;
		//--- file chooser button
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_btnChooseFile, gbc);
		gbc.gridy++;
		
		return panel;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
