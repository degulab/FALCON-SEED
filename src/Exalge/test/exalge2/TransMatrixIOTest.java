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
import java.math.BigDecimal;
import java.util.Iterator;

import junit.framework.TestCase;
import exalge2.io.FileUtil;
import exalge2.io.csv.CsvFormatException;
import exalge2.io.xml.XmlDomParseException;

public class TransMatrixIOTest extends TestCase {
	static private final String patAppleYen	= "りんご-*-円-*-*";
	static private final String patOrangeYen	= "みかん-*-円-*-*";
	static private final String patBananaYen	= "ばなな-*-円-*-*";
	
	static private final String patFruit		= "果物-*-*-*-*";
	
	static private final String patTuna		= "まぐろ-*-*-*-*";
	static private final String patBonito		= "かつお-*-*-*-*";
	
	static private final String patFish		= "魚-*-*-*-*";
	
	static private final String patMelonYen	= "メロン-*-円-*-*";
	static private final String patMangoYen	= "マンゴー-*-円-*-*";
	static private final String patExpFruit	= "高級果物-*-*-*-*";

	/*---
	private ExBasePattern bpAppleYen;
	private ExBasePattern bpOrangeYen;
	private ExBasePattern bpBananaYen;
	private ExBasePattern bpFruit;
	
	private ExBasePattern bpTuna;
	private ExBasePattern bpBonito;
	private ExBasePattern bpFish;
	---*/
	
	
	static private final String UTF8 = "UTF-8";
	
	static private final Object[][] seqFruitData = new Object[][]{
		{new BigDecimal(1),	new ExBasePattern(patAppleYen)},
		{new BigDecimal(1),	new ExBasePattern(patOrangeYen)},
		{new BigDecimal(1),	new ExBasePattern(patBananaYen)}
	};
	
	static private final Object[][] seqExpFruitData = new Object[][]{
		{new BigDecimal("0.3"),	new ExBasePattern(patMelonYen)},
		{new BigDecimal("0.7"),	new ExBasePattern(patMangoYen)}
	};
	
	static private final Object[][] seqFishData = new Object[][]{
		{new BigDecimal("0.5"),	new ExBasePattern(patTuna)},
		{new BigDecimal("0.5"),	new ExBasePattern(patBonito)}
	};
	
	static private final Object[][] seqData = new Object[][]{
		{new ExBasePattern(patFruit),		seqFruitData},
		{new ExBasePattern(patExpFruit),	seqExpFruitData},
		{new ExBasePattern(patFish),		seqFishData}
	};
	
	static private TransDivideRatios createTransDivideRatios(Object[][] matrixData) {
		TransDivideRatios tdr = new TransDivideRatios();
		for (int i = 0; i < matrixData.length; i++) {
			BigDecimal val = (BigDecimal)matrixData[i][0];
			ExBasePattern pat = (ExBasePattern)matrixData[i][1];
			tdr.put(pat, val);
		}
		tdr.updateTotalRatio();
		return tdr;
	}
	
	static public TransMatrix createTransMatrix() {
		// create TransMatrix
		TransMatrix matrix = new TransMatrix();
		for (int i = 0; i < seqData.length; i++) {
			ExBasePattern patFrom = (ExBasePattern)seqData[i][0];
			Object[][] mdata = (Object[][])seqData[i][1];
			TransDivideRatios tdr = createTransDivideRatios(mdata);
			matrix.put(patFrom, tdr);
		}
		return matrix;
	}
	
