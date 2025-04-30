/*
 * @(#)JConversionElementSeparatorComboBox.java	3.4.0	2020/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import javax.swing.JComboBox;

import ssac.falconseed.runner.RunnerMessages;

/**
 * JSON-CSV 変換設定の、要素区切り文字を選択するコンボボックス・コンポーネント。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class JConversionElementSeparatorComboBox extends JComboBox<String>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1030772303373941651L;
	
	static public final int	ITEMIDX_SEPARATOR_COMMA	= 0;
	static public final int	ITEMIDX_SEPARATOR_TAB	= 1;
	static public final int	ITEMIDX_SEPARATOR_SPACE	= 2;
	
	static public final String	ELEM_SEPARATOR_COMMA	= ",";
	static public final String	ELEM_SEPARATOR_TAB		= "\t";
	static public final String	ELEM_SEPARATOR_SPACE	= " ";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JConversionElementSeparatorComboBox() {
		super();
		addItem(RunnerMessages.getInstance().ConversionJsonCsvConfig_JsonElemDelim_comma);
		addItem(RunnerMessages.getInstance().ConversionJsonCsvConfig_JsonElemDelim_tab);
		addItem(RunnerMessages.getInstance().ConversionJsonCsvConfig_JsonElemDelim_space);
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
		return ELEM_SEPARATOR_COMMA;
	}
	
	/**
	 * 指定された区切り文字に対応するインデックスを取得する。
	 * @param separator	区切り文字
	 * @return	<em>separator</em> に対応するインデックス、対応するものがない場合は (-1)
	 */
	public int indexOf(String separator) {
		if (ELEM_SEPARATOR_SPACE.equals(separator)) {
			return ITEMIDX_SEPARATOR_SPACE;
		}
		else if (ELEM_SEPARATOR_TAB.equals(separator)) {
			return ITEMIDX_SEPARATOR_TAB;
		}
		else if (ELEM_SEPARATOR_COMMA.equals(separator)) {
			return ITEMIDX_SEPARATOR_COMMA;
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
		if (index == ITEMIDX_SEPARATOR_SPACE) {
			return ELEM_SEPARATOR_SPACE;
		}
		else if (index == ITEMIDX_SEPARATOR_TAB) {
			return ELEM_SEPARATOR_TAB;
		}
		else if (index >= 0) {
			return ELEM_SEPARATOR_COMMA;
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
