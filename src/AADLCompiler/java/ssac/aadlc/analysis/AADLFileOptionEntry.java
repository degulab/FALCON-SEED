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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLKeyword.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import org.antlr.runtime.Token;

import ssac.aadlc.codegen.ACodeObject;

/**
 * AADLファイル識別子のオプション要素
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class AADLFileOptionEntry
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final Token		_id;
	private final ACodeObject	_exp;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLFileOptionEntry(Token id, ACodeObject exp) {
		if (id == null)
			throw new NullPointerException("'id' is null.");
		this._id  = id;
		this._exp = exp;
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String name() {
		return _id.getText();
	}
	
	public Token identifier() {
		return _id;
	}
	
	public ACodeObject expression() {
		return _exp;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
