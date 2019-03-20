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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FilterManager.java	2.0.0	2012/10/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import ssac.util.io.VirtualFile;

/**
 * 標準のフィルタ実行時引数値編集コンポーネント。
 * このコンポーネントは、スクロールバーをベースとするコンポーネントとなる。
 * 
 * @version 2.0.0	2012/10/10
 * @since 2.0.0
 */
public class FilterArgValuesEditPane extends AbFilterArgValuesEditPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 引数設定ペイン **/
	private MExecArgsConfigPane	_argsPane;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FilterArgValuesEditPane() {
		super();
	}

	@Override
	public void initialComponent() {
		super.initialComponent();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントの表示を更新する。
	 */
	@Override
	public void refreshDisplay() {
		// No entry
	}

	/**
	 * このコンポーネントに設定された、エディタビューで表示されているファイルの抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getViewingFileOnEditor() {
		return getArgsEditPane().getViewingFileOnEditor();
	}

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 */
	public void setViewingFileOnEditor(VirtualFile newFile) {
		getArgsEditPane().setViewingFileOnEditor(newFile);
	}

	/**
	 * 主コンポーネントの先頭が表示されるようにスクロールする。
	 */
	public void scrollTopToVisible() {
		getArgsEditPane().scrollTopToVisible();
	}

	/**
	 * 主コンポーネントの終端が表示されるようにスクロールする。
	 */
	public void scrollBottomToVisible() {
		getArgsEditPane().scrollBottomToVisible();
	}
	
	/**
	 * 主コンポーネントの指定された位置のアイテムが表示されるようにスクロールする。
	 * ただし、指定された位置のアイテムが非表示の場合や、インデックスが
	 * 無効な場合、このメソッドはなにもしない。
	 * @param index	アイテムの位置を示すインデックス
	 */
	public void scrollItemToVisible(int index) {
		getArgsEditPane().scrollItemToVisible(index);
	}

	/**
	 * 指定された引数インデックスに対応する箇所に、入力フォーカスを設定する。
	 * インデックスが適切ではない場合、このコンポーネントにフォーカスを設定する。
	 * @param argIndex	引数インデックス
	 */
	public void setFocusToArgument(int argIndex) {
		// TODO: フォーカス設定
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected MExecArgsConfigPane getArgsEditPane() {
		return _argsPane;
	}

	/**
	 * 引数値編集パネルを生成する。
	 * @return	<code>JComponent</code> オブジェクト
	 */
	protected JComponent createArgValuesEditPanel() {
		_argsPane = new MExecArgsConfigPane();

		JScrollPane scArgs = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scArgs.setViewportView(_argsPane);
		scArgs.setBackground(this.getBackground());
		
		return scArgs;
	}
	
	protected MExecArgsConfigPane createArgsEditPane() {
		MExecArgsConfigPane pane = new MExecArgsConfigPane();
		return pane;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
