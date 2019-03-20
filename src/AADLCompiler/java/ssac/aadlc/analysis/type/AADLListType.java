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
