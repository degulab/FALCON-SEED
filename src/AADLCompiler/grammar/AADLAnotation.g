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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
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
 * @version 1.10 2008/05/22
 **/
grammar AADLAnotation;
options {
	language=Java;
	output = AST;
	ASTLabelType = CommonTree;
}
//options {k=2; backtrack=true; memoize=true;}

tokens {
	AADL_INFO; AADL_NAME; AADL_ARGS; AADL_FUNCS; AADL_PROGRAM;
	FCALL_RM; FCALL_SM; FUNCALL_PATH; FUNCALL_ARGS;
	FUNCDEF_RETURN; FUNCDEF_PARAMS;
	IFCOND;
	BLOCK; BODY;
	VARTYPE; VARDEF; VARINIT; VARNEW;
	ITERATE; ALIAS;
	INVOLV; INVOLV_EXP; INVOLV_COND;
	EXBASE;
	ARRAY; HASH;
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
 * @version 1.10 2008/05/22
 **/
import ssac.aadlc.io.ReportPrinter;
}

@members {
//-- begin [@members]
private boolean hasErrorOccured = false;

private String aadlClassName = "";
private ArrayList<CommonTree> cmdArgs = new ArrayList<CommonTree>();
private ArrayList<CommonTree> funcDefs = new ArrayList<CommonTree>();

private void setClassName(String name) {
	aadlClassName = name;
}

private void addCommandArg(Token astToken) {
	cmdArgs.add(new CommonTree(astToken));
}

private void addFuncDef(CommonTree astTree) {
	funcDefs.add(astTree);
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

program
    :	JAVA_HEADER_ACTION*
    	(JAVA_ACTION* moduleDeclaration)+
    	
//    	(sm1=subModule)?
//    	mainModule
//    	(sm2=subModule)?
//    		-> ^(AADL_INFO["AADL_INFO"] AADL_NAME[$mainModule.start, getClassName()] {getTreeCommandArgs()} {getTreeFuncDefs()})
//    		   ^(AADL_PROGRAM[$mainModule.start, getClassName()] JAVA_HEADER_ACTION* $sm1? mainModule $sm2?)
//    		-> {ctArgs} {getFunctionTypesTree()} ^(AADL_PROGRAM[$mainModule.start, aadlClassName] $sm1? mainModule $sm2?)
    ;

moduleDeclaration
    :	annotation? (function | mainModule)
    ;

// main module

mainModule
    :	PROGRAM Identifier { setClassName($Identifier.text); } mainBody
    		-> ^(PROGRAM Identifier mainBody)
    ;

mainBody
    :	lc='{' blockStatement* '}'	-> ^(BODY[$lc, "BODY"] blockStatement*)
    ;

// ANNOTATIONS

annotations
    :	annotation+
    ;

annotation
    :	'@' annotationName ('(' elementValuePairs? ')')?
    ;

annotationName
    :	Identifier ('.' Identifier)*
    ;

elementValuePairs
    :	elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    :	(Identifier '=')? elementValue
    ;

elementValue
    :	conditionalOrExpression
    |	annotation
    |	elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :	'{' (elementValue (',' elementValue)*)? '}'
    ;

// functions
//--- function Identifier(...)[:type] -> normal function
//--- function Identifier[...](...)[:type] -> ExBaseSet args function

function
    :	FUNCTION functionDeclaration functionBody
    	{addFuncDef($functionDeclaration.tree);}
    		-> ^(FUNCTION functionDeclaration functionBody)
    ;

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
    :	lc='{' blockStatement* '}'	-> ^(BODY[$lc, "BODY"] blockStatement*)
    ;

// Statement / Block

block
    :	lc='{' blockStatement* '}'	-> ^(BLOCK[$lc, "BODY"] blockStatement*)
    ;

/*
blockStatement
    :	localVariableDeclaration ';'!
    |	statement
    ;
*/
blockStatement
    :	statement
    ;

statement
    :	block
    |	if_statement
    |	vardef_statement
    |	assign_statement
    |	jump_statement
    |	exp_statement
    |	';'!
    ;

singleStatement
    :	vardef_statement
    |	assign_statement
    |	jump_statement
    |	exp_statement
    |	';'!
    ;

vardef_statement
    :	localVariableDeclaration ';'!
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

jump_statement
    :	'return'^ expression? ';'!
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
//    		-> ^(INVOLV[$lc,"INVOLV"] ^(INVOLV_COND[$vl,"INVOLV_COND"] involvingCondition) ^(INVOLV_EXP expression))
    ;

involvingCondition
    :	filterExpression ( ','! filterExpression )*
    ;

filterExpression
    :	listExpression | aliasExpression | conditionalOrExpression
    ;

listExpression
    :	vn=Identifier it='<-'
    	    (	cn=Identifier		-> ^(ITERATE[$it,"ITERATE"] $vn $cn)
    	    |	arrayLiteral		-> ^(ITERATE[$it,"ITERATE"] $vn arrayLiteral)
    	    //|	involvingExpression	-> ^(ITERATE[$it,"ITERATE"] $vn involvingExpression)
    	    )
    ;

//@@@ modified by Y.Ishizuka : 2008/05/21
aliasExpression
    :	Identifier op='=' conditionalOrExpression	-> ^(ALIAS[$op,"ALIAS"] Identifier conditionalOrExpression)
    ;
/*@@@ old codes
aliasExpression
    :	Identifier op='=' expression	-> ^(ALIAS[$op,"ALIAS"] Identifier expression)
    ;
**@@@ end of old codes. */
//@@@ end of modified.

expression
    :	conditionalOrExpression //conditionalExpression
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
    |	primary //selector*
    ;

// Primary

//@@@ modified by Y.Ishizuka : 2008/05/20
primary
    :	'('! expression ')'! //-> expression
    |	arrayLiteral
    |	IntegerLiteral
    |	FloatingPointLiteral
    |	booleanLiteral
    |	StringLiteral
    |	exbase
    |	methodCall
    |	JAVA_ACTION
    |	Identifier
    |	commandArg
    ;
/*@@@ old codes.
primary
    :	'('! expression ')'! //-> expression
    |	arrayLiteral
    |	decimalLiteral
    |	booleanLiteral
    |	StringLiteral
    |	exbase
    |	methodCall
    |	JAVA_ACTION
    |	Identifier
    |	commandArg
    ;
**@@@ end of old codes. */
//@@@ end of modified.

selector
    :	'::'^ (Identifier | callRegisteredMethod)
    ;

methodCall
    :	callRegisteredMethod
    |	callSpecialMethod
    ;

callRegisteredMethod
    :	Identifier '(' methodArguments? ')'
    		-> ^(FCALL_RM Identifier ^(FUNCALL_ARGS methodArguments?))
    ;

callSpecialMethod
    :	Identifier '[' methodArguments? ']' '(' expression ')'
    		-> ^(FCALL_SM Identifier ^(FUNCALL_PATH expression) ^(FUNCALL_ARGS methodArguments?))
    ;

methodArguments
    :	expression (','! expression)*
    ;

/*
exbaseArguments
    :	exbase (','! exbase)*
    ;
*/

/*
javaAction
    :	JAVA_ACTION
    ;
*/

// Types

fileDeclarator
    :	txtFileDeclarator^ | csvFileDeclarator^ | xmlFileDeclarator^
    ;

txtFileDeclarator
    :	'txtFile'^ '('! (expression) (','! expression)? ')'!
    ;

csvFileDeclarator
    :	'csvFile'^ '('! (expression) (','! expression)? ')'!
    ;

xmlFileDeclarator
    :	'xmlFile'^ '('! (expression) ')'!
    ;

/*--- modified 2008/05/16
txtFileDeclarator
    :	'txtFile'^ '('! (commandArg) (','! expression)? ')'!
    ;

csvFileDeclarator
    :	'csvFile'^ '('! (commandArg) (','! expression)? ')'!
    ;

xmlFileDeclarator
    :	'xmlFile'^ '('! (commandArg) ')'!
    ;
--- end of modified */

variableDefinition
    :	Identifier ':' type
		-> ^(VARDEF[$Identifier,"VARDEF"] Identifier type)
    ;

/*
packageOrTypeName
    :	Identifier ('.' Identifier)*
    ;

variableTypeName
    :	Identifier
    |	packageOrTypeName '.' Identifier
    ;
*/

variableTypeName
    :	Identifier
    |	JAVA_CLASSPATH
    ;

//@@@ modified by Y.Ishizuka : 2008/05/20
type
    :	variableTypeName	-> ^(VARTYPE variableTypeName)
    ;
/*@@@ old codes
type
    :	PrimitiveTypes		-> ^(VARTYPE PrimitiveTypes)
    |	variableTypeName	-> ^(VARTYPE variableTypeName)
    ;
**@@@ end of old codes. */
//@@@ end of modified.

// Literal

arrayLiteral
    :	'[' arrayParameter (',' arrayParameter)* (',')? ']'
    		-> ^(ARRAY arrayParameter+)
    ;

//@@@ modified by Y.Ishizuka : 2008/05/20
arrayParameter
    :	hk=expression (
    		':' hv=expression	-> ^(HASH $hk $hv)
    	|				-> $hk
    	)
    ;
/*@@@ old codes
arrayParameter
    :	hk=expression (
    		'=' hv=expression	-> ^(HASH $hk $hv)
    	|				-> $hk
    	)
    ;
**@@@ end of old codes. */
//@@@ end of modified.

booleanLiteral
    :	'true'
    |	'false'
    ;

exbase
    :	LESS namedOperationExpression (',' namedOperationExpression)* GREATER
    		-> ^(EXBASE[$LESS,"EXBASE"] namedOperationExpression+)
    ;

/*@@@ delete by Y.Ishizuka : 2008/05/20
decimalLiteral
    :	IntegerLiteral
    |	FloatingPointLiteral
    ;
**@@@ end of deleted. */

commandArg
    :	CMDARG
    	{addCommandArg($CMDARG);}
    ;

/*
** LEXER
*/

/*@@@ deleted by Y.Ishizuka : 2008/05/20
PrimitiveTypes
    :	'Exalge'
    |	'ExBase'
    |	'ExBasePattern'
    |	'ExAlgeSet'
    |	'ExBaseSet'
    |	'ExBasePatternSet'
    |	'TransTable'
    |	'TransMatrix'
    |	'Boolean'
    |	'BooleanList'
    |	'Decimal'
    |	'DecimalList'
    |	'String'
    |	'StringList'
    ;
**@@@ end of deleted. */

SIF		: 'if' ;

SELSE		: 'else' ;

//CMDARG		: '$' ('1'..'9') ;
CMDARG		: '$' (Letter|Digit)+ ;

SEMI		: ';' ;

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

JAVA_CLASSPATH
    :	ID ('.' ID)+
    ;

fragment
BaseKeyLetters
    :	'"' (~InvalidBaseKeyLetter)+ '"'
    |	'\'' (~InvalidBaseKeyLetter)+ '\''
    ;

fragment
InvalidBaseKeyLetter
    :	('<'|'>'|'-'|'+'|'/'|'^'|','|'~'|'%'|'&'|'?'|'|'|'@'|'\''|'"'|WHITESPACE)
    ;

FloatingPointLiteral
    :   '.' Digit+ (Exponent)?
    |	('0' | '1'..'9' Digit*) ('.' (Digit+ (Exponent)?)? | Exponent)
    ;

fragment
Exponent
    :	('e'|'E') ('+'|'-')? Digit+
    ;

IntegerLiteral
    :	('0' | '1'..'9' Digit*)
    ;

StringLiteral
    :	'"' ( EscapeSequence | ~('\\'|'"') )* '"'
    |	'\'' ( EscapeSequence | ~('\''|'\\') )* '\''
    ;

NamedOperator
//    :	('%' Letter (Letter|Digit)* '%')
    :	('%' ID '%')
    ;

/*---
DollarIdentifier
    :	'$' ID
    ;
---*/

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
