/*
 * @(#)MacroModifier.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * AADLマクロ要素のアクションに付随する修飾子種別を示す列挙型。
 * 
 * @version 2.1.0	2014/05/29
 * @since 2.1.0
 */
public enum MacroModifier
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 'start' 修飾子 **/
	START("start", false),
	/** 'after' 修飾子 **/
	AFTER("after", true),	// プロセス名リスト必須
	;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** プロセス名リストが必須なら <tt>true</tt> **/
	private final boolean	_requiredProcNameList;
	/** 修飾子の名前 **/
	private final String 	_modifier;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	private MacroModifier(String modifier, boolean requiredProcNameList) {
		_modifier = modifier;
		_requiredProcNameList = requiredProcNameList;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String modifierString() {
		return _modifier;
	}

	/**
	 * プロセス名リストが必須の場合に <tt>true</tt> を返す。
	 * @return	プロセス名リストが必須の修飾子なら <tt>true</tt>
	 */
	public boolean isRequiredProcessNameList() {
		return _requiredProcNameList;
	}
	
	static public MacroModifier fromCommand(String modifierText) {
		MacroModifier retModifier = null;
		
		if (modifierText != null && modifierText.length() > 0) {
			if (START._modifier.equalsIgnoreCase(modifierText))
				retModifier = START;
			else if (AFTER._modifier.equalsIgnoreCase(modifierText))
				retModifier = AFTER;
		}
		
		return retModifier;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
