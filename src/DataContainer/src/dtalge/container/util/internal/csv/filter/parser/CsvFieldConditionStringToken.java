package dtalge.container.util.internal.csv.filter.parser;

/**
 * CSVフィールド検索条件構文のトークンを保持するクラス。
 * <p>
 * なお、このオブジェクトは不変である。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CsvFieldConditionStringToken
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 終端到達を表すトークン種別 **/
	static public final int	TYPE_EOS				= (-1);
	
	/** トークン種別：空白(&lt;= 0x20) **/
	static public final int TYPE_WHITESPACES		= 0x0000;
	/** トークン種別：文字列リテラル(記号と制御文字を除く) **/
	static public final int TYPE_NAKED_STRING		= 0x0001;
	/** トークン種別：ダブルクオートで囲まれた文字 **/
	static public final int TYPE_ENQUOTED_STRING	= 0x0002;
	/** 種別判定マスク：文字列タイプを判定するマスク **/
	static public final int TYPEMASK_STRING			= 0x0003;
	/** トークン種別：左括弧 **/
	static public final int	TYPE_PAREN_BEGIN		= 0x0004;
	/** トークン種別：右括弧 **/
	static public final int TYPE_PAREN_END			= 0x0008;
	/** トークン種別：比較演算子：等しい(=, ==) **/
	static public final int TYPE_OPERATOR_CMP_EQ	= 0x0010;
	/** トークン種別：比較演算子：等しくない(!=) **/
	static public final int TYPE_OPERATOR_CMP_NE	= 0x0020;
	/** トークン種別：比較演算子: &lt; **/
	static public final int TYPE_OPERATOR_CMP_LT	= 0x0040;
	/** トークン種別：比較演算子： &lt;= **/
	static public final int TYPE_OPERATOR_CMP_LE	= 0x0080;
	/** トークン種別：比較演算子： &gt; **/
	static public final int TYPE_OPERATOR_CMP_GT	= 0x0100;
	/** トークン種別：比較演算子： &gt;= **/
	static public final int TYPE_OPERATOR_CMP_GE	= 0x0200;
	/** 種別判定マスク：比較演算子 **/
	static public final int	TYPEMASK_OPERATOR_CMP	= 0x03F0;
	/** トークン種別：カンマ(,) **/
	static public final int TYPE_OPERATOR_COMMA		= 0x0800;
	/** トークン種別：論理演算子：論理否定(!) **/
	static public final int TYPE_OPERATOR_LOG_NOT	= 0x1000;
	/** トークン種別：論理演算子：論理積(&amp;&amp;) **/
	static public final int TYPE_OPERATOR_LOG_AND	= 0x2000;
	/** トークン種別：論理演算子：論理和(||) **/
	static public final int TYPE_OPERATOR_LOG_OR	= 0x4000;
	/** 種別判定マスク：論理演算子 **/
	static public final int TYPEMASK_OPERATOR_LOG	= 0x7000;


	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** テキスト先頭からの文字位置(char インデックス) **/
	public int _pos;
	/** トークン種別 **/
	public int _type;
	/** 入力文字列 **/
	public String	_rawText;
	/** 有効文字列 **/
	public String	_text;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFieldConditionStringToken(int pos, int type)
	{
		this(pos, type, null, null);
	}
	
	public CsvFieldConditionStringToken(int pos, int type, String text)
	{
		this(pos, type, text, text);
	}
	
	public CsvFieldConditionStringToken(int pos, int type, String rawText, String text)
	{
		_pos     = pos;
		_type    = type;
		_rawText = rawText;
		_text    = text;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int position() {
		return _pos;
	}
	
	public int tokenType() {
		return _type;
	}
	
	public String rawText() {
		return _rawText;
	}
	
	public String availableText() {
		return _text;
	}
	
	public boolean isEOS() {
		return (_type == TYPE_EOS);
	}
	
	public boolean isWhitespaces() {
		return (_type == TYPE_WHITESPACES);
	}
	
	public boolean isStringLiteral() {
		return ((_type & TYPEMASK_STRING) != 0);
	}
	
	public boolean isParenBegin() {
		return (_type == TYPE_PAREN_BEGIN);
	}
	
	public boolean isParentEnd() {
		return (_type == TYPE_PAREN_END);
	}
	
	public boolean isCommaSeparator() {
		return (_type == TYPE_OPERATOR_COMMA);
	}
	
	public boolean isComparisonEqualOperator() {
		return (_type == TYPE_OPERATOR_CMP_EQ);
	}
	
	public boolean isComparisonNotEqualOperator() {
		return (_type == TYPE_OPERATOR_CMP_NE);
	}
	
	public boolean isComparisonOperator() {
		return ((_type & TYPEMASK_OPERATOR_CMP) != 0);
	}
	
	public boolean isLogicalOperator() {
		return ((_type & TYPEMASK_OPERATOR_LOG) != 0);
	}
	
	public boolean isLogicalNegationOperator() {
		return (_type == TYPE_OPERATOR_LOG_NOT);
	}
	
	public boolean isLogicalAndOperator() {
		return (_type == TYPE_OPERATOR_LOG_AND);
	}
	
	public boolean isLogicalOrOperator() {
		return (_type == TYPE_OPERATOR_LOG_OR);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
