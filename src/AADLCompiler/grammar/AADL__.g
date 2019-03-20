lexer grammar AADL;
options {
  language=Java;

}
@members {
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
@header {
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

T102 : ';' ;
T103 : '{' ;
T104 : '}' ;
T105 : '(' ;
T106 : ')' ;
T107 : ',' ;
T108 : 'var' ;
T109 : '||' ;
T110 : '&&' ;
T111 : '@' ;
T112 : '.' ;
T113 : 'cast' ;
T114 : '[' ;
T115 : ']' ;
T116 : 'typeof' ;
T117 : 'txtFile' ;
T118 : 'csvFile' ;
T119 : 'xmlFile' ;
T120 : 'true' ;
T121 : 'false' ;
T122 : 'null' ;
T123 : '<<' ;
T124 : '>>' ;
T125 : 'public' ;

// $ANTLR src "AADL.g" 803
SIF		: 'if' ;

// $ANTLR src "AADL.g" 805
SELSE		: 'else' ;

// $ANTLR src "AADL.g" 807
CMDARG		: '$' (Letter|Digit)+ ;

//SEMI		: ';' ;

// $ANTLR src "AADL.g" 811
COLON		: ':' ;

// $ANTLR src "AADL.g" 813
PLUS		: '+' ;

// $ANTLR src "AADL.g" 815
MINUS		: '-' ;

// $ANTLR src "AADL.g" 817
STAR		: '*' ;

// $ANTLR src "AADL.g" 819
SLASH		: '/' ;

// $ANTLR src "AADL.g" 821
SHARP		: '#' ;

// $ANTLR src "AADL.g" 823
VBAR		: '|' ;

// $ANTLR src "AADL.g" 825
AMPER		: '&' ;

// $ANTLR src "AADL.g" 827
LESS		: '<' ;

// $ANTLR src "AADL.g" 829
GREATER		: '>' ;

// $ANTLR src "AADL.g" 831
ASSIGN		: '=' ;

// $ANTLR src "AADL.g" 833
PERCENT		: '%' ;

// $ANTLR src "AADL.g" 835
NOT		: '!' ;

// $ANTLR src "AADL.g" 837
HAT		: '^' ;

// $ANTLR src "AADL.g" 839
BAR		: '~' ;

// $ANTLR src "AADL.g" 841
EQUAL		: '==' ;

// $ANTLR src "AADL.g" 843
NOTEQUAL	: '!=' ;

// $ANTLR src "AADL.g" 845
LESSEQUAL	: '<=' ;

// $ANTLR src "AADL.g" 847
GREATEREQUAL	: '>=' ;

// $ANTLR src "AADL.g" 849
DOUBLESTAR	: '**' ;

// $ANTLR src "AADL.g" 851
ITERATOR	: '<-' ;

// $ANTLR src "AADL.g" 853
FINPUT		: '<<-' ;

// $ANTLR src "AADL.g" 855
FOUTPUT		: '->>' ;

// $ANTLR src "AADL.g" 857
SCOPE		: '::' ;

// $ANTLR src "AADL.g" 859
FUNCTION	: 'function' ;

// $ANTLR src "AADL.g" 861
PROGRAM		: 'program' ;

// $ANTLR src "AADL.g" 863
RETURN		: 'return' ;

// $ANTLR src "AADL.g" 865
BREAK		: 'break' ;

// $ANTLR src "AADL.g" 867
ID_PACKAGE	: 'package' ;

// $ANTLR src "AADL.g" 869
CONST		: 'const' ;

// $ANTLR src "AADL.g" 871
fragment
BaseKeyLetters
    :	'"' (~InvalidBaseKeyLetter)+ '"'
//@@@ deleted by Y.Ishizuka : 2010.07.12
//    |	'\'' (~InvalidBaseKeyLetter)+ '\''
//@@@ end of deleted : 2010.07.12
    ;

// $ANTLR src "AADL.g" 879
fragment
InvalidBaseKeyLetter
    :	('<'|'>'|'-'|'+'|'/'|'^'|','|'~'|'%'|'&'|'?'|'|'|'@'|'\''|'"'|WHITESPACE)
    ;

//@@@ added by Y.Ishizuka : 2010.07.12
// $ANTLR src "AADL.g" 885
HexLiteral
    :	'0' ('x'|'X') HexDigit+ IntegerTypeSuffix?
    ;
//@@@ end of added : 2010.07.12

// $ANTLR src "AADL.g" 890
IntegerLiteral
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    :	('0' | '1'..'9' Digit*)
**--- end of old codes : 2010.07.12 ---*/
    :	('0' | '1'..'9' Digit*) IntegerTypeSuffix?
//@@@ end of modified : 2010.07.12
    ;

//@@@ added by Y.Ishizuka : 2010.07.12
// $ANTLR src "AADL.g" 900
OctalLiteral
    :	'0' ('0'..'7')+ IntegerTypeSuffix?
    ;
//@@@ end of added : 2010.07.12

//@@@ added by Y.Ishizuka : 2010.07.12
// $ANTLR src "AADL.g" 906
fragment
IntegerTypeSuffix
    :	('i' | 'I' | 'l' | 'L')
    ;
//@@@ end of added : 2010.07.12

// $ANTLR src "AADL.g" 912
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

// $ANTLR src "AADL.g" 924
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
// $ANTLR src "AADL.g" 937
fragment
FloatTypeSuffix
    :	('f' | 'F' | 'd' | 'D')
    ;
//@@@ end of added : 2010.07.12

//@@@ added by Y.Ishizuka : 2010.07.12
// $ANTLR src "AADL.g" 944
CharacterLiteral
    :	'\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;
//@@@ end of added : 2010.07.12

// $ANTLR src "AADL.g" 949
StringLiteral
    :	'"' ( EscapeSequence | ~('\\'|'"') )* '"'
//@@@ deleted by Y.Ishizuka : 2010.07.12
//    |	'\'' ( EscapeSequence | ~('\''|'\\') )* '\''
//@@@ end of deleted : 2010.07.12
    ;

// $ANTLR src "AADL.g" 956
NamedOperator
//    :	('%' Letter (Letter|Digit)* '%')
    :	('%' ID '%')
    ;

// $ANTLR src "AADL.g" 961
Identifier
    :	ID
    ;

// $ANTLR src "AADL.g" 965
fragment
ID  :	Letter (Letter|Digit)*
    ;

// $ANTLR src "AADL.g" 969
fragment
EscapeSequence
    :	'\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |	UnicodeEscape
    |	OctalEscape
    ;

// $ANTLR src "AADL.g" 976
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "AADL.g" 983
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "AADL.g" 988
fragment
HexDigit
    :	('0'..'9'|'a'..'f'|'A'..'F')
    ;

// $ANTLR src "AADL.g" 993
fragment
Letter
    :  //'\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a'
    ;

// $ANTLR src "AADL.g" 1001
fragment
Digit
    :	'0'..'9'
    ;

// $ANTLR src "AADL.g" 1006
WS  
    :	(WHITESPACE) {$channel=HIDDEN;}
    ;

// $ANTLR src "AADL.g" 1010
COMMENT
    :	'/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

// $ANTLR src "AADL.g" 1014
LINE_COMMENT
//    :	'//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    :	'//' ~('\n'|'\r')* {$channel=HIDDEN;}
    ;

// $ANTLR src "AADL.g" 1019
JAVA_ACTION
    :	'@{'! ( options {greedy=false;} : .)* '}@'!
    ;

// $ANTLR src "AADL.g" 1023
JAVA_HEADER_ACTION
    :	'@header{'! ( options {greedy=false;} : .)* '}@'!
    ;

// $ANTLR src "AADL.g" 1027
fragment
WHITESPACE
    :	(' '|'\t'|'\r'|'\n')
    ;

// $ANTLR src "AADL.g" 1032
fragment
NEWLINE
    :	('\r'? '\n')
    ;
