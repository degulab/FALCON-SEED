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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/**
 * AADL(Algebraic Accounting Description Language) Grammar
 *
 * AADL Parser prototype.
 *
 * @version 1.70 2011/05/25
 **/
grammar AADL;
options {
	language=Java;
	output = AST;
	ASTLabelType = CommonTree;
}
//options {k=2; backtrack=true; memoize=true;}

tokens {
	AADL_INFO; AADL_NAME; AADL_ARGS; AADL_FUNCS; AADL_PROGRAM; AADL_CONSTS;
	FCALL_RM; FCALL_SM; FUNCALL_PATH; FUNCALL_ARGS;
	FCALL_IM; FCALL_MM;
	FUNCDEF_RETURN; FUNCDEF_PARAMS;
	IFCOND;
	BLOCK; BODY;
	JAVACLASSPATH;
	VARTYPE; VARDEF; VARINIT; VARNEW;
	ITERATE; ALIAS;
	INVOLV; VOIDINVOLV; INVOLV_EXP; INVOLV_COND; INVOLV_BLOCK;
	EXBASE; DTBASE;
	ARRAY; HASH; AINDEX; FOPTION;
	CAST; INSTANCEOF; MODIFIERS;
}

@header {
package ssac.aadlc.core;

import java.util.ArrayList;
import java.util.Iterator;

import ssac.aadlc.io.ReportPrinter;
}

@lexer::header {
package ssac.aadlc.core;
/**
 * AADL(Algebraic Accounting Description Language) Grammar
 *
 * AADL Lexer prototype.
 *
 * @version 1.70 2011/05/25
 **/
import ssac.aadlc.io.ReportPrinter;
}

@members {
//-- begin [@members]
private boolean hasErrorOccured = false;

private String aadlClassName = "";
private ArrayList<CommonTree> cmdArgs = new ArrayList<CommonTree>();
private ArrayList<CommonTree> funcDefs = new ArrayList<CommonTree>();
private ArrayList<CommonTree> constDefs = new ArrayList<CommonTree>();

private void setClassName(String name) {
	aadlClassName = name;
}

private void addCommandArg(Token astToken) {
	cmdArgs.add(new CommonTree(astToken));
}

private void addFuncDef(CommonTree astTree) {
	funcDefs.add(astTree);
}

private void addConstDef(CommonTree astTree) {
	constDefs.add(astTree);
}

private String getClassName() {
	return aadlClassName;
}

private CommonTree getTreeCommandArgs() {
	CommonTree retTree = new CommonTree(new CommonToken(AADL_ARGS,"AADL_ARGS"));
	Iterator<CommonTree> it = cmdArgs.iterator();
	while (it.hasNext()) {
		retTree.addChild(it.next());
	}
	return retTree;
}

private CommonTree getTreeFuncDefs() {
	CommonTree retTree = new CommonTree(new CommonToken(AADL_FUNCS,"AADL_FUNCS"));
	Iterator<CommonTree> it = funcDefs.iterator();
	while (it.hasNext()) {
		retTree.addChild(it.next());
	}
	return retTree;
}

private CommonTree getTreeConstDefs() {
	CommonTree retTree = new CommonTree(new CommonToken(AADL_CONSTS,"AADL_CONSTS"));
	Iterator<CommonTree> it = constDefs.iterator();
	while (it.hasNext()) {
		retTree.addChild(it.next());
	}
	return retTree;
}

// Error check
public String getErrorHeader(RecognitionException e) {
	return sourceFileName + ":" + e.line + ":" + e.charPositionInLine;
	//return "line "+e.line+":"+e.charPositionInLine;
}

public void displayRecognitionError(String[] tokenNames,
                                     RecognitionException e)
{
	String hdr = getErrorHeader(e);
	String msg = getErrorMessage(e, tokenNames);
	//emitErrorMessage(hdr+" "+msg);
	emitErrorMessage(hdr+":"+msg);
}

public String getErrorMessage(RecognitionException e, String[] tokenNames) {
	hasErrorOccured = true;
	return (super.getErrorMessage(e, tokenNames));
}

public boolean hasError() {
	return hasErrorOccured;
}

// Print
private String sourceFileName = "";
private ReportPrinter outWriter = null;
private ReportPrinter errWriter = null;

public void setSourceFileName(String name) {
	if (name != null)
		sourceFileName = name;
	else
		sourceFileName = "";
}

public ReportPrinter getOutputWriter() {
	return outWriter;
}

public void setOutputWriter(ReportPrinter printer) {
	outWriter = printer;
}

public ReportPrinter getErrorWriter() {
	return errWriter;
}

public void setErrorWriter(ReportPrinter printer) {
	errWriter = printer;
}

// print message for Debug
public void emitDebugMessage(String msg) {
	//emitMessage("[Debug] " + msg);
}

// print message
public void emitMessage(String msg) {
	if (outWriter != null)
		outWriter.println(msg);
	else
		System.out.println(msg);
}

// override error report
public void emitErrorMessage(String msg) {
	if (errWriter != null)
		errWriter.println(msg);
	else
		System.err.println(msg);
}

public void recoverFromMismatchedToken(IntStream input,
                                       RecognitionException e,
                                       int ttype,
                                       BitSet follow)
	throws RecognitionException
{
	// (Forgetting erasing?) System.err.println("BR.recoverFromMismatchedToken");
	// if next token is what we are looking for then "delete" this token
	if ( input.LA(2)==ttype ) {
		reportError(e);
		/*
		System.err.println("recoverFromMismatchedToken deleting "+input.LT(1)+
		" since "+input.LT(2)+" is what we want");
		*/
		beginResync();
		input.consume(); // simply delete extra token
		endResync();
		input.consume(); // move past ttype token as if all were ok
		return;
	}
	if ( !recoverFromMismatchedElement(input,e,follow) ) {
		throw e;
	}
}
//-- end [@members]
}

