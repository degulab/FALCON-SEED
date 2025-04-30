/*
 * @(#)ConversionJsonCsvConfigPanel.java	3.4.0	2020/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.JCharsetComboBox;
import ssac.util.swing.JCsvFieldDelimiterCharComboBox;
import ssac.util.swing.JMaskedNumberSpinner;

/**
 * JSON-CSV 変換設定パネル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionJsonCsvConfigPanel extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static private final int EXPAND_PREFFERED_WIDTH	= 20;
	
	static protected final String	SUBKEY_ENCODING_TYPE		= ".encoding.type";
	static protected final String	SUBKEY_ENCODING_NAME		= ".encoding.name";
	static protected final String	SUBKEY_DELIMITER_NAME		= ".delimiter";
//	static protected final String	SUBKEY_DATAMODEL_TYPE		= ".datamodel";
	static protected final String	SUBKEY_HEADERLINE_TYPE		= ".headerline.type";
	static protected final String	SUBKEY_HEADERLINE_NUM		= ".headerline.num";
	static protected final String	SUBKEY_JSONLIST_FORMAT		= ".jsonlist.format";
	static protected final String	SUBKEY_JSONLIST_ELEM_SEP	= ".jsonlist.element.separator";
	static protected final String	SUBKEY_JSONKEYVAL_FORMAT	= ".jsonkeyval.format";
	static protected final String	SUBKEY_JSONKEYVAL_ELEM_SEP	= ".jsonkeyval.element.separator";
	static protected final String	SUBKEY_JSONKEYVAL_SEP		= ".jsonkeyval.separator";
	
	static public final String	CONFIG_ENCODING_DEFAULT		= "encoding.default";
	static public final String	CONFIG_ENCODING_CUSTOM		= "encoding.custom";
//	static public final String	CONFIG_DATAMODEL_EXALGE		= "datamodel.exalge";
//	static public final String	CONFIG_DATAMODEL_DTALGE		= "datamodel.dtalge";
//	static public final String	CONFIG_DATAMODEL_CUSTOM		= "datamodel.custom";
	static public final String	CONFIG_HEADERLINE_NONE		= "headerline.none";
	static public final String	CONFIG_HEADERLINE_SINGLE	= "headerline.single";
	static public final String	CONFIG_HEADERLINE_MULTI		= "headerline.multi";
	static public final String	CONFIG_JSONLIST_JSON		= "jsonlist.format.json";
	static public final String	CONFIG_JSONLIST_CUSTOM		= "jsonlist.format.custom";
	static public final String	CONFIG_JSONKEYVAL_JSON		= "jsonkeyval.format.json";
	static public final String	CONFIG_JSONKEYVAL_CUSTOM	= "jsonkeyval.format.custom";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 複数ヘッダー行を許可する場合は <tt>true</tt> **/
	private final boolean	_allowHeaderMultiline;
	/** 複数ヘッダー行の選択にスピンボタンを使用する場合は <tt>true</tt> **/
	private final boolean	_useHeaderMultiLineSpinner;
	/** JSCON コレクション形式を無視する場合は <tt>true</tt> **/
	private final boolean	_ignoreJsonCollectionFormats;
	
	/** このコンポーネントに対するイベントハンドラ **/
	private ConversionJsonCsvConfigHandler	_handler;
	
	/** このコンポーネントの状態を保存する際のキープレフィックス **/
	private String		_configPrefix;
	/** このコンポーネントの状態を保存する <code>ExConfiguration</code> オブジェクト **/
	private ExConfiguration	_configProps;

	/** キャプション：フィールド区切り文字 **/
	private JLabel				_capDelimiterChar;
	/** フィールド区切り文字を選択するコンボボックス **/
	private JCsvFieldDelimiterCharComboBox	_cmbDelimiterChar;
	// エスケープ文字はダブルクオート固定
	///** エスケープ文字を選択するコンボボックス **/
	//private JComboBox			_cmbEscapeChar;
	
	/** ファイル・エンコーディング選択ラジオボタンで選択されているコマンド名 **/
	private String				_selEncoding;
	/** キャプション：ファイルエンコーディング **/
	private JLabel				_capEncoding;
	/** ファイル・エンコーディングを選択するコンボボックス **/
	private JCharsetComboBox	_cmbEncoding;
	/** システムデフォルトのファイル・エンコーディングを選択するラジオボタン **/
	private JRadioButton		_rdoEncodingDefault;
	/** ユーザー指定のファイルエンコーディングを選択するラジオボタン **/
	private JRadioButton		_rdoEncodingCustom;
	
