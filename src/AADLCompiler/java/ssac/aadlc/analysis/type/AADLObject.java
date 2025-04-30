/*
 * @(#)AADLObject.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;


/**
 * java.lang.Object を表す、特殊なデータ型
 * 
 * 
 * @version 1.00	2007/11/29
 */
public final class AADLObject extends AADLJavaClass
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final AADLObject instance = new AADLObject();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLObject() {
		super("Object", java.lang.Object.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
