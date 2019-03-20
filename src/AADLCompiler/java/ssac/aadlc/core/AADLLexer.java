// $ANTLR 3.0.1 AADL.g 2011-05-25 18:40:43

package ssac.aadlc.core;
/**
 * AADL(Algebraic Accounting Description Language) Grammar
 *
 * AADL Lexer prototype.
 *
 * @version 1.70 2011/05/25
 **/
import ssac.aadlc.io.ReportPrinter;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AADLLexer extends Lexer {
    public static final int T114=114;
    public static final int FUNCTION=47;
    public static final int CAST=39;
    public static final int T115=115;
    public static final int T116=116;
    public static final int T117=117;
    public static final int VOIDINVOLV=29;
    public static final int T118=118;
    public static final int STAR=68;
    public static final int T119=119;
    public static final int FloatTypeSuffix=93;
    public static final int OctalLiteral=57;
    public static final int AADL_ARGS=6;
    public static final int CONST=48;
    public static final int JAVACLASSPATH=21;
    public static final int VARINIT=24;
    public static final int GREATEREQUAL=83;
    public static final int INVOLV_EXP=30;
    public static final int NOT=77;
    public static final int EOF=-1;
    public static final int T120=120;
    public static final int AADL_FUNCS=7;
    public static final int AINDEX=37;
    public static final int BREAK=51;
    public static final int T122=122;
    public static final int Identifier=46;
    public static final int T121=121;
    public static final int T124=124;
    public static final int T123=123;
    public static final int NOTEQUAL=81;
    public static final int HAT=78;
    public static final int VBAR=71;
    public static final int FUNCDEF_RETURN=16;
    public static final int T125=125;
    public static final int GREATER=74;
    public static final int AADL_INFO=4;
    public static final int FUNCALL_PATH=12;
    public static final int IFCOND=18;
    public static final int FOUTPUT=50;
    public static final int LESS=73;
    public static final int SIF=53;
    public static final int RETURN=52;
    public static final int BODY=20;
    public static final int AADL_NAME=5;
    public static final int VARDEF=23;
    public static final int COMMENT=99;
    public static final int ARRAY=35;
    public static final int DTBASE=34;
    public static final int AADL_CONSTS=9;
    public static final int SHARP=70;
    public static final int LINE_COMMENT=100;
    public static final int IntegerTypeSuffix=91;
    public static final int CMDARG=62;
    public static final int FINPUT=49;
    public static final int SELSE=54;
    public static final int WHITESPACE=89;
    public static final int T102=102;
    public static final int ID_PACKAGE=43;
    public static final int JAVA_HEADER_ACTION=42;
    public static final int FCALL_MM=15;
    public static final int T109=109;
    public static final int T107=107;
    public static final int T108=108;
    public static final int T105=105;
    public static final int WS=98;
    public static final int T106=106;
    public static final int T103=103;
    public static final int T104=104;
    public static final int FloatingPointLiteral=59;
    public static final int ALIAS=27;
    public static final int T113=113;
    public static final int T112=112;
    public static final int T111=111;
    public static final int LESSEQUAL=82;
    public static final int T110=110;
    public static final int EscapeSequence=94;
    public static final int INVOLV_COND=31;
    public static final int Letter=63;
    public static final int FOPTION=38;
    public static final int JAVA_ACTION=44;
    public static final int FUNCALL_ARGS=13;
    public static final int DOUBLESTAR=84;
    public static final int CharacterLiteral=60;
    public static final int VARNEW=25;
    public static final int Exponent=92;
    public static final int ID=95;
    public static final int HexDigit=90;
    public static final int FCALL_SM=11;
    public static final int MODIFIERS=41;
    public static final int INVOLV=28;
    public static final int SLASH=69;
    public static final int SCOPE=86;
    public static final int AMPER=72;
    public static final int ITERATOR=85;
    public static final int FCALL_IM=14;
    public static final int EQUAL=80;
    public static final int INVOLV_BLOCK=32;
    public static final int NamedOperator=55;
    public static final int PLUS=66;
    public static final int AADL_PROGRAM=8;
    public static final int HexLiteral=56;
    public static final int PERCENT=76;
    public static final int HASH=36;
    public static final int INSTANCEOF=40;
    public static final int MINUS=67;
    public static final int Digit=64;
    public static final int Tokens=126;
    public static final int VARTYPE=22;
    public static final int StringLiteral=61;
    public static final int COLON=65;
    public static final int EXBASE=33;
    public static final int ITERATE=26;
    public static final int FUNCDEF_PARAMS=17;
    public static final int NEWLINE=101;
    public static final int UnicodeEscape=96;
    public static final int BLOCK=19;
    public static final int ASSIGN=75;
    public static final int PROGRAM=45;
    public static final int IntegerLiteral=58;
    public static final int FCALL_RM=10;
    public static final int InvalidBaseKeyLetter=87;
    public static final int OctalEscape=97;
    public static final int BaseKeyLetters=88;
    public static final int BAR=79;
    
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

    public AADLLexer() {;} 
    public AADLLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "AADL.g"; }

    // $ANTLR start T102
    public final void mT102() throws RecognitionException {
        try {
            int _type = T102;
            // AADL.g:121:6: ( ';' )
            // AADL.g:121:8: ';'
            {
            match(';'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T102

    // $ANTLR start T103
    public final void mT103() throws RecognitionException {
        try {
            int _type = T103;
            // AADL.g:122:6: ( '{' )
            // AADL.g:122:8: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T103

    // $ANTLR start T104
    public final void mT104() throws RecognitionException {
        try {
            int _type = T104;
            // AADL.g:123:6: ( '}' )
            // AADL.g:123:8: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T104

    // $ANTLR start T105
    public final void mT105() throws RecognitionException {
        try {
            int _type = T105;
            // AADL.g:124:6: ( '(' )
            // AADL.g:124:8: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T105

    // $ANTLR start T106
    public final void mT106() throws RecognitionException {
        try {
            int _type = T106;
            // AADL.g:125:6: ( ')' )
            // AADL.g:125:8: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T106

    // $ANTLR start T107
    public final void mT107() throws RecognitionException {
        try {
            int _type = T107;
            // AADL.g:126:6: ( ',' )
            // AADL.g:126:8: ','
            {
            match(','); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T107

    // $ANTLR start T108
    public final void mT108() throws RecognitionException {
        try {
            int _type = T108;
            // AADL.g:127:6: ( 'var' )
            // AADL.g:127:8: 'var'
            {
            match("var"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T108

    // $ANTLR start T109
    public final void mT109() throws RecognitionException {
        try {
            int _type = T109;
            // AADL.g:128:6: ( '||' )
            // AADL.g:128:8: '||'
            {
            match("||"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T109

    // $ANTLR start T110
    public final void mT110() throws RecognitionException {
        try {
            int _type = T110;
            // AADL.g:129:6: ( '&&' )
            // AADL.g:129:8: '&&'
            {
            match("&&"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T110

    // $ANTLR start T111
    public final void mT111() throws RecognitionException {
        try {
            int _type = T111;
            // AADL.g:130:6: ( '@' )
            // AADL.g:130:8: '@'
            {
            match('@'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T111

    // $ANTLR start T112
    public final void mT112() throws RecognitionException {
        try {
            int _type = T112;
            // AADL.g:131:6: ( '.' )
            // AADL.g:131:8: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T112

    // $ANTLR start T113
    public final void mT113() throws RecognitionException {
        try {
            int _type = T113;
            // AADL.g:132:6: ( 'cast' )
            // AADL.g:132:8: 'cast'
            {
            match("cast"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T113

    // $ANTLR start T114
    public final void mT114() throws RecognitionException {
        try {
            int _type = T114;
            // AADL.g:133:6: ( '[' )
            // AADL.g:133:8: '['
            {
            match('['); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T114

    // $ANTLR start T115
    public final void mT115() throws RecognitionException {
        try {
            int _type = T115;
            // AADL.g:134:6: ( ']' )
            // AADL.g:134:8: ']'
            {
            match(']'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T115

    // $ANTLR start T116
    public final void mT116() throws RecognitionException {
        try {
            int _type = T116;
            // AADL.g:135:6: ( 'typeof' )
            // AADL.g:135:8: 'typeof'
            {
            match("typeof"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T116

    // $ANTLR start T117
    public final void mT117() throws RecognitionException {
        try {
            int _type = T117;
            // AADL.g:136:6: ( 'txtFile' )
            // AADL.g:136:8: 'txtFile'
            {
            match("txtFile"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T117

    // $ANTLR start T118
    public final void mT118() throws RecognitionException {
        try {
            int _type = T118;
            // AADL.g:137:6: ( 'csvFile' )
            // AADL.g:137:8: 'csvFile'
            {
            match("csvFile"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T118

    // $ANTLR start T119
    public final void mT119() throws RecognitionException {
        try {
            int _type = T119;
            // AADL.g:138:6: ( 'xmlFile' )
            // AADL.g:138:8: 'xmlFile'
            {
            match("xmlFile"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T119

    // $ANTLR start T120
    public final void mT120() throws RecognitionException {
        try {
            int _type = T120;
            // AADL.g:139:6: ( 'true' )
            // AADL.g:139:8: 'true'
            {
            match("true"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T120

    // $ANTLR start T121
    public final void mT121() throws RecognitionException {
        try {
            int _type = T121;
            // AADL.g:140:6: ( 'false' )
            // AADL.g:140:8: 'false'
            {
            match("false"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T121

    // $ANTLR start T122
    public final void mT122() throws RecognitionException {
        try {
            int _type = T122;
            // AADL.g:141:6: ( 'null' )
            // AADL.g:141:8: 'null'
            {
            match("null"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T122

    // $ANTLR start T123
    public final void mT123() throws RecognitionException {
        try {
            int _type = T123;
            // AADL.g:142:6: ( '<<' )
            // AADL.g:142:8: '<<'
            {
            match("<<"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T123

    // $ANTLR start T124
    public final void mT124() throws RecognitionException {
        try {
            int _type = T124;
            // AADL.g:143:6: ( '>>' )
            // AADL.g:143:8: '>>'
            {
            match(">>"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T124

    // $ANTLR start T125
    public final void mT125() throws RecognitionException {
        try {
            int _type = T125;
            // AADL.g:144:6: ( 'public' )
            // AADL.g:144:8: 'public'
            {
            match("public"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T125

    // $ANTLR start SIF
    public final void mSIF() throws RecognitionException {
        try {
            int _type = SIF;
            // AADL.g:803:6: ( 'if' )
            // AADL.g:803:8: 'if'
            {
            match("if"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SIF

    // $ANTLR start SELSE
    public final void mSELSE() throws RecognitionException {
        try {
            int _type = SELSE;
            // AADL.g:805:8: ( 'else' )
            // AADL.g:805:10: 'else'
            {
            match("else"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SELSE

    // $ANTLR start CMDARG
    public final void mCMDARG() throws RecognitionException {
        try {
            int _type = CMDARG;
            // AADL.g:807:9: ( '$' ( Letter | Digit )+ )
            // AADL.g:807:11: '$' ( Letter | Digit )+
            {
            match('$'); 
            // AADL.g:807:15: ( Letter | Digit )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // AADL.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CMDARG

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // AADL.g:811:8: ( ':' )
            // AADL.g:811:10: ':'
            {
            match(':'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // AADL.g:813:7: ( '+' )
            // AADL.g:813:9: '+'
            {
            match('+'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            // AADL.g:815:8: ( '-' )
            // AADL.g:815:10: '-'
            {
            match('-'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MINUS

    // $ANTLR start STAR
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            // AADL.g:817:7: ( '*' )
            // AADL.g:817:9: '*'
            {
            match('*'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STAR

    // $ANTLR start SLASH
    public final void mSLASH() throws RecognitionException {
        try {
            int _type = SLASH;
            // AADL.g:819:8: ( '/' )
            // AADL.g:819:10: '/'
            {
            match('/'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SLASH

    // $ANTLR start SHARP
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            // AADL.g:821:8: ( '#' )
            // AADL.g:821:10: '#'
            {
            match('#'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SHARP

    // $ANTLR start VBAR
    public final void mVBAR() throws RecognitionException {
        try {
            int _type = VBAR;
            // AADL.g:823:7: ( '|' )
            // AADL.g:823:9: '|'
            {
            match('|'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end VBAR

    // $ANTLR start AMPER
    public final void mAMPER() throws RecognitionException {
        try {
            int _type = AMPER;
            // AADL.g:825:8: ( '&' )
            // AADL.g:825:10: '&'
            {
            match('&'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AMPER

    // $ANTLR start LESS
    public final void mLESS() throws RecognitionException {
        try {
            int _type = LESS;
            // AADL.g:827:7: ( '<' )
            // AADL.g:827:9: '<'
            {
            match('<'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESS

    // $ANTLR start GREATER
    public final void mGREATER() throws RecognitionException {
        try {
            int _type = GREATER;
            // AADL.g:829:10: ( '>' )
            // AADL.g:829:12: '>'
            {
            match('>'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATER

    // $ANTLR start ASSIGN
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            // AADL.g:831:9: ( '=' )
            // AADL.g:831:11: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ASSIGN

    // $ANTLR start PERCENT
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            // AADL.g:833:10: ( '%' )
            // AADL.g:833:12: '%'
            {
            match('%'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PERCENT

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // AADL.g:835:6: ( '!' )
            // AADL.g:835:8: '!'
            {
            match('!'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start HAT
    public final void mHAT() throws RecognitionException {
        try {
            int _type = HAT;
            // AADL.g:837:6: ( '^' )
            // AADL.g:837:8: '^'
            {
            match('^'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HAT

    // $ANTLR start BAR
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            // AADL.g:839:6: ( '~' )
            // AADL.g:839:8: '~'
            {
            match('~'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BAR

    // $ANTLR start EQUAL
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            // AADL.g:841:8: ( '==' )
            // AADL.g:841:10: '=='
            {
            match("=="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUAL

    // $ANTLR start NOTEQUAL
    public final void mNOTEQUAL() throws RecognitionException {
        try {
            int _type = NOTEQUAL;
            // AADL.g:843:10: ( '!=' )
            // AADL.g:843:12: '!='
            {
            match("!="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOTEQUAL

    // $ANTLR start LESSEQUAL
    public final void mLESSEQUAL() throws RecognitionException {
        try {
            int _type = LESSEQUAL;
            // AADL.g:845:11: ( '<=' )
            // AADL.g:845:13: '<='
            {
            match("<="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESSEQUAL

    // $ANTLR start GREATEREQUAL
    public final void mGREATEREQUAL() throws RecognitionException {
        try {
            int _type = GREATEREQUAL;
            // AADL.g:847:14: ( '>=' )
            // AADL.g:847:16: '>='
            {
            match(">="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATEREQUAL

    // $ANTLR start DOUBLESTAR
    public final void mDOUBLESTAR() throws RecognitionException {
        try {
            int _type = DOUBLESTAR;
            // AADL.g:849:12: ( '**' )
            // AADL.g:849:14: '**'
            {
            match("**"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLESTAR

    // $ANTLR start ITERATOR
    public final void mITERATOR() throws RecognitionException {
        try {
            int _type = ITERATOR;
            // AADL.g:851:10: ( '<-' )
            // AADL.g:851:12: '<-'
            {
            match("<-"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ITERATOR

    // $ANTLR start FINPUT
    public final void mFINPUT() throws RecognitionException {
        try {
            int _type = FINPUT;
            // AADL.g:853:9: ( '<<-' )
            // AADL.g:853:11: '<<-'
            {
            match("<<-"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FINPUT

    // $ANTLR start FOUTPUT
    public final void mFOUTPUT() throws RecognitionException {
        try {
            int _type = FOUTPUT;
            // AADL.g:855:10: ( '->>' )
            // AADL.g:855:12: '->>'
            {
            match("->>"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FOUTPUT

    // $ANTLR start SCOPE
    public final void mSCOPE() throws RecognitionException {
        try {
            int _type = SCOPE;
            // AADL.g:857:8: ( '::' )
            // AADL.g:857:10: '::'
            {
            match("::"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SCOPE

    // $ANTLR start FUNCTION
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            // AADL.g:859:10: ( 'function' )
            // AADL.g:859:12: 'function'
            {
            match("function"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FUNCTION

    // $ANTLR start PROGRAM
    public final void mPROGRAM() throws RecognitionException {
        try {
            int _type = PROGRAM;
            // AADL.g:861:10: ( 'program' )
            // AADL.g:861:12: 'program'
            {
            match("program"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PROGRAM

    // $ANTLR start RETURN
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            // AADL.g:863:9: ( 'return' )
            // AADL.g:863:11: 'return'
            {
            match("return"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RETURN

    // $ANTLR start BREAK
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            // AADL.g:865:8: ( 'break' )
            // AADL.g:865:10: 'break'
            {
            match("break"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BREAK

    // $ANTLR start ID_PACKAGE
    public final void mID_PACKAGE() throws RecognitionException {
        try {
            int _type = ID_PACKAGE;
            // AADL.g:867:12: ( 'package' )
            // AADL.g:867:14: 'package'
            {
            match("package"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ID_PACKAGE

    // $ANTLR start CONST
    public final void mCONST() throws RecognitionException {
        try {
            int _type = CONST;
            // AADL.g:869:8: ( 'const' )
            // AADL.g:869:10: 'const'
            {
            match("const"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CONST

    // $ANTLR start BaseKeyLetters
    public final void mBaseKeyLetters() throws RecognitionException {
        try {
            // AADL.g:873:5: ( '\"' (~ InvalidBaseKeyLetter )+ '\"' )
            // AADL.g:873:7: '\"' (~ InvalidBaseKeyLetter )+ '\"'
            {
            match('\"'); 
            // AADL.g:873:11: (~ InvalidBaseKeyLetter )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='\b')||(LA2_0>='\u000B' && LA2_0<='\f')||(LA2_0>='\u000E' && LA2_0<='\u001F')||LA2_0=='!'||(LA2_0>='#' && LA2_0<='$')||(LA2_0>='(' && LA2_0<='*')||LA2_0=='.'||(LA2_0>='0' && LA2_0<=';')||LA2_0=='='||(LA2_0>='A' && LA2_0<=']')||(LA2_0>='_' && LA2_0<='{')||LA2_0=='}'||(LA2_0>='\u007F' && LA2_0<='\uFFFE')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // AADL.g:873:12: ~ InvalidBaseKeyLetter
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='V')||(input.LA(1)>='X' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            match('\"'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end BaseKeyLetters

    // $ANTLR start InvalidBaseKeyLetter
    public final void mInvalidBaseKeyLetter() throws RecognitionException {
        try {
            // AADL.g:881:5: ( ( '<' | '>' | '-' | '+' | '/' | '^' | ',' | '~' | '%' | '&' | '?' | '|' | '@' | '\\'' | '\"' | WHITESPACE ) )
            // AADL.g:881:7: ( '<' | '>' | '-' | '+' | '/' | '^' | ',' | '~' | '%' | '&' | '?' | '|' | '@' | '\\'' | '\"' | WHITESPACE )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' '||input.LA(1)=='\"'||(input.LA(1)>='%' && input.LA(1)<='\'')||(input.LA(1)>='+' && input.LA(1)<='-')||input.LA(1)=='/'||input.LA(1)=='<'||(input.LA(1)>='>' && input.LA(1)<='@')||input.LA(1)=='^'||input.LA(1)=='|'||input.LA(1)=='~' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end InvalidBaseKeyLetter

    // $ANTLR start HexLiteral
    public final void mHexLiteral() throws RecognitionException {
        try {
            int _type = HexLiteral;
            // AADL.g:886:5: ( '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )? )
            // AADL.g:886:7: '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // AADL.g:886:21: ( HexDigit )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='0' && LA3_0<='9')||(LA3_0>='A' && LA3_0<='F')||(LA3_0>='a' && LA3_0<='f')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // AADL.g:886:21: HexDigit
            	    {
            	    mHexDigit(); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            // AADL.g:886:31: ( IntegerTypeSuffix )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='I'||LA4_0=='L'||LA4_0=='i'||LA4_0=='l') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // AADL.g:886:31: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HexLiteral

    // $ANTLR start IntegerLiteral
    public final void mIntegerLiteral() throws RecognitionException {
        try {
            int _type = IntegerLiteral;
            // AADL.g:895:5: ( ( '0' | '1' .. '9' ( Digit )* ) ( IntegerTypeSuffix )? )
            // AADL.g:895:7: ( '0' | '1' .. '9' ( Digit )* ) ( IntegerTypeSuffix )?
            {
            // AADL.g:895:7: ( '0' | '1' .. '9' ( Digit )* )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='0') ) {
                alt6=1;
            }
            else if ( ((LA6_0>='1' && LA6_0<='9')) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("895:7: ( '0' | '1' .. '9' ( Digit )* )", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // AADL.g:895:8: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // AADL.g:895:14: '1' .. '9' ( Digit )*
                    {
                    matchRange('1','9'); 
                    // AADL.g:895:23: ( Digit )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // AADL.g:895:23: Digit
                    	    {
                    	    mDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    }
                    break;

            }

            // AADL.g:895:31: ( IntegerTypeSuffix )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='I'||LA7_0=='L'||LA7_0=='i'||LA7_0=='l') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // AADL.g:895:31: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IntegerLiteral

    // $ANTLR start OctalLiteral
    public final void mOctalLiteral() throws RecognitionException {
        try {
            int _type = OctalLiteral;
            // AADL.g:901:5: ( '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )? )
            // AADL.g:901:7: '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            // AADL.g:901:11: ( '0' .. '7' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='0' && LA8_0<='7')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // AADL.g:901:12: '0' .. '7'
            	    {
            	    matchRange('0','7'); 

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);

            // AADL.g:901:23: ( IntegerTypeSuffix )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='I'||LA9_0=='L'||LA9_0=='i'||LA9_0=='l') ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // AADL.g:901:23: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OctalLiteral

    // $ANTLR start IntegerTypeSuffix
    public final void mIntegerTypeSuffix() throws RecognitionException {
        try {
            // AADL.g:908:5: ( ( 'i' | 'I' | 'l' | 'L' ) )
            // AADL.g:908:7: ( 'i' | 'I' | 'l' | 'L' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='L'||input.LA(1)=='i'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end IntegerTypeSuffix

    // $ANTLR start FloatingPointLiteral
    public final void mFloatingPointLiteral() throws RecognitionException {
        try {
            int _type = FloatingPointLiteral;
            // AADL.g:918:5: ( '.' ( Digit )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' | '1' .. '9' ( Digit )* ) ( '.' ( Digit )* ( Exponent )? ( FloatTypeSuffix )? | Exponent ( FloatTypeSuffix )? | FloatTypeSuffix ) )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='.') ) {
                alt20=1;
            }
            else if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("912:1: FloatingPointLiteral : ( '.' ( Digit )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' | '1' .. '9' ( Digit )* ) ( '.' ( Digit )* ( Exponent )? ( FloatTypeSuffix )? | Exponent ( FloatTypeSuffix )? | FloatTypeSuffix ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // AADL.g:918:7: '.' ( Digit )+ ( Exponent )? ( FloatTypeSuffix )?
                    {
                    match('.'); 
                    // AADL.g:918:11: ( Digit )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>='0' && LA10_0<='9')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // AADL.g:918:11: Digit
                    	    {
                    	    mDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);

                    // AADL.g:918:18: ( Exponent )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='E'||LA11_0=='e') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // AADL.g:918:18: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // AADL.g:918:28: ( FloatTypeSuffix )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0=='D'||LA12_0=='F'||LA12_0=='d'||LA12_0=='f') ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // AADL.g:918:28: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // AADL.g:920:7: ( '0' | '1' .. '9' ( Digit )* ) ( '.' ( Digit )* ( Exponent )? ( FloatTypeSuffix )? | Exponent ( FloatTypeSuffix )? | FloatTypeSuffix )
                    {
                    // AADL.g:920:7: ( '0' | '1' .. '9' ( Digit )* )
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0=='0') ) {
                        alt14=1;
                    }
                    else if ( ((LA14_0>='1' && LA14_0<='9')) ) {
                        alt14=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("920:7: ( '0' | '1' .. '9' ( Digit )* )", 14, 0, input);

                        throw nvae;
                    }
                    switch (alt14) {
                        case 1 :
                            // AADL.g:920:8: '0'
                            {
                            match('0'); 

                            }
                            break;
                        case 2 :
                            // AADL.g:920:14: '1' .. '9' ( Digit )*
                            {
                            matchRange('1','9'); 
                            // AADL.g:920:23: ( Digit )*
                            loop13:
                            do {
                                int alt13=2;
                                int LA13_0 = input.LA(1);

                                if ( ((LA13_0>='0' && LA13_0<='9')) ) {
                                    alt13=1;
                                }


                                switch (alt13) {
                            	case 1 :
                            	    // AADL.g:920:23: Digit
                            	    {
                            	    mDigit(); 

                            	    }
                            	    break;

                            	default :
                            	    break loop13;
                                }
                            } while (true);


                            }
                            break;

                    }

                    // AADL.g:920:31: ( '.' ( Digit )* ( Exponent )? ( FloatTypeSuffix )? | Exponent ( FloatTypeSuffix )? | FloatTypeSuffix )
                    int alt19=3;
                    switch ( input.LA(1) ) {
                    case '.':
                        {
                        alt19=1;
                        }
                        break;
                    case 'E':
                    case 'e':
                        {
                        alt19=2;
                        }
                        break;
                    case 'D':
                    case 'F':
                    case 'd':
                    case 'f':
                        {
                        alt19=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("920:31: ( '.' ( Digit )* ( Exponent )? ( FloatTypeSuffix )? | Exponent ( FloatTypeSuffix )? | FloatTypeSuffix )", 19, 0, input);

                        throw nvae;
                    }

                    switch (alt19) {
                        case 1 :
                            // AADL.g:920:32: '.' ( Digit )* ( Exponent )? ( FloatTypeSuffix )?
                            {
                            match('.'); 
                            // AADL.g:920:36: ( Digit )*
                            loop15:
                            do {
                                int alt15=2;
                                int LA15_0 = input.LA(1);

                                if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                                    alt15=1;
                                }


                                switch (alt15) {
                            	case 1 :
                            	    // AADL.g:920:36: Digit
                            	    {
                            	    mDigit(); 

                            	    }
                            	    break;

                            	default :
                            	    break loop15;
                                }
                            } while (true);

                            // AADL.g:920:43: ( Exponent )?
                            int alt16=2;
                            int LA16_0 = input.LA(1);

                            if ( (LA16_0=='E'||LA16_0=='e') ) {
                                alt16=1;
                            }
                            switch (alt16) {
                                case 1 :
                                    // AADL.g:920:43: Exponent
                                    {
                                    mExponent(); 

                                    }
                                    break;

                            }

                            // AADL.g:920:53: ( FloatTypeSuffix )?
                            int alt17=2;
                            int LA17_0 = input.LA(1);

                            if ( (LA17_0=='D'||LA17_0=='F'||LA17_0=='d'||LA17_0=='f') ) {
                                alt17=1;
                            }
                            switch (alt17) {
                                case 1 :
                                    // AADL.g:920:53: FloatTypeSuffix
                                    {
                                    mFloatTypeSuffix(); 

                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // AADL.g:920:72: Exponent ( FloatTypeSuffix )?
                            {
                            mExponent(); 
                            // AADL.g:920:81: ( FloatTypeSuffix )?
                            int alt18=2;
                            int LA18_0 = input.LA(1);

                            if ( (LA18_0=='D'||LA18_0=='F'||LA18_0=='d'||LA18_0=='f') ) {
                                alt18=1;
                            }
                            switch (alt18) {
                                case 1 :
                                    // AADL.g:920:81: FloatTypeSuffix
                                    {
                                    mFloatTypeSuffix(); 

                                    }
                                    break;

                            }


                            }
                            break;
                        case 3 :
                            // AADL.g:920:100: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FloatingPointLiteral

    // $ANTLR start Exponent
    public final void mExponent() throws RecognitionException {
        try {
            // AADL.g:930:5: ( ( 'e' | 'E' ) ( '+' | '-' ) ( Digit )+ | ( 'e' | 'E' ) ( Digit )+ | ( 'e' | 'E' ) ( '+' | '-' )? )
            int alt24=3;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='E'||LA24_0=='e') ) {
                switch ( input.LA(2) ) {
                case '+':
                case '-':
                    {
                    int LA24_2 = input.LA(3);

                    if ( ((LA24_2>='0' && LA24_2<='9')) ) {
                        alt24=1;
                    }
                    else {
                        alt24=3;}
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt24=2;
                    }
                    break;
                default:
                    alt24=3;}

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("924:1: fragment Exponent : ( ( 'e' | 'E' ) ( '+' | '-' ) ( Digit )+ | ( 'e' | 'E' ) ( Digit )+ | ( 'e' | 'E' ) ( '+' | '-' )? );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // AADL.g:930:7: ( 'e' | 'E' ) ( '+' | '-' ) ( Digit )+
                    {
                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // AADL.g:930:27: ( Digit )+
                    int cnt21=0;
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // AADL.g:930:27: Digit
                    	    {
                    	    mDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt21 >= 1 ) break loop21;
                                EarlyExitException eee =
                                    new EarlyExitException(21, input);
                                throw eee;
                        }
                        cnt21++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // AADL.g:931:7: ( 'e' | 'E' ) ( Digit )+
                    {
                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // AADL.g:931:17: ( Digit )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( ((LA22_0>='0' && LA22_0<='9')) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // AADL.g:931:17: Digit
                    	    {
                    	    mDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt22 >= 1 ) break loop22;
                                EarlyExitException eee =
                                    new EarlyExitException(22, input);
                                throw eee;
                        }
                        cnt22++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // AADL.g:932:7: ( 'e' | 'E' ) ( '+' | '-' )?
                    {
                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // AADL.g:932:17: ( '+' | '-' )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0=='+'||LA23_0=='-') ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // AADL.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recover(mse);    throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end Exponent

    // $ANTLR start FloatTypeSuffix
    public final void mFloatTypeSuffix() throws RecognitionException {
        try {
            // AADL.g:939:5: ( ( 'f' | 'F' | 'd' | 'D' ) )
            // AADL.g:939:7: ( 'f' | 'F' | 'd' | 'D' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end FloatTypeSuffix

    // $ANTLR start CharacterLiteral
    public final void mCharacterLiteral() throws RecognitionException {
        try {
            int _type = CharacterLiteral;
            // AADL.g:945:5: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\'' )
            // AADL.g:945:7: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\''
            {
            match('\''); 
            // AADL.g:945:12: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0=='\\') ) {
                alt25=1;
            }
            else if ( ((LA25_0>='\u0000' && LA25_0<='&')||(LA25_0>='(' && LA25_0<='[')||(LA25_0>=']' && LA25_0<='\uFFFE')) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("945:12: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // AADL.g:945:14: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // AADL.g:945:31: ~ ( '\\'' | '\\\\' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            match('\''); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CharacterLiteral

    // $ANTLR start StringLiteral
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            // AADL.g:950:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
            // AADL.g:950:7: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // AADL.g:950:11: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
            loop26:
            do {
                int alt26=3;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='\\') ) {
                    alt26=1;
                }
                else if ( ((LA26_0>='\u0000' && LA26_0<='!')||(LA26_0>='#' && LA26_0<='[')||(LA26_0>=']' && LA26_0<='\uFFFE')) ) {
                    alt26=2;
                }


                switch (alt26) {
            	case 1 :
            	    // AADL.g:950:13: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // AADL.g:950:30: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end StringLiteral

    // $ANTLR start NamedOperator
    public final void mNamedOperator() throws RecognitionException {
        try {
            int _type = NamedOperator;
            // AADL.g:958:5: ( ( '%' ID '%' ) )
            // AADL.g:958:7: ( '%' ID '%' )
            {
            // AADL.g:958:7: ( '%' ID '%' )
            // AADL.g:958:8: '%' ID '%'
            {
            match('%'); 
            mID(); 
            match('%'); 

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NamedOperator

    // $ANTLR start Identifier
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            // AADL.g:962:5: ( ID )
            // AADL.g:962:7: ID
            {
            mID(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end Identifier

    // $ANTLR start ID
    public final void mID() throws RecognitionException {
        try {
            // AADL.g:966:5: ( Letter ( Letter | Digit )* )
            // AADL.g:966:7: Letter ( Letter | Digit )*
            {
            mLetter(); 
            // AADL.g:966:14: ( Letter | Digit )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0>='0' && LA27_0<='9')||(LA27_0>='A' && LA27_0<='Z')||LA27_0=='_'||(LA27_0>='a' && LA27_0<='z')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // AADL.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end ID

    // $ANTLR start EscapeSequence
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // AADL.g:971:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt28=3;
            int LA28_0 = input.LA(1);

            if ( (LA28_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt28=1;
                    }
                    break;
                case 'u':
                    {
                    alt28=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt28=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("969:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 28, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("969:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // AADL.g:971:7: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // AADL.g:972:7: UnicodeEscape
                    {
                    mUnicodeEscape(); 

                    }
                    break;
                case 3 :
                    // AADL.g:973:7: OctalEscape
                    {
                    mOctalEscape(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end EscapeSequence

    // $ANTLR start OctalEscape
    public final void mOctalEscape() throws RecognitionException {
        try {
            // AADL.g:978:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt29=3;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='\\') ) {
                int LA29_1 = input.LA(2);

                if ( ((LA29_1>='0' && LA29_1<='3')) ) {
                    int LA29_2 = input.LA(3);

                    if ( ((LA29_2>='0' && LA29_2<='7')) ) {
                        int LA29_4 = input.LA(4);

                        if ( ((LA29_4>='0' && LA29_4<='7')) ) {
                            alt29=1;
                        }
                        else {
                            alt29=2;}
                    }
                    else {
                        alt29=3;}
                }
                else if ( ((LA29_1>='4' && LA29_1<='7')) ) {
                    int LA29_3 = input.LA(3);

                    if ( ((LA29_3>='0' && LA29_3<='7')) ) {
                        alt29=2;
                    }
                    else {
                        alt29=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("976:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 29, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("976:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // AADL.g:978:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // AADL.g:978:14: ( '0' .. '3' )
                    // AADL.g:978:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // AADL.g:978:25: ( '0' .. '7' )
                    // AADL.g:978:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // AADL.g:978:36: ( '0' .. '7' )
                    // AADL.g:978:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // AADL.g:979:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // AADL.g:979:14: ( '0' .. '7' )
                    // AADL.g:979:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // AADL.g:979:25: ( '0' .. '7' )
                    // AADL.g:979:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // AADL.g:980:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // AADL.g:980:14: ( '0' .. '7' )
                    // AADL.g:980:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end OctalEscape

    // $ANTLR start UnicodeEscape
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // AADL.g:985:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // AADL.g:985:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
            {
            match('\\'); 
            match('u'); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end UnicodeEscape

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // AADL.g:990:5: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // AADL.g:990:7: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start Letter
    public final void mLetter() throws RecognitionException {
        try {
            // AADL.g:995:5: ( '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' )
            // AADL.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end Letter

    // $ANTLR start Digit
    public final void mDigit() throws RecognitionException {
        try {
            // AADL.g:1003:5: ( '0' .. '9' )
            // AADL.g:1003:7: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end Digit

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // AADL.g:1007:5: ( ( WHITESPACE ) )
            // AADL.g:1007:7: ( WHITESPACE )
            {
            // AADL.g:1007:7: ( WHITESPACE )
            // AADL.g:1007:8: WHITESPACE
            {
            mWHITESPACE(); 

            }

            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start COMMENT
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            // AADL.g:1011:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // AADL.g:1011:7: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // AADL.g:1011:12: ( options {greedy=false; } : . )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0=='*') ) {
                    int LA30_1 = input.LA(2);

                    if ( (LA30_1=='/') ) {
                        alt30=2;
                    }
                    else if ( ((LA30_1>='\u0000' && LA30_1<='.')||(LA30_1>='0' && LA30_1<='\uFFFE')) ) {
                        alt30=1;
                    }


                }
                else if ( ((LA30_0>='\u0000' && LA30_0<=')')||(LA30_0>='+' && LA30_0<='\uFFFE')) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // AADL.g:1011:40: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            match("*/"); 

            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMENT

    // $ANTLR start LINE_COMMENT
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            // AADL.g:1016:5: ( '//' (~ ( '\\n' | '\\r' ) )* )
            // AADL.g:1016:7: '//' (~ ( '\\n' | '\\r' ) )*
            {
            match("//"); 

            // AADL.g:1016:12: (~ ( '\\n' | '\\r' ) )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>='\u0000' && LA31_0<='\t')||(LA31_0>='\u000B' && LA31_0<='\f')||(LA31_0>='\u000E' && LA31_0<='\uFFFE')) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // AADL.g:1016:12: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE_COMMENT

    // $ANTLR start JAVA_ACTION
    public final void mJAVA_ACTION() throws RecognitionException {
        try {
            int _type = JAVA_ACTION;
            // AADL.g:1020:5: ( '@{' ( options {greedy=false; } : . )* '}@' )
            // AADL.g:1020:7: '@{' ( options {greedy=false; } : . )* '}@'
            {
            match("@{"); 

            // AADL.g:1020:13: ( options {greedy=false; } : . )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0=='}') ) {
                    int LA32_1 = input.LA(2);

                    if ( (LA32_1=='@') ) {
                        alt32=2;
                    }
                    else if ( ((LA32_1>='\u0000' && LA32_1<='?')||(LA32_1>='A' && LA32_1<='\uFFFE')) ) {
                        alt32=1;
                    }


                }
                else if ( ((LA32_0>='\u0000' && LA32_0<='|')||(LA32_0>='~' && LA32_0<='\uFFFE')) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // AADL.g:1020:41: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);

            match("}@"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end JAVA_ACTION

    // $ANTLR start JAVA_HEADER_ACTION
    public final void mJAVA_HEADER_ACTION() throws RecognitionException {
        try {
            int _type = JAVA_HEADER_ACTION;
            // AADL.g:1024:5: ( '@header{' ( options {greedy=false; } : . )* '}@' )
            // AADL.g:1024:7: '@header{' ( options {greedy=false; } : . )* '}@'
            {
            match("@header{"); 

            // AADL.g:1024:19: ( options {greedy=false; } : . )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0=='}') ) {
                    int LA33_1 = input.LA(2);

                    if ( (LA33_1=='@') ) {
                        alt33=2;
                    }
                    else if ( ((LA33_1>='\u0000' && LA33_1<='?')||(LA33_1>='A' && LA33_1<='\uFFFE')) ) {
                        alt33=1;
                    }


                }
                else if ( ((LA33_0>='\u0000' && LA33_0<='|')||(LA33_0>='~' && LA33_0<='\uFFFE')) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // AADL.g:1024:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);

            match("}@"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end JAVA_HEADER_ACTION

    // $ANTLR start WHITESPACE
    public final void mWHITESPACE() throws RecognitionException {
        try {
            // AADL.g:1029:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // AADL.g:1029:7: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end WHITESPACE

    // $ANTLR start NEWLINE
    public final void mNEWLINE() throws RecognitionException {
        try {
            // AADL.g:1034:5: ( ( ( '\\r' )? '\\n' ) )
            // AADL.g:1034:7: ( ( '\\r' )? '\\n' )
            {
            // AADL.g:1034:7: ( ( '\\r' )? '\\n' )
            // AADL.g:1034:8: ( '\\r' )? '\\n'
            {
            // AADL.g:1034:8: ( '\\r' )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0=='\r') ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // AADL.g:1034:8: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end NEWLINE

    public void mTokens() throws RecognitionException {
        // AADL.g:1:8: ( T102 | T103 | T104 | T105 | T106 | T107 | T108 | T109 | T110 | T111 | T112 | T113 | T114 | T115 | T116 | T117 | T118 | T119 | T120 | T121 | T122 | T123 | T124 | T125 | SIF | SELSE | CMDARG | COLON | PLUS | MINUS | STAR | SLASH | SHARP | VBAR | AMPER | LESS | GREATER | ASSIGN | PERCENT | NOT | HAT | BAR | EQUAL | NOTEQUAL | LESSEQUAL | GREATEREQUAL | DOUBLESTAR | ITERATOR | FINPUT | FOUTPUT | SCOPE | FUNCTION | PROGRAM | RETURN | BREAK | ID_PACKAGE | CONST | HexLiteral | IntegerLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | NamedOperator | Identifier | WS | COMMENT | LINE_COMMENT | JAVA_ACTION | JAVA_HEADER_ACTION )
        int alt35=70;
        alt35 = dfa35.predict(input);
        switch (alt35) {
            case 1 :
                // AADL.g:1:10: T102
                {
                mT102(); 

                }
                break;
            case 2 :
                // AADL.g:1:15: T103
                {
                mT103(); 

                }
                break;
            case 3 :
                // AADL.g:1:20: T104
                {
                mT104(); 

                }
                break;
            case 4 :
                // AADL.g:1:25: T105
                {
                mT105(); 

                }
                break;
            case 5 :
                // AADL.g:1:30: T106
                {
                mT106(); 

                }
                break;
            case 6 :
                // AADL.g:1:35: T107
                {
                mT107(); 

                }
                break;
            case 7 :
                // AADL.g:1:40: T108
                {
                mT108(); 

                }
                break;
            case 8 :
                // AADL.g:1:45: T109
                {
                mT109(); 

                }
                break;
            case 9 :
                // AADL.g:1:50: T110
                {
                mT110(); 

                }
                break;
            case 10 :
                // AADL.g:1:55: T111
                {
                mT111(); 

                }
                break;
            case 11 :
                // AADL.g:1:60: T112
                {
                mT112(); 

                }
                break;
            case 12 :
                // AADL.g:1:65: T113
                {
                mT113(); 

                }
                break;
            case 13 :
                // AADL.g:1:70: T114
                {
                mT114(); 

                }
                break;
            case 14 :
                // AADL.g:1:75: T115
                {
                mT115(); 

                }
                break;
            case 15 :
                // AADL.g:1:80: T116
                {
                mT116(); 

                }
                break;
            case 16 :
                // AADL.g:1:85: T117
                {
                mT117(); 

                }
                break;
            case 17 :
                // AADL.g:1:90: T118
                {
                mT118(); 

                }
                break;
            case 18 :
                // AADL.g:1:95: T119
                {
                mT119(); 

                }
                break;
            case 19 :
                // AADL.g:1:100: T120
                {
                mT120(); 

                }
                break;
            case 20 :
                // AADL.g:1:105: T121
                {
                mT121(); 

                }
                break;
            case 21 :
                // AADL.g:1:110: T122
                {
                mT122(); 

                }
                break;
            case 22 :
                // AADL.g:1:115: T123
                {
                mT123(); 

                }
                break;
            case 23 :
                // AADL.g:1:120: T124
                {
                mT124(); 

                }
                break;
            case 24 :
                // AADL.g:1:125: T125
                {
                mT125(); 

                }
                break;
            case 25 :
                // AADL.g:1:130: SIF
                {
                mSIF(); 

                }
                break;
            case 26 :
                // AADL.g:1:134: SELSE
                {
                mSELSE(); 

                }
                break;
            case 27 :
                // AADL.g:1:140: CMDARG
                {
                mCMDARG(); 

                }
                break;
            case 28 :
                // AADL.g:1:147: COLON
                {
                mCOLON(); 

                }
                break;
            case 29 :
                // AADL.g:1:153: PLUS
                {
                mPLUS(); 

                }
                break;
            case 30 :
                // AADL.g:1:158: MINUS
                {
                mMINUS(); 

                }
                break;
            case 31 :
                // AADL.g:1:164: STAR
                {
                mSTAR(); 

                }
                break;
            case 32 :
                // AADL.g:1:169: SLASH
                {
                mSLASH(); 

                }
                break;
            case 33 :
                // AADL.g:1:175: SHARP
                {
                mSHARP(); 

                }
                break;
            case 34 :
                // AADL.g:1:181: VBAR
                {
                mVBAR(); 

                }
                break;
            case 35 :
                // AADL.g:1:186: AMPER
                {
                mAMPER(); 

                }
                break;
            case 36 :
                // AADL.g:1:192: LESS
                {
                mLESS(); 

                }
                break;
            case 37 :
                // AADL.g:1:197: GREATER
                {
                mGREATER(); 

                }
                break;
            case 38 :
                // AADL.g:1:205: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 39 :
                // AADL.g:1:212: PERCENT
                {
                mPERCENT(); 

                }
                break;
            case 40 :
                // AADL.g:1:220: NOT
                {
                mNOT(); 

                }
                break;
            case 41 :
                // AADL.g:1:224: HAT
                {
                mHAT(); 

                }
                break;
            case 42 :
                // AADL.g:1:228: BAR
                {
                mBAR(); 

                }
                break;
            case 43 :
                // AADL.g:1:232: EQUAL
                {
                mEQUAL(); 

                }
                break;
            case 44 :
                // AADL.g:1:238: NOTEQUAL
                {
                mNOTEQUAL(); 

                }
                break;
            case 45 :
                // AADL.g:1:247: LESSEQUAL
                {
                mLESSEQUAL(); 

                }
                break;
            case 46 :
                // AADL.g:1:257: GREATEREQUAL
                {
                mGREATEREQUAL(); 

                }
                break;
            case 47 :
                // AADL.g:1:270: DOUBLESTAR
                {
                mDOUBLESTAR(); 

                }
                break;
            case 48 :
                // AADL.g:1:281: ITERATOR
                {
                mITERATOR(); 

                }
                break;
            case 49 :
                // AADL.g:1:290: FINPUT
                {
                mFINPUT(); 

                }
                break;
            case 50 :
                // AADL.g:1:297: FOUTPUT
                {
                mFOUTPUT(); 

                }
                break;
            case 51 :
                // AADL.g:1:305: SCOPE
                {
                mSCOPE(); 

                }
                break;
            case 52 :
                // AADL.g:1:311: FUNCTION
                {
                mFUNCTION(); 

                }
                break;
            case 53 :
                // AADL.g:1:320: PROGRAM
                {
                mPROGRAM(); 

                }
                break;
            case 54 :
                // AADL.g:1:328: RETURN
                {
                mRETURN(); 

                }
                break;
            case 55 :
                // AADL.g:1:335: BREAK
                {
                mBREAK(); 

                }
                break;
            case 56 :
                // AADL.g:1:341: ID_PACKAGE
                {
                mID_PACKAGE(); 

                }
                break;
            case 57 :
                // AADL.g:1:352: CONST
                {
                mCONST(); 

                }
                break;
            case 58 :
                // AADL.g:1:358: HexLiteral
                {
                mHexLiteral(); 

                }
                break;
            case 59 :
                // AADL.g:1:369: IntegerLiteral
                {
                mIntegerLiteral(); 

                }
                break;
            case 60 :
                // AADL.g:1:384: OctalLiteral
                {
                mOctalLiteral(); 

                }
                break;
            case 61 :
                // AADL.g:1:397: FloatingPointLiteral
                {
                mFloatingPointLiteral(); 

                }
                break;
            case 62 :
                // AADL.g:1:418: CharacterLiteral
                {
                mCharacterLiteral(); 

                }
                break;
            case 63 :
                // AADL.g:1:435: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 64 :
                // AADL.g:1:449: NamedOperator
                {
                mNamedOperator(); 

                }
                break;
            case 65 :
                // AADL.g:1:463: Identifier
                {
                mIdentifier(); 

                }
                break;
            case 66 :
                // AADL.g:1:474: WS
                {
                mWS(); 

                }
                break;
            case 67 :
                // AADL.g:1:477: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 68 :
                // AADL.g:1:485: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;
            case 69 :
                // AADL.g:1:498: JAVA_ACTION
                {
                mJAVA_ACTION(); 

                }
                break;
            case 70 :
                // AADL.g:1:510: JAVA_HEADER_ACTION
                {
                mJAVA_HEADER_ACTION(); 

                }
                break;

        }

    }


    protected DFA35 dfa35 = new DFA35(this);
    static final String DFA35_eotS =
        "\7\uffff\1\52\1\56\1\60\1\63\1\64\1\52\2\uffff\4\52\1\103\1\106"+
        "\3\52\1\uffff\1\115\1\uffff\1\117\1\121\1\124\1\uffff\1\126\1\130"+
        "\1\132\2\uffff\2\52\2\137\4\uffff\1\52\11\uffff\12\52\1\155\6\uffff"+
        "\3\52\1\161\1\52\17\uffff\2\52\3\uffff\1\137\1\165\12\52\2\uffff"+
        "\3\52\1\uffff\3\52\1\uffff\1\u0086\2\52\1\u0089\5\52\1\u008f\3\52"+
        "\1\u0093\2\52\1\uffff\1\52\1\u0097\1\uffff\3\52\1\u009b\1\52\1\uffff"+
        "\3\52\1\uffff\1\52\1\u00a1\1\52\1\uffff\1\u00a3\2\52\1\uffff\1\52"+
        "\1\u00a7\2\52\1\u00aa\1\uffff\1\u00ab\1\uffff\1\u00ac\1\u00ad\1"+
        "\52\1\uffff\1\u00af\1\u00b0\4\uffff\1\u00b1\3\uffff";
    static final String DFA35_eofS =
        "\u00b2\uffff";
    static final String DFA35_minS =
        "\1\11\6\uffff\1\141\1\174\1\46\1\150\1\60\1\141\2\uffff\1\162\1"+
        "\155\1\141\1\165\1\55\1\75\1\141\1\146\1\154\1\uffff\1\72\1\uffff"+
        "\1\76\2\52\1\uffff\1\75\1\101\1\75\2\uffff\1\145\1\162\2\56\4\uffff"+
        "\1\162\11\uffff\1\163\1\166\1\156\1\165\1\160\1\164\2\154\1\156"+
        "\1\154\1\55\6\uffff\1\142\1\157\1\143\1\60\1\163\17\uffff\1\164"+
        "\1\145\3\uffff\1\56\1\60\1\164\1\106\1\163\2\145\2\106\1\163\1\143"+
        "\1\154\2\uffff\1\154\1\147\1\153\1\uffff\1\145\1\165\1\141\1\uffff"+
        "\1\60\1\151\1\164\1\60\1\157\2\151\1\145\1\164\1\60\1\151\1\162"+
        "\1\141\1\60\1\162\1\153\1\uffff\1\154\1\60\1\uffff\1\146\2\154\1"+
        "\60\1\151\1\uffff\1\143\1\141\1\147\1\uffff\1\156\1\60\1\145\1\uffff"+
        "\1\60\2\145\1\uffff\1\157\1\60\1\155\1\145\1\60\1\uffff\1\60\1\uffff"+
        "\2\60\1\156\1\uffff\2\60\4\uffff\1\60\3\uffff";
    static final String DFA35_maxS =
        "\1\176\6\uffff\1\141\1\174\1\46\1\173\1\71\1\163\2\uffff\1\171\1"+
        "\155\2\165\1\75\1\76\1\165\1\146\1\154\1\uffff\1\72\1\uffff\1\76"+
        "\1\52\1\57\1\uffff\1\75\1\172\1\75\2\uffff\1\145\1\162\1\170\1\146"+
        "\4\uffff\1\162\11\uffff\1\163\1\166\1\156\1\165\1\160\1\164\2\154"+
        "\1\156\1\154\1\55\6\uffff\1\142\1\157\1\143\1\172\1\163\17\uffff"+
        "\1\164\1\145\3\uffff\1\146\1\172\1\164\1\106\1\163\2\145\2\106\1"+
        "\163\1\143\1\154\2\uffff\1\154\1\147\1\153\1\uffff\1\145\1\165\1"+
        "\141\1\uffff\1\172\1\151\1\164\1\172\1\157\2\151\1\145\1\164\1\172"+
        "\1\151\1\162\1\141\1\172\1\162\1\153\1\uffff\1\154\1\172\1\uffff"+
        "\1\146\2\154\1\172\1\151\1\uffff\1\143\1\141\1\147\1\uffff\1\156"+
        "\1\172\1\145\1\uffff\1\172\2\145\1\uffff\1\157\1\172\1\155\1\145"+
        "\1\172\1\uffff\1\172\1\uffff\2\172\1\156\1\uffff\2\172\4\uffff\1"+
        "\172\3\uffff";
    static final String DFA35_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\6\uffff\1\15\1\16\11\uffff\1\33"+
        "\1\uffff\1\35\3\uffff\1\41\3\uffff\1\51\1\52\4\uffff\1\76\1\77\1"+
        "\101\1\102\1\uffff\1\10\1\42\1\11\1\43\1\105\1\106\1\12\1\13\1\75"+
        "\13\uffff\1\60\1\55\1\44\1\27\1\56\1\45\5\uffff\1\63\1\34\1\62\1"+
        "\36\1\57\1\37\1\103\1\104\1\40\1\53\1\46\1\100\1\47\1\54\1\50\2"+
        "\uffff\1\72\1\74\1\73\14\uffff\1\61\1\26\3\uffff\1\31\3\uffff\1"+
        "\7\20\uffff\1\14\2\uffff\1\23\5\uffff\1\25\3\uffff\1\32\3\uffff"+
        "\1\71\3\uffff\1\24\5\uffff\1\67\1\uffff\1\17\3\uffff\1\30\2\uffff"+
        "\1\66\1\21\1\20\1\22\1\uffff\1\65\1\70\1\64";
    static final String DFA35_specialS =
        "\u00b2\uffff}>";
    static final String[] DFA35_transitionS = {
            "\2\53\2\uffff\1\53\22\uffff\1\53\1\41\1\51\1\36\1\30\1\40\1"+
            "\11\1\50\1\4\1\5\1\34\1\32\1\6\1\33\1\13\1\35\1\46\11\47\1\31"+
            "\1\1\1\23\1\37\1\24\1\uffff\1\12\32\52\1\15\1\uffff\1\16\1\42"+
            "\1\52\1\uffff\1\52\1\45\1\14\1\52\1\27\1\21\2\52\1\26\4\52\1"+
            "\22\1\52\1\25\1\52\1\44\1\52\1\17\1\52\1\7\1\52\1\20\2\52\1"+
            "\2\1\10\1\3\1\43",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\54",
            "\1\55",
            "\1\57",
            "\1\62\22\uffff\1\61",
            "\12\65",
            "\1\66\15\uffff\1\70\3\uffff\1\67",
            "",
            "",
            "\1\71\5\uffff\1\73\1\72",
            "\1\74",
            "\1\75\23\uffff\1\76",
            "\1\77",
            "\1\101\16\uffff\1\100\1\102",
            "\1\105\1\104",
            "\1\111\20\uffff\1\110\2\uffff\1\107",
            "\1\112",
            "\1\113",
            "",
            "\1\114",
            "",
            "\1\116",
            "\1\120",
            "\1\122\4\uffff\1\123",
            "",
            "\1\125",
            "\32\127\4\uffff\1\127\1\uffff\32\127",
            "\1\131",
            "",
            "",
            "\1\133",
            "\1\134",
            "\1\65\1\uffff\10\136\14\uffff\3\65\21\uffff\1\135\13\uffff\3"+
            "\65\21\uffff\1\135",
            "\1\65\1\uffff\12\140\12\uffff\3\65\35\uffff\3\65",
            "",
            "",
            "",
            "",
            "\1\141",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\156",
            "\1\157",
            "\1\160",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\162",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\163",
            "\1\164",
            "",
            "",
            "",
            "\1\65\1\uffff\12\140\12\uffff\3\65\35\uffff\3\65",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\166",
            "\1\167",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "\1\176",
            "\1\177",
            "",
            "",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "",
            "\1\u0083",
            "\1\u0084",
            "\1\u0085",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u0087",
            "\1\u0088",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u0094",
            "\1\u0095",
            "",
            "\1\u0096",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u009c",
            "",
            "\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "",
            "\1\u00a0",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u00a2",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u00a4",
            "\1\u00a5",
            "",
            "\1\u00a6",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u00a8",
            "\1\u00a9",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\u00ae",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "",
            "",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T102 | T103 | T104 | T105 | T106 | T107 | T108 | T109 | T110 | T111 | T112 | T113 | T114 | T115 | T116 | T117 | T118 | T119 | T120 | T121 | T122 | T123 | T124 | T125 | SIF | SELSE | CMDARG | COLON | PLUS | MINUS | STAR | SLASH | SHARP | VBAR | AMPER | LESS | GREATER | ASSIGN | PERCENT | NOT | HAT | BAR | EQUAL | NOTEQUAL | LESSEQUAL | GREATEREQUAL | DOUBLESTAR | ITERATOR | FINPUT | FOUTPUT | SCOPE | FUNCTION | PROGRAM | RETURN | BREAK | ID_PACKAGE | CONST | HexLiteral | IntegerLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | NamedOperator | Identifier | WS | COMMENT | LINE_COMMENT | JAVA_ACTION | JAVA_HEADER_ACTION );";
        }
    }
 

}