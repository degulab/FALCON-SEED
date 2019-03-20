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
 * @(#)AbFilterArgValuesEditPane.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbFilterArgValuesEditPane.java	2.0.0	2012/10/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.FilterError;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.JMaskedNumberSpinner;
import dtalge.util.Strings;

/**
 * フィルタの実行時の引数値を設定するためのユーザーインタフェース。
 * このクラスは基本機能と共通インタフェースのみを定義する抽象クラスである。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public abstract class AbFilterArgValuesEditPane extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * データモデル
	 */
	protected IFilterValuesEditModel	_model;

	/**
	 * 履歴ラベル
	 */
	private JLabel		_lblHistory;
	/**
	 * 履歴消去ボタン
	 */
	private JButton	_btnClearHistory;
	/**
	 * 履歴番号
	 */
	private JMaskedNumberSpinner	_spnHistoryNo;
	/** 履歴番号変更イベントを無視するフラグ **/
	private boolean	_ignoreHistoryNoChangeEvent = false;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public AbFilterArgValuesEditPane() {
		super(new BorderLayout());
	}
	
	public void initialComponent() {
		this._model = createDefaultDataModel();
		
		// add sub components
		this.add(createArgValuesEditPanel(), BorderLayout.CENTER);
		this.add(createArgValuesTitlePanel(), BorderLayout.NORTH);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントの表示を更新する。
	 */
	abstract public void refreshDisplay();

	/**
	 * このコンポーネントに設定されているデータモデルを返す。
	 */
	public IFilterValuesEditModel getDataModel() {
		return _model;
	}

	/**
	 * このコンポーネントに新しいデータモデルを設定する。
	 * <em>newModel</em> がすでに設定されているデータモデルと等しいと判断された場合、
	 * このメソッドではデータモデルの置き換えは行わない。
	 * @param newModel	新しいモデルを設定する。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void setDataModel(IFilterValuesEditModel newModel) {
		if (!newModel.equals(_model)) {
			IFilterValuesEditModel oldModel = _model;
			_model = newModel;
			onChangedDataModel(oldModel, newModel);
		}
	}

	/**
	 * このコンポーネントに設定された、エディタビューで表示されているファイルの抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	abstract public VirtualFile getViewingFileOnEditor();

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 */
	abstract public void setViewingFileOnEditor(VirtualFile newFile);

	/**
	 * 主コンポーネントの先頭が表示されるようにスクロールする。
	 */
	abstract public void scrollTopToVisible();

	/**
	 * 主コンポーネントの終端が表示されるようにスクロールする。
	 */
	abstract public void scrollBottomToVisible();
	
	/**
	 * 主コンポーネントの指定された位置のアイテムが表示されるようにスクロールする。
	 * ただし、指定された位置のアイテムが非表示の場合や、インデックスが
	 * 無効な場合、このメソッドはなにもしない。
	 * @param index	アイテムの位置を示すインデックス
	 */
	abstract public void scrollItemToVisible(int index);

	/**
	 * このコンポーネントのデータモデルにエラーがある場合に <tt>true</tt> を返す。
	 * @return	エラーがある場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasError() {
		return (getFirstErrorData() != null);
	}

	/**
	 * このコンポーネントのデータモデルから、エラーを保持する最初のデータオブジェクト取得する。
	 * モジュールデータのエラー、各引数のエラーの順に取得し、エラーが存在しない場合は <tt>null</tt> を返す。
	 * @return	エラーを持つ最初のデータオブジェクトを返す。エラーが存在しない場合は <tt>null</tt>
	 */
	public Object getFirstErrorData() {
		if (_model == null)
			return null;
		ModuleRuntimeData moduledata = _model.getModuleData();
		if (moduledata == null)
			return null;
		
		// モジュールエラーの取得(@since 3.1.0)
		Object errdata = moduledata.getModuleError();
		if (errdata != null) {
			return moduledata;
		}
		
		// モジュール引数のエラー
		for (ModuleArgConfig argdata : moduledata) {
			errdata = argdata.getUserData();
			if (errdata instanceof FilterError) {
				return argdata;
			}
		}
		
		// エラーなし
		return null;
	}

	/**
	 * 指定された引数インデックスに対応する箇所に、入力フォーカスを設定する。
	 * インデックスが適切ではない場合、このコンポーネントにフォーカスを設定する。
	 * @param argIndex	引数インデックス
	 */
	abstract public void setFocusToArgument(int argIndex);

	//------------------------------------------------------------
	// Internal events
	//------------------------------------------------------------
	
	/**
	 * このコンポーネントのデータモデルが変更されたときに呼び出される。
	 * このメソッドが呼び出された時点で、<code>getDataModel()</code> が返すインスタンスは、
	 * <em>newModel</em> と同一のものとなっている。
	 */
	protected void onChangedDataModel(IFilterValuesEditModel oldModel, IFilterValuesEditModel newModel) {
		
	}

	/**
	 * このモジュールの履歴をすべて消去するボタン押下時のアクションハンドラ
	 */
	protected void onHistoryAllClearButton() {
		if (_model.isArgsHistoryEnabled()) {
			MExecDefHistory history = _model.getArgsHistory();
			if (history != null && !history.isHistoryEmpty()) {
				history.clearHistory();
				commitArgsHistory(history);
				//--- 履歴コントロールの再表示
				SpinnerNumberModel model = (SpinnerNumberModel)_spnHistoryNo.getModel();
				model.setMinimum(0);
				model.setValue(0);
				_lblHistory.setEnabled(false);
				_btnClearHistory.setEnabled(false);
				_spnHistoryNo.setEnabled(false);
			}
		}
	}

	/**
	 * 履歴番号入力テキストボックスの内容が変更されたときのアクションハンドラ
	 */
	protected void onHistoryNumberChanged() {
		MExecDefHistory history = _model.getArgsHistory();
		if (history == null || history.isHistoryEmpty()) {
			return;
		}
		
		// 履歴の取得
		if (!_ignoreHistoryNoChangeEvent) {
			Object value = _spnHistoryNo.getValue();
			if (!(value instanceof Number)) {
				return;		// not a value type
			}
			int historyNo = ((Number)value).intValue();
			if (historyNo <= 0 || historyNo > history.getNumHistory()) {
				return;
			}
			ArrayList<ModuleArgData> hitem = history.getHistory(historyNo-1);

			// TODO : 履歴の適用
//			//--- 表示位置の維持
//			final Rectangle rc = _argsPane.getVisibleRect();
//			_argsPane.setHistory(hitem);
//			SwingUtilities.invokeLater(new Runnable() {
//				public void run() {
//					_argsPane.scrollRectToVisible(rc);
//				}
//			});
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected void storeArgsHistory(final MExecDefHistory history, ModuleRuntimeData data) {
		if (history == null)
			return;		// 履歴の保存先が存在しない
		if (data.isEmptyArgument())
			return;		// 引数が存在しない
		
		// 履歴データを作成
		int numArgs = data.getArgumentCount();
		ArrayList<ModuleArgData> hitem = new ArrayList<ModuleArgData>(numArgs);
		for (int index = 0; index < numArgs; index++) {
			IModuleArgConfig arg = data.getArgument(index);
			ModuleArgData hdata = new ModuleArgData(arg.getType(), arg.getDescription());
			if (ModuleArgType.OUT == arg.getType()) {
				if (arg.getOutToTempEnabled()) {
					String tempprefix = arg.getTempFilePrefix();
					if (!Strings.isNullOrEmpty(tempprefix)) {
						// プレフィックス付きのテンポラリ出力指定
						hdata.setValue(tempprefix);
					} else {
						// プレフィックスなしのテンポラリ出力指定
						hdata.setValue(MExecArgTempFile.instance);
					}
				} else {
					hdata.setValue(arg.getValue());
				}
			}
			else {
				hdata.setValue(arg.getValue());
			}
			hitem.add(hdata);
		}
		
		// 新しい履歴を保存
		history.addHistory(hitem);
		commitArgsHistory(history);
	}
	
	static protected void commitArgsHistory(final MExecDefHistory history) {
		history.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());
		try {
			history.commit();
		}
		catch (Throwable ex) {
			AppLogger.error("Failed to save to MED History file.\nFile : \"" + history.getVirtualPropertyFile().getAbsolutePath() + "\"", ex);
		}
	}

	/**
	 * このコンポーネント標準のデータモデルを生成する。
	 */
	protected IFilterValuesEditModel createDefaultDataModel() {
		return new EmptyFilterValuesEditModel();
	}

	/**
	 * 引数値編集パネルのタイトル部を生成する。
	 * このタイトル部には、引数値の履歴を設定するためのコントロールも含まれる。
	 * @return	引数値編集パネルのタイトル部となるコンポーネント
	 */
	protected JPanel createArgValuesTitlePanel() {
		// create components
		JLabel lblTitle = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Args);
		_lblHistory = new JLabel(RunnerMessages.getInstance().MExecArgsEditDlg_Label_History);
		_btnClearHistory = CommonResources.createIconButton(CommonResources.ICON_CONSOLE_CLEAR, RunnerMessages.getInstance().MEXecArgsEditDlg_Tooltip_HistoryAllClear);
		_spnHistoryNo = new JMaskedNumberSpinner("#0", 1, 1, AppSettings.getInstance().getHistoryMaxLengthLimit(), 1);
		
		// 履歴の初期化
		_spnHistoryNo = new JMaskedNumberSpinner("#0", 0, 0, 0, 1);
		_spnHistoryNo.setVisible(false);
		_spnHistoryNo.setEnabled(false);
		_btnClearHistory.setVisible(false);
		_btnClearHistory.setEnabled(false);
		_lblHistory.setVisible(false);
		_lblHistory.setEnabled(false);
		//--- setup minimum size;
		Dimension dim = _spnHistoryNo.getPreferredSize();
		if (dim.width < 80) {
			dim.width = 80;
		}
		_spnHistoryNo.setPreferredSize(dim);
		_spnHistoryNo.setMinimumSize(dim);
		
		// layout
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill   = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- title
		pnl.add(lblTitle, gbc);
		//--- dummy
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(new JLabel(), gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		//--- label
		gbc.gridx++;
		pnl.add(_lblHistory, gbc);
		//--- clear button
		gbc.gridx++;
		pnl.add(_btnClearHistory, gbc);
		//--- field
		gbc.gridx++;
		pnl.add(_spnHistoryNo, gbc);
		
		// actions
		//--- history clear button
		_btnClearHistory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				AbFilterArgValuesEditPane.this.onHistoryAllClearButton();
			}
		});
		//--- history spin action
		_spnHistoryNo.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				AbFilterArgValuesEditPane.this.onHistoryNumberChanged();
			}
		});
		
		return pnl;
	}

	/**
	 * 引数値編集パネルを生成する。
	 * @return	<code>JComponent</code> オブジェクト
	 */
	abstract protected JComponent createArgValuesEditPanel();

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
