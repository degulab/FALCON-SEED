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
