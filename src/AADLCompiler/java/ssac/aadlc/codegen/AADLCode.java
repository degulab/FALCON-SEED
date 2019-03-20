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
 * @(#)AADLCode.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen;

import org.antlr.runtime.Token;

import ssac.aadlc.analysis.type.AADLObject;
import ssac.aadlc.analysis.type.AADLType;

/**
 * AADL から生成される Java コード
 * 
 * @version 1.00	2007/11/29
 */
public abstract class AADLCode implements Cloneable
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected JavaLineBuffer	jlb;		// Javaソースコード
	private AADLType			type;		// AADL データ型

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLCode() {
		this(null);
	}
	
	protected AADLCode(AADLType dataType) {
		this.jlb = new JavaLineBuffer();
		setType(dataType);
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return this.jlb.isEmpty();
	}
	
	public int getLineCount() {
		return this.jlb.getLineCount();
	}
	
	public int getMinLineNo() {
		return this.jlb.getMinLineNo();
	}
	
	public int getMaxLineNo() {
		return this.jlb.getMaxLineNo();
	}
	
	public JavaLineBuffer getJavaLineBuffer() {
		return this.jlb;
	}
	
	public AADLType getType() {
		return this.type;
	}
	
	public boolean isInstanceOf(Class javaClassType) {
		return this.type.isInstanceOf(javaClassType);
	}
	
	public boolean isInstanceOf(AADLType dataType) {
		return this.type.isInstanceOf(dataType); 
	}
	
	public boolean isInstanceOf(AADLCode aCode) {
		return this.type.isInstanceOf(aCode.getType());
	}

	//------------------------------------------------------------
	// Implements java.lang.Object
	//------------------------------------------------------------

	@Override
	public Object clone() {
		try {
			AADLCode newInstance = (AADLCode)super.clone();
			newInstance.jlb = new JavaLineBuffer(this.jlb);
			return newInstance;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.jlb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected void setType(AADLType dataType) {
		if (dataType != null)
			this.type = dataType;
		else
			this.type = AADLObject.instance;
	}
	
	protected String getOperandErrorMessage(Token tkOperand, AADLCode aCode) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(tkOperand.getText());
		sb.append(") not supported parameter type : ");
		if (aCode != null) {
			sb.append("(");
			sb.append(this.type.getName());
			sb.append(") ");
			sb.append(tkOperand.getText());
			sb.append(" (");
			sb.append(aCode.getType().getName());
			sb.append(")");
		}
		else {
			sb.append(tkOperand.getText());
			sb.append("(");
			sb.append(this.type.getName());
			sb.append(")");
		}
		
		return getErrorMessage(tkOperand.getLine(), sb.toString());
	}
	
	protected String getErrorMessage(int lineNo, String msg) {
		return ("Error [line:" + lineNo + "] " + msg);
	}

}
