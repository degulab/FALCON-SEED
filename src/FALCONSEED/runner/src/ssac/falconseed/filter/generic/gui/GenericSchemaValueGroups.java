/*
 * @(#)GenericSchemaValueGroups.java	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import ssac.falconseed.runner.RunnerMessages;

/**
 * 汎用フィルタ定義における最小要素となる値の所属を示す列挙値。
 * @version 3.2.0
 * @since 3.2.0
 */
public enum GenericSchemaValueGroups
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	INPUT("GenericSchemaInput", RunnerMessages.getInstance().GenericFilterEditDlg_name_InputSchema),
	OUTPUT("GenericSchemaOutput", RunnerMessages.getInstance().GenericFilterEditDlg_name_OutputSchema),
	EXP("GenericSchemaArithExp", RunnerMessages.getInstance().GenericFilterEditDlg_name_ArithExp),
	JOIN("GenericSchemaJoin", RunnerMessages.getInstance().GenericFilterEditDlg_name_JoinCond),
	WHERE("GenericSchemaWhere", RunnerMessages.getInstance().GenericFilterEditDlg_name_WhereCond),
	F_ARG("FilterArgument", RunnerMessages.getInstance().GenericFilterEditDlg_name_FilterArg),
	LITERAL("GenericSchemaLiteral", RunnerMessages.getInstance().GenericFilterEditDlg_name_Literal);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String	_typeName;
	private final String	_dispName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * このインスタンスの名前を設定する。
	 */
	private GenericSchemaValueGroups(String typeName, String dispName) {
		this._typeName = typeName;
		this._dispName = dispName;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このインスタンスに設定されている識別名を返す。
	 */
	public String typeName() {
		return _typeName;
	}

	/**
	 * このインスタンスに設定されている表示名を返す。
	 */
	public String displayName() {
		return _dispName;
	}
	
	/**
	 * 名前に対応する種類を返す。
	 * @param name	判定する名前
	 * @return	対応する種類を返す。
	 * 			<em>name</em> に指定された名前がどの種類にも一致しない場合は <tt>null</tt> を返す。
	 */
	static public GenericSchemaValueGroups fromTypeName(String typeName) {
		GenericSchemaValueGroups retType = null;
		
		if (typeName != null && !typeName.isEmpty()) {
			if (INPUT._typeName.equalsIgnoreCase(typeName)) {
				retType = INPUT;
			}
			else if (OUTPUT._typeName.equalsIgnoreCase(typeName)) {
				retType = OUTPUT;
			}
			else if (EXP._typeName.equalsIgnoreCase(typeName)) {
				retType = EXP;
			}
			else if (JOIN._typeName.equalsIgnoreCase(typeName)) {
				retType = JOIN;
			}
			else if (WHERE._typeName.equalsIgnoreCase(typeName)) {
				retType = WHERE;
			}
			else if (F_ARG._typeName.equalsIgnoreCase(typeName)) {
				retType = F_ARG;
			}
			else if (LITERAL._typeName.equalsIgnoreCase(typeName)) {
				retType = LITERAL;
			}
		}
		// 対応しない名前の場合は null を返す

		return retType;
	}

	/**
	 * このインスタンスの表示名を返す。
	 * @see #displayName()
	 */
	@Override
	public String toString() {
		return _dispName;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
