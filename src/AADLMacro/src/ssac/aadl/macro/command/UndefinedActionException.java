/*
 * @(#)UndefindActionException.java	1.00	2008/11/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * 未定義のマクロアクションを示すエラー
 * 
 * @version 1.00	2008/11/07
 *
 * @since 1.00
 */
public class UndefinedActionException extends RecognitionException
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
	
	public UndefinedActionException(CommandToken token) {
		super(token, generateMessage(token));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private String generateMessage(CommandToken token) {
		if (token != null)
			return "Undefined macro action : \'" + token.getText() + "\'";
		else
			return "Undefined empty macro action.";
	}
}
