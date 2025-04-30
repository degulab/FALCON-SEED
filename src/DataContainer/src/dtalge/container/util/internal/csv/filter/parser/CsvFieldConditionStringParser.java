package dtalge.container.util.internal.csv.filter.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import dtalge.container.util.CsvFieldConditionStringParseError;
import dtalge.container.util.internal.csv.CsvColumnInfo;
import dtalge.container.util.internal.csv.filter.CsvFIeldConditionAnd;
import dtalge.container.util.internal.csv.filter.CsvFieldConditionNot;
import dtalge.container.util.internal.csv.filter.CsvFieldConditionOr;
import dtalge.container.util.internal.csv.filter.CsvFieldConditionPattern;
import dtalge.container.util.internal.csv.filter.ICsvFieldCondition;
import dtalge.util.Strings;

/**
 * CSVフィールド検索条件構文のパーサー。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CsvFieldConditionStringParser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** エスケープシーケンス **/
	static protected final Map<Character, Character>	EscapeSequenceConversionMap;
	static {
		EscapeSequenceConversionMap = new HashMap<Character, Character>();
		EscapeSequenceConversionMap.put('b', '\b');
		EscapeSequenceConversionMap.put('t', '\t');
		EscapeSequenceConversionMap.put('n', '\n');
		EscapeSequenceConversionMap.put('f', '\f');
		EscapeSequenceConversionMap.put('r', '\r');
		EscapeSequenceConversionMap.put('\"', '\"');
		EscapeSequenceConversionMap.put('\'', '\'');
		EscapeSequenceConversionMap.put('\\', '\\');
	}

	/** エンクオートされていない文字列リテラルに含めない文字の配列(昇順ソート済み) **/
	static protected final char[] NotNakedStringLiteralCharacters;
	static {
		NotNakedStringLiteralCharacters = new char[]{
			// comma
			',',
			// operator
			'!', '&', '|', '=', '<', '>',
			// enquote
			'\"',
			// paren
			'(', ')',
		};
		Arrays.sort(NotNakedStringLiteralCharacters);
	}

	/** 演算子の先頭文字の配列(昇順ソート済み) **/
	static protected final char[] OperatorLeadCharacters;
	static {
		OperatorLeadCharacters = new char[]{
			'=', '<', '>', '!', '&', '|',
		};
		Arrays.sort(OperatorLeadCharacters);
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 解析対象文字列 **/
	protected String	_srcstr;
	/** 解析対象文字列の長さ **/
	protected int		_srclen;
	/** 次の取得位置の文字インデックス **/
	protected int		_nextPos;
	/** 終端を表すトークン **/
	protected CsvFieldConditionStringToken	_eosToken;
	/** 読み込みキャッシュとしてインスタンス内で再利用されるバッファ **/
	protected StringBuilder	_strbuf;
	/** 条件式に含まれるCSV フィールド位置を示すオブジェクトのリスト **/
	protected List<CsvColumnInfo>	_fieldPositions;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFieldConditionStringParser(String strCondition)
	{
		_srcstr = (Strings.isNullOrEmpty(strCondition) ? "" : strCondition);
		_srclen = _srcstr.length();
		_nextPos = 0;
		_eosToken = new CsvFieldConditionStringToken(_srclen, CsvFieldConditionStringToken.TYPE_EOS);
		_strbuf = new StringBuilder(_srclen);
		_fieldPositions = new ArrayList<>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getSourceString() {
		return _srcstr;
	}

	/**
	 * フィールド位置を持つすべての条件式から取得されたフィールド位置を示すオブジェクトの配列を取得する。
	 * @return	フィールド位置を持つすべての条件式から取得されたフィールド位置を示すオブジェクトの配列
	 */
	public CsvColumnInfo[] getCachedFieldPositions() {
		CsvColumnInfo[] positions = _fieldPositions.toArray(new CsvColumnInfo[_fieldPositions.size()]);
		return positions;
	}
	
	/**
	 * 文字列をフィールド検索条件としてパースする。
	 * @return	パース結果のフィールド検索条件式、式が存在しない場合は <code>null</code>
	 * @throws CsvFieldConditionStringParseError	文法が正しくない場合
	 */
	public ICsvFieldCondition parse()	throws CsvFieldConditionStringParseError

	{
		_fieldPositions.clear();
		return parseConditionExpression(null);
	}

	//------------------------------------------------------------
	// Internal error messages
	//------------------------------------------------------------
	
	protected String errorConditionEmpty() {
		return "Condition is not specified.";
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 後続のトークンを条件文としてパースする。
	 * <p>括弧内の式として <em>inParenBlock</em> に <code>true</code> が指定された場合、括弧の終端が検出された時点でこのメソッドは終了する。
	 * @param parenBeginToken	パース対象が括弧ブロック内の式であれば先頭の括弧を示すトークン、そうでない場合は <code>null</code>
	 * @return	取得した条件式、取得するものがない場合は <code>null</code>
	 * @throws CsvFieldConditionStringParseError	文法が正しくない場合
	 */
	protected ICsvFieldCondition parseConditionExpression(CsvFieldConditionStringToken parenBeginToken)
		throws CsvFieldConditionStringParseError
	{
		// 直前のトークンが二項論理演算子のトークンであればそのインスタンス、そうでない場合は null
		CsvFieldConditionStringToken prevBinaryLogicalOperatorToken = null;
		// 直前に取得した条件、何も取得していない状態であれば null
		ICsvFieldCondition lastCondition = null;

		// 最初のトークン
		CsvFieldConditionStringToken token = readNextTokenIgnoreWhitespaces();
		
		// 式のパース
		ICsvFieldCondition parseResult;
		for (; !token.isParentEnd() && !token.isEOS(); token = readNextTokenIgnoreWhitespaces()) {
			// パース
			parseResult = null;
			if (token.isCommaSeparator()) {
				// カンマ群(省略可能な二項論理積)
				if (prevBinaryLogicalOperatorToken != null) {
					// error: すでに二項演算子が存在する
					String errmsg = "Unexpected token (" + token.availableText() + ") instead of Field name.";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				else if (lastCondition == null) {
					// 式先頭のコンマは省略可能
					continue;
				}
				else {
					// 二項演算子の論理積と同義
					prevBinaryLogicalOperatorToken = token;
					continue;
				}
			}
			else if (token.isLogicalAndOperator()) {
				// 二項論理積
				if (prevBinaryLogicalOperatorToken != null) {
					// error: すでに二項演算子が存在する
					String errmsg = "Unexpected token (" + token.availableText() + ") instead of Field name.";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				else if (lastCondition == null) {
					// error: 前方のオペランドが存在しない
					String errmsg = "Unexpected token (" + token.availableText() + ") instead of Field name.";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				else {
					// 二項演算子の論理積
					prevBinaryLogicalOperatorToken = token;
					continue;
				}
			}
			else if (token.isLogicalOrOperator()) {
				// 二項論理和
				if (prevBinaryLogicalOperatorToken != null) {
					// error: すでに二項演算子が存在する
					String errmsg = "Unexpected token (" + token.availableText() + ") instead of Field name.";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				else if (lastCondition == null) {
					// error: 前方のオペランドが存在しない
					String errmsg = "Unexpected token (" + token.availableText() + ") instead of Field name.";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				else {
					// 二項演算子の論理和
					prevBinaryLogicalOperatorToken = token;
					continue;
				}
			}
			else if (token.isLogicalNegationOperator()) {
				// 単項論理否定
				//--- 前方の二項演算子の有無
				if (prevBinaryLogicalOperatorToken == null && lastCondition != null) {
					// error: 適切な二項演算子が存在しない
					String errmsg = "Unexpected token (" + token.availableText() + ")";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				//--- 次のトークンは parenBlock でなければならない
				CsvFieldConditionStringToken negationToken = token;
				token = readNextTokenIgnoreWhitespaces();
				if (!token.isParenBegin()) {
					// error: '!' の後は '(' のみ許可
					String errmsg = "Unexpected token (" + token.availableText() + ") instead of '(' after '!'.";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				//--- paren block のパース
				parseResult = parseConditionExpression(token);
				//--- 論理否定の合成
				parseResult = new CsvFieldConditionNot(parseResult);
			}
			else if (token.isParenBegin()) {
				// 優先演算ブロック
				parseResult = parseConditionExpression(token);	// return not null
			}
			else {
				// フィールド比較条件式
				parseResult = parseFieldComparisonExpression(token);	// return not null
			}
			
			// 結合
			if (parseResult == null) {
				String errmsg = "Field name is not specified.";
				throw new CsvFieldConditionStringParseError(_nextPos, errmsg);
			}
			else if (lastCondition != null) {
				if (prevBinaryLogicalOperatorToken == null) {
					String errmsg = "Comma or Logical operator is not specified before field condition.";
					throw new CsvFieldConditionStringParseError(token.position(), errmsg);
				}
				if (prevBinaryLogicalOperatorToken.isLogicalOrOperator()) {
					// 論理和
					lastCondition = new CsvFieldConditionOr(lastCondition, parseResult);
				}
				else {
					// 論理積
					lastCondition = new CsvFIeldConditionAnd(lastCondition, parseResult);
				}
				prevBinaryLogicalOperatorToken = null;
			}
			else {
				// 初回の条件式
				lastCondition = parseResult;
			}
		}
		
		// 終端の確認
		if (token.isParentEnd()) {
			// paren block 終端の ')'
			if (parenBeginToken == null) {
				// paren block needs left paren
				String errmsg = "'(' is not specified before ')'.";
				throw new CsvFieldConditionStringParseError(token.position(), errmsg);
			}
			else if (lastCondition == null) {
				// 条件式が存在しない
				String errmsg = "There is no field condition between '(' and ')'.";
				throw new CsvFieldConditionStringParseError(parenBeginToken.position(), errmsg);
			}
			// else: 正当
		}
		else {
			// EOS
			if (parenBeginToken != null) {
				// paren block needs right paren
				String errmsg = "')' is not specified.";
				throw new CsvFieldConditionStringParseError(token.position(), errmsg);
			}
		}
		return lastCondition;
	}

	/**
	 * 後続のトークンを単一の比較条件式としてパースする。
	 * @param lookaheadToken	パース対象となる直前に取得されたトークン、指定しない場合は <code>null</code>
	 * @return	取得した条件式(<code>null</code> 以外)
	 * @throws CsvFieldConditionStringParseError	文法が正しくない場合
	 */
	protected ICsvFieldCondition parseFieldComparisonExpression(CsvFieldConditionStringToken lookaheadToken)
		throws CsvFieldConditionStringParseError
	{
		// parse name
		CsvFieldConditionStringToken tokenFieldName;
		tokenFieldName = (lookaheadToken != null ? lookaheadToken : readNextTokenIgnoreWhitespaces());
		if (!tokenFieldName.isStringLiteral()) {
			// error: 文字列ではない
			if (tokenFieldName.isEOS()) {
				// not found
				String errmsg = "Field name is not specified before end of string.";
				throw new CsvFieldConditionStringParseError(tokenFieldName.position(), errmsg);
			}
			else {
				// unexpected
				String errmsg = "Unexpected token (" + tokenFieldName.availableText() + ") instead of Field name.";
				throw new CsvFieldConditionStringParseError(tokenFieldName.position(), errmsg);
			}
		}
		String fieldName = tokenFieldName.availableText();
		
		// parse operator
		CsvFieldConditionStringToken tokenOperator = readNextTokenIgnoreWhitespaces();
		if (!tokenOperator.isComparisonOperator()) {
			// error: 比較演算子ではない
			if (tokenOperator.isEOS()) {
				// not found
				String errmsg = "Comparison operator ('=', '!=') is not specified after Field name (" + tokenFieldName.rawText() + ")";
				throw new CsvFieldConditionStringParseError(tokenOperator.position(), errmsg);
			}
			else {
				// unexpected
				String errmsg = "Unexpected token (" + tokenOperator.rawText() + ") instead of Comparison operator ('=', '!=')";
				throw new CsvFieldConditionStringParseError(tokenOperator.position(), errmsg);
			}
		}
		else if (!tokenOperator.isComparisonEqualOperator() && !tokenOperator.isComparisonNotEqualOperator()) {
			// error: '=', '==', '!=' 以外
			String errmsg = "Unsupported token (" + tokenOperator.rawText() + ")";
			throw new CsvFieldConditionStringParseError(tokenOperator.position(), errmsg);
		}
		
		// parse pattern
		CsvFieldConditionStringToken tokenFieldPattern = readNextTokenIgnoreWhitespaces();
		if (!tokenFieldPattern.isStringLiteral()) {
			// error: 文字列ではない
			if (tokenFieldPattern.isEOS()) {
				// not found
				String errmsg = "Field pattern is not specified after Comparison operator (" + tokenOperator.rawText() + ")";
				throw new CsvFieldConditionStringParseError(tokenFieldPattern.position(), errmsg);
			}
			else {
				// unexpected
				String errmsg = "Unexpected token (" + tokenFieldPattern.rawText() + ") instead of Field pattern.";
				throw new CsvFieldConditionStringParseError(tokenFieldPattern.position(), errmsg);
			}
		}
		//--- check pattern
		Pattern fieldPattern;
		try {
			fieldPattern = Pattern.compile(tokenFieldPattern.availableText(), Pattern.DOTALL);
		}
		catch (Throwable ex) {
			// error: 正規表現の文法誤り
			String errmsg = "Invalid field pattern: " + tokenFieldPattern.rawText();
			throw new CsvFieldConditionStringParseError(tokenFieldPattern.position(), errmsg);
		}
		
		// make pattern condition
		CsvFieldConditionPattern result = new CsvFieldConditionPattern(fieldName, fieldPattern);
		//--- フィールド位置を示すオブジェクトを保存
		_fieldPositions.add(result.fieldPosition());
		//--- != 対応
		if (tokenOperator.isComparisonNotEqualOperator()) {
			// 否定(!=)
			ICsvFieldCondition notEqualResult = new CsvFieldConditionNot(result);
			return notEqualResult;
		}
		else {
			// 一致(==)
			return result;
		}
	}

	/**
	 * 次の読み込み位置の文字が、指定された文字かどうかを判定する。
	 * @param expect	期待する文字
	 * @return	次の読み込み位置の文字が期待する文字であれば <code>true</code>
	 */
	protected boolean isExpectedNextChar(char expect)
	{
		return (_nextPos < _srclen && _srcstr.charAt(_nextPos) == expect);
	}

	/**
	 * 指定された文字が空白かどうかを判定する。
	 * 空白は、0x00 <= ch <= 0x20 (' ') までの範囲内の文字とする。
	 * @param ch	判定する文字
	 * @return	空白と見なす場合は <code>true</code>
	 */
	protected boolean isWhitespace(char ch)
	{
		return (ch <= ' ');
	}

	/**
	 * エンクオートされていない文字列リテラルに含めない文字かどうかを判定する。
	 * @param ch	判定する文字
	 * @return	エンクオートされていない文字列リテラルに含めない文字であれば <code>true</code>
	 */
	protected boolean isNotNakedStringLiteralCharacter(char ch) {
		if (isWhitespace(ch)) {
			return true;	// 空白も、エンクオートされていない文字列リテラルに含めない
		}
		else {
			int result = Arrays.binarySearch(NotNakedStringLiteralCharacters, ch);
			return (result >= 0);
		}
	}

	/**
	 * 演算子の先頭文字かどうかを判定する。
	 * @param ch	判定する文字
	 * @return	演算子の先頭文字であれば <code>true</code>
	 */
	protected boolean isOperatorLeadCharacter(char ch) {
		int result = Arrays.binarySearch(OperatorLeadCharacters, ch);
		return (result >= 0);
	}

	/**
	 * 次の取得位置から、空白をスキップしてトークンを取得し、取得位置を更新する。
	 * @return	取得した空白以外のトークン
	 * @throws CsvFieldConditionStringParseError	トークンとして適当ではない場合
	 */
	protected CsvFieldConditionStringToken readNextTokenIgnoreWhitespaces()
		throws CsvFieldConditionStringParseError
	{
		CsvFieldConditionStringToken token;
		do {
			token = readNextToken();
		} while (token.tokenType() == CsvFieldConditionStringToken.TYPE_WHITESPACES);
		
		return token;
	}

	/**
	 * 次の取得位置のトークンを取得し、取得位置を更新する。
	 * @return	取得したトークン
	 * @throws CsvFieldConditionStringParseError	トークンとして適当ではない場合
	 */
	protected CsvFieldConditionStringToken readNextToken()
		throws CsvFieldConditionStringParseError
	{
		if (_nextPos >= _srclen) {
			// End of String
			return _eosToken;
		}

		// fetch first char
		char fetchedChar = _srcstr.charAt(_nextPos);
		
		// judge first char
		if (isWhitespace(fetchedChar)) {
			// whitespaces
			int spos = _nextPos++;
			for (; _nextPos < _srclen; _nextPos++) {
				if (!isWhitespace(_srcstr.charAt(_nextPos))) {
					// not whitespace
					break;
				}
			}
			//--- return Whitespaces token
			return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_WHITESPACES, _srcstr.substring(spos, _nextPos));
		}
		else if (fetchedChar == '\"') {
			// paren block begin
			return readNextTokenAsEnquotedString();
		}
		else if (fetchedChar == '(') {
			// ブロック先頭(単一文字)
			int cpos = _nextPos++;
			return new CsvFieldConditionStringToken(cpos, CsvFieldConditionStringToken.TYPE_PAREN_BEGIN, "(");
		}
		else if (fetchedChar == ')') {
			// ブロック終端(単一文字)
			int cpos = _nextPos++;
			return new CsvFieldConditionStringToken(cpos, CsvFieldConditionStringToken.TYPE_PAREN_END, ")");
		}
		else if (fetchedChar == ',') {
			// コンマ(単一文字 or 連続)
			return readNextTokenAsOneOrMoreComma();
		}
		else if (isOperatorLeadCharacter(fetchedChar)) {
			// 演算子
			return readNextTokenAsOperator();
		}
		else {
			// エンクオートされていない文字列リテラル
			return readNextTokenAsNakedString();
		}
	}
	
	/**
	 * 次の取得位置のトークンをカンマとして、さらに空白も許可して複数のカンマを単一のカンマとして取得し、取得位置を更新する。
	 * @return	取得したトークン
	 * @throws CsvFieldConditionStringParseError	トークンとして適当ではない場合
	 */
	protected CsvFieldConditionStringToken readNextTokenAsOneOrMoreComma()
	{
		int spos = _nextPos++;
		
		// 連続する空白とカンマは、以降読み飛ばす(カンマの単一化)
		char ch;
		for (; _nextPos < _srclen; _nextPos++) {
			ch = _srcstr.charAt(_nextPos);
			if (!isWhitespace(ch) && ch != ',') {
				// 空白でもカンマでもない場合に、ループ終了
				break;
			}
		}
		
		// 単一のカンマとしてトークン化
		return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_COMMA, _srcstr.substring(spos, _nextPos), ",");
	}
	
	/**
	 * 次の取得位置のトークンを演算子として取得し、取得位置を更新する。
	 * @return	取得したトークン
	 * @throws CsvFieldConditionStringParseError	トークンとして適当ではない場合
	 */
	protected CsvFieldConditionStringToken readNextTokenAsOperator()
		throws CsvFieldConditionStringParseError
	{
		int spos = _nextPos++;
		char firstChar = _srcstr.charAt(spos);
		char secondChar = (_nextPos < _srclen ? _srcstr.charAt(_nextPos) : 0);

		if (firstChar == '=') {
			if (secondChar == '=') {
				// double equals
				++_nextPos;	// 最後の '=' の次の位置にする
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_CMP_EQ, "==");
			}
			else {
				// single equal
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_CMP_EQ, "=");
			}
		}
		else if (firstChar == '!') {
			if (secondChar == '=') {
				// not equals
				++_nextPos;	// 最後の '=' の次の位置にする
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_CMP_NE, "!=");
			}
			else {
				// single negation
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_LOG_NOT, "!");
			}
		}
		else if (firstChar == '<') {
			if (secondChar == '=') {
				// less equal
				++_nextPos;	// 最後の '=' の次の位置にする
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_CMP_LE, "<=");
			}
			else {
				// less than
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_CMP_LT, "<");
			}
		}
		else if (firstChar == '>') {
			if (secondChar == '=') {
				// greater equal
				++_nextPos;	// 最後の '=' の次の位置にする
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_CMP_GE, ">=");
			}
			else {
				// greater than
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_CMP_GT, ">");
			}
		}
		else if (firstChar == '&') {
			if (secondChar == '&') {
				// logical and
				++_nextPos;	// 最後の '&' の次の位置にする
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_LOG_AND, "&&");
			}
			else {
				// error
				String errmsg = "Undefined operator token: " + firstChar;
				throw new CsvFieldConditionStringParseError(spos, errmsg);
			}
		}
		else if (firstChar == '|') {
			if (secondChar == '|') {
				// logical or
				++_nextPos;	// 最後の '|' の次の位置にする
				return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_OPERATOR_LOG_OR, "||");
			}
			else {
				// error
				String errmsg = "Undefined operator token: " + firstChar;
				throw new CsvFieldConditionStringParseError(spos, errmsg);
			}
		}
		else {
			// unsupported operator
			String errmsg = "Undefined operator token: " + firstChar;
			throw new CsvFieldConditionStringParseError(spos, errmsg);
		}
	}
	
	/**
	 * 次の取得位置のトークンをエンクオートされていない文字列として取得し、取得位置を更新する。
	 * @return	取得したトークン
	 * @throws CsvFieldConditionStringParseError	トークンとして適当ではない場合
	 */
	protected CsvFieldConditionStringToken readNextTokenAsNakedString()
	{
		int spos = _nextPos;
		_strbuf.setLength(0);	// reset buffer
		
		// read as naked string literal
		char ch;
		for (; _nextPos < _srclen; _nextPos++) {
			ch = _srcstr.charAt(_nextPos);
			if (isNotNakedStringLiteralCharacter(ch)) {
				break;
			}
		}
		
		// check empty
		if (spos == _nextPos) {
			// error
			throw new IllegalStateException("pos:[" + spos + ", " + _nextPos + "] Naked string literal is empty: " + _srcstr);
		}
		
		// return token
		return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_NAKED_STRING, _srcstr.substring(spos, _nextPos));
	}
	
	/**
	 * 次の取得位置のトークンをエンクオートされた文字列として取得し、取得位置を更新する。
	 * @return	取得したトークン
	 * @throws CsvFieldConditionStringParseError	トークンとして適当ではない場合
	 */
	protected CsvFieldConditionStringToken readNextTokenAsEnquotedString()
		throws CsvFieldConditionStringParseError
	{
		// must: _srcstr.charAt(_nextPos) == '\"'
		int spos = _nextPos++;
		_strbuf.setLength(0);	// reset buffer
		
		// unpack
		boolean foundEndQuote = false;
		char firstChar;
		for (; _nextPos < _srclen; _nextPos++) {
			firstChar = _srcstr.charAt(_nextPos);
			if (firstChar == '\"') {
				// end of quote
				foundEndQuote = true;
				++_nextPos;	// 終端クオートの次の文字インデックス
				break;
			}
			else if (firstChar == '\\') {
				// escape begin
				++_nextPos;
				if (_nextPos >= _srclen) {
					// Unexpected end of string
					String errmsg = "Invalid escape sequence in enquoted string: " + _srcstr.substring(spos, _nextPos);
					throw new CsvFieldConditionStringParseError(_nextPos - 1, errmsg);
				}
				char secondChar = _srcstr.charAt(_nextPos);
				if (EscapeSequenceConversionMap.containsKey(secondChar)) {
					// Escape sequence
					_strbuf.append(EscapeSequenceConversionMap.get(secondChar).charValue());
				}
				else if (secondChar == 'u') {
					// unicode 4-hex
					int escape_spos  = _nextPos - 1;
					int unicode_spos = _nextPos + 1;
					int unicode_epos = Math.min(unicode_spos + 4, _srclen);
					if ((unicode_epos - unicode_spos) != 4) {
						// 16 進数を表す文字列が 4 桁に満たない
						String errmsg = "Invalid Unicode value in enquoted string: " + _srcstr.substring(escape_spos, unicode_epos);
						throw new CsvFieldConditionStringParseError(escape_spos, errmsg);
					}
					//--- Unicode string => unicode
					int codepoint;
					try {
						codepoint = Integer.parseInt(_srcstr, unicode_spos, unicode_epos, 16);
					}
					catch (NumberFormatException ex) {
						// invalid 4-hex
						String errmsg = "Invalid Unicode value in enquoted string: " + _srcstr.substring(escape_spos, unicode_epos);
						throw new CsvFieldConditionStringParseError(escape_spos, errmsg);
					}
					//--- append unicode
					try {
						_strbuf.appendCodePoint(codepoint);
					}
					catch (IllegalArgumentException ex) {
						// invalid 4-hex
						String errmsg = "Invalid Unicode value in enquoted string: " + _srcstr.substring(escape_spos, unicode_epos);
						throw new CsvFieldConditionStringParseError(escape_spos, errmsg, ex);
					}
					_nextPos = unicode_epos - 1;
				}
				else {
					// escaped normal char
					_strbuf.append(secondChar);
				}
			}
			else {
				// normal char
				_strbuf.append(firstChar);
			}
		}
		
		// check end of quote
		if (!foundEndQuote) {
			String errmsg = "Invalid escaped string, termination quote does not found.";
			throw new CsvFieldConditionStringParseError(_nextPos, errmsg);
		}
		
		// return token
		return new CsvFieldConditionStringToken(spos, CsvFieldConditionStringToken.TYPE_ENQUOTED_STRING, _srcstr.substring(spos, _nextPos), _strbuf.toString());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
