/*
 * @(#)AADLListType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;


/**
 * AADL における List型を表すデータ型。
 * 
 * @version 1.00	2007/11/29
 */
public class AADLListType extends AADLCollectionType
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
	
	public AADLListType(AADLType elemType) {
		this(java.util.List.class, elemType);
	}
	
	public AADLListType(Class javaClassType, AADLType elemType) {
		this(javaClassType.getName(), javaClassType, elemType);
	}
	
	public AADLListType(String aadlName, Class javaClassType, AADLType elemType) {
		super(aadlName, javaClassType, elemType);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	// JAVAクラス名
	public String getJavaClassName() {
		String retName;
		if (this.getJavaClass().getTypeParameters().length > 0 &&
			this.isInstanceOf(java.util.List.class))
		{
			retName = getJavaExtendsGenericClassName(java.util.List.class.getCanonicalName(),
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
