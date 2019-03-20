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
 * @(#)AbCsvFileConfigDialog.java	1.20	2012/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbCsvFileConfigDialog.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.nio.csv.CsvUtil;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.JCharsetComboBox;
import ssac.util.swing.JStaticMultilineTextPane;

/**
 * CSVファイルの詳細設定ダイアログの共通機能を実装する抽象クラス
 * 
 * @version 1.20	2012/03/16
 * @since 1.17
 */
public abstract class AbCsvFileConfigDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 320);

	static protected final String SUBKEY_HEADERLINE		= ".headerline";
	static protected final String SUBKEY_AUTODETECTTYPE	= ".auteDetectDataType";
	static protected final String SUBKEY_ENCODING			= ".encoding";
	static protected final String SUBKEY_DELIMITER		= ".delimiter";
	static protected final String SUBKEY_QUOTECHAR		= ".quotechar";
	static protected final String SUBKEY_ALLOW_ESCAPE		= ".allowEscape";
	static protected final String SUBKEY_ALLOW_MULTILINE	= ".allowMultiline";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このコンポーネントをデバッグモードで動作させることを示すフラグ **/
	static private boolean	_flgDebug = false;

	/** このコンポーネント標準のCSV設定 **/
	private CsvParameters		_defCsvParams;

	/** 対象のファイル **/
	private final File			_fTargetFile;

	/** フィールド区切り文字を選択するコンボボックス **/
	private JComboBox			_cmbDelimiterChar;
	/** エスケープ文字を選択するコンボボックス **/
	private JComboBox			_cmbEscapeChar;
	/** 改行文字はエスケープしないことを選択するチェックボックス **/
	private JCheckBox			_chkDenyMultiline;
	/** 第１行をヘッダ行とすることを選択するチェックボックス **/
	private JCheckBox			_chkUseHeaderLine;
	/** データ型の自動判別を行うことを選択するチェックボックス **/
	private JCheckBox			_chkAutoDetectType;
	/** ファイル・エンコーディングを選択するコンボボックス **/
	private JCharsetComboBox	_cmbEncoding;
	/** 選択されたファイルのパスを表示するラベル **/
	private JTextComponent		_lblFilePath;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbCsvFileConfigDialog(Frame owner, File targetFile, String title, boolean modal) {
		super(owner, title, modal);
		if (targetFile == null)
			throw new NullPointerException("The specified file is null.");
		this._fTargetFile = targetFile;
	}
	
	public AbCsvFileConfigDialog(Dialog owner, File targetFile, String title, boolean modal) {
		super(owner, title, modal);
		if (targetFile == null)
			throw new NullPointerException("The specified file is null.");
		this._fTargetFile = targetFile;
	}

	@Override
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// initial component
		super.initialComponent();
		
		// 設定情報の反映
		restoreConfiguration();
		restoreCsvConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 設定対象のファイルを取得する。
	 * @return	設定対象のファイルを示す抽象パス
	 */
	public File getTargetFile() {
		return _fTargetFile;
	}

	/**
	 * 現在選択されているエンコーディングの文字セットを取得する。
	 * @return	文字セット
	 */
	public Charset getEncodingCharset() {
		return getEncodingComboBox().getSelectedCharset();
	}

	/**
	 * 現在選択されているエンコーディングの文字セット名を取得する。
	 * @return	文字セット名
	 */
	public String getEncodingCharsetName() {
		return getEncodingComboBox().getSelectedCharsetName();
	}

	/**
	 * 指定された文字セットを選択する。
	 * 無効な文字セットが指定された場合は、標準の文字セットを選択する。
	 * @param charset	文字セット
	 */
	public void setEncodingCharset(Charset charset) {
		JCharsetComboBox cmb = getEncodingComboBox();
		cmb.setSelectedCharset(charset);
		if (cmb.getSelectedIndex() < 0) {
			cmb.setSelectedDefaultCharset();
		}
	}

	/**
	 * 指定された文字セット名を選択する。
	 * このメソッドは、文字セット名のエイリアスにも対応する。
	 * 無効な文字セット名が指定された場合は、標準の文字セット名を選択する。
	 * @param charsetName	文字セット名
	 */
	public void setEncodingCharsetName(String charsetName) {
		JCharsetComboBox cmb = getEncodingComboBox();
		cmb.setSelectedCharsetName(charsetName);
		if (cmb.getSelectedIndex() < 0) {
			cmb.setSelectedDefaultCharset();
		}
	}

	/**
	 * 現在のCSV設定を取得する。
	 * @return	現在のCSV設定を格納する <code>CsvParameters</code> オブジェクト
	 */
	public CsvParameters getCsvParameters() {
		CsvParameters params = new CsvParameters();
		params.setDelimiterChar(getDelimiterChar());
		params.setQuoteChar(getQuoteChar());
		params.setQuoteEscapeEnabled(isAllowEscapeByChar());
		params.setAllowMultiLineField(isAllowMultiline());
		params.setUseHeaderLine(getUseHeaderLine());
		params.setAutoDetectDataType(getAutoDetectDataType());
		return params;
	}

	/**
	 * CSV設定を、指定のパラメータに設定する。
	 * @param params	<code>CsvParameters</code> オブジェクト
	 */
	public void setCsvParameters(CsvParameters params) {
		setDelimiterChar(params.getDelimiterChar());
		setEscapeByChar(params.isQuoteEscapeEnabled(), params.getQuoteChar());
		setAllowMultiline(params.isAllowMutiLineField());
		setUseHeaderLine(params.getUseHeaderLine());
		setAutoDetectDataType(params.getAutoDetectDataType());
	}

	/**
	 * このコンポーネントがデバッグモードに設定されているかを取得する。
	 * @return	デバッグモードに設定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean isDebugMode() {
		return _flgDebug;
	}

	/**
	 * このコンポーネントのデバッグモードを設定する。
	 * @param forDebug	デバッグモードで動作させる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public void setDebugMode(boolean forDebug) {
		_flgDebug = forDebug;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	@Override
	protected boolean doOkAction() {
		// [OK] ボタン押下の場合のみ、CSV設定を保存する
		storeCsvConfiguration();
		
		// [OK] を許可
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getDefaultEncodingName() {
		return JCharsetComboBox.getDefaultCharsetName();
	}
	
	protected CsvParameters getDefaultCsvParameters() {
		if (_defCsvParams == null) {
			CsvParameters params = new CsvParameters();
			//--- CsvParameters のデフォルトと同じ
			_defCsvParams = params;
		}
		return _defCsvParams;
	}

	protected void restoreCsvConfiguration() {
		String curEncoding = getDefaultEncodingName();
		char cDelimiter = getDefaultCsvParameters().getDelimiterChar();
		char cQuoteChar = getDefaultCsvParameters().getQuoteChar();
		boolean allowEscape = getDefaultCsvParameters().isQuoteEscapeEnabled();
		boolean allowMultiline = getDefaultCsvParameters().isAllowMutiLineField();
		boolean headerLine = getDefaultCsvParameters().getUseHeaderLine();
		boolean autoDetectType = getDefaultCsvParameters().getAutoDetectDataType();
		
		// get configuration
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null && !Strings.isNullOrEmpty(prefix)) {
			//--- encoding
			curEncoding = config.getString(prefix+SUBKEY_ENCODING, curEncoding);
			//--- delimiter
			String strValue = config.getString(prefix+SUBKEY_DELIMITER, String.valueOf(cDelimiter));
			if (!Strings.isNullOrEmpty(strValue)) {
				cDelimiter = strValue.charAt(0);
			}
			//--- quote char
			strValue = config.getString(prefix+SUBKEY_QUOTECHAR, String.valueOf(cQuoteChar));
			if (!Strings.isNullOrEmpty(strValue)) {
				cQuoteChar = strValue.charAt(0);
			}
			//--- allow escape
			allowEscape = config.getBooleanValue(prefix+SUBKEY_ALLOW_ESCAPE, allowEscape);
			//--- deny multiline
			allowMultiline = config.getBooleanValue(prefix+SUBKEY_ALLOW_MULTILINE, allowMultiline);
			//--- header line
			headerLine = config.getBooleanValue(prefix + SUBKEY_HEADERLINE, headerLine);
			//--- auto detect data type
			autoDetectType = config.getBooleanValue(prefix + SUBKEY_AUTODETECTTYPE, autoDetectType);
		}
		
		// restore configurations
		setEncodingCharsetName(curEncoding);
		setDelimiterChar(cDelimiter);
		setEscapeByChar(allowEscape, cQuoteChar);
		setAllowMultiline(allowMultiline);
		setUseHeaderLine(headerLine);
		setAutoDetectDataType(autoDetectType);
	}

	/**
	 * このダイアログの選択内容を保存する
	 */
	protected void storeCsvConfiguration() {
		// get configuration
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config == null || Strings.isNullOrEmpty(prefix)) {
			// 設定オブジェクトが存在しないか、専用の prefix が未定義の場合は保存しない
			return;
		}
		
		// store parameters
		String curEncoding = getEncodingCharsetName();
		CsvParameters curParams = getCsvParameters();
		//--- encoding
		if (Objects.isEqual(curEncoding, getDefaultEncodingName())) {
			// default
			config.clearProperty(prefix + SUBKEY_ENCODING);
		} else {
			config.setString(prefix + SUBKEY_ENCODING, curEncoding);
		}
		//--- delimiter
		if (curParams.getDelimiterChar() == getDefaultCsvParameters().getDelimiterChar()) {
			// default
			config.clearProperty(prefix + SUBKEY_DELIMITER);
		} else {
			config.setString(prefix + SUBKEY_DELIMITER, String.valueOf(curParams.getDelimiterChar()));
		}
		//--- escape char
		if (curParams.getQuoteChar() == getDefaultCsvParameters().getQuoteChar()) {
			// default
			config.clearProperty(prefix + SUBKEY_QUOTECHAR);
		} else {
			config.setString(prefix + SUBKEY_QUOTECHAR, String.valueOf(curParams.getQuoteChar()));
		}
		//--- allow escape
		if (curParams.isQuoteEscapeEnabled() == getDefaultCsvParameters().isQuoteEscapeEnabled()) {
			// default
			config.clearProperty(prefix + SUBKEY_ALLOW_ESCAPE);
		} else {
			config.setBooleanValue(prefix + SUBKEY_ALLOW_ESCAPE, curParams.isQuoteEscapeEnabled());
		}
		//--- deny multiline
		if (curParams.isAllowMutiLineField() == getDefaultCsvParameters().isAllowMutiLineField()) {
			// default
			config.clearProperty(prefix + SUBKEY_ALLOW_MULTILINE);
		} else {
			config.setBooleanValue(prefix + SUBKEY_ALLOW_MULTILINE, curParams.isAllowMutiLineField());
		}
		//--- header line
		if (curParams.getUseHeaderLine() == getDefaultCsvParameters().getUseHeaderLine()) {
			// default
			config.clearProperty(prefix + SUBKEY_HEADERLINE);
		} else {
			config.setBooleanValue(prefix + SUBKEY_HEADERLINE, curParams.getUseHeaderLine());
		}
		//--- auto detect data type(この設定は保存しない)
