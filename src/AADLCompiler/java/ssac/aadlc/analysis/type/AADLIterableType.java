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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
