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
 * AADL(Algebraic Accounting Description Language)
 *
 * AADL Tree Walker
 *
 * @version 1.70 2011/05/25
 **/
tree grammar AADLWalker;
options {
	language=Java;
	tokenVocab = AADL;
	ASTLabelType = CommonTree;
}

@header {
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
}

@members {
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
}

@rulecatch {
catch (CompileException ex) {
	reportError(ex);
	recover(input,null);
}
catch (RecognitionException re) {
	reportError(re);
	recover(input,re);
}
}

program
    :	programInfo programBody
    ;

programInfo
    :	^(AADL_INFO AADL_NAME {setClassName($AADL_NAME.text);} infoCmdArgs infoFuncDefs)
    ;

programBody
@init{
//--- begin [@init]
analyzer.getJavaProgram().addMainCode(ACodeModule.buildMainClassBegin(analyzer));
//--- end [@init]
}
@after{
//--- begin [@after]
analyzer.getJavaProgram().addMainCode(ACodeModule.buildMainClassEnd(analyzer));
//--- end [@after]
}
    :	^(AADL_PROGRAM
    		packageDeclaration?
    		(jha=javaHeaderAction{analyzer.getJavaProgram().addHeaderCode($jha.aAction);})*
    		constClassVariables?
    		subModule?
    		mm=mainModule{analyzer.getJavaProgram().addMainCode($mm.acode);}
    	)
    ;

packageDeclaration
@init{
//--- begin packageDeclaration [@init]
analyzer.beginDeclarationBlockParsing();
//--- end packageDeclaration [@init]
}
    :	^(ID_PACKAGE Identifier)
    		{analyzer.getJavaProgram().setPackageCode(ACodePackage.buildPackageDeclaration(analyzer, $ID_PACKAGE.token, $Identifier.token));}
    |	^(ID_PACKAGE javaClassPath)
    		{analyzer.getJavaProgram().setPackageCode(ACodePackage.buildPackageDeclaration(analyzer, $ID_PACKAGE.token, $javaClassPath.tokens));}
    ;
    finally {
	//--- begin packageDeclaration [@finally]
	analyzer.endDeclarationBlockParsing();
	//--- begin packageDeclaration [@finally]
    }

constClassVariables
    :	^(AADL_CONSTS constClassVariableDeclaration?)
    ;

constClassVariableDeclaration
@init{
//--- begin constClassVariableDeclaration [@init]
analyzer.beginDeclarationBlockParsing();
//--- end constClassVariableDeclaration [@init]
}
    :	(cv=constVariableDeclaration {analyzer.addConstructionCode($cv.stm);})+
    ;
    finally {
	//--- begin constClassVariableDeclaration [@finally]
	analyzer.endDeclarationBlockParsing();
	//--- begin constClassVariableDeclaration [@finally]
    }

subModule
    :	(	ja=javaAction	{analyzer.getJavaProgram().addMainCode($ja.aAction);}
    	|	fn=function	{analyzer.getJavaProgram().addMainCode($fn.acode);}
    	)+
    ;

// Analysis

