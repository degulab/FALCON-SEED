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
 *  Copyright 2007-2014  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
package exalge2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;
import exalge2.io.FileUtil;
import exalge2.io.csv.CsvFormatException;
import exalge2.io.xml.XmlDomParseException;

public class TransTableIOTest extends TestCase {
	static private final String patAppleYen	= "りんご-*-円-*-*";
	static private final String patOrangeYen	= "みかん-*-円-*-*";
	static private final String patBananaYen	= "ばなな-*-円-*-*";
	
	static private final String patFruit		= "果物-*-*-*-*";
	
	static private final String patTuna		= "まぐろ-*-*-*-*";
	static private final String patBonito		= "かつお-*-*-*-*";
	
	static private final String patFish		= "魚-*-*-*-*";
	
	static private final String patMelon		= "メロン-*-*-*-*";
	static private final String patMango		= "マンゴー-*-*-*-*";
	static private final String patExpFruit	= "高級果物-*-*-*-*";

	/*---
	private ExBasePattern bpAppleYen;
	private ExBasePattern bpOrangeYen;
	private ExBasePattern bpBananaYen;
	private ExBasePattern bpFruit;
	
	private ExBasePattern bpMelon;
	private ExBasePattern bpMango;
	private ExBasePattern bpExpFruit;
	
	private ExBasePattern bpTuna;
	private ExBasePattern bpBonito;
	private ExBasePattern bpFish;
	---*/
	
	
	static private final String UTF8 = "UTF-8";
	
	static private final ExBasePattern[][] seqData = new ExBasePattern[][]{
		{new ExBasePattern(patAppleYen),	new ExBasePattern(patFruit)},
		{new ExBasePattern(patBananaYen),	new ExBasePattern(patFruit)},
		{new ExBasePattern(patOrangeYen),	new ExBasePattern(patFruit)},
		{new ExBasePattern(patMelon),		new ExBasePattern(patExpFruit)},
		{new ExBasePattern(patMango),		new ExBasePattern(patExpFruit)},
		{new ExBasePattern(patTuna),		new ExBasePattern(patFish)},
		{new ExBasePattern(patBonito),		new ExBasePattern(patFish)}
	};
	
	static public TransTable createTransTable() {
		TransTable newTable = new TransTable();
		for (int i = 0; i < seqData.length; i++) {
			newTable.put(seqData[i][0], seqData[i][1]);
		}
		return newTable;
	}
	
	static public void checkTableData(TransTable targetTable) {
		// check data
		assertEquals(seqData.length, targetTable.size());
		for (int i = 0; i < seqData.length; i++) {
			assertEquals(seqData[i][1], targetTable.getTransTo(seqData[i][0]));
		}
		// check sequence
		int idx = 0;
		Iterator<ExBasePattern> it = targetTable.transFromPatterns().iterator();
		while (it.hasNext()) {
			ExBasePattern fFrom = it.next();
			ExBasePattern fTo   = targetTable.getTransTo(fFrom);
			ExBasePattern sFrom = seqData[idx][0];
			ExBasePattern sTo   = seqData[idx][1];
			idx++;
			assertTrue(sFrom.equals(fFrom));
			assertTrue(sTo.equals(fTo));
		}
	}
	
	static public void printTableData(TransTable targetTable, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("(TransTable) = ");
		Iterator<ExBasePattern> it = targetTable.transFromPatterns().iterator();
		while (it.hasNext()) {
			ExBasePattern patFrom = it.next();
			ExBasePattern patTo   = targetTable.getTransTo(patFrom);
			sb.append("\n    F[");
			sb.append(patFrom.toString());
			sb.append("] --> T[");
			sb.append(patTo.toString());
			sb.append("]");
		}
		
		System.out.println(sb.toString());
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		/*---
		bpAppleYen	= new ExBasePattern(patAppleYen);
		bpOrangeYen	= new ExBasePattern(patOrangeYen);
		bpBananaYen	= new ExBasePattern(patBananaYen);
		bpFruit		= new ExBasePattern(patFruit);
		
		bpMelon		= new ExBasePattern(patMelon);
		bpMango		= new ExBasePattern(patMango);
		bpExpFruit	= new ExBasePattern(patExpFruit);
		
		bpTuna		= new ExBasePattern(patTuna);
		bpBonito	= new ExBasePattern(patBonito);
		bpFish		= new ExBasePattern(patFish);
		---*/
	}
	
