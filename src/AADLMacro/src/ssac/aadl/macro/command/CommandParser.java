/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CommandParser.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandParser.java	1.00	2008/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * マクロコマンドのパーサー。
 * 
 * @version 2.1.0	2014/05/29
 *
 * @since 1.00
 */
public class CommandParser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final CommandToken OmittedJavaCommand = new CommandToken(CommandToken.ACTION, -1, -1, "java");

	static private final String EOT_TEXT = "<End of text>";
	static private final String EOT_ERROR_MSG = "Token not found because " + EOT_TEXT + ".";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CommandNode parseCommand(String text) throws RecognitionException
	{
		// lexer
		CommandTokenizer tokens = new CommandTokenizer(text);
		// parser
		return parseAction(tokens);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	// (modifier ('(' (identifier(','identifier)*)? ')')? ':')? (action)? (conditionalExpression|termConditionalExpression)?  
	private CommandNode parseAction(CommandTokenizer tokens) throws RecognitionException
	{
		CommandActionNode actionNode = null;
		CommandModifierNode modifierNode = null;
		
		CommandToken token = tokens.getToken();
		if (token == null) {
			// 何もない場合は、Javaプロセス実行の省略とみなす
			actionNode = new CommandActionNode(MacroAction.JAVA, OmittedJavaCommand);
			return actionNode;
		}
		
		// トークン判別
		int tokenType = token.getType();
		if (tokenType == CommandToken.COMMENT) {
			// コメントなので、これ以降のトークンは存在しない
			CommandToken actionToken = new CommandToken(CommandToken.ACTION, token);
			actionNode = new CommandActionNode(MacroAction.COMMENT, actionToken);
			tokens.consume();
			return actionNode;
		}
		else if (tokenType == CommandToken.LBRACKET) {
			// Javaプロセス実行の省略形で、条件指定あり
			actionNode = new CommandActionNode(MacroAction.JAVA, OmittedJavaCommand);
			// 条件式
			CommandNode cnode = parseConditionalExpression(tokens);
			if (cnode != null) {
				actionNode.addChild(cnode);
			}
			// これ以上の入力は許可しない
			if (tokens.hasMoreTokens()) {
				// 不明なトークン
				throw new UndefinedTokenException(token);
			}
			return actionNode;
		}
		else if (tokenType != CommandToken.IDENTIFIER && tokenType != CommandToken.GROUP) {
			// コマンドではない：不明なトークン
			throw new UndefinedTokenException(token);
		}
		
		// 先頭トークンの判別
		CommandToken idToken = token;
		MacroAction   action   = MacroAction.fromCommand(idToken.getText());
		MacroModifier modifier = MacroModifier.fromCommand(idToken.getText());
		tokens.consume();
		token = tokens.getToken();
		
		// プロセス名リストのパース
		CommandProcessNameListNode procListNode = null;
		if (token != null && token.getType() == CommandToken.LPAREN) {
			// プロセス名リストをパース
			procListNode = parseProcessNameList(tokens);
			token = tokens.getToken();
		}
		
		// 修飾子の判別
		if (modifier != null) {
			// 修飾子の指定
			CommandToken modifierToken = new CommandToken(CommandToken.MODIFIER, idToken);
			modifierNode = new CommandModifierNode(modifier, modifierToken);
			if (procListNode != null) {
				if (!modifier.isRequiredProcessNameList()) {
					// プロセス名リストが不要の修飾子に対し、プロセス名リストが指定された
					throw new RecognitionException(procListNode.getToken(), "'" + idToken.getText() + "' macro modifier cannot be specified process name list.");
				}
				modifierNode.setProcessNameListNode(procListNode);
			}
			else {
				if (modifier.isRequiredProcessNameList()) {
					// プロセス名リストが必須の修飾子に対し、プロセス名リストが存在しない
					throw new RecognitionException(tokens, "Process name list is required for the macro modifier '" + idToken.getText() + "'.");
				}
			}
			//--- 修飾子区切りが必須
			needTypes(tokens, "':'", CommandToken.COLON);
			//--- アクション取得
			idToken = needTypes(tokens, "macro action", CommandToken.IDENTIFIER);	// アクション必須
			action = MacroAction.fromCommand(idToken.getText());
			if (action == null) {
				throw new UndefinedActionException(idToken);
			}
			token = tokens.getToken();
			if (token != null && token.getType() == CommandToken.LPAREN) {
				// プロセス名リストをパース
				procListNode = parseProcessNameList(tokens);
				token = tokens.getToken();
			} else {
				procListNode = null;
			}
		}
		else if (action == null) {
			// 不明なトークン
			if (token != null && token.getType() == CommandToken.COLON) {
				//--- 修飾子区切りがあれば、先頭は修飾子として扱う
				throw new UndefinedModifierException(idToken);
			} else {
				//--- 修飾子区切りがなければ、先頭はアクションとして扱う
				throw new UndefinedActionException(idToken);
			}
		}
		
		// アクションの判別
		CommandToken actionToken = new CommandToken(CommandToken.ACTION, idToken);
		actionNode = new CommandActionNode(action, actionToken);
		if (modifierNode != null) {
			if (!action.isModifierArrowed(modifier)) {
				// 許可されていない修飾子
				String errmsg = String.format("'%s' macro action cannot be specified macro modifier '%s'.",
												action.commandString(), modifier.modifierString());
				throw new RecognitionException(modifierNode.getToken(), errmsg);
			}
			actionNode.setModifier(modifierNode);
		}
		if (procListNode != null) {
			if (!action.isRequiredProcessNameList()) {
				// プロセス名リストが不要のアクションに対し、プロセス名リストが指定された
				throw new RecognitionException(procListNode.getToken(), "'" + idToken.getText() + "' macro action cannot be specified process name list.");
			}
			actionNode.setProcessNameListNode(procListNode);
		}
		else {
			if (action.isRequiredProcessNameList()) {
				// プロセス名リストが必須のアクションに対し、プロセス名リストが存在しない
				throw new RecognitionException(tokens, "Process name list is required for the macro action '" + idToken.getText() + "'.");
			}
		}
		if (!tokens.hasMoreTokens()) {
			// これ以上のトークンがなければ、解析完了
			return actionNode;
		}

		// 条件式の判別
		if (action == MacroAction.ERRORCOND) {
			// 終了条件式
			CommandNode cnode = parseTermConditionalExpression(tokens);
			if (cnode != null) {
				actionNode.addChild(cnode);
			}
		}
		else if (action == MacroAction.EXIT) {
			// 条件式
			CommandNode cnode = parseConditionalExpression(tokens);
			if (cnode != null) {
				actionNode.addChild(cnode);
			}
		}
		else {
			// 条件式
			CommandNode cnode = parseConditionalExpression(tokens);
			if (cnode != null) {
				actionNode.addChild(cnode);
			}
		}
		
		// 完了
		if (tokens.hasMoreTokens()) {
			// これ以上のトークンは、エラー
			throw new UndefinedTokenException(tokens.getToken());
		}
		return actionNode;
		
		
		
		
//		// トークン判別
//		int tokenType = actionToken.getType();
//		if (tokenType == CommandToken.IDENTIFIER || tokenType == CommandToken.GROUP)
//		{
//			// コマンド判別
//			MacroAction action = MacroAction.fromCommand(token.getText());
//			if (action == null) {
//				throw new UndefinedActionException(token);
//			}
//			// アクションノード生成
//			CommandToken actionToken = new CommandToken(CommandToken.ACTION, token);
//			actionNode = new CommandActionNode(action, actionToken);
//			tokens.consume();
//			// コマンドに指定可能な修飾子の判別
//			if (modifierNode != null) {
//				if (!action.isModifierArrowed(modifierNode.getModifier())) {
//					// 許可されていない修飾子
//					String errmsg = String.format("Cannot specify '%s' modifier to the '%s' command.",
//													modifierNode.getModifier().modifierString(),
//													action.commandString());
//					throw new RecognitionException(modifierNode.getToken(), errmsg);
//				}
//				actionNode.setModifier(modifierNode);
//			}
//			// コマンドに付加するプロセス名リストの判別
//			if (action.isRequiredProcessNameList()) {
//				needTypes(tokens, "Process names", CommandToken.LPAREN);
//				CommandProcessNameListNode procNameListNode = parseProcessNameList(tokens);
//				actionNode.setProcessNameListNode(procNameListNode);
//			}
//			// 条件式の判別
//			if (tokens.hasMoreTokens()) {
//				if (action == MacroAction.ERRORCOND) {
//					// 終了条件式
//					CommandNode cnode = parseTermConditionalExpression(tokens);
//					if (cnode != null) {
//						actionNode.addChild(cnode);
//					}
//				}
//				else if (action == MacroAction.EXIT) {
//					// 条件式
//					CommandNode cnode = parseConditionalExpression(tokens);
//					if (cnode != null) {
//						actionNode.addChild(cnode);
//					}
//				}
//				else {
//					// 条件式
//					CommandNode cnode = parseConditionalExpression(tokens);
//					if (cnode != null) {
//						actionNode.addChild(cnode);
//					}
//				}
//			}
//			return actionNode;
//		}
//		else if (tokenType == CommandToken.LBRACKET) {
//			// Javaプロセス実行の省略形で、条件指定あり
//			actionNode = new CommandActionNode(MacroAction.JAVA, OmittedJavaCommand);
//			// 条件式
//			CommandNode cnode = parseConditionalExpression(tokens);
//			if (cnode != null) {
//				actionNode.addChild(cnode);
//			}
//			return actionNode;
//		}
//		else if (tokenType == CommandToken.COMMENT) {
//			// コメントなので、これ以降のトークンは存在しない
//			CommandToken actionToken = new CommandToken(CommandToken.ACTION, token);
//			actionNode = new CommandActionNode(MacroAction.COMMENT, actionToken);
//			tokens.consume();
//			return actionNode;
//		}
//
//		// 不明なトークン
//		throw new UndefinedTokenException(token);
	}

//	// ( identifier ( '(' identifier (',' identifier)* (',')? ')' )? ':' )
//	/**
//	 * Modifier のパース。
//	 * Modifier ではない場合、<tt>null</tt> を返す。
//	 * このメソッドでは、Identifier の次が ':' もしくは '(' の場合、修飾子とみなす。
//	 * @param tokens	先頭識別子の次の位置を示すトークナイザー
//	 * @return	生成されたコマンドノード、Modifier ではない場合は <tt>null</tt>
//	 * @throws RecognitionException	構文エラー
//	 * @since 2.1.0
//	 */
//	private CommandModifierNode parseModifier(CommandTokenizer tokens) throws RecognitionException
//	{
//		int startPos = tokens.getCurrentPosition();
//		
//		// identifier
//		CommandToken tokenId = tokens.getToken();
//		if (tokenId.getType() != CommandToken.IDENTIFIER) {
//			return null;	// not modifier
//		}
//		tokens.consume();
//		
//		// check modifier
//		MacroModifier modifier = MacroModifier.fromCommand(tokenId.getText());
//		if (modifier == null) {
//			return null;	// not modifier
//		}
//		// 修飾子ノード生成
//		CommandToken modifierToken = new CommandToken(CommandToken.MODIFIER, tokenId);
//		CommandModifierNode modifierNode = new CommandModifierNode(modifier, modifierToken);
//		tokens.consume();
//		
//		// プロセス名リストの取得
//		CommandToken token = tokens.getToken();
//		if (token.getType() == CommandToken.LPAREN) {
//			// プロセス名リストを許可していない修飾子ならエラー
//			if (!modifier.isRequiredProcessNameList()) {
//				throw new MismatchedTokenException(token, "':'");
//			}
//			// プロセス名リストのパース
//			CommandProcessNameListNode procNameList = parseProcessNameList(tokens);
//			modifierNode.setProcessNameListNode(procNameList);
//		}
//		
//		// ':'
//		needTypes(tokens, "':'", CommandToken.COLON);
//		return modifierNode;
//	}

	// '(' identifier (',' identifier)* ')'
	/**
	 * プロセスIDを一つ以上含むカンマ区切りの配列としてパースする。
	 * トークナイザーの現在位置のトークンは '(' でなければならない。
	 * @param tokens	トークナイザー
	 * @return	生成されたコマンドノード(プロセスIDを子に持つノード)
	 * @throws RecognitionException	構文エラー
	 * @since 2.1.0
	 */
	private CommandProcessNameListNode parseProcessNameList(CommandTokenizer tokens) throws RecognitionException
	{
		// '('
		CommandToken token = needTypes(tokens, "'('", CommandToken.LPAREN);
		CommandProcessNameListNode root = new CommandProcessNameListNode(new CommandToken(CommandToken.PROCID_LIST, token));
		
		// check exist token before ')'
		CommandToken termToken = tokens.getToken();
		if (termToken == null) {
			// mismatched <EOT> expecting ')'
			needTypes(tokens, "')'", CommandToken.RPAREN);
		}
		else if (termToken.getType() == CommandToken.RPAREN) {
			String msg = String.format("Unexpected Identifier before '%s'.", termToken.getText());
			throw new RecognitionException(termToken, msg);
		}
		
		// identifier's array
		do {
			//--- identifier
			token = tokens.getToken();
			if (token == null || token.getType() == CommandToken.RPAREN)
				break;
			if (token.getType() == CommandToken.COMMA) {
				String msg = String.format("Unexpected Identifier before '%s'.", token.getText());
				throw new RecognitionException(token, msg);
			}
			else if (token.getType() != CommandToken.IDENTIFIER) {
				throw new MismatchedTokenException(tokens, "Identifier");
			}
			root.addChild(new CommandNode(token));
			tokens.consume();
			//--- delimiter
			token = tokens.getToken();
			if (token == null || token.getType() == CommandToken.RPAREN)
				break;
			if (token.getType() == CommandToken.IDENTIFIER) {
				String msg = String.format("Unexpected ',' before '%s'.", token.getText());
				throw new RecognitionException(token, msg);
			}
			else if (token.getType() != CommandToken.COMMA) {
				String msg = String.format("Unexpected ')' before '%s'.", token.getText());
				throw new RecognitionException(token, msg);
			}
			tokens.consume();
		} while(true);
		
		// ')'
		needTypes(tokens, "')'", CommandToken.RPAREN);
		
		// update
		root.updateProcessNameMap();
		
		return root;
	}

	// '[' conditionalOrExpression ']'
	private CommandNode parseConditionalExpression(CommandTokenizer tokens) throws RecognitionException
	{
		// '['
		CommandToken token = needTypes(tokens, "'['", CommandToken.LBRACKET);
		CommandNode root = new CommandConditionNode(new CommandToken(CommandToken.COND_EXP, token));
		
		// check exist token before ']'
		CommandToken termToken = tokens.getToken();
		if (termToken == null) {
			// mismatched <EOT> expecting ']'
			needTypes(tokens, "']'", CommandToken.RBRACKET);
		} else if (termToken.getType() == CommandToken.RBRACKET) {
			String msg = String.format("No expression that returns Boolean type before '%s'.", termToken.getText());
			throw new RecognitionException(termToken, msg);
		}
		
		// 条件式('||')
		CommandNode child = parseConditionalOrExpression(tokens);
		root.addChild(child);

		// ']'
		termToken = needTypes(tokens, "']'", CommandToken.RBRACKET);
		checkBooleanExpression(termToken, child);

		return root;
	}

	// '[' ('==' | '!=' | '<' | '>' | '<=' | '>=') ('+'|'-')? integerLiteral ']' 
	private CommandNode parseTermConditionalExpression(CommandTokenizer tokens) throws RecognitionException
	{
		// '['
		CommandToken token = needTypes(tokens, "'['", CommandToken.LBRACKET);
		CommandNode root = new CommandTermConditionNode(new CommandToken(CommandToken.TERM_EXP, token));
		
		// ('==' | '!=' | '<' | '>' | '<=' | '>=')
		token = needTypes(tokens, "'==','!=','<','>','<=','>='",
							CommandToken.EQUAL, CommandToken.NOTEQUAL,
							CommandToken.LESS, CommandToken.LESSEQUAL,
							CommandToken.GREATER, CommandToken.GREATEREQUAL);
		CommandNode mark = new CommandNode(token);
		root.addChild(mark);
		
		
		
		// ('+'|'-')? NUMBER
		CommandNode integer = parseIntegerLiteral(tokens);
		mark.addChild(integer);

		// ']'
		needTypes(tokens, "']'", CommandToken.RBRACKET);
		
		return root;
	}

	// ( conditionalAndExpression ('||' conditionalAndExpression)* )
	private CommandNode parseConditionalOrExpression(CommandTokenizer tokens) throws RecognitionException
	{
		// 条件式('&&')
		CommandNode node = parseConditionalAndExpression(tokens);

		// 条件式('||')
		CommandNode root = null;
		do {
			// '||'
			CommandToken token = tokens.getToken();
			if (token == null || token.getType() != CommandToken.OR)
				break;
			if (root == null) {
				checkOperandBoolean(token, node);
				root = new CommandNode(token);
				root.addChild(node);
			}
			tokens.consume();
			
			// 条件式('&&')
			node = parseConditionalAndExpression(tokens);
			checkOperandBoolean(token, node);
			root.addChild(node);
		} while(true);
		
		if (root != null)
			return root;
		else
			return node;
	}

	// ( comparableExpression ('&&' comparableExpression)* )
	private CommandNode parseConditionalAndExpression(CommandTokenizer tokens) throws RecognitionException
	{
		// 条件式 ('==' | '!=')
		CommandNode node = parseEqualityExpression(tokens);
		
		// 条件式('&&')
		CommandNode root = null;
		do {
			// '&&'
			CommandToken token = tokens.getToken();
			if (token == null || token.getType() != CommandToken.AND)
				break;
			if (root == null) {
				checkOperandBoolean(token, node);
				root = new CommandNode(token);
				root.addChild(node);
			}
			tokens.consume();
			
			// 条件式 ('==' | '!=')
			node = parseEqualityExpression(tokens);
			checkOperandBoolean(token, node);
			root.addChild(node);
		} while(true);
		
		if (root != null)
			return root;
		else
			return node;
	}

	// ( comparativeExpression (('==' | '!=') comparativeExpression)? )
	private CommandNode parseEqualityExpression(CommandTokenizer tokens) throws RecognitionException
	{
		// 条件式('<' | '>' | '<=' | '>=')
		CommandNode node = parseComparativeExpression(tokens);
		
		// 条件式('==' | '!=')
		CommandNode root = null;
		do {
			// '==' | '!='
			CommandToken token = tokens.getToken();
			if (token == null || (token.getType() != CommandToken.EQUAL && token.getType() != CommandToken.NOTEQUAL))
				break;
			if (root == null) {
				checkOperandInteger(token, node);
				root = new CommandNode(token);
				root.addChild(node);
			}
			tokens.consume();
			
			// 条件式('<' | '>' | '<=' | '>=')
			node = parseComparativeExpression(tokens);
			checkOperandInteger(token, node);
			root.addChild(node);
		} while(false);
		
		if (root != null)
			return root;
		else
			return node;
	}

	// (primary (('<' | '>' | '<=' | '>=') primary)
	private CommandNode parseComparativeExpression(CommandTokenizer tokens) throws RecognitionException
	{
		// primary
		CommandNode node = parsePrimary(tokens);
		
		// 条件式('<' | '>' | '<=' | '>=')
		CommandNode root = null;
		do {
			// '<' | '>' | '<=' | '>='
			CommandToken token = tokens.getToken();
			if (token == null)
				break;
			else if (token.getType() != CommandToken.LESSEQUAL &&
					token.getType() != CommandToken.LESS &&
					token.getType() != CommandToken.GREATEREQUAL &&
					token.getType() != CommandToken.GREATER)
				break;
			if (root == null) {
				checkOperandInteger(token, node);
				root = new CommandNode(token);
				root.addChild(node);
			}
			tokens.consume();
			
			// primary
			node = parsePrimary(tokens);
			checkOperandInteger(token, node);
			root.addChild(node);
		} while(false);
		
		if (root != null)
			return root;
		else
			return node;
	}

	// ('(' conditionalOrExpression ')') | IDENTIFIER | integerLietral
	private CommandNode parsePrimary(CommandTokenizer tokens) throws RecognitionException
	{
		CommandToken token = tokens.getToken();
		if (token != null) {
			if (token.getType() == CommandToken.LPAREN) {
				tokens.consume();
				//--- conditionalOrExpression
				CommandNode root = parseConditionalOrExpression(tokens);
				//--- ')'
				needTypes(tokens, "')'", CommandToken.RPAREN);
				//--- completed
				return root;
			}
			else if (token.getType() == CommandToken.IDENTIFIER) {
				CommandNode root = new CommandNode(token);
				tokens.consume();
				return root;
			}
			else if (token.getType()==CommandToken.NUMBER || token.getType()==CommandToken.MINUS || token.getType()==CommandToken.PLUS) {
				CommandNode root = parseIntegerLiteral(tokens);
				return root;
			}
			// エラー
			throw new UndefinedTokenException(token);
		}
		
		// エラー
		throw new RecognitionException(tokens, EOT_ERROR_MSG);
	}

	// ('+'|'-')? NUMBER
	private CommandNode parseIntegerLiteral(CommandTokenizer tokens) throws RecognitionException
	{
		CommandNode root = null;
		
		// ('+'|'-')?
		CommandToken token = tokens.getToken();
		if (token != null && (token.getType()==CommandToken.MINUS || token.getType()==CommandToken.PLUS)) {
			root = new CommandNode(token);
			tokens.consume();
		}
		
		// NUMBER
		token = needTypes(tokens, "Integer value", CommandToken.NUMBER);
		//--- check integer range
		try {
			Integer.valueOf(token.getText());
		} catch (NumberFormatException ex) {
			throw new MismatchedTokenException(token, "between 0 and " + Integer.MAX_VALUE);
		}
		//--- append
		CommandNode number = new CommandNode(token);
		if (root != null)
			root.addChild(number);
		else
			root = number;
		
		return root;
	}

	/**
	 * トークナイザーの現在のトークンが、<em>tokenTypes</em> に指定された候補のどれか一つと一致するかどうかを判定する。
	 * 指定された候補のどれか一つと一致した場合、トークナイザーのトークン位置を次へ一つ進める。
	 * @param tokens		トークナイザー
	 * @param tokenTypes	判定するトークン候補の配列
	 * @return	一致した場合は現在のトークン、それ以外の場合は <tt>null</tt>
	 */
	private CommandToken allowOmittionTypes(CommandTokenizer tokens, int...tokenTypes) {
		CommandToken token = tokens.getToken();
		if (token != null) {
			int type = token.getType();
			for (int needType : tokenTypes) {
				if (needType == type) {
					tokens.consume();
					return token;	// valid token
				}
			}
		}
		// omitted
		return null;
	}

	/**
	 * トークナイザーの現在のトークンが、<em>tokenTypes</em> に指定された候補のどれか一つと一致しない場合に例外をスローする。
	 * 指定された候補のどれか一つと一致した場合、トークナイザーのトークン位置を次へ一つ進める。
	 * @param tokens		トークナイザー
	 * @param expectings	必要なトークンを示す文字列(例外に代入される)
	 * @param tokenTypes	判定するトークン候補の配列
	 * @return	一致した現在のトークン
	 * @throws RecognitionException	トークン候補に一致しなかった場合
	 */
	private CommandToken needTypes(CommandTokenizer tokens, String expectings, int...tokenTypes) throws RecognitionException
	{
		CommandToken token = allowOmittionTypes(tokens, tokenTypes);
		if (token == null) {
			throw new MismatchedTokenException(tokens, expectings);
		}
		return token;
	}
	
	// expToken の引数が真偽値であるかをチェック
	private void checkOperandBoolean(CommandToken expToken, CommandNode op) throws RecognitionException
	{
		if (op != null) {
			CommandToken token = op.getToken();
			int tokenType = token.getType();
			switch (tokenType) {
				case CommandToken.AND :
				case CommandToken.OR :
				case CommandToken.NOT :
				case CommandToken.EQUAL :
				case CommandToken.NOTEQUAL :
				case CommandToken.GREATER :
				case CommandToken.GREATEREQUAL :
				case CommandToken.LESS :
				case CommandToken.LESSEQUAL :
					break;
				default :
				{
					String msg = String.format("Operand is not Boolean type for '%s' operator.", expToken.getText());
					throw new RecognitionException(token, msg);
				}
			}
		}
	}
	
	// expToken の引数が数値であるかをチェック
	private void checkOperandInteger(CommandToken expToken, CommandNode op) throws RecognitionException
	{
		if (op != null) {
			CommandToken token = op.getToken();
			int tokenType = token.getType();
			switch (tokenType) {
				case CommandToken.PLUS :
				case CommandToken.MINUS :
				case CommandToken.NUMBER :
				case CommandToken.IDENTIFIER :
					break;
				default :
				{
					String msg = String.format("Operand is not Integer type for '%s' operator.", expToken.getText());
					throw new RecognitionException(token, msg);
				}
			}
		}
	}

	// termToken の前が Boolean 評価式であるかをチェック
	private void checkBooleanExpression(CommandToken termToken, CommandNode node) throws RecognitionException
	{
		CommandToken token = node.getToken();
		int tokenType = token.getType();
		
		switch (tokenType) {
			case CommandToken.AND :
			case CommandToken.OR :
			case CommandToken.NOT :
			case CommandToken.EQUAL :
			case CommandToken.NOTEQUAL :
			case CommandToken.GREATER :
			case CommandToken.GREATEREQUAL :
			case CommandToken.LESS :
			case CommandToken.LESSEQUAL :
				break;
			default :
			{
				String msg = String.format("No expression that returns Boolean type before '%s'.", termToken.getText());
				throw new RecognitionException(termToken, msg);
			}
		}
	}
}
