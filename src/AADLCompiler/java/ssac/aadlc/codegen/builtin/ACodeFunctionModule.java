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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ACodeFunctionModule.java	2.2.0	2015/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	2.0.0	2014/03/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.90	2013/08/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.80	2012/05/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.60	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.40	2010/03/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.22	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.21	2009/01/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.21	2008/12/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.20	2008/10/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.10	2008/05/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionModule.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.builtin;

import ssac.aadlc.analysis.type.AADLCollectionType;
import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLObject;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLTypeManager;
import ssac.aadlc.analysis.type.func.AADLFuncType;
import ssac.aadlc.analysis.type.prim.APrimBoolean;
import ssac.aadlc.analysis.type.prim.APrimBooleanList;
import ssac.aadlc.analysis.type.prim.APrimCsvFileReader;
import ssac.aadlc.analysis.type.prim.APrimCsvFileWriter;
import ssac.aadlc.analysis.type.prim.APrimDecimal;
import ssac.aadlc.analysis.type.prim.APrimDecimalList;
import ssac.aadlc.analysis.type.prim.APrimDecimalRange;
import ssac.aadlc.analysis.type.prim.APrimDtAlgeSet;
import ssac.aadlc.analysis.type.prim.APrimDtBase;
import ssac.aadlc.analysis.type.prim.APrimDtBasePattern;
import ssac.aadlc.analysis.type.prim.APrimDtBasePatternSet;
import ssac.aadlc.analysis.type.prim.APrimDtBaseSet;
import ssac.aadlc.analysis.type.prim.APrimDtStringThesaurus;
import ssac.aadlc.analysis.type.prim.APrimDtalge;
import ssac.aadlc.analysis.type.prim.APrimExAlgeSet;
import ssac.aadlc.analysis.type.prim.APrimExBase;
import ssac.aadlc.analysis.type.prim.APrimExBasePattern;
import ssac.aadlc.analysis.type.prim.APrimExBasePatternSet;
import ssac.aadlc.analysis.type.prim.APrimExBaseSet;
import ssac.aadlc.analysis.type.prim.APrimExTransfer;
import ssac.aadlc.analysis.type.prim.APrimExalge;
import ssac.aadlc.analysis.type.prim.APrimMqttCsvParameter;
import ssac.aadlc.analysis.type.prim.APrimNaturalNumberDecimalRange;
import ssac.aadlc.analysis.type.prim.APrimSimpleDecimalRange;
import ssac.aadlc.analysis.type.prim.APrimString;
import ssac.aadlc.analysis.type.prim.APrimStringList;
import ssac.aadlc.analysis.type.prim.APrimTextFileReader;
import ssac.aadlc.analysis.type.prim.APrimTextFileWriter;
import ssac.aadlc.analysis.type.prim.APrimTransMatrix;
import ssac.aadlc.analysis.type.prim.APrimTransTable;
import ssac.aadlc.codegen.ACodeModule;

/**
 * AADL組み込み関数定義。
 * <br>
 * 現在のバージョンでは、AADL組み込み関数となるJAVAコードは、
 * コンパイルによって転換されたJAVAコードのメソッドとして
 * 埋め込まれる。
 * 
 * @version 2.2.0	2015/05/29
 */
