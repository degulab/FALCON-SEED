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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLTypeManager.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLTypeManager.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLTypeManager.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLTypeManager.java	1.40	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLTypeManager.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLTypeManager.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;

import java.util.HashMap;

import ssac.aadlc.analysis.type.prim.*;


/**
 * AADLプリミティブの型情報を管理するクラス。
 * 
 * @version 2.1.0	2014/05/29
 */
public class AADLTypeManager
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/*
	static private final Pattern patAadlList = Pattern.compile("\\AList(?:<([^,]+)>)?\\z");
	static private final Pattern patAadlSet  = Pattern.compile("\\ASet(?:<([^,]+)>)?\\z");
	static private final Pattern patAadlMap  = Pattern.compile("\\AMap(?:<([^,]+),([^,]+)>)?\\z");
	
	static private final AADLTypeTable primTypes = new AADLTypeTable();
	*/

	static private final HashMap<String,Class<?>> mapJavaPrimNames = new HashMap<String,Class<?>>();
	static private final JavaClassTypeMap mapPrimitives = new JavaClassTypeMap();
	static private final TypeNameMap mapPrimNames = new TypeNameMap();
	static private final HashMap<Class<?>,Class<?>> mapJavaPrimBoxing = new HashMap<Class<?>,Class<?>>();
	static private final HashMap<Class<?>,Class<?>> mapJavaPrimUnboxing = new HashMap<Class<?>,Class<?>>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static {
		// AADL type name map - Simple Primitives
		mapPrimNames.set(AADLObject.instance);
		mapPrimNames.set(APrimBoolean.instance);
		mapPrimNames.set(APrimDecimal.instance);
		mapPrimNames.set(APrimString.instance);
		mapPrimNames.set(APrimExalge.instance);
		mapPrimNames.set(APrimExBase.instance);
		mapPrimNames.set(APrimExBasePattern.instance);
		mapPrimNames.set(APrimTransTable.instance);
		mapPrimNames.set(APrimTransMatrix.instance);
		mapPrimNames.set(APrimExTransfer.instance);
		mapPrimNames.set(APrimExAlgeSet.instance);
		mapPrimNames.set(APrimExBaseSet.instance);
		mapPrimNames.set(APrimExBasePatternSet.instance);
		mapPrimNames.set(APrimDtalge.instance);
		mapPrimNames.set(APrimDtBase.instance);
		mapPrimNames.set(APrimDtBasePattern.instance);
		mapPrimNames.set(APrimDtStringThesaurus.instance);
		mapPrimNames.set(APrimDtAlgeSet.instance);
		mapPrimNames.set(APrimDtBaseSet.instance);
		mapPrimNames.set(APrimDtBasePatternSet.instance);
		mapPrimNames.set(APrimTextFileReader.instance);
		mapPrimNames.set(APrimTextFileWriter.instance);
		mapPrimNames.set(APrimCsvFileReader.instance);
		mapPrimNames.set(APrimCsvFileWriter.instance);
		mapPrimNames.set(APrimDecimalRange.instance);
		//--- MQTT
		mapPrimNames.set(APrimMqttCsvParameter.instance);
		
		// AADL type name map - List primitives
		mapPrimNames.set(APrimBooleanList.instance);
		mapPrimNames.set(APrimDecimalList.instance);
		mapPrimNames.set(APrimStringList.instance);
		
		// Java Class map - Simple Primitives
		mapPrimitives.set(AADLObject.instance);
		mapPrimitives.set(APrimBoolean.instance);
		mapPrimitives.set(APrimDecimal.instance);
		mapPrimitives.set(APrimString.instance);
		mapPrimitives.set(APrimExalge.instance);
		mapPrimitives.set(APrimExBase.instance);
		mapPrimitives.set(APrimExBasePattern.instance);
		mapPrimitives.set(APrimTransTable.instance);
		mapPrimitives.set(APrimTransMatrix.instance);
		mapPrimitives.set(APrimExTransfer.instance);
		mapPrimitives.set(APrimExAlgeSet.instance);
		mapPrimitives.set(APrimExBaseSet.instance);
		mapPrimitives.set(APrimExBasePatternSet.instance);
		mapPrimitives.set(APrimDtalge.instance);
		mapPrimitives.set(APrimDtBase.instance);
		mapPrimitives.set(APrimDtBasePattern.instance);
		mapPrimitives.set(APrimDtStringThesaurus.instance);
		mapPrimitives.set(APrimDtAlgeSet.instance);
		mapPrimitives.set(APrimDtBaseSet.instance);
		mapPrimitives.set(APrimDtBasePatternSet.instance);
		mapPrimitives.set(APrimTextFileReader.instance);
		mapPrimitives.set(APrimTextFileWriter.instance);
		mapPrimitives.set(APrimCsvFileReader.instance);
		mapPrimitives.set(APrimCsvFileWriter.instance);
		mapPrimitives.set(APrimDecimalRange.instance);
		//--- MQTT
		mapPrimitives.set(APrimMqttCsvParameter.instance);
		
		// Java Primitive name map
		mapJavaPrimNames.put("boolean", Boolean.TYPE);
		mapJavaPrimNames.put("char"   , Character.TYPE);
		mapJavaPrimNames.put("byte"   , Byte.TYPE);
		mapJavaPrimNames.put("short"  , Short.TYPE);
		mapJavaPrimNames.put("int"    , Integer.TYPE);
		mapJavaPrimNames.put("long"   , Long.TYPE);
		mapJavaPrimNames.put("float"  , Float.TYPE);
		mapJavaPrimNames.put("double" , Double.TYPE);

		// Java Primitive Boxing/Unboxing map
		mapJavaPrimBoxing.put(Boolean.TYPE  , Boolean.class);
		mapJavaPrimBoxing.put(Character.TYPE, Character.class);
		mapJavaPrimBoxing.put(Byte.TYPE     , Byte.class);
		mapJavaPrimBoxing.put(Short.TYPE    , Short.class);
		mapJavaPrimBoxing.put(Integer.TYPE  , Integer.class);
		mapJavaPrimBoxing.put(Long.TYPE     , Long.class);
		mapJavaPrimBoxing.put(Float.TYPE    , Float.class);
		mapJavaPrimBoxing.put(Double.TYPE   , Double.class);
		mapJavaPrimUnboxing.put(Boolean.class  , Boolean.TYPE);
		mapJavaPrimUnboxing.put(Character.class, Character.TYPE);
		mapJavaPrimUnboxing.put(Byte.class     , Byte.TYPE);
		mapJavaPrimUnboxing.put(Short.class    , Short.TYPE);
		mapJavaPrimUnboxing.put(Integer.class  , Integer.TYPE);
		mapJavaPrimUnboxing.put(Long.class     , Long.TYPE);
		mapJavaPrimUnboxing.put(Float.class    , Float.TYPE);
		mapJavaPrimUnboxing.put(Double.class   , Double.TYPE);
	}
	
	public AADLTypeManager() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public AADLType getTypeByName(String name) {
		// Primitive
		AADLType retType = mapPrimNames.get(name);
		if (retType != null)
			return retType;
		// 組み込みデータ型以外は許可しない
		return null;
	}
	
	static public AADLType getTypeByJavaClass(Class<?> javaClass) {
		// Primitive
		AADLType retType = findPrimitiveType(javaClass);
		if (retType != null)
			return retType;

		// Java class
		if (javaClass.isArray()) {
			throw new IllegalArgumentException("Array class not supported!");
		}
		else if (javaClass.isPrimitive()) {
			return new AADLType(javaClass);
		}
		else if (java.util.Map.class.isAssignableFrom(javaClass)) {
			return new AADLMapType(javaClass, AADLObject.instance, AADLObject.instance);
		}
		else if (java.util.Set.class.isAssignableFrom(javaClass)) {
			return new AADLSetType(javaClass, AADLObject.instance);
		}
		else if (java.util.List.class.isAssignableFrom(javaClass)) {
			return new AADLListType(javaClass, AADLObject.instance);
		}
		else {
			return new AADLType(javaClass);
		}
	}
	
	static public boolean isJavaPrimitiveName(String name) {
		return mapJavaPrimNames.containsKey(name);
	}
	
	static public Class<?> getJavaPrimitiveByName(String name) {
		Class<?> ret = mapJavaPrimNames.get(name);
		if (ret != null)
			return ret;
		
		throw new IllegalArgumentException("'" + String.valueOf(name) + "' is not Java primitive.");
	}
	
	static public Class<?> getJavaClassByName(String name) {
		// Java primitive
		Class<?> ret = mapJavaPrimNames.get(name);
		if (ret != null)
			return ret;
		
		// Java class
		try {
			ret = Class.forName(name);
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException("'" + String.valueOf(name) + "' class not supported!", ex);
		}
		
		return ret;
	}
	
	static public AADLType getCollectionTypeByJavaClass(Class<?> javaClass, AADLType elemType) {
		if (java.util.Set.class.isAssignableFrom(javaClass)) {
			return new AADLSetType(javaClass, elemType);
		}
		else if (java.util.List.class.isAssignableFrom(javaClass)) {
			return new AADLListType(javaClass, elemType);
		}
		else {
			// not have Collection interface
			throw new IllegalArgumentException("No " + java.util.Map.class.getName());
		}
	}
	
	static public AADLType getMapTypeByJavaClass(Class<?> javaClass, AADLType keyType, AADLType valType) {
		if (java.util.Map.class.isAssignableFrom(javaClass)) {
			return new AADLMapType(javaClass, keyType, valType);
		}
		else {
			// not have Map interface
			throw new IllegalArgumentException("No " + java.util.Map.class.getName());
		}
	}
	
	static public boolean isPrimitive(AADLType dataType) {
		if (dataType instanceof AADLPrimitive)
			return true;
		else
			return false;
	}

	/**
	 * 指定されたデータ型が Java プリミティブ型の場合は、そのプリミティブ型に
	 * 対応するオブジェクトを示すデータ型に変換する。
	 * 変換できない場合は、引数に指定されたデータ型をそのまま返す。
	 * @param elemType	変換するデータ型
	 * @return	変換後のデータ型
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.40
	 */
	static public AADLType boxingJavaPrimitive(AADLType elemType) {
		if (elemType.isJavaPrimitive()) {
			Class<?> javaClass = mapJavaPrimBoxing.get(elemType.getJavaClass());
			if (javaClass != null) {
				elemType = findPrimitiveType(javaClass);
				if (elemType == null) {
					elemType = new AADLType(javaClass);
				}
			}
		}
		return elemType;
	}

	/*
	public AADLType getByName(String aadlName) {
		AADLType retType = null;
		// primitive
		retType = primTypes.getByName(aadlName);
		if (retType != null)
			return retType;
		// List
		retType = makeListByName(aadlName);
		if (retType != null)
			return retType;
		// Set
		retType = makeSetByName(aadlName);
		if (retType != null)
			return retType;
		// Map
		retType = makeMapByName(aadlName);
		if (retType != null)
			return retType;
		// no entry
		return null;
	}
	
	public AADLType getByJavaName(String javaName) {
		AADLType retType = null;
		// primitive
		retType = primTypes.getByJavaName(javaName);
		if (retType != null)
			return retType;
		// make java class info
		try {
			Class javaClass = Class.forName(javaName);
			retType = new AADLJavaClass(javaClass.getName(), javaClass);
		}
		catch (Exception ex) {
			// no class
		}
		if (retType != null)
			return retType;
		// no entry
		return null;
	}
	
	public AADLType getByJavaClass(Class javaClass) {
		AADLType retType = null;
		Iterator<AADLType> it = primTypes.aadlTypes().iterator();
		while (it.hasNext()) {
			AADLType dt = it.next();
			if (javaClass.equals(dt.getJavaClass())) {
				retType = dt;
				break;
			}
		}
		if (retType != null)
			return retType;
		// make java class info
		retType = new AADLJavaClass(javaClass.getName(), javaClass);
		return retType;
	}
	*/

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private AADLType findPrimitiveType(Class<?> javaClassType) {
		AADLType retType = mapPrimitives.get(javaClassType);
		return retType;
	}
	
	// AADL Type name による、AADLTypeマップ
	static protected class TypeNameMap extends HashMap<String,AADLType>
	{
		public TypeNameMap() {
		}
		
		public boolean contains(String name) {
			return this.containsKey(name);
		}
		
		public boolean contains(AADLType dataType) {
			return this.containsKey(dataType.getName());
		}
		
		public void set(String name, AADLType dataType) {
			if (containsKey(name))
				throw new IllegalArgumentException("Already exist " + name);
			put(name, dataType);
		}
		
		public void set(AADLType dataType) {
			set(dataType.getName(), dataType);
		}
	}
	
	// Java Class インスタンスによる、AADLTypeマップ
	static protected class JavaClassTypeMap extends HashMap<Class<?>,AADLType>
	{
		public JavaClassTypeMap() {
		}
		
		public boolean contains(Class<?> javaClassType) {
			return this.containsKey(javaClassType);
		}
		
		public boolean contains(AADLType dataType) {
			return this.containsKey(dataType.getJavaClass());
		}
		
		public void put(AADLType dataType) {
			put(dataType.getJavaClass(), dataType);
		}
		
		public void set(AADLType dataType) {
			if (contains(dataType))
				throw new IllegalArgumentException("Alread exist " + dataType.getJavaClass().getName());
			put(dataType);
		}
	}

	/*
	private AADLType makeListByName(String aadlName) {
		AADLType retType = null;
		Matcher mc = patAadlList.matcher(aadlName);
		if (mc.matches()) {
			if (mc.groupCount() > 0) {
				String elemName = mc.group(1);
				AADLType elemType = getByName(elemName);
				if (elemType != null) {
					retType = new APrimList(elemType);
				}
			}
			else {
				retType = APrimBasic.List;
			}
		}
		return retType;
	}
	
	private AADLType makeSetByName(String aadlName) {
		AADLType retType = null;
		Matcher mc = patAadlSet.matcher(aadlName);
		if (mc.matches()) {
			if (mc.groupCount() > 0) {
				String elemName = mc.group(1);
				AADLType elemType = getByName(elemName);
				if (elemType != null) {
					retType = new APrimSet(elemType);
				}
			}
			else {
				retType = APrimBasic.Set;
			}
		}
		return retType;
	}
	
	private AADLType makeMapByName(String aadlName) {
		AADLType retType = null;
		Matcher mc = patAadlMap.matcher(aadlName);
		if (mc.matches()) {
			if (mc.groupCount() > 1) {
				String keyName = mc.group(1);
				String valName = mc.group(2);
				AADLType keyType = getByName(keyName);
				AADLType valType = getByName(valName);
				if (keyType != null && valType != null) {
					retType = new APrimMap(keyType, valType);
				}
			}
			else {
				retType = APrimBasic.Map;
			}
		}
		return retType;
	}

	static protected class AADLTypeTable {
		private final HashMap<String,AADLType> aadlMap = new HashMap<String,AADLType>();
		private final HashMap<String,AADLType> javaMap = new HashMap<String,AADLType>();
		
		public AADLTypeTable() {
			
		}
		
		public void clear() {
			this.aadlMap.clear();
			this.javaMap.clear();
		}
		
		public Collection<AADLType> aadlTypes() {
			return this.aadlMap.values();
		}
		
		public Collection<AADLType> javaTypes() {
			return this.javaMap.values();
		}
		
		public boolean hasType(String aadlNameKey) {
			return this.aadlMap.containsKey(aadlNameKey);
		}
		
		public boolean hasJavaType(String javaNameKey) {
			return this.javaMap.containsKey(javaNameKey);
		}
		
		public AADLType getByName(String name) {
			return this.aadlMap.get(name);
		}
		
		public AADLType getByJavaName(String name) {
			return this.javaMap.get(name);
		}
		
		public void setByName(AADLType dataType) {
			set(dataType.getName(), dataType.getJavaNameKey(), dataType);
		}
		
		public void setByKeyName(AADLType dataType) {
			set(dataType.getNameKey(), dataType.getJavaNameKey(), dataType);
		}
		
		public void set(String aadlNameKey, String javaNameKey, AADLType dataType) {
			this.aadlMap.put(aadlNameKey, dataType);
			this.javaMap.put(javaNameKey, dataType);
		}
	}
	*/
}
