/*
 * @(#)AADLSymbolStack.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLSymbolStack.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import java.util.ArrayList;
import java.util.Iterator;

import ssac.aadlc.analysis.type.AADLType;

/**
 * シンボル情報のスコープスタック
 * 
 * 
 * @version 1.30	2009/12/02
 */
public class AADLSymbolStack
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ArrayList<AADLSymbolMap> scopeStack = new ArrayList<AADLSymbolMap>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLSymbolStack() {
		
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public boolean isEmpty() {
		return scopeStack.isEmpty();
	}
	
	public int getLevelCount() {
		return scopeStack.size();
	}
	
	public void clear() {
		scopeStack.clear();
	}
	
	public void pushScope() {
		scopeStack.add(0, new AADLSymbolMap());
	}
	
	public void popScope() {
		if (!scopeStack.isEmpty()) {
			scopeStack.remove(0);
		}
	}
	
	public AADLSymbolMap peek() {
		if (!scopeStack.isEmpty())
			return scopeStack.get(0);
		else
			return null;
	}

	// AADL シンボル名の有無
	public boolean hasSymbol(String name) {
		return (getSymbolValue(name) != null);
	}

	// JAVA シンボル名の有無
	public boolean hasJavaSymbol(String name) {
		return (getJavaSymbolValue(name) != null);
	}

	// AADL シンボル名に対応するシンボル取得
	public AADLSymbol getSymbolValue(String name) {
		if (!isEmpty()) {
			Iterator<AADLSymbolMap> it = scopeStack.iterator();
			while (it.hasNext()) {
				AADLSymbol curSymbol = it.next().getSymbolValue(name);
				if (curSymbol != null) {
					return curSymbol;
				}
			}
		}
		
		return null;
	}
	
	// JAVA シンボル名に対応するシンボル取得
	public AADLSymbol getJavaSymbolValue(String name) {
		if (!isEmpty()) {
			Iterator<AADLSymbolMap> it = scopeStack.iterator();
			while (it.hasNext()) {
				AADLSymbol curSymbol = it.next().getJavaSymbolValue(name);
				if (curSymbol != null) {
					return curSymbol;
				}
			}
		}
		
		return null;
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
		peek().setSymbol(symbol);
	}
	
	public void setSymbol(boolean isConst, String aadlName, AADLType dataType) {
		setSymbol(new AADLSymbol(isConst, aadlName, dataType));
	}
	
	public void setSymbol(boolean isConst, String aadlName, String javaName, AADLType dataType) {
		setSymbol(new AADLSymbol(isConst, aadlName, javaName, dataType));
	}
}
