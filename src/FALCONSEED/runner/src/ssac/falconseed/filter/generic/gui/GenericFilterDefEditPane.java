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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)GenericFilterDefEditPane.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericFilterDefEditPane.java	3.2.0	2015/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.filter.common.gui.FilterEditModel;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterDefArgsTablePane;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.falconseed.module.swing.table.IMExecDefArgTableModel;
import ssac.falconseed.module.swing.table.IModuleArgConfigTableModel;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.Application;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.MenuToggleButton;

/**
 * 汎用フィルタ専用のフィルタ定義編集パネル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterDefEditPane extends JPanel implements ComponentListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final FilenameValidator _validator = FileDialogManager.createDefaultFilenameValidator();

	/** データモデル **/
	private GenericFilterEditModel		_editModel;
	
	/** 親フォルダの抽象パス **/
	private VirtualFile			_vfParent;

	/** 実行対象のモジュールファイル **/
	private JStaticMultilineTextPane	_stcModuleFile;
	/** 実行対象モジュールのメインクラス名 **/
	private JStaticMultilineTextPane	_stcMainClass;

	/** 名称編集用テキストフィールド **/
	private JTextField					_cNameField;
	/** 名称表示用テキストコンポーネント **/
	private JStaticMultilineTextPane	_cNameLabel;
	/** 格納位置のパス **/
	private JStaticMultilineTextPane	_stcLocation;
	/** 引数定義テーブル **/
	private GenericFilterDefArgsTablePane	_argTable;
	/** 機能説明のテキストエリア **/
	private JTextArea					_taDesc;
	/** モジュール表示パネル **/
	private JPanel						_paneModule;

	/** 位置選択ボタン **/
	private JButton			_btnChooseLocation;
	/** 文字列引数の追加ボタン **/
	private JButton			_btnArgAdd;
	/** 引数の編集ボタン **/
	private MenuToggleButton	_btnArgEdit;
	/** 引数の削除ボタン **/
	private JButton			_btnArgDel;
	/** 引数の上へ移動ボタン **/
	private JButton			_btnArgUp;
	/** 引数の下へ移動ボタン **/
	private JButton			_btnArgDown;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericFilterDefEditPane(final GenericFilterEditModel editModel) {
		super(new GridBagLayout());
		this._editModel = editModel;
		
		createComponents();
		layoutComponents();
		
		// 引数定義の相対パス表示のための基準パスを設定
		_argTable.setPathFormatter(_editModel.getPathFormatter());
		_argTable.setBasePath(_editModel.getAvailableFilterRootDirectory());
		_argTable.setEditModel(_editModel);
		
		// 固定モジュール情報の設定
		redisplayModuleInfo();
		
		// 定義情報の表示
		_vfParent = _editModel.getFilterParentDirectory();
		setMExecDefDescription(_editModel.getMExecDefDescription());
		setLocationPath(_vfParent);
		setMExecDefName(_editModel.getFilterName());
		
		// 参照モード対応
		if (!_editModel.isEditing()) {
			_btnArgAdd.setVisible(false);
			_btnArgAdd.setEnabled(false);
		}
		
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在設定されているフィルタ定義が配置されるディレクトリを返す。
	 * @return フィルタの親ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefParentDirectory() {
		return _vfParent;
	}
	
	/**
	 * 現在コンポーネントに設定されているフィルタ定義の場所とフィルタ名から、
	 * モジュール実行定義ディレクトリを返す。モジュール実行定義ディレクトリが
	 * 配置される親ディレクトリやフィルタ名が未定の場合は <tt>null</tt> を返す。
	 * @return	フィルタのルートディレクトリを示す抽象パスを返す。
	 * 			ルートディレクトリが未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefDirectory() {
		VirtualFile vfDir = null;
		String name = getMExecDefName();
		if (_vfParent != null && name != null && name.length() > 0) {
			vfDir = _vfParent.getChildFile(name);
		}
		return vfDir;
	}

	/**
	 * 現在のフィルタ名を返す。未定義の場合は <tt>null</tt> を返す。
	 * @return	現在のフィルタ名
	 */
	
	public String getMExecDefName() {
		return getNameTextComponent().getText();
	}

	/**
	 * 現在のフィルタ説明を返す。
	 * @return	現在のフィルタ機能説明
	 */
	public String getMExecDefDescription() {
		return _taDesc.getText();
	}
	
	public void adjustArgTableAllColumnsPreferredWidth() {
		_argTable.adjustTableAllColumnsPreferredWidth();
	}
	
	public void updateStatus() {
		_argTable.updateButtons();
	}
	
	public IModuleArgConfigTableModel getTableModel() {
		return _editModel.getMExecDefArgTableModel();
	}

	/**
	 * モジュール実行定義名の正当性をチェックする。
	 * @return	モジュール実行定義名で新しいディレクトリが作成可能な場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean checkFilename() {
		// validator に、存在するファイル名を設定
		_validator.setAlreadyExistsFilenames(_vfParent);
		
		// verify
		boolean ret = _validator.verify(_cNameField, _cNameField.getText());
		if (!ret) {
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					_cNameField.requestFocusInWindow();
					_cNameField.selectAll();
				}
			});
		}
		return ret;
	}

	/**
	 * 実行時引数設定の正当性をチェックする。
	 * @return	実行時引数設定が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean checkArguments() {
		IMExecDefArgTableModel model = _argTable.getTableModel();
		int rowCount = model.getRowCount();
		if (rowCount <= 0) {
			// 引数設定は存在しないので、正しいとする
			return true;
		}
		
		// 引数の型が指定されているかをチェックする
		for (int row = 0 ; row < rowCount; row++) {
			ModuleArgType type = model.getArgumentAttr(row);
			if (type == null || ModuleArgType.NONE == type) {
				String errmsg = MacroFilterEditModel.formatArgNo(row) + " " + RunnerMessages.getInstance().msgMExecDefArgNothingAttr;
				Application.showErrorMessage(this, errmsg);
				return false;
			}
		}
		
		// 引数の値のチェック
		for (int row = 0 ; row < rowCount; row++) {
			ModuleArgType type = model.getArgumentAttr(row);
			Object val = model.getArgumentValue(row);
			if (val instanceof VirtualFile) {
				// Real file
				if (ModuleArgType.OUT == type) {
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFixedOutput;
					Application.showErrorMessage(this, errmsg);
					return false;
				}
				else if (ModuleArgType.IN != type) {
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFile;
					Application.showErrorMessage(this, errmsg);
					return false;
				}
				else {
					// type == ModuleArgType.IN
					//--- 存在確認
					VirtualFile file = (VirtualFile)val;
					boolean exists;
					try {
						exists = file.exists();
					}
					catch (SecurityException ex) {
						exists = false;
					}
					if (!exists) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + CommonMessages.getInstance().msgFileNotFound;
						Application.showErrorMessage(this, errmsg);
						return false;
					}
				}
			}
			else if (val instanceof IMExecArgParam) {
				// Parameter
				if (val instanceof MExecArgString) {
					if (ModuleArgType.STR != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseString;
						Application.showErrorMessage(this, errmsg);
						return false;
					}
				}
				else if (val instanceof MExecArgPublish) {
					if (ModuleArgType.PUB != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUsePubParam;
						Application.showErrorMessage(this, errmsg);
						return false;
					}
				}
				else if (val instanceof MExecArgSubscribe) {
					if (ModuleArgType.SUB != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseSubParam;
						Application.showErrorMessage(this, errmsg);
						return false;
					}
				} else {
					if (ModuleArgType.IN != type && ModuleArgType.OUT != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFile;
						Application.showErrorMessage(this, errmsg);
						return false;
					}
					if (val instanceof MExecArgTempFile) {
						if (ModuleArgType.IN == type) {
							String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseTempFile;
							Application.showErrorMessage(this, errmsg);
							return false;
						}
					}
				}
			}
			else {
				// String
				if (val == null || val.toString().length() < 1) {
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgNothingValue;
					Application.showErrorMessage(this, errmsg);
					return false;
				}
				
				if (ModuleArgType.STR != type && ModuleArgType.PUB != type && ModuleArgType.SUB != type) {
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseString;
					Application.showErrorMessage(this, errmsg);
					return false;
				}
			}
		}
		
		// 正当
		return true;
	}

	public void restoreConfiguration(ExConfiguration config, String prefix) {
		// no entry
	}

	public void storeConfiguration(ExConfiguration config, String prefix) {
		// no entry
	}

	//------------------------------------------------------------
	// Implements java.awt.event.ComponentListener interfaces
	//------------------------------------------------------------

	@Override
	public void componentResized(ComponentEvent ce) {}

	@Override
	public void componentMoved(ComponentEvent ce) {}

	@Override
	public void componentShown(ComponentEvent ce) {
		// 引数テーブル用ボタンの更新
		_argTable.updateButtons();
	}

	@Override
	public void componentHidden(ComponentEvent ce) {}

	//------------------------------------------------------------
	// Actions
	//------------------------------------------------------------

	/**
	 * 親のダイアログが表示される直前に呼び出されるイベントハンドラ。
	 * @since 3.2.1
	 */
	public void onInitDialog() {
		// adjust all column width
		adjustArgTableAllColumnsPreferredWidth();
		// TODO: 全部のテーブルの幅を調整？
//		initialAllTableColumnWidth();
	}

	/**
	 * フィルタの配置フォルダ選択ボタンをクリックしたときのイベント
	 */
	protected void onChooseLocationButton(ActionEvent ae) {
		// 編集モードのチェック
		if (_editModel==null || _editModel.getEditType() != FilterEditModel.EditType.NEW) {
			throw new IllegalStateException("GenericFilterDefEditPane#onChooseLocationButton : Edit mode is not NEW : editMode=" + String.valueOf(_editModel.getEditType()));
		}

		// 親フォルダの選択
		RunnerFrame mainFrame = (RunnerFrame)ModuleRunner.getApplicationMainFrame();
		VirtualFile vfDir = mainFrame.chooseMExecDefFolder(this,
									true,				// フォルダ作成を許可
									false,				// システムルートは表示しない
									RunnerMessages.getInstance().MExecDefFolderChooser_Title,	// タイトル
									null,				// 説明は表示しない
									_vfParent);			// 初期選択位置
		if (vfDir == null) {
			// user canceled
			return;
		}
		
		// 親フォルダの保存
		if (!vfDir.equals(_vfParent)) {
			_vfParent = vfDir;
			setLocationPath(vfDir);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void redisplayModuleInfo() {
		String path = MExecDefFileManager.GENERICFILTER_SCHEMA_FILENAME;
		String mainClass = (_editModel==null ? "" : _editModel.getMExecDefMainClass());
		_stcModuleFile.setText(path);
		_stcMainClass.setText(mainClass);
	}
	
	protected void setMExecDefName(String text) {
		getNameTextComponent().setText(Strings.nullToEmpty(text));
	}
	
	protected void setMExecDefDescription(String text) {
		_taDesc.setText(Strings.nullToEmpty(text));
		_taDesc.setCaretPosition(0);
	}
	
	protected void setLocationPath(VirtualFile file) {
		_stcLocation.setText(_editModel==null ? file.toString() : _editModel.formatFilePath(file));
	}
	
	protected void createComponents() {
		// create components
		_stcLocation = createFilePathLabel();
		_stcModuleFile = createFilePathLabel();
		_stcMainClass  = createFilePathLabel();
		_argTable    = createArgTable();
		_taDesc      = createDescTextComponent();
		
		// create Buttons
		_btnChooseLocation = CommonResources.createBrowseButton(RunnerMessages.getInstance().MExecDefEditDlg_Tooltip_ChooseLocation);
		_btnArgAdd   = CommonResources.createIconButton(CommonResources.ICON_ADD, RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_new + "...");
		_btnArgEdit  = CommonResources.createMenuIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		_btnArgDel   = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnArgUp    = CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		_btnArgDown  = CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);

		_argTable.attachAddButton(_btnArgAdd);
		_argTable.attachEditButton(_btnArgEdit);
		_argTable.attachDeleteButton(_btnArgDel);
		_argTable.attachUpButton(_btnArgUp);
		_argTable.attachDownButton(_btnArgDown);
		
		_paneModule = createModulePanel();
		
		// setup component state
		switch (_editModel.getEditType()) {
			case VIEW :
				{
					_cNameField = null;
					_cNameLabel = createNameLabel();
					_btnChooseLocation.setVisible(false);
					_btnArgEdit.setVisible(false);
					_btnArgDel .setVisible(false);
					_btnArgUp  .setVisible(false);
					_btnArgDown.setVisible(false);
					_argTable  .setEditable(false);		// read only
					_taDesc    .setEditable(false);		// readonly
				}
				break;
			case NEW :
				{
					_cNameField = createNameTextField();
					_cNameLabel = null;
					_btnChooseLocation.setVisible(true);
					_btnArgEdit.setVisible(true);
					_btnArgDel .setVisible(true);
					_btnArgUp  .setVisible(true);
					_btnArgDown.setVisible(true);
					_argTable  .setEditable(true);
					_taDesc    .setEditable(true);
				}
				break;
			default :
				{
					_cNameField = null;
					_cNameLabel = createNameLabel();
					_btnChooseLocation.setVisible(false);
					_btnArgEdit.setVisible(true);
					_btnArgDel .setVisible(true);
					_btnArgUp  .setVisible(true);
					_btnArgDown.setVisible(true);
					_argTable  .setEditable(true);
					_taDesc    .setEditable(true);
				}
		}
	}
	
	protected void layoutComponents() {
		// ラベルを生成
		JLabel lblName      = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Name + " :");
		JLabel lblLocation  = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Location + " :");
		JLabel lblArgs      = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Args + " :");
		JLabel lblDesc      = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Desc + " :");
		
		// 引数テーブルパネル
		JPanel pnlArgs = createModuleArgsTablePanel();
		
		// 引数ラベルパネル
		JPanel pnlArgTitle = new JPanel(new GridBagLayout());
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.NONE;
			//--- label
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			pnlArgTitle.add(lblArgs, gbc);
			//--- glue
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.SOUTHEAST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			pnlArgTitle.add(new JLabel(), gbc);
		}
		
		// スクロールペインを設定
		//--- desc
		JScrollPane scDesc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scDesc.setViewportView(_taDesc);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		//--- initialize
		Insets vertSpacing = new Insets(2, 0, 0, 0);
		Insets compMargin  = new Insets(2, 2, 0, 0);
		Insets vertSeparate = new Insets(8, 0, 0, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		//--- name
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblName, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.insets = vertSpacing;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(getNameTextComponent(), gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- location
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblLocation, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(_stcLocation, gbc);
		//gbc.gridy++;
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.insets = compMargin;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(_btnChooseLocation, gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- module
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(_paneModule, gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- args
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblArgs, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(pnlArgs, gbc);
		gbc.insets = vertSeparate;
		gbc.weighty = 0;
		gbc.gridy++;
		//--- desc
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblDesc, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(scDesc, gbc);
		gbc.insets = vertSeparate;
		gbc.weighty = 0;
		gbc.gridy++;
		
		// adjust panel size
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//	GridBagLayout では、複数のコンポーネントで余った領域を分け合うとき、
		//	Component#getMinimumSize() や Component#getPrefferedSize() が返す
		//	値が大きいと、そのコンポーネントの実サイズが preffered を超えたときに、
		//	そのサイズが優先され、余分領域の配分が異なってしまう。
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Dimension dmSize = new Dimension(50,50);
		_argTable.setMinimumSize(dmSize);
		_argTable.setPreferredSize(dmSize);
		scDesc.setMinimumSize(dmSize);
		scDesc.setPreferredSize(dmSize);
		pnlArgs.setMinimumSize(dmSize);
		pnlArgs.setPreferredSize(dmSize);
		//---
		dmSize = new Dimension(300, 200);
		this.setMinimumSize(dmSize);
		this.setPreferredSize(dmSize);
	}

	protected void setupActions() {
		// 引数テーブルのドロップを、編集モードなら許可する
		_argTable.setValueDropEnabled(_editModel.isEditing());
		
		// ボタンアクション
		_btnChooseLocation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onChooseLocationButton(ae);
			}
		});
		
		// コンポーネントイベント
		addComponentListener(this);
	}
	
	protected JPanel createModuleArgsTablePanel() {
		// create panel (no border)
		JPanel pnl = new JPanel(new BorderLayout());
		
		// button layout
		Box btnbox = Box.createVerticalBox();
		btnbox.add(_btnArgAdd);
		btnbox.add(Box.createVerticalStrut(2));
		btnbox.add(_btnArgEdit);
		btnbox.add(Box.createVerticalStrut(2));
		btnbox.add(_btnArgDel);
		btnbox.add(Box.createVerticalStrut(2));
		btnbox.add(_btnArgUp);
		btnbox.add(Box.createVerticalStrut(2));
		btnbox.add(_btnArgDown);
		btnbox.add(Box.createGlue());
		
		// adjust button preferred size
		Dimension dm = _btnArgEdit.getPreferredSize();
		_btnArgAdd.setPreferredSize(dm);
		_btnArgAdd.setMinimumSize(dm);
		_btnArgAdd.setMaximumSize(dm);
		_btnArgEdit.setMinimumSize(dm);
		_btnArgEdit.setMaximumSize(dm);
		_btnArgDel.setPreferredSize(dm);
		_btnArgDel.setMinimumSize(dm);
		_btnArgDel.setMaximumSize(dm);
		_btnArgUp.setPreferredSize(dm);
		_btnArgUp.setMinimumSize(dm);
		_btnArgUp.setMaximumSize(dm);
		_btnArgDown.setPreferredSize(dm);
		_btnArgDown.setMinimumSize(dm);
		_btnArgDown.setMaximumSize(dm);

		// layout
		pnl.add(_argTable, BorderLayout.CENTER);
		pnl.add(btnbox, BorderLayout.EAST);
		
		Dimension dmMin = pnl.getMinimumSize();
		Dimension dmBox = btnbox.getPreferredSize();
		pnl.setMinimumSize(new Dimension(dmMin.width, dmBox.height));
		
		return pnl;
	}
	
	protected JTextField createNameTextField() {
		JTextField field = new JTextField();
		return field;
	}
	
	protected JStaticMultilineTextPane createNameLabel() {
		return new JStaticMultilineTextPane();
	}
	
	protected JTextComponent getNameTextComponent() {
		return (_cNameField==null ? _cNameLabel : _cNameField);
	}
	
	protected JStaticMultilineTextPane createFilePathLabel() {
		return new JStaticMultilineTextPane();
	}
	
	protected GenericFilterDefArgsTablePane createArgTable() {
		GenericFilterDefArgsTablePane pane = new GenericFilterDefArgsTablePane();
		pane.initialComponent();
		return pane;
	}
	
	protected JTextArea createDescTextComponent() {
		JTextArea ta = new JTextArea();
		ta.setEditable(true);
		ta.setLineWrap(true);
		return ta;
	}
	
	protected JPanel createModulePanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		Border bdLine = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border outsideBorder = BorderFactory.createTitledBorder(bdLine,	RunnerMessages.getInstance().MExecDefEditDlg_Label_Module, TitledBorder.LEFT, TitledBorder.TOP);
		Border insideBorder = BorderFactory.createEmptyBorder(0, 0, 5, 5);
		pnl.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));
		
		// create label
		JLabel lblFile      = new JLabel(CommonMessages.getInstance().labelFile + " :");
		JLabel lblMainClass = new JLabel(CommonMessages.getInstance().labelMainClass + " :");
		Dimension dmFieldSize = _stcModuleFile.getPreferredSize();
		Dimension dmLabelSize = lblFile.getPreferredSize();
		int height = dmFieldSize.height - dmLabelSize.height;
		if (height > 1) {
			int itop = height / 2;
			int ibottom = height - itop;
			Border bdMargin = BorderFactory.createEmptyBorder(itop, 0, ibottom, 0);
			lblFile.setBorder(bdMargin);
			lblMainClass.setBorder(bdMargin);
		}
		
		// layout
		Insets insets = new Insets(5,5,0,0);
		Insets btnInsets = new Insets(5,3,0,0);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = insets;
		gbc.gridy = 0;
		//--- file
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(lblFile, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(_stcModuleFile, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets = insets;
		gbc.gridy++;
		//--- main class
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(lblMainClass, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(_stcMainClass, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets = btnInsets;
//		pnl.add(_btnGraphModule, gbc);
		gbc.insets = insets;
		gbc.gridy++;
		
		return pnl;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