infoCmdArgs
@init {
//--- begin [@init]
ArrayList<Token> aryArgs = new ArrayList<Token>();
//--- end [@init]
}
@after {
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
    :	^(AADL_ARGS (CMDARG{aryArgs.add($CMDARG.token);})*)
    ;

infoFuncDefs
@init {
//--- begin infoFuncDefs [@init]
analyzer.pushScope();
ArrayList<AADLFuncType> aryFuncs = new ArrayList<AADLFuncType>();
//--- end infoFuncDefs [@init]
}
@after {
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
    :	^(AADL_FUNCS (infoFuncTypes[aryFuncs])*)
    //:	^(AADL_FUNCS (functionType{aryFuncs.add($functionType.aFuncType.getFuncType());})*)
    ;
    finally {
	//--- begin infoFuncDefs [@finally]
	analyzer.popScope();
	//--- end infoFuncDefs [@finally]
    }

infoFuncTypes[ArrayList<AADLFuncType> funcs]
@init {
//--- begin infoFuncTypes [@init]
analyzer.pushScope();
//--- end infoFuncTypes [@init]
}
    :	functionType
    	{
    		if ($functionType.aFuncType != null) {
    			funcs.add($functionType.aFuncType.getFuncType());
    		}
    	}
    ;
    finally {
	//--- begin infoFuncTypes [@after]
	analyzer.popScope();
	//--- end infoFuncTypes [@after]
    }

// main module

mainModule returns [ACodeModule acode]
    :	^(PROGRAM Identifier mainBody)
    	{$acode = ACodeModule.buildMainModule(analyzer, $Identifier, $mainBody.aBlock);}
    ;

mainBody returns [ACodeBlock aBlock]
@init{
//--- begin mainBody [@init]
incrementIndent();
analyzer.beginProgramBlockParsing();
List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
//--- end mainBody [@init]
}
    :	^(BODY (statement{stmList.add($statement.stm);})*)
    	{$aBlock = ACodeBlock.withoutBlock(analyzer, $BODY, stmList);}
    ;
    finally {
	//--- begin mainBody [@finally]
	analyzer.endProgramBlockParsing();
	decrementIndent();
	//--- begin mainBody [@finally]
    }

// functions
//--- function Identifier(...)[:type] -> normal function
//--- function Identifier[...](...)[:type] -> ExBaseSet args function

//@@@ modified Y.Ishizuka : 2011.05.25
function returns [ACodeModule acode]
@init{
//--- begin function [@init]
analyzer.pushScope();
//--- end function [@init]
}
    :	^(FUNCTION modifiers functionType functionBody)
    	{$acode = ACodeModule.buildFunctionModule(analyzer, $modifiers.list, $FUNCTION, $functionType.aFuncType, $functionBody.aBlock);}
    ;
    finally {
	//--- begin function [@finally]
	analyzer.popScope();
	//--- end function [@finally]
    }
/*--- old codes : 2011.05.25 ---**
function returns [ACodeModule acode]
@init{
//--- begin function [@init]
analyzer.pushScope();
//--- end function [@init]
}
    :	^(FUNCTION functionType functionBody)
    	{$acode = ACodeModule.buildFunctionModule(analyzer, $FUNCTION, $functionType.aFuncType, $functionBody.aBlock);}
    ;
    finally {
	//--- begin function [@finally]
	analyzer.popScope();
	//--- end function [@finally]
    }
/*--- end of old codes : 2011.05.25 ---*/
//@@@ end of modified : 2011.05.25

//@@@ added by Y.Ishizuka : 2011.05.25
modifiers returns [List<Token> list]
@init{
//--- begin function [@init]
list = new ArrayList<Token>();
//--- end function [@init]
}
    :	^(MODIFIERS (
    	t=(
    		'public'
    	    |	'static'
    	) {list.add($t.token);}
    	)*)
    ;

//@@@ end of added : 2011.05.25

functionType returns [ACodeFunctionType aFuncType]
@init{
//--- begin [@init]
List<ACodeVariable> funcParams = new ArrayList<ACodeVariable>();
//--- end [@init]
}
@after{
//--- begin [@after]
//--- end [@after]
}
    :	^(Identifier functionReturnDeclaration? (functionFormalParameters[funcParams])?)
    	{$aFuncType = ACodeFunctionType.buildFunctionType(analyzer, $Identifier.token, $functionReturnDeclaration.aType, funcParams);}
    ;

functionReturnDeclaration returns [ACodeDataType aType]
    :	^(FUNCDEF_RETURN type)	{$aType = $type.aType;}
    ;

functionFormalParameters[List<ACodeVariable> params]
    :	^(FUNCDEF_PARAMS (variableDefinition {params.add($variableDefinition.aVarDef);})+)
    ;

functionBody returns [ACodeBlock aBlock]
@init{
//--- begin functionBody [@init]
incrementIndent();
List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
//--- end functionBody [@init]
}
    :	^(BODY (statement{stmList.add($statement.stm);})*)
    	{$aBlock = ACodeBlock.inBlock(analyzer, $BODY, stmList);}
    ;
    finally {
	//--- begin functionBody [@finally]
	decrementIndent();
	//--- begin functionBody [@finally]
    }

// Statement / Block

block returns [ACodeBlock aBlock]
@init{
//--- begin block [@init]
incrementIndent();
analyzer.pushScope();
List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
//--- end block [@init]
}
    :	^(BLOCK (statement{stmList.add($statement.stm);})*)
    	{$aBlock = ACodeBlock.inBlock(analyzer, $BLOCK, stmList);}
    ;
    finally {
	//--- begin block [@finally]
	analyzer.popScope();
	decrementIndent();
	//--- begin block [@finally]
    }


statement returns [ACodeStatement stm]
    :
    	involvingExpression		{$stm = ACodeControl.binExpressionWithoutTerm(analyzer, $involvingExpression.aInvolv);}
    |	block				{$stm = $block.aBlock;}
    |	if_statement			{$stm = $if_statement.stm;}
    |	localVariableDeclaration	{$stm = $localVariableDeclaration.stm;}
    |	constVariableDeclaration	{$stm = $constVariableDeclaration.stm;}
    |	assign_statement		{$stm = $assign_statement.stm;}
    |	break_statement			{$stm = $break_statement.stm;}
    |	jump_statement			{$stm = $jump_statement.stm;}
    |	expression	{$stm = ACodeControl.binExpression(analyzer, $expression.aObject);}
    ;

constVariableDeclaration returns [ACodeInitVariable stm]
    :	^(CONST ^(VARDEF vd=localVariableDeclarationRest[true]
    		( vi=localVariableInitialization[$vd.varDef] )
    	))
    	{$stm = $vi.stm;}
    ;

localVariableDeclaration returns [ACodeInitVariable stm]
    :	^(VARDEF vd=localVariableDeclarationRest[false]
    		( vi=localVariableInitialization[$vd.varDef] )
    	)
    	{$stm = $vi.stm;}
    ;

localVariableDeclarationRest[boolean isConst] returns [ACodeVariable varDef]
    :	Identifier type
    	{$varDef = ACodeVariable.buildValiableDefinition(analyzer, isConst, $Identifier.token, $type.aType);}
    ;

localVariableInitialization[ACodeVariable varDef] returns [ACodeInitVariable stm]
@init{
//--- begin [@init]
List<ACodeObject> args = new ArrayList<ACodeObject>();
//--- end [@init]
}
    :	^(vi=VARINIT exp=expression)
		{$stm = ACodeInitVariable.initByExpression(analyzer, varDef, $vi, $exp.aObject);}
    |	^(vi=VARINIT involvingExpression)
		{$stm = ACodeInitVariable.initByInvolv(analyzer, varDef, $vi, $involvingExpression.aInvolv);}
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    |	^(FINPUT fd=fileDeclarator)
		{$stm = ACodeInitVariable.initByFile(analyzer, varDef, $FINPUT.token, $fd.fileType, $fd.fileArg, $fd.encoding);}
**--- end of old codes : 2010.07.12 ---*/
    |	^(FINPUT fd=fileDeclarator)
		{$stm = ACodeInitVariable.initByFile(analyzer, varDef, $FINPUT.token, $fd.fileType, $fd.fileArg, $fd.encoding, $fd.foptions);}
//@@@ end of modified : 2010.07.12
    |	^(VARNEW (exp=expression{args.add($exp.aObject);})*)
		{$stm = ACodeInitVariable.construct(analyzer, varDef, args);}
    |
		{$stm = ACodeInitVariable.construct(analyzer, varDef, args);}
    ;


assign_statement returns [ACodeAssign stm]
@init{
//--- begin [@init]
ACodeVariable varRef = null;
//--- end [@init]
}
    :	^(Identifier {varRef = ACodeVariable.buildVariableRef(analyzer, $Identifier.token);}
		(asr=assign_rest[varRef])
    	)
    	{$stm = $asr.stm;}
    ;

assign_rest[ACodeVariable varRef] returns [ACodeAssign stm]
    :	^(as=ASSIGN expression)
		{$stm = ACodeAssign.assignExpression(analyzer, varRef, $as, $expression.aObject);}
    |	^(as=ASSIGN involvingExpression)
		{$stm = ACodeAssign.assignInvolv(analyzer, varRef, $as, $involvingExpression.aInvolv);}
//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
    |	^(FINPUT fd=fileDeclarator)
		{$stm = ACodeAssign.assignFile(analyzer, varRef, $FINPUT.token, $fd.fileType, $fd.fileArg, $fd.encoding);}
    |	^(FOUTPUT fd=fileDeclarator)
		{$stm = ACodeAssign.assignFile(analyzer, varRef, $FOUTPUT.token, $fd.fileType, $fd.fileArg, $fd.encoding);}
**--- end of old codes : 2010.07.12 ---*/
    |	^(FINPUT fd=fileDeclarator)
		{$stm = ACodeAssign.assignFile(analyzer, varRef, $FINPUT.token, $fd.fileType, $fd.fileArg, $fd.encoding, $fd.foptions);}
    |	^(FOUTPUT fd=fileDeclarator)
		{$stm = ACodeAssign.assignFile(analyzer, varRef, $FOUTPUT.token, $fd.fileType, $fd.fileArg, $fd.encoding, $fd.foptions);}
//@@@ end of modified : 2010.07.12
    ;

break_statement returns [ACodeControl stm]
    :	^(BREAK Identifier?)
		{$stm = ACodeControl.ctrlBreak(analyzer, $BREAK, $Identifier);}
    ;

jump_statement returns [ACodeControl stm]
    :	^(RETURN expression?)
    	{$stm = ACodeControl.ctrlReturn(analyzer, $RETURN, $expression.aObject);}
    ;

if_statement returns [ACodeControl stm]
    :	^(SIF cd=expression th=ifelse_statement_rest el=else_statement?)
    	{$stm = ACodeControl.ctrlIf(analyzer, $SIF, $cd.aObject, $th.stm, $el.stm);}
    ;

else_statement returns [ACodeControl stm]
    :	^(SELSE es=ifelse_statement_rest)
    	{$stm = ACodeControl.ctrlElse(analyzer, $SELSE, $es.stm);}
    ;

ifelse_statement_rest returns [ACodeStatement stm]
@init{
//--- begin ifelse_statement_rest [@init]
analyzer.pushScope();
//--- end ifelse_statement_rest [@init]
}
    :	statement	{$stm = $statement.stm;}
    ;
    finally {
	//--- begin ifelse_statement_rest [@finally]
	analyzer.popScope();
	//--- begin ifelse_statement_rest [@finally]
    }

// Expressions

involvingExpression returns [ACodeInvolving aInvolv]
@init{
//--- begin involv [@init]
analyzer.pushScope();
analyzer.beginInvolvingBlockParsing();
List<ACodeObject> filters = new ArrayList<ACodeObject>();
//--- end involv [@init]
}
    :	^(INVOLV ^(INVOLV_COND (fe=filterExpression{filters.add($fe.aObject);})*) exp=expression)
    	{$aInvolv = ACodeInvolving.buildInvolving(analyzer, $INVOLV, $exp.aObject, $INVOLV_COND, filters);}
    |	^(VOIDINVOLV ^(INVOLV_COND (fe=filterExpression{filters.add($fe.aObject);})*) exp=expression)
    	{$aInvolv = ACodeInvolving.buildInvolving(analyzer, $VOIDINVOLV, $exp.aObject, $INVOLV_COND, filters);}
    ;
    finally {
	//--- begin involv [@finally]
	analyzer.endInvolvingBlockParsing();
	analyzer.popScope();
	//--- end involv [@finally]
    }

filterExpression returns [ACodeObject aObject]
    :	iterateExpression	{$aObject = $iterateExpression.aIt;}
    |	aliasExpression		{$aObject = $aliasExpression.aAlias;}
    |	expression		{$aObject = $expression.aObject;}
    |	involvingBlock		{$aObject = $involvingBlock.aBlock;}
    ;

involvingBlock returns [ACodeBlock aBlock]
@init{
//--- begin block [@init]
incrementIndent();
analyzer.pushScope();
List<ACodeStatement> stmList = new ArrayList<ACodeStatement>();
//--- end block [@init]
}
@after{
//--- begin block [@after]
analyzer.popScope();
decrementIndent();
//--- begin block [@after]
}
    :	^(INVOLV_BLOCK (statement{stmList.add($statement.stm);})*)
    	{$aBlock = ACodeBlock.inBlock(analyzer, $INVOLV_BLOCK, stmList);}
    ;

iterateExpression returns [ACodeInvolvIterator aIt]
    :	^(ITERATE en=Identifier it=iterateExpressionRest[$en])
    	{$aIt = $it.aIt;}
    ;

iterateExpressionRest[CommonTree en] returns [ACodeInvolvIterator aIt]
    :
    	fd=fileDeclarator
		{$aIt = ACodeInvolvIterator.buildReaderIterator(analyzer, en.token, $fd.fileType, $fd.fileArg, $fd.encoding, $fd.foptions);}
    |	ln=Identifier
		{$aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, ACodeVariable.buildVariableRef(analyzer, $ln.token));}
    |	al=arrayLiteral
		{$aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, $al.aLiteral);}
