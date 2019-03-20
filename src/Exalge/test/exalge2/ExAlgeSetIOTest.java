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

public class ExAlgeSetIOTest extends TestCase {
	static private final String nohatAppleYen		= "りんご-NO_HAT-円-#-#";
	//static private final String hatAppleYen		= "りんご-HAT-円-#-#";
	static private final String nohatOrangeYen	= "みかん-NO_HAT-円-#-#";
	//static private final String hatOrangeYen		= "みかん-HAT-円-#-#";
	static private final String nohatBananaYen	= "ばなな-NO_HAT-円-#-#";
	//static private final String hatBananaYen		= "ばなな-HAT-円-#-#";
	static private final String nohatMelonYen		= "メロン-NO_HAT-円-#-#";
	static private final String hatMelonYen		= "メロン-HAT-円-#-#";
	//static private final String nohatMangoYen		= "マンゴー-NO_HAT-円-#-#";
	//static private final String hatMangoYen		= "マンゴー-HAT-円-#-#";
	
	//static private final String nohatTuanYen		= "まぐろ-NO_HAT-円-Y2000-Ａ社";
	static private final String hatTunaYen		= "まぐろ-HAT-円-Y2000-Ａ社";
	//static private final String nohatBonitoYen	= "かつお-NO_HAT-円-Y2001-Ａ社";
	static private final String hatBonitoYen		= "かつお-HAT-円-Y2001-Ａ社";
	
	//static private final String nohatCashYen		= "現金-NO_HAT-円-#-#";
	static private final String hatCashYen		= "現金-HAT-円-#-#";
	
	static private final String nohatCashYenTS	= "現金-NO_HAT-円-Y2002-Ｂ社";
	
	static private final String nohatNullYen		= "NULL基底-NO_HAT-円-Y2010-#";
	static private final String hatNullYen		= "NULL基底-HAT-円-Y2010-#";

	//----- Test data -----
	static private final Object[] fileElemData1 = new Object[]{
		new ExBase(nohatAppleYen),	new BigDecimal(10),
		new ExBase(nohatOrangeYen),	new BigDecimal(20),
		new ExBase(nohatBananaYen),	new BigDecimal(30),
		new ExBase(nohatMelonYen),	BigDecimal.ZERO,
		new ExBase(hatCashYen),		new BigDecimal(60)
	};
	
	static private final Object[] fileElemData2 = new Object[]{
		new ExBase(nohatAppleYen),	new BigDecimal(1),
		new ExBase(nohatOrangeYen),	new BigDecimal(2),
		new ExBase(nohatBananaYen),	new BigDecimal(3),
		new ExBase(hatMelonYen),	BigDecimal.ZERO,
		new ExBase(hatCashYen),		new BigDecimal(6)
	};
	
	static private final Object[] fileElemData3 = new Object[]{
		new ExBase(hatTunaYen),		new BigDecimal(100),
		new ExBase(hatBonitoYen),	new BigDecimal(200),
		new ExBase(nohatCashYenTS),	new BigDecimal(300)
	};
	
	static private final Object[] fileElemData4 = new Object[]{
		new ExBase(hatNullYen),		null,
		new ExBase(nohatNullYen),	null,
	};
	
	static private final Object[] seqData = new Object[]{
		new ExBase(nohatAppleYen),	new BigDecimal(11),
		new ExBase(nohatOrangeYen),	new BigDecimal(22),
		new ExBase(nohatBananaYen),	new BigDecimal(33),
		new ExBase(nohatMelonYen),	BigDecimal.ZERO,
		new ExBase(hatCashYen),		new BigDecimal(66),
		new ExBase(hatMelonYen),	BigDecimal.ZERO,
		new ExBase(hatTunaYen),		new BigDecimal(100),
		new ExBase(hatBonitoYen),	new BigDecimal(200),
		new ExBase(nohatCashYenTS),	new BigDecimal(300),
		new ExBase(hatNullYen),		null,
		new ExBase(nohatNullYen),	null,
	};
	
	static private final String UTF8 = "UTF-8";
	
