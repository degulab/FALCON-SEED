/*
 * @(#)MongoDelegateDocExBase.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoDelegateDocExBase.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db.mongo;

import java.util.Objects;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Sorts;

import exalge2.ExBase;

/**
 * MongoDB のドキュメントの、交換代数基底としてのデリゲートクラス。
 * 
 * @version 0.991
 * @since 0.990
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDelegateDocExBase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 交換代数基底のハットキーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_HAT		= "hat";
	/** 交換代数基底の名前キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_NAME		= "name";
	/** 交換代数基底の単位キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_UNIT		= "unit";
	/** 交換代数基底の時間キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_TIME		= "time";
	/** 交換代数基底の主体キーを示す、ドキュメントのキー **/
	static public final String MONGO_KEY_SUBJECT	= "subject";

	/** 基底キーのすべてのキーで昇順ソートするソートドキュメント(ハットキーは最後) **/
	static public final Bson   MONGO_SORT_EXBASE_ALL = Sorts.ascending(MONGO_KEY_NAME, MONGO_KEY_UNIT, MONGO_KEY_TIME, MONGO_KEY_SUBJECT, MONGO_KEY_HAT);

	/** 基底キーのインデックスに対応したドキュメントキーの配列 **/
	static public final String[] MOGNO_KEYS_ARRAY = {
			MONGO_KEY_NAME,
			MONGO_KEY_HAT,
			MONGO_KEY_UNIT,
			MONGO_KEY_TIME,
			MONGO_KEY_SUBJECT,
	};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
