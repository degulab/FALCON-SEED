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
/*
 * @(#)AADLMessage.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMessage.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMessage.java	1.40	2010/03/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMessage.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMessage.java	1.10	2008/05/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLMessage.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc;

import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.codegen.ACodeObject;

/**
 * AADLコンパイラーで出力するメッセージ。
 * 
 * @version 1.70	2011/06/29
 */
public class AADLMessage
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String DEBUG_HEADER = "@[Debug] ";
	static public final String ERROR_HEADER = "[Error] ";
	static public final String WARN_HEADER = "[Warning] ";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Static interfaces
	//------------------------------------------------------------
	
	static public String[] toStringParams(AADLType...params) {
		String[] argTypes = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null)
				argTypes[i] = params[i].getName();
			else
				argTypes[i] = "null";
		}
		return argTypes;
	}
	
	static public String[] toStringParams(ACodeObject...params) {
		String[] argTypes = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null)
				argTypes[i] = params[i].getType().getName();
			else
				argTypes[i] = "null";
		}
		return argTypes;
	}
	
	// --- basic
	
	static public String printException(Throwable ex) {
		String msg = String.format("%s : %s", ex.getClass().getSimpleName(), ex.getMessage());
		return msg;
	}

	// --- compile

	static public String illegalLiteralFormat(String literal, String type) {
		String msg = String.format("The literal %s of type %s is illegal format.", literal, type);
		return msg;
	}
	
	static public String literalOutOfRange(String literal, String type) {
		String msg = String.format("The literal %s of type %s is out of range.", literal, type);
		return msg;
	}
	
	static public String cannotConvert(String toType) {
		String msg = String.format("Cannot convert to '%s'.", toType);
		return msg;
	}
	
	static public String cannotConvert(String fromType, String toType) {
		String msg = String.format("Cannot convert '%s' to '%s'.", fromType, toType);
		return msg;
	}
	
	static public String undefinedToken(String token) {
		String msg = String.format("Undefined '%s' token.", token);
		return msg;
	}
	
	static public String cannotUseKeyword(String keyword) {
		String msg = String.format("Cannot use '%s' keyword.", keyword);
		return msg;
	}
	
	static public String unknownDataType() {
		return "Unknown data type.";
	}
	
	static public String undefinedSymbol(String symbol) {
		String msg = String.format("Undefined '%s' symbol.", symbol);
		return msg;
	}
	
	static public String undefinedConstructor(String typeName, String...argTypes) {
		String params = createParamTypesString(argTypes);
		String msg = String.format("Undefined '%s(%s)' constructor.", typeName, params);
		return msg;
	}
	
	static public String undefinedFunction(String funcName, String...argTypes) {
		String params = createParamTypesString(argTypes);
		String msg = String.format("Undefined '%s(%s)' function.", funcName, params);
		return msg;
	}
	
	static public String undefinedSpecialFunction(String classType, String funcName, String...argTypes) {
		String params = createParamTypesString(argTypes);
		String msg = String.format("Undefined '%s[%s](%s)' function.", funcName, params, classType);
		return msg;
	}
	
	static public String undefinedMemberField(String classType, String fieldName) {
		String msg = String.format("'%s' is undefined in %s.", fieldName, classType);
		return msg;
	}
	
	static public String undefinedMemberMethod(String classType, String funcName, String...argTypes) {
		String params = createParamTypesString(argTypes);
		String msg = String.format("'%s(%s)' is undefined in %s.", funcName, params, classType);
		return msg;
	}
	
	static public String undefinedOperator(String operand, String argType) {
		String msg = String.format("Operator '%s' is undefined in type %s of the argument.",
									operand, argType);
		return msg;
	}
	
	static public String undefinedOperator(String operand, String argType1, String argType2) {
		String msg = String.format("Operator '%s' is undefined in type %s and %s of arguments.",
									operand, argType1, argType2);
		return msg;
	}
	
	static public String alreadyExistVariable(String strName) {
		String msg = String.format("Variable '%s' has already been declared.", strName);
		return msg;
	}
	
	static public String alreadyExistFunction(String strPrototype) {
		String msg = String.format("Function '%s' already exist.", strPrototype);
		return msg;
	}
	
	static public String illegalParameter(String name, int no, String strType, String strNeedType) {
		String msg;
		if (strNeedType != null && strNeedType.length() > 0) {
			msg = String.format("Illegal parameter '%s' type at #%d in %s, not '%s' type.",
					strType, no, name, strNeedType);
		} else {
			msg = String.format("Illegal parameter '%s' type at #%d in %s.",
					strType, no, name);
		}
		return msg;
	}
	
	static public String illegalListElementType(String strType) {
		String msg = String.format("Cannot use '%s' type in Array.", strType);
		return msg;
	}
	
	static public String notEqualListElementType(String strType) {
		String msg = String.format("'%s' type is not equal another type in Array.", strType);
		return msg;
	}
	
	static public String notIterable(String strName) {
		String msg = String.format("'%s' is not iterable interface.", strName);
		return msg;
	}
	
	static public String listIteratorFromNotList(String strName) {
		String msg = String.format("'%s' is not iterated from the list.", strName);
		return msg;
	}
	
	static public String notConditionalExpression() {
		String msg = "Not conditional expression.";
		return msg;
	}
	
	static public String notExistExBaseLiteralKeys() {
		return "Not exist ExBase literal keys."; 
	}
	
	static public String notExistDtBaseLiteralKeys() {
		return "Not exist DtBase literal keys."; 
	}
	
	static public String notExistDtBaseTypeKey() {
		return "Not exist DtBase data type key.";
	}
	
	static public String illegalExBaseLiteralType(int keyIndex) {
		String msg = String.format("ExBase literal #%d key is not String.", (keyIndex + 1));
		return msg;
	}
	
	static public String illegalDtBaseLiteralType(int keyIndex) {
		String msg = String.format("DtBase literal #%d key is not String.", (keyIndex + 1));
		return msg;
	}
	
	static public String notVariableLeftOfOperator(String strOperator) {
		String msg = String.format("The left side of '%s' operator should be a variable.", strOperator);
		return msg;
	}
	
	static public String noPackagePath() {
		String msg = "Package identifier is not specified.";
		return msg;
	}
	
	static public String cannotReassignConstVariable(String strName) {
		String msg = String.format("Cannot re-assign const variable '%s'.", String.valueOf(strName));
		return msg;
	}
	
	static public String cannotUseBreak() {
		String msg = "Cannot use 'break' in the outside of the embedded notation.";
		return msg;
	}
	
	static public String unsupportedFileOptions(String strFileType) {
		String msg = String.format("Unsupported %s option.", strFileType);
		return msg;
	}
	
	static public String illegalFileOptionType(int no, String strType, String strNeedType) {
		String msg;
		if (strNeedType != null && strNeedType.length() > 0) {
			msg = String.format("Illegal File option '%s' type at #%d, not '%s' type.",
					strType, no, strNeedType);
		} else {
			msg = String.format("Illegal parameter '%s' type at #%d.",
					strType, no);
		}
		return msg;
	}

	static public String duplicateModifierForField(String strModifier, String strFieldName) {
		String msg;
		if (strFieldName != null && strFieldName.length() > 0) {
			msg = String.format("Duplicate '%s' modifier for the field %s.", String.valueOf(strModifier), strFieldName);
		} else {
			msg = String.format("Duplicate '%s' modifier for the field.", String.valueOf(strModifier));
		}
		return msg;
	}
	
	static public String duplicateModifierForField(String strModifier) {
		return duplicateModifierForField(strModifier, null);
	}

	static public String duplicateModifierForFunction(String strModifier, String strFuncName) {
		String msg;
		if (strFuncName != null && strFuncName.length() > 0) {
			msg = String.format("Duplicate '%s' modifier for the function %s.", String.valueOf(strModifier), strFuncName);
		} else {
			msg = String.format("Duplicate '%s' modifier for the function.", String.valueOf(strModifier));
		}
		return msg;
	}
	
	static public String duplicateModifierForFunction(String strModifier) {
		return duplicateModifierForFunction(strModifier, null);
	}

	//------------------------------------------------------------
	// Static internal methods
	//------------------------------------------------------------
	
	static private String createParamTypesString(String...paramTypes) {
		StringBuffer sb = new StringBuffer();
		if (paramTypes.length > 0) {
			sb.append(paramTypes[0]);
			for (int i = 1; i < paramTypes.length; i++) {
				sb.append(",");
				sb.append(paramTypes[i]);
			}
		}
		return sb.toString();
	}
}
