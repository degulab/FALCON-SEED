package dtalge.container.util;

import java.util.Objects;

/**
 * 勘定科目としての基底情報を保持するクラス。
 * <p>
 * 勘定科目の基底情報として、以下の内容を保持する。
 * <ul>
 *  <li>科目名 (名前基底キーに相当する文字列)</li>
 * 	<li>借方・貸方の貸借種別 (借方:Debit, 貸方:Credit)</li>
 * </ul>
 * なお、このオブジェクトは不変である。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DebitCreditItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 不明な貸借種別を表す **/
	static public final int SIDE_UNKNOWN	= 0;
	/** 借方科目であることを示す **/
	static public final int	SIDE_DEBIT	= 1;
	/** 貸方科目であることを示す **/
	static public final int SIDE_CREDIT	= 2;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final int		_type;
	private final String	_nameKey;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 科目名が <code>null</code> の <em>type</em> で指定された貸借種別を持つ、新しいインスタンスを生成する。
	 * @param type	貸借種別({@link #SIDE_DEBIT} or {@link #SIDE_CREDIT})
	 */
	public DebitCreditItem(int type) {
		this(type, null);
	}

	/**
	 * 指定されたパラメーターで、新しいインスタンスを生成する。
	 * @param type	貸借種別({@link #SIDE_DEBIT} or {@link #SIDE_CREDIT})
	 * @param name	科目名
	 */
	public DebitCreditItem(int type, String name) {
		if (type == SIDE_DEBIT || type == SIDE_CREDIT) {
			_type = type;
		}
		else {
			_type = SIDE_UNKNOWN;
		}
		_nameKey = name;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトが借方科目かどうかを判定する。
	 * @return	借方科目であれば <code>true</code>
	 */
	public boolean isDebitSide() {
		return (_type == SIDE_DEBIT);
	}

	/**
	 * このオブジェクトが貸方科目かどうかを判定する。
	 * @return	貸方科目であれば <code>true</code>
	 */
	public boolean isCreditSide() {
		return (_type == SIDE_CREDIT);
	}

	/**
	 * 科目名が設定されているかどうかを判定する。
	 * @return	科目名が設定されていれば <code>true</code>
	 */
	public boolean isEmptyNameKey() {
		return (_nameKey == null || _nameKey.length() <= 0);
	}

	/**
	 * 貸借種別を取得する。
	 * @return	次のいずれかの貸借種別を返す。
	 * 			<ul>
	 * 				<li>借方： {@link #SIDE_DEBIT}</li>
	 * 				<li>貸方： {@link #SIDE_CREDIT}</li>
	 * 				<li>未定義： {@link #SIDE_UNKNOWN}</li>
	 * 			</ul>
	 */
	public int getType() {
		return _type;
	}

	/**
	 * 設定されている科目名を取得する。
	 * @return	設定されている科目名、設定されていない場合は <code>null</code>
	 */
	public String getNameKey() {
		return _nameKey;
	}

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	@Override
	public int hashCode() {
		int h = _type;
		h = 31 * h + (_nameKey==null ? 0 : _nameKey.hashCode());
		return h;
	}

	/**
	 * 指定されたオブジェクトとこのインスタンスの内容が等しいかどうかを判定する。
	 * 
	 * @param obj	同値性を判定するオブジェクトの一方
	 * 
	 * @return 同値である場合に <code>true</code> を返す。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj != null && obj.getClass() == this.getClass()) {
			DebitCreditItem another = (DebitCreditItem)obj;
			if (another._type == this._type &&
				Objects.equals(another._nameKey, this._nameKey))
			{
				return true;
			}
		}
		
		// not equals
		return false;
	}

	/**
	 * このインスタンスの文字列表現を返す。
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("{");
		if (_type == SIDE_DEBIT)
			sb.append("Debit, ");
		sb.append(toPrintString(_nameKey));
		sb.append("}");
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String toPrintString(String str) {
		if (str == null)
			return "null";
		
		return "\"" + str.toString() + "\"";
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
