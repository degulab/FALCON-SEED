/*
 * @(#)LibFileType.java	4.0.0	2021/08/25 : for Java11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

/**
 * 1 つのライブラリファイルの種類
 * 
 * @version 4.0.0
 * @since 4.0.0
 */
public enum LibFileType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	JAR("jar"),
	CLASSES("classes")
	;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String _typeName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private LibFileType(String name)
	{
		_typeName = name;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String typeName()
	{
		return _typeName;
	}
	
	@Override
	public String toString()
	{
		return _typeName;
	}
	
	static public LibFileType fromName(String name)
	{
		if (name != null && !name.isEmpty())
		{
			if (JAR._typeName.equalsIgnoreCase(name)) {
				return JAR;
			}
			else if (CLASSES._typeName.equalsIgnoreCase(name)) {
				return CLASSES;
			}
		}
		// undefined
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