	/**
	 * {@link exalge2.TransTable#fromCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVFile() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalTransTable.csv";
		try {
			TransTable retNormal = TransTable.fromCSV(new File(csvNormal));
			printTableData(retNormal, "testFromCSVFile - " + csvNormal);
			// check
			checkTableData(retNormal);
			/*---
			assertEquals(5, retNormal.size());
			assertEquals(bpFruit, retNormal.getTransTo(bpAppleYen));
			assertEquals(bpFruit, retNormal.getTransTo(bpOrangeYen));
			assertEquals(bpFruit, retNormal.getTransTo(bpBananaYen));
			assertEquals(bpFish, retNormal.getTransTo(bpTuna));
			assertEquals(bpFish, retNormal.getTransTo(bpBonito));
			---*/
		}
		catch (CsvFormatException ex) {
			System.out.println("CSV Format error : factor["
					+ ex.getIllegalFactor() + "]  line["
					+ ex.getLineNumber() + "]  column["
					+ ex.getColumnNumber() + "]");
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvNormal + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvNormal + "\"");
		}
	}
	
	/**
	 * {@link exalge2.TransTable#fromCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromCSVFileString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalTransTable_UTF8.csv";
		try {
			TransTable retNormal = TransTable.fromCSV(new File(csvNormal), UTF8);
			// check
			checkTableData(retNormal);
		}
		catch (CsvFormatException ex) {
			System.out.println("CSV Format error : factor["
					+ ex.getIllegalFactor() + "]  line["
					+ ex.getLineNumber() + "]  column["
					+ ex.getColumnNumber() + "]");
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvNormal + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvNormal + "\"");
		}
	}

	/**
	 * {@link exalge2.TransTable#toCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToCSVFile() {
		/*---
		TransTable table = new TransTable();
		table.put(bpAppleYen, bpFruit);
		table.put(bpOrangeYen, bpFruit);
		table.put(bpBananaYen, bpFruit);
		table.put(bpTuna, bpFish);
		table.put(bpBonito, bpFish);
		assertEquals(5, table.size());
		---*/
		TransTable table = createTransTable();
		printTableData(table, "testToCSVFile - table");
		
		final String csvOutput = "testdata/CSV/OutputTransTable.csv";

		try {
			table.toCSV(new File(csvOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			TransTable ret1 = TransTable.fromCSV(new File(csvOutput));
			checkTableData(ret1);
			/*---
			assertEquals(5, ret1.size());
			assertEquals(bpFruit, ret1.getTransTo(bpAppleYen));
			assertEquals(bpFruit, ret1.getTransTo(bpOrangeYen));
			assertEquals(bpFruit, ret1.getTransTo(bpBananaYen));
			assertEquals(bpFish, ret1.getTransTo(bpTuna));
			assertEquals(bpFish, ret1.getTransTo(bpBonito));
			---*/
		}
		catch (CsvFormatException ex) {
			System.out.println("CSV Format error : factor["
					+ ex.getIllegalFactor() + "]  line["
					+ ex.getLineNumber() + "]  column["
					+ ex.getColumnNumber() + "]");
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvOutput + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvOutput + "\"");
		}
	}

	/**
	 * {@link exalge2.TransTable#toCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testToCSVFileString() {
		TransTable table = createTransTable();
		
		final String csvOutput = "testdata/CSV/OutputTransTable_UTF8.csv";

		try {
			table.toCSV(new File(csvOutput), UTF8);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			TransTable ret1 = TransTable.fromCSV(new File(csvOutput), UTF8);
			checkTableData(ret1);
		}
		catch (CsvFormatException ex) {
			System.out.println("CSV Format error : factor["
					+ ex.getIllegalFactor() + "]  line["
					+ ex.getLineNumber() + "]  column["
					+ ex.getColumnNumber() + "]");
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvOutput + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvOutput + "\"");
		}
	}

	/**
	 * {@link exalge2.TransTable#fromXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromXMLFile() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalTransTable.xml";
		try {
			TransTable retNormal = TransTable.fromXML(new File(xmlNormal));
			// check
			checkTableData(retNormal);
			/*---
			assertEquals(5, retNormal.size());
			assertEquals(bpFruit, retNormal.getTransTo(bpAppleYen));
			assertEquals(bpFruit, retNormal.getTransTo(bpOrangeYen));
			assertEquals(bpFruit, retNormal.getTransTo(bpBananaYen));
			assertEquals(bpFish, retNormal.getTransTo(bpTuna));
			assertEquals(bpFish, retNormal.getTransTo(bpBonito));
			---*/
		}
		catch (XmlDomParseException ex) {
			StringBuffer sb = new StringBuffer();
			sb.append("XML Format error : factor[");
			sb.append(ex.getIllegalFactor());
			sb.append("]  XmlDocument[");
			if (ex.getFailedDocument() != null)
				sb.append(ex.getFailedDocument().toString());
			else
				sb.append("null");
			sb.append("]  Element[");
			if (ex.getFailedElement() != null)
				sb.append(ex.getFailedElement().getNodeName() + "(" + ex.getFailedElement().toString() + ")");
			else
				sb.append("null");
			sb.append("]  Attribute[");
			if (ex.getFailedAttributeName() != null)
				sb.append(ex.getFailedAttributeName());
			else
				sb.append("null");
			sb.append("]  Value[");
			if (ex.getFailedValue() != null)
				sb.append(ex.getFailedValue());
			else
				sb.append("null");
			sb.append("]");
			
			super.fail("Failed to read from XML file \"" + xmlNormal + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlNormal + "\"");
		}
	}

	/**
	 * {@link exalge2.TransTable#toXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToXMLFile() {
		/*---
		TransTable table = new TransTable();
		table.put(bpAppleYen, bpFruit);
		table.put(bpOrangeYen, bpFruit);
		table.put(bpBananaYen, bpFruit);
		table.put(bpTuna, bpFish);
		table.put(bpBonito, bpFish);
		assertEquals(5, table.size());
		---*/
		TransTable table = createTransTable();
		
		final String xmlOutput = "testdata/XML/OutputTransTable.xml";

		try {
			table.toXML(new File(xmlOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to XML file \"" + xmlOutput + "\"");
		}
		
		// check
		try {
			TransTable ret1 = TransTable.fromXML(new File(xmlOutput));
			checkTableData(ret1);
			/*---
			assertEquals(5, ret1.size());
			assertEquals(bpFruit, ret1.getTransTo(bpAppleYen));
			assertEquals(bpFruit, ret1.getTransTo(bpOrangeYen));
			assertEquals(bpFruit, ret1.getTransTo(bpBananaYen));
			assertEquals(bpFish, ret1.getTransTo(bpTuna));
			assertEquals(bpFish, ret1.getTransTo(bpBonito));
			---*/
		}
		catch (XmlDomParseException ex) {
			StringBuffer sb = new StringBuffer();
			sb.append("XML Format error : factor[");
			sb.append(ex.getIllegalFactor());
			sb.append("]  XmlDocument[");
			if (ex.getFailedDocument() != null)
				sb.append(ex.getFailedDocument().toString());
			else
				sb.append("null");
			sb.append("]  Element[");
			if (ex.getFailedElement() != null)
				sb.append(ex.getFailedElement().getNodeName() + "(" + ex.getFailedElement().toString() + ")");
			else
				sb.append("null");
			sb.append("]  Attribute[");
			if (ex.getFailedAttributeName() != null)
				sb.append(ex.getFailedAttributeName());
			else
				sb.append("null");
			sb.append("]  Value[");
			if (ex.getFailedValue() != null)
				sb.append(ex.getFailedValue());
			else
				sb.append("null");
			sb.append("]");
			
			super.fail("Failed to read from XML file \"" + xmlOutput + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlOutput + "\"");
		}
	}

	/**
	 * {@link exalge2.TransTable#fromXML(exalge2.io.xml.XmlDocument)} のためのテスト・メソッド。
	 */
	/*---
	public void testFromXMLXmlDocument() {
		fail("まだ実装されていません。");
	}
	---*/

	/**
	 * {@link exalge2.TransTable#toXML()} のためのテスト・メソッド。
	 */
	/*---
	public void testToXML() {
		fail("まだ実装されていません。");
	}
	---*/
	
	/**
	 * {@link exalge2.TransTable#fromCsvString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromCsvStringString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalTransTable.csv";
		//--- read from file to string
		String csvString = null;
		{
			FileInputStream fis = null;
			try {
				File f = new File(csvNormal);
				byte[] buf = new byte[8192];
				ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());
				fis = new FileInputStream(new File(csvNormal));
				int r;
				while ((r = fis.read(buf)) >= 0) {
					bos.write(buf, 0, r);
				}
				
				csvString = bos.toString("MS932");
			}
			catch (IOException ex) {
				ex.printStackTrace();
				super.fail("Failed to read from CSV file \"" + csvNormal + "\"");
			}
			finally {
				if (fis != null) {
					FileUtil.closeStream(fis);
					fis = null;
				}
			}
		}
		//--- read from string
		try {
			TransTable retNormal = TransTable.fromCsvString(csvString);
			// check
			checkTableData(retNormal);
		}
		catch (CsvFormatException ex) {
			System.out.println("CSV Format error : factor["
					+ ex.getIllegalFactor() + "]  line["
					+ ex.getLineNumber() + "]  column["
					+ ex.getColumnNumber() + "]");
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvNormal + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + csvNormal + "\"");
		}
	}

	/**
	 * {@link exalge2.TransTable#fromXmlString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromXmlStringString() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalTransTable.xml";
		//--- read from file to string
		String xmlString = null;
		{
			FileInputStream fis = null;
			try {
				File f = new File(xmlNormal);
				byte[] buf = new byte[8192];
				ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());
				fis = new FileInputStream(new File(xmlNormal));
				int r;
				while ((r = fis.read(buf)) >= 0) {
					bos.write(buf, 0, r);
				}
				
				xmlString = bos.toString("UTF-8");
			}
			catch (IOException ex) {
				ex.printStackTrace();
				super.fail("Failed to read from XML file \"" + xmlNormal + "\"");
			}
			finally {
				if (fis != null) {
					FileUtil.closeStream(fis);
					fis = null;
				}
			}
		}
		//--- read from string
		try {
			TransTable retNormal = TransTable.fromXmlString(xmlString);
			// check
			checkTableData(retNormal);
		}
		catch (XmlDomParseException ex) {
			StringBuffer sb = new StringBuffer();
			sb.append("XML Format error : factor[");
			sb.append(ex.getIllegalFactor());
			sb.append("]  XmlDocument[");
			if (ex.getFailedDocument() != null)
				sb.append(ex.getFailedDocument().toString());
			else
				sb.append("null");
			sb.append("]  Element[");
			if (ex.getFailedElement() != null)
				sb.append(ex.getFailedElement().getNodeName() + "(" + ex.getFailedElement().toString() + ")");
			else
				sb.append("null");
			sb.append("]  Attribute[");
			if (ex.getFailedAttributeName() != null)
				sb.append(ex.getFailedAttributeName());
			else
				sb.append("null");
			sb.append("]  Value[");
			if (ex.getFailedValue() != null)
				sb.append(ex.getFailedValue());
			else
				sb.append("null");
			sb.append("]");
			
			super.fail("Failed to read from XML file \"" + xmlNormal + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlNormal + "\"");
		}
	}

	/**
	 * {@link exalge2.TransTable#toCsvString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToCsvString() {
		TransTable table = createTransTable();
		String retString = null;

		try {
			retString = table.toCsvString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to CSV String.");
		}
		
		// check
		try {
			TransTable ret1 = TransTable.fromCsvString(retString);
			checkTableData(ret1);
		}
		catch (CsvFormatException ex) {
			System.out.println("CSV Format error : factor["
					+ ex.getIllegalFactor() + "]  line["
					+ ex.getLineNumber() + "]  column["
					+ ex.getColumnNumber() + "]");
			ex.printStackTrace();
			super.fail("Failed to read from CSV String.");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV String.");
		}
	}

	/**
	 * {@link exalge2.TransTable#toXmlString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToXmlString() {
		TransTable table = createTransTable();
		String retString = null;

		try {
			retString = table.toXmlString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to XML String.");
		}
		
		// check
		try {
			TransTable ret1 = TransTable.fromXmlString(retString);
			checkTableData(ret1);
		}
		catch (XmlDomParseException ex) {
			StringBuffer sb = new StringBuffer();
			sb.append("XML Format error : factor[");
			sb.append(ex.getIllegalFactor());
			sb.append("]  XmlDocument[");
			if (ex.getFailedDocument() != null)
				sb.append(ex.getFailedDocument().toString());
			else
				sb.append("null");
			sb.append("]  Element[");
			if (ex.getFailedElement() != null)
				sb.append(ex.getFailedElement().getNodeName() + "(" + ex.getFailedElement().toString() + ")");
			else
				sb.append("null");
			sb.append("]  Attribute[");
			if (ex.getFailedAttributeName() != null)
				sb.append(ex.getFailedAttributeName());
			else
				sb.append("null");
			sb.append("]  Value[");
			if (ex.getFailedValue() != null)
				sb.append(ex.getFailedValue());
			else
				sb.append("null");
			sb.append("]");
			
			super.fail("Failed to read from XML String.");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML String.");
		}
	}

}
