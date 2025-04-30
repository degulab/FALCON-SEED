/*
 * @(#)JConversionKeyValueSeparatorComboBox.java	3.4.0	2020/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import javax.swing.JComboBox;

import ssac.falconseed.runner.RunnerMessages;

/**
 * JSON-CSV 変換設定の、KeyValue 区切り文字を選択するコンボボックス・コンポーネント。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class JConversionKeyValueSeparatorComboBox extends JComboBox<String>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -4013302969080350838L;
	
	static public final int	ITEMIDX_SEPARATOR_EQUAL	= 0;
	static public final int	ITEMIDX_SEPARATOR_COLON	= 1;
	
	static public final String	ELEM_SEPARATOR_EQUAL	= "=";
	static public final String	ELEM_SEPARATOR_COLON	= ":";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JConversionKeyValueSeparatorComboBox() {
		super();
		addItem(RunnerMessages.getInstance().ConversionJsonCsvConfig_JsonKeyValueDelim_equal);
		addItem(RunnerMessages.getInstance().ConversionJsonCsvConfig_JsonKeyValueDelim_colon);
		setSelectedIndex(0);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 区切り文字のデフォルトを返す。
	 * @return	デフォルトの区切り文字
	 */
	static public String getDefaultSeparator() {
		return ELEM_SEPARATOR_EQUAL;
	}
	
	/**
	 * 指定された区切り文字に対応するインデックスを取得する。
	 * @param separator	区切り文字
	 * @return	<em>separator</em> に対応するインデックス、対応するものがない場合は (-1)
	 */
	public int indexOf(String separator) {
		if (ELEM_SEPARATOR_COLON.equals(separator)) {
			return ITEMIDX_SEPARATOR_COLON;
		}
		else if (ELEM_SEPARATOR_EQUAL.equals(separator)) {
			return ITEMIDX_SEPARATOR_EQUAL;
		}
		else {
			return (-1);
		}
	}
	
	/**
	 * 指定されたインデックスに対応する区切り文字を取得する。
	 * @param index	コンボボックスのアイテムインデックス
	 * @return	<em>index</em> に対応する区切り文字、対応するものがない場合は <tt>null</tt>
	 */
	public String separatorAt(int index) {
		if (index == ITEMIDX_SEPARATOR_COLON) {
			return ELEM_SEPARATOR_COLON;
		}
		else if (index >= 0) {
			return ELEM_SEPARATOR_EQUAL;
		}
		else {
			return null;
		}
	}
	
	/**
	 * 現在選択されている区切り文字を取得する。
	 * @return	選択されている区切り文字、選択されていない場合は <tt>null</tt>
	 */
	public String getSelectedSeparator() {
		return separatorAt(getSelectedIndex());
	}
	
	/**
	 * 指定された区切り文字を選択する。
	 * <em>separator</em> が候補にない場合、未選択状態となる。
	 * @param separator	選択する区切り文字
	 */
	public void setSelectedSeparator(String separator) {
		setSelectedIndex(indexOf(separator));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
