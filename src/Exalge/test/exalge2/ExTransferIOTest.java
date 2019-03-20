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

import junit.framework.TestCase;
import exalge2.io.FileUtil;
import exalge2.io.csv.CsvFormatException;
import exalge2.io.xml.XmlDomParseException;

/**
 * <code>ExTransfer</code> テスト(I/O のみ)
 * @since 0.982
 */
public class ExTransferIOTest extends TestCase {
	
	static private final String UTF8 = "UTF-8";
	
	static private final String[] illegalCsvFiles = {
		"testdata/CSV/IllegalExTransfer11.csv",
		"testdata/CSV/IllegalExTransfer12.csv",
		"testdata/CSV/IllegalExTransfer13.csv",
		"testdata/CSV/IllegalExTransfer21.csv",
		"testdata/CSV/IllegalExTransfer22.csv",
		"testdata/CSV/IllegalExTransfer23.csv",
		"testdata/CSV/IllegalExTransfer31.csv",
		"testdata/CSV/IllegalExTransfer32.csv",
		"testdata/CSV/IllegalExTransfer33.csv",
		"testdata/CSV/IllegalExTransfer41.csv",
		"testdata/CSV/IllegalExTransfer42.csv",
		"testdata/CSV/IllegalExTransfer43.csv",
		"testdata/CSV/IllegalExTransfer44.csv",
		"testdata/CSV/IllegalExTransfer51.csv",
		"testdata/CSV/IllegalExTransfer52.csv",
		"testdata/CSV/IllegalExTransfer53.csv",
		"testdata/CSV/IllegalExTransfer54.csv",
		"testdata/CSV/IllegalExTransfer61.csv",
	};
	
