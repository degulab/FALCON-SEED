/*
 * @(#)AADLMapType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;


/**
 * AADLにおける Map型を表すデータ型。
 * 
 * @version 1.00	2007/11/29
 */
public class AADLMapType extends AADLJavaClass
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final AADLType		keyType;		// マップのキー型
	private final AADLType		valueType;		// マップの値型

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLMapType(AADLType keyType, AADLType valueType) {
		this(java.util.Map.class, keyType, valueType);
	}
	
	public AADLMapType(Class javaClassType, AADLType keyType, AADLType valueType) {
		this(javaClassType.getName(), javaClassType, keyType, valueType);
	}
	
	public AADLMapType(String aadlName, Class javaClassType, AADLType keyType, AADLType valueType) {
		super(aadlName, javaClassType);
		if (keyType == null)
			throw new NullPointerException("keyType");
		if (valueType == null)
			throw new NullPointerException("valueType");
		this.keyType = keyType;
		this.valueType = valueType;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public AADLType getKeyType() {
		return this.keyType;
	}
	
	public AADLType getValueType() {
		return this.valueType;
	}
	
	public String getNameKey() {
		return createNameKey(this.getName(), this.keyType.getNameKey(), this.valueType.getNameKey());
	}

	/*
	public String getJavaNameKey() {
		return createNameKey(this.getJavaName(), this.keyType.getJavaNameKey(), this.valueType.getJavaNameKey());
	}
	*/

	//------------------------------------------------------------
	// Public interfaces for JavaClass
	//------------------------------------------------------------

	// JAVAクラス名
	public String getJavaClassName() {
		String retName;
		if (this.getJavaClass().getTypeParameters().length > 0 &&
			this.isInstanceOf(java.util.Map.class))
		{
			retName = getJavaExtendsGenericClassName(java.util.Map.class.getCanonicalName(),
														this.keyType, this.valueType);
		}
		else {
			retName = getJavaExtendsGenericClassName(this.getJavaClass().getCanonicalName(),
														this.keyType, this.valueType);
		}
		return retName;
	}
	
	// JAVA コンストラクタ名
	public String getJavaConstructorName() {
		return getJavaFixedGenericClassName(this.getJavaClass().getCanonicalName(), this.keyType, this.valueType);
	}

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AADLMapType) {
			AADLMapType aInfo = (AADLMapType)obj;
			if (super.equals(aInfo) &&
				aInfo.keyType.equals(this.keyType) &&
				aInfo.valueType.equals(this.valueType))
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
	
	static protected String createNameKey(String name, String keyNameKey, String valNameKey) {
		return String.format("%s<%s,%s>", name, keyNameKey, valNameKey);
	}
}
