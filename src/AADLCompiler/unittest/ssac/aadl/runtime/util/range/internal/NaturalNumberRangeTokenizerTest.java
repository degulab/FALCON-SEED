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
 * @(#)NaturalNumberRangeTokenizerTest.java	1.70	2011/05/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer} クラスのテスト。
 * 
 * @version 1.70	2011/05/12
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class NaturalNumberRangeTokenizerTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String srcString1 = "1-3,5,7,-100,10-,1ab1";
	static private final String srcString2 = " 1 - 3 , 5 , 7 , - 100 435. 10 - , 1 a b 1 ";
	
	static private final TokenInfo[] aryTokenInfo1 = {
		toTokenInfo(NaturalNumberRangeTokenizer.INVALID_POSITION, NaturalNumberRangeTokenizer.INVALID, ""),
		toTokenInfo(0, NaturalNumberRangeTokenizer.NUMBER, "1"),
		toTokenInfo(1, NaturalNumberRangeTokenizer.SERIAL, "-"),
		toTokenInfo(2, NaturalNumberRangeTokenizer.NUMBER, "3"),
		toTokenInfo(3, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(4, NaturalNumberRangeTokenizer.NUMBER, "5"),
		toTokenInfo(5, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(6, NaturalNumberRangeTokenizer.NUMBER, "7"),
		toTokenInfo(7, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(8, NaturalNumberRangeTokenizer.SERIAL, "-"),
		toTokenInfo(9, NaturalNumberRangeTokenizer.NUMBER, "100"),
		toTokenInfo(12, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(13, NaturalNumberRangeTokenizer.NUMBER, "10"),
		toTokenInfo(15, NaturalNumberRangeTokenizer.SERIAL, "-"),
		toTokenInfo(16, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(17, NaturalNumberRangeTokenizer.INVALID, "1ab1"),
		toTokenInfo(NaturalNumberRangeTokenizer.INVALID_POSITION, NaturalNumberRangeTokenizer.EOT, null),
	};
	
	static private final TokenInfo[] aryTokenInfo2 = {
		toTokenInfo(NaturalNumberRangeTokenizer.INVALID_POSITION, NaturalNumberRangeTokenizer.INVALID, ""),
		toTokenInfo(1, NaturalNumberRangeTokenizer.NUMBER, "1"),
		toTokenInfo(3, NaturalNumberRangeTokenizer.SERIAL, "-"),
		toTokenInfo(5, NaturalNumberRangeTokenizer.NUMBER, "3"),
		toTokenInfo(7, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(9, NaturalNumberRangeTokenizer.NUMBER, "5"),
		toTokenInfo(11, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(13, NaturalNumberRangeTokenizer.NUMBER, "7"),
		toTokenInfo(15, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(17, NaturalNumberRangeTokenizer.SERIAL, "-"),
		toTokenInfo(19, NaturalNumberRangeTokenizer.INVALID, "100 435. 10 - "),
		toTokenInfo(33, NaturalNumberRangeTokenizer.DELIMITER, ","),
		toTokenInfo(35, NaturalNumberRangeTokenizer.INVALID, "1 a b 1 "),
		toTokenInfo(NaturalNumberRangeTokenizer.INVALID_POSITION, NaturalNumberRangeTokenizer.EOT, null),
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private TokenInfo toTokenInfo(int pos, int type, String token) {
		return new TokenInfo(pos, type, token);
	}
	
	static private class TokenInfo {
		public TokenInfo(int pos, int type, String token) {
			this.lastPos = pos;
			this.lastType = type;
			this.lastToken = token;
		}
		
		/** 最後のトークン位置 **/
		public final int	lastPos;
		/** 最後のトークン種別 **/
		public final int lastType;
		/** 最後のトークン **/
		public final String lastToken;
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer#NaturalNumberRangeTokenizer(java.lang.String)}.
	 */
	public void testNaturalNumberRangeTokenizer() {
		NaturalNumberRangeTokenizer tokenizer;
		
		// null
		tokenizer = new NaturalNumberRangeTokenizer(null);
		assertEquals(NaturalNumberRangeTokenizer.INVALID_POSITION, tokenizer.getLastTokenPosition());
		assertEquals(NaturalNumberRangeTokenizer.EOT, tokenizer.getLastTokenType());
		assertNull(tokenizer.getLastToken());
		
		// empty string
		tokenizer = new NaturalNumberRangeTokenizer("");
		assertEquals(NaturalNumberRangeTokenizer.INVALID_POSITION, tokenizer.getLastTokenPosition());
		assertEquals(NaturalNumberRangeTokenizer.EOT, tokenizer.getLastTokenType());
		assertNull(tokenizer.getLastToken());
		
		// number
		tokenizer = new NaturalNumberRangeTokenizer("123");
		assertEquals(NaturalNumberRangeTokenizer.INVALID_POSITION, tokenizer.getLastTokenPosition());
		assertEquals(NaturalNumberRangeTokenizer.INVALID, tokenizer.getLastTokenType());
		assertEquals("", tokenizer.getLastToken());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer#getLastTokenType()}.
	 */
	public void testGetLastTokenType() {
		NaturalNumberRangeTokenizer tokenizer;
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString1);
		for (int i = 0; i < aryTokenInfo1.length; i++) {
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo1[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo1[i].lastType, tokenizer.getLastTokenType());
			tokenizer.nextToken();
		}
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString2);
		for (int i = 0; i < aryTokenInfo2.length; i++) {
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo2[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo2[i].lastType, tokenizer.getLastTokenType());
			tokenizer.nextToken();
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer#getLastToken()}.
	 */
	public void testGetLastToken() {
		NaturalNumberRangeTokenizer tokenizer;
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString1);
		for (int i = 0; i < aryTokenInfo1.length; i++) {
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo1[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo1[i].lastToken, tokenizer.getLastToken());
			tokenizer.nextToken();
		}
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString2);
		for (int i = 0; i < aryTokenInfo2.length; i++) {
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo2[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo2[i].lastToken, tokenizer.getLastToken());
			tokenizer.nextToken();
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer#getLastTokenPosition()}.
	 */
	public void testGetLastTokenPosition() {
		NaturalNumberRangeTokenizer tokenizer;
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString1);
		for (int i = 0; i < aryTokenInfo1.length; i++) {
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo1[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo1[i].lastPos, tokenizer.getLastTokenPosition());
			tokenizer.nextToken();
		}
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString2);
		for (int i = 0; i < aryTokenInfo2.length; i++) {
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo2[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo2[i].lastPos, tokenizer.getLastTokenPosition());
			tokenizer.nextToken();
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer#nextToken()}.
	 */
	public void testNextToken() {
		NaturalNumberRangeTokenizer tokenizer;
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString1);
		for (int i = 1; i < aryTokenInfo1.length; i++) {
			int tokenType = tokenizer.nextToken();
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo1[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo1[i].lastType, tokenType);
			assertEquals(msg, aryTokenInfo1[i].lastType, tokenizer.getLastTokenType());
		}
		
		tokenizer = new NaturalNumberRangeTokenizer(srcString2);
		for (int i = 1; i < aryTokenInfo2.length; i++) {
			int tokenType = tokenizer.nextToken();
			String msg = String.format("Test params : [%d] ta[%s] tr[%s]", i, String.valueOf(aryTokenInfo2[i].lastToken), String.valueOf(tokenizer.getLastToken()));
			assertEquals(msg, aryTokenInfo2[i].lastType, tokenType);
			assertEquals(msg, aryTokenInfo2[i].lastType, tokenizer.getLastTokenType());
		}
	}
}
