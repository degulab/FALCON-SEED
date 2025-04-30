/*
 * @(#)MongoDelegateDocExAlgeSetElem.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoDelegateDocExAlgeSetElem.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db.mongo;

import java.math.BigDecimal;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Sorts;

import exalge2.ExBase;

/**
 * MongoDB のドキュメントの、交換代数集合の要素としてのデリゲートクラス。
 * 
 * @version 0.991
 * @since 0.990
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDelegateDocExAlgeSetElem extends MongoDelegateDocExalgeElem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 交換代数集合の所属要素の ID を示す、ドキュメントのキー **/
	static public final String MONGO_KEY_SETID		= "elem_id";

	/** 交換代数元 ID とすべての基底キーと値の順で昇順ソートするソートドキュメント(HATキーが基底の最後) **/
	static public final Bson   MONGO_SORT_EXALGESET_ELEM_ALL = Sorts.ascending(
			MONGO_KEY_SETID,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_NAME,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_UNIT,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_TIME,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_SUBJECT,
			MONGO_KEY_BASE + "." + MongoDelegateDocExBase.MONGO_KEY_HAT,
			MONGO_KEY_VALUE);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
//	/**
//	 * 指定されたドキュメントを保持する、新しいインスタンスを生成する。
//	 * @param doc	MongoDB ドキュメント
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 * @throws IllegalArgumentException	ドキュメントの基底オブジェクトが存在しない場合
//	 * @throws IllegalArgumentException	ドキュメントの所属交換代数元 ID が有効な文字列ではない場合
//	 * @throws ClassCastException	ドキュメントの基底オブジェクトが JSON オブジェクトではない場合
//	 */
//	protected MongoDelegateDocExAlgeSetElem(Document doc) {
//		super(doc);
//		String strSetID = _mongoDocument.getString(MONGO_KEY_SETID);
//		if (strSetID == null || strSetID.isEmpty()) {
//			throw new IllegalArgumentException("'" + MONGO_KEY_SETID + "' value is invalid ID : " + String.valueOf(strSetID));
//		}
//	}
//	
//	/**
//	 * 指定された交換代数要素をドキュメントとして保持する、新しいインスタンスを生成する。
//	 * @param setid 所属する交換代数元の ID
//	 * @param base	ソースとする交換代数要素の基底
//	 * @param value	ソースとする交換代数要素の値
//	 * @throws NullPointerException	<em>setid</em> もしくは <em>base</em> が <tt>null</tt> の場合
//	 * @throws IllegalArgumentException	<em>setid</em> が空文字の場合
//	 */
//	protected MongoDelegateDocExAlgeSetElem(String setid, ExBase base, BigDecimal value) {
//		super(base, value);
//		if (setid.isEmpty())
//			throw new IllegalArgumentException("'setid' must not empty!");
//		_mongoDocument.append(MONGO_KEY_SETID, setid);
//	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 交換代数集合の要素から、MongoDB ドキュメントを生成する。
	 * @param setid ソースとする所属交換代数元の ID
	 * @param base	ソースとする交換代数基底
	 * @param value	ソースとする値
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>setid</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>setid</em> が空文字の場合
	 */
	static public Document makeExAlgeSetElemDocument(String setid, ExBase base, BigDecimal value) {
		return appendExAlgeSetElemToDocument(new Document(), setid, base, value);
	}

	/**
	 * 指定された交換代数集合の要素を、指定されたドキュメントの要素として追加する。
	 * @param dest	対象のドキュメント
	 * @param setid ソースとする所属交換代数元の ID
	 * @param base	ソースとする交換代数基底
	 * @param value	ソースとする値
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> もしくは <em>setid</em> もしくは <em>base</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>setid</em> が空文字の場合
	 */
	static public Document appendExAlgeSetElemToDocument(Document dest, String setid, ExBase base, BigDecimal value) {
		MongoDelegateDocExalgeElem.appendExalgeElemToDocument(dest, base, value);
		if (setid.isEmpty())
			throw new IllegalArgumentException("'setid' must not empty!");
		dest.append(MONGO_KEY_SETID, setid);
		return dest;
	}

	/**
	 * 指定された交換代数集合の要素を保持するドキュメントから、基底と交換代数元 ID のみを持つフィルタードキュメントを生成する。
	 * @param docExAlgeSetElem	交換代数集合の要素を保持するドキュメント
	 * @return	生成されたフィルタードキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Document makeFilterBySetIDandExBase(Document docExAlgeSetElem) {
		Document doc = MongoDelegateDocExalgeElem.makeFilterByExBase(docExAlgeSetElem);
		doc.append(MONGO_KEY_SETID, docExAlgeSetElem.getString(MONGO_KEY_SETID));
		return doc;
	}

	/**
	 * 指定された交換代数集合の要素を保持するドキュメントから、交換代数元 ID を取得する。
	 * @param docExAlgeSetElem	対象のドキュメントオブジェクト
	 * @return	交換代数元 ID、存在しない場合は <tt>null</tt>
	 */
	static public String getExAlgeSetIDfromDocument(Document docExAlgeSetElem) {
		return docExAlgeSetElem.getString(MONGO_KEY_SETID);
	}
	
	/**
//	 * このオブジェクトの所属交換代数元 ID を取得する。
//	 * @return	交換代数元 ID を表す文字列
//	 */
//	protected String getExAlgeSetID() {
//		return _mongoDocument.getString(MONGO_KEY_SETID);
//	}
//
//	/**
//	 * このオブジェクトに、新しい所属交換代数元 ID を設定する。
//	 * @param newSetID	新しい交換代数元 ID を表す文字列
//	 * @throws NullPointerException	<em>newSetID</em> が <tt>null</tt> の場合
//	 * @throws IllegalArgumentException	<em>newSetID</em> が空文字の場合
//	 */
//	protected void setExAlgeSetID(String newSetID) {
//		if (newSetID.isEmpty()) {
//			throw new IllegalArgumentException("'setid' must not empty!");
//		}
//		_mongoDocument.put(MONGO_KEY_SETID, newSetID);
//	}
//	
//	/**
//	 * このオブジェクトが保持する基底オブジェクトのみを含む、フィルターとして利用可能なドキュメントオブジェクトを取得する。
//	 * @return	フィルタードキュメント
//	 */
//	protected Document getFilterBySetIDandExBase() {
//		return makeFilterBySetIDandExBase(_mongoDocument);
//	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
