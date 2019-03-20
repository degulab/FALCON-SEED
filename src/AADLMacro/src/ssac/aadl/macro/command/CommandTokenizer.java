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
 * @(#)CommandTokenizer.java	1.00	2008/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;


/**
 * マクロコマンドのトークン解析。
 * 
 * @version 1.00	2008/11/06
 *
 * @since 1.00
 */
public class CommandTokenizer extends AbstractTokenizer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String marks = "#&|$%(){}[]!=<>+-*/\'\"^~\\;:?.,@`_";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandTokenizer(String text) {
		super(text);
		analyz();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void mAND(TextLocation loc) {
		if (read(loc.getIndex()+1) == '&') {
			addToken(CommandToken.AND, loc, getText(loc.getIndex(), 2));
			loc.seek(2);
		} else {
			addToken(CommandToken.GROUP, loc, getText(loc.getIndex(), 1));
			loc.seek(1);
		}
	}
	
	private void mOR(TextLocation loc) {
		if (read(loc.getIndex()+1) == '|') {
			addToken(CommandToken.OR, loc, getText(loc.getIndex(), 2));
			loc.seek(2);
		} else {
			addToken(CommandToken.VBAR, loc, getText(loc.getIndex(), 1));
			loc.seek(1);
		}
	}
	
	private void mEQUAL(TextLocation loc) {
		if (read(loc.getIndex()+1) == '=') {
			addToken(CommandToken.EQUAL, loc, getText(loc.getIndex(), 2));
			loc.seek(2);
		} else {
			addToken(CommandToken.ASSIGN, loc, getText(loc.getIndex(), 1));
			loc.seek(1);
		}
	}
	
	private void mNOTEQUAL(TextLocation loc) {
		if (read(loc.getIndex()+1) == '=') {
			addToken(CommandToken.NOTEQUAL, loc, getText(loc.getIndex(), 2));
			loc.seek(2);
		} else {
			addToken(CommandToken.NOT, loc, getText(loc.getIndex(), 1));
		}
	}
	
	private void mGREATER(TextLocation loc) {
		if (read(loc.getIndex()+1) == '=') {
			addToken(CommandToken.GREATEREQUAL, loc, getText(loc.getIndex(), 2));
			loc.seek(2);
		} else {
			addToken(CommandToken.GREATER, loc, getText(loc.getIndex(), 1));
			loc.seek(1);
		}
	}
	
	private void mLESS(TextLocation loc) {
		if (read(loc.getIndex()+1) == '=') {
			addToken(CommandToken.LESSEQUAL, loc, getText(loc.getIndex(), 2));
			loc.seek(2);
		} else {
			addToken(CommandToken.LESS, loc, getText(loc.getIndex(), 1));
			loc.seek(1);
		}
	}
	
	private void mIDENTIFIER(TextLocation loc) {
		int np = loc.getIndex()+1;
		while (np < srcLength) {
			char c = srcText.charAt(np);
			if (!isIdentifierStart(c) && !isDigit(c)) {
				break;
			}
			np++;
		}
		addToken(CommandToken.IDENTIFIER, loc, srcText.substring(loc.getIndex(), np));
		loc.seek(np-loc.getIndex());
	}
	
	private void mNUMBER(TextLocation loc) {
		int np = loc.getIndex()+1;
		while (np < srcLength) {
			char c = srcText.charAt(np);
			if (!isDigit(c)) {
				break;
			}
			np++;
		}
		addToken(CommandToken.NUMBER, loc, srcText.substring(loc.getIndex(), np));
		loc.seek(np-loc.getIndex());
	}
	
	private void mUNKNOWN(TextLocation loc) {
		int pos = loc.getIndex();
		while (pos < srcLength) {
			char c = srcText.charAt(pos);
			if (isIdentifierStart(c) || isDigit(c) || Character.isWhitespace(c)) {
				break;
			}
			else if (marks.indexOf(c) >= 0) {
				break;
			}
			pos += Character.charCount(srcText.codePointAt(pos));
		}
		addToken(CommandToken.UNKNOWN, loc, srcText.substring(loc.getIndex(), pos));
		loc.seek(pos-loc.getIndex());
	}
	
	private void analyz() {
		TextLocation loc = new TextLocation();
		
		while (loc.getIndex() < srcLength) {
			skipWhitespace(loc);
			if (loc.getIndex() >= srcLength)
				break;	// source text terminated.
			
			char c = srcText.charAt(loc.getIndex());
			switch (c) {
				case '#':
					addToken(CommandToken.COMMENT, loc, srcText.substring(loc.getIndex()));
					loc.setIndex(srcLength);
					break;
				case '&':
					mAND(loc);
					break;
				case '|':
					mOR(loc);
					break;
				case '!':
					mNOTEQUAL(loc);
					break;
				case '=':
					mEQUAL(loc);
					break;
				case '<':
					mLESS(loc);
					break;
				case '>':
					mGREATER(loc);
					break;
				case '$':
				case '%':
				case '(':
				case ')':
				case '{':
				case '}':
				case '[':
				case ']':
				case '+':
				case '-':
				case '*':
				case '/':
				case '\'':
				case '\"':
				case '^':
				case '~':
				case '\\':
				case ';':
				case ':':
				case '?':
				case '.':
				case ',':
				case '@':
				case '`':
					addToken(c, loc, getText(loc.getIndex(), 1));
					loc.seek(1);
					break;
				default:
				{
					if (isIdentifierStart(c)) {
						mIDENTIFIER(loc);
					}
					else if (isDigit(c)) {
						mNUMBER(loc);
					}
					else {
						// no viable characters
						mUNKNOWN(loc);
					}
				}
			}
		}
	}
}
