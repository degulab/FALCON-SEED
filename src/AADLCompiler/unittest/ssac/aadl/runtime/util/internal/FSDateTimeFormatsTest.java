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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FSDateTimeFormatsTest.java	2.2.1	2015/07/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.internal;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.TestCase;

/**
 * Test for FSDateTimeFormats class.
 * @version 2.2.1
 * @since 2.2.1
 */
public class FSDateTimeFormatsTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected int calcHashCode(FSDateTimeFormats dtf) {
		int h = Boolean.valueOf(dtf._useDefault).hashCode();
		if (dtf._patterns != null && !dtf._patterns.isEmpty()) {
			for (SimpleDateFormat sdf : dtf._patterns) {
				h = 31 * h + (sdf==null ? 0 : sdf.hashCode());
			}
		} else {
			h = 31 * h + 0;
		}
		return h;
	}
	
	protected String toEnquotedString(String fieldString) {
		if (fieldString == null)
			return null;
		
		if (fieldString.isEmpty())
			return fieldString;
		
		if (fieldString.indexOf(',') < 0 && fieldString.indexOf('\"') < 0) {
			return fieldString;
		}
		
		fieldString = fieldString.replaceAll("\\\"", "\"\"");
		return "\"" + fieldString + "\"";
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class TestPatternData {
		public final String 			inputString;
		public final String				patternString;
		public final SimpleDateFormat	dtformat;
		public final String				validDateTime;
		public final String				invalidDateTime;
		
		public TestPatternData(String strInputString, String strPatternString, String strValidDateTime, String strInvalidDateTime) {
			inputString   = strInputString;
			patternString = strPatternString;
			dtformat      = (strPatternString==null ? null : new SimpleDateFormat(strPatternString));
			validDateTime   = strValidDateTime;
			invalidDateTime = strInvalidDateTime;
		}
	}

	//------------------------------------------------------------
	// Test data
	//------------------------------------------------------------
	
	// invalid default datetimes
	static protected final String[] invalidDefaultValues = {
		null,
		"",
		"20150718 12:34:56.123",
		"2015/07,18 12:34:56.123",
		"2015_07_18 12:34:56.123",
		"2015-07-18 12-34-56.123",
		"2015/07/18 123456.123",
		"2015",
		"  2015 07 18 12 34 56 123",
		"12:34:56.123",
	};
	
	// default datetimes
	static protected final String[][] validDefaultValues = {
		{"2015-07-18 12:34:56.123", "2015/07/18 12:34:56.123"},
		{"2015.07.18 12:34:56.123", "2015/07/18 12:34:56.123"},
		{"2015/07/18 12:34:56.123", "2015/07/18 12:34:56.123"},
		{"  2015-07-18 12:34:56.123", "2015/07/18 12:34:56.123"},
		{"2015.7.18 12:4:56", "2015/07/18 12:04:56.000"},
		{"      2015/7/8    12:34      ", "2015/07/08 12:34:00.000"},
		{"2015/7/8", "2015/07/08 00:00:00.000"},
	};
	
	// custom patterns
	static protected final TestPatternData[] customPatterns = {
		new TestPatternData("yyyy.MM.dd G 'at' HH:mm:ss z", "yyyy.MM.dd G 'at' HH:mm:ss z",
				"2001.07.04 西暦 at 12:08:56 JST", "2001.07.04 AD mat 12:08:56 PDT"),
		new TestPatternData("\"   yyyy.MM.dd G \"\"'at'\"\" HH:mm:ss z   \"", "yyyy.MM.dd G \"'at'\" HH:mm:ss z",
				"2001.07.04 西暦 \"at\" 12:08:56 JST", "2001.07.04 AD at 12:08:56 PDT"),
		new TestPatternData("\"EEE, MMM d, ''yy\"", "EEE, MMM d, ''yy",
				"水, 7 4, '01", "Wed, Jul 4, \"01"),
		new TestPatternData("\"EEE, MMM d, \"\"yy\"", "EEE, MMM d, \"yy",
				"水, 7 4, \"01", "Wed, Jul 4, '01"),
		new TestPatternData("yyyyy.MMMMM.dd GGG hh:mm aaa", "yyyyy.MMMMM.dd GGG hh:mm aaa",
				"02001.7月.04 西暦 12:08 午後", "02001.July.04 12:08 PM"),
		new TestPatternData("\"   yyyyy.MMMMM.dd GGG hh:mm aaa\"", "yyyyy.MMMMM.dd GGG hh:mm aaa",
				"02001.7月.04 西暦 12:08 午後", "02001.July.04 12:08 PM"),
		new TestPatternData("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
				"2001-07-04T12:08:56.235+0900", "2001-07-04 12:08:56.235-0700"),
		new TestPatternData("\"yyyy-MM-dd\"\"'T'\"\"HH:mm:ss.SSSZ      \"", "yyyy-MM-dd\"'T'\"HH:mm:ss.SSSZ",
				"2001-07-04\"T\"12:08:56.235+0900", "2001-07-04T12:08:56.235-0700"),
	};

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#isSingleDefaultPatternString(java.lang.String)}.
	 */
	public void testIsSingleDefaultPatternString() {
		assertTrue(FSDateTimeFormats.isSingleDefaultPatternString("default"));
		assertTrue(FSDateTimeFormats.isSingleDefaultPatternString("DEFAULT"));
		assertTrue(FSDateTimeFormats.isSingleDefaultPatternString("dEfAuLt"));
		assertTrue(FSDateTimeFormats.isSingleDefaultPatternString("DeFaUlT"));
		assertTrue(FSDateTimeFormats.isSingleDefaultPatternString("  default  "));
		assertTrue(FSDateTimeFormats.isSingleDefaultPatternString("  DEFAULT  "));

		assertFalse(FSDateTimeFormats.isSingleDefaultPatternString(null));
		assertFalse(FSDateTimeFormats.isSingleDefaultPatternString(""));
		assertFalse(FSDateTimeFormats.isSingleDefaultPatternString("def ault"));
		assertFalse(FSDateTimeFormats.isSingleDefaultPatternString("hoge"));
		assertFalse(FSDateTimeFormats.isSingleDefaultPatternString("   "));
		assertFalse(FSDateTimeFormats.isSingleDefaultPatternString("yyyy/mm/dd hh:MM:ss.sss"));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#isValidSinglePatternString(java.lang.String)}.
	 */
	public void testIsValidSinglePatternString() {
		assertTrue(FSDateTimeFormats.isValidSinglePatternString("default"));
		assertTrue(FSDateTimeFormats.isValidSinglePatternString("DEFAULT"));
		assertTrue(FSDateTimeFormats.isValidSinglePatternString("dEfAuLt"));
		assertTrue(FSDateTimeFormats.isValidSinglePatternString("DeFaUlT"));
		assertTrue(FSDateTimeFormats.isValidSinglePatternString("yyyy-MM-dd HH:mm:ss.SSS"));
		assertTrue(FSDateTimeFormats.isValidSinglePatternString("  yyyy  "));
		assertTrue(FSDateTimeFormats.isValidSinglePatternString("HH''mm\"ss"));
		
		assertFalse(FSDateTimeFormats.isValidSinglePatternString(null));
		assertFalse(FSDateTimeFormats.isValidSinglePatternString(""));
		assertFalse(FSDateTimeFormats.isValidSinglePatternString("abcdefG"));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#FSDateTimeFormats()}.
	 */
	public void testFSDateTimeFormats() {
		FSDateTimeFormats dtf = new FSDateTimeFormats();
		
		assertTrue(dtf._useDefault);
		assertNull(dtf._patterns);
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#FSDateTimeFormats(java.lang.String)}.
	 */
	public void testFSDateTimeFormatsString() {
		FSDateTimeFormats dtf;
		
		// check null
		dtf = new FSDateTimeFormats(null);
		assertTrue(dtf._useDefault);
		assertNull(dtf._patterns);
		
		// check empty
		dtf = new FSDateTimeFormats("");
		assertTrue(dtf._useDefault);
		assertNull(dtf._patterns);
		
		// check only space
		dtf = new FSDateTimeFormats("              ");
		assertTrue(dtf._useDefault);
		assertNull(dtf._patterns);
		
		// check multi fields, but space only
		dtf = new FSDateTimeFormats("   ,     ,    ,  ");
		assertTrue(dtf._useDefault);
		assertNull(dtf._patterns);
		
		// check default
		dtf = new FSDateTimeFormats(" deFAUlt  ");
		assertTrue(dtf._useDefault);
		assertNull(dtf._patterns);
		
		// check default multi fields
		dtf = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		assertTrue(dtf._useDefault);
		assertNull(dtf._patterns);
		
		// check custom only
		StringBuffer buffer1 = new StringBuffer();
		StringBuffer buffer2 = new StringBuffer();
		for (int index = 0; index < customPatterns.length; ++index) {
			String strHeader = "index[" + index + "]:";
			TestPatternData pdata = customPatterns[index];
			
			dtf = new FSDateTimeFormats(pdata.inputString);
			assertFalse(strHeader, dtf._useDefault);
			assertNotNull(strHeader, dtf._patterns);
			assertEquals(strHeader, 1, dtf._patterns.size());
			String strPattern = dtf.toPatternsString(false);
			assertEquals(strHeader, pdata.patternString, strPattern);
			
			if (index > 0) {
				buffer1.append(',');
				buffer2.append(',');
			}
			buffer1.append(pdata.inputString);
			buffer2.append(pdata.patternString);
		}
		String strAllInputString = buffer1.toString();
		String strAllPatternString = buffer2.toString();
		dtf = new FSDateTimeFormats(strAllInputString);
		assertFalse(dtf._useDefault);
		assertNotNull(dtf._patterns);
		assertEquals(customPatterns.length, dtf._patterns.size());
		assertEquals(strAllPatternString, dtf.toPatternsString(false));
		
		// with default
		strAllInputString = strAllInputString + ",DeFaULt," + strAllInputString;
		strAllPatternString = strAllPatternString + "," + FSDateTimeFormats.DefaultSpecifier + "," + strAllPatternString;
		dtf = new FSDateTimeFormats(strAllInputString);
		assertTrue(dtf._useDefault);
		assertNotNull(dtf._patterns);
		assertEquals(customPatterns.length*2+1, dtf._patterns.size());
		assertEquals(strAllPatternString, dtf.toPatternsString(false));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#isOnlyDefaultFormat()}.
	 */
	public void testIsOnlyDefaultFormat() {
		FSDateTimeFormats dtf;
		
		// check null
		dtf = new FSDateTimeFormats(null);
		assertTrue(dtf.isOnlyDefaultFormat());
		
		// check empty
		dtf = new FSDateTimeFormats("");
		assertTrue(dtf.isOnlyDefaultFormat());
		
		// check only space
		dtf = new FSDateTimeFormats("              ");
		assertTrue(dtf.isOnlyDefaultFormat());
		
		// check multi fields, but space only
		dtf = new FSDateTimeFormats("   ,     ,    ,  ");
		assertTrue(dtf.isOnlyDefaultFormat());
		
		// check default
		dtf = new FSDateTimeFormats(" deFAUlt  ");
		assertTrue(dtf.isOnlyDefaultFormat());
		
		// check default multi fields
		dtf = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		assertTrue(dtf.isOnlyDefaultFormat());
		
		// check no default fields
		String strInputString = customPatterns[0].inputString + "," + customPatterns[1].inputString;
		String strPatternString = customPatterns[0].patternString + "," + customPatterns[1].patternString;
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertFalse(dtf.isOnlyDefaultFormat());
		
		// check mixed fields
		strInputString = customPatterns[0].inputString + ",DeFauLT," + customPatterns[1].inputString;
		strPatternString = customPatterns[0].patternString + "," + FSDateTimeFormats.DefaultSpecifier + "," + customPatterns[1].patternString;
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertFalse(dtf.isOnlyDefaultFormat());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#isUseDefaultFormats()}.
	 */
	public void testIsUseDefaultFormats() {
		FSDateTimeFormats dtf;
		
		// check null
		dtf = new FSDateTimeFormats(null);
		assertTrue(dtf.isUseDefaultFormats());
		
		// check empty
		dtf = new FSDateTimeFormats("");
		assertTrue(dtf.isUseDefaultFormats());
		
		// check only space
		dtf = new FSDateTimeFormats("              ");
		assertTrue(dtf.isUseDefaultFormats());
		
		// check multi fields, but space only
		dtf = new FSDateTimeFormats("   ,     ,    ,  ");
		assertTrue(dtf.isUseDefaultFormats());
		
		// check default
		dtf = new FSDateTimeFormats(" deFAUlt  ");
		assertTrue(dtf.isUseDefaultFormats());
		
		// check default multi fields
		dtf = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		assertTrue(dtf.isUseDefaultFormats());
		
		// check no default fields
		String strInputString = customPatterns[0].inputString + "," + customPatterns[1].inputString;
		String strPatternString = customPatterns[0].patternString + "," + customPatterns[1].patternString;
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertFalse(dtf.isUseDefaultFormats());
		
		// check mixed fields
		strInputString = customPatterns[0].inputString + ",DeFauLT," + customPatterns[1].inputString;
		strPatternString = customPatterns[0].patternString + "," + FSDateTimeFormats.DefaultSpecifier + "," + customPatterns[1].patternString;
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertTrue(dtf.isUseDefaultFormats());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#hashCode()}.
	 */
	public void testHashCode() {
		FSDateTimeFormats dtf;
		
		// check default
		dtf = new FSDateTimeFormats();
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check null
		dtf = new FSDateTimeFormats(null);
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check empty
		dtf = new FSDateTimeFormats("");
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check only space
		dtf = new FSDateTimeFormats("              ");
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check multi fields, but space only
		dtf = new FSDateTimeFormats("   ,     ,    ,  ");
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check default
		dtf = new FSDateTimeFormats(" deFAUlt  ");
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check default multi fields
		dtf = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check no default fields
		String strInputString = customPatterns[0].inputString + "," + customPatterns[1].inputString;
		String strPatternString = customPatterns[0].patternString + "," + customPatterns[1].patternString;
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertEquals(calcHashCode(dtf), dtf.hashCode());
		
		// check mixed fields
		strInputString = customPatterns[0].inputString + ",DeFauLT," + customPatterns[1].inputString;
		strPatternString = customPatterns[0].patternString + "," + FSDateTimeFormats.DefaultSpecifier + "," + customPatterns[1].patternString;
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertEquals(calcHashCode(dtf), dtf.hashCode());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() {
		// check default
		FSDateTimeFormats dtf11 = new FSDateTimeFormats();
		FSDateTimeFormats dtf21 = new FSDateTimeFormats();
		
		// check default multi fields
		FSDateTimeFormats dtf12 = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		FSDateTimeFormats dtf22 = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		
		// check no default fields
		String strInputString = customPatterns[0].inputString + "," + customPatterns[1].inputString;
		String strPatternString = customPatterns[0].patternString + "," + customPatterns[1].patternString;
		FSDateTimeFormats dtf13 = new FSDateTimeFormats(strInputString);
		FSDateTimeFormats dtf23 = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf13.toPatternsString(false));
		
		// check mixed fields
		strInputString = customPatterns[0].inputString + ",DeFauLT," + customPatterns[1].inputString;
		strPatternString = customPatterns[0].patternString + "," + FSDateTimeFormats.DefaultSpecifier + "," + customPatterns[1].patternString;
		FSDateTimeFormats dtf14 = new FSDateTimeFormats(strInputString);
		FSDateTimeFormats dtf24 = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf14.toPatternsString(false));
		
		// chekc equals
		assertEquals(dtf11, dtf21);
		assertEquals(dtf12, dtf22);
		assertEquals(dtf13, dtf23);
		assertEquals(dtf14, dtf24);
		
		assertEquals(dtf11, dtf12);
		
		assertFalse(dtf11.equals(dtf13));
		assertFalse(dtf13.equals(dtf14));
		assertFalse(dtf14.equals(dtf11));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#toPatternsString(boolean)}.
	 */
	public void testToPatternsString() {
		FSDateTimeFormats dtf;
		
		// check default
		dtf = new FSDateTimeFormats();
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(false));
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(true));
		
		// check null
		dtf = new FSDateTimeFormats(null);
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(false));
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(true));
		
		// check default multi fields
		dtf = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(false));
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(true));
		
		// check no default fields
		String strInputString = customPatterns[0].inputString + "," + customPatterns[1].inputString;
		String strPatternString = customPatterns[0].patternString + "," + customPatterns[1].patternString;
		String strEnquotString  = toEnquotedString(customPatterns[0].patternString) + "," + toEnquotedString(customPatterns[1].patternString);
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertEquals(strEnquotString, dtf.toPatternsString(true));
		
		// check mixed fields
		strInputString = customPatterns[0].inputString + ",DeFauLT," + customPatterns[1].inputString;
		strPatternString = customPatterns[0].patternString + "," + FSDateTimeFormats.DefaultSpecifier + "," + customPatterns[1].patternString;
		strEnquotString = toEnquotedString(customPatterns[0].patternString)
				+ "," + toEnquotedString(FSDateTimeFormats.DefaultSpecifier)
				+ "," + toEnquotedString(customPatterns[1].patternString);
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertEquals(strEnquotString, dtf.toPatternsString(true));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#toString()}.
	 */
	public void testToString() {
		FSDateTimeFormats dtf;
		
		// check default
		dtf = new FSDateTimeFormats();
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(false));
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toString());
		
		// check null
		dtf = new FSDateTimeFormats(null);
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(false));
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toString());
		
		// check default multi fields
		dtf = new FSDateTimeFormats(" deFAUlt  ,default,\"default\",  DEFAULT");
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toPatternsString(false));
		assertEquals(FSDateTimeFormats.DefaultSpecifier, dtf.toString());
		
		// check no default fields
		String strInputString = customPatterns[0].inputString + "," + customPatterns[1].inputString;
		String strPatternString = customPatterns[0].patternString + "," + customPatterns[1].patternString;
		String strEnquotString  = toEnquotedString(customPatterns[0].patternString) + "," + toEnquotedString(customPatterns[1].patternString);
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertEquals(strEnquotString, dtf.toString());
		
		// check mixed fields
		strInputString = customPatterns[0].inputString + ",DeFauLT," + customPatterns[1].inputString;
		strPatternString = customPatterns[0].patternString + "," + FSDateTimeFormats.DefaultSpecifier + "," + customPatterns[1].patternString;
		strEnquotString = toEnquotedString(customPatterns[0].patternString)
				+ "," + toEnquotedString(FSDateTimeFormats.DefaultSpecifier)
				+ "," + toEnquotedString(customPatterns[1].patternString);
		dtf = new FSDateTimeFormats(strInputString);
		assertEquals(strPatternString, dtf.toPatternsString(false));
		assertEquals(strEnquotString, dtf.toString());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#parse(java.lang.String)}.
	 * Test method for {@link ssac.aadl.runtime.util.internal.FSDateTimeFormats#format(java.util.Calendar)}.
	 */
	public void testFormatAndParse() {
		FSDateTimeFormats dtf;
		
		// default
		dtf = new FSDateTimeFormats();
		//--- invalid default format
		for (int index = 0; index < invalidDefaultValues.length; ++index) {
			String strHeader = "index[" + index + "]:";
			Calendar cal = dtf.parse(invalidDefaultValues[index]);
			assertNull(strHeader, cal);
		}
		//--- valid default format
		for (int index = 0; index < validDefaultValues.length; ++index) {
			String strHeader = "index[" + index + "]:";
			String strInput  = validDefaultValues[index][0];
			String strOutput = validDefaultValues[index][1];
			Calendar cal = dtf.parse(strInput);
			assertNotNull(strHeader, cal);
			String strResult = dtf.format(cal);
			assertEquals(strHeader, strOutput, strResult);
		}
		
		// custom patterns
		StringBuffer buffer1 = new StringBuffer();
		for (int index = 0; index < customPatterns.length; ++index) {
			String strHeader = "index[" + index + "]:";
			TestPatternData pdata = customPatterns[index];
			String strInvalid = pdata.invalidDateTime;
			
			dtf = new FSDateTimeFormats(pdata.inputString);
			//--- invalid
			Calendar cal = dtf.parse(strInvalid);
			assertNull(strHeader, cal);
			//--- valid
			String strValid = pdata.validDateTime;
			cal = dtf.parse(strValid);
			assertNotNull(strHeader, cal);
			String strFormatted = dtf.format(cal);
			assertEquals(strHeader, strValid, strFormatted);
			//--- current
			Calendar calCurrent = Calendar.getInstance();
			String strCurrent = dtf.format(calCurrent);
			assertNotNull(strHeader, strCurrent);
			cal = dtf.parse(strCurrent);
			assertNotNull(strHeader, cal);
			String strResult = dtf.format(cal);
			assertEquals(strHeader, strCurrent, strResult);
			
			if (index > 0) {
				buffer1.append(',');
			}
			buffer1.append(pdata.inputString);
		}
		
		// check all patterns
		String strValid  = "2001-07-04\"T\"12:08:56.235+0900";
		String strAnswer = "2001.07.04 西暦 at 12:08:56 JST";
		String strAllInputString = buffer1.toString();
		dtf = new FSDateTimeFormats(strAllInputString);
		Calendar cal = dtf.parse(strValid);
		assertNotNull(cal);
		String strResult = dtf.format(cal);
		assertEquals(strAnswer, strResult);
		
		// check first default
		strAnswer = "2001/07/04 12:08:56.235";
		dtf = new FSDateTimeFormats("default," + strAllInputString);
		assertTrue(dtf.isUseDefaultFormats());
		assertFalse(dtf.isOnlyDefaultFormat());
		cal = dtf.parse(strValid);
		assertNotNull(cal);
		strResult = dtf.format(cal);
		assertEquals(strAnswer, strResult);
	}
}
