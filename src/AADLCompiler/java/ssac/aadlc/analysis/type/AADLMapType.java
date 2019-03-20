/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
