/**
 * 
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

/**
 * @author ishizuka
 *
 */
public class ExBasePatternSetIOTest extends TestCase
{
	static private final String wcAppleYen     = "りんご-*-円-*-*";
	static private final String wcOrangeYen    = "みかん-*-円-*-*";
	static private final String wcBananaYen    = "ばなな-*-円-*-*";
	static private final String nohatMelonYen  = "メロン-NO_HAT-円-*-*";
	static private final String hatMangoYen    = "マンゴー-HAT-円-*-*";
	
	static private final String nohatBeefYen   = "牛肉-NO_HAT-円-Y2000-肉類";
	static private final String hatPorkYen     = "豚肉-HAT-円-Y2000-肉類";
	static private final String wcChickenYen   = "鶏肉-*-円-Y2000-肉類";
	
	static private final String wcTunaAll      = "まぐろ-*-*-*-*";
	static private final String wcBonitoAll    = "かつお-*-*-*-*";
	static private final String wcSauryAll     = "さんま-*-*-*-*";
	static private final String wcSardineAll   = "いわし-*-*-*-*";
	
	static private final String UTF8 = "UTF-8";
	
	static private final ExBasePattern[] fileData = new ExBasePattern[]{
		new ExBasePattern(wcAppleYen),
		new ExBasePattern(wcOrangeYen),
		new ExBasePattern(wcBananaYen),
		new ExBasePattern(nohatMelonYen),
		new ExBasePattern(hatMangoYen),
		new ExBasePattern(nohatBeefYen),
		new ExBasePattern(hatPorkYen),
		new ExBasePattern(wcChickenYen),
		new ExBasePattern(wcTunaAll),
		new ExBasePattern(wcBonitoAll),
		new ExBasePattern(wcSauryAll),
		new ExBasePattern(wcSardineAll),
	};
	
	static private final ExBasePattern[] seqData = new ExBasePattern[]{
		new ExBasePattern(wcAppleYen),
		new ExBasePattern(wcOrangeYen),
		new ExBasePattern(wcBananaYen),
		new ExBasePattern(nohatMelonYen),
		new ExBasePattern(hatMangoYen),
		new ExBasePattern(nohatBeefYen),
		new ExBasePattern(hatPorkYen),
		new ExBasePattern(wcChickenYen),
		new ExBasePattern(wcTunaAll),
		new ExBasePattern(wcBonitoAll),
		new ExBasePattern(wcSauryAll),
		new ExBasePattern(wcSardineAll),
	};
	
	static public ExBasePatternSet createBasePatternSet() {
		ExBasePatternSet newSet = new ExBasePatternSet(Arrays.asList(fileData));
		return newSet;
	}
	
	static public void checkSetData(ExBasePatternSet targetSet) {
		// check data
		assertEquals(seqData.length, targetSet.size());
		for (int i = 0; i < seqData.length; i++) {
			assertTrue(targetSet.contains(seqData[i]));
		}
		// check sequence
		int idx = 0;
		Iterator<ExBasePattern> it = targetSet.iterator();
		while (it.hasNext()) {
			ExBasePattern fbase = it.next();
			ExBasePattern sbase = seqData[idx++];
			assertTrue(sbase.equals(fbase));
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * {@link exalge2.ExBasePatternSet#fromCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVFile() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExBasePatternSet.csv";
		try {
			ExBasePatternSet retNormal = ExBasePatternSet.fromCSV(new File(csvNormal));
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
	 * {@link exalge2.ExBasePatternSet#fromCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromCSVFileString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExBasePatternSet_UTF8.csv";
		try {
			ExBasePatternSet retNormal = ExBasePatternSet.fromCSV(new File(csvNormal), UTF8);
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
	 * {@link exalge2.ExBasePatternSet#toCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToCSVFile() {
		ExBasePatternSet data = createBasePatternSet();
		
		final String csvOutput = "testdata/CSV/OutputExBasePatternSet.csv";

		try {
			data.toCSV(new File(csvOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			ExBasePatternSet ret1 = ExBasePatternSet.fromCSV(new File(csvOutput));
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
	 * {@link exalge2.ExBasePatternSet#toCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testToCSVFileString() {
		ExBasePatternSet data = createBasePatternSet();
		
		final String csvOutput = "testdata/CSV/OutputExBasePatternSet_UTF8.csv";

		try {
			data.toCSV(new File(csvOutput), UTF8);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to CSV file \"" + csvOutput + "\"");
		}
		
		// check
		try {
			ExBasePatternSet ret1 = ExBasePatternSet.fromCSV(new File(csvOutput), UTF8);
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
	 * {@link exalge2.ExBasePatternSet#fromXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromXMLFile() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalExBasePatternSet.xml";
		try {
			ExBasePatternSet retNormal = ExBasePatternSet.fromXML(new File(xmlNormal));
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
	 * {@link exalge2.ExBasePatternSet#toXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testToXMLFile() {
		ExBasePatternSet data = createBasePatternSet();
		
		final String xmlOutput = "testdata/XML/OutputExBasePatternSet.xml";

		try {
			data.toXML(new File(xmlOutput));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to write to XML file \"" + xmlOutput + "\"");
		}
		
		// check
		try {
			ExBasePatternSet ret1 = ExBasePatternSet.fromXML(new File(xmlOutput));
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
	 * {@link exalge2.ExBasePatternSet#fromCsvString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromCsvStringString() {
		// 正常系
		final String csvNormal = "testdata/CSV/NormalExBasePatternSet.csv";
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
			ExBasePatternSet retNormal = ExBasePatternSet.fromCsvString(csvString);
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
	 * {@link exalge2.ExBasePatternSet#fromXmlString(String)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testFromXmlStringString() {
		// 正常系
		final String xmlNormal = "testdata/XML/NormalExBasePatternSet.xml";
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
			ExBasePatternSet retNormal = ExBasePatternSet.fromXmlString(xmlString);
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
	 * {@link exalge2.ExBasePatternSet#toCsvString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToCsvString() {
		ExBasePatternSet data = createBasePatternSet();
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
			ExBasePatternSet ret1 = ExBasePatternSet.fromCsvString(retString);
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
	 * {@link exalge2.ExBasePatternSet#toXmlString()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testToXmlString() {
		ExBasePatternSet data = createBasePatternSet();
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
			ExBasePatternSet ret1 = ExBasePatternSet.fromXmlString(retString);
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
