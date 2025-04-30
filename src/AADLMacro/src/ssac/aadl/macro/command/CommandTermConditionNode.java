/*
 * @(#)CommandTermConditionNode.java	1.00	2008/11/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * AADLマクロコマンドの終了条件式のルートノード。
 * 
 * @version 1.00	2008/11/14
 *
 * @since 1.00
 */
public class CommandTermConditionNode extends CommandNode implements Cloneable
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
	
	public CommandTermConditionNode(CommandToken token) {
		super(token);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isTermExitCode(int exitCode) {
		CommandNode condNode = getChild(0);
		CommandNode valNode = condNode.getChild(0);
		CommandToken token = condNode.getToken();
		
		switch (token.getType()) {
			case CommandToken.EQUAL :
				return (exitCode == getIntegerValue(null, valNode));
			case CommandToken.NOTEQUAL :
				return (exitCode != getIntegerValue(null, valNode));
			case CommandToken.GREATER :
				return (exitCode > getIntegerValue(null, valNode));
			case CommandToken.GREATEREQUAL :
				return (exitCode >= getIntegerValue(null, valNode));
			case CommandToken.LESS :
				return (exitCode < getIntegerValue(null, valNode));
			case CommandToken.LESSEQUAL :
				return (exitCode <= getIntegerValue(null, valNode));
			default :
				throw new AssertionError();
		}
	}

	@Override
	public CommandTermConditionNode clone() throws CloneNotSupportedException
	{
		try {
			CommandTermConditionNode n = (CommandTermConditionNode)super.clone();
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