//	/** データモデル選択ラジオボタンで選択されているコマンド名 **/
//	private String				_selTypeOfDataModel;
//	/** キャプション：データ形式 **/
//	private JLabel				_capTypeOfDataModel;
//	/** データモデルとして交換代数形式を選択するラジオボタン **/
//	private JRadioButton		_rdoDataModelExalge;
//	/** データモデルとしてデータ代数形式を選択するラジオボタン **/
//	private JRadioButton		_rdoDataModelDtalge;
//	/** データモデルとして任意のCSV形式を選択するラジオボタン **/
//	private JRadioButton		_rdoDataModelCustom;
	
	/** ヘッダー行数選択ラジオボタンで選択されているコマンド名 **/
	private String				_selHeaderLines;
	/** キャプション：ヘッダー型式 **/
	private JLabel				_capHeaderType;
	/** ヘッダー行無しを選択するラジオボタン **/
	private JRadioButton		_rdoHeaderNone;
	/** ヘッダー行先頭１行のみであることを選択するラジオボタン **/
	private JRadioButton		_rdoHeaderSingle;
	/** ヘッダー行は複数行であることを選択するラジオボタン **/
	private JRadioButton		_rdoHeaderMulti;
	/** 任意のヘッダー行数を入力するスピンボタンエディタ **/
	private JMaskedNumberSpinner	_spnHeaderLinesEditor;
	
	/** JSON-KeyValue フォーマット選択ラジオボタンで選択されているコマンド名 **/
	private String				_selJsonKeyValueFormat;
	/** キャプション： JSON-KeyValue フォーマット **/
	private JLabel				_capJsonKeyValueFormat;
	/** JSON-KeyValue フォーマットを JSON 形式とすることを選択するラジオボタン **/
	private JRadioButton		_rdoJsonKeyValueFormatJson;
	/** JSON-KeyValue フォーマットをカスタムとすることを選択するラジオボタン **/
	private JRadioButton		_rdoJsonKeyValueFormatCustom;
	/** JSON-KeyValue 要素の区切り文字を選択するコンボボックス **/
	private JConversionElementSeparatorComboBox	_cmbJsonKeyValueElementSeparator;
	/** キャプション： JSON-KeyValue 区切り文字 **/
	private JLabel				_capJsonKeyValueSeparator;
	/** JSON-KeyValue 区切り文字を選択するコンボボックス **/
	private JConversionKeyValueSeparatorComboBox	_cmbJsonKeyValueSeparator;
	
	/** JSON リストフォーマット選択ラジオボタンで選択されているコマンド名 **/
	private String				_selJsonListFormat;
	/** キャプション： JSON リストフォーマット **/
	private JLabel				_capJsonListFormat;
	/** JSON リストフォーマットを JSON 形式とすることを選択するラジオボタン **/
	private JRadioButton		_rdoJsonListFormatJson;
	/** JSON リストフォーマットをカスタムとすることを選択するラジオボタン **/
	private JRadioButton		_rdoJsonListFormatCustom;
	/** JSON リスト要素の区切り文字を選択するコンボボックス **/
	private JConversionElementSeparatorComboBox	_cmbJsonListElementSeparator;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConversionJsonCsvConfigPanel() {
		this(false, false, false);
	}
	
	public ConversionJsonCsvConfigPanel(boolean allowCsvHeaderMultiline, boolean useCsvHeaderMultiLineSpinner, boolean ignoreJsonCollections) {
		super(new GridBagLayout());
		_allowHeaderMultiline = allowCsvHeaderMultiline;
		_useHeaderMultiLineSpinner = useCsvHeaderMultiLineSpinner;
		_ignoreJsonCollectionFormats = ignoreJsonCollections;
	}
	
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// setup main contents
		setupMainContents();
		
		// 設定情報の反映
		restoreConfiguration();
		
		// setup Actions
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ConversionJsonCsvConfigHandler getHandler() {
		return _handler;
	}
	
	public void setHandler(ConversionJsonCsvConfigHandler newHandler) {
		_handler = newHandler;
	}
	
	public String getPrefferedCsvFileEncoding() {
		return AppSettings.getInstance().getAadlCsvEncodingName();
	}
	
	public String getSelectedCsvFileEncodingName() {
		String name;
		if (_rdoEncodingCustom.isSelected()) {
			name = _cmbEncoding.getSelectedCharsetName();
			if (name == null) {
				name = getPrefferedCsvFileEncoding();
			}
		}
		else {
			// default
			name = getPrefferedCsvFileEncoding();
		}
		return name;
	}
	
	public Charset getSelectedCsvFileEncoding() {
		Charset ret;
		if (_rdoEncodingCustom.isSelected()) {
			ret = _cmbEncoding.getSelectedCharset();
			if (ret == null) {
				ret = JCharsetComboBox.getAvailableCharset(getPrefferedCsvFileEncoding());
			}
		}
		else {
			// default
			ret = JCharsetComboBox.getAvailableCharset(getPrefferedCsvFileEncoding());
		}
		return ret;
	}
	
	public char getSelectedCsvDelimiterChar() {
		char delim = _cmbDelimiterChar.getSelectedDelimiterChar();
		if (delim == '\0') {
			delim = JCsvFieldDelimiterCharComboBox.getDefaultDelimiterChar();
		}
		return delim;
	}
	
	/**
	 * 現在の設定における、CSV ファイルヘッダー行数を取得する。
	 * @return	ヘッダー行なしなら 0、指定されている場合はその行数、行数不定の複数行の場合は (-1)
	 */
	public int getSelectedHeaderLineCount() {
		if (_rdoHeaderMulti.isSelected() && _allowHeaderMultiline) {
			// 複数行が許可されている場合のみ
			if (_spnHeaderLinesEditor != null && _spnHeaderLinesEditor.isVisible())
				return ((Number)_spnHeaderLinesEditor.getValue()).intValue();
			else
				return (-1);
		}
		else if (_rdoHeaderSingle.isSelected()) {
			// 1 行
			return 1;
		}
		else {
			// なし
			return 0;
		}
	}
	
	/**
	 * 現在の設定に応じた <code>CsvParameters</code> オブジェクトを生成する。
	 * @return	現在の設定で生成された <code>CsvParameters</code> オブジェクト
	 */
	public CsvParameters getCurrentCsvParameters() {
		CsvParameters params = new CsvParameters();
		params.setAutoDetectDataType(true);
		params.setAllowMultiLineField(true);
		params.setQuoteEscapeEnabled(true);
		params.setQuoteChar('\"');
		params.setDelimiterChar(getSelectedCsvDelimiterChar());
		params.setUseHeaderLine(getSelectedHeaderLineCount() != 0);
		params.setHeaderLineCount(getSelectedHeaderLineCount());
		return params;
	}

	/**
	 * ダイアログの状態を保存するキーのプレフィックスを返す。
	 * プレフィックスが設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getConfigurationPrefix() {
		return _configPrefix;
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトを返す。
	 * @return	このダイアログに設定されている <code>ExConfiguration</code> オブジェクトを返す。
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public ExConfiguration getConfiguration() {
		return _configProps;
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトと、
	 * 保存時のキーのプレフィックスを設定する。
	 * @param prefix	キーのプレフィックスとする文字列
	 * @param config	<code>ExConfiguration</code> オブジェクト
	 */
	public void setConfiguration(String prefix, ExConfiguration config) {
		_configPrefix = prefix;
		_configProps  = config;
	}
	
	public void restoreConfiguration() {
		// set defaults
		String selEncoding = CONFIG_ENCODING_DEFAULT;
//		String selTypeOfDataModel = CONFIG_DATAMODEL_EXALGE;
		String selHeaderLines = CONFIG_HEADERLINE_NONE;
		String selJsonListFormat = CONFIG_JSONLIST_JSON;
		String selJsonKeyValueFormat = CONFIG_JSONKEYVAL_JSON;
		int		numHeaderLines  = 0;
		String	customEncoding  = getPrefferedCsvFileEncoding();
		String	customDelimiter = ""+JCsvFieldDelimiterCharComboBox.getDefaultDelimiterChar();
		String	customListElemSeparator = JConversionElementSeparatorComboBox.getDefaultSeparator();
		String	customKeyValElemSeparator = JConversionElementSeparatorComboBox.getDefaultSeparator();
		String	customKeyValSeparator = JConversionKeyValueSeparatorComboBox.getDefaultSeparator();
		
		// restore from properties
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			//--- encoding
			selEncoding = config.getString(prefix+SUBKEY_ENCODING_TYPE, selEncoding);
			customEncoding = config.getString(prefix+SUBKEY_ENCODING_NAME, customEncoding);
//			//--- data model
//			selTypeOfDataModel = config.getString(prefix+SUBKEY_DATAMODEL_TYPE, selTypeOfDataModel);
			//--- delimiter
			customDelimiter = config.getString(prefix+SUBKEY_DELIMITER_NAME, customDelimiter);
			//--- header lines
			selHeaderLines = config.getString(prefix+SUBKEY_HEADERLINE_TYPE, selHeaderLines);
			numHeaderLines = config.getIntegerValue(prefix+SUBKEY_HEADERLINE_NUM, numHeaderLines);
			//--- json list format
			selJsonListFormat = config.getString(prefix+SUBKEY_JSONLIST_FORMAT, selJsonListFormat);
			customListElemSeparator = config.getString(prefix+SUBKEY_JSONLIST_ELEM_SEP, customListElemSeparator);
			//--- json keyvalue format
			selJsonKeyValueFormat = config.getString(prefix+SUBKEY_JSONKEYVAL_FORMAT, selJsonKeyValueFormat);
			customKeyValElemSeparator = config.getString(prefix+SUBKEY_JSONKEYVAL_ELEM_SEP, customKeyValElemSeparator);
			customKeyValSeparator = config.getString(prefix+SUBKEY_JSONKEYVAL_SEP, customKeyValSeparator);
		}
		
		// restore values
		restoreEncoding(selEncoding, customEncoding);
