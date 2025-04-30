/*
 * @(#)JCharsetComboBox.java	3.4.0	2020/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import javax.swing.JComboBox;

import ssac.aadl.common.CommonMessages;
import ssac.util.nio.csv.CsvUtil;

/**
 * CSV フィールド区切り文字を選択するコンボボックス・コンポーネント。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class JCsvFieldDelimiterCharComboBox extends JComboBox<String>
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -1806954858377764742L;
	
	static public final int	ITEMIDX_DELIMITER_CSV	= 0;
	static public final int	ITEMIDX_DELIMITER_TSV	= 1;
	static public final int	ITEMIDX_DELIMITER_SSV	= 2;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JCsvFieldDelimiterCharComboBox() {
		super();
		addItem(CommonMessages.getInstance().CsvConfigDlgDelim_comma);
		addItem(CommonMessages.getInstance().CsvConfigDlgDelim_tab);
		addItem(CommonMessages.getInstance().CsvConfigDlgDelim_space);
		setSelectedIndex(0);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * CSV フィールド区切り文字のデフォルト文字を返す。
	 * @return	カンマを表す文字
	 */
	static public char getDefaultDelimiterChar() {
		return CsvUtil.CSV_DELIMITER_CHAR;
	}
	
	/**
	 * 指定された CSV フィールド区切り文字に対応するインデックスを取得する。
	 * @param delim	CSV フィールド区切り文字
	 * @return	<em>delim</em> に対応するインデックス、対応するものがない場合は (-1)
	 */
	public int indexOf(char delim) {
		if (delim == CsvUtil.SSV_DELIMITER_CHAR) {
			return ITEMIDX_DELIMITER_SSV;
		}
		else if (delim == CsvUtil.TSV_DELIMITER_CHAR) {
			return ITEMIDX_DELIMITER_TSV;
		}
		else if (delim == CsvUtil.CSV_DELIMITER_CHAR) {
			return ITEMIDX_DELIMITER_CSV;
		}
		else {
			return (-1);
		}
	}
	
	/**
	 * 指定されたインデックスに対応する CSV フィールド区切り文字を取得する。
	 * @param index	コンボボックスのアイテムインデックス
	 * @return	<em>index</em> に対応する CSV フィールド区切り文字、対応するものがない場合は 0
	 */
	public char delimiterCharAt(int index) {
		if (index == ITEMIDX_DELIMITER_SSV) {
			return CsvUtil.SSV_DELIMITER_CHAR;
		}
		else if (index == ITEMIDX_DELIMITER_TSV) {
			return CsvUtil.TSV_DELIMITER_CHAR;
		}
		else if (index >= 0){
			return CsvUtil.CSV_DELIMITER_CHAR;
		}
		else {
			return '\0';
		}
	}
	
	/**
	 * 現在選択されている CSV フィールド区切り文字を取得する。
	 * @return	選択されている区切り文字、選択されていない場合は 0
	 */
	public char getSelectedDelimiterChar() {
		return delimiterCharAt(getSelectedIndex());
	}
	
	/**
	 * 指定された CSV フィールド区切り文字を選択する。
	 * <em>delim</em> が候補にない場合、未選択状態となる。
	 * @param delim	選択する CSV フィールド区切り文字
	 */
	public void setSelectedDelimiterChar(char delim) {
		setSelectedIndex(indexOf(delim));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
