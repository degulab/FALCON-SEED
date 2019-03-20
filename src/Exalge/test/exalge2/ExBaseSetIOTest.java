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
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;
import exalge2.io.FileUtil;
import exalge2.io.csv.CsvFormatException;
import exalge2.io.xml.XmlDomParseException;

public class ExBaseSetIOTest extends TestCase {
	
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

	
	static private final String UTF8 = "UTF-8";
	
	static private final ExBase[] fileData = new ExBase[]{
		new ExBase(nohatAppleYen),
		new ExBase(nohatOrangeYen),
		new ExBase(nohatBananaYen),
		new ExBase(nohatMelonYen),
		new ExBase(hatCashYen),
		new ExBase(nohatAppleYen),
		new ExBase(nohatOrangeYen),
		new ExBase(nohatBananaYen),
		new ExBase(hatMelonYen),
		new ExBase(hatCashYen),
		new ExBase(hatTunaYen),
		new ExBase(hatBonitoYen),
		new ExBase(nohatCashYenTS)
	};
	
	static private final ExBase[] seqData = new ExBase[]{
		new ExBase(nohatAppleYen),
		new ExBase(nohatOrangeYen),
		new ExBase(nohatBananaYen),
		new ExBase(nohatMelonYen),
		new ExBase(hatCashYen),
		new ExBase(hatMelonYen),
		new ExBase(hatTunaYen),
		new ExBase(hatBonitoYen),
		new ExBase(nohatCashYenTS)
	};
	
	static public ExBaseSet createBaseSet() {
		ExBaseSet newSet = new ExBaseSet(Arrays.asList(fileData));
		return newSet;
	}
	
