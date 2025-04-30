/*
 * @(#)CommandModifierNode.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

import java.util.Iterator;

/**
 * AADLマクロコマンド修飾子の構文木のルート要素
 * 
 * @version 2.1.0	2014/05/29
 * @since 2.1.0
 */
public class CommandModifierNode extends CommandNode implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 修飾子 **/
	private final MacroModifier _modifier;
	/**
	 * このノードのプロセス名リストのルートノード
	 */
	private CommandProcessNameListNode	_procNameList;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandModifierNode(MacroModifier modifier, CommandToken token) {
		super(token);
		this._modifier = modifier;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public MacroModifier getModifier() {
		return _modifier;
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
	public CommandModifierNode clone()
	{
		try {
			CommandModifierNode n = (CommandModifierNode)super.clone();
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
		if (_modifier == null)
			sb.append("no modifier");
		else
			sb.append(_modifier.name());
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
		//--- process name list
		if (_procNameList != null) {
			sb.append("[");
			hasChild = true;
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
