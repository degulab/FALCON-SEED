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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CharsetChooserPanel.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ssac.aadl.common.CommonMessages;
import ssac.util.Objects;
import ssac.util.Strings;

/**
 * テキストエンコーディングとする文字セット名を選択するパネル。
 * このパネルには、標準文字セット（デフォルト）とユーザー指定文字セットを
 * 選択するラジオボタンと、文字セット名を選択するコンボボックスから構成される。
 * <p>
 * このクラスでは、文字セットを {@link java.nio.charset.Charset} オブジェクトで
 * 保持するが、文字セットの表示名は
 * {@link ssac.util.swing.JCharsetComboBox#getAvailableCharsetName(Charset)} メソッドにより取得する。
 * <p>
 * コンストラクタもしくは {@link #setCaption(String)} によりキャプションが指定された場合、
 * 指定されたキャプションでタイトルボーダーを表示する。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class CharsetChooserPanel extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このパネルのキャプション **/
	private String				_panelCaption;
	/** デフォルトの文字セット **/
	private Charset			_defCharset;

	private ButtonGroup		_bgCharset;
	/** デフォルト文字セット用ラジオボタン **/
	private JRadioButton		_rbDefaultCharset;
	/** カスタム文字セット用ラジオボタン **/
	private JRadioButton		_rbCustomCharset;
	/** デフォルト文字セット用ラベル **/
	private JLabel				_lblDefaultCharset;
	/** カスタム文字セット用コンボボックス **/
	private JCharsetComboBox	_cmbCustomCharset;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CharsetChooserPanel() {
		this(null, (Charset)null);
	}
	
	public CharsetChooserPanel(String caption) {
		this(caption, (Charset)null);
	}
	
	public CharsetChooserPanel(String caption, String defCharsetName) {
		super(new GridBagLayout());
		setCaptionBorder(caption);
		this._panelCaption = caption;
		initialComponent();
		setDefaultCharsetName(defCharsetName);
	}
	
	public CharsetChooserPanel(String caption, Charset defCharset) {
		super(new GridBagLayout());
		setCaptionBorder(caption);
		this._panelCaption = caption;
		initialComponent();
		setDefaultCharset(defCharset);
	}
	
	private final void initialComponent() {
		// setup Objects
		//--- Radio buttons
		_rbDefaultCharset = new JRadioButton(CommonMessages.getInstance().labelDefault);
		_rbCustomCharset  = new JRadioButton(CommonMessages.getInstance().labelCustom);
		_bgCharset = new ButtonGroup();
		_bgCharset.add(_rbDefaultCharset);
		_bgCharset.add(_rbCustomCharset);
		//--- Labels
		_lblDefaultCharset = new JLabel(_rbDefaultCharset.getText());
		//--- Combo box
		_cmbCustomCharset = new JCharsetComboBox();
		
		// Layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(1,1,1,1);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		//--- radio button
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(_rbDefaultCharset, gbc);
		gbc.gridy = 1;
		this.add(_rbCustomCharset, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		this.add(_lblDefaultCharset, gbc);
		//--- combo
		gbc.gridy = 1;
		this.add(_cmbCustomCharset, gbc);
		
		// Actions
		//--- radio button
		ActionListener rbal = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onRadioButtonCharset(ae);
			}
		};
		_rbDefaultCharset.addActionListener(rbal);
		_rbCustomCharset .addActionListener(rbal);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このパネルのキャプションを取得する。
	 * @return	キャプションとして設定されている文字列を返す。
	 * 			キャプションが設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getCaption() {
		return _panelCaption;
	}

	/**
	 * このパネルのキャプションを設定する。
	 * @param caption	キャプションとして指定する文字列。<tt>null</tt> の場合はキャプションを設定しない。
	 */
	public void setCaption(String caption) {
		if (!Objects.isEqual(_panelCaption, caption)) {
			setCaptionBorder(caption);
			_panelCaption = caption;
		}
	}

	/**
	 * デフォルト文字セットを取得する。
	 * @return	デフォルト文字セット
	 */
	public Charset getDefaultCharset() {
		return _defCharset;
	}

	/**
	 * デフォルト文字セット名を取得する。
	 * @return	デフォルト文字セット名
	 */
	public String getDefaultCharsetName() {
		return JCharsetComboBox.getAvailableCharsetName(_defCharset);
	}

	/**
	 * デフォルト文字セットを設定する。
	 * 指定された文字セットが選択候補に含まれていない場合は、
	 * プラットフォーム標準の文字セットを設定する。
	 * @param charset	文字セット
	 */
	public void setDefaultCharset(Charset charset) {
		if (JCharsetComboBox.isAvailableCharset(charset)) {
			_defCharset = charset;
		} else {
			_defCharset = JCharsetComboBox.getDefaultCharset();
		}
		setDefaultCharsetLabel(_defCharset);
	}

	/**
	 * デフォルト文字セットを設定する。
	 * 指定された文字セット名が選択候補に含まれていない場合は、
	 * プラットフォーム標準の文字セットを設定する。
	 * このメソッドは、文字セット名のエイリアスにも対応する。
	 * @param charsetName	文字セット名
	 */
	public void setDefaultCharsetName(String charsetName) {
		Charset cs = JCharsetComboBox.getAvailableCharset(charsetName);
		if (cs != null) {
			_defCharset = cs;
		} else {
			_defCharset = JCharsetComboBox.getDefaultCharset();
		}
		setDefaultCharsetLabel(_defCharset);
	}

	/**
	 * 現在選択されているカスタム文字セットを返す。
	 * @return	文字セット
	 */
	public Charset getSelectedCustomCharset() {
		return _cmbCustomCharset.getSelectedCharset();
	}

	/**
	 * 現在選択されているカスタム文字セット名を返す。
	 * @return	文字セット名
	 */
	public String getSelectedCustomCharsetName() {
		return _cmbCustomCharset.getSelectedCharsetName();
	}

	/**
	 * 指定された文字セットで、カスタム文字セット候補を選択する。
	 * 候補に含まれない文字セットの場合、デフォルト文字セットを選択する。
	 * @param charset	選択する文字セット
	 */
	public void setSelectedCustomCharset(Charset charset) {
		if (JCharsetComboBox.isAvailableCharset(charset)) {
			_cmbCustomCharset.setSelectedCharset(charset);
		} else {
			_cmbCustomCharset.setSelectedCharset(_defCharset);
		}
	}

	/**
	 * 指定された文字セット名で、カスタム文字セット候補を選択する。
	 * 候補に含まれない文字セット名の場合、デフォルト文字セットを選択する。
	 * このメソッドは、文字セット名のエイリアスにも対応する。
	 * @param charsetName	選択する文字セット名
	 */
	public void setSelectedCustomCharsetName(String charsetName) {
		setSelectedCustomCharset(JCharsetComboBox.getAvailableCharset(charsetName));
	}

	/**
	 * デフォルトが選択されているかを判定する。
	 * @return	デフォルトが選択されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isDefaultSelected() {
		return _rbDefaultCharset.isSelected();
	}

	/**
	 * カスタムが選択されているかを判定する。
	 * @return	カスタムが選択されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isCustomSelected() {
		return _rbCustomCharset.isSelected();
	}

	/**
	 * デフォルト文字セット用ラジオボタンを選択状態とし、関連する表示を更新する。
	 */
	public void selectDefault() {
		_rbDefaultCharset.setSelected(true);
		updateDisplay();
	}

	/**
	 * カスタム文字セット用ラジオボタンを選択状態とし、関連する表示を更新する。
	 */
	public void selectCustom() {
		_rbCustomCharset.setSelected(true);
		updateDisplay();
	}

	/**
	 * 指定されたパラメータで、表示内容を更新する。
	 * @param customSelected	カスタム文字セット用ラジオボタンを選択状態とする場合は <tt>true</tt>、デフォルトを選択状態とする場合は <tt>false</tt>
	 * @param defaultCharset	デフォルト文字セットに指定する文字セット
	 * @param customCharset		カスタム文字セット候補から選択状態とする文字セット
	 */
	public void setupDisplay(boolean customSelected, Charset defaultCharset, Charset customCharset) {
		setDefaultCharset(defaultCharset);
		setSelectedCustomCharset(customCharset);
		if (customSelected) {
			selectCustom();
		} else {
			selectDefault();
		}
	}
	
	/**
	 * 指定されたパラメータで、表示内容を更新する。
	 * このメソッドは、文字セット名のエイリアスにも対応する。
	 * @param customSelected	カスタム文字セット用ラジオボタンを選択状態とする場合は <tt>true</tt>、デフォルトを選択状態とする場合は <tt>false</tt>
	 * @param defaultCharset	デフォルト文字セットに指定する文字セット名
	 * @param customCharset		カスタム文字セット候補から選択状態とする文字セット名
	 */
	public void setupDisplay(boolean customSelected, String defaultCharsetName, String customCharsetName) {
		setDefaultCharsetName(defaultCharsetName);
		setSelectedCustomCharsetName(customCharsetName);
		if (customSelected) {
			selectCustom();
		} else {
			selectDefault();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ラジオボタンの状態により、表示を更新する。
	 */
	protected void updateDisplay() {
		boolean flgEnable = _rbCustomCharset.isSelected();
		_cmbCustomCharset.setEnabled(flgEnable);
	}

	/**
	 * 指定されたキャプションでボーダーを設定する。
	 * @param caption	キャプションとして表示する文字列。<tt>null</tt> の場合はボーダーを表示しない。
	 */
	protected void setCaptionBorder(String caption) {
		if (caption != null) {
			Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border bro = BorderFactory.createTitledBorder(bri, caption, TitledBorder.LEFT, TitledBorder.TOP);
			setBorder(bro);
		} else {
			setBorder(null);
		}
	}

	/**
	 * 指定された文字セットに対応する表示名をデフォルト文字セット名ラベルに設定する。
	 * @param charset	文字セット
	 */
	protected void setDefaultCharsetLabel(Charset charset) {
		setDefaultCharsetLabel(JCharsetComboBox.getAvailableCharsetName(charset));
	}

	/**
	 * 指定された文字列をデフォルト文字セット名ラベルに設定する。
	 * @param charsetName	文字セット名
	 */
	protected void setDefaultCharsetLabel(String charsetName) {
		if (!Strings.isNullOrEmpty(charsetName)) {
			_lblDefaultCharset.setText("(" + charsetName + ")");
		} else {
			_lblDefaultCharset.setText("(Default)");
		}
	}

	/**
	 * ラジオボタンが押されたときのアクション
	 * @param ae	アクションイベント
	 */
	protected void onRadioButtonCharset(ActionEvent ae) {
		updateDisplay();
	}
}
