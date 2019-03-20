// $ANTLR 3.0.1 AADLWalker.g 2011-05-25 18:40:57

package ssac.aadlc.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLFileOptionEntry;
import ssac.aadlc.analysis.type.func.AADLFuncType;
import ssac.aadlc.codegen.*;
import ssac.aadlc.codegen.define.*;
import ssac.aadlc.codegen.expression.*;
import ssac.aadlc.codegen.stms.*;
import ssac.aadlc.io.ReportPrinter;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/**
 * AADL(Algebraic Accounting Description Language)
 *
 * AADL Tree Walker
 *
 * @version 1.70 2011/05/25
 **/
public class AADLWalker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AADL_INFO", "AADL_NAME", "AADL_ARGS", "AADL_FUNCS", "AADL_PROGRAM", "AADL_CONSTS", "FCALL_RM", "FCALL_SM", "FUNCALL_PATH", "FUNCALL_ARGS", "FCALL_IM", "FCALL_MM", "FUNCDEF_RETURN", "FUNCDEF_PARAMS", "IFCOND", "BLOCK", "BODY", "JAVACLASSPATH", "VARTYPE", "VARDEF", "VARINIT", "VARNEW", "ITERATE", "ALIAS", "INVOLV", "VOIDINVOLV", "INVOLV_EXP", "INVOLV_COND", "INVOLV_BLOCK", "EXBASE", "DTBASE", "ARRAY", "HASH", "AINDEX", "FOPTION", "CAST", "INSTANCEOF", "MODIFIERS", "JAVA_HEADER_ACTION", "ID_PACKAGE", "JAVA_ACTION", "PROGRAM", "Identifier", "FUNCTION", "CONST", "FINPUT", "FOUTPUT", "BREAK", "RETURN", "SIF", "SELSE", "NamedOperator", "HexLiteral", "OctalLiteral", "IntegerLiteral", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "CMDARG", "Letter", "Digit", "COLON", "PLUS", "MINUS", "STAR", "SLASH", "SHARP", "VBAR", "AMPER", "LESS", "GREATER", "ASSIGN", "PERCENT", "NOT", "HAT", "BAR", "EQUAL", "NOTEQUAL", "LESSEQUAL", "GREATEREQUAL", "DOUBLESTAR", "ITERATOR", "SCOPE", "InvalidBaseKeyLetter", "BaseKeyLetters", "WHITESPACE", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "ID", "UnicodeEscape", "OctalEscape", "WS", "COMMENT", "LINE_COMMENT", "NEWLINE", "';'", "'{'", "'}'", "'('", "')'", "','", "'var'", "'||'", "'&&'", "'@'", "'.'", "'cast'", "'['", "']'", "'typeof'", "'txtFile'", "'csvFile'", "'xmlFile'", "'true'", "'false'", "'null'", "'<<'", "'>>'", "'public'", "'static'", "'?'"
    };
    public static final int FUNCTION=47;
    public static final int CAST=39;
    public static final int VOIDINVOLV=29;
    public static final int STAR=68;
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
    public static final int AADL_FUNCS=7;
    public static final int AINDEX=37;
    public static final int BREAK=51;
    public static final int Identifier=46;
    public static final int NOTEQUAL=81;
    public static final int HAT=78;
    public static final int VBAR=71;
    public static final int FUNCDEF_RETURN=16;
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
    public static final int ID_PACKAGE=43;
    public static final int JAVA_HEADER_ACTION=42;
    public static final int FCALL_MM=15;
    public static final int WS=98;
    public static final int FloatingPointLiteral=59;
    public static final int ALIAS=27;
    public static final int LESSEQUAL=82;
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

        public AADLWalker(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "AADLWalker.g"; }

    
    //--- begin [@members]
    // Analyzer
    private AADLAnalyzer analyzer = new AADLAnalyzer();
    
    private int indentLevel = 0;
    private boolean hasErrorOccured = false;
    
    public AADLAnalyzer getAnalyzer() {
    	return analyzer;
    }
    
    // Analysis
    public void setAnalyzer(AADLAnalyzer newAnalyzer) {
    	analyzer = newAnalyzer;
    }
    
    private void setClassName(String name) {
    	analyzer.setAadlClassName(name);
    	emitDebugMessage("aadlClassName=" + name);
    }
    
    private void incrementIndent() {
    	indentLevel++;
    	emitDebugMessage("indent level[" + indentLevel + "](U)");
    }
    
    private void decrementIndent() {
    	indentLevel--;
    	emitDebugMessage("indent level[" + indentLevel + "](D)");
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
    
    // Error report for CompileException
    public void reportError(CompileException e) {
    	// if we've already reported an error and have not matched a token
    	// yet successfully, don't report any errors.
    	if ( errorRecovery ) {
    		//System.err.print("[SPURIOUS] ");
    		return;
    	}
    	errorRecovery = true;
    
    	displayRecognitionError(this.getTokenNames(), e);
    }
    
    public void displayRecognitionError(String[] tokenNames, CompileException e)
    {
    	hasErrorOccured = true;
    	//String hdr = getErrorHeader(e);
    	//String msg = getErrorMessage(e, tokenNames);
    	//emitErrorMessage(hdr+" "+msg);
    	emitErrorMessage(sourceFileName + ":" + e.getMessage());
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
    //--- end [@members]



    // $ANTLR start program
    // AADLWalker.g:219:1: program : programInfo programBody ;
    public final void program() throws RecognitionException {
        try {
            // AADLWalker.g:220:5: ( programInfo programBody )
            // AADLWalker.g:220:7: programInfo programBody
            {
            pushFollow(FOLLOW_programInfo_in_program64);
            programInfo();
            _fsp--;

            pushFollow(FOLLOW_programBody_in_program66);
            programBody();
            _fsp--;


            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end program


    // $ANTLR start programInfo
    // AADLWalker.g:223:1: programInfo : ^( AADL_INFO AADL_NAME infoCmdArgs infoFuncDefs ) ;
    public final void programInfo() throws RecognitionException {
        CommonTree AADL_NAME1=null;

        try {
            // AADLWalker.g:224:5: ( ^( AADL_INFO AADL_NAME infoCmdArgs infoFuncDefs ) )
            // AADLWalker.g:224:7: ^( AADL_INFO AADL_NAME infoCmdArgs infoFuncDefs )
            {
            match(input,AADL_INFO,FOLLOW_AADL_INFO_in_programInfo84); 

            match(input, Token.DOWN, null); 
            AADL_NAME1=(CommonTree)input.LT(1);
            match(input,AADL_NAME,FOLLOW_AADL_NAME_in_programInfo86); 
            setClassName(AADL_NAME1.getText());
            pushFollow(FOLLOW_infoCmdArgs_in_programInfo90);
            infoCmdArgs();
            _fsp--;

            pushFollow(FOLLOW_infoFuncDefs_in_programInfo92);
            infoFuncDefs();
            _fsp--;


            match(input, Token.UP, null); 

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end programInfo


    // $ANTLR start programBody
    // AADLWalker.g:227:1: programBody : ^( AADL_PROGRAM ( packageDeclaration )? (jha= javaHeaderAction )* ( constClassVariables )? ( subModule )? mm= mainModule ) ;
    public final void programBody() throws RecognitionException {
        ACodeJavaAction jha = null;

        ACodeModule mm = null;


        
        //--- begin [@init]
        analyzer.getJavaProgram().addMainCode(ACodeModule.buildMainClassBegin(analyzer));
        //--- end [@init]

        try {
            // AADLWalker.g:238:5: ( ^( AADL_PROGRAM ( packageDeclaration )? (jha= javaHeaderAction )* ( constClassVariables )? ( subModule )? mm= mainModule ) )
            // AADLWalker.g:238:7: ^( AADL_PROGRAM ( packageDeclaration )? (jha= javaHeaderAction )* ( constClassVariables )? ( subModule )? mm= mainModule )
            {
            match(input,AADL_PROGRAM,FOLLOW_AADL_PROGRAM_in_programBody119); 

            match(input, Token.DOWN, null); 
            // AADLWalker.g:239:7: ( packageDeclaration )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID_PACKAGE) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // AADLWalker.g:239:7: packageDeclaration
                    {
                    pushFollow(FOLLOW_packageDeclaration_in_programBody127);
                    packageDeclaration();
                    _fsp--;


                    }
                    break;

            }

            // AADLWalker.g:240:7: (jha= javaHeaderAction )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==JAVA_HEADER_ACTION) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // AADLWalker.g:240:8: jha= javaHeaderAction
            	    {
            	    pushFollow(FOLLOW_javaHeaderAction_in_programBody139);
            	    jha=javaHeaderAction();
            	    _fsp--;

            	    analyzer.getJavaProgram().addHeaderCode(jha);

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // AADLWalker.g:241:7: ( constClassVariables )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==AADL_CONSTS) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // AADLWalker.g:241:7: constClassVariables
                    {
                    pushFollow(FOLLOW_constClassVariables_in_programBody150);
                    constClassVariables();
                    _fsp--;


                    }
                    break;

            }

            // AADLWalker.g:242:7: ( subModule )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==JAVA_ACTION||LA4_0==FUNCTION) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // AADLWalker.g:242:7: subModule
                    {
                    pushFollow(FOLLOW_subModule_in_programBody159);
                    subModule();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_mainModule_in_programBody170);
            mm=mainModule();
            _fsp--;

            analyzer.getJavaProgram().addMainCode(mm);

            match(input, Token.UP, null); 

            }

            
            //--- begin [@after]
            analyzer.getJavaProgram().addMainCode(ACodeModule.buildMainClassEnd(analyzer));
            //--- end [@after]

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end programBody


    // $ANTLR start packageDeclaration
    // AADLWalker.g:247:1: packageDeclaration : ( ^( ID_PACKAGE Identifier ) | ^( ID_PACKAGE javaClassPath ) );
    public final void packageDeclaration() throws RecognitionException {
        CommonTree ID_PACKAGE2=null;
        CommonTree Identifier3=null;
        CommonTree ID_PACKAGE4=null;
        List<Token> javaClassPath5 = null;


        
        //--- begin packageDeclaration [@init]
        analyzer.beginDeclarationBlockParsing();
        //--- end packageDeclaration [@init]

        try {
            // AADLWalker.g:253:5: ( ^( ID_PACKAGE Identifier ) | ^( ID_PACKAGE javaClassPath ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ID_PACKAGE) ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==DOWN) ) {
                    int LA5_2 = input.LA(3);

                    if ( (LA5_2==Identifier) ) {
                        alt5=1;
                    }
                    else if ( (LA5_2==JAVACLASSPATH) ) {
                        alt5=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("247:1: packageDeclaration : ( ^( ID_PACKAGE Identifier ) | ^( ID_PACKAGE javaClassPath ) );", 5, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("247:1: packageDeclaration : ( ^( ID_PACKAGE Identifier ) | ^( ID_PACKAGE javaClassPath ) );", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("247:1: packageDeclaration : ( ^( ID_PACKAGE Identifier ) | ^( ID_PACKAGE javaClassPath ) );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // AADLWalker.g:253:7: ^( ID_PACKAGE Identifier )
                    {
                    ID_PACKAGE2=(CommonTree)input.LT(1);
                    match(input,ID_PACKAGE,FOLLOW_ID_PACKAGE_in_packageDeclaration200); 

                    match(input, Token.DOWN, null); 
                    Identifier3=(CommonTree)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_packageDeclaration202); 

                    match(input, Token.UP, null); 
                    analyzer.getJavaProgram().setPackageCode(ACodePackage.buildPackageDeclaration(analyzer, ID_PACKAGE2.token, Identifier3.token));

                    }
                    break;
                case 2 :
                    // AADLWalker.g:255:7: ^( ID_PACKAGE javaClassPath )
                    {
                    ID_PACKAGE4=(CommonTree)input.LT(1);
                    match(input,ID_PACKAGE,FOLLOW_ID_PACKAGE_in_packageDeclaration220); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_javaClassPath_in_packageDeclaration222);
                    javaClassPath5=javaClassPath();
                    _fsp--;


                    match(input, Token.UP, null); 
                    analyzer.getJavaProgram().setPackageCode(ACodePackage.buildPackageDeclaration(analyzer, ID_PACKAGE4.token, javaClassPath5));

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin packageDeclaration [@finally]
            	analyzer.endDeclarationBlockParsing();
            	//--- begin packageDeclaration [@finally]
                
        }
        return ;
    }
    // $ANTLR end packageDeclaration


    // $ANTLR start constClassVariables
    // AADLWalker.g:264:1: constClassVariables : ^( AADL_CONSTS ( constClassVariableDeclaration )? ) ;
    public final void constClassVariables() throws RecognitionException {
        try {
            // AADLWalker.g:265:5: ( ^( AADL_CONSTS ( constClassVariableDeclaration )? ) )
            // AADLWalker.g:265:7: ^( AADL_CONSTS ( constClassVariableDeclaration )? )
            {
            match(input,AADL_CONSTS,FOLLOW_AADL_CONSTS_in_constClassVariables257); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:265:21: ( constClassVariableDeclaration )?
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==CONST) ) {
                    alt6=1;
                }
                switch (alt6) {
                    case 1 :
                        // AADLWalker.g:265:21: constClassVariableDeclaration
                        {
                        pushFollow(FOLLOW_constClassVariableDeclaration_in_constClassVariables259);
                        constClassVariableDeclaration();
                        _fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end constClassVariables


    // $ANTLR start constClassVariableDeclaration
    // AADLWalker.g:268:1: constClassVariableDeclaration : (cv= constVariableDeclaration )+ ;
    public final void constClassVariableDeclaration() throws RecognitionException {
        ACodeInitVariable cv = null;


        
        //--- begin constClassVariableDeclaration [@init]
        analyzer.beginDeclarationBlockParsing();
        //--- end constClassVariableDeclaration [@init]

        try {
            // AADLWalker.g:274:5: ( (cv= constVariableDeclaration )+ )
            // AADLWalker.g:274:7: (cv= constVariableDeclaration )+
            {
            // AADLWalker.g:274:7: (cv= constVariableDeclaration )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==CONST) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // AADLWalker.g:274:8: cv= constVariableDeclaration
            	    {
            	    pushFollow(FOLLOW_constVariableDeclaration_in_constClassVariableDeclaration285);
            	    cv=constVariableDeclaration();
            	    _fsp--;

            	    analyzer.addConstructionCode(cv);

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin constClassVariableDeclaration [@finally]
            	analyzer.endDeclarationBlockParsing();
            	//--- begin constClassVariableDeclaration [@finally]
                
        }
        return ;
    }
    // $ANTLR end constClassVariableDeclaration


    // $ANTLR start subModule
    // AADLWalker.g:282:1: subModule : (ja= javaAction | fn= function )+ ;
    public final void subModule() throws RecognitionException {
        ACodeJavaAction ja = null;

        ACodeModule fn = null;


        try {
            // AADLWalker.g:283:5: ( (ja= javaAction | fn= function )+ )
            // AADLWalker.g:283:7: (ja= javaAction | fn= function )+
            {
            // AADLWalker.g:283:7: (ja= javaAction | fn= function )+
            int cnt8=0;
            loop8:
            do {
                int alt8=3;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==JAVA_ACTION) ) {
                    alt8=1;
                }
                else if ( (LA8_0==FUNCTION) ) {
                    alt8=2;
                }


                switch (alt8) {
            	case 1 :
            	    // AADLWalker.g:283:9: ja= javaAction
            	    {
            	    pushFollow(FOLLOW_javaAction_in_subModule318);
            	    ja=javaAction();
            	    _fsp--;

            	    analyzer.getJavaProgram().addMainCode(ja);

            	    }
            	    break;
            	case 2 :
            	    // AADLWalker.g:284:8: fn= function
            	    {
            	    pushFollow(FOLLOW_function_in_subModule331);
            	    fn=function();
            	    _fsp--;

            	    analyzer.getJavaProgram().addMainCode(fn);

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


            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end subModule


    // $ANTLR start infoCmdArgs
    // AADLWalker.g:290:1: infoCmdArgs : ^( AADL_ARGS ( CMDARG )* ) ;
    public final void infoCmdArgs() throws RecognitionException {
        CommonTree CMDARG6=null;

        
        //--- begin [@init]
        ArrayList<Token> aryArgs = new ArrayList<Token>();
        //--- end [@init]

        try {
            // AADLWalker.g:310:5: ( ^( AADL_ARGS ( CMDARG )* ) )
            // AADLWalker.g:310:7: ^( AADL_ARGS ( CMDARG )* )
            {
            match(input,AADL_ARGS,FOLLOW_AADL_ARGS_in_infoCmdArgs371); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:310:19: ( CMDARG )*
                loop9:
                do {
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==CMDARG) ) {
                        alt9=1;
                    }


                    switch (alt9) {
                	case 1 :
                	    // AADLWalker.g:310:20: CMDARG
                	    {
                	    CMDARG6=(CommonTree)input.LT(1);
                	    match(input,CMDARG,FOLLOW_CMDARG_in_infoCmdArgs374); 
                	    aryArgs.add(CMDARG6.token);

                	    }
                	    break;

                	default :
                	    break loop9;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

            
            //--- begin [@after]
            Iterator<Token> it = aryArgs.iterator();
            while (it.hasNext()) {
            	try {
            		analyzer.setCommandArgSymbol(it.next());
            	}
            	catch (CompileException ex) {
            		// no report!
            		//reportError(ex);
            	}
            }
            //--- end [@after]

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end infoCmdArgs


    // $ANTLR start infoFuncDefs
    // AADLWalker.g:313:1: infoFuncDefs : ^( AADL_FUNCS ( infoFuncTypes[aryFuncs] )* ) ;
    public final void infoFuncDefs() throws RecognitionException {
        
        //--- begin infoFuncDefs [@init]
        analyzer.pushScope();
        ArrayList<AADLFuncType> aryFuncs = new ArrayList<AADLFuncType>();
        //--- end infoFuncDefs [@init]

        try {
            // AADLWalker.g:331:5: ( ^( AADL_FUNCS ( infoFuncTypes[aryFuncs] )* ) )
            // AADLWalker.g:331:7: ^( AADL_FUNCS ( infoFuncTypes[aryFuncs] )* )
            {
            match(input,AADL_FUNCS,FOLLOW_AADL_FUNCS_in_infoFuncDefs406); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:331:20: ( infoFuncTypes[aryFuncs] )*
                loop10:
                do {
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==Identifier) ) {
                        alt10=1;
                    }


                    switch (alt10) {
                	case 1 :
                	    // AADLWalker.g:331:21: infoFuncTypes[aryFuncs]
                	    {
                	    pushFollow(FOLLOW_infoFuncTypes_in_infoFuncDefs409);
                	    infoFuncTypes(aryFuncs);
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop10;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

            
            //--- begin infoFuncDefs [@after]
            Iterator<AADLFuncType> it = aryFuncs.iterator();
            while (it.hasNext()) {
            	AADLFuncType funcType = it.next();
            	if (funcType != null) {
            		analyzer.getFuncManager().setUserFunction(funcType);
            	}
            }
            //--- end infoFuncDefs [@after]

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin infoFuncDefs [@finally]
            	analyzer.popScope();
            	//--- end infoFuncDefs [@finally]
                
        }
        return ;
    }
    // $ANTLR end infoFuncDefs


    // $ANTLR start infoFuncTypes
    // AADLWalker.g:340:1: infoFuncTypes[ArrayList<AADLFuncType> funcs] : functionType ;
    public final void infoFuncTypes(ArrayList<AADLFuncType> funcs) throws RecognitionException {
        ACodeFunctionType functionType7 = null;


        
        //--- begin infoFuncTypes [@init]
        analyzer.pushScope();
        //--- end infoFuncTypes [@init]

        try {
            // AADLWalker.g:346:5: ( functionType )
            // AADLWalker.g:346:7: functionType
            {
            pushFollow(FOLLOW_functionType_in_infoFuncTypes449);
            functionType7=functionType();
            _fsp--;

            
                		if (functionType7 != null) {
                			funcs.add(functionType7.getFuncType());
                		}
                	

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin infoFuncTypes [@after]
            	analyzer.popScope();
            	//--- end infoFuncTypes [@after]
                
        }
        return ;
    }
    // $ANTLR end infoFuncTypes


    // $ANTLR start mainModule
    // AADLWalker.g:361:1: mainModule returns [ACodeModule acode] : ^( PROGRAM Identifier mainBody ) ;
    public final ACodeModule mainModule() throws RecognitionException {
        ACodeModule acode = null;

        CommonTree Identifier8=null;
        ACodeBlock mainBody9 = null;


        try {
            // AADLWalker.g:362:5: ( ^( PROGRAM Identifier mainBody ) )
            // AADLWalker.g:362:7: ^( PROGRAM Identifier mainBody )
            {
            match(input,PROGRAM,FOLLOW_PROGRAM_in_mainModule488); 

            match(input, Token.DOWN, null); 
            Identifier8=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_mainModule490); 
            pushFollow(FOLLOW_mainBody_in_mainModule492);
            mainBody9=mainBody();
            _fsp--;


            match(input, Token.UP, null); 
            acode = ACodeModule.buildMainModule(analyzer, Identifier8, mainBody9);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return acode;
    }
    // $ANTLR end mainModule


    // $ANTLR start mainBody
    // AADLWalker.g:366:1: mainBody returns [ACodeBlock aBlock] : ^( BODY ( statement )* ) ;
    public final ACodeBlock mainBody() throws RecognitionException {
        ACodeBlock aBlock = null;

        CommonTree BODY11=null;
        ACodeStatement statement10 = null;


        
        //--- begin mainBody [@init]
        incrementIndent();
        analyzer.beginProgramBlockParsing();
        List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
        //--- end mainBody [@init]

        try {
            // AADLWalker.g:374:5: ( ^( BODY ( statement )* ) )
            // AADLWalker.g:374:7: ^( BODY ( statement )* )
            {
            BODY11=(CommonTree)input.LT(1);
            match(input,BODY,FOLLOW_BODY_in_mainBody526); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:374:14: ( statement )*
                loop11:
                do {
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( ((LA11_0>=FCALL_RM && LA11_0<=FCALL_SM)||(LA11_0>=FCALL_IM && LA11_0<=FCALL_MM)||LA11_0==BLOCK||LA11_0==VARDEF||(LA11_0>=INVOLV && LA11_0<=VOIDINVOLV)||(LA11_0>=EXBASE && LA11_0<=ARRAY)||LA11_0==JAVA_ACTION||LA11_0==Identifier||LA11_0==CONST||(LA11_0>=BREAK && LA11_0<=SIF)||(LA11_0>=NamedOperator && LA11_0<=CMDARG)||(LA11_0>=PLUS && LA11_0<=SLASH)||(LA11_0>=LESS && LA11_0<=GREATER)||(LA11_0>=PERCENT && LA11_0<=GREATEREQUAL)||(LA11_0>=109 && LA11_0<=111)||LA11_0==113||LA11_0==116||(LA11_0>=120 && LA11_0<=122)||LA11_0==127) ) {
                        alt11=1;
                    }


                    switch (alt11) {
                	case 1 :
                	    // AADLWalker.g:374:15: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_mainBody529);
                	    statement10=statement();
                	    _fsp--;

                	    stmList.add(statement10);

                	    }
                	    break;

                	default :
                	    break loop11;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            aBlock = ACodeBlock.withoutBlock(analyzer, BODY11, stmList);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin mainBody [@finally]
            	analyzer.endProgramBlockParsing();
            	decrementIndent();
            	//--- begin mainBody [@finally]
                
        }
        return aBlock;
    }
    // $ANTLR end mainBody


    // $ANTLR start function
    // AADLWalker.g:389:1: function returns [ACodeModule acode] : ^( FUNCTION modifiers functionType functionBody ) ;
    public final ACodeModule function() throws RecognitionException {
        ACodeModule acode = null;

        CommonTree FUNCTION13=null;
        List<Token> modifiers12 = null;

        ACodeFunctionType functionType14 = null;

        ACodeBlock functionBody15 = null;


        
        //--- begin function [@init]
        analyzer.pushScope();
        //--- end function [@init]

        try {
            // AADLWalker.g:395:5: ( ^( FUNCTION modifiers functionType functionBody ) )
            // AADLWalker.g:395:7: ^( FUNCTION modifiers functionType functionBody )
            {
            FUNCTION13=(CommonTree)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function579); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_modifiers_in_function581);
            modifiers12=modifiers();
            _fsp--;

            pushFollow(FOLLOW_functionType_in_function583);
            functionType14=functionType();
            _fsp--;

            pushFollow(FOLLOW_functionBody_in_function585);
            functionBody15=functionBody();
            _fsp--;


            match(input, Token.UP, null); 
            acode = ACodeModule.buildFunctionModule(analyzer, modifiers12, FUNCTION13, functionType14, functionBody15);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin function [@finally]
            	analyzer.popScope();
            	//--- end function [@finally]
                
        }
        return acode;
    }
    // $ANTLR end function


    // $ANTLR start modifiers
    // AADLWalker.g:422:1: modifiers returns [List<Token> list] : ^( MODIFIERS (t= ( 'public' | 'static' ) )* ) ;
    public final List<Token> modifiers() throws RecognitionException {
        List<Token> list = null;

        CommonTree t=null;

        
        //--- begin function [@init]
        list = new ArrayList<Token>();
        //--- end function [@init]

        try {
            // AADLWalker.g:428:5: ( ^( MODIFIERS (t= ( 'public' | 'static' ) )* ) )
            // AADLWalker.g:428:7: ^( MODIFIERS (t= ( 'public' | 'static' ) )* )
            {
            match(input,MODIFIERS,FOLLOW_MODIFIERS_in_modifiers631); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:428:19: (t= ( 'public' | 'static' ) )*
                loop12:
                do {
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( ((LA12_0>=125 && LA12_0<=126)) ) {
                        alt12=1;
                    }


                    switch (alt12) {
                	case 1 :
                	    // AADLWalker.g:429:6: t= ( 'public' | 'static' )
                	    {
                	    t=(CommonTree)input.LT(1);
                	    if ( (input.LA(1)>=125 && input.LA(1)<=126) ) {
                	        input.consume();
                	        errorRecovery=false;
                	    }
                	    else {
                	        MismatchedSetException mse =
                	            new MismatchedSetException(null,input);
                	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_modifiers642);    throw mse;
                	    }

                	    list.add(t.token);

                	    }
                	    break;

                	default :
                	    break loop12;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return list;
    }
    // $ANTLR end modifiers


    // $ANTLR start functionType
    // AADLWalker.g:438:1: functionType returns [ACodeFunctionType aFuncType] : ^( Identifier ( functionReturnDeclaration )? ( functionFormalParameters[funcParams] )? ) ;
    public final ACodeFunctionType functionType() throws RecognitionException {
        ACodeFunctionType aFuncType = null;

        CommonTree Identifier16=null;
        ACodeDataType functionReturnDeclaration17 = null;


        
        //--- begin [@init]
        List<ACodeVariable> funcParams = new ArrayList<ACodeVariable>();
        //--- end [@init]

        try {
            // AADLWalker.g:448:5: ( ^( Identifier ( functionReturnDeclaration )? ( functionFormalParameters[funcParams] )? ) )
            // AADLWalker.g:448:7: ^( Identifier ( functionReturnDeclaration )? ( functionFormalParameters[funcParams] )? )
            {
            Identifier16=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_functionType713); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:448:20: ( functionReturnDeclaration )?
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==FUNCDEF_RETURN) ) {
                    alt13=1;
                }
                switch (alt13) {
                    case 1 :
                        // AADLWalker.g:448:20: functionReturnDeclaration
                        {
                        pushFollow(FOLLOW_functionReturnDeclaration_in_functionType715);
                        functionReturnDeclaration17=functionReturnDeclaration();
                        _fsp--;


                        }
                        break;

                }

                // AADLWalker.g:448:47: ( functionFormalParameters[funcParams] )?
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==FUNCDEF_PARAMS) ) {
                    alt14=1;
                }
                switch (alt14) {
                    case 1 :
                        // AADLWalker.g:448:48: functionFormalParameters[funcParams]
                        {
                        pushFollow(FOLLOW_functionFormalParameters_in_functionType719);
                        functionFormalParameters(funcParams);
                        _fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }
            aFuncType = ACodeFunctionType.buildFunctionType(analyzer, Identifier16.token, functionReturnDeclaration17, funcParams);

            }

            
            //--- begin [@after]
            //--- end [@after]

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aFuncType;
    }
    // $ANTLR end functionType


    // $ANTLR start functionReturnDeclaration
    // AADLWalker.g:452:1: functionReturnDeclaration returns [ACodeDataType aType] : ^( FUNCDEF_RETURN type ) ;
    public final ACodeDataType functionReturnDeclaration() throws RecognitionException {
        ACodeDataType aType = null;

        ACodeDataType type18 = null;


        try {
            // AADLWalker.g:453:5: ( ^( FUNCDEF_RETURN type ) )
            // AADLWalker.g:453:7: ^( FUNCDEF_RETURN type )
            {
            match(input,FUNCDEF_RETURN,FOLLOW_FUNCDEF_RETURN_in_functionReturnDeclaration752); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_type_in_functionReturnDeclaration754);
            type18=type();
            _fsp--;


            match(input, Token.UP, null); 
            aType = type18;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aType;
    }
    // $ANTLR end functionReturnDeclaration


    // $ANTLR start functionFormalParameters
    // AADLWalker.g:456:1: functionFormalParameters[List<ACodeVariable> params] : ^( FUNCDEF_PARAMS ( variableDefinition )+ ) ;
    public final void functionFormalParameters(List<ACodeVariable> params) throws RecognitionException {
        ACodeVariable variableDefinition19 = null;


        try {
            // AADLWalker.g:457:5: ( ^( FUNCDEF_PARAMS ( variableDefinition )+ ) )
            // AADLWalker.g:457:7: ^( FUNCDEF_PARAMS ( variableDefinition )+ )
            {
            match(input,FUNCDEF_PARAMS,FOLLOW_FUNCDEF_PARAMS_in_functionFormalParameters776); 

            match(input, Token.DOWN, null); 
            // AADLWalker.g:457:24: ( variableDefinition )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==VARDEF) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // AADLWalker.g:457:25: variableDefinition
            	    {
            	    pushFollow(FOLLOW_variableDefinition_in_functionFormalParameters779);
            	    variableDefinition19=variableDefinition();
            	    _fsp--;

            	    params.add(variableDefinition19);

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end functionFormalParameters


    // $ANTLR start functionBody
    // AADLWalker.g:460:1: functionBody returns [ACodeBlock aBlock] : ^( BODY ( statement )* ) ;
    public final ACodeBlock functionBody() throws RecognitionException {
        ACodeBlock aBlock = null;

        CommonTree BODY21=null;
        ACodeStatement statement20 = null;


        
        //--- begin functionBody [@init]
        incrementIndent();
        List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
        //--- end functionBody [@init]

        try {
            // AADLWalker.g:467:5: ( ^( BODY ( statement )* ) )
            // AADLWalker.g:467:7: ^( BODY ( statement )* )
            {
            BODY21=(CommonTree)input.LT(1);
            match(input,BODY,FOLLOW_BODY_in_functionBody810); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:467:14: ( statement )*
                loop16:
                do {
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( ((LA16_0>=FCALL_RM && LA16_0<=FCALL_SM)||(LA16_0>=FCALL_IM && LA16_0<=FCALL_MM)||LA16_0==BLOCK||LA16_0==VARDEF||(LA16_0>=INVOLV && LA16_0<=VOIDINVOLV)||(LA16_0>=EXBASE && LA16_0<=ARRAY)||LA16_0==JAVA_ACTION||LA16_0==Identifier||LA16_0==CONST||(LA16_0>=BREAK && LA16_0<=SIF)||(LA16_0>=NamedOperator && LA16_0<=CMDARG)||(LA16_0>=PLUS && LA16_0<=SLASH)||(LA16_0>=LESS && LA16_0<=GREATER)||(LA16_0>=PERCENT && LA16_0<=GREATEREQUAL)||(LA16_0>=109 && LA16_0<=111)||LA16_0==113||LA16_0==116||(LA16_0>=120 && LA16_0<=122)||LA16_0==127) ) {
                        alt16=1;
                    }


                    switch (alt16) {
                	case 1 :
                	    // AADLWalker.g:467:15: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_functionBody813);
                	    statement20=statement();
                	    _fsp--;

                	    stmList.add(statement20);

                	    }
                	    break;

                	default :
                	    break loop16;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            aBlock = ACodeBlock.inBlock(analyzer, BODY21, stmList);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin functionBody [@finally]
            	decrementIndent();
            	//--- begin functionBody [@finally]
                
        }
        return aBlock;
    }
    // $ANTLR end functionBody


    // $ANTLR start block
    // AADLWalker.g:478:1: block returns [ACodeBlock aBlock] : ^( BLOCK ( statement )* ) ;
    public final ACodeBlock block() throws RecognitionException {
        ACodeBlock aBlock = null;

        CommonTree BLOCK23=null;
        ACodeStatement statement22 = null;


        
        //--- begin block [@init]
        incrementIndent();
        analyzer.pushScope();
        List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
        //--- end block [@init]

        try {
            // AADLWalker.g:486:5: ( ^( BLOCK ( statement )* ) )
            // AADLWalker.g:486:7: ^( BLOCK ( statement )* )
            {
            BLOCK23=(CommonTree)input.LT(1);
            match(input,BLOCK,FOLLOW_BLOCK_in_block860); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:486:15: ( statement )*
                loop17:
                do {
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( ((LA17_0>=FCALL_RM && LA17_0<=FCALL_SM)||(LA17_0>=FCALL_IM && LA17_0<=FCALL_MM)||LA17_0==BLOCK||LA17_0==VARDEF||(LA17_0>=INVOLV && LA17_0<=VOIDINVOLV)||(LA17_0>=EXBASE && LA17_0<=ARRAY)||LA17_0==JAVA_ACTION||LA17_0==Identifier||LA17_0==CONST||(LA17_0>=BREAK && LA17_0<=SIF)||(LA17_0>=NamedOperator && LA17_0<=CMDARG)||(LA17_0>=PLUS && LA17_0<=SLASH)||(LA17_0>=LESS && LA17_0<=GREATER)||(LA17_0>=PERCENT && LA17_0<=GREATEREQUAL)||(LA17_0>=109 && LA17_0<=111)||LA17_0==113||LA17_0==116||(LA17_0>=120 && LA17_0<=122)||LA17_0==127) ) {
                        alt17=1;
                    }


                    switch (alt17) {
                	case 1 :
                	    // AADLWalker.g:486:16: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_block863);
                	    statement22=statement();
                	    _fsp--;

                	    stmList.add(statement22);

                	    }
                	    break;

                	default :
                	    break loop17;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            aBlock = ACodeBlock.inBlock(analyzer, BLOCK23, stmList);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin block [@finally]
            	analyzer.popScope();
            	decrementIndent();
            	//--- begin block [@finally]
                
        }
        return aBlock;
    }
    // $ANTLR end block


    // $ANTLR start statement
    // AADLWalker.g:497:1: statement returns [ACodeStatement stm] : ( involvingExpression | block | if_statement | localVariableDeclaration | constVariableDeclaration | assign_statement | break_statement | jump_statement | expression );
    public final ACodeStatement statement() throws RecognitionException {
        ACodeStatement stm = null;

        ACodeInvolving involvingExpression24 = null;

        ACodeBlock block25 = null;

        ACodeControl if_statement26 = null;

        ACodeInitVariable localVariableDeclaration27 = null;

        ACodeInitVariable constVariableDeclaration28 = null;

        ACodeAssign assign_statement29 = null;

        ACodeControl break_statement30 = null;

        ACodeControl jump_statement31 = null;

        ACodeObject expression32 = null;


        try {
            // AADLWalker.g:498:5: ( involvingExpression | block | if_statement | localVariableDeclaration | constVariableDeclaration | assign_statement | break_statement | jump_statement | expression )
            int alt18=9;
            switch ( input.LA(1) ) {
            case INVOLV:
            case VOIDINVOLV:
                {
                alt18=1;
                }
                break;
            case BLOCK:
                {
                alt18=2;
                }
                break;
            case SIF:
                {
                alt18=3;
                }
                break;
            case VARDEF:
                {
                alt18=4;
                }
                break;
            case CONST:
                {
                alt18=5;
                }
                break;
            case Identifier:
                {
                int LA18_6 = input.LA(2);

                if ( (LA18_6==DOWN) ) {
                    alt18=6;
                }
                else if ( (LA18_6==UP||(LA18_6>=FCALL_RM && LA18_6<=FCALL_SM)||(LA18_6>=FCALL_IM && LA18_6<=FCALL_MM)||LA18_6==BLOCK||LA18_6==VARDEF||(LA18_6>=INVOLV && LA18_6<=VOIDINVOLV)||(LA18_6>=EXBASE && LA18_6<=ARRAY)||LA18_6==JAVA_ACTION||LA18_6==Identifier||LA18_6==CONST||(LA18_6>=BREAK && LA18_6<=CMDARG)||(LA18_6>=PLUS && LA18_6<=SLASH)||(LA18_6>=LESS && LA18_6<=GREATER)||(LA18_6>=PERCENT && LA18_6<=GREATEREQUAL)||LA18_6==SCOPE||(LA18_6>=109 && LA18_6<=111)||LA18_6==113||LA18_6==116||(LA18_6>=120 && LA18_6<=122)||LA18_6==127) ) {
                    alt18=9;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("497:1: statement returns [ACodeStatement stm] : ( involvingExpression | block | if_statement | localVariableDeclaration | constVariableDeclaration | assign_statement | break_statement | jump_statement | expression );", 18, 6, input);

                    throw nvae;
                }
                }
                break;
            case BREAK:
                {
                alt18=7;
                }
                break;
            case RETURN:
                {
                alt18=8;
                }
                break;
            case FCALL_RM:
            case FCALL_SM:
            case FCALL_IM:
            case FCALL_MM:
            case EXBASE:
            case DTBASE:
            case ARRAY:
            case JAVA_ACTION:
            case NamedOperator:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case PLUS:
            case MINUS:
            case STAR:
            case SLASH:
            case LESS:
            case GREATER:
            case PERCENT:
            case NOT:
            case HAT:
            case BAR:
            case EQUAL:
            case NOTEQUAL:
            case LESSEQUAL:
            case GREATEREQUAL:
            case 109:
            case 110:
            case 111:
            case 113:
            case 116:
            case 120:
            case 121:
            case 122:
            case 127:
                {
                alt18=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("497:1: statement returns [ACodeStatement stm] : ( involvingExpression | block | if_statement | localVariableDeclaration | constVariableDeclaration | assign_statement | break_statement | jump_statement | expression );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // AADLWalker.g:499:6: involvingExpression
                    {
                    pushFollow(FOLLOW_involvingExpression_in_statement909);
                    involvingExpression24=involvingExpression();
                    _fsp--;

                    stm = ACodeControl.binExpressionWithoutTerm(analyzer, involvingExpression24);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:500:7: block
                    {
                    pushFollow(FOLLOW_block_in_statement920);
                    block25=block();
                    _fsp--;

                    stm = block25;

                    }
                    break;
                case 3 :
                    // AADLWalker.g:501:7: if_statement
                    {
                    pushFollow(FOLLOW_if_statement_in_statement933);
                    if_statement26=if_statement();
                    _fsp--;

                    stm = if_statement26;

                    }
                    break;
                case 4 :
                    // AADLWalker.g:502:7: localVariableDeclaration
                    {
                    pushFollow(FOLLOW_localVariableDeclaration_in_statement945);
                    localVariableDeclaration27=localVariableDeclaration();
                    _fsp--;

                    stm = localVariableDeclaration27;

                    }
                    break;
                case 5 :
                    // AADLWalker.g:503:7: constVariableDeclaration
                    {
                    pushFollow(FOLLOW_constVariableDeclaration_in_statement955);
                    constVariableDeclaration28=constVariableDeclaration();
                    _fsp--;

                    stm = constVariableDeclaration28;

                    }
                    break;
                case 6 :
                    // AADLWalker.g:504:7: assign_statement
                    {
                    pushFollow(FOLLOW_assign_statement_in_statement965);
                    assign_statement29=assign_statement();
                    _fsp--;

                    stm = assign_statement29;

                    }
                    break;
                case 7 :
                    // AADLWalker.g:505:7: break_statement
                    {
                    pushFollow(FOLLOW_break_statement_in_statement976);
                    break_statement30=break_statement();
                    _fsp--;

                    stm = break_statement30;

                    }
                    break;
                case 8 :
                    // AADLWalker.g:506:7: jump_statement
                    {
                    pushFollow(FOLLOW_jump_statement_in_statement988);
                    jump_statement31=jump_statement();
                    _fsp--;

                    stm = jump_statement31;

                    }
                    break;
                case 9 :
                    // AADLWalker.g:507:7: expression
                    {
                    pushFollow(FOLLOW_expression_in_statement1000);
                    expression32=expression();
                    _fsp--;

                    stm = ACodeControl.binExpression(analyzer, expression32);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end statement


    // $ANTLR start constVariableDeclaration
    // AADLWalker.g:510:1: constVariableDeclaration returns [ACodeInitVariable stm] : ^( CONST ^( VARDEF vd= localVariableDeclarationRest[true] (vi= localVariableInitialization[$vd.varDef] ) ) ) ;
    public final ACodeInitVariable constVariableDeclaration() throws RecognitionException {
        ACodeInitVariable stm = null;

        ACodeVariable vd = null;

        ACodeInitVariable vi = null;


        try {
            // AADLWalker.g:511:5: ( ^( CONST ^( VARDEF vd= localVariableDeclarationRest[true] (vi= localVariableInitialization[$vd.varDef] ) ) ) )
            // AADLWalker.g:511:7: ^( CONST ^( VARDEF vd= localVariableDeclarationRest[true] (vi= localVariableInitialization[$vd.varDef] ) ) )
            {
            match(input,CONST,FOLLOW_CONST_in_constVariableDeclaration1024); 

            match(input, Token.DOWN, null); 
            match(input,VARDEF,FOLLOW_VARDEF_in_constVariableDeclaration1027); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_localVariableDeclarationRest_in_constVariableDeclaration1031);
            vd=localVariableDeclarationRest(true);
            _fsp--;

            // AADLWalker.g:512:7: (vi= localVariableInitialization[$vd.varDef] )
            // AADLWalker.g:512:9: vi= localVariableInitialization[$vd.varDef]
            {
            pushFollow(FOLLOW_localVariableInitialization_in_constVariableDeclaration1044);
            vi=localVariableInitialization(vd);
            _fsp--;


            }


            match(input, Token.UP, null); 

            match(input, Token.UP, null); 
            stm = vi;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end constVariableDeclaration


    // $ANTLR start localVariableDeclaration
    // AADLWalker.g:517:1: localVariableDeclaration returns [ACodeInitVariable stm] : ^( VARDEF vd= localVariableDeclarationRest[false] (vi= localVariableInitialization[$vd.varDef] ) ) ;
    public final ACodeInitVariable localVariableDeclaration() throws RecognitionException {
        ACodeInitVariable stm = null;

        ACodeVariable vd = null;

        ACodeInitVariable vi = null;


        try {
            // AADLWalker.g:518:5: ( ^( VARDEF vd= localVariableDeclarationRest[false] (vi= localVariableInitialization[$vd.varDef] ) ) )
            // AADLWalker.g:518:7: ^( VARDEF vd= localVariableDeclarationRest[false] (vi= localVariableInitialization[$vd.varDef] ) )
            {
            match(input,VARDEF,FOLLOW_VARDEF_in_localVariableDeclaration1084); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_localVariableDeclarationRest_in_localVariableDeclaration1088);
            vd=localVariableDeclarationRest(false);
            _fsp--;

            // AADLWalker.g:519:7: (vi= localVariableInitialization[$vd.varDef] )
            // AADLWalker.g:519:9: vi= localVariableInitialization[$vd.varDef]
            {
            pushFollow(FOLLOW_localVariableInitialization_in_localVariableDeclaration1101);
            vi=localVariableInitialization(vd);
            _fsp--;


            }


            match(input, Token.UP, null); 
            stm = vi;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end localVariableDeclaration


    // $ANTLR start localVariableDeclarationRest
    // AADLWalker.g:524:1: localVariableDeclarationRest[boolean isConst] returns [ACodeVariable varDef] : Identifier type ;
    public final ACodeVariable localVariableDeclarationRest(boolean isConst) throws RecognitionException {
        ACodeVariable varDef = null;

        CommonTree Identifier33=null;
        ACodeDataType type34 = null;


        try {
            // AADLWalker.g:525:5: ( Identifier type )
            // AADLWalker.g:525:7: Identifier type
            {
            Identifier33=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_localVariableDeclarationRest1140); 
            pushFollow(FOLLOW_type_in_localVariableDeclarationRest1142);
            type34=type();
            _fsp--;

            varDef = ACodeVariable.buildValiableDefinition(analyzer, isConst, Identifier33.token, type34);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return varDef;
    }
    // $ANTLR end localVariableDeclarationRest


    // $ANTLR start localVariableInitialization
    // AADLWalker.g:529:1: localVariableInitialization[ACodeVariable varDef] returns [ACodeInitVariable stm] : ( ^(vi= VARINIT exp= expression ) | ^(vi= VARINIT involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( VARNEW (exp= expression )* ) | );
    public final ACodeInitVariable localVariableInitialization(ACodeVariable varDef) throws RecognitionException {
        ACodeInitVariable stm = null;

        CommonTree vi=null;
        CommonTree FINPUT36=null;
        ACodeObject exp = null;

        fileDeclarator_return fd = null;

        ACodeInvolving involvingExpression35 = null;


        
        //--- begin [@init]
        List<ACodeObject> args = new ArrayList<ACodeObject>();
        //--- end [@init]

        try {
            // AADLWalker.g:535:5: ( ^(vi= VARINIT exp= expression ) | ^(vi= VARINIT involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( VARNEW (exp= expression )* ) | )
            int alt20=5;
            switch ( input.LA(1) ) {
            case VARINIT:
                {
                int LA20_1 = input.LA(2);

                if ( (LA20_1==DOWN) ) {
                    int LA20_5 = input.LA(3);

                    if ( ((LA20_5>=FCALL_RM && LA20_5<=FCALL_SM)||(LA20_5>=FCALL_IM && LA20_5<=FCALL_MM)||(LA20_5>=EXBASE && LA20_5<=ARRAY)||LA20_5==JAVA_ACTION||LA20_5==Identifier||(LA20_5>=NamedOperator && LA20_5<=CMDARG)||(LA20_5>=PLUS && LA20_5<=SLASH)||(LA20_5>=LESS && LA20_5<=GREATER)||(LA20_5>=PERCENT && LA20_5<=GREATEREQUAL)||(LA20_5>=109 && LA20_5<=111)||LA20_5==113||LA20_5==116||(LA20_5>=120 && LA20_5<=122)||LA20_5==127) ) {
                        alt20=1;
                    }
                    else if ( ((LA20_5>=INVOLV && LA20_5<=VOIDINVOLV)) ) {
                        alt20=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("529:1: localVariableInitialization[ACodeVariable varDef] returns [ACodeInitVariable stm] : ( ^(vi= VARINIT exp= expression ) | ^(vi= VARINIT involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( VARNEW (exp= expression )* ) | );", 20, 5, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("529:1: localVariableInitialization[ACodeVariable varDef] returns [ACodeInitVariable stm] : ( ^(vi= VARINIT exp= expression ) | ^(vi= VARINIT involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( VARNEW (exp= expression )* ) | );", 20, 1, input);

                    throw nvae;
                }
                }
                break;
            case FINPUT:
                {
                alt20=3;
                }
                break;
            case VARNEW:
                {
                alt20=4;
                }
                break;
            case UP:
                {
                alt20=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("529:1: localVariableInitialization[ACodeVariable varDef] returns [ACodeInitVariable stm] : ( ^(vi= VARINIT exp= expression ) | ^(vi= VARINIT involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( VARNEW (exp= expression )* ) | );", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // AADLWalker.g:535:7: ^(vi= VARINIT exp= expression )
                    {
                    vi=(CommonTree)input.LT(1);
                    match(input,VARINIT,FOLLOW_VARINIT_in_localVariableInitialization1178); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_localVariableInitialization1182);
                    exp=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    stm = ACodeInitVariable.initByExpression(analyzer, varDef, vi, exp);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:537:7: ^(vi= VARINIT involvingExpression )
                    {
                    vi=(CommonTree)input.LT(1);
                    match(input,VARINIT,FOLLOW_VARINIT_in_localVariableInitialization1198); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_involvingExpression_in_localVariableInitialization1200);
                    involvingExpression35=involvingExpression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    stm = ACodeInitVariable.initByInvolv(analyzer, varDef, vi, involvingExpression35);

                    }
                    break;
                case 3 :
                    // AADLWalker.g:544:7: ^( FINPUT fd= fileDeclarator )
                    {
                    FINPUT36=(CommonTree)input.LT(1);
                    match(input,FINPUT,FOLLOW_FINPUT_in_localVariableInitialization1217); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fileDeclarator_in_localVariableInitialization1221);
                    fd=fileDeclarator();
                    _fsp--;


                    match(input, Token.UP, null); 
                    stm = ACodeInitVariable.initByFile(analyzer, varDef, FINPUT36.token, fd.fileType, fd.fileArg, fd.encoding, fd.foptions);

                    }
                    break;
                case 4 :
                    // AADLWalker.g:547:7: ^( VARNEW (exp= expression )* )
                    {
                    match(input,VARNEW,FOLLOW_VARNEW_in_localVariableInitialization1236); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // AADLWalker.g:547:16: (exp= expression )*
                        loop19:
                        do {
                            int alt19=2;
                            int LA19_0 = input.LA(1);

                            if ( ((LA19_0>=FCALL_RM && LA19_0<=FCALL_SM)||(LA19_0>=FCALL_IM && LA19_0<=FCALL_MM)||(LA19_0>=EXBASE && LA19_0<=ARRAY)||LA19_0==JAVA_ACTION||LA19_0==Identifier||(LA19_0>=NamedOperator && LA19_0<=CMDARG)||(LA19_0>=PLUS && LA19_0<=SLASH)||(LA19_0>=LESS && LA19_0<=GREATER)||(LA19_0>=PERCENT && LA19_0<=GREATEREQUAL)||(LA19_0>=109 && LA19_0<=111)||LA19_0==113||LA19_0==116||(LA19_0>=120 && LA19_0<=122)||LA19_0==127) ) {
                                alt19=1;
                            }


                            switch (alt19) {
                        	case 1 :
                        	    // AADLWalker.g:547:17: exp= expression
                        	    {
                        	    pushFollow(FOLLOW_expression_in_localVariableInitialization1241);
                        	    exp=expression();
                        	    _fsp--;

                        	    args.add(exp);

                        	    }
                        	    break;

                        	default :
                        	    break loop19;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                    stm = ACodeInitVariable.construct(analyzer, varDef, args);

                    }
                    break;
                case 5 :
                    // AADLWalker.g:550:3: 
                    {
                    stm = ACodeInitVariable.construct(analyzer, varDef, args);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end localVariableInitialization


    // $ANTLR start assign_statement
    // AADLWalker.g:554:1: assign_statement returns [ACodeAssign stm] : ^( Identifier (asr= assign_rest[varRef] ) ) ;
    public final ACodeAssign assign_statement() throws RecognitionException {
        ACodeAssign stm = null;

        CommonTree Identifier37=null;
        ACodeAssign asr = null;


        
        //--- begin [@init]
        ACodeVariable varRef = null;
        //--- end [@init]

        try {
            // AADLWalker.g:560:5: ( ^( Identifier (asr= assign_rest[varRef] ) ) )
            // AADLWalker.g:560:7: ^( Identifier (asr= assign_rest[varRef] ) )
            {
            Identifier37=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_assign_statement1286); 

            varRef = ACodeVariable.buildVariableRef(analyzer, Identifier37.token);

            match(input, Token.DOWN, null); 
            // AADLWalker.g:561:3: (asr= assign_rest[varRef] )
            // AADLWalker.g:561:4: asr= assign_rest[varRef]
            {
            pushFollow(FOLLOW_assign_rest_in_assign_statement1295);
            asr=assign_rest(varRef);
            _fsp--;


            }


            match(input, Token.UP, null); 
            stm = asr;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end assign_statement


    // $ANTLR start assign_rest
    // AADLWalker.g:566:1: assign_rest[ACodeVariable varRef] returns [ACodeAssign stm] : ( ^(as= ASSIGN expression ) | ^(as= ASSIGN involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( FOUTPUT fd= fileDeclarator ) );
    public final ACodeAssign assign_rest(ACodeVariable varRef) throws RecognitionException {
        ACodeAssign stm = null;

        CommonTree as=null;
        CommonTree FINPUT40=null;
        CommonTree FOUTPUT41=null;
        fileDeclarator_return fd = null;

        ACodeObject expression38 = null;

        ACodeInvolving involvingExpression39 = null;


        try {
            // AADLWalker.g:567:5: ( ^(as= ASSIGN expression ) | ^(as= ASSIGN involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( FOUTPUT fd= fileDeclarator ) )
            int alt21=4;
            switch ( input.LA(1) ) {
            case ASSIGN:
                {
                int LA21_1 = input.LA(2);

                if ( (LA21_1==DOWN) ) {
                    int LA21_4 = input.LA(3);

                    if ( ((LA21_4>=FCALL_RM && LA21_4<=FCALL_SM)||(LA21_4>=FCALL_IM && LA21_4<=FCALL_MM)||(LA21_4>=EXBASE && LA21_4<=ARRAY)||LA21_4==JAVA_ACTION||LA21_4==Identifier||(LA21_4>=NamedOperator && LA21_4<=CMDARG)||(LA21_4>=PLUS && LA21_4<=SLASH)||(LA21_4>=LESS && LA21_4<=GREATER)||(LA21_4>=PERCENT && LA21_4<=GREATEREQUAL)||(LA21_4>=109 && LA21_4<=111)||LA21_4==113||LA21_4==116||(LA21_4>=120 && LA21_4<=122)||LA21_4==127) ) {
                        alt21=1;
                    }
                    else if ( ((LA21_4>=INVOLV && LA21_4<=VOIDINVOLV)) ) {
                        alt21=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("566:1: assign_rest[ACodeVariable varRef] returns [ACodeAssign stm] : ( ^(as= ASSIGN expression ) | ^(as= ASSIGN involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( FOUTPUT fd= fileDeclarator ) );", 21, 4, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("566:1: assign_rest[ACodeVariable varRef] returns [ACodeAssign stm] : ( ^(as= ASSIGN expression ) | ^(as= ASSIGN involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( FOUTPUT fd= fileDeclarator ) );", 21, 1, input);

                    throw nvae;
                }
                }
                break;
            case FINPUT:
                {
                alt21=3;
                }
                break;
            case FOUTPUT:
                {
                alt21=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("566:1: assign_rest[ACodeVariable varRef] returns [ACodeAssign stm] : ( ^(as= ASSIGN expression ) | ^(as= ASSIGN involvingExpression ) | ^( FINPUT fd= fileDeclarator ) | ^( FOUTPUT fd= fileDeclarator ) );", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // AADLWalker.g:567:7: ^(as= ASSIGN expression )
                    {
                    as=(CommonTree)input.LT(1);
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_assign_rest1336); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_assign_rest1338);
                    expression38=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    stm = ACodeAssign.assignExpression(analyzer, varRef, as, expression38);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:569:7: ^(as= ASSIGN involvingExpression )
                    {
                    as=(CommonTree)input.LT(1);
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_assign_rest1354); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_involvingExpression_in_assign_rest1356);
                    involvingExpression39=involvingExpression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    stm = ACodeAssign.assignInvolv(analyzer, varRef, as, involvingExpression39);

                    }
                    break;
                case 3 :
                    // AADLWalker.g:578:7: ^( FINPUT fd= fileDeclarator )
                    {
                    FINPUT40=(CommonTree)input.LT(1);
                    match(input,FINPUT,FOLLOW_FINPUT_in_assign_rest1373); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fileDeclarator_in_assign_rest1377);
                    fd=fileDeclarator();
                    _fsp--;


                    match(input, Token.UP, null); 
                    stm = ACodeAssign.assignFile(analyzer, varRef, FINPUT40.token, fd.fileType, fd.fileArg, fd.encoding, fd.foptions);

                    }
                    break;
                case 4 :
                    // AADLWalker.g:580:7: ^( FOUTPUT fd= fileDeclarator )
                    {
                    FOUTPUT41=(CommonTree)input.LT(1);
                    match(input,FOUTPUT,FOLLOW_FOUTPUT_in_assign_rest1391); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_fileDeclarator_in_assign_rest1395);
                    fd=fileDeclarator();
                    _fsp--;


                    match(input, Token.UP, null); 
                    stm = ACodeAssign.assignFile(analyzer, varRef, FOUTPUT41.token, fd.fileType, fd.fileArg, fd.encoding, fd.foptions);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end assign_rest


    // $ANTLR start break_statement
    // AADLWalker.g:585:1: break_statement returns [ACodeControl stm] : ^( BREAK ( Identifier )? ) ;
    public final ACodeControl break_statement() throws RecognitionException {
        ACodeControl stm = null;

        CommonTree BREAK42=null;
        CommonTree Identifier43=null;

        try {
            // AADLWalker.g:586:5: ( ^( BREAK ( Identifier )? ) )
            // AADLWalker.g:586:7: ^( BREAK ( Identifier )? )
            {
            BREAK42=(CommonTree)input.LT(1);
            match(input,BREAK,FOLLOW_BREAK_in_break_statement1423); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:586:15: ( Identifier )?
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==Identifier) ) {
                    alt22=1;
                }
                switch (alt22) {
                    case 1 :
                        // AADLWalker.g:586:15: Identifier
                        {
                        Identifier43=(CommonTree)input.LT(1);
                        match(input,Identifier,FOLLOW_Identifier_in_break_statement1425); 

                        }
                        break;

                }


                match(input, Token.UP, null); 
            }
            stm = ACodeControl.ctrlBreak(analyzer, BREAK42, Identifier43);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end break_statement


    // $ANTLR start jump_statement
    // AADLWalker.g:590:1: jump_statement returns [ACodeControl stm] : ^( RETURN ( expression )? ) ;
    public final ACodeControl jump_statement() throws RecognitionException {
        ACodeControl stm = null;

        CommonTree RETURN44=null;
        ACodeObject expression45 = null;


        try {
            // AADLWalker.g:591:5: ( ^( RETURN ( expression )? ) )
            // AADLWalker.g:591:7: ^( RETURN ( expression )? )
            {
            RETURN44=(CommonTree)input.LT(1);
            match(input,RETURN,FOLLOW_RETURN_in_jump_statement1453); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:591:16: ( expression )?
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>=FCALL_RM && LA23_0<=FCALL_SM)||(LA23_0>=FCALL_IM && LA23_0<=FCALL_MM)||(LA23_0>=EXBASE && LA23_0<=ARRAY)||LA23_0==JAVA_ACTION||LA23_0==Identifier||(LA23_0>=NamedOperator && LA23_0<=CMDARG)||(LA23_0>=PLUS && LA23_0<=SLASH)||(LA23_0>=LESS && LA23_0<=GREATER)||(LA23_0>=PERCENT && LA23_0<=GREATEREQUAL)||(LA23_0>=109 && LA23_0<=111)||LA23_0==113||LA23_0==116||(LA23_0>=120 && LA23_0<=122)||LA23_0==127) ) {
                    alt23=1;
                }
                switch (alt23) {
                    case 1 :
                        // AADLWalker.g:591:16: expression
                        {
                        pushFollow(FOLLOW_expression_in_jump_statement1455);
                        expression45=expression();
                        _fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }
            stm = ACodeControl.ctrlReturn(analyzer, RETURN44, expression45);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end jump_statement


    // $ANTLR start if_statement
    // AADLWalker.g:595:1: if_statement returns [ACodeControl stm] : ^( SIF cd= expression th= ifelse_statement_rest (el= else_statement )? ) ;
    public final ACodeControl if_statement() throws RecognitionException {
        ACodeControl stm = null;

        CommonTree SIF46=null;
        ACodeObject cd = null;

        ACodeStatement th = null;

        ACodeControl el = null;


        try {
            // AADLWalker.g:596:5: ( ^( SIF cd= expression th= ifelse_statement_rest (el= else_statement )? ) )
            // AADLWalker.g:596:7: ^( SIF cd= expression th= ifelse_statement_rest (el= else_statement )? )
            {
            SIF46=(CommonTree)input.LT(1);
            match(input,SIF,FOLLOW_SIF_in_if_statement1486); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_if_statement1490);
            cd=expression();
            _fsp--;

            pushFollow(FOLLOW_ifelse_statement_rest_in_if_statement1494);
            th=ifelse_statement_rest();
            _fsp--;

            // AADLWalker.g:596:54: (el= else_statement )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==SELSE) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // AADLWalker.g:596:54: el= else_statement
                    {
                    pushFollow(FOLLOW_else_statement_in_if_statement1498);
                    el=else_statement();
                    _fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 
            stm = ACodeControl.ctrlIf(analyzer, SIF46, cd, th, el);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end if_statement


    // $ANTLR start else_statement
    // AADLWalker.g:600:1: else_statement returns [ACodeControl stm] : ^( SELSE es= ifelse_statement_rest ) ;
    public final ACodeControl else_statement() throws RecognitionException {
        ACodeControl stm = null;

        CommonTree SELSE47=null;
        ACodeStatement es = null;


        try {
            // AADLWalker.g:601:5: ( ^( SELSE es= ifelse_statement_rest ) )
            // AADLWalker.g:601:7: ^( SELSE es= ifelse_statement_rest )
            {
            SELSE47=(CommonTree)input.LT(1);
            match(input,SELSE,FOLLOW_SELSE_in_else_statement1529); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_ifelse_statement_rest_in_else_statement1533);
            es=ifelse_statement_rest();
            _fsp--;


            match(input, Token.UP, null); 
            stm = ACodeControl.ctrlElse(analyzer, SELSE47, es);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return stm;
    }
    // $ANTLR end else_statement


    // $ANTLR start ifelse_statement_rest
    // AADLWalker.g:605:1: ifelse_statement_rest returns [ACodeStatement stm] : statement ;
    public final ACodeStatement ifelse_statement_rest() throws RecognitionException {
        ACodeStatement stm = null;

        ACodeStatement statement48 = null;


        
        //--- begin ifelse_statement_rest [@init]
        analyzer.pushScope();
        //--- end ifelse_statement_rest [@init]

        try {
            // AADLWalker.g:611:5: ( statement )
            // AADLWalker.g:611:7: statement
            {
            pushFollow(FOLLOW_statement_in_ifelse_statement_rest1566);
            statement48=statement();
            _fsp--;

            stm = statement48;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin ifelse_statement_rest [@finally]
            	analyzer.popScope();
            	//--- begin ifelse_statement_rest [@finally]
                
        }
        return stm;
    }
    // $ANTLR end ifelse_statement_rest


    // $ANTLR start involvingExpression
    // AADLWalker.g:621:1: involvingExpression returns [ACodeInvolving aInvolv] : ( ^( INVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression ) | ^( VOIDINVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression ) );
    public final ACodeInvolving involvingExpression() throws RecognitionException {
        ACodeInvolving aInvolv = null;

        CommonTree INVOLV49=null;
        CommonTree INVOLV_COND50=null;
        CommonTree VOIDINVOLV51=null;
        CommonTree INVOLV_COND52=null;
        ACodeObject fe = null;

        ACodeObject exp = null;


        
        //--- begin involv [@init]
        analyzer.pushScope();
        analyzer.beginInvolvingBlockParsing();
        List<ACodeObject> filters = new ArrayList<ACodeObject>();
        //--- end involv [@init]

        try {
            // AADLWalker.g:629:5: ( ^( INVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression ) | ^( VOIDINVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression ) )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==INVOLV) ) {
                alt27=1;
            }
            else if ( (LA27_0==VOIDINVOLV) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("621:1: involvingExpression returns [ACodeInvolving aInvolv] : ( ^( INVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression ) | ^( VOIDINVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression ) );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // AADLWalker.g:629:7: ^( INVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression )
                    {
                    INVOLV49=(CommonTree)input.LT(1);
                    match(input,INVOLV,FOLLOW_INVOLV_in_involvingExpression1604); 

                    match(input, Token.DOWN, null); 
                    INVOLV_COND50=(CommonTree)input.LT(1);
                    match(input,INVOLV_COND,FOLLOW_INVOLV_COND_in_involvingExpression1607); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // AADLWalker.g:629:30: (fe= filterExpression )*
                        loop25:
                        do {
                            int alt25=2;
                            int LA25_0 = input.LA(1);

                            if ( ((LA25_0>=FCALL_RM && LA25_0<=FCALL_SM)||(LA25_0>=FCALL_IM && LA25_0<=FCALL_MM)||(LA25_0>=ITERATE && LA25_0<=ALIAS)||(LA25_0>=INVOLV_BLOCK && LA25_0<=ARRAY)||LA25_0==JAVA_ACTION||LA25_0==Identifier||(LA25_0>=NamedOperator && LA25_0<=CMDARG)||(LA25_0>=PLUS && LA25_0<=SLASH)||(LA25_0>=LESS && LA25_0<=GREATER)||(LA25_0>=PERCENT && LA25_0<=GREATEREQUAL)||(LA25_0>=109 && LA25_0<=111)||LA25_0==113||LA25_0==116||(LA25_0>=120 && LA25_0<=122)||LA25_0==127) ) {
                                alt25=1;
                            }


                            switch (alt25) {
                        	case 1 :
                        	    // AADLWalker.g:629:31: fe= filterExpression
                        	    {
                        	    pushFollow(FOLLOW_filterExpression_in_involvingExpression1612);
                        	    fe=filterExpression();
                        	    _fsp--;

                        	    filters.add(fe);

                        	    }
                        	    break;

                        	default :
                        	    break loop25;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                    pushFollow(FOLLOW_expression_in_involvingExpression1620);
                    exp=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aInvolv = ACodeInvolving.buildInvolving(analyzer, INVOLV49, exp, INVOLV_COND50, filters);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:631:7: ^( VOIDINVOLV ^( INVOLV_COND (fe= filterExpression )* ) exp= expression )
                    {
                    VOIDINVOLV51=(CommonTree)input.LT(1);
                    match(input,VOIDINVOLV,FOLLOW_VOIDINVOLV_in_involvingExpression1637); 

                    match(input, Token.DOWN, null); 
                    INVOLV_COND52=(CommonTree)input.LT(1);
                    match(input,INVOLV_COND,FOLLOW_INVOLV_COND_in_involvingExpression1640); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // AADLWalker.g:631:34: (fe= filterExpression )*
                        loop26:
                        do {
                            int alt26=2;
                            int LA26_0 = input.LA(1);

                            if ( ((LA26_0>=FCALL_RM && LA26_0<=FCALL_SM)||(LA26_0>=FCALL_IM && LA26_0<=FCALL_MM)||(LA26_0>=ITERATE && LA26_0<=ALIAS)||(LA26_0>=INVOLV_BLOCK && LA26_0<=ARRAY)||LA26_0==JAVA_ACTION||LA26_0==Identifier||(LA26_0>=NamedOperator && LA26_0<=CMDARG)||(LA26_0>=PLUS && LA26_0<=SLASH)||(LA26_0>=LESS && LA26_0<=GREATER)||(LA26_0>=PERCENT && LA26_0<=GREATEREQUAL)||(LA26_0>=109 && LA26_0<=111)||LA26_0==113||LA26_0==116||(LA26_0>=120 && LA26_0<=122)||LA26_0==127) ) {
                                alt26=1;
                            }


                            switch (alt26) {
                        	case 1 :
                        	    // AADLWalker.g:631:35: fe= filterExpression
                        	    {
                        	    pushFollow(FOLLOW_filterExpression_in_involvingExpression1645);
                        	    fe=filterExpression();
                        	    _fsp--;

                        	    filters.add(fe);

                        	    }
                        	    break;

                        	default :
                        	    break loop26;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                    pushFollow(FOLLOW_expression_in_involvingExpression1653);
                    exp=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aInvolv = ACodeInvolving.buildInvolving(analyzer, VOIDINVOLV51, exp, INVOLV_COND52, filters);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
            
            	//--- begin involv [@finally]
            	analyzer.endInvolvingBlockParsing();
            	analyzer.popScope();
            	//--- end involv [@finally]
                
        }
        return aInvolv;
    }
    // $ANTLR end involvingExpression


    // $ANTLR start filterExpression
    // AADLWalker.g:641:1: filterExpression returns [ACodeObject aObject] : ( iterateExpression | aliasExpression | expression | involvingBlock );
    public final ACodeObject filterExpression() throws RecognitionException {
        ACodeObject aObject = null;

        ACodeInvolvIterator iterateExpression53 = null;

        ACodeInvolvAlias aliasExpression54 = null;

        ACodeObject expression55 = null;

        ACodeBlock involvingBlock56 = null;


        try {
            // AADLWalker.g:642:5: ( iterateExpression | aliasExpression | expression | involvingBlock )
            int alt28=4;
            switch ( input.LA(1) ) {
            case ITERATE:
                {
                alt28=1;
                }
                break;
            case ALIAS:
                {
                alt28=2;
                }
                break;
            case FCALL_RM:
            case FCALL_SM:
            case FCALL_IM:
            case FCALL_MM:
            case EXBASE:
            case DTBASE:
            case ARRAY:
            case JAVA_ACTION:
            case Identifier:
            case NamedOperator:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case PLUS:
            case MINUS:
            case STAR:
            case SLASH:
            case LESS:
            case GREATER:
            case PERCENT:
            case NOT:
            case HAT:
            case BAR:
            case EQUAL:
            case NOTEQUAL:
            case LESSEQUAL:
            case GREATEREQUAL:
            case 109:
            case 110:
            case 111:
            case 113:
            case 116:
            case 120:
            case 121:
            case 122:
            case 127:
                {
                alt28=3;
                }
                break;
            case INVOLV_BLOCK:
                {
                alt28=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("641:1: filterExpression returns [ACodeObject aObject] : ( iterateExpression | aliasExpression | expression | involvingBlock );", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // AADLWalker.g:642:7: iterateExpression
                    {
                    pushFollow(FOLLOW_iterateExpression_in_filterExpression1690);
                    iterateExpression53=iterateExpression();
                    _fsp--;

                    aObject = iterateExpression53;

                    }
                    break;
                case 2 :
                    // AADLWalker.g:643:7: aliasExpression
                    {
                    pushFollow(FOLLOW_aliasExpression_in_filterExpression1700);
                    aliasExpression54=aliasExpression();
                    _fsp--;

                    aObject = aliasExpression54;

                    }
                    break;
                case 3 :
                    // AADLWalker.g:644:7: expression
                    {
                    pushFollow(FOLLOW_expression_in_filterExpression1711);
                    expression55=expression();
                    _fsp--;

                    aObject = expression55;

                    }
                    break;
                case 4 :
                    // AADLWalker.g:645:7: involvingBlock
                    {
                    pushFollow(FOLLOW_involvingBlock_in_filterExpression1722);
                    involvingBlock56=involvingBlock();
                    _fsp--;

                    aObject = involvingBlock56;

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end filterExpression


    // $ANTLR start involvingBlock
    // AADLWalker.g:648:1: involvingBlock returns [ACodeBlock aBlock] : ^( INVOLV_BLOCK ( statement )* ) ;
    public final ACodeBlock involvingBlock() throws RecognitionException {
        ACodeBlock aBlock = null;

        CommonTree INVOLV_BLOCK58=null;
        ACodeStatement statement57 = null;


        
        //--- begin block [@init]
        incrementIndent();
        analyzer.pushScope();
        List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
        //--- end block [@init]

        try {
            // AADLWalker.g:662:5: ( ^( INVOLV_BLOCK ( statement )* ) )
            // AADLWalker.g:662:7: ^( INVOLV_BLOCK ( statement )* )
            {
            INVOLV_BLOCK58=(CommonTree)input.LT(1);
            match(input,INVOLV_BLOCK,FOLLOW_INVOLV_BLOCK_in_involvingBlock1755); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:662:22: ( statement )*
                loop29:
                do {
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( ((LA29_0>=FCALL_RM && LA29_0<=FCALL_SM)||(LA29_0>=FCALL_IM && LA29_0<=FCALL_MM)||LA29_0==BLOCK||LA29_0==VARDEF||(LA29_0>=INVOLV && LA29_0<=VOIDINVOLV)||(LA29_0>=EXBASE && LA29_0<=ARRAY)||LA29_0==JAVA_ACTION||LA29_0==Identifier||LA29_0==CONST||(LA29_0>=BREAK && LA29_0<=SIF)||(LA29_0>=NamedOperator && LA29_0<=CMDARG)||(LA29_0>=PLUS && LA29_0<=SLASH)||(LA29_0>=LESS && LA29_0<=GREATER)||(LA29_0>=PERCENT && LA29_0<=GREATEREQUAL)||(LA29_0>=109 && LA29_0<=111)||LA29_0==113||LA29_0==116||(LA29_0>=120 && LA29_0<=122)||LA29_0==127) ) {
                        alt29=1;
                    }


                    switch (alt29) {
                	case 1 :
                	    // AADLWalker.g:662:23: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_involvingBlock1758);
                	    statement57=statement();
                	    _fsp--;

                	    stmList.add(statement57);

                	    }
                	    break;

                	default :
                	    break loop29;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            aBlock = ACodeBlock.inBlock(analyzer, INVOLV_BLOCK58, stmList);

            }

            
            //--- begin block [@after]
            analyzer.popScope();
            decrementIndent();
            //--- begin block [@after]

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aBlock;
    }
    // $ANTLR end involvingBlock


    // $ANTLR start iterateExpression
    // AADLWalker.g:666:1: iterateExpression returns [ACodeInvolvIterator aIt] : ^( ITERATE en= Identifier it= iterateExpressionRest[$en] ) ;
    public final ACodeInvolvIterator iterateExpression() throws RecognitionException {
        ACodeInvolvIterator aIt = null;

        CommonTree en=null;
        ACodeInvolvIterator it = null;


        try {
            // AADLWalker.g:667:5: ( ^( ITERATE en= Identifier it= iterateExpressionRest[$en] ) )
            // AADLWalker.g:667:7: ^( ITERATE en= Identifier it= iterateExpressionRest[$en] )
            {
            match(input,ITERATE,FOLLOW_ITERATE_in_iterateExpression1791); 

            match(input, Token.DOWN, null); 
            en=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_iterateExpression1795); 
            pushFollow(FOLLOW_iterateExpressionRest_in_iterateExpression1799);
            it=iterateExpressionRest(en);
            _fsp--;


            match(input, Token.UP, null); 
            aIt = it;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aIt;
    }
    // $ANTLR end iterateExpression


    // $ANTLR start iterateExpressionRest
    // AADLWalker.g:671:1: iterateExpressionRest[CommonTree en] returns [ACodeInvolvIterator aIt] : (fd= fileDeclarator | ln= Identifier | al= arrayLiteral | mc= methodCall | ie= involvingExpression );
    public final ACodeInvolvIterator iterateExpressionRest(CommonTree en) throws RecognitionException {
        ACodeInvolvIterator aIt = null;

        CommonTree ln=null;
        fileDeclarator_return fd = null;

        ACodeLiteral al = null;

        ACodeFuncCall mc = null;

        ACodeInvolving ie = null;


        try {
            // AADLWalker.g:672:5: (fd= fileDeclarator | ln= Identifier | al= arrayLiteral | mc= methodCall | ie= involvingExpression )
            int alt30=5;
            switch ( input.LA(1) ) {
            case 117:
            case 118:
            case 119:
                {
                alt30=1;
                }
                break;
            case Identifier:
                {
                alt30=2;
                }
                break;
            case ARRAY:
                {
                alt30=3;
                }
                break;
            case FCALL_RM:
            case FCALL_SM:
            case FCALL_IM:
            case FCALL_MM:
            case 113:
            case 116:
                {
                alt30=4;
                }
                break;
            case INVOLV:
            case VOIDINVOLV:
                {
                alt30=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("671:1: iterateExpressionRest[CommonTree en] returns [ACodeInvolvIterator aIt] : (fd= fileDeclarator | ln= Identifier | al= arrayLiteral | mc= methodCall | ie= involvingExpression );", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // AADLWalker.g:673:6: fd= fileDeclarator
                    {
                    pushFollow(FOLLOW_fileDeclarator_in_iterateExpressionRest1837);
                    fd=fileDeclarator();
                    _fsp--;

                    aIt = ACodeInvolvIterator.buildReaderIterator(analyzer, en.token, fd.fileType, fd.fileArg, fd.encoding, fd.foptions);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:675:7: ln= Identifier
                    {
                    ln=(CommonTree)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_iterateExpressionRest1851); 
                    aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, ACodeVariable.buildVariableRef(analyzer, ln.token));

                    }
                    break;
                case 3 :
                    // AADLWalker.g:677:7: al= arrayLiteral
                    {
                    pushFollow(FOLLOW_arrayLiteral_in_iterateExpressionRest1865);
                    al=arrayLiteral();
                    _fsp--;

                    aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, al);

                    }
                    break;
                case 4 :
                    // AADLWalker.g:680:7: mc= methodCall
                    {
                    pushFollow(FOLLOW_methodCall_in_iterateExpressionRest1880);
                    mc=methodCall();
                    _fsp--;

                    aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, mc);

                    }
                    break;
                case 5 :
                    // AADLWalker.g:683:7: ie= involvingExpression
                    {
                    pushFollow(FOLLOW_involvingExpression_in_iterateExpressionRest1895);
                    ie=involvingExpression();
                    _fsp--;

                    aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, ie);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aIt;
    }
    // $ANTLR end iterateExpressionRest


    // $ANTLR start aliasExpression
    // AADLWalker.g:687:1: aliasExpression returns [ACodeInvolvAlias aAlias] : ^( ALIAS Identifier (tp= type )? expression ) ;
    public final ACodeInvolvAlias aliasExpression() throws RecognitionException {
        ACodeInvolvAlias aAlias = null;

        CommonTree Identifier59=null;
        ACodeDataType tp = null;

        ACodeObject expression60 = null;


        try {
            // AADLWalker.g:688:5: ( ^( ALIAS Identifier (tp= type )? expression ) )
            // AADLWalker.g:688:7: ^( ALIAS Identifier (tp= type )? expression )
            {
            match(input,ALIAS,FOLLOW_ALIAS_in_aliasExpression1921); 

            match(input, Token.DOWN, null); 
            Identifier59=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_aliasExpression1923); 
            // AADLWalker.g:688:28: (tp= type )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==VARTYPE) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // AADLWalker.g:688:28: tp= type
                    {
                    pushFollow(FOLLOW_type_in_aliasExpression1927);
                    tp=type();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_expression_in_aliasExpression1930);
            expression60=expression();
            _fsp--;


            match(input, Token.UP, null); 
            aAlias = ACodeInvolvAlias.buildAlias(analyzer, Identifier59.token, tp, expression60);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aAlias;
    }
    // $ANTLR end aliasExpression


    // $ANTLR start expression
    // AADLWalker.g:692:1: expression returns [ACodeObject aObject] : (exp= newExalgeExpression | exp= conditionalAndOrExpression | exp= conditionalExpression | exp= namedOperationExpression | exp= mathOperationExpression | exp= plusExpression | exp= minusExpression | exp= simpleExpression | exp= specialExpression | exp= primaryExpression );
    public final ACodeObject expression() throws RecognitionException {
        ACodeObject aObject = null;

        ACodeObject exp = null;


        try {
            // AADLWalker.g:693:5: (exp= newExalgeExpression | exp= conditionalAndOrExpression | exp= conditionalExpression | exp= namedOperationExpression | exp= mathOperationExpression | exp= plusExpression | exp= minusExpression | exp= simpleExpression | exp= specialExpression | exp= primaryExpression )
            int alt32=10;
            switch ( input.LA(1) ) {
            case 111:
                {
                alt32=1;
                }
                break;
            case 109:
            case 110:
                {
                alt32=2;
                }
                break;
            case LESS:
            case GREATER:
            case EQUAL:
            case NOTEQUAL:
            case LESSEQUAL:
            case GREATEREQUAL:
                {
                alt32=3;
                }
                break;
            case NamedOperator:
                {
                alt32=4;
                }
                break;
            case STAR:
            case SLASH:
            case PERCENT:
                {
                alt32=5;
                }
                break;
            case PLUS:
                {
                alt32=6;
                }
                break;
            case MINUS:
                {
                alt32=7;
                }
                break;
            case NOT:
            case HAT:
            case BAR:
                {
                alt32=8;
                }
                break;
            case 127:
                {
                alt32=9;
                }
                break;
            case FCALL_RM:
            case FCALL_SM:
            case FCALL_IM:
            case FCALL_MM:
            case EXBASE:
            case DTBASE:
            case ARRAY:
            case JAVA_ACTION:
            case Identifier:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case 113:
            case 116:
            case 120:
            case 121:
            case 122:
                {
                alt32=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("692:1: expression returns [ACodeObject aObject] : (exp= newExalgeExpression | exp= conditionalAndOrExpression | exp= conditionalExpression | exp= namedOperationExpression | exp= mathOperationExpression | exp= plusExpression | exp= minusExpression | exp= simpleExpression | exp= specialExpression | exp= primaryExpression );", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // AADLWalker.g:693:7: exp= newExalgeExpression
                    {
                    pushFollow(FOLLOW_newExalgeExpression_in_expression1961);
                    exp=newExalgeExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 2 :
                    // AADLWalker.g:694:7: exp= conditionalAndOrExpression
                    {
                    pushFollow(FOLLOW_conditionalAndOrExpression_in_expression1974);
                    exp=conditionalAndOrExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 3 :
                    // AADLWalker.g:695:7: exp= conditionalExpression
                    {
                    pushFollow(FOLLOW_conditionalExpression_in_expression1986);
                    exp=conditionalExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 4 :
                    // AADLWalker.g:696:7: exp= namedOperationExpression
                    {
                    pushFollow(FOLLOW_namedOperationExpression_in_expression1998);
                    exp=namedOperationExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 5 :
                    // AADLWalker.g:697:7: exp= mathOperationExpression
                    {
                    pushFollow(FOLLOW_mathOperationExpression_in_expression2010);
                    exp=mathOperationExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 6 :
                    // AADLWalker.g:698:7: exp= plusExpression
                    {
                    pushFollow(FOLLOW_plusExpression_in_expression2022);
                    exp=plusExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 7 :
                    // AADLWalker.g:699:7: exp= minusExpression
                    {
                    pushFollow(FOLLOW_minusExpression_in_expression2035);
                    exp=minusExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 8 :
                    // AADLWalker.g:700:7: exp= simpleExpression
                    {
                    pushFollow(FOLLOW_simpleExpression_in_expression2048);
                    exp=simpleExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 9 :
                    // AADLWalker.g:701:7: exp= specialExpression
                    {
                    pushFollow(FOLLOW_specialExpression_in_expression2061);
                    exp=specialExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;
                case 10 :
                    // AADLWalker.g:702:7: exp= primaryExpression
                    {
                    pushFollow(FOLLOW_primaryExpression_in_expression2074);
                    exp=primaryExpression();
                    _fsp--;

                    aObject = exp;

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end expression


    // $ANTLR start specialExpression
    // AADLWalker.g:705:1: specialExpression returns [ACodeObject aObject] : ^(op= '?' cd= expression rt= expression rf= expression ) ;
    public final ACodeObject specialExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject cd = null;

        ACodeObject rt = null;

        ACodeObject rf = null;


        try {
            // AADLWalker.g:706:5: ( ^(op= '?' cd= expression rt= expression rf= expression ) )
            // AADLWalker.g:706:7: ^(op= '?' cd= expression rt= expression rf= expression )
            {
            op=(CommonTree)input.LT(1);
            match(input,127,FOLLOW_127_in_specialExpression2101); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_specialExpression2105);
            cd=expression();
            _fsp--;

            pushFollow(FOLLOW_expression_in_specialExpression2109);
            rt=expression();
            _fsp--;

            pushFollow(FOLLOW_expression_in_specialExpression2113);
            rf=expression();
            _fsp--;


            match(input, Token.UP, null); 
            aObject = (cd!=null ? cd.conditionalAssign(analyzer, op.token, rt, rf) : null);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end specialExpression


    // $ANTLR start conditionalAndOrExpression
    // AADLWalker.g:710:1: conditionalAndOrExpression returns [ACodeObject aObject] : ^(op= ( '||' | '&&' ) a1= expression a2= expression ) ;
    public final ACodeObject conditionalAndOrExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject a1 = null;

        ACodeObject a2 = null;


        try {
            // AADLWalker.g:711:5: ( ^(op= ( '||' | '&&' ) a1= expression a2= expression ) )
            // AADLWalker.g:711:7: ^(op= ( '||' | '&&' ) a1= expression a2= expression )
            {
            op=(CommonTree)input.LT(1);
            if ( (input.LA(1)>=109 && input.LA(1)<=110) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_conditionalAndOrExpression2145);    throw mse;
            }


            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_conditionalAndOrExpression2153);
            a1=expression();
            _fsp--;

            pushFollow(FOLLOW_expression_in_conditionalAndOrExpression2157);
            a2=expression();
            _fsp--;


            match(input, Token.UP, null); 
            aObject = (a1!=null ? a1.conditionalAndOr(analyzer, op.token, a2) : null);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end conditionalAndOrExpression


    // $ANTLR start conditionalExpression
    // AADLWalker.g:715:1: conditionalExpression returns [ACodeObject aObject] : ( ^(op= '==' a1= expression a2= expression ) | ^(op= '!=' a1= expression a2= expression ) | ^(op= ( '<=' | '>=' | '<' | '>' ) a1= expression a2= expression ) );
    public final ACodeObject conditionalExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject a1 = null;

        ACodeObject a2 = null;


        try {
            // AADLWalker.g:716:5: ( ^(op= '==' a1= expression a2= expression ) | ^(op= '!=' a1= expression a2= expression ) | ^(op= ( '<=' | '>=' | '<' | '>' ) a1= expression a2= expression ) )
            int alt33=3;
            switch ( input.LA(1) ) {
            case EQUAL:
                {
                alt33=1;
                }
                break;
            case NOTEQUAL:
                {
                alt33=2;
                }
                break;
            case LESS:
            case GREATER:
            case LESSEQUAL:
            case GREATEREQUAL:
                {
                alt33=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("715:1: conditionalExpression returns [ACodeObject aObject] : ( ^(op= '==' a1= expression a2= expression ) | ^(op= '!=' a1= expression a2= expression ) | ^(op= ( '<=' | '>=' | '<' | '>' ) a1= expression a2= expression ) );", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // AADLWalker.g:716:7: ^(op= '==' a1= expression a2= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,EQUAL,FOLLOW_EQUAL_in_conditionalExpression2189); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_conditionalExpression2193);
                    a1=expression();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_conditionalExpression2197);
                    a2=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.equal(analyzer, op.token, a2) : null);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:718:7: ^(op= '!=' a1= expression a2= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,NOTEQUAL,FOLLOW_NOTEQUAL_in_conditionalExpression2217); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_conditionalExpression2221);
                    a1=expression();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_conditionalExpression2225);
                    a2=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.notEqual(analyzer, op.token, a2) : null);

                    }
                    break;
                case 3 :
                    // AADLWalker.g:720:7: ^(op= ( '<=' | '>=' | '<' | '>' ) a1= expression a2= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    if ( (input.LA(1)>=LESS && input.LA(1)<=GREATER)||(input.LA(1)>=LESSEQUAL && input.LA(1)<=GREATEREQUAL) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_conditionalExpression2245);    throw mse;
                    }


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_conditionalExpression2257);
                    a1=expression();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_conditionalExpression2261);
                    a2=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.compare(analyzer, op.token, a2) : null);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end conditionalExpression


    // $ANTLR start namedOperationExpression
    // AADLWalker.g:724:1: namedOperationExpression returns [ACodeObject aObject] : ^( NamedOperator a1= expression a2= expression ) ;
    public final ACodeObject namedOperationExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree NamedOperator61=null;
        ACodeObject a1 = null;

        ACodeObject a2 = null;


        try {
            // AADLWalker.g:725:5: ( ^( NamedOperator a1= expression a2= expression ) )
            // AADLWalker.g:725:7: ^( NamedOperator a1= expression a2= expression )
            {
            NamedOperator61=(CommonTree)input.LT(1);
            match(input,NamedOperator,FOLLOW_NamedOperator_in_namedOperationExpression2292); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_namedOperationExpression2296);
            a1=expression();
            _fsp--;

            pushFollow(FOLLOW_expression_in_namedOperationExpression2300);
            a2=expression();
            _fsp--;


            match(input, Token.UP, null); 
            aObject = (a1!=null ? a1.namedOperation(analyzer, NamedOperator61.token, a2) : null);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end namedOperationExpression


    // $ANTLR start newExalgeExpression
    // AADLWalker.g:729:1: newExalgeExpression returns [ACodeObject aObject] : ^(op= '@' a1= expression a2= expression ) ;
    public final ACodeObject newExalgeExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject a1 = null;

        ACodeObject a2 = null;


        try {
            // AADLWalker.g:730:5: ( ^(op= '@' a1= expression a2= expression ) )
            // AADLWalker.g:730:7: ^(op= '@' a1= expression a2= expression )
            {
            op=(CommonTree)input.LT(1);
            match(input,111,FOLLOW_111_in_newExalgeExpression2332); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_newExalgeExpression2336);
            a1=expression();
            _fsp--;

            pushFollow(FOLLOW_expression_in_newExalgeExpression2340);
            a2=expression();
            _fsp--;


            match(input, Token.UP, null); 
            aObject = (a1!=null ? a1.cat(analyzer, op.token, a2) : null);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end newExalgeExpression


    // $ANTLR start mathOperationExpression
    // AADLWalker.g:734:1: mathOperationExpression returns [ACodeObject aObject] : ( ^(op= '*' a1= expression a2= expression ) | ^(op= '/' a1= expression a2= expression ) | ^(op= '%' a1= expression a2= expression ) );
    public final ACodeObject mathOperationExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject a1 = null;

        ACodeObject a2 = null;


        try {
            // AADLWalker.g:735:5: ( ^(op= '*' a1= expression a2= expression ) | ^(op= '/' a1= expression a2= expression ) | ^(op= '%' a1= expression a2= expression ) )
            int alt34=3;
            switch ( input.LA(1) ) {
            case STAR:
                {
                alt34=1;
                }
                break;
            case SLASH:
                {
                alt34=2;
                }
                break;
            case PERCENT:
                {
                alt34=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("734:1: mathOperationExpression returns [ACodeObject aObject] : ( ^(op= '*' a1= expression a2= expression ) | ^(op= '/' a1= expression a2= expression ) | ^(op= '%' a1= expression a2= expression ) );", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // AADLWalker.g:735:7: ^(op= '*' a1= expression a2= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,STAR,FOLLOW_STAR_in_mathOperationExpression2372); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_mathOperationExpression2376);
                    a1=expression();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_mathOperationExpression2380);
                    a2=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.multiple(analyzer, op.token, a2) : null);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:737:7: ^(op= '/' a1= expression a2= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,SLASH,FOLLOW_SLASH_in_mathOperationExpression2400); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_mathOperationExpression2404);
                    a1=expression();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_mathOperationExpression2408);
                    a2=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.divide(analyzer, op.token, a2) : null);

                    }
                    break;
                case 3 :
                    // AADLWalker.g:739:7: ^(op= '%' a1= expression a2= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,PERCENT,FOLLOW_PERCENT_in_mathOperationExpression2428); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_mathOperationExpression2432);
                    a1=expression();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_mathOperationExpression2436);
                    a2=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.mod(analyzer, op.token, a2) : null);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end mathOperationExpression


    // $ANTLR start plusExpression
    // AADLWalker.g:743:1: plusExpression returns [ACodeObject aObject] : ^(op= '+' expr= expression ret= plusExpressionRest[$op,$expr.aObject] ) ;
    public final ACodeObject plusExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject expr = null;

        ACodeObject ret = null;


        try {
            // AADLWalker.g:744:5: ( ^(op= '+' expr= expression ret= plusExpressionRest[$op,$expr.aObject] ) )
            // AADLWalker.g:744:7: ^(op= '+' expr= expression ret= plusExpressionRest[$op,$expr.aObject] )
            {
            op=(CommonTree)input.LT(1);
            match(input,PLUS,FOLLOW_PLUS_in_plusExpression2469); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_plusExpression2473);
            expr=expression();
            _fsp--;

            pushFollow(FOLLOW_plusExpressionRest_in_plusExpression2477);
            ret=plusExpressionRest(op, expr);
            _fsp--;


            match(input, Token.UP, null); 
            aObject = ret;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end plusExpression


    // $ANTLR start plusExpressionRest
    // AADLWalker.g:748:1: plusExpressionRest[CommonTree op, ACodeObject expr] returns [ACodeObject aObject] : ( expression | );
    public final ACodeObject plusExpressionRest(CommonTree op, ACodeObject expr) throws RecognitionException {
        ACodeObject aObject = null;

        ACodeObject expression62 = null;


        try {
            // AADLWalker.g:749:5: ( expression | )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( ((LA35_0>=FCALL_RM && LA35_0<=FCALL_SM)||(LA35_0>=FCALL_IM && LA35_0<=FCALL_MM)||(LA35_0>=EXBASE && LA35_0<=ARRAY)||LA35_0==JAVA_ACTION||LA35_0==Identifier||(LA35_0>=NamedOperator && LA35_0<=CMDARG)||(LA35_0>=PLUS && LA35_0<=SLASH)||(LA35_0>=LESS && LA35_0<=GREATER)||(LA35_0>=PERCENT && LA35_0<=GREATEREQUAL)||(LA35_0>=109 && LA35_0<=111)||LA35_0==113||LA35_0==116||(LA35_0>=120 && LA35_0<=122)||LA35_0==127) ) {
                alt35=1;
            }
            else if ( (LA35_0==UP) ) {
                alt35=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("748:1: plusExpressionRest[CommonTree op, ACodeObject expr] returns [ACodeObject aObject] : ( expression | );", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // AADLWalker.g:749:7: expression
                    {
                    pushFollow(FOLLOW_expression_in_plusExpressionRest2508);
                    expression62=expression();
                    _fsp--;

                    aObject = (expr!=null ? expr.add(analyzer, op.token, expression62) : null);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:750:9: 
                    {
                    aObject = (expr!=null ? expr.plus(analyzer, op.token) : null);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end plusExpressionRest


    // $ANTLR start minusExpression
    // AADLWalker.g:754:1: minusExpression returns [ACodeObject aObject] : ^(op= '-' expr= expression ret= minusExpressionRest[$op,$expr.aObject] ) ;
    public final ACodeObject minusExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject expr = null;

        ACodeObject ret = null;


        try {
            // AADLWalker.g:755:5: ( ^(op= '-' expr= expression ret= minusExpressionRest[$op,$expr.aObject] ) )
            // AADLWalker.g:755:7: ^(op= '-' expr= expression ret= minusExpressionRest[$op,$expr.aObject] )
            {
            op=(CommonTree)input.LT(1);
            match(input,MINUS,FOLLOW_MINUS_in_minusExpression2545); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_minusExpression2549);
            expr=expression();
            _fsp--;

            pushFollow(FOLLOW_minusExpressionRest_in_minusExpression2553);
            ret=minusExpressionRest(op, expr);
            _fsp--;


            match(input, Token.UP, null); 
            aObject = ret;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end minusExpression


    // $ANTLR start minusExpressionRest
    // AADLWalker.g:759:1: minusExpressionRest[CommonTree op, ACodeObject expr] returns [ACodeObject aObject] : ( expression | );
    public final ACodeObject minusExpressionRest(CommonTree op, ACodeObject expr) throws RecognitionException {
        ACodeObject aObject = null;

        ACodeObject expression63 = null;


        try {
            // AADLWalker.g:760:5: ( expression | )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( ((LA36_0>=FCALL_RM && LA36_0<=FCALL_SM)||(LA36_0>=FCALL_IM && LA36_0<=FCALL_MM)||(LA36_0>=EXBASE && LA36_0<=ARRAY)||LA36_0==JAVA_ACTION||LA36_0==Identifier||(LA36_0>=NamedOperator && LA36_0<=CMDARG)||(LA36_0>=PLUS && LA36_0<=SLASH)||(LA36_0>=LESS && LA36_0<=GREATER)||(LA36_0>=PERCENT && LA36_0<=GREATEREQUAL)||(LA36_0>=109 && LA36_0<=111)||LA36_0==113||LA36_0==116||(LA36_0>=120 && LA36_0<=122)||LA36_0==127) ) {
                alt36=1;
            }
            else if ( (LA36_0==UP) ) {
                alt36=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("759:1: minusExpressionRest[CommonTree op, ACodeObject expr] returns [ACodeObject aObject] : ( expression | );", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // AADLWalker.g:760:7: expression
                    {
                    pushFollow(FOLLOW_expression_in_minusExpressionRest2584);
                    expression63=expression();
                    _fsp--;

                    aObject = (expr!=null ? expr.subtract(analyzer, op.token, expression63) : null);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:761:9: 
                    {
                    aObject = (expr!=null ? expr.negate(analyzer, op.token) : null);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end minusExpressionRest


    // $ANTLR start simpleExpression
    // AADLWalker.g:764:1: simpleExpression returns [ACodeObject aObject] : ( ^(op= '!' a1= expression ) | ^(op= '~' a1= expression ) | ^(op= '^' a1= expression ) );
    public final ACodeObject simpleExpression() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree op=null;
        ACodeObject a1 = null;


        try {
            // AADLWalker.g:765:5: ( ^(op= '!' a1= expression ) | ^(op= '~' a1= expression ) | ^(op= '^' a1= expression ) )
            int alt37=3;
            switch ( input.LA(1) ) {
            case NOT:
                {
                alt37=1;
                }
                break;
            case BAR:
                {
                alt37=2;
                }
                break;
            case HAT:
                {
                alt37=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("764:1: simpleExpression returns [ACodeObject aObject] : ( ^(op= '!' a1= expression ) | ^(op= '~' a1= expression ) | ^(op= '^' a1= expression ) );", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // AADLWalker.g:765:7: ^(op= '!' a1= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simpleExpression2620); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_simpleExpression2624);
                    a1=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.not(analyzer, op.token) : null);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:766:7: ^(op= '~' a1= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,BAR,FOLLOW_BAR_in_simpleExpression2638); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_simpleExpression2642);
                    a1=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.bar(analyzer, op.token) : null);

                    }
                    break;
                case 3 :
                    // AADLWalker.g:767:7: ^(op= '^' a1= expression )
                    {
                    op=(CommonTree)input.LT(1);
                    match(input,HAT,FOLLOW_HAT_in_simpleExpression2656); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_simpleExpression2660);
                    a1=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aObject = (a1!=null ? a1.hat(analyzer, op.token) : null);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end simpleExpression


    // $ANTLR start primaryExpression
    // AADLWalker.g:770:1: primaryExpression returns [ACodeObject aObject] : primary ( selector[$aObject] )* ;
    public final ACodeObject primaryExpression() throws RecognitionException {
        ACodeObject aObject = null;

        ACodeObject primary64 = null;

        ACodeObject selector65 = null;


        try {
            // AADLWalker.g:771:5: ( primary ( selector[$aObject] )* )
            // AADLWalker.g:771:7: primary ( selector[$aObject] )*
            {
            pushFollow(FOLLOW_primary_in_primaryExpression2684);
            primary64=primary();
            _fsp--;

            aObject = primary64;
            // AADLWalker.g:772:6: ( selector[$aObject] )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==SCOPE) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // AADLWalker.g:772:7: selector[$aObject]
            	    {
            	    pushFollow(FOLLOW_selector_in_primaryExpression2694);
            	    selector65=selector(aObject);
            	    _fsp--;

            	    aObject = selector65;

            	    }
            	    break;

            	default :
            	    break loop38;
                }
            } while (true);


            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end primaryExpression


    // $ANTLR start primary
    // AADLWalker.g:777:1: primary returns [ACodeObject aObject] : ( arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | javaAction | Identifier | CMDARG );
    public final ACodeObject primary() throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree HexLiteral67=null;
        CommonTree OctalLiteral68=null;
        CommonTree IntegerLiteral69=null;
        CommonTree FloatingPointLiteral70=null;
        CommonTree CharacterLiteral73=null;
        CommonTree StringLiteral74=null;
        CommonTree Identifier79=null;
        CommonTree CMDARG80=null;
        ACodeLiteral arrayLiteral66 = null;

        ACodeLiteral nullLiteral71 = null;

        ACodeLiteral booleanLiteral72 = null;

        ACodeLiteral exbase75 = null;

        ACodeLiteral dtbase76 = null;

        ACodeFuncCall methodCall77 = null;

        ACodeJavaAction javaAction78 = null;


        try {
            // AADLWalker.g:778:5: ( arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | javaAction | Identifier | CMDARG )
            int alt39=15;
            switch ( input.LA(1) ) {
            case ARRAY:
                {
                alt39=1;
                }
                break;
            case HexLiteral:
                {
                alt39=2;
                }
                break;
            case OctalLiteral:
                {
                alt39=3;
                }
                break;
            case IntegerLiteral:
                {
                alt39=4;
                }
                break;
            case FloatingPointLiteral:
                {
                alt39=5;
                }
                break;
            case 122:
                {
                alt39=6;
                }
                break;
            case 120:
            case 121:
                {
                alt39=7;
                }
                break;
            case CharacterLiteral:
                {
                alt39=8;
                }
                break;
            case StringLiteral:
                {
                alt39=9;
                }
                break;
            case EXBASE:
                {
                alt39=10;
                }
                break;
            case DTBASE:
                {
                alt39=11;
                }
                break;
            case FCALL_RM:
            case FCALL_SM:
            case FCALL_IM:
            case FCALL_MM:
            case 113:
            case 116:
                {
                alt39=12;
                }
                break;
            case JAVA_ACTION:
                {
                alt39=13;
                }
                break;
            case Identifier:
                {
                alt39=14;
                }
                break;
            case CMDARG:
                {
                alt39=15;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("777:1: primary returns [ACodeObject aObject] : ( arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | javaAction | Identifier | CMDARG );", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // AADLWalker.g:778:7: arrayLiteral
                    {
                    pushFollow(FOLLOW_arrayLiteral_in_primary2722);
                    arrayLiteral66=arrayLiteral();
                    _fsp--;

                    aObject = arrayLiteral66;

                    }
                    break;
                case 2 :
                    // AADLWalker.g:780:7: HexLiteral
                    {
                    HexLiteral67=(CommonTree)input.LT(1);
                    match(input,HexLiteral,FOLLOW_HexLiteral_in_primary2734); 
                    aObject = ACodeLiteral.buildHexInteger(analyzer, HexLiteral67);

                    }
                    break;
                case 3 :
                    // AADLWalker.g:781:7: OctalLiteral
                    {
                    OctalLiteral68=(CommonTree)input.LT(1);
                    match(input,OctalLiteral,FOLLOW_OctalLiteral_in_primary2745); 
                    aObject = ACodeLiteral.buildOctalInteger(analyzer, OctalLiteral68);

                    }
                    break;
                case 4 :
                    // AADLWalker.g:783:7: IntegerLiteral
                    {
                    IntegerLiteral69=(CommonTree)input.LT(1);
                    match(input,IntegerLiteral,FOLLOW_IntegerLiteral_in_primary2757); 
                    aObject = ACodeLiteral.buildInteger(analyzer, IntegerLiteral69);

                    }
                    break;
                case 5 :
                    // AADLWalker.g:784:7: FloatingPointLiteral
                    {
                    FloatingPointLiteral70=(CommonTree)input.LT(1);
                    match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_primary2768); 
                    aObject = ACodeLiteral.buildDecimal(analyzer, FloatingPointLiteral70);

                    }
                    break;
                case 6 :
                    // AADLWalker.g:785:7: nullLiteral
                    {
                    pushFollow(FOLLOW_nullLiteral_in_primary2778);
                    nullLiteral71=nullLiteral();
                    _fsp--;

                    aObject = nullLiteral71;

                    }
                    break;
                case 7 :
                    // AADLWalker.g:786:7: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_primary2789);
                    booleanLiteral72=booleanLiteral();
                    _fsp--;

                    aObject = booleanLiteral72;

                    }
                    break;
                case 8 :
                    // AADLWalker.g:788:7: CharacterLiteral
                    {
                    CharacterLiteral73=(CommonTree)input.LT(1);
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_primary2801); 
                    aObject = ACodeLiteral.buildCharacter(analyzer, CharacterLiteral73);

                    }
                    break;
                case 9 :
                    // AADLWalker.g:790:7: StringLiteral
                    {
                    StringLiteral74=(CommonTree)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_primary2812); 
                    aObject = ACodeLiteral.buildString(analyzer, StringLiteral74);

                    }
                    break;
                case 10 :
                    // AADLWalker.g:791:7: exbase
                    {
                    pushFollow(FOLLOW_exbase_in_primary2823);
                    exbase75=exbase();
                    _fsp--;

                    aObject = exbase75;

                    }
                    break;
                case 11 :
                    // AADLWalker.g:792:7: dtbase
                    {
                    pushFollow(FOLLOW_dtbase_in_primary2835);
                    dtbase76=dtbase();
                    _fsp--;

                    aObject = dtbase76;

                    }
                    break;
                case 12 :
                    // AADLWalker.g:793:7: methodCall
                    {
                    pushFollow(FOLLOW_methodCall_in_primary2847);
                    methodCall77=methodCall();
                    _fsp--;

                    aObject = methodCall77;

                    }
                    break;
                case 13 :
                    // AADLWalker.g:794:7: javaAction
                    {
                    pushFollow(FOLLOW_javaAction_in_primary2858);
                    javaAction78=javaAction();
                    _fsp--;

                    aObject = javaAction78;

                    }
                    break;
                case 14 :
                    // AADLWalker.g:795:7: Identifier
                    {
                    Identifier79=(CommonTree)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_primary2869); 
                    aObject = ACodeVariable.buildVariableRef(analyzer, Identifier79.token);

                    }
                    break;
                case 15 :
                    // AADLWalker.g:796:7: CMDARG
                    {
                    CMDARG80=(CommonTree)input.LT(1);
                    match(input,CMDARG,FOLLOW_CMDARG_in_primary2880); 
                    aObject = ACodeVariable.buildVariableRef(analyzer, CMDARG80.token);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end primary


    // $ANTLR start selector
    // AADLWalker.g:799:1: selector[ACodeObject prm] returns [ACodeObject aObject] : ( ^( SCOPE Identifier ) | ^( SCOPE ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) ) );
    public final ACodeObject selector(ACodeObject prm) throws RecognitionException {
        ACodeObject aObject = null;

        CommonTree Identifier81=null;
        CommonTree Identifier83=null;
        ACodeObject expression82 = null;


        
        //--- begin [@init]
        List<ACodeObject> args = new ArrayList<ACodeObject>();
        //--- end [@init]

        try {
            // AADLWalker.g:805:5: ( ^( SCOPE Identifier ) | ^( SCOPE ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) ) )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==SCOPE) ) {
                int LA41_1 = input.LA(2);

                if ( (LA41_1==DOWN) ) {
                    int LA41_2 = input.LA(3);

                    if ( (LA41_2==Identifier) ) {
                        alt41=1;
                    }
                    else if ( (LA41_2==FCALL_RM) ) {
                        alt41=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("799:1: selector[ACodeObject prm] returns [ACodeObject aObject] : ( ^( SCOPE Identifier ) | ^( SCOPE ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) ) );", 41, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("799:1: selector[ACodeObject prm] returns [ACodeObject aObject] : ( ^( SCOPE Identifier ) | ^( SCOPE ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) ) );", 41, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("799:1: selector[ACodeObject prm] returns [ACodeObject aObject] : ( ^( SCOPE Identifier ) | ^( SCOPE ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) ) );", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // AADLWalker.g:805:7: ^( SCOPE Identifier )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_selector2911); 

                    match(input, Token.DOWN, null); 
                    Identifier81=(CommonTree)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_selector2913); 

                    match(input, Token.UP, null); 
                    aObject = (prm!=null ? prm.memberField(analyzer, Identifier81.token) : null);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:807:7: ^( SCOPE ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_selector2931); 

                    match(input, Token.DOWN, null); 
                    match(input,FCALL_RM,FOLLOW_FCALL_RM_in_selector2934); 

                    match(input, Token.DOWN, null); 
                    Identifier83=(CommonTree)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_selector2936); 
                    match(input,FUNCALL_ARGS,FOLLOW_FUNCALL_ARGS_in_selector2939); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // AADLWalker.g:807:52: ( expression )*
                        loop40:
                        do {
                            int alt40=2;
                            int LA40_0 = input.LA(1);

                            if ( ((LA40_0>=FCALL_RM && LA40_0<=FCALL_SM)||(LA40_0>=FCALL_IM && LA40_0<=FCALL_MM)||(LA40_0>=EXBASE && LA40_0<=ARRAY)||LA40_0==JAVA_ACTION||LA40_0==Identifier||(LA40_0>=NamedOperator && LA40_0<=CMDARG)||(LA40_0>=PLUS && LA40_0<=SLASH)||(LA40_0>=LESS && LA40_0<=GREATER)||(LA40_0>=PERCENT && LA40_0<=GREATEREQUAL)||(LA40_0>=109 && LA40_0<=111)||LA40_0==113||LA40_0==116||(LA40_0>=120 && LA40_0<=122)||LA40_0==127) ) {
                                alt40=1;
                            }


                            switch (alt40) {
                        	case 1 :
                        	    // AADLWalker.g:807:53: expression
                        	    {
                        	    pushFollow(FOLLOW_expression_in_selector2942);
                        	    expression82=expression();
                        	    _fsp--;

                        	    args.add(expression82);

                        	    }
                        	    break;

                        	default :
                        	    break loop40;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 

                    match(input, Token.UP, null); 
                    aObject = (prm!=null ? prm.memberMethod(analyzer, Identifier83.token, args) : null);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aObject;
    }
    // $ANTLR end selector


    // $ANTLR start methodCall
    // AADLWalker.g:811:1: methodCall returns [ACodeFuncCall aFuncCall] : (fc= callSystemMethod | fc= callRegisteredMethod | fc= callSpecialMethod | fc= callModuleMethod | fc= callInstanceMethod );
    public final ACodeFuncCall methodCall() throws RecognitionException {
        ACodeFuncCall aFuncCall = null;

        ACodeFuncCall fc = null;


        try {
            // AADLWalker.g:812:5: (fc= callSystemMethod | fc= callRegisteredMethod | fc= callSpecialMethod | fc= callModuleMethod | fc= callInstanceMethod )
            int alt42=5;
            switch ( input.LA(1) ) {
            case 113:
            case 116:
                {
                alt42=1;
                }
                break;
            case FCALL_RM:
                {
                alt42=2;
                }
                break;
            case FCALL_SM:
                {
                alt42=3;
                }
                break;
            case FCALL_MM:
                {
                alt42=4;
                }
                break;
            case FCALL_IM:
                {
                alt42=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("811:1: methodCall returns [ACodeFuncCall aFuncCall] : (fc= callSystemMethod | fc= callRegisteredMethod | fc= callSpecialMethod | fc= callModuleMethod | fc= callInstanceMethod );", 42, 0, input);

                throw nvae;
            }

            switch (alt42) {
                case 1 :
                    // AADLWalker.g:812:7: fc= callSystemMethod
                    {
                    pushFollow(FOLLOW_callSystemMethod_in_methodCall2979);
                    fc=callSystemMethod();
                    _fsp--;

                    aFuncCall = fc;

                    }
                    break;
                case 2 :
                    // AADLWalker.g:814:7: fc= callRegisteredMethod
                    {
                    pushFollow(FOLLOW_callRegisteredMethod_in_methodCall2992);
                    fc=callRegisteredMethod();
                    _fsp--;

                    aFuncCall = fc;

                    }
                    break;
                case 3 :
                    // AADLWalker.g:815:7: fc= callSpecialMethod
                    {
                    pushFollow(FOLLOW_callSpecialMethod_in_methodCall3004);
                    fc=callSpecialMethod();
                    _fsp--;

                    aFuncCall = fc;

                    }
                    break;
                case 4 :
                    // AADLWalker.g:816:7: fc= callModuleMethod
                    {
                    pushFollow(FOLLOW_callModuleMethod_in_methodCall3016);
                    fc=callModuleMethod();
                    _fsp--;

                    aFuncCall = fc;

                    }
                    break;
                case 5 :
                    // AADLWalker.g:817:7: fc= callInstanceMethod
                    {
                    pushFollow(FOLLOW_callInstanceMethod_in_methodCall3028);
                    fc=callInstanceMethod();
                    _fsp--;

                    aFuncCall = fc;

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aFuncCall;
    }
    // $ANTLR end methodCall


    // $ANTLR start callSystemMethod
    // AADLWalker.g:821:1: callSystemMethod returns [ACodeFuncCall aFuncCall] : ( ^(cs= 'cast' type expression ) | ^(ia= 'typeof' type expression ) );
    public final ACodeFuncCall callSystemMethod() throws RecognitionException {
        ACodeFuncCall aFuncCall = null;

        CommonTree cs=null;
        CommonTree ia=null;
        ACodeDataType type84 = null;

        ACodeObject expression85 = null;

        ACodeDataType type86 = null;

        ACodeObject expression87 = null;


        
        //--- begin [@init]
        //--- end [@init]

        try {
            // AADLWalker.g:826:5: ( ^(cs= 'cast' type expression ) | ^(ia= 'typeof' type expression ) )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==113) ) {
                alt43=1;
            }
            else if ( (LA43_0==116) ) {
                alt43=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("821:1: callSystemMethod returns [ACodeFuncCall aFuncCall] : ( ^(cs= 'cast' type expression ) | ^(ia= 'typeof' type expression ) );", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // AADLWalker.g:826:7: ^(cs= 'cast' type expression )
                    {
                    cs=(CommonTree)input.LT(1);
                    match(input,113,FOLLOW_113_in_callSystemMethod3059); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_type_in_callSystemMethod3061);
                    type84=type();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_callSystemMethod3063);
                    expression85=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aFuncCall = ACodeFuncCall.buildDirectCast(analyzer, cs.token, type84, expression85);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:828:7: ^(ia= 'typeof' type expression )
                    {
                    ia=(CommonTree)input.LT(1);
                    match(input,116,FOLLOW_116_in_callSystemMethod3082); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_type_in_callSystemMethod3084);
                    type86=type();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_callSystemMethod3086);
                    expression87=expression();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aFuncCall = ACodeFuncCall.buildInstanceOf(analyzer, ia.token, type86, expression87);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aFuncCall;
    }
    // $ANTLR end callSystemMethod


    // $ANTLR start callRegisteredMethod
    // AADLWalker.g:844:1: callRegisteredMethod returns [ACodeFuncCall aFuncCall] : ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) ;
    public final ACodeFuncCall callRegisteredMethod() throws RecognitionException {
        ACodeFuncCall aFuncCall = null;

        CommonTree Identifier89=null;
        ACodeObject expression88 = null;


        
        //--- begin [@init]
        List<ACodeObject> args = new ArrayList<ACodeObject>();
        //--- end [@init]

        try {
            // AADLWalker.g:850:5: ( ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) ) )
            // AADLWalker.g:850:7: ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( expression )* ) )
            {
            match(input,FCALL_RM,FOLLOW_FCALL_RM_in_callRegisteredMethod3124); 

            match(input, Token.DOWN, null); 
            Identifier89=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_callRegisteredMethod3126); 
            match(input,FUNCALL_ARGS,FOLLOW_FUNCALL_ARGS_in_callRegisteredMethod3129); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:850:44: ( expression )*
                loop44:
                do {
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( ((LA44_0>=FCALL_RM && LA44_0<=FCALL_SM)||(LA44_0>=FCALL_IM && LA44_0<=FCALL_MM)||(LA44_0>=EXBASE && LA44_0<=ARRAY)||LA44_0==JAVA_ACTION||LA44_0==Identifier||(LA44_0>=NamedOperator && LA44_0<=CMDARG)||(LA44_0>=PLUS && LA44_0<=SLASH)||(LA44_0>=LESS && LA44_0<=GREATER)||(LA44_0>=PERCENT && LA44_0<=GREATEREQUAL)||(LA44_0>=109 && LA44_0<=111)||LA44_0==113||LA44_0==116||(LA44_0>=120 && LA44_0<=122)||LA44_0==127) ) {
                        alt44=1;
                    }


                    switch (alt44) {
                	case 1 :
                	    // AADLWalker.g:850:45: expression
                	    {
                	    pushFollow(FOLLOW_expression_in_callRegisteredMethod3132);
                	    expression88=expression();
                	    _fsp--;

                	    args.add(expression88);

                	    }
                	    break;

                	default :
                	    break loop44;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            match(input, Token.UP, null); 
            aFuncCall = ACodeFuncCall.buildNormalCall(analyzer, Identifier89.token, args);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aFuncCall;
    }
    // $ANTLR end callRegisteredMethod


    // $ANTLR start callSpecialMethod
    // AADLWalker.g:854:1: callSpecialMethod returns [ACodeFuncCall aFuncCall] : ^( FCALL_SM Identifier ^( FUNCALL_PATH tgt= expression ) ^( FUNCALL_ARGS (a= expression )* ) ) ;
    public final ACodeFuncCall callSpecialMethod() throws RecognitionException {
        ACodeFuncCall aFuncCall = null;

        CommonTree Identifier90=null;
        ACodeObject tgt = null;

        ACodeObject a = null;


        
        //--- begin [@init]
        List<ACodeObject> args = new ArrayList<ACodeObject>();
        //--- end [@init]

        try {
            // AADLWalker.g:860:5: ( ^( FCALL_SM Identifier ^( FUNCALL_PATH tgt= expression ) ^( FUNCALL_ARGS (a= expression )* ) ) )
            // AADLWalker.g:860:7: ^( FCALL_SM Identifier ^( FUNCALL_PATH tgt= expression ) ^( FUNCALL_ARGS (a= expression )* ) )
            {
            match(input,FCALL_SM,FOLLOW_FCALL_SM_in_callSpecialMethod3170); 

            match(input, Token.DOWN, null); 
            Identifier90=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_callSpecialMethod3172); 
            match(input,FUNCALL_PATH,FOLLOW_FUNCALL_PATH_in_callSpecialMethod3181); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_callSpecialMethod3185);
            tgt=expression();
            _fsp--;


            match(input, Token.UP, null); 
            match(input,FUNCALL_ARGS,FOLLOW_FUNCALL_ARGS_in_callSpecialMethod3195); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:862:22: (a= expression )*
                loop45:
                do {
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( ((LA45_0>=FCALL_RM && LA45_0<=FCALL_SM)||(LA45_0>=FCALL_IM && LA45_0<=FCALL_MM)||(LA45_0>=EXBASE && LA45_0<=ARRAY)||LA45_0==JAVA_ACTION||LA45_0==Identifier||(LA45_0>=NamedOperator && LA45_0<=CMDARG)||(LA45_0>=PLUS && LA45_0<=SLASH)||(LA45_0>=LESS && LA45_0<=GREATER)||(LA45_0>=PERCENT && LA45_0<=GREATEREQUAL)||(LA45_0>=109 && LA45_0<=111)||LA45_0==113||LA45_0==116||(LA45_0>=120 && LA45_0<=122)||LA45_0==127) ) {
                        alt45=1;
                    }


                    switch (alt45) {
                	case 1 :
                	    // AADLWalker.g:862:23: a= expression
                	    {
                	    pushFollow(FOLLOW_expression_in_callSpecialMethod3200);
                	    a=expression();
                	    _fsp--;

                	    args.add(a);

                	    }
                	    break;

                	default :
                	    break loop45;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            match(input, Token.UP, null); 
            aFuncCall = ACodeFuncCall.buildSpecialCall(analyzer, Identifier90.token, tgt, args);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aFuncCall;
    }
    // $ANTLR end callSpecialMethod


    // $ANTLR start callModuleMethod
    // AADLWalker.g:866:1: callModuleMethod returns [ACodeFuncCall aFuncCall] : ^( FCALL_MM ^( FUNCALL_PATH tgt= callModuleMethodPath ) Identifier ^( FUNCALL_ARGS (a= expression )* ) ) ;
    public final ACodeFuncCall callModuleMethod() throws RecognitionException {
        ACodeFuncCall aFuncCall = null;

        CommonTree Identifier91=null;
        ACodeDataType tgt = null;

        ACodeObject a = null;


        
        //--- begin [@init]
        List<ACodeObject> args = new ArrayList<ACodeObject>();
        //--- end [@init]

        try {
            // AADLWalker.g:872:5: ( ^( FCALL_MM ^( FUNCALL_PATH tgt= callModuleMethodPath ) Identifier ^( FUNCALL_ARGS (a= expression )* ) ) )
            // AADLWalker.g:872:7: ^( FCALL_MM ^( FUNCALL_PATH tgt= callModuleMethodPath ) Identifier ^( FUNCALL_ARGS (a= expression )* ) )
            {
            match(input,FCALL_MM,FOLLOW_FCALL_MM_in_callModuleMethod3238); 

            match(input, Token.DOWN, null); 
            match(input,FUNCALL_PATH,FOLLOW_FUNCALL_PATH_in_callModuleMethod3241); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_callModuleMethodPath_in_callModuleMethod3245);
            tgt=callModuleMethodPath();
            _fsp--;


            match(input, Token.UP, null); 
            Identifier91=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_callModuleMethod3248); 
            match(input,FUNCALL_ARGS,FOLLOW_FUNCALL_ARGS_in_callModuleMethod3251); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:872:85: (a= expression )*
                loop46:
                do {
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( ((LA46_0>=FCALL_RM && LA46_0<=FCALL_SM)||(LA46_0>=FCALL_IM && LA46_0<=FCALL_MM)||(LA46_0>=EXBASE && LA46_0<=ARRAY)||LA46_0==JAVA_ACTION||LA46_0==Identifier||(LA46_0>=NamedOperator && LA46_0<=CMDARG)||(LA46_0>=PLUS && LA46_0<=SLASH)||(LA46_0>=LESS && LA46_0<=GREATER)||(LA46_0>=PERCENT && LA46_0<=GREATEREQUAL)||(LA46_0>=109 && LA46_0<=111)||LA46_0==113||LA46_0==116||(LA46_0>=120 && LA46_0<=122)||LA46_0==127) ) {
                        alt46=1;
                    }


                    switch (alt46) {
                	case 1 :
                	    // AADLWalker.g:872:86: a= expression
                	    {
                	    pushFollow(FOLLOW_expression_in_callModuleMethod3256);
                	    a=expression();
                	    _fsp--;

                	    args.add(a);

                	    }
                	    break;

                	default :
                	    break loop46;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            match(input, Token.UP, null); 
            aFuncCall = ACodeFuncCall.buildModuleMethodCall(analyzer, Identifier91.token, tgt, args);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aFuncCall;
    }
    // $ANTLR end callModuleMethod


    // $ANTLR start callModuleMethodPath
    // AADLWalker.g:876:1: callModuleMethodPath returns [ACodeDataType aType] : ( Identifier | javaClassPath );
    public final ACodeDataType callModuleMethodPath() throws RecognitionException {
        ACodeDataType aType = null;

        CommonTree Identifier92=null;
        List<Token> javaClassPath93 = null;


        try {
            // AADLWalker.g:877:5: ( Identifier | javaClassPath )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==Identifier) ) {
                alt47=1;
            }
            else if ( (LA47_0==JAVACLASSPATH) ) {
                alt47=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("876:1: callModuleMethodPath returns [ACodeDataType aType] : ( Identifier | javaClassPath );", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // AADLWalker.g:877:7: Identifier
                    {
                    Identifier92=(CommonTree)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_callModuleMethodPath3289); 
                    aType = ACodeDataType.buildType(analyzer, Identifier92.token);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:878:7: javaClassPath
                    {
                    pushFollow(FOLLOW_javaClassPath_in_callModuleMethodPath3299);
                    javaClassPath93=javaClassPath();
                    _fsp--;

                    aType = ACodeDataType.buildType(analyzer, javaClassPath93);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aType;
    }
    // $ANTLR end callModuleMethodPath


    // $ANTLR start callInstanceMethod
    // AADLWalker.g:881:1: callInstanceMethod returns [ACodeFuncCall aFuncCall] : ^( FCALL_IM ^( FUNCALL_PATH tgt= expression ) Identifier ^( FUNCALL_ARGS (a= expression )* ) ) ;
    public final ACodeFuncCall callInstanceMethod() throws RecognitionException {
        ACodeFuncCall aFuncCall = null;

        CommonTree Identifier94=null;
        ACodeObject tgt = null;

        ACodeObject a = null;


        
        //--- begin [@init]
        List<ACodeObject> args = new ArrayList<ACodeObject>();
        //--- end [@init]

        try {
            // AADLWalker.g:887:5: ( ^( FCALL_IM ^( FUNCALL_PATH tgt= expression ) Identifier ^( FUNCALL_ARGS (a= expression )* ) ) )
            // AADLWalker.g:887:7: ^( FCALL_IM ^( FUNCALL_PATH tgt= expression ) Identifier ^( FUNCALL_ARGS (a= expression )* ) )
            {
            match(input,FCALL_IM,FOLLOW_FCALL_IM_in_callInstanceMethod3327); 

            match(input, Token.DOWN, null); 
            match(input,FUNCALL_PATH,FOLLOW_FUNCALL_PATH_in_callInstanceMethod3330); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_callInstanceMethod3334);
            tgt=expression();
            _fsp--;


            match(input, Token.UP, null); 
            Identifier94=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_callInstanceMethod3337); 
            match(input,FUNCALL_ARGS,FOLLOW_FUNCALL_ARGS_in_callInstanceMethod3340); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // AADLWalker.g:887:75: (a= expression )*
                loop48:
                do {
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( ((LA48_0>=FCALL_RM && LA48_0<=FCALL_SM)||(LA48_0>=FCALL_IM && LA48_0<=FCALL_MM)||(LA48_0>=EXBASE && LA48_0<=ARRAY)||LA48_0==JAVA_ACTION||LA48_0==Identifier||(LA48_0>=NamedOperator && LA48_0<=CMDARG)||(LA48_0>=PLUS && LA48_0<=SLASH)||(LA48_0>=LESS && LA48_0<=GREATER)||(LA48_0>=PERCENT && LA48_0<=GREATEREQUAL)||(LA48_0>=109 && LA48_0<=111)||LA48_0==113||LA48_0==116||(LA48_0>=120 && LA48_0<=122)||LA48_0==127) ) {
                        alt48=1;
                    }


                    switch (alt48) {
                	case 1 :
                	    // AADLWalker.g:887:76: a= expression
                	    {
                	    pushFollow(FOLLOW_expression_in_callInstanceMethod3345);
                	    a=expression();
                	    _fsp--;

                	    args.add(a);

                	    }
                	    break;

                	default :
                	    break loop48;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            match(input, Token.UP, null); 
            aFuncCall = ACodeFuncCall.buildInstanceMethodCall(analyzer, Identifier94.token, tgt, args);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aFuncCall;
    }
    // $ANTLR end callInstanceMethod


    // $ANTLR start javaHeaderAction
    // AADLWalker.g:891:1: javaHeaderAction returns [ACodeJavaAction aAction] : JAVA_HEADER_ACTION ;
    public final ACodeJavaAction javaHeaderAction() throws RecognitionException {
        ACodeJavaAction aAction = null;

        CommonTree JAVA_HEADER_ACTION95=null;

        try {
            // AADLWalker.g:892:5: ( JAVA_HEADER_ACTION )
            // AADLWalker.g:892:7: JAVA_HEADER_ACTION
            {
            JAVA_HEADER_ACTION95=(CommonTree)input.LT(1);
            match(input,JAVA_HEADER_ACTION,FOLLOW_JAVA_HEADER_ACTION_in_javaHeaderAction3378); 
            aAction = ACodeJavaAction.buildJavaHeaderAction(analyzer, JAVA_HEADER_ACTION95);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aAction;
    }
    // $ANTLR end javaHeaderAction


    // $ANTLR start javaAction
    // AADLWalker.g:896:1: javaAction returns [ACodeJavaAction aAction] : JAVA_ACTION ;
    public final ACodeJavaAction javaAction() throws RecognitionException {
        ACodeJavaAction aAction = null;

        CommonTree JAVA_ACTION96=null;

        try {
            // AADLWalker.g:897:5: ( JAVA_ACTION )
            // AADLWalker.g:897:7: JAVA_ACTION
            {
            JAVA_ACTION96=(CommonTree)input.LT(1);
            match(input,JAVA_ACTION,FOLLOW_JAVA_ACTION_in_javaAction3406); 
            aAction = ACodeJavaAction.buildJavaAction(analyzer, JAVA_ACTION96);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aAction;
    }
    // $ANTLR end javaAction

    public static class fileDeclarator_return extends TreeRuleReturnScope {
        public Token fileType;
        public ACodeObject fileArg;
        public ACodeObject encoding;
        public List<AADLFileOptionEntry> foptions;
    };

    // $ANTLR start fileDeclarator
    // AADLWalker.g:911:1: fileDeclarator returns [Token fileType, ACodeObject fileArg, ACodeObject encoding, List<AADLFileOptionEntry> foptions] : ( ^(ft= 'txtFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? ) | ^(ft= 'csvFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? ) | ^(ft= 'xmlFile' (fo= fileDeclaratorOptionEntry )* fa= expression ) );
    public final fileDeclarator_return fileDeclarator() throws RecognitionException {
        fileDeclarator_return retval = new fileDeclarator_return();
        retval.start = input.LT(1);

        CommonTree ft=null;
        AADLFileOptionEntry fo = null;

        ACodeObject fa = null;

        ACodeObject enc = null;


        
        //--- begin [@init]
        retval.foptions = new ArrayList<AADLFileOptionEntry>();
        //--- end [@init]

        try {
            // AADLWalker.g:917:5: ( ^(ft= 'txtFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? ) | ^(ft= 'csvFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? ) | ^(ft= 'xmlFile' (fo= fileDeclaratorOptionEntry )* fa= expression ) )
            int alt54=3;
            switch ( input.LA(1) ) {
            case 117:
                {
                alt54=1;
                }
                break;
            case 118:
                {
                alt54=2;
                }
                break;
            case 119:
                {
                alt54=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("911:1: fileDeclarator returns [Token fileType, ACodeObject fileArg, ACodeObject encoding, List<AADLFileOptionEntry> foptions] : ( ^(ft= 'txtFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? ) | ^(ft= 'csvFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? ) | ^(ft= 'xmlFile' (fo= fileDeclaratorOptionEntry )* fa= expression ) );", 54, 0, input);

                throw nvae;
            }

            switch (alt54) {
                case 1 :
                    // AADLWalker.g:917:7: ^(ft= 'txtFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? )
                    {
                    ft=(CommonTree)input.LT(1);
                    match(input,117,FOLLOW_117_in_fileDeclarator3446); 

                    retval.fileType =ft.token;

                    match(input, Token.DOWN, null); 
                    // AADLWalker.g:917:45: (fo= fileDeclaratorOptionEntry )*
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==FOPTION) ) {
                            alt49=1;
                        }


                        switch (alt49) {
                    	case 1 :
                    	    // AADLWalker.g:917:46: fo= fileDeclaratorOptionEntry
                    	    {
                    	    pushFollow(FOLLOW_fileDeclaratorOptionEntry_in_fileDeclarator3453);
                    	    fo=fileDeclaratorOptionEntry();
                    	    _fsp--;

                    	    retval.foptions.add(fo);

                    	    }
                    	    break;

                    	default :
                    	    break loop49;
                        }
                    } while (true);

                    pushFollow(FOLLOW_expression_in_fileDeclarator3460);
                    fa=expression();
                    _fsp--;

                    retval.fileArg =fa;
                    // AADLWalker.g:917:145: (enc= expression )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( ((LA50_0>=FCALL_RM && LA50_0<=FCALL_SM)||(LA50_0>=FCALL_IM && LA50_0<=FCALL_MM)||(LA50_0>=EXBASE && LA50_0<=ARRAY)||LA50_0==JAVA_ACTION||LA50_0==Identifier||(LA50_0>=NamedOperator && LA50_0<=CMDARG)||(LA50_0>=PLUS && LA50_0<=SLASH)||(LA50_0>=LESS && LA50_0<=GREATER)||(LA50_0>=PERCENT && LA50_0<=GREATEREQUAL)||(LA50_0>=109 && LA50_0<=111)||LA50_0==113||LA50_0==116||(LA50_0>=120 && LA50_0<=122)||LA50_0==127) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // AADLWalker.g:917:146: enc= expression
                            {
                            pushFollow(FOLLOW_expression_in_fileDeclarator3467);
                            enc=expression();
                            _fsp--;

                            retval.encoding =enc;

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // AADLWalker.g:918:7: ^(ft= 'csvFile' (fo= fileDeclaratorOptionEntry )* fa= expression (enc= expression )? )
                    {
                    ft=(CommonTree)input.LT(1);
                    match(input,118,FOLLOW_118_in_fileDeclarator3482); 

                    retval.fileType =ft.token;

                    match(input, Token.DOWN, null); 
                    // AADLWalker.g:918:45: (fo= fileDeclaratorOptionEntry )*
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==FOPTION) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // AADLWalker.g:918:46: fo= fileDeclaratorOptionEntry
                    	    {
                    	    pushFollow(FOLLOW_fileDeclaratorOptionEntry_in_fileDeclarator3489);
                    	    fo=fileDeclaratorOptionEntry();
                    	    _fsp--;

                    	    retval.foptions.add(fo);

                    	    }
                    	    break;

                    	default :
                    	    break loop51;
                        }
                    } while (true);

                    pushFollow(FOLLOW_expression_in_fileDeclarator3496);
                    fa=expression();
                    _fsp--;

                    retval.fileArg =fa;
                    // AADLWalker.g:918:145: (enc= expression )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( ((LA52_0>=FCALL_RM && LA52_0<=FCALL_SM)||(LA52_0>=FCALL_IM && LA52_0<=FCALL_MM)||(LA52_0>=EXBASE && LA52_0<=ARRAY)||LA52_0==JAVA_ACTION||LA52_0==Identifier||(LA52_0>=NamedOperator && LA52_0<=CMDARG)||(LA52_0>=PLUS && LA52_0<=SLASH)||(LA52_0>=LESS && LA52_0<=GREATER)||(LA52_0>=PERCENT && LA52_0<=GREATEREQUAL)||(LA52_0>=109 && LA52_0<=111)||LA52_0==113||LA52_0==116||(LA52_0>=120 && LA52_0<=122)||LA52_0==127) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // AADLWalker.g:918:146: enc= expression
                            {
                            pushFollow(FOLLOW_expression_in_fileDeclarator3503);
                            enc=expression();
                            _fsp--;

                            retval.encoding =enc;

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // AADLWalker.g:919:7: ^(ft= 'xmlFile' (fo= fileDeclaratorOptionEntry )* fa= expression )
                    {
                    ft=(CommonTree)input.LT(1);
                    match(input,119,FOLLOW_119_in_fileDeclarator3518); 

                    retval.fileType =ft.token;

                    match(input, Token.DOWN, null); 
                    // AADLWalker.g:919:45: (fo= fileDeclaratorOptionEntry )*
                    loop53:
                    do {
                        int alt53=2;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==FOPTION) ) {
                            alt53=1;
                        }


                        switch (alt53) {
                    	case 1 :
                    	    // AADLWalker.g:919:46: fo= fileDeclaratorOptionEntry
                    	    {
                    	    pushFollow(FOLLOW_fileDeclaratorOptionEntry_in_fileDeclarator3525);
                    	    fo=fileDeclaratorOptionEntry();
                    	    _fsp--;

                    	    retval.foptions.add(fo);

                    	    }
                    	    break;

                    	default :
                    	    break loop53;
                        }
                    } while (true);

                    pushFollow(FOLLOW_expression_in_fileDeclarator3532);
                    fa=expression();
                    _fsp--;

                    retval.fileArg =fa;

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fileDeclarator


    // $ANTLR start fileDeclaratorOptionEntry
    // AADLWalker.g:924:1: fileDeclaratorOptionEntry returns [AADLFileOptionEntry optEntry] : ^( FOPTION Identifier ( expression )? ) ;
    public final AADLFileOptionEntry fileDeclaratorOptionEntry() throws RecognitionException {
        AADLFileOptionEntry optEntry = null;

        CommonTree Identifier97=null;
        ACodeObject expression98 = null;


        try {
            // AADLWalker.g:925:5: ( ^( FOPTION Identifier ( expression )? ) )
            // AADLWalker.g:925:7: ^( FOPTION Identifier ( expression )? )
            {
            match(input,FOPTION,FOLLOW_FOPTION_in_fileDeclaratorOptionEntry3559); 

            match(input, Token.DOWN, null); 
            Identifier97=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_fileDeclaratorOptionEntry3561); 
            // AADLWalker.g:925:28: ( expression )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( ((LA55_0>=FCALL_RM && LA55_0<=FCALL_SM)||(LA55_0>=FCALL_IM && LA55_0<=FCALL_MM)||(LA55_0>=EXBASE && LA55_0<=ARRAY)||LA55_0==JAVA_ACTION||LA55_0==Identifier||(LA55_0>=NamedOperator && LA55_0<=CMDARG)||(LA55_0>=PLUS && LA55_0<=SLASH)||(LA55_0>=LESS && LA55_0<=GREATER)||(LA55_0>=PERCENT && LA55_0<=GREATEREQUAL)||(LA55_0>=109 && LA55_0<=111)||LA55_0==113||LA55_0==116||(LA55_0>=120 && LA55_0<=122)||LA55_0==127) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // AADLWalker.g:925:28: expression
                    {
                    pushFollow(FOLLOW_expression_in_fileDeclaratorOptionEntry3563);
                    expression98=expression();
                    _fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 
            optEntry = new AADLFileOptionEntry(Identifier97.token, expression98);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return optEntry;
    }
    // $ANTLR end fileDeclaratorOptionEntry


    // $ANTLR start variableDefinition
    // AADLWalker.g:930:1: variableDefinition returns [ACodeVariable aVarDef] : ^( VARDEF Identifier type ) ;
    public final ACodeVariable variableDefinition() throws RecognitionException {
        ACodeVariable aVarDef = null;

        CommonTree Identifier99=null;
        ACodeDataType type100 = null;


        try {
            // AADLWalker.g:931:5: ( ^( VARDEF Identifier type ) )
            // AADLWalker.g:931:7: ^( VARDEF Identifier type )
            {
            match(input,VARDEF,FOLLOW_VARDEF_in_variableDefinition3595); 

            match(input, Token.DOWN, null); 
            Identifier99=(CommonTree)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_variableDefinition3597); 
            pushFollow(FOLLOW_type_in_variableDefinition3599);
            type100=type();
            _fsp--;


            match(input, Token.UP, null); 
            aVarDef = ACodeVariable.buildValiableDefinition(analyzer, false, Identifier99.token, type100);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aVarDef;
    }
    // $ANTLR end variableDefinition


    // $ANTLR start type
    // AADLWalker.g:935:1: type returns [ACodeDataType aType] : ( ^( VARTYPE Identifier ) | ^( VARTYPE javaClassPath ) );
    public final ACodeDataType type() throws RecognitionException {
        ACodeDataType aType = null;

        CommonTree Identifier101=null;
        List<Token> javaClassPath102 = null;


        try {
            // AADLWalker.g:936:5: ( ^( VARTYPE Identifier ) | ^( VARTYPE javaClassPath ) )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==VARTYPE) ) {
                int LA56_1 = input.LA(2);

                if ( (LA56_1==DOWN) ) {
                    int LA56_2 = input.LA(3);

                    if ( (LA56_2==Identifier) ) {
                        alt56=1;
                    }
                    else if ( (LA56_2==JAVACLASSPATH) ) {
                        alt56=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("935:1: type returns [ACodeDataType aType] : ( ^( VARTYPE Identifier ) | ^( VARTYPE javaClassPath ) );", 56, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("935:1: type returns [ACodeDataType aType] : ( ^( VARTYPE Identifier ) | ^( VARTYPE javaClassPath ) );", 56, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("935:1: type returns [ACodeDataType aType] : ( ^( VARTYPE Identifier ) | ^( VARTYPE javaClassPath ) );", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // AADLWalker.g:936:7: ^( VARTYPE Identifier )
                    {
                    match(input,VARTYPE,FOLLOW_VARTYPE_in_type3629); 

                    match(input, Token.DOWN, null); 
                    Identifier101=(CommonTree)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_type3631); 

                    match(input, Token.UP, null); 
                    aType = ACodeDataType.buildType(analyzer, Identifier101.token);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:937:7: ^( VARTYPE javaClassPath )
                    {
                    match(input,VARTYPE,FOLLOW_VARTYPE_in_type3644); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_javaClassPath_in_type3646);
                    javaClassPath102=javaClassPath();
                    _fsp--;


                    match(input, Token.UP, null); 
                    aType = ACodeDataType.buildType(analyzer, javaClassPath102);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aType;
    }
    // $ANTLR end type


    // $ANTLR start javaClassPath
    // AADLWalker.g:940:1: javaClassPath returns [List<Token> tokens] : ^( JAVACLASSPATH ( Identifier )+ ) ;
    public final List<Token> javaClassPath() throws RecognitionException {
        List<Token> tokens = null;

        CommonTree Identifier103=null;

        
        //--- begin [@init]
        List<Token> ids = new ArrayList<Token>();
        //--- end [@init]

        try {
            // AADLWalker.g:946:5: ( ^( JAVACLASSPATH ( Identifier )+ ) )
            // AADLWalker.g:946:7: ^( JAVACLASSPATH ( Identifier )+ )
            {
            match(input,JAVACLASSPATH,FOLLOW_JAVACLASSPATH_in_javaClassPath3675); 

            match(input, Token.DOWN, null); 
            // AADLWalker.g:946:23: ( Identifier )+
            int cnt57=0;
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==Identifier) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // AADLWalker.g:946:24: Identifier
            	    {
            	    Identifier103=(CommonTree)input.LT(1);
            	    match(input,Identifier,FOLLOW_Identifier_in_javaClassPath3678); 
            	    ids.add(Identifier103.token);

            	    }
            	    break;

            	default :
            	    if ( cnt57 >= 1 ) break loop57;
                        EarlyExitException eee =
                            new EarlyExitException(57, input);
                        throw eee;
                }
                cnt57++;
            } while (true);


            match(input, Token.UP, null); 
            tokens = ids;

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return tokens;
    }
    // $ANTLR end javaClassPath


    // $ANTLR start arrayLiteral
    // AADLWalker.g:952:1: arrayLiteral returns [ACodeLiteral aLiteral] : ( ^( ARRAY (al= expression )+ ) | ^( ARRAY ( ^( HASH hk= expression hv= expression ) )+ ) );
    public final ACodeLiteral arrayLiteral() throws RecognitionException {
        ACodeLiteral aLiteral = null;

        CommonTree ARRAY104=null;
        CommonTree ARRAY105=null;
        ACodeObject al = null;

        ACodeObject hk = null;

        ACodeObject hv = null;


        
        //--- begin exbase[@init]
        List<ACodeObject> expKeys = new ArrayList<ACodeObject>();
        List<ACodeObject> expVals = new ArrayList<ACodeObject>();
        //--- end exbase[@init]

        try {
            // AADLWalker.g:959:5: ( ^( ARRAY (al= expression )+ ) | ^( ARRAY ( ^( HASH hk= expression hv= expression ) )+ ) )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==ARRAY) ) {
                int LA60_1 = input.LA(2);

                if ( (LA60_1==DOWN) ) {
                    int LA60_2 = input.LA(3);

                    if ( ((LA60_2>=FCALL_RM && LA60_2<=FCALL_SM)||(LA60_2>=FCALL_IM && LA60_2<=FCALL_MM)||(LA60_2>=EXBASE && LA60_2<=ARRAY)||LA60_2==JAVA_ACTION||LA60_2==Identifier||(LA60_2>=NamedOperator && LA60_2<=CMDARG)||(LA60_2>=PLUS && LA60_2<=SLASH)||(LA60_2>=LESS && LA60_2<=GREATER)||(LA60_2>=PERCENT && LA60_2<=GREATEREQUAL)||(LA60_2>=109 && LA60_2<=111)||LA60_2==113||LA60_2==116||(LA60_2>=120 && LA60_2<=122)||LA60_2==127) ) {
                        alt60=1;
                    }
                    else if ( (LA60_2==HASH) ) {
                        alt60=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("952:1: arrayLiteral returns [ACodeLiteral aLiteral] : ( ^( ARRAY (al= expression )+ ) | ^( ARRAY ( ^( HASH hk= expression hv= expression ) )+ ) );", 60, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("952:1: arrayLiteral returns [ACodeLiteral aLiteral] : ( ^( ARRAY (al= expression )+ ) | ^( ARRAY ( ^( HASH hk= expression hv= expression ) )+ ) );", 60, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("952:1: arrayLiteral returns [ACodeLiteral aLiteral] : ( ^( ARRAY (al= expression )+ ) | ^( ARRAY ( ^( HASH hk= expression hv= expression ) )+ ) );", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // AADLWalker.g:959:7: ^( ARRAY (al= expression )+ )
                    {
                    ARRAY104=(CommonTree)input.LT(1);
                    match(input,ARRAY,FOLLOW_ARRAY_in_arrayLiteral3714); 

                    match(input, Token.DOWN, null); 
                    // AADLWalker.g:959:15: (al= expression )+
                    int cnt58=0;
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( ((LA58_0>=FCALL_RM && LA58_0<=FCALL_SM)||(LA58_0>=FCALL_IM && LA58_0<=FCALL_MM)||(LA58_0>=EXBASE && LA58_0<=ARRAY)||LA58_0==JAVA_ACTION||LA58_0==Identifier||(LA58_0>=NamedOperator && LA58_0<=CMDARG)||(LA58_0>=PLUS && LA58_0<=SLASH)||(LA58_0>=LESS && LA58_0<=GREATER)||(LA58_0>=PERCENT && LA58_0<=GREATEREQUAL)||(LA58_0>=109 && LA58_0<=111)||LA58_0==113||LA58_0==116||(LA58_0>=120 && LA58_0<=122)||LA58_0==127) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // AADLWalker.g:959:16: al= expression
                    	    {
                    	    pushFollow(FOLLOW_expression_in_arrayLiteral3719);
                    	    al=expression();
                    	    _fsp--;

                    	    expVals.add(al);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt58 >= 1 ) break loop58;
                                EarlyExitException eee =
                                    new EarlyExitException(58, input);
                                throw eee;
                        }
                        cnt58++;
                    } while (true);


                    match(input, Token.UP, null); 
                     aLiteral = ACodeLiteral.buildArrayLiteral(analyzer, ARRAY104, expVals); 

                    }
                    break;
                case 2 :
                    // AADLWalker.g:961:7: ^( ARRAY ( ^( HASH hk= expression hv= expression ) )+ )
                    {
                    ARRAY105=(CommonTree)input.LT(1);
                    match(input,ARRAY,FOLLOW_ARRAY_in_arrayLiteral3740); 

                    match(input, Token.DOWN, null); 
                    // AADLWalker.g:961:15: ( ^( HASH hk= expression hv= expression ) )+
                    int cnt59=0;
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);

                        if ( (LA59_0==HASH) ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // AADLWalker.g:961:16: ^( HASH hk= expression hv= expression )
                    	    {
                    	    match(input,HASH,FOLLOW_HASH_in_arrayLiteral3744); 

                    	    match(input, Token.DOWN, null); 
                    	    pushFollow(FOLLOW_expression_in_arrayLiteral3748);
                    	    hk=expression();
                    	    _fsp--;

                    	    expKeys.add(hk);
                    	    pushFollow(FOLLOW_expression_in_arrayLiteral3753);
                    	    hv=expression();
                    	    _fsp--;

                    	    expVals.add(hv);

                    	    match(input, Token.UP, null); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt59 >= 1 ) break loop59;
                                EarlyExitException eee =
                                    new EarlyExitException(59, input);
                                throw eee;
                        }
                        cnt59++;
                    } while (true);


                    match(input, Token.UP, null); 
                     aLiteral = ACodeLiteral.buildMapLiteral(analyzer, ARRAY105, expKeys, expVals); 

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aLiteral;
    }
    // $ANTLR end arrayLiteral


    // $ANTLR start booleanLiteral
    // AADLWalker.g:965:1: booleanLiteral returns [ACodeLiteral aLiteral] : (bt= 'true' | bf= 'false' );
    public final ACodeLiteral booleanLiteral() throws RecognitionException {
        ACodeLiteral aLiteral = null;

        CommonTree bt=null;
        CommonTree bf=null;

        try {
            // AADLWalker.g:966:5: (bt= 'true' | bf= 'false' )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==120) ) {
                alt61=1;
            }
            else if ( (LA61_0==121) ) {
                alt61=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("965:1: booleanLiteral returns [ACodeLiteral aLiteral] : (bt= 'true' | bf= 'false' );", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // AADLWalker.g:966:7: bt= 'true'
                    {
                    bt=(CommonTree)input.LT(1);
                    match(input,120,FOLLOW_120_in_booleanLiteral3789); 
                    aLiteral = ACodeLiteral.buildBoolean(analyzer, bt);

                    }
                    break;
                case 2 :
                    // AADLWalker.g:967:7: bf= 'false'
                    {
                    bf=(CommonTree)input.LT(1);
                    match(input,121,FOLLOW_121_in_booleanLiteral3801); 
                    aLiteral = ACodeLiteral.buildBoolean(analyzer, bf);

                    }
                    break;

            }
        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aLiteral;
    }
    // $ANTLR end booleanLiteral


    // $ANTLR start nullLiteral
    // AADLWalker.g:970:1: nullLiteral returns [ACodeLiteral aLiteral] : nl= 'null' ;
    public final ACodeLiteral nullLiteral() throws RecognitionException {
        ACodeLiteral aLiteral = null;

        CommonTree nl=null;

        try {
            // AADLWalker.g:971:5: (nl= 'null' )
            // AADLWalker.g:971:7: nl= 'null'
            {
            nl=(CommonTree)input.LT(1);
            match(input,122,FOLLOW_122_in_nullLiteral3826); 
            aLiteral = ACodeLiteral.buildNull(analyzer, nl);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aLiteral;
    }
    // $ANTLR end nullLiteral


    // $ANTLR start exbase
    // AADLWalker.g:974:1: exbase returns [ACodeLiteral aLiteral] : ^( EXBASE ( expression )+ ) ;
    public final ACodeLiteral exbase() throws RecognitionException {
        ACodeLiteral aLiteral = null;

        CommonTree EXBASE107=null;
        ACodeObject expression106 = null;


        
        //--- begin exbase[@init]
        List<ACodeObject> codes = new ArrayList<ACodeObject>();
        //--- end exbase[@init]

        try {
            // AADLWalker.g:980:5: ( ^( EXBASE ( expression )+ ) )
            // AADLWalker.g:980:7: ^( EXBASE ( expression )+ )
            {
            EXBASE107=(CommonTree)input.LT(1);
            match(input,EXBASE,FOLLOW_EXBASE_in_exbase3854); 

            match(input, Token.DOWN, null); 
            // AADLWalker.g:980:16: ( expression )+
            int cnt62=0;
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( ((LA62_0>=FCALL_RM && LA62_0<=FCALL_SM)||(LA62_0>=FCALL_IM && LA62_0<=FCALL_MM)||(LA62_0>=EXBASE && LA62_0<=ARRAY)||LA62_0==JAVA_ACTION||LA62_0==Identifier||(LA62_0>=NamedOperator && LA62_0<=CMDARG)||(LA62_0>=PLUS && LA62_0<=SLASH)||(LA62_0>=LESS && LA62_0<=GREATER)||(LA62_0>=PERCENT && LA62_0<=GREATEREQUAL)||(LA62_0>=109 && LA62_0<=111)||LA62_0==113||LA62_0==116||(LA62_0>=120 && LA62_0<=122)||LA62_0==127) ) {
                    alt62=1;
                }


                switch (alt62) {
            	case 1 :
            	    // AADLWalker.g:980:17: expression
            	    {
            	    pushFollow(FOLLOW_expression_in_exbase3857);
            	    expression106=expression();
            	    _fsp--;

            	    codes.add(expression106);

            	    }
            	    break;

            	default :
            	    if ( cnt62 >= 1 ) break loop62;
                        EarlyExitException eee =
                            new EarlyExitException(62, input);
                        throw eee;
                }
                cnt62++;
            } while (true);


            match(input, Token.UP, null); 
            aLiteral = ACodeLiteral.buildExBase(analyzer, EXBASE107, codes);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aLiteral;
    }
    // $ANTLR end exbase


    // $ANTLR start dtbase
    // AADLWalker.g:984:1: dtbase returns [ACodeLiteral aLiteral] : ^( DTBASE ( expression )+ ) ;
    public final ACodeLiteral dtbase() throws RecognitionException {
        ACodeLiteral aLiteral = null;

        CommonTree DTBASE109=null;
        ACodeObject expression108 = null;


        
        //--- begin exbase[@init]
        List<ACodeObject> codes = new ArrayList<ACodeObject>();
        //--- end exbase[@init]

        try {
            // AADLWalker.g:990:5: ( ^( DTBASE ( expression )+ ) )
            // AADLWalker.g:990:7: ^( DTBASE ( expression )+ )
            {
            DTBASE109=(CommonTree)input.LT(1);
            match(input,DTBASE,FOLLOW_DTBASE_in_dtbase3895); 

            match(input, Token.DOWN, null); 
            // AADLWalker.g:990:16: ( expression )+
            int cnt63=0;
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( ((LA63_0>=FCALL_RM && LA63_0<=FCALL_SM)||(LA63_0>=FCALL_IM && LA63_0<=FCALL_MM)||(LA63_0>=EXBASE && LA63_0<=ARRAY)||LA63_0==JAVA_ACTION||LA63_0==Identifier||(LA63_0>=NamedOperator && LA63_0<=CMDARG)||(LA63_0>=PLUS && LA63_0<=SLASH)||(LA63_0>=LESS && LA63_0<=GREATER)||(LA63_0>=PERCENT && LA63_0<=GREATEREQUAL)||(LA63_0>=109 && LA63_0<=111)||LA63_0==113||LA63_0==116||(LA63_0>=120 && LA63_0<=122)||LA63_0==127) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // AADLWalker.g:990:17: expression
            	    {
            	    pushFollow(FOLLOW_expression_in_dtbase3898);
            	    expression108=expression();
            	    _fsp--;

            	    codes.add(expression108);

            	    }
            	    break;

            	default :
            	    if ( cnt63 >= 1 ) break loop63;
                        EarlyExitException eee =
                            new EarlyExitException(63, input);
                        throw eee;
                }
                cnt63++;
            } while (true);


            match(input, Token.UP, null); 
            aLiteral = ACodeLiteral.buildDtBase(analyzer, DTBASE109, codes);

            }

        }
        
        catch (CompileException ex) {
        	reportError(ex);
        	recover(input,null);
        }
        catch (RecognitionException re) {
        	reportError(re);
        	recover(input,re);
        }
        finally {
        }
        return aLiteral;
    }
    // $ANTLR end dtbase


 

    public static final BitSet FOLLOW_programInfo_in_program64 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_programBody_in_program66 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AADL_INFO_in_programInfo84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_AADL_NAME_in_programInfo86 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_infoCmdArgs_in_programInfo90 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_infoFuncDefs_in_programInfo92 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AADL_PROGRAM_in_programBody119 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_packageDeclaration_in_programBody127 = new BitSet(new long[]{0x0000B40000000200L});
    public static final BitSet FOLLOW_javaHeaderAction_in_programBody139 = new BitSet(new long[]{0x0000B40000000200L});
    public static final BitSet FOLLOW_constClassVariables_in_programBody150 = new BitSet(new long[]{0x0000B00000000000L});
    public static final BitSet FOLLOW_subModule_in_programBody159 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_mainModule_in_programBody170 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_PACKAGE_in_packageDeclaration200 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_packageDeclaration202 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_PACKAGE_in_packageDeclaration220 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_javaClassPath_in_packageDeclaration222 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AADL_CONSTS_in_constClassVariables257 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constClassVariableDeclaration_in_constClassVariables259 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constVariableDeclaration_in_constClassVariableDeclaration285 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_javaAction_in_subModule318 = new BitSet(new long[]{0x0000900000000002L});
    public static final BitSet FOLLOW_function_in_subModule331 = new BitSet(new long[]{0x0000900000000002L});
    public static final BitSet FOLLOW_AADL_ARGS_in_infoCmdArgs371 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CMDARG_in_infoCmdArgs374 = new BitSet(new long[]{0x4000000000000008L});
    public static final BitSet FOLLOW_AADL_FUNCS_in_infoFuncDefs406 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_infoFuncTypes_in_infoFuncDefs409 = new BitSet(new long[]{0x0000400000000008L});
    public static final BitSet FOLLOW_functionType_in_infoFuncTypes449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROGRAM_in_mainModule488 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_mainModule490 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_mainBody_in_mainModule492 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BODY_in_mainBody526 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_mainBody529 = new BitSet(new long[]{0x7FB9500E3088CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_FUNCTION_in_function579 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_modifiers_in_function581 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_functionType_in_function583 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_functionBody_in_function585 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFIERS_in_modifiers631 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_modifiers642 = new BitSet(new long[]{0x0000000000000008L,0x6000000000000000L});
    public static final BitSet FOLLOW_Identifier_in_functionType713 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functionReturnDeclaration_in_functionType715 = new BitSet(new long[]{0x0000000000020008L});
    public static final BitSet FOLLOW_functionFormalParameters_in_functionType719 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCDEF_RETURN_in_functionReturnDeclaration752 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_functionReturnDeclaration754 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCDEF_PARAMS_in_functionFormalParameters776 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_variableDefinition_in_functionFormalParameters779 = new BitSet(new long[]{0x0000000000800008L});
    public static final BitSet FOLLOW_BODY_in_functionBody810 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_functionBody813 = new BitSet(new long[]{0x7FB9500E3088CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_BLOCK_in_block860 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block863 = new BitSet(new long[]{0x7FB9500E3088CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_involvingExpression_in_statement909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_statement920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_statement_in_statement933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_statement945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constVariableDeclaration_in_statement955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_statement_in_statement965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_break_statement_in_statement976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jump_statement_in_statement988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_statement1000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONST_in_constVariableDeclaration1024 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VARDEF_in_constVariableDeclaration1027 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_localVariableDeclarationRest_in_constVariableDeclaration1031 = new BitSet(new long[]{0x0002000003000008L});
    public static final BitSet FOLLOW_localVariableInitialization_in_constVariableDeclaration1044 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARDEF_in_localVariableDeclaration1084 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_localVariableDeclarationRest_in_localVariableDeclaration1088 = new BitSet(new long[]{0x0002000003000008L});
    public static final BitSet FOLLOW_localVariableInitialization_in_localVariableDeclaration1101 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Identifier_in_localVariableDeclarationRest1140 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_type_in_localVariableDeclarationRest1142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARINIT_in_localVariableInitialization1178 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_localVariableInitialization1182 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARINIT_in_localVariableInitialization1198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_involvingExpression_in_localVariableInitialization1200 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINPUT_in_localVariableInitialization1217 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fileDeclarator_in_localVariableInitialization1221 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARNEW_in_localVariableInitialization1236 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_localVariableInitialization1241 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_Identifier_in_assign_statement1286 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_assign_rest_in_assign_statement1295 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_assign_rest1336 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_assign_rest1338 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_assign_rest1354 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_involvingExpression_in_assign_rest1356 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINPUT_in_assign_rest1373 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fileDeclarator_in_assign_rest1377 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FOUTPUT_in_assign_rest1391 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fileDeclarator_in_assign_rest1395 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BREAK_in_break_statement1423 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_break_statement1425 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RETURN_in_jump_statement1453 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_jump_statement1455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SIF_in_if_statement1486 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_if_statement1490 = new BitSet(new long[]{0x7FB9500E3088CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_ifelse_statement_rest_in_if_statement1494 = new BitSet(new long[]{0x0040000000000008L});
    public static final BitSet FOLLOW_else_statement_in_if_statement1498 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SELSE_in_else_statement1529 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ifelse_statement_rest_in_else_statement1533 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_statement_in_ifelse_statement_rest1566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INVOLV_in_involvingExpression1604 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INVOLV_COND_in_involvingExpression1607 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterExpression_in_involvingExpression1612 = new BitSet(new long[]{0x7F80500F0C00CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_involvingExpression1620 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VOIDINVOLV_in_involvingExpression1637 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INVOLV_COND_in_involvingExpression1640 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_filterExpression_in_involvingExpression1645 = new BitSet(new long[]{0x7F80500F0C00CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_involvingExpression1653 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iterateExpression_in_filterExpression1690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aliasExpression_in_filterExpression1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_filterExpression1711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_involvingBlock_in_filterExpression1722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INVOLV_BLOCK_in_involvingBlock1755 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_involvingBlock1758 = new BitSet(new long[]{0x7FB9500E3088CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_ITERATE_in_iterateExpression1791 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_iterateExpression1795 = new BitSet(new long[]{0x000040083000CC00L,0x00F2000000000000L});
    public static final BitSet FOLLOW_iterateExpressionRest_in_iterateExpression1799 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_fileDeclarator_in_iterateExpressionRest1837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_iterateExpressionRest1851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayLiteral_in_iterateExpressionRest1865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodCall_in_iterateExpressionRest1880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_involvingExpression_in_iterateExpressionRest1895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALIAS_in_aliasExpression1921 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_aliasExpression1923 = new BitSet(new long[]{0x7F80500E0040CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_type_in_aliasExpression1927 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_aliasExpression1930 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_newExalgeExpression_in_expression1961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndOrExpression_in_expression1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression1986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_namedOperationExpression_in_expression1998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mathOperationExpression_in_expression2010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_plusExpression_in_expression2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_minusExpression_in_expression2035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleExpression_in_expression2048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_specialExpression_in_expression2061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpression_in_expression2074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_127_in_specialExpression2101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_specialExpression2105 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_specialExpression2109 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_specialExpression2113 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_conditionalAndOrExpression2145 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalAndOrExpression2153 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_conditionalAndOrExpression2157 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUAL_in_conditionalExpression2189 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2193 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2197 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOTEQUAL_in_conditionalExpression2217 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2221 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2225 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_conditionalExpression2245 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2257 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2261 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NamedOperator_in_namedOperationExpression2292 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_namedOperationExpression2296 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_namedOperationExpression2300 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_111_in_newExalgeExpression2332 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_newExalgeExpression2336 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_newExalgeExpression2340 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_mathOperationExpression2372 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_mathOperationExpression2376 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_mathOperationExpression2380 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SLASH_in_mathOperationExpression2400 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_mathOperationExpression2404 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_mathOperationExpression2408 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PERCENT_in_mathOperationExpression2428 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_mathOperationExpression2432 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_mathOperationExpression2436 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_plusExpression2469 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_plusExpression2473 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_plusExpressionRest_in_plusExpression2477 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_plusExpressionRest2508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_minusExpression2545 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_minusExpression2549 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_minusExpressionRest_in_minusExpression2553 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_minusExpressionRest2584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleExpression2620 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_simpleExpression2624 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BAR_in_simpleExpression2638 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_simpleExpression2642 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_HAT_in_simpleExpression2656 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_simpleExpression2660 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_primary_in_primaryExpression2684 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_selector_in_primaryExpression2694 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_arrayLiteral_in_primary2722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HexLiteral_in_primary2734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_primary2745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IntegerLiteral_in_primary2757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_primary2768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullLiteral_in_primary2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_primary2789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_primary2801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_primary2812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exbase_in_primary2823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dtbase_in_primary2835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodCall_in_primary2847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_javaAction_in_primary2858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary2869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CMDARG_in_primary2880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_selector2911 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_selector2913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_selector2931 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FCALL_RM_in_selector2934 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_selector2936 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_FUNCALL_ARGS_in_selector2939 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_selector2942 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_callSystemMethod_in_methodCall2979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callRegisteredMethod_in_methodCall2992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callSpecialMethod_in_methodCall3004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callModuleMethod_in_methodCall3016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callInstanceMethod_in_methodCall3028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_callSystemMethod3059 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_callSystemMethod3061 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_callSystemMethod3063 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_116_in_callSystemMethod3082 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_callSystemMethod3084 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_callSystemMethod3086 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FCALL_RM_in_callRegisteredMethod3124 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_callRegisteredMethod3126 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_FUNCALL_ARGS_in_callRegisteredMethod3129 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_callRegisteredMethod3132 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_FCALL_SM_in_callSpecialMethod3170 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_callSpecialMethod3172 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_FUNCALL_PATH_in_callSpecialMethod3181 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_callSpecialMethod3185 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCALL_ARGS_in_callSpecialMethod3195 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_callSpecialMethod3200 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_FCALL_MM_in_callModuleMethod3238 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FUNCALL_PATH_in_callModuleMethod3241 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_callModuleMethodPath_in_callModuleMethod3245 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Identifier_in_callModuleMethod3248 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_FUNCALL_ARGS_in_callModuleMethod3251 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_callModuleMethod3256 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_Identifier_in_callModuleMethodPath3289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_javaClassPath_in_callModuleMethodPath3299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FCALL_IM_in_callInstanceMethod3327 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FUNCALL_PATH_in_callInstanceMethod3330 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_callInstanceMethod3334 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_Identifier_in_callInstanceMethod3337 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_FUNCALL_ARGS_in_callInstanceMethod3340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_callInstanceMethod3345 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_JAVA_HEADER_ACTION_in_javaHeaderAction3378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_JAVA_ACTION_in_javaAction3406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_117_in_fileDeclarator3446 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fileDeclaratorOptionEntry_in_fileDeclarator3453 = new BitSet(new long[]{0x7F80504E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_fileDeclarator3460 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_fileDeclarator3467 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_118_in_fileDeclarator3482 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fileDeclaratorOptionEntry_in_fileDeclarator3489 = new BitSet(new long[]{0x7F80504E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_fileDeclarator3496 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_fileDeclarator3503 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_119_in_fileDeclarator3518 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_fileDeclaratorOptionEntry_in_fileDeclarator3525 = new BitSet(new long[]{0x7F80504E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_fileDeclarator3532 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FOPTION_in_fileDeclaratorOptionEntry3559 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_fileDeclaratorOptionEntry3561 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_fileDeclaratorOptionEntry3563 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARDEF_in_variableDefinition3595 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_variableDefinition3597 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_type_in_variableDefinition3599 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARTYPE_in_type3629 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_type3631 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARTYPE_in_type3644 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_javaClassPath_in_type3646 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_JAVACLASSPATH_in_javaClassPath3675 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_Identifier_in_javaClassPath3678 = new BitSet(new long[]{0x0000400000000008L});
    public static final BitSet FOLLOW_ARRAY_in_arrayLiteral3714 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arrayLiteral3719 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_ARRAY_in_arrayLiteral3740 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_HASH_in_arrayLiteral3744 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arrayLiteral3748 = new BitSet(new long[]{0x7F80500E0000CC00L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_expression_in_arrayLiteral3753 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_120_in_booleanLiteral3789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_121_in_booleanLiteral3801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_122_in_nullLiteral3826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXBASE_in_exbase3854 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_exbase3857 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});
    public static final BitSet FOLLOW_DTBASE_in_dtbase3895 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_dtbase3898 = new BitSet(new long[]{0x7F80500E0000CC08L,0x8712E000000FF63CL});

}