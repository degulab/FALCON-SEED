/*
 * @(#)DtJsonDtBaseSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;

import dtalge.DtBase;
import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import exalge2.util.Strings;
import net.arnx.jsonic.JSONEventType;

/**
 * データ代数基底(dtalge.DtBase)の JSON 標準形でのシリアライズ／デシリアライズを行うクラス。<br>
 * JSON 形式は、次の通り。
 * <pre><code>
 * {
 *   "name": <i><b>&lt;string&gt;</b></i>,
 *   "type": <i><b>&lt;string&gt;</b></i>,
 *   "attr": <i><b>&lt;string&gt;</b></i>,
 *   "subject": <i><b>&lt;string&gt;</b></i>
 * }
 * </code></pre>
 * <dl>
 * 	<dt>name (string)
 * 	</dt><dd>データ代数基底の名前（name）キー。省略や空文字は許可されない。
 * 	</dd>
 * 	<dt>type (string)
 * 	</dt><dd>データ代数基底のデータ型（type）キー。この値は、“string”（文字列）、“decimal”（実数値）、“boolean”（真偽値）のいずれかのみ、省略や空文字は許可されない。
 * 	</dd>
 * 	<dt>attr (string, 省略可)
 * 	</dt><dd>データ代数基底の属性（attribute）キー。
 * 	</dd>
 * 	<dt>subject (string, 省略可)
 * 	</dt><dd>データ代数基底の主体（subject）キー。
 * 	</dd>
 * </dl>
 * なお、データ代数基底の各基底キーには、以下の文字は使用できない。
 * <blockquote>
 * {@code < > - , ^ " % & ? | @ ' " (空白)}
 * </blockquote>
 * 
 * <p>データ代数基底(dtalge.DtBase)のオブジェクト型名は、
 * <blockquote>
 * {@code !&DtBase}
 * </blockquote>
 * であり、オブジェクト型名が付加された場合の JSON 形式は、次の通り。
 * <pre><code>
 * { <b>"!&amp;DtBase"</b>:
 *   {
 *     "name": <i><b>&lt;string&gt;</b></i>,
 *     "type": <i><b>&lt;string&gt;</b></i>,
 *     "attr": <i><b>&lt;string&gt;</b></i>,
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
public class DtJsonDtBaseSerializer extends AbDtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データ代数基底(dtalge.DtBase)の JSON 標準形であることを示すタイプ名。 */
	static public final String	TYPENAME	= DtJsonSerializer.OBJTYPENAME_PREFIX_STR + DtBase.class.getSimpleName();
	
	/** データ代数基底名前キーの JSON オブジェクトにおけるキー */
	static public final String	KEY_NAME	= "name";
	/** データ代数基底データ型キーの JSON オブジェクトにおけるキー */
	static public final String	KEY_TYPE	= "type";
	/** データ代数基底属性キーの JSON オブジェクトにおけるキー */
	static public final String	KEY_ATTR	= "attr";
	/** データ代数基底主体キーの JSON オブジェクトにおけるキー */
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
	 * @return	{@code "!&DtBase"} を返す。
	 */
	public String getTypeName() {
		return TYPENAME;
	}
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	データ代数基底(dtalge.DtBase)のクラスオブジェクト({@link java.lang.Class}を返す。
	 */
	public Class<DtBase> getTargetClass() {
		return DtBase.class;
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
			// DtBase
			DtBase base = (DtBase)target;
			//--- begin
			writer.beginObject();
			//--- name
			writer.writeName(KEY_NAME);
			writer.writeValue(base.getNameKey());
			//--- type
			writer.writeName(KEY_TYPE);
			writer.writeValue(base.getTypeKey());
			//--- attr
			String val = base.getAttributeKey();
			if (!isDtBaseKeyOmitted(val)) {
				writer.writeName(KEY_ATTR);
				writer.writeValue(val);
			}
			//--- subject
			val = base.getSubjectKey();
			if (!isDtBaseKeyOmitted(val)) {
				writer.writeName(KEY_SUBJECT);
				writer.writeValue(val);
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
		String name = null;
		String type = null;
		String attr = null;
		String subject = null;
		etype = reader.nextToken();
		while (etype != null && etype != JSONEventType.END_OBJECT) {
			// key
			if (etype != JSONEventType.NAME) {
				throw reader.createUnexpectedTypeError(etype, JSONEventType.NAME);
			}
			String key = reader.getStringValue();
			
			// value
			if (KEY_NAME.equalsIgnoreCase(key)) {
				name = reader.nextStringValue();
			}
			else if (KEY_TYPE.equalsIgnoreCase(key)) {
				type = reader.nextStringValue();
			}
			else if (KEY_ATTR.equalsIgnoreCase(key)) {
				attr = reader.nextStringValue();
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
		if (Strings.isNullOrEmpty(type)) {
			throw reader.createUnspecifiedValueError(KEY_TYPE);
		}
		
		// Object end
		if (etype != JSONEventType.END_OBJECT) {
			throw reader.createUnclosedObjectError();
		}
		
		// make Instance
		return DtBase.newBase(name, type, attr, subject);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isDtBaseKeyOmitted(String basekey) {
		return (basekey == null || basekey.isEmpty() || basekey.equals(DtBase.OMITTED));
	}


	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