	static public void checkMatrixData(TransMatrix targetMatrix) {
		// Check data
		assertEquals(seqData.length, targetMatrix.size());
		for (int i = 0; i < seqData.length; i++) {
			ExBasePattern patFrom = (ExBasePattern)seqData[i][0];
			Object[][] mdata = (Object[][])seqData[i][1];
			//--- check TransFrom patterns
			TransDivideRatios tdr = targetMatrix.getTransTo(patFrom);
			assertNotNull(tdr);
			//--- check TransDivideRatios
			assertEquals(mdata.length, tdr.size());
			BigDecimal totalRatio = BigDecimal.ZERO;
			for (int r = 0; r < mdata.length; r++) {
				BigDecimal ratio = (BigDecimal)mdata[r][0];
				totalRatio = totalRatio.add(ratio);
				ExBasePattern patTo = (ExBasePattern)mdata[r][1];
				assertTrue(tdr.containsPattern(patTo));
				assertEquals(0, tdr.getRatio(patTo).compareTo(ratio));
			}
			assertEquals(0, tdr.getTotalRatio().compareTo(totalRatio));
		}
		
		// Check sequence
		int idx = 0;
		Iterator<ExBasePattern> it = targetMatrix.transFromPatterns().iterator();
		while (it.hasNext()) {
			ExBasePattern fFrom = it.next();
			TransDivideRatios tdr = targetMatrix.getTransTo(fFrom);
			ExBasePattern sFrom = (ExBasePattern)seqData[idx][0];
			Object[][] mdata = (Object[][])seqData[idx][1];
			idx++;
			//--- check TransFrom sequence
			assertTrue(sFrom.equals(fFrom));
			//--- check TransTo sequence
			int midx = 0;
			Iterator<ExBasePattern> mit = tdr.patterns().iterator();
			while (mit.hasNext()) {
				ExBasePattern fTo = mit.next();
				BigDecimal fRatio = tdr.getRatio(fTo);
				BigDecimal sRatio = (BigDecimal)mdata[midx][0];
				ExBasePattern sTo = (ExBasePattern)mdata[midx][1];
				midx++;
				assertTrue(sTo.equals(fTo));
				assertTrue(0 == sRatio.compareTo(fRatio));
			}
		}
	}
	
