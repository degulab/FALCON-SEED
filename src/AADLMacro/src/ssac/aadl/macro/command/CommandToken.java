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
 * @(#)CommandToken.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandToken.java	1.00	2008/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * マクロコマンドのトークン。
 * <p>このクラスは不変オブジェクト(Immutable)である。
 * 
 * @version 2.1.0	2014/05/29
 * @since 1.00
 */
public class CommandToken
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int TYPE_BASE = 16777216;	// 0xFFFFFF
	
	static public final int UNKNOWN      = 0;
	
	static public final int COMMENT      = (int)'#';
	static public final int GROUP        = (int)'&';
	static public final int LESS         = (int)'<';
	static public final int GREATER      = (int)'>';
	static public final int LESSEQUAL    = TYPE_BASE+1;
	static public final int GREATEREQUAL = TYPE_BASE+2;
	static public final int LPAREN       = (int)'(';
	static public final int RPAREN       = (int)')';
	static public final int LBRACKET     = (int)'[';
	static public final int RBRACKET     = (int)']';
	static public final int LCURLY       = (int)'{';
	static public final int RCURLY       = (int)'}';
	static public final int PLUS         = (int)'+';
	static public final int MINUS        = (int)'-';
	static public final int STAR         = (int)'*';
	static public final int SLASH        = (int)'/';
	static public final int DOLLER       = (int)'$';
	static public final int ASSIGN       = (int)'=';
	static public final int NOT          = (int)'!';
	static public final int PERCENT      = (int)'%';
	static public final int VBAR         = (int)'|';
	/** @since 2.1.0 **/
	static public final int COLON        = (int)':';
	/** @since 2.1.0 **/
	static public final int COMMA        = (int)',';
	static public final int EQUAL          = TYPE_BASE+3;
	static public final int NOTEQUAL       = TYPE_BASE+4;
	static public final int AND            = TYPE_BASE+5;
	static public final int OR             = TYPE_BASE+6;
	static public final int NUMBER         = TYPE_BASE+7;
	static public final int IDENTIFIER     = TYPE_BASE+8;
	static public final int REF_IDENTIFIER = TYPE_BASE+9;

	static public final int ACTION       = TYPE_BASE+10;
	static public final int COND_EXP     = TYPE_BASE+11;
	static public final int TERM_EXP     = TYPE_BASE+12;
	/** @since 2.1.0 **/
	static public final int MODIFIER     = TYPE_BASE+13;
	/** @since 2.1.0 **/
	static public final int PROCID_LIST  = TYPE_BASE+14;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final int type;
	private final int line;
	private final int posInLine;
	private final String text;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandToken(int type) {
		this(type, -1, -1, null);
	}
	
	public CommandToken(int type, String text) {
		this(type, -1, -1, text);
	}
	
	public CommandToken(int type, int line, int posInLine, String text) {
		this.type = type;
		this.line = line;
		this.posInLine = posInLine;
		this.text = text;
	}
	
	public CommandToken(int type, CommandToken oldToken) {
		this(type, oldToken.line, oldToken.posInLine, oldToken.text);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getType() {
		return type;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getPositionInLine() {
		return posInLine;
	}
	
	public String getText() {
		return text;
	}
	
	public String getTypeName() {
		switch (type) {
			case COMMENT:
				return "COMMENT";
			case GROUP:
				return "GROUP";
			case LESS:
				return "LESS";
			case GREATER:
				return "GREATER";
			case LESSEQUAL:
				return "LESSEQUAL";
			case GREATEREQUAL:
				return "GREATEREQUAL";
			case LPAREN:
				return "LPAREN";
			case RPAREN:
				return "RPAREN";
			case LBRACKET:
				return "LBRACKET";
			case RBRACKET:
				return "RBRACKET";
			case LCURLY:
				return "LCURLY";
			case RCURLY:
				return "RCURLY";
			case PLUS:
				return "PLUS";
			case MINUS:
				return "MINUS";
			case STAR:
				return "STAR";
			case SLASH:
				return "SLASH";
			case DOLLER:
				return "DOLLER";
			case ASSIGN:
				return "ASSIGN";
			case NOT:
				return "NOT";
			case PERCENT:
				return "PERCENT";
			case VBAR:
				return "VBAR";
			case COLON:
				return "COLON";
			case COMMA:
				return "COMMA";
			case EQUAL:
				return "EQUAL";
			case NOTEQUAL:
				return "NOTEQUAL";
			case AND:
				return "AND";
			case OR:
				return "OR";
			case IDENTIFIER:
				return "IDENTIFIER";
			case NUMBER:
				return "NUMBER";
			case ACTION:
				return "ACTION";
			case COND_EXP:
				return "COND_EXP";
			case TERM_EXP:
				return "TERM_EXP";
			case MODIFIER:
				return "MODIFIER";
			case PROCID_LIST:
				return "PROCID_LIST";
			default:
				return ("UNKNOWN(0x"+Integer.toHexString(type)+")");
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(getTypeName());
		sb.append(">[0x");
		sb.append(Integer.toHexString(type));
		sb.append(',');
		sb.append(line);
		sb.append(',');
		sb.append(posInLine);
		sb.append(',');
		if (text == null)
			sb.append("<null>");
		else {
			sb.append("\"");
			sb.append(text);
			sb.append("\"");
		}
		sb.append("]");
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