//		restoreDataModel(selTypeOfDataModel);
		restoreDelimiterChar(customDelimiter);
		restoreHeaderLines(selHeaderLines, numHeaderLines);
		restoreJsonListFormat(selJsonListFormat, customListElemSeparator);
		restoreJsonKeyValueFormat(selJsonKeyValueFormat, customKeyValElemSeparator, customKeyValSeparator);
		updateFileEncodingContents();
		updateDataModelContents();
	}
	
	protected void restoreEncoding(String selection, String encoding) {
		if (CONFIG_ENCODING_CUSTOM.equals(selection)) {
			_rdoEncodingDefault.setSelected(false);
			_rdoEncodingCustom.setSelected(true);
			_selEncoding = CONFIG_ENCODING_CUSTOM;
		} else {
			_rdoEncodingDefault.setSelected(true);
			_rdoEncodingCustom.setSelected(false);
			_selEncoding = CONFIG_ENCODING_DEFAULT;
		}
		_cmbEncoding.setSelectedCharsetName(encoding);
	}
	
//	protected void restoreDataModel(String selection) {
//		if (CONFIG_DATAMODEL_CUSTOM.equals(selection)) {
//			_rdoDataModelExalge.setSelected(false);
//			_rdoDataModelDtalge.setSelected(false);
//			_rdoDataModelCustom.setSelected(true);
//			_selTypeOfDataModel = CONFIG_DATAMODEL_CUSTOM;
//		}
//		else if (CONFIG_DATAMODEL_DTALGE.equals(selection)) {
//			_rdoDataModelExalge.setSelected(false);
//			_rdoDataModelDtalge.setSelected(true);
//			_rdoDataModelCustom.setSelected(false);
//			_selTypeOfDataModel = CONFIG_DATAMODEL_DTALGE;
//		}
//		else {
//			_rdoDataModelExalge.setSelected(true);
//			_rdoDataModelDtalge.setSelected(false);
//			_rdoDataModelCustom.setSelected(false);
//			_selTypeOfDataModel = CONFIG_DATAMODEL_EXALGE;
//		}
//	}
	
	protected void restoreDelimiterChar(String delim) {
		_cmbDelimiterChar.setSelectedDelimiterChar(delim==null || delim.isEmpty() ? '\0' : delim.charAt(0));
	}
	
	protected void restoreHeaderLines(String selection, int numLines) {
		if (CONFIG_HEADERLINE_MULTI.equals(selection) && _allowHeaderMultiline) {
			// 複数行ヘッダーが許可されている場合のみ
			_rdoHeaderNone.setSelected(false);
			_rdoHeaderSingle.setSelected(false);
			_rdoHeaderMulti.setSelected(true);
			_selHeaderLines = CONFIG_HEADERLINE_MULTI;
		}
		else if (CONFIG_HEADERLINE_SINGLE.equals(selection)) {
			_rdoHeaderNone.setSelected(false);
			_rdoHeaderSingle.setSelected(true);
			_rdoHeaderMulti.setSelected(false);
			_selHeaderLines = CONFIG_HEADERLINE_SINGLE;
		}
		else {
			_rdoHeaderNone.setSelected(true);
			_rdoHeaderSingle.setSelected(false);
			_rdoHeaderMulti.setSelected(false);
			_selHeaderLines = CONFIG_HEADERLINE_NONE;
		}
		if (_spnHeaderLinesEditor != null) {
			_spnHeaderLinesEditor.setValue(numLines);
		}
	}
	
	protected void restoreJsonListFormat(String selection, String elemSeparator) {
		if (CONFIG_JSONLIST_CUSTOM.equals(selection)) {
			_rdoJsonListFormatJson.setSelected(false);
			_rdoJsonListFormatCustom.setSelected(true);
			_selJsonListFormat = CONFIG_JSONLIST_CUSTOM;
		} else {
			_rdoJsonListFormatJson.setSelected(true);
			_rdoJsonListFormatCustom.setSelected(false);
			_selJsonListFormat = CONFIG_JSONLIST_JSON;
		}
		_cmbJsonListElementSeparator.setSelectedSeparator(elemSeparator);
		
		if (_ignoreJsonCollectionFormats) {
			_rdoJsonListFormatJson.setEnabled(false);
			_rdoJsonListFormatCustom.setEnabled(false);
			_cmbJsonListElementSeparator.setEnabled(false);
		}
		else {
			_rdoJsonListFormatJson.setEnabled(true);
			_rdoJsonListFormatCustom.setEnabled(true);
			_cmbJsonListElementSeparator.setEnabled(true);
		}
	}
	
	protected void restoreJsonKeyValueFormat(String selection, String elemSeparator, String keyValSeparator) {
		if (CONFIG_JSONKEYVAL_CUSTOM.equals(selection)) {
			_rdoJsonKeyValueFormatJson.setSelected(false);
			_rdoJsonKeyValueFormatCustom.setSelected(true);
			_selJsonKeyValueFormat = CONFIG_JSONKEYVAL_CUSTOM;
		} else {
			_rdoJsonKeyValueFormatJson.setSelected(true);
			_rdoJsonKeyValueFormatCustom.setSelected(false);
			_selJsonKeyValueFormat = CONFIG_JSONKEYVAL_JSON;
		}
		_cmbJsonKeyValueElementSeparator.setSelectedSeparator(elemSeparator);
		_cmbJsonKeyValueSeparator.setSelectedSeparator(keyValSeparator);
		
		if (_ignoreJsonCollectionFormats) {
			_rdoJsonKeyValueFormatJson.setEnabled(false);
			_rdoJsonKeyValueFormatCustom.setEnabled(false);
			_cmbJsonKeyValueElementSeparator.setEnabled(false);
			_cmbJsonKeyValueSeparator.setEnabled(false);
		}
		else {
			_rdoJsonKeyValueFormatJson.setEnabled(true);
			_rdoJsonKeyValueFormatCustom.setEnabled(true);
			_cmbJsonKeyValueElementSeparator.setEnabled(true);
			_cmbJsonKeyValueSeparator.setEnabled(true);
		}
	}
	
	public void storeConfiguration() {
		// store to properties
		String prefix = getConfigurationPrefix();
		ExConfiguration config = getConfiguration();
		if (config != null) {
			String value;
			//--- encoding
			if (_rdoEncodingCustom.isSelected()) {
				config.setString(prefix+SUBKEY_ENCODING_TYPE, CONFIG_ENCODING_CUSTOM);
				config.setString(prefix+SUBKEY_ENCODING_NAME, _cmbEncoding.getSelectedCharsetName());
			} else {
				config.clearProperty(prefix+SUBKEY_ENCODING_TYPE);
				config.clearProperty(prefix+SUBKEY_ENCODING_NAME);
			}
//			//--- data model
//			selTypeOfDataModel = config.getString(prefix+SUBKEY_DATAMODEL_TYPE, selTypeOfDataModel);
			//--- delimiter
			char delim = _cmbDelimiterChar.getSelectedDelimiterChar();
			if (delim != '\0' && delim != JCsvFieldDelimiterCharComboBox.getDefaultDelimiterChar())
				config.setString(prefix+SUBKEY_DELIMITER_NAME, ""+delim);
			else
				config.clearProperty(prefix+SUBKEY_DELIMITER_NAME);
			//--- header lines
			if (_rdoHeaderMulti.isSelected()) {
				if (_allowHeaderMultiline) {
					config.setString(prefix+SUBKEY_HEADERLINE_TYPE, CONFIG_HEADERLINE_MULTI);
					if (_spnHeaderLinesEditor != null && _spnHeaderLinesEditor.isVisible())
						config.setIntegerValue(prefix+SUBKEY_HEADERLINE_NUM, ((Number)_spnHeaderLinesEditor.getValue()).intValue());
					else
						config.clearProperty(prefix+SUBKEY_HEADERLINE_NUM);
				}
				else {
					// 複数行のヘッダーを許可しない
					config.clearProperty(prefix+SUBKEY_HEADERLINE_TYPE);
					config.clearProperty(prefix+SUBKEY_HEADERLINE_NUM);
				}
			}
			else if (_rdoHeaderSingle.isSelected()) {
				config.setString(prefix+SUBKEY_HEADERLINE_TYPE, CONFIG_HEADERLINE_SINGLE);
				config.clearProperty(prefix+SUBKEY_HEADERLINE_NUM);
			}
			else {
				config.setString(prefix+SUBKEY_HEADERLINE_TYPE, CONFIG_HEADERLINE_NONE);
				config.clearProperty(prefix+SUBKEY_HEADERLINE_NUM);
			}
			//--- json list format
			if (_ignoreJsonCollectionFormats) {
				config.clearProperty(prefix+SUBKEY_JSONLIST_FORMAT);
				config.clearProperty(prefix+SUBKEY_JSONLIST_ELEM_SEP);
			}
			else {
				if (_rdoJsonListFormatCustom.isSelected()) {
					config.setString(prefix+SUBKEY_JSONLIST_FORMAT, CONFIG_JSONLIST_CUSTOM);
					value = _cmbJsonListElementSeparator.getSelectedSeparator();
					if (value != null && !value.isEmpty())
						config.setString(prefix+SUBKEY_JSONLIST_ELEM_SEP, value);
					else
						config.clearProperty(prefix+SUBKEY_JSONLIST_ELEM_SEP);
				}
				else {
					config.setString(prefix+SUBKEY_JSONLIST_FORMAT, CONFIG_JSONLIST_JSON);
					config.clearProperty(prefix+SUBKEY_JSONLIST_ELEM_SEP);
				}
			}
			//--- json keyvalue format
			if (_ignoreJsonCollectionFormats) {
				config.clearProperty(prefix+SUBKEY_JSONKEYVAL_FORMAT);
				config.clearProperty(prefix+SUBKEY_JSONKEYVAL_ELEM_SEP);
				config.clearProperty(prefix+SUBKEY_JSONKEYVAL_SEP);
			}
			else {
				if (_rdoJsonKeyValueFormatCustom.isSelected()) {
					config.setString(prefix+SUBKEY_JSONKEYVAL_FORMAT, CONFIG_JSONKEYVAL_CUSTOM);
					value = _cmbJsonKeyValueElementSeparator.getSelectedSeparator();
					if (value != null && !value.isEmpty())
						config.setString(prefix+SUBKEY_JSONKEYVAL_ELEM_SEP, value);
					else
						config.clearProperty(prefix+SUBKEY_JSONKEYVAL_ELEM_SEP);
					value = _cmbJsonKeyValueSeparator.getSelectedSeparator();
					if (value != null && !value.isEmpty())
						config.setString(prefix+SUBKEY_JSONKEYVAL_SEP, value);
					else
						config.clearProperty(prefix+SUBKEY_JSONKEYVAL_SEP);
				}
				else {
					config.setString(prefix+SUBKEY_JSONKEYVAL_FORMAT, CONFIG_JSONKEYVAL_JSON);
					config.clearProperty(prefix+SUBKEY_JSONKEYVAL_ELEM_SEP);
					config.clearProperty(prefix+SUBKEY_JSONKEYVAL_SEP);
				}
			}
		}
	}
	