//		if (curParams.getAutoDetectDataType()) {
//			// default
//			config.clearProperty(prefix + SUBKEY_AUTODETECTTYPE);
//		} else {
//			config.setBooleanValue(prefix + SUBKEY_AUTODETECTTYPE, curParams.getAutoDetectDataType());
//		}
	}
	
	protected char getDelimiterChar() {
		JComboBox cmb = getDelimiterCharComboBox();
		if (cmb != null) {
			if (cmb.getSelectedIndex() == 2) {
				return CsvUtil.SSV_DELIMITER_CHAR;
			} else if (cmb.getSelectedIndex() == 1) {
				return CsvUtil.TSV_DELIMITER_CHAR;
			} else {
				return CsvUtil.CSV_DELIMITER_CHAR;
			}
		} else {
			return getDefaultCsvParameters().getDelimiterChar();
		}
	}
	
	protected void setDelimiterChar(char delimiter) {
		JComboBox cmb = getDelimiterCharComboBox();
		if (cmb != null) {
			if (delimiter == CsvUtil.SSV_DELIMITER_CHAR) {
				cmb.setSelectedIndex(2);
			} else if (delimiter == CsvUtil.TSV_DELIMITER_CHAR) {
				cmb.setSelectedIndex(1);
			} else {
				cmb.setSelectedIndex(0);
			}
		}
	}
	
	protected char getQuoteChar() {
		JComboBox cmb = getEscapeCharComboBox();
		if (cmb != null) {
			if (cmb.getSelectedIndex() == 1) {
				return CsvUtil.CSV_SINGLE_QUOTE_CHAR;
			} else {
				return CsvUtil.CSV_QUOTE_CHAR;
			}
		} else {
			return getDefaultCsvParameters().getQuoteChar();
		}
	}
	
	protected boolean isAllowEscapeByChar() {
		JComboBox cmb = getEscapeCharComboBox();
		if (cmb != null) {
			return (cmb.getSelectedIndex() != 0);
		} else {
			return getDefaultCsvParameters().isQuoteEscapeEnabled();
		}
	}
	
	protected void setEscapeByChar(boolean allowEscape, char quoteChar) {
		JComboBox cmb = getEscapeCharComboBox();
		if (cmb != null) {
			if (allowEscape) {
				if (quoteChar == CsvUtil.CSV_SINGLE_QUOTE_CHAR) {
					cmb.setSelectedIndex(1);
				} else {
					cmb.setSelectedIndex(2);
				}
			} else {
				cmb.setSelectedIndex(0);
			}
		}
	}
	
	protected boolean isAllowMultiline() {
		JCheckBox chk = getDenyMultilineCheckBox();
		if (chk != null) {
			return (!chk.isSelected());
		} else {
			return getDefaultCsvParameters().isAllowMutiLineField();
		}
	}
	
	protected void setAllowMultiline(boolean allow) {
		JCheckBox chk = getDenyMultilineCheckBox();
		if (chk != null) {
			chk.setSelected(!allow);
		}
	}
	
	protected boolean getUseHeaderLine() {
		JCheckBox chk = getUseHeaderLineCheckBox();
		if (chk != null) {
			return chk.isSelected();
		} else {
			return getDefaultCsvParameters().getUseHeaderLine();
		}
	}
	
	protected void setUseHeaderLine(boolean use) {
		JCheckBox chk = getUseHeaderLineCheckBox();
		if (chk != null) {
			chk.setSelected(use);
		}
	}
	
	protected boolean getAutoDetectDataType() {
		JCheckBox chk = getAutoDetectDataTypeCheckBox();
		if (chk != null) {
			return chk.isSelected();
		} else {
			return getDefaultCsvParameters().getAutoDetectDataType();
		}
	}
	
	protected void setAutoDetectDataType(boolean flag) {
		JCheckBox chk = getAutoDetectDataTypeCheckBox();
		if (chk != null) {
			chk.setSelected(flag);
		}
	}
	
	protected void createContentComponents() {
		//--- file path label
		this._lblFilePath = createFilePathLabel();
		//--- encoding combobox
		this._cmbEncoding = createEncodingComboBox();
		//--- use header line checkbox
		this._chkUseHeaderLine = createUseHeaderLineCheckBox();
		//--- auto detect check box
		this._chkAutoDetectType = createAutoDetectDataTypeCheckBox();
		//--- delimiter char combobox
		this._cmbDelimiterChar = createDelimiterCharComboBox();
		//--- escape char combobox
		this._cmbEscapeChar = createEscapeCharComboBox();
		//--- deny multiline checkbox
		this._chkDenyMultiline = createDenyMultilineCheckBox();
	}

	@Override
	protected void setupMainContents() {
		// create labels
		JLabel lblFile = new JLabel(CommonMessages.getInstance().CsvConfigDlgLabel_filepath);
		JLabel lblCharset = new JLabel(CommonMessages.getInstance().CsvConfigDlgLabel_encoding);
		JLabel lblDelimiter = new JLabel(CommonMessages.getInstance().CsvConfigDlgLabel_delimiter);
		JLabel lblEscape = new JLabel(CommonMessages.getInstance().CsvConfigDlgLabel_escape);
		
		// create Panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 3, 3);
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- file
		if (_lblFilePath != null) {
			gbc.anchor = GridBagConstraints.NORTHEAST;
			mainPanel.add(lblFile, gbc);
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			mainPanel.add(_lblFilePath, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx = 0;
			gbc.gridy++;
		}
		//--- charset
		if (_cmbEncoding != null) {
			gbc.anchor = GridBagConstraints.EAST;
			mainPanel.add(lblCharset, gbc);
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			mainPanel.add(_cmbEncoding, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx=0;
			gbc.gridy++;
		}
		//--- Use header line
		if (_chkUseHeaderLine != null) {
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			mainPanel.add(_chkUseHeaderLine, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx=0;
			gbc.gridy++;
		}
		//--- Auto detect data type
		if (_chkAutoDetectType != null) {
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			mainPanel.add(_chkAutoDetectType, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx=0;
			gbc.gridy++;
		}
		//--- Delimiter char
		if (_cmbDelimiterChar != null) {
			gbc.anchor = GridBagConstraints.EAST;
			mainPanel.add(lblDelimiter, gbc);
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			mainPanel.add(_cmbDelimiterChar, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx=0;
			gbc.gridy++;
		}
		//--- Escape char
		if (_cmbEscapeChar != null) {
			gbc.anchor = GridBagConstraints.EAST;
			mainPanel.add(lblEscape, gbc);
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			mainPanel.add(_cmbEscapeChar, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx=0;
			gbc.gridy++;
		}
		//--- Deny multiline
		if (_chkDenyMultiline != null) {
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			mainPanel.add(_chkDenyMultiline, gbc);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx=0;
			gbc.gridy++;
		}
		//--- dummy
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0,0,0,0);
		mainPanel.add(new JLabel(), gbc);
		
		// add main panel
		this.add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		this.setResizable(true);
		this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}
	
	@Override
	protected JButton createApplyButton() {
		// No apply button
		return null;
	}
	
	protected JTextComponent getFilePathTextComponent() {
		return _lblFilePath;
	}

	protected JComboBox getDelimiterCharComboBox() {
		return _cmbDelimiterChar;
	}
	
	protected JComboBox getEscapeCharComboBox() {
		return _cmbEscapeChar;
	}
	
	protected JCheckBox getDenyMultilineCheckBox() {
		return _chkDenyMultiline;
	}
	
	protected JCheckBox getUseHeaderLineCheckBox() {
		return _chkUseHeaderLine;
	}
	
	protected JCheckBox getAutoDetectDataTypeCheckBox() {
		return _chkAutoDetectType;
	}
	
	protected JCharsetComboBox getEncodingComboBox() {
		return _cmbEncoding;
	}
	
	protected JComboBox createDelimiterCharComboBox() {
		JComboBox cmb = new JComboBox();
		cmb.addItem(CommonMessages.getInstance().CsvConfigDlgDelim_comma);
		cmb.addItem(CommonMessages.getInstance().CsvConfigDlgDelim_tab);
		cmb.addItem(CommonMessages.getInstance().CsvConfigDlgDelim_space);
		cmb.setSelectedIndex(0);
		return cmb;
	}
	
	protected JComboBox createEscapeCharComboBox() {
		JComboBox cmb = new JComboBox();
		cmb.addItem(CommonMessages.getInstance().CsvConfigDlgEscape_none);
		cmb.addItem(CommonMessages.getInstance().CsvConfigDlgEscape_squot);
		cmb.addItem(CommonMessages.getInstance().CsvConfigDlgEscape_dquot);
		cmb.setSelectedIndex(2);
		return cmb;
	}
	
	protected JCheckBox createDenyMultilineCheckBox() {
		JCheckBox chk = new JCheckBox(CommonMessages.getInstance().CsvConfigDlgLabel_denyNL);
		chk.setSelected(false);
		return chk;
	}
	
	protected JCheckBox createUseHeaderLineCheckBox() {
		JCheckBox chk = new JCheckBox(CommonMessages.getInstance().CsvConfigDlgLabel_headerline);
		chk.setSelected(false);
		return chk;
	}
	
	protected JCheckBox createAutoDetectDataTypeCheckBox() {
		if (isDebugMode()) {
			JCheckBox chk = new JCheckBox(CommonMessages.getInstance().CsvConfigDlgLabel_autodetect);
			chk.setSelected(true);
			return chk;
		} else {
			return null;
		}
	}
	
	protected JCharsetComboBox createEncodingComboBox() {
		return new JCharsetComboBox();
	}
	
	protected JTextComponent createFilePathLabel() {
		JStaticMultilineTextPane label = new JStaticMultilineTextPane();
		if (_fTargetFile != null) {
			label.setText(_fTargetFile.toString());
		}
		return label;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
