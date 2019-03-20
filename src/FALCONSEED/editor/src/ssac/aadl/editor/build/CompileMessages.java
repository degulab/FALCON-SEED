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

import java.util.ArrayList;

/**
 * コンパイル時に出力されるメッセージ。
 * ソースコード１行分のメッセージを保持する。
 * 
 * @version 1.02 2008/05/23
 * 
 * @since 1.02
 */
public class CompileMessages extends ArrayList<CompileMessageElem>
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileMessages() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getMessageText() {
		StringBuffer sb = new StringBuffer();
		for (CompileMessageElem elem : this) {
			if (elem != null) {
				String strElem = getElementText(elem);
				if (strElem != null) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(strElem);
				}
			}
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getElementText(CompileMessageElem elem) {
		String ret = null;
		if (elem.hasMessages()) {
			String strcn = elem.hasColumnNo() ? String.valueOf(elem.getColumnNo()) : "-";
			ret = "Column:" + strcn + " : " + elem.getMessages();
		}
		return ret;
	}
}