	private ExAlgeSet algeSet = null;
	
	static public ExAlgeSet createExAlgeSet() {
		Exalge alge1 = new Exalge(fileElemData1);
		Exalge alge2 = new Exalge(fileElemData2);
		Exalge alge3 = new Exalge(fileElemData3);
		Exalge alge4 = new Exalge(fileElemData4);
		
		ExAlgeSet newSet = new ExAlgeSet();
		newSet.add(alge1);
		newSet.add(alge2);
		newSet.add(alge3);
		newSet.add(alge4);
		
		return newSet;
	}
	
	static private void checkAlgeData(Exalge targetData) {
		// check
		assertEquals(0, targetData.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(11)));
		assertEquals(0, targetData.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(22)));
		assertEquals(0, targetData.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(33)));
		assertEquals(0, targetData.get(new ExBase(nohatMelonYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, targetData.get(new ExBase(hatMelonYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, targetData.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(66)));
		assertEquals(0, targetData.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, targetData.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, targetData.get(new ExBase(nohatCashYenTS)).compareTo(new BigDecimal(300)));
		assertNull(targetData.get(new ExBase(hatNullYen)));
		assertNull(targetData.get(new ExBase(nohatNullYen)));
		//assertEquals(0, targetData.norm().compareTo(new BigDecimal(732)));
		// check sequence
		checkElemSequence(targetData, seqData);
		/*---
		int idx = 0;
		Iterator<ExBase> it = targetData.getBases().iterator();
		while (it.hasNext()) {
			ExBase fbase = it.next();
			BigDecimal fval = targetData.get(fbase);
			ExBase sbase = (ExBase)seqData[idx++];
			BigDecimal sval = (BigDecimal)seqData[idx++];
			assertTrue(sbase.equals(fbase));
			assertTrue(0 == sval.compareTo(fval));
		}
		---*/
	}
	
	static public void checkElemSequence(Exalge targetAlge, Object[] dataSequence) {
		int idx = 0;
		Iterator<ExBase> it = targetAlge.getBases().iterator();
		while (it.hasNext()) {
			ExBase fbase = it.next();
			BigDecimal fval = targetAlge.get(fbase);
			ExBase sbase = (ExBase)dataSequence[idx++];
			BigDecimal sval = (BigDecimal)dataSequence[idx++];
			assertTrue(sbase.equals(fbase));
			assertTrue(sval==fval || (sval!=null && fval!=null && 0==sval.compareTo(fval)));
			//assertTrue(0 == sval.compareTo(fval));
		}
	}
	
	static public void checkSetData(ExAlgeSet targetSet) {
		assertEquals(4, targetSet.size());
		Exalge alge;
		//--- check [0]
		alge = targetSet.get(0);
		assertEquals(0, alge.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, alge.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, alge.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, alge.get(new ExBase(nohatMelonYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, alge.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(60)));
		assertEquals(0, alge.norm().compareTo(new BigDecimal(120)));
		checkElemSequence(alge, fileElemData1);
		//--- check [1]
		alge = targetSet.get(1);
		assertEquals(0, alge.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(1)));
		assertEquals(0, alge.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(2)));
		assertEquals(0, alge.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(3)));
		assertEquals(0, alge.get(new ExBase(hatMelonYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, alge.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(6)));
		assertEquals(0, alge.norm().compareTo(new BigDecimal(12)));
		checkElemSequence(alge, fileElemData2);
		//--- check [2]
		alge = targetSet.get(2);
		assertEquals(0, alge.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, alge.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, alge.get(new ExBase(nohatCashYenTS)).compareTo(new BigDecimal(300)));
		assertEquals(0, alge.norm().compareTo(new BigDecimal(600)));
		checkElemSequence(alge, fileElemData3);
		//--- check [3]
		alge = targetSet.get(3);
		assertNull(alge.get(new ExBase(hatNullYen)));
		assertNull(alge.get(new ExBase(nohatNullYen)));
		checkElemSequence(alge, fileElemData4);
		//--- total check
		Exalge sumAlge = targetSet.sum();
		checkAlgeData(sumAlge);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		algeSet = new ExAlgeSet();
		algeSet.add(new Exalge(fileElemData1));
		algeSet.add(new Exalge(fileElemData2));
		algeSet.add(new Exalge(fileElemData3));
		algeSet.add(new Exalge(fileElemData4));
	}

	/**
	 * {@link exalge2.ExAlgeSet#fromCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVFile() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExalgeSet.csv";
		try {
			ExAlgeSet retNormal = ExAlgeSet.fromCSV(new File(csvNormal));
			// check
			checkSetData(retNormal);
			/*---
			assertEquals(3, retNormal.size());
			Exalge alge;
			//--- check [0]
			alge = retNormal.get(0);
			assertEquals(0, alge.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(10)));
			assertEquals(0, alge.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(20)));
			assertEquals(0, alge.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(30)));
			assertEquals(0, alge.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(60)));
			assertEquals(0, alge.norm().compareTo(new BigDecimal(120)));
			//--- check [1]
			alge = retNormal.get(1);
			assertEquals(0, alge.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(1)));
			assertEquals(0, alge.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(2)));
			assertEquals(0, alge.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(3)));
			assertEquals(0, alge.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(6)));
			assertEquals(0, alge.norm().compareTo(new BigDecimal(12)));
			//--- check [2]
			alge = retNormal.get(2);
			assertEquals(0, alge.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(100)));
			assertEquals(0, alge.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(200)));
			assertEquals(0, alge.get(new ExBase(nohatCashYenTS)).compareTo(new BigDecimal(300)));
			assertEquals(0, alge.norm().compareTo(new BigDecimal(600)));
			//--- total check
			assertEquals(0, retNormal.sum().norm().compareTo(new BigDecimal(732)));
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
	 * {@link exalge2.ExAlgeSet#fromCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromCSVFileString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExalgeSet_UTF8.csv";
		try {
			ExAlgeSet retNormal = ExAlgeSet.fromCSV(new File(csvNormal), UTF8);
			// check
			checkSetData(retNormal);
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
	 * {@link exalge2.ExAlgeSet#toCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToCSVFile() {
		/*---
		Exalge alge1 = new Exalge(fileElemData1);
		Exalge alge2 = new Exalge(fileElemData2);
		Exalge alge3 = new Exalge(fileElemData3);
		
		ExAlgeSet data = new ExAlgeSet();
		data.add(alge1);
		data.add(alge2);
		data.add(alge3);
		---*/
		ExAlgeSet data = createExAlgeSet();
		
		final String csvOutput = "testdata/CSV/OutputExalgeSet.csv";

		try {
			data.toCSV(new File(csvOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			ExAlgeSet ret1 = ExAlgeSet.fromCSV(new File(csvOutput));
			checkSetData(ret1);
			/*---
			assertEquals(3, ret1.size());
			assertTrue(alge1.isEqualValues(ret1.get(0)));
			assertTrue(alge2.isEqualValues(ret1.get(1)));
			assertTrue(alge3.isEqualValues(ret1.get(2)));
			//--- total check
			assertEquals(0, ret1.sum().norm().compareTo(new BigDecimal(732)));
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
	 * {@link exalge2.ExAlgeSet#toCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testToCSVFileString() {
		ExAlgeSet data = createExAlgeSet();
		
		final String csvOutput = "testdata/CSV/OutputExalgeSet_UTF8.csv";

		try {
			data.toCSV(new File(csvOutput), UTF8);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			ExAlgeSet ret1 = ExAlgeSet.fromCSV(new File(csvOutput), UTF8);
			checkSetData(ret1);
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
	 * {@link exalge2.ExAlgeSet#fromXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromXMLFile() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalExalgeSet.xml";
		try {
			ExAlgeSet retNormal = ExAlgeSet.fromXML(new File(xmlNormal));
			// check
			checkSetData(retNormal);
			/*---
			assertEquals(3, retNormal.size());
			Exalge alge;
			//--- check [0]
			alge = retNormal.get(0);
			assertEquals(0, alge.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(10)));
			assertEquals(0, alge.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(20)));
			assertEquals(0, alge.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(30)));
			assertEquals(0, alge.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(60)));
			assertEquals(0, alge.norm().compareTo(new BigDecimal(120)));
			//--- check [1]
			alge = retNormal.get(1);
			assertEquals(0, alge.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(1)));
			assertEquals(0, alge.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(2)));
			assertEquals(0, alge.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(3)));
			assertEquals(0, alge.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(6)));
			assertEquals(0, alge.norm().compareTo(new BigDecimal(12)));
			//--- check [2]
			alge = retNormal.get(2);
			assertEquals(0, alge.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(100)));
			assertEquals(0, alge.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(200)));
			assertEquals(0, alge.get(new ExBase(nohatCashYenTS)).compareTo(new BigDecimal(300)));
			assertEquals(0, alge.norm().compareTo(new BigDecimal(600)));
			//--- total check
			assertEquals(0, retNormal.sum().norm().compareTo(new BigDecimal(732)));
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
	 * {@link exalge2.ExAlgeSet#toXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToXMLFile() {
		/*---
		Exalge alge1 = new Exalge(fileElemData1);
		Exalge alge2 = new Exalge(fileElemData2);
		Exalge alge3 = new Exalge(fileElemData3);
		
		ExAlgeSet data = new ExAlgeSet();
		data.add(alge1);
		data.add(alge2);
		data.add(alge3);
		---*/
		ExAlgeSet data = createExAlgeSet();
		
		final String xmlOutput = "testdata/XML/OutputExalgeSet.xml";

		try {
			data.toXML(new File(xmlOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to XML file \"" + xmlOutput + "\"");
		}
		
		// check
		try {
			ExAlgeSet ret1 = ExAlgeSet.fromXML(new File(xmlOutput));
			checkSetData(ret1);
			/*---
			assertEquals(3, ret1.size());
			assertTrue(alge1.isEqualValues(ret1.get(0)));
			assertTrue(alge2.isEqualValues(ret1.get(1)));
			assertTrue(alge3.isEqualValues(ret1.get(2)));
			//--- total check
			assertEquals(0, ret1.sum().norm().compareTo(new BigDecimal(732)));
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
	 * {@link exalge2.ExAlgeSet#toXML()} のためのテスト・メソッド。
	 */
	/*---
	public void testToXML() {
		fail("まだ実装されていません。");
	}
	---*/

	/**
	 * {@link exalge2.ExAlgeSet#fromXML(exalge2.io.xml.XmlDocument)} のためのテスト・メソッド。
	 */
	/*---
	public void testFromXMLXmlDocument() {
		fail("まだ実装されていません。");
	}
	---*/
	
	/**
	 * {@link exalge2.ExAlgeSet#fromCsvString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromCsvStringString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExalgeSet.csv";
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
			ExAlgeSet retNormal = ExAlgeSet.fromCsvString(csvString);
			// check
			checkSetData(retNormal);
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
	 * {@link exalge2.ExAlgeSet#fromXmlString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromXmlStringString() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalExalgeSet.xml";
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
			ExAlgeSet retNormal = ExAlgeSet.fromXmlString(xmlString);
			// check
			checkSetData(retNormal);
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
	 * {@link exalge2.ExAlgeSet#toCsvString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToCsvString() {
		ExAlgeSet data1 = createExAlgeSet();
		String retString = null;

		try {
			retString = data1.toCsvString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to CSV String.");
		}
		
		// check
		try {
			ExAlgeSet ret1 = ExAlgeSet.fromCsvString(retString);
			checkSetData(ret1);
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
	 * {@link exalge2.Exalge#toXmlString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToXmlString() {
		ExAlgeSet data1 = createExAlgeSet();
		String retString = null;

		try {
			retString = data1.toXmlString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to XML String.");
		}
		
		// check
		try {
			ExAlgeSet ret1 = ExAlgeSet.fromXmlString(retString);
			checkSetData(ret1);
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
