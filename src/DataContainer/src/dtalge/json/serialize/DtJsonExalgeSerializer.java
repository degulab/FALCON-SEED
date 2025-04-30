/*
 * @(#)DtJsonExalgeSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import exalge2.ExBase;
import exalge2.Exalge;
import net.arnx.jsonic.JSONEventType;

/**
 * 交換代数元(exalge2.Exalge)の JSON 標準形でのシリアライズ／デシリアライズを行うクラス。<br>
 * JSON 形式は、次の通り。
 * <pre><code>
 * [
 *   {
 *     "exbase": <i><b>&lt;交換代数基底の JSON 標準形&gt;</b></i>,
 *     "value": <i><b>&lt;number&gt;</b></i>
 *   },
 *   ...,
 *   {
 *     "exbase": <i><b>&lt;交換代数基底の JSON 標準形&gt;</b></i>,
 *     "value": <i><b>&lt;number&gt;</b></i>
 *   }
 * ]
 * </code></pre>
 * 交換代数元は、0 個以上の交換代数基底と実数値のペアを要素とする JSON 配列。
 * 交換代数元の JSON 標準形では、JSON 配列の要素となる JSON オブジェクトにオブジェクト型名は付加されない。
 * JSON 配列要素である JSON オブジェクトの構成は、次の通り。
 * <dl>
 * 	<dt>exbase (交換代数基底の JSON 標準形)
 * 	</dt><dd>タイプ名が付加されない、交換代数基底の JSON 標準形 ({@link DtJsonExBaseSerializer} を参照)
 * 	</dd>
 * 	<dt>value (number)
 * 	</dt><dd>交換代数基底とのペアとなる実数値。
 * 	</dd>
 * </dl>
 * 
 * <p>交換代数元(exalge2.Exalge)のオブジェクト型名は、
 * <blockquote>
 * {@code !&Exalge}
 * </blockquote>
 * であり、オブジェクト型名が付加された場合の JSON 形式は、次の通り。
 * <pre><code>
 * { <b>"!&amp;Exalge"</b>:
 *   [
 *     {
 *       "exbase": <i><b>&lt;交換代数基底の JSON 標準形&gt;</b></i>,
 *       "value": <i><b>&lt;number&gt;</b></i>
 *     },
 *     ...,
 *     {
 *       "exbase": <i><b>&lt;交換代数基底の JSON 標準形&gt;</b></i>,
 *       "value": <i><b>&lt;number&gt;</b></i>
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
public class DtJsonExalgeSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 交換代数元(exalge2.Exalge)の JSON 標準形であることを示すタイプ名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + Exalge.class.getSimpleName();
	
	/** 交換代数基底の JSON オブジェクトにおけるキー */
	static public final String	KEY_BASE	= "exbase";
	/** 実数値の JSON オブジェクトにおけるキー */
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
	 * @return	{@code "!&Exalge"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	交換代数元(exalge2.Exalge)のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<Exalge> getTargetClass() {
		return Exalge.class;
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
			// Exalge
			Exalge alge = (Exalge)target;
			//--- begin
			writer.beginArray();
			//--- elements
			for (Map.Entry<ExBase, BigDecimal> entry : alge.getUnmodifiableEntrySet()) {
				serializeExalgeElem(writer, entry.getKey(), entry.getValue());
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
	 * @param reader	読み込み対象のリーダー
	 * @param beginType	直前に読み込まれたトークンタイプ、先頭のトークンを新たに読み込む場合は <code>null</code>
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
		Exalge newalge = new Exalge();
		while ((etype = deserializeExalgeElem(reader, newalge)) == JSONEventType.START_OBJECT) {}
		
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
	
	protected void serializeExalgeElem(DtJsonOutputWriter writer, ExBase base, BigDecimal value) throws IOException
	{
		// begin
		writer.beginObject();
		
		// base
		DtJsonSerializer serializer = DtJsonSerializerManager.instance().getSerializerByClass(ExBase.class);
		writer.writeName(KEY_BASE);
		serializer.serialize(writer, base, OMIT_DATATYPE_NAME);
		
		// value
		writer.writeName(KEY_VALUE);
		writer.writeValue(value);
		
		// end
		writer.endObject();
	}
	
	protected JSONEventType deserializeExalgeElem(DtJsonInputReader reader, Exalge destalge) throws IOException
	{
		JSONEventType beginType = reader.nextToken();
		if (beginType == JSONEventType.END_ARRAY) {
			return beginType;	// 配列の終了
		}
		else if (beginType != JSONEventType.START_OBJECT) {
			throw reader.createUnexpectedTypeError(beginType, JSONEventType.START_OBJECT);
		}
		
		// elements
		ExBase base = null;
		BigDecimal value = null;
		JSONEventType etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			if (KEY_BASE.equalsIgnoreCase(key)) {
				// ExBase
				DtJsonSerializer serializer = DtJsonSerializerManager.instance().getSerializerByClass(ExBase.class);
				base = (ExBase)serializer.deserialize(reader, null);
			}
			else if (KEY_VALUE.equalsIgnoreCase(key)) {
				// Decimal
				value = reader.nextNumberValue();
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
