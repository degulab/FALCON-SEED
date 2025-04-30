/*
 * @(#)MongoDelegateDocDtStringThesaurusElem.java	0.5.0	2019/02/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Sorts;

/**
 * MongoDB のドキュメントの、シソーラスの要素としてのデリゲートクラス。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDelegateDocDtStringThesaurusElem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 無名のシソーラス定義を表す、シソーラス名(シソーラス定義 ID) **/
	static public final String NoNameThesKey = "";
	
	/**
	 * シソーラス要素の親子関係のうち、親の語句を示すドキュメントのキー
	 */
	static public final String MONGO_KEY_PARENT		= "parent";
	static public final String MONGO_KEY_$PARENT	= "$parent";
	/**
	 * シソーラス要素の親子関係のうち、子の語句を示すドキュメントのキー
	 *  <p>同値判定の際にソートによって他オブジェクトと比較するため、要素でソートしやすい形式とする。
	 */
	static public final String MONGO_KEY_CHILD		= "child";
	static public final String MONGO_KEY_$CHILD		= "$child";
	/** シソーラス要素の所属名を示す、ドキュメントのキー **/
	static public final String MONGO_KEY_NAME		= "name";
	static public final String MONGO_KEY_$NAME		= "$name";
	/** シソーラス要素のアグリゲーションで深度を示す、ドキュメントのキー **/
	static public final String MONGO_AGGRE_KEY_DEPTH	= "depth";
	/** シソーラス要素のアグリゲーションで graphLookup の出力を示す、ドキュメントのキー **/
	static public final String MONGO_AGGRE_KEY_GRAPH	= "nodelist";
	static public final String MONGO_AGGRE_KEY_$GRAPH	= "$nodelist";
	
	/** すべての親子の語句のみで辞書順ソートするソートドキュメント **/
	static public final Bson MONGO_SORT_STRINGTHES_ELEM_ALL = Sorts.ascending(MONGO_KEY_PARENT, MONGO_KEY_CHILD);
	/** すべてのシソーラス定義名、親語句、子語句の順に辞書順ソートするソートドキュメント **/
	static public final Bson MONGO_SORT_NAMEDSTRINGTHES_ELEM_ALL = Sorts.ascending(MONGO_KEY_NAME, MONGO_KEY_PARENT, MONGO_KEY_CHILD);

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
	 * 指定されたシソーラス名を正規化する。
	 * このメソッドでは、無名を表すシソーラス名を空文字とする。
	 * @param name	対象のシソーラス名
	 * @return	正規化されたシソーラス名
	 */
	static public String normalizeThesName(String name) {
		return (name!=null && !name.isEmpty() ? name : NoNameThesKey);
	}

	/**
	 * シソーラス定義の単一ペアを表すドキュメントを生成する。
	 * @param parent	ペアの親の語句
	 * @param child		ペアの子の語句
	 * @return	生成されたドキュメント
	 */
	static public Document makeThesaurusElemDocument(String parent, String child) {
		return appendThesaurusElemToDocument(new Document(), parent, child);
	}

	/**
	 * 名前付きシソーラス定義の単一ペアを表すドキュメントを生成する。
	 * @param name	シソーラス名
	 * @param parent	ペアの親の語句
	 * @param child		ペアの子の語句
	 * @return	生成されたドキュメント
	 */
	static public Document makeNamedThesaurusElemDocument(String name, String parent, String child) {
		return appendNamedThesaurusElemToDocument(new Document(), name, parent, child);
	}
	
	/**
	 * 指定されたシソーラス定義ペアを、シソーラス定義の要素として、指定されたドキュメントに追加する。
	 * @param dest	対象のドキュメント
	 * @param parent	ペアの親の語句
	 * @param child		ペアの子の語句
	 * @return	生成されたドキュメント
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> が <tt>null</tt> の場合
	 */
	static public Document appendThesaurusElemToDocument(Document dest, String parent, String child) {
		dest.append(MONGO_KEY_PARENT, parent);
		dest.append(MONGO_KEY_CHILD, child);
		return dest;
	}
	
	/**
	 * 指定された名前付きシソーラス定義ペアを、名前付きシソーラス定義の要素として、指定されたドキュメントに追加する。
	 * @param dest	対象のドキュメント
	 * @param name	シソーラス名
	 * @param parent	ペアの親の語句
	 * @param child		ペアの子の語句
	 * @return	指定されたドキュメント
	 * @throws NullPointerException	<em>dest</em> が <tt>null</tt> の場合
	 */
	static public Document appendNamedThesaurusElemToDocument(Document dest, String name, String parent, String child) {
		dest.append(MONGO_KEY_NAME, normalizeThesName(name));
		dest.append(MONGO_KEY_PARENT, parent);
		dest.append(MONGO_KEY_CHILD, child);
		return dest;
	}

	/**
	 * 指定されたドキュメントから、シソーラス定義名を取得する。
	 * @param docThesElem	対象のドキュメントオブジェクト
	 * @return	ドキュメントに含まれるシソーラス定義名、存在しない場合は <tt>null</tt>
	 */
	static public String getThesaurusNameFromDocument(Document docThesElem) {
		return docThesElem.getString(MONGO_KEY_NAME);
	}
	
	/**
	 * 指定されたドキュメントから、シソーラス定義の親語句を取得する。
	 * @param docThesElem	対象のドキュメントオブジェクト
	 * @return	ドキュメントに含まれるシソーラス定義の親語句、存在しない場合は <tt>null</tt>
	 */
	static public String getThesaurusParentWordFromDocument(Document docThesElem) {
		return docThesElem.getString(MONGO_KEY_PARENT);
	}
	
	/**
	 * 指定されたドキュメントから、シソーラス定義の子の語句を取得する。
	 * @param docThesElem	対象のドキュメントオブジェクト
	 * @return	ドキュメントに含まれるシソーラス定義の子の語句、存在しない場合は <tt>null</tt>
	 */
	static public String getThesaurusChildWordFromDocument(Document docThesElem) {
		return docThesElem.getString(MONGO_KEY_CHILD);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
