/*
 * @(#)AADLCollectionType.java	1.60	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLCollectionType.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLCollectionType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;

/**
 * AADLにおけるコレクション型を表すデータ型。
 * 
 * @version 1.60	2011/03/16
 */
public class AADLCollectionType extends AADLIterableType
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
	
	public AADLCollectionType(String aadlName, Class javaClassType, AADLType elemType) {
		super(aadlName, javaClassType, elemType);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getNameKey() {
		return createNameKey(this.getName(), this.elemType.getNameKey());
	}

	/*
	public String getJavaNameKey() {
		return createNameKey(this.getJavaName(), this.elemType.getJavaNameKey());
	}
	*/

	//------------------------------------------------------------
	// Public interfaces for JavaClass
	//------------------------------------------------------------

	// JAVAクラス名
	public String getJavaClassName() {
		String retName;
		if (this.getJavaClass().getTypeParameters().length > 0 &&
			this.isInstanceOf(java.util.Collection.class))
		{
			retName = getJavaExtendsGenericClassName(java.util.Collection.class.getCanonicalName(), this.elemType);
		}
		else {
			retName = getJavaExtendsGenericClassName(this.getJavaClass().getCanonicalName(), this.elemType);
		}
		return retName;
	}
	
	// JAVA コンストラクタ名
	public String getJavaConstructorName() {
		return getJavaFixedGenericClassName(this.getJavaClass().getCanonicalName(), this.elemType);
	}

	//@@@ added by Y.Ishizuka : 2011.03.16
	@Override
	public boolean isInstanceOf(AADLType type) {
		if (type instanceof AADLCollectionType) {
			if (!super.isInstanceOf(type)) {
				return false;
			}
			return getElementType().isInstanceOf(((AADLCollectionType)type).getElementType());
		}
		else {
			return super.isInstanceOf(type);
		}
	}
	//@@@ end of added : 2011.03.16

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AADLCollectionType) {
			AADLCollectionType aInfo = (AADLCollectionType)obj;
			if (super.equals(aInfo) &&
				aInfo.elemType.equals(this.elemType))
			{
				// Same object
				return true;
			}
		}
		
		// Not equal
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected String createNameKey(String name, String elemNameKey) {
		return String.format("%s<%s>", name, elemNameKey);
	}
}
