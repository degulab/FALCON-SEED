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
 * @(#)MacroAction.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroAction.java	1.00	2008/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * AADLマクロ要素のアクション種別を示す列挙型。
 * 単独実行可能なコマンドのみの情報を保持する。
 * 
 * @version 2.1.0	2014/05/29
 *
 * @since 1.00
 */
public enum MacroAction
{
	/** 'java' コマンド **/
	JAVA("java", false, MacroModifier.values()),
	/** '&' コマンド **/
	GROUP("&", false),
	/** 'exec' コマンド **/
	SHELL("exec", false, MacroModifier.values()),
	/** 'macro' コマンド **/
	MACRO("macro", false, MacroModifier.values()),
	/** 'echo' コマンド **/
	ECHO("echo", false, MacroModifier.AFTER),
	/** コメント **/
	COMMENT("#", false),
	/** エラー終了条件コマンド **/
	ERRORCOND("errorcond", false, MacroModifier.AFTER),
	/**
	 * 'wait' コマンド
	 * @since 2.1.0
	 */
	WAIT("wait", true),
	/** 終了コマンド **/
	EXIT("exit", false, MacroModifier.AFTER),
	;
	
	/** コマンド名 **/
	private final String	_command;
	/**
	 * プロセス名リストが必須の場合は <tt>true</tt>
	 * @since 2.1.0
	 */
	private final boolean	_requiredProcNameList;
	/**
	 * 指定可能な修飾子の集合
	 * @since 2.1.0
	 */
	private final Set<MacroModifier>	_arrowModifierSet;

	private MacroAction(String command, boolean requiredProcNameList, MacroModifier...arrowModifiers) {
		this._command = command;
		this._requiredProcNameList = requiredProcNameList;
		if (arrowModifiers != null && arrowModifiers.length > 0)
			_arrowModifierSet = Collections.unmodifiableSet(new HashSet<MacroModifier>(Arrays.asList(arrowModifiers)));
		else
			_arrowModifierSet = null;
	}
	
	public String commandString() {
		return _command;
	}

	/**
	 * プロセス名リストが必須の場合に <tt>true</tt> を返す。
	 * @return	プロセス名リストが必須のアクションなら <tt>true</tt>
	 * @since 2.1.0
	 */
	public boolean isRequiredProcessNameList() {
		return _requiredProcNameList;
	}

	/**
	 * 指定された修飾子を指定可能なアクションかどうかを判定する。
	 * @param modifier	判定する修飾子
	 * @return	指定された修飾子が指定可能なら <tt>true</tt>
	 * @since 2.1.0
	 */
	public boolean isModifierArrowed(final MacroModifier modifier) {
		if (_arrowModifierSet != null)
			return _arrowModifierSet.contains(modifier);
		else
			return false;
	}

	static public MacroAction fromCommand(String cmdText) {
		MacroAction retAction = null;
		
		if (cmdText != null && cmdText.length() > 0) {
			if (cmdText.length() == 1) {
				if (GROUP._command.equals(cmdText))
					retAction = GROUP;
				else if (COMMENT._command.equals(cmdText))
					retAction = COMMENT;
			}
			else if (cmdText.startsWith(COMMENT._command))
				retAction = COMMENT;
			else if (JAVA._command.equalsIgnoreCase(cmdText))
				retAction = JAVA;
			else if (SHELL._command.equalsIgnoreCase(cmdText))
				retAction = SHELL;
			else if (ECHO._command.equalsIgnoreCase(cmdText))
				retAction = ECHO;
			else if (MACRO._command.equalsIgnoreCase(cmdText))
				retAction = MACRO;
			else if (ERRORCOND._command.equalsIgnoreCase(cmdText))
				retAction = ERRORCOND;
			else if (EXIT._command.equalsIgnoreCase(cmdText))
				retAction = EXIT;
			else if (WAIT._command.equalsIgnoreCase(cmdText))
				retAction = WAIT;
		}
		else {
			retAction = JAVA;
		}
		
		return retAction;
	}
}
