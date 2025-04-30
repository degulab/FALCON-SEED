/*
 * @(#)DtJsonSerializer.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.serialize;

import java.io.IOException;

import dtalge.json.io.DtJsonInputReader;
import dtalge.json.io.DtJsonOutputWriter;
import net.arnx.jsonic.JSONEventType;

/**
 * JSON 形式でのシリアライズ／デシリアライズを行うためのインターフェース。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public interface DtJsonSerializer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** オブジェクトのタイプ名のプレフィックス、{@code "!&"} の2文字 **/
	static public final String	OBJTYPENAME_PREFIX_STR	= "!&";

	/** JSON における {@code null} を表す文字列。 */
	static public final String	JSON_NULL = "null";
	
	/** {@code true} を示す値 */
	static public final boolean OMIT_DATATYPE_NAME = true;
	/** {@code false} を示す値 */
	static public final boolean OUTPUT_DATATYPE_NAME = false;

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * シリアライズ対象オブジェクトのオブジェクト型名を取得する。
	 * @return	{@link DtJsonSerializer#OBJTYPENAME_PREFIX_STR} で始まる、シリアライズ対象のオブジェクト型名
	 */
	public String getTypeName();
	
	/**
	 * シリアライズ対象オブジェクトのクラスを取得する。
	 * @return	シリアライズ対象オブジェクトのクラス
	 */
	public Class<?> getTargetClass();
	
	/**
	 * <em>target</em> の内容を、<em>writer</em> に JSON フォーマットで出力する。 
	 * @param writer	出力先のライター
	 * @param target	出力対象のオブジェクト
	 * @param omitTypeName	シリアライズ時にオブジェクト型名を付加して出力するには {@code true}、付加せずに出力するには {@code false} を指定する。
	 * @throws NullPointerException	<em>writer</em> が <code>null</code> の場合
	 * @throws ClassCastException	<em>target</em> が <code>null</code> 以外であり、処理可能なクラスに割り当てられない場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void serialize(DtJsonOutputWriter writer, Object target, boolean omitTypeName) throws IOException;
	
	/**
	 * <em>reader</em> の現在の読み込み位置から JSON フォーマットで読み込み、デシリアライズしたオブジェクトを返す。
	 * このメソッドでは基本的に、オブジェクト型名が付加されていない JSON 標準形のデシリアライズを行う。
	 * @param reader		読み込み対象のリーダー
	 * @param beginType		直前に読み込まれたトークンタイプ、先頭のトークンを新たに読み込む場合は <code>null</code>
	 * @return	デシリアライズされたオブジェクト、もしくは <code>null</code>
	 * @throws NullPointerException <em>reader</em> が <code>null</code> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public Object deserialize(DtJsonInputReader reader, JSONEventType beginType) throws IOException;
	
	/**
	 * データ型名を先頭に持つオブジェクトの終端までスキップする。
	 * このメソッドの呼び出しは、オブジェクト型名が記述された JSON オブジェクトの先頭キーバリューより後のトークンを読み出し、整合性を保持しつつ内容を読み飛ばす。
	 * @param reader	リーダー
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void ensureEndTypeNamedObject(DtJsonInputReader reader) throws IOException;
}
