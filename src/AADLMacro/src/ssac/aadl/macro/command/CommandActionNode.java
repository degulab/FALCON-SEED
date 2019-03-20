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
 * @(#)CommandActionNode.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandActionNode.java	1.00	2008/11/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

import java.util.Iterator;

/**
 * AADLマクロコマンドの構文木のルート要素
 * 
 * @version 2.1.0	2014/05/29
 *
 * @since 1.00
 */
public class CommandActionNode extends CommandNode implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このノードのマクロアクション **/
	private final MacroAction action;
	/**
	 * このノードのコマンド修飾子
	 * @since 2.1.0
	 */
	private CommandModifierNode	_modifier;
	/**
	 * このノードのプロセス名リストのルートノード
	 * @since 2.1.0
	 */
	private CommandProcessNameListNode	_procNameList;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandActionNode(MacroAction action, CommandToken token) {
		super(token);
		this.action = action;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public MacroAction getAction() {
		return action;
	}

	/**
	 * 修飾子の有無を判定する。
	 * @return	修飾子があれば <tt>true</tt>、なければ <tt>false</tt>
	 * @since 2.1.0
	 */
	public boolean hasModifier() {
		return (_modifier != null);
	}

	/**
	 * 修飾子を取得する。
	 * @return	修飾子があればそのオブジェクト、なければ <tt>null</tt>
	 * @since 2.1.0
	 */
	public CommandModifierNode getModifier() {
		return _modifier;
	}

	/**
	 * このノードに修飾子を設定する。
	 * @param node	設定する修飾子を保持するノード
	 * @since 2.1.0
	 */
	public void setModifier(CommandModifierNode node) {
		_modifier = node;
	}

	/**
	 * プロセス名リストの有無を判定する。
	 * @return	プロセス名リストがあれば <tt>true</tt>、なければ <tt>false</tt>
	 * @since 2.1.0
	 */
	public boolean hasProcessNameList() {
		return (_procNameList != null);
	}

	/**
	 * プロセス名リストのルートノードを取得する。
	 * @return	プロセス名リストがあればそのルートノード、なければ <tt>null</tt>
	 * @since 2.1.0
	 */
	public CommandProcessNameListNode getProcessNameListNode() {
		return _procNameList;
	}

	/**
	 * このノードにプロセス名リストのルートノードを設定する。
	 * @param node	設定するプロセス名リストのルートノード
	 * @throws IllegalArgumentException	指定されたノードがプロセス名リストのルートノードではない場合
	 * @since 2.1.0
	 */
	public void setProcessNameListNode(CommandProcessNameListNode node) {
		if (node != null && !node.isEmptyProcessName()) {
			_procNameList = node;
		} else {
			_procNameList = null;
		}
	}

	@Override
	public CommandActionNode clone() throws CloneNotSupportedException
	{
		try {
			CommandActionNode n = (CommandActionNode)super.clone();
			if (_modifier != null) {
				n._modifier = (CommandModifierNode)_modifier.clone();
			}
			if (_procNameList != null) {
				n._procNameList = (CommandProcessNameListNode)_procNameList.clone();
			}
			return n;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		if (action == null)
			sb.append("no action");
		else
			sb.append(action.name());
		sb.append(">");
		if (token == null)
			sb.append("null");
		else {
			sb.append(token.getTypeName());
			String text = token.getText();
			if (text == null)
				sb.append("<no text>");
			else {
				sb.append("\"");
				sb.append(text);
				sb.append("\"");
			}
		}
		boolean hasChild = false;
		//--- modifier
		if (_modifier != null) {
			sb.append("[");
			hasChild = true;
			sb.append(_modifier);
		}
		//--- process name list
		if (_procNameList != null) {
			if (hasChild) {
				sb.append(", ");
			} else {
				sb.append("[");
				hasChild = true;
			}
			sb.append(_procNameList);
		}
		//--- children
		if (children != null && !children.isEmpty()) {
			if (hasChild) {
				sb.append(", ");
			} else {
				sb.append("[");
				hasChild = true;
			}
			Iterator<CommandNode> it = children.iterator();
			sb.append(it.next());
			while (it.hasNext()) {
				sb.append(", ");
				sb.append(it.next());
			}
		}
		if (hasChild) {
			sb.append("]");
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