//	public boolean isSelectedDataModelExalge() {
//		return _rdoDataModelExalge.isSelected();
//	}
//	
//	public boolean isSelectedDataModelDtalge() {
//		return _rdoDataModelDtalge.isSelected();
//	}
//	
//	public boolean isSelectedDataModelCustom() {
//		return _rdoDataModelCustom.isSelected();
//	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	protected void onChangedEncodingSelection(boolean customEncoding, String newEncoding) {
		
	}
	
	protected void onChangedDataModelType(String newSelection) {
		if (_handler != null) {
			_handler.onSelectionChangedDataModel(newSelection);
		}
	}
	
	protected void onChangedHeaderLineType(String newSelection, int newLineCount) {
		
	}
	
	protected void onChangedJsonListFormat(String newElemSeparator) {
		
	}
	
	protected void onChangedJsonKeyValueFormat(String newElemSeparator, String newKeyValueSeparator) {
		
	}
	
	protected void onRadioButtonActionPerformed(ActionEvent ae) {
		switch (ae.getActionCommand()) {
			case CONFIG_ENCODING_DEFAULT:
				onRadioButtonEncodingDefaultClicked();
				break;
			case CONFIG_ENCODING_CUSTOM:
				onRadioButtonEncodingCustomClicked();
				break;
//			case CONFIG_DATAMODEL_EXALGE:
//				onRadioButtonDataModelExalgeClicked();
//				break;
//			case CONFIG_DATAMODEL_DTALGE:
//				onRadioButtonDataModelDtalgeClicked();
//				break;
//			case CONFIG_DATAMODEL_CUSTOM:
//				onRadioButtonDataModelCustomClicked();
//				break;
			case CONFIG_HEADERLINE_NONE:
				onRadioButtonHeaderLineNoneClicked();
				break;
			case CONFIG_HEADERLINE_SINGLE:
				onRadioButtonHeaderLineSingleClicked();
				break;
			case CONFIG_HEADERLINE_MULTI:
				onRadioButtonHeaderLineMultiClicked();
				break;
			case CONFIG_JSONLIST_JSON:
				onRadioButtonJsonListFormatJsonClicked();
				break;
			case CONFIG_JSONLIST_CUSTOM:
				onRadioButtonJsonListFormatCustomClicked();
				break;
			case CONFIG_JSONKEYVAL_JSON:
				onRadioButtonJsonKeyValueFormatJsonClicked();
				break;
			case CONFIG_JSONKEYVAL_CUSTOM:
				onRadioButtonJsonKeyValueFormatCustomClicked();
				break;
		}
	}
	
	protected void onRadioButtonEncodingDefaultClicked() {
		_rdoEncodingDefault.setSelected(true);
		_rdoEncodingCustom.setSelected(false);
		if (!CONFIG_ENCODING_DEFAULT.equals(_selEncoding)) {
			// changed
			_selEncoding = CONFIG_ENCODING_DEFAULT;
			updateFileEncodingContents();
		}
	}
	
	protected void onRadioButtonEncodingCustomClicked() {
		_rdoEncodingDefault.setSelected(false);
		_rdoEncodingCustom.setSelected(true);
		if (!CONFIG_ENCODING_CUSTOM.equals(_selEncoding)) {
			// changed
			_selEncoding = CONFIG_ENCODING_CUSTOM;
			updateFileEncodingContents();
		}
	}
	