//@@@ added by Y.Ishizuka : 2011.05.17
    |	mc=methodCall
		{$aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, $mc.aFuncCall);}
//@@@ end of added : 2011.05.17
    |	ie=involvingExpression
		{$aIt = ACodeInvolvIterator.buildIterator(analyzer, en.token, $ie.aInvolv);}
    ;

aliasExpression returns [ACodeInvolvAlias aAlias]
    :	^(ALIAS Identifier tp=type? expression)
    	{$aAlias = ACodeInvolvAlias.buildAlias(analyzer, $Identifier.token, $tp.aType, $expression.aObject);}
    ;

expression returns [ACodeObject aObject]
    :	exp=newExalgeExpression		{$aObject = $exp.aObject;}
    |	exp=conditionalAndOrExpression	{$aObject = $exp.aObject;}
    |	exp=conditionalExpression	{$aObject = $exp.aObject;}
    |	exp=namedOperationExpression	{$aObject = $exp.aObject;}
    |	exp=mathOperationExpression	{$aObject = $exp.aObject;}
    |	exp=plusExpression		{$aObject = $exp.aObject;}
    |	exp=minusExpression		{$aObject = $exp.aObject;}
    |	exp=simpleExpression		{$aObject = $exp.aObject;}
    |	exp=specialExpression		{$aObject = $exp.aObject;}
    |	exp=primaryExpression		{$aObject = $exp.aObject;}
    ;

