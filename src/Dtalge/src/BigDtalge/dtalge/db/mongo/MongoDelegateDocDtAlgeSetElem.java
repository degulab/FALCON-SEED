/*
 * @(#)MongoDelegateDocDtalgeSetElem.java	0.5.0	2019/02/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Sorts;

import dtalge.DtBase;

/**
 * MongoDB のドキュメントの、データ代数集合の要素としてのデリゲートクラス。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDelegateDocDtAlgeSetElem extends MongoDelegateDocDtalgeElem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** データ代数集合の所属要素の ID を示す、ドキュメントのキー **/
	static public final String MONGO_KEY_SETID		= "elem_id";
	static public final String MONGO_KEY_$SETID		= "$elem_id";

	/** データ代数元 ID とすべての基底キーと値の順で昇順ソートするソートドキュメント **/
	static public final Bson   MONGO_SORT_DTALGESET_ELEM_ALL = Sorts.ascending(
			MONGO_KEY_SETID,
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_NAME,
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_TYPE,
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_ATTR,
			MONGO_KEY_BASE + "." + MongoDelegateDocDtBase.MONGO_KEY_SUBJECT,
			MONGO_KEY_VALUE);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたドキュメントを保持する、新しいインスタンスを生成する。
	 * @param doc	MongoDB ドキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	ドキュメントの基底オブジェクトが存在しない場合
	 * @throws IllegalArgumentException	ドキュメントの所属データ代数元 ID が有効な文字列ではない場合
	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
	 */
	protected MongoDelegateDocDtAlgeSetElem(Document doc) {
		super(doc);
		String strSetID = _mongoDocument.getString(MONGO_KEY_SETID);
		if (strSetID == null || strSetID.isEmpty()) {
			throw new IllegalArgumentException("'" + MONGO_KEY_SETID + "' value is invalid ID : " + String.valueOf(strSetID));
		}
	}
	
	/**
	 * 指定されたデータ代数要素をドキュメントとして保持する、新しいインスタンスを生成する。
	 * @param setid 所属するデータ代数元の ID
	 * @param base	ソースとするデータ代数要素の基底
	 * @param value	ソースとするデータ代数要素の値
	 * @throws NullPointerException	<em>setid</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>setid</em> が空文字の場合
	 */
	protected MongoDelegateDocDtAlgeSetElem(String setid, DtBase base, Object value) {
		super(base, value);
		if (setid.isEmpty())
			throw new IllegalArgumentException("'setid' must not empty!");
		_mongoDocument.append(MONGO_KEY_SETID, setid);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * データ代数集合の要素から、MongoDB ドキュメントを生成する。
	 * @param setid ソースとする所属データ代数元の ID
	 * @param base	ソースとするデータ代数基底
	 * @param value	ソースとする値
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>setid</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>setid</em> が空文字の場合
	 */
	static public Document makeDtAlgeSetElemDocument(String setid, DtBase base, Object value) {
		return appendDtAlgeSetElemToDocument(new Document(), setid, base, value);
	}

	/**
	 * 指定されたデータ代数集合の要素を、指定されたドキュメントの要素として追加する。
	 * @param dest	対象のドキュメント
	 * @param setid ソースとする所属データ代数元の ID
	 * @param base	ソースとするデータ代数基底
	 * @param value	ソースとする値
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> もしくは <em>setid</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>setid</em> が空文字の場合
	 */
	static public Document appendDtAlgeSetElemToDocument(Document dest, String setid, DtBase base, Object value) {
		MongoDelegateDocDtalgeElem.appendDtalgeElemToDocument(dest, base, value);
		if (setid.isEmpty())
			throw new IllegalArgumentException("'setid' must not empty!");
		dest.append(MONGO_KEY_SETID, setid);
		return dest;
	}

	/**
	 * 指定されたデータ代数集合の要素を保持するドキュメントから、基底とデータ代数元 ID のみを持つフィルタードキュメントを生成する。
	 * @param docDtAlgeSetElem	データ代数集合の要素を保持するドキュメント
	 * @return	生成されたフィルタードキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Document makeFilterBySetIDandDtBase(Document docDtAlgeSetElem) {
		Document doc = MongoDelegateDocDtalgeElem.makeFilterByDtBase(docDtAlgeSetElem);
		doc.append(MONGO_KEY_SETID, docDtAlgeSetElem.getString(MONGO_KEY_SETID));
		return doc;
	}

	/**
	 * 指定されたデータ代数集合の要素を保持するドキュメントから、データ代数元 ID を取得する。
	 * @param docDtAlgeSetElem	対象のドキュメントオブジェクト
	 * @return	データ代数元 ID、存在しない場合は <tt>null</tt>
	 */
	static public String getDtAlgeSetIDfromDocument(Document docDtAlgeSetElem) {
		return docDtAlgeSetElem.getString(MONGO_KEY_SETID);
	}
	
	/**
	 * このオブジェクトの所属データ代数元 ID を取得する。
	 * @return	データ代数元 ID を表す文字列
	 */
	protected String getDtAlgeSetID() {
		return _mongoDocument.getString(MONGO_KEY_SETID);
	}

	/**
	 * このオブジェクトに、新しい所属データ代数元 ID を設定する。
	 * @param newSetID	新しいデータ代数元 ID を表す文字列
	 * @throws NullPointerException	<em>newSetID</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>newSetID</em> が空文字の場合
	 */
	protected void setDtAlgeSetID(String newSetID) {
		if (newSetID.isEmpty()) {
			throw new IllegalArgumentException("'setid' must not empty!");
		}
		_mongoDocument.put(MONGO_KEY_SETID, newSetID);
	}
	
	/**
	 * このオブジェクトが保持する基底オブジェクトのみを含む、フィルターとして利用可能なドキュメントオブジェクトを取得する。
	 * @return	フィルタードキュメント
	 */
	protected Document getFilterBySetIDandDtBase() {
		return makeFilterBySetIDandDtBase(_mongoDocument);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
