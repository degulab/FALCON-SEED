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
 * @(#)AADLSymbolMap.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLSymbolMap.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import java.util.HashMap;

import ssac.aadlc.analysis.type.AADLType;

/**
 * AADL シンボル情報のマップ
 * 
 * 
 * @version 1.30	2009/12/02
 */
public class AADLSymbolMap
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final HashMap<String,AADLSymbol>	mapJavaToAadl = new HashMap<String,AADLSymbol>();
	private final HashMap<String,AADLSymbol>	mapSymbols = new HashMap<String,AADLSymbol>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLSymbolMap() {
		
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return mapSymbols.isEmpty();
	}
	
	public int getSymbolCount() {
		return mapSymbols.size();
	}
	
	public void clear() {
		mapSymbols.clear();
		mapJavaToAadl.clear();
	}

	// AADL シンボル名の有無
	public boolean hasSymbol(String name) {
		return mapSymbols.containsKey(name);
	}

	// JAVA シンボル名の有無
	public boolean hasJavaSymbol(String name) {
		if (mapJavaToAadl.containsKey(name))
			return true;
		else if (mapSymbols.containsKey(name))
			return true;
		else
			return false;
	}

	// AADL シンボル名に対応するシンボル取得
	public AADLSymbol getSymbolValue(String name) {
		return mapSymbols.get(name);
	}
	
	// JAVA シンボル名に対応するシンボル取得
	public AADLSymbol getJavaSymbolValue(String name) {
		if (mapJavaToAadl.containsKey(name))
			return mapJavaToAadl.get(name);
		else
			return mapSymbols.get(name);
	}

	// シンボル情報設定
	public void setSymbol(AADLSymbol symbol) {
		// Check
		if (hasSymbol(symbol.getAadlSymbolName())) {
			throw new IllegalArgumentException("Already exist AADL Symbol!");
		}
		if (symbol.hasJavaSymbolName() && hasJavaSymbol(symbol.getJavaSymbolName())) {
			throw new IllegalArgumentException("Already exist Java Symbol!");
		}
		
		// Set
		mapSymbols.put(symbol.getAadlSymbolName(), symbol);
		if (symbol.hasJavaSymbolName()) {
			mapJavaToAadl.put(symbol.getJavaSymbolName(), symbol);
		}
	}
	
	public void setSymbol(boolean isConst, String aadlName, AADLType dataType) {
		setSymbol(new AADLSymbol(isConst, aadlName, dataType));
	}
	
	public void setSymbol(boolean isConst, String aadlName, String javaName, AADLType dataType) {
		setSymbol(new AADLSymbol(isConst, aadlName, javaName, dataType));
	}
}
