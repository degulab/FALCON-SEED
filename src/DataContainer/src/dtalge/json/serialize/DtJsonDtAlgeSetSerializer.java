/*
 * @(#)DtJsonDtAlgeSetSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;

import dtalge.DtAlgeSet;
import dtalge.Dtalge;
import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import net.arnx.jsonic.JSONEventType;

/**
 * データ代数集合(dtalge.DtAlgeSet)の JSON 標準形でのシリアライズ／デシリアライズを行うクラス。<br>
 * JSON 形式は、次の通り。
 * <pre><code>
 * [
 *   <i><b>&lt;データ代数元の JSON 標準形&gt;</b></i>,
 *   ...,
 *   <i><b>&lt;データ代数元の JSON 標準形&gt;</b></i>
 * ]
 * </code></pre>
 * データ代数集合は、0 個以上のデータ代数元(JSON 標準形については {@link DtJsonDtalgeSerializer} を参照)を要素とする JSON 配列。
 * データ代数集合の JSON 標準形では、JSON 配列の要素となる JSON オブジェクトにオブジェクト型名は付加されない。
 * 
 * <p>データ代数集合(dtalge.DtAlgeSet)のオブジェクト型名は、
 * <blockquote>
 * {@code !&DtAlgeSet}
 * </blockquote>
 * であり、オブジェクト型名が付加された場合の JSON 形式は、次の通り。
 * <pre><code>
 * { <b>"!&amp;DtAlgeSet"</b>:
 *   [
 *     <i><b>&lt;データ代数元の JSON 標準形&gt;</b></i>,
 *     ...,
 *     <i><b>&lt;データ代数元の JSON 標準形&gt;</b></i>
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
public class DtJsonDtAlgeSetSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データ代数集合(dtalge.DtAlgeSet)の JSON 標準形であることを示すタイプ名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + DtAlgeSet.class.getSimpleName();

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
	 * @return	{@code "!&DtAlgeSet"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	データ代数集合(dtalge.DtAlgeSet)のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<DtAlgeSet> getTargetClass() {
		return DtAlgeSet.class;
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
			// DtAlgeSet
			DtAlgeSet algeset = (DtAlgeSet)target;
			//--- begin
			writer.beginArray();
			//--- elements
			DtJsonSerializer elemSerializer = DtJsonSerializerManager.instance().getSerializerByClass(Dtalge.class);
			for (Dtalge elem : algeset) {
				elemSerializer.serialize(writer, elem, OMIT_DATATYPE_NAME);
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
		DtJsonSerializer algeSerializer = DtJsonSerializerManager.instance().getSerializerByClass(Dtalge.class);
		DtAlgeSet newalgeset = new DtAlgeSet();
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_ARRAY) {
			Dtalge newelem = (Dtalge)algeSerializer.deserialize(reader, etype);
			if (newelem != null) {
				newalgeset.add(newelem);
			}
			
			etype = reader.nextToken();
		}
		
		// Array end
		if (etype != JSONEventType.END_ARRAY) {
			throw reader.createUnclosedArrayError();
		}
		
		// succeeded
		return newalgeset;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