//	protected void onRadioButtonDataModelExalgeClicked() {
//		_rdoDataModelExalge.setSelected(true);
//		_rdoDataModelDtalge.setSelected(false);
//		_rdoDataModelCustom.setSelected(false);
//		if (!CONFIG_DATAMODEL_EXALGE.equals(_selTypeOfDataModel)) {
//			// changed
//			_selTypeOfDataModel = CONFIG_DATAMODEL_EXALGE;
//			updateDataModelContents();
//			onChangedDataModelType(_selTypeOfDataModel);
//		}
//	}
	
//	protected void onRadioButtonDataModelDtalgeClicked() {
//		_rdoDataModelExalge.setSelected(false);
//		_rdoDataModelDtalge.setSelected(true);
//		_rdoDataModelCustom.setSelected(false);
//		if (!CONFIG_DATAMODEL_DTALGE.equals(_selTypeOfDataModel)) {
//			// changed
//			_selTypeOfDataModel = CONFIG_DATAMODEL_DTALGE;
//			updateDataModelContents();
//			onChangedDataModelType(_selTypeOfDataModel);
//		}
//	}
	
//	protected void onRadioButtonDataModelCustomClicked() {
//		_rdoDataModelExalge.setSelected(false);
//		_rdoDataModelDtalge.setSelected(false);
//		_rdoDataModelCustom.setSelected(true);
//		if (!CONFIG_DATAMODEL_CUSTOM.equals(_selTypeOfDataModel)) {
//			// changed
//			_selTypeOfDataModel = CONFIG_DATAMODEL_CUSTOM;
//			updateDataModelContents();
//			onChangedDataModelType(_selTypeOfDataModel);
//		}
//	}
	
	protected void onRadioButtonHeaderLineNoneClicked() {
		_rdoHeaderNone.setSelected(true);
		_rdoHeaderSingle.setSelected(false);
		_rdoHeaderMulti.setSelected(false);
		if (!CONFIG_HEADERLINE_NONE.equals(_selHeaderLines)) {
			// changed
			_selHeaderLines = CONFIG_HEADERLINE_NONE;
			updateHeaderLineContents();
		}
	}
	
	protected void onRadioButtonHeaderLineSingleClicked() {
		_rdoHeaderNone.setSelected(false);
		_rdoHeaderSingle.setSelected(true);
		_rdoHeaderMulti.setSelected(false);
		if (!CONFIG_HEADERLINE_SINGLE.equals(_selHeaderLines)) {
			// changed
			_selHeaderLines = CONFIG_HEADERLINE_SINGLE;
			updateHeaderLineContents();
		}
	}
	
	protected void onRadioButtonHeaderLineMultiClicked() {
		_rdoHeaderNone.setSelected(false);
		_rdoHeaderSingle.setSelected(false);
		_rdoHeaderMulti.setSelected(true);
		if (!CONFIG_HEADERLINE_MULTI.equals(_selHeaderLines)) {
			// changed
			_selHeaderLines = CONFIG_HEADERLINE_MULTI;
			updateHeaderLineContents();
		}
	}
	
	protected void onRadioButtonJsonListFormatJsonClicked() {
		_rdoJsonListFormatJson.setSelected(true);
		_rdoJsonListFormatCustom.setSelected(false);
		if (!CONFIG_JSONLIST_JSON.equals(_selJsonListFormat)) {
			// changed
			_selJsonListFormat = CONFIG_JSONLIST_JSON;
			updateJsonListFormatContents();
		}
	}
	
	protected void onRadioButtonJsonListFormatCustomClicked() {
		_rdoJsonListFormatJson.setSelected(false);
		_rdoJsonListFormatCustom.setSelected(true);
		if (!CONFIG_JSONLIST_CUSTOM.equals(_selJsonListFormat)) {
			// changed
			_selJsonListFormat = CONFIG_JSONLIST_CUSTOM;
			updateJsonListFormatContents();
		}
	}
	
	protected void onRadioButtonJsonKeyValueFormatJsonClicked() {
		_rdoJsonKeyValueFormatJson.setSelected(true);
		_rdoJsonKeyValueFormatCustom.setSelected(false);
		if (!CONFIG_JSONKEYVAL_JSON.equals(_selJsonKeyValueFormat)) {
			// changed
			_selJsonKeyValueFormat = CONFIG_JSONKEYVAL_JSON;
			updateJsonKeyValueFormatContents();
		}
	}
	
	protected void onRadioButtonJsonKeyValueFormatCustomClicked() {
		_rdoJsonKeyValueFormatJson.setSelected(false);
		_rdoJsonKeyValueFormatCustom.setSelected(true);
		if (!CONFIG_JSONKEYVAL_CUSTOM.equals(_selJsonKeyValueFormat)) {
			// changed
			_selJsonKeyValueFormat = CONFIG_JSONKEYVAL_CUSTOM;
			updateJsonKeyValueFormatContents();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void updateFileEncodingContents() {
		_cmbEncoding.setEnabled(_rdoEncodingCustom.isSelected());
	}
	
	protected void updateDataModelContents() {
		updateDelimiterCharContents();
		updateHeaderLineContents();
		updateJsonListFormatContents();
		updateJsonKeyValueFormatContents();
	}
	
	protected void updateDelimiterCharContents() {
//		if (_rdoDataModelCustom.isSelected()) {
			_capDelimiterChar.setEnabled(true);
			_cmbDelimiterChar.setEnabled(true);
//		}
//		else {
//			_capDelimiterChar.setEnabled(false);
//			_cmbDelimiterChar.setEnabled(false);
//		}
	}
	
	protected void updateHeaderLineContents() {
//		if (_rdoDataModelCustom.isSelected()) {
			_capHeaderType.setEnabled(true);
			_rdoHeaderNone.setEnabled(true);
			_rdoHeaderSingle.setEnabled(true);
			_rdoHeaderMulti.setEnabled(true);
			if (_spnHeaderLinesEditor != null) {
				_spnHeaderLinesEditor.setEnabled(_rdoHeaderMulti.isSelected());
			}
//		}
//		else {
//			_capHeaderType.setEnabled(false);
//			_rdoHeaderNone.setEnabled(false);
//			_rdoHeaderSingle.setEnabled(false);
//			_rdoHeaderMulti.setEnabled(false);
//			if (_spnHeaderLinesEditor != null) {
//				_spnHeaderLinesEditor.setEnabled(false);
//			}
//		}
	}
	
	protected void updateJsonListFormatContents() {
//		if (_rdoDataModelCustom.isSelected()) {
			_capJsonListFormat.setEnabled(true);
			_rdoJsonListFormatJson.setEnabled(true);
			_rdoJsonListFormatCustom.setEnabled(true);
			_cmbJsonListElementSeparator.setEnabled(_rdoJsonListFormatCustom.isSelected());
//		}
//		else {
//			_capJsonListFormat.setEnabled(false);
//			_rdoJsonListFormatJson.setEnabled(false);
//			_rdoJsonListFormatCustom.setEnabled(false);
//			_cmbJsonListElementSeparator.setEnabled(false);
//		}
	}
	
	protected void updateJsonKeyValueFormatContents() {
//		if (_rdoDataModelCustom.isSelected()) {
			_capJsonKeyValueFormat.setEnabled(true);
			_rdoJsonKeyValueFormatJson.setEnabled(true);
			_rdoJsonKeyValueFormatCustom.setEnabled(true);
			if (_rdoJsonKeyValueFormatCustom.isSelected()) {
				_cmbJsonKeyValueElementSeparator.setEnabled(true);
				_capJsonKeyValueSeparator.setEnabled(true);
				_cmbJsonKeyValueSeparator.setEnabled(true);
			} else {
				_cmbJsonKeyValueElementSeparator.setEnabled(false);
				_capJsonKeyValueSeparator.setEnabled(false);
				_cmbJsonKeyValueSeparator.setEnabled(false);
			}
//		}
//		else {
//			_capJsonKeyValueFormat.setEnabled(false);
//			_rdoJsonKeyValueFormatJson.setEnabled(false);
//			_rdoJsonKeyValueFormatCustom.setEnabled(false);
//			_cmbJsonKeyValueElementSeparator.setEnabled(false);
//			_capJsonKeyValueSeparator.setEnabled(false);
//			_cmbJsonKeyValueSeparator.setEnabled(false);
//		}
	}
	
	protected void createContentComponents() {
		//--- CSV encoding
		_capEncoding = new JLabel(CommonMessages.getInstance().CsvConfigDlgLabel_encoding + ":");
		_cmbEncoding = createEncodingComboBox();
		createEncodingRadioButtons();
//		//--- Type of Data model
//		_capTypeOfDataModel = new JLabel(RunnerMessages.getInstance().ConversionJsonCsvConfig_label_datamodel+":");
//		createDataModelRadioButtons();
		//--- CSV delimiter
		_capDelimiterChar = new JLabel(RunnerMessages.getInstance().ConversionJsonCsvConfig_label_delimiter + ":");
		_cmbDelimiterChar = createDelimiterCharComboBox();
		//--- CSV header lines
		_capHeaderType = new JLabel(RunnerMessages.getInstance().ConversionJsonCsvConfig_label_header + ":");
		createHeaderLineRadioButtons();
		_spnHeaderLinesEditor = createCustomHeaderLinesEditor();
		//--- JSON-KeyValue format Radio buttons
		_capJsonKeyValueFormat = new JLabel(RunnerMessages.getInstance().ConversionJsonCsvConfig_label_format_keyvalue + ":");
		createJsonKeyValueFormatRadioButtons();
		//--- JSON-KeyValue element separator ComboBox
		_cmbJsonKeyValueElementSeparator = createJsonKeyValueElementSeparatorComboBox();
		//--- JSON-KeyValue separator ComboBox
		_capJsonKeyValueSeparator = new JLabel(RunnerMessages.getInstance().ConversionJsonCsvConfig_Format_keyvaldelim);
		_cmbJsonKeyValueSeparator = createJsonKeyValueSeparatorComboBox();
		//--- JSON-List format Radio buttons
		_capJsonListFormat = new JLabel(RunnerMessages.getInstance().ConversionJsonCsvConfig_label_format_list + ":");
		createJsonListFormatRadioButtons();
		//--- JSON-List element separator ComboBox
		_cmbJsonListElementSeparator = createJsonListElementSeparatorComboBox();
	}
	
	protected JCsvFieldDelimiterCharComboBox createDelimiterCharComboBox() {
		JCsvFieldDelimiterCharComboBox cmb = new JCsvFieldDelimiterCharComboBox();
		cmb.setSelectedIndex(0);
		Dimension dm = cmb.getPreferredSize();
		dm.width += EXPAND_PREFFERED_WIDTH;
		cmb.setMinimumSize(dm);
		cmb.setPreferredSize(dm);
		return cmb;
	}
	
	protected JCharsetComboBox createEncodingComboBox() {
		JCharsetComboBox cmb = new JCharsetComboBox();
		Dimension dm = cmb.getPreferredSize();
		dm.width += EXPAND_PREFFERED_WIDTH;
		cmb.setMinimumSize(dm);
		cmb.setPreferredSize(dm);
		return cmb;
	}
	
	protected void createEncodingRadioButtons() {
		_rdoEncodingDefault = new JRadioButton(CommonMessages.getInstance().labelDefault + "(" + getPrefferedCsvFileEncoding() + ")");
		_rdoEncodingCustom  = new JRadioButton(CommonMessages.getInstance().labelCustom);
	}
	
//	protected void createDataModelRadioButtons() {
//		_rdoDataModelExalge = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_DataModel_Exalge);
//		_rdoDataModelDtalge = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_DataModel_Dtalge);
//		_rdoDataModelCustom = new JRadioButton(CommonMessages.getInstance().labelCustom);
//	}
	
	protected void createHeaderLineRadioButtons() {
		_rdoHeaderNone   = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_CsvHeader_none);
		_rdoHeaderSingle = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_CsvHeader_single);
		_rdoHeaderMulti  = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_CsvHeader_multi);
		if (!_allowHeaderMultiline) {
			// 複数行は許可しない
			_rdoHeaderMulti.setVisible(false);
		}
	}
	
	protected JMaskedNumberSpinner createCustomHeaderLinesEditor() {
		if (_useHeaderMultiLineSpinner) {
			JMaskedNumberSpinner spn = new JMaskedNumberSpinner("#0", Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(10L), Long.valueOf(1L));
			Dimension dm = spn.getPreferredSize();
			dm.width += EXPAND_PREFFERED_WIDTH;
			spn.setMinimumSize(dm);
			spn.setPreferredSize(dm);
			return spn;
		}
		else {
			return null;
		}
	}
	
	protected void createJsonKeyValueFormatRadioButtons() {
		_rdoJsonKeyValueFormatJson   = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_Format_json);
		_rdoJsonKeyValueFormatCustom = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_Format_elemdelim);
	}
	
	protected JConversionElementSeparatorComboBox createJsonKeyValueElementSeparatorComboBox() {
		JConversionElementSeparatorComboBox cmb = new JConversionElementSeparatorComboBox();
		cmb.setSelectedIndex(0);
		Dimension dm = cmb.getPreferredSize();
		dm.width += EXPAND_PREFFERED_WIDTH;
		cmb.setMinimumSize(dm);
		cmb.setPreferredSize(dm);
		return cmb;
	}
	
	protected JConversionKeyValueSeparatorComboBox createJsonKeyValueSeparatorComboBox() {
		JConversionKeyValueSeparatorComboBox cmb = new JConversionKeyValueSeparatorComboBox();
		cmb.setSelectedIndex(0);
		Dimension dm = cmb.getPreferredSize();
		dm.width += EXPAND_PREFFERED_WIDTH;
		cmb.setMinimumSize(dm);
		cmb.setPreferredSize(dm);
		return cmb;
	}
	
	protected void createJsonListFormatRadioButtons() {
		_rdoJsonListFormatJson   = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_Format_json);
		_rdoJsonListFormatCustom = new JRadioButton(RunnerMessages.getInstance().ConversionJsonCsvConfig_Format_elemdelim);
	}
	
	protected JConversionElementSeparatorComboBox createJsonListElementSeparatorComboBox() {
		JConversionElementSeparatorComboBox cmb = new JConversionElementSeparatorComboBox();
		cmb.setSelectedIndex(0);
		Dimension dm = cmb.getPreferredSize();
		dm.width += EXPAND_PREFFERED_WIDTH;
		cmb.setMinimumSize(dm);
		cmb.setPreferredSize(dm);
		return cmb;
	}
	
	protected void setupMainContents() {
		// layout
		final int strutsWidth = 5;
		Box body;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 3, 3);
		gbc.gridy = 0;
		//--- file encoding : caption
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(_capEncoding, gbc);
		//--- file encoding : body
		body = new Box(BoxLayout.LINE_AXIS);
		{
			body.add(_rdoEncodingDefault);
			body.add(Box.createHorizontalStrut(strutsWidth));
			body.add(_rdoEncodingCustom);
			body.add(_cmbEncoding);
		}
		gbc.weightx = 0;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.add(body, gbc);
		//--- file encoding : glue
		gbc.weightx = 1;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.add(new JLabel(), gbc);
		gbc.gridy++;