specialExpression returns [ACodeObject aObject]
    :	^(op='?' cd=expression rt=expression rf=expression)
    	{$aObject = ($cd.aObject!=null ? $cd.aObject.conditionalAssign(analyzer, $op.token, $rt.aObject, $rf.aObject) : null);}
    ;

conditionalAndOrExpression returns [ACodeObject aObject]
    :	^(op=('||'|'&&') a1=expression a2=expression)
    	{$aObject = ($a1.aObject!=null ? $a1.aObject.conditionalAndOr(analyzer, $op.token, $a2.aObject) : null);}
    ;

conditionalExpression returns [ACodeObject aObject]
    :	^(op='==' a1=expression a2=expression)
    		{$aObject = ($a1.aObject!=null ? $a1.aObject.equal(analyzer, $op.token, $a2.aObject) : null);}
    |	^(op='!=' a1=expression a2=expression)
    		{$aObject = ($a1.aObject!=null ? $a1.aObject.notEqual(analyzer, $op.token, $a2.aObject) : null);}
    |	^(op=('<='|'>='|'<'|'>') a1=expression a2=expression)
    		{$aObject = ($a1.aObject!=null ? $a1.aObject.compare(analyzer, $op.token, $a2.aObject) : null);}
    ;

namedOperationExpression returns [ACodeObject aObject]
    :	^(NamedOperator a1=expression a2=expression)
    	{$aObject = ($a1.aObject!=null ? $a1.aObject.namedOperation(analyzer, $NamedOperator.token, $a2.aObject) : null);}
    ;

