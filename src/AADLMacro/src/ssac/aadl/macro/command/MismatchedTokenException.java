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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