//		//--- data model : caption
//		gbc.weightx = 0;
//		gbc.gridx = 0;
//		gbc.fill = GridBagConstraints.NONE;
//		gbc.anchor = GridBagConstraints.EAST;
//		this.add(_capTypeOfDataModel, gbc);
//		//--- data model : body
//		body = new Box(BoxLayout.LINE_AXIS);
//		{
//			body.add(_rdoDataModelExalge);
//			body.add(Box.createHorizontalStrut(strutsWidth));
//			body.add(_rdoDataModelDtalge);
//			body.add(Box.createHorizontalStrut(strutsWidth));
//			body.add(_rdoDataModelCustom);
//		}
//		gbc.weightx = 0;
//		gbc.gridx++;
//		gbc.fill = GridBagConstraints.NONE;
//		gbc.anchor = GridBagConstraints.NORTHWEST;
//		this.add(body, gbc);
//		//--- data model : glue
//		gbc.weightx = 1;
//		gbc.gridx++;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.NORTHWEST;
//		this.add(new JLabel(), gbc);
//		gbc.gridy++;
		//--- delimiter : caption
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(_capDelimiterChar, gbc);
		//--- delimiter : body
		gbc.weightx = 0;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.add(_cmbDelimiterChar, gbc);
		//--- delimiter : glue
		gbc.weightx = 1;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.add(new JLabel(), gbc);
		gbc.gridy++;
		//--- csv header : caption
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(_capHeaderType, gbc);
		//--- csv header : body
		body = new Box(BoxLayout.LINE_AXIS);
		{
			body.add(_rdoHeaderNone);
			body.add(Box.createHorizontalStrut(strutsWidth));
			body.add(_rdoHeaderSingle);
			body.add(Box.createHorizontalStrut(strutsWidth));
			body.add(_rdoHeaderMulti);
			if (_spnHeaderLinesEditor != null) {
				body.add(_spnHeaderLinesEditor);
			}
		}
		gbc.weightx = 0;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.add(body, gbc);
		//--- csv header : glue
		gbc.weightx = 1;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		this.add(new JLabel(), gbc);
		gbc.gridy++;
		//--- JSON collections
		if (!_ignoreJsonCollectionFormats) {
			//--- JSON list : caption
			gbc.weightx = 0;
			gbc.gridx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(_capJsonListFormat, gbc);
			//--- JSON list : body
			body = new Box(BoxLayout.LINE_AXIS);
			{
				body.add(_rdoJsonListFormatJson);
				body.add(Box.createHorizontalStrut(strutsWidth));
				body.add(_rdoJsonListFormatCustom);
				body.add(_cmbJsonListElementSeparator);
			}
			gbc.weightx = 0;
			gbc.gridx++;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			this.add(body, gbc);
			//--- JSON list : glue
			gbc.weightx = 1;
			gbc.gridx++;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			this.add(new JLabel(), gbc);
			gbc.gridy++;
			//--- JSON KeyValue : caption
			gbc.weightx = 0;
			gbc.gridx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.EAST;
			this.add(_capJsonKeyValueFormat, gbc);
			//--- file JSON KeyValue : body
			body = new Box(BoxLayout.LINE_AXIS);
			{
				body.add(_rdoJsonKeyValueFormatJson);
				body.add(Box.createHorizontalStrut(strutsWidth));
				body.add(_rdoJsonKeyValueFormatCustom);
				body.add(_cmbJsonKeyValueElementSeparator);
				body.add(Box.createHorizontalStrut(strutsWidth));
				body.add(_capJsonKeyValueSeparator);
				body.add(_cmbJsonKeyValueSeparator);
			}
			gbc.weightx = 0;
			gbc.gridx++;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			this.add(body, gbc);
			//--- JSON list : glue
			gbc.weightx = 1;
			gbc.gridx++;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			this.add(new JLabel(), gbc);
			gbc.gridy++;
		}
	}
	
	protected void setupActions() {
		// radio button event
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRadioButtonActionPerformed(e);
			}
		};
		_rdoEncodingDefault.setActionCommand(CONFIG_ENCODING_DEFAULT);
		_rdoEncodingDefault.addActionListener(al);
		_rdoEncodingCustom.setActionCommand(CONFIG_ENCODING_CUSTOM);
		_rdoEncodingCustom.addActionListener(al);
