/*
 * @(#)DtJsonDtSlipSerializer.java	0.1.1	2022/12/14
 *     - modified by Y.Ishizuka(PieCake.inc,) - buf fixed
 * @(#)DtJsonDtSlipSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;
import java.util.Map;

import dtalge.Dtalge;
import dtalge.container.DtSlip;
import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import net.arnx.jsonic.JSONEventType;

/**
 * データスリップ({@link dtalge.container.DtSlip})の JSON 標準形でのシリアライズ／デシリアライズを行うクラス。<br>
 * JSON 形式は、次の通り。
 * <pre><code>
 * {
 *   "note": <i><b>&lt;データ代数元の JSON 標準形&gt;</b></i>,
 *   "objects": {
 *     <i><b>&lt;任意のデータオブジェクト名&gt;</b></i> : { <i><b>&lt;オブジェクト型名&gt;</b></i> : <i><b>&lt;データオブジェクトの JSON 標準形&gt;</b></i> },
 *     ...,
 *     <i><b>&lt;任意のデータオブジェクト名&gt;</b></i> : { <i><b>&lt;オブジェクト型名&gt;</b></i> : <i><b>&lt;データオブジェクトの JSON 標準形&gt;</b></i> }
 *   }
 * }
 * </code></pre>
 * <dl>
 * 	<dt>note (データ代数元の JSON 標準形)
 * 	</dt><dd>データスリップにおける説明等の情報を保持するデータ代数元を値として保持する。
 *  このデータ代数元の JSON 標準形 ({@link DtJsonExBaseSerializer} を参照) には、オブジェクト型名は付加されない。
 *  要素が空のデータ代数元も許容するが、この項目は必須。
 * 	</dd>
 * 	<dt>objects (JSON オブジェクト)
 * 	</dt><dd>任意のデータオブジェクト名とデータオブジェクトのオブジェクト型名付き JSON 標準形のペアを要素とする JSONオ ブジェクトを値として保持する。
 *  基本的に、任意のデータオブジェクト名は重複しないことを前提とする。
 *  要素が空の JSON オブジェクトを許容するが、この項目は必須。
 * 	</dd>
 * </dl>
 * データスリップの "objects" に格納可能なデータオブジェクトは、次の通り。
 * <table>
 * 	 <caption>&nbsp;</caption>
 *   <tr>
 *     <th>データオブジェクトの種類</th>
 *     <th>オブジェクト型名</th>
 *     <th>データオブジェクトの JSON 標準形</th>
 *   </tr>
 *   <tr>
 *     <td>交換代数集合</td>
 *     <td>{@code "!&ExAlgeSet"}</td>
 *     <td>交換代数集合の JSON 標準形 ({@link DtJsonExAlgeSetSerializer} を参照)</td>
 *   </tr>
 *   <tr>
 *     <td>交換代数元</td>
 *     <td>{@code "!&Exalge"}</td>
 *     <td>交換代数元の JSON 標準形 ({@link DtJsonExalgeSerializer} を参照)</td>
 *   </tr>
 *   <tr>
 *     <td>データ代数集合</td>
 *     <td>{@code "!&DtAlgeSet"}</td>
 *     <td>データ代数集合の JSON 標準形 ({@link DtJsonDtAlgeSetSerializer} を参照)</td>
 *   </tr>
 *   <tr>
 *     <td>データ代数元</td>
 *     <td>{@code "!&Dtalge"}</td>
 *     <td>データ代数元の JSON 標準形 ({@link DtJsonDtalgeSerializer} を参照)</td>
 *   </tr>
 * </table>
 * 
 * <p>データスリップ({@link dtalge.container.DtSlip})のオブジェクト型名は、
 * <blockquote>
 * {@code !&DtSlip}
 * </blockquote>
 * であり、オブジェクト型名が付加された場合の JSON 形式は、次の通り。
 * <pre><code>
 * { <b>"!&amp;DtSlip"</b>:
 *   {
 *     "note": <i><b>&lt;データ代数元の JSON 標準形&gt;</b></i>,
 *     "objects": {
 *       <i><b>&lt;任意のデータオブジェクト名&gt;</b></i> : { <i><b>&lt;オブジェクト型名&gt;</b></i> : <i><b>&lt;データオブジェクトの JSON 標準形&gt;</b></i> },
 *       ...,
 *       <i><b>&lt;任意のデータオブジェクト名&gt;</b></i> : { <i><b>&lt;オブジェクト型名&gt;</b></i> : <i><b>&lt;データオブジェクトの JSON 標準形&gt;</b></i> }
 *     }
 *   }
 * }
 * </code></pre>
 * 
 * @version 0.1.1
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonDtSlipSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データスリップ({@link dtalge.container.DtSlip})の JSON 標準形であることを示すタイプ名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + DtSlip.class.getSimpleName();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** ノートを示す JSON オブジェクトにおけるキー */
	static protected final String	KEY_NOTE	= "note";
	/** オブジェクトを示す JSON オブジェクトにおけるキー */
	static protected final String	KEY_OBJECTS	= "objects";

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * シリアライズ対象オブジェクトのオブジェクト型名を取得する。
	 * @return	{@code "!&DtSlip"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	データスリップ({@link dtalge.container.DtSlip})のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<DtSlip> getTargetClass() {
		return DtSlip.class;
	}
	
	/**
	 * 指定されたオブジェクトを、このシリアライザーで処理可能なクラスにキャストする。
	 * @param obj	対象のオブジェクト
	 * @return	キャスト後のオブジェクト
	 * @throws ClassCastException	オブジェクトが <code>null</code> 以外であり、処理可能なクラスに割り当てられない場合
	 */
	public DtSlip cast(Object obj) {
		return getTargetClass().cast(obj);
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
			// DtSlip
			DtSlip slip = (DtSlip)target;
			//--- begin
			writer.beginObject();
			//--- note
			writer.writeName(KEY_NOTE);
			if (slip.isNoteEmpty()) {
				// empty
				writer.beginArray();
				writer.endArray();
			}
			else {
				// Dtalge
				DtJsonSerializer dtalgeSerializer = DtJsonSerializerManager.instance().getSerializerByClass(Dtalge.class);
				dtalgeSerializer.serialize(writer, slip.getNote(), OMIT_DATATYPE_NAME);
			}
			//--- objects
			writer.writeName(KEY_OBJECTS);
			writer.beginObject();
			if (!slip.isObjectEmpty()) {
				for (Map.Entry<String, Object> entry : slip.getUnmodifiableObjects().entrySet()) {
					writer.writeName(entry.getKey());
					serializeSlipObjectElement(writer, entry.getValue());
				}
			}
			writer.endObject();
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
		boolean existNote = false;
		boolean existObjects = false;
		DtSlip newslip = new DtSlip();
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			if (KEY_NOTE.equalsIgnoreCase(key)) {
				DtJsonSerializer noteSerializer = DtJsonSerializerManager.instance().getSerializerByClass(Dtalge.class);
				Dtalge note = (Dtalge)noteSerializer.deserialize(reader, null);
				newslip.setNote(note);
				existNote = true;
			}
			else if (KEY_OBJECTS.equalsIgnoreCase(key)) {
				deserializeSlipObjects(reader, null, newslip);
				existObjects = true;
			}

			// next
			etype = reader.nextToken();
		}
		
		// Map end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedArrayError();
		}
		
		// check
		if (!existNote) {
			throw reader.createUnspecifiedValueError(KEY_NOTE);
		}
		if (!existObjects) {
			throw reader.createUnspecifiedValueError(KEY_OBJECTS);
		}
		
		// succeeded
		return newslip;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void serializeSlipObjectElement(DtJsonOutputWriter writer, Object elem) throws IOException
	{
		if (elem == null) {
			writer.writeValue(null);
			return;
		}
		
		DtJsonSerializer elemSerializer = DtJsonSerializerManager.instance().getSerializerByObject(elem);
		if (elemSerializer == null) {
			throw new IllegalArgumentException("Unsupported object for element of objects in DtSlip: " + elem.getClass().getName());
		}
		elemSerializer.serialize(writer, elem, OUTPUT_DATATYPE_NAME);
	}
	
	protected void deserializeSlipObjects(DtJsonInputReader reader, JSONEventType beginType, DtSlip destslip) throws IOException
	{
		// Map begin
		JSONEventType etype = (beginType != null ? beginType : reader.nextToken());
		if (etype == JSONEventType.NULL) {
			// null value
			return;
		}
		else if (etype != JSONEventType.START_OBJECT) {
			// unexpected type
			throw reader.createConversionError(etype, getTargetClass());
		}
		
		// elements
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			Object value = deserializeObjectWithTypeName(reader, null);
			
			// add to slip
			destslip.putObject(key, value);

			// next
			etype = reader.nextToken();
		}
		
		// Map end
		if (etype != JSONEventType.END_OBJECT) {
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
			DtJsonSerializer elemSerializer = DtJsonSerializerManager.instance().getSerializerByTypeName(key);
			if (elemSerializer == null) {
				throw reader.createUnsupportedDataType(key);
			}
			if (!DtSlip.isObjectTypeSupported(elemSerializer.getTargetClass())) {
				throw reader.createUnsupportedDataType(key);
			}
			value = elemSerializer.deserialize(reader, null);

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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
