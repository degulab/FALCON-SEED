/*
 * @(#)DtJsonExBaseSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;

import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import exalge2.ExBase;
import exalge2.ExtendedKeyID;
import exalge2.util.Strings;
import net.arnx.jsonic.JSONEventType;

/**
 * 交換代数基底(exalge2.ExBase)の JSON 標準形でのシリアライズ／デシリアライズを行うクラス。<br>
 * JSON 形式は、次の通り。
 * <pre><code>
 * {
 *   "hat": <i><b>&lt;boolean&gt;</b></i>,
 *   "name": <i><b>&lt;string&gt;</b></i>,
 *   "unit": <i><b>&lt;string&gt;</b></i>,
 *   "time": <i><b>&lt;string&gt;</b></i>,
 *   "subject": <i><b>&lt;string&gt;</b></i>
 * }
 * </code></pre>
 * <dl>
 * 	<dt>hat (boolean, 省略可)
 * 	</dt><dd>ハット付き交換代数基底を表す場合は true、ハットなし交換代数基底を表す場合は false を記述する。省略された場合は、ハットなし交換代数基底と見なされる。
 * 	</dd>
 * 	<dt>name (string)
 * 	</dt><dd>交換代数基底の名前（name）キー。省略や空文字は許可されない。
 * 	</dd>
 * 	<dt>unit (string, 省略可)
 * 	</dt><dd>交換代数基底の単位（unit）キー。
 * 	</dd>
 * 	<dt>time (string, 省略可)
 * 	</dt><dd>交換代数基底の時間（time）キー。
 * 	</dd>
 * 	<dt>subject (string, 省略可)
 * 	</dt><dd>交換代数基底の主体（subject）キー。
 * 	</dd>
 * </dl>
 * なお、データ代数基底の各基底キーには、以下の文字は使用できない。
 * <blockquote>
 * {@code < > - , ^ " % & ? | @ ' " (空白)}
 * </blockquote>
 * 
 * <p>交換代数基底(exalge2.ExBase)のオブジェクト型名は、
 * <blockquote>
 * {@code !&ExBase}
 * </blockquote>
 * であり、オブジェクト型名が付加された場合の JSON 形式は、次の通り。
 * <pre><code>
 * { <b>"!&amp;ExBase"</b>:
 *   {
 *     "hat": <i><b>&lt;boolean&gt;</b></i>,
 *     "name": <i><b>&lt;string&gt;</b></i>,
 *     "unit": <i><b>&lt;string&gt;</b></i>,
 *     "time": <i><b>&lt;string&gt;</b></i>,
 *     "subject": <i><b>&lt;string&gt;</b></i>
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
public class DtJsonExBaseSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 交換代数基底(exalge2.ExBase)の JSON 標準形であることを示すタイプ名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + ExBase.class.getSimpleName();
	
	/** 交換代数基底ハットキーの JSON オブジェクトにおけるキー */
	static public final String	KEY_HAT		= "hat";
	/** 交換代数基底名前キーの JSON オブジェクトにおけるキー */
	static public final String	KEY_NAME	= "name";
	/** 交換代数基底単位キーの JSON オブジェクトにおけるキー */
	static public final String	KEY_UNIT	= "unit";
	/** 交換代数基底時間キーの JSON オブジェクトにおけるキー */
	static public final String	KEY_TIME	= "time";
	/** 交換代数基底主体キーの JSON オブジェクトにおけるキー */
	static public final String	KEY_SUBJECT	= "subject";

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
	 * @return	{@code "!&ExBase"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	データ代数基底(exalge2.ExBase)のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<ExBase> getTargetClass() {
		return ExBase.class;
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
			// ExBase
			ExBase base = (ExBase)target;
			//--- begin
			writer.beginObject();
			//--- hat
			writer.writeName(KEY_HAT);
			writer.writeValue(base.isHat());
			//--- name
			writer.writeName(KEY_NAME);
			writer.writeValue(base.getNameKey());
			//--- unit
			if (!base.isExtendedKeyOmitted(ExtendedKeyID.UNIT)) {
				writer.writeName(KEY_UNIT);
				writer.writeValue(base.getUnitKey());
			}
			//--- time
			if (!base.isExtendedKeyOmitted(ExtendedKeyID.TIME)) {
				writer.writeName(KEY_TIME);
				writer.writeValue(base.getTimeKey());
			}
			//--- subject
			if (!base.isExtendedKeyOmitted(ExtendedKeyID.SUBJECT)) {
				writer.writeName(KEY_SUBJECT);
				writer.writeValue(base.getSubjectKey());
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
		String hat = ExBase.NO_HAT;
		String name = null;
		String unit = null;
		String time = null;
		String subject = null;
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			if (KEY_HAT.equalsIgnoreCase(key)) {
				Boolean val = reader.nextBooleanValue();
				hat = (val != null && val.booleanValue() ? ExBase.HAT : ExBase.NO_HAT);
			}
			else if (KEY_NAME.equalsIgnoreCase(key)) {
				name = reader.nextStringValue();
			}
			else if (KEY_UNIT.equalsIgnoreCase(key)) {
				unit = reader.nextStringValue();
			}
			else if (KEY_TIME.equalsIgnoreCase(key)) {
				time = reader.nextStringValue();
			}
			else if (KEY_SUBJECT.equalsIgnoreCase(key)) {
				subject = reader.nextStringValue();
			}
			else {
				// drop value
				deserializeAndDropValue(reader, null);
			}

			// next
			etype = reader.nextToken();
		}
		if (Strings.isNullOrEmpty(name)) {
			throw reader.createUnspecifiedValueError(KEY_NAME);
		}
		
		// Object end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedObjectError();
		}
		
		// make Instance
		return new ExBase(name, hat, unit, time, subject);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
