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
