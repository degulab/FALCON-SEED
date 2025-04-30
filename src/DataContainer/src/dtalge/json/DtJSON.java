/*
 * @(#)DtJSON.java	0.1.0	2022/07/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import dtalge.json.io.DtJsonFileInputReader;
import dtalge.json.io.DtJsonFileOutputWriter;
import dtalge.json.io.DtJsonStringInputReader;
import dtalge.json.io.DtJsonStringOutputWriter;
import dtalge.json.serialize.DtJsonSerializer;
import dtalge.json.serialize.DtJsonSerializerManager;

/**
 * データコンテナにおける JSON ユーティリティ。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
final public class DtJSON
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
	
	private DtJSON() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/*
	static public int getSize(Object obj) {
		if (obj instanceof Map) {
			return ((Map<?,?>)obj).size();
		}
		else if (obj instanceof Collection) {
			return ((Collection<?>)obj).size();
		}
		else {
			throw new ClassCastException();
		}
	}
	
	static public Object getObjectFromMap(Object map, String key) {
		return ((Map<?, ?>)map).get(key);
	}
	
	static public Object getObjectFromArray(Object ary, BigDecimal index) {
		return getObjectFromArray(ary, index.intValueExact());
	}
	
	static public Object getObjectFromArray(Object ary, int index) {
		List<?> list = (List<?>)ary;
		if (index >= 0 && index < list.size()) {
			return list.get(index);
		} else {
			return null;
		}
	}
	*/
	
	static public String toJsonString(Object obj) throws IOException
	{
		StringBuilder strbuf = new StringBuilder();
		DtJsonStringOutputWriter writer = new DtJsonStringOutputWriter(strbuf);
		try {
			DtJsonSerializer spec = DtJsonSerializerManager.instance().getSerializerByObject(obj);
			if (spec != null) {
				spec.serialize(writer, obj, DtJsonSerializer.OUTPUT_DATATYPE_NAME);	// データ型は省略しない
			}
			else {
				DtJsonSerializerManager.instance().serializeByDefault(writer, obj, DtJsonSerializer.OUTPUT_DATATYPE_NAME);	// データ型は省略しない
			}
		}
		finally {
			writer.closeSilent();
		}
		return strbuf.toString();
	}
	
	static public String toJsonString(Object obj, boolean omitTypeName) throws IOException
	{
		StringBuilder strbuf = new StringBuilder();
		DtJsonStringOutputWriter writer = new DtJsonStringOutputWriter(strbuf);
		try {
			DtJsonSerializerManager.instance().serializeByDefault(writer, obj, omitTypeName);
		}
		finally {
			writer.closeSilent();
		}
		return strbuf.toString();
	}
	
	static public Object fromJsonString(String jsontext) throws IOException
	{
		DtJsonStringInputReader reader = new DtJsonStringInputReader(jsontext);
		try {
			return DtJsonSerializerManager.instance().deserializeByDefault(reader);
		}
		finally {
			reader.closeSilent();
		}
	}
	
	static public <T> T fromJsonString(String jsontext, Class<T> ctype) throws IOException
	{
		DtJsonStringInputReader reader = new DtJsonStringInputReader(jsontext);
		try {
			return DtJsonSerializerManager.instance().deserializeAsDataType(reader, ctype);
		}
		finally {
			reader.closeSilent();
		}
	}
	
	static public void serialize(File jsonfile, Object obj) throws IOException
	{
		DtJsonFileOutputWriter writer = new DtJsonFileOutputWriter(jsonfile);
		try {
			DtJsonSerializer spec = DtJsonSerializerManager.instance().getSerializerByObject(obj);
			if (spec != null) {
				spec.serialize(writer, obj, DtJsonSerializer.OUTPUT_DATATYPE_NAME);	// データ型は省略しない
			}
			else {
				DtJsonSerializerManager.instance().serializeByDefault(writer, obj, DtJsonSerializer.OUTPUT_DATATYPE_NAME);	// データ型は省略しない
			}
		}
		finally {
			writer.closeSilent();
		}
	}
	
	static public void serialize(File jsonfile, Object obj, boolean omitTypeName) throws IOException
	{
		DtJsonFileOutputWriter writer = new DtJsonFileOutputWriter(jsonfile);
		try {
			DtJsonSerializerManager.instance().serializeByDefault(writer, obj, omitTypeName);
		}
		finally {
			writer.closeSilent();
		}
	}
	
	static public Object deserialize(File jsonfile) throws FileNotFoundException, IOException
	{
		DtJsonFileInputReader reader = new DtJsonFileInputReader(jsonfile);
		try {
			return DtJsonSerializerManager.instance().deserializeByDefault(reader);
		}
		finally {
			reader.closeSilent();
		}
	}
	
	static public <T> T deserialize(File jsonfile, Class<? extends T> ctype) throws FileNotFoundException, IOException
	{
		DtJsonFileInputReader reader = new DtJsonFileInputReader(jsonfile);
		try {
			return DtJsonSerializerManager.instance().deserializeAsDataType(reader, ctype);
		}
		finally {
			reader.closeSilent();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
