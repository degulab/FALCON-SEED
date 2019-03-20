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
 * @(#)EtcDestItemEditPane.java	3.3.0	2016/05/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.excel2csv.swing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.runtime.util.Objects;
import ssac.falconseed.file.VirtualFileTransferable;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.Strings;
import ssac.util.excel2csv.EtcConfigCsvData;
import ssac.util.io.VirtualFile;
import ssac.util.swing.JMultilineLabel;
import ssac.util.swing.JMultilineTextField;
import ssac.util.swing.JStaticMultilineTextPane;

/**
 * <code>[Excel to CSV]</code> 変換定義の出力先設定パネル。
 * 一つの CSV ファイルの出力先を編集するために利用される。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcDestItemEditPane extends JPanel implements DocumentListener, FocusListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** この要素コンポーネントを管理するコンポーネント **/
	private EtcDestSetEditPane		_parentPane;

	/** 出力設定データのインデックス(0～) **/
	private int					_itemIndex;
	/** 現在の出力先ファイルパス **/
	private VirtualFile			_vfDestFile;
	
	/** 出力設定データ **/
	private EtcConfigCsvData	_itemData;
	
	/** このパネルのエラーメッセージ、エラーがない場合は <tt>null</tt> **/
	private String				_errmsg;

	/** 値編集用テキストボックスのデフォルト背景色 **/
	private Color	_defStcValueBackground;
	/** 値表示用ラベルのデフォルト背景色 **/
	private Color	_defEdtValueBackground;

	/** 番号表示ラベル **/
	private JLabel				_lblDestNo;
	/** 変換タイトル表示ラベル **/
	private JMultilineLabel		_lblTitle;
	/** テンポラリ用プレフィックス編集コンポーネント **/
	private JMultilineTextField	_edtValue;
	/** ファイルパス表示ラベル **/
	private JStaticMultilineTextPane	_stcValue;
	/** 閲覧ファイル選択ボタン **/
	private JButton		_btnChooseFile;
//	/** ユーザー操作による結果閲覧チェックボックスの状態保存 **/
//	private Boolean		_saveUserShowDestFlag = null;
	/** オプションチェックボックス **/
	private JCheckBox	_chkShowDest;
	/** テンポラリファイル出力 **/
	private JCheckBox	_chkOutToTemp;
	/** ファイル閲覧チェックボックスの変更イベントを無視するフラグ **/
	private boolean	_ignoreShowDestChangeEvent = false;
	/** テンポラリ出力チェックボックスの変更イベントを無視するフラグ **/
	private boolean	_ignoreOutToTempChangeEvent = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EtcDestItemEditPane(int index, EtcConfigCsvData destItem, EtcDestSetEditPane parentPane) {
		super(new GridBagLayout());
		if (destItem == null)
			throw new NullPointerException();
		if (parentPane == null)
			throw new NullPointerException();
		_parentPane = parentPane;
		_itemIndex  = index;
		_itemData   = destItem;
		_errmsg     = null;
		
		// create components
		_lblDestNo             = createDestNoLabel();
		_lblTitle              = createTitleLabel();
		_chkShowDest           = createShowDestCheckBox();
		_chkOutToTemp          = createOutToTempCheckBox();
		_btnChooseFile         = createChooseFileButton();
		_stcValue              = createStaticValueComponent();
		_edtValue              = createEditValueComponent();
		_defStcValueBackground = _stcValue.getBackground();
		_defEdtValueBackground = _edtValue.getBackground();
		
		// layout
		initialLayout();

		// setup actions
		//--- document listener
		_edtValue.getDocument().addDocumentListener(this);
		//--- drop target
		DataFileDropTargetListener hDrop = new DataFileDropTargetListener();
		new DropTarget(this._stcValue, DnDConstants.ACTION_COPY, hDrop, true);
		new DropTarget(this._edtValue, DnDConstants.ACTION_COPY, hDrop, true);
		//--- focus listener
		_lblDestNo.setFocusable(false);
		_lblTitle.setFocusable(false);
		_stcValue.setFocusable(false);
		_lblDestNo.addFocusListener(this);
		_lblTitle.addFocusListener(this);
		_chkShowDest.addFocusListener(this);
		_chkOutToTemp.addFocusListener(this);
		_btnChooseFile.addFocusListener(this);
		_stcValue.addFocusListener(this);
		_edtValue.addFocusListener(this);
		
		// setup initial value
		_lblDestNo.setText("[" + String.valueOf(_itemIndex + 1) + "]");
		_lblTitle .setText(_itemData.getConversionTitle());
		redisplayAll();
		verifyValue();
		redisplayErrorState();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getItemIndex() {
		return _itemIndex;
	}
	
	public EtcConfigCsvData getItemData() {
		return _itemData;
	}
	
	public String getDestNoLabelText() {
		return _lblDestNo.getText();
	}
	
	public String getTitleLabelText() {
		return _lblTitle.getText();
	}

	/**
	 * このコンポーネントにエラーメッセージが設定されているかを判定する。
	 * @return	エラーメッセージが設定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean hasError() {
		return (_errmsg != null);
	}

	/**
	 * このコンポーネントに設定されているエラーメッセージを取得する。
	 * @return	設定されているエラーメッセージを返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getError() {
		return _errmsg;
	}

	/**
	 * このコンポーネントからエラーメッセージを除去する。
	 * このメソッド呼び出しの直後、{@link #hasError()} は <tt>false</tt> を返す。
	 */
	public void clearError() {
		if (_errmsg != null) {
			clearErrorDisplay();
			_errmsg = null;
		}
	}

	/**
	 * このコンポーネントに新しいエラーメッセージを設定する。
	 * <em>errmsg</em> が <tt>null</tt> もしくは空文字列の場合は、
	 * {@link #clearError()} を呼び出したのと同じ結果となる。
	 * @param errmsg	設定するエラーメッセージ
	 */
	public void setError(String errmsg) {
		// 指定されたメッセージが無効なら、エラーをクリア
		if (Strings.isNullOrEmpty(errmsg)) {
			clearError();
		}
		
		// 新しいメッセージを設定
		String olderr = this._errmsg;
		this._errmsg = errmsg;
		if (!errmsg.equals(olderr)) {
			setErrorDisplay(errmsg);
		}
	}

	/**
	 * 現在の出力先ディレクトリを取得する。
	 * <p>このメソッドはテンポラリ出力設定の状態に関係なく、現在の設定を返す。
	 * @return	現在の出力先ディレクトリ、出力先が設定されていない場合は <tt>null</tt>
	 */
	public VirtualFile getCurrentOutputDirectory() {
		return (_vfDestFile==null ? null : _vfDestFile.getParentFile());
	}

	/**
	 * 結果出力先のディレクトリが変更可能かを判定する。
	 * この判定では、テンポラリ出力ではなく、出力先ファイルパスのディレクトリが以前のディレクトリ異なる場合のみ、出力先ディレクトリが変更可能となる。
	 * また、<em>oldBaseOutDir</em> が <tt>null</tt> の場合も変更可能とする。
	 * @param newBaseOutDir	新しい基準出力ディレクトリ
	 * @param oldBaseOutDir	以前の基準出力ディレクトリ
	 * @return	変更可能なら <tt>true</tt>、それ以外なら <tt>false</tt>
	 */
	public boolean canChangeOutputDirectory(VirtualFile newBaseOutDir, VirtualFile oldBaseOutDir) {
		return canChangeOutputDirectory(getCurrentOutputDirectory(), newBaseOutDir, oldBaseOutDir);
	}

	/**
	 * 結果出力先のディレクトリが変更可能かを判定する。
	 * この判定では、テンポラリ出力ではなく、出力先ファイルパスのディレクトリが以前のディレクトリ異なる場合のみ、出力先ディレクトリが変更可能となる。
	 * また、<em>curOutDir</em> または <em>oldBaseOutDir</em> が <tt>null</tt> の場合も変更可能とする。
	 * @param curOutDir		現在の出力先ディレクトリ
	 * @param newBaseOutDir	新しい基準出力ディレクトリ
	 * @param oldBaseOutDir	以前の基準出力ディレクトリ
	 * @return	変更可能なら <tt>true</tt>、それ以外なら <tt>false</tt>
	 */
	protected boolean canChangeOutputDirectory(VirtualFile curOutDir, VirtualFile newBaseOutDir, VirtualFile oldBaseOutDir) {
		if (getOutToTempEnabled() || curOutDir == null || oldBaseOutDir == null) {
			// 変更可能
			return true;
		}
		else if (curOutDir.equals(oldBaseOutDir) || curOutDir.equals(newBaseOutDir)) {
			// 変更可能
			return true;
		}
		else {
			// 変更不可能
			return false;
		}
	}

	/**
	 * 結果出力先の基準ディレクトリを変更する。
	 * このメソッドでは、出力先ファイルパスのディレクトリが以前のディレクトリ異なる場合のみ、出力先ディレクトリを変更する。
	 * <em>oldBaseOutDir</em> が <tt>null</tt> の場合は、強制的に出力先ディレクトリを変更する。
	 * @param newBaseOutDir	新しい基準出力ディレクトリ
	 * @param oldBaseOutDir	以前の基準出力ディレクトリ
	 * @return	出力先ファイルが変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean changeBaseOutputDirectory(VirtualFile newBaseOutDir, VirtualFile oldBaseOutDir) {
		// 現在の出力先ディレクトリを取得
		VirtualFile curOutDir = getCurrentOutputDirectory();

		// 出力先ディレクトリの変更が許可できない場合は、処理終了
		if (!canChangeOutputDirectory(curOutDir, newBaseOutDir, oldBaseOutDir)) {
			return false;
		}
		
		// 出力先ディレクトリの変更
		VirtualFile vfDestFile;
		if (curOutDir == null) {
			String name = _itemData.getDefaultFilename();
			if (name == null)
				name = "dest";
			name = name + FileChooserManager.getCsvFileFilter().getDefaultExtension();
			vfDestFile = newBaseOutDir.getChildFile(name);
		} else {
			vfDestFile = newBaseOutDir.getChildFile(_vfDestFile.getName());
		}
		
		return setDestFile(vfDestFile, false);
	}

	/**
	 * 結果出力先ファイルパスを取得する。
	 * @return	結果出力先ファイルパス、設定されていない場合は <tt>null</tt>
	 */
	public VirtualFile getDestinationFile() {
		return _vfDestFile;
	}

	/**
	 * 結果出力先ファイルパスを設定する。
	 * @param destfile		設定するパス
	 * @param userModified	ユーザーによる変更なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @return	出力先ファイルパスが変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 */
	public boolean setDestFile(VirtualFile vfDestFile, boolean userModified) {
		if (Objects.equals(vfDestFile, _vfDestFile)) {
			// no changes
			return false;
		}
		
		_vfDestFile = vfDestFile;
		if (vfDestFile == null) {
			_stcValue.setText("");
			_itemData.setDestFile(null);
		} else {
			_stcValue.setText(_parentPane.formatDestPath(vfDestFile));
			_itemData.setDestFile(ModuleFileManager.toJavaFile(vfDestFile));
		}
		verifyValue();
		return true;
	}

	/**
	 * テンポラリ出力時のプレフィックスを設定する。
	 * @param prefix		設定する文字列
	 * @param userModified	ユーザーによる変更なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @return	テンポラリ出力時のプレフィックスが変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 */
	public boolean setOutputTemporaryPrefix(String prefix, boolean userModified) {
		if (prefix != null && !prefix.isEmpty()) {
			if (prefix.equals(_itemData.getTemporaryPrefix())) {
				// no changes
				return false;
			}
		}
		else {
			if (_itemData.getTemporaryPrefix() == null) {
				// no changes
				return false;
			}
			prefix = "";
		}
		
		_itemData.setTemporaryPrefix(prefix);
		if (!userModified) {
			_edtValue.setText(prefix);
			//verifyValue();	- verify は DocumentChangeEvent で実行
		}
		return true;
	}

	/**
	 * テンポラリ出力設定が有効であるかどうかを判定する。
	 * @return	有効なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getOutToTempEnabled() {
		return _itemData.getOutputTemporaryEnabled();
	}

	/**
	 * テンポラリ出力設定を変更する。
	 * @param toEnable		テンポラリ出力を有効とする場合は <tt>true</tt>、無効とする場合は <tt>false</tt>
	 * @param userModified	ユーザーによる変更なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @return	設定が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean setOutToTempEnabled(boolean toEnable, boolean userModified) {
		if (_itemData.getOutputTemporaryEnabled() == toEnable) {
			// no changes
			return false;
		}
		
		_itemData.setOutputTemporaryEnabled(toEnable);
		if (!userModified) {
			_ignoreOutToTempChangeEvent = true;
			_chkOutToTemp.setSelected(true);
			_ignoreOutToTempChangeEvent = false;
		}
		redisplayCheckedOptions();
		verifyValue();
		
		_parentPane.editOutToTempChanged(this);
		
//		if (_saveUserShowDestFlag == null) {
			if (_itemData.getShowDestFileEnabled() != toEnable) {
				_itemData.setShowDestFileEnabled(toEnable);
				_ignoreShowDestChangeEvent = true;
				_chkShowDest.setSelected(toEnable);
				_ignoreShowDestChangeEvent = false;
				_parentPane.editShowDestChanged(this);
			}
//		}
		
		return true;
	}

	/**
	 * 結果閲覧設定が有効であるかどうかを判定する。
	 * @return	有効なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getShowDestEnabled() {
		return _itemData.getShowDestFileEnabled();
	}
	
	/**
	 * テンポラリ出力設定を変更する。
	 * @param toEnable		テンポラリ出力を有効とする場合は <tt>true</tt>、無効とする場合は <tt>false</tt>
	 * @param userModified	ユーザーによる変更なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @return	設定が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean setShowDestEnabled(boolean toEnable, boolean userModified) {
		if (_itemData.getShowDestFileEnabled() == toEnable) {
			// no changes
			return false;
		}

		_itemData.setShowDestFileEnabled(toEnable);
		if (!userModified) {
			_ignoreShowDestChangeEvent = true;
			_chkShowDest.setSelected(toEnable);
			_ignoreShowDestChangeEvent = false;
		} else {
//			_saveUserShowDestFlag = toEnable;
		}
		
		_parentPane.editShowDestChanged(this);
		return true;
	}

	/**
	 * すべてのコンポーネントの表示を更新する。
	 */
	public void updateValueComponents() {
		redisplayAll();
	}

	/**
	 * 値の正当性を検証する。
	 * このメソッドは、編集の可不可に関わらず値を検証し、エラーステートを更新する。
	 * @return	値が正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyValue() {
		if (_itemData.getOutputTemporaryEnabled()) {
			// テンポラリ出力
			String strPrefix = _itemData.getTemporaryPrefix();
			if (strPrefix != null && strPrefix.length() < 3) {
				setError(RunnerMessages.getInstance().msgErrorRequiredOver3chars);
				return false;
			}
		}
		else if (_itemData.getDestFile() == null) {
			setError(RunnerMessages.getInstance().Excel2csv_msgConfigDestFileIsNone);
			return false;
		}
		else if (_itemData.getDestFile().isDirectory()) {
			setError(RunnerMessages.getInstance().Excel2csv_msgConfigDestFileIsDir);
			return false;
		}
		
		// エラーなし
		clearError();
		return true;
	}

	//------------------------------------------------------------
	// Implement javax.swing.event.DocumentListener interfaces
	//------------------------------------------------------------

	@Override
	public void changedUpdate(DocumentEvent de) {
		onChangeStringValue(de);
	}

	@Override
	public void insertUpdate(DocumentEvent de) {
		onChangeStringValue(de);
	}

	@Override
	public void removeUpdate(DocumentEvent de) {
		onChangeStringValue(de);
	}

	//------------------------------------------------------------
	// Implement java.awt.event.FocusListener interfaces
	//------------------------------------------------------------
	
	@Override
	public void focusGained(FocusEvent fe) {
		_parentPane.scrollClientComponentToVisible(fe.getComponent());
	}

	@Override
	public void focusLost(FocusEvent fe) {}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onChangeStringValue(DocumentEvent e) {
		String strValue;
		try {
			strValue = e.getDocument().getText(0, e.getDocument().getLength());
		} catch (BadLocationException ignoreEx) {
			strValue = "";
		}
		_itemData.setTemporaryPrefix(strValue);
		verifyValue();
		//System.out.println("EtcDestItem[" + _itemIndex + "]#onChangeStringValue(" + e.toString() + ")");
	}
	
	protected void onSelectedShowDestCheckBox() {
		if (!_ignoreShowDestChangeEvent) {
			setShowDestEnabled(_chkShowDest.isSelected(), true);
		}
	}
	
	protected void onSelectedOutToTempCheckBox() {
		if (!_ignoreOutToTempChangeEvent) {
			setOutToTempEnabled(_chkOutToTemp.isSelected(), true);
		}
	}

	protected void onButtonChooseFile() {
		RunnerFrame frame = (RunnerFrame)ModuleRunner.getInstance().getMainFrame();
		File orgRecFile = FileChooserManager.getRecommendedDirectory();
		FileChooserManager.setRecommendedDirectory(frame.getDataFileUserRootDirectory());
		File selFile = FileChooserManager.chooseArgCsvFile(this, FileChooserManager.getInitialDocumentFile(_itemData.getDestFile()));
		FileChooserManager.setRecommendedDirectory(orgRecFile);
		if (selFile != null) {
			setDestFile(ModuleFileManager.fromJavaFile(selFile), true);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このコンポーネントが値を表示するコンポーネントを取得する。
	 * @return	<code>JTextComponent</code> オブジェクト
	 */
	protected JTextComponent getCurrentValueComponent() {
		if (_edtValue.isVisible()) {
			return _edtValue;
		} else {
			return _stcValue;
		}
	}
	
	protected void redisplayDestFile() {
		String strValue = _parentPane.formatDestPath(_vfDestFile);
		_stcValue.setText(strValue==null ? "" : strValue);
	}
	
	protected void redisplayTempPrefix() {
		String strValue = _itemData.getTemporaryPrefix();
		_edtValue.setText(strValue==null ? "" : strValue);
	}
	
	protected void redisplayCheckedOptions() {
		boolean enableOutToTemp = _itemData.getOutputTemporaryEnabled();
		_edtValue.setVisible(enableOutToTemp);
		_stcValue.setVisible(!enableOutToTemp);
		_btnChooseFile.setEnabled(!enableOutToTemp);
		_ignoreOutToTempChangeEvent = true;
		_chkOutToTemp.setSelected(_itemData.getOutputTemporaryEnabled());
		_ignoreOutToTempChangeEvent = false;
		_ignoreShowDestChangeEvent = true;
		_chkShowDest.setSelected(_itemData.getShowDestFileEnabled());
		_ignoreShowDestChangeEvent = false;
	}
	
	protected void redisplayAll() {
		redisplayDestFile();
		redisplayTempPrefix();
		redisplayCheckedOptions();
	}
	
	protected void redisplayErrorState() {
		if (hasError()) {
			setErrorDisplay(getError());
		} else {
			clearErrorDisplay();
		}
	}
	
	protected void clearErrorDisplay() {
		_edtValue.setToolTipText(null);
		_stcValue.setToolTipText(null);
		_edtValue.setBackground(_defEdtValueBackground);
		_stcValue.setBackground(_defStcValueBackground);
	}
	
	protected void setErrorDisplay(String errmsg) {
		JTextComponent cValue = getCurrentValueComponent();
		cValue.setToolTipText(errmsg);
		cValue.setBackground(CommonResources.DEF_BACKCOLOR_ERROR);
	}

	/**
	 * 番号表示用ラベルを生成
	 * @return	生成された <code>JLabel</code> オブジェクト
	 */
	protected JLabel createDestNoLabel() {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(1,5,1,5)));
		label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
		return label;
	}

	/**
	 * 変換タイトルのラベルを生成
	 * @return	生成された <code>JMultilineLabel</code> オブジェクト
	 */
	protected JMultilineLabel createTitleLabel() {
		JMultilineLabel label = new JMultilineLabel();
		return label;
	}

	/**
	 * 引数設定オプションのチェックボックスを生成
	 * @return	生成された <code>JCheckBox</code> オブジェクト
	 */
	protected JCheckBox createShowDestCheckBox() {
		JCheckBox chk = new JCheckBox(RunnerMessages.getInstance().MExecDefEditDlg_Arg_ShowResultCsv);
		chk.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				onSelectedShowDestCheckBox();
			}
		});
		return chk;
	}
	
	protected JCheckBox createOutToTempCheckBox() {
		JCheckBox chk = new JCheckBox();
		chk.setText(RunnerMessages.getInstance().MExecDefEditDlg_Arg_OutToTemp);
		chk.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				onSelectedOutToTempCheckBox();
			}
		});
		return chk;
	}

	/**
	 * 編集ボタンを生成
	 * @return	生成された <code>JButton</code> オブジェクト
	 */
	protected JButton createChooseFileButton() {
		JButton btn = CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonChooseFile();
			}
		});
		return btn;
	}

	/**
	 * 値編集用コンポーネントを生成
	 * @return	生成された <code>JMultilineTextField</code> オブジェクト
	 */
	protected JMultilineTextField createEditValueComponent() {
		JMultilineTextField ta = new JMultilineTextField();
		ta.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		ta.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		ta.setEditable(true);
		ta.setBorder(UIManager.getBorder("TextField.border"));
		return ta;
	}

	/**
	 * 編集不可能な引数値表示コンポーネントを生成
	 * @return	生成された <code>JStaticMultilineTextPane</code> オブジェクト
	 */
	protected JStaticMultilineTextPane createStaticValueComponent() {
		return new JStaticMultilineTextPane();
	}


	/**
	 * コンポーネントを配置する。
	 */
	protected void initialLayout() {
		Insets typeInsets = new Insets(0, 0, 3, 0);
		Insets descInsets = new Insets(0, 3, 3, 0);
		Insets valueInsets = new Insets(0, 20, 0, 0);
		Insets rightInsets = new Insets(0, 3, 0, 0);
		JPanel valuePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0;
		//--- check box
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		valuePanel.add(_chkShowDest, gbc);
		//--- out to temp
		gbc.gridy=1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		valuePanel.add(_chkOutToTemp, gbc);
		gbc.gridy=0;
		gbc.gridheight = 2;
		gbc.weighty = 1;
		gbc.gridx++;
		gbc.insets = rightInsets;
		//--- value component
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		valuePanel.add(_stcValue, gbc);
		valuePanel.add(_edtValue, gbc);
		gbc.gridx++;
		gbc.insets = rightInsets;
		//--- button
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		valuePanel.add(_btnChooseFile, gbc);
		gbc.gridx++;
		
		// layout main panel
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		//--- attr
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = typeInsets;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		this.add(_lblDestNo, gbc);
		//--- desc
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = descInsets;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(_lblTitle, gbc);
		//--- value
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = valueInsets;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(valuePanel, gbc);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * アイテムへのファイルのドロップを受け付けるリスナー
	 * @version 3.3.0
	 * @since 3.3.0
	 */
	protected class DataFileDropTargetListener extends DropTargetAdapter
	{
		private boolean _canImport = false;
		
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			_canImport = false;
			
			// サポートする DataFlavor のチェック
			if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// サポートされないデータ形式
				dtde.rejectDrag();
				return;
			}
			
			// ソースアクションにコピーが含まれていない場合は、許可しない
			if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
				// サポートされないドロップアクション
				dtde.rejectDrag();
				return;
			}
			
			// ドロップアクションをコピー操作に限定
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			_canImport = true;
		}
		
		public void dragExit(DropTargetEvent dte) {
			_canImport = false;
		}

		public void dragOver(DropTargetDragEvent dtde) {
			if (_canImport) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.rejectDrag();
			}
		}
		
		public void drop(DropTargetDropEvent dtde) {
			// ソースアクションにコピーが含まれていない場合は、許可しない
			if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
				// サポートされないドロップアクション
				dtde.rejectDrop();
				return;
			}
			
			// データソースの取得
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			Transferable t = dtde.getTransferable();
			if (t == null) {
				dtde.rejectDrop();
				return;
			}
			
			// ファイルを取得する
			VirtualFile vfTarget = getTargetFile(t);
			if (vfTarget != null) {
				//--- テンポラリ出力の場合は、ファイル選択に切り替える
				if (_itemData.getOutputTemporaryEnabled()) {
					setOutToTempEnabled(true, false);
				}
				if (!vfTarget.equals(_vfDestFile)) {
					setDestFile(_vfDestFile, true);
				}
				//--- ドロップ完了
				dtde.dropComplete(true);
			}
			
			// drop を受け付けない
			dtde.rejectDrop();
		}
		
		protected VirtualFile getTargetFile(Transferable transfer) {
			// VirtualFile
			{
				VirtualFile vf = getTargetVirtualFile(transfer);
				if (vf != null) {
					return vf;
				}
			}
			
			// not supported data
			return null;
		}
		
		protected VirtualFile getTargetVirtualFile(Transferable transfer) {
			if (!transfer.isDataFlavorSupported(VirtualFileTransferable.virtualFileListFlavor))
				return null;
			
			VirtualFile vfTarget = null;
			try {
				// 先頭のファイルのみ受け入れる
				//--- 設定ファイル(*.prefs) を除くパスのみを取得
				VirtualFile[] filelist = (VirtualFile[])transfer.getTransferData(VirtualFileTransferable.virtualFileListFlavor);
				if (filelist != null) {
					for (VirtualFile f : filelist) {
						vfTarget = f;
//						if (!Strings.endsWithIgnoreCase(f.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
//							fTarget = f;
//							break;
//						}
						break;
					}
				}
			}
			catch (Throwable ex) {
				// ignore exception
			}
			
			return vfTarget;
		}
	}
}