@lexer::members {
//--- begin [@members]
private boolean hasErrorOccured = false;

// Error check
public String getErrorHeader(RecognitionException e) {
	return sourceFileName + ":" + e.line + ":" + e.charPositionInLine;
	//return "line "+e.line+":"+e.charPositionInLine;
}

public void displayRecognitionError(String[] tokenNames,
                                     RecognitionException e)
{
	String hdr = getErrorHeader(e);
	String msg = getErrorMessage(e, tokenNames);
	//emitErrorMessage(hdr+" "+msg);
	emitErrorMessage(hdr+":"+msg);
}

public String getErrorMessage(RecognitionException e, String[] tokenNames){
	hasErrorOccured = true;
	return (super.getErrorMessage(e, tokenNames));
}

public boolean hasError() {
	return hasErrorOccured;
}

// Print
private String sourceFileName = "";
private ReportPrinter outWriter = null;
private ReportPrinter errWriter = null;

public void setSourceFileName(String name) {
	if (name != null)
		sourceFileName = name;
	else
		sourceFileName = "";
}

public ReportPrinter getOutputWriter() {
	return outWriter;
}

public void setOutputWriter(ReportPrinter printer) {
	outWriter = printer;
}

public ReportPrinter getErrorWriter() {
	return errWriter;
}

public void setErrorWriter(ReportPrinter printer) {
	errWriter = printer;
}

// print message for Debug
public void emitDebugMessage(String msg) {
	//emitMessage("[Debug] " + msg);
}

// print message
public void emitMessage(String msg) {
	if (outWriter != null)
		outWriter.println(msg);
	else
		System.out.println(msg);
}

// override error report
public void emitErrorMessage(String msg) {
	if (errWriter != null)
		errWriter.println(msg);
	else
		System.err.println(msg);
}

public void recoverFromMismatchedToken(IntStream input,
                                       RecognitionException e,
                                       int ttype,
                                       BitSet follow)
	throws RecognitionException
{
	// (Forgetting erasing?) System.err.println("BR.recoverFromMismatchedToken");
	// if next token is what we are looking for then "delete" this token
	if ( input.LA(2)==ttype ) {
		reportError(e);
		/*
		System.err.println("recoverFromMismatchedToken deleting "+input.LT(1)+
		" since "+input.LT(2)+" is what we want");
		*/
		beginResync();
		input.consume(); // simply delete extra token
		endResync();
		input.consume(); // move past ttype token as if all were ok
		return;
	}
	if ( !recoverFromMismatchedElement(input,e,follow) ) {
		throw e;
	}
}
//--- end [@members]
}