	static public void printMatrixData(TransMatrix targetMatrix, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("(TransMatrix) = ");
		Iterator<ExBasePattern> it = targetMatrix.transFromPatterns().iterator();
		while (it.hasNext()) {
			ExBasePattern patFrom = it.next();
			sb.append("\n    F[");
			sb.append(patFrom.toString());
			sb.append("]");
			
			TransDivideRatios tdr = targetMatrix.getTransTo(patFrom);
			Iterator<ExBasePattern> rit = tdr.patterns().iterator();
			while (rit.hasNext()) {
				ExBasePattern patTo = rit.next();
				BigDecimal ratio = tdr.getRatio(patTo);
				sb.append("\n        --> T[");
				sb.append(patTo.toString());
				sb.append("]  R[");
				sb.append(ratio.toPlainString());
				sb.append("]");
			}
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
		
		bpTuna		= new ExBasePattern(patTuna);
		bpBonito	= new ExBasePattern(patBonito);
		bpFish		= new ExBasePattern(patFish);
		---*/
	}

	/**
	 * {@link exalge2.TransMatrix#fromCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVFile() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalTransMatrix.csv";
		try {
			TransMatrix retNormal = TransMatrix.fromCSV(new File(csvNormal));
			printMatrixData(retNormal, "testFromCSVFile - " + csvNormal);
			// check
			checkMatrixData(retNormal);
			/*---
			assertEquals(2, retNormal.size());
			assertNotNull(retNormal.getTransTo(bpFruit));
			assertNotNull(retNormal.getTransTo(bpFish));
			assertTrue(retNormal.isTotalRatioUsed());
			//--- check Fruit
			TransDivideRatios tdrFruit = retNormal.getTransTo(bpFruit);
			assertEquals(3, tdrFruit.size());
			assertEquals(0, tdrFruit.getRatio(bpAppleYen).compareTo(new BigDecimal(1)));
			assertEquals(0, tdrFruit.getRatio(bpOrangeYen).compareTo(new BigDecimal(1)));
			assertEquals(0, tdrFruit.getRatio(bpBananaYen).compareTo(new BigDecimal(1)));
			assertEquals(0, tdrFruit.getTotalRatio().compareTo(new BigDecimal(3)));
			//--- check Fish
			TransDivideRatios tdrFish = retNormal.getTransTo(bpFish);
			assertEquals(2, tdrFish.size());
			assertEquals(0, tdrFish.getRatio(bpTuna).compareTo(new BigDecimal(0.5)));
			assertEquals(0, tdrFish.getRatio(bpBonito).compareTo(new BigDecimal(0.5)));
			assertEquals(0, tdrFish.getTotalRatio().compareTo(new BigDecimal(1)));
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
	 * {@link exalge2.TransMatrix#fromCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromCSVFileString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalTransMatrix_UTF8.csv";
		try {
			TransMatrix retNormal = TransMatrix.fromCSV(new File(csvNormal), UTF8);
			// check
			checkMatrixData(retNormal);
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
	 * {@link exalge2.TransMatrix#toCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToCSVFile() {
		/*---
		TransDivideRatios tdrFruit = new TransDivideRatios();
		tdrFruit.put(bpAppleYen, new BigDecimal(1));
		tdrFruit.put(bpOrangeYen, new BigDecimal(1));
		tdrFruit.put(bpBananaYen, new BigDecimal(1));
		tdrFruit.updateTotalRatio();
		assertEquals(3, tdrFruit.size());
		
		TransDivideRatios tdrFish = new TransDivideRatios();
		tdrFish.put(bpTuna, new BigDecimal(0.5));
		tdrFish.put(bpBonito, new BigDecimal(0.5));
		tdrFish.updateTotalRatio();
		assertEquals(2, tdrFish.size());
		
		TransMatrix matrix = new TransMatrix();
		matrix.put(bpFruit, tdrFruit);
		matrix.put(bpFish, tdrFish);
		assertEquals(2, matrix.size());
		---*/
		TransMatrix matrix = createTransMatrix();
		printMatrixData(matrix, "testToCSVFile - matrix");
		
		final String csvOutput = "testdata/CSV/OutputTransMatrix.csv";

		try {
			matrix.toCSV(new File(csvOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			TransMatrix ret1 = TransMatrix.fromCSV(new File(csvOutput));
			// check
			checkMatrixData(ret1);
			/*---
			assertEquals(2, ret1.size());
			assertNotNull(ret1.getTransTo(bpFruit));
			assertNotNull(ret1.getTransTo(bpFish));
			assertTrue(ret1.isTotalRatioUsed());
			//--- check Fruit
			TransDivideRatios retFruit = ret1.getTransTo(bpFruit);
			assertEquals(3, retFruit.size());
			assertEquals(0, retFruit.getRatio(bpAppleYen).compareTo(new BigDecimal(1)));
			assertEquals(0, retFruit.getRatio(bpOrangeYen).compareTo(new BigDecimal(1)));
			assertEquals(0, retFruit.getRatio(bpBananaYen).compareTo(new BigDecimal(1)));
			assertEquals(0, retFruit.getTotalRatio().compareTo(new BigDecimal(3)));
			//--- check Fish
			TransDivideRatios retFish = ret1.getTransTo(bpFish);
			assertEquals(2, retFish.size());
			assertEquals(0, retFish.getRatio(bpTuna).compareTo(new BigDecimal(0.5)));
			assertEquals(0, retFish.getRatio(bpBonito).compareTo(new BigDecimal(0.5)));
			assertEquals(0, retFish.getTotalRatio().compareTo(new BigDecimal(1)));
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
	 * {@link exalge2.TransMatrix#toCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testToCSVFileString() {
		TransMatrix matrix = createTransMatrix();
		
		final String csvOutput = "testdata/CSV/OutputTransMatrix_UTF8.csv";

		try {
			matrix.toCSV(new File(csvOutput), UTF8);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			TransMatrix ret1 = TransMatrix.fromCSV(new File(csvOutput), UTF8);
			// check
			checkMatrixData(ret1);
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
	 * {@link exalge2.TransMatrix#fromXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromXMLFile() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalTransMatrix.xml";
		try {
			TransMatrix retNormal = TransMatrix.fromXML(new File(xmlNormal));
			// check
			checkMatrixData(retNormal);
			/*---
			assertEquals(2, retNormal.size());
			assertNotNull(retNormal.getTransTo(bpFruit));
			assertNotNull(retNormal.getTransTo(bpFish));
			assertTrue(retNormal.isTotalRatioUsed());
			//--- check Fruit
			TransDivideRatios tdrFruit = retNormal.getTransTo(bpFruit);
			assertEquals(3, tdrFruit.size());
			assertEquals(0, tdrFruit.getRatio(bpAppleYen).compareTo(new BigDecimal(1)));
			assertEquals(0, tdrFruit.getRatio(bpOrangeYen).compareTo(new BigDecimal(1)));
			assertEquals(0, tdrFruit.getRatio(bpBananaYen).compareTo(new BigDecimal(1)));
			assertEquals(0, tdrFruit.getTotalRatio().compareTo(new BigDecimal(3)));
			//--- check Fish
			TransDivideRatios tdrFish = retNormal.getTransTo(bpFish);
			assertEquals(2, tdrFish.size());
			assertEquals(0, tdrFish.getRatio(bpTuna).compareTo(new BigDecimal(0.5)));
			assertEquals(0, tdrFish.getRatio(bpBonito).compareTo(new BigDecimal(0.5)));
			assertEquals(0, tdrFish.getTotalRatio().compareTo(new BigDecimal(1)));
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
	 * {@link exalge2.TransMatrix#toXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToXMLFile() {
		/*---
		TransDivideRatios tdrFruit = new TransDivideRatios();
		tdrFruit.put(bpAppleYen, new BigDecimal(1));
		tdrFruit.put(bpOrangeYen, new BigDecimal(1));
		tdrFruit.put(bpBananaYen, new BigDecimal(1));
		tdrFruit.updateTotalRatio();
		assertEquals(3, tdrFruit.size());
		
		TransDivideRatios tdrFish = new TransDivideRatios();
		tdrFish.put(bpTuna, new BigDecimal(0.5));
		tdrFish.put(bpBonito, new BigDecimal(0.5));
		tdrFish.updateTotalRatio();
		assertEquals(2, tdrFish.size());
		
		TransMatrix matrix = new TransMatrix();
		matrix.put(bpFruit, tdrFruit);
		matrix.put(bpFish, tdrFish);
		assertEquals(2, matrix.size());
		---*/
		TransMatrix matrix = createTransMatrix();
		
		final String xmlOutput = "testdata/XML/OutputTransMatrix.xml";

		try {
			matrix.toXML(new File(xmlOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to XML file \"" + xmlOutput + "\"");
		}
		
		// check
		try {
			TransMatrix ret1 = TransMatrix.fromXML(new File(xmlOutput));
			// check
			checkMatrixData(ret1);
			/*---
			assertEquals(2, ret1.size());
			assertNotNull(ret1.getTransTo(bpFruit));
			assertNotNull(ret1.getTransTo(bpFish));
			assertTrue(ret1.isTotalRatioUsed());
			//--- check Fruit
			TransDivideRatios retFruit = ret1.getTransTo(bpFruit);
			assertEquals(3, retFruit.size());
			assertEquals(0, retFruit.getRatio(bpAppleYen).compareTo(new BigDecimal(1)));
			assertEquals(0, retFruit.getRatio(bpOrangeYen).compareTo(new BigDecimal(1)));
			assertEquals(0, retFruit.getRatio(bpBananaYen).compareTo(new BigDecimal(1)));
			assertEquals(0, retFruit.getTotalRatio().compareTo(new BigDecimal(3)));
			//--- check Fish
			TransDivideRatios retFish = ret1.getTransTo(bpFish);
			assertEquals(2, retFish.size());
			assertEquals(0, retFish.getRatio(bpTuna).compareTo(new BigDecimal(0.5)));
			assertEquals(0, retFish.getRatio(bpBonito).compareTo(new BigDecimal(0.5)));
			assertEquals(0, retFish.getTotalRatio().compareTo(new BigDecimal(1)));
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
	 * {@link exalge2.TransMatrix#fromXML(exalge2.io.xml.XmlDocument)} のためのテスト・メソッド。
	 */
	/*---
	public void testFromXMLXmlDocument() {
		fail("まだ実装されていません。");
	}
	---*/

	/**
	 * {@link exalge2.TransMatrix#toXML()} のためのテスト・メソッド。
	 */
	/*---
	public void testToXML() {
		fail("まだ実装されていません。");
	}
	---*/
	
	/**
	 * {@link exalge2.TransMatrix#fromCsvString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromCsvStringString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalTransMatrix.csv";
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
			TransMatrix retNormal = TransMatrix.fromCsvString(csvString);
			// check
			checkMatrixData(retNormal);
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
	 * {@link exalge2.TransMatrix#fromXmlString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromXmlStringString() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalTransMatrix.xml";
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
			TransMatrix retNormal = TransMatrix.fromXmlString(xmlString);
			// check
			checkMatrixData(retNormal);
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
	 * {@link exalge2.TransMatrix#toCsvString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToCsvString() {
		TransMatrix table = createTransMatrix();
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
			TransMatrix ret1 = TransMatrix.fromCsvString(retString);
			checkMatrixData(ret1);
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
	 * {@link exalge2.TransMatrix#toXmlString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToXmlString() {
		TransMatrix table = createTransMatrix();
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
			TransMatrix ret1 = TransMatrix.fromXmlString(retString);
			checkMatrixData(ret1);
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
