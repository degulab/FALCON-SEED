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
 * @(#)CommandNode.java	2.0.0	2014/03/18 : move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandNode.java	1.00	2008/11/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

import static ssac.aadl.macro.util.Validations.validNotNull;

import java.util.ArrayList;
import java.util.Iterator;

import ssac.aadl.macro.data.MacroData;

/**
 * AADLマクロコマンドの構文木要素
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
public class CommandNode implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final CommandToken token;
	
	protected ArrayList<CommandNode> children = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandNode(CommandToken token) {
		this.token = token;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CommandToken getToken() {
		return token;
	}
	
	public boolean hasChildren() {
		return (children != null && !children.isEmpty());
	}
	
	public int getNumChildren() {
		return (children != null ? children.size() : 0);
	}
	
	public CommandNode getChild(int index) {
		if (children != null) {
			try {
				return children.get(index);
			} catch (IndexOutOfBoundsException ignoreEx) {
				ignoreEx = null;
				return null;
			}
		} else {
			return null;
		}
	}
	
	public int addChild(CommandNode child) {
		if (children == null)
			children = new ArrayList<CommandNode>();
		int retIndex = children.size();
		children.add(validNotNull(child));
		return retIndex;
	}
	
	public int insertChild(int index, CommandNode child) {
		if (children == null)
			children = new ArrayList<CommandNode>();
		int retIndex;
		if (index < 0) {
			retIndex = 0;
			children.add(0, validNotNull(child));
		}
		else if (index >= children.size()) {
			retIndex = children.size();
			children.add(validNotNull(child));
		}
		else {
			retIndex = index;
			children.add(index, validNotNull(child));
		}
		return retIndex;
	}
	
	public CommandNode removeChild(int index) {
		CommandNode retNode;
		if (children != null) {
			try {
				retNode = children.remove(index);
			} catch (IndexOutOfBoundsException ignoreEx) {
				ignoreEx = null;
				retNode = null;
			}
		} else {
			retNode = null;
		}
		return retNode;
	}
	
	public boolean removeChild(CommandNode node) {
		if (children != null)
			return children.remove(node);
		else
			return false;
	}
	
	public void removeAllChildren() {
		if (children != null) {
			children.clear();
			children = null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CommandNode clone() throws CloneNotSupportedException
	{
		try {
			CommandNode n = (CommandNode)super.clone();
			if (this.children != null) {
				n.children = (ArrayList<CommandNode>)this.children.clone();
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
		if (children != null && !children.isEmpty()) {
			sb.append("[");
			Iterator<CommandNode> it = children.iterator();
			sb.append(it.next());
			while (it.hasNext()) {
				sb.append(", ");
				sb.append(it.next());
			}
			sb.append("]");
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたノードのコンディションを取得する。
	 * <p>
	 * コンディションは、このメソッド呼び出し時点での参照プロセス名の終了コードを
	 * 条件式により判定した真偽値となる。
	 * トークンが存在しないノードでは、常に <tt>false</tt> を返す。
	 * 
	 * @param macroData	プロセス名に対応する終了コードを保持するマクロデータ
	 * @param node			対象ノード
	 * @return	条件式による判定結果
	 */
	static protected boolean getCondition(final MacroData macroData, final CommandNode node) {
		CommandToken token = node.getToken();
		if (token == null) {
			//--- トークン未定義のため、false
			return false;
		}

		switch (token.getType()) {
			case CommandToken.OR :
			{
				//--- 子ノードの条件式のどれか一つが true なら true
				boolean retCond = false;
				for (int i = 0; i < node.getNumChildren(); i++) {
					retCond = getCondition(macroData, node.getChild(i));
					if (retCond)
						break;
				}
				return retCond;
			}
			case CommandToken.AND :
			{
				//--- 子ノードの条件式の全てが true なら true
				boolean retCond = false;
				for (int i = 0; i < node.getNumChildren(); i++) {
					retCond = getCondition(macroData, node.getChild(i));
					if (!retCond)
						break;
				}
				return retCond;
			}
			case CommandToken.NOT :
				//--- 子ノードの条件式の結果が true なら false
				return (!getCondition(macroData, node.getChild(0)));
			case CommandToken.EQUAL :
			case CommandToken.NOTEQUAL :
			case CommandToken.GREATER :
			case CommandToken.GREATEREQUAL :
			case CommandToken.LESS :
			case CommandToken.LESSEQUAL :
				//--- 値の比較結果
				return compareValues(macroData, token, node.getChild(0), node.getChild(1));
			case CommandToken.PLUS :
			case CommandToken.MINUS :
			case CommandToken.IDENTIFIER :
			case CommandToken.NUMBER :
			{
				//--- 数値の取得
				Integer integer = getIntegerValue(macroData, node);
				return (integer == null ? false : (integer.intValue() != 0));
			}
			default :
				throw new AssertionError();
		}
	}
	
	static protected Integer getIntegerValue(final MacroData macroData, final CommandNode node) {
		CommandToken token = node.getToken();
		switch (token.getType()) {
			case CommandToken.IDENTIFIER :
				return macroData.getProcessExitCode(token.getText());
			case CommandToken.NUMBER :
				try {
					return Integer.valueOf(token.getText());
				} catch (NumberFormatException ex) {
					throw new AssertionError(ex);
				}
			case CommandToken.PLUS :
				return getIntegerValue(macroData, node.getChild(0));
			case CommandToken.MINUS :
			{
				Integer integer = getIntegerValue(macroData, node.getChild(0));
				return (integer == null ? null : new Integer(-integer.intValue()));
			}
			default :
				throw new AssertionError();
		}
	}
	
	static protected boolean compareValues(final MacroData macroData, final CommandToken token, final CommandNode node1, final CommandNode node2) {
		Integer ret1 = getIntegerValue(macroData, node1);
		Integer ret2 = getIntegerValue(macroData, node2);
		if (ret1 == null || ret2 == null) {
			return false;	// force by any null.
		}
		
		switch (token.getType()) {
			case CommandToken.EQUAL :
				return (ret1.intValue() == ret2.intValue());
			case CommandToken.NOTEQUAL :
				return (ret1.intValue() != ret2.intValue());
			case CommandToken.GREATER :
				return (ret1.intValue() > ret2.intValue());
			case CommandToken.GREATEREQUAL :
				return (ret1.intValue() >= ret2.intValue());
			case CommandToken.LESS :
				return (ret1.intValue() < ret2.intValue());
			case CommandToken.LESSEQUAL :
				return (ret1.intValue() <= ret2.intValue());
			default :
				throw new AssertionError();
		}
	}
}
