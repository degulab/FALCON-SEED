/*
 * @(#)AbDtContainerContentImportDialog.java	1.0.0	2022/12/16
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dtalge.DtAlgeSet;
import dtalge.Dtalge;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import dtalge.container.editor.DtContainerEditor;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.DtContainerContentDisplayableType;
import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.DtContainerContentTypesManager;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.container.editor.setting.DtContainerEditorSettings;
import dtalge.container.editor.view.dialog.DtContainerFileChooserManager;
import dtalge.json.DtJSON;
import exalge2.ExAlgeSet;
import exalge2.Exalge;
import net.arnx.jsonic.JSONException;
import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.Files;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.JCharsetComboBox;
import ssac.util.swing.JMaskedNumberSpinner;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.StaticTextComponent;

/**
 * データコンテナ要素をインポートするダイアログの共通実装。
 * 
 * @version 1.0.0
 */
public abstract class AbDtContainerContentImportDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 挿入モードなら {@code true}、変更モードなら {@code false} **/
	private final boolean		_flgInsertMode;
	/** インデックスで管理される編集対象なら {@code true}、名前で管理される編集対象なら {@code false} */
	private final boolean		_flgIndexedChild;
	/** JSON 形式での保存が必要なファイルなら {@code true} */
	private final boolean		_flgTargetSlipObject;
	
	/** file chooser */
	protected JFileChooser	_chooser;
	
	/** ファイルからインポートする選択の場合は {@code true}、空のオブジェクトとする場合は {@code false} **/
	private boolean	_flgImportFromFile = true;	// 初期値
	
	/** 追加変更位置の親ノード **/
	protected DtContainerContentParentTreeNode	_targetParent;
	/** 変更対象のノード **/
	protected IDtContainerContentTreeNode		_replaceTarget;
	/** 追加または変更可能なデータ形式 **/
	protected DtContainerContentDisplayableType[]	_allowTargetTypes;
	/** 追加変更位置を示すインデックス、名前付オフジェクトの場合は (-1) **/
	protected int				_initialInsertIndex;
	/** 同一の親の直下に、すでに存在するすべてのオブジェクト名、このセットが作成されていない場合は {@code null} **/
	protected Set<String>		_existObjectNames;
	/** ファイルから読み込まれたデータオブジェクト **/
	protected Object			_dataObject;
	/** 読み込み対象のファイル **/
	protected File				_srcSelectedFile;

	/** オブジェクトのパス、または挿入先のパス **/
	protected JTextComponent		_dstObjectPath;
	/** コンテンツの名前編集フィールド **/
	protected JTextComponent		_dstObjectName;
	/** コンテンツの挿入インデックス入力コンポーネント **/
	protected JSpinner				_dstInsertIndex;
	/** コンテンツの種類 **/
	private JComboBox<DtContainerContentDisplayableType>	_cmbContentType;
	/** ファイルの親ディレクトリのパス **/
	private JTextComponent		_stcFileDir;
	/** ファイルのパスを含まない名前 **/
	private JTextComponent		_stcFileName;
	/** 空の要素のオブジェクトを利用することを選択するラジオボタン **/
	private JRadioButton		_rdoEmptyObject;
	/** オブジェクトをファイルから読み込むことを選択するラジオボタン **/
	private JRadioButton		_rdoFromFile;
	/** オブジェクトのデータを選択するラジオボタンのグループ **/
	private ButtonGroup			_bgChooseObjectData;
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
	
	/** 読み込みファイルの選択を行うパネル **/
	private JPanel				_pnlFileController;
	/** 無効化対象のファイルパネルのコンポーネント **/
	private JComponent[]		_disableFilePanelComponents;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * インポートダイアログを生成する。
	 * <em>replaceTarget</em> が {@code null} のとき、オブジェクトを新たに追加する挿入モードとなる。
	 * また、<em>replaceTarget</em> が {@code null} ではないとき、既存のオブジェクトを変更するモードとなる。
	 * 挿入モードのときのみ、オブジェクト名を編集できる。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param configId		状態を保存する際のコンフィグレーション ID
	 * @param title			ダイアログのタイトル
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param replaceTarget	変更対象のノード、{@code null} を指定した場合は名前付きオブジェクトの挿入モード
	 */
	public AbDtContainerContentImportDialog(Frame owner, boolean forSlipObject, String configId, String title, DtContainerContentParentTreeNode targetParent, IDtContainerContentTreeNode replaceTarget)
	{
		super(owner, title, true);
		if (targetParent == null)
			throw new NullPointerException("Target parent node is null");
		
		_flgTargetSlipObject = forSlipObject;
		_flgInsertMode = (replaceTarget == null);
		_flgIndexedChild = DtContainerContentTypesManager.getInstance().isIndexedChildren(targetParent.getContentType());
		
		commonConstruction(targetParent, replaceTarget, -1);
		
		setConfiguration(configId, DtContainerEditorSettings.getInstance().getConfiguration());
	}
	
	/**
	 * インポートダイアログを生成する。
	 * <em>replaceTarget</em> が {@code null} のとき、オブジェクトを新たに追加する挿入モードとなる。
	 * また、<em>replaceTarget</em> が {@code null} ではないとき、既存のオブジェクトを変更するモードとなる。
	 * 挿入モードのときのみ、オブジェクト名を編集できる。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param configId		状態を保存する際のコンフィグレーション ID
	 * @param title			ダイアログのタイトル
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param replaceTarget	変更対象のノード、{@code null} を指定した場合は名前付きオブジェクトの挿入モード
	 */
	public AbDtContainerContentImportDialog(Dialog owner, boolean forSlipObject, String configId, String title, DtContainerContentParentTreeNode targetParent, IDtContainerContentTreeNode replaceTarget)
	{
		super(owner, title, true);
		if (targetParent == null)
			throw new NullPointerException("Target parent node is null");
		
		_flgTargetSlipObject = forSlipObject;
		_flgInsertMode = (replaceTarget == null);
		_flgIndexedChild = DtContainerContentTypesManager.getInstance().isIndexedChildren(targetParent.getContentType());
		
		commonConstruction(targetParent, replaceTarget, -1);
		
		setConfiguration(configId, DtContainerEditorSettings.getInstance().getConfiguration());
	}
	
	/**
	 * 挿入モードで、インポートダイアログを生成する。
	 * この場合の親ノードは、リストタイプのもののみとし、<em>insertIndex</em> が初期挿入位置となる。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param configId		状態を保存する際のコンフィグレーション ID
	 * @param title			ダイアログのタイトル
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param insertIndex	初期挿入位置を示すインデックス
	 */
	public AbDtContainerContentImportDialog(Frame owner, boolean forSlipObject, String configId, String title, DtContainerContentParentTreeNode targetParent, int insertIndex)
	{
		super(owner, title, true);
		if (targetParent == null)
			throw new NullPointerException("Target parent node is null");
		
		_flgTargetSlipObject = forSlipObject;
		_flgInsertMode = true;
		if (!DtContainerContentTypesManager.getInstance().isIndexedChildren(targetParent.getContentType()))
			throw new IllegalArgumentException("Target parent is not allow indexed child : (target parent)=" + String.valueOf(targetParent.getContentType()));
		_flgIndexedChild = true;
		
		commonConstruction(targetParent, null, insertIndex);
		
		setConfiguration(configId, DtContainerEditorSettings.getInstance().getConfiguration());
	}
	
	/**
	 * 挿入モードで、インポートダイアログを生成する。
	 * この場合の親ノードは、リストタイプのもののみとし、<em>insertIndex</em> が初期挿入位置となる。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param configId		状態を保存する際のコンフィグレーション ID
	 * @param title			ダイアログのタイトル
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param insertIndex	初期挿入位置を示すインデックス
	 */
	public AbDtContainerContentImportDialog(Dialog owner, boolean forSlipObject, String configId, String title, DtContainerContentParentTreeNode targetParent, int insertIndex)
	{
		super(owner, title, true);
		if (targetParent == null)
			throw new NullPointerException("Target parent node is null");
		
		_flgTargetSlipObject = forSlipObject;
		_flgInsertMode = true;
		if (!DtContainerContentTypesManager.getInstance().isIndexedChildren(targetParent.getContentType()))
			throw new IllegalArgumentException("Target parent is not allow indexed child : (target parent)=" + String.valueOf(targetParent.getContentType()));
		_flgIndexedChild = true;
		
		commonConstruction(targetParent, null, insertIndex);
		
		setConfiguration(configId, DtContainerEditorSettings.getInstance().getConfiguration());
	}
	
	private final void commonConstruction(DtContainerContentParentTreeNode targetParent, IDtContainerContentTreeNode replaceTarget, int insertIndex) {
		_targetParent = targetParent;
		_replaceTarget = replaceTarget;

		if (replaceTarget != null) {
			switch (replaceTarget.getContentType()) {
				case ContentDtSlipNote:
				case ContentDtBinderNote:
					_allowTargetTypes = new DtContainerContentDisplayableType[] { DtContainerContentTypesManager.getInstance().getDisplayableType(DtContainerContentTypes.ContentDtalge) };
					break;
				default:
					_allowTargetTypes = DtContainerContentTypesManager.getInstance().getChildDisplayableTypes(targetParent.getContentType());
			}
		}
		else {
			_allowTargetTypes = DtContainerContentTypesManager.getInstance().getChildDisplayableTypes(targetParent.getContentType());
		}
		
		if (_flgTargetSlipObject) {
			// JSON : allows DtSlip or DtSlipList
			_chooser = DtContainerFileChooserManager.createFileOnlyChooser(true, null, DtContainerFileChooserManager.getInstance().filterJSON);
		}
		else {
			// CSV/XML object
			_chooser = DtContainerFileChooserManager.createCsvXmlFileOnlyChooser(true, null);
		}
		
		if (_flgInsertMode && !_flgIndexedChild) {
			// 名前付きオブジェクトの挿入モード時は、挿入先親ノード直下のすべてのオブジェクト名を収集しておく
			_existObjectNames = new HashSet<>();
			for (int i = 0; i < _targetParent.getChildCount(); ++i) {
				_existObjectNames.add( _targetParent.getChildAt(i).getNodeName() );
			}
		}
		else {
			// 上記条件以外では、オブジェクト名集合は不要
			_existObjectNames = null;
		}
		
		// 初期挿入インデックスの設定
		if (_flgInsertMode) {
			// 挿入モード
			if (_flgIndexedChild) {
				// 子要素はインデックスで配置
				if (insertIndex < 0 || insertIndex > targetParent.getChildCount()) {
					_initialInsertIndex = targetParent.getChildCount();
				} else {
					_initialInsertIndex = insertIndex;
				}
			}
			else {
				// 子要素は名前順に配置
				_initialInsertIndex = (-1);
			}
		}
		else {
			// 変更モード
			_initialInsertIndex = (-1);
		}
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
	
	public boolean isInsertMode() {
		return _flgInsertMode;
	}
	
	public boolean isIndexedChild() {
		return _flgIndexedChild;
	}
	
	public boolean isSlipObject() {
		return _flgTargetSlipObject;
	}
	
	public String getCsvEncoding() {
		return _cmbCsvEncoding.getSelectedCharsetName();
	}
	
	public File getSelectedFile() {
		return _srcSelectedFile;
	}
	
	public Object getDataObject() {
		return _dataObject;
	}
	
	public String getObjectName() {
		return (_dstObjectName==null ? null : _dstObjectName.getText());
	}
	
	public int getInsertIndex() {
		return (_flgIndexedChild && _flgInsertMode && _dstInsertIndex != null ? ((Integer)_dstInsertIndex.getValue()).intValue() : (-1));
	}
	
	public DtContainerContentTypes getSelectedContentType() {
		DtContainerContentDisplayableType selectedType = (DtContainerContentDisplayableType)_cmbContentType.getSelectedItem();
		return (selectedType == null ? null : selectedType.type());
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	/**
	 * 空のオブジェクトかファイルから読み込むかを選択するラジオボタンの選択が変更されたときのみ呼び出されるイベントハンドラ
	 */
	protected void onChangedImportBehaviorRadioButton() {
		_pnlFileController.setEnabled(_flgImportFromFile);
		for (JComponent comp : _disableFilePanelComponents) {
			if (comp != null) {
				comp.setEnabled(_flgImportFromFile);
			}
		}
	}
	
	/**
	 * [OK] ボタンが押下された後に呼び出されるイベントハンドラ。
	 * @return	ダイアログを閉じる場合は {@code true}、それ以外の場合は {@code false}
	 */
	@Override
	protected boolean doOkAction() {
		_dataObject = null;
		
		// オブジェクト名の有無の確認
		if (_flgInsertMode && !_flgIndexedChild) {
			String inputObjectName = getObjectName();
			if (inputObjectName == null || inputObjectName.isEmpty()) {
				DtContainerEditor.showErrorMessage(this, DtContainerEditorMessages.getInstance().ObjectNameValidator_Empty);
				return false;
			}
		}
		
		// ファイルからインポートが選択されている場合の、ファイルの確認
		if (_rdoFromFile.isSelected()) {
			// ファイルが指定されているか？
			if (_srcSelectedFile == null) {
				DtContainerEditor.showErrorMessage(this, DtContainerEditorMessages.getInstance().msgNoDestinationFile);
				return false;
			}

			// ファイルが存在するか？
			if (!_srcSelectedFile.exists() || !_srcSelectedFile.isFile()) {
				DtContainerEditor.showErrorMessage(this, DtContainerEditorMessages.getInstance().msgNoDestinationFile);
				return false;
			}
		}
		
		// オブジェクト名の確認
		if (_flgInsertMode && !_flgIndexedChild && _existObjectNames != null && !_existObjectNames.isEmpty()) {
			if (_existObjectNames.contains(getObjectName())) {
				// すでに存在するオブジェクト名
				int ret = DtContainerEditor.showConfirmMessageBox(this, getTitle(), DtContainerEditorMessages.getInstance().confirmReplaceObjectAlreadyExistName, JOptionPane.OK_CANCEL_OPTION);
				if (ret != JOptionPane.OK_OPTION) {
					// user canceled
					return false;
				}
			}
		}
		
		// ファイルからインポートが選択されている場合の、ファイル読み込み確認
		if (_rdoFromFile.isSelected()) {
			// 読み込み
			Object readObject = validateSourceFile();
			if (readObject == null) {
				// failed
				return false;
			}
			_dataObject = readObject;
		}
		else {
			// 空のオブジェクトを生成
			DtContainerContentTypes targetType = getSelectedContentType();
			switch (targetType) {
				case ContentExalge:
					_dataObject = new Exalge();
					break;
				case ContentExAlgeSet:
					_dataObject = new ExAlgeSet();
					break;
				case ContentDtalge:
					_dataObject = new Dtalge();
					break;
				case ContentDtAlgeSet:
					_dataObject = new DtAlgeSet();
					break;
				case ContentDtSlip:
					_dataObject = new DtSlip();
					break;
				case ContentDtSlipList:
					_dataObject = new DtSlipList();
					break;
				default:
				{
					DtContainerEditor.showErrorMessage(this, DtContainerEditorMessages.getInstance().msgNotSelectedContentType);
					return false;
				}
			}
		}
		
		// 完了
		return true;
	}
	
	/**
	 * [ファイル選択] ボタンが押下された後に呼び出されるイベントハンドラ。
	 */
	protected void onClickedChooseFileButton() {
		File selectedFile;
		if (_flgTargetSlipObject) {
			// JSON
			selectedFile = chooseSlipObjectJsonFile(_srcSelectedFile);
		}
		else {
			// CSV/XML
			selectedFile = chooseDataObjectCsvXmlFile(_srcSelectedFile);
		}
		if (selectedFile == null) {
			// user canceled
			return;
		}
		_srcSelectedFile = selectedFile;
		refreshFileInfo();
	}
	
	protected File chooseDataObjectCsvXmlFile(File initialFile) {
		if (_rdoXmlFile.isSelected()) {
			_chooser.setFileFilter(DtContainerFileChooserManager.getInstance().filterXML);
		}
		else {
			_chooser.setFileFilter(DtContainerFileChooserManager.getInstance().filterCSV);
		}
		
		String title = DtContainerEditorMessages.getInstance().ContentImportDlg_title_FileChooser + " [" + String.valueOf(_cmbContentType.getSelectedItem()) + "]";
		_chooser.setDialogTitle(title);
		
		File lastFile = initialFile;
		if (lastFile == null) {
			lastFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.CONTENT_IMPORT_PREFS);
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
		int ret = _chooser.showOpenDialog(this);
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
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.CONTENT_IMPORT_PREFS, lastFile);
		return lastFile;
	}
	
	protected File chooseSlipObjectJsonFile(File initialFile) {
		String title = DtContainerEditorMessages.getInstance().ContentImportDlg_title_FileChooser + " [" + String.valueOf(_cmbContentType.getSelectedItem()) + "]";
		_chooser.setDialogTitle(title);
		
		File lastFile = initialFile;
		if (lastFile == null) {
			lastFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.CONTENT_IMPORT_PREFS);
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
		int ret = _chooser.showOpenDialog(this);
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
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.CONTENT_IMPORT_PREFS, lastFile);
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
	
	/**
	 * 現在のダイアログの内容で、指定されたファイルの読み込み確認を行う。
	 * @return	読み込みが成功した場合は読み込まれたオブジェクト値、それ以外の場合は {@code null}
	 */
	protected Object validateSourceFile() {
		// 空のオブジェクトを追加するモードかどうかは、このメソッドを呼び出す前に判定
		assert(_rdoFromFile.isSelected());
		
		if (_flgTargetSlipObject) {
			// スリップオブジェクト(JSON)
			return validateAndLoadSourceJsonFile(_srcSelectedFile);
		}
		else if (_rdoXmlFile.isSelected()) {
			// データオブジェクト(XML)
			return validateAndLoadSourceXmlFile(_srcSelectedFile);
		}
		else {
			// データオブジェクト(CSV)
			return validateAndLoadSourceCsvFile(_srcSelectedFile, getCsvEncoding());
		}
	}
	
	/**
	 * 現在のダイアログの内容で、指定されたファイルを JSON 形式のスリップオブジェクトとして、読み込み確認を行う。
	 * @param targetFile	読み込み対象のファイル
	 * @return	読み込みが成功した場合は読み込まれたオブジェクト値、それ以外の場合は {@code null}
	 */
	protected Object validateAndLoadSourceJsonFile(File targetFile) {
		assert(targetFile != null);
		DtContainerContentDisplayableType selectedType = (DtContainerContentDisplayableType)_cmbContentType.getSelectedItem();
		
		try {
			switch (selectedType.type()) {
				case ContentDtSlip:
					return DtJSON.deserialize(targetFile, DtSlip.class);
				case ContentDtSlipList:
					return DtJSON.deserialize(targetFile, DtSlipList.class);
				default:
					throw new RuntimeException("Selected content type is not supported : " + String.valueOf(selectedType));
			}
		}
		catch (JSONException ex) {
			String errmsg = String.format(DtContainerEditorMessages.getInstance().msgUnexpectedContentFormat, selectedType.toString(), "JSON");
			errmsg = DtContainerEditorMessages.formatErrorMessage(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		
		// unexpected format
		return null;
	}
	
	/**
	 * 現在のダイアログの内容で、指定されたファイルを XML 形式のデータオブジェクトとして、読み込み確認を行う。
	 * @param targetFile	読み込み対象のファイル
	 * @return	読み込みが成功した場合は読み込まれたオブジェクト値、それ以外の場合は {@code null}
	 */
	protected Object validateAndLoadSourceXmlFile(File targetFile) {
		assert(targetFile != null);
		DtContainerContentDisplayableType selectedType = (DtContainerContentDisplayableType)_cmbContentType.getSelectedItem();
		
		try {
			switch (selectedType.type()) {
				case ContentExalge:
					return Exalge.fromXML(targetFile);
				case ContentExAlgeSet:
					return ExAlgeSet.fromXML(targetFile);
				case ContentDtalge:
					return Dtalge.fromXML(targetFile);
				case ContentDtAlgeSet:
					return DtAlgeSet.fromXML(targetFile);
				default:
					throw new RuntimeException("Selected content type is not supported : " + String.valueOf(selectedType));
			}
		}
		catch (ParserConfigurationException | SAXException ex) {
			String errmsg = String.format(DtContainerEditorMessages.getInstance().msgUnexpectedContentFormat, selectedType.toString(), "XML");
			errmsg = DtContainerEditorMessages.formatErrorMessage(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		
		// unexpected format
		return null;
	}
	
	/**
	 * 現在のダイアログの内容で、指定されたファイルを CSV 形式のデータオブジェクトとして、読み込み確認を行う。
	 * @param targetFile	読み込み対象のファイル
	 * @param encoding		文字コード名
	 * @return	読み込みが成功した場合は読み込まれたオブジェクト値、それ以外の場合は {@code null}
	 */
	protected Object validateAndLoadSourceCsvFile(File targetFile, String encoding) {
		assert(targetFile != null);
		DtContainerContentDisplayableType selectedType = (DtContainerContentDisplayableType)_cmbContentType.getSelectedItem();
		
		try {
			switch (selectedType.type()) {
				case ContentExalge:
					return Exalge.fromCSV(targetFile, encoding);
				case ContentExAlgeSet:
					return ExAlgeSet.fromCSV(targetFile, encoding);
				case ContentDtalge:
					return Dtalge.fromCSV(targetFile, encoding);
				case ContentDtAlgeSet:
					return DtAlgeSet.fromCSV(targetFile, encoding);
				default:
					throw new RuntimeException("Selected content type is not supported : " + String.valueOf(selectedType));
			}
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex, String.valueOf(encoding));
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (exalge2.io.csv.CsvFormatException | dtalge.exception.CsvFormatException ex) {
			String errmsg = String.format(DtContainerEditorMessages.getInstance().msgUnexpectedContentFormat, selectedType.toString(), "CSV");
			errmsg = DtContainerEditorMessages.formatErrorMessage(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		
		// unexpected format
		return null;
	}

	protected void refreshFileInfo() {
		if (_srcSelectedFile != null) {
			_stcFileDir.setText(_srcSelectedFile.getParentFile().getPath());
			_stcFileName.setText(_srcSelectedFile.getName());
		}
		else {
			_stcFileDir.setText(null);
			_stcFileName.setText(null);
		}
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
		_dstObjectPath = createStaticObjectPathTextComponent();
		_cmbContentType = createContentTypeComboBox();
		if (_flgInsertMode) {
			// 挿入モード
			if (_flgIndexedChild) {
				_dstInsertIndex = createEditableObjectIndexSpinner();
			} else {
				_dstObjectName = createEditableObjectNameTextField();
			}
		}
		
		// ファイル情報
		_stcFileDir = createStaticFileDirTextComponent();
		_stcFileName = createStaticFileNameTextComponent();
		_btnChooseFile = createChooseFileButton();
		
		// オブジェクトの内容選択
		_rdoEmptyObject = new JRadioButton(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_ObjectEmpty);
		_rdoFromFile = new JRadioButton(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_ObjectFromFile);
		_bgChooseObjectData = new ButtonGroup();
		_bgChooseObjectData.add(_rdoEmptyObject);
		_bgChooseObjectData.add(_rdoFromFile);
		if (_flgImportFromFile)
			_rdoFromFile.setSelected(true);
		else
			_rdoEmptyObject.setSelected(true);
		
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
	
	private JComboBox<DtContainerContentDisplayableType> createContentTypeComboBox() {
		JComboBox<DtContainerContentDisplayableType> cmb = new JComboBox<>(_allowTargetTypes);
		if (_replaceTarget != null) {
			DtContainerContentDisplayableType replaceType = DtContainerContentTypesManager.getInstance().getDisplayableType(_replaceTarget.getContentType());
			cmb.setSelectedItem(replaceType);
		}
		else {
			cmb.setSelectedIndex(0);
		}
		return cmb;
	}
	
	private JTextComponent createStaticObjectPathTextComponent() {
		return new StaticTextComponent();
	}
	
	private JTextComponent createEditableObjectNameTextField() {
		return new JTextField();
	}
	
	private JSpinner createEditableObjectIndexSpinner() {
		JMaskedNumberSpinner spin = new JMaskedNumberSpinner("#0", _initialInsertIndex, 0, _targetParent.getChildCount(), 1);
		Dimension dim = spin.getPreferredSize();
		if (dim.width < 80) {
			dim.width = 80;
		}
		spin.setPreferredSize(dim);
		spin.setMinimumSize(dim);
		return spin;
	}
	
	private JTextComponent createStaticFileDirTextComponent() {
		return new JStaticMultilineTextPane();
	}
	
	private JTextComponent createStaticFileNameTextComponent() {
		return new StaticTextComponent();
	}
	
	private JButton createChooseFileButton() {
		JButton btn = new JButton(DtContainerEditorMessages.getInstance().ContentImportDlg_btn_ChooseFile);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedChooseFileButton();
			}
		});
		return btn;
	}

	/**
	 * ダイアログのアクションをセットアップする。
	 */
	@Override
	protected void setupActions() {
		super.setupActions();
		
		ChangeListener rdoImportBehaviorListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (_bgChooseObjectData.getSelection() == _rdoEmptyObject.getModel()) {
					// empty object
					if (_flgImportFromFile) {
						_flgImportFromFile = false;
						onChangedImportBehaviorRadioButton();
					}
				}
				else {
					// from file
					if (!_flgImportFromFile) {
						_flgImportFromFile = true;
						onChangedImportBehaviorRadioButton();
					}
				}
			}
		};
		_rdoEmptyObject.addChangeListener(rdoImportBehaviorListener);
		_rdoFromFile.addChangeListener(rdoImportBehaviorListener);
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
		
		// create source file panel
		_pnlFileController = createFilePanel();
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		Insets spaceInsets = new Insets(0, 0, 3, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = spaceInsets;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- content info
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(createContentInfoPanel(), gbc);
		gbc.gridy++;
		//--- data import type
		mainPanel.add(_rdoEmptyObject, gbc);
		gbc.gridy++;
		mainPanel.add(_rdoFromFile, gbc);
		gbc.gridy++;
		//--- source file
		gbc.insets = new Insets(0, 20, 3, 0);
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(_pnlFileController, gbc);
		
		// add main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	private JPanel createContentInfoPanel() {
		// labels
		JLabel lblObjectPath, lblObjectName;
		JComponent cObjectName;
		if (_flgInsertMode) {
			// 挿入モード
			_dstObjectPath.setText(_targetParent.getContentPathString());
			lblObjectPath = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_ParentPath + ": ");
			if (_flgIndexedChild) {
				// indexed child
				lblObjectName = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_InsertPosition + ": ");
				cObjectName = _dstInsertIndex;
			} else {
				// named child
				lblObjectName = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_ObjectName + ": ");
				cObjectName = _dstObjectName;
			}
		}
		else {
			// 変更モード
			_dstObjectPath.setText(_replaceTarget.getContentPathString());
			lblObjectPath = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_ObjectPath + ": ");
			lblObjectName = null;
			cObjectName = null;
		}
		JLabel lblContentType = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_ContentType + ": ");
		
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
		panel.add(lblObjectPath, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(_dstObjectPath, gbc);
		gbc.gridy++;
		//--- content name
		if (cObjectName != null) {
			gbc.gridx = 0;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.EAST;
			panel.add(lblObjectName, gbc);
			gbc.gridx++;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			panel.add(cObjectName, gbc);
			gbc.gridy++;
		}
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
		panel.add(_cmbContentType, gbc);
		gbc.gridy++;

		return panel;
	}
	
	private JPanel createFilePanel() {
		// labels
		JLabel lblFilePath = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_FileDir + ": ");
		JLabel lblFileName = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_FileName + ": ");
		JLabel lblTextType = new JLabel(DtContainerEditorMessages.getInstance().ContentImportDlg_lbl_FileFormat + ": ");
		
		// text type panel
		JComponent pnlTextTypes;
		if (_flgTargetSlipObject) {
			// JSON only
			pnlTextTypes = new JLabel("JSON");
			_disableFilePanelComponents = new JComponent[] {
					_stcFileDir,
					_stcFileName,
					lblFilePath,
					lblFileName,
					lblTextType,
					pnlTextTypes,
					_btnChooseFile,
			};
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
			_disableFilePanelComponents = new JComponent[] {
					_stcFileDir,
					_stcFileName,
					lblFilePath,
					lblFileName,
					lblTextType,
					_rdoCsvFile,
					_cmbCsvEncoding,
					_rdoXmlFile,
					_btnChooseFile,
			};
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
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.insets = filedirLabelInsets;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		panel.add(lblFilePath, gbc);
		gbc.gridx++;
		gbc.insets = spaceInsets;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(_stcFileDir, gbc);
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
		panel.add(_stcFileName, gbc);
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