newExalgeExpression returns [ACodeObject aObject]
    :	^(op='@' a1=expression a2=expression)
    	{$aObject = ($a1.aObject!=null ? $a1.aObject.cat(analyzer, $op.token, $a2.aObject) : null);}
    ;

mathOperationExpression returns [ACodeObject aObject]
    :	^(op='*' a1=expression a2=expression)
    		{$aObject = ($a1.aObject!=null ? $a1.aObject.multiple(analyzer, $op.token, $a2.aObject) : null);}
    |	^(op='/' a1=expression a2=expression)
    		{$aObject = ($a1.aObject!=null ? $a1.aObject.divide(analyzer, $op.token, $a2.aObject) : null);}
    |	^(op='%' a1=expression a2=expression)
    		{$aObject = ($a1.aObject!=null ? $a1.aObject.mod(analyzer, $op.token, $a2.aObject) : null);}
    ;

plusExpression returns [ACodeObject aObject]
    :	^(op='+' expr=expression ret=plusExpressionRest[$op,$expr.aObject])
    	{$aObject = $ret.aObject;}
    ;

plusExpressionRest[CommonTree op, ACodeObject expr] returns [ACodeObject aObject]
    :	expression	{$aObject = (expr!=null ? expr.add(analyzer, op.token, $expression.aObject) : null);}
    |			{$aObject = (expr!=null ? expr.plus(analyzer, op.token) : null);}
    ;


