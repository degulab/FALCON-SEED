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
 * @(#)AADLAlgeIOFunctionsTest.java	1.70	2011/05/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

import dtalge.DtAlgeSet;
import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.DtStringThesaurus;
import dtalge.Dtalge;

import exalge2.ExAlgeSet;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.ExTransfer;
import exalge2.Exalge;
import exalge2.TransDivideRatios;
import exalge2.TransMatrix;
import exalge2.TransTable;
import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.AADLFunctions} クラスのテスト。
 * 
 * @version 1.70	2011/05/19
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class AADLAlgeIOFunctionsTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String basePathCsv = "testdata/unittest/runtime/csv";
	static private final String basePathXml = "testdata/unittest/runtime/xml";
	
	static private final ExAlgeSet dataExAlgeSet = new ExAlgeSet(Arrays.asList(
				new Exalge("りんご-NO_HAT-円-#-#", new BigDecimal(100)),
				new Exalge("みかん-NO_HAT-円-#-#", new BigDecimal(200)),
				new Exalge("ばなな-NO_HAT-円-#-#", new BigDecimal(300)),
				new Exalge("現金-HAT-円-#-#", new BigDecimal(600))
			));
	
	static private final Exalge dataExalge = dataExAlgeSet.sum();
	
	static private final ExBaseSet dataExBaseSet = dataExAlgeSet.getBases();
	
	static private final ExBasePatternSet dataExBasePatternSet = new ExBasePatternSet(Arrays.asList(
				new ExBasePattern("りんご-*-*-#-#"),
				new ExBasePattern("みかん-*-円-*-#"),
				new ExBasePattern("ばなな-*-円-#-*"),
				new ExBasePattern("現金-*-円-#-#")
			));
	
	static private final TransTable dataTransTable = new TransTable();
	
	static private final TransMatrix dataTransMatrix = new TransMatrix(false);
	
	static private final ExTransfer dataExTransfer = new ExTransfer();
	
	static private final DtAlgeSet dataDtAlgeSet = new DtAlgeSet(Arrays.asList(
				new Dtalge(new DtBase("真偽値", "boolean"), Boolean.TRUE),
				new Dtalge(new DtBase("実数値", "decimal"), new BigDecimal(111)),
				new Dtalge(new DtBase("文字列", "string"), "文字列データ")
			));