//@@@ for Debug
test
    :	program
    	{if ($program.tree != null) System.out.println($program.tree.toStringTree());}
    ;
//@@@ end of Debug.

program
    :	packageDeclaration?
    	JAVA_HEADER_ACTION*
    	(sm1=subModule)?
    	mainModule
    	(sm2=subModule)?
    	EOF
    		-> ^(AADL_INFO["AADL_INFO"] AADL_NAME[$mainModule.start, getClassName()] {getTreeCommandArgs()} {getTreeFuncDefs()})
    		   ^(AADL_PROGRAM[$mainModule.start, getClassName()] packageDeclaration? JAVA_HEADER_ACTION* {getTreeConstDefs()} $sm1? $sm2? mainModule)
    ;

packageDeclaration
    :	ID_PACKAGE variableTypeName ';'	-> ^(ID_PACKAGE variableTypeName)
    ;

constDeclaration
    :	constVariableDeclaration ';'! {addConstDef($constVariableDeclaration.tree);}
    ;

subModule
    :	(JAVA_ACTION|function|constDeclaration!)+
    ;

// main module

mainModule
    :	PROGRAM Identifier { setClassName($Identifier.text); } mainBody
    		-> ^(PROGRAM Identifier mainBody)
    ;

mainBody
    :	lc='{' statement* '}'	-> ^(BODY[$lc, "BODY"] statement*)
    ;

// functions
//--- function Identifier(...)[:type] -> normal function
//--- function Identifier[...](...)[:type] -> ExBaseSet args function

//@@@ modified by Y.Ishizuka : 2011.05.25
function
    :	modifier* FUNCTION functionDeclaration functionBody
    	{addFuncDef($functionDeclaration.tree);}
    		-> ^(FUNCTION ^(MODIFIERS modifier*) functionDeclaration functionBody)
    ;
/*--- old codes : 2011.05.25 ---**
function
    :	FUNCTION functionDeclaration functionBody
    	{addFuncDef($functionDeclaration.tree);}
    		-> ^(FUNCTION functionDeclaration functionBody)
    ;
/*--- end of old codes : 2011.05.25 ---*/
//@@@ end of modified : 2011.05.25

functionDeclaration
    :	Identifier  '(' functionFormalParameters? ')' functionReturnDeclaration?
    		-> ^(Identifier functionReturnDeclaration? functionFormalParameters?)
    ;

functionReturnDeclaration
    :	fr=':' type
    		-> ^(FUNCDEF_RETURN[$fr, "FUNCDEF_RETURN"] type)
    ;

functionFormalParameters
    :	variableDefinition (',' variableDefinition)*
    		-> ^(FUNCDEF_PARAMS variableDefinition+)
    ;

functionBody
    :	lc='{' statement* '}'	-> ^(BODY[$lc, "BODY"] statement*)
    ;

// Statement / Block

block
    :	lc='{' expression (
    		vl='|' involvingCondition '}'
			-> ^(VOIDINVOLV[$lc,"VOIDINVOLV"] ^(INVOLV_COND[$vl,"INVOLV_COND"] involvingCondition) expression)
    	    |	';' statement* '}'
			-> ^(BLOCK[$lc, "BODY"] expression statement*)
    	)
    |	lc='{' noexpStatement statement* '}'
			-> ^(BLOCK[$lc, "BODY"] noexpStatement statement*)
    |	lc='{' '}'
			-> ^(BLOCK[$lc, "BODY"])
    ;

statement
    :	block
    |	if_statement
    |	vardef_statement
    |	assign_statement
    |	break_statement
    |	jump_statement
    |	exp_statement
    |	';'!
    ;

noexpStatement
    :	block
    |	if_statement
    |	vardef_statement
    |	assign_statement
    |	break_statement
    |	jump_statement
    |	';'!
    ;

