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
package ssac.aadl.editor.build;


/**
 * コンパイル時に出力されるメッセージ要素
 * 
 * @version 1.02 2008/05/23
 * 
 * @since 1.02
 */
public class CompileMessageElem {
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final int IGNORE_NO = -1;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private int	lineNo;
	private int	colNo;
	private String	messages;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileMessageElem() {
		this(IGNORE_NO, IGNORE_NO, null);
	}
	
	public CompileMessageElem(String messages) {
		this(IGNORE_NO, IGNORE_NO, messages);
	}
	
	public CompileMessageElem(int lineNo, String messages) {
		this(lineNo, IGNORE_NO, messages);
	}
	
	public CompileMessageElem(int lineNo, int colNo, String messages) {
		this.lineNo   = lineNo;
		this.colNo    = colNo;
		this.messages = messages;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasMessages() {
		return (this.messages != null && this.messages.length() > 0);
	}
	
	public boolean hasLineNo() {
		return (this.lineNo >= 0);
	}
	
	public boolean isValidLineNo() {
		return (this.lineNo > 0);
	}
	
	public boolean hasColumnNo() {
		return (this.colNo > 0);
	}
	
	public int getLineNo() {
		return this.lineNo;
	}
	
	public int getColumnNo() {
		return this.colNo;
	}
	
	public String getMessages() {
		return this.messages;
	}
	
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	
	public void setColumnNo(int colNo) {
		this.colNo = colNo;
	}
	
	public void setMessages(String messages) {
		this.messages = messages;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CompileMessageElem) {
			CompileMessageElem elem = (CompileMessageElem)obj;
			boolean hm1 = this.hasMessages();
			boolean hm2 = elem.hasMessages();
			if ((hm1 && hm2) || (!hm1 && !hm2)) {
				if (this.lineNo == elem.lineNo && this.colNo == elem.colNo) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hc = this.lineNo;
		hc ^= this.colNo;
		if (hasMessages()) {
			hc ^= this.messages.hashCode();
		}
		return hc;
	}

	@Override
	public String toString() {
		String strln  = (this.hasLineNo() ? String.valueOf(this.lineNo) : "-");
		String strcn  = (this.hasColumnNo() ? String.valueOf(this.colNo) : "-");
		String strmsg = (this.hasMessages() ? this.messages : "");
		return ("[" + strln + ":" + strcn + "] " + strmsg);
	}
}