public class ACodeFunctionModule extends ACodeModule
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static protected final AADLType AADLTypeJava_int = AADLTypeManager.getTypeByJavaClass(int.class);
	
	static protected final String[] EMPTY_CODES = {};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	static public final ACodeFunctionModule[] builtinFuncs = {
			new ACodeFunctionModule(new AADLFuncType("isNull", APrimBoolean.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimTransTable.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimTransMatrix.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimExTransfer.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance,
					new AADLCollectionType("Collection", java.util.Collection.class, AADLObject.instance))),
			new ACodeFunctionModule(new AADLFuncType("isNoHat", APrimBoolean.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("isHat", APrimBoolean.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getNameKey", APrimString.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getUnitKey", APrimString.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTimeKey", APrimString.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getSubjectKey", APrimString.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBases", APrimExBaseSet.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBases", APrimExBaseSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getOneBase", APrimExBase.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("removeHat", APrimExBase.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("removeHat", APrimExBaseSet.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("setHat", APrimExBase.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("setHat", APrimExBaseSet.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getNoHatBases", APrimExBaseSet.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getNoHatBases", APrimExBaseSet.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getNoHatBases", APrimExBaseSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getHatBases", APrimExBaseSet.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getHatBases", APrimExBaseSet.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getHatBases", APrimExBaseSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBasesWithRemoveHat", APrimExBaseSet.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBasesWithRemoveHat", APrimExBaseSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBasesWithSetHat", APrimExBaseSet.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBasesWithSetHat", APrimExBaseSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExBasePattern", APrimExBasePattern.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExBasePattern", APrimExBasePattern.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExBasePattern", APrimExBasePattern.instance, APrimString.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExBasePattern", APrimExBasePattern.instance, APrimString.instance, APrimString.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExBasePattern", APrimExBasePattern.instance, APrimStringList.instance)),
			new ACodeFunctionModule(new AADLFuncType("getNameKey", APrimString.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getUnitKey", APrimString.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTimeKey", APrimString.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getSubjectKey", APrimString.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("toExBasePattern", APrimExBasePattern.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("toExBasePattern", APrimExBasePatternSet.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("toExBase", APrimExBase.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("toExBase", APrimExBaseSet.instance, APrimExBasePatternSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("inverse", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("invElement", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("normalize", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("norm", APrimDecimal.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("sum", APrimExalge.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("sum", APrimDecimal.instance, APrimDecimalList.instance)),
			new ACodeFunctionModule(new AADLFuncType("aggreTransfer", APrimExalge.instance,
													APrimExalge.instance, APrimTransTable.instance)),
			new ACodeFunctionModule(new AADLFuncType("aggreTransfer", APrimExalge.instance,
													APrimExalge.instance, APrimExBase.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("aggreTransfer", APrimExalge.instance,
													APrimExalge.instance, APrimExBasePattern.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("aggreTransfer", APrimExalge.instance,
													APrimExalge.instance, APrimExBaseSet.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("aggreTransfer", APrimExalge.instance,
													APrimExalge.instance, APrimExBasePatternSet.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExalge.instance,
													APrimExalge.instance, APrimExBase.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExalge.instance,
													APrimExalge.instance, APrimExBasePattern.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExalge.instance,
													APrimExalge.instance, APrimExBaseSet.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExalge.instance,
													APrimExalge.instance, APrimExBasePatternSet.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("divideTransfer", APrimExalge.instance,
													APrimExalge.instance, APrimTransMatrix.instance)),
			new ACodeFunctionModule(new AADLFuncType("transfer", APrimExalge.instance,
													APrimExalge.instance, APrimExTransfer.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExBase.instance,
													APrimTransTable.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExBaseSet.instance,
													APrimTransTable.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferFromPatterns",
													APrimExBasePatternSet.instance, APrimTransTable.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferToPatterns",
													APrimExBasePatternSet.instance, APrimTransTable.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExBaseSet.instance,
													APrimTransMatrix.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExBaseSet.instance,
													APrimTransMatrix.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferFromPatterns",
													APrimExBasePatternSet.instance, APrimTransMatrix.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferToPatterns",
													APrimExBasePatternSet.instance, APrimTransMatrix.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExBaseSet.instance,
													APrimExTransfer.instance, APrimExBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("transform", APrimExBaseSet.instance,
													APrimExTransfer.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferFromPatterns",
													APrimExBasePatternSet.instance, APrimExTransfer.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferToPatterns",
													APrimExBasePatternSet.instance, APrimExTransfer.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferTotalValue", APrimDecimal.instance,
													APrimExTransfer.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferValue", APrimDecimal.instance,
													APrimExTransfer.instance, APrimExBasePattern.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTransferAttribute", APrimString.instance,
													APrimExTransfer.instance, APrimExBasePattern.instance, APrimExBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("setDecimalScale", APrimDecimal.instance,
													APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("getDecimalScale", APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("canConvertToDoubleValue", APrimBoolean.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("doubleValue", APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("bigpow", APrimDecimal.instance,
													APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("pow", APrimDecimal.instance,
													APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("sqrt", APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("abs", APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("min", APrimDecimal.instance,
													APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("max", APrimDecimal.instance,
													APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("ceil", APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("floor", APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("isDecimal", APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDecimal", APrimDecimal.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDecimal", APrimDecimal.instance, AADLJavaClass.JavaByte)),
			new ACodeFunctionModule(new AADLFuncType("toDecimal", APrimDecimal.instance, AADLJavaClass.JavaShort)),
			new ACodeFunctionModule(new AADLFuncType("toDecimal", APrimDecimal.instance, AADLJavaClass.JavaInt)),
			new ACodeFunctionModule(new AADLFuncType("toDecimal", APrimDecimal.instance, AADLJavaClass.JavaLong)),
			new ACodeFunctionModule(new AADLFuncType("toDecimal", APrimDecimal.instance, AADLJavaClass.JavaFloat)),
			new ACodeFunctionModule(new AADLFuncType("toDecimal", APrimDecimal.instance, AADLJavaClass.JavaDouble)),
			new ACodeFunctionModule(new AADLFuncType("toString", APrimString.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("print", APrimBoolean.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("println", APrimBoolean.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("println", APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("errorprint", APrimBoolean.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("errorprintln", APrimBoolean.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("errorprintln", APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("xmlTransform", null, APrimString.instance, APrimString.instance, APrimString.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("exec", AADLTypeJava_int, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("exec", AADLTypeJava_int, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("exec", AADLTypeJava_int, APrimStringList.instance)),
			new ACodeFunctionModule(new AADLFuncType("exec", AADLTypeJava_int, APrimStringList.instance, APrimString.instance)),
			
			// @since 1.40
			new ACodeFunctionModule(new AADLFuncType("_sort", null, new AADLListType(AADLObject.instance))),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimDtStringThesaurus.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBases", APrimDtBaseSet.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBases", APrimDtBaseSet.instance, APrimDtAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("getOneBase", APrimDtBase.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getOneValue", AADLObject.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("getNameKey", APrimString.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTypeKey", APrimString.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getAttributeKey", APrimString.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getSubjectKey", APrimString.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("getNameKey", APrimString.instance, APrimDtBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getTypeKey", APrimString.instance, APrimDtBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getAttributeKey", APrimString.instance, APrimDtBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("getSubjectKey", APrimString.instance, APrimDtBasePattern.instance)),
			new ACodeFunctionModule(new AADLFuncType("newDtBasePattern", APrimDtBasePattern.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newDtBasePattern", APrimDtBasePattern.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newDtBasePattern", APrimDtBasePattern.instance, APrimString.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newDtBasePattern", APrimDtBasePattern.instance, APrimString.instance, APrimString.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newDtBasePattern", APrimDtBasePattern.instance, APrimStringList.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDtBasePattern", APrimDtBasePattern.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDtBasePattern", APrimDtBasePatternSet.instance, APrimDtBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("normalize", APrimDtalge.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("thesconv", APrimDtalge.instance, APrimDtalge.instance, APrimDtBase.instance, AADLObject.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("thesconv", APrimDtalge.instance, APrimDtalge.instance, APrimDtBase.instance, APrimDtStringThesaurus.instance, APrimStringList.instance)),
			new ACodeFunctionModule(new AADLFuncType("containsNull", APrimBoolean.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("containsNull", APrimBoolean.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("nullProj", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("nullProj", APrimExAlgeSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("nullProj", APrimDtalge.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("nullProj", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("nonullProj", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("nonullProj", APrimExAlgeSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("nonullProj", APrimDtalge.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("nonullProj", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtalge.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtalge.instance,
													APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtAlgeSet.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtAlgeSet.instance,
													APrimString.instance, APrimString.instance)),
			
			// @since 1.50
			new ACodeFunctionModule(new AADLFuncType("newTextFileReader", APrimTextFileReader.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newTextFileReader", APrimTextFileReader.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newTextFileWriter", APrimTextFileWriter.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newTextFileWriter", APrimTextFileWriter.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newCsvFileReader", APrimCsvFileReader.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newCsvFileReader", APrimCsvFileReader.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newCsvFileWriter", APrimCsvFileWriter.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newCsvFileWriter", APrimCsvFileWriter.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("closeReader", null, APrimTextFileReader.instance)),
			new ACodeFunctionModule(new AADLFuncType("closeReader", null, APrimCsvFileReader.instance)),
			new ACodeFunctionModule(new AADLFuncType("closeWriter", APrimBoolean.instance, APrimTextFileWriter.instance)),
			new ACodeFunctionModule(new AADLFuncType("closeWriter", APrimBoolean.instance, APrimCsvFileWriter.instance)),
			
			// @since 1.60
			new ACodeFunctionModule(new AADLFuncType("isEqual", APrimBoolean.instance, AADLObject.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("getThesaurusParents", APrimStringList.instance, APrimDtStringThesaurus.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("getThesaurusChildren", APrimStringList.instance, APrimDtStringThesaurus.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("sum", APrimDtalge.instance, APrimDtAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("toStringList", APrimStringList.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDistinctStringList", APrimStringList.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDecimalList", APrimDecimalList.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDistinctDecimalList", APrimDecimalList.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("toBooleanList", APrimBooleanList.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("toDistinctBooleanList", APrimBooleanList.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("minValue", AADLObject.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("maxValue", AADLObject.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("minDecimal", APrimDecimal.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("maxDecimal", APrimDecimal.instance, APrimDtAlgeSet.instance, APrimDtBase.instance)),
			new ACodeFunctionModule(new AADLFuncType("sortedAlgesByValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("sortAlgesByValue", null, APrimDtAlgeSet.instance, APrimDtBase.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("containsValue", APrimBoolean.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("containsAnyValues", APrimBoolean.instance, APrimDtAlgeSet.instance, APrimDtBase.instance,
					new AADLCollectionType("Collection", java.util.Collection.class, AADLObject.instance))),
			new ACodeFunctionModule(new AADLFuncType("selectEqualValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("selectNotEqualValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("selectLessThanValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("selectLessEqualValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("selectGreaterThanValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("selectGreaterEqualValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("replaceEqualValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("updateEqualValue", APrimBoolean.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance, APrimDtalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("removeEqualValue", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("deleteEqualValue", APrimBoolean.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("selectThesaurusMax", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, APrimDtStringThesaurus.instance)),
			new ACodeFunctionModule(new AADLFuncType("selectThesaurusMin", APrimDtAlgeSet.instance, APrimDtAlgeSet.instance, APrimDtBase.instance, APrimDtStringThesaurus.instance)),
			
			// @since 1.70
			new ACodeFunctionModule(new AADLFuncType("range", APrimSimpleDecimalRange.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("range", APrimSimpleDecimalRange.instance, APrimDecimal.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("range", APrimSimpleDecimalRange.instance, APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("range", APrimSimpleDecimalRange.instance, APrimDecimal.instance, APrimDecimal.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("range", APrimSimpleDecimalRange.instance, APrimDecimal.instance, APrimDecimal.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("range", APrimSimpleDecimalRange.instance, APrimDecimal.instance, APrimDecimal.instance, APrimDecimal.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("naturalNumberRange", APrimNaturalNumberDecimalRange.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("naturalNumberRange", APrimNaturalNumberDecimalRange.instance, APrimString.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("isNaturalNumberRangeFormat", APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("isEmpty", APrimBoolean.instance, APrimDecimalRange.instance)),
			new ACodeFunctionModule(new AADLFuncType("isIncludeRangeLast", APrimBoolean.instance, APrimDecimalRange.instance)),
			new ACodeFunctionModule(new AADLFuncType("isIncremental", APrimBoolean.instance, APrimDecimalRange.instance)),
			new ACodeFunctionModule(new AADLFuncType("containsValue", APrimBoolean.instance, APrimDecimalRange.instance, AADLObject.instance)),
			new ACodeFunctionModule(new AADLFuncType("rangeFirst", APrimDecimal.instance, APrimDecimalRange.instance)),
			new ACodeFunctionModule(new AADLFuncType("rangeLast", APrimDecimal.instance, APrimDecimalRange.instance)),
			new ACodeFunctionModule(new AADLFuncType("rangeStep", APrimDecimal.instance, APrimDecimalRange.instance)),
			new ACodeFunctionModule(new AADLFuncType("rangeMin", APrimDecimal.instance, APrimDecimalRange.instance)),
			new ACodeFunctionModule(new AADLFuncType("rangeMax", APrimDecimal.instance, APrimDecimalRange.instance)),
			
			// @since 1.80
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithoutNull", null, APrimDtalge.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithoutNull", null, APrimDtalge.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithoutNull", null, APrimDtAlgeSet.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithoutNull", null, APrimDtAlgeSet.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithBaseOrder", null, APrimDtalge.instance, APrimDtBaseSet.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithBaseOrder", null, APrimDtalge.instance, APrimDtBaseSet.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithBaseOrder", null, APrimDtAlgeSet.instance, APrimDtBaseSet.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFileWithBaseOrder", null, APrimDtAlgeSet.instance, APrimDtBaseSet.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtalge.instance, APrimDtBaseSet.instance, APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtalge.instance, APrimDtBaseSet.instance, APrimBoolean.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtAlgeSet.instance, APrimDtBaseSet.instance, APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("writeTableCsvFile", null, APrimDtAlgeSet.instance, APrimDtBaseSet.instance, APrimBoolean.instance, APrimString.instance, APrimString.instance)),
			
			// @since 1.90
			new ACodeFunctionModule(new AADLFuncType("writerValidSucceeded", null, APrimTextFileWriter.instance)),
			new ACodeFunctionModule(new AADLFuncType("writerValidSucceeded", null, APrimCsvFileWriter.instance)),
			new ACodeFunctionModule(new AADLFuncType("closeWriterValidSucceeded", null, APrimTextFileWriter.instance)),
			new ACodeFunctionModule(new AADLFuncType("closeWriterValidSucceeded", null, APrimCsvFileWriter.instance)),
			new ACodeFunctionModule(new AADLFuncType("toBoolean", APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("isBooleanExactly", APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBooleanValueByIndex", APrimBoolean.instance, APrimBooleanList.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("getBooleanValueByNumber", APrimBoolean.instance, APrimBooleanList.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("getDecimalValueByIndex", APrimDecimal.instance, APrimDecimalList.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("getDecimalValueByNumber", APrimDecimal.instance, APrimDecimalList.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("getStringValueByIndex", APrimString.instance, APrimStringList.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("getStringValueByNumber", APrimString.instance, APrimStringList.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("deleteFile", APrimBoolean.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("deleteFile", APrimBoolean.instance, APrimString.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("createTemporaryFile", APrimString.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("createTemporaryFile", APrimString.instance, APrimString.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("createTemporaryDirectory", APrimString.instance, APrimString.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("createTemporaryDirectory", APrimString.instance, APrimString.instance, APrimString.instance, APrimString.instance)),
			
			// @since 2.0.0
			new ACodeFunctionModule(new AADLFuncType("checkAndAcceptTerminateRequest", null)),
			new ACodeFunctionModule(new AADLFuncType("acceptTerminateRequest", APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("acceptTerminateReqest", APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("isTerminateRequested", APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("strictBar", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("strictBarLeaveZero", APrimExalge.instance, APrimExalge.instance, APrimBoolean.instance)),
			
			// @since 2.1.0
			new ACodeFunctionModule(new AADLFuncType("zeroProj", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("zeroProj", APrimExAlgeSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("notzeroProj", APrimExalge.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("notzeroProj", APrimExAlgeSet.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExalgeFromCsvString", APrimExalge.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExAlgeSetFromCsvString", APrimExAlgeSet.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExBaseSetFromCsvString", APrimExBaseSet.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExBasePatternSetFromCsvString", APrimExBasePatternSet.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newTransTableFromCsvString", APrimTransTable.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newTransMatrixFromCsvString", APrimTransMatrix.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("newExTransferFromCsvString", APrimExTransfer.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimExalge.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimExAlgeSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimExBaseSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimExBasePatternSet.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimTransTable.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimTransMatrix.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimExTransfer.instance)),
			new ACodeFunctionModule(new AADLFuncType("parseMqttCsvParameter", APrimMqttCsvParameter.instance, APrimString.instance)),
			new ACodeFunctionModule(new AADLFuncType("parseMqttCsvParameter", APrimMqttCsvParameter.instance, APrimString.instance, APrimBoolean.instance)),
			new ACodeFunctionModule(new AADLFuncType("parseMqttCsvParameter", APrimMqttCsvParameter.instance, APrimString.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("parseMqttCsvParameter", APrimMqttCsvParameter.instance, APrimString.instance, APrimBoolean.instance, APrimDecimal.instance)),
			new ACodeFunctionModule(new AADLFuncType("getMqttServerURI", APrimString.instance, APrimMqttCsvParameter.instance)),
			new ACodeFunctionModule(new AADLFuncType("getMqttClientID", APrimString.instance, APrimMqttCsvParameter.instance)),
			new ACodeFunctionModule(new AADLFuncType("getAvailableMqttClientID", APrimString.instance, APrimMqttCsvParameter.instance)),
			new ACodeFunctionModule(new AADLFuncType("getMqttQOS", APrimDecimal.instance, APrimMqttCsvParameter.instance)),
			new ACodeFunctionModule(new AADLFuncType("getMqttTopicList", APrimStringList.instance, APrimMqttCsvParameter.instance)),
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance, APrimMqttCsvParameter.instance)),
			
			// @since 2.2.0
			new ACodeFunctionModule(new AADLFuncType("toCsvString", APrimString.instance,
					new AADLCollectionType("Collection", java.util.Collection.class, AADLObject.instance))),
			new ACodeFunctionModule(new AADLFuncType("newStringListFromCsvString", APrimStringList.instance, APrimString.instance)),
	};
	
	protected final AADLFuncType funcType;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeFunctionModule(AADLFuncType ftype) {
		super();
		this.funcType = ftype;
	}
	
	protected ACodeFunctionModule(AADLFuncType ftype, String[] codeLines) {
		super();
		this.funcType = ftype;
		setCodeLines(codeLines);
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasFunctionType() {
		return (this.funcType != null);
	}
	
	public AADLFuncType getFunctionType() {
		return this.funcType;
	}

	//------------------------------------------------------------
	// generate codes
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void setCodeLines(String[] codeLines) {
		for (String code : codeLines) {
			this.jlb.appendLine(-1, code);
		}
	}
}