singleStatement
    :	vardef_statement
    |	assign_statement
    |	break_statement
    |	jump_statement
    |	exp_statement
    |	';'!
    ;

vardef_statement
    :	localVariableDeclaration ';'!
    |	constVariableDeclaration ';'!
    ;

constVariableDeclaration
    :	CONST localVariableDeclarationRest (',' localVariableDeclarationRest)*
    		-> ^(CONST localVariableDeclarationRest)+
    ;

localVariableDeclaration
    :	'var'! localVariableDeclarationRest (','! localVariableDeclarationRest)*
    ;

localVariableDeclarationRest
    :	variableDefinition (
    		(eq='=' (
    			expression		-> ^(variableDefinition ^(VARINIT[$eq,"VARINIT"] expression))
    		    |	involvingExpression	-> ^(variableDefinition ^(VARINIT[$eq,"VARINIT"] involvingExpression))
    		) )
    	|	(FINPUT fileDeclarator)		-> ^(variableDefinition ^(FINPUT fileDeclarator))
    	|	lc='(' methodArguments? ')'	-> ^(variableDefinition ^(VARNEW[$lc,"VARNEW"] methodArguments?))
    	|					-> variableDefinition
    	)
    ;

exp_statement
    :	expression ';'!
    ;

assign_statement
    :	Identifier (
    		(eq='=' (
    			expression		-> ^(Identifier ^($eq expression))
    		    |	involvingExpression	-> ^(Identifier ^($eq involvingExpression))
    		) )
    	|	FINPUT fileDeclarator		-> ^(Identifier ^(FINPUT fileDeclarator))
    	|	FOUTPUT fileDeclarator		-> ^(Identifier ^(FOUTPUT fileDeclarator))
    	) ';'
    ;

break_statement
    :	BREAK^ ';'!
    ;

jump_statement
    :	RETURN^ expression? ';'!
    ;

if_statement
    :	SIF '(' expression ')'
    (	if_statement			-> ^(SIF expression if_statement)
    |	block else_statement?		-> ^(SIF expression block else_statement?)
    |	singleStatement else_statement?	-> ^(SIF expression singleStatement else_statement?)
    )
    ;

else_statement
    :	SELSE^
    (	block
    |	if_statement
    |	singleStatement
    )
    ;


// Expressions

involvingExpression
    :	lc='{' expression vl='|' involvingCondition '}'
    		-> ^(INVOLV[$lc,"INVOLV"] ^(INVOLV_COND[$vl,"INVOLV_COND"] involvingCondition) expression)
/*
    :	Identifier? lc='{' expression vl='|' involvingCondition '}'
    		-> ^(INVOLV[$lc,"INVOLV"] ^(INVOLV_COND[$vl,"INVOLV_COND"] involvingCondition) expression Identifier?)
*/
    ;

involvingCondition
    :	filterExpression ( ','! filterExpression )*
    ;

filterExpression
    :	listExpression | aliasExpression | conditionalOrExpression | involvingBlock
    ;

listExpression
    :	vn=Identifier it='<-'
    	    (
    	    	fileDeclarator		-> ^(ITERATE[$it,"ITERATE"] $vn fileDeclarator)
    	    |	cn=Identifier		-> ^(ITERATE[$it,"ITERATE"] $vn $cn)
    	    |	arrayLiteral		-> ^(ITERATE[$it,"ITERATE"] $vn arrayLiteral)
    	    |	methodCall		-> ^(ITERATE[$it,"ITERATE"] $vn methodCall)
    	    //|	involvingExpression	-> ^(ITERATE[$it,"ITERATE"] $vn involvingExpression)
    	    )
    ;

involvingBlock
    :	lc='{' statement* '}'	-> ^(INVOLV_BLOCK[$lc, "INVOLV_BLOCK"] statement*)
    ;

aliasExpression
    :	Identifier (':' type)? op='=' conditionalOrExpression	-> ^(ALIAS[$op,"ALIAS"] Identifier type? conditionalOrExpression)
    ;

expression
    :	conditionalOrExpression
//    :	conditionalExpression
    ;

/*
conditionalExpression
    :	conditionalOrExpression ( '?'^ expression ':'! expression )?
    ;
*/

