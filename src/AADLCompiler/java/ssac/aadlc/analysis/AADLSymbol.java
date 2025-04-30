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