//		_rdoDataModelExalge.setActionCommand(CONFIG_DATAMODEL_EXALGE);
//		_rdoDataModelExalge.addActionListener(al);
//		_rdoDataModelDtalge.setActionCommand(CONFIG_DATAMODEL_DTALGE);
//		_rdoDataModelDtalge.addActionListener(al);
//		_rdoDataModelCustom.setActionCommand(CONFIG_DATAMODEL_CUSTOM);
//		_rdoDataModelCustom.addActionListener(al);
		_rdoHeaderNone.setActionCommand(CONFIG_HEADERLINE_NONE);
		_rdoHeaderNone.addActionListener(al);
		_rdoHeaderSingle.setActionCommand(CONFIG_HEADERLINE_SINGLE);
		_rdoHeaderSingle.addActionListener(al);
		_rdoHeaderMulti.setActionCommand(CONFIG_HEADERLINE_MULTI);
		_rdoHeaderMulti.addActionListener(al);
		_rdoJsonListFormatJson.setActionCommand(CONFIG_JSONLIST_JSON);
		_rdoJsonListFormatJson.addActionListener(al);
		_rdoJsonListFormatCustom.setActionCommand(CONFIG_JSONLIST_CUSTOM);
		_rdoJsonListFormatCustom.addActionListener(al);
		_rdoJsonKeyValueFormatJson.setActionCommand(CONFIG_JSONKEYVAL_JSON);
		_rdoJsonKeyValueFormatJson.addActionListener(al);
		_rdoJsonKeyValueFormatCustom.setActionCommand(CONFIG_JSONKEYVAL_CUSTOM);
		_rdoJsonKeyValueFormatCustom.addActionListener(al);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
