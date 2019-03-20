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
 * @(#)AADLJavaClass.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLJavaClass.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;

/**
 * AADLにおける Javaクラスを表すデータ型。
 * 
 * @version 1.30	2009/12/02
 */
public class AADLJavaClass extends AADLType
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final AADLJavaClass JavaBoolean = new AADLJavaClass(Boolean.TYPE);
	static public final AADLJavaClass JavaChar    = new AADLJavaClass(Character.TYPE);
	static public final AADLJavaClass JavaByte    = new AADLJavaClass(Byte.TYPE);
	static public final AADLJavaClass JavaShort   = new AADLJavaClass(Short.TYPE);
	static public final AADLJavaClass JavaInt     = new AADLJavaClass(Integer.TYPE);
	static public final AADLJavaClass JavaLong    = new AADLJavaClass(Long.TYPE);
	static public final AADLJavaClass JavaFloat   = new AADLJavaClass(Float.TYPE);
	static public final AADLJavaClass JavaDouble  = new AADLJavaClass(Double.TYPE);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLJavaClass(Class javaClassType) {
		super(javaClassType.getName(), javaClassType);
	}
	
	public AADLJavaClass(String aadlName, Class javaClassType) {
		super(aadlName, javaClassType);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	// AADL operator
	// ・文字列あり：対応するメソッドが存在する
	// ・null：operator をそのまま使用
	// ・長さ0の文字列：サポートなし(メソッド検索で必ず失敗する)
	public String getOperatorMethodName(int opType) {
		switch (opType) {
			case Operator.PLUS :
				return "plus";
			case Operator.MINUS :
				return "negate";
			case Operator.HAT :
				return NO_METHOD;
			case Operator.BAR :
				return NO_METHOD;
			case Operator.ADD :
				return "add";
			case Operator.SUBTRACT :
				return "subtract";
			case Operator.MULTIPLE :
				return "multiple";
			case Operator.DIVIDE :
				return "divide";
			case Operator.MOD :
				return "mod";
			case Operator.CAT :
				return NO_METHOD;
		}
		
		return super.getOperatorMethodName(opType);
	}

}