//	static private final DtAlgeSet dataTableDtAlgeSet = new DtAlgeSet(Arrays.asList(
//				new Dtalge(new DtBase("真偽値", "boolean"), Boolean.TRUE)
//				.put(new DtBase("実数値", "decimal"), null)
//				.put(new DtBase("文字列", "string"), null),
//				new Dtalge(new DtBase("真偽値", "boolean"), null)
//				.put(new DtBase("実数値", "decimal"), new BigDecimal(111))
//				.put(new DtBase("文字列", "string"), null),
//				new Dtalge(new DtBase("真偽値", "boolean"), null)
//				.put(new DtBase("実数値", "decimal"), null)
//				.put(new DtBase("文字列", "string"), "文字列データ")
//			));
	
	static private final Dtalge dataDtalge = dataDtAlgeSet.sum();
	
	static private final DtBaseSet dataDtBaseSet = dataDtAlgeSet.getBases();
	
	static private final DtBasePatternSet dataDtBasePatternSet = new DtBasePatternSet(Arrays.asList(
				new DtBasePattern("真偽値", "boolean"),
				new DtBasePattern("実*値", "decimal"),
				new DtBasePattern("文字列", "*")
			));
	
	static private final DtStringThesaurus dataDtStringThesaurus = new DtStringThesaurus();

	static {
		dataTransTable.put(new ExBasePattern("りんご-*-*-#-#"), new ExBasePattern("果物-*-*-*-*"));
		dataTransTable.put(new ExBasePattern("みかん-*-円-*-#"), new ExBasePattern("果物-*-*-*-*"));
		dataTransTable.put(new ExBasePattern("ばなな-*-円-#-*"), new ExBasePattern("果物-*-*-*-*"));

		TransDivideRatios tdr = new TransDivideRatios();
		tdr.put(new ExBasePattern("りんご"), BigDecimal.ONE);
		tdr.put(new ExBasePattern("みかん"), BigDecimal.ONE);
		tdr.put(new ExBasePattern("ばなな"), BigDecimal.ONE);
		dataTransMatrix.put(new ExBasePattern("果物"), tdr);
		dataTransMatrix.put(new ExBasePattern("くだもの"), tdr);
		dataTransMatrix.updateTotalRatios();
		
		dataExTransfer.putAggregate(new ExBasePattern("りんご"), new ExBasePattern("くだもの"));
		dataExTransfer.putAggregate(new ExBasePattern("みかん"), new ExBasePattern("くだもの"));
		dataExTransfer.putAggregate(new ExBasePattern("ばなな"), new ExBasePattern("くだもの"));
		dataExTransfer.putMultiply(new ExBasePattern("現金-*-円"), new ExBasePattern("*-*-$"), new BigDecimal("0.012345679012345679012345679012346"));
		
		dataDtStringThesaurus.put("果物", "くだもの");
		dataDtStringThesaurus.put("くだもの", "りんご");
		dataDtStringThesaurus.put("くだもの", "みかん");
		dataDtStringThesaurus.put("くだもの", "ばなな");
	}
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String toCsvFilepath(String key) {
		return toCsvFilepath(key, "def");
	}
	
	static private final String toCsvFilepath(String key, String charsetName) {
		return String.format("%s/writeTest%s_%s.csv", basePathCsv, key, charsetName);
	}
	
	static private final String toXmlFilepath(String key) {
		return String.format("%s/writeTest%s.xml", basePathXml, key);
	}
	
	static private final String fromCsvFilepath(String key) {
		return fromCsvFilepath(key, "def");
	}
	
	static private final String fromCsvFilepath(String key, String charsetName) {
		return String.format("%s/readTest%s_%s.csv", basePathCsv, key, charsetName);
	}
	
	static private final String fromXmlFilepath(String key) {
		return String.format("%s/readTest%s.xml", basePathXml, key);
	}

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#getDefaultTextEncoding()} のためのテスト・メソッド。
	 */
	public void testGetDefaultTextEncoding() {
		System.clearProperty(AADLFunctions.PROPKEY_AADL_TXT_ENCODING);
		assertNull(AADLFunctions.getDefaultTextEncoding());
		
		System.setProperty(AADLFunctions.PROPKEY_AADL_TXT_ENCODING, "");
		assertNull(AADLFunctions.getDefaultTextEncoding());
		
		System.setProperty(AADLFunctions.PROPKEY_AADL_TXT_ENCODING, "EUC");
		assertEquals("EUC", AADLFunctions.getDefaultTextEncoding());
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#getDefaultCsvEncoding()} のためのテスト・メソッド。
	 */
	public void testGetDefaultCsvEncoding() {
		System.clearProperty(AADLFunctions.PROPKEY_AADL_CSV_ENCODING);
		assertNull(AADLFunctions.getDefaultCsvEncoding());
		
		System.setProperty(AADLFunctions.PROPKEY_AADL_CSV_ENCODING, "");
		assertNull(AADLFunctions.getDefaultCsvEncoding());
		
		System.setProperty(AADLFunctions.PROPKEY_AADL_CSV_ENCODING, "SJIS");
		assertEquals("SJIS", AADLFunctions.getDefaultCsvEncoding());
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#setDefaultTextEncoding(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testSetDefaultTextEncoding() {
		AADLFunctions.setDefaultTextEncoding(null);
		assertNull(System.getProperty(AADLFunctions.PROPKEY_AADL_TXT_ENCODING));
		
		AADLFunctions.setDefaultTextEncoding("");
		assertNull(System.getProperty(AADLFunctions.PROPKEY_AADL_TXT_ENCODING));
		
		AADLFunctions.setDefaultTextEncoding("UTF-8");
		assertEquals("UTF-8", System.getProperty(AADLFunctions.PROPKEY_AADL_TXT_ENCODING));
		
		try {
			AADLFunctions.setDefaultTextEncoding("hoge");
			fail("Must be throw AADLRuntimeException.");
		}
		catch (AADLRuntimeException ex) {}
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#setDefaultCsvEncoding(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testSetDefaultCsvEncoding() {
		AADLFunctions.setDefaultCsvEncoding(null);
		assertNull(System.getProperty(AADLFunctions.PROPKEY_AADL_CSV_ENCODING));
		
		AADLFunctions.setDefaultCsvEncoding("");
		assertNull(System.getProperty(AADLFunctions.PROPKEY_AADL_CSV_ENCODING));
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		assertEquals("UTF-8", System.getProperty(AADLFunctions.PROPKEY_AADL_CSV_ENCODING));
		
		try {
			AADLFunctions.setDefaultCsvEncoding("hoge");
			fail("Must be throw AADLRuntimeException.");
		}
		catch (AADLRuntimeException ex) {}
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExalgeFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExalgeFromCsvFileString() {
		String filename = fromCsvFilepath("Exalge");
		Exalge alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataExalge.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExalgeFromCsvFile(filename);
		assertEquals(dataExalge, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExalge.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExalgeFromCsvFile(filename);
		assertEquals(dataExalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExalgeFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExalgeFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("Exalge", encoding);
		Exalge alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExalge.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExalgeFromCsvFile(filename, encoding);
		assertEquals(dataExalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExalgeFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExalgeFromXmlFile() {
		String filename = fromXmlFilepath("Exalge");
		Exalge alge;
		
		try {
			dataExalge.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExalgeFromXmlFile(filename);
		assertEquals(dataExalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExAlgeSetFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExAlgeSetFromCsvFileString() {
		String filename = fromCsvFilepath("ExAlgeSet");
		ExAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataExAlgeSet.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExAlgeSetFromCsvFile(filename);
		assertEquals(dataExAlgeSet, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExAlgeSet.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExAlgeSetFromCsvFile(filename);
		assertEquals(dataExAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExAlgeSetFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExAlgeSetFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("ExAlgeSet", encoding);
		ExAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExAlgeSet.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExAlgeSetFromCsvFile(filename, encoding);
		assertEquals(dataExAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExAlgeSetFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExAlgeSetFromXmlFile() {
		String filename = fromXmlFilepath("ExAlgeSet");
		ExAlgeSet alge;
		
		try {
			dataExAlgeSet.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExAlgeSetFromXmlFile(filename);
		assertEquals(dataExAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExBaseSetFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExBaseSetFromCsvFileString() {
		String filename = fromCsvFilepath("ExBaseSet");
		ExBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataExBaseSet.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBaseSetFromCsvFile(filename);
		assertEquals(dataExBaseSet, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExBaseSet.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBaseSetFromCsvFile(filename);
		assertEquals(dataExBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExBaseSetFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExBaseSetFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("ExBaseSet", encoding);
		ExBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExBaseSet.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBaseSetFromCsvFile(filename, encoding);
		assertEquals(dataExBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExBaseSetFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExBaseSetFromXmlFile() {
		String filename = fromXmlFilepath("ExBaseSet");
		ExBaseSet alge;
		
		try {
			dataExBaseSet.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBaseSetFromXmlFile(filename);
		assertEquals(dataExBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExBasePatternSetFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExBasePatternSetFromCsvFileString() {
		String filename = fromCsvFilepath("ExBasePatternSet");
		ExBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataExBasePatternSet.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBasePatternSetFromCsvFile(filename);
		assertEquals(dataExBasePatternSet, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExBasePatternSet.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBasePatternSetFromCsvFile(filename);
		assertEquals(dataExBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExBasePatternSetFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExBasePatternSetFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("ExBasePatternSet", encoding);
		ExBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExBasePatternSet.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBasePatternSetFromCsvFile(filename, encoding);
		assertEquals(dataExBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExBasePatternSetFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExBasePatternSetFromXmlFile() {
		String filename = fromXmlFilepath("ExBasePatternSet");
		ExBasePatternSet alge;
		
		try {
			dataExBasePatternSet.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExBasePatternSetFromXmlFile(filename);
		assertEquals(dataExBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newTransTableFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewTransTableFromCsvFileString() {
		String filename = fromCsvFilepath("TransTable");
		TransTable alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataTransTable.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransTableFromCsvFile(filename);
		assertEquals(dataTransTable, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataTransTable.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransTableFromCsvFile(filename);
		assertEquals(dataTransTable, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newTransTableFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewTransTableFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("TransTable", encoding);
		TransTable alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataTransTable.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransTableFromCsvFile(filename, encoding);
		assertEquals(dataTransTable, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newTransTableFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewTransTableFromXmlFile() {
		String filename = fromXmlFilepath("TransTable");
		TransTable alge;
		
		try {
			dataTransTable.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransTableFromXmlFile(filename);
		assertEquals(dataTransTable, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newTransMatrixFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewTransMatrixFromCsvFileString() {
		String filename = fromCsvFilepath("TransMatrix");
		TransMatrix alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataTransMatrix.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransMatrixFromCsvFile(filename);
		assertEquals(dataTransMatrix, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataTransMatrix.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransMatrixFromCsvFile(filename);
		assertEquals(dataTransMatrix, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newTransMatrixFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewTransMatrixFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("TransMatrix", encoding);
		TransMatrix alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataTransMatrix.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransMatrixFromCsvFile(filename, encoding);
		assertEquals(dataTransMatrix, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newTransMatrixFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewTransMatrixFromXmlFile() {
		String filename = fromXmlFilepath("TransMatrix");
		TransMatrix alge;
		
		try {
			dataTransMatrix.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newTransMatrixFromXmlFile(filename);
		assertEquals(dataTransMatrix, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExTransferFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExTransferFromCsvFileString() {
		String filename = fromCsvFilepath("ExTransfer");
		ExTransfer alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataExTransfer.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExTransferFromCsvFile(filename);
		assertEquals(dataExTransfer, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExTransfer.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExTransferFromCsvFile(filename);
		assertEquals(dataExTransfer, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExTransferFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExTransferFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("ExTransfer", encoding);
		ExTransfer alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataExTransfer.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExTransferFromCsvFile(filename, encoding);
		assertEquals(dataExTransfer, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newExTransferFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewExTransferFromXmlFile() {
		String filename = fromXmlFilepath("ExTransfer");
		ExTransfer alge;
		
		try {
			dataExTransfer.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newExTransferFromXmlFile(filename);
		assertEquals(dataExTransfer, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtalgeFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtalgeFromCsvFileString() {
		String filename = fromCsvFilepath("Dtalge");
		Dtalge alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataDtalge.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtalgeFromCsvFile(filename);
		assertEquals(dataDtalge, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtalge.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtalgeFromCsvFile(filename);
		assertEquals(dataDtalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtalgeFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtalgeFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("Dtalge", encoding);
		Dtalge alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtalge.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtalgeFromCsvFile(filename, encoding);
		assertEquals(dataDtalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtalgeFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtalgeFromXmlFile() {
		String filename = fromXmlFilepath("Dtalge");
		Dtalge alge;
		
		try {
			dataDtalge.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtalgeFromXmlFile(filename);
		assertEquals(dataDtalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtAlgeSetFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtAlgeSetFromCsvFileString() {
		String filename = fromCsvFilepath("DtAlgeSet");
		DtAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataDtAlgeSet.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtAlgeSetFromCsvFile(filename);
		assertEquals(dataDtAlgeSet, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtAlgeSet.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtAlgeSetFromCsvFile(filename);
		assertEquals(dataDtAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtAlgeSetFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtAlgeSetFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("DtAlgeSet", encoding);
		DtAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtAlgeSet.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtAlgeSetFromCsvFile(filename, encoding);
		assertEquals(dataDtAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtAlgeSetFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtAlgeSetFromXmlFile() {
		String filename = fromXmlFilepath("DtAlgeSet");
		DtAlgeSet alge;
		
		try {
			dataDtAlgeSet.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtAlgeSetFromXmlFile(filename);
		assertEquals(dataDtAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtBaseSetFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtBaseSetFromCsvFileString() {
		String filename = fromCsvFilepath("DtBaseSet");
		DtBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataDtBaseSet.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBaseSetFromCsvFile(filename);
		assertEquals(dataDtBaseSet, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtBaseSet.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBaseSetFromCsvFile(filename);
		assertEquals(dataDtBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtBaseSetFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtBaseSetFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("DtBaseSet", encoding);
		DtBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtBaseSet.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBaseSetFromCsvFile(filename, encoding);
		assertEquals(dataDtBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtBaseSetFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtBaseSetFromXmlFile() {
		String filename = fromXmlFilepath("DtBaseSet");
		DtBaseSet alge;
		
		try {
			dataDtBaseSet.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBaseSetFromXmlFile(filename);
		assertEquals(dataDtBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtBasePatternSetFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtBasePatternSetFromCsvFileString() {
		String filename = fromCsvFilepath("DtBasePatternSet");
		DtBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataDtBasePatternSet.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBasePatternSetFromCsvFile(filename);
		assertEquals(dataDtBasePatternSet, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtBasePatternSet.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBasePatternSetFromCsvFile(filename);
		assertEquals(dataDtBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtBasePatternSetFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtBasePatternSetFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("DtBasePatternSet", encoding);
		DtBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtBasePatternSet.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBasePatternSetFromCsvFile(filename, encoding);
		assertEquals(dataDtBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtBasePatternSetFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtBasePatternSetFromXmlFile() {
		String filename = fromXmlFilepath("DtBasePatternSet");
		DtBasePatternSet alge;
		
		try {
			dataDtBasePatternSet.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtBasePatternSetFromXmlFile(filename);
		assertEquals(dataDtBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtStringThesaurusFromCsvFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtStringThesaurusFromCsvFileString() {
		String filename = fromCsvFilepath("DtStringThesaurus");
		DtStringThesaurus alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		try {
			dataDtStringThesaurus.toCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtStringThesaurusFromCsvFile(filename);
		assertEquals(dataDtStringThesaurus, alge);

		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtStringThesaurus.toCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtStringThesaurusFromCsvFile(filename);
		assertEquals(dataDtStringThesaurus, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtStringThesaurusFromCsvFile(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtStringThesaurusFromCsvFileStringString() {
		String encoding = "UTF-8";
		String filename = fromCsvFilepath("DtStringThesaurus", encoding);
		DtStringThesaurus alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		try {
			dataDtStringThesaurus.toCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to CSV file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtStringThesaurusFromCsvFile(filename, encoding);
		assertEquals(dataDtStringThesaurus, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#newDtStringThesaurusFromXmlFile(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewDtStringThesaurusFromXmlFile() {
		String filename = fromXmlFilepath("DtStringThesaurus");
		DtStringThesaurus alge;
		
		try {
			dataDtStringThesaurus.toXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to XML file : \"" + filename + "\"");
		}
		alge = AADLFunctions.newDtStringThesaurusFromXmlFile(filename);
		assertEquals(dataDtStringThesaurus, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.Exalge, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExalgeString() {
		String filename = toCsvFilepath("Exalge");
		Exalge alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataExalge, filename);
		alge = null;
		try {
			alge = Exalge.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExalge, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExalge, filename);
		alge = null;
		try {
			alge = Exalge.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.Exalge, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExalgeStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("Exalge", encoding);
		Exalge alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExalge, filename, encoding);
		alge = null;
		try {
			alge = Exalge.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(exalge2.Exalge, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileExalgeString() {
		String filename = toXmlFilepath("Exalge");
		Exalge alge;
		
		AADLFunctions.writeXmlFile(dataExalge, filename);
		alge = null;
		try {
			alge = Exalge.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataExalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExAlgeSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExAlgeSetString() {
		String filename = toCsvFilepath("ExAlgeSet");
		ExAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataExAlgeSet, filename);
		alge = null;
		try {
			alge = ExAlgeSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExAlgeSet, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExAlgeSet, filename);
		alge = null;
		try {
			alge = ExAlgeSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExAlgeSet, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExAlgeSetStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("ExAlgeSet", encoding);
		ExAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExAlgeSet, filename, encoding);
		alge = null;
		try {
			alge = ExAlgeSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(exalge2.ExAlgeSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileExAlgeSetString() {
		String filename = toXmlFilepath("ExAlgeSet");
		ExAlgeSet alge;
		
		AADLFunctions.writeXmlFile(dataExAlgeSet, filename);
		alge = null;
		try {
			alge = ExAlgeSet.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataExAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExBaseSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExBaseSetString() {
		String filename = toCsvFilepath("ExBaseSet");
		ExBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataExBaseSet, filename);
		alge = null;
		try {
			alge = ExBaseSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExBaseSet, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExBaseSet, filename);
		alge = null;
		try {
			alge = ExBaseSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExBaseSet, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExBaseSetStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("ExBaseSet", encoding);
		ExBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExBaseSet, filename, encoding);
		alge = null;
		try {
			alge = ExBaseSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(exalge2.ExBaseSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileExBaseSetString() {
		String filename = toXmlFilepath("ExBaseSet");
		ExBaseSet alge;
		
		AADLFunctions.writeXmlFile(dataExBaseSet, filename);
		alge = null;
		try {
			alge = ExBaseSet.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataExBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExBasePatternSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExBasePatternSetString() {
		String filename = toCsvFilepath("ExBasePatternSet");
		ExBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataExBasePatternSet, filename);
		alge = null;
		try {
			alge = ExBasePatternSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExBasePatternSet, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExBasePatternSet, filename);
		alge = null;
		try {
			alge = ExBasePatternSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExBasePatternSet, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExBasePatternSetStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("ExBasePatternSet", encoding);
		ExBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExBasePatternSet, filename, encoding);
		alge = null;
		try {
			alge = ExBasePatternSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(exalge2.ExBasePatternSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileExBasePatternSetString() {
		String filename = toXmlFilepath("ExBasePatternSet");
		ExBasePatternSet alge;
		
		AADLFunctions.writeXmlFile(dataExBasePatternSet, filename);
		alge = null;
		try {
			alge = ExBasePatternSet.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataExBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.TransTable, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileTransTableString() {
		String filename = toCsvFilepath("TransTable");
		TransTable alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataTransTable, filename);
		alge = null;
		try {
			alge = TransTable.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataTransTable, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataTransTable, filename);
		alge = null;
		try {
			alge = TransTable.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataTransTable, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.TransTable, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileTransTableStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("TransTable", encoding);
		TransTable alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataTransTable, filename, encoding);
		alge = null;
		try {
			alge = TransTable.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataTransTable, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(exalge2.TransTable, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileTransTableString() {
		String filename = toXmlFilepath("TransTable");
		TransTable alge;
		
		AADLFunctions.writeXmlFile(dataTransTable, filename);
		alge = null;
		try {
			alge = TransTable.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataTransTable, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.TransMatrix, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileTransMatrixString() {
		String filename = toCsvFilepath("TransMatrix");
		TransMatrix alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataTransMatrix, filename);
		alge = null;
		try {
			alge = TransMatrix.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataTransMatrix, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataTransMatrix, filename);
		alge = null;
		try {
			alge = TransMatrix.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataTransMatrix, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.TransMatrix, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileTransMatrixStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("TransMatrix", encoding);
		TransMatrix alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataTransMatrix, filename, encoding);
		alge = null;
		try {
			alge = TransMatrix.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataTransMatrix, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(exalge2.TransMatrix, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileTransMatrixString() {
		String filename = toXmlFilepath("TransMatrix");
		TransMatrix alge;
		
		AADLFunctions.writeXmlFile(dataTransMatrix, filename);
		alge = null;
		try {
			alge = TransMatrix.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataTransMatrix, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExTransfer, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExTransferString() {
		String filename = toCsvFilepath("ExTransfer");
		ExTransfer alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataExTransfer, filename);
		alge = null;
		try {
			alge = ExTransfer.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExTransfer, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExTransfer, filename);
		alge = null;
		try {
			alge = ExTransfer.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExTransfer, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(exalge2.ExTransfer, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileExTransferStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("ExTransfer", encoding);
		ExTransfer alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataExTransfer, filename, encoding);
		alge = null;
		try {
			alge = ExTransfer.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataExTransfer, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(exalge2.ExTransfer, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileExTransferString() {
		String filename = toXmlFilepath("ExTransfer");
		ExTransfer alge;
		
		AADLFunctions.writeXmlFile(dataExTransfer, filename);
		alge = null;
		try {
			alge = ExTransfer.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataExTransfer, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.Dtalge, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtalgeString() {
		String filename = toCsvFilepath("Dtalge");
		Dtalge alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataDtalge, filename);
		alge = null;
		try {
			alge = Dtalge.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtalge, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtalge, filename);
		alge = null;
		try {
			alge = Dtalge.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.Dtalge, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtalgeStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("Dtalge", encoding);
		Dtalge alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtalge, filename, encoding);
		alge = null;
		try {
			alge = Dtalge.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(dtalge.Dtalge, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileDtalgeString() {
		String filename = toXmlFilepath("Dtalge");
		Dtalge alge;
		
		AADLFunctions.writeXmlFile(dataDtalge, filename);
		alge = null;
		try {
			alge = Dtalge.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataDtalge, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtAlgeSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtAlgeSetString() {
		String filename = toCsvFilepath("DtAlgeSet");
		DtAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataDtAlgeSet, filename);
		alge = null;
		try {
			alge = DtAlgeSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtAlgeSet, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtAlgeSet, filename);
		alge = null;
		try {
			alge = DtAlgeSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtAlgeSet, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtAlgeSetStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("DtAlgeSet", encoding);
		DtAlgeSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtAlgeSet, filename, encoding);
		alge = null;
		try {
			alge = DtAlgeSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(dtalge.DtAlgeSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileDtAlgeSetString() {
		String filename = toXmlFilepath("DtAlgeSet");
		DtAlgeSet alge;
		
		AADLFunctions.writeXmlFile(dataDtAlgeSet, filename);
		alge = null;
		try {
			alge = DtAlgeSet.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataDtAlgeSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtBaseSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtBaseSetString() {
		String filename = toCsvFilepath("DtBaseSet");
		DtBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataDtBaseSet, filename);
		alge = null;
		try {
			alge = DtBaseSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtBaseSet, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtBaseSet, filename);
		alge = null;
		try {
			alge = DtBaseSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtBaseSet, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtBaseSetStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("DtBaseSet", encoding);
		DtBaseSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtBaseSet, filename, encoding);
		alge = null;
		try {
			alge = DtBaseSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(dtalge.DtBaseSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileDtBaseSetString() {
		String filename = toXmlFilepath("DtBaseSet");
		DtBaseSet alge;
		
		AADLFunctions.writeXmlFile(dataDtBaseSet, filename);
		alge = null;
		try {
			alge = DtBaseSet.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataDtBaseSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtBasePatternSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtBasePatternSetString() {
		String filename = toCsvFilepath("DtBasePatternSet");
		DtBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataDtBasePatternSet, filename);
		alge = null;
		try {
			alge = DtBasePatternSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtBasePatternSet, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtBasePatternSet, filename);
		alge = null;
		try {
			alge = DtBasePatternSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtBasePatternSet, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtBasePatternSetStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("DtBasePatternSet", encoding);
		DtBasePatternSet alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtBasePatternSet, filename, encoding);
		alge = null;
		try {
			alge = DtBasePatternSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(dtalge.DtBasePatternSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileDtBasePatternSetString() {
		String filename = toXmlFilepath("DtBasePatternSet");
		DtBasePatternSet alge;
		
		AADLFunctions.writeXmlFile(dataDtBasePatternSet, filename);
		alge = null;
		try {
			alge = DtBasePatternSet.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataDtBasePatternSet, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtStringThesaurus, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtStringThesaurusString() {
		String filename = toCsvFilepath("DtStringThesaurus");
		DtStringThesaurus alge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeCsvFile(dataDtStringThesaurus, filename);
		alge = null;
		try {
			alge = DtStringThesaurus.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtStringThesaurus, alge);
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtStringThesaurus, filename);
		alge = null;
		try {
			alge = DtStringThesaurus.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtStringThesaurus, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeCsvFile(dtalge.DtStringThesaurus, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteCsvFileDtStringThesaurusStringString() {
		String encoding = "UTF-8";
		String filename = toCsvFilepath("DtStringThesaurus", encoding);
		DtStringThesaurus alge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeCsvFile(dataDtStringThesaurus, filename, encoding);
		alge = null;
		try {
			alge = DtStringThesaurus.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(dataDtStringThesaurus, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeXmlFile(dtalge.DtStringThesaurus, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteXmlFileDtStringThesaurusString() {
		String filename = toXmlFilepath("DtStringThesaurus");
		DtStringThesaurus alge;
		
		AADLFunctions.writeXmlFile(dataDtStringThesaurus, filename);
		alge = null;
		try {
			alge = DtStringThesaurus.fromXML(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from XML file : \"" + filename + "\"");
		}
		assertEquals(dataDtStringThesaurus, alge);
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(dtalge.Dtalge, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtalgeString() {
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String filename = toCsvFilepath("TableDtalge");
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFile(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtalgeIOTestHelper.equalElementSequence(retAlge, DtalgeIOTestHelper.readRandomAlgeData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtalgeIOTestHelper.equalElementSequence(retAlge, DtalgeIOTestHelper.readRandomAlgeData));
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(dtalge.Dtalge, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtalgeStringString() {
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String encoding = "UTF-8";
		String filename = toCsvFilepath("TableDtalge", encoding);
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, filename, encoding);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtalgeIOTestHelper.equalElementSequence(retAlge, DtalgeIOTestHelper.readRandomAlgeData));
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(dtalge.DtAlgeSet, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtAlgeSetString() {
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String filename = toCsvFilepath("TableDtAlgeSet");
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFile(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalElementSequence(retAlge, DtAlgeSetIOTestHelper.readRandomTableAlgeSetData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalElementSequence(retAlge, DtAlgeSetIOTestHelper.readRandomTableAlgeSetData));
	}

	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(dtalge.DtAlgeSet, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtAlgeSetStringString() {
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String encoding = "UTF-8";
		String filename = toCsvFilepath("TableDtAlgeSet", encoding);
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, filename, encoding);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalElementSequence(retAlge, DtAlgeSetIOTestHelper.readRandomTableAlgeSetData));
	}
	
	//
	// Test for Version 1.80
	//
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(Dtalge, DtBaseSet, boolean, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtalgeDtBaseSetBooleanString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String filename = toCsvFilepath("TableDtalgeBaseOrderWithoutNull");
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFile(srcAlge, orderBases, true, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge.nonullProjection(), retAlge);
		assertTrue(DtalgeIOTestHelper.equalNoNullElementSequence(retAlge, DtalgeIOTestHelper.readSortedAlgeData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, orderBases, true, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge.nonullProjection(), retAlge);
		assertTrue(DtalgeIOTestHelper.equalNoNullElementSequence(retAlge, DtalgeIOTestHelper.readSortedAlgeData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(Dtalge, DtBaseSet, boolean, String, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtalgeDtBaseSetBooleanStringString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String encoding = "UTF-8";
		String filename = toCsvFilepath("TableDtalgeBaseOrderWithoutNull", encoding);
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, orderBases, true, filename, encoding);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge.nonullProjection(), retAlge);
		assertTrue(DtalgeIOTestHelper.equalNoNullElementSequence(retAlge, DtalgeIOTestHelper.readSortedAlgeData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(DtAlgeSet, DtBaseSet, boolean, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtAlgeSetDtBaseSetBooleanString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String filename = toCsvFilepath("TableDtAlgeSetBaseOrderWithoutNull");
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFile(srcAlge, orderBases, true, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(DtAlgeSetIOTestHelper.removeNullElements(srcAlge), retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalNoNullElementSequence(retAlge, DtAlgeSetIOTestHelper.readSortedAlgeSetData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, orderBases, true, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(DtAlgeSetIOTestHelper.removeNullElements(srcAlge), retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalNoNullElementSequence(retAlge, DtAlgeSetIOTestHelper.readSortedAlgeSetData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFile(DtAlgeSet, DtBaseSet, boolean, String, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileDtAlgeSetDtBaseSetBooleanStringString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String filename = toCsvFilepath("TableDtAlgeSetBaseOrderWithoutNull");
		String encoding = "UTF-8";
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFile(srcAlge, orderBases, true, filename, encoding);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(DtAlgeSetIOTestHelper.removeNullElements(srcAlge), retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalNoNullElementSequence(retAlge, DtAlgeSetIOTestHelper.readSortedAlgeSetData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithoutNull(Dtalge, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithoutNullDtalgeString() {
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String filename = toCsvFilepath("TableDtalgeWithoutNull");
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFileWithoutNull(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge.nonullProjection(), retAlge);
		assertTrue(DtalgeIOTestHelper.equalNoNullElementSequence(retAlge, DtalgeIOTestHelper.readRandomAlgeData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithoutNull(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge.nonullProjection(), retAlge);
		assertTrue(DtalgeIOTestHelper.equalNoNullElementSequence(retAlge, DtalgeIOTestHelper.readRandomAlgeData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithoutNull(Dtalge, String, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithoutNullDtalgeStringString() {
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String encoding = "UTF-8";
		String filename = toCsvFilepath("TableDtalgeWithoutNull", encoding);
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithoutNull(srcAlge, filename, encoding);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge.nonullProjection(), retAlge);
		assertTrue(DtalgeIOTestHelper.equalNoNullElementSequence(retAlge, DtalgeIOTestHelper.readRandomAlgeData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithoutNull(DtAlgeSet, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithoutNullDtAlgeSetString() {
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String filename = toCsvFilepath("TableDtAlgeSetWithoutNull");
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFileWithoutNull(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(DtAlgeSetIOTestHelper.removeNullElements(srcAlge), retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalNoNullElementSequence(retAlge, DtAlgeSetIOTestHelper.readRandomTableAlgeSetData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithoutNull(srcAlge, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(DtAlgeSetIOTestHelper.removeNullElements(srcAlge), retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalNoNullElementSequence(retAlge, DtAlgeSetIOTestHelper.readRandomTableAlgeSetData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithoutNull(DtAlgeSet, String, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithoutNullDtAlgeSetStringString() {
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String filename = toCsvFilepath("TableDtAlgeSetWithoutNull");
		String encoding = "UTF-8";
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithoutNull(srcAlge, filename, encoding);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(DtAlgeSetIOTestHelper.removeNullElements(srcAlge), retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalNoNullElementSequence(retAlge, DtAlgeSetIOTestHelper.readRandomTableAlgeSetData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithBaseOrder(Dtalge, DtBaseSet, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithBaseOrderDtalgeDtBaseSetString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String filename = toCsvFilepath("TableDtalgeBaseOrder");
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFileWithBaseOrder(srcAlge, orderBases, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtalgeIOTestHelper.equalElementSequence(retAlge, DtalgeIOTestHelper.readSortedAlgeData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithBaseOrder(srcAlge, orderBases, filename);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtalgeIOTestHelper.equalElementSequence(retAlge, DtalgeIOTestHelper.readSortedAlgeData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithBaseOrder(Dtalge, DtBaseSet, String, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithBaseOrderDtalgeDtBaseSetStringString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		Dtalge srcAlge = new Dtalge(DtalgeIOTestHelper.readRandomAlgeData);
		String encoding = "UTF-8";
		String filename = toCsvFilepath("TableDtalgeBaseOrder", encoding);
		Dtalge retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithBaseOrder(srcAlge, orderBases, filename, encoding);
		retAlge = null;
		try {
			retAlge = Dtalge.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtalgeIOTestHelper.equalElementSequence(retAlge, DtalgeIOTestHelper.readSortedAlgeData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithBaseOrder(DtAlgeSet, DtBaseSet, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithBaseOrderDtAlgeSetDtBaseSetString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String filename = toCsvFilepath("TableDtAlgeSetBaseOrder");
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding("UTF-8");
		AADLFunctions.writeTableCsvFileWithBaseOrder(srcAlge, orderBases, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), "UTF-8");
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalElementSequence(retAlge, DtAlgeSetIOTestHelper.readSortedAlgeSetData));
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithBaseOrder(srcAlge, orderBases, filename);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename));
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalElementSequence(retAlge, DtAlgeSetIOTestHelper.readSortedAlgeSetData));
	}
	
	/**
	 * {@link ssac.aadl.runtime.AADLFunctions#writeTableCsvFileWithBaseOrder(DtAlgeSet, DtBaseSet, String, String)} のためのテスト・メソッド。
	 */
	public void testWriteTableCsvFileWithBaseOrderDtAlgeSetDtBaseSetStringString() {
		DtBaseSet orderBases = new DtBaseSet(Arrays.asList(DtAlgeSetIOTestHelper.sortedAllBaseSetData));
		DtAlgeSet srcAlge = new DtAlgeSet(DtAlgeSetIOTestHelper.makeDtalges(DtAlgeSetIOTestHelper.readRandomAlgeSetData));
		String filename = toCsvFilepath("TableDtAlgeSetBaseOrder");
		String encoding = "UTF-8";
		DtAlgeSet retAlge;
		
		AADLFunctions.setDefaultCsvEncoding(null);
		AADLFunctions.writeTableCsvFileWithBaseOrder(srcAlge, orderBases, filename, encoding);
		retAlge = null;
		try {
			retAlge = DtAlgeSet.fromCSV(new File(filename), encoding);
		} catch (Throwable ex) {
			fail("Failed to read from CSV file : \"" + filename + "\"");
		}
		assertEquals(srcAlge, retAlge);
		assertTrue(DtAlgeSetIOTestHelper.equalElementSequence(retAlge, DtAlgeSetIOTestHelper.readSortedAlgeSetData));
	}
}
