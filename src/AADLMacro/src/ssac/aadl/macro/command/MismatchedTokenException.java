/*
 * @(#)MismatchedTokenException.java	1.00	2008/11/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * トークンが要求タイプと一致しないエラー。
 * 
 * @version 1.00	2008/11/07
 *
 * @since 1.00
 */
public class MismatchedTokenException extends RecognitionException
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
	
	public MismatchedTokenException(CommandToken token, String expectings) {
		super(token, makeErrorMessage(token, expectings));
	}
	
	public MismatchedTokenException(CommandTokenizer tokens, String expectings) {
		super(tokens, makeErrorMessage(tokens.getToken(), expectings));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String makeErrorMessage(CommandToken token, String expectings) {
		String txtToken = (token != null ? token.getText() : "<End of text>");
		if (expectings != null && expectings.length() > 0)
			return String.format("Mismatched '%s' token, expecting %s.", txtToken, expectings);
		else
			return String.format("Mismatched '%s' token.", txtToken);
	}
}
