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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecArgsConfigPane.java	3.1.0	2014/05/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsConfigPane.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
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
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.file.VirtualFileTransferable;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.IModuleArgValidationHandler;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleArgValidator;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgDirectory;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.args.MExecArgTextFile;
import ssac.falconseed.module.args.MExecArgXmlFile;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.swing.JMultilineLabel;
import ssac.util.swing.JStaticMultilineTextPane;

/**
 * モジュール実行時引数の１引数設定ペイン。
 * 
 * @version 3.1.0	2014/05/14
 */
public class MExecArgConfigPane extends JPanel implements IModuleArgValidationHandler, DocumentListener, FocusListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 編集操作時のイベントハンドラ **/
	private final IMExecArgConfigHandler	_handler;

	/** 設定対象のデータモデル **/
	private IModuleArgConfig	_itemdata;
	/** 値のエラーとして表示する文字列 **/
	private String	_errmsg;
	
	// Common controls

	/** 値編集用テキストボックスのデフォルト背景色 **/
	private Color	_defStcValueBackground;
	/** 値表示用ラベルのデフォルト背景色 **/
	private Color	_defEdtValueBackground;

	/** 引数属性表示ラベル **/
	private JLabel				_lblArgType;
	/** 引数説明表示ラベル **/
	private JMultilineLabel	_lblArgDesc;
	/** 値編集コンポーネント **/
	private JTextArea			_edtValue;
	/** 値表示ラベル **/
	private JStaticMultilineTextPane	_stcValue;
	/** 編集ボタン **/
	private JButton	_btnEdit;
	
	// for [IN] argument
	
	/** 閲覧ファイル選択ボタン **/
	private JButton	_btnChooseViewingFile;
	
	// for [STR] argument
	
	// for [OUT] argument

	/** オプションチェックボックス **/
	private JCheckBox	_chkShowFileAfterRun;
	/** テンポラリファイル出力 **/
	private JCheckBox	_chkOutToTemp;
	/** ファイル出力チェックボックスの状態保存 **/
	private boolean	_saveShowFileAfterRun;
	
	/** ファイル閲覧状態の保存を無視するフラグ **/
	//private boolean	_ignoreSaveShowFileAfterRunState = false;
	/** ファイル閲覧チェックボックスの変更イベントを無視するフラグ **/
	private boolean	_ignoreShowFileAfterRunChangeEvent = false;
	/** テンポラリ出力チェックボックスの変更イベントを無視するフラグ **/
	private boolean	_ignoreOutToTempChangeEvent = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecArgConfigPane(final IMExecArgConfigHandler handler) {
		super(new GridBagLayout());
		assert (handler != null);
		this._handler = handler;
		
		// create components
		this._lblArgType            = createArgTypeLabel();
		this._lblArgDesc            = createDescriptionLabel();
		this._chkShowFileAfterRun   = createShowFileAfterRunCheckBox();
		this._chkOutToTemp          = createOutToTempCheckBox();
		this._btnChooseViewingFile  = createChooseViewingFileButton();
		this._btnEdit               = createEditButton();
		this._stcValue              = createStaticValueComponent();
		this._edtValue              = createEditValueComponent();
		this._defStcValueBackground = _stcValue.getBackground();
		this._defEdtValueBackground = _edtValue.getBackground();
		
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
		this._lblArgType.setFocusable(false);
		this._lblArgDesc.setFocusable(false);
		this._stcValue.setFocusable(false);
		this._lblArgType.addFocusListener(this);
		this._lblArgDesc.addFocusListener(this);
		this._chkShowFileAfterRun.addFocusListener(this);
		this._chkOutToTemp.addFocusListener(this);
		this._btnChooseViewingFile.addFocusListener(this);
		this._btnEdit.addFocusListener(this);
		this._stcValue.addFocusListener(this);
		this._edtValue.addFocusListener(this);
		
		//setFocusTraversalPolicy(new ItemFocusTraversalPolicy());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントが編集可能か判定する。
	 * @return	編集可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEditable() {
		if (!_handler.isEditable())
			return false;

		return isDataEditable(_itemdata);
	}

	/**
	 * 指定されたデータモデルのデータが編集可能か判定する。
	 * @return	編集可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean isDataEditable(final IModuleArgConfig itemdata) {
		if (itemdata == null)
			return false;
		
		if (itemdata.isFixedValue())
			return false;	// 固定値の場合は編集不可
		
		if (itemdata.getValue() instanceof ModuleArgID)
			return false;	// 引数値が別モジュールの引数を指定している場合は編集不可
		
		// 編集可能
		return true;
	}

	/**
	 * このコンポーネントのデータモデルを取得する。
	 * @return	<code>IModuleArgConfig</code> インタフェースを実装するオブジェクトを返す。
	 * 			データモデルが設定されていない場合は <tt>null</tt> を返す。
	 */
	public IModuleArgConfig getArgDataModel() {
		return _itemdata;
	}

	/**
	 * このコンポーネントのデータモデルを設定する。
	 * このコンポーネントは指定されたデータモデルに従い、表示するコンポーネントを制御する。
	 * なお、指定されたコンポーネントによってデータが編集された場合、設定されている
	 * データモデルの内容は変更される。
	 * @param datamodel	データモデルを指定する。設定しない場合は <tt>null</tt> を指定する。
	 */
	public void setArgDataModel(final IModuleArgConfig datamodel) {
		if (_itemdata == datamodel)
			return;	// 変更なし
		
		// 引数の更新
		_itemdata = datamodel;
		redisplayArgType(_handler.isVisibleArgNo());
		redisplayArgDescription();
		redisplayArgValue();
		redisplayErrorState();
	}

	/**
	 * このコンポーネントに設定されたデータモデルの引数番号を取得する。
	 * @return	データモデルが設定されている場合はその引数番号、設定されていない場合は 0 を返す。
	 */
	public int getArgNo() {
		return (_itemdata==null ? 0 : _itemdata.getArgNo());
	}

	/**
	 * このコンポーネントに設定されたデータモデルの引数種別を取得する。
	 * @return	データモデルが設定されている場合はその引数種別、設定されていない場合は <tt>null</tt> を返す。
	 */
	public ModuleArgType getArgType() {
		return (_itemdata==null ? null : _itemdata.getType());
	}

	/**
	 * 引数種別のラベルに表示している文字列を取得する。
	 * @return	ラベルの文字列
	 */
	public String getArgTypeLabelText() {
		return _lblArgType.getText();
	}

	/**
	 * 引数説明のラベルに表示している文字列を取得する。
	 * @return	ラベルの文字列
	 */
	public String getArgDescriptionLabelText() {
		return _lblArgDesc.getText();
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
	 * 引数番号と引数属性の表示を更新する。
	 */
	public void updateArgNo() {
		redisplayArgType(_handler.isVisibleArgNo());
	}

	/**
	 * 引数値に関するすべてのコンポーネントの表示を更新する。
	 */
	public void updateValueComponents() {
		redisplayArgValue();
	}

	/**
	 * 値の正当性を検証する。
	 * このメソッドは、編集の可不可に関わらず値を検証し、エラーステートを更新する。
	 * @return	値が正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyValue() {
		return ModuleArgValidator.getInstance().verifyValue(_itemdata, this);
	}

	/**
	 * このコンポーネントに履歴として保存された引数の値を設定する。
	 * この履歴引数がこのコンポーネントの引数属性と異なる場合や、
	 * 値の変更が許可されていない場合、値の型が合わない場合、
	 * 値が <tt>null</tt> の場合、引数の値は設定されない。
	 * @param hdata	履歴引数
	 * @return	履歴引数によりこのコンポーネントの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.20
	 */
	public boolean setHistoryData(ModuleArgData hdata) {
		Object hvalue = hdata.getValue();
		if (hvalue == null)
			return false;
		
		if (isEditable() && _itemdata.getType()==hdata.getType()) {
			switch (_itemdata.getType()) {
				case IN :
					return setInputHistoryData(hdata.getValue());
				case OUT :
					return setOutputHistoryData(hdata.getValue());
				case STR :
					return setStringHistoryData(hdata.getValue());
				case PUB :
					return setPublishHistoryData(hdata.getValue());
				case SUB :
					return setSubscribeHistoryData(hdata.getValue());
			}
		}
		
		// not set
		return false;
	}
	
	protected boolean setInputHistoryData(Object histValue) {
		VirtualFile vfFile = null;
		if (histValue instanceof VirtualFile) {
			vfFile = ((VirtualFile)histValue).getAbsoluteFile().getNormalizedFile();
		}
		else if (histValue instanceof File) {
			vfFile = ModuleFileManager.fromJavaFile((File)histValue).getAbsoluteFile().getNormalizedFile();
		}
		
		if (vfFile != null) {
			_itemdata.setValue(vfFile);
			_stcValue.setText(formatValue(vfFile));
			verifyValue();
			return true;
		}
		else {
			return false;
		}
	}
	
	protected boolean setOutputHistoryData(Object histValue) {
		if (_itemdata == null)
			return false;	// no data
		
		VirtualFile vfFile = null;
		if (histValue instanceof MExecArgTempFile) {
			// テンポラリファイル出力
			if (_chkOutToTemp.isVisible() && _chkOutToTemp.isEnabled()) {
				_itemdata.setOutToTempEnabled(true);
				_itemdata.setTempFilePrefix(null);
				redisplayOutputValue();
				updateOutputControlStates(true, _itemdata.getShowFileAfterRun());
//				_chkOutToTemp.setSelected(true);
//				_stcValue.setText("");
//				getItem().setTempFilePrefix("");
				ModuleArgValidator.getInstance().verifyOutputFileValue(_itemdata, this);
				return true;
			} else {
				return false;
			}
		}
		else if (histValue instanceof String) {
			// プレフィックス指定のテンポラリファイル出力
			if (_chkOutToTemp.isVisible() && _chkOutToTemp.isEnabled()) {
				_itemdata.setOutToTempEnabled(true);
				_itemdata.setTempFilePrefix(histValue.toString());
				redisplayOutputValue();
				updateOutputControlStates(true, _itemdata.getShowFileAfterRun());
//				_chkOutToTemp.setSelected(true);
//				String strValue = histValue.toString();
//				_stcValue.setText(strValue);
//				getItem().setTempFilePrefix(strValue);
				ModuleArgValidator.getInstance().verifyOutputFileValue(_itemdata, this);
				return true;
			} else {
				return false;
			}
		}
		else if (histValue instanceof VirtualFile) {
			vfFile = ((VirtualFile)histValue).getAbsoluteFile().getNormalizedFile();
		}
		else if (histValue instanceof File) {
			vfFile = ModuleFileManager.fromJavaFile((File)histValue).getAbsoluteFile().getNormalizedFile();
		}
		
		if (vfFile != null) {
			_itemdata.setValue(vfFile);
			_itemdata.setOutToTempEnabled(false);
			_itemdata.setTempFilePrefix(null);
			redisplayOutputValue();
			updateOutputControlStates(false, _itemdata.getShowFileAfterRun());
//			_stcValue.setText(formatValue(vfFile));
			verifyValue();
			return true;
		}
		else {
			return false;
		}
	}
	
	protected boolean setStringHistoryData(Object histValue) {
		if (_itemdata != null) {
			String strValue = histValue.toString();
			_itemdata.setValue(strValue);
			_edtValue.setText(formatValue(strValue));
			ModuleArgValidator.getInstance().verifyStringValue(_itemdata, this);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 履歴の値をパブリッシュパラメータ(文字列)としてコンポーネントにセットする。
	 * @param histValue	履歴の値
	 * @return	コンポーネントの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	protected boolean setPublishHistoryData(Object histValue) {
		if (_itemdata != null) {
			String strValue = histValue.toString();
			_itemdata.setValue(strValue);
			_edtValue.setText(formatValue(strValue));
			ModuleArgValidator.getInstance().verifyPublishValue(_itemdata, this);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 履歴の値をサブスクライブパラメータ(文字列)としてコンポーネントにセットする。
	 * @param histValue	履歴の値
	 * @return	コンポーネントの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	protected boolean setSubscribeHistoryData(Object histValue) {
		if (_itemdata != null) {
			String strValue = histValue.toString();
			_itemdata.setValue(strValue);
			_edtValue.setText(formatValue(strValue));
			ModuleArgValidator.getInstance().verifySubscribeValue(_itemdata, this);
			return true;
		}
		else {
			return false;
		}
	}

	//------------------------------------------------------------
	// Implement javax.swing.event.DocumentListener interfaces
	//------------------------------------------------------------
	
	public void changedUpdate(DocumentEvent de) {
		onChangeStringValue(de);
	}

	public void insertUpdate(DocumentEvent de) {
		onChangeStringValue(de);
	}

	public void removeUpdate(DocumentEvent de) {
		onChangeStringValue(de);
	}

	//------------------------------------------------------------
	// Implement java.awt.event.FocusListener interfaces
	//------------------------------------------------------------
	
	public void focusGained(FocusEvent fe) {
		_handler.scrollClientComponentToVisible(fe.getComponent());
	}

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
		if (_itemdata != null) {
			if (ModuleArgType.STR == _itemdata.getType()) {
				_itemdata.setValue(strValue);
				ModuleArgValidator.getInstance().verifyStringValue(_itemdata, this);
			}
			else if (ModuleArgType.PUB == _itemdata.getType()) {
				_itemdata.setValue(strValue);
				ModuleArgValidator.getInstance().verifyPublishValue(_itemdata, this);
			}
			else if (ModuleArgType.SUB == _itemdata.getType()) {
				_itemdata.setValue(strValue);
				ModuleArgValidator.getInstance().verifySubscribeValue(_itemdata, this);
			}
			else if (ModuleArgType.OUT == _itemdata.getType()) {
				IMExecArgParam param = _itemdata.getParameterType();
				if (param instanceof MExecArgCsvFile) {
					_itemdata.setTempFilePrefix(strValue);
					ModuleArgValidator.getInstance().verifyOutputFileValue(_itemdata, this);
				}
			}
		}
	}
	
	protected void onSelectedShowFileAfterRunCheckBox() {
		if (!_ignoreShowFileAfterRunChangeEvent && _itemdata != null) {
			_itemdata.setShowFileAfterRun(_chkShowFileAfterRun.isSelected());
			_saveShowFileAfterRun = _chkShowFileAfterRun.isSelected();
		}
	}
	
	protected void onSelectedOutToTempCheckBox() {
		if (!_chkOutToTemp.isVisible())
			return;		// ignore event

		if (!_ignoreOutToTempChangeEvent && _itemdata != null && ModuleArgType.OUT == _itemdata.getType()) {
			boolean toEnable = _chkOutToTemp.isSelected();
			if (_itemdata != null && _itemdata.getOutToTempEnabled() != toEnable) {
				_itemdata.setOutToTempEnabled(toEnable);
				if (toEnable) {
					_saveShowFileAfterRun = _itemdata.getShowFileAfterRun();
					_itemdata.setShowFileAfterRun(true);
				}
				else {
					_itemdata.setShowFileAfterRun(_saveShowFileAfterRun);
				}
				updateOutputControlStates(toEnable, _itemdata.getShowFileAfterRun());
				verifyValue();
			}
		}
	}
	
	protected void onSelectedChooseViewingFile() {
		VirtualFile vfFile = _handler.getViewingFileOnEditor();
		if (vfFile == null)
			return;		// no viewing file
		if (!isEditable())
			return;		// no editable
		
		if (!vfFile.equals(_itemdata.getValue())) {
			_itemdata.setValue(vfFile);
			_stcValue.setText(formatValue(vfFile));
			verifyValue();
		}
	}
	
	protected void updateOutputControlStates(boolean outToTemp, boolean showFileAfterRun) {
		if (outToTemp) {
			_btnEdit.setEnabled(false);
			_stcValue.setVisible(false);
			_edtValue.setVisible(true);
		} else {
			_btnEdit.setEnabled(true);
			_edtValue.setVisible(false);
			_stcValue.setVisible(true);
		}
		_ignoreShowFileAfterRunChangeEvent = true;
		_chkShowFileAfterRun.setSelected(showFileAfterRun);
		_ignoreShowFileAfterRunChangeEvent = false;
	}

	protected void onButtonEdit() {
		if (!isEditable())
			return;
		
		IMExecArgParam param = _itemdata.getParameterType();
		RunnerFrame frame = (RunnerFrame)ModuleRunner.getInstance().getMainFrame();
		File orgRecFile = FileChooserManager.getRecommendedDirectory();
		FileChooserManager.setRecommendedDirectory(frame.getDataFileUserRootDirectory());
		File selFile = null;
		if (param instanceof MExecArgCsvFile) {
			selFile = FileChooserManager.chooseArgCsvFile(this, FileChooserManager.getInitialDocumentFile(_itemdata.getValue()));
		}
		else if (param instanceof MExecArgTextFile) {
			selFile = FileChooserManager.chooseArgTextFile(this, FileChooserManager.getInitialDocumentFile(_itemdata.getValue()));
		}
		else if (param instanceof MExecArgXmlFile) {
			selFile = FileChooserManager.chooseArgXmlFile(this, FileChooserManager.getInitialDocumentFile(_itemdata.getValue()));
		}
		else if (param instanceof MExecArgDirectory) {
			selFile = FileChooserManager.chooseArgDirectory(this, FileChooserManager.getInitialDocumentFile(_itemdata.getValue()));
		}
		else {
			selFile = FileChooserManager.chooseArgumentFile(this, FileChooserManager.getInitialDocumentFile(_itemdata.getValue()));
		}
		FileChooserManager.setRecommendedDirectory(orgRecFile);
		if (selFile != null) {
			FileChooserManager.setLastChooseDocumentFile(selFile);
			VirtualFile vfFile = ModuleFileManager.fromJavaFile(selFile);
			if (!vfFile.equals(_itemdata.getValue())) {
				_itemdata.setValue(vfFile);
				_stcValue.setText(formatValue(vfFile));
				verifyValue();
			}
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
	 * 引数属性のラベルを生成
	 * @return	生成された <code>JLabel</code> オブジェクト
	 */
	protected JLabel createArgTypeLabel() {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(1,5,1,5)));
		return label;
	}

	/**
	 * 引数説明のラベルを生成
	 * @return	生成された <code>JMultilineLabel</code> オブジェクト
	 */
	protected JMultilineLabel createDescriptionLabel() {
		JMultilineLabel label = new JMultilineLabel();
		return label;
	}

	/**
	 * 引数設定オプションのチェックボックスを生成
	 * @return	生成された <code>JCheckBox</code> オブジェクト
	 */
	protected JCheckBox createShowFileAfterRunCheckBox() {
		JCheckBox chk = new JCheckBox(RunnerMessages.getInstance().MExecDefEditDlg_Arg_ShowResultCsv);
		chk.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				onSelectedShowFileAfterRunCheckBox();
			}
		});
		return chk;
	}
	
	protected JButton createChooseViewingFileButton() {
		JButton btn = new JButton(RunnerMessages.getInstance().MExecDefEditDlg_Arg_UseViewingFile);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectedChooseViewingFile();
			}
		});
		return btn;
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
	protected JButton createEditButton() {
		JButton btn = CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonEdit();
			}
		});
		return btn;
	}

	/**
	 * 値編集用コンポーネントを生成
	 * @return	生成された <code>JTextArea</code> オブジェクト
	 */
	protected JTextArea createEditValueComponent() {
		JTextArea ta = new JTextArea();
		ta.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		ta.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(false);
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
		valuePanel.add(_chkShowFileAfterRun, gbc);
		valuePanel.add(_btnChooseViewingFile);
		valuePanel.add(new JLabel(" "), gbc);
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
		valuePanel.add(_btnEdit, gbc);
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
		this.add(_lblArgType, gbc);
		//--- desc
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = descInsets;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(_lblArgDesc, gbc);
		//--- value
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = valueInsets;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(valuePanel, gbc);
	}

	/**
	 * 指定されたパラメータで、引数属性のラベルの表示を更新する。
	 * @param visibleArgNo	引数番号を表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	protected void redisplayArgType(boolean visibleArgNo) {
		// 表示文字列
		String strLabel;
		ModuleArgType argtype;
		if (_itemdata != null) {
			argtype = _itemdata.getType();
			if (visibleArgNo) {
				// 引数番号を表示
				strLabel = String.format("($%d) %s", _itemdata.getArgNo(), argtype.typeName());
			} else {
				// 引数番号は非表示
				strLabel = argtype.typeName();
			}
		}
		else {
			// 引数情報は不明
			strLabel = "($?)";
			argtype = null;
		}
		
		// 背景色
		JLabel label = _lblArgType;
		switch (argtype) {
			case IN :
				label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
				break;
			case OUT :
				label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
				break;
			case STR :
				label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
				break;
			case PUB :
				label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_PUB);
				break;
			case SUB :
				label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_SUB);
				break;
			default :
				// 標準の背景色
				label.setBackground(UIManager.getColor("Label.background"));
		}
		// 文字列設定
		label.setText(strLabel);
	}

	/**
	 * 指定された文字列で、引数説明の表示を更新する。
	 */
	protected void redisplayArgDescription() {
		String desc = (_itemdata==null ? null : _itemdata.getDescription());
		_lblArgDesc.setText(desc==null ? "" : desc);
	}

	/**
	 * 指定された引数設定で、引数値の表示を更新する。
	 */
	protected void redisplayArgValue() {
		ModuleArgType argtype = getArgType();
		switch (argtype) {
			case IN :
				redisplayInputValue();
				break;
			case OUT :
				redisplayOutputValue();
				break;
			case STR :	// [STR]
			case PUB :	// [PUB] もコンポーネント制御は [STR] と同じ
			case SUB :	// [SUB] もコンポーネント制御は [STR] と同じ
				redisplayStringValue();
				break;
		}
	}

	/**
	 * 現在の引数設定で、[IN] 属性の引数値の表示を更新する。
	 */
	protected void redisplayInputValue() {
		assert (_itemdata != null);
		IMExecArgParam param = _itemdata.getParameterType();
		Object value = _itemdata.getValue();
		_chkShowFileAfterRun.setVisible(false);
		_chkOutToTemp.setVisible(false);
		_edtValue.setVisible(false);
		_stcValue.setVisible(true);
		if (value instanceof ModuleArgID) {
			_btnChooseViewingFile.setVisible(false);
			_btnChooseViewingFile.setEnabled(false);
			_btnEdit.setVisible(false);
			_stcValue.setText(formatValue(value));
		}
		else if (param instanceof MExecArgTempFile) {
			_btnChooseViewingFile.setVisible(false);
			_btnChooseViewingFile.setEnabled(false);
			_btnEdit.setVisible(false);
			if (value == null) {
				_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary);
			} else {
				_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary + " (" + value.toString() + ")");
			}
		}
		else if (param instanceof MExecArgCsvFile) {
			if (isEditable()) {
				_btnEdit.setVisible(true);
				_btnChooseViewingFile.setVisible(true);
				_btnChooseViewingFile.setEnabled(_handler.getViewingFileOnEditor()!=null);
				_btnEdit.setEnabled(true);
			} else {
				_btnChooseViewingFile.setVisible(false);
				_btnEdit.setVisible(false);
			}
			_stcValue.setText(formatValue(value));
		}
		else {
			_btnChooseViewingFile.setVisible(false);
			_btnChooseViewingFile.setEnabled(false);
			if (isEditable()) {
				_btnEdit.setVisible(true);
				_btnEdit.setEnabled(true);
			} else {
				_btnEdit.setVisible(false);
			}
			_stcValue.setText(formatValue(value));
		}
	}
	
	/**
	 * 現在の引数設定で、[OUT] 属性の引数値の表示を更新する。
	 */
	protected void redisplayOutputValue() {
		assert (_itemdata != null);
		IMExecArgParam param = _itemdata.getParameterType();
		Object value = _itemdata.getValue();
		_btnChooseViewingFile.setVisible(false);
		_btnChooseViewingFile.setEnabled(false);
		_edtValue.setVisible(false);
		_stcValue.setVisible(true);
		if (param instanceof MExecArgTempFile) {
			_chkShowFileAfterRun.setVisible(false);
			_chkOutToTemp.setVisible(false);
			_btnEdit.setVisible(false);
			if (value == null) {
				_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary);
			} else {
				_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary + " (" + value.toString() + ")");
			}
		}
		else if (param instanceof MExecArgCsvFile) {
			_chkShowFileAfterRun.setVisible(true);
			_chkShowFileAfterRun.setEnabled(true);
			_chkOutToTemp.setVisible(true);
			_chkShowFileAfterRun.setSelected(_itemdata.getShowFileAfterRun());
			_chkOutToTemp.setSelected(_itemdata.getOutToTempEnabled());
			if (isEditable()) {
				updateOutputControlStates(_itemdata.getOutToTempEnabled(), _itemdata.getShowFileAfterRun());
			}
			else {
				_btnEdit.setVisible(false);
				_chkOutToTemp.setEnabled(false);
			}
			_edtValue.setText(_itemdata.getTempFilePrefix());
			_stcValue.setText(formatValue(value));
		}
		else {
			_chkShowFileAfterRun.setVisible(false);
			_chkOutToTemp.setVisible(false);
			if (isEditable()) {
				_btnEdit.setVisible(true);
				_btnEdit.setEnabled(true);
			} else {
				_btnEdit.setVisible(false);
			}
			_stcValue.setText(formatValue(value));
		}
	}
	
	/**
	 * 現在の引数設定で、[STR] 属性の引数値の表示を更新する。
	 */
	protected void redisplayStringValue() {
		// CheckBox は非表示
		_chkShowFileAfterRun.setVisible(false);
		_chkOutToTemp.setVisible(false);
		_btnChooseViewingFile.setVisible(false);
		_btnChooseViewingFile.setEnabled(false);
		
		// 編集ボタンは非表示
		_btnEdit.setVisible(false);
		
		// 表示テキストを設定
		if (isEditable()) {
			_stcValue.setVisible(false);
			_edtValue.setVisible(true);
			_edtValue.setText(formatValue(_itemdata.getValue()));
		} else {
			_edtValue.setVisible(false);
			_stcValue.setVisible(true);
			_stcValue.setText(formatValue(_itemdata.getValue()));
		}
	}

	/**
	 * 引数値として表示するテキストの整形
	 * @param value	表示する値
	 * @return	値の文字列表現
	 */
	protected String formatValue(Object value) {
		if (value instanceof ModuleArgID) {
			return _handler.getDisplayModuleArgID((ModuleArgID)value);
		}
		else if (value instanceof VirtualFile) {
			VirtualFile vfValue = (VirtualFile)value;
			VirtualFile vfBase = _handler.getBasePath();
			if (vfBase != null && vfValue.isDescendingFrom(vfBase)) {
				return vfValue.relativePathFrom(vfBase, Files.CommonSeparatorChar);
			} else {
				return vfValue.toString();
			}
		}
		else {
			return (value==null ? "" : value.toString());
		}
	}

	/**
	 * アイテムへのファイルのドロップを受け付けるリスナー
	 */
	protected class DataFileDropTargetListener extends DropTargetAdapter
	{
		private boolean _canImport = false;
		
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			_canImport = false;
			
			// 編集可能かをチェック
			if (!isEditable()) {
				// 編集不可
				dtde.rejectDrag();
				return;
			}
			
			// ファイルを受け付けるアイテムかをチェック
			ModuleArgType argtype = getArgType();
			if (argtype!=ModuleArgType.IN && argtype!=ModuleArgType.OUT) {
				// ファイル形式の引数ではない
				dtde.rejectDrag();
				return;
			}
			
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
			// 編集可能かをチェック
			if (!isEditable()) {
				// 編集不可
				dtde.rejectDrop();
				return;
			}
			
			// ファイルを受け付けるアイテムかをチェック
			ModuleArgType argtype = getArgType();
			if (argtype!=ModuleArgType.IN && argtype!=ModuleArgType.OUT) {
				// ファイル形式の引数ではない
				dtde.rejectDrop();
				return;
			}
			
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
				if (!vfTarget.equals(_itemdata.getValue())) {
					_itemdata.setValue(vfTarget);
					_stcValue.setText(formatValue(vfTarget));
					verifyValue();
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
			
			// java File
			{
				File f = getTargetJavaFile(transfer);
				if (f != null) {
					return ModuleFileManager.fromJavaFile(f);
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
						if (!Strings.endsWithIgnoreCase(f.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
							vfTarget = f;
							break;
						}
					}
				}
			}
			catch (Throwable ex) {
				// ignore exception
				//AppLogger.error("MExecArgsEditPane.MExecArgItemPane.DataFileDropTarget#getTargetVirtualFile() : Failed to drop to editor.", ex);
			}
			
			return vfTarget;
		}
		
		protected File getTargetJavaFile(Transferable transfer) {
			if (!transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				return null;

			File fTarget = null;
			try {
				// 先頭のファイルのみ受け入れる
				//--- 設定ファイル(*.prefs) を除くパスのみを取得
				File f;
				List<?> filelist = (List<?>)transfer.getTransferData(DataFlavor.javaFileListFlavor);
				for (Object elem : filelist) {
					if (elem instanceof File) {
						f = (File)elem;
						if (!Strings.endsWithIgnoreCase(f.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
							fTarget = f;
							break;
						}
					}
				}
			}
			catch (Throwable ex) {
				// ignore exception
				//AppLogger.error("MExecArgsEditPane.MExecArgItemPane.DataFileDropTarget#getTargetJavaFile() : Failed to drop to editor.", ex);
			}
			
			return fTarget;
		}
	}
}