conditionalOrExpression
    :	conditionalAndExpression ( '||'^ conditionalAndExpression )*
    ;

conditionalAndExpression
    :	equalityExpression ( '&&'^ equalityExpression )*
    ;

equalityExpression
    :	comparativeExpression ( ('==' | '!=')^ comparativeExpression )*
    ;

comparativeExpression
    :	namedOperationExpression ( ('<=' | '>=' | '<' | '>')^ namedOperationExpression )*
    ;

namedOperationExpression
    :	additiveExpression ( NamedOperator^ additiveExpression )*
    ;

additiveExpression
    :	multiplicativeExpression ( ('+' | '-')^ multiplicativeExpression )*
    ;

multiplicativeExpression
    :	simpleExpression ( ( '*' | '/' | '%' | '@' )^ simpleExpression )*
    ;

simpleExpression
    :	'+'^ simpleExpression
    |	'-'^ simpleExpression
    |	simpleExpressionNotPlusMinus
    ;

simpleExpressionNotPlusMinus
    :	'~'^ simpleExpression
    |	'!'^ simpleExpression
    |	'^'^ simpleExpression
    |	postfixExpression
    ;

postfixExpression
    :	(primary -> primary) (
    		lc='.' mn=Identifier ma=callInstanceMethodArguments
    			-> ^(FCALL_IM[$lc,"FCALL_IM"] ^(FUNCALL_PATH $postfixExpression) $mn $ma)
    	)*
    ;

callInstanceMethodArguments
    :	'(' methodArguments? ')'	-> ^(FUNCALL_ARGS methodArguments?)
    ;

// Primary

primary
    :	'('! expression ')'! //-> expression
    |	arrayLiteral
    |	HexLiteral
    |	OctalLiteral
    |	IntegerLiteral
    |	FloatingPointLiteral
    |	nullLiteral
    |	booleanLiteral
    |	CharacterLiteral
    |	StringLiteral
    |	exbase
    |	dtbase
    |	methodCall
    |	JAVA_ACTION
    |	Identifier
    |	commandArg
    ;

/*@@@ delete by Y.Ishizuka : 2009/09/13
selector
    :	'::'^ (Identifier | callRegisteredMethod)
    ;
**@@@ end of deleted. */

methodCall
    :	callSystemMethod
//    |	callDirectCastMethod
    |	callRegisteredMethod
    |	callSpecialMethod
    |	callModuleMethod
    ;

//@@@ added by Y.Ishizuka : 2011.05.25
callSystemMethod
    :	csf='cast' '[' type ']' '(' expression ')'
    		-> ^(CAST[$csf] type expression)
    |	isf='typeof' '[' type ']' '(' expression ')'
    		-> ^(INSTANCEOF[$isf] type expression)
    ;
//@@@ end of added : 2011.05.25

/*@@@ deleted by Y.Ishizuka : 2011.05.25
callDirectCastMethod
    :	cs='cast' '[' type ']' '(' expression ')'
    		-> ^($cs type expression)
    ;
**@@@ end of deleted : 2011.05.25 */

callRegisteredMethod
    :	Identifier '(' methodArguments? ')'
    		-> ^(FCALL_RM Identifier ^(FUNCALL_ARGS methodArguments?))
    ;

callSpecialMethod
    :	Identifier '[' methodArguments? ']' '(' expression ')'
    		-> ^(FCALL_SM Identifier ^(FUNCALL_PATH expression) ^(FUNCALL_ARGS methodArguments?))
    ;

callModuleMethod
    :	variableTypeName '::' Identifier '(' methodArguments ')'
    		-> ^(FCALL_MM ^(FUNCALL_PATH variableTypeName) Identifier ^(FUNCALL_ARGS methodArguments?))
    ;

methodArguments
    :	expression (','! expression)*
    ;

// Types

fileDeclarator
    :	txtFileDeclarator^ | csvFileDeclarator^ | xmlFileDeclarator^
    ;

txtFileDeclarator
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    :	'txtFile'^ '('! (expression) (','! expression)? ')'!
**--- end of old codes : 2010.07.12 ---*/
    :	'txtFile'^ fileDeclaratorOptions? '('! (expression) (','! expression)? ')'!
