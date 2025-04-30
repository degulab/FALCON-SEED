/*
 * @(#)DtJsonDtSlipMapSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;
import java.util.Map;

import dtalge.container.DtSlip;
import dtalge.container.DtSlipMap;
import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import exalge2.ExAlgeSet;
import net.arnx.jsonic.JSONEventType;

/**
 * データスリップマップ({@link dtalge.container.DtSlipMap})の JSON 標準形でのシリアライズ／デシリアライズを行うクラス。<br>
 * JSON 形式は、次の通り。
 * <pre><code>
 * {
 *   <i><b>&lt;キー&gt;</b></i> : <i><b>&lt;データスリップの JSON 標準形&gt;</b></i>,
 *   ...,
 *   <i><b>&lt;キー&gt;</b></i> : <i><b>&lt;データスリップの JSON 標準形&gt;</b></i>
 * }
 * </code></pre>
 * データスリップマップは、0 個以上のキー(任意の文字列)とデータスリップ(JSON 標準形については {@link DtJsonDtSlipSerializer} を参照)のペアを要素とする JSON オブジェクト。
 * データスリップマップにおけるデータスリップの JSON 標準形では、オブジェクト型名は付加されない。
 * 
 * <p>データスリップマップ({@link dtalge.container.DtSlipMap})のオブジェクト型名は、
 * <blockquote>
 * {@code !&DtSlipList}
 * </blockquote>
 * であり、オブジェクト型名が付加された場合の JSON 形式は、次の通り。
 * <pre><code>
 * { <b>"!&amp;DtSlipMap"</b>:
 *   {
 *     <i><b>&lt;キー&gt;</b></i> : <i><b>&lt;データスリップの JSON 標準形&gt;</b></i>,
 *     ...,
 *     <i><b>&lt;キー&gt;</b></i> : <i><b>&lt;データスリップの JSON 標準形&gt;</b></i>
 *   }
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
public class DtJsonDtSlipMapSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データスリップマップ({@link dtalge.container.DtSlipMap})の JSON 標準形であることを示すタイプ名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + DtSlipMap.class.getSimpleName();

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
	 * @return	{@code "!&DtSlipMap"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	データスリップマップ({@link dtalge.container.DtSlipMap})のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<ExAlgeSet> getTargetClass() {
		return ExAlgeSet.class;
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
			// DtSlipMap
			DtSlipMap map = (DtSlipMap)target;
			//--- begin
			writer.beginObject();
			//--- elements
			DtJsonSerializer slipSerializer = DtJsonSerializerManager.instance().getSerializerByClass(DtSlip.class);
			for (Map.Entry<String, DtSlip> entry : map.entrySet()) {
				writer.writeName(entry.getKey());
				slipSerializer.serialize(writer, entry.getValue(), OMIT_DATATYPE_NAME);
			}
			//--- end
			writer.endObject();
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
		
		// elements
		DtJsonSerializer slipSerializer = DtJsonSerializerManager.instance().getSerializerByClass(DtSlip.class);
		DtSlipMap newmap = new DtSlipMap();
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			newmap.put(key, (DtSlip)slipSerializer.deserialize(reader, null));

			// next
			etype = reader.nextToken();
		}
		
		// Object end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedObjectError();
		}
		
		// succeeded
		return newmap;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
