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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EtcDestEditDialog.java	3.3.0	2016/05/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.excel2csv.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.falconseed.runner.view.dialog.MessageDetailDialog;
import ssac.util.excel2csv.EtcConfigDataSet;
import ssac.util.io.VirtualFile;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.JMultilineLabel;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.JTriStateCheckBox;
import ssac.util.swing.JTriStateCheckBox.Status;

/**
 * <code>[Excel to CSV]</code> 変換定義の出力先設定ダイアログ。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcDestEditDialog extends AbBasicDialog implements EtcDestSetChangeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(640, 480);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 表示用パス文字列フォーマッター */
	private VirtualFilePathFormatterList	_vfFormatter;
	/** データフォルダのユーザールートディレクトリ **/
	private VirtualFile						_vfDataUserRoot;
	/** Excel ファイルの抽象パス **/
	private VirtualFile						_vfExcelFile;

	/** データモデル **/
	private EtcConfigDataSet				_datamodel;
	/** 各 CSV 出力設定用コンポーネント **/
	private EtcDestSetEditPane				_editPane;

	/** 処理対象の Excel ファイルパスを表示するコンポーネント **/
	private JStaticMultilineTextPane		_stcExcelFilePath;
	/** 基準出力ディレクトリを表示するコンポーネント **/
	private JStaticMultilineTextPane		_stcBaseOutputDir;
	/** 基準出力ディレクトリを選択するボタン **/
	private JButton							_btnChooseBaseOutDir;
	/** 全出力設定のテンポラリ出力を変更するためのチェックボックス **/
	private JTriStateCheckBox				_chkAllOutToTemp;
	/** 全出力設定の結果閲覧を変更するためのチェックボックス **/
	private JTriStateCheckBox				_chkAllShowDest;
	/** 全出力設定のテンポラリ出力のユーザー選択状態、チェックボックスで状態を変更した時のみ更新される **/
	private boolean							_flgLastSelectedAllOutToTemp;
	/** 全出力設定の結果閲覧のユーザー選択状態、チェックボックスで状態を変更した時のみ更新される **/
	private boolean							_flgLastSelectedAllShowDest;
	/** 基準出力ディレクトリ **/
	private VirtualFile						_vfBaseOutDir;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EtcDestEditDialog(Frame owner, EtcConfigDataSet dataset, File excelfile) {
		super(owner, RunnerMessages.getInstance().Excel2csv_DestEditDlg_title + " - " + excelfile.getName(), true);
		setConfiguration(AppSettings.Excel2CSV_DESTEDIT_DLG, AppSettings.getInstance().getConfiguration());
		if (dataset == null)
			throw new NullPointerException();
		_datamodel = dataset;
		RunnerFrame mainframe = (RunnerFrame)ModuleRunner.getApplicationMainFrame();
		_vfDataUserRoot = mainframe.getDataFileUserRootDirectory();
		_vfFormatter = mainframe.getDataFilePathFormatter();
		_vfExcelFile = ModuleFileManager.fromJavaFile(excelfile);
	}
	
	public EtcDestEditDialog(Dialog owner, EtcConfigDataSet dataset, File excelfile) {
		super(owner, RunnerMessages.getInstance().Excel2csv_DestEditDlg_title + " - " + excelfile.getName(), true);
		setConfiguration(AppSettings.Excel2CSV_DESTEDIT_DLG, AppSettings.getInstance().getConfiguration());
		if (dataset == null)
			throw new NullPointerException();
		_datamodel = dataset;
		RunnerFrame mainframe = (RunnerFrame)ModuleRunner.getApplicationMainFrame();
		_vfDataUserRoot = mainframe.getDataFileUserRootDirectory();
		_vfFormatter = mainframe.getDataFilePathFormatter();
		_vfExcelFile = ModuleFileManager.fromJavaFile(excelfile);
	}
	
	@Override
	public void initialComponent() {
		// コンポーネントの初期化
		super.initialComponent();
		
		// Excel ファイルの表示
		_stcExcelFilePath.setText(_editPane.formatDestPath(_vfExcelFile));
		
		// データモデルを設定
		_editPane.setModel(_datamodel);
		
		// 設定情報の反映
		restoreConfiguration();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public EtcConfigDataSet getDataModel() {
		return _datamodel;
	}

	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
		
		// base output directory
		File fOutDir = AppSettings.getInstance().getLastFile(AppSettings.Excel2CSV_BASE_OUTPUTDIR);
		if (fOutDir == null) {
			// user data root directory
			_vfBaseOutDir = (_vfDataUserRoot==null ? _vfExcelFile.getParentFile() : _vfDataUserRoot);
		} else {
			// user selected directory
			_vfBaseOutDir = ModuleFileManager.fromJavaFile(fOutDir);
		}
		refreshBaseOutDir();
		
		// last selection flag
		_flgLastSelectedAllOutToTemp = AppSettings.getInstance().getConfiguration().getBooleanValue(AppSettings.Excel2CSV_DESTEDIT_OUT2TEMP);
		_flgLastSelectedAllShowDest  = AppSettings.getInstance().getConfiguration().getBooleanValue(AppSettings.Excel2CSV_DESTEDIT_SHOWDEST);
		_chkAllOutToTemp.setSelected(_flgLastSelectedAllOutToTemp);
		_chkAllShowDest .setSelected(_flgLastSelectedAllShowDest);
		
		// restore された設定情報から、出力設定の初期状態を構築する。
		for (int index = 0; index < _datamodel.size(); ++index) {
			EtcDestItemEditPane itemPane = _editPane.getItemPane(index);
			itemPane.setShowDestEnabled(_flgLastSelectedAllShowDest, false);
			itemPane.setOutToTempEnabled(_flgLastSelectedAllOutToTemp, false);
			itemPane.changeBaseOutputDirectory(_vfBaseOutDir, null);
			itemPane.setOutputTemporaryPrefix(itemPane.getItemData().getDefaultFilename(), false);
		}
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
		
		// base output directory
		if (_vfBaseOutDir == null || _vfBaseOutDir.equals(_vfDataUserRoot)) {
			AppSettings.getInstance().setLastFile(AppSettings.Excel2CSV_BASE_OUTPUTDIR, null);
		} else {
			AppSettings.getInstance().setLastFile(AppSettings.Excel2CSV_BASE_OUTPUTDIR, ModuleFileManager.toJavaFile(_vfBaseOutDir));
		}
		
		// last selection flag
		AppSettings.getInstance().getConfiguration().setBoolean(AppSettings.Excel2CSV_DESTEDIT_OUT2TEMP, _flgLastSelectedAllOutToTemp);
		AppSettings.getInstance().getConfiguration().setBoolean(AppSettings.Excel2CSV_DESTEDIT_SHOWDEST, _flgLastSelectedAllShowDest);
	}

	/**
	 * ダイアログが表示されている場合はダイアログを閉じ、リソースを開放する。
	 * このメソッドでは、{@link java.awt.Window#dispose()} を呼び出す。
	 */
	public void destroy() {
		if (this.isDisplayable()) {
			dialogClose(DialogResult_Cancel);
		}
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------

	/**
	 * テンポラリ出力の 3 ステートチェックボックスの次の状態が要求されたときに呼び出されるイベントハンドラ。
	 * @param currentStatus	現在のステート
	 * @return	新しいステート
	 */
	protected JTriStateCheckBox.Status onNextSelectAllOutToTemp(JTriStateCheckBox.Status currentStatus) {
		if (JTriStateCheckBox.Status.SELECTED == currentStatus) {
			// selected -> deselected
			_flgLastSelectedAllOutToTemp = false;
			_editPane.selectAllOutToTemp(_flgLastSelectedAllOutToTemp);
			return JTriStateCheckBox.Status.DESELECTED;
		}
		else if (JTriStateCheckBox.Status.DESELECTED == currentStatus) {
			// deselected -> selected
			_flgLastSelectedAllOutToTemp = true;
			_editPane.selectAllOutToTemp(_flgLastSelectedAllOutToTemp);
			return JTriStateCheckBox.Status.SELECTED;
		}
		else {
			// indeterminate -> selected
			_flgLastSelectedAllOutToTemp = true;
			_editPane.selectAllOutToTemp(_flgLastSelectedAllOutToTemp);
			return JTriStateCheckBox.Status.SELECTED;
		}
	}

	/**
	 * 結果を閲覧の 3 ステートチェックボックスの次の状態が要求されたときに呼び出されるイベントハンドラ。
	 * @param currentStatus	現在のステート
	 * @return	新しいステート
	 */
	protected JTriStateCheckBox.Status onNextSelectAllShowDest(JTriStateCheckBox.Status currentStatus) {
		if (JTriStateCheckBox.Status.SELECTED == currentStatus) {
			// selected -> deselected
			_flgLastSelectedAllShowDest = false;
			_editPane.selectAllShowDest(_flgLastSelectedAllShowDest);
			return JTriStateCheckBox.Status.DESELECTED;
		}
		else if (JTriStateCheckBox.Status.DESELECTED == currentStatus) {
			// deselected -> selected
			_flgLastSelectedAllShowDest = true;
			_editPane.selectAllShowDest(_flgLastSelectedAllShowDest);
			return JTriStateCheckBox.Status.SELECTED;
		}
		else {
			// indeterminate -> selected
			_flgLastSelectedAllShowDest = true;
			_editPane.selectAllShowDest(_flgLastSelectedAllShowDest);
			return JTriStateCheckBox.Status.SELECTED;
		}
	}

	/**
	 * 変換出力設定のどれか一つで、テンポラリ出力設定が変更されたときに呼び出されるイベントハンドラ。
	 * @param editPane	変換出力設定パネル
	 */
	@Override
	public void editOutToTempChanged(EtcDestSetEditPane editPane) {
		if (editPane.isOutToTempSelectedAll()) {
			// すべての要素でテンポラリ出力が選択されている
			_chkAllOutToTemp.setState(JTriStateCheckBox.Status.SELECTED);
		}
		else if (editPane.isOutToTempSelectionEmpty()) {
			// すべての要素でテンポラリ出力が選択解除されている
			_chkAllOutToTemp.setState(JTriStateCheckBox.Status.DESELECTED);
		}
		else {
			// 混在
			_chkAllOutToTemp.setState(JTriStateCheckBox.Status.INDETERMINATE);
		}
	}

	/**
	 * 変換出力設定のどれか一つで、結果閲覧設定が変更されたときに呼び出されるイベントハンドラ。
	 * @param editPane	変換出力設定パネル
	 */
	@Override
	public void editShowDestChanged(EtcDestSetEditPane editPane) {
		if (editPane.isShowDestSelectedAll()) {
			// すべての要素で結果閲覧が選択されている
			_chkAllShowDest.setState(JTriStateCheckBox.Status.SELECTED);
		}
		else if (editPane.isShowDestSelectionEmpty()) {
			// すべての要素で結果閲覧が選択解除されている
			_chkAllShowDest.setState(JTriStateCheckBox.Status.DESELECTED);
		}
		else {
			// 混在
			_chkAllShowDest.setState(JTriStateCheckBox.Status.INDETERMINATE);
		}
	}

	/**
	 * 基準出力ディレクトリを選択するボタンが押下されたときに呼び出されるイベントハンドラ。
	 */
	protected void onButtonChooseBaseOutDir() {
		File curOutDir;
		if (_vfBaseOutDir == null)
			curOutDir = ModuleFileManager.toJavaFile(_vfDataUserRoot);
		else
			curOutDir = ModuleFileManager.toJavaFile(_vfBaseOutDir);
		File dirPath = FileChooserManager.chooseSaveDirectory(this, curOutDir,
													CommonMessages.getInstance().Choose_folder,
													CommonMessages.getInstance().Button_OK);
		if (dirPath == null) {
			// user canceled
			return;
		}
		
		// 出力ディレクトリの変更チェック
		VirtualFile newBaseOutDir = ModuleFileManager.fromJavaFile(dirPath);
		ArrayList<EtcDestItemEditPane> unchangableItemPanes = new ArrayList<EtcDestItemEditPane>(_editPane.getNumItems());
		for (int index = 0; index < _editPane.getNumItems(); ++index) {
			if (!_editPane.getItemPane(index).canChangeOutputDirectory(newBaseOutDir, _vfBaseOutDir)) {
				unchangableItemPanes.add(_editPane.getItemPane(index));
			}
		}
		if (!unchangableItemPanes.isEmpty()) {
			// 親ディレクトリを変更不可能なものが混ざっているので、強制的に変更、一部変更、キャンセルかを問い合わせる
			StringBuilder sb = new StringBuilder();
			for (EtcDestItemEditPane itemPane : unchangableItemPanes) {
				sb.append(itemPane.getDestNoLabelText());
				sb.append(' ');
				sb.append(_editPane.formatDestPath(itemPane.getDestinationFile()));
				sb.append("\n");
			}
			int ret = MessageDetailDialog.showConfirmDetailMessage(this,
						CommonMessages.getInstance().msgboxTitleConfirm,
						RunnerMessages.getInstance().Excel2csv_confirmChangeAllOutDir,
						sb.toString(), MessageDetailDialog.YES_NO_CANCEL_OPTION);
			if (ret == MessageDetailDialog.YES_OPTION) {
				// すべて変更
				for (int index = 0; index < _editPane.getNumItems(); ++index) {
					// 強制的にすべて変更
					_editPane.getItemPane(index).changeBaseOutputDirectory(newBaseOutDir, null);
				}
			}
			else if (ret == MessageDetailDialog.NO_OPTION) {
				// 可能なもののみ変更
				for (int index = 0; index < _editPane.getNumItems(); ++index) {
					_editPane.getItemPane(index).changeBaseOutputDirectory(newBaseOutDir, _vfBaseOutDir);
				}
			}
			else {
				// キャンセル
				return;
			}
		}
		else {
			// すべて変更
			for (int index = 0; index < _editPane.getNumItems(); ++index) {
				// 強制的にすべて変更
				_editPane.getItemPane(index).changeBaseOutputDirectory(newBaseOutDir, null);
			}
		}
		
		// 変更の反映
		_vfBaseOutDir = newBaseOutDir;
		refreshBaseOutDir();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void refreshBaseOutDir() {
		_stcBaseOutputDir.setText(_editPane.formatDestPath(_vfBaseOutDir));
	}
	
	protected EtcDestSetEditPane getEditPane() {
		if (_editPane == null) {
			_editPane = createEditPane();
		}
		return _editPane;
	}
	
	protected EtcDestSetEditPane createEditPane() {
		EtcDestSetEditPane pane = new EtcDestSetEditPane(_vfFormatter, this);
		return pane;
	}
	
	protected JTextComponent createDescriptionLabel() {
		JMultilineLabel label = new JMultilineLabel();
		return label;
	}
	
	protected JPanel createCommonConfigPanel() {
		// create local components
		_stcExcelFilePath = new JStaticMultilineTextPane();
		_stcExcelFilePath.setWrapStyleWord(false);
		_stcBaseOutputDir = new JStaticMultilineTextPane();
		_stcBaseOutputDir.setWrapStyleWord(false);
		_btnChooseBaseOutDir = CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		
		// create panel
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx    = 0;
		gbc.weighty    = 0;
		gbc.gridx      = 0;
		gbc.gridy      = 0;
		
		// layout
		//--- Excel ファイル
		gbc.gridwidth = 1;
		gbc.weightx   = 0;
		gbc.fill      = GridBagConstraints.NONE;
		gbc.anchor    = GridBagConstraints.EAST;
		pnl.add(new JLabel(RunnerMessages.getInstance().Excel2csv_DestEditDlg_lbl_excelfile + " "), gbc);
		gbc.gridx++;
		gbc.gridwidth = 2;
		gbc.weightx   = 1;
		gbc.fill      = GridBagConstraints.HORIZONTAL;
		gbc.anchor    = GridBagConstraints.NORTHWEST;
		pnl.add(_stcExcelFilePath, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		//--- 基準出力フォルダ
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.gridwidth = 1;
		gbc.weightx   = 0;
		gbc.fill      = GridBagConstraints.NONE;
		gbc.anchor    = GridBagConstraints.EAST;
		pnl.add(new JLabel(RunnerMessages.getInstance().Excel2csv_DestEditDlg_lbl_baseoutdir + " "), gbc);
		gbc.gridx++;
		gbc.weightx   = 1;
		gbc.fill      = GridBagConstraints.HORIZONTAL;
		gbc.anchor    = GridBagConstraints.NORTHWEST;
		pnl.add(_stcBaseOutputDir, gbc);
		gbc.gridx++;
		gbc.weightx   = 0;
		gbc.fill      = GridBagConstraints.NONE;
		pnl.add(_btnChooseBaseOutDir, gbc);
		
		return pnl;
	}
	
	protected JPanel createMainPanel() {
		// create local components
		JPanel pnlCommonConfig = createCommonConfigPanel();
		JScrollPane scItems = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scItems.setViewportView(getEditPane());

		// main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(pnlCommonConfig, BorderLayout.NORTH);
		mainPanel.add(scItems, BorderLayout.CENTER);

		return mainPanel;
	}

	@Override
	protected void setupMainContents() {
		JPanel mainPanel = createMainPanel();
		
		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
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
		
		this.setResizable(true);
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		_chkAllOutToTemp.setHandler(new JTriStateCheckBox.TriStateHandler() {
			@Override
			public Status nextState(JTriStateCheckBox.Status currentState) {
				return onNextSelectAllOutToTemp(currentState);
			}
		});
		
		_chkAllShowDest.setHandler(new JTriStateCheckBox.TriStateHandler() {
			@Override
			public Status nextState(JTriStateCheckBox.Status currentState) {
				return onNextSelectAllShowDest(currentState);
			}
		});
		
		_btnChooseBaseOutDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonChooseBaseOutDir();
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	protected boolean doCancelAction() {
		return super.doCancelAction();
	}

	@Override
	protected boolean doOkAction() {
		// 値のチェック
		if (!getEditPane().verifyItemValues()) {
			String errmsg = getEditPane().getFirstItemError();
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// 出力ファイルの重複チェック
		ArrayList<EtcDestItemEditPane> existDestFiles = new ArrayList<EtcDestItemEditPane>();
		ArrayList<EtcDestItemEditPane> dupDestFiles   = new ArrayList<EtcDestItemEditPane>();
		{
			HashSet<VirtualFile> destfileset = new HashSet<VirtualFile>();
			for (int index = 0; index < getEditPane().getNumItems(); ++index) {
				EtcDestItemEditPane itemPane = getEditPane().getItemPane(index);
				if (!itemPane.getOutToTempEnabled()) {
					VirtualFile vfDestFile = itemPane.getDestinationFile();
					//--- 重複チェック
					if (destfileset.contains(vfDestFile)) {
						//--- 重複している
						dupDestFiles.add(itemPane);
					} else {
						//--- 重複なし
						destfileset.add(vfDestFile);
					}
					//--- 上書きチェック
					if (vfDestFile.exists()) {
						//--- ファイルがすでに存在している
						existDestFiles.add(itemPane);
					}
				}
			}
		}
		if (!dupDestFiles.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (EtcDestItemEditPane itemPane : dupDestFiles) {
				sb.append(itemPane.getDestNoLabelText());
				sb.append(' ');
				sb.append(itemPane.getDestinationFile().getName());
				sb.append("\n");
			}
			int ret = MessageDetailDialog.showConfirmDetailMessage(this,
						CommonMessages.getInstance().msgboxTitleConfirm,
						RunnerMessages.getInstance().Excel2csv_confirmDestFilesMultipled,
						sb.toString(), MessageDetailDialog.OK_CANCEL_OPTION);
			if (ret != MessageDetailDialog.OK_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 出力ファイルの上書きチェック
		if (!existDestFiles.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (EtcDestItemEditPane itemPane : existDestFiles) {
				sb.append(itemPane.getDestNoLabelText());
				sb.append(' ');
				sb.append(itemPane.getDestinationFile().getName());
				sb.append("\n");
			}
			int ret = MessageDetailDialog.showConfirmDetailMessage(this,
						CommonMessages.getInstance().msgboxTitleConfirm,
						RunnerMessages.getInstance().Excel2csv_confirmDestFilesOverwrite,
						sb.toString(), MessageDetailDialog.OK_CANCEL_OPTION);
			if (ret != MessageDetailDialog.OK_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 基本クラスの処理(ローカルファイルを閉じる)
		if (!super.doOkAction()) {
			return false;
		}
		
		// 設定を反映
		return true;
	}

	/**
	 * ダイアログ下部のボタンパネルを生成する。
	 * これは編集時専用のパネルであり、コンソールの表示設定も含む。
	 * @since 3.0.0
	 */
	@Override
	protected JComponent createButtonsPanel() {
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		// create local components
		_chkAllOutToTemp = new JTriStateCheckBox(RunnerMessages.getInstance().MExecDefEditDlg_Arg_OutToTemp);
		_chkAllShowDest  = new JTriStateCheckBox(RunnerMessages.getInstance().MExecDefEditDlg_Arg_ShowResultCsv);
		_chkAllOutToTemp.setSelected(AppSettings.getInstance().getConfiguration().getBooleanValue(AppSettings.Excel2CSV_DESTEDIT_OUT2TEMP));
		_chkAllShowDest .setSelected(AppSettings.getInstance().getConfiguration().getBooleanValue(AppSettings.Excel2CSV_DESTEDIT_SHOWDEST));
		_chkAllOutToTemp.setMargin(new Insets(0,0,0,0));
		_chkAllShowDest .setMargin(new Insets(0,0,0,0));
		
		// Layout
		Box btnBox = Box.createHorizontalBox();
		Box chkBox = Box.createVerticalBox();
		chkBox.add(_chkAllShowDest);
		chkBox.add(Box.createVerticalGlue());
		chkBox.add(_chkAllOutToTemp);
		btnBox.add(chkBox);
		//---
		btnBox.add(Box.createHorizontalGlue());
		for (JButton btn : buttons) {
			btnBox.add(btn);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();

		// scroll to top item
		getEditPane().scrollTopToVisible();
		
		// verify
		getEditPane().verifyItemValues();
		
		// refresh tree context menu
//		updateTreeContextMenu();
	}

	@Override
	protected void onWindowClosed(WindowEvent e) {
//		_handler.onClosedDialog(this);
	}
	
	@Override
	protected void onShown(ComponentEvent e) {
//		_handler.onShownDialog(this);
	}

	@Override
	protected void onHidden(ComponentEvent e) {
//		_handler.onHiddenDialog(this);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
