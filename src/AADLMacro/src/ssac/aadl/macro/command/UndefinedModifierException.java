/*
 * @(#)UndefinedModifierException.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * 未定義のマクロアクション修飾子を示すエラー
 * 
 * @version 2.1.0	2014/05/29
 * @since 2.1.0
 */
public class UndefinedModifierException extends RecognitionException
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
	
	public UndefinedModifierException(CommandToken token) {
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
			return "Undefined macro action modifier : \'" + token.getText() + "\'";
		else
			return "Undefined empty macro action modifier.";
	}
}
