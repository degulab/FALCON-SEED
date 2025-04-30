/*
 * @(#)CommandConditionNode.java	1.00	2008/11/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

import ssac.aadl.macro.data.MacroData;

/**
 * AADLマクロコマンドの条件式のルートノード。
 * 
 * @version 1.00	2008/11/14
 *
 * @since 1.00
 */
public class CommandConditionNode extends CommandNode implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandConditionNode(CommandToken token) {
		super(token);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isTrueCondition(final MacroData macroData) {
		return getCondition(macroData, getChild(0));
	}

	@Override
	public CommandConditionNode clone() throws CloneNotSupportedException
	{
		try {
			CommandConditionNode n = (CommandConditionNode)super.clone();
			return n;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
