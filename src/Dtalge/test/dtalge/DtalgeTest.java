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
 *  Copyright 2007-2012  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtalgeTest.java	0.40	2012/04/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtalgeTest.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtalgeTest.java	0.20	2010/02/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtalgeTest.java	0.10	2008/08/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import dtalge.exception.DtBaseNotFoundException;
import dtalge.exception.IllegalValueOfDataTypeException;
import dtalge.util.DtDataTypes;

/**
 * <code>Dtalge</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtalgeTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Object objNull = null;
	static private final Boolean objBoolean = Boolean.TRUE;
	//static private final BigInteger objInteger = new BigInteger("123");
	static private final BigDecimal objInteger = new BigDecimal("123");
	static private final BigDecimal objDecimal = new BigDecimal("0.1");
	static private final String objString = new String("hogeString!");
	static private final Boolean objOtherBoolean = Boolean.FALSE;
	//static private final BigInteger objOtherInteger = new BigInteger("9876543210");
	static private final BigDecimal objOtherInteger = new BigDecimal("9876543210");
	static private final BigDecimal objOtherDecimal = new BigDecimal("42.195");
	static private final String objOtherString = new String("OtherSageString!");
	static private final BigDecimal objDecimal2 = new BigDecimal("0.10");
	static private final BigDecimal objOtherDecimal2 = new BigDecimal("42.1950");
	
	static final Object[] normalAlges = new Object[]{
		new DtBase("testBoolean", DtDataTypes.BOOLEAN, "#", "null"), objNull,
		//new DtBase("testInteger", DtDataTypes.INTEGER, "#", "null"), objNull,
		new DtBase("testInteger", DtDataTypes.DECIMAL, "#", "null"), objNull,
		new DtBase("testDecimal", DtDataTypes.DECIMAL, "#", "null"), objNull,
		new DtBase("testString" , DtDataTypes.STRING , "#", "null"), objNull,
		new DtBase("testBoolean", DtDataTypes.BOOLEAN, "#", objBoolean.toString()), objBoolean,
		//new DtBase("testInteger", DtDataTypes.INTEGER, "#", objInteger.toString()), objInteger,
		new DtBase("testInteger", DtDataTypes.DECIMAL, "#", objInteger.toString()), objInteger,
		new DtBase("testDecimal", DtDataTypes.DECIMAL, "#", objDecimal.toString()), objDecimal,
		new DtBase("testString" , DtDataTypes.STRING , "#", objString.toString()) , objString,
	};
	
	static final Object[] normalOtherAlges = new Object[]{
		new DtBase("testBoolean", DtDataTypes.BOOLEAN, "#", objOtherBoolean.toString()), objOtherBoolean,
		//new DtBase("testInteger", DtDataTypes.INTEGER, "#", objOtherInteger.toString()), objOtherInteger,
		new DtBase("testInteger", DtDataTypes.DECIMAL, "#", objOtherInteger.toString()), objOtherInteger,
		new DtBase("testDecimal", DtDataTypes.DECIMAL, "#", objOtherDecimal.toString()), objOtherDecimal,
		new DtBase("testString" , DtDataTypes.STRING , "#", objOtherString.toString()) , objOtherString,
	};
	
	static final Object[] illegalBooleanAlges = new Object[]{
		new DtBase("testIllegalBoolean", DtDataTypes.BOOLEAN, "#", objInteger.toString()), objInteger,
		new DtBase("testIllegalBoolean", DtDataTypes.BOOLEAN, "#", objDecimal.toString()), objDecimal,
		new DtBase("testIllegalBoolean", DtDataTypes.BOOLEAN, "#", objString.toString()) , objString,
	};
	
	//static final Object[] illegalIntegerAlges = new Object[]{
	//	new DtBase("testIllegalInteger", DtDataTypes.INTEGER, "#", objBoolean.toString()), objBoolean,
	//	new DtBase("testIllegalInteger", DtDataTypes.INTEGER, "#", objDecimal.toString()), objDecimal,
	//	new DtBase("testIllegalInteger", DtDataTypes.INTEGER, "#", objString.toString()) , objString,
	//};
	
	static final Object[] illegalDecimalAlges = new Object[]{
		new DtBase("testIllegalDecimal", DtDataTypes.DECIMAL, "#", objBoolean.toString()), objBoolean,
		new DtBase("testIllegalDecimal", DtDataTypes.DECIMAL, "#", objInteger.toString()), objInteger,
		new DtBase("testIllegalDecimal", DtDataTypes.DECIMAL, "#", objString.toString()) , objString,
	};
	
	static final Object[] illegalStringAlges = new Object[]{
		new DtBase("testIllegalString", DtDataTypes.STRING, "#", objBoolean.toString()), objBoolean,
		new DtBase("testIllegalString", DtDataTypes.STRING, "#", objInteger.toString()), objInteger,
		new DtBase("testIllegalString", DtDataTypes.STRING, "#", objDecimal.toString()), objDecimal,
	};
	
	static final Object[] sumAlgeData1 = new Object[]{
		new DtBase("testBoolean1", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,			// sumAlge1
		new DtBase("TestDecimal1", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.123"),	// sumAlge1
		new DtBase("TestString1" , DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum01", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum11", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum11", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum12", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum02", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum12", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum13", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum13", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum03", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum04", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum04", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum04", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
	};
	
	static final Object[] sumAlgeData2 = new Object[]{
		new DtBase("testBoolean2", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,	// sumAlge2
		new DtBase("TestDecimal2", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestString2" , DtDataTypes.STRING , "#", "#"), "string2",		// sumAlge2
		new DtBase("TestSum01", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum21", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum21", DtDataTypes.STRING , "#", "#"), "TestSum21",		// sumAlge2
		new DtBase("TestSum22", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum02", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum22", DtDataTypes.STRING , "#", "#"), "TestSum22",		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum03", DtDataTypes.STRING , "#", "#"), "TestSum23",		// sumAlge2
		new DtBase("TestSum04", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum04", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum04", DtDataTypes.STRING , "#", "#"), "TestSum24",		// sumAlge2
	};
	
	static final Object[] sumAlgeData3 = new Object[]{
		new DtBase("testBoolean3", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestDecimal3", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.789"),	// sumAlge3
		new DtBase("TestString3" , DtDataTypes.STRING , "#", "#"), "string3",				// sumAlge3
		new DtBase("TestSum01", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum31", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum31", DtDataTypes.STRING , "#", "#"), "TestSum31",				// sumAlge3
		new DtBase("TestSum32", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum02", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum32", DtDataTypes.STRING , "#", "#"), "TestSum32",				// sumAlge3
		new DtBase("TestSum33", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum33", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum03", DtDataTypes.STRING , "#", "#"), "TestSum33",				// sumAlge3
		new DtBase("TestSum04", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum04", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum04", DtDataTypes.STRING , "#", "#"), "TestSum34",				// sumAlge3
	};
	
	static final Object[] sumRetData123 = new Object[]{
		//--- 1
		new DtBase("testBoolean1", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,			// sumAlge1
		new DtBase("TestDecimal1", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.123"),	// sumAlge1
		new DtBase("TestString1" , DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum01", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum11", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum11", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum12", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum02", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum12", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum13", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum13", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum03", DtDataTypes.STRING , "#", "#"), "TestSum33",				// sumAlge3
		new DtBase("TestSum04", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum04", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum04", DtDataTypes.STRING , "#", "#"), "TestSum34",				// sumAlge3
		//--- 2
		new DtBase("testBoolean2", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,	// sumAlge2
		new DtBase("TestDecimal2", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestString2" , DtDataTypes.STRING , "#", "#"), "string2",		// sumAlge2
		new DtBase("TestSum21", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum21", DtDataTypes.STRING , "#", "#"), "TestSum21",		// sumAlge2
		new DtBase("TestSum22", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum22", DtDataTypes.STRING , "#", "#"), "TestSum22",		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		//--- 3
		new DtBase("testBoolean3", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestDecimal3", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.789"),	// sumAlge3
		new DtBase("TestString3" , DtDataTypes.STRING , "#", "#"), "string3",				// sumAlge3
		new DtBase("TestSum31", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum31", DtDataTypes.STRING , "#", "#"), "TestSum31",				// sumAlge3
		new DtBase("TestSum32", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum32", DtDataTypes.STRING , "#", "#"), "TestSum32",				// sumAlge3
		new DtBase("TestSum33", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum33", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
	};
	
	static final Object[] sumRetData321 = new Object[]{
		//--- 3
		new DtBase("testBoolean3", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestDecimal3", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.789"),	// sumAlge3
		new DtBase("TestString3" , DtDataTypes.STRING , "#", "#"), "string3",				// sumAlge3
		new DtBase("TestSum01", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum31", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum31", DtDataTypes.STRING , "#", "#"), "TestSum31",				// sumAlge3
		new DtBase("TestSum32", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum02", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum32", DtDataTypes.STRING , "#", "#"), "TestSum32",				// sumAlge3
		new DtBase("TestSum33", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum33", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum03", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum04", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum04", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum04", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		//--- 2
		new DtBase("testBoolean2", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,	// sumAlge2
		new DtBase("TestDecimal2", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestString2" , DtDataTypes.STRING , "#", "#"), "string2",		// sumAlge2
		new DtBase("TestSum21", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum21", DtDataTypes.STRING , "#", "#"), "TestSum21",		// sumAlge2
		new DtBase("TestSum22", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum22", DtDataTypes.STRING , "#", "#"), "TestSum22",		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		//--- 1
		new DtBase("testBoolean1", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,			// sumAlge1
		new DtBase("TestDecimal1", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.123"),	// sumAlge1
		new DtBase("TestString1" , DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum11", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum11", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum12", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum12", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum13", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum13", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
	};
	
	static final Object[] sumRetData213 = new Object[]{
		//--- 2
		new DtBase("testBoolean2", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,	// sumAlge2
		new DtBase("TestDecimal2", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestString2" , DtDataTypes.STRING , "#", "#"), "string2",		// sumAlge2
		new DtBase("TestSum01", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum21", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum21", DtDataTypes.STRING , "#", "#"), "TestSum21",		// sumAlge2
		new DtBase("TestSum22", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum02", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum22", DtDataTypes.STRING , "#", "#"), "TestSum22",		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum03", DtDataTypes.STRING , "#", "#"), "TestSum33",				// sumAlge3
		new DtBase("TestSum04", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum04", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum04", DtDataTypes.STRING , "#", "#"), "TestSum34",				// sumAlge3
		//--- 1
		new DtBase("testBoolean1", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,			// sumAlge1
		new DtBase("TestDecimal1", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.123"),	// sumAlge1
		new DtBase("TestString1" , DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum11", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum11", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum12", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum12", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum13", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum13", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		//--- 3
		new DtBase("testBoolean3", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestDecimal3", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.789"),	// sumAlge3
		new DtBase("TestString3" , DtDataTypes.STRING , "#", "#"), "string3",				// sumAlge3
		new DtBase("TestSum31", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum31", DtDataTypes.STRING , "#", "#"), "TestSum31",				// sumAlge3
		new DtBase("TestSum32", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum32", DtDataTypes.STRING , "#", "#"), "TestSum32",				// sumAlge3
		new DtBase("TestSum33", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum33", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
	};
	
	static final Object[] sumRetData231 = new Object[]{
		//--- 2
		new DtBase("testBoolean2", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,	// sumAlge2
		new DtBase("TestDecimal2", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestString2" , DtDataTypes.STRING , "#", "#"), "string2",		// sumAlge2
		new DtBase("TestSum01", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum21", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum21", DtDataTypes.STRING , "#", "#"), "TestSum21",		// sumAlge2
		new DtBase("TestSum22", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum02", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum22", DtDataTypes.STRING , "#", "#"), "TestSum22",		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.BOOLEAN, "#", "#"), Boolean.FALSE,		// sumAlge2
		new DtBase("TestSum23", DtDataTypes.DECIMAL, "#", "#"), objNull,			// sumAlge2
		new DtBase("TestSum03", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum04", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum04", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum04", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		//--- 3
		new DtBase("testBoolean3", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestDecimal3", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.789"),	// sumAlge3
		new DtBase("TestString3" , DtDataTypes.STRING , "#", "#"), "string3",				// sumAlge3
		new DtBase("TestSum31", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		new DtBase("TestSum31", DtDataTypes.STRING , "#", "#"), "TestSum31",				// sumAlge3
		new DtBase("TestSum32", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum32", DtDataTypes.STRING , "#", "#"), "TestSum32",				// sumAlge3
		new DtBase("TestSum33", DtDataTypes.BOOLEAN, "#", "#"), objNull,					// sumAlge3
		new DtBase("TestSum33", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1953"),	// sumAlge3
		//--- 1
		new DtBase("testBoolean1", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,			// sumAlge1
		new DtBase("TestDecimal1", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("1.123"),	// sumAlge1
		new DtBase("TestString1" , DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum11", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
		new DtBase("TestSum11", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum12", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum12", DtDataTypes.STRING , "#", "#"), objNull,					// sumAlge1
		new DtBase("TestSum13", DtDataTypes.BOOLEAN, "#", "#"), Boolean.TRUE,				// sumAlge1
		new DtBase("TestSum13", DtDataTypes.DECIMAL, "#", "#"), new BigDecimal("42.1951"),	// sumAlge1
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static Object[] filterAlgeValue(Object[] algeElements, Object...values) {
		if (values == null || values.length <= 0) {
			return new Object[0];
		}
		
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < algeElements.length; i+=2) {
			Object eBase = algeElements[i];
			Object eValue = (algeElements.length%2==0 ? algeElements[i+1] : null);
			boolean saveValue = false;
			if (eValue == null) {
				for (Object v : values) {
					if (v == null) {
						saveValue = true;
						break;
					}
				}
			}
			else if (eValue instanceof BigDecimal) {
				for (Object v : values) {
					if ((v instanceof BigDecimal) && 0==((BigDecimal)v).compareTo((BigDecimal)eValue)) {
						saveValue = true;
						break;
					}
				}
			} else {
				for (Object v : values) {
					if (eValue.equals(v)) {
						saveValue = true;
						break;
					}
				}
			}
			if (saveValue) {
				list.add(eBase);
				list.add(eValue);
			}
		}
		
		return list.toArray();
	}
	
	static Object[] removeAlgeValue(Object[] algeElements, Object...values) {
		if (values == null || values.length <= 0) {
			return new Object[0];
		}
		
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < algeElements.length; i+=2) {
			Object eBase = algeElements[i];
			Object eValue = (algeElements.length%2==0 ? algeElements[i+1] : null);
			boolean saveValue = true;
			if (eValue == null) {
				for (Object v : values) {
					if (v == null) {
						saveValue = false;
						break;
					}
				}
			}
			else if (eValue instanceof BigDecimal) {
				for (Object v : values) {
					if ((v instanceof BigDecimal) && 0==((BigDecimal)v).compareTo((BigDecimal)eValue)) {
						saveValue = false;
						break;
					}
				}
			} else {
				for (Object v : values) {
					if (eValue.equals(v)) {
						saveValue = false;
						break;
					}
				}
			}
			if (saveValue) {
				list.add(eBase);
				list.add(eValue);
			}
		}
		
		return list.toArray();
	}
	
	static Object[] catAlgeObjects(Object[] algeElements, Object...elements) {
		int newSize = 0;
		if (algeElements != null && algeElements.length > 0) {
			newSize = algeElements.length;
		}
		if (elements != null && elements.length > 0) {
			newSize += elements.length;
		}
		
		Object[] newElements = new Object[newSize];
		int index = 0;
		if (algeElements != null && algeElements.length > 0) {
			for (; index < algeElements.length; index++) {
				newElements[index] = algeElements[index];
			}
		}
		if (elements != null && elements.length > 0) {
			for (int i = 0; i < elements.length; i++) {
				newElements[index+i] = elements[i];
			}
		}
		
		return newElements;
	}
	
	static Map<DtBase,Object> convertObjectsToMap(Object...elements) {
		int newSize = 0;
		if (elements != null && elements.length > 0) {
			newSize = elements.length / 2;
		}
		
		Map<DtBase,Object> map = new LinkedHashMap<DtBase,Object>(newSize);
		if (elements != null && elements.length > 0) {
			for (int i = 0; i < elements.length; i+=2) {
				DtBase eBase = (DtBase)elements[i];
				Object eValue = (elements.length%2==0 ? elements[i+1] : null);
				map.put(eBase, eValue);
			}
		}
		return map;
	}
	
	static boolean equalElementSequence(Dtalge alge, Object...elements) {
		boolean result;
		
		if (alge.isEmpty()) {
			if (elements != null && elements.length > 0) {
				System.err.println("equalElementSequence : Dtalge("
						+ alge.getNumElements()
						+ ") != elements("
						+ (elements != null ? String.valueOf(elements.length) : "null")
						+ ")");
				result = false;
			} else {
				result = true;
			}
		}
		else if (elements == null || (elements.length%2 != 0) || alge.getNumElements() != (elements.length/2)) {
			System.err.println("equalElementSequence : Dtalge("
					+ alge.getNumElements()
					+ ") != elements("
					+ (elements != null ? String.valueOf(elements.length) : "null")
					+ ")");
			result = false;
		}
		else {
			int index = 0;
			result = true;
			for (Map.Entry<DtBase, Object> entry : alge.data.entrySet()) {
				Object eBase = elements[index];
				Object eValue = elements[index+1];
				DtBase aBase = entry.getKey();
				Object aValue = entry.getValue();
				if (!aBase.equals(eBase)) {
					System.err.println("equalElementSequence : not equal DtBase : "
							+ String.valueOf(aValue) + aBase.toString()
							+ " : base[" + String.valueOf(index)
							+ "]=" + String.valueOf(eBase));
					result = false;
				}
				if (aValue==null || eValue==null) {
					if (aValue != eValue) {
						System.err.println("equalElementSequence : not equal value : "
								+ String.valueOf(aValue) + aBase.toString()
								+ " : value[" + String.valueOf(index+1)
								+ "]=" + String.valueOf(eValue));
						result = false;
					}
				}
				else if (aValue instanceof BigDecimal) {
					if (((BigDecimal)aValue).compareTo((BigDecimal)eValue) != 0) {
						System.err.println("equalElementSequence : not equal value : "
								+ String.valueOf(aValue) + aBase.toString()
								+ " : value[" + String.valueOf(index+1)
								+ "]=" + String.valueOf(eValue));
						result = false;
					}
				}
				else if (!aValue.equals(eValue)) {
					System.err.println("equalElementSequence : not equal value : "
							+ String.valueOf(aValue) + aBase.toString()
							+ " : value[" + String.valueOf(index+1)
							+ "]=" + String.valueOf(eValue));
					result = false;
				}
				index+=2;
			}
		}
		
		return result;
	}
	
	static boolean equalNoNullElementSequence(Dtalge alge, Object...elements) {
		if (elements == null || elements.length < 1) {
			return (alge==null ? true : alge.isEmpty());
		}
		
		ArrayList<Object> list = new ArrayList<Object>(elements.length);
		for (int index = 0; index < elements.length; index+=2) {
			Object eBase  = elements[index];
			Object eValue = ((index+1)<elements.length ? elements[index+1] : null);
			if (eValue != null) {
				list.add(eBase);
				list.add(eValue);
			}
		}
		Object[] noNullElements = list.toArray(new Object[list.size()]);
		
		return equalElementSequence(alge, noNullElements);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * {@link dtalge.Dtalge#createDefaultMap()} のためのテスト・メソッド。
	 *
	 */
	public void testCreateDefaultMap() {
		Dtalge alge = new Dtalge();
		Map<DtBase,Object> map = alge.createDefaultMap();
		assertTrue(map instanceof LinkedHashMap);
		assertTrue(map.isEmpty());
		assertEquals(0, map.size());
	}

	/**
	 * {@link dtalge.Dtalge#Dtalge()} のためのテスト・メソッド。
	 */
	public void testDtalge() {
		Dtalge alge = new Dtalge();
		assertTrue(alge.isEmpty());
		assertEquals(0, alge.getNumElements());
	}

	/**
	 * {@link dtalge.Dtalge#Dtalge(dtalge.DtBase, java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testDtalgeDtBaseObject() {
		Object objNull    = null;
		Object objBoolean = Boolean.TRUE;
		//Object objInteger = new BigInteger("123");
		//Object objInteger = new BigDecimal("123");
		Object objDecimal = new BigDecimal("0.1");
		Object objString  = new String("hogeString!");
		
		//--- make base
		DtBase baseBoolean = new DtBase("testDtalgeDtBaseObject", DtDataTypes.BOOLEAN, "#", objBoolean.toString());
		//DtBase baseInteger = new DtBase("testDtalgeDtBaseObject", DtDataTypes.INTEGER, "#", objInteger.toString());
		DtBase baseDecimal = new DtBase("testDtalgeDtBaseObject", DtDataTypes.DECIMAL, "#", objDecimal.toString());
		DtBase baseString  = new DtBase("testDtalgeDtBaseObject", DtDataTypes.STRING,  "#", objString.toString());
		
		Dtalge alge;
		
		//--- null
		try {
			alge = new Dtalge(null, objNull);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- Boolean
		alge = new Dtalge(baseBoolean, objNull);
		assertTrue(equalElementSequence(alge, baseBoolean, objNull));
		alge = new Dtalge(baseBoolean, objBoolean);
		assertTrue(equalElementSequence(alge, baseBoolean, objBoolean));
		//try {
		//	alge = new Dtalge(baseInteger, objBoolean);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			alge = new Dtalge(baseDecimal, objBoolean);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			alge = new Dtalge(baseString, objBoolean);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		
		//--- Integer
		/*
		alge = new Dtalge(baseInteger, objNull);
		assertTrue(equalElementSequence(alge, baseInteger, objNull));
		alge = new Dtalge(baseInteger, objInteger);
		assertTrue(equalElementSequence(alge, baseInteger, objInteger));
		try {
			alge = new Dtalge(baseBoolean, objInteger);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			alge = new Dtalge(baseDecimal, objInteger);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			alge = new Dtalge(baseString, objInteger);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		*/
		
		//--- Decimal
		alge = new Dtalge(baseDecimal, objNull);
		assertTrue(equalElementSequence(alge, baseDecimal, objNull));
		alge = new Dtalge(baseDecimal, objDecimal);
		assertTrue(equalElementSequence(alge, baseDecimal, objDecimal));
		try {
			alge = new Dtalge(baseBoolean, objDecimal);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		//try {
		//	alge = new Dtalge(baseInteger, objDecimal);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			alge = new Dtalge(baseString, objDecimal);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		
		//--- String
		alge = new Dtalge(baseString, objNull);
		assertTrue(equalElementSequence(alge, baseString, objNull));
		alge = new Dtalge(baseString, objString);
		assertTrue(equalElementSequence(alge, baseString, objString));
		try {
			alge = new Dtalge(baseBoolean, objString);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		//try {
		//	alge = new Dtalge(baseInteger, objString);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			alge = new Dtalge(baseDecimal, objString);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#Dtalge(java.lang.Object[])} のためのテスト・メソッド。
	 */
	public void testDtalgeObjectArray() {
		Dtalge alge;
		
		// null
		try {
			Object[] val = null;
			alge = new Dtalge(val);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// normal
		try {
			alge = new Dtalge(normalAlges);
			assertTrue(equalElementSequence(alge, normalAlges));
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		// illegal length
		try {
			Object[] val = catAlgeObjects(normalAlges, new DtBase("testString", DtDataTypes.STRING, "#", "no value"));
			alge = new Dtalge(val);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			Object[] val = catAlgeObjects(normalAlges, "<Base>", new DtBase("testString", DtDataTypes.STRING, "#", "no value"));
			alge = new Dtalge(val);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		
		// illegal arguments
		try {
			Object[] val = catAlgeObjects(normalAlges, illegalBooleanAlges);
			alge = new Dtalge(val);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		//try {
		//	Object[] val = catAlgeObjects(normalAlges, illegalIntegerAlges);
		//	alge = new Dtalge(val);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			Object[] val = catAlgeObjects(normalAlges, illegalDecimalAlges);
			alge = new Dtalge(val);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			Object[] val = catAlgeObjects(normalAlges, illegalStringAlges);
			alge = new Dtalge(val);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#Dtalge(java.util.Map)} のためのテスト・メソッド。
	 */
	public void testDtalgeMapOfQextendsDtBaseQextendsObject() {
		Map<DtBase,Object> map;
		Dtalge alge;
		
		// null
		try {
			map = null;
			alge = new Dtalge(map);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// normal
		try {
			map = convertObjectsToMap(normalAlges);
			alge = new Dtalge(map);
			assertTrue(equalElementSequence(alge, normalAlges));
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		// illegal length
		try {
			map = convertObjectsToMap(catAlgeObjects(normalAlges, null, new DtBase("testString", DtDataTypes.STRING, "#", "no value")));
			alge = new Dtalge(map);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		
		// illegal arguments
		try {
			map = convertObjectsToMap(catAlgeObjects(normalAlges, illegalBooleanAlges));
			alge = new Dtalge(map);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		//try {
		//	map = convertObjectsToMap(catAlgeObjects(normalAlges, illegalIntegerAlges));
		//	alge = new Dtalge(map);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			map = convertObjectsToMap(catAlgeObjects(normalAlges, illegalDecimalAlges));
			alge = new Dtalge(map);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			map = convertObjectsToMap(catAlgeObjects(normalAlges, illegalStringAlges));
			alge = new Dtalge(map);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#Dtalge(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testDtalgeCollectionOfQextendsDtalge() {
		Dtalge alge;
		
		// null
		try {
			Collection<? extends Dtalge> c = null;
			alge = new Dtalge(c);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// normal
		try {
			ArrayList<Dtalge> c = new ArrayList<Dtalge>();
			for (int i = 0; i < normalAlges.length; i+=2) {
				DtBase eBase = (DtBase)normalAlges[i];
				Object eValue = normalAlges[i+1];
				c.add(new Dtalge(eBase, eValue));
			}
			alge = new Dtalge(c);
			assertTrue(equalElementSequence(alge, normalAlges));
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		Dtalge alge;
		
		alge = new Dtalge();
		assertTrue(alge.isEmpty());
		
		alge = new Dtalge(normalAlges);
		assertFalse(alge.isEmpty());
	}

	/**
	 * {@link dtalge.Dtalge#getNumElements()} のためのテスト・メソッド。
	 */
	public void testGetNumElements() {
		Dtalge alge;
		
		alge = new Dtalge();
		assertTrue(alge.isEmpty());
		assertEquals(0, alge.getNumElements());
		
		alge = new Dtalge(normalAlges);
		assertFalse(alge.isEmpty());
		assertEquals((normalAlges.length/2), alge.getNumElements());
	}

	/**
	 * {@link dtalge.Dtalge#containsBase(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testContainsBase() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			assertFalse(alge1.containsBase(eBase));
			assertTrue("not found " + eBase.toString(), alge2.containsBase(eBase));
			assertFalse("is exist " + eBase.toString(), alge3.containsBase(eBase));
		}
		
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			assertFalse(alge1.containsBase(eBase));
			assertFalse("is exist " + eBase.toString(), alge2.containsBase(eBase));
			assertTrue("not found " + eBase.toString(), alge3.containsBase(eBase));
		}
	}

	/**
	 * {@link dtalge.Dtalge#containsAnyBases(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testContainsAnyBases() {
		DtBaseSet bases0 = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet();
		DtBaseSet bases2 = new DtBaseSet();
		DtBaseSet bases3 = new DtBaseSet();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			bases1.add(eBase);
			bases3.add(eBase);
		}
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			bases2.add(eBase);
			bases3.add(eBase);
		}
		
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge alge4 = new Dtalge(catAlgeObjects(normalAlges, normalOtherAlges));
		
		assertFalse(alge1.containsAnyBases(null));
		assertFalse(alge2.containsAnyBases(null));
		assertFalse(alge3.containsAnyBases(null));
		assertFalse(alge4.containsAnyBases(null));
		
		assertFalse(alge1.containsAnyBases(bases0));
		assertFalse(alge2.containsAnyBases(bases0));
		assertFalse(alge3.containsAnyBases(bases0));
		assertFalse(alge4.containsAnyBases(bases0));
		
		assertFalse(alge1.containsAnyBases(bases1));
		assertTrue (alge2.containsAnyBases(bases1));
		assertFalse(alge3.containsAnyBases(bases1));
		assertTrue (alge4.containsAnyBases(bases1));
		
		assertFalse(alge1.containsAnyBases(bases2));
		assertFalse(alge2.containsAnyBases(bases2));
		assertTrue (alge3.containsAnyBases(bases2));
		assertTrue (alge4.containsAnyBases(bases2));
		
		assertFalse(alge1.containsAnyBases(bases3));
		assertTrue (alge2.containsAnyBases(bases3));
		assertTrue (alge3.containsAnyBases(bases3));
		assertTrue (alge4.containsAnyBases(bases3));
	}

	/**
	 * {@link dtalge.Dtalge#containsAllBases(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testContainsAllBases() {
		DtBaseSet bases0 = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet();
		DtBaseSet bases2 = new DtBaseSet();
		DtBaseSet bases3 = new DtBaseSet();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			bases1.add(eBase);
			bases3.add(eBase);
		}
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			bases2.add(eBase);
			bases3.add(eBase);
		}
		
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge alge4 = new Dtalge(catAlgeObjects(normalAlges, normalOtherAlges));
		
		assertFalse(alge1.containsAllBases(null));
		assertFalse(alge2.containsAllBases(null));
		assertFalse(alge3.containsAllBases(null));
		assertFalse(alge4.containsAllBases(null));
		
		assertFalse(alge1.containsAllBases(bases0));
		assertFalse(alge2.containsAllBases(bases0));
		assertFalse(alge3.containsAllBases(bases0));
		assertFalse(alge4.containsAllBases(bases0));
		
		assertFalse(alge1.containsAllBases(bases1));
		assertTrue (alge2.containsAllBases(bases1));
		assertFalse(alge3.containsAllBases(bases1));
		assertTrue (alge4.containsAllBases(bases1));
		
		assertFalse(alge1.containsAllBases(bases2));
		assertFalse(alge2.containsAllBases(bases2));
		assertTrue (alge3.containsAllBases(bases2));
		assertTrue (alge4.containsAllBases(bases2));
		
		assertFalse(alge1.containsAllBases(bases3));
		assertFalse(alge2.containsAllBases(bases3));
		assertFalse(alge3.containsAllBases(bases3));
		assertTrue (alge4.containsAllBases(bases3));
	}

	/**
	 * {@link dtalge.Dtalge#containsValue(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testContainsValue() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			Object eValue = normalAlges[i+1];
			assertFalse(alge1.containsBase(eBase));
			assertTrue("not found " + eBase.toString(), alge2.containsBase(eBase));
			assertFalse("is exist " + eBase.toString(), alge3.containsBase(eBase));
			assertFalse(alge1.containsValue(eValue));
			assertTrue("not found " + String.valueOf(eValue), alge2.containsValue(eValue));
			assertFalse("is exist " + String.valueOf(eValue), alge3.containsValue(eValue));
		}
		
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			Object eValue = normalOtherAlges[i+1];
			assertFalse(alge1.containsBase(eBase));
			assertFalse("is exist " + eBase.toString(), alge2.containsBase(eBase));
			assertTrue("not found " + eBase.toString(), alge3.containsBase(eBase));
			assertFalse(alge1.containsValue(eValue));
			assertFalse("is exist " + String.valueOf(eValue), alge2.containsValue(eValue));
			assertTrue("not found " + String.valueOf(eValue), alge3.containsValue(eValue));
		}
	}

	/**
	 * {@link dtalge.Dtalge#containsAnyValues(Collection)} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testContainsAnyValues() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge orgAlge1 = new Dtalge();
		Dtalge orgAlge2 = new Dtalge(normalAlges);
		Dtalge orgAlge3 = new Dtalge(normalOtherAlges);
		
		Collection<?> c1 = Collections.<Object>emptyList();
		Collection<?> c2 = Arrays.<Object>asList(objNull, objDecimal2);
		Collection<?> c3 = Arrays.<Object>asList(objOtherDecimal2, objOtherString);
		Collection<?> c4 = Arrays.<Object>asList(objDecimal2, objString, objOtherDecimal2, objOtherString);
		
		boolean ret;
		
		// null
		ret = alge1.containsAnyValues(null);
		assertFalse(ret);
		ret = alge2.containsAnyValues(null);
		assertFalse(ret);
		ret = alge3.containsAnyValues(null);
		assertFalse(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c1
		ret = alge1.containsAnyValues(c1);
		assertFalse(ret);
		ret = alge2.containsAnyValues(c1);
		assertFalse(ret);
		ret = alge3.containsAnyValues(c1);
		assertFalse(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c2
		ret = alge1.containsAnyValues(c2);
		assertFalse(ret);
		ret = alge2.containsAnyValues(c2);
		assertTrue(ret);
		ret = alge3.containsAnyValues(c2);
		assertFalse(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c3
		ret = alge1.containsAnyValues(c3);
		assertFalse(ret);
		ret = alge2.containsAnyValues(c3);
		assertFalse(ret);
		ret = alge3.containsAnyValues(c3);
		assertTrue(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c4
		ret = alge1.containsAnyValues(c4);
		assertFalse(ret);
		ret = alge2.containsAnyValues(c4);
		assertTrue(ret);
		ret = alge3.containsAnyValues(c4);
		assertTrue(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
	}
	
	/**
	 * {@link dtalge.Dtalge#containsAllValues(Collection)} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testContainsAllValues() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge orgAlge1 = new Dtalge();
		Dtalge orgAlge2 = new Dtalge(normalAlges);
		Dtalge orgAlge3 = new Dtalge(normalOtherAlges);
		
		Collection<?> c1 = Collections.<Object>emptyList();
		Collection<?> c2 = Arrays.<Object>asList(objNull, objDecimal2);
		Collection<?> c3 = Arrays.<Object>asList(objOtherDecimal2, objOtherString);
		Collection<?> c4 = Arrays.<Object>asList(objDecimal2, objString, objOtherDecimal2, objOtherString);
		
		boolean ret;
		
		// null
		ret = alge1.containsAllValues(null);
		assertFalse(ret);
		ret = alge2.containsAllValues(null);
		assertFalse(ret);
		ret = alge3.containsAllValues(null);
		assertFalse(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c1
		ret = alge1.containsAllValues(c1);
		assertFalse(ret);
		ret = alge2.containsAllValues(c1);
		assertFalse(ret);
		ret = alge3.containsAllValues(c1);
		assertFalse(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c2
		ret = alge1.containsAllValues(c2);
		assertFalse(ret);
		ret = alge2.containsAllValues(c2);
		assertTrue(ret);
		ret = alge3.containsAllValues(c2);
		assertFalse(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c3
		ret = alge1.containsAllValues(c3);
		assertFalse(ret);
		ret = alge2.containsAllValues(c3);
		assertFalse(ret);
		ret = alge3.containsAllValues(c3);
		assertTrue(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// c4
		ret = alge1.containsAllValues(c4);
		assertFalse(ret);
		ret = alge2.containsAllValues(c4);
		assertFalse(ret);
		ret = alge3.containsAllValues(c4);
		assertFalse(ret);
		//---
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
	}
	
	/**
	 * {@link dtalge.Dtalge#containsNull()} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testContainsNull() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge orgAlge2 = new Dtalge(normalAlges);
		Dtalge orgAlge3 = new Dtalge(normalOtherAlges);
		
		boolean ret;
		
		ret = alge1.containsNull();
		assertFalse(ret);
		assertTrue(alge1.isEmpty());
		
		ret = alge2.containsNull();
		assertTrue(ret);
		assertEquals(alge2, orgAlge2);
		
		ret = alge3.containsNull();
		assertFalse(ret);
		assertEquals(alge3, orgAlge3);
	}

	/**
	 * {@link dtalge.Dtalge#getBases()} のためのテスト・メソッド。
	 */
	public void testGetBases() {
		DtBaseSet bases;

		Dtalge alge0 = new Dtalge();
		bases = alge0.getBases();
		assertTrue(bases.isEmpty());
		
		Dtalge alge1 = new Dtalge(normalAlges);
		bases = alge1.getBases();
		assertFalse(bases.isEmpty());
		assertEquals((normalAlges.length/2), bases.size());
		assertEquals(alge1.getNumElements(), bases.size());
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			assertTrue(bases.contains(eBase));
		}
		
		Dtalge alge2 = new Dtalge(normalOtherAlges);
		bases = alge2.getBases();
		assertFalse(bases.isEmpty());
		assertEquals((normalOtherAlges.length/2), bases.size());
		assertEquals(alge2.getNumElements(), bases.size());
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			assertTrue(bases.contains(eBase));
		}
	}

	/**
	 * {@link dtalge.Dtalge#updateMatchedBases(dtalge.DtBaseSet, dtalge.DtBaseSet, dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testUpdateMatchedBasesDtBaseSetDtBaseSetDtBasePattern() {
		DtBasePattern pattern = new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*");
		DtBaseSet decimalBases = new DtBaseSet();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				decimalBases.add(eBase);
			}
		}

		DtBaseSet bases0e = new DtBaseSet();
		DtBaseSet bases0n = new DtBaseSet();
		Dtalge alge0 = new Dtalge();
		alge0.updateMatchedBases(bases0e, bases0n, pattern);
		assertTrue(bases0e.isEmpty());
		assertTrue(bases0n.isEmpty());
		
		DtBaseSet bases1e = new DtBaseSet();
		DtBaseSet bases1n = new DtBaseSet();
		Dtalge alge1 = new Dtalge(normalAlges);
		alge1.updateMatchedBases(bases1e, bases1n, pattern);
		assertFalse(bases1e.isEmpty());
		assertFalse(bases1n.isEmpty());
		assertEquals(decimalBases.size(), bases1e.size());
		assertEquals(alge1.getNumElements()-decimalBases.size(), bases1n.size());
		for (DtBase base : decimalBases) {
			assertTrue(bases1e.contains(base));
			assertFalse(bases1n.contains(base));
		}
	}

	/**
	 * {@link dtalge.Dtalge#updateMatchedBases(dtalge.DtBaseSet, dtalge.DtBaseSet, dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testUpdateMatchedBasesDtBaseSetDtBaseSetDtBasePatternSet() {
		DtBasePatternSet pset = new DtBasePatternSet();
		pset.add(new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*"));
		pset.add(new DtBasePattern("*", DtDataTypes.STRING, "*", "*"));
		DtBaseSet matchedBases = new DtBaseSet();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				matchedBases.add(eBase);
			}
			if (DtDataTypes.STRING.equals(eBase.getTypeKey())) {
				matchedBases.add(eBase);
			}
		}
		
		DtBaseSet bases0e = new DtBaseSet();
		DtBaseSet bases0n = new DtBaseSet();
		Dtalge alge0 = new Dtalge();
		alge0.updateMatchedBases(bases0e, bases0n, pset);
		assertTrue(bases0e.isEmpty());
		assertTrue(bases0n.isEmpty());
		
		DtBaseSet bases1e = new DtBaseSet();
		DtBaseSet bases1n = new DtBaseSet();
		Dtalge alge1 = new Dtalge(normalAlges);
		alge1.updateMatchedBases(bases1e, bases1n, pset);
		assertFalse(bases1e.isEmpty());
		assertFalse(bases1n.isEmpty());
		assertEquals(matchedBases.size(), bases1e.size());
		assertEquals(alge1.getNumElements()-matchedBases.size(), bases1n.size());
		for (DtBase base : matchedBases) {
			assertTrue(bases1e.contains(base));
			assertFalse(bases1n.contains(base));
		}
	}

	/**
	 * {@link dtalge.Dtalge#getMatchedBases(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetMatchedBasesDtBasePattern() {
		DtBasePattern pattern = new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*");
		DtBaseSet decimalBases = new DtBaseSet();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				decimalBases.add(eBase);
			}
		}

		Dtalge alge0 = new Dtalge();
		DtBaseSet bases0 = alge0.getMatchedBases(pattern);
		assertTrue(bases0.isEmpty());

		Dtalge alge1 = new Dtalge(normalAlges);
		DtBaseSet bases1 = alge1.getMatchedBases(pattern);
		assertFalse(bases1.isEmpty());
		assertEquals(decimalBases.size(), bases1.size());
		for (DtBase base : decimalBases) {
			assertTrue(bases1.contains(base));
		}
	}

	/**
	 * {@link dtalge.Dtalge#getMatchedBases(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testGetMatchedBasesDtBasePatternSet() {
		DtBasePatternSet pset = new DtBasePatternSet();
		pset.add(new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*"));
		pset.add(new DtBasePattern("*", DtDataTypes.STRING, "*", "*"));
		DtBaseSet matchedBases = new DtBaseSet();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				matchedBases.add(eBase);
			}
			if (DtDataTypes.STRING.equals(eBase.getTypeKey())) {
				matchedBases.add(eBase);
			}
		}

		Dtalge alge0 = new Dtalge();
		DtBaseSet bases0 = alge0.getMatchedBases(pset);
		assertTrue(bases0.isEmpty());

		Dtalge alge1 = new Dtalge(normalAlges);
		DtBaseSet bases1 = alge1.getMatchedBases(pset);
		assertFalse(bases1.isEmpty());
		assertEquals(matchedBases.size(), bases1.size());
		for (DtBase base : matchedBases) {
			assertTrue(bases1.contains(base));
		}
	}

	/**
	 * {@link dtalge.Dtalge#getElement()} のためのテスト・メソッド。
	 */
	public void testGetElement() {
		Dtalge alge0 = new Dtalge();
		try {
			Map.Entry<DtBase, Object> entry = alge0.getElement();
			fail("Dtalge[" + alge0.toString() + "].[" + entry.toString() + "] must be throw NoSuchElementException.");
		}
		catch (NoSuchElementException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}

		DtBase eBase = (DtBase)normalAlges[0];
		Object eValue = normalAlges[1];
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			Map.Entry<DtBase, Object> entry = alge1.getElement();
			assertEquals(eBase, entry.getKey());
			assertEquals(eValue, entry.getValue());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#getOneBase()} のためのテスト・メソッド。
	 */
	public void testGetOneBase() {
		Dtalge alge0 = new Dtalge();
		try {
			DtBase base = alge0.getOneBase();
			fail("Dtalge[" + alge0.toString() + "]->[" + base.toString() + "] must be throw NoSuchElementException.");
		}
		catch (NoSuchElementException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}

		DtBase eBase = (DtBase)normalAlges[0];
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			DtBase base = alge1.getOneBase();
			assertEquals(eBase, base);
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#getOneValue()} のためのテスト・メソッド。
	 */
	public void testGetValue() {
		Dtalge alge0 = new Dtalge();
		try {
			Object value = alge0.getOneValue();
			fail("Dtalge[" + alge0.toString() + "]->[" + String.valueOf(value) + "] must be throw NoSuchElementException.");
		}
		catch (NoSuchElementException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}

		Object eValue = normalAlges[1];
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			Object value = alge1.getOneValue();
			assertEquals(eValue, value);
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#get(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testGet() {
		Dtalge alge0 = new Dtalge();
		try {
			Object value = alge0.get((DtBase)normalAlges[0]);
			fail("Dtalge[" + alge0.toString() + "]->[" + String.valueOf(value) + "] must be throw DtBaseNotFoundException.");
		}
		catch (DtBaseNotFoundException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			Object value = alge0.get(null);
			fail("Dtalge[" + alge0.toString() + "]->[" + String.valueOf(value) + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			Object value = alge1.get(null);
			fail("Dtalge[" + alge1.toString() + "]->[" + String.valueOf(value) + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			for (int i = 0; i < normalAlges.length; i+=2) {
				DtBase eBase = (DtBase)normalAlges[i];
				Object eValue = normalAlges[i+1];
				Object value = alge1.get(eBase);
				assertEquals(eValue, value);
			}
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#put(dtalge.DtBase, java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testPutDtBaseObject() {
		Dtalge alge;
		
		//--- phase 1
		try {
			alge = new Dtalge();
			alge = alge.put(null, null);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		//--- phase2
		alge = new Dtalge();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			Object eValue = normalAlges[i+1];
			alge = alge.put(eBase, eValue);
		}
		assertTrue(equalElementSequence(alge, normalAlges));
		
		//--- phase3
		Object[] normalAlges2 = catAlgeObjects(normalAlges, normalOtherAlges);
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			Object eValue = normalOtherAlges[i+1];
			alge = alge.put(eBase, eValue);
		}
		assertTrue(equalElementSequence(alge, normalAlges2));
		
		//--- phase4
		normalAlges2[5] = new BigDecimal("9876.54321");
		alge = alge.put((DtBase)normalAlges2[4], normalAlges2[5]);
		assertTrue(equalElementSequence(alge, normalAlges2));
	}

	/**
	 * {@link dtalge.Dtalge#put(dtalge.Dtalge)} のためのテスト・メソッド。
	 */
	public void testPutDtalge() {
		Dtalge alge;
		
		//--- phase 1
		try {
			alge = new Dtalge();
			alge = alge.put(null);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		//--- phase 2
		alge = new Dtalge();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			Object eValue = normalAlges[i+1];
			alge = alge.put(new Dtalge(eBase, eValue));
		}
		assertTrue(equalElementSequence(alge, normalAlges));
		
		//--- phase3
		Object[] normalAlges2 = catAlgeObjects(normalAlges, normalOtherAlges);
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			Object eValue = normalOtherAlges[i+1];
			alge = alge.put(new Dtalge(eBase, eValue));
		}
		assertTrue(equalElementSequence(alge, normalAlges2));
		
		//--- phase4
		normalAlges2[5] = new BigDecimal("9876.54321");
		alge = alge.put(new Dtalge((DtBase)normalAlges2[4], normalAlges2[5]));
		assertTrue(equalElementSequence(alge, normalAlges2));
	}

	/**
	 * {@link dtalge.Dtalge#normalization()} のためのテスト・メソッド。
	 */
	public void testNormalization() {
		Dtalge alge0 = new Dtalge();
		Dtalge ret0 = alge0.normalization();
		assertTrue(ret0.isEmpty());
		
		Dtalge alge1 = new Dtalge(normalOtherAlges);
		Dtalge ret1 = alge1.normalization();
		assertEquals(alge1, ret1);
		assertTrue(equalElementSequence(ret1, normalOtherAlges));

		ArrayList<Object> ary = new ArrayList<Object>();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			Object eValue = normalAlges[i+1];
			if (eValue != null) {
				ary.add(eBase);
				ary.add(eValue);
			}
		}
		Object[] normalAlges2 = ary.toArray(new Object[ary.size()]);
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge ret2 = alge2.normalization();
		assertTrue(equalElementSequence(alge2, normalAlges));
		assertTrue(equalElementSequence(ret2, normalAlges2));
	}

	/**
	 * {@link dtalge.Dtalge#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		Dtalge alge0 = new Dtalge();
		assertTrue(alge0.isEmpty());
		assertEquals(0, alge0.hashCode());
		
		Dtalge alge1 = new Dtalge(normalAlges);
		int hash1 = 0;
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			Object eValue = normalAlges[i+1];
			int hv = 0;
			if (eValue instanceof BigDecimal) {
				if (0!=BigDecimal.ZERO.compareTo((BigDecimal)eValue)) {
					hv = ((BigDecimal)eValue).stripTrailingZeros().hashCode();
				}
			} else if (eValue != null) {
				hv = eValue.hashCode();
			}
			int h = eBase.hashCode() ^ hv;
			hash1 += h;
		}
		assertTrue(equalElementSequence(alge1, normalAlges));
		assertEquals(hash1, alge1.hashCode());
		
		Dtalge alge2 = new Dtalge(normalOtherAlges);
		int hash2 = 0;
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalOtherAlges[i];
			Object eValue = normalOtherAlges[i+1];
			int hv = 0;
			if (eValue instanceof BigDecimal) {
				if (0!=BigDecimal.ZERO.compareTo((BigDecimal)eValue)) {
					hv = ((BigDecimal)eValue).stripTrailingZeros().hashCode();
				}
			} else if (eValue != null) {
				hv = eValue.hashCode();
			}
			int h = eBase.hashCode() ^ hv;
			hash2 += h;
		}
		assertTrue(equalElementSequence(alge2, normalOtherAlges));
		assertEquals(hash2, alge2.hashCode());
	}

	/**
	 * {@link dtalge.Dtalge#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		Dtalge alge1a = new Dtalge();
		Dtalge alge1b = new Dtalge();
		Dtalge alge2a = new Dtalge(normalAlges);
		Dtalge alge2b = new Dtalge(normalAlges);
		Dtalge alge3a = new Dtalge(normalOtherAlges);
		Dtalge alge3b = new Dtalge(normalOtherAlges);
		
		assertTrue(alge1a.isEmpty());
		assertTrue(alge1b.isEmpty());
		assertTrue(equalElementSequence(alge2a, normalAlges));
		assertTrue(equalElementSequence(alge2b, normalAlges));
		assertTrue(equalElementSequence(alge3a, normalOtherAlges));
		assertTrue(equalElementSequence(alge3b, normalOtherAlges));
		
		assertEquals(alge1a.hashCode(), alge1b.hashCode());
		assertTrue(alge1a.equals(alge1b));
		assertTrue(alge1b.equals(alge1a));
		assertEquals(alge2a.hashCode(), alge2b.hashCode());
		assertTrue(alge2a.equals(alge2b));
		assertTrue(alge2b.equals(alge2a));
		assertEquals(alge3a.hashCode(), alge3b.hashCode());
		assertTrue(alge3a.equals(alge3b));
		assertTrue(alge3b.equals(alge3a));
		
		assertFalse(alge1a.equals(alge2b));
		assertFalse(alge1a.equals(alge3b));
		assertFalse(alge2a.equals(alge1b));
		assertFalse(alge2a.equals(alge3b));
		assertFalse(alge3a.equals(alge1b));
		assertFalse(alge3a.equals(alge2b));
	}

	/**
	 * {@link dtalge.Dtalge#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		Dtalge alge0 = new Dtalge();
		assertEquals("()", alge0.toString());
		
		Dtalge alge1 = new Dtalge(normalAlges);
		String str1;
		{
			StringBuilder sb = new StringBuilder();
			//sb.append(normalAlges[1]);
			Object val = normalAlges[1];
			if (val instanceof BigDecimal) {
				sb.append(((BigDecimal)val).stripTrailingZeros().toPlainString());
			} else {
				sb.append(val);
			}
			sb.append(normalAlges[0]);
			for (int i = 2; i < normalAlges.length; i+=2) {
				sb.append('+');
				//sb.append(normalAlges[i+1]);
				val = normalAlges[i+1];
				if (val instanceof BigDecimal) {
					sb.append(((BigDecimal)val).stripTrailingZeros().toPlainString());
				} else {
					sb.append(val);
				}
				sb.append(normalAlges[i]);
			}
			str1 = sb.toString();
		}
		assertEquals(str1, alge1.toString());
		
		Dtalge alge2 = new Dtalge(normalOtherAlges);
		String str2;
		{
			StringBuilder sb = new StringBuilder();
			//sb.append(normalOtherAlges[1]);
			Object val = normalOtherAlges[1];
			if (val instanceof BigDecimal) {
				sb.append(((BigDecimal)val).stripTrailingZeros().toPlainString());
			} else {
				sb.append(val);
			}
			sb.append(normalOtherAlges[0]);
			for (int i = 2; i < normalOtherAlges.length; i+=2) {
				sb.append('+');
				//sb.append(normalOtherAlges[i+1]);
				val = normalOtherAlges[i+1];
				if (val instanceof BigDecimal) {
					sb.append(((BigDecimal)val).stripTrailingZeros().toPlainString());
				} else {
					sb.append(val);
				}
				sb.append(normalOtherAlges[i]);
			}
			str2 = sb.toString();
		}
		assertEquals(str2, alge2.toString());
	}

	/**
	 * {@link dtalge.Dtalge#iterator()} のためのテスト・メソッド。
	 */
	public void testIterator() {
		Dtalge alge0 = new Dtalge();
		Iterator<Dtalge> it0 = alge0.iterator();
		assertFalse(it0.hasNext());
		try {
			Dtalge ialge = it0.next();
			fail("Dtalge[" + ialge.toString() + "] must be throw NoSuchElementException.");
		}
		catch (NoSuchElementException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		Dtalge alge1 = new Dtalge(normalAlges);
		Iterator<Dtalge> it1 = alge1.iterator();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			Object eValue = normalAlges[i+1];
			assertTrue(it1.hasNext());
			Dtalge ialge = it1.next();
			assertEquals(new Dtalge(eBase, eValue), ialge);
			try {
				it1.remove();
				fail("it1[" + it1.toString() + "] must be throw UnsupportedOperationException.");
			}
			catch (UnsupportedOperationException ex) {
				System.out.println("Valid exception : " + ex.toString());
			}
			catch (Exception ex) {
				fail("Unknown exception : " + ex.toString());
			}
		}
		assertFalse(it1.hasNext());
	}
	
	/**
	 * {@link dtalge.Dtalge#oneValueProjection(Object)} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testOneValueProjection() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge orgAlge1 = new Dtalge();
		Dtalge orgAlge2 = new Dtalge(normalAlges);
		Dtalge orgAlge3 = new Dtalge(normalOtherAlges);
		
		Object[] values = new Object[]{
				objNull,
				objDecimal2,
				objString,
				objOtherDecimal2,
				objOtherString,
		};
		
		// test
		for (int i = 0; i < values.length; i++) {
			Dtalge ret1 = alge1.oneValueProjection(values[i]);
			assertTrue(ret1.isEmpty());
			assertEquals(alge1, orgAlge1);

			Dtalge ans2 = new Dtalge(filterAlgeValue(normalAlges, values[i]));
			Dtalge ret2 = alge2.oneValueProjection(values[i]);
			assertEquals(ret2, ans2);
			assertEquals(alge2, orgAlge2);
			
			Dtalge ans3 = new Dtalge(filterAlgeValue(normalOtherAlges, values[i]));
			Dtalge ret3 = alge3.oneValueProjection(values[i]);
			assertEquals(ret3, ans3);
			assertEquals(alge3, orgAlge3);
		}
	}
	
	/**
	 * {@link dtalge.Dtalge#valuesProjection(Collection)} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testValuesProjection() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge orgAlge1 = new Dtalge();
		Dtalge orgAlge2 = new Dtalge(normalAlges);
		Dtalge orgAlge3 = new Dtalge(normalOtherAlges);
		
		Object[] c1 = new Object[0];
		Object[] c2 = new Object[]{objNull, objDecimal2};
		Object[] c3 = new Object[]{objOtherDecimal2, objOtherString};
		Object[] c4 = new Object[]{objDecimal2, objString, objOtherDecimal2, objOtherString};
		Object[][] values = new Object[][]{c1, c2, c3, c4};
		
		// null
		try {
			alge1.valuesProjection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			alge2.valuesProjection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			alge3.valuesProjection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		assertEquals(alge1, orgAlge1);
		assertEquals(alge2, orgAlge2);
		assertEquals(alge3, orgAlge3);
		
		// test
		for (int i = 0; i < values.length; i++) {
			Dtalge ret1 = alge1.valuesProjection(Arrays.asList(values[i]));
			assertTrue(ret1.isEmpty());
			assertEquals(alge1, orgAlge1);

			Dtalge ans2 = new Dtalge(filterAlgeValue(normalAlges, values[i]));
			Dtalge ret2 = alge2.valuesProjection(Arrays.asList(values[i]));
			assertEquals(ret2, ans2);
			assertEquals(alge2, orgAlge2);
			
			Dtalge ans3 = new Dtalge(filterAlgeValue(normalOtherAlges, values[i]));
			Dtalge ret3 = alge3.valuesProjection(Arrays.asList(values[i]));
			assertEquals(ret3, ans3);
			assertEquals(alge3, orgAlge3);
		}
	}
	
	/**
	 * {@link dtalge.Dtalge#nullProjection()} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testNullProjection() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge orgAlge1 = new Dtalge();
		Dtalge orgAlge2 = new Dtalge(normalAlges);
		Dtalge orgAlge3 = new Dtalge(normalOtherAlges);
		
		Dtalge ret;
		
		ret = alge1.nullProjection();
		assertTrue(ret.isEmpty());
		assertEquals(alge1, orgAlge1);
		
		ret = alge2.nullProjection();
		assertEquals(ret, new Dtalge(filterAlgeValue(normalAlges, objNull)));
		assertEquals(alge2, orgAlge2);
		
		ret = alge3.nullProjection();
		assertEquals(ret, new Dtalge(filterAlgeValue(normalOtherAlges, objNull)));
		assertEquals(alge3, orgAlge3);
	}
	
	/**
	 * {@link dtalge.Dtalge#nonullProjection()} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testNoNullProjection() {
		Dtalge alge1 = new Dtalge();
		Dtalge alge2 = new Dtalge(normalAlges);
		Dtalge alge3 = new Dtalge(normalOtherAlges);
		Dtalge orgAlge1 = new Dtalge();
		Dtalge orgAlge2 = new Dtalge(normalAlges);
		Dtalge orgAlge3 = new Dtalge(normalOtherAlges);
		
		Dtalge ret;
		
		ret = alge1.nonullProjection();
		assertTrue(ret.isEmpty());
		assertEquals(alge1, orgAlge1);
		
		ret = alge2.nonullProjection();
		assertEquals(ret, new Dtalge(removeAlgeValue(normalAlges, objNull)));
		assertEquals(alge2, orgAlge2);
		
		ret = alge3.nonullProjection();
		assertEquals(ret, new Dtalge(removeAlgeValue(normalOtherAlges, objNull)));
		assertEquals(alge3, orgAlge3);
	}

	/**
	 * {@link dtalge.Dtalge#projection(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testProjectionDtBase() {
		Dtalge palge;
		
		DtBase base1 = (DtBase)normalAlges[4];
		Object val1 = normalAlges[5];
		DtBase base2 = (DtBase)normalAlges[10];
		Object val2 = normalAlges[11];
		DtBase base3 = (DtBase)normalOtherAlges[6];
		//Object val3 = normalOtherAlges[7];
		
		Dtalge alge0 = new Dtalge();
		try {
			palge = alge0.projection((DtBase)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge0.projection(base1);
		assertTrue(palge.isEmpty());
		palge = alge0.projection(base2);
		assertTrue(palge.isEmpty());
		palge = alge0.projection(base3);
		assertTrue(palge.isEmpty());
		
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			palge = alge1.projection((DtBase)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge1.projection(base1);
		assertEquals(1, palge.getNumElements());
		assertEquals(new Dtalge(base1, val1), palge);
		palge = alge1.projection(base2);
		assertEquals(1, palge.getNumElements());
		assertEquals(new Dtalge(base2, val2), palge);
		palge = alge1.projection(base3);
		assertTrue(palge.isEmpty());
	}

	/**
	 * {@link dtalge.Dtalge#projection(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testProjectionDtBaseSet() {
		Dtalge palge;
		
		DtBase base1 = (DtBase)normalAlges[4];
		Object val1 = normalAlges[5];
		DtBase base2 = (DtBase)normalAlges[10];
		Object val2 = normalAlges[11];
		DtBase base3 = (DtBase)normalOtherAlges[6];
		//Object val3 = normalOtherAlges[7];
		DtBaseSet bset = new DtBaseSet();
		bset.add(base1);
		bset.add(base2);
		bset.add(base3);
		
		Dtalge alge0 = new Dtalge();
		try {
			palge = alge0.projection((DtBaseSet)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge0.projection(bset);
		assertTrue(palge.isEmpty());
		
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			palge = alge1.projection((DtBaseSet)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge1.projection(bset);
		Object[] ralges = new Object[]{
			base1, val1, base2, val2,	
		};
		assertFalse(palge.isEmpty());
		assertTrue(equalElementSequence(palge, ralges));
	}

	/**
	 * {@link dtalge.Dtalge#patternProjection(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testPatternProjectionDtBasePattern() {
		//--- results
		DtBasePattern pattern = new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*");
		ArrayList<Object> ary = new ArrayList<Object>();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				ary.add(eBase);
				ary.add(normalAlges[i+1]);
			}
		}
		Object[] retAlges = ary.toArray(new Object[ary.size()]);
		Dtalge palge;

		//--- test1
		Dtalge alge0 = new Dtalge();
		try {
			palge = alge0.patternProjection((DtBasePattern)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge0.patternProjection(pattern);
		assertTrue(palge.isEmpty());

		//--- test2
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			palge = alge1.patternProjection((DtBasePattern)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge1.patternProjection(pattern);
		assertFalse(palge.isEmpty());
		assertTrue(equalElementSequence(palge, retAlges));
	}

	/**
	 * {@link dtalge.Dtalge#patternProjection(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testPatternProjectionDtBasePatternSet() {
		//--- results
		DtBasePatternSet pset = new DtBasePatternSet();
		pset.add(new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*"));
		pset.add(new DtBasePattern("*", DtDataTypes.STRING, "*", "*"));
		ArrayList<Object> ary = new ArrayList<Object>();
		for (int i = 0; i < normalAlges.length; i+=2) {
			DtBase eBase = (DtBase)normalAlges[i];
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				ary.add(eBase);
				ary.add(normalAlges[i+1]);
			}
			else if (DtDataTypes.STRING.equals(eBase.getTypeKey())) {
				ary.add(eBase);
				ary.add(normalAlges[i+1]);
			}
		}
		Object[] retAlges = ary.toArray(new Object[ary.size()]);
		Dtalge palge;

		//--- test1
		Dtalge alge0 = new Dtalge();
		try {
			palge = alge0.patternProjection((DtBasePatternSet)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge0.patternProjection(pset);
		assertTrue(palge.isEmpty());

		//--- test2
		Dtalge alge1 = new Dtalge(normalAlges);
		try {
			palge = alge1.patternProjection((DtBasePatternSet)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		palge = alge1.patternProjection(pset);
		assertFalse(palge.isEmpty());
		assertTrue(equalElementSequence(palge, retAlges));
	}

	/**
	 * {@link dtalge.Dtalge#thesconv(dtalge.DtBase, java.lang.Object, java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testThesconvDtBaseObjectObject() {
		//fail("まだ実装されていません。");
		Dtalge palge;

		final DtBase base0 = new DtBase("testNone", DtDataTypes.STRING, "#", "none");
		//final DtBase base1 = new DtBase("testBoolean", DtDataTypes.BOOLEAN, "#", "null");
		final DtBase base2 = new DtBase("testInteger", DtDataTypes.DECIMAL, "#", "null");
		final DtBase base3 = new DtBase("testDecimal", DtDataTypes.DECIMAL, "#", "null");
		//final DtBase base4 = new DtBase("testString" , DtDataTypes.STRING , "#", "null");
		//final DtBase base5 = new DtBase("testBoolean", DtDataTypes.BOOLEAN, "#", objBoolean.toString());
		//final DtBase base6 = new DtBase("testInteger", DtDataTypes.INTEGER, "#", objInteger.toString());
		final DtBase base7 = new DtBase("testDecimal", DtDataTypes.DECIMAL, "#", objDecimal.toString());
		final DtBase base8 = new DtBase("testString" , DtDataTypes.STRING , "#", objString.toString());
		
		// test1
		Dtalge alge0 = new Dtalge();
		//--- null
		try {
			palge = alge0.thesconv((DtBase)null, (Object)null, (Object)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- invalid data type
		try {
			palge = alge0.thesconv(base3, objDecimal, objBoolean);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		}
		catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//try {
		//	palge = alge0.thesconv(base3, objDecimal, objInteger);
		//	fail("Dtalge[" + palge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//}
		//catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		//catch (Exception ex) {
		//	fail("Unknown exception : " + ex.toString());
		//}
		try {
			palge = alge0.thesconv(base3, objDecimal, objString);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		}
		catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- not exist case
		palge = alge0.thesconv(base0, "none", "hoge");
		assertTrue(palge.isEmpty());
		//palge = alge0.thesconv(base2, objInteger, new BigInteger("4321"));
		palge = alge0.thesconv(base2, objInteger, new BigDecimal("4321"));
		assertTrue(palge.isEmpty());
		
		// test2
		Dtalge alge1 = new Dtalge(normalAlges);
		assertTrue(equalElementSequence(alge1, normalAlges));
		//--- null
		try {
			palge = alge1.thesconv((DtBase)null, (Object)null, (Object)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- invalid data type
		try {
			palge = alge1.thesconv(base3, objDecimal, objBoolean);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		}
		catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//try {
		//	palge = alge1.thesconv(base3, objDecimal, objInteger);
		//	fail("Dtalge[" + palge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//}
		//catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		//catch (Exception ex) {
		//	fail("Unknown exception : " + ex.toString());
		//}
		try {
			palge = alge1.thesconv(base3, objDecimal, objString);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		}
		catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- not exist case
		palge = alge1.thesconv(base0, "none", "hoge");
		assertEquals(alge1, palge);
		assertSame(alge1, palge);
		//palge = alge1.thesconv(base2, objInteger, new BigInteger("4321"));
		palge = alge1.thesconv(base2, objInteger, new BigDecimal("4321"));
		assertEquals(alge1, palge);
		assertSame(alge1, palge);
		//--- exist pattern
		Object objSrc1 = null;
		//Object objDst1 = new BigInteger("4321");
		Object objDst1 = new BigDecimal("4321");
		Object objSrc2 = objDecimal;
		Object objDst2 = new BigDecimal("2008.08");
		Object objSrc3 = objString;
		Object objDst3 = null;
		assertFalse(objDst1.equals(objSrc1));
		assertFalse(objDst2.equals(objSrc2));
		assertFalse(objSrc3.equals(objDst3));
		assertEquals(objSrc1, alge1.get(base2));
		assertEquals(objSrc2, alge1.get(base7));
		assertEquals(objSrc3, alge1.get(base8));
		palge = alge1.thesconv(base2, objSrc1, objDst1);
		assertNotSame(palge, alge1);
		assertFalse(palge.equals(alge1));
		assertEquals(objSrc1, alge1.get(base2));
		assertEquals(objDst1, palge.get(base2));
		palge = alge1.thesconv(base7, objSrc2, objDst2);
		assertNotSame(palge, alge1);
		assertFalse(palge.equals(alge1));
		assertEquals(objSrc2, alge1.get(base7));
		assertEquals(objDst2, palge.get(base7));
		palge = alge1.thesconv(base8, objSrc3, objDst3);
		assertNotSame(palge, alge1);
		assertFalse(palge.equals(alge1));
		assertEquals(objSrc3, alge1.get(base8));
		assertEquals(objDst3, palge.get(base8));
	}

	/**
	 * {@link dtalge.Dtalge#thesconv(dtalge.DtBase, dtalge.DtStringThesaurus, java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testThesconvDtBaseDtStringThesaurusStringArray() {
		final String[] thesPairs = {
				"りんご", "つがる",
				"りんご", "富士",
				"果物", "りんご",
				"果物", "みかん",
				"果物", "メロン",
				"肉", "牛肉",
				"肉", "豚肉",
				"肉", "鶏肉",
				"食料品", "果物",
				"食料品", "肉",
		};
		final String[] thesClasses = {
				"りんご", "みかん", "メロン", "肉",
		};
		final String[] notClasses = {
				"果物", "みかん", "りんご",
		};
		DtBase base0 = new DtBase("none", "string");
		DtBase base1 = new DtBase("name", "string");
		DtBase base2 = new DtBase("male", "boolean");
		//DtBase base3 = new DtBase("age", "integer");
		DtBase base3 = new DtBase("age", "decimal");
		DtBase base4 = new DtBase("fruits", "string");
		DtBase base5 = new DtBase("meat", "string");
		DtBase base6 = new DtBase("fruitType", "string");
		final Object[] alges = {
				base1, "yamada",
				base2, Boolean.TRUE,
				//base3, new BigInteger("38"),
				base3, new BigDecimal("38"),
				base4, "富士",
				base5, null,
				base6, "りんご",
		};
		DtStringThesaurus thes = DtStringThesaurusTest.newThesaurus(thesPairs);
		Dtalge palge;
		
		// test1
		Dtalge alge0 = new Dtalge();
		assertTrue(alge0.isEmpty());
		//--- null
		try {
			palge = alge0.thesconv((DtBase)null, thes, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base4, (DtStringThesaurus)null, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base4, thes, (String[])null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- illegal arugment
		try {
			palge = alge0.thesconv(base2, thes, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base1, thes, notClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- not exist base
		try {
			palge = alge0.thesconv(base0, thes, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw DtBaseNotFoundException.");
		}
		catch (DtBaseNotFoundException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base4, thes, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw DtBaseNotFoundException.");
		}
		catch (DtBaseNotFoundException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		// test2
		Dtalge alge1 = new Dtalge(alges);
		assertTrue(equalElementSequence(alge1, alges));
		//--- null
		try {
			palge = alge1.thesconv((DtBase)null, thes, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base4, (DtStringThesaurus)null, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base4, thes, (String[])null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- illegal arugment
		try {
			palge = alge1.thesconv(base2, thes, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base1, thes, notClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base1, thes);
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- not exist base
		try {
			palge = alge1.thesconv(base0, thes, thesClasses);
			fail("Dtalge[" + palge.toString() + "] must be throw DtBaseNotFoundException.");
		}
		catch (DtBaseNotFoundException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- conversion
		palge = alge1.thesconv(base5, thes, thesClasses);
		assertNull(palge);
		assertTrue(equalElementSequence(alge1, alges));
		palge = alge1.thesconv(base4, thes, thesClasses);
		assertNotNull(palge);
		assertFalse(palge.isEmpty());
		assertTrue(equalElementSequence(alge1, alges));
		assertFalse(alge1.equals(palge));
		assertEquals("りんご", palge.get(base4));
		palge = alge1.thesconv(base6, thes, "りんご");
		assertNotNull(palge);
		assertEquals(palge, alge1);
		assertSame(palge, alge1);
	}

	/**
	 * {@link dtalge.Dtalge#thesconv(dtalge.DtBase, dtalge.DtStringThesaurus, java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testThesconvDtBaseDtStringThesaurusCollectionOfQextendsString() {
		final String[] thesPairs = {
				"りんご", "つがる",
				"りんご", "富士",
				"果物", "りんご",
				"果物", "みかん",
				"果物", "メロン",
				"肉", "牛肉",
				"肉", "豚肉",
				"肉", "鶏肉",
				"食料品", "果物",
				"食料品", "肉",
		};
		final String[] thesClasses = {
				"りんご", "みかん", "メロン", "肉",
		};
		final String[] notClasses = {
				"果物", "みかん", "りんご",
		};
		DtBase base0 = new DtBase("none", "string");
		DtBase base1 = new DtBase("name", "string");
		DtBase base2 = new DtBase("male", "boolean");
		//DtBase base3 = new DtBase("age", "integer");
		DtBase base3 = new DtBase("age", "decimal");
		DtBase base4 = new DtBase("fruits", "string");
		DtBase base5 = new DtBase("meat", "string");
		DtBase base6 = new DtBase("fruitType", "string");
		final Object[] alges = {
				base1, "yamada",
				base2, Boolean.TRUE,
				base3, new BigDecimal("38"),
				base4, "富士",
				base5, null,
				base6, "りんご",
		};
		DtStringThesaurus thes = DtStringThesaurusTest.newThesaurus(thesPairs);
		Dtalge palge;
		
		// test1
		Dtalge alge0 = new Dtalge();
		assertTrue(alge0.isEmpty());
		//--- null
		try {
			palge = alge0.thesconv((DtBase)null, thes, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base4, (DtStringThesaurus)null, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base4, thes, (Collection<String>)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- illegal arugment
		try {
			palge = alge0.thesconv(base2, thes, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base1, thes, Arrays.asList(notClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- not exist base
		try {
			palge = alge0.thesconv(base0, thes, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw DtBaseNotFoundException.");
		}
		catch (DtBaseNotFoundException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge0.thesconv(base4, thes, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw DtBaseNotFoundException.");
		}
		catch (DtBaseNotFoundException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		// test2
		Dtalge alge1 = new Dtalge(alges);
		assertTrue(equalElementSequence(alge1, alges));
		//--- null
		try {
			palge = alge1.thesconv((DtBase)null, thes, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base4, (DtStringThesaurus)null, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base4, thes, (Collection<String>)null);
			fail("Dtalge[" + palge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- illegal arugment
		try {
			palge = alge1.thesconv(base2, thes, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base1, thes, Arrays.asList(notClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		try {
			palge = alge1.thesconv(base1, thes, Arrays.asList(new String[0]));
			fail("Dtalge[" + palge.toString() + "] must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- not exist base
		try {
			palge = alge1.thesconv(base0, thes, Arrays.asList(thesClasses));
			fail("Dtalge[" + palge.toString() + "] must be throw DtBaseNotFoundException.");
		}
		catch (DtBaseNotFoundException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		//--- conversion
		palge = alge1.thesconv(base5, thes, Arrays.asList(thesClasses));
		assertNull(palge);
		assertTrue(equalElementSequence(alge1, alges));
		palge = alge1.thesconv(base4, thes, Arrays.asList(thesClasses));
		assertNotNull(palge);
		assertFalse(palge.isEmpty());
		assertTrue(equalElementSequence(alge1, alges));
		assertFalse(alge1.equals(palge));
		assertEquals("りんご", palge.get(base4));
		palge = alge1.thesconv(base6, thes, Arrays.asList("りんご"));
		assertNotNull(palge);
		assertEquals(palge, alge1);
		assertSame(palge, alge1);
	}

	/**
	 * {@link dtalge.Dtalge#putValue(dtalge.DtBase, java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testPutValueDtBaseObject() {
		Object objNull    = null;
		Object objBoolean = Boolean.TRUE;
		//Object objInteger = new BigInteger("123");
		Object objDecimal = new BigDecimal("0.1");
		Object objString  = new String("hogeString!");
		
		//--- make base
		DtBase baseBoolean = new DtBase("testDtalgeDtBaseObject", DtDataTypes.BOOLEAN, "#", objBoolean.toString());
		//DtBase baseInteger = new DtBase("testDtalgeDtBaseObject", DtDataTypes.INTEGER, "#", objInteger.toString());
		DtBase baseDecimal = new DtBase("testDtalgeDtBaseObject", DtDataTypes.DECIMAL, "#", objDecimal.toString());
		DtBase baseString  = new DtBase("testDtalgeDtBaseObject", DtDataTypes.STRING,  "#", objString.toString());
		
		Dtalge alge = new Dtalge(1);
		
		//--- null
		try {
			alge.putValue(null, objNull);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- Boolean
		alge = new Dtalge(1);
		alge.putValue(baseBoolean, objNull);
		assertTrue(equalElementSequence(alge, baseBoolean, objNull));
		alge.putValue(baseBoolean, objBoolean);
		assertTrue(equalElementSequence(alge, baseBoolean, objBoolean));
		//try {
		//	alge.putValue(baseInteger, objBoolean);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			alge.putValue(baseDecimal, objBoolean);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			alge.putValue(baseString, objBoolean);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		
		//--- Integer
		/*
		alge = new Dtalge(1);
		alge.putValue(baseInteger, objNull);
		assertTrue(equalElementSequence(alge, baseInteger, objNull));
		alge.putValue(baseInteger, objInteger);
		assertTrue(equalElementSequence(alge, baseInteger, objInteger));
		try {
			alge.putValue(baseBoolean, objInteger);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			alge.putValue(baseDecimal, objInteger);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		try {
			alge.putValue(baseString, objInteger);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		*/
		
		//--- Decimal
		alge = new Dtalge(1);
		alge.putValue(baseDecimal, objNull);
		assertTrue(equalElementSequence(alge, baseDecimal, objNull));
		alge.putValue(baseDecimal, objDecimal);
		assertTrue(equalElementSequence(alge, baseDecimal, objDecimal));
		try {
			alge.putValue(baseBoolean, objDecimal);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		//try {
		//	alge.putValue(baseInteger, objDecimal);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			alge.putValue(baseString, objDecimal);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		
		//--- String
		alge = new Dtalge(1);
		alge.putValue(baseString, objNull);
		assertTrue(equalElementSequence(alge, baseString, objNull));
		alge.putValue(baseString, objString);
		assertTrue(equalElementSequence(alge, baseString, objString));
		try {
			alge.putValue(baseBoolean, objString);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		//try {
		//	alge.putValue(baseInteger, objString);
		//	fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		//} catch (IllegalValueOfDataTypeException ex) {
		//	System.out.println("Valid exception : " + ex.toString());
		//}
		try {
			alge.putValue(baseDecimal, objString);
			fail("Dtalge[" + alge.toString() + "] must be throw IllegalValueOfDataTypeException.");
		} catch (IllegalValueOfDataTypeException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
	}

	/**
	 * {@link dtalge.Dtalge#putValue(dtalge.Dtalge)} のためのテスト・メソッド。
	 */
	public void testPutValueDtalge() {
		Dtalge alge = new Dtalge(1);
		
		//--- null
		try {
			alge.putValue(null);
			fail("Dtalge[" + alge.toString() + "] must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- make data
		ArrayList<Object> ary = new ArrayList<Object>();
		for (int i = 0; i < normalAlges.length; i+=2) {
			ary.add(normalAlges[i]);
			ary.add(normalAlges[i+1]);
		}
		for (int i = 0; i < normalOtherAlges.length; i+=2) {
			ary.add(normalOtherAlges[i]);
			ary.add(normalOtherAlges[i+1]);
		}
		Object[] nullAlges = ary.toArray(new Object[ary.size()]);
		
		//--- new
		alge = new Dtalge(1);
		alge.putValue(new Dtalge(nullAlges));
		assertTrue(equalElementSequence(alge, nullAlges));
		
		//--- marge
		Object[] newAlges = catAlgeObjects(normalAlges, normalOtherAlges);
		alge.putValue(new Dtalge(normalAlges));
		assertTrue(equalElementSequence(alge, newAlges));
	}
	
	/**
	 * {@link dtalge.Dtalge#sum(java.util.Collection)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSumCollectionOfQextendsDtalge() {
		Dtalge empty1 = new Dtalge();
		Dtalge empty2 = new Dtalge();
		Dtalge empty3 = new Dtalge();
		Dtalge sumalge1 = new Dtalge(sumAlgeData1);
		Dtalge sumalge2 = new Dtalge(sumAlgeData2);
		Dtalge sumalge3 = new Dtalge(sumAlgeData3);
		Dtalge sumret123 = new Dtalge(sumRetData123);
		Dtalge sumret321 = new Dtalge(sumRetData321);
		Dtalge sumret213 = new Dtalge(sumRetData213);
		Dtalge sumret231 = new Dtalge(sumRetData231);
		Dtalge ret;
		
		//--- null
		try {
			Dtalge.sum(null);
			fail("Dtalge#sum(null) must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// empty
		ret = Dtalge.sum(new DtAlgeSet());
		assertTrue(ret.isEmpty());
		ret = Dtalge.sum(Arrays.asList(empty1));
		assertTrue(ret.isEmpty());
		ret = Dtalge.sum(Arrays.asList(empty1, empty2, empty3));
		assertTrue(ret.isEmpty());
		
		// 123
		ret = Dtalge.sum(Arrays.asList(sumalge1, sumalge2, sumalge3));
		assertEquals(ret, sumret123);
		assertTrue(equalElementSequence(ret, sumRetData123));
		ret = Dtalge.sum(Arrays.asList(sumalge1, empty1, sumalge2, empty2, sumalge3, empty3));
		assertEquals(ret, sumret123);
		assertTrue(equalElementSequence(ret, sumRetData123));
		
		// 321
		ret = Dtalge.sum(Arrays.asList(sumalge3, sumalge2, sumalge1));
		assertEquals(ret, sumret321);
		assertTrue(equalElementSequence(ret, sumRetData321));
		ret = Dtalge.sum(Arrays.asList(empty1, sumalge3, empty2, sumalge2, empty3, sumalge1));
		assertEquals(ret, sumret321);
		assertTrue(equalElementSequence(ret, sumRetData321));
		
		// 213
		ret = Dtalge.sum(Arrays.asList(sumalge2, sumalge1, sumalge3));
		assertEquals(ret, sumret213);
		assertTrue(equalElementSequence(ret, sumRetData213));
		ret = Dtalge.sum(Arrays.asList(sumalge2, empty1, sumalge1, empty2, sumalge3));
		assertEquals(ret, sumret213);
		assertTrue(equalElementSequence(ret, sumRetData213));
		
		// 231
		ret = Dtalge.sum(Arrays.asList(sumalge2, sumalge3, sumalge1));
		assertEquals(ret, sumret231);
		assertTrue(equalElementSequence(ret, sumRetData231));
		ret = Dtalge.sum(Arrays.asList(empty3, sumalge2, empty1, sumalge3, empty2, sumalge1, empty3));
		assertEquals(ret, sumret231);
		assertTrue(equalElementSequence(ret, sumRetData231));
	}
	
	/**
	 * {@link dtalge.Dtalge#objectEquals(Object, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testObjectEqualsObjectObject() {
		Object[][] equalsCases = {
				{ null, null },
				{ Boolean.TRUE, Boolean.TRUE },
				{ Boolean.FALSE, Boolean.FALSE },
				{ "test1", "test1" },
				{ "10.2340", "10.2340" },
				{ new BigDecimal("10.2340"), new BigDecimal("10.2340") },
				{ new BigDecimal("-45.1920"), new BigDecimal("-45.1920") },
		};
		
		Object[][] notEqualsCases = {
				{ null, Boolean.TRUE },
				{ new BigDecimal("10.2340"), null },
				{ Boolean.TRUE, Boolean.FALSE },
				{ "test1", "test2" },
				{ "10.2340", "10.234" },
				{ "test1", Boolean.FALSE },
				{ new BigDecimal("10.234"), "10.234" },
				{ new BigDecimal("10.234"), new BigDecimal("10.2340") },
				{ new BigDecimal("-45.192"), new BigDecimal("-45.1920") },
		};
		
		// test equals cases
		for (int i = 0; i < equalsCases.length; i++) {
			assertTrue(Dtalge.objectEquals(equalsCases[i][0], equalsCases[i][1]));
		}
		
		// test not equals cases
		for (int i = 0; i < notEqualsCases.length; i++) {
			assertFalse(Dtalge.objectEquals(notEqualsCases[i][0], notEqualsCases[i][1]));
		}
	}
	
	/**
	 * {@link dtalge.Dtalge#valueEquals(Object, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testValueEqualsObjectObject() {
		Object[][] equalsCases = {
				{ null, null },
				{ Boolean.TRUE, Boolean.TRUE },
				{ Boolean.FALSE, Boolean.FALSE },
				{ "test1", "test1" },
				{ "10.2340", "10.2340" },
				{ new BigDecimal("10.2340"), new BigDecimal("10.2340") },
				{ new BigDecimal("-45.1920"), new BigDecimal("-45.1920") },
				{ new BigDecimal("10.2340000"), new BigDecimal("10.234") },
				{ new BigDecimal("-45.192"), new BigDecimal("-45.19200000") },
		};
		
		Object[][] notEqualsCases = {
				{ null, Boolean.TRUE },
				{ new BigDecimal("10.2340"), null },
				{ Boolean.TRUE, Boolean.FALSE },
				{ "test1", "test2" },
				{ "10.2340", "10.234" },
				{ "test1", Boolean.FALSE },
				{ new BigDecimal("10.234"), "10.234" },
				{ new BigDecimal("10.234"), new BigDecimal("10.23400000000000000000000000000000000000000001") },
				{ new BigDecimal("-45.192"), new BigDecimal("-45.19200000000000000000000000000000000000000001") },
		};
		
		// test equals cases
		for (int i = 0; i < equalsCases.length; i++) {
			assertTrue(Dtalge.valueEquals(equalsCases[i][0], equalsCases[i][1]));
		}
		
		// test not equals cases
		for (int i = 0; i < notEqualsCases.length; i++) {
			assertFalse(Dtalge.valueEquals(notEqualsCases[i][0], notEqualsCases[i][1]));
		}
	}
}
