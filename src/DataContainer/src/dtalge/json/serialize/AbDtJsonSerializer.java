/*
 * @(#)AbDtJsonSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;

import dtalge.json.io.DtJsonInputReader;
import net.arnx.jsonic.JSONEventType;

/**
 * JSON 形式でのシリアライズ／デシリアライズを行うクラスの基本実装。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
abstract public class AbDtJsonSerializer implements DtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

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
	 * データ型名を先頭に持つオブジェクトの終端までスキップする。
	 * このメソッドの呼び出しは、データ型名オブジェクトの先頭キーバリューより後のトークンを読み出し、整合性を保持しつつ内容を読み飛ばす。
	 * @param reader	リーダー
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void ensureEndTypeNamedObject(DtJsonInputReader reader) throws IOException
	{
		JSONEventType etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			
			// value
			deserializeAndDropValue(reader, null);

			// next
			etype = reader.nextToken();
		}
		
		// Object end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedArrayError();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void deserializeAndDropValue(DtJsonInputReader reader, JSONEventType beginType) throws IOException
	{
		// begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == null) {
			return;	// end of input
		}
		else if (etype == JSONEventType.NULL) {
			return;	// null value
		}
		
		// types
		switch (etype) {
			case START_OBJECT:
				deserializeAndDropMap(reader, etype);
				break;
			case END_OBJECT:
				throw reader.createError(DtJsonInputReader.MSGID_UNEXPECTED_CHAR, "}");
			case START_ARRAY:
				deserializeAndDropArray(reader, etype);
				break;
			case END_ARRAY:
				throw reader.createError(DtJsonInputReader.MSGID_UNEXPECTED_CHAR, "]");
			case NAME:
				throw reader.createUnexpectedPrimitiveTypeError(etype);
			default:
				break;
		}
	}
	
	protected void deserializeAndDropMap(DtJsonInputReader reader, JSONEventType beginType) throws IOException
	{
		// Object begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == JSONEventType.NULL) {
			// null value
			return;
		}
		else if (etype != JSONEventType.START_OBJECT) {
			// unexpected type
			throw reader.createConversionError(etype, getTargetClass());
		}
		
		// entries
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			
			// value
			deserializeAndDropValue(reader, null);

			// next
			etype = reader.nextToken();
		}
		
		// Object end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedObjectError();
		}
	}
	
	protected void deserializeAndDropArray(DtJsonInputReader reader, JSONEventType beginType) throws IOException
	{
		// Array begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == JSONEventType.NULL) {
			// null value
			return;
		}
		else if (etype != JSONEventType.START_ARRAY) {
			// unexpected type
			throw reader.createConversionError(etype, getTargetClass());
		}
		
		// elements
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_ARRAY) {
			// element
			deserializeAndDropValue(reader, etype);
			
			// next
			etype = reader.nextToken();
		}
		
		// Array end
		if (etype != JSONEventType.END_ARRAY) {
			throw reader.createUnclosedArrayError();
		}
	}
	
	protected Object deserializeObjectWithTypeName(DtJsonInputReader reader, JSONEventType beginType) throws IOException
	{
		// Map begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == JSONEventType.NULL) {
			// null value
			return null;
		}
		else if (etype != JSONEventType.START_OBJECT) {
			// unexpected type
			throw reader.createConversionError(etype, getTargetClass());
		}
		
		// elements
		Object value = null;
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			value = deserializeValueWithTypeName(reader, key);

			// next
			etype = reader.nextToken();
		}
		
		// Map end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedArrayError();
		}
		
		// succeeded
		return value;
	}
	
	protected Object deserializeValueWithTypeName(DtJsonInputReader reader, String typename) throws IOException
	{
		// serializer
		DtJsonSerializer elemSerializer = DtJsonSerializerManager.instance().getSerializerByTypeName(typename);
		if (elemSerializer == null) {
			throw reader.createUnsupportedDataType(typename);
		}
		
		// deserialize value
		return elemSerializer.deserialize(reader, null);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
