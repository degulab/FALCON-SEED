/*
 * @(#)DtJsonDefaultSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dtalge.json.DtJsonList;
import dtalge.json.DtJsonMap;
import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import net.arnx.jsonic.JSONEventType;

/**
 * 一般的な JSON 形式でのシリアライズ／デシリアライズを行うクラス。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonDefaultSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** インターフェース実装の為の、オブジェクト型名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + "json";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * シリアライズ対象オブジェクトのオブジェクト型名を取得する。
	 * @return	{@code "!&json"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	{@link java.lang.Object} のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<Object> getTargetClass() {
		return Object.class;
	}
	
	/**
	 * <em>target</em> の内容を、<em>writer</em> に JSON フォーマットで出力する。 
	 * @param writer	出力先のライター
	 * @param target	出力対象のオブジェクト
	 * @param omitTypeName	シリアライズ時にオブジェクト型名を付加して出力するには {@code true}、付加せずに出力するには {@code false} を指定する。
	 * @throws NullPointerException	<em>writer</em> が <code>null</code> の場合
	 * @throws ClassCastException	<em>target</em> が <code>null</code> 以外であり、処理可能なクラスに割り当てられない場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void serialize(DtJsonOutputWriter writer, Object target, boolean omitTypeName) throws IOException
	{
		// null
		if (target == null) {
			writer.writeValue(null);
			return;
		}
		
		// special class
		DtJsonSerializer serializer = DtJsonSerializerManager.instance().getSerializerByObject(target);
		if (serializer != null) {
			serializer.serialize(writer, target, omitTypeName);
			return;	// completed
		}
		
		// Map
		if (serializeForMap(writer, target, omitTypeName)) {
			return;	// completed
		}
		
		// Array
		if (serializeForArray(writer, target, omitTypeName)) {
			return;	// completed
		}
		
		// Primitives
		writer.writeValue(target);
	}
	
	/**
	 * <em>reader</em> の現在の読み込み位置から JSON フォーマットで読み込み、デシリアライズしたオブジェクトを返す。
	 * このメソッドでは基本的に、オブジェクト型名が付加されていない JSON 標準形のデシリアライズを行う。
	 * @param reader		読み込み対象のリーダー
	 * @param beginType		直前に読み込まれたトークンタイプ、先頭のトークンを新たに読み込む場合は <code>null</code>
	 * @return	デシリアライズされたオブジェクト、もしくは <code>null</code>
	 * @throws NullPointerException <em>reader</em> が <code>null</code> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public Object deserialize(DtJsonInputReader reader, JSONEventType beginType) throws IOException
	{
		// begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == null) {
			return null;	// end of input
		}
		else if (etype == JSONEventType.NULL) {
			return null;	// null value
		}
		
		// types
		switch (etype) {
			case START_OBJECT:
				return deserializeMap(reader, etype);
			case END_OBJECT:
				throw reader.createError(DtJsonInputReader.MSGID_UNEXPECTED_CHAR, "}");
			case START_ARRAY:
				return deserializeArray(reader, etype);
			case END_ARRAY:
				throw reader.createError(DtJsonInputReader.MSGID_UNEXPECTED_CHAR, "]");
			case NAME:
				throw reader.createUnexpectedPrimitiveTypeError(etype);
			case STRING:
				return reader.getStringValue();
			case NUMBER:
				return reader.getNumberValue();
			case BOOLEAN:
				return reader.getBooleanValue();
			case NULL:
				return null;
			default:
				throw new IllegalArgumentException("Undefined JSONEventType: " + String.valueOf(etype));
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean serializeForMap(DtJsonOutputWriter writer, Object target, boolean omitTypeName) throws IOException
	{
		if (!(target instanceof Map)) {
			// not map
			return false;
		}
		
		Map<?,?> map = (Map<?,?>)target;
		//--- begin
		writer.beginObject();
		//--- entries
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			//--- name
			writer.writeName(String.valueOf(entry.getKey()));
			
			//--- value
			serialize(writer, entry.getValue(), omitTypeName);
		}
		//--- end
		writer.endObject();
		return true;
	}
	
	protected boolean serializeForArray(DtJsonOutputWriter writer, Object target, boolean omitTypeName) throws IOException
	{
		if (target instanceof Iterable) {
			// List, Collection, etc.
			writer.beginArray();
			for (Object elem : (Iterable<?>)target) {
				serialize(writer, elem, omitTypeName);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof Iterator) {
			// Iterator
			writer.beginArray();
			Iterator<?> it = (Iterator<?>)target;
			while (it.hasNext()) {
				serialize(writer, it.next(), omitTypeName);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof Enumeration) {
			// Enumeration
			writer.beginArray();
			Enumeration<?> l = (Enumeration<?>)target;
			while (l.hasMoreElements()) {
				serialize(writer, l.nextElement(), omitTypeName);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof boolean[]) {
			// boolean[]
			writer.beginArray();
			for (boolean elem : (boolean[])target) {
				writer.writeValue(elem);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof short[]) {
			// boolean[]
			writer.beginArray();
			for (short elem : (short[])target) {
				writer.writeValue(elem);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof int[]) {
			// boolean[]
			writer.beginArray();
			for (int elem : (int[])target) {
				writer.writeValue(elem);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof long[]) {
			// boolean[]
			writer.beginArray();
			for (long elem : (long[])target) {
				writer.writeValue(elem);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof float[]) {
			// boolean[]
			writer.beginArray();
			for (float elem : (float[])target) {
				writer.writeValue(elem);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof double[]) {
			// boolean[]
			writer.beginArray();
			for (double elem : (double[])target) {
				writer.writeValue(elem);
			}
			writer.endArray();
			return true;
		}
		else if (target instanceof Object[]) {
			// boolean[]
			writer.beginArray();
			for (Object elem : (Object[])target) {
				serialize(writer, elem, omitTypeName);
			}
			writer.endArray();
			return true;
		}
		else if (target != null && target.getClass().isArray()) {
			// boolean[]
			writer.beginArray();
			for (Object elem : (Object[])target) {
				serialize(writer, elem, omitTypeName);
			}
			writer.endArray();
			return true;
		}
		
		// not array
		return false;
	}
	
	protected Object deserializeMap(DtJsonInputReader reader, JSONEventType beginType) throws IOException
	{
		// Object begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == JSONEventType.NULL) {
			// null value
			return null;
		}
		else if (etype != JSONEventType.START_OBJECT) {
			// unexpected type
			throw reader.createConversionError(etype, getTargetClass());
		}
		
		// first element
		etype = reader.nextToken();
		if (etype == JSONEventType.END_OBJECT) {
			// empty map
			return new LinkedHashMap<String, Object>();
		}
		else if (etype != JSONEventType.NAME) {
			throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
		}
		String key = reader.getStringValue();
		if (DtJsonSerializerManager.instance().startWithTypeNamePrefix(key)) {
			// タイプ名が指定されたオブジェクト
			Object value = deserializeValueWithTypeName(reader, key);
			//--- タイプ名が指定されていた場合、以降の要素はスキップ
			etype = reader.nextToken();
			while (etype != null && etype != JSONEventType.END_OBJECT) {
				// typename
				if (etype != JSONEventType.NAME) {
					throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
				}
				// value
				deserializeAndDropValue(reader, null);

				// next
				etype = reader.nextToken();
			}
			//--- 終了
			if (etype != JSONEventType.END_OBJECT) {
				throw reader.createUnclosedObjectError();
			}
			return value;
		}
		
		// 通常の KeyValue として処理
		DtJsonMap map = new DtJsonMap();
		//--- 最初の値
		map.put(key, deserialize(reader, null));
		//--- 2つめ以降の KeyValue
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			key = reader.getStringValue();
			
			// value
			map.put(key, deserialize(reader, null));

			// next
			etype = reader.nextToken();
		}
		
		// Object end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedObjectError();
		}
		
		// succeeded
		return map;
	}
	
	protected List<?> deserializeArray(DtJsonInputReader reader, JSONEventType beginType) throws IOException
	{
		// Array begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == JSONEventType.NULL) {
			// null value
			return null;
		}
		else if (etype != JSONEventType.START_ARRAY) {
			// unexpected type
			throw reader.createConversionError(etype, getTargetClass());
		}
		
		// elements
		DtJsonList list = new DtJsonList();
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_ARRAY) {
			// element
			list.add( deserialize(reader, etype) );
			
			// next
			etype = reader.nextToken();
		}
		
		// Array end
		if (etype != JSONEventType.END_ARRAY) {
			throw reader.createUnclosedArrayError();
		}
		
		// succeeded
		return list;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
