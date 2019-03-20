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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLKeyword.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLKeyword.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLKeyword.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLKeyword.java	1.40	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,) - bugfix
 * @(#)AADLKeyword.java	1.30	2008/08/047
 *     - modified by Y.Ishizuka(PieCake.inc,) - bugfix
 * @(#)AADLKeyword.java	1.10	2008/05/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLKeyword.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import java.util.Arrays;
import java.util.TreeSet;

/**
 * AADL 予約語
 *
 * 
 * @version 2.1.0	2014/05/29
 */
public final class AADLKeyword
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final TreeSet<String> kwset;
	
	static public final AADLKeyword instance;
	
	static {
		instance = new AADLKeyword(
						// AADL Keywords
						"AADL",	"aadl", "aadlRun",
						"program", "function",
						"Object", "Boolean", "Integer", "Decimal", "String",
						"List", "BooleanList", "IntegerList", "DecimalList", "StringList",
						"exalge2",
						"ExBase", "ExBasePattern", "Exalge",
						"ExBaseSet", "ExBasePatternSet", "ExAlgeSet",
						"TransTable", "TransMatrix", "ExTransfer",
						"dtalge",
						"DtBase", "DtBasePattern", "Dtalge",
						"DtBaseSet", "DtBasePatternSet", "DtAlgeSet",
						"DtStringThesaurus",
						"txtFile", "csvFile", "xmlFile", "true", "false",
						"const", "var", "return", "break", "package",
						"if", "else",
						"null", "void",
						"TextFileReader", "TextFileWriter", "CsvFileReader", "CsvFileWriter",
						"Range", "DecimalRange",
						"Byte", "Char", "Short", "Integer", "Long", "Float", "Double",
						"ByteList", "CharList", "ShortList", "IntegerList", "LongList", "FloatList", "DoubleList",
						"MqttCsvParameter",
						// Java keywords
						"abstract", "boolean", "break", "byte",
						"cast", "catch", "char", "class",
						"const", "continue", "default", "do",
						"double", "else", "extends", "final",
						"false", "finally", "float", "for",
						"goto", "if", "implements", "import",
						"instanceof", "int", "interface", "long",
						"native", "new", "package", "private",
						"protected", "public", "return", "short",
						"static", "super", "switch", "synchronized",
						"this", "throw", "throws", "transient",
						"true", "try", "void", "volatile",
						"while"
				   );
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLKeyword(String...words) {
		this.kwset = new TreeSet<String>(Arrays.asList(words));
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public boolean contains(String word) {
		return this.kwset.contains(word);
	}
}