//@@@ end of modified : 2010.07.12
    ;

csvFileDeclarator
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    :	'csvFile'^ '('! (expression) (','! expression)? ')'!
**--- end of old codes : 2010.07.12 ---*/
    :	'csvFile'^ fileDeclaratorOptions? '('! (expression) (','! expression)? ')'!
//@@@ end of modified : 2010.07.12
    ;

xmlFileDeclarator
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    :	'xmlFile'^ '('! (expression) ')'!
**--- end of old codes : 2010.07.12 ---*/
    :	'xmlFile'^ fileDeclaratorOptions? '('! (expression) ')'!
//@@@ end of modified : 2010.07.12
    ;

//@@@ added by Y.Ishizuka : 2010.07.12
fileDeclaratorOptions
    :	'[' ( fileDeclaratorOptionEntry (',' fileDeclaratorOptionEntry)* (',')? )? ']'
    		-> fileDeclaratorOptionEntry*
    ;

fileDeclaratorOptionEntry
    :	Identifier eq='=' expression?
    		-> ^(FOPTION[$eq,"FOPTION"] Identifier expression?)
    ;
//@@@ end of added : 2010.07.12

variableDefinition
    :	Identifier ':' type
		-> ^(VARDEF[$Identifier,"VARDEF"] Identifier type)
    ;

variableTypeName
    :	Identifier ('.' Identifier)+	-> ^(JAVACLASSPATH Identifier+)
    |	Identifier			-> Identifier
    ;

type
    :	variableTypeName	-> ^(VARTYPE variableTypeName)
    ;

// Literal

arrayLiteral
    :	'[' arrayParameter (',' arrayParameter)* (',')? ']'
    		-> ^(ARRAY arrayParameter+)
    ;

arrayParameter
    :	hk=expression (
    		':' hv=expression	-> ^(HASH $hk $hv)
    	|				-> $hk
    	)
    ;

booleanLiteral
    :	'true'
    |	'false'
    ;

nullLiteral
    :	'null'
    ;

exbase
    :	eb='<'  namedOperationExpression (',' namedOperationExpression)* '>'
    		-> ^(EXBASE[$eb,"EXBASE"] namedOperationExpression+)
    ;

dtbase
    :	db='<<' namedOperationExpression (',' namedOperationExpression)* '>>'
		-> ^(DTBASE[$db,"DTBASE"] namedOperationExpression+)
    ;

commandArg
    :	CMDARG
    	{addCommandArg($CMDARG);}
    ;

//@@@ added by Y.Ishizuka : 2011.05.25
modifier
    :	'public'
    ;
//@@@ end of added : 2011.05.25

/*
** LEXER
*/

SIF		: 'if' ;

SELSE		: 'else' ;

CMDARG		: '$' (Letter|Digit)+ ;

//SEMI		: ';' ;

COLON		: ':' ;

PLUS		: '+' ;

MINUS		: '-' ;

STAR		: '*' ;

SLASH		: '/' ;

SHARP		: '#' ;

VBAR		: '|' ;

AMPER		: '&' ;

LESS		: '<' ;

GREATER		: '>' ;

ASSIGN		: '=' ;

PERCENT		: '%' ;

NOT		: '!' ;

HAT		: '^' ;

BAR		: '~' ;

EQUAL		: '==' ;

NOTEQUAL	: '!=' ;

LESSEQUAL	: '<=' ;

GREATEREQUAL	: '>=' ;

DOUBLESTAR	: '**' ;

ITERATOR	: '<-' ;

FINPUT		: '<<-' ;

FOUTPUT		: '->>' ;

SCOPE		: '::' ;

FUNCTION	: 'function' ;

PROGRAM		: 'program' ;

RETURN		: 'return' ;

BREAK		: 'break' ;

ID_PACKAGE	: 'package' ;

CONST		: 'const' ;

fragment
BaseKeyLetters
    :	'"' (~InvalidBaseKeyLetter)+ '"'
//@@@ deleted by Y.Ishizuka : 2010.07.12
//    |	'\'' (~InvalidBaseKeyLetter)+ '\''
//@@@ end of deleted : 2010.07.12
    ;