//	/** MongoDB のドキュメントオブジェクト **/
//	private Document	_mongoDocument;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
//	/**
//	 * 指定されたドキュメントを保持する、新しいインスタンスを生成する。
//	 * @param doc	MongoDB ドキュメント
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 */
//	protected MongoDelegateDocExBase(Document doc) {
//		if (doc == null)
//			throw new NullPointerException("Document object is null.");
//		_mongoDocument = doc;
//	}
//	
//	/**
//	 * 指定された交換代数基底をドキュメントとして保持する、新しいインスタンスを生成する。
//	 * @param base	ソースとする交換代数基底
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 */
//	protected MongoDelegateDocExBase(ExBase base) {
//		_mongoDocument = makeExBaseDocument(base);
//	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 基底キー文字列を、ドキュメントに格納する形式に正規化する。
	 * @param baseKey	基底キー
	 * @return	正規化後の文字列
	 */
	static public String normalizeOmmitKey(String baseKey) {
		if (baseKey == null || baseKey.isEmpty()) {
			return "";
		}
		else if (baseKey.equals(ExBase.OMITTED)) {
			return "";
		}
		else {
			return baseKey;
		}
	}
	
	/**
	 * 交換代数基底から、MongoDB ドキュメントを生成する。
	 * @param base	ソースとする交換代数基底
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Document makeExBaseDocument(ExBase base) {
		return appendExBaseToDocument(new Document(), base);
	}

	/**
	 * 指定された交換代数基底を、指定されたドキュメントの要素として追加する。
	 * @param dest	対象のドキュメント
	 * @param base	ソースとする交換代数基底
	 * @return	指定されたドキュメント
	 */
	static public Document appendExBaseToDocument(Document dest, ExBase base) {
		dest.append(MONGO_KEY_HAT, base.isHat());
		dest.append(MONGO_KEY_NAME, normalizeOmmitKey(base.getNameKey()));
		dest.append(MONGO_KEY_UNIT, normalizeOmmitKey(base.getUnitKey()));
		dest.append(MONGO_KEY_TIME, normalizeOmmitKey(base.getTimeKey()));
		dest.append(MONGO_KEY_SUBJECT, normalizeOmmitKey(base.getSubjectKey()));
		return dest;
	}

	/**
	 * 指定された交換代数基底ドキュメントから、指定されたインデックスに相当する基底キー文字列を取得する。
	 * ハットキーも文字列として取得する。
	 * @param docExBase	交換代数基底を表すドキュメントオブジェクト
	 * @param keyIndex	交換代数基底キーのインデックス
	 * @return	取得した文字列、存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>keyIndex</em> が範囲外の場合
	 */
	static public String getExBaseKeyStringByIndex(Document docExBase, int keyIndex) {
		String mongoKey = MOGNO_KEYS_ARRAY[keyIndex];
		if (mongoKey == MONGO_KEY_HAT) {
			// hat key
			Boolean hat = docExBase.getBoolean(MONGO_KEY_HAT);
			return (hat != null && hat.booleanValue() ? ExBase.HAT : ExBase.NO_HAT);
		}
		else {
			// other key
			return docExBase.getString(mongoKey);
		}
	}

	/**
	 * 指定されたドキュメントの交換代数基底のすべてのキーが等しいかを判定する。
	 * @param docExBase1	判定するドキュメントの一方
	 * @param docExBase2	判定するドキュメントのもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean equalsExBaseElemDocument(Document docExBase1, Document docExBase2) {
		if (docExBase1 == docExBase2)
			return true;
		if (docExBase1 == null || docExBase2 == null)
			return false;
		
		//--- equals name key
		Object obj1 = docExBase1.get(MONGO_KEY_NAME);
		Object obj2 = docExBase2.get(MONGO_KEY_NAME);
		if (!Objects.equals(obj1, obj2))
			return false;
		//--- equals hat key
		obj1 = docExBase1.get(MONGO_KEY_HAT);
		obj2 = docExBase2.get(MONGO_KEY_HAT);
		if (!Objects.equals(obj1, obj2))
			return false;
		//--- equals unit key
		obj1 = docExBase1.get(MONGO_KEY_UNIT);
		obj2 = docExBase2.get(MONGO_KEY_UNIT);
		if (!Objects.equals(obj1, obj2))
			return false;
		//--- equals time key
		obj1 = docExBase1.get(MONGO_KEY_TIME);
		obj2 = docExBase2.get(MONGO_KEY_TIME);
		if (!Objects.equals(obj1, obj2))
			return false;
		//--- equals subject key
		obj1 = docExBase1.get(MONGO_KEY_SUBJECT);
		obj2 = docExBase2.get(MONGO_KEY_SUBJECT);
		return Objects.equals(obj1, obj2);
	}
	
//	/**
//	 * このオブジェクトが保持するドキュメントオブジェクトを取得する。
//	 * @return	ドキュメントオブジェクト
//	 */
//	protected Document getDocument() {
//		return _mongoDocument;
//	}
//	
//	/**
//	 * このオブジェクトにハットキーが記述されている場合に <tt>true</tt> を返す。
//	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
//	 */
//	protected boolean isSpecifiedHatKey() {
//		return _mongoDocument.containsKey(MONGO_KEY_HAT);
//	}
//	
//	/**
//	 * このオブジェクトに名前キーが記述されている場合に <tt>true</tt> を返す。
//	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
//	 */
//	protected boolean isSpecifiedNameKey() {
//		return _mongoDocument.containsKey(MONGO_KEY_NAME);
//	}
//	
//	/**
//	 * このオブジェクトに単位キーが記述されている場合に <tt>true</tt> を返す。
//	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
//	 */
//	protected boolean isSpecifiedUnitKey() {
//		return _mongoDocument.containsKey(MONGO_KEY_UNIT);
//	}
//	
//	/**
//	 * このオブジェクトに時間キーが記述されている場合に <tt>true</tt> を返す。
//	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
//	 */
//	protected boolean isSpecifiedTimeKey() {
//		return _mongoDocument.containsKey(MONGO_KEY_TIME);
//	}
//	
//	/**
//	 * このオブジェクトに主体キーが記述されている場合に <tt>true</tt> を返す。
//	 * <p>このメソッドは、キーの有無のみを判定するもので、値の有無は関知しない。
//	 */
//	protected boolean isSpecifiedSubjectKey() {
//		return _mongoDocument.containsKey(MONGO_KEY_SUBJECT);
//	}
//
//	/**
//	 * 基底が <b>hat基底</b> であることを示す。
//	 * 
//	 * @return 基底が <b>hat基底</b> であれば、true を返す
//	 * @throws ClassCastException	データ型が正しくない場合
//	 */
//	protected boolean isHat() {
//		Boolean bhat = _mongoDocument.getBoolean(MONGO_KEY_HAT);
//		return (bhat != null && bhat.booleanValue());
//	}
//
//	/**
//	 * 基底が <b>hatなし基底</b> であることを示す。
//	 * 
//	 * @return 基底が <b>hatなし基底</b> であれば、true を返す。
//	 * @throws ClassCastException	データ型が正しくない場合
//	 */
//	protected boolean isNoHat() {
//		return !isHat();
//	}
//
//	/**
//	 * 基底の名前キーを返す。
//	 * 
//	 * @return 名前キー
//	 */
//	protected String getNameKey() {
//		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_NAME));
//	}
//
//	/**
//	 * 基底のハットキーを返す。
//	 * 
//	 * @return ハットキー
//	 */
//	protected String getHatKey() {
//		return (isHat() ? ExBase.HAT : ExBase.NO_HAT);
//	}
//
//	/**
//	 * 基底の単位キーを返す。
//	 * 
//	 * @return 単位キー
//	 */
//	protected String getUnitKey() {
//		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_UNIT));
//	}
//
//	/**
//	 * 基底の時間キーを返す。
//	 * 
//	 * @return 時間キー
//	 */
//	protected String getTimeKey() {
//		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_TIME));
//	}
//
//	/**
//	 * 基底のサブジェクトキーを返す。
//	 * 
//	 * @return サブジェクトキー
//	 */
//	protected String getSubjectKey() {
//		return normalizeOmmitKey(_mongoDocument.getString(MONGO_KEY_SUBJECT));
//	}

	/**
	 * 指定されたドキュメントから、交換代数基底を生成する。
	 * @param docExBase	交換代数基底の要素を持つドキュメント
	 * @return	生成された交換代数基底
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws	IllegalArgumentException	名前キーが <tt>null</tt> もしくは空文字列の場合
	 * @throws ClassCastException	ドキュメントの要素キーに対応するデータ型が正しくない場合
	 */
	static public ExBase toExBase(Document docExBase) {
		//--- hat key
		Boolean bhat = docExBase.getBoolean(MONGO_KEY_HAT);
		if (bhat == null) {
			bhat = Boolean.FALSE;
		}
		//--- name key
		String strName = docExBase.getString(MONGO_KEY_NAME);
		//--- unit key
		String strUnit = docExBase.getString(MONGO_KEY_UNIT);
		//--- time key
		String strTime = docExBase.getString(MONGO_KEY_TIME);
		//--- subject key
		String strSubject = docExBase.getString(MONGO_KEY_SUBJECT);
		
		return new ExBase(strName, (bhat.booleanValue() ? ExBase.HAT : ExBase.NO_HAT), strUnit, strTime, strSubject);
	}

