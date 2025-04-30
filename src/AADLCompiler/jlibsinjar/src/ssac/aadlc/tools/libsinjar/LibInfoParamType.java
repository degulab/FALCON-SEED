/*
 * @(#)LibInfoParamType.java	4.0.0	2021/08/25 : for Java11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.tools.libsinjar;

/**
 * 1 つのライブラリ情報に含まれるパラメータタイプの列挙型。
 * 
 * @version 4.0.0
 * @since 4.0.0
 */
public enum LibInfoParamType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	NAME("name="),
	PATH("path="),
	LICENSE_NAME("license="),
	LICENSE_FILE("license_file=");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String _typeName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private LibInfoParamType(String name)
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
	
	static public LibInfoParamType fromName(String name)
	{
		if (name != null && !name.isEmpty()) {
			if (NAME._typeName.equalsIgnoreCase(name)) {
				return NAME;
			}
			else if (PATH._typeName.equalsIgnoreCase(name)) {
				return PATH;
			}
			else if (LICENSE_NAME._typeName.equalsIgnoreCase(name)) {
				return LICENSE_NAME;
			}
			else if (LICENSE_FILE._typeName.equalsIgnoreCase(name)) {
				return LICENSE_FILE;
			}
		}
		// undefined
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