fragment
InvalidBaseKeyLetter
    :	('<'|'>'|'-'|'+'|'/'|'^'|','|'~'|'%'|'&'|'?'|'|'|'@'|'\''|'"'|WHITESPACE)
    ;

//@@@ added by Y.Ishizuka : 2010.07.12
HexLiteral
    :	'0' ('x'|'X') HexDigit+ IntegerTypeSuffix?
    ;
//@@@ end of added : 2010.07.12

IntegerLiteral
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    :	('0' | '1'..'9' Digit*)
**--- end of old codes : 2010.07.12 ---*/
    :	('0' | '1'..'9' Digit*) IntegerTypeSuffix?
//@@@ end of modified : 2010.07.12
    ;

//@@@ added by Y.Ishizuka : 2010.07.12
OctalLiteral
    :	'0' ('0'..'7')+ IntegerTypeSuffix?
    ;
//@@@ end of added : 2010.07.12

//@@@ added by Y.Ishizuka : 2010.07.12
fragment
IntegerTypeSuffix
    :	('i' | 'I' | 'l' | 'L')
    ;
//@@@ end of added : 2010.07.12

FloatingPointLiteral
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    :   '.' Digit+ (Exponent)?
    |	('0' | '1'..'9' Digit*) ('.' (Digit+ (Exponent)?)? | Exponent)
**--- end of old codes : 2010.07.12 ---*/
    :	'.' Digit+ Exponent? FloatTypeSuffix?
//    |	('0' | '1'..'9' Digit*) ('.' Digit+ Exponent? FloatTypeSuffix? | Exponent FloatTypeSuffix? | FloatTypeSuffix)
    |	('0' | '1'..'9' Digit*) ('.' Digit* Exponent? FloatTypeSuffix? | Exponent FloatTypeSuffix? | FloatTypeSuffix)
//@@@ end of modified : 2010.07.12
    ;

fragment
Exponent
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    :	('e'|'E') ('+'|'-')? Digit+
**--- end of old codes : 2010.07.12 ---*/
    :	('e'|'E') ('+'|'-') Digit+
    |	('e'|'E') Digit+
    |	('e'|'E') ('+'|'-')?
//@@@ end of modified : 2010.07.12
    ;

//@@@ added by Y.Ishizuka : 2010.07.12
fragment
FloatTypeSuffix
    :	('f' | 'F' | 'd' | 'D')
    ;
//@@@ end of added : 2010.07.12

//@@@ added by Y.Ishizuka : 2010.07.12
CharacterLiteral
    :	'\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;
//@@@ end of added : 2010.07.12

StringLiteral
    :	'"' ( EscapeSequence | ~('\\'|'"') )* '"'
//@@@ deleted by Y.Ishizuka : 2010.07.12
//    |	'\'' ( EscapeSequence | ~('\''|'\\') )* '\''
//@@@ end of deleted : 2010.07.12
    ;

NamedOperator
//    :	('%' Letter (Letter|Digit)* '%')
    :	('%' ID '%')
    ;

Identifier
    :	ID
    ;

fragment
ID  :	Letter (Letter|Digit)*
    ;

fragment
EscapeSequence
    :	'\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |	UnicodeEscape
    |	OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
HexDigit
    :	('0'..'9'|'a'..'f'|'A'..'F')
    ;

fragment
Letter
    :  //'\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a'
    ;

fragment
Digit
    :	'0'..'9'
    ;

WS  
    :	(WHITESPACE) {$channel=HIDDEN;}
    ;

COMMENT
    :	'/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT
//    :	'//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    :	'//' ~('\n'|'\r')* {$channel=HIDDEN;}
    ;

JAVA_ACTION
    :	'@{'! ( options {greedy=false;} : .)* '}@'!
    ;

JAVA_HEADER_ACTION
    :	'@header{'! ( options {greedy=false;} : .)* '}@'!
    ;

fragment
WHITESPACE
    :	(' '|'\t'|'\r'|'\n')
    ;

fragment
NEWLINE
    :	('\r'? '\n')
    ;
