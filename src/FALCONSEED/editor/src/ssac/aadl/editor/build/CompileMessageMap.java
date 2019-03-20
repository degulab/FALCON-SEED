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
 * @(#)EditorResources.java	1.02	2008/05/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorResources.java	1.10	2008/12/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.build;

import java.util.HashMap;

import ssac.util.Strings;

/**
 * コンパイル時に出力されるメッセージ。
 * ソースコード１行分のメッセージと行番号とのマップ
 * 
 * @version 1.10	2008/11/28
 * 
 * @since 1.02
 */
public class CompileMessageMap
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final HashMap<Integer,CompileMessages> mmap = new HashMap<Integer,CompileMessages>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileMessageMap() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		this.mmap.clear();
	}
	
	public int size() {
		return this.mmap.size();
	}
	
	public boolean containsLine(int lineNo) {
		return this.mmap.containsKey(lineNo);
	}
	
	public CompileMessages get(int lineNo) {
		return this.mmap.get(lineNo);
	}
	
	public String getMessage(int lineNo) {
		String ret = null;
		CompileMessages cm = this.mmap.get(lineNo);
		if (cm != null) {
			ret = cm.getMessageText();
		}
		return ret;
	}
	
	public CompileMessages put(int lineNo, CompileMessages cm) {
		if (cm == null) {
			throw new NullPointerException("CompileMessages instance is null!");
		}
		
		return this.mmap.put(lineNo, cm);
	}
	
	public boolean addLineMessage(int lineNo, int colNo, String messages) {
		boolean bAdded = false;
		if (!Strings.isNullOrEmpty(messages)) {
			CompileMessageElem elem = new CompileMessageElem(lineNo, colNo, messages);
			CompileMessages cm = this.mmap.get(lineNo);
			if (cm != null) {
				cm = new CompileMessages();
				this.mmap.put(lineNo, cm);
			}
			cm.add(elem);
			bAdded = true;
		}
		return bAdded;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CompileMessageMap) {
			return ((CompileMessageMap)obj).mmap.equals(this.mmap);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.mmap.hashCode();
	}

	@Override
	public String toString() {
		return this.mmap.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
