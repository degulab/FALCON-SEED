/*
 * @(#)AADLSetType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;


/**
 * AADLにおける Set型を表すデータ型。
 * 
 * @version 1.00	2007/11/29
 */
public class AADLSetType extends AADLCollectionType
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLSetType(AADLType elemType) {
		this(java.util.Set.class, elemType);
	}
	
	public AADLSetType(Class javaClassType, AADLType elemType) {
		this(javaClassType.getName(), javaClassType, elemType);
	}
	
	public AADLSetType(String aadlName, Class javaClassType, AADLType elemType) {
		super(aadlName, javaClassType, elemType);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	// JAVAクラス名
	public String getJavaClassName() {
		String retName;
		if (this.getJavaClass().getTypeParameters().length > 0 &&
			this.isInstanceOf(java.util.Set.class))
		{
			retName = getJavaExtendsGenericClassName(java.util.Set.class.getCanonicalName(),
													this.getElementType());
		}
		else {
			retName = getJavaExtendsGenericClassName(this.getJavaClass().getCanonicalName(),
													this.getElementType());
		}
		return retName;
	}

	//------------------------------------------------------------
	// Public interfaces for JavaClass
	//------------------------------------------------------------
}
