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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLFuncType.java	1.60	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLFuncType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.func;

import ssac.aadlc.analysis.type.AADLType;

/**
 * AADLの組み込み関数の型情報を保持するクラス。
 * 
 * @version 1.60	2011/03/16
 */
public class AADLFuncType
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final int			localHashCode;
	private final String		funcName;
	private final AADLType		funcReturn;
	private final AADLType[]	funcParams;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLFuncType(String name, AADLType typeReturn, AADLType...paramTypes) {
		// Check
		if (name == null)
			throw new NullPointerException("name");
		if (name.length() <= 0)
			throw new IllegalArgumentException("name");
			
		// Construct
		this.funcName = name;
		this.funcReturn = typeReturn;
		this.funcParams = paramTypes;
		this.localHashCode = getPrototypeString(name, paramTypes).hashCode();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public String getPrototypeString(String name, AADLType...paramTypes) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(name);
		sb.append("(");
		if (paramTypes.length > 0) {
			//--- first
			sb.append(paramTypes[0].getName());
			//--- next
			for (int i = 1; i < paramTypes.length; i++) {
				sb.append(",");
				sb.append(paramTypes[i].getName());
			}
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	static public String getDetailString(AADLType retType, String name, AADLType...paramTypes) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(name);
		sb.append("(");
		if (paramTypes.length > 0) {
			//--- first
			sb.append(paramTypes[0].getNameKey());
			//--- next
			for (int i = 1; i < paramTypes.length; i++) {
				sb.append(",");
				sb.append(paramTypes[i].getNameKey());
			}
		}
		sb.append(")");
		
		if (retType != null) {
			sb.append(":");
			sb.append(retType.getNameKey());
		}
		
		return sb.toString();
	}

	public boolean hasReturn() {
		return (this.funcReturn != null);
	}
	
	public boolean hasParams() {
		return (this.funcParams.length > 0);
	}
	
	public int getParamCount() {
		return this.funcParams.length;
	}
	
	public String getName() {
		return this.funcName;
	}
	
	public AADLType getReturnType() {
		return this.funcReturn;
	}
	
	public AADLType[] getParamTypes() {
		return this.funcParams;
	}
	
	public boolean isCallable(String name, AADLType...paramTypes) {
		// function name
		if (!name.equals(this.funcName)) {
			// Cannot call
			return false;
		}
		
		// function parameters
		if (paramTypes.length != this.funcParams.length) {
			// Cannot call
			return false;
		}
		for (int i = 0; i < paramTypes.length; i++) {
			//@@@ modified by Y.Ishizuka : 2011.03.16
			if (!paramTypes[i].equals(this.funcParams[i])) {
				if (!paramTypes[i].isJavaAction() && !paramTypes[i].isInstanceOf(this.funcParams[i])) {
					// Cannot call
					return false;
				}
			}
			/*--- old codes : 2011.03.16 ---
			//if (!paramTypes[i].equals(this.funcParams[i])) {
			//	// Cannot call
			//	return false;
			//}
			if (!paramTypes[i].isJavaAction() && !paramTypes[i].isInstanceOf(this.funcParams[i])) {
				// Cannot call
				return false;
			}
			/*--- end of old codes : 2011.03.16 ---*/
			//@@@ end of modified : 2011.03.16
		}
		
		// Can call
		return true;
	}
	
	public boolean isCallable(AADLFuncType funcType) {
		return isCallable(funcType.getName(), funcType.getParamTypes());
	}
	
	public String toPrototypeString() {
		return getPrototypeString(this.funcName, this.funcParams);
	}
	
	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AADLFuncType) {
			AADLFuncType aType = (AADLFuncType)obj;
			if (aType.funcName.equals(this.funcName) &&
				aType.funcParams.length == this.funcParams.length)
			{
				for (int i = 0; i < this.funcParams.length; i++) {
					if (!aType.funcParams[i].isNearlyEqual(this.funcParams[i])) {
						// Not same parameter type
						return false;
					}
				}
				// Same prototype
				return true;
			}
		}
		
		// Not equal
		return false;
	}

	@Override
	public int hashCode() {
		return this.localHashCode;
	}

	@Override
	public String toString() {
		return getDetailString(this.funcReturn, this.funcName, this.funcParams);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/*
	protected boolean isEqualReturnType(AADLPrimitive prim) {
		if (prim != null && this.funcReturn != null) {
			return (prim.equals(this.funcReturn));
		}
		else if (prim == null && this.funcReturn == null) {
			return true;
		}
		
		// Not equal
		return false;
	}

	protected boolean isEqualParamTypes(AADLPrimitive[] params) {
		return this.funcParams.equals(params);
	}
	*/
}