//	/**
//	 * このオブジェクトが保持するパラメータから、新しい交換代数基底を生成する。
//	 * @return	新しい交換代数基底のインスタンス
//	 * @throws ClassCastException	データ型が正しくない場合
//	 * @throws IllegalStateException	パラメータが交換代数基底として正しくない場合
//	 */
//	protected ExBase toExBase() {
//		//--- hat key
//		Boolean bhat = _mongoDocument.getBoolean(MONGO_KEY_HAT);
//		if (bhat == null) {
//			bhat = Boolean.FALSE;
//		}
//		//--- name key
//		String strName = _mongoDocument.getString(MONGO_KEY_NAME);
//		//--- unit key
//		String strUnit = _mongoDocument.getString(MONGO_KEY_UNIT);
//		//--- time key
//		String strTime = _mongoDocument.getString(MONGO_KEY_TIME);
//		//--- subject key
//		String strSubject = _mongoDocument.getString(MONGO_KEY_SUBJECT);
//		
//		return new ExBase(strName, (bhat.booleanValue() ? ExBase.HAT : ExBase.NO_HAT), strUnit, strTime, strSubject);
//	}

	/*
	 * このオブジェクトのハッシュ値を返す。
	 *
	@Override
	public int hashCode() {
		return _mongoDocument.hashCode();
	}
	*/

	/*
	 * 指定されたオブジェクトが、このオブジェクトと等しいかどうかを判定する。
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 *
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}
		
		MongoDelegateDocExBase that = (MongoDelegateDocExBase)obj;
		return this._mongoDocument.equals(that._mongoDocument);
	}
	*/

	/*
	@Override
	public String toString() {
		return _mongoDocument.toString();
	}
	*/

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
