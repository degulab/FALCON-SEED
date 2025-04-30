/*
 * @(#)AADLIterableType.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;

/**
 * 反復子を返すインタフェースを実装しているクラスタイプを表すデータ型。
 * 基本的に、{@link java.lang.Iterable} インタフェースを実装している
 * クラスのデータ型となる。通常、{@link java.lang.Iterable#iterator()} が
 * 返す反復子から、その反復子が返すデータ型を限定するため、このクラスを
 * 実装する。
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class AADLIterableType extends AADLJavaClass
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final AADLType		elemType;		// 反復子が返す要素の型

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLIterableType(String aadlName, Class javaClassType, AADLType elemType) {
		super(aadlName, javaClassType);
		if (elemType == null)
			throw new NullPointerException("elemType");
		this.elemType = AADLTypeManager.boxingJavaPrimitive(elemType);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public AADLType getElementType() {
		return this.elemType;
	}

	//------------------------------------------------------------
	// Public interfaces for JavaClass
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AADLIterableType) {
			AADLIterableType aInfo = (AADLIterableType)obj;
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
}
