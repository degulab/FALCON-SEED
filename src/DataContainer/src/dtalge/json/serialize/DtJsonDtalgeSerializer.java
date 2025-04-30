/*
 * @(#)DtJsonDtalgeSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;
import java.util.Map;

import dtalge.DtBase;
import dtalge.Dtalge;
import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import net.arnx.jsonic.JSONEventType;

/**
 * データ代数元(dtalge.Dtalge)の JSON 標準形でのシリアライズ／デシリアライズを行うクラス。<br>
 * JSON 形式は、次の通り。
 * <pre><code>
 * [
 *   {
 *     "dtbase": <i><b>&lt;データ代数基底の JSON 標準形&gt;</b></i>,
 *     "value": <i><b>&lt;string&gt; or &lt;number&gt; or &lt;boolean&gt;</b></i>
 *   },
 *   ...,
 *   {
 *     "exbase": <i><b>&lt;交換代数基底の JSON 標準形&gt;</b></i>,
 *     "value": <i><b>&lt;string&gt; or &lt;number&gt; or &lt;boolean&gt;</b></i>
 *   }
 * ]
 * </code></pre>
 * 交換代数元は、0 個以上の交換代数基底と実数値のペアを要素とする JSON 配列。
 * 交換代数元の JSON 標準形では、JSON 配列の要素となる JSON オブジェクトにオブジェクト型名は付加されない。
 * JSON 配列要素である JSON オブジェクトの構成は、次の通り。
 * <dl>
 * 	<dt>dtbase (データ代数基底の JSON 標準形)
 * 	</dt><dd>タイプ名が付加されない、交換代数基底の JSON 標準形 ({@link DtJsonDtBaseSerializer} を参照)
 * 	</dd>
 * 	<dt>value (string | number | boolean)
 * 	</dt><dd>データ代数基底とのペアとなる値。文字列(string)、実数値(number)、真偽値(boolean)、null値のいずれかであり、
 *  データ代数基底のデータ型キーには文字列(string)、実数値(decimal)、真偽値(boolean) のうち、、この値型に対応するデータ型が指定されていること。
 * 	</dd>
 * </dl>
 * 
 * <p>データ代数元(dtalge.Dtalge)のオブジェクト型名は、
 * <blockquote>
 * {@code !&Dtalge}
 * </blockquote>
 * であり、オブジェクト型名が付加された場合の JSON 形式は、次の通り。
 * <pre><code>
 * { <b>"!&amp;Dtalge"</b>:
 *   [
 *     {
 *       "dtbase": <i><b>&lt;データ代数基底の JSON 標準形&gt;</b></i>,
 *       "value": <i><b>&lt;string&gt; or &lt;number&gt; or &lt;boolean&gt;</b></i>
 *     },
 *     ...,
 *     {
 *       "exbase": <i><b>&lt;交換代数基底の JSON 標準形&gt;</b></i>,
 *       "value": <i><b>&lt;string&gt; or &lt;number&gt; or &lt;boolean&gt;</b></i>
 *     }
 *   ]
 * }
 * </code></pre>
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonDtalgeSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データ代数元(dtalge.Dtalge)の JSON 標準形であることを示すタイプ名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + Dtalge.class.getSimpleName();
	
	/** データ代数基底の JSON オブジェクトにおけるキー */
	static public final String	KEY_BASE	= "dtbase";
	/** 値の JSON オブジェクトにおけるキー */
	static public final String	KEY_VALUE	= "value";

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
	 * @return	{@code "!&Dtalge"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	データ代数元(dtalge.Dtalge)のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<Dtalge> getTargetClass() {
		return Dtalge.class;
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
		// begin type name
		if (!omitTypeName) {
			writer.beginObject();
			writer.writeName(getTypeName());
		}
		
		// value
		if (target == null) {
			// null
			writer.writeNull();
		}
		else {
			// Dtalge
			Dtalge alge = (Dtalge)target;
			//--- begin
			writer.beginArray();
			//--- elements
			for (Map.Entry<DtBase, Object> entry : alge.getUnmodifiableEntrySet()) {
				serializeDtalgeElem(writer, entry.getKey(), entry.getValue());
			}
			//--- end
			writer.endArray();
		}
		
		// end type name
		if (!omitTypeName) {
			writer.endObject();
		}
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
		Dtalge newalge = new Dtalge();
		while ((etype = deserializeDtalgeElem(reader, newalge)) == JSONEventType.START_OBJECT) {}
		
		// Array end
		if (etype != JSONEventType.END_ARRAY) {
			throw reader.createUnclosedArrayError();
		}
		
		// succeeded
		return newalge;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void serializeDtalgeElem(DtJsonOutputWriter writer, DtBase base, Object value) throws IOException
	{
		// begin
		writer.beginObject();
		
		// base
		DtJsonSerializer serializer = DtJsonSerializerManager.instance().getSerializerByClass(DtBase.class);
		writer.writeName(KEY_BASE);
		serializer.serialize(writer, base, OMIT_DATATYPE_NAME);
		
		// value
		writer.writeName(KEY_VALUE);
		writer.writeValue(value);
		
		// end
		writer.endObject();
	}
	
	protected JSONEventType deserializeDtalgeElem(DtJsonInputReader reader, Dtalge destalge) throws IOException
	{
		JSONEventType beginType = reader.nextToken();
		if (beginType == JSONEventType.END_ARRAY) {
			return beginType;	// 配列の終了
		}
		else if (beginType != JSONEventType.START_OBJECT) {
			throw reader.createUnexpectedTypeError(beginType, JSONEventType.START_OBJECT);
		}
		
		// elements
		DtBase base = null;
		Object value = null;
		JSONEventType etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			if (KEY_BASE.equalsIgnoreCase(key)) {
				// DtBase
				DtJsonSerializer serializer = DtJsonSerializerManager.instance().getSerializerByClass(DtBase.class);
				base = (DtBase)serializer.deserialize(reader, null);
			}
			else if (KEY_VALUE.equalsIgnoreCase(key)) {
				// value for Dtalge
				value = reader.nextPrimitiveValue();
			}
			else {
				// drop value
				deserializeAndDropValue(reader, null);
			}

			// next
			etype = reader.nextToken();
		}
		if (base == null) {
			throw reader.createUnspecifiedValueError(KEY_BASE);
		}
		
		// Object end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedObjectError();
		}
		
		// put element
		destalge.add(base, value);
		return beginType;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
