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
 * @(#)AADLType.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

import ssac.aadlc.analysis.type.func.AADLFuncType;

/**
 * AADL データ型の基本クラス。
 * AADLコンパイルにおいて、データ型識別するためのクラスは、全て
 * このクラスから派生される。
 * 
 * @version 1.30	2009/12/02
 */
public class AADLType
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static protected String NO_METHOD = "";
	
	static public interface Operator {
		static public final int PLUS		= 1;
		static public final int MINUS		= 2;
		static public final int HAT		= 3;
		static public final int BAR		= 4;
		
		static public final int ADD		= 11;
		static public final int SUBTRACT	= 12;
		static public final int MULTIPLE	= 13;
		static public final int DIVIDE	= 14;
		static public final int MOD		= 15;
		static public final int CAT		= 16;
	}
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final int		localHashCode;		// ハッシュコード
	private final String	aadlName;			// AADL データ型名
	private final Class	javaClass;			// 対応 Javaクラス

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLType(Class javaClassType) {
		this(javaClassType.getName(), javaClassType);
	}
	
	protected AADLType(String aadlName, Class javaClassType) {
		// Check
		if (aadlName == null)
			throw new NullPointerException("aadlName");
		if (javaClassType == null)
			throw new NullPointerException("javaClassType");
		
		// Set
		this.aadlName = aadlName;
		this.javaClass = javaClassType;
		this.localHashCode = (aadlName + javaClassType.getName()).hashCode();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public String getName() {
		return this.aadlName;
	}
	
	public String getJavaName() {
		/**/
		String retName = this.javaClass.getCanonicalName();
		if (retName == null) {
			retName = this.javaClass.getName();
		}
		return retName;
		/**/
		//return this.javaClass.getName();
	}
	
	public Class getJavaClass() {
		return this.javaClass;
	}
	
	public String getNameKey() {
		return this.getName();
	}
	
	/*
	public String getJavaNameKey() {
		return this.getJavaName();
	}
	*/

	// AADLType が JavaAction か？
	public boolean isJavaAction() {
		return false;
	}

	// AADLType が AADLPrimitive か？
	public boolean isPrimitive() {
		return (this instanceof AADLPrimitive);
	}

	// AADLType が、JAVA Primitive を格納しているか？
	public boolean isJavaPrimitive() {
		return this.javaClass.isPrimitive();
	}
	
	public boolean isJavaArray() {
		return this.javaClass.isArray();
	}
	
	public boolean isMap() {
		return java.util.Map.class.isAssignableFrom(this.javaClass);
	}
	
	public boolean isSet() {
		return java.util.Set.class.isAssignableFrom(this.javaClass);
	}
	
	public boolean isList() {
		return java.util.List.class.isAssignableFrom(this.javaClass);
	}

	// Boolean として利用可能か？
	public boolean canBooleanOperation() {
		// Check class type
		if (isInstanceOf(boolean.class))
			return true;
		if (isInstanceOf(Boolean.class))
			return true;
		
		// Cannot operation
		return false;
	}
	
	// #compareTo() メソッドを持っているか？
	public boolean canCompareToOperation() {
		return isInstanceOf(Comparable.class);
	}
	
	// AADL operator
	// ・文字列あり：対応するメソッドが存在する
	// ・null：operator をそのまま使用
	// ・長さ0の文字列：サポートなし(メソッド検索で必ず失敗する)
	public String getOperatorMethodName(int opType) {
		String retName = null;
		switch (opType) {
			case Operator.PLUS :
				retName = null;
				break;
			case Operator.MINUS :
				retName = null;
				break;
			case Operator.HAT :
				retName = NO_METHOD;
				break;
			case Operator.BAR :
				retName = NO_METHOD;
				break;
			case Operator.ADD :
				retName = null;
				break;
			case Operator.SUBTRACT :
				retName = null;
				break;
			case Operator.MULTIPLE :
				retName = null;
				break;
			case Operator.DIVIDE :
				retName = null;
				break;
			case Operator.MOD :
				retName = null;
				break;
			case Operator.CAT :
				retName = NO_METHOD;
				break;
		}
		return retName;
	}

	//------------------------------------------------------------
	// Public interfaces for JavaClass
	//------------------------------------------------------------
	
	// 正規JAVAクラス名
	public String getJavaCanonicalName() {
		return this.javaClass.getCanonicalName();
	}

	// JAVAクラス名
	public String getJavaClassName() {
		return getJavaExtendsGenericClassName(this.javaClass.getCanonicalName());
	}
	
	// JAVA コンストラクタ名
	public String getJavaConstructorName() {
		return getJavaFixedGenericClassName(this.javaClass.getCanonicalName());
	}
	
	// 指定の Constructor が存在するかを検証する
	public boolean hasConstructor(AADLType...paramTypes) {
		boolean isExist;
		try {
			getJavaConstructor(paramTypes);
			isExist = true;
		}
		catch (Exception ex) {
			isExist = false;
		}
		return isExist;
	}
	
	// 指定の Public member field が存在するかを検証する
	public boolean hasField(String name) {
		boolean isExist;
		try {
			getField(name);
			isExist = true;
		}
		catch (Exception ex) {
			isExist = false;
		}
		return isExist;
	}
	
	// 指定の Public member method が存在するかを検証する
	public boolean hasMethod(String name, AADLType...paramTypes) {
		boolean isExist;
		try {
			getMethod(name, paramTypes);
			isExist = true;
		}
		catch (Exception ex) {
			isExist = false;
		}
		return isExist;
	}
	
	// 指定の static public method が存在するかを検証する
	public boolean hasStaticMethod(String name, AADLType...paramTypes) {
		boolean isExist;
		try {
			getStaticMethod(name, paramTypes);
			isExist = true;
		}
		catch (Exception ex) {
			isExist = false;
		}
		return isExist;
	}
	
	// メンバーフィールドの取得
	public AADLType getField(String name)
		throws NoSuchFieldException, SecurityException
	{
		Field javaField = getJavaField(name);
		return AADLTypeManager.getTypeByJavaClass(javaField.getDeclaringClass());
	}
	
	// メンバーメソッドの取得
	public AADLFuncType getMethod(String name, AADLType...paramTypes)
		throws NoSuchMethodException, SecurityException
	{
		Method javaMethod = getJavaMethod(name, paramTypes);
		// 戻り値型
		AADLType funcRetType;
		if (javaMethod.getReturnType() == null) {
			//--- 戻り値なし
			funcRetType = null;
		}
		else if (javaMethod.getReturnType() == void.class) {
			//--- 戻り値なし
			funcRetType = null;
		}
		else {
			//--- 戻り値あり
			funcRetType = AADLTypeManager.getTypeByJavaClass(javaMethod.getReturnType());
		}
		// 関数型生成
		AADLFuncType funcType = new AADLFuncType(name, funcRetType, paramTypes);
		return funcType;
	}
	
	// static メソッドの取得
	public AADLFuncType getStaticMethod(String name, AADLType...paramTypes)
		throws NoSuchMethodException, SecurityException
	{
		Method javaMethod = getJavaStaticMethod(name, paramTypes);
		// 戻り値型
		AADLType funcRetType;
		if (javaMethod.getReturnType() == null) {
			//--- 戻り値なし
			funcRetType = null;
		}
		else if (javaMethod.getReturnType() == void.class) {
			//--- 戻り値なし
			funcRetType = null;
		}
		else {
			//--- 戻り値あり
			funcRetType = AADLTypeManager.getTypeByJavaClass(javaMethod.getReturnType());
		}
		// 関数型生成
		AADLFuncType funcType = new AADLFuncType(name, funcRetType, paramTypes);
		return funcType;
	}
	
	public boolean isNearlyEqual(AADLType aType) {
		if (aType != null) {
			if (aType.getJavaClass().equals(this.javaClass)) {
				// 表面的なJava型が同じなら、同値とみなす
				return true;
			}
		}
		
		// Not nearly eqaul
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean isInstanceOf(Class javaClassType) {
		return javaClassType.isAssignableFrom(this.javaClass);
	}
	
	public boolean isInstanceOf(AADLType aType) {
		return isInstanceOf(aType.javaClass);
	}

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AADLType) {
			AADLType aInfo = (AADLType)obj;
			if (aInfo.aadlName.equals(this.aadlName) &&
				aInfo.javaClass.equals(this.javaClass))
			{
				// Same object
				return true;
			}
		}
		
		// Not equal
		return false;
	}

	@Override
	public int hashCode() {
		return this.localHashCode;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append("[");
		sb.append(this.getNameKey());
		sb.append(":");
		sb.append(this.getJavaConstructorName());
		sb.append("]");
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	// Java Generic クラス名取得
	protected String getJavaFixedGenericClassName(String className, AADLType...paramTypes) {
		return (className + getJavaFixedGenericParameters(paramTypes));
	}

	protected String getJavaExtendsGenericClassName(String className, AADLType...paramTypes) {
		//return (className + getJavaExtendsGenericParameters(paramTypes));
		// 変数宣言には、↑のタイプは指定できない
		return (className + getJavaFixedGenericParameters(paramTypes));
	}
	
	// Java Generic パラメータ取得
	protected String getJavaFixedGenericParameters(AADLType...paramTypes) {
		String retParams;
		TypeVariable<?>[] types = this.javaClass.getTypeParameters();
		if (types.length == 0) {
			// no generic class
			retParams = "";
		}
		else if (types.length == paramTypes.length) {
			StringBuffer sb = new StringBuffer();
			sb.append("<");
			sb.append(paramTypes[0].getJavaConstructorName());
			for (int i = 1; i < paramTypes.length; i++) {
				sb.append(",");
				sb.append(paramTypes[i].getJavaConstructorName());
			}
			sb.append(">");
			retParams = sb.toString();
		}
		else {
			String objClassName = "Object";
			StringBuffer sb = new StringBuffer();
			sb.append("<");
			sb.append(objClassName);
			for (int i = 1; i < types.length; i++) {
				sb.append(",");
				sb.append(objClassName);
			}
			sb.append(">");
			retParams = sb.toString();
		}
		return retParams;
	}

	/*--- 変数宣言には、このタイプは指定できない
	protected String getJavaExtendsGenericParameters(AADLType...paramTypes) {
		String retParams;
		TypeVariable<?>[] types = this.javaClass.getTypeParameters();
		if (types.length == 0) {
			// no generic class
			retParams = "";
		}
		else if (types.length == paramTypes.length) {
			StringBuffer sb = new StringBuffer();
			sb.append("<");
			sb.append("? extends " + paramTypes[0].getJavaClassName());
			for (int i = 1; i < paramTypes.length; i++) {
				sb.append(",");
				sb.append("? extends " + paramTypes[i].getJavaClassName());
			}
			sb.append(">");
			retParams = sb.toString();
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append("<");
			sb.append("?");
			for (int i = 1; i < types.length; i++) {
				sb.append(",");
				sb.append("?");
			}
			sb.append(">");
			retParams = sb.toString();
		}
		return retParams;
	}
	*/
	
	// Javaクラス情報から、Public constructor の情報を取得する
	protected Constructor getJavaConstructor(AADLType...paramTypes)
		throws NoSuchMethodException, SecurityException
	{
		Constructor retConst = null;
		Constructor[] consts = this.javaClass.getConstructors();
		for (Constructor ic : consts) {
			Class<?>[] args = ic.getParameterTypes();
			if (paramTypes.length == args.length) {
				retConst = ic;
				for (int i = 0; i < paramTypes.length; i++) {
					if (!paramTypes[i].isJavaAction() && !args[i].isAssignableFrom(paramTypes[i].getJavaClass())) {
						//--- 引数に渡せないデータ型
						retConst = null;
						break;
					}
				}
				if (retConst != null) {
					return retConst;
				}
			}
		}
		
		// 該当なし
		throw new NoSuchMethodException();
	}

	// Javaクラス情報から Public member field の情報を取得する
	protected Field getJavaField(String name)
		throws NoSuchFieldException, SecurityException
	{
		Field retField = null;
		retField = this.javaClass.getField(name);
		return retField;
	}

	// Javaクラス情報から Public member method の情報を取得する
	protected Method getJavaMethod(String name, AADLType...paramTypes)
		throws NoSuchMethodException, SecurityException
	{
		Method retMethod = null;
		Method[] methods = this.javaClass.getMethods();
		for (Method im : methods) {
			if (im.getName().equals(name)) {
				Class<?>[] args = im.getParameterTypes();
				if (paramTypes.length == args.length) {
					retMethod = im;
					for (int i = 0; i < paramTypes.length; i++) {
						if (!paramTypes[i].isJavaAction() && !args[i].isAssignableFrom(paramTypes[i].getJavaClass())) {
							//--- 引数に渡せないデータ型
							retMethod = null;
							break;
						}
					}
					if (retMethod != null) {
						return retMethod;
					}
				}
			}
		}
		
		// 該当なし
		throw new NoSuchMethodException();
	}

	// Javaクラス情報から Public static method の情報を取得する
	protected Method getJavaStaticMethod(String name, AADLType...paramTypes)
		throws NoSuchMethodException, SecurityException
	{
		Method retMethod = null;
		Method[] methods = this.javaClass.getMethods();
		for (Method im : methods) {
			if (im.getName().equals(name) && Modifier.isStatic(im.getModifiers())) {
				Class<?>[] args = im.getParameterTypes();
				if (paramTypes.length == args.length) {
					retMethod = im;
					for (int i = 0; i < paramTypes.length; i++) {
						if (!paramTypes[i].isJavaAction() && !args[i].isAssignableFrom(paramTypes[i].getJavaClass())) {
							//--- 引数に渡せないデータ型
							retMethod = null;
							break;
						}
					}
					if (retMethod != null) {
						return retMethod;
					}
				}
			}
		}
		
		// 該当なし
		throw new NoSuchMethodException();
	}
}
