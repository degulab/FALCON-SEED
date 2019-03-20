// $ANTLR 3.0.1 AADL.g 2011-05-25 18:40:42

package ssac.aadlc.core;

import java.util.ArrayList;
import java.util.Iterator;

import ssac.aadlc.io.ReportPrinter;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

/**
 * AADL(Algebraic Accounting Description Language) Grammar
 *
 * AADL Parser prototype.
 *
 * @version 1.70 2011/05/25
 **/
public class AADLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AADL_INFO", "AADL_NAME", "AADL_ARGS", "AADL_FUNCS", "AADL_PROGRAM", "AADL_CONSTS", "FCALL_RM", "FCALL_SM", "FUNCALL_PATH", "FUNCALL_ARGS", "FCALL_IM", "FCALL_MM", "FUNCDEF_RETURN", "FUNCDEF_PARAMS", "IFCOND", "BLOCK", "BODY", "JAVACLASSPATH", "VARTYPE", "VARDEF", "VARINIT", "VARNEW", "ITERATE", "ALIAS", "INVOLV", "VOIDINVOLV", "INVOLV_EXP", "INVOLV_COND", "INVOLV_BLOCK", "EXBASE", "DTBASE", "ARRAY", "HASH", "AINDEX", "FOPTION", "CAST", "INSTANCEOF", "MODIFIERS", "JAVA_HEADER_ACTION", "ID_PACKAGE", "JAVA_ACTION", "PROGRAM", "Identifier", "FUNCTION", "CONST", "FINPUT", "FOUTPUT", "BREAK", "RETURN", "SIF", "SELSE", "NamedOperator", "HexLiteral", "OctalLiteral", "IntegerLiteral", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "CMDARG", "Letter", "Digit", "COLON", "PLUS", "MINUS", "STAR", "SLASH", "SHARP", "VBAR", "AMPER", "LESS", "GREATER", "ASSIGN", "PERCENT", "NOT", "HAT", "BAR", "EQUAL", "NOTEQUAL", "LESSEQUAL", "GREATEREQUAL", "DOUBLESTAR", "ITERATOR", "SCOPE", "InvalidBaseKeyLetter", "BaseKeyLetters", "WHITESPACE", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "ID", "UnicodeEscape", "OctalEscape", "WS", "COMMENT", "LINE_COMMENT", "NEWLINE", "';'", "'{'", "'}'", "'('", "')'", "','", "'var'", "'||'", "'&&'", "'@'", "'.'", "'cast'", "'['", "']'", "'typeof'", "'txtFile'", "'csvFile'", "'xmlFile'", "'true'", "'false'", "'null'", "'<<'", "'>>'", "'public'"
    };
    public static final int CAST=39;
    public static final int FUNCTION=47;
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
    public static final int FUNCDEF_RETURN=16;
    public static final int VBAR=71;
    public static final int AADL_INFO=4;
    public static final int GREATER=74;
    public static final int FUNCALL_PATH=12;
    public static final int IFCOND=18;
    public static final int FOUTPUT=50;
    public static final int RETURN=52;
    public static final int SIF=53;
    public static final int LESS=73;
    public static final int BODY=20;
    public static final int COMMENT=99;
    public static final int VARDEF=23;
    public static final int AADL_NAME=5;
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
    public static final int BAR=79;
    public static final int BaseKeyLetters=88;

        public AADLParser(TokenStream input) {
            super(input);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "AADL.g"; }

    
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


    public static class test_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start test
    // AADL.g:336:1: test : program ;
    public final test_return test() throws RecognitionException {
        test_return retval = new test_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        program_return program1 = null;



        try {
            // AADL.g:337:5: ( program )
            // AADL.g:337:7: program
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_program_in_test209);
            program1=program();
            _fsp--;

            adaptor.addChild(root_0, program1.getTree());
            if (((CommonTree)program1.tree) != null) System.out.println(((CommonTree)program1.tree).toStringTree());

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end test

    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // AADL.g:342:1: program : ( packageDeclaration )? ( JAVA_HEADER_ACTION )* (sm1= subModule )? mainModule (sm2= subModule )? EOF -> ^( AADL_INFO[\"AADL_INFO\"] AADL_NAME[$mainModule.start, getClassName()] ) ^( AADL_PROGRAM[$mainModule.start, getClassName()] ( packageDeclaration )? ( JAVA_HEADER_ACTION )* ( $sm1)? ( $sm2)? mainModule ) ;
    public final program_return program() throws RecognitionException {
        program_return retval = new program_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token JAVA_HEADER_ACTION3=null;
        Token EOF5=null;
        subModule_return sm1 = null;

        subModule_return sm2 = null;

        packageDeclaration_return packageDeclaration2 = null;

        mainModule_return mainModule4 = null;


        CommonTree JAVA_HEADER_ACTION3_tree=null;
        CommonTree EOF5_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleTokenStream stream_JAVA_HEADER_ACTION=new RewriteRuleTokenStream(adaptor,"token JAVA_HEADER_ACTION");
        RewriteRuleSubtreeStream stream_packageDeclaration=new RewriteRuleSubtreeStream(adaptor,"rule packageDeclaration");
        RewriteRuleSubtreeStream stream_subModule=new RewriteRuleSubtreeStream(adaptor,"rule subModule");
        RewriteRuleSubtreeStream stream_mainModule=new RewriteRuleSubtreeStream(adaptor,"rule mainModule");
        try {
            // AADL.g:343:5: ( ( packageDeclaration )? ( JAVA_HEADER_ACTION )* (sm1= subModule )? mainModule (sm2= subModule )? EOF -> ^( AADL_INFO[\"AADL_INFO\"] AADL_NAME[$mainModule.start, getClassName()] ) ^( AADL_PROGRAM[$mainModule.start, getClassName()] ( packageDeclaration )? ( JAVA_HEADER_ACTION )* ( $sm1)? ( $sm2)? mainModule ) )
            // AADL.g:343:7: ( packageDeclaration )? ( JAVA_HEADER_ACTION )* (sm1= subModule )? mainModule (sm2= subModule )? EOF
            {
            // AADL.g:343:7: ( packageDeclaration )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID_PACKAGE) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // AADL.g:343:7: packageDeclaration
                    {
                    pushFollow(FOLLOW_packageDeclaration_in_program234);
                    packageDeclaration2=packageDeclaration();
                    _fsp--;

                    stream_packageDeclaration.add(packageDeclaration2.getTree());

                    }
                    break;

            }

            // AADL.g:344:6: ( JAVA_HEADER_ACTION )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==JAVA_HEADER_ACTION) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // AADL.g:344:6: JAVA_HEADER_ACTION
            	    {
            	    JAVA_HEADER_ACTION3=(Token)input.LT(1);
            	    match(input,JAVA_HEADER_ACTION,FOLLOW_JAVA_HEADER_ACTION_in_program242); 
            	    stream_JAVA_HEADER_ACTION.add(JAVA_HEADER_ACTION3);


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // AADL.g:345:6: (sm1= subModule )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==JAVA_ACTION||(LA3_0>=FUNCTION && LA3_0<=CONST)||LA3_0==125) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // AADL.g:345:7: sm1= subModule
                    {
                    pushFollow(FOLLOW_subModule_in_program253);
                    sm1=subModule();
                    _fsp--;

                    stream_subModule.add(sm1.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_mainModule_in_program262);
            mainModule4=mainModule();
            _fsp--;

            stream_mainModule.add(mainModule4.getTree());
            // AADL.g:347:6: (sm2= subModule )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==JAVA_ACTION||(LA4_0>=FUNCTION && LA4_0<=CONST)||LA4_0==125) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // AADL.g:347:7: sm2= subModule
                    {
                    pushFollow(FOLLOW_subModule_in_program272);
                    sm2=subModule();
                    _fsp--;

                    stream_subModule.add(sm2.getTree());

                    }
                    break;

            }

            EOF5=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_program281); 
            stream_EOF.add(EOF5);


            // AST REWRITE
            // elements: sm2, JAVA_HEADER_ACTION, mainModule, packageDeclaration, sm1
            // token labels: 
            // rule labels: retval, sm1, sm2
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_sm1=new RewriteRuleSubtreeStream(adaptor,"token sm1",sm1!=null?sm1.tree:null);
            RewriteRuleSubtreeStream stream_sm2=new RewriteRuleSubtreeStream(adaptor,"token sm2",sm2!=null?sm2.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 349:7: -> ^( AADL_INFO[\"AADL_INFO\"] AADL_NAME[$mainModule.start, getClassName()] ) ^( AADL_PROGRAM[$mainModule.start, getClassName()] ( packageDeclaration )? ( JAVA_HEADER_ACTION )* ( $sm1)? ( $sm2)? mainModule )
            {
                // AADL.g:349:10: ^( AADL_INFO[\"AADL_INFO\"] AADL_NAME[$mainModule.start, getClassName()] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(AADL_INFO, "AADL_INFO"), root_1);

                adaptor.addChild(root_1, adaptor.create(AADL_NAME, ((Token)mainModule4.start),  getClassName()));
                adaptor.addChild(root_1, getTreeCommandArgs());
                adaptor.addChild(root_1, getTreeFuncDefs());

                adaptor.addChild(root_0, root_1);
                }
                // AADL.g:350:10: ^( AADL_PROGRAM[$mainModule.start, getClassName()] ( packageDeclaration )? ( JAVA_HEADER_ACTION )* ( $sm1)? ( $sm2)? mainModule )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(AADL_PROGRAM, ((Token)mainModule4.start),  getClassName()), root_1);

                // AADL.g:350:60: ( packageDeclaration )?
                if ( stream_packageDeclaration.hasNext() ) {
                    adaptor.addChild(root_1, stream_packageDeclaration.next());

                }
                stream_packageDeclaration.reset();
                // AADL.g:350:80: ( JAVA_HEADER_ACTION )*
                while ( stream_JAVA_HEADER_ACTION.hasNext() ) {
                    adaptor.addChild(root_1, stream_JAVA_HEADER_ACTION.next());

                }
                stream_JAVA_HEADER_ACTION.reset();
                adaptor.addChild(root_1, getTreeConstDefs());
                // AADL.g:350:121: ( $sm1)?
                if ( stream_sm1.hasNext() ) {
                    adaptor.addChild(root_1, stream_sm1.next());

                }
                stream_sm1.reset();
                // AADL.g:350:127: ( $sm2)?
                if ( stream_sm2.hasNext() ) {
                    adaptor.addChild(root_1, stream_sm2.next());

                }
                stream_sm2.reset();
                adaptor.addChild(root_1, stream_mainModule.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end program

    public static class packageDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start packageDeclaration
    // AADL.g:353:1: packageDeclaration : ID_PACKAGE variableTypeName ';' -> ^( ID_PACKAGE variableTypeName ) ;
    public final packageDeclaration_return packageDeclaration() throws RecognitionException {
        packageDeclaration_return retval = new packageDeclaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID_PACKAGE6=null;
        Token char_literal8=null;
        variableTypeName_return variableTypeName7 = null;


        CommonTree ID_PACKAGE6_tree=null;
        CommonTree char_literal8_tree=null;
        RewriteRuleTokenStream stream_ID_PACKAGE=new RewriteRuleTokenStream(adaptor,"token ID_PACKAGE");
        RewriteRuleTokenStream stream_102=new RewriteRuleTokenStream(adaptor,"token 102");
        RewriteRuleSubtreeStream stream_variableTypeName=new RewriteRuleSubtreeStream(adaptor,"rule variableTypeName");
        try {
            // AADL.g:354:5: ( ID_PACKAGE variableTypeName ';' -> ^( ID_PACKAGE variableTypeName ) )
            // AADL.g:354:7: ID_PACKAGE variableTypeName ';'
            {
            ID_PACKAGE6=(Token)input.LT(1);
            match(input,ID_PACKAGE,FOLLOW_ID_PACKAGE_in_packageDeclaration350); 
            stream_ID_PACKAGE.add(ID_PACKAGE6);

            pushFollow(FOLLOW_variableTypeName_in_packageDeclaration352);
            variableTypeName7=variableTypeName();
            _fsp--;

            stream_variableTypeName.add(variableTypeName7.getTree());
            char_literal8=(Token)input.LT(1);
            match(input,102,FOLLOW_102_in_packageDeclaration354); 
            stream_102.add(char_literal8);


            // AST REWRITE
            // elements: ID_PACKAGE, variableTypeName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 354:39: -> ^( ID_PACKAGE variableTypeName )
            {
                // AADL.g:354:42: ^( ID_PACKAGE variableTypeName )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_ID_PACKAGE.next(), root_1);

                adaptor.addChild(root_1, stream_variableTypeName.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end packageDeclaration

    public static class constDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constDeclaration
    // AADL.g:357:1: constDeclaration : constVariableDeclaration ';' ;
    public final constDeclaration_return constDeclaration() throws RecognitionException {
        constDeclaration_return retval = new constDeclaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal10=null;
        constVariableDeclaration_return constVariableDeclaration9 = null;


        CommonTree char_literal10_tree=null;

        try {
            // AADL.g:358:5: ( constVariableDeclaration ';' )
            // AADL.g:358:7: constVariableDeclaration ';'
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_constVariableDeclaration_in_constDeclaration379);
            constVariableDeclaration9=constVariableDeclaration();
            _fsp--;

            adaptor.addChild(root_0, constVariableDeclaration9.getTree());
            char_literal10=(Token)input.LT(1);
            match(input,102,FOLLOW_102_in_constDeclaration381); 
            addConstDef(((CommonTree)constVariableDeclaration9.tree));

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end constDeclaration

    public static class subModule_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start subModule
    // AADL.g:361:1: subModule : ( JAVA_ACTION | function | constDeclaration )+ ;
    public final subModule_return subModule() throws RecognitionException {
        subModule_return retval = new subModule_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token JAVA_ACTION11=null;
        function_return function12 = null;

        constDeclaration_return constDeclaration13 = null;


        CommonTree JAVA_ACTION11_tree=null;

        try {
            // AADL.g:362:5: ( ( JAVA_ACTION | function | constDeclaration )+ )
            // AADL.g:362:7: ( JAVA_ACTION | function | constDeclaration )+
            {
            root_0 = (CommonTree)adaptor.nil();

            // AADL.g:362:7: ( JAVA_ACTION | function | constDeclaration )+
            int cnt5=0;
            loop5:
            do {
                int alt5=4;
                switch ( input.LA(1) ) {
                case JAVA_ACTION:
                    {
                    alt5=1;
                    }
                    break;
                case FUNCTION:
                case 125:
                    {
                    alt5=2;
                    }
                    break;
                case CONST:
                    {
                    alt5=3;
                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // AADL.g:362:8: JAVA_ACTION
            	    {
            	    JAVA_ACTION11=(Token)input.LT(1);
            	    match(input,JAVA_ACTION,FOLLOW_JAVA_ACTION_in_subModule402); 
            	    JAVA_ACTION11_tree = (CommonTree)adaptor.create(JAVA_ACTION11);
            	    adaptor.addChild(root_0, JAVA_ACTION11_tree);


            	    }
            	    break;
            	case 2 :
            	    // AADL.g:362:20: function
            	    {
            	    pushFollow(FOLLOW_function_in_subModule404);
            	    function12=function();
            	    _fsp--;

            	    adaptor.addChild(root_0, function12.getTree());

            	    }
            	    break;
            	case 3 :
            	    // AADL.g:362:29: constDeclaration
            	    {
            	    pushFollow(FOLLOW_constDeclaration_in_subModule406);
            	    constDeclaration13=constDeclaration();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end subModule

    public static class mainModule_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start mainModule
    // AADL.g:367:1: mainModule : PROGRAM Identifier mainBody -> ^( PROGRAM Identifier mainBody ) ;
    public final mainModule_return mainModule() throws RecognitionException {
        mainModule_return retval = new mainModule_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token PROGRAM14=null;
        Token Identifier15=null;
        mainBody_return mainBody16 = null;


        CommonTree PROGRAM14_tree=null;
        CommonTree Identifier15_tree=null;
        RewriteRuleTokenStream stream_PROGRAM=new RewriteRuleTokenStream(adaptor,"token PROGRAM");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_mainBody=new RewriteRuleSubtreeStream(adaptor,"rule mainBody");
        try {
            // AADL.g:368:5: ( PROGRAM Identifier mainBody -> ^( PROGRAM Identifier mainBody ) )
            // AADL.g:368:7: PROGRAM Identifier mainBody
            {
            PROGRAM14=(Token)input.LT(1);
            match(input,PROGRAM,FOLLOW_PROGRAM_in_mainModule428); 
            stream_PROGRAM.add(PROGRAM14);

            Identifier15=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_mainModule430); 
            stream_Identifier.add(Identifier15);

             setClassName(Identifier15.getText()); 
            pushFollow(FOLLOW_mainBody_in_mainModule434);
            mainBody16=mainBody();
            _fsp--;

            stream_mainBody.add(mainBody16.getTree());

            // AST REWRITE
            // elements: PROGRAM, Identifier, mainBody
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 369:7: -> ^( PROGRAM Identifier mainBody )
            {
                // AADL.g:369:10: ^( PROGRAM Identifier mainBody )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_PROGRAM.next(), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                adaptor.addChild(root_1, stream_mainBody.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end mainModule

    public static class mainBody_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start mainBody
    // AADL.g:372:1: mainBody : lc= '{' ( statement )* '}' -> ^( BODY[$lc, \"BODY\"] ( statement )* ) ;
    public final mainBody_return mainBody() throws RecognitionException {
        mainBody_return retval = new mainBody_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token lc=null;
        Token char_literal18=null;
        statement_return statement17 = null;


        CommonTree lc_tree=null;
        CommonTree char_literal18_tree=null;
        RewriteRuleTokenStream stream_104=new RewriteRuleTokenStream(adaptor,"token 104");
        RewriteRuleTokenStream stream_103=new RewriteRuleTokenStream(adaptor,"token 103");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // AADL.g:373:5: (lc= '{' ( statement )* '}' -> ^( BODY[$lc, \"BODY\"] ( statement )* ) )
            // AADL.g:373:7: lc= '{' ( statement )* '}'
            {
            lc=(Token)input.LT(1);
            match(input,103,FOLLOW_103_in_mainBody469); 
            stream_103.add(lc);

            // AADL.g:373:14: ( statement )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==JAVA_ACTION||LA6_0==Identifier||LA6_0==CONST||(LA6_0>=BREAK && LA6_0<=SIF)||(LA6_0>=HexLiteral && LA6_0<=CMDARG)||(LA6_0>=PLUS && LA6_0<=MINUS)||LA6_0==LESS||(LA6_0>=NOT && LA6_0<=BAR)||(LA6_0>=102 && LA6_0<=103)||LA6_0==105||LA6_0==108||(LA6_0>=113 && LA6_0<=114)||LA6_0==116||(LA6_0>=120 && LA6_0<=123)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // AADL.g:373:14: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_mainBody471);
            	    statement17=statement();
            	    _fsp--;

            	    stream_statement.add(statement17.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            char_literal18=(Token)input.LT(1);
            match(input,104,FOLLOW_104_in_mainBody474); 
            stream_104.add(char_literal18);


            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 373:29: -> ^( BODY[$lc, \"BODY\"] ( statement )* )
            {
                // AADL.g:373:32: ^( BODY[$lc, \"BODY\"] ( statement )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(BODY, lc,  "BODY"), root_1);

                // AADL.g:373:52: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.next());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end mainBody

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function
    // AADL.g:381:1: function : ( modifier )* FUNCTION functionDeclaration functionBody -> ^( FUNCTION ^( MODIFIERS ( modifier )* ) functionDeclaration functionBody ) ;
    public final function_return function() throws RecognitionException {
        function_return retval = new function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token FUNCTION20=null;
        modifier_return modifier19 = null;

        functionDeclaration_return functionDeclaration21 = null;

        functionBody_return functionBody22 = null;


        CommonTree FUNCTION20_tree=null;
        RewriteRuleTokenStream stream_FUNCTION=new RewriteRuleTokenStream(adaptor,"token FUNCTION");
        RewriteRuleSubtreeStream stream_modifier=new RewriteRuleSubtreeStream(adaptor,"rule modifier");
        RewriteRuleSubtreeStream stream_functionBody=new RewriteRuleSubtreeStream(adaptor,"rule functionBody");
        RewriteRuleSubtreeStream stream_functionDeclaration=new RewriteRuleSubtreeStream(adaptor,"rule functionDeclaration");
        try {
            // AADL.g:382:5: ( ( modifier )* FUNCTION functionDeclaration functionBody -> ^( FUNCTION ^( MODIFIERS ( modifier )* ) functionDeclaration functionBody ) )
            // AADL.g:382:7: ( modifier )* FUNCTION functionDeclaration functionBody
            {
            // AADL.g:382:7: ( modifier )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==125) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // AADL.g:382:7: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_function506);
            	    modifier19=modifier();
            	    _fsp--;

            	    stream_modifier.add(modifier19.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            FUNCTION20=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function509); 
            stream_FUNCTION.add(FUNCTION20);

            pushFollow(FOLLOW_functionDeclaration_in_function511);
            functionDeclaration21=functionDeclaration();
            _fsp--;

            stream_functionDeclaration.add(functionDeclaration21.getTree());
            pushFollow(FOLLOW_functionBody_in_function513);
            functionBody22=functionBody();
            _fsp--;

            stream_functionBody.add(functionBody22.getTree());
            addFuncDef(((CommonTree)functionDeclaration21.tree));

            // AST REWRITE
            // elements: FUNCTION, modifier, functionDeclaration, functionBody
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 384:7: -> ^( FUNCTION ^( MODIFIERS ( modifier )* ) functionDeclaration functionBody )
            {
                // AADL.g:384:10: ^( FUNCTION ^( MODIFIERS ( modifier )* ) functionDeclaration functionBody )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_FUNCTION.next(), root_1);

                // AADL.g:384:21: ^( MODIFIERS ( modifier )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(MODIFIERS, "MODIFIERS"), root_2);

                // AADL.g:384:33: ( modifier )*
                while ( stream_modifier.hasNext() ) {
                    adaptor.addChild(root_2, stream_modifier.next());

                }
                stream_modifier.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_functionDeclaration.next());
                adaptor.addChild(root_1, stream_functionBody.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function

    public static class functionDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start functionDeclaration
    // AADL.g:395:1: functionDeclaration : Identifier '(' ( functionFormalParameters )? ')' ( functionReturnDeclaration )? -> ^( Identifier ( functionReturnDeclaration )? ( functionFormalParameters )? ) ;
    public final functionDeclaration_return functionDeclaration() throws RecognitionException {
        functionDeclaration_return retval = new functionDeclaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier23=null;
        Token char_literal24=null;
        Token char_literal26=null;
        functionFormalParameters_return functionFormalParameters25 = null;

        functionReturnDeclaration_return functionReturnDeclaration27 = null;


        CommonTree Identifier23_tree=null;
        CommonTree char_literal24_tree=null;
        CommonTree char_literal26_tree=null;
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_functionReturnDeclaration=new RewriteRuleSubtreeStream(adaptor,"rule functionReturnDeclaration");
        RewriteRuleSubtreeStream stream_functionFormalParameters=new RewriteRuleSubtreeStream(adaptor,"rule functionFormalParameters");
        try {
            // AADL.g:396:5: ( Identifier '(' ( functionFormalParameters )? ')' ( functionReturnDeclaration )? -> ^( Identifier ( functionReturnDeclaration )? ( functionFormalParameters )? ) )
            // AADL.g:396:7: Identifier '(' ( functionFormalParameters )? ')' ( functionReturnDeclaration )?
            {
            Identifier23=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_functionDeclaration563); 
            stream_Identifier.add(Identifier23);

            char_literal24=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_functionDeclaration566); 
            stream_105.add(char_literal24);

            // AADL.g:396:23: ( functionFormalParameters )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==Identifier) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // AADL.g:396:23: functionFormalParameters
                    {
                    pushFollow(FOLLOW_functionFormalParameters_in_functionDeclaration568);
                    functionFormalParameters25=functionFormalParameters();
                    _fsp--;

                    stream_functionFormalParameters.add(functionFormalParameters25.getTree());

                    }
                    break;

            }

            char_literal26=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_functionDeclaration571); 
            stream_106.add(char_literal26);

            // AADL.g:396:53: ( functionReturnDeclaration )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==COLON) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // AADL.g:396:53: functionReturnDeclaration
                    {
                    pushFollow(FOLLOW_functionReturnDeclaration_in_functionDeclaration573);
                    functionReturnDeclaration27=functionReturnDeclaration();
                    _fsp--;

                    stream_functionReturnDeclaration.add(functionReturnDeclaration27.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: functionReturnDeclaration, Identifier, functionFormalParameters
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 397:7: -> ^( Identifier ( functionReturnDeclaration )? ( functionFormalParameters )? )
            {
                // AADL.g:397:10: ^( Identifier ( functionReturnDeclaration )? ( functionFormalParameters )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_Identifier.next(), root_1);

                // AADL.g:397:23: ( functionReturnDeclaration )?
                if ( stream_functionReturnDeclaration.hasNext() ) {
                    adaptor.addChild(root_1, stream_functionReturnDeclaration.next());

                }
                stream_functionReturnDeclaration.reset();
                // AADL.g:397:50: ( functionFormalParameters )?
                if ( stream_functionFormalParameters.hasNext() ) {
                    adaptor.addChild(root_1, stream_functionFormalParameters.next());

                }
                stream_functionFormalParameters.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end functionDeclaration

    public static class functionReturnDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start functionReturnDeclaration
    // AADL.g:400:1: functionReturnDeclaration : fr= ':' type -> ^( FUNCDEF_RETURN[$fr, \"FUNCDEF_RETURN\"] type ) ;
    public final functionReturnDeclaration_return functionReturnDeclaration() throws RecognitionException {
        functionReturnDeclaration_return retval = new functionReturnDeclaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token fr=null;
        type_return type28 = null;


        CommonTree fr_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            // AADL.g:401:5: (fr= ':' type -> ^( FUNCDEF_RETURN[$fr, \"FUNCDEF_RETURN\"] type ) )
            // AADL.g:401:7: fr= ':' type
            {
            fr=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_functionReturnDeclaration611); 
            stream_COLON.add(fr);

            pushFollow(FOLLOW_type_in_functionReturnDeclaration613);
            type28=type();
            _fsp--;

            stream_type.add(type28.getTree());

            // AST REWRITE
            // elements: type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 402:7: -> ^( FUNCDEF_RETURN[$fr, \"FUNCDEF_RETURN\"] type )
            {
                // AADL.g:402:10: ^( FUNCDEF_RETURN[$fr, \"FUNCDEF_RETURN\"] type )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCDEF_RETURN, fr,  "FUNCDEF_RETURN"), root_1);

                adaptor.addChild(root_1, stream_type.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end functionReturnDeclaration

    public static class functionFormalParameters_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start functionFormalParameters
    // AADL.g:405:1: functionFormalParameters : variableDefinition ( ',' variableDefinition )* -> ^( FUNCDEF_PARAMS ( variableDefinition )+ ) ;
    public final functionFormalParameters_return functionFormalParameters() throws RecognitionException {
        functionFormalParameters_return retval = new functionFormalParameters_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal30=null;
        variableDefinition_return variableDefinition29 = null;

        variableDefinition_return variableDefinition31 = null;


        CommonTree char_literal30_tree=null;
        RewriteRuleTokenStream stream_107=new RewriteRuleTokenStream(adaptor,"token 107");
        RewriteRuleSubtreeStream stream_variableDefinition=new RewriteRuleSubtreeStream(adaptor,"rule variableDefinition");
        try {
            // AADL.g:406:5: ( variableDefinition ( ',' variableDefinition )* -> ^( FUNCDEF_PARAMS ( variableDefinition )+ ) )
            // AADL.g:406:7: variableDefinition ( ',' variableDefinition )*
            {
            pushFollow(FOLLOW_variableDefinition_in_functionFormalParameters645);
            variableDefinition29=variableDefinition();
            _fsp--;

            stream_variableDefinition.add(variableDefinition29.getTree());
            // AADL.g:406:26: ( ',' variableDefinition )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==107) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // AADL.g:406:27: ',' variableDefinition
            	    {
            	    char_literal30=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_functionFormalParameters648); 
            	    stream_107.add(char_literal30);

            	    pushFollow(FOLLOW_variableDefinition_in_functionFormalParameters650);
            	    variableDefinition31=variableDefinition();
            	    _fsp--;

            	    stream_variableDefinition.add(variableDefinition31.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            // AST REWRITE
            // elements: variableDefinition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 407:7: -> ^( FUNCDEF_PARAMS ( variableDefinition )+ )
            {
                // AADL.g:407:10: ^( FUNCDEF_PARAMS ( variableDefinition )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCDEF_PARAMS, "FUNCDEF_PARAMS"), root_1);

                if ( !(stream_variableDefinition.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_variableDefinition.hasNext() ) {
                    adaptor.addChild(root_1, stream_variableDefinition.next());

                }
                stream_variableDefinition.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end functionFormalParameters

    public static class functionBody_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start functionBody
    // AADL.g:410:1: functionBody : lc= '{' ( statement )* '}' -> ^( BODY[$lc, \"BODY\"] ( statement )* ) ;
    public final functionBody_return functionBody() throws RecognitionException {
        functionBody_return retval = new functionBody_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token lc=null;
        Token char_literal33=null;
        statement_return statement32 = null;


        CommonTree lc_tree=null;
        CommonTree char_literal33_tree=null;
        RewriteRuleTokenStream stream_104=new RewriteRuleTokenStream(adaptor,"token 104");
        RewriteRuleTokenStream stream_103=new RewriteRuleTokenStream(adaptor,"token 103");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // AADL.g:411:5: (lc= '{' ( statement )* '}' -> ^( BODY[$lc, \"BODY\"] ( statement )* ) )
            // AADL.g:411:7: lc= '{' ( statement )* '}'
            {
            lc=(Token)input.LT(1);
            match(input,103,FOLLOW_103_in_functionBody686); 
            stream_103.add(lc);

            // AADL.g:411:14: ( statement )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==JAVA_ACTION||LA11_0==Identifier||LA11_0==CONST||(LA11_0>=BREAK && LA11_0<=SIF)||(LA11_0>=HexLiteral && LA11_0<=CMDARG)||(LA11_0>=PLUS && LA11_0<=MINUS)||LA11_0==LESS||(LA11_0>=NOT && LA11_0<=BAR)||(LA11_0>=102 && LA11_0<=103)||LA11_0==105||LA11_0==108||(LA11_0>=113 && LA11_0<=114)||LA11_0==116||(LA11_0>=120 && LA11_0<=123)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // AADL.g:411:14: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_functionBody688);
            	    statement32=statement();
            	    _fsp--;

            	    stream_statement.add(statement32.getTree());

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            char_literal33=(Token)input.LT(1);
            match(input,104,FOLLOW_104_in_functionBody691); 
            stream_104.add(char_literal33);


            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 411:29: -> ^( BODY[$lc, \"BODY\"] ( statement )* )
            {
                // AADL.g:411:32: ^( BODY[$lc, \"BODY\"] ( statement )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(BODY, lc,  "BODY"), root_1);

                // AADL.g:411:52: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.next());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end functionBody

    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start block
    // AADL.g:416:1: block : (lc= '{' expression (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) ) | lc= '{' noexpStatement ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] noexpStatement ( statement )* ) | lc= '{' '}' -> ^( BLOCK[$lc, \"BODY\"] ) );
    public final block_return block() throws RecognitionException {
        block_return retval = new block_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token lc=null;
        Token vl=null;
        Token char_literal36=null;
        Token char_literal37=null;
        Token char_literal39=null;
        Token char_literal42=null;
        Token char_literal43=null;
        expression_return expression34 = null;

        involvingCondition_return involvingCondition35 = null;

        statement_return statement38 = null;

        noexpStatement_return noexpStatement40 = null;

        statement_return statement41 = null;


        CommonTree lc_tree=null;
        CommonTree vl_tree=null;
        CommonTree char_literal36_tree=null;
        CommonTree char_literal37_tree=null;
        CommonTree char_literal39_tree=null;
        CommonTree char_literal42_tree=null;
        CommonTree char_literal43_tree=null;
        RewriteRuleTokenStream stream_VBAR=new RewriteRuleTokenStream(adaptor,"token VBAR");
        RewriteRuleTokenStream stream_104=new RewriteRuleTokenStream(adaptor,"token 104");
        RewriteRuleTokenStream stream_103=new RewriteRuleTokenStream(adaptor,"token 103");
        RewriteRuleTokenStream stream_102=new RewriteRuleTokenStream(adaptor,"token 102");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        RewriteRuleSubtreeStream stream_noexpStatement=new RewriteRuleSubtreeStream(adaptor,"rule noexpStatement");
        RewriteRuleSubtreeStream stream_involvingCondition=new RewriteRuleSubtreeStream(adaptor,"rule involvingCondition");
        try {
            // AADL.g:417:5: (lc= '{' expression (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) ) | lc= '{' noexpStatement ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] noexpStatement ( statement )* ) | lc= '{' '}' -> ^( BLOCK[$lc, \"BODY\"] ) )
            int alt15=3;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==103) ) {
                switch ( input.LA(2) ) {
                case 104:
                    {
                    alt15=3;
                    }
                    break;
                case CONST:
                case BREAK:
                case RETURN:
                case SIF:
                case 102:
                case 103:
                case 108:
                    {
                    alt15=2;
                    }
                    break;
                case Identifier:
                    {
                    int LA15_4 = input.LA(3);

                    if ( (LA15_4==NamedOperator||(LA15_4>=PLUS && LA15_4<=SLASH)||LA15_4==VBAR||(LA15_4>=LESS && LA15_4<=GREATER)||LA15_4==PERCENT||(LA15_4>=EQUAL && LA15_4<=GREATEREQUAL)||LA15_4==SCOPE||LA15_4==102||LA15_4==105||(LA15_4>=109 && LA15_4<=112)||LA15_4==114) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_4>=FINPUT && LA15_4<=FOUTPUT)||LA15_4==ASSIGN) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("416:1: block : (lc= '{' expression (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) ) | lc= '{' noexpStatement ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] noexpStatement ( statement )* ) | lc= '{' '}' -> ^( BLOCK[$lc, \"BODY\"] ) );", 15, 4, input);

                        throw nvae;
                    }
                    }
                    break;
                case JAVA_ACTION:
                case HexLiteral:
                case OctalLiteral:
                case IntegerLiteral:
                case FloatingPointLiteral:
                case CharacterLiteral:
                case StringLiteral:
                case CMDARG:
                case PLUS:
                case MINUS:
                case LESS:
                case NOT:
                case HAT:
                case BAR:
                case 105:
                case 113:
                case 114:
                case 116:
                case 120:
                case 121:
                case 122:
                case 123:
                    {
                    alt15=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("416:1: block : (lc= '{' expression (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) ) | lc= '{' noexpStatement ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] noexpStatement ( statement )* ) | lc= '{' '}' -> ^( BLOCK[$lc, \"BODY\"] ) );", 15, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("416:1: block : (lc= '{' expression (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) ) | lc= '{' noexpStatement ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] noexpStatement ( statement )* ) | lc= '{' '}' -> ^( BLOCK[$lc, \"BODY\"] ) );", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // AADL.g:417:7: lc= '{' expression (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) )
                    {
                    lc=(Token)input.LT(1);
                    match(input,103,FOLLOW_103_in_block722); 
                    stream_103.add(lc);

                    pushFollow(FOLLOW_expression_in_block724);
                    expression34=expression();
                    _fsp--;

                    stream_expression.add(expression34.getTree());
                    // AADL.g:417:25: (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) )
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==VBAR) ) {
                        alt13=1;
                    }
                    else if ( (LA13_0==102) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("417:25: (vl= '|' involvingCondition '}' -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) | ';' ( statement )* '}' -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* ) )", 13, 0, input);

                        throw nvae;
                    }
                    switch (alt13) {
                        case 1 :
                            // AADL.g:418:7: vl= '|' involvingCondition '}'
                            {
                            vl=(Token)input.LT(1);
                            match(input,VBAR,FOLLOW_VBAR_in_block736); 
                            stream_VBAR.add(vl);

                            pushFollow(FOLLOW_involvingCondition_in_block738);
                            involvingCondition35=involvingCondition();
                            _fsp--;

                            stream_involvingCondition.add(involvingCondition35.getTree());
                            char_literal36=(Token)input.LT(1);
                            match(input,104,FOLLOW_104_in_block740); 
                            stream_104.add(char_literal36);


                            // AST REWRITE
                            // elements: expression, involvingCondition
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 419:4: -> ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression )
                            {
                                // AADL.g:419:7: ^( VOIDINVOLV[$lc,\"VOIDINVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(VOIDINVOLV, lc, "VOIDINVOLV"), root_1);

                                // AADL.g:419:38: ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(INVOLV_COND, vl, "INVOLV_COND"), root_2);

                                adaptor.addChild(root_2, stream_involvingCondition.next());

                                adaptor.addChild(root_1, root_2);
                                }
                                adaptor.addChild(root_1, stream_expression.next());

                                adaptor.addChild(root_0, root_1);
                                }

                            }



                            }
                            break;
                        case 2 :
                            // AADL.g:420:12: ';' ( statement )* '}'
                            {
                            char_literal37=(Token)input.LT(1);
                            match(input,102,FOLLOW_102_in_block772); 
                            stream_102.add(char_literal37);

                            // AADL.g:420:16: ( statement )*
                            loop12:
                            do {
                                int alt12=2;
                                int LA12_0 = input.LA(1);

                                if ( (LA12_0==JAVA_ACTION||LA12_0==Identifier||LA12_0==CONST||(LA12_0>=BREAK && LA12_0<=SIF)||(LA12_0>=HexLiteral && LA12_0<=CMDARG)||(LA12_0>=PLUS && LA12_0<=MINUS)||LA12_0==LESS||(LA12_0>=NOT && LA12_0<=BAR)||(LA12_0>=102 && LA12_0<=103)||LA12_0==105||LA12_0==108||(LA12_0>=113 && LA12_0<=114)||LA12_0==116||(LA12_0>=120 && LA12_0<=123)) ) {
                                    alt12=1;
                                }


                                switch (alt12) {
                            	case 1 :
                            	    // AADL.g:420:16: statement
                            	    {
                            	    pushFollow(FOLLOW_statement_in_block774);
                            	    statement38=statement();
                            	    _fsp--;

                            	    stream_statement.add(statement38.getTree());

                            	    }
                            	    break;

                            	default :
                            	    break loop12;
                                }
                            } while (true);

                            char_literal39=(Token)input.LT(1);
                            match(input,104,FOLLOW_104_in_block777); 
                            stream_104.add(char_literal39);


                            // AST REWRITE
                            // elements: expression, statement
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 421:4: -> ^( BLOCK[$lc, \"BODY\"] expression ( statement )* )
                            {
                                // AADL.g:421:7: ^( BLOCK[$lc, \"BODY\"] expression ( statement )* )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(BLOCK, lc,  "BODY"), root_1);

                                adaptor.addChild(root_1, stream_expression.next());
                                // AADL.g:421:39: ( statement )*
                                while ( stream_statement.hasNext() ) {
                                    adaptor.addChild(root_1, stream_statement.next());

                                }
                                stream_statement.reset();

                                adaptor.addChild(root_0, root_1);
                                }

                            }



                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // AADL.g:423:7: lc= '{' noexpStatement ( statement )* '}'
                    {
                    lc=(Token)input.LT(1);
                    match(input,103,FOLLOW_103_in_block809); 
                    stream_103.add(lc);

                    pushFollow(FOLLOW_noexpStatement_in_block811);
                    noexpStatement40=noexpStatement();
                    _fsp--;

                    stream_noexpStatement.add(noexpStatement40.getTree());
                    // AADL.g:423:29: ( statement )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==JAVA_ACTION||LA14_0==Identifier||LA14_0==CONST||(LA14_0>=BREAK && LA14_0<=SIF)||(LA14_0>=HexLiteral && LA14_0<=CMDARG)||(LA14_0>=PLUS && LA14_0<=MINUS)||LA14_0==LESS||(LA14_0>=NOT && LA14_0<=BAR)||(LA14_0>=102 && LA14_0<=103)||LA14_0==105||LA14_0==108||(LA14_0>=113 && LA14_0<=114)||LA14_0==116||(LA14_0>=120 && LA14_0<=123)) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // AADL.g:423:29: statement
                    	    {
                    	    pushFollow(FOLLOW_statement_in_block813);
                    	    statement41=statement();
                    	    _fsp--;

                    	    stream_statement.add(statement41.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    char_literal42=(Token)input.LT(1);
                    match(input,104,FOLLOW_104_in_block816); 
                    stream_104.add(char_literal42);


                    // AST REWRITE
                    // elements: noexpStatement, statement
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 424:4: -> ^( BLOCK[$lc, \"BODY\"] noexpStatement ( statement )* )
                    {
                        // AADL.g:424:7: ^( BLOCK[$lc, \"BODY\"] noexpStatement ( statement )* )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(BLOCK, lc,  "BODY"), root_1);

                        adaptor.addChild(root_1, stream_noexpStatement.next());
                        // AADL.g:424:43: ( statement )*
                        while ( stream_statement.hasNext() ) {
                            adaptor.addChild(root_1, stream_statement.next());

                        }
                        stream_statement.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 3 :
                    // AADL.g:425:7: lc= '{' '}'
                    {
                    lc=(Token)input.LT(1);
                    match(input,103,FOLLOW_103_in_block841); 
                    stream_103.add(lc);

                    char_literal43=(Token)input.LT(1);
                    match(input,104,FOLLOW_104_in_block843); 
                    stream_104.add(char_literal43);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 426:4: -> ^( BLOCK[$lc, \"BODY\"] )
                    {
                        // AADL.g:426:7: ^( BLOCK[$lc, \"BODY\"] )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(BLOCK, lc,  "BODY"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end block

    public static class statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // AADL.g:429:1: statement : ( block | if_statement | vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' );
    public final statement_return statement() throws RecognitionException {
        statement_return retval = new statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal51=null;
        block_return block44 = null;

        if_statement_return if_statement45 = null;

        vardef_statement_return vardef_statement46 = null;

        assign_statement_return assign_statement47 = null;

        break_statement_return break_statement48 = null;

        jump_statement_return jump_statement49 = null;

        exp_statement_return exp_statement50 = null;


        CommonTree char_literal51_tree=null;

        try {
            // AADL.g:430:5: ( block | if_statement | vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' )
            int alt16=8;
            switch ( input.LA(1) ) {
            case 103:
                {
                alt16=1;
                }
                break;
            case SIF:
                {
                alt16=2;
                }
                break;
            case CONST:
            case 108:
                {
                alt16=3;
                }
                break;
            case Identifier:
                {
                int LA16_4 = input.LA(2);

                if ( (LA16_4==NamedOperator||(LA16_4>=PLUS && LA16_4<=SLASH)||(LA16_4>=LESS && LA16_4<=GREATER)||LA16_4==PERCENT||(LA16_4>=EQUAL && LA16_4<=GREATEREQUAL)||LA16_4==SCOPE||LA16_4==102||LA16_4==105||(LA16_4>=109 && LA16_4<=112)||LA16_4==114) ) {
                    alt16=7;
                }
                else if ( ((LA16_4>=FINPUT && LA16_4<=FOUTPUT)||LA16_4==ASSIGN) ) {
                    alt16=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("429:1: statement : ( block | if_statement | vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' );", 16, 4, input);

                    throw nvae;
                }
                }
                break;
            case BREAK:
                {
                alt16=5;
                }
                break;
            case RETURN:
                {
                alt16=6;
                }
                break;
            case JAVA_ACTION:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case PLUS:
            case MINUS:
            case LESS:
            case NOT:
            case HAT:
            case BAR:
            case 105:
            case 113:
            case 114:
            case 116:
            case 120:
            case 121:
            case 122:
            case 123:
                {
                alt16=7;
                }
                break;
            case 102:
                {
                alt16=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("429:1: statement : ( block | if_statement | vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' );", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // AADL.g:430:7: block
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_block_in_statement870);
                    block44=block();
                    _fsp--;

                    adaptor.addChild(root_0, block44.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:431:7: if_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_if_statement_in_statement878);
                    if_statement45=if_statement();
                    _fsp--;

                    adaptor.addChild(root_0, if_statement45.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:432:7: vardef_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_vardef_statement_in_statement886);
                    vardef_statement46=vardef_statement();
                    _fsp--;

                    adaptor.addChild(root_0, vardef_statement46.getTree());

                    }
                    break;
                case 4 :
                    // AADL.g:433:7: assign_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_assign_statement_in_statement894);
                    assign_statement47=assign_statement();
                    _fsp--;

                    adaptor.addChild(root_0, assign_statement47.getTree());

                    }
                    break;
                case 5 :
                    // AADL.g:434:7: break_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_break_statement_in_statement902);
                    break_statement48=break_statement();
                    _fsp--;

                    adaptor.addChild(root_0, break_statement48.getTree());

                    }
                    break;
                case 6 :
                    // AADL.g:435:7: jump_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_jump_statement_in_statement910);
                    jump_statement49=jump_statement();
                    _fsp--;

                    adaptor.addChild(root_0, jump_statement49.getTree());

                    }
                    break;
                case 7 :
                    // AADL.g:436:7: exp_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_exp_statement_in_statement918);
                    exp_statement50=exp_statement();
                    _fsp--;

                    adaptor.addChild(root_0, exp_statement50.getTree());

                    }
                    break;
                case 8 :
                    // AADL.g:437:7: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal51=(Token)input.LT(1);
                    match(input,102,FOLLOW_102_in_statement926); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end statement

    public static class noexpStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start noexpStatement
    // AADL.g:440:1: noexpStatement : ( block | if_statement | vardef_statement | assign_statement | break_statement | jump_statement | ';' );
    public final noexpStatement_return noexpStatement() throws RecognitionException {
        noexpStatement_return retval = new noexpStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal58=null;
        block_return block52 = null;

        if_statement_return if_statement53 = null;

        vardef_statement_return vardef_statement54 = null;

        assign_statement_return assign_statement55 = null;

        break_statement_return break_statement56 = null;

        jump_statement_return jump_statement57 = null;


        CommonTree char_literal58_tree=null;

        try {
            // AADL.g:441:5: ( block | if_statement | vardef_statement | assign_statement | break_statement | jump_statement | ';' )
            int alt17=7;
            switch ( input.LA(1) ) {
            case 103:
                {
                alt17=1;
                }
                break;
            case SIF:
                {
                alt17=2;
                }
                break;
            case CONST:
            case 108:
                {
                alt17=3;
                }
                break;
            case Identifier:
                {
                alt17=4;
                }
                break;
            case BREAK:
                {
                alt17=5;
                }
                break;
            case RETURN:
                {
                alt17=6;
                }
                break;
            case 102:
                {
                alt17=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("440:1: noexpStatement : ( block | if_statement | vardef_statement | assign_statement | break_statement | jump_statement | ';' );", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // AADL.g:441:7: block
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_block_in_noexpStatement944);
                    block52=block();
                    _fsp--;

                    adaptor.addChild(root_0, block52.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:442:7: if_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_if_statement_in_noexpStatement952);
                    if_statement53=if_statement();
                    _fsp--;

                    adaptor.addChild(root_0, if_statement53.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:443:7: vardef_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_vardef_statement_in_noexpStatement960);
                    vardef_statement54=vardef_statement();
                    _fsp--;

                    adaptor.addChild(root_0, vardef_statement54.getTree());

                    }
                    break;
                case 4 :
                    // AADL.g:444:7: assign_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_assign_statement_in_noexpStatement968);
                    assign_statement55=assign_statement();
                    _fsp--;

                    adaptor.addChild(root_0, assign_statement55.getTree());

                    }
                    break;
                case 5 :
                    // AADL.g:445:7: break_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_break_statement_in_noexpStatement976);
                    break_statement56=break_statement();
                    _fsp--;

                    adaptor.addChild(root_0, break_statement56.getTree());

                    }
                    break;
                case 6 :
                    // AADL.g:446:7: jump_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_jump_statement_in_noexpStatement984);
                    jump_statement57=jump_statement();
                    _fsp--;

                    adaptor.addChild(root_0, jump_statement57.getTree());

                    }
                    break;
                case 7 :
                    // AADL.g:447:7: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal58=(Token)input.LT(1);
                    match(input,102,FOLLOW_102_in_noexpStatement992); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end noexpStatement

    public static class singleStatement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start singleStatement
    // AADL.g:450:1: singleStatement : ( vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' );
    public final singleStatement_return singleStatement() throws RecognitionException {
        singleStatement_return retval = new singleStatement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal64=null;
        vardef_statement_return vardef_statement59 = null;

        assign_statement_return assign_statement60 = null;

        break_statement_return break_statement61 = null;

        jump_statement_return jump_statement62 = null;

        exp_statement_return exp_statement63 = null;


        CommonTree char_literal64_tree=null;

        try {
            // AADL.g:451:5: ( vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' )
            int alt18=6;
            switch ( input.LA(1) ) {
            case CONST:
            case 108:
                {
                alt18=1;
                }
                break;
            case Identifier:
                {
                int LA18_2 = input.LA(2);

                if ( (LA18_2==NamedOperator||(LA18_2>=PLUS && LA18_2<=SLASH)||(LA18_2>=LESS && LA18_2<=GREATER)||LA18_2==PERCENT||(LA18_2>=EQUAL && LA18_2<=GREATEREQUAL)||LA18_2==SCOPE||LA18_2==102||LA18_2==105||(LA18_2>=109 && LA18_2<=112)||LA18_2==114) ) {
                    alt18=5;
                }
                else if ( ((LA18_2>=FINPUT && LA18_2<=FOUTPUT)||LA18_2==ASSIGN) ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("450:1: singleStatement : ( vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' );", 18, 2, input);

                    throw nvae;
                }
                }
                break;
            case BREAK:
                {
                alt18=3;
                }
                break;
            case RETURN:
                {
                alt18=4;
                }
                break;
            case JAVA_ACTION:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case PLUS:
            case MINUS:
            case LESS:
            case NOT:
            case HAT:
            case BAR:
            case 105:
            case 113:
            case 114:
            case 116:
            case 120:
            case 121:
            case 122:
            case 123:
                {
                alt18=5;
                }
                break;
            case 102:
                {
                alt18=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("450:1: singleStatement : ( vardef_statement | assign_statement | break_statement | jump_statement | exp_statement | ';' );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // AADL.g:451:7: vardef_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_vardef_statement_in_singleStatement1010);
                    vardef_statement59=vardef_statement();
                    _fsp--;

                    adaptor.addChild(root_0, vardef_statement59.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:452:7: assign_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_assign_statement_in_singleStatement1018);
                    assign_statement60=assign_statement();
                    _fsp--;

                    adaptor.addChild(root_0, assign_statement60.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:453:7: break_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_break_statement_in_singleStatement1026);
                    break_statement61=break_statement();
                    _fsp--;

                    adaptor.addChild(root_0, break_statement61.getTree());

                    }
                    break;
                case 4 :
                    // AADL.g:454:7: jump_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_jump_statement_in_singleStatement1034);
                    jump_statement62=jump_statement();
                    _fsp--;

                    adaptor.addChild(root_0, jump_statement62.getTree());

                    }
                    break;
                case 5 :
                    // AADL.g:455:7: exp_statement
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_exp_statement_in_singleStatement1042);
                    exp_statement63=exp_statement();
                    _fsp--;

                    adaptor.addChild(root_0, exp_statement63.getTree());

                    }
                    break;
                case 6 :
                    // AADL.g:456:7: ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal64=(Token)input.LT(1);
                    match(input,102,FOLLOW_102_in_singleStatement1050); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end singleStatement

    public static class vardef_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start vardef_statement
    // AADL.g:459:1: vardef_statement : ( localVariableDeclaration ';' | constVariableDeclaration ';' );
    public final vardef_statement_return vardef_statement() throws RecognitionException {
        vardef_statement_return retval = new vardef_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal66=null;
        Token char_literal68=null;
        localVariableDeclaration_return localVariableDeclaration65 = null;

        constVariableDeclaration_return constVariableDeclaration67 = null;


        CommonTree char_literal66_tree=null;
        CommonTree char_literal68_tree=null;

        try {
            // AADL.g:460:5: ( localVariableDeclaration ';' | constVariableDeclaration ';' )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==108) ) {
                alt19=1;
            }
            else if ( (LA19_0==CONST) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("459:1: vardef_statement : ( localVariableDeclaration ';' | constVariableDeclaration ';' );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // AADL.g:460:7: localVariableDeclaration ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_localVariableDeclaration_in_vardef_statement1068);
                    localVariableDeclaration65=localVariableDeclaration();
                    _fsp--;

                    adaptor.addChild(root_0, localVariableDeclaration65.getTree());
                    char_literal66=(Token)input.LT(1);
                    match(input,102,FOLLOW_102_in_vardef_statement1070); 

                    }
                    break;
                case 2 :
                    // AADL.g:461:7: constVariableDeclaration ';'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_constVariableDeclaration_in_vardef_statement1079);
                    constVariableDeclaration67=constVariableDeclaration();
                    _fsp--;

                    adaptor.addChild(root_0, constVariableDeclaration67.getTree());
                    char_literal68=(Token)input.LT(1);
                    match(input,102,FOLLOW_102_in_vardef_statement1081); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end vardef_statement

    public static class constVariableDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start constVariableDeclaration
    // AADL.g:464:1: constVariableDeclaration : CONST localVariableDeclarationRest ( ',' localVariableDeclarationRest )* -> ( ^( CONST localVariableDeclarationRest ) )+ ;
    public final constVariableDeclaration_return constVariableDeclaration() throws RecognitionException {
        constVariableDeclaration_return retval = new constVariableDeclaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token CONST69=null;
        Token char_literal71=null;
        localVariableDeclarationRest_return localVariableDeclarationRest70 = null;

        localVariableDeclarationRest_return localVariableDeclarationRest72 = null;


        CommonTree CONST69_tree=null;
        CommonTree char_literal71_tree=null;
        RewriteRuleTokenStream stream_107=new RewriteRuleTokenStream(adaptor,"token 107");
        RewriteRuleTokenStream stream_CONST=new RewriteRuleTokenStream(adaptor,"token CONST");
        RewriteRuleSubtreeStream stream_localVariableDeclarationRest=new RewriteRuleSubtreeStream(adaptor,"rule localVariableDeclarationRest");
        try {
            // AADL.g:465:5: ( CONST localVariableDeclarationRest ( ',' localVariableDeclarationRest )* -> ( ^( CONST localVariableDeclarationRest ) )+ )
            // AADL.g:465:7: CONST localVariableDeclarationRest ( ',' localVariableDeclarationRest )*
            {
            CONST69=(Token)input.LT(1);
            match(input,CONST,FOLLOW_CONST_in_constVariableDeclaration1099); 
            stream_CONST.add(CONST69);

            pushFollow(FOLLOW_localVariableDeclarationRest_in_constVariableDeclaration1101);
            localVariableDeclarationRest70=localVariableDeclarationRest();
            _fsp--;

            stream_localVariableDeclarationRest.add(localVariableDeclarationRest70.getTree());
            // AADL.g:465:42: ( ',' localVariableDeclarationRest )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==107) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // AADL.g:465:43: ',' localVariableDeclarationRest
            	    {
            	    char_literal71=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_constVariableDeclaration1104); 
            	    stream_107.add(char_literal71);

            	    pushFollow(FOLLOW_localVariableDeclarationRest_in_constVariableDeclaration1106);
            	    localVariableDeclarationRest72=localVariableDeclarationRest();
            	    _fsp--;

            	    stream_localVariableDeclarationRest.add(localVariableDeclarationRest72.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            // AST REWRITE
            // elements: CONST, localVariableDeclarationRest
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 466:7: -> ( ^( CONST localVariableDeclarationRest ) )+
            {
                if ( !(stream_CONST.hasNext()||stream_localVariableDeclarationRest.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_CONST.hasNext()||stream_localVariableDeclarationRest.hasNext() ) {
                    // AADL.g:466:10: ^( CONST localVariableDeclarationRest )
                    {
                    CommonTree root_1 = (CommonTree)adaptor.nil();
                    root_1 = (CommonTree)adaptor.becomeRoot(stream_CONST.next(), root_1);

                    adaptor.addChild(root_1, stream_localVariableDeclarationRest.next());

                    adaptor.addChild(root_0, root_1);
                    }

                }
                stream_CONST.reset();
                stream_localVariableDeclarationRest.reset();

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end constVariableDeclaration

    public static class localVariableDeclaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start localVariableDeclaration
    // AADL.g:469:1: localVariableDeclaration : 'var' localVariableDeclarationRest ( ',' localVariableDeclarationRest )* ;
    public final localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        localVariableDeclaration_return retval = new localVariableDeclaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal73=null;
        Token char_literal75=null;
        localVariableDeclarationRest_return localVariableDeclarationRest74 = null;

        localVariableDeclarationRest_return localVariableDeclarationRest76 = null;


        CommonTree string_literal73_tree=null;
        CommonTree char_literal75_tree=null;

        try {
            // AADL.g:470:5: ( 'var' localVariableDeclarationRest ( ',' localVariableDeclarationRest )* )
            // AADL.g:470:7: 'var' localVariableDeclarationRest ( ',' localVariableDeclarationRest )*
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal73=(Token)input.LT(1);
            match(input,108,FOLLOW_108_in_localVariableDeclaration1140); 
            pushFollow(FOLLOW_localVariableDeclarationRest_in_localVariableDeclaration1143);
            localVariableDeclarationRest74=localVariableDeclarationRest();
            _fsp--;

            adaptor.addChild(root_0, localVariableDeclarationRest74.getTree());
            // AADL.g:470:43: ( ',' localVariableDeclarationRest )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==107) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // AADL.g:470:44: ',' localVariableDeclarationRest
            	    {
            	    char_literal75=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_localVariableDeclaration1146); 
            	    pushFollow(FOLLOW_localVariableDeclarationRest_in_localVariableDeclaration1149);
            	    localVariableDeclarationRest76=localVariableDeclarationRest();
            	    _fsp--;

            	    adaptor.addChild(root_0, localVariableDeclarationRest76.getTree());

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end localVariableDeclaration

    public static class localVariableDeclarationRest_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start localVariableDeclarationRest
    // AADL.g:473:1: localVariableDeclarationRest : variableDefinition ( (eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) ) ) | ( FINPUT fileDeclarator ) -> ^( variableDefinition ^( FINPUT fileDeclarator ) ) | lc= '(' ( methodArguments )? ')' -> ^( variableDefinition ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? ) ) | -> variableDefinition ) ;
    public final localVariableDeclarationRest_return localVariableDeclarationRest() throws RecognitionException {
        localVariableDeclarationRest_return retval = new localVariableDeclarationRest_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token eq=null;
        Token lc=null;
        Token FINPUT80=null;
        Token char_literal83=null;
        variableDefinition_return variableDefinition77 = null;

        expression_return expression78 = null;

        involvingExpression_return involvingExpression79 = null;

        fileDeclarator_return fileDeclarator81 = null;

        methodArguments_return methodArguments82 = null;


        CommonTree eq_tree=null;
        CommonTree lc_tree=null;
        CommonTree FINPUT80_tree=null;
        CommonTree char_literal83_tree=null;
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleTokenStream stream_FINPUT=new RewriteRuleTokenStream(adaptor,"token FINPUT");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_variableDefinition=new RewriteRuleSubtreeStream(adaptor,"rule variableDefinition");
        RewriteRuleSubtreeStream stream_methodArguments=new RewriteRuleSubtreeStream(adaptor,"rule methodArguments");
        RewriteRuleSubtreeStream stream_involvingExpression=new RewriteRuleSubtreeStream(adaptor,"rule involvingExpression");
        RewriteRuleSubtreeStream stream_fileDeclarator=new RewriteRuleSubtreeStream(adaptor,"rule fileDeclarator");
        try {
            // AADL.g:474:5: ( variableDefinition ( (eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) ) ) | ( FINPUT fileDeclarator ) -> ^( variableDefinition ^( FINPUT fileDeclarator ) ) | lc= '(' ( methodArguments )? ')' -> ^( variableDefinition ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? ) ) | -> variableDefinition ) )
            // AADL.g:474:7: variableDefinition ( (eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) ) ) | ( FINPUT fileDeclarator ) -> ^( variableDefinition ^( FINPUT fileDeclarator ) ) | lc= '(' ( methodArguments )? ')' -> ^( variableDefinition ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? ) ) | -> variableDefinition )
            {
            pushFollow(FOLLOW_variableDefinition_in_localVariableDeclarationRest1168);
            variableDefinition77=variableDefinition();
            _fsp--;

            stream_variableDefinition.add(variableDefinition77.getTree());
            // AADL.g:474:26: ( (eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) ) ) | ( FINPUT fileDeclarator ) -> ^( variableDefinition ^( FINPUT fileDeclarator ) ) | lc= '(' ( methodArguments )? ')' -> ^( variableDefinition ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? ) ) | -> variableDefinition )
            int alt24=4;
            switch ( input.LA(1) ) {
            case ASSIGN:
                {
                alt24=1;
                }
                break;
            case FINPUT:
                {
                alt24=2;
                }
                break;
            case 105:
                {
                alt24=3;
                }
                break;
            case 102:
            case 107:
                {
                alt24=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("474:26: ( (eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) ) ) | ( FINPUT fileDeclarator ) -> ^( variableDefinition ^( FINPUT fileDeclarator ) ) | lc= '(' ( methodArguments )? ')' -> ^( variableDefinition ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? ) ) | -> variableDefinition )", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // AADL.g:475:7: (eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) ) )
                    {
                    // AADL.g:475:7: (eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) ) )
                    // AADL.g:475:8: eq= '=' ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) )
                    {
                    eq=(Token)input.LT(1);
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_localVariableDeclarationRest1181); 
                    stream_ASSIGN.add(eq);

                    // AADL.g:475:15: ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) )
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==JAVA_ACTION||LA22_0==Identifier||(LA22_0>=HexLiteral && LA22_0<=CMDARG)||(LA22_0>=PLUS && LA22_0<=MINUS)||LA22_0==LESS||(LA22_0>=NOT && LA22_0<=BAR)||LA22_0==105||(LA22_0>=113 && LA22_0<=114)||LA22_0==116||(LA22_0>=120 && LA22_0<=123)) ) {
                        alt22=1;
                    }
                    else if ( (LA22_0==103) ) {
                        alt22=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("475:15: ( expression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) ) | involvingExpression -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) ) )", 22, 0, input);

                        throw nvae;
                    }
                    switch (alt22) {
                        case 1 :
                            // AADL.g:476:8: expression
                            {
                            pushFollow(FOLLOW_expression_in_localVariableDeclarationRest1192);
                            expression78=expression();
                            _fsp--;

                            stream_expression.add(expression78.getTree());

                            // AST REWRITE
                            // elements: variableDefinition, expression
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 476:20: -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) )
                            {
                                // AADL.g:476:23: ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] expression ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(stream_variableDefinition.nextNode(), root_1);

                                // AADL.g:476:44: ^( VARINIT[$eq,\"VARINIT\"] expression )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(VARINIT, eq, "VARINIT"), root_2);

                                adaptor.addChild(root_2, stream_expression.next());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }



                            }
                            break;
                        case 2 :
                            // AADL.g:477:13: involvingExpression
                            {
                            pushFollow(FOLLOW_involvingExpression_in_localVariableDeclarationRest1220);
                            involvingExpression79=involvingExpression();
                            _fsp--;

                            stream_involvingExpression.add(involvingExpression79.getTree());

                            // AST REWRITE
                            // elements: involvingExpression, variableDefinition
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 477:33: -> ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) )
                            {
                                // AADL.g:477:36: ^( variableDefinition ^( VARINIT[$eq,\"VARINIT\"] involvingExpression ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(stream_variableDefinition.nextNode(), root_1);

                                // AADL.g:477:57: ^( VARINIT[$eq,\"VARINIT\"] involvingExpression )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(VARINIT, eq, "VARINIT"), root_2);

                                adaptor.addChild(root_2, stream_involvingExpression.next());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }



                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // AADL.g:479:8: ( FINPUT fileDeclarator )
                    {
                    // AADL.g:479:8: ( FINPUT fileDeclarator )
                    // AADL.g:479:9: FINPUT fileDeclarator
                    {
                    FINPUT80=(Token)input.LT(1);
                    match(input,FINPUT,FOLLOW_FINPUT_in_localVariableDeclarationRest1253); 
                    stream_FINPUT.add(FINPUT80);

                    pushFollow(FOLLOW_fileDeclarator_in_localVariableDeclarationRest1255);
                    fileDeclarator81=fileDeclarator();
                    _fsp--;

                    stream_fileDeclarator.add(fileDeclarator81.getTree());

                    }


                    // AST REWRITE
                    // elements: FINPUT, variableDefinition, fileDeclarator
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 479:33: -> ^( variableDefinition ^( FINPUT fileDeclarator ) )
                    {
                        // AADL.g:479:36: ^( variableDefinition ^( FINPUT fileDeclarator ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_variableDefinition.nextNode(), root_1);

                        // AADL.g:479:57: ^( FINPUT fileDeclarator )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(stream_FINPUT.next(), root_2);

                        adaptor.addChild(root_2, stream_fileDeclarator.next());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 3 :
                    // AADL.g:480:8: lc= '(' ( methodArguments )? ')'
                    {
                    lc=(Token)input.LT(1);
                    match(input,105,FOLLOW_105_in_localVariableDeclarationRest1280); 
                    stream_105.add(lc);

                    // AADL.g:480:15: ( methodArguments )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==JAVA_ACTION||LA23_0==Identifier||(LA23_0>=HexLiteral && LA23_0<=CMDARG)||(LA23_0>=PLUS && LA23_0<=MINUS)||LA23_0==LESS||(LA23_0>=NOT && LA23_0<=BAR)||LA23_0==105||(LA23_0>=113 && LA23_0<=114)||LA23_0==116||(LA23_0>=120 && LA23_0<=123)) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // AADL.g:480:15: methodArguments
                            {
                            pushFollow(FOLLOW_methodArguments_in_localVariableDeclarationRest1282);
                            methodArguments82=methodArguments();
                            _fsp--;

                            stream_methodArguments.add(methodArguments82.getTree());

                            }
                            break;

                    }

                    char_literal83=(Token)input.LT(1);
                    match(input,106,FOLLOW_106_in_localVariableDeclarationRest1285); 
                    stream_106.add(char_literal83);


                    // AST REWRITE
                    // elements: variableDefinition, methodArguments
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 480:36: -> ^( variableDefinition ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? ) )
                    {
                        // AADL.g:480:39: ^( variableDefinition ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_variableDefinition.nextNode(), root_1);

                        // AADL.g:480:60: ^( VARNEW[$lc,\"VARNEW\"] ( methodArguments )? )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(VARNEW, lc, "VARNEW"), root_2);

                        // AADL.g:480:83: ( methodArguments )?
                        if ( stream_methodArguments.hasNext() ) {
                            adaptor.addChild(root_2, stream_methodArguments.next());

                        }
                        stream_methodArguments.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 4 :
                    // AADL.g:481:12: 
                    {

                    // AST REWRITE
                    // elements: variableDefinition
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 481:12: -> variableDefinition
                    {
                        adaptor.addChild(root_0, stream_variableDefinition.next());

                    }



                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end localVariableDeclarationRest

    public static class exp_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start exp_statement
    // AADL.g:485:1: exp_statement : expression ';' ;
    public final exp_statement_return exp_statement() throws RecognitionException {
        exp_statement_return retval = new exp_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal85=null;
        expression_return expression84 = null;


        CommonTree char_literal85_tree=null;

        try {
            // AADL.g:486:5: ( expression ';' )
            // AADL.g:486:7: expression ';'
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_exp_statement1338);
            expression84=expression();
            _fsp--;

            adaptor.addChild(root_0, expression84.getTree());
            char_literal85=(Token)input.LT(1);
            match(input,102,FOLLOW_102_in_exp_statement1340); 

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end exp_statement

    public static class assign_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start assign_statement
    // AADL.g:489:1: assign_statement : Identifier ( (eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) ) ) | FINPUT fileDeclarator -> ^( Identifier ^( FINPUT fileDeclarator ) ) | FOUTPUT fileDeclarator -> ^( Identifier ^( FOUTPUT fileDeclarator ) ) ) ';' ;
    public final assign_statement_return assign_statement() throws RecognitionException {
        assign_statement_return retval = new assign_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token eq=null;
        Token Identifier86=null;
        Token FINPUT89=null;
        Token FOUTPUT91=null;
        Token char_literal93=null;
        expression_return expression87 = null;

        involvingExpression_return involvingExpression88 = null;

        fileDeclarator_return fileDeclarator90 = null;

        fileDeclarator_return fileDeclarator92 = null;


        CommonTree eq_tree=null;
        CommonTree Identifier86_tree=null;
        CommonTree FINPUT89_tree=null;
        CommonTree FOUTPUT91_tree=null;
        CommonTree char_literal93_tree=null;
        RewriteRuleTokenStream stream_FINPUT=new RewriteRuleTokenStream(adaptor,"token FINPUT");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_102=new RewriteRuleTokenStream(adaptor,"token 102");
        RewriteRuleTokenStream stream_FOUTPUT=new RewriteRuleTokenStream(adaptor,"token FOUTPUT");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_involvingExpression=new RewriteRuleSubtreeStream(adaptor,"rule involvingExpression");
        RewriteRuleSubtreeStream stream_fileDeclarator=new RewriteRuleSubtreeStream(adaptor,"rule fileDeclarator");
        try {
            // AADL.g:490:5: ( Identifier ( (eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) ) ) | FINPUT fileDeclarator -> ^( Identifier ^( FINPUT fileDeclarator ) ) | FOUTPUT fileDeclarator -> ^( Identifier ^( FOUTPUT fileDeclarator ) ) ) ';' )
            // AADL.g:490:7: Identifier ( (eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) ) ) | FINPUT fileDeclarator -> ^( Identifier ^( FINPUT fileDeclarator ) ) | FOUTPUT fileDeclarator -> ^( Identifier ^( FOUTPUT fileDeclarator ) ) ) ';'
            {
            Identifier86=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_assign_statement1358); 
            stream_Identifier.add(Identifier86);

            // AADL.g:490:18: ( (eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) ) ) | FINPUT fileDeclarator -> ^( Identifier ^( FINPUT fileDeclarator ) ) | FOUTPUT fileDeclarator -> ^( Identifier ^( FOUTPUT fileDeclarator ) ) )
            int alt26=3;
            switch ( input.LA(1) ) {
            case ASSIGN:
                {
                alt26=1;
                }
                break;
            case FINPUT:
                {
                alt26=2;
                }
                break;
            case FOUTPUT:
                {
                alt26=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("490:18: ( (eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) ) ) | FINPUT fileDeclarator -> ^( Identifier ^( FINPUT fileDeclarator ) ) | FOUTPUT fileDeclarator -> ^( Identifier ^( FOUTPUT fileDeclarator ) ) )", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // AADL.g:491:7: (eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) ) )
                    {
                    // AADL.g:491:7: (eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) ) )
                    // AADL.g:491:8: eq= '=' ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) )
                    {
                    eq=(Token)input.LT(1);
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_assign_statement1371); 
                    stream_ASSIGN.add(eq);

                    // AADL.g:491:15: ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) )
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==JAVA_ACTION||LA25_0==Identifier||(LA25_0>=HexLiteral && LA25_0<=CMDARG)||(LA25_0>=PLUS && LA25_0<=MINUS)||LA25_0==LESS||(LA25_0>=NOT && LA25_0<=BAR)||LA25_0==105||(LA25_0>=113 && LA25_0<=114)||LA25_0==116||(LA25_0>=120 && LA25_0<=123)) ) {
                        alt25=1;
                    }
                    else if ( (LA25_0==103) ) {
                        alt25=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("491:15: ( expression -> ^( Identifier ^( $eq expression ) ) | involvingExpression -> ^( Identifier ^( $eq involvingExpression ) ) )", 25, 0, input);

                        throw nvae;
                    }
                    switch (alt25) {
                        case 1 :
                            // AADL.g:492:8: expression
                            {
                            pushFollow(FOLLOW_expression_in_assign_statement1382);
                            expression87=expression();
                            _fsp--;

                            stream_expression.add(expression87.getTree());

                            // AST REWRITE
                            // elements: Identifier, eq, expression
                            // token labels: eq
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleTokenStream stream_eq=new RewriteRuleTokenStream(adaptor,"token eq",eq);
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 492:20: -> ^( Identifier ^( $eq expression ) )
                            {
                                // AADL.g:492:23: ^( Identifier ^( $eq expression ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(stream_Identifier.next(), root_1);

                                // AADL.g:492:36: ^( $eq expression )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(stream_eq.next(), root_2);

                                adaptor.addChild(root_2, stream_expression.next());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }



                            }
                            break;
                        case 2 :
                            // AADL.g:493:13: involvingExpression
                            {
                            pushFollow(FOLLOW_involvingExpression_in_assign_statement1410);
                            involvingExpression88=involvingExpression();
                            _fsp--;

                            stream_involvingExpression.add(involvingExpression88.getTree());

                            // AST REWRITE
                            // elements: eq, involvingExpression, Identifier
                            // token labels: eq
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleTokenStream stream_eq=new RewriteRuleTokenStream(adaptor,"token eq",eq);
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 493:33: -> ^( Identifier ^( $eq involvingExpression ) )
                            {
                                // AADL.g:493:36: ^( Identifier ^( $eq involvingExpression ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(stream_Identifier.next(), root_1);

                                // AADL.g:493:49: ^( $eq involvingExpression )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(stream_eq.next(), root_2);

                                adaptor.addChild(root_2, stream_involvingExpression.next());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }



                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // AADL.g:495:8: FINPUT fileDeclarator
                    {
                    FINPUT89=(Token)input.LT(1);
                    match(input,FINPUT,FOLLOW_FINPUT_in_assign_statement1442); 
                    stream_FINPUT.add(FINPUT89);

                    pushFollow(FOLLOW_fileDeclarator_in_assign_statement1444);
                    fileDeclarator90=fileDeclarator();
                    _fsp--;

                    stream_fileDeclarator.add(fileDeclarator90.getTree());

                    // AST REWRITE
                    // elements: FINPUT, Identifier, fileDeclarator
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 495:31: -> ^( Identifier ^( FINPUT fileDeclarator ) )
                    {
                        // AADL.g:495:34: ^( Identifier ^( FINPUT fileDeclarator ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_Identifier.next(), root_1);

                        // AADL.g:495:47: ^( FINPUT fileDeclarator )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(stream_FINPUT.next(), root_2);

                        adaptor.addChild(root_2, stream_fileDeclarator.next());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 3 :
                    // AADL.g:496:8: FOUTPUT fileDeclarator
                    {
                    FOUTPUT91=(Token)input.LT(1);
                    match(input,FOUTPUT,FOLLOW_FOUTPUT_in_assign_statement1466); 
                    stream_FOUTPUT.add(FOUTPUT91);

                    pushFollow(FOLLOW_fileDeclarator_in_assign_statement1468);
                    fileDeclarator92=fileDeclarator();
                    _fsp--;

                    stream_fileDeclarator.add(fileDeclarator92.getTree());

                    // AST REWRITE
                    // elements: FOUTPUT, fileDeclarator, Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 496:32: -> ^( Identifier ^( FOUTPUT fileDeclarator ) )
                    {
                        // AADL.g:496:35: ^( Identifier ^( FOUTPUT fileDeclarator ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_Identifier.next(), root_1);

                        // AADL.g:496:48: ^( FOUTPUT fileDeclarator )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(stream_FOUTPUT.next(), root_2);

                        adaptor.addChild(root_2, stream_fileDeclarator.next());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;

            }

            char_literal93=(Token)input.LT(1);
            match(input,102,FOLLOW_102_in_assign_statement1490); 
            stream_102.add(char_literal93);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end assign_statement

    public static class break_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start break_statement
    // AADL.g:500:1: break_statement : BREAK ';' ;
    public final break_statement_return break_statement() throws RecognitionException {
        break_statement_return retval = new break_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token BREAK94=null;
        Token char_literal95=null;

        CommonTree BREAK94_tree=null;
        CommonTree char_literal95_tree=null;

        try {
            // AADL.g:501:5: ( BREAK ';' )
            // AADL.g:501:7: BREAK ';'
            {
            root_0 = (CommonTree)adaptor.nil();

            BREAK94=(Token)input.LT(1);
            match(input,BREAK,FOLLOW_BREAK_in_break_statement1507); 
            BREAK94_tree = (CommonTree)adaptor.create(BREAK94);
            root_0 = (CommonTree)adaptor.becomeRoot(BREAK94_tree, root_0);

            char_literal95=(Token)input.LT(1);
            match(input,102,FOLLOW_102_in_break_statement1510); 

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end break_statement

    public static class jump_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start jump_statement
    // AADL.g:504:1: jump_statement : RETURN ( expression )? ';' ;
    public final jump_statement_return jump_statement() throws RecognitionException {
        jump_statement_return retval = new jump_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token RETURN96=null;
        Token char_literal98=null;
        expression_return expression97 = null;


        CommonTree RETURN96_tree=null;
        CommonTree char_literal98_tree=null;

        try {
            // AADL.g:505:5: ( RETURN ( expression )? ';' )
            // AADL.g:505:7: RETURN ( expression )? ';'
            {
            root_0 = (CommonTree)adaptor.nil();

            RETURN96=(Token)input.LT(1);
            match(input,RETURN,FOLLOW_RETURN_in_jump_statement1528); 
            RETURN96_tree = (CommonTree)adaptor.create(RETURN96);
            root_0 = (CommonTree)adaptor.becomeRoot(RETURN96_tree, root_0);

            // AADL.g:505:15: ( expression )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==JAVA_ACTION||LA27_0==Identifier||(LA27_0>=HexLiteral && LA27_0<=CMDARG)||(LA27_0>=PLUS && LA27_0<=MINUS)||LA27_0==LESS||(LA27_0>=NOT && LA27_0<=BAR)||LA27_0==105||(LA27_0>=113 && LA27_0<=114)||LA27_0==116||(LA27_0>=120 && LA27_0<=123)) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // AADL.g:505:15: expression
                    {
                    pushFollow(FOLLOW_expression_in_jump_statement1531);
                    expression97=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression97.getTree());

                    }
                    break;

            }

            char_literal98=(Token)input.LT(1);
            match(input,102,FOLLOW_102_in_jump_statement1534); 

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end jump_statement

    public static class if_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start if_statement
    // AADL.g:508:1: if_statement : SIF '(' expression ')' ( if_statement -> ^( SIF expression if_statement ) | block ( else_statement )? -> ^( SIF expression block ( else_statement )? ) | singleStatement ( else_statement )? -> ^( SIF expression singleStatement ( else_statement )? ) ) ;
    public final if_statement_return if_statement() throws RecognitionException {
        if_statement_return retval = new if_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token SIF99=null;
        Token char_literal100=null;
        Token char_literal102=null;
        expression_return expression101 = null;

        if_statement_return if_statement103 = null;

        block_return block104 = null;

        else_statement_return else_statement105 = null;

        singleStatement_return singleStatement106 = null;

        else_statement_return else_statement107 = null;


        CommonTree SIF99_tree=null;
        CommonTree char_literal100_tree=null;
        CommonTree char_literal102_tree=null;
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleTokenStream stream_SIF=new RewriteRuleTokenStream(adaptor,"token SIF");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_else_statement=new RewriteRuleSubtreeStream(adaptor,"rule else_statement");
        RewriteRuleSubtreeStream stream_if_statement=new RewriteRuleSubtreeStream(adaptor,"rule if_statement");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        RewriteRuleSubtreeStream stream_singleStatement=new RewriteRuleSubtreeStream(adaptor,"rule singleStatement");
        try {
            // AADL.g:509:5: ( SIF '(' expression ')' ( if_statement -> ^( SIF expression if_statement ) | block ( else_statement )? -> ^( SIF expression block ( else_statement )? ) | singleStatement ( else_statement )? -> ^( SIF expression singleStatement ( else_statement )? ) ) )
            // AADL.g:509:7: SIF '(' expression ')' ( if_statement -> ^( SIF expression if_statement ) | block ( else_statement )? -> ^( SIF expression block ( else_statement )? ) | singleStatement ( else_statement )? -> ^( SIF expression singleStatement ( else_statement )? ) )
            {
            SIF99=(Token)input.LT(1);
            match(input,SIF,FOLLOW_SIF_in_if_statement1552); 
            stream_SIF.add(SIF99);

            char_literal100=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_if_statement1554); 
            stream_105.add(char_literal100);

            pushFollow(FOLLOW_expression_in_if_statement1556);
            expression101=expression();
            _fsp--;

            stream_expression.add(expression101.getTree());
            char_literal102=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_if_statement1558); 
            stream_106.add(char_literal102);

            // AADL.g:510:5: ( if_statement -> ^( SIF expression if_statement ) | block ( else_statement )? -> ^( SIF expression block ( else_statement )? ) | singleStatement ( else_statement )? -> ^( SIF expression singleStatement ( else_statement )? ) )
            int alt30=3;
            switch ( input.LA(1) ) {
            case SIF:
                {
                alt30=1;
                }
                break;
            case 103:
                {
                alt30=2;
                }
                break;
            case JAVA_ACTION:
            case Identifier:
            case CONST:
            case BREAK:
            case RETURN:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case PLUS:
            case MINUS:
            case LESS:
            case NOT:
            case HAT:
            case BAR:
            case 102:
            case 105:
            case 108:
            case 113:
            case 114:
            case 116:
            case 120:
            case 121:
            case 122:
            case 123:
                {
                alt30=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("510:5: ( if_statement -> ^( SIF expression if_statement ) | block ( else_statement )? -> ^( SIF expression block ( else_statement )? ) | singleStatement ( else_statement )? -> ^( SIF expression singleStatement ( else_statement )? ) )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // AADL.g:510:7: if_statement
                    {
                    pushFollow(FOLLOW_if_statement_in_if_statement1566);
                    if_statement103=if_statement();
                    _fsp--;

                    stream_if_statement.add(if_statement103.getTree());

                    // AST REWRITE
                    // elements: if_statement, expression, SIF
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 510:22: -> ^( SIF expression if_statement )
                    {
                        // AADL.g:510:25: ^( SIF expression if_statement )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_SIF.next(), root_1);

                        adaptor.addChild(root_1, stream_expression.next());
                        adaptor.addChild(root_1, stream_if_statement.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 2 :
                    // AADL.g:511:7: block ( else_statement )?
                    {
                    pushFollow(FOLLOW_block_in_if_statement1586);
                    block104=block();
                    _fsp--;

                    stream_block.add(block104.getTree());
                    // AADL.g:511:13: ( else_statement )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==SELSE) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // AADL.g:511:13: else_statement
                            {
                            pushFollow(FOLLOW_else_statement_in_if_statement1588);
                            else_statement105=else_statement();
                            _fsp--;

                            stream_else_statement.add(else_statement105.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: expression, else_statement, block, SIF
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 511:30: -> ^( SIF expression block ( else_statement )? )
                    {
                        // AADL.g:511:33: ^( SIF expression block ( else_statement )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_SIF.next(), root_1);

                        adaptor.addChild(root_1, stream_expression.next());
                        adaptor.addChild(root_1, stream_block.next());
                        // AADL.g:511:56: ( else_statement )?
                        if ( stream_else_statement.hasNext() ) {
                            adaptor.addChild(root_1, stream_else_statement.next());

                        }
                        stream_else_statement.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 3 :
                    // AADL.g:512:7: singleStatement ( else_statement )?
                    {
                    pushFollow(FOLLOW_singleStatement_in_if_statement1611);
                    singleStatement106=singleStatement();
                    _fsp--;

                    stream_singleStatement.add(singleStatement106.getTree());
                    // AADL.g:512:23: ( else_statement )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==SELSE) ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // AADL.g:512:23: else_statement
                            {
                            pushFollow(FOLLOW_else_statement_in_if_statement1613);
                            else_statement107=else_statement();
                            _fsp--;

                            stream_else_statement.add(else_statement107.getTree());

                            }
                            break;

                    }


                    // AST REWRITE
                    // elements: SIF, else_statement, singleStatement, expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 512:39: -> ^( SIF expression singleStatement ( else_statement )? )
                    {
                        // AADL.g:512:42: ^( SIF expression singleStatement ( else_statement )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_SIF.next(), root_1);

                        adaptor.addChild(root_1, stream_expression.next());
                        adaptor.addChild(root_1, stream_singleStatement.next());
                        // AADL.g:512:75: ( else_statement )?
                        if ( stream_else_statement.hasNext() ) {
                            adaptor.addChild(root_1, stream_else_statement.next());

                        }
                        stream_else_statement.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end if_statement

    public static class else_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start else_statement
    // AADL.g:516:1: else_statement : SELSE ( block | if_statement | singleStatement ) ;
    public final else_statement_return else_statement() throws RecognitionException {
        else_statement_return retval = new else_statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token SELSE108=null;
        block_return block109 = null;

        if_statement_return if_statement110 = null;

        singleStatement_return singleStatement111 = null;


        CommonTree SELSE108_tree=null;

        try {
            // AADL.g:517:5: ( SELSE ( block | if_statement | singleStatement ) )
            // AADL.g:517:7: SELSE ( block | if_statement | singleStatement )
            {
            root_0 = (CommonTree)adaptor.nil();

            SELSE108=(Token)input.LT(1);
            match(input,SELSE,FOLLOW_SELSE_in_else_statement1650); 
            SELSE108_tree = (CommonTree)adaptor.create(SELSE108);
            root_0 = (CommonTree)adaptor.becomeRoot(SELSE108_tree, root_0);

            // AADL.g:518:5: ( block | if_statement | singleStatement )
            int alt31=3;
            switch ( input.LA(1) ) {
            case 103:
                {
                alt31=1;
                }
                break;
            case SIF:
                {
                alt31=2;
                }
                break;
            case JAVA_ACTION:
            case Identifier:
            case CONST:
            case BREAK:
            case RETURN:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case PLUS:
            case MINUS:
            case LESS:
            case NOT:
            case HAT:
            case BAR:
            case 102:
            case 105:
            case 108:
            case 113:
            case 114:
            case 116:
            case 120:
            case 121:
            case 122:
            case 123:
                {
                alt31=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("518:5: ( block | if_statement | singleStatement )", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // AADL.g:518:7: block
                    {
                    pushFollow(FOLLOW_block_in_else_statement1659);
                    block109=block();
                    _fsp--;

                    adaptor.addChild(root_0, block109.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:519:7: if_statement
                    {
                    pushFollow(FOLLOW_if_statement_in_else_statement1667);
                    if_statement110=if_statement();
                    _fsp--;

                    adaptor.addChild(root_0, if_statement110.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:520:7: singleStatement
                    {
                    pushFollow(FOLLOW_singleStatement_in_else_statement1675);
                    singleStatement111=singleStatement();
                    _fsp--;

                    adaptor.addChild(root_0, singleStatement111.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end else_statement

    public static class involvingExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start involvingExpression
    // AADL.g:527:1: involvingExpression : lc= '{' expression vl= '|' involvingCondition '}' -> ^( INVOLV[$lc,\"INVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) ;
    public final involvingExpression_return involvingExpression() throws RecognitionException {
        involvingExpression_return retval = new involvingExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token lc=null;
        Token vl=null;
        Token char_literal114=null;
        expression_return expression112 = null;

        involvingCondition_return involvingCondition113 = null;


        CommonTree lc_tree=null;
        CommonTree vl_tree=null;
        CommonTree char_literal114_tree=null;
        RewriteRuleTokenStream stream_VBAR=new RewriteRuleTokenStream(adaptor,"token VBAR");
        RewriteRuleTokenStream stream_104=new RewriteRuleTokenStream(adaptor,"token 104");
        RewriteRuleTokenStream stream_103=new RewriteRuleTokenStream(adaptor,"token 103");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_involvingCondition=new RewriteRuleSubtreeStream(adaptor,"rule involvingCondition");
        try {
            // AADL.g:528:5: (lc= '{' expression vl= '|' involvingCondition '}' -> ^( INVOLV[$lc,\"INVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression ) )
            // AADL.g:528:7: lc= '{' expression vl= '|' involvingCondition '}'
            {
            lc=(Token)input.LT(1);
            match(input,103,FOLLOW_103_in_involvingExpression1703); 
            stream_103.add(lc);

            pushFollow(FOLLOW_expression_in_involvingExpression1705);
            expression112=expression();
            _fsp--;

            stream_expression.add(expression112.getTree());
            vl=(Token)input.LT(1);
            match(input,VBAR,FOLLOW_VBAR_in_involvingExpression1709); 
            stream_VBAR.add(vl);

            pushFollow(FOLLOW_involvingCondition_in_involvingExpression1711);
            involvingCondition113=involvingCondition();
            _fsp--;

            stream_involvingCondition.add(involvingCondition113.getTree());
            char_literal114=(Token)input.LT(1);
            match(input,104,FOLLOW_104_in_involvingExpression1713); 
            stream_104.add(char_literal114);


            // AST REWRITE
            // elements: involvingCondition, expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 529:7: -> ^( INVOLV[$lc,\"INVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression )
            {
                // AADL.g:529:10: ^( INVOLV[$lc,\"INVOLV\"] ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition ) expression )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(INVOLV, lc, "INVOLV"), root_1);

                // AADL.g:529:33: ^( INVOLV_COND[$vl,\"INVOLV_COND\"] involvingCondition )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(INVOLV_COND, vl, "INVOLV_COND"), root_2);

                adaptor.addChild(root_2, stream_involvingCondition.next());

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_expression.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end involvingExpression

    public static class involvingCondition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start involvingCondition
    // AADL.g:536:1: involvingCondition : filterExpression ( ',' filterExpression )* ;
    public final involvingCondition_return involvingCondition() throws RecognitionException {
        involvingCondition_return retval = new involvingCondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal116=null;
        filterExpression_return filterExpression115 = null;

        filterExpression_return filterExpression117 = null;


        CommonTree char_literal116_tree=null;

        try {
            // AADL.g:537:5: ( filterExpression ( ',' filterExpression )* )
            // AADL.g:537:7: filterExpression ( ',' filterExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_filterExpression_in_involvingCondition1754);
            filterExpression115=filterExpression();
            _fsp--;

            adaptor.addChild(root_0, filterExpression115.getTree());
            // AADL.g:537:24: ( ',' filterExpression )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==107) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // AADL.g:537:26: ',' filterExpression
            	    {
            	    char_literal116=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_involvingCondition1758); 
            	    pushFollow(FOLLOW_filterExpression_in_involvingCondition1761);
            	    filterExpression117=filterExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, filterExpression117.getTree());

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end involvingCondition

    public static class filterExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start filterExpression
    // AADL.g:540:1: filterExpression : ( listExpression | aliasExpression | conditionalOrExpression | involvingBlock );
    public final filterExpression_return filterExpression() throws RecognitionException {
        filterExpression_return retval = new filterExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        listExpression_return listExpression118 = null;

        aliasExpression_return aliasExpression119 = null;

        conditionalOrExpression_return conditionalOrExpression120 = null;

        involvingBlock_return involvingBlock121 = null;



        try {
            // AADL.g:541:5: ( listExpression | aliasExpression | conditionalOrExpression | involvingBlock )
            int alt33=4;
            switch ( input.LA(1) ) {
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case NamedOperator:
                case PLUS:
                case MINUS:
                case STAR:
                case SLASH:
                case LESS:
                case GREATER:
                case PERCENT:
                case EQUAL:
                case NOTEQUAL:
                case LESSEQUAL:
                case GREATEREQUAL:
                case SCOPE:
                case 104:
                case 105:
                case 107:
                case 109:
                case 110:
                case 111:
                case 112:
                case 114:
                    {
                    alt33=3;
                    }
                    break;
                case ITERATOR:
                    {
                    alt33=1;
                    }
                    break;
                case COLON:
                case ASSIGN:
                    {
                    alt33=2;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("540:1: filterExpression : ( listExpression | aliasExpression | conditionalOrExpression | involvingBlock );", 33, 1, input);

                    throw nvae;
                }

                }
                break;
            case JAVA_ACTION:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case PLUS:
            case MINUS:
            case LESS:
            case NOT:
            case HAT:
            case BAR:
            case 105:
            case 113:
            case 114:
            case 116:
            case 120:
            case 121:
            case 122:
            case 123:
                {
                alt33=3;
                }
                break;
            case 103:
                {
                alt33=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("540:1: filterExpression : ( listExpression | aliasExpression | conditionalOrExpression | involvingBlock );", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // AADL.g:541:7: listExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_listExpression_in_filterExpression1781);
                    listExpression118=listExpression();
                    _fsp--;

                    adaptor.addChild(root_0, listExpression118.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:541:24: aliasExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_aliasExpression_in_filterExpression1785);
                    aliasExpression119=aliasExpression();
                    _fsp--;

                    adaptor.addChild(root_0, aliasExpression119.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:541:42: conditionalOrExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_conditionalOrExpression_in_filterExpression1789);
                    conditionalOrExpression120=conditionalOrExpression();
                    _fsp--;

                    adaptor.addChild(root_0, conditionalOrExpression120.getTree());

                    }
                    break;
                case 4 :
                    // AADL.g:541:68: involvingBlock
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_involvingBlock_in_filterExpression1793);
                    involvingBlock121=involvingBlock();
                    _fsp--;

                    adaptor.addChild(root_0, involvingBlock121.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end filterExpression

    public static class listExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start listExpression
    // AADL.g:544:1: listExpression : vn= Identifier it= '<-' ( fileDeclarator -> ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator ) | cn= Identifier -> ^( ITERATE[$it,\"ITERATE\"] $vn $cn) | arrayLiteral -> ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral ) | methodCall -> ^( ITERATE[$it,\"ITERATE\"] $vn methodCall ) ) ;
    public final listExpression_return listExpression() throws RecognitionException {
        listExpression_return retval = new listExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token vn=null;
        Token it=null;
        Token cn=null;
        fileDeclarator_return fileDeclarator122 = null;

        arrayLiteral_return arrayLiteral123 = null;

        methodCall_return methodCall124 = null;


        CommonTree vn_tree=null;
        CommonTree it_tree=null;
        CommonTree cn_tree=null;
        RewriteRuleTokenStream stream_ITERATOR=new RewriteRuleTokenStream(adaptor,"token ITERATOR");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_arrayLiteral=new RewriteRuleSubtreeStream(adaptor,"rule arrayLiteral");
        RewriteRuleSubtreeStream stream_methodCall=new RewriteRuleSubtreeStream(adaptor,"rule methodCall");
        RewriteRuleSubtreeStream stream_fileDeclarator=new RewriteRuleSubtreeStream(adaptor,"rule fileDeclarator");
        try {
            // AADL.g:545:5: (vn= Identifier it= '<-' ( fileDeclarator -> ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator ) | cn= Identifier -> ^( ITERATE[$it,\"ITERATE\"] $vn $cn) | arrayLiteral -> ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral ) | methodCall -> ^( ITERATE[$it,\"ITERATE\"] $vn methodCall ) ) )
            // AADL.g:545:7: vn= Identifier it= '<-' ( fileDeclarator -> ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator ) | cn= Identifier -> ^( ITERATE[$it,\"ITERATE\"] $vn $cn) | arrayLiteral -> ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral ) | methodCall -> ^( ITERATE[$it,\"ITERATE\"] $vn methodCall ) )
            {
            vn=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_listExpression1812); 
            stream_Identifier.add(vn);

            it=(Token)input.LT(1);
            match(input,ITERATOR,FOLLOW_ITERATOR_in_listExpression1816); 
            stream_ITERATOR.add(it);

            // AADL.g:546:10: ( fileDeclarator -> ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator ) | cn= Identifier -> ^( ITERATE[$it,\"ITERATE\"] $vn $cn) | arrayLiteral -> ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral ) | methodCall -> ^( ITERATE[$it,\"ITERATE\"] $vn methodCall ) )
            int alt34=4;
            switch ( input.LA(1) ) {
            case 117:
            case 118:
            case 119:
                {
                alt34=1;
                }
                break;
            case Identifier:
                {
                int LA34_2 = input.LA(2);

                if ( (LA34_2==SCOPE||LA34_2==105||LA34_2==112||LA34_2==114) ) {
                    alt34=4;
                }
                else if ( (LA34_2==104||LA34_2==107) ) {
                    alt34=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("546:10: ( fileDeclarator -> ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator ) | cn= Identifier -> ^( ITERATE[$it,\"ITERATE\"] $vn $cn) | arrayLiteral -> ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral ) | methodCall -> ^( ITERATE[$it,\"ITERATE\"] $vn methodCall ) )", 34, 2, input);

                    throw nvae;
                }
                }
                break;
            case 114:
                {
                alt34=3;
                }
                break;
            case 113:
            case 116:
                {
                alt34=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("546:10: ( fileDeclarator -> ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator ) | cn= Identifier -> ^( ITERATE[$it,\"ITERATE\"] $vn $cn) | arrayLiteral -> ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral ) | methodCall -> ^( ITERATE[$it,\"ITERATE\"] $vn methodCall ) )", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // AADL.g:547:11: fileDeclarator
                    {
                    pushFollow(FOLLOW_fileDeclarator_in_listExpression1839);
                    fileDeclarator122=fileDeclarator();
                    _fsp--;

                    stream_fileDeclarator.add(fileDeclarator122.getTree());

                    // AST REWRITE
                    // elements: vn, fileDeclarator
                    // token labels: vn
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_vn=new RewriteRuleTokenStream(adaptor,"token vn",vn);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 547:27: -> ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator )
                    {
                        // AADL.g:547:30: ^( ITERATE[$it,\"ITERATE\"] $vn fileDeclarator )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(ITERATE, it, "ITERATE"), root_1);

                        adaptor.addChild(root_1, stream_vn.next());
                        adaptor.addChild(root_1, stream_fileDeclarator.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 2 :
                    // AADL.g:548:12: cn= Identifier
                    {
                    cn=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_listExpression1867); 
                    stream_Identifier.add(cn);


                    // AST REWRITE
                    // elements: cn, vn
                    // token labels: cn, vn
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_cn=new RewriteRuleTokenStream(adaptor,"token cn",cn);
                    RewriteRuleTokenStream stream_vn=new RewriteRuleTokenStream(adaptor,"token vn",vn);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 548:27: -> ^( ITERATE[$it,\"ITERATE\"] $vn $cn)
                    {
                        // AADL.g:548:30: ^( ITERATE[$it,\"ITERATE\"] $vn $cn)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(ITERATE, it, "ITERATE"), root_1);

                        adaptor.addChild(root_1, stream_vn.next());
                        adaptor.addChild(root_1, stream_cn.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 3 :
                    // AADL.g:549:12: arrayLiteral
                    {
                    pushFollow(FOLLOW_arrayLiteral_in_listExpression1894);
                    arrayLiteral123=arrayLiteral();
                    _fsp--;

                    stream_arrayLiteral.add(arrayLiteral123.getTree());

                    // AST REWRITE
                    // elements: arrayLiteral, vn
                    // token labels: vn
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_vn=new RewriteRuleTokenStream(adaptor,"token vn",vn);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 549:26: -> ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral )
                    {
                        // AADL.g:549:29: ^( ITERATE[$it,\"ITERATE\"] $vn arrayLiteral )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(ITERATE, it, "ITERATE"), root_1);

                        adaptor.addChild(root_1, stream_vn.next());
                        adaptor.addChild(root_1, stream_arrayLiteral.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 4 :
                    // AADL.g:550:12: methodCall
                    {
                    pushFollow(FOLLOW_methodCall_in_listExpression1920);
                    methodCall124=methodCall();
                    _fsp--;

                    stream_methodCall.add(methodCall124.getTree());

                    // AST REWRITE
                    // elements: methodCall, vn
                    // token labels: vn
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_vn=new RewriteRuleTokenStream(adaptor,"token vn",vn);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 550:24: -> ^( ITERATE[$it,\"ITERATE\"] $vn methodCall )
                    {
                        // AADL.g:550:27: ^( ITERATE[$it,\"ITERATE\"] $vn methodCall )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(ITERATE, it, "ITERATE"), root_1);

                        adaptor.addChild(root_1, stream_vn.next());
                        adaptor.addChild(root_1, stream_methodCall.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end listExpression

    public static class involvingBlock_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start involvingBlock
    // AADL.g:555:1: involvingBlock : lc= '{' ( statement )* '}' -> ^( INVOLV_BLOCK[$lc, \"INVOLV_BLOCK\"] ( statement )* ) ;
    public final involvingBlock_return involvingBlock() throws RecognitionException {
        involvingBlock_return retval = new involvingBlock_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token lc=null;
        Token char_literal126=null;
        statement_return statement125 = null;


        CommonTree lc_tree=null;
        CommonTree char_literal126_tree=null;
        RewriteRuleTokenStream stream_104=new RewriteRuleTokenStream(adaptor,"token 104");
        RewriteRuleTokenStream stream_103=new RewriteRuleTokenStream(adaptor,"token 103");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // AADL.g:556:5: (lc= '{' ( statement )* '}' -> ^( INVOLV_BLOCK[$lc, \"INVOLV_BLOCK\"] ( statement )* ) )
            // AADL.g:556:7: lc= '{' ( statement )* '}'
            {
            lc=(Token)input.LT(1);
            match(input,103,FOLLOW_103_in_involvingBlock1973); 
            stream_103.add(lc);

            // AADL.g:556:14: ( statement )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==JAVA_ACTION||LA35_0==Identifier||LA35_0==CONST||(LA35_0>=BREAK && LA35_0<=SIF)||(LA35_0>=HexLiteral && LA35_0<=CMDARG)||(LA35_0>=PLUS && LA35_0<=MINUS)||LA35_0==LESS||(LA35_0>=NOT && LA35_0<=BAR)||(LA35_0>=102 && LA35_0<=103)||LA35_0==105||LA35_0==108||(LA35_0>=113 && LA35_0<=114)||LA35_0==116||(LA35_0>=120 && LA35_0<=123)) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // AADL.g:556:14: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_involvingBlock1975);
            	    statement125=statement();
            	    _fsp--;

            	    stream_statement.add(statement125.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

            char_literal126=(Token)input.LT(1);
            match(input,104,FOLLOW_104_in_involvingBlock1978); 
            stream_104.add(char_literal126);


            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 556:29: -> ^( INVOLV_BLOCK[$lc, \"INVOLV_BLOCK\"] ( statement )* )
            {
                // AADL.g:556:32: ^( INVOLV_BLOCK[$lc, \"INVOLV_BLOCK\"] ( statement )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(INVOLV_BLOCK, lc,  "INVOLV_BLOCK"), root_1);

                // AADL.g:556:68: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.next());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end involvingBlock

    public static class aliasExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start aliasExpression
    // AADL.g:559:1: aliasExpression : Identifier ( ':' type )? op= '=' conditionalOrExpression -> ^( ALIAS[$op,\"ALIAS\"] Identifier ( type )? conditionalOrExpression ) ;
    public final aliasExpression_return aliasExpression() throws RecognitionException {
        aliasExpression_return retval = new aliasExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token op=null;
        Token Identifier127=null;
        Token char_literal128=null;
        type_return type129 = null;

        conditionalOrExpression_return conditionalOrExpression130 = null;


        CommonTree op_tree=null;
        CommonTree Identifier127_tree=null;
        CommonTree char_literal128_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_conditionalOrExpression=new RewriteRuleSubtreeStream(adaptor,"rule conditionalOrExpression");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            // AADL.g:560:5: ( Identifier ( ':' type )? op= '=' conditionalOrExpression -> ^( ALIAS[$op,\"ALIAS\"] Identifier ( type )? conditionalOrExpression ) )
            // AADL.g:560:7: Identifier ( ':' type )? op= '=' conditionalOrExpression
            {
            Identifier127=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_aliasExpression2005); 
            stream_Identifier.add(Identifier127);

            // AADL.g:560:18: ( ':' type )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==COLON) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // AADL.g:560:19: ':' type
                    {
                    char_literal128=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_aliasExpression2008); 
                    stream_COLON.add(char_literal128);

                    pushFollow(FOLLOW_type_in_aliasExpression2010);
                    type129=type();
                    _fsp--;

                    stream_type.add(type129.getTree());

                    }
                    break;

            }

            op=(Token)input.LT(1);
            match(input,ASSIGN,FOLLOW_ASSIGN_in_aliasExpression2016); 
            stream_ASSIGN.add(op);

            pushFollow(FOLLOW_conditionalOrExpression_in_aliasExpression2018);
            conditionalOrExpression130=conditionalOrExpression();
            _fsp--;

            stream_conditionalOrExpression.add(conditionalOrExpression130.getTree());

            // AST REWRITE
            // elements: conditionalOrExpression, Identifier, type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 560:61: -> ^( ALIAS[$op,\"ALIAS\"] Identifier ( type )? conditionalOrExpression )
            {
                // AADL.g:560:64: ^( ALIAS[$op,\"ALIAS\"] Identifier ( type )? conditionalOrExpression )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(ALIAS, op, "ALIAS"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                // AADL.g:560:96: ( type )?
                if ( stream_type.hasNext() ) {
                    adaptor.addChild(root_1, stream_type.next());

                }
                stream_type.reset();
                adaptor.addChild(root_1, stream_conditionalOrExpression.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end aliasExpression

    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression
    // AADL.g:563:1: expression : conditionalOrExpression ;
    public final expression_return expression() throws RecognitionException {
        expression_return retval = new expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        conditionalOrExpression_return conditionalOrExpression131 = null;



        try {
            // AADL.g:564:5: ( conditionalOrExpression )
            // AADL.g:564:7: conditionalOrExpression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_expression2049);
            conditionalOrExpression131=conditionalOrExpression();
            _fsp--;

            adaptor.addChild(root_0, conditionalOrExpression131.getTree());

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionalOrExpression
    // AADL.g:574:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        conditionalOrExpression_return retval = new conditionalOrExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal133=null;
        conditionalAndExpression_return conditionalAndExpression132 = null;

        conditionalAndExpression_return conditionalAndExpression134 = null;


        CommonTree string_literal133_tree=null;

        try {
            // AADL.g:575:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // AADL.g:575:7: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression2070);
            conditionalAndExpression132=conditionalAndExpression();
            _fsp--;

            adaptor.addChild(root_0, conditionalAndExpression132.getTree());
            // AADL.g:575:32: ( '||' conditionalAndExpression )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==109) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // AADL.g:575:34: '||' conditionalAndExpression
            	    {
            	    string_literal133=(Token)input.LT(1);
            	    match(input,109,FOLLOW_109_in_conditionalOrExpression2074); 
            	    string_literal133_tree = (CommonTree)adaptor.create(string_literal133);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal133_tree, root_0);

            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression2077);
            	    conditionalAndExpression134=conditionalAndExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, conditionalAndExpression134.getTree());

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end conditionalOrExpression

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionalAndExpression
    // AADL.g:578:1: conditionalAndExpression : equalityExpression ( '&&' equalityExpression )* ;
    public final conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        conditionalAndExpression_return retval = new conditionalAndExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal136=null;
        equalityExpression_return equalityExpression135 = null;

        equalityExpression_return equalityExpression137 = null;


        CommonTree string_literal136_tree=null;

        try {
            // AADL.g:579:5: ( equalityExpression ( '&&' equalityExpression )* )
            // AADL.g:579:7: equalityExpression ( '&&' equalityExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_equalityExpression_in_conditionalAndExpression2097);
            equalityExpression135=equalityExpression();
            _fsp--;

            adaptor.addChild(root_0, equalityExpression135.getTree());
            // AADL.g:579:26: ( '&&' equalityExpression )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==110) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // AADL.g:579:28: '&&' equalityExpression
            	    {
            	    string_literal136=(Token)input.LT(1);
            	    match(input,110,FOLLOW_110_in_conditionalAndExpression2101); 
            	    string_literal136_tree = (CommonTree)adaptor.create(string_literal136);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal136_tree, root_0);

            	    pushFollow(FOLLOW_equalityExpression_in_conditionalAndExpression2104);
            	    equalityExpression137=equalityExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, equalityExpression137.getTree());

            	    }
            	    break;

            	default :
            	    break loop38;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end conditionalAndExpression

    public static class equalityExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start equalityExpression
    // AADL.g:582:1: equalityExpression : comparativeExpression ( ( '==' | '!=' ) comparativeExpression )* ;
    public final equalityExpression_return equalityExpression() throws RecognitionException {
        equalityExpression_return retval = new equalityExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set139=null;
        comparativeExpression_return comparativeExpression138 = null;

        comparativeExpression_return comparativeExpression140 = null;


        CommonTree set139_tree=null;

        try {
            // AADL.g:583:5: ( comparativeExpression ( ( '==' | '!=' ) comparativeExpression )* )
            // AADL.g:583:7: comparativeExpression ( ( '==' | '!=' ) comparativeExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_comparativeExpression_in_equalityExpression2124);
            comparativeExpression138=comparativeExpression();
            _fsp--;

            adaptor.addChild(root_0, comparativeExpression138.getTree());
            // AADL.g:583:29: ( ( '==' | '!=' ) comparativeExpression )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( ((LA39_0>=EQUAL && LA39_0<=NOTEQUAL)) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // AADL.g:583:31: ( '==' | '!=' ) comparativeExpression
            	    {
            	    set139=(Token)input.LT(1);
            	    if ( (input.LA(1)>=EQUAL && input.LA(1)<=NOTEQUAL) ) {
            	        input.consume();
            	        root_0 = (CommonTree)adaptor.becomeRoot(adaptor.create(set139), root_0);
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_equalityExpression2128);    throw mse;
            	    }

            	    pushFollow(FOLLOW_comparativeExpression_in_equalityExpression2137);
            	    comparativeExpression140=comparativeExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, comparativeExpression140.getTree());

            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end equalityExpression

    public static class comparativeExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start comparativeExpression
    // AADL.g:586:1: comparativeExpression : namedOperationExpression ( ( '<=' | '>=' | '<' | '>' ) namedOperationExpression )* ;
    public final comparativeExpression_return comparativeExpression() throws RecognitionException {
        comparativeExpression_return retval = new comparativeExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set142=null;
        namedOperationExpression_return namedOperationExpression141 = null;

        namedOperationExpression_return namedOperationExpression143 = null;


        CommonTree set142_tree=null;

        try {
            // AADL.g:587:5: ( namedOperationExpression ( ( '<=' | '>=' | '<' | '>' ) namedOperationExpression )* )
            // AADL.g:587:7: namedOperationExpression ( ( '<=' | '>=' | '<' | '>' ) namedOperationExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_namedOperationExpression_in_comparativeExpression2157);
            namedOperationExpression141=namedOperationExpression();
            _fsp--;

            adaptor.addChild(root_0, namedOperationExpression141.getTree());
            // AADL.g:587:32: ( ( '<=' | '>=' | '<' | '>' ) namedOperationExpression )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( ((LA40_0>=LESS && LA40_0<=GREATER)||(LA40_0>=LESSEQUAL && LA40_0<=GREATEREQUAL)) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // AADL.g:587:34: ( '<=' | '>=' | '<' | '>' ) namedOperationExpression
            	    {
            	    set142=(Token)input.LT(1);
            	    if ( (input.LA(1)>=LESS && input.LA(1)<=GREATER)||(input.LA(1)>=LESSEQUAL && input.LA(1)<=GREATEREQUAL) ) {
            	        input.consume();
            	        root_0 = (CommonTree)adaptor.becomeRoot(adaptor.create(set142), root_0);
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_comparativeExpression2161);    throw mse;
            	    }

            	    pushFollow(FOLLOW_namedOperationExpression_in_comparativeExpression2178);
            	    namedOperationExpression143=namedOperationExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, namedOperationExpression143.getTree());

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end comparativeExpression

    public static class namedOperationExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start namedOperationExpression
    // AADL.g:590:1: namedOperationExpression : additiveExpression ( NamedOperator additiveExpression )* ;
    public final namedOperationExpression_return namedOperationExpression() throws RecognitionException {
        namedOperationExpression_return retval = new namedOperationExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token NamedOperator145=null;
        additiveExpression_return additiveExpression144 = null;

        additiveExpression_return additiveExpression146 = null;


        CommonTree NamedOperator145_tree=null;

        try {
            // AADL.g:591:5: ( additiveExpression ( NamedOperator additiveExpression )* )
            // AADL.g:591:7: additiveExpression ( NamedOperator additiveExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_namedOperationExpression2198);
            additiveExpression144=additiveExpression();
            _fsp--;

            adaptor.addChild(root_0, additiveExpression144.getTree());
            // AADL.g:591:26: ( NamedOperator additiveExpression )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==NamedOperator) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // AADL.g:591:28: NamedOperator additiveExpression
            	    {
            	    NamedOperator145=(Token)input.LT(1);
            	    match(input,NamedOperator,FOLLOW_NamedOperator_in_namedOperationExpression2202); 
            	    NamedOperator145_tree = (CommonTree)adaptor.create(NamedOperator145);
            	    root_0 = (CommonTree)adaptor.becomeRoot(NamedOperator145_tree, root_0);

            	    pushFollow(FOLLOW_additiveExpression_in_namedOperationExpression2205);
            	    additiveExpression146=additiveExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, additiveExpression146.getTree());

            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end namedOperationExpression

    public static class additiveExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start additiveExpression
    // AADL.g:594:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final additiveExpression_return additiveExpression() throws RecognitionException {
        additiveExpression_return retval = new additiveExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set148=null;
        multiplicativeExpression_return multiplicativeExpression147 = null;

        multiplicativeExpression_return multiplicativeExpression149 = null;


        CommonTree set148_tree=null;

        try {
            // AADL.g:595:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // AADL.g:595:7: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2225);
            multiplicativeExpression147=multiplicativeExpression();
            _fsp--;

            adaptor.addChild(root_0, multiplicativeExpression147.getTree());
            // AADL.g:595:32: ( ( '+' | '-' ) multiplicativeExpression )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( ((LA42_0>=PLUS && LA42_0<=MINUS)) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // AADL.g:595:34: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set148=(Token)input.LT(1);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=MINUS) ) {
            	        input.consume();
            	        root_0 = (CommonTree)adaptor.becomeRoot(adaptor.create(set148), root_0);
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_additiveExpression2229);    throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2238);
            	    multiplicativeExpression149=multiplicativeExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, multiplicativeExpression149.getTree());

            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end additiveExpression

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start multiplicativeExpression
    // AADL.g:598:1: multiplicativeExpression : simpleExpression ( ( '*' | '/' | '%' | '@' ) simpleExpression )* ;
    public final multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        multiplicativeExpression_return retval = new multiplicativeExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set151=null;
        simpleExpression_return simpleExpression150 = null;

        simpleExpression_return simpleExpression152 = null;


        CommonTree set151_tree=null;

        try {
            // AADL.g:599:5: ( simpleExpression ( ( '*' | '/' | '%' | '@' ) simpleExpression )* )
            // AADL.g:599:7: simpleExpression ( ( '*' | '/' | '%' | '@' ) simpleExpression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_simpleExpression_in_multiplicativeExpression2258);
            simpleExpression150=simpleExpression();
            _fsp--;

            adaptor.addChild(root_0, simpleExpression150.getTree());
            // AADL.g:599:24: ( ( '*' | '/' | '%' | '@' ) simpleExpression )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( ((LA43_0>=STAR && LA43_0<=SLASH)||LA43_0==PERCENT||LA43_0==111) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // AADL.g:599:26: ( '*' | '/' | '%' | '@' ) simpleExpression
            	    {
            	    set151=(Token)input.LT(1);
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT||input.LA(1)==111 ) {
            	        input.consume();
            	        root_0 = (CommonTree)adaptor.becomeRoot(adaptor.create(set151), root_0);
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_multiplicativeExpression2262);    throw mse;
            	    }

            	    pushFollow(FOLLOW_simpleExpression_in_multiplicativeExpression2281);
            	    simpleExpression152=simpleExpression();
            	    _fsp--;

            	    adaptor.addChild(root_0, simpleExpression152.getTree());

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end multiplicativeExpression

    public static class simpleExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start simpleExpression
    // AADL.g:602:1: simpleExpression : ( '+' simpleExpression | '-' simpleExpression | simpleExpressionNotPlusMinus );
    public final simpleExpression_return simpleExpression() throws RecognitionException {
        simpleExpression_return retval = new simpleExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal153=null;
        Token char_literal155=null;
        simpleExpression_return simpleExpression154 = null;

        simpleExpression_return simpleExpression156 = null;

        simpleExpressionNotPlusMinus_return simpleExpressionNotPlusMinus157 = null;


        CommonTree char_literal153_tree=null;
        CommonTree char_literal155_tree=null;

        try {
            // AADL.g:603:5: ( '+' simpleExpression | '-' simpleExpression | simpleExpressionNotPlusMinus )
            int alt44=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt44=1;
                }
                break;
            case MINUS:
                {
                alt44=2;
                }
                break;
            case JAVA_ACTION:
            case Identifier:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case LESS:
            case NOT:
            case HAT:
            case BAR:
            case 105:
            case 113:
            case 114:
            case 116:
            case 120:
            case 121:
            case 122:
            case 123:
                {
                alt44=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("602:1: simpleExpression : ( '+' simpleExpression | '-' simpleExpression | simpleExpressionNotPlusMinus );", 44, 0, input);

                throw nvae;
            }

            switch (alt44) {
                case 1 :
                    // AADL.g:603:7: '+' simpleExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal153=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_simpleExpression2301); 
                    char_literal153_tree = (CommonTree)adaptor.create(char_literal153);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal153_tree, root_0);

                    pushFollow(FOLLOW_simpleExpression_in_simpleExpression2304);
                    simpleExpression154=simpleExpression();
                    _fsp--;

                    adaptor.addChild(root_0, simpleExpression154.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:604:7: '-' simpleExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal155=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_simpleExpression2312); 
                    char_literal155_tree = (CommonTree)adaptor.create(char_literal155);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal155_tree, root_0);

                    pushFollow(FOLLOW_simpleExpression_in_simpleExpression2315);
                    simpleExpression156=simpleExpression();
                    _fsp--;

                    adaptor.addChild(root_0, simpleExpression156.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:605:7: simpleExpressionNotPlusMinus
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_simpleExpressionNotPlusMinus_in_simpleExpression2323);
                    simpleExpressionNotPlusMinus157=simpleExpressionNotPlusMinus();
                    _fsp--;

                    adaptor.addChild(root_0, simpleExpressionNotPlusMinus157.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end simpleExpression

    public static class simpleExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start simpleExpressionNotPlusMinus
    // AADL.g:608:1: simpleExpressionNotPlusMinus : ( '~' simpleExpression | '!' simpleExpression | '^' simpleExpression | postfixExpression );
    public final simpleExpressionNotPlusMinus_return simpleExpressionNotPlusMinus() throws RecognitionException {
        simpleExpressionNotPlusMinus_return retval = new simpleExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal158=null;
        Token char_literal160=null;
        Token char_literal162=null;
        simpleExpression_return simpleExpression159 = null;

        simpleExpression_return simpleExpression161 = null;

        simpleExpression_return simpleExpression163 = null;

        postfixExpression_return postfixExpression164 = null;


        CommonTree char_literal158_tree=null;
        CommonTree char_literal160_tree=null;
        CommonTree char_literal162_tree=null;

        try {
            // AADL.g:609:5: ( '~' simpleExpression | '!' simpleExpression | '^' simpleExpression | postfixExpression )
            int alt45=4;
            switch ( input.LA(1) ) {
            case BAR:
                {
                alt45=1;
                }
                break;
            case NOT:
                {
                alt45=2;
                }
                break;
            case HAT:
                {
                alt45=3;
                }
                break;
            case JAVA_ACTION:
            case Identifier:
            case HexLiteral:
            case OctalLiteral:
            case IntegerLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case CMDARG:
            case LESS:
            case 105:
            case 113:
            case 114:
            case 116:
            case 120:
            case 121:
            case 122:
            case 123:
                {
                alt45=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("608:1: simpleExpressionNotPlusMinus : ( '~' simpleExpression | '!' simpleExpression | '^' simpleExpression | postfixExpression );", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // AADL.g:609:7: '~' simpleExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal158=(Token)input.LT(1);
                    match(input,BAR,FOLLOW_BAR_in_simpleExpressionNotPlusMinus2340); 
                    char_literal158_tree = (CommonTree)adaptor.create(char_literal158);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal158_tree, root_0);

                    pushFollow(FOLLOW_simpleExpression_in_simpleExpressionNotPlusMinus2343);
                    simpleExpression159=simpleExpression();
                    _fsp--;

                    adaptor.addChild(root_0, simpleExpression159.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:610:7: '!' simpleExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal160=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simpleExpressionNotPlusMinus2351); 
                    char_literal160_tree = (CommonTree)adaptor.create(char_literal160);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal160_tree, root_0);

                    pushFollow(FOLLOW_simpleExpression_in_simpleExpressionNotPlusMinus2354);
                    simpleExpression161=simpleExpression();
                    _fsp--;

                    adaptor.addChild(root_0, simpleExpression161.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:611:7: '^' simpleExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal162=(Token)input.LT(1);
                    match(input,HAT,FOLLOW_HAT_in_simpleExpressionNotPlusMinus2362); 
                    char_literal162_tree = (CommonTree)adaptor.create(char_literal162);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal162_tree, root_0);

                    pushFollow(FOLLOW_simpleExpression_in_simpleExpressionNotPlusMinus2365);
                    simpleExpression163=simpleExpression();
                    _fsp--;

                    adaptor.addChild(root_0, simpleExpression163.getTree());

                    }
                    break;
                case 4 :
                    // AADL.g:612:7: postfixExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_postfixExpression_in_simpleExpressionNotPlusMinus2373);
                    postfixExpression164=postfixExpression();
                    _fsp--;

                    adaptor.addChild(root_0, postfixExpression164.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end simpleExpressionNotPlusMinus

    public static class postfixExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start postfixExpression
    // AADL.g:615:1: postfixExpression : ( primary -> primary ) (lc= '.' mn= Identifier ma= callInstanceMethodArguments -> ^( FCALL_IM[$lc,\"FCALL_IM\"] ^( FUNCALL_PATH $postfixExpression) $mn $ma) )* ;
    public final postfixExpression_return postfixExpression() throws RecognitionException {
        postfixExpression_return retval = new postfixExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token lc=null;
        Token mn=null;
        callInstanceMethodArguments_return ma = null;

        primary_return primary165 = null;


        CommonTree lc_tree=null;
        CommonTree mn_tree=null;
        RewriteRuleTokenStream stream_112=new RewriteRuleTokenStream(adaptor,"token 112");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_callInstanceMethodArguments=new RewriteRuleSubtreeStream(adaptor,"rule callInstanceMethodArguments");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // AADL.g:616:5: ( ( primary -> primary ) (lc= '.' mn= Identifier ma= callInstanceMethodArguments -> ^( FCALL_IM[$lc,\"FCALL_IM\"] ^( FUNCALL_PATH $postfixExpression) $mn $ma) )* )
            // AADL.g:616:7: ( primary -> primary ) (lc= '.' mn= Identifier ma= callInstanceMethodArguments -> ^( FCALL_IM[$lc,\"FCALL_IM\"] ^( FUNCALL_PATH $postfixExpression) $mn $ma) )*
            {
            // AADL.g:616:7: ( primary -> primary )
            // AADL.g:616:8: primary
            {
            pushFollow(FOLLOW_primary_in_postfixExpression2391);
            primary165=primary();
            _fsp--;

            stream_primary.add(primary165.getTree());

            // AST REWRITE
            // elements: primary
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 616:16: -> primary
            {
                adaptor.addChild(root_0, stream_primary.next());

            }



            }

            // AADL.g:616:28: (lc= '.' mn= Identifier ma= callInstanceMethodArguments -> ^( FCALL_IM[$lc,\"FCALL_IM\"] ^( FUNCALL_PATH $postfixExpression) $mn $ma) )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( (LA46_0==112) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // AADL.g:617:7: lc= '.' mn= Identifier ma= callInstanceMethodArguments
            	    {
            	    lc=(Token)input.LT(1);
            	    match(input,112,FOLLOW_112_in_postfixExpression2408); 
            	    stream_112.add(lc);

            	    mn=(Token)input.LT(1);
            	    match(input,Identifier,FOLLOW_Identifier_in_postfixExpression2412); 
            	    stream_Identifier.add(mn);

            	    pushFollow(FOLLOW_callInstanceMethodArguments_in_postfixExpression2416);
            	    ma=callInstanceMethodArguments();
            	    _fsp--;

            	    stream_callInstanceMethodArguments.add(ma.getTree());

            	    // AST REWRITE
            	    // elements: mn, ma, postfixExpression
            	    // token labels: mn
            	    // rule labels: retval, ma
            	    // token list labels: 
            	    // rule list labels: 
            	    retval.tree = root_0;
            	    RewriteRuleTokenStream stream_mn=new RewriteRuleTokenStream(adaptor,"token mn",mn);
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_ma=new RewriteRuleSubtreeStream(adaptor,"token ma",ma!=null?ma.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 618:8: -> ^( FCALL_IM[$lc,\"FCALL_IM\"] ^( FUNCALL_PATH $postfixExpression) $mn $ma)
            	    {
            	        // AADL.g:618:11: ^( FCALL_IM[$lc,\"FCALL_IM\"] ^( FUNCALL_PATH $postfixExpression) $mn $ma)
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FCALL_IM, lc, "FCALL_IM"), root_1);

            	        // AADL.g:618:38: ^( FUNCALL_PATH $postfixExpression)
            	        {
            	        CommonTree root_2 = (CommonTree)adaptor.nil();
            	        root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCALL_PATH, "FUNCALL_PATH"), root_2);

            	        adaptor.addChild(root_2, stream_retval.next());

            	        adaptor.addChild(root_1, root_2);
            	        }
            	        adaptor.addChild(root_1, stream_mn.next());
            	        adaptor.addChild(root_1, stream_ma.next());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }



            	    }
            	    break;

            	default :
            	    break loop46;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end postfixExpression

    public static class callInstanceMethodArguments_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start callInstanceMethodArguments
    // AADL.g:622:1: callInstanceMethodArguments : '(' ( methodArguments )? ')' -> ^( FUNCALL_ARGS ( methodArguments )? ) ;
    public final callInstanceMethodArguments_return callInstanceMethodArguments() throws RecognitionException {
        callInstanceMethodArguments_return retval = new callInstanceMethodArguments_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal166=null;
        Token char_literal168=null;
        methodArguments_return methodArguments167 = null;


        CommonTree char_literal166_tree=null;
        CommonTree char_literal168_tree=null;
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleSubtreeStream stream_methodArguments=new RewriteRuleSubtreeStream(adaptor,"rule methodArguments");
        try {
            // AADL.g:623:5: ( '(' ( methodArguments )? ')' -> ^( FUNCALL_ARGS ( methodArguments )? ) )
            // AADL.g:623:7: '(' ( methodArguments )? ')'
            {
            char_literal166=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_callInstanceMethodArguments2468); 
            stream_105.add(char_literal166);

            // AADL.g:623:11: ( methodArguments )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==JAVA_ACTION||LA47_0==Identifier||(LA47_0>=HexLiteral && LA47_0<=CMDARG)||(LA47_0>=PLUS && LA47_0<=MINUS)||LA47_0==LESS||(LA47_0>=NOT && LA47_0<=BAR)||LA47_0==105||(LA47_0>=113 && LA47_0<=114)||LA47_0==116||(LA47_0>=120 && LA47_0<=123)) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // AADL.g:623:11: methodArguments
                    {
                    pushFollow(FOLLOW_methodArguments_in_callInstanceMethodArguments2470);
                    methodArguments167=methodArguments();
                    _fsp--;

                    stream_methodArguments.add(methodArguments167.getTree());

                    }
                    break;

            }

            char_literal168=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_callInstanceMethodArguments2473); 
            stream_106.add(char_literal168);


            // AST REWRITE
            // elements: methodArguments
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 623:32: -> ^( FUNCALL_ARGS ( methodArguments )? )
            {
                // AADL.g:623:35: ^( FUNCALL_ARGS ( methodArguments )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCALL_ARGS, "FUNCALL_ARGS"), root_1);

                // AADL.g:623:50: ( methodArguments )?
                if ( stream_methodArguments.hasNext() ) {
                    adaptor.addChild(root_1, stream_methodArguments.next());

                }
                stream_methodArguments.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end callInstanceMethodArguments

    public static class primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start primary
    // AADL.g:628:1: primary : ( '(' expression ')' | arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | JAVA_ACTION | Identifier | commandArg );
    public final primary_return primary() throws RecognitionException {
        primary_return retval = new primary_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal169=null;
        Token char_literal171=null;
        Token HexLiteral173=null;
        Token OctalLiteral174=null;
        Token IntegerLiteral175=null;
        Token FloatingPointLiteral176=null;
        Token CharacterLiteral179=null;
        Token StringLiteral180=null;
        Token JAVA_ACTION184=null;
        Token Identifier185=null;
        expression_return expression170 = null;

        arrayLiteral_return arrayLiteral172 = null;

        nullLiteral_return nullLiteral177 = null;

        booleanLiteral_return booleanLiteral178 = null;

        exbase_return exbase181 = null;

        dtbase_return dtbase182 = null;

        methodCall_return methodCall183 = null;

        commandArg_return commandArg186 = null;


        CommonTree char_literal169_tree=null;
        CommonTree char_literal171_tree=null;
        CommonTree HexLiteral173_tree=null;
        CommonTree OctalLiteral174_tree=null;
        CommonTree IntegerLiteral175_tree=null;
        CommonTree FloatingPointLiteral176_tree=null;
        CommonTree CharacterLiteral179_tree=null;
        CommonTree StringLiteral180_tree=null;
        CommonTree JAVA_ACTION184_tree=null;
        CommonTree Identifier185_tree=null;

        try {
            // AADL.g:629:5: ( '(' expression ')' | arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | JAVA_ACTION | Identifier | commandArg )
            int alt48=16;
            switch ( input.LA(1) ) {
            case 105:
                {
                alt48=1;
                }
                break;
            case 114:
                {
                alt48=2;
                }
                break;
            case HexLiteral:
                {
                alt48=3;
                }
                break;
            case OctalLiteral:
                {
                alt48=4;
                }
                break;
            case IntegerLiteral:
                {
                alt48=5;
                }
                break;
            case FloatingPointLiteral:
                {
                alt48=6;
                }
                break;
            case 122:
                {
                alt48=7;
                }
                break;
            case 120:
            case 121:
                {
                alt48=8;
                }
                break;
            case CharacterLiteral:
                {
                alt48=9;
                }
                break;
            case StringLiteral:
                {
                alt48=10;
                }
                break;
            case LESS:
                {
                alt48=11;
                }
                break;
            case 123:
                {
                alt48=12;
                }
                break;
            case 113:
            case 116:
                {
                alt48=13;
                }
                break;
            case Identifier:
                {
                switch ( input.LA(2) ) {
                case SCOPE:
                case 105:
                case 114:
                    {
                    alt48=13;
                    }
                    break;
                case 112:
                    {
                    int LA48_17 = input.LA(3);

                    if ( (LA48_17==Identifier) ) {
                        int LA48_19 = input.LA(4);

                        if ( (LA48_19==SCOPE||LA48_19==112) ) {
                            alt48=13;
                        }
                        else if ( (LA48_19==105) ) {
                            alt48=15;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("628:1: primary : ( '(' expression ')' | arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | JAVA_ACTION | Identifier | commandArg );", 48, 19, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("628:1: primary : ( '(' expression ')' | arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | JAVA_ACTION | Identifier | commandArg );", 48, 17, input);

                        throw nvae;
                    }
                    }
                    break;
                case NamedOperator:
                case COLON:
                case PLUS:
                case MINUS:
                case STAR:
                case SLASH:
                case VBAR:
                case LESS:
                case GREATER:
                case PERCENT:
                case EQUAL:
                case NOTEQUAL:
                case LESSEQUAL:
                case GREATEREQUAL:
                case 102:
                case 104:
                case 106:
                case 107:
                case 109:
                case 110:
                case 111:
                case 115:
                case 124:
                    {
                    alt48=15;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("628:1: primary : ( '(' expression ')' | arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | JAVA_ACTION | Identifier | commandArg );", 48, 14, input);

                    throw nvae;
                }

                }
                break;
            case JAVA_ACTION:
                {
                alt48=14;
                }
                break;
            case CMDARG:
                {
                alt48=16;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("628:1: primary : ( '(' expression ')' | arrayLiteral | HexLiteral | OctalLiteral | IntegerLiteral | FloatingPointLiteral | nullLiteral | booleanLiteral | CharacterLiteral | StringLiteral | exbase | dtbase | methodCall | JAVA_ACTION | Identifier | commandArg );", 48, 0, input);

                throw nvae;
            }

            switch (alt48) {
                case 1 :
                    // AADL.g:629:7: '(' expression ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal169=(Token)input.LT(1);
                    match(input,105,FOLLOW_105_in_primary2501); 
                    pushFollow(FOLLOW_expression_in_primary2504);
                    expression170=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression170.getTree());
                    char_literal171=(Token)input.LT(1);
                    match(input,106,FOLLOW_106_in_primary2506); 

                    }
                    break;
                case 2 :
                    // AADL.g:630:7: arrayLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_arrayLiteral_in_primary2516);
                    arrayLiteral172=arrayLiteral();
                    _fsp--;

                    adaptor.addChild(root_0, arrayLiteral172.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:631:7: HexLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    HexLiteral173=(Token)input.LT(1);
                    match(input,HexLiteral,FOLLOW_HexLiteral_in_primary2524); 
                    HexLiteral173_tree = (CommonTree)adaptor.create(HexLiteral173);
                    adaptor.addChild(root_0, HexLiteral173_tree);


                    }
                    break;
                case 4 :
                    // AADL.g:632:7: OctalLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    OctalLiteral174=(Token)input.LT(1);
                    match(input,OctalLiteral,FOLLOW_OctalLiteral_in_primary2532); 
                    OctalLiteral174_tree = (CommonTree)adaptor.create(OctalLiteral174);
                    adaptor.addChild(root_0, OctalLiteral174_tree);


                    }
                    break;
                case 5 :
                    // AADL.g:633:7: IntegerLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    IntegerLiteral175=(Token)input.LT(1);
                    match(input,IntegerLiteral,FOLLOW_IntegerLiteral_in_primary2540); 
                    IntegerLiteral175_tree = (CommonTree)adaptor.create(IntegerLiteral175);
                    adaptor.addChild(root_0, IntegerLiteral175_tree);


                    }
                    break;
                case 6 :
                    // AADL.g:634:7: FloatingPointLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    FloatingPointLiteral176=(Token)input.LT(1);
                    match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_primary2548); 
                    FloatingPointLiteral176_tree = (CommonTree)adaptor.create(FloatingPointLiteral176);
                    adaptor.addChild(root_0, FloatingPointLiteral176_tree);


                    }
                    break;
                case 7 :
                    // AADL.g:635:7: nullLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_nullLiteral_in_primary2556);
                    nullLiteral177=nullLiteral();
                    _fsp--;

                    adaptor.addChild(root_0, nullLiteral177.getTree());

                    }
                    break;
                case 8 :
                    // AADL.g:636:7: booleanLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_booleanLiteral_in_primary2564);
                    booleanLiteral178=booleanLiteral();
                    _fsp--;

                    adaptor.addChild(root_0, booleanLiteral178.getTree());

                    }
                    break;
                case 9 :
                    // AADL.g:637:7: CharacterLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    CharacterLiteral179=(Token)input.LT(1);
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_primary2572); 
                    CharacterLiteral179_tree = (CommonTree)adaptor.create(CharacterLiteral179);
                    adaptor.addChild(root_0, CharacterLiteral179_tree);


                    }
                    break;
                case 10 :
                    // AADL.g:638:7: StringLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    StringLiteral180=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_primary2580); 
                    StringLiteral180_tree = (CommonTree)adaptor.create(StringLiteral180);
                    adaptor.addChild(root_0, StringLiteral180_tree);


                    }
                    break;
                case 11 :
                    // AADL.g:639:7: exbase
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_exbase_in_primary2588);
                    exbase181=exbase();
                    _fsp--;

                    adaptor.addChild(root_0, exbase181.getTree());

                    }
                    break;
                case 12 :
                    // AADL.g:640:7: dtbase
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_dtbase_in_primary2596);
                    dtbase182=dtbase();
                    _fsp--;

                    adaptor.addChild(root_0, dtbase182.getTree());

                    }
                    break;
                case 13 :
                    // AADL.g:641:7: methodCall
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_methodCall_in_primary2604);
                    methodCall183=methodCall();
                    _fsp--;

                    adaptor.addChild(root_0, methodCall183.getTree());

                    }
                    break;
                case 14 :
                    // AADL.g:642:7: JAVA_ACTION
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    JAVA_ACTION184=(Token)input.LT(1);
                    match(input,JAVA_ACTION,FOLLOW_JAVA_ACTION_in_primary2612); 
                    JAVA_ACTION184_tree = (CommonTree)adaptor.create(JAVA_ACTION184);
                    adaptor.addChild(root_0, JAVA_ACTION184_tree);


                    }
                    break;
                case 15 :
                    // AADL.g:643:7: Identifier
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    Identifier185=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_primary2620); 
                    Identifier185_tree = (CommonTree)adaptor.create(Identifier185);
                    adaptor.addChild(root_0, Identifier185_tree);


                    }
                    break;
                case 16 :
                    // AADL.g:644:7: commandArg
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_commandArg_in_primary2628);
                    commandArg186=commandArg();
                    _fsp--;

                    adaptor.addChild(root_0, commandArg186.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end primary

    public static class methodCall_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start methodCall
    // AADL.g:653:1: methodCall : ( callSystemMethod | callRegisteredMethod | callSpecialMethod | callModuleMethod );
    public final methodCall_return methodCall() throws RecognitionException {
        methodCall_return retval = new methodCall_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        callSystemMethod_return callSystemMethod187 = null;

        callRegisteredMethod_return callRegisteredMethod188 = null;

        callSpecialMethod_return callSpecialMethod189 = null;

        callModuleMethod_return callModuleMethod190 = null;



        try {
            // AADL.g:654:5: ( callSystemMethod | callRegisteredMethod | callSpecialMethod | callModuleMethod )
            int alt49=4;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==113||LA49_0==116) ) {
                alt49=1;
            }
            else if ( (LA49_0==Identifier) ) {
                switch ( input.LA(2) ) {
                case 105:
                    {
                    alt49=2;
                    }
                    break;
                case 114:
                    {
                    alt49=3;
                    }
                    break;
                case SCOPE:
                case 112:
                    {
                    alt49=4;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("653:1: methodCall : ( callSystemMethod | callRegisteredMethod | callSpecialMethod | callModuleMethod );", 49, 2, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("653:1: methodCall : ( callSystemMethod | callRegisteredMethod | callSpecialMethod | callModuleMethod );", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // AADL.g:654:7: callSystemMethod
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_callSystemMethod_in_methodCall2648);
                    callSystemMethod187=callSystemMethod();
                    _fsp--;

                    adaptor.addChild(root_0, callSystemMethod187.getTree());

                    }
                    break;
                case 2 :
                    // AADL.g:656:7: callRegisteredMethod
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_callRegisteredMethod_in_methodCall2657);
                    callRegisteredMethod188=callRegisteredMethod();
                    _fsp--;

                    adaptor.addChild(root_0, callRegisteredMethod188.getTree());

                    }
                    break;
                case 3 :
                    // AADL.g:657:7: callSpecialMethod
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_callSpecialMethod_in_methodCall2665);
                    callSpecialMethod189=callSpecialMethod();
                    _fsp--;

                    adaptor.addChild(root_0, callSpecialMethod189.getTree());

                    }
                    break;
                case 4 :
                    // AADL.g:658:7: callModuleMethod
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_callModuleMethod_in_methodCall2673);
                    callModuleMethod190=callModuleMethod();
                    _fsp--;

                    adaptor.addChild(root_0, callModuleMethod190.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end methodCall

    public static class callSystemMethod_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start callSystemMethod
    // AADL.g:662:1: callSystemMethod : (csf= 'cast' '[' type ']' '(' expression ')' -> ^( CAST[$csf] type expression ) | isf= 'typeof' '[' type ']' '(' expression ')' -> ^( INSTANCEOF[$isf] type expression ) );
    public final callSystemMethod_return callSystemMethod() throws RecognitionException {
        callSystemMethod_return retval = new callSystemMethod_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token csf=null;
        Token isf=null;
        Token char_literal191=null;
        Token char_literal193=null;
        Token char_literal194=null;
        Token char_literal196=null;
        Token char_literal197=null;
        Token char_literal199=null;
        Token char_literal200=null;
        Token char_literal202=null;
        type_return type192 = null;

        expression_return expression195 = null;

        type_return type198 = null;

        expression_return expression201 = null;


        CommonTree csf_tree=null;
        CommonTree isf_tree=null;
        CommonTree char_literal191_tree=null;
        CommonTree char_literal193_tree=null;
        CommonTree char_literal194_tree=null;
        CommonTree char_literal196_tree=null;
        CommonTree char_literal197_tree=null;
        CommonTree char_literal199_tree=null;
        CommonTree char_literal200_tree=null;
        CommonTree char_literal202_tree=null;
        RewriteRuleTokenStream stream_116=new RewriteRuleTokenStream(adaptor,"token 116");
        RewriteRuleTokenStream stream_114=new RewriteRuleTokenStream(adaptor,"token 114");
        RewriteRuleTokenStream stream_115=new RewriteRuleTokenStream(adaptor,"token 115");
        RewriteRuleTokenStream stream_113=new RewriteRuleTokenStream(adaptor,"token 113");
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            // AADL.g:663:5: (csf= 'cast' '[' type ']' '(' expression ')' -> ^( CAST[$csf] type expression ) | isf= 'typeof' '[' type ']' '(' expression ')' -> ^( INSTANCEOF[$isf] type expression ) )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==113) ) {
                alt50=1;
            }
            else if ( (LA50_0==116) ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("662:1: callSystemMethod : (csf= 'cast' '[' type ']' '(' expression ')' -> ^( CAST[$csf] type expression ) | isf= 'typeof' '[' type ']' '(' expression ')' -> ^( INSTANCEOF[$isf] type expression ) );", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // AADL.g:663:7: csf= 'cast' '[' type ']' '(' expression ')'
                    {
                    csf=(Token)input.LT(1);
                    match(input,113,FOLLOW_113_in_callSystemMethod2693); 
                    stream_113.add(csf);

                    char_literal191=(Token)input.LT(1);
                    match(input,114,FOLLOW_114_in_callSystemMethod2695); 
                    stream_114.add(char_literal191);

                    pushFollow(FOLLOW_type_in_callSystemMethod2697);
                    type192=type();
                    _fsp--;

                    stream_type.add(type192.getTree());
                    char_literal193=(Token)input.LT(1);
                    match(input,115,FOLLOW_115_in_callSystemMethod2699); 
                    stream_115.add(char_literal193);

                    char_literal194=(Token)input.LT(1);
                    match(input,105,FOLLOW_105_in_callSystemMethod2701); 
                    stream_105.add(char_literal194);

                    pushFollow(FOLLOW_expression_in_callSystemMethod2703);
                    expression195=expression();
                    _fsp--;

                    stream_expression.add(expression195.getTree());
                    char_literal196=(Token)input.LT(1);
                    match(input,106,FOLLOW_106_in_callSystemMethod2705); 
                    stream_106.add(char_literal196);


                    // AST REWRITE
                    // elements: type, expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 664:7: -> ^( CAST[$csf] type expression )
                    {
                        // AADL.g:664:10: ^( CAST[$csf] type expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(CAST, csf), root_1);

                        adaptor.addChild(root_1, stream_type.next());
                        adaptor.addChild(root_1, stream_expression.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 2 :
                    // AADL.g:665:7: isf= 'typeof' '[' type ']' '(' expression ')'
                    {
                    isf=(Token)input.LT(1);
                    match(input,116,FOLLOW_116_in_callSystemMethod2732); 
                    stream_116.add(isf);

                    char_literal197=(Token)input.LT(1);
                    match(input,114,FOLLOW_114_in_callSystemMethod2734); 
                    stream_114.add(char_literal197);

                    pushFollow(FOLLOW_type_in_callSystemMethod2736);
                    type198=type();
                    _fsp--;

                    stream_type.add(type198.getTree());
                    char_literal199=(Token)input.LT(1);
                    match(input,115,FOLLOW_115_in_callSystemMethod2738); 
                    stream_115.add(char_literal199);

                    char_literal200=(Token)input.LT(1);
                    match(input,105,FOLLOW_105_in_callSystemMethod2740); 
                    stream_105.add(char_literal200);

                    pushFollow(FOLLOW_expression_in_callSystemMethod2742);
                    expression201=expression();
                    _fsp--;

                    stream_expression.add(expression201.getTree());
                    char_literal202=(Token)input.LT(1);
                    match(input,106,FOLLOW_106_in_callSystemMethod2744); 
                    stream_106.add(char_literal202);


                    // AST REWRITE
                    // elements: expression, type
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 666:7: -> ^( INSTANCEOF[$isf] type expression )
                    {
                        // AADL.g:666:10: ^( INSTANCEOF[$isf] type expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(INSTANCEOF, isf), root_1);

                        adaptor.addChild(root_1, stream_type.next());
                        adaptor.addChild(root_1, stream_expression.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end callSystemMethod

    public static class callRegisteredMethod_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start callRegisteredMethod
    // AADL.g:677:1: callRegisteredMethod : Identifier '(' ( methodArguments )? ')' -> ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( methodArguments )? ) ) ;
    public final callRegisteredMethod_return callRegisteredMethod() throws RecognitionException {
        callRegisteredMethod_return retval = new callRegisteredMethod_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier203=null;
        Token char_literal204=null;
        Token char_literal206=null;
        methodArguments_return methodArguments205 = null;


        CommonTree Identifier203_tree=null;
        CommonTree char_literal204_tree=null;
        CommonTree char_literal206_tree=null;
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_methodArguments=new RewriteRuleSubtreeStream(adaptor,"rule methodArguments");
        try {
            // AADL.g:678:5: ( Identifier '(' ( methodArguments )? ')' -> ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( methodArguments )? ) ) )
            // AADL.g:678:7: Identifier '(' ( methodArguments )? ')'
            {
            Identifier203=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_callRegisteredMethod2782); 
            stream_Identifier.add(Identifier203);

            char_literal204=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_callRegisteredMethod2784); 
            stream_105.add(char_literal204);

            // AADL.g:678:22: ( methodArguments )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==JAVA_ACTION||LA51_0==Identifier||(LA51_0>=HexLiteral && LA51_0<=CMDARG)||(LA51_0>=PLUS && LA51_0<=MINUS)||LA51_0==LESS||(LA51_0>=NOT && LA51_0<=BAR)||LA51_0==105||(LA51_0>=113 && LA51_0<=114)||LA51_0==116||(LA51_0>=120 && LA51_0<=123)) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // AADL.g:678:22: methodArguments
                    {
                    pushFollow(FOLLOW_methodArguments_in_callRegisteredMethod2786);
                    methodArguments205=methodArguments();
                    _fsp--;

                    stream_methodArguments.add(methodArguments205.getTree());

                    }
                    break;

            }

            char_literal206=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_callRegisteredMethod2789); 
            stream_106.add(char_literal206);


            // AST REWRITE
            // elements: methodArguments, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 679:7: -> ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( methodArguments )? ) )
            {
                // AADL.g:679:10: ^( FCALL_RM Identifier ^( FUNCALL_ARGS ( methodArguments )? ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FCALL_RM, "FCALL_RM"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                // AADL.g:679:32: ^( FUNCALL_ARGS ( methodArguments )? )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCALL_ARGS, "FUNCALL_ARGS"), root_2);

                // AADL.g:679:47: ( methodArguments )?
                if ( stream_methodArguments.hasNext() ) {
                    adaptor.addChild(root_2, stream_methodArguments.next());

                }
                stream_methodArguments.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end callRegisteredMethod

    public static class callSpecialMethod_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start callSpecialMethod
    // AADL.g:682:1: callSpecialMethod : Identifier '[' ( methodArguments )? ']' '(' expression ')' -> ^( FCALL_SM Identifier ^( FUNCALL_PATH expression ) ^( FUNCALL_ARGS ( methodArguments )? ) ) ;
    public final callSpecialMethod_return callSpecialMethod() throws RecognitionException {
        callSpecialMethod_return retval = new callSpecialMethod_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier207=null;
        Token char_literal208=null;
        Token char_literal210=null;
        Token char_literal211=null;
        Token char_literal213=null;
        methodArguments_return methodArguments209 = null;

        expression_return expression212 = null;


        CommonTree Identifier207_tree=null;
        CommonTree char_literal208_tree=null;
        CommonTree char_literal210_tree=null;
        CommonTree char_literal211_tree=null;
        CommonTree char_literal213_tree=null;
        RewriteRuleTokenStream stream_114=new RewriteRuleTokenStream(adaptor,"token 114");
        RewriteRuleTokenStream stream_115=new RewriteRuleTokenStream(adaptor,"token 115");
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_methodArguments=new RewriteRuleSubtreeStream(adaptor,"rule methodArguments");
        try {
            // AADL.g:683:5: ( Identifier '[' ( methodArguments )? ']' '(' expression ')' -> ^( FCALL_SM Identifier ^( FUNCALL_PATH expression ) ^( FUNCALL_ARGS ( methodArguments )? ) ) )
            // AADL.g:683:7: Identifier '[' ( methodArguments )? ']' '(' expression ')'
            {
            Identifier207=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_callSpecialMethod2827); 
            stream_Identifier.add(Identifier207);

            char_literal208=(Token)input.LT(1);
            match(input,114,FOLLOW_114_in_callSpecialMethod2829); 
            stream_114.add(char_literal208);

            // AADL.g:683:22: ( methodArguments )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==JAVA_ACTION||LA52_0==Identifier||(LA52_0>=HexLiteral && LA52_0<=CMDARG)||(LA52_0>=PLUS && LA52_0<=MINUS)||LA52_0==LESS||(LA52_0>=NOT && LA52_0<=BAR)||LA52_0==105||(LA52_0>=113 && LA52_0<=114)||LA52_0==116||(LA52_0>=120 && LA52_0<=123)) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // AADL.g:683:22: methodArguments
                    {
                    pushFollow(FOLLOW_methodArguments_in_callSpecialMethod2831);
                    methodArguments209=methodArguments();
                    _fsp--;

                    stream_methodArguments.add(methodArguments209.getTree());

                    }
                    break;

            }

            char_literal210=(Token)input.LT(1);
            match(input,115,FOLLOW_115_in_callSpecialMethod2834); 
            stream_115.add(char_literal210);

            char_literal211=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_callSpecialMethod2836); 
            stream_105.add(char_literal211);

            pushFollow(FOLLOW_expression_in_callSpecialMethod2838);
            expression212=expression();
            _fsp--;

            stream_expression.add(expression212.getTree());
            char_literal213=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_callSpecialMethod2840); 
            stream_106.add(char_literal213);


            // AST REWRITE
            // elements: methodArguments, expression, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 684:7: -> ^( FCALL_SM Identifier ^( FUNCALL_PATH expression ) ^( FUNCALL_ARGS ( methodArguments )? ) )
            {
                // AADL.g:684:10: ^( FCALL_SM Identifier ^( FUNCALL_PATH expression ) ^( FUNCALL_ARGS ( methodArguments )? ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FCALL_SM, "FCALL_SM"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                // AADL.g:684:32: ^( FUNCALL_PATH expression )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCALL_PATH, "FUNCALL_PATH"), root_2);

                adaptor.addChild(root_2, stream_expression.next());

                adaptor.addChild(root_1, root_2);
                }
                // AADL.g:684:59: ^( FUNCALL_ARGS ( methodArguments )? )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCALL_ARGS, "FUNCALL_ARGS"), root_2);

                // AADL.g:684:74: ( methodArguments )?
                if ( stream_methodArguments.hasNext() ) {
                    adaptor.addChild(root_2, stream_methodArguments.next());

                }
                stream_methodArguments.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end callSpecialMethod

    public static class callModuleMethod_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start callModuleMethod
    // AADL.g:687:1: callModuleMethod : variableTypeName '::' Identifier '(' methodArguments ')' -> ^( FCALL_MM ^( FUNCALL_PATH variableTypeName ) Identifier ^( FUNCALL_ARGS ( methodArguments )? ) ) ;
    public final callModuleMethod_return callModuleMethod() throws RecognitionException {
        callModuleMethod_return retval = new callModuleMethod_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal215=null;
        Token Identifier216=null;
        Token char_literal217=null;
        Token char_literal219=null;
        variableTypeName_return variableTypeName214 = null;

        methodArguments_return methodArguments218 = null;


        CommonTree string_literal215_tree=null;
        CommonTree Identifier216_tree=null;
        CommonTree char_literal217_tree=null;
        CommonTree char_literal219_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_105=new RewriteRuleTokenStream(adaptor,"token 105");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_methodArguments=new RewriteRuleSubtreeStream(adaptor,"rule methodArguments");
        RewriteRuleSubtreeStream stream_variableTypeName=new RewriteRuleSubtreeStream(adaptor,"rule variableTypeName");
        try {
            // AADL.g:688:5: ( variableTypeName '::' Identifier '(' methodArguments ')' -> ^( FCALL_MM ^( FUNCALL_PATH variableTypeName ) Identifier ^( FUNCALL_ARGS ( methodArguments )? ) ) )
            // AADL.g:688:7: variableTypeName '::' Identifier '(' methodArguments ')'
            {
            pushFollow(FOLLOW_variableTypeName_in_callModuleMethod2884);
            variableTypeName214=variableTypeName();
            _fsp--;

            stream_variableTypeName.add(variableTypeName214.getTree());
            string_literal215=(Token)input.LT(1);
            match(input,SCOPE,FOLLOW_SCOPE_in_callModuleMethod2886); 
            stream_SCOPE.add(string_literal215);

            Identifier216=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_callModuleMethod2888); 
            stream_Identifier.add(Identifier216);

            char_literal217=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_callModuleMethod2890); 
            stream_105.add(char_literal217);

            pushFollow(FOLLOW_methodArguments_in_callModuleMethod2892);
            methodArguments218=methodArguments();
            _fsp--;

            stream_methodArguments.add(methodArguments218.getTree());
            char_literal219=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_callModuleMethod2894); 
            stream_106.add(char_literal219);


            // AST REWRITE
            // elements: Identifier, methodArguments, variableTypeName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 689:7: -> ^( FCALL_MM ^( FUNCALL_PATH variableTypeName ) Identifier ^( FUNCALL_ARGS ( methodArguments )? ) )
            {
                // AADL.g:689:10: ^( FCALL_MM ^( FUNCALL_PATH variableTypeName ) Identifier ^( FUNCALL_ARGS ( methodArguments )? ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FCALL_MM, "FCALL_MM"), root_1);

                // AADL.g:689:21: ^( FUNCALL_PATH variableTypeName )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCALL_PATH, "FUNCALL_PATH"), root_2);

                adaptor.addChild(root_2, stream_variableTypeName.next());

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_Identifier.next());
                // AADL.g:689:65: ^( FUNCALL_ARGS ( methodArguments )? )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(adaptor.create(FUNCALL_ARGS, "FUNCALL_ARGS"), root_2);

                // AADL.g:689:80: ( methodArguments )?
                if ( stream_methodArguments.hasNext() ) {
                    adaptor.addChild(root_2, stream_methodArguments.next());

                }
                stream_methodArguments.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end callModuleMethod

    public static class methodArguments_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start methodArguments
    // AADL.g:692:1: methodArguments : expression ( ',' expression )* ;
    public final methodArguments_return methodArguments() throws RecognitionException {
        methodArguments_return retval = new methodArguments_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal221=null;
        expression_return expression220 = null;

        expression_return expression222 = null;


        CommonTree char_literal221_tree=null;

        try {
            // AADL.g:693:5: ( expression ( ',' expression )* )
            // AADL.g:693:7: expression ( ',' expression )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_expression_in_methodArguments2938);
            expression220=expression();
            _fsp--;

            adaptor.addChild(root_0, expression220.getTree());
            // AADL.g:693:18: ( ',' expression )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==107) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // AADL.g:693:19: ',' expression
            	    {
            	    char_literal221=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_methodArguments2941); 
            	    pushFollow(FOLLOW_expression_in_methodArguments2944);
            	    expression222=expression();
            	    _fsp--;

            	    adaptor.addChild(root_0, expression222.getTree());

            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end methodArguments

    public static class fileDeclarator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fileDeclarator
    // AADL.g:698:1: fileDeclarator : ( txtFileDeclarator | csvFileDeclarator | xmlFileDeclarator );
    public final fileDeclarator_return fileDeclarator() throws RecognitionException {
        fileDeclarator_return retval = new fileDeclarator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        txtFileDeclarator_return txtFileDeclarator223 = null;

        csvFileDeclarator_return csvFileDeclarator224 = null;

        xmlFileDeclarator_return xmlFileDeclarator225 = null;



        try {
            // AADL.g:699:5: ( txtFileDeclarator | csvFileDeclarator | xmlFileDeclarator )
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
                    new NoViableAltException("698:1: fileDeclarator : ( txtFileDeclarator | csvFileDeclarator | xmlFileDeclarator );", 54, 0, input);

                throw nvae;
            }

            switch (alt54) {
                case 1 :
                    // AADL.g:699:7: txtFileDeclarator
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_txtFileDeclarator_in_fileDeclarator2965);
                    txtFileDeclarator223=txtFileDeclarator();
                    _fsp--;

                    root_0 = (CommonTree)adaptor.becomeRoot(txtFileDeclarator223.getTree(), root_0);

                    }
                    break;
                case 2 :
                    // AADL.g:699:28: csvFileDeclarator
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_csvFileDeclarator_in_fileDeclarator2970);
                    csvFileDeclarator224=csvFileDeclarator();
                    _fsp--;

                    root_0 = (CommonTree)adaptor.becomeRoot(csvFileDeclarator224.getTree(), root_0);

                    }
                    break;
                case 3 :
                    // AADL.g:699:49: xmlFileDeclarator
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_xmlFileDeclarator_in_fileDeclarator2975);
                    xmlFileDeclarator225=xmlFileDeclarator();
                    _fsp--;

                    root_0 = (CommonTree)adaptor.becomeRoot(xmlFileDeclarator225.getTree(), root_0);

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

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

    public static class txtFileDeclarator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start txtFileDeclarator
    // AADL.g:702:1: txtFileDeclarator : 'txtFile' ( fileDeclaratorOptions )? '(' ( expression ) ( ',' expression )? ')' ;
    public final txtFileDeclarator_return txtFileDeclarator() throws RecognitionException {
        txtFileDeclarator_return retval = new txtFileDeclarator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal226=null;
        Token char_literal228=null;
        Token char_literal230=null;
        Token char_literal232=null;
        fileDeclaratorOptions_return fileDeclaratorOptions227 = null;

        expression_return expression229 = null;

        expression_return expression231 = null;


        CommonTree string_literal226_tree=null;
        CommonTree char_literal228_tree=null;
        CommonTree char_literal230_tree=null;
        CommonTree char_literal232_tree=null;

        try {
            // AADL.g:707:5: ( 'txtFile' ( fileDeclaratorOptions )? '(' ( expression ) ( ',' expression )? ')' )
            // AADL.g:707:7: 'txtFile' ( fileDeclaratorOptions )? '(' ( expression ) ( ',' expression )? ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal226=(Token)input.LT(1);
            match(input,117,FOLLOW_117_in_txtFileDeclarator2996); 
            string_literal226_tree = (CommonTree)adaptor.create(string_literal226);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal226_tree, root_0);

            // AADL.g:707:18: ( fileDeclaratorOptions )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==114) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // AADL.g:707:18: fileDeclaratorOptions
                    {
                    pushFollow(FOLLOW_fileDeclaratorOptions_in_txtFileDeclarator2999);
                    fileDeclaratorOptions227=fileDeclaratorOptions();
                    _fsp--;

                    adaptor.addChild(root_0, fileDeclaratorOptions227.getTree());

                    }
                    break;

            }

            char_literal228=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_txtFileDeclarator3002); 
            // AADL.g:707:46: ( expression )
            // AADL.g:707:47: expression
            {
            pushFollow(FOLLOW_expression_in_txtFileDeclarator3006);
            expression229=expression();
            _fsp--;

            adaptor.addChild(root_0, expression229.getTree());

            }

            // AADL.g:707:59: ( ',' expression )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==107) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // AADL.g:707:60: ',' expression
                    {
                    char_literal230=(Token)input.LT(1);
                    match(input,107,FOLLOW_107_in_txtFileDeclarator3010); 
                    pushFollow(FOLLOW_expression_in_txtFileDeclarator3013);
                    expression231=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression231.getTree());

                    }
                    break;

            }

            char_literal232=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_txtFileDeclarator3017); 

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end txtFileDeclarator

    public static class csvFileDeclarator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start csvFileDeclarator
    // AADL.g:711:1: csvFileDeclarator : 'csvFile' ( fileDeclaratorOptions )? '(' ( expression ) ( ',' expression )? ')' ;
    public final csvFileDeclarator_return csvFileDeclarator() throws RecognitionException {
        csvFileDeclarator_return retval = new csvFileDeclarator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal233=null;
        Token char_literal235=null;
        Token char_literal237=null;
        Token char_literal239=null;
        fileDeclaratorOptions_return fileDeclaratorOptions234 = null;

        expression_return expression236 = null;

        expression_return expression238 = null;


        CommonTree string_literal233_tree=null;
        CommonTree char_literal235_tree=null;
        CommonTree char_literal237_tree=null;
        CommonTree char_literal239_tree=null;

        try {
            // AADL.g:716:5: ( 'csvFile' ( fileDeclaratorOptions )? '(' ( expression ) ( ',' expression )? ')' )
            // AADL.g:716:7: 'csvFile' ( fileDeclaratorOptions )? '(' ( expression ) ( ',' expression )? ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal233=(Token)input.LT(1);
            match(input,118,FOLLOW_118_in_csvFileDeclarator3039); 
            string_literal233_tree = (CommonTree)adaptor.create(string_literal233);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal233_tree, root_0);

            // AADL.g:716:18: ( fileDeclaratorOptions )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==114) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // AADL.g:716:18: fileDeclaratorOptions
                    {
                    pushFollow(FOLLOW_fileDeclaratorOptions_in_csvFileDeclarator3042);
                    fileDeclaratorOptions234=fileDeclaratorOptions();
                    _fsp--;

                    adaptor.addChild(root_0, fileDeclaratorOptions234.getTree());

                    }
                    break;

            }

            char_literal235=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_csvFileDeclarator3045); 
            // AADL.g:716:46: ( expression )
            // AADL.g:716:47: expression
            {
            pushFollow(FOLLOW_expression_in_csvFileDeclarator3049);
            expression236=expression();
            _fsp--;

            adaptor.addChild(root_0, expression236.getTree());

            }

            // AADL.g:716:59: ( ',' expression )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==107) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // AADL.g:716:60: ',' expression
                    {
                    char_literal237=(Token)input.LT(1);
                    match(input,107,FOLLOW_107_in_csvFileDeclarator3053); 
                    pushFollow(FOLLOW_expression_in_csvFileDeclarator3056);
                    expression238=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression238.getTree());

                    }
                    break;

            }

            char_literal239=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_csvFileDeclarator3060); 

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end csvFileDeclarator

    public static class xmlFileDeclarator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start xmlFileDeclarator
    // AADL.g:720:1: xmlFileDeclarator : 'xmlFile' ( fileDeclaratorOptions )? '(' ( expression ) ')' ;
    public final xmlFileDeclarator_return xmlFileDeclarator() throws RecognitionException {
        xmlFileDeclarator_return retval = new xmlFileDeclarator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal240=null;
        Token char_literal242=null;
        Token char_literal244=null;
        fileDeclaratorOptions_return fileDeclaratorOptions241 = null;

        expression_return expression243 = null;


        CommonTree string_literal240_tree=null;
        CommonTree char_literal242_tree=null;
        CommonTree char_literal244_tree=null;

        try {
            // AADL.g:725:5: ( 'xmlFile' ( fileDeclaratorOptions )? '(' ( expression ) ')' )
            // AADL.g:725:7: 'xmlFile' ( fileDeclaratorOptions )? '(' ( expression ) ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal240=(Token)input.LT(1);
            match(input,119,FOLLOW_119_in_xmlFileDeclarator3082); 
            string_literal240_tree = (CommonTree)adaptor.create(string_literal240);
            root_0 = (CommonTree)adaptor.becomeRoot(string_literal240_tree, root_0);

            // AADL.g:725:18: ( fileDeclaratorOptions )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==114) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // AADL.g:725:18: fileDeclaratorOptions
                    {
                    pushFollow(FOLLOW_fileDeclaratorOptions_in_xmlFileDeclarator3085);
                    fileDeclaratorOptions241=fileDeclaratorOptions();
                    _fsp--;

                    adaptor.addChild(root_0, fileDeclaratorOptions241.getTree());

                    }
                    break;

            }

            char_literal242=(Token)input.LT(1);
            match(input,105,FOLLOW_105_in_xmlFileDeclarator3088); 
            // AADL.g:725:46: ( expression )
            // AADL.g:725:47: expression
            {
            pushFollow(FOLLOW_expression_in_xmlFileDeclarator3092);
            expression243=expression();
            _fsp--;

            adaptor.addChild(root_0, expression243.getTree());

            }

            char_literal244=(Token)input.LT(1);
            match(input,106,FOLLOW_106_in_xmlFileDeclarator3095); 

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end xmlFileDeclarator

    public static class fileDeclaratorOptions_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fileDeclaratorOptions
    // AADL.g:730:1: fileDeclaratorOptions : '[' ( fileDeclaratorOptionEntry ( ',' fileDeclaratorOptionEntry )* ( ',' )? )? ']' -> ( fileDeclaratorOptionEntry )* ;
    public final fileDeclaratorOptions_return fileDeclaratorOptions() throws RecognitionException {
        fileDeclaratorOptions_return retval = new fileDeclaratorOptions_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal245=null;
        Token char_literal247=null;
        Token char_literal249=null;
        Token char_literal250=null;
        fileDeclaratorOptionEntry_return fileDeclaratorOptionEntry246 = null;

        fileDeclaratorOptionEntry_return fileDeclaratorOptionEntry248 = null;


        CommonTree char_literal245_tree=null;
        CommonTree char_literal247_tree=null;
        CommonTree char_literal249_tree=null;
        CommonTree char_literal250_tree=null;
        RewriteRuleTokenStream stream_114=new RewriteRuleTokenStream(adaptor,"token 114");
        RewriteRuleTokenStream stream_115=new RewriteRuleTokenStream(adaptor,"token 115");
        RewriteRuleTokenStream stream_107=new RewriteRuleTokenStream(adaptor,"token 107");
        RewriteRuleSubtreeStream stream_fileDeclaratorOptionEntry=new RewriteRuleSubtreeStream(adaptor,"rule fileDeclaratorOptionEntry");
        try {
            // AADL.g:731:5: ( '[' ( fileDeclaratorOptionEntry ( ',' fileDeclaratorOptionEntry )* ( ',' )? )? ']' -> ( fileDeclaratorOptionEntry )* )
            // AADL.g:731:7: '[' ( fileDeclaratorOptionEntry ( ',' fileDeclaratorOptionEntry )* ( ',' )? )? ']'
            {
            char_literal245=(Token)input.LT(1);
            match(input,114,FOLLOW_114_in_fileDeclaratorOptions3115); 
            stream_114.add(char_literal245);

            // AADL.g:731:11: ( fileDeclaratorOptionEntry ( ',' fileDeclaratorOptionEntry )* ( ',' )? )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==Identifier) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // AADL.g:731:13: fileDeclaratorOptionEntry ( ',' fileDeclaratorOptionEntry )* ( ',' )?
                    {
                    pushFollow(FOLLOW_fileDeclaratorOptionEntry_in_fileDeclaratorOptions3119);
                    fileDeclaratorOptionEntry246=fileDeclaratorOptionEntry();
                    _fsp--;

                    stream_fileDeclaratorOptionEntry.add(fileDeclaratorOptionEntry246.getTree());
                    // AADL.g:731:39: ( ',' fileDeclaratorOptionEntry )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==107) ) {
                            int LA60_1 = input.LA(2);

                            if ( (LA60_1==Identifier) ) {
                                alt60=1;
                            }


                        }


                        switch (alt60) {
                    	case 1 :
                    	    // AADL.g:731:40: ',' fileDeclaratorOptionEntry
                    	    {
                    	    char_literal247=(Token)input.LT(1);
                    	    match(input,107,FOLLOW_107_in_fileDeclaratorOptions3122); 
                    	    stream_107.add(char_literal247);

                    	    pushFollow(FOLLOW_fileDeclaratorOptionEntry_in_fileDeclaratorOptions3124);
                    	    fileDeclaratorOptionEntry248=fileDeclaratorOptionEntry();
                    	    _fsp--;

                    	    stream_fileDeclaratorOptionEntry.add(fileDeclaratorOptionEntry248.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);

                    // AADL.g:731:72: ( ',' )?
                    int alt61=2;
                    int LA61_0 = input.LA(1);

                    if ( (LA61_0==107) ) {
                        alt61=1;
                    }
                    switch (alt61) {
                        case 1 :
                            // AADL.g:731:73: ','
                            {
                            char_literal249=(Token)input.LT(1);
                            match(input,107,FOLLOW_107_in_fileDeclaratorOptions3129); 
                            stream_107.add(char_literal249);


                            }
                            break;

                    }


                    }
                    break;

            }

            char_literal250=(Token)input.LT(1);
            match(input,115,FOLLOW_115_in_fileDeclaratorOptions3136); 
            stream_115.add(char_literal250);


            // AST REWRITE
            // elements: fileDeclaratorOptionEntry
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 732:7: -> ( fileDeclaratorOptionEntry )*
            {
                // AADL.g:732:10: ( fileDeclaratorOptionEntry )*
                while ( stream_fileDeclaratorOptionEntry.hasNext() ) {
                    adaptor.addChild(root_0, stream_fileDeclaratorOptionEntry.next());

                }
                stream_fileDeclaratorOptionEntry.reset();

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fileDeclaratorOptions

    public static class fileDeclaratorOptionEntry_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start fileDeclaratorOptionEntry
    // AADL.g:735:1: fileDeclaratorOptionEntry : Identifier eq= '=' ( expression )? -> ^( FOPTION[$eq,\"FOPTION\"] Identifier ( expression )? ) ;
    public final fileDeclaratorOptionEntry_return fileDeclaratorOptionEntry() throws RecognitionException {
        fileDeclaratorOptionEntry_return retval = new fileDeclaratorOptionEntry_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token eq=null;
        Token Identifier251=null;
        expression_return expression252 = null;


        CommonTree eq_tree=null;
        CommonTree Identifier251_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // AADL.g:736:5: ( Identifier eq= '=' ( expression )? -> ^( FOPTION[$eq,\"FOPTION\"] Identifier ( expression )? ) )
            // AADL.g:736:7: Identifier eq= '=' ( expression )?
            {
            Identifier251=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_fileDeclaratorOptionEntry3164); 
            stream_Identifier.add(Identifier251);

            eq=(Token)input.LT(1);
            match(input,ASSIGN,FOLLOW_ASSIGN_in_fileDeclaratorOptionEntry3168); 
            stream_ASSIGN.add(eq);

            // AADL.g:736:25: ( expression )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==JAVA_ACTION||LA63_0==Identifier||(LA63_0>=HexLiteral && LA63_0<=CMDARG)||(LA63_0>=PLUS && LA63_0<=MINUS)||LA63_0==LESS||(LA63_0>=NOT && LA63_0<=BAR)||LA63_0==105||(LA63_0>=113 && LA63_0<=114)||LA63_0==116||(LA63_0>=120 && LA63_0<=123)) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // AADL.g:736:25: expression
                    {
                    pushFollow(FOLLOW_expression_in_fileDeclaratorOptionEntry3170);
                    expression252=expression();
                    _fsp--;

                    stream_expression.add(expression252.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: Identifier, expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 737:7: -> ^( FOPTION[$eq,\"FOPTION\"] Identifier ( expression )? )
            {
                // AADL.g:737:10: ^( FOPTION[$eq,\"FOPTION\"] Identifier ( expression )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(FOPTION, eq, "FOPTION"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                // AADL.g:737:46: ( expression )?
                if ( stream_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_expression.next());

                }
                stream_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end fileDeclaratorOptionEntry

    public static class variableDefinition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variableDefinition
    // AADL.g:741:1: variableDefinition : Identifier ':' type -> ^( VARDEF[$Identifier,\"VARDEF\"] Identifier type ) ;
    public final variableDefinition_return variableDefinition() throws RecognitionException {
        variableDefinition_return retval = new variableDefinition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier253=null;
        Token char_literal254=null;
        type_return type255 = null;


        CommonTree Identifier253_tree=null;
        CommonTree char_literal254_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            // AADL.g:742:5: ( Identifier ':' type -> ^( VARDEF[$Identifier,\"VARDEF\"] Identifier type ) )
            // AADL.g:742:7: Identifier ':' type
            {
            Identifier253=(Token)input.LT(1);
            match(input,Identifier,FOLLOW_Identifier_in_variableDefinition3207); 
            stream_Identifier.add(Identifier253);

            char_literal254=(Token)input.LT(1);
            match(input,COLON,FOLLOW_COLON_in_variableDefinition3209); 
            stream_COLON.add(char_literal254);

            pushFollow(FOLLOW_type_in_variableDefinition3211);
            type255=type();
            _fsp--;

            stream_type.add(type255.getTree());

            // AST REWRITE
            // elements: type, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 743:3: -> ^( VARDEF[$Identifier,\"VARDEF\"] Identifier type )
            {
                // AADL.g:743:6: ^( VARDEF[$Identifier,\"VARDEF\"] Identifier type )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(VARDEF, Identifier253, "VARDEF"), root_1);

                adaptor.addChild(root_1, stream_Identifier.next());
                adaptor.addChild(root_1, stream_type.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end variableDefinition

    public static class variableTypeName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variableTypeName
    // AADL.g:746:1: variableTypeName : ( Identifier ( '.' Identifier )+ -> ^( JAVACLASSPATH ( Identifier )+ ) | Identifier -> Identifier );
    public final variableTypeName_return variableTypeName() throws RecognitionException {
        variableTypeName_return retval = new variableTypeName_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token Identifier256=null;
        Token char_literal257=null;
        Token Identifier258=null;
        Token Identifier259=null;

        CommonTree Identifier256_tree=null;
        CommonTree char_literal257_tree=null;
        CommonTree Identifier258_tree=null;
        CommonTree Identifier259_tree=null;
        RewriteRuleTokenStream stream_112=new RewriteRuleTokenStream(adaptor,"token 112");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            // AADL.g:747:5: ( Identifier ( '.' Identifier )+ -> ^( JAVACLASSPATH ( Identifier )+ ) | Identifier -> Identifier )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==Identifier) ) {
                int LA65_1 = input.LA(2);

                if ( (LA65_1==FINPUT||LA65_1==ASSIGN||LA65_1==SCOPE||(LA65_1>=102 && LA65_1<=103)||(LA65_1>=105 && LA65_1<=107)||LA65_1==115) ) {
                    alt65=2;
                }
                else if ( (LA65_1==112) ) {
                    alt65=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("746:1: variableTypeName : ( Identifier ( '.' Identifier )+ -> ^( JAVACLASSPATH ( Identifier )+ ) | Identifier -> Identifier );", 65, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("746:1: variableTypeName : ( Identifier ( '.' Identifier )+ -> ^( JAVACLASSPATH ( Identifier )+ ) | Identifier -> Identifier );", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // AADL.g:747:7: Identifier ( '.' Identifier )+
                    {
                    Identifier256=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_variableTypeName3241); 
                    stream_Identifier.add(Identifier256);

                    // AADL.g:747:18: ( '.' Identifier )+
                    int cnt64=0;
                    loop64:
                    do {
                        int alt64=2;
                        int LA64_0 = input.LA(1);

                        if ( (LA64_0==112) ) {
                            alt64=1;
                        }


                        switch (alt64) {
                    	case 1 :
                    	    // AADL.g:747:19: '.' Identifier
                    	    {
                    	    char_literal257=(Token)input.LT(1);
                    	    match(input,112,FOLLOW_112_in_variableTypeName3244); 
                    	    stream_112.add(char_literal257);

                    	    Identifier258=(Token)input.LT(1);
                    	    match(input,Identifier,FOLLOW_Identifier_in_variableTypeName3246); 
                    	    stream_Identifier.add(Identifier258);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt64 >= 1 ) break loop64;
                                EarlyExitException eee =
                                    new EarlyExitException(64, input);
                                throw eee;
                        }
                        cnt64++;
                    } while (true);


                    // AST REWRITE
                    // elements: Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 747:36: -> ^( JAVACLASSPATH ( Identifier )+ )
                    {
                        // AADL.g:747:39: ^( JAVACLASSPATH ( Identifier )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(JAVACLASSPATH, "JAVACLASSPATH"), root_1);

                        if ( !(stream_Identifier.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_Identifier.hasNext() ) {
                            adaptor.addChild(root_1, stream_Identifier.next());

                        }
                        stream_Identifier.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 2 :
                    // AADL.g:748:7: Identifier
                    {
                    Identifier259=(Token)input.LT(1);
                    match(input,Identifier,FOLLOW_Identifier_in_variableTypeName3265); 
                    stream_Identifier.add(Identifier259);


                    // AST REWRITE
                    // elements: Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 748:20: -> Identifier
                    {
                        adaptor.addChild(root_0, stream_Identifier.next());

                    }



                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end variableTypeName

    public static class type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start type
    // AADL.g:751:1: type : variableTypeName -> ^( VARTYPE variableTypeName ) ;
    public final type_return type() throws RecognitionException {
        type_return retval = new type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        variableTypeName_return variableTypeName260 = null;


        RewriteRuleSubtreeStream stream_variableTypeName=new RewriteRuleSubtreeStream(adaptor,"rule variableTypeName");
        try {
            // AADL.g:752:5: ( variableTypeName -> ^( VARTYPE variableTypeName ) )
            // AADL.g:752:7: variableTypeName
            {
            pushFollow(FOLLOW_variableTypeName_in_type3288);
            variableTypeName260=variableTypeName();
            _fsp--;

            stream_variableTypeName.add(variableTypeName260.getTree());

            // AST REWRITE
            // elements: variableTypeName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 752:24: -> ^( VARTYPE variableTypeName )
            {
                // AADL.g:752:27: ^( VARTYPE variableTypeName )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(VARTYPE, "VARTYPE"), root_1);

                adaptor.addChild(root_1, stream_variableTypeName.next());

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end type

    public static class arrayLiteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start arrayLiteral
    // AADL.g:757:1: arrayLiteral : '[' arrayParameter ( ',' arrayParameter )* ( ',' )? ']' -> ^( ARRAY ( arrayParameter )+ ) ;
    public final arrayLiteral_return arrayLiteral() throws RecognitionException {
        arrayLiteral_return retval = new arrayLiteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal261=null;
        Token char_literal263=null;
        Token char_literal265=null;
        Token char_literal266=null;
        arrayParameter_return arrayParameter262 = null;

        arrayParameter_return arrayParameter264 = null;


        CommonTree char_literal261_tree=null;
        CommonTree char_literal263_tree=null;
        CommonTree char_literal265_tree=null;
        CommonTree char_literal266_tree=null;
        RewriteRuleTokenStream stream_114=new RewriteRuleTokenStream(adaptor,"token 114");
        RewriteRuleTokenStream stream_115=new RewriteRuleTokenStream(adaptor,"token 115");
        RewriteRuleTokenStream stream_107=new RewriteRuleTokenStream(adaptor,"token 107");
        RewriteRuleSubtreeStream stream_arrayParameter=new RewriteRuleSubtreeStream(adaptor,"rule arrayParameter");
        try {
            // AADL.g:758:5: ( '[' arrayParameter ( ',' arrayParameter )* ( ',' )? ']' -> ^( ARRAY ( arrayParameter )+ ) )
            // AADL.g:758:7: '[' arrayParameter ( ',' arrayParameter )* ( ',' )? ']'
            {
            char_literal261=(Token)input.LT(1);
            match(input,114,FOLLOW_114_in_arrayLiteral3315); 
            stream_114.add(char_literal261);

            pushFollow(FOLLOW_arrayParameter_in_arrayLiteral3317);
            arrayParameter262=arrayParameter();
            _fsp--;

            stream_arrayParameter.add(arrayParameter262.getTree());
            // AADL.g:758:26: ( ',' arrayParameter )*
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==107) ) {
                    int LA66_1 = input.LA(2);

                    if ( (LA66_1==JAVA_ACTION||LA66_1==Identifier||(LA66_1>=HexLiteral && LA66_1<=CMDARG)||(LA66_1>=PLUS && LA66_1<=MINUS)||LA66_1==LESS||(LA66_1>=NOT && LA66_1<=BAR)||LA66_1==105||(LA66_1>=113 && LA66_1<=114)||LA66_1==116||(LA66_1>=120 && LA66_1<=123)) ) {
                        alt66=1;
                    }


                }


                switch (alt66) {
            	case 1 :
            	    // AADL.g:758:27: ',' arrayParameter
            	    {
            	    char_literal263=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_arrayLiteral3320); 
            	    stream_107.add(char_literal263);

            	    pushFollow(FOLLOW_arrayParameter_in_arrayLiteral3322);
            	    arrayParameter264=arrayParameter();
            	    _fsp--;

            	    stream_arrayParameter.add(arrayParameter264.getTree());

            	    }
            	    break;

            	default :
            	    break loop66;
                }
            } while (true);

            // AADL.g:758:48: ( ',' )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==107) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // AADL.g:758:49: ','
                    {
                    char_literal265=(Token)input.LT(1);
                    match(input,107,FOLLOW_107_in_arrayLiteral3327); 
                    stream_107.add(char_literal265);


                    }
                    break;

            }

            char_literal266=(Token)input.LT(1);
            match(input,115,FOLLOW_115_in_arrayLiteral3331); 
            stream_115.add(char_literal266);


            // AST REWRITE
            // elements: arrayParameter
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 759:7: -> ^( ARRAY ( arrayParameter )+ )
            {
                // AADL.g:759:10: ^( ARRAY ( arrayParameter )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(ARRAY, "ARRAY"), root_1);

                if ( !(stream_arrayParameter.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_arrayParameter.hasNext() ) {
                    adaptor.addChild(root_1, stream_arrayParameter.next());

                }
                stream_arrayParameter.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end arrayLiteral

    public static class arrayParameter_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start arrayParameter
    // AADL.g:762:1: arrayParameter : hk= expression ( ':' hv= expression -> ^( HASH $hk $hv) | -> $hk) ;
    public final arrayParameter_return arrayParameter() throws RecognitionException {
        arrayParameter_return retval = new arrayParameter_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal267=null;
        expression_return hk = null;

        expression_return hv = null;


        CommonTree char_literal267_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // AADL.g:763:5: (hk= expression ( ':' hv= expression -> ^( HASH $hk $hv) | -> $hk) )
            // AADL.g:763:7: hk= expression ( ':' hv= expression -> ^( HASH $hk $hv) | -> $hk)
            {
            pushFollow(FOLLOW_expression_in_arrayParameter3365);
            hk=expression();
            _fsp--;

            stream_expression.add(hk.getTree());
            // AADL.g:763:21: ( ':' hv= expression -> ^( HASH $hk $hv) | -> $hk)
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==COLON) ) {
                alt68=1;
            }
            else if ( (LA68_0==107||LA68_0==115) ) {
                alt68=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("763:21: ( ':' hv= expression -> ^( HASH $hk $hv) | -> $hk)", 68, 0, input);

                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // AADL.g:764:7: ':' hv= expression
                    {
                    char_literal267=(Token)input.LT(1);
                    match(input,COLON,FOLLOW_COLON_in_arrayParameter3375); 
                    stream_COLON.add(char_literal267);

                    pushFollow(FOLLOW_expression_in_arrayParameter3379);
                    hv=expression();
                    _fsp--;

                    stream_expression.add(hv.getTree());

                    // AST REWRITE
                    // elements: hv, hk
                    // token labels: 
                    // rule labels: retval, hv, hk
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_hv=new RewriteRuleSubtreeStream(adaptor,"token hv",hv!=null?hv.tree:null);
                    RewriteRuleSubtreeStream stream_hk=new RewriteRuleSubtreeStream(adaptor,"token hk",hk!=null?hk.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 764:25: -> ^( HASH $hk $hv)
                    {
                        // AADL.g:764:28: ^( HASH $hk $hv)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(HASH, "HASH"), root_1);

                        adaptor.addChild(root_1, stream_hk.next());
                        adaptor.addChild(root_1, stream_hv.next());

                        adaptor.addChild(root_0, root_1);
                        }

                    }



                    }
                    break;
                case 2 :
                    // AADL.g:765:11: 
                    {

                    // AST REWRITE
                    // elements: hk
                    // token labels: 
                    // rule labels: retval, hk
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_hk=new RewriteRuleSubtreeStream(adaptor,"token hk",hk!=null?hk.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 765:11: -> $hk
                    {
                        adaptor.addChild(root_0, stream_hk.next());

                    }



                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end arrayParameter

    public static class booleanLiteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start booleanLiteral
    // AADL.g:769:1: booleanLiteral : ( 'true' | 'false' );
    public final booleanLiteral_return booleanLiteral() throws RecognitionException {
        booleanLiteral_return retval = new booleanLiteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set268=null;

        CommonTree set268_tree=null;

        try {
            // AADL.g:770:5: ( 'true' | 'false' )
            // AADL.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set268=(Token)input.LT(1);
            if ( (input.LA(1)>=120 && input.LA(1)<=121) ) {
                input.consume();
                adaptor.addChild(root_0, adaptor.create(set268));
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_booleanLiteral0);    throw mse;
            }


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end booleanLiteral

    public static class nullLiteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start nullLiteral
    // AADL.g:774:1: nullLiteral : 'null' ;
    public final nullLiteral_return nullLiteral() throws RecognitionException {
        nullLiteral_return retval = new nullLiteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal269=null;

        CommonTree string_literal269_tree=null;

        try {
            // AADL.g:775:5: ( 'null' )
            // AADL.g:775:7: 'null'
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal269=(Token)input.LT(1);
            match(input,122,FOLLOW_122_in_nullLiteral3455); 
            string_literal269_tree = (CommonTree)adaptor.create(string_literal269);
            adaptor.addChild(root_0, string_literal269_tree);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end nullLiteral

    public static class exbase_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start exbase
    // AADL.g:778:1: exbase : eb= '<' namedOperationExpression ( ',' namedOperationExpression )* '>' -> ^( EXBASE[$eb,\"EXBASE\"] ( namedOperationExpression )+ ) ;
    public final exbase_return exbase() throws RecognitionException {
        exbase_return retval = new exbase_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token eb=null;
        Token char_literal271=null;
        Token char_literal273=null;
        namedOperationExpression_return namedOperationExpression270 = null;

        namedOperationExpression_return namedOperationExpression272 = null;


        CommonTree eb_tree=null;
        CommonTree char_literal271_tree=null;
        CommonTree char_literal273_tree=null;
        RewriteRuleTokenStream stream_GREATER=new RewriteRuleTokenStream(adaptor,"token GREATER");
        RewriteRuleTokenStream stream_107=new RewriteRuleTokenStream(adaptor,"token 107");
        RewriteRuleTokenStream stream_LESS=new RewriteRuleTokenStream(adaptor,"token LESS");
        RewriteRuleSubtreeStream stream_namedOperationExpression=new RewriteRuleSubtreeStream(adaptor,"rule namedOperationExpression");
        try {
            // AADL.g:779:5: (eb= '<' namedOperationExpression ( ',' namedOperationExpression )* '>' -> ^( EXBASE[$eb,\"EXBASE\"] ( namedOperationExpression )+ ) )
            // AADL.g:779:7: eb= '<' namedOperationExpression ( ',' namedOperationExpression )* '>'
            {
            eb=(Token)input.LT(1);
            match(input,LESS,FOLLOW_LESS_in_exbase3474); 
            stream_LESS.add(eb);

            pushFollow(FOLLOW_namedOperationExpression_in_exbase3477);
            namedOperationExpression270=namedOperationExpression();
            _fsp--;

            stream_namedOperationExpression.add(namedOperationExpression270.getTree());
            // AADL.g:779:40: ( ',' namedOperationExpression )*
            loop69:
            do {
                int alt69=2;
                int LA69_0 = input.LA(1);

                if ( (LA69_0==107) ) {
                    alt69=1;
                }


                switch (alt69) {
            	case 1 :
            	    // AADL.g:779:41: ',' namedOperationExpression
            	    {
            	    char_literal271=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_exbase3480); 
            	    stream_107.add(char_literal271);

            	    pushFollow(FOLLOW_namedOperationExpression_in_exbase3482);
            	    namedOperationExpression272=namedOperationExpression();
            	    _fsp--;

            	    stream_namedOperationExpression.add(namedOperationExpression272.getTree());

            	    }
            	    break;

            	default :
            	    break loop69;
                }
            } while (true);

            char_literal273=(Token)input.LT(1);
            match(input,GREATER,FOLLOW_GREATER_in_exbase3486); 
            stream_GREATER.add(char_literal273);


            // AST REWRITE
            // elements: namedOperationExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 780:7: -> ^( EXBASE[$eb,\"EXBASE\"] ( namedOperationExpression )+ )
            {
                // AADL.g:780:10: ^( EXBASE[$eb,\"EXBASE\"] ( namedOperationExpression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(EXBASE, eb, "EXBASE"), root_1);

                if ( !(stream_namedOperationExpression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_namedOperationExpression.hasNext() ) {
                    adaptor.addChild(root_1, stream_namedOperationExpression.next());

                }
                stream_namedOperationExpression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end exbase

    public static class dtbase_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dtbase
    // AADL.g:783:1: dtbase : db= '<<' namedOperationExpression ( ',' namedOperationExpression )* '>>' -> ^( DTBASE[$db,\"DTBASE\"] ( namedOperationExpression )+ ) ;
    public final dtbase_return dtbase() throws RecognitionException {
        dtbase_return retval = new dtbase_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token db=null;
        Token char_literal275=null;
        Token string_literal277=null;
        namedOperationExpression_return namedOperationExpression274 = null;

        namedOperationExpression_return namedOperationExpression276 = null;


        CommonTree db_tree=null;
        CommonTree char_literal275_tree=null;
        CommonTree string_literal277_tree=null;
        RewriteRuleTokenStream stream_123=new RewriteRuleTokenStream(adaptor,"token 123");
        RewriteRuleTokenStream stream_107=new RewriteRuleTokenStream(adaptor,"token 107");
        RewriteRuleTokenStream stream_124=new RewriteRuleTokenStream(adaptor,"token 124");
        RewriteRuleSubtreeStream stream_namedOperationExpression=new RewriteRuleSubtreeStream(adaptor,"rule namedOperationExpression");
        try {
            // AADL.g:784:5: (db= '<<' namedOperationExpression ( ',' namedOperationExpression )* '>>' -> ^( DTBASE[$db,\"DTBASE\"] ( namedOperationExpression )+ ) )
            // AADL.g:784:7: db= '<<' namedOperationExpression ( ',' namedOperationExpression )* '>>'
            {
            db=(Token)input.LT(1);
            match(input,123,FOLLOW_123_in_dtbase3521); 
            stream_123.add(db);

            pushFollow(FOLLOW_namedOperationExpression_in_dtbase3523);
            namedOperationExpression274=namedOperationExpression();
            _fsp--;

            stream_namedOperationExpression.add(namedOperationExpression274.getTree());
            // AADL.g:784:40: ( ',' namedOperationExpression )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==107) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // AADL.g:784:41: ',' namedOperationExpression
            	    {
            	    char_literal275=(Token)input.LT(1);
            	    match(input,107,FOLLOW_107_in_dtbase3526); 
            	    stream_107.add(char_literal275);

            	    pushFollow(FOLLOW_namedOperationExpression_in_dtbase3528);
            	    namedOperationExpression276=namedOperationExpression();
            	    _fsp--;

            	    stream_namedOperationExpression.add(namedOperationExpression276.getTree());

            	    }
            	    break;

            	default :
            	    break loop70;
                }
            } while (true);

            string_literal277=(Token)input.LT(1);
            match(input,124,FOLLOW_124_in_dtbase3532); 
            stream_124.add(string_literal277);


            // AST REWRITE
            // elements: namedOperationExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 785:3: -> ^( DTBASE[$db,\"DTBASE\"] ( namedOperationExpression )+ )
            {
                // AADL.g:785:6: ^( DTBASE[$db,\"DTBASE\"] ( namedOperationExpression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(adaptor.create(DTBASE, db, "DTBASE"), root_1);

                if ( !(stream_namedOperationExpression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_namedOperationExpression.hasNext() ) {
                    adaptor.addChild(root_1, stream_namedOperationExpression.next());

                }
                stream_namedOperationExpression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end dtbase

    public static class commandArg_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start commandArg
    // AADL.g:788:1: commandArg : CMDARG ;
    public final commandArg_return commandArg() throws RecognitionException {
        commandArg_return retval = new commandArg_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token CMDARG278=null;

        CommonTree CMDARG278_tree=null;

        try {
            // AADL.g:789:5: ( CMDARG )
            // AADL.g:789:7: CMDARG
            {
            root_0 = (CommonTree)adaptor.nil();

            CMDARG278=(Token)input.LT(1);
            match(input,CMDARG,FOLLOW_CMDARG_in_commandArg3561); 
            CMDARG278_tree = (CommonTree)adaptor.create(CMDARG278);
            adaptor.addChild(root_0, CMDARG278_tree);

            addCommandArg(CMDARG278);

            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end commandArg

    public static class modifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start modifier
    // AADL.g:794:1: modifier : 'public' ;
    public final modifier_return modifier() throws RecognitionException {
        modifier_return retval = new modifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal279=null;

        CommonTree string_literal279_tree=null;

        try {
            // AADL.g:795:5: ( 'public' )
            // AADL.g:795:7: 'public'
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal279=(Token)input.LT(1);
            match(input,125,FOLLOW_125_in_modifier3586); 
            string_literal279_tree = (CommonTree)adaptor.create(string_literal279);
            adaptor.addChild(root_0, string_literal279_tree);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end modifier


 

    public static final BitSet FOLLOW_program_in_test209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_packageDeclaration_in_program234 = new BitSet(new long[]{0x0001B40000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_JAVA_HEADER_ACTION_in_program242 = new BitSet(new long[]{0x0001B40000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_subModule_in_program253 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_mainModule_in_program262 = new BitSet(new long[]{0x0001900000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_subModule_in_program272 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_program281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_PACKAGE_in_packageDeclaration350 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_variableTypeName_in_packageDeclaration352 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_packageDeclaration354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constVariableDeclaration_in_constDeclaration379 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_constDeclaration381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_JAVA_ACTION_in_subModule402 = new BitSet(new long[]{0x0001900000000002L,0x2000000000000000L});
    public static final BitSet FOLLOW_function_in_subModule404 = new BitSet(new long[]{0x0001900000000002L,0x2000000000000000L});
    public static final BitSet FOLLOW_constDeclaration_in_subModule406 = new BitSet(new long[]{0x0001900000000002L,0x2000000000000000L});
    public static final BitSet FOLLOW_PROGRAM_in_mainModule428 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_Identifier_in_mainModule430 = new BitSet(new long[]{0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_mainBody_in_mainModule434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_mainBody469 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_statement_in_mainBody471 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_104_in_mainBody474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_function506 = new BitSet(new long[]{0x0000800000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_FUNCTION_in_function509 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_functionDeclaration_in_function511 = new BitSet(new long[]{0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_functionBody_in_function513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_functionDeclaration563 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_functionDeclaration566 = new BitSet(new long[]{0x0000400000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_functionFormalParameters_in_functionDeclaration568 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_functionDeclaration571 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_functionReturnDeclaration_in_functionDeclaration573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_functionReturnDeclaration611 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_type_in_functionReturnDeclaration613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDefinition_in_functionFormalParameters645 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_107_in_functionFormalParameters648 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_variableDefinition_in_functionFormalParameters650 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_103_in_functionBody686 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_statement_in_functionBody688 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_104_in_functionBody691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_block722 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_block724 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000080L});
    public static final BitSet FOLLOW_VBAR_in_block736 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602800000E20CL});
    public static final BitSet FOLLOW_involvingCondition_in_block738 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_block740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_block772 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_statement_in_block774 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_104_in_block777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_block809 = new BitSet(new long[]{0x0039400000000000L,0x000010C000000000L});
    public static final BitSet FOLLOW_noexpStatement_in_block811 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_statement_in_block813 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_104_in_block816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_block841 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_block843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_statement870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_statement_in_statement878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vardef_statement_in_statement886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_statement_in_statement894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_break_statement_in_statement902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jump_statement_in_statement910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exp_statement_in_statement918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_statement926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_noexpStatement944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_statement_in_noexpStatement952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vardef_statement_in_noexpStatement960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_statement_in_noexpStatement968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_break_statement_in_noexpStatement976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jump_statement_in_noexpStatement984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_noexpStatement992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_vardef_statement_in_singleStatement1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_statement_in_singleStatement1018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_break_statement_in_singleStatement1026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jump_statement_in_singleStatement1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exp_statement_in_singleStatement1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_102_in_singleStatement1050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_vardef_statement1068 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_vardef_statement1070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constVariableDeclaration_in_vardef_statement1079 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_vardef_statement1081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONST_in_constVariableDeclaration1099 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_localVariableDeclarationRest_in_constVariableDeclaration1101 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_107_in_constVariableDeclaration1104 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_localVariableDeclarationRest_in_constVariableDeclaration1106 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_108_in_localVariableDeclaration1140 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_localVariableDeclarationRest_in_localVariableDeclaration1143 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_107_in_localVariableDeclaration1146 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_localVariableDeclarationRest_in_localVariableDeclaration1149 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_variableDefinition_in_localVariableDeclarationRest1168 = new BitSet(new long[]{0x0002000000000002L,0x0000020000000800L});
    public static final BitSet FOLLOW_ASSIGN_in_localVariableDeclarationRest1181 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602800000E20CL});
    public static final BitSet FOLLOW_expression_in_localVariableDeclarationRest1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_involvingExpression_in_localVariableDeclarationRest1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINPUT_in_localVariableDeclarationRest1253 = new BitSet(new long[]{0x0000000000000000L,0x00E0000000000000L});
    public static final BitSet FOLLOW_fileDeclarator_in_localVariableDeclarationRest1255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_localVariableDeclarationRest1280 = new BitSet(new long[]{0x7F00500000000000L,0x0F1606000000E20CL});
    public static final BitSet FOLLOW_methodArguments_in_localVariableDeclarationRest1282 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_localVariableDeclarationRest1285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_exp_statement1338 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_exp_statement1340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_assign_statement1358 = new BitSet(new long[]{0x0006000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_ASSIGN_in_assign_statement1371 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602800000E20CL});
    public static final BitSet FOLLOW_expression_in_assign_statement1382 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_involvingExpression_in_assign_statement1410 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_FINPUT_in_assign_statement1442 = new BitSet(new long[]{0x0000000000000000L,0x00E0000000000000L});
    public static final BitSet FOLLOW_fileDeclarator_in_assign_statement1444 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_FOUTPUT_in_assign_statement1466 = new BitSet(new long[]{0x0000000000000000L,0x00E0000000000000L});
    public static final BitSet FOLLOW_fileDeclarator_in_assign_statement1468 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_assign_statement1490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_break_statement1507 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_break_statement1510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_jump_statement1528 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602400000E20CL});
    public static final BitSet FOLLOW_expression_in_jump_statement1531 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_102_in_jump_statement1534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIF_in_if_statement1552 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_if_statement1554 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_if_statement1556 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_if_statement1558 = new BitSet(new long[]{0x7F39500000000000L,0x0F1612C00000E20CL});
    public static final BitSet FOLLOW_if_statement_in_if_statement1566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_if_statement1586 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_else_statement_in_if_statement1588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleStatement_in_if_statement1611 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_else_statement_in_if_statement1613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELSE_in_else_statement1650 = new BitSet(new long[]{0x7F39500000000000L,0x0F1612C00000E20CL});
    public static final BitSet FOLLOW_block_in_else_statement1659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_statement_in_else_statement1667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleStatement_in_else_statement1675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_involvingExpression1703 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_involvingExpression1705 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_VBAR_in_involvingExpression1709 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602800000E20CL});
    public static final BitSet FOLLOW_involvingCondition_in_involvingExpression1711 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_involvingExpression1713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_filterExpression_in_involvingCondition1754 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_107_in_involvingCondition1758 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602800000E20CL});
    public static final BitSet FOLLOW_filterExpression_in_involvingCondition1761 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_listExpression_in_filterExpression1781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aliasExpression_in_filterExpression1785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_filterExpression1789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_involvingBlock_in_filterExpression1793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_listExpression1812 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_ITERATOR_in_listExpression1816 = new BitSet(new long[]{0x0000400000000000L,0x00F6000000000000L});
    public static final BitSet FOLLOW_fileDeclarator_in_listExpression1839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_listExpression1867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayLiteral_in_listExpression1894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodCall_in_listExpression1920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_103_in_involvingBlock1973 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_statement_in_involvingBlock1975 = new BitSet(new long[]{0x7F39500000000000L,0x0F1613C00000E20CL});
    public static final BitSet FOLLOW_104_in_involvingBlock1978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_aliasExpression2005 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000802L});
    public static final BitSet FOLLOW_COLON_in_aliasExpression2008 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_type_in_aliasExpression2010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_ASSIGN_in_aliasExpression2016 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_conditionalOrExpression_in_aliasExpression2018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_expression2049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression2070 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_109_in_conditionalOrExpression2074 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression2077 = new BitSet(new long[]{0x0000000000000002L,0x0000200000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_conditionalAndExpression2097 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_110_in_conditionalAndExpression2101 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_equalityExpression_in_conditionalAndExpression2104 = new BitSet(new long[]{0x0000000000000002L,0x0000400000000000L});
    public static final BitSet FOLLOW_comparativeExpression_in_equalityExpression2124 = new BitSet(new long[]{0x0000000000000002L,0x0000000000030000L});
    public static final BitSet FOLLOW_set_in_equalityExpression2128 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_comparativeExpression_in_equalityExpression2137 = new BitSet(new long[]{0x0000000000000002L,0x0000000000030000L});
    public static final BitSet FOLLOW_namedOperationExpression_in_comparativeExpression2157 = new BitSet(new long[]{0x0000000000000002L,0x00000000000C0600L});
    public static final BitSet FOLLOW_set_in_comparativeExpression2161 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_namedOperationExpression_in_comparativeExpression2178 = new BitSet(new long[]{0x0000000000000002L,0x00000000000C0600L});
    public static final BitSet FOLLOW_additiveExpression_in_namedOperationExpression2198 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_NamedOperator_in_namedOperationExpression2202 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_additiveExpression_in_namedOperationExpression2205 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2225 = new BitSet(new long[]{0x0000000000000002L,0x000000000000000CL});
    public static final BitSet FOLLOW_set_in_additiveExpression2229 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2238 = new BitSet(new long[]{0x0000000000000002L,0x000000000000000CL});
    public static final BitSet FOLLOW_simpleExpression_in_multiplicativeExpression2258 = new BitSet(new long[]{0x0000000000000002L,0x0000800000001030L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression2262 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_simpleExpression_in_multiplicativeExpression2281 = new BitSet(new long[]{0x0000000000000002L,0x0000800000001030L});
    public static final BitSet FOLLOW_PLUS_in_simpleExpression2301 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_simpleExpression_in_simpleExpression2304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_simpleExpression2312 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_simpleExpression_in_simpleExpression2315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleExpressionNotPlusMinus_in_simpleExpression2323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAR_in_simpleExpressionNotPlusMinus2340 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_simpleExpression_in_simpleExpressionNotPlusMinus2343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleExpressionNotPlusMinus2351 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_simpleExpression_in_simpleExpressionNotPlusMinus2354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAT_in_simpleExpressionNotPlusMinus2362 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_simpleExpression_in_simpleExpressionNotPlusMinus2365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_postfixExpression_in_simpleExpressionNotPlusMinus2373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_postfixExpression2391 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_112_in_postfixExpression2408 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_Identifier_in_postfixExpression2412 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_callInstanceMethodArguments_in_postfixExpression2416 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_105_in_callInstanceMethodArguments2468 = new BitSet(new long[]{0x7F00500000000000L,0x0F1606000000E20CL});
    public static final BitSet FOLLOW_methodArguments_in_callInstanceMethodArguments2470 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_callInstanceMethodArguments2473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_primary2501 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_primary2504 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_primary2506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayLiteral_in_primary2516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HexLiteral_in_primary2524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_primary2532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IntegerLiteral_in_primary2540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_primary2548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullLiteral_in_primary2556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_primary2564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_primary2572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_primary2580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exbase_in_primary2588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dtbase_in_primary2596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodCall_in_primary2604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_JAVA_ACTION_in_primary2612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary2620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_commandArg_in_primary2628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callSystemMethod_in_methodCall2648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callRegisteredMethod_in_methodCall2657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callSpecialMethod_in_methodCall2665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callModuleMethod_in_methodCall2673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_113_in_callSystemMethod2693 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_114_in_callSystemMethod2695 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_type_in_callSystemMethod2697 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_115_in_callSystemMethod2699 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_callSystemMethod2701 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_callSystemMethod2703 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_callSystemMethod2705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_116_in_callSystemMethod2732 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_114_in_callSystemMethod2734 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_type_in_callSystemMethod2736 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_115_in_callSystemMethod2738 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_callSystemMethod2740 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_callSystemMethod2742 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_callSystemMethod2744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_callRegisteredMethod2782 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_callRegisteredMethod2784 = new BitSet(new long[]{0x7F00500000000000L,0x0F1606000000E20CL});
    public static final BitSet FOLLOW_methodArguments_in_callRegisteredMethod2786 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_callRegisteredMethod2789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_callSpecialMethod2827 = new BitSet(new long[]{0x0000000000000000L,0x0004000000000000L});
    public static final BitSet FOLLOW_114_in_callSpecialMethod2829 = new BitSet(new long[]{0x7F00500000000000L,0x0F1E02000000E20CL});
    public static final BitSet FOLLOW_methodArguments_in_callSpecialMethod2831 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_115_in_callSpecialMethod2834 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_callSpecialMethod2836 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_callSpecialMethod2838 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_callSpecialMethod2840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableTypeName_in_callModuleMethod2884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_SCOPE_in_callModuleMethod2886 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_Identifier_in_callModuleMethod2888 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_callModuleMethod2890 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_methodArguments_in_callModuleMethod2892 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_callModuleMethod2894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_methodArguments2938 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_107_in_methodArguments2941 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_methodArguments2944 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_txtFileDeclarator_in_fileDeclarator2965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_csvFileDeclarator_in_fileDeclarator2970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_xmlFileDeclarator_in_fileDeclarator2975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_117_in_txtFileDeclarator2996 = new BitSet(new long[]{0x0000000000000000L,0x0004020000000000L});
    public static final BitSet FOLLOW_fileDeclaratorOptions_in_txtFileDeclarator2999 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_txtFileDeclarator3002 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_txtFileDeclarator3006 = new BitSet(new long[]{0x0000000000000000L,0x00000C0000000000L});
    public static final BitSet FOLLOW_107_in_txtFileDeclarator3010 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_txtFileDeclarator3013 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_txtFileDeclarator3017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_118_in_csvFileDeclarator3039 = new BitSet(new long[]{0x0000000000000000L,0x0004020000000000L});
    public static final BitSet FOLLOW_fileDeclaratorOptions_in_csvFileDeclarator3042 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_csvFileDeclarator3045 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_csvFileDeclarator3049 = new BitSet(new long[]{0x0000000000000000L,0x00000C0000000000L});
    public static final BitSet FOLLOW_107_in_csvFileDeclarator3053 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_csvFileDeclarator3056 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_csvFileDeclarator3060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_119_in_xmlFileDeclarator3082 = new BitSet(new long[]{0x0000000000000000L,0x0004020000000000L});
    public static final BitSet FOLLOW_fileDeclaratorOptions_in_xmlFileDeclarator3085 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_xmlFileDeclarator3088 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_xmlFileDeclarator3092 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_106_in_xmlFileDeclarator3095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_fileDeclaratorOptions3115 = new BitSet(new long[]{0x0000400000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_fileDeclaratorOptionEntry_in_fileDeclaratorOptions3119 = new BitSet(new long[]{0x0000000000000000L,0x0008080000000000L});
    public static final BitSet FOLLOW_107_in_fileDeclaratorOptions3122 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_fileDeclaratorOptionEntry_in_fileDeclaratorOptions3124 = new BitSet(new long[]{0x0000000000000000L,0x0008080000000000L});
    public static final BitSet FOLLOW_107_in_fileDeclaratorOptions3129 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_115_in_fileDeclaratorOptions3136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_fileDeclaratorOptionEntry3164 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_ASSIGN_in_fileDeclaratorOptionEntry3168 = new BitSet(new long[]{0x7F00500000000002L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_fileDeclaratorOptionEntry3170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDefinition3207 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_variableDefinition3209 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_type_in_variableDefinition3211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableTypeName3241 = new BitSet(new long[]{0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_112_in_variableTypeName3244 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_Identifier_in_variableTypeName3246 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_Identifier_in_variableTypeName3265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableTypeName_in_type3288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_114_in_arrayLiteral3315 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_arrayParameter_in_arrayLiteral3317 = new BitSet(new long[]{0x0000000000000000L,0x0008080000000000L});
    public static final BitSet FOLLOW_107_in_arrayLiteral3320 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_arrayParameter_in_arrayLiteral3322 = new BitSet(new long[]{0x0000000000000000L,0x0008080000000000L});
    public static final BitSet FOLLOW_107_in_arrayLiteral3327 = new BitSet(new long[]{0x0000000000000000L,0x0008000000000000L});
    public static final BitSet FOLLOW_115_in_arrayLiteral3331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayParameter3365 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_arrayParameter3375 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_expression_in_arrayParameter3379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_122_in_nullLiteral3455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_exbase3474 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_namedOperationExpression_in_exbase3477 = new BitSet(new long[]{0x0000000000000000L,0x0000080000000400L});
    public static final BitSet FOLLOW_107_in_exbase3480 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_namedOperationExpression_in_exbase3482 = new BitSet(new long[]{0x0000000000000000L,0x0000080000000400L});
    public static final BitSet FOLLOW_GREATER_in_exbase3486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_123_in_dtbase3521 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_namedOperationExpression_in_dtbase3523 = new BitSet(new long[]{0x0000000000000000L,0x1000080000000000L});
    public static final BitSet FOLLOW_107_in_dtbase3526 = new BitSet(new long[]{0x7F00500000000000L,0x0F1602000000E20CL});
    public static final BitSet FOLLOW_namedOperationExpression_in_dtbase3528 = new BitSet(new long[]{0x0000000000000000L,0x1000080000000000L});
    public static final BitSet FOLLOW_124_in_dtbase3532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CMDARG_in_commandArg3561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_125_in_modifier3586 = new BitSet(new long[]{0x0000000000000002L});

}