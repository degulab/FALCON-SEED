/*
 * @(#)AADLJavaAction.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;

/**
 * JavaAction を表す、特殊なデータ型
 * 
 * @version 1.00	2007/11/29
 */
public final class AADLJavaAction extends AADLType
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static public final AADLJavaAction instance = new AADLJavaAction();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLJavaAction() {
		super("JavaAction", java.lang.Object.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isJavaAction() {
		return true;
	}

}