	static public void checkSetData(ExBaseSet targetSet) {
		// check data
		assertEquals(seqData.length, targetSet.size());
		for (int i = 0; i < seqData.length; i++) {
			assertTrue(targetSet.contains(seqData[i]));
		}
		// check sequence
		int idx = 0;
		Iterator<ExBase> it = targetSet.iterator();
		while (it.hasNext()) {
			ExBase fbase = it.next();
			ExBase sbase = seqData[idx++];
			assertTrue(sbase.equals(fbase));
		}
	}


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.ExBaseSet#fromCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVFile() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExBaseSet.csv";
		try {
			ExBaseSet retNormal = ExBaseSet.fromCSV(new File(csvNormal));
			// check
			checkSetData(retNormal);
			/*---
			assertEquals(7, retNormal.size());
			assertTrue(retNormal.contains(new ExBase(nohatAppleYen)));
			assertTrue(retNormal.contains(new ExBase(nohatOrangeYen)));
			assertTrue(retNormal.contains(new ExBase(nohatBananaYen)));
			assertTrue(retNormal.contains(new ExBase(hatCashYen)));
			assertTrue(retNormal.contains(new ExBase(hatTunaYen)));
			assertTrue(retNormal.contains(new ExBase(hatBonitoYen)));
			assertTrue(retNormal.contains(new ExBase(nohatCashYenTS)));
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
	 * {@link exalge2.ExBaseSet#fromCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromCSVFileString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExBaseSet_UTF8.csv";
		try {
			ExBaseSet retNormal = ExBaseSet.fromCSV(new File(csvNormal), UTF8);
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
	 * {@link exalge2.ExBaseSet#toCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToCSVFile() {
		/*---
		ExBaseSet data = new ExBaseSet();
		data.add(new ExBase(nohatAppleYen));
		data.add(new ExBase(nohatOrangeYen));
		data.add(new ExBase(nohatBananaYen));
		data.add(new ExBase(hatCashYen));
		data.add(new ExBase(hatTunaYen));
		data.add(new ExBase(hatBonitoYen));
		data.add(new ExBase(nohatCashYenTS));
		---*/
		ExBaseSet data = createBaseSet();
		
		final String csvOutput = "testdata/CSV/OutputExBaseSet.csv";

		try {
			data.toCSV(new File(csvOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			ExBaseSet ret1 = ExBaseSet.fromCSV(new File(csvOutput));
			assertEquals(data.size(), ret1.size());
			assertTrue(data.containsAll(ret1));
			assertTrue(data.equals(ret1));
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
	 * {@link exalge2.ExBaseSet#toCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testToCSVFileString() {
		ExBaseSet data = createBaseSet();
		
		final String csvOutput = "testdata/CSV/OutputExBaseSet_UTF8.csv";

		try {
			data.toCSV(new File(csvOutput), UTF8);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			ExBaseSet ret1 = ExBaseSet.fromCSV(new File(csvOutput), UTF8);
			assertEquals(data.size(), ret1.size());
			assertTrue(data.containsAll(ret1));
			assertTrue(data.equals(ret1));
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
	 * {@link exalge2.ExBaseSet#fromXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromXMLFile() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalExBaseSet.xml";
		try {
			ExBaseSet retNormal = ExBaseSet.fromXML(new File(xmlNormal));
			// check
			checkSetData(retNormal);
			/*---
			assertEquals(7, retNormal.size());
			assertTrue(retNormal.contains(new ExBase(nohatAppleYen)));
			assertTrue(retNormal.contains(new ExBase(nohatOrangeYen)));
			assertTrue(retNormal.contains(new ExBase(nohatBananaYen)));
			assertTrue(retNormal.contains(new ExBase(hatCashYen)));
			assertTrue(retNormal.contains(new ExBase(hatTunaYen)));
			assertTrue(retNormal.contains(new ExBase(hatBonitoYen)));
			assertTrue(retNormal.contains(new ExBase(nohatCashYenTS)));
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
	 * {@link exalge2.ExBaseSet#toXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToXMLFile() {
		/*---
		ExBaseSet data = new ExBaseSet();
		data.add(new ExBase(nohatAppleYen));
		data.add(new ExBase(nohatOrangeYen));
		data.add(new ExBase(nohatBananaYen));
		data.add(new ExBase(hatCashYen));
		data.add(new ExBase(hatTunaYen));
		data.add(new ExBase(hatBonitoYen));
		data.add(new ExBase(nohatCashYenTS));
		---*/
		ExBaseSet data = createBaseSet();
		
		final String xmlOutput = "testdata/XML/OutputExBaseSet.xml";

		try {
			data.toXML(new File(xmlOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to XML file \"" + xmlOutput + "\"");
		}
		
		// check
		try {
			ExBaseSet ret1 = ExBaseSet.fromXML(new File(xmlOutput));
			assertEquals(data.size(), ret1.size());
			assertTrue(data.containsAll(ret1));
			assertTrue(data.equals(ret1));
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
			
			super.fail("Failed to read from XML file \"" + xmlOutput + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlOutput + "\"");
		}
	}

	/**
	 * {@link exalge2.ExBaseSet#fromXML(exalge2.io.xml.XmlDocument)} のためのテスト・メソッド。
	 */
	/*---
	public void testFromXMLXmlDocument() {
		fail("まだ実装されていません。");
	}
	---*/

	/**
	 * {@link exalge2.ExBaseSet#toXML()} のためのテスト・メソッド。
	 */
	/*---
	public void testToXML() {
		fail("まだ実装されていません。");
	}
	---*/
	
	/**
	 * {@link exalge2.ExBaseSet#fromCsvString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromCsvStringString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExBaseSet.csv";
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
			ExBaseSet retNormal = ExBaseSet.fromCsvString(csvString);
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
	 * {@link exalge2.ExBaseSet#fromXmlString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromXmlStringString() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalExBaseSet.xml";
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
			ExBaseSet retNormal = ExBaseSet.fromXmlString(xmlString);
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
	 * {@link exalge2.ExBaseSet#toCsvString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToCsvString() {
		ExBaseSet data = createBaseSet();
		String retString = null;

		try {
			retString = data.toCsvString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to CSV String.");
		}
		
		// check
		try {
			ExBaseSet ret1 = ExBaseSet.fromCsvString(retString);
			assertEquals(data.size(), ret1.size());
			assertTrue(data.containsAll(ret1));
			assertTrue(data.equals(ret1));
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
	 * {@link exalge2.ExBaseSet#toXmlString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToXmlString() {
		ExBaseSet data = createBaseSet();
		String retString = null;

		try {
			retString = data.toXmlString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to XML String.");
		}
		
		// check
		try {
			ExBaseSet ret1 = ExBaseSet.fromXmlString(retString);
			assertEquals(data.size(), ret1.size());
			assertTrue(data.containsAll(ret1));
			assertTrue(data.equals(ret1));
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