minusExpression returns [ACodeObject aObject]
    :	^(op='-' expr=expression ret=minusExpressionRest[$op,$expr.aObject])
    	{$aObject = $ret.aObject;}
    ;

minusExpressionRest[CommonTree op, ACodeObject expr] returns [ACodeObject aObject]
    :	expression	{$aObject = (expr!=null ? expr.subtract(analyzer, op.token, $expression.aObject) : null);}
    |			{$aObject = (expr!=null ? expr.negate(analyzer, op.token) : null);}
    ;

simpleExpression returns [ACodeObject aObject]
    :	^(op='!' a1=expression)	{$aObject = ($a1.aObject!=null ? $a1.aObject.not(analyzer, $op.token) : null);}
    |	^(op='~' a1=expression)	{$aObject = ($a1.aObject!=null ? $a1.aObject.bar(analyzer, $op.token) : null);}
    |	^(op='^' a1=expression)	{$aObject = ($a1.aObject!=null ? $a1.aObject.hat(analyzer, $op.token) : null);}
    ;

primaryExpression returns [ACodeObject aObject]
    :	primary	{$aObject = $primary.aObject;}
    	(selector[$aObject] {$aObject = $selector.aObject;})*
    ;

// Primary

primary returns [ACodeObject aObject]
    :	arrayLiteral		{$aObject = $arrayLiteral.aLiteral;}
//@@@ added by Y.Ishziuka : 2010.07.12
    |	HexLiteral		{$aObject = ACodeLiteral.buildHexInteger(analyzer, $HexLiteral);}
    |	OctalLiteral		{$aObject = ACodeLiteral.buildOctalInteger(analyzer, $OctalLiteral);}
//@@@ end of added : 2010.07.12
    |	IntegerLiteral		{$aObject = ACodeLiteral.buildInteger(analyzer, $IntegerLiteral);}
    |	FloatingPointLiteral	{$aObject = ACodeLiteral.buildDecimal(analyzer, $FloatingPointLiteral);}
    |	nullLiteral		{$aObject = $nullLiteral.aLiteral;}
    |	booleanLiteral		{$aObject = $booleanLiteral.aLiteral;}
//@@@ added by Y.Ishizuka : 2010.07.12
    |	CharacterLiteral	{$aObject = ACodeLiteral.buildCharacter(analyzer, $CharacterLiteral);}
//@@@ end of added : 2010.07.12
    |	StringLiteral		{$aObject = ACodeLiteral.buildString(analyzer, $StringLiteral);}
    |	exbase			{$aObject = $exbase.aLiteral;}
    |	dtbase			{$aObject = $dtbase.aLiteral;}
    |	methodCall		{$aObject = $methodCall.aFuncCall;}
    |	javaAction		{$aObject = $javaAction.aAction;}
    |	Identifier		{$aObject = ACodeVariable.buildVariableRef(analyzer, $Identifier.token);}
    |	CMDARG			{$aObject = ACodeVariable.buildVariableRef(analyzer, $CMDARG.token);}
    ;

selector[ACodeObject prm] returns [ACodeObject aObject]
@init{
//--- begin [@init]
List<ACodeObject> args = new ArrayList<ACodeObject>();
//--- end [@init]
}
    :	^(SCOPE Identifier)
    		{$aObject = ($prm!=null ? $prm.memberField(analyzer, $Identifier.token) : null);}
    |	^(SCOPE ^(FCALL_RM Identifier ^(FUNCALL_ARGS (expression{args.add($expression.aObject);})*)))
    		{$aObject = ($prm!=null ? $prm.memberMethod(analyzer, $Identifier.token, args) : null);}
    ;

methodCall returns [ACodeFuncCall aFuncCall]
    :	fc=callSystemMethod	{$aFuncCall = $fc.aFuncCall;}
//    :	fc=callDirectCastMethod	{$aFuncCall = $fc.aFuncCall;}
    |	fc=callRegisteredMethod	{$aFuncCall = $fc.aFuncCall;}
    |	fc=callSpecialMethod	{$aFuncCall = $fc.aFuncCall;}
    |	fc=callModuleMethod	{$aFuncCall = $fc.aFuncCall;}
    |	fc=callInstanceMethod	{$aFuncCall = $fc.aFuncCall;}
    ;

