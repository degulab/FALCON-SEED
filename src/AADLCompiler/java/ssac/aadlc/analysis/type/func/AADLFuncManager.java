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
 * @(#)AADLFuncManager.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.func;

import java.util.Collection;
import java.util.HashSet;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.codegen.builtin.ACodeFunctionModule;

/**
 * AADL における関数情報
 * 
 * @version 1.00	2007/11/29
 */
public class AADLFuncManager
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private final HashSet<String> keywords = new HashSet<String>();	// 使用できない関数名
	
	static private final AADLFuncMap builtinFuncs = new AADLFuncMap();	// 組み込み関数情報
	
	private final AADLFuncMap userFuncs = new AADLFuncMap();	// ユーザー定義関数

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static {
		// 予約語
		keywords.add("null");
		keywords.add("get");
		keywords.add("proj");
		keywords.add("prjection");
		
		// 組み込み関数情報
		for (ACodeFunctionModule func : ACodeFunctionModule.builtinFuncs) {
			AADLFuncType ftype = func.getFunctionType();
			if (ftype != null) {
				AADLFuncManager.builtinFuncs.set(ftype);
			}
		}
	}
	
	public AADLFuncManager() {
		
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		this.userFuncs.clear();
	}
	
	public Collection<AADLFuncType> getUserFunctions() {
		return this.userFuncs.getAllFunctions();
	}
	
	public boolean contains(AADLFuncType funcType) {
		if (this.userFuncs.contains(funcType))
			return true;
		if (builtinFuncs.contains(funcType))
			return true;
		// No entry
		return false;
	}
	
	public boolean hasCallable(AADLFuncType funcType) {
		if (this.userFuncs.hasCallableFunction(funcType.getName(), funcType.getParamTypes()))
			return true;
		if (builtinFuncs.hasCallableFunction(funcType.getName(), funcType.getParamTypes()))
			return true;
		// No entry
		return false;
	}
	
	public boolean hasCallable(String name, AADLType...paramTypes) {
		if (this.userFuncs.hasCallableFunction(name, paramTypes))
			return true;
		if (builtinFuncs.hasCallableFunction(name, paramTypes))
			return true;
		// No entry
		return false;
	}
	
	public boolean isBuiltinFunction(AADLFuncType funcType) {
		return builtinFuncs.contains(funcType);
	}
	
	public boolean isUserFunction(AADLFuncType funcType) {
		return this.userFuncs.contains(funcType);
	}
	
	public AADLFuncType get(AADLFuncType funcType) {
		AADLFuncType retType = this.userFuncs.get(funcType);
		if (retType != null)
			return retType;
		retType = builtinFuncs.get(funcType);
		if (retType != null)
			return retType;
		// No entry
		return null;
	}
	
	public AADLFuncType getCallable(AADLFuncType funcType) {
		AADLFuncType retType = null;
		//--- User function
		retType = this.userFuncs.getCallable(funcType.getName(), funcType.getParamTypes());
		if (retType != null)
			return retType;
		//--- built-in function
		retType = builtinFuncs.getCallable(funcType.getName(), funcType.getParamTypes());
		if (retType != null)
			return retType;
		// No entry
		return null;
	}
	
	public AADLFuncType getCallable(String name, AADLType...paramTypes) {
		AADLFuncType retType = null;
		//--- User function
		retType = this.userFuncs.getCallable(name, paramTypes);
		if (retType != null)
			return retType;
		//--- built-in function
		retType = builtinFuncs.getCallable(name, paramTypes);
		if (retType != null)
			return retType;
		// No entry
		return null;
	}
	
	public void setUserFunction(AADLFuncType userFunc) {
		// Check function name
		if (keywords.contains(userFunc.getName())) {
			// キーワードは使用できない
			throw new IllegalArgumentException(AADLMessage.cannotUseKeyword(userFunc.getName()));
		}
		
		// Check built-in function
		if (builtinFuncs.contains(userFunc)) {
			// 組み込み関数は再定義できない
			throw new IllegalArgumentException(AADLMessage.alreadyExistFunction(userFunc.toPrototypeString()));
		}
		
		// Check user function
		if (this.userFuncs.contains(userFunc)) {
			// 二重定義できない
			throw new IllegalArgumentException(AADLMessage.alreadyExistFunction(userFunc.toPrototypeString()));
		}
		
		// 登録
		this.userFuncs.set(userFunc);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
