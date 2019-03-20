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
 * @(#)AADLSymbol.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLSymbol.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import ssac.aadlc.analysis.type.AADLType;

/**
 * AADL シンボル情報
 * 
 * 
 * @version 1.30	2009/12/02
 */
public class AADLSymbol
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final boolean flgConst;	// CONST 指定フラグ
	private boolean	initialized;	// 初期化済フラグ
	
	private String		aadlName;	// AADL シンボル名
	private String		javaName;	// JAVA シンボル名
	private AADLType	aadlType;	// AADL データ型情報

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLSymbol(boolean isConst, String aadlName, AADLType dataType) {
		this(isConst, aadlName, null, dataType);
	}
	
	public AADLSymbol(boolean isConst, String aadlName, String javaName, AADLType dataType) {
		this.flgConst = isConst;
		this.initialized = false;
		this.aadlName = aadlName;
		this.javaName = (aadlName.equals(javaName) ? null : javaName);
		this.aadlType = dataType;
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isConst() {
		return this.flgConst;
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public void setInitialized() {
		this.initialized = true;
	}
	
	public boolean hasAadlSymbolName() {
		return (this.aadlName != null);
	}
	
	public boolean hasJavaSymbolName() {
		return (this.javaName != null);
	}

	public String getAadlSymbolName() {
		return this.aadlName;
	}
	
	public String getJavaSymbolName() {
		return ((this.javaName != null) ? this.javaName : this.aadlName);
	}
	
	public AADLType getType() {
		return this.aadlType;
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
