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
 * @(#)RecognitionException.java	1.00	2008/11/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * マクロコマンドの認証エラー
 * 
 * @version 1.00	2008/11/07
 *
 * @since 1.00
 */
public class RecognitionException extends Exception
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final CommandToken token;
	private final int line;
	private final int posInLine;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public RecognitionException(String message) {
		super(message);
		this.token = null;
		this.line = -1;
		this.posInLine = -1;
	}
	
	public RecognitionException(CommandToken token) {
		this(token, null);
	}
	
	public RecognitionException(CommandTokenizer tokens) {
		this(tokens, null);
	}
	
	public RecognitionException(CommandToken token, String message) {
		super(message);
		this.token = token;
		if (token != null) {
			this.line = token.getLine();
			this.posInLine = token.getPositionInLine();
		} else {
			this.line = -1;
			this.posInLine = -1;
		}
	}
	
	public RecognitionException(CommandTokenizer tokens, String message) {
		super(message);
		if (tokens != null) {
			CommandToken prevToken = tokens.getPreviousToken();
			this.token = tokens.getToken();
			if (this.token != null) {
				this.line = this.token.getLine();
				this.posInLine = this.token.getPositionInLine();
			}
			else if (prevToken != null) {
				this.line = prevToken.getLine();
				String tt = prevToken.getText();
				if (tt != null && tt.length() > 0)
					this.posInLine = prevToken.getPositionInLine() + tt.length();
				else
					this.posInLine = prevToken.getPositionInLine();
			}
			else {
				this.line = -1;
				this.posInLine = -1;
			}
		} else {
			this.token = null;
			this.line = -1;
			this.posInLine = -1;
		}
	}
	
	public RecognitionException(CommandToken token, int line, int posInLine) {
		this(token, line, posInLine, null);
	}
	
	public RecognitionException(CommandToken token, int line, int posInLine, String message) {
		super(message);
		this.token     = token;
		this.line      = line;
		this.posInLine = posInLine;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CommandToken getToken() {
		return token;
	}
	
	public int getTokenType() {
		return (token != null ? token.getType() : CommandToken.UNKNOWN);
	}
	
	public String getTokenTypeName() {
		return (token != null ? token.getTypeName() : "<no token>");
	}
	
	public int getLine() {
		return line;
	}
	
	public int getPositionInLine() {
		return posInLine;
	}
	
	public String getPositionString() {
		return "[line:" + line + ",column:" + (posInLine < 0 ? posInLine : posInLine+1) + "]";
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getClass().getName());
		if (token != null) {
			sb.append(":");
			sb.append(token.toString());
		}
		sb.append(":");
		sb.append(getPositionString());
		String message = getLocalizedMessage();
		if (message != null) {
			sb.append(":");
			sb.append(message);
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