//@@@ added by Y.Ishizuka : 2011.05.25
callSystemMethod returns [ACodeFuncCall aFuncCall]
@init{
//--- begin [@init]
//--- end [@init]
}
    :	^(cs='cast' type expression)
    	{$aFuncCall = ACodeFuncCall.buildDirectCast(analyzer, $cs.token, $type.aType, $expression.aObject);}
    |	^(ia='typeof' type expression)
    	{$aFuncCall = ACodeFuncCall.buildInstanceOf(analyzer, $ia.token, $type.aType, $expression.aObject);}
    ;
//@@@ end of added : 2011.05.25

/*@@@ deleted by Y.Ishizuka : 2011.05.25
callDirectCastMethod returns [ACodeFuncCall aFuncCall]
@init{
//--- begin [@init]
//--- end [@init]
}
    :	^(cs='cast' type expression)
    	{$aFuncCall = ACodeFuncCall.buildDirectCast(analyzer, $cs.token, $type.aType, $expression.aObject);}
    ;
**@@@ end of deleted : 2011.05.25 */

callRegisteredMethod returns [ACodeFuncCall aFuncCall]
@init{
//--- begin [@init]
List<ACodeObject> args = new ArrayList<ACodeObject>();
//--- end [@init]
}
    :	^(FCALL_RM Identifier ^(FUNCALL_ARGS (expression{args.add($expression.aObject);})*))
    	{$aFuncCall = ACodeFuncCall.buildNormalCall(analyzer, $Identifier.token, args);}
    ;

callSpecialMethod returns [ACodeFuncCall aFuncCall]
@init{
//--- begin [@init]
List<ACodeObject> args = new ArrayList<ACodeObject>();
//--- end [@init]
}
    :	^(FCALL_SM Identifier
    		^(FUNCALL_PATH tgt=expression)
    		^(FUNCALL_ARGS (a=expression{args.add($a.aObject);})*))
    	{$aFuncCall = ACodeFuncCall.buildSpecialCall(analyzer, $Identifier.token, $tgt.aObject, args);}
    ;

callModuleMethod returns [ACodeFuncCall aFuncCall]
@init{
//--- begin [@init]
List<ACodeObject> args = new ArrayList<ACodeObject>();
//--- end [@init]
}
    :	^(FCALL_MM ^(FUNCALL_PATH tgt=callModuleMethodPath) Identifier ^(FUNCALL_ARGS (a=expression{args.add($a.aObject);})*))
    	{$aFuncCall = ACodeFuncCall.buildModuleMethodCall(analyzer, $Identifier.token, $tgt.aType, args);}
    ;

callModuleMethodPath returns [ACodeDataType aType]
    :	Identifier	{$aType = ACodeDataType.buildType(analyzer, $Identifier.token);}
    |	javaClassPath	{$aType = ACodeDataType.buildType(analyzer, $javaClassPath.tokens);}
    ;

callInstanceMethod returns [ACodeFuncCall aFuncCall]
@init{
//--- begin [@init]
List<ACodeObject> args = new ArrayList<ACodeObject>();
//--- end [@init]
}
    :	^(FCALL_IM ^(FUNCALL_PATH tgt=expression) Identifier ^(FUNCALL_ARGS (a=expression{args.add($a.aObject);})*))
    	{$aFuncCall = ACodeFuncCall.buildInstanceMethodCall(analyzer, $Identifier.token, $tgt.aObject, args);}
    ;

javaHeaderAction returns [ACodeJavaAction aAction]
    :	JAVA_HEADER_ACTION
    	{$aAction = ACodeJavaAction.buildJavaHeaderAction(analyzer, $JAVA_HEADER_ACTION);}
    ;

javaAction returns [ACodeJavaAction aAction]
    :	JAVA_ACTION
    	{$aAction = ACodeJavaAction.buildJavaAction(analyzer, $JAVA_ACTION);}
    ;

// Types