	static private final String[] illegalXmlFiles = {
		"testdata/XML/IllegalExTransfer21.xml",
		"testdata/XML/IllegalExTransfer22.xml",
		"testdata/XML/IllegalExTransfer23.xml",
		"testdata/XML/IllegalExTransfer31.xml",
		"testdata/XML/IllegalExTransfer32.xml",
		"testdata/XML/IllegalExTransfer33.xml",
		"testdata/XML/IllegalExTransfer41.xml",
		"testdata/XML/IllegalExTransfer42.xml",
		"testdata/XML/IllegalExTransfer43.xml",
		"testdata/XML/IllegalExTransfer44.xml",
		"testdata/XML/IllegalExTransfer51.xml",
		"testdata/XML/IllegalExTransfer52.xml",
		"testdata/XML/IllegalExTransfer53.xml",
		"testdata/XML/IllegalExTransfer54.xml",
		"testdata/XML/IllegalExTransfer61.xml",
	};

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	static public void checkExTransferFileData(ExTransfer trans) {
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * {@link exalge2.ExTransfer#fromCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVFile() {
		final String csvNormal = "testdata/CSV/NormalExTransfer.csv";
		// 正常系
		try {
			ExTransfer retNormal = ExTransfer.fromCSV(new File(csvNormal));
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
		// 異常系
		boolean coughtException;
		for (String fname : illegalCsvFiles) {
			try {
				ExTransfer ret = ExTransfer.fromCSV(new File(fname));
				coughtException = false;
				System.err.println("[Error] ExTransferIOTest:testFromCSVFile(\"" + fname + "\") : no throw exception!");
			}
			catch (CsvFormatException ex) {
				coughtException = true;
				System.out.println("[Check] ExTransferIOTest:testFromCSVFile(\"" + fname + "\") : " + ex);
			}
			catch (Throwable ex) {
				coughtException = false;
				System.err.println("[Error] ExTransferIOTest:testFromCSVFile(\"" + fname + "\") : unknown exception");
				ex.printStackTrace();
			}
			assertTrue(coughtException);
		}
	}

	/**
	 * {@link exalge2.ExTransfer#fromCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromCSVFileString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExTransfer_UTF8.csv";
		try {
			ExTransfer retNormal = ExTransfer.fromCSV(new File(csvNormal), UTF8);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
	 * {@link exalge2.ExTransfer#fromCSV(boolean, java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVBooleanFile() {
		//fail("まだ実装されていません。");
	}

	/**
	 * {@link exalge2.ExTransfer#toCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToCSVFile() {
		final String csvOutput = "testdata/CSV/OutputExTransfer.csv";
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			trans.toCSV(new File(csvOutput));
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromCSV(new File(csvOutput));
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			trans.toCSV(new File(csvOutput));
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromCSV(new File(csvOutput));
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
	 * {@link exalge2.ExTransfer#toCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testToCSVFileString() {
		final String csvOutput = "testdata/CSV/OutputExTransfer_UTF8.csv";
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			trans.toCSV(new File(csvOutput), UTF8);
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromCSV(new File(csvOutput), UTF8);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			trans.toCSV(new File(csvOutput), UTF8);
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromCSV(new File(csvOutput), UTF8);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
	 * {@link exalge2.ExTransfer#fromXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromXMLFile() {
		final String xmlNormal = "testdata/XML/NormalExTransfer.xml";
		// 正常系
		try {
			ExTransfer retNormal = ExTransfer.fromXML(new File(xmlNormal));
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
		// 異常系
		boolean coughtException;
		for (String fname : illegalXmlFiles) {
			try {
				ExTransfer ret = ExTransfer.fromXML(new File(fname));
				coughtException = false;
				System.err.println("[Error] ExTransferIOTest:testFromXMLFile(\"" + fname + "\") : no throw exception!");
			}
			catch (XmlDomParseException ex) {
				coughtException = true;
				System.out.println("[Check] ExTransferIOTest:testFromXMLFile(\"" + fname + "\") : " + ex);
			}
			catch (Throwable ex) {
				coughtException = false;
				System.err.println("[Error] ExTransferIOTest:testFromXMLFile(\"" + fname + "\") : unknown exception");
				ex.printStackTrace();
			}
			assertTrue(coughtException);
		}
	}

	/**
	 * {@link exalge2.ExTransfer#toXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToXMLFile() {
		final String xmlOutput = "testdata/XML/OutputExTransfer.xml";
		
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			trans.toXML(new File(xmlOutput));
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to XML file \"" + xmlOutput + "\"");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromXML(new File(xmlOutput));
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			trans.toXML(new File(xmlOutput));
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to XML file \"" + xmlOutput + "\"");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromXML(new File(xmlOutput));
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
	 * {@link exalge2.ExTransfer#fromCsvString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromCsvStringString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExTransfer.csv";
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
			ExTransfer retNormal = ExTransfer.fromCsvString(csvString);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
	 * {@link exalge2.ExTransfer#fromXmlString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromXmlStringString() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalExTransfer.xml";
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
			ExTransfer retNormal = ExTransfer.fromXmlString(xmlString);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
	 * {@link exalge2.ExTransfer#toCsvString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToCsvString() {
		String retString = null;
		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			retString = trans.toCsvString();
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to CSV String.");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromCsvString(retString);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
		
		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			retString = trans.toCsvString();
		} catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to CSV String.");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromCsvString(retString);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
	 * {@link exalge2.ExTransfer#toXmlString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToXmlString() {
		String retString = null;

		//----------
		// no index
		//----------
		ExTransfer trans = new ExTransfer(false);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			retString = trans.toXmlString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to XML String.");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromXmlString(retString);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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

		//----------
		// with index
		//----------
		trans = new ExTransfer(true);
		ExTransferTest.putAllTransfer(trans, ExTransferTest.data1);
		ExTransferTest.assertEqualTransfer(trans, ExTransferTest.data1);
		//--- output
		try {
			retString = trans.toXmlString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to convert to XML String.");
		}
		//--- check
		try {
			ExTransfer retNormal = ExTransfer.fromXmlString(retString);
			ExTransferTest.assertEqualTransfer(retNormal, ExTransferTest.data1);
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