//@@@ modified by Y.Ishizuka : 2010.07.12
/*--- old codes : 2010.07.12 ---
fileDeclarator returns [Token fileType, ACodeObject fileArg, ACodeObject encoding]
    :	^(ft='txtFile' {$fileType=$ft.token;} fa=expression {$fileArg=$fa.aObject;} (enc=expression{$encoding=$enc.aObject;})?)
    |	^(ft='csvFile' {$fileType=$ft.token;} fa=expression {$fileArg=$fa.aObject;} (enc=expression{$encoding=$enc.aObject;})?)
    |	^(ft='xmlFile' {$fileType=$ft.token;} fa=expression {$fileArg=$fa.aObject;})
    ;
**--- end of old codes : 2010.07.12 ---*/
fileDeclarator returns [Token fileType, ACodeObject fileArg, ACodeObject encoding, List<AADLFileOptionEntry> foptions]
@init{
//--- begin [@init]
$foptions = new ArrayList<AADLFileOptionEntry>();
//--- end [@init]
}
    :	^(ft='txtFile' {$fileType=$ft.token;} (fo=fileDeclaratorOptionEntry{$foptions.add($fo.optEntry);})* fa=expression {$fileArg=$fa.aObject;} (enc=expression{$encoding=$enc.aObject;})?)
    |	^(ft='csvFile' {$fileType=$ft.token;} (fo=fileDeclaratorOptionEntry{$foptions.add($fo.optEntry);})* fa=expression {$fileArg=$fa.aObject;} (enc=expression{$encoding=$enc.aObject;})?)
    |	^(ft='xmlFile' {$fileType=$ft.token;} (fo=fileDeclaratorOptionEntry{$foptions.add($fo.optEntry);})* fa=expression {$fileArg=$fa.aObject;})
    ;
//@@@ end of modified : 2010.07.12

//@@@ added by Y.Ishizuka : 2010.07.12
fileDeclaratorOptionEntry returns [AADLFileOptionEntry optEntry]
    :	^(FOPTION Identifier expression?)
    	{$optEntry = new AADLFileOptionEntry($Identifier.token, $expression.aObject);}
    ;
//@@@ end of added : 2010.07.12

variableDefinition returns [ACodeVariable aVarDef]
    :	^(VARDEF Identifier type)
    	{$aVarDef = ACodeVariable.buildValiableDefinition(analyzer, false, $Identifier.token, $type.aType);}
    ;

type returns [ACodeDataType aType]
    :	^(VARTYPE Identifier)		{$aType = ACodeDataType.buildType(analyzer, $Identifier.token);}
    |	^(VARTYPE javaClassPath)	{$aType = ACodeDataType.buildType(analyzer, $javaClassPath.tokens);}
    ;

javaClassPath returns [List<Token> tokens]
@init{
//--- begin [@init]
List<Token> ids = new ArrayList<Token>();
//--- end [@init]
}
    :	^(JAVACLASSPATH (Identifier{ids.add($Identifier.token);})+)
		{$tokens = ids;}
    ;

// Literal

arrayLiteral returns [ACodeLiteral aLiteral]
@init{
//--- begin exbase[@init]
List<ACodeObject> expKeys = new ArrayList<ACodeObject>();
List<ACodeObject> expVals = new ArrayList<ACodeObject>();
//--- end exbase[@init]
}
    :	^(ARRAY (al=expression{expVals.add($al.aObject);})+)
    		{ $aLiteral = ACodeLiteral.buildArrayLiteral(analyzer, $ARRAY, expVals); }
    |	^(ARRAY (^(HASH hk=expression{expKeys.add($hk.aObject);} hv=expression{expVals.add($hv.aObject);}))+)
    		{ $aLiteral = ACodeLiteral.buildMapLiteral(analyzer, $ARRAY, expKeys, expVals); }
    ;

booleanLiteral returns [ACodeLiteral aLiteral]
    :	bt='true'	{$aLiteral = ACodeLiteral.buildBoolean(analyzer, $bt);}
    |	bf='false'	{$aLiteral = ACodeLiteral.buildBoolean(analyzer, $bf);}
    ;

nullLiteral returns [ACodeLiteral aLiteral]
    :	nl='null'	{$aLiteral = ACodeLiteral.buildNull(analyzer, $nl);}
    ;

exbase returns [ACodeLiteral aLiteral]
@init{
//--- begin exbase[@init]
List<ACodeObject> codes = new ArrayList<ACodeObject>();
//--- end exbase[@init]
}
    :	^(EXBASE (expression{codes.add($expression.aObject);})+)
    		{$aLiteral = ACodeLiteral.buildExBase(analyzer, $EXBASE, codes);}
    ;

dtbase returns [ACodeLiteral aLiteral]
@init{
//--- begin exbase[@init]
List<ACodeObject> codes = new ArrayList<ACodeObject>();
//--- end exbase[@init]
}
    :	^(DTBASE (expression{codes.add($expression.aObject);})+)
    		{$aLiteral = ACodeLiteral.buildDtBase(analyzer, $DTBASE, codes);}
    ;
