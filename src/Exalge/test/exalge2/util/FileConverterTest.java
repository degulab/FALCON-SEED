/**
 * 
 */
package exalge2.util;

import java.io.File;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import exalge2.ExAlgeSet;
import exalge2.ExAlgeSetIOTest;
import exalge2.ExBasePatternSet;
import exalge2.ExBasePatternSetIOTest;
import exalge2.ExBaseSet;
import exalge2.ExBaseSetIOTest;
import exalge2.ExTransfer;
import exalge2.ExTransferIOTest;
import exalge2.TransMatrix;
import exalge2.TransMatrixIOTest;
import exalge2.TransTable;
import exalge2.TransTableIOTest;
import exalge2.io.xml.XmlDocument;

/**
 * for only Microsoft Windows platform
 * 
 * @author ishizuka
 *
 */
public class FileConverterTest extends TestCase {
	
	private static final String UTF8 = "UTF-8";
	
	private static final String sjisAlgeSet = "testdata/CSV/NormalExalgeSet.csv";
	private static final String sjisBaseSet = "testdata/CSV/NormalExBaseSet.csv";
	private static final String sjisBaseSet2 = "testdata/CSV/NormalExBaseSet2.csv";
	private static final String sjisBaseSet3 = "testdata/CSV/NormalExBaseSet3.csv";
	private static final String sjisTransTable = "testdata/CSV/NormalTransTable.csv";
	private static final String sjisTransMatrix = "testdata/CSV/NormalTransMatrix.csv";
	private static final String sjisExTransfer = "testdata/CSV/NormalExTransfer.csv";
	private static final String sjisBasePatternSet = "testdata/CSV/NormalExBasePatternSet.csv";
	
	private static final String utfAlgeSet = "testdata/CSV/NormalExalgeSet_UTF8.csv";
	private static final String utfBaseSet = "testdata/CSV/NormalExBaseSet_UTF8.csv";
	//private static final String utfBaseSet2 = "testdata/CSV/NormalExBaseSet2_UTF8.csv";
	//private static final String utfBaseSet3 = "testdata/CSV/NormalExBaseSet3_UTF8.csv";
	private static final String utfTransTable ="testdata/CSV/NormalTransTable_UTF8.csv";
	private static final String utfTransMatrix = "testdata/CSV/NormalTransMatrix_UTF8.csv";
	private static final String utfExTransfer = "testdata/CSV/NormalExTransfer_UTF8.csv";
	private static final String utfBasePatternSet = "testdata/CSV/NormalExBasePatternSet_UTF8.csv";
	
	private static final String xmlAlgeSet = "testdata/XML/NormalExalgeSet.xml";
	private static final String xmlBaseSet = "testdata/XML/NormalExBaseSet.xml";
	private static final String xmlTransTable = "testdata/XML/NormalTransTable.xml";
	private static final String xmlTransMatrix = "testdata/XML/NormalTransMatrix.xml";
	private static final String xmlExTransfer = "testdata/XML/NormalExTransfer.xml";
	private static final String xmlBasePatternSet = "testdata/XML/NormalExBasePatternSet.xml";
	
	private Charset defaultCharset = null;
	private Charset anotherCharset = null;
	
	private Document getDocumentFromXmlFile(String filePath) {
		try {
			XmlDocument inxml = XmlDocument.fromFile(new File(filePath));
			return inxml.getDocument();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + filePath + "\"");
			return null;
		}
	}
	
	private void checkFromObject(Object obj) {
		if (obj instanceof ExAlgeSet) {
			ExAlgeSet algeSet = (ExAlgeSet)obj;
			ExAlgeSetIOTest.checkSetData(algeSet);
		}
		else if (obj instanceof ExBaseSet) {
			ExBaseSet baseSet = (ExBaseSet)obj;
			ExBaseSetIOTest.checkSetData(baseSet);
		}
		else if (obj instanceof ExBasePatternSet) {
			ExBasePatternSet patSet = (ExBasePatternSet)obj;
			ExBasePatternSetIOTest.checkSetData(patSet);
		}
		else if (obj instanceof TransTable) {
			TransTable table = (TransTable)obj;
			TransTableIOTest.checkTableData(table);
		}
		else if (obj instanceof TransMatrix) {
			TransMatrix matrix = (TransMatrix)obj;
			TransMatrixIOTest.checkMatrixData(matrix);
		}
		else if (obj instanceof ExTransfer) {
			ExTransfer trans = (ExTransfer)obj;
			ExTransferIOTest.checkExTransferFileData(trans);
		}
		else {
			super.fail("Unknown object [" + obj.getClass().getName() + "]");
		}
	}
	
	private void checkFromCsvFile(String filePath) {
		try {
			Object obj = FileConverter.fromCSV(new File(filePath));
			assertNotNull(obj);
			checkFromObject(obj);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + filePath + "\"");
		}
	}
	
	private void checkFromCsvFile(String filePath, String charsetName) {
		try {
			Object obj = FileConverter.fromCSV(new File(filePath), charsetName);
			assertNotNull(obj);
			checkFromObject(obj);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file [" + charsetName + "]\"" + filePath + "\"");
		}
	}
	
	private void checkFromXmlFile(String filePath) {
		try {
			Object obj = FileConverter.fromXML(new File(filePath));
			assertNotNull(obj);
			checkFromObject(obj);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + filePath + "\"");
		}
	}
	
	private void checkFromXmlDocument(Document doc, String name) {
		try {
			Object obj = FileConverter.fromXML(doc);
			assertNotNull(obj);
			checkFromObject(obj);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to parse from XML Document[" + name + "]");
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		defaultCharset = Charset.defaultCharset();

		anotherCharset = Charset.forName("UTF-8");
		if (defaultCharset.equals(anotherCharset)) {
			anotherCharset = Charset.forName("Shift_JIS");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#fromCSV(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromCSVFile() {
		// ExAlgeSet
		try {
			Object obj = FileConverter.fromCSV(new File(sjisAlgeSet));
			assertTrue(obj instanceof ExAlgeSet);
			ExAlgeSet ret = (ExAlgeSet)obj;
			ExAlgeSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + sjisAlgeSet + "\"");
		}
		
		// ExBaseSet
		try {
			Object obj = FileConverter.fromCSV(new File(sjisBaseSet));
			assertTrue(obj instanceof ExBaseSet);
			ExBaseSet ret = (ExBaseSet)obj;
			ExBaseSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + sjisBaseSet + "\"");
		}
		//--- ExBaseSet - pattern.2
		try {
			Object obj = FileConverter.fromCSV(new File(sjisBaseSet2));
			assertFalse(obj instanceof ExAlgeSet);
			assertTrue(obj instanceof ExBaseSet);
			//ExBaseSet ret = (ExBaseSet)obj;
			//ExBaseSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + sjisBaseSet2 + "\"");
		}
		//--- ExBaseSet - pattern.3
		try {
			Object obj = FileConverter.fromCSV(new File(sjisBaseSet3));
			assertFalse(obj instanceof ExBaseSet);
			super.fail("Must be failed to read from CSV file \"" + sjisBaseSet3 + "\"");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(true);
		}
		
		// ExBasePatternSet
		try {
			Object obj = FileConverter.fromCSV(new File(sjisBasePatternSet));
			assertTrue(obj instanceof ExBasePatternSet);
			ExBasePatternSet ret = (ExBasePatternSet)obj;
			ExBasePatternSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + sjisBasePatternSet + "\"");
		}
		
		// TransTable
		try {
			Object obj = FileConverter.fromCSV(new File(sjisTransTable));
			assertTrue(obj instanceof TransTable);
			TransTable ret = (TransTable)obj;
			TransTableIOTest.checkTableData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + sjisTransTable + "\"");
		}
		
		// TransMatrix
		try {
			Object obj = FileConverter.fromCSV(new File(sjisTransMatrix));
			assertTrue(obj instanceof TransMatrix);
			TransMatrix ret = (TransMatrix)obj;
			TransMatrixIOTest.checkMatrixData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + sjisTransMatrix + "\"");
		}
		
		// ExTransfer
		try {
			Object obj = FileConverter.fromCSV(new File(sjisExTransfer));
			assertTrue(obj instanceof ExTransfer);
			ExTransfer ret = (ExTransfer)obj;
			ExTransferIOTest.checkExTransferFileData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + sjisExTransfer + "\"");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#fromCSV(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromCSVFileString() {
		// ExAlgeSet
		try {
			Object obj = FileConverter.fromCSV(new File(utfAlgeSet), UTF8);
			assertTrue(obj instanceof ExAlgeSet);
			ExAlgeSet ret = (ExAlgeSet)obj;
			ExAlgeSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + utfAlgeSet + "\"");
		}
		
		// ExBaseSet
		try {
			Object obj = FileConverter.fromCSV(new File(utfBaseSet), UTF8);
			assertTrue(obj instanceof ExBaseSet);
			ExBaseSet ret = (ExBaseSet)obj;
			ExBaseSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + utfBaseSet + "\"");
		}
		
		// ExBasePatternSet
		try {
			Object obj = FileConverter.fromCSV(new File(utfBasePatternSet), UTF8);
			assertTrue(obj instanceof ExBasePatternSet);
			ExBasePatternSet ret = (ExBasePatternSet)obj;
			ExBasePatternSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + utfBasePatternSet + "\"");
		}
		
		// TransTable
		try {
			Object obj = FileConverter.fromCSV(new File(utfTransTable), UTF8);
			assertTrue(obj instanceof TransTable);
			TransTable ret = (TransTable)obj;
			TransTableIOTest.checkTableData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + utfTransTable + "\"");
		}
		
		// TransMatrix
		try {
			Object obj = FileConverter.fromCSV(new File(utfTransMatrix), UTF8);
			assertTrue(obj instanceof TransMatrix);
			TransMatrix ret = (TransMatrix)obj;
			TransMatrixIOTest.checkMatrixData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + utfTransMatrix + "\"");
		}
		
		// ExTransfer
		try {
			Object obj = FileConverter.fromCSV(new File(utfExTransfer), UTF8);
			assertTrue(obj instanceof ExTransfer);
			ExTransfer ret = (ExTransfer)obj;
			ExTransferIOTest.checkExTransferFileData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from CSV file \"" + utfExTransfer + "\"");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#fromXML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testFromXMLFile() {
		// ExAlgeSet
		try {
			Object obj = FileConverter.fromXML(new File(xmlAlgeSet));
			assertTrue(obj instanceof ExAlgeSet);
			ExAlgeSet ret = (ExAlgeSet)obj;
			ExAlgeSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlAlgeSet + "\"");
		}
		
		// ExBaseSet
		try {
			Object obj = FileConverter.fromXML(new File(xmlBaseSet));
			assertTrue(obj instanceof ExBaseSet);
			ExBaseSet ret = (ExBaseSet)obj;
			ExBaseSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlBaseSet + "\"");
		}
		
		// ExBasePatternSet
		try {
			Object obj = FileConverter.fromXML(new File(xmlBasePatternSet));
			assertTrue(obj instanceof ExBasePatternSet);
			ExBasePatternSet ret = (ExBasePatternSet)obj;
			ExBasePatternSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlBasePatternSet + "\"");
		}
		
		// TransTable
		try {
			Object obj = FileConverter.fromXML(new File(xmlTransTable));
			assertTrue(obj instanceof TransTable);
			TransTable ret = (TransTable)obj;
			TransTableIOTest.checkTableData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlTransTable + "\"");
		}
		
		// TransMatrix
		try {
			Object obj = FileConverter.fromXML(new File(xmlTransMatrix));
			assertTrue(obj instanceof TransMatrix);
			TransMatrix ret = (TransMatrix)obj;
			TransMatrixIOTest.checkMatrixData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlTransMatrix + "\"");
		}
		
		// ExTransfer
		try {
			Object obj = FileConverter.fromXML(new File(xmlExTransfer));
			assertTrue(obj instanceof ExTransfer);
			ExTransfer ret = (ExTransfer)obj;
			ExTransferIOTest.checkExTransferFileData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from XML file \"" + xmlExTransfer + "\"");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#fromXML(org.w3c.dom.Document)} のためのテスト・メソッド。
	 */
	public void testFromXMLDocument() {
		// ExAlgeSet
		try {
			Document doc = getDocumentFromXmlFile(xmlAlgeSet);
			Object obj = FileConverter.fromXML(doc);
			assertTrue(obj instanceof ExAlgeSet);
			ExAlgeSet ret = (ExAlgeSet)obj;
			ExAlgeSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from \"ExAlgeSet\" XML document");
		}
		
		// ExBaseSet
		try {
			Document doc = getDocumentFromXmlFile(xmlBaseSet);
			Object obj = FileConverter.fromXML(doc);
			assertTrue(obj instanceof ExBaseSet);
			ExBaseSet ret = (ExBaseSet)obj;
			ExBaseSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from \"ExBaseSet\" XML document");
		}
		
		// ExBasePatternSet
		try {
			Document doc = getDocumentFromXmlFile(xmlBasePatternSet);
			Object obj = FileConverter.fromXML(doc);
			assertTrue(obj instanceof ExBasePatternSet);
			ExBasePatternSet ret = (ExBasePatternSet)obj;
			ExBasePatternSetIOTest.checkSetData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from \"ExBasePatternSet\" XML document");
		}
		
		// TransTable
		try {
			Document doc = getDocumentFromXmlFile(xmlTransTable);
			Object obj = FileConverter.fromXML(doc);
			assertTrue(obj instanceof TransTable);
			TransTable ret = (TransTable)obj;
			TransTableIOTest.checkTableData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from \"TransTable\" XML document");
		}
		
		// TransMatrix
		try {
			Document doc = getDocumentFromXmlFile(xmlTransMatrix);
			Object obj = FileConverter.fromXML(doc);
			assertTrue(obj instanceof TransMatrix);
			TransMatrix ret = (TransMatrix)obj;
			TransMatrixIOTest.checkMatrixData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from \"TransMatrix\" XML document");
		}
		
		// ExTransfer
		try {
			Document doc = getDocumentFromXmlFile(xmlExTransfer);
			Object obj = FileConverter.fromXML(doc);
			assertTrue(obj instanceof ExTransfer);
			ExTransfer ret = (ExTransfer)obj;
			ExTransferIOTest.checkExTransferFileData(ret);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to read from \"ExTransfer\" XML document");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertCSV2XML(java.io.File, java.io.File)} のためのテスト・メソッド。
	 */
	public void testConvertCSV2XMLFileFile() {
		String targetPath = "";
		String outputPath = "";
		
		String[][] testfile = new String[][]{
				{sjisAlgeSet,			"testdata/XML/convertSjisCsvExalgeSet.xml"},
				{sjisBaseSet,			"testdata/XML/convertSjisCsvExBaseSet.xml"},
				{sjisBasePatternSet,	"testdata/XML/convertSjisCsvExBasePatternSet.xml"},
				{sjisTransTable,		"testdata/XML/convertSjisCsvTransTable.xml"},
				{sjisTransMatrix,		"testdata/XML/convertSjisCsvTransMatrix.xml"},
				{sjisExTransfer,		"testdata/XML/convertSjisCsvExTransfer.xml"},
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i][0];
				outputPath = testfile[i][1];
				FileConverter.convertCSV2XML(new File(targetPath), new File(outputPath));
				checkFromXmlFile(outputPath);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert CSV[" + targetPath + "] to XML[" + outputPath + "] files");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertCSV2XML(java.io.File, java.lang.String, java.io.File)} のためのテスト・メソッド。
	 */
	public void testConvertCSV2XMLFileStringFile() {
		String targetPath = "";
		String outputPath = "";
		
		String[][] testfile = new String[][]{
				{utfAlgeSet,		"testdata/XML/convertUtf8CsvExalgeSet.xml"},
				{utfBaseSet,		"testdata/XML/convertUtf8CsvExBaseSet.xml"},
				{utfBasePatternSet,	"testdata/XML/convertUtf8CsvExBasePatternSet.xml"},
				{utfTransTable,		"testdata/XML/convertUtf8CsvTransTable.xml"},
				{utfTransMatrix,	"testdata/XML/convertUtf8CsvTransMatrix.xml"},
				{utfExTransfer,		"testdata/XML/convertUtf8CsvExTransfer.xml"},
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i][0];
				outputPath = testfile[i][1];
				FileConverter.convertCSV2XML(new File(targetPath), UTF8, new File(outputPath));
				checkFromXmlFile(outputPath);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert CSV[" + targetPath + "] to XML[" + outputPath + "] files");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertXML2CSV(java.io.File, java.io.File)} のためのテスト・メソッド。
	 */
	public void testConvertXML2CSVFileFile() {
		String targetPath = "";
		String outputPath = "";
		
		String[][] testfile = new String[][]{
				{xmlAlgeSet,		"testdata/CSV/convertXmlExalgeSet.csv"},
				{xmlBaseSet,		"testdata/CSV/convertXmlExBaseSet.csv"},
				{xmlBasePatternSet,	"testdata/CSV/convertXmlExBasePatternSet.csv"},
				{xmlTransTable,		"testdata/CSV/convertXmlTransTable.csv"},
				{xmlTransMatrix,	"testdata/CSV/convertXmlTransMatrix.csv"},
				{xmlExTransfer,		"testdata/CSV/convertXmlExTransfer.csv"},
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i][0];
				outputPath = testfile[i][1];
				FileConverter.convertXML2CSV(new File(targetPath), new File(outputPath));
				checkFromCsvFile(outputPath);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert XML[" + targetPath + "] to CSV[" + outputPath + "] files");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertXML2CSV(java.io.File, java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testConvertXML2CSVFileFileString() {
		String targetPath = "";
		String outputPath = "";
		
		String[][] testfile = new String[][]{
				{xmlAlgeSet,		"testdata/CSV/convertXmlExalgeSet_UTF8.csv"},
				{xmlBaseSet,		"testdata/CSV/convertXmlExBaseSet_UTF8.csv"},
				{xmlBasePatternSet,	"testdata/CSV/convertXmlExBasePatternSet_UTF8.csv"},
				{xmlTransTable,		"testdata/CSV/convertXmlTransTable_UTF8.csv"},
				{xmlTransMatrix,	"testdata/CSV/convertXmlTransMatrix_UTF8.csv"},
				{xmlExTransfer,		"testdata/CSV/convertXmlExTransfer_UTF8.csv"},
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i][0];
				outputPath = testfile[i][1];
				FileConverter.convertXML2CSV(new File(targetPath), new File(outputPath), UTF8);
				checkFromCsvFile(outputPath, UTF8);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert XML[" + targetPath + "] to CSV[" + outputPath + "] files");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertCSV2XML(java.io.File)} のためのテスト・メソッド。
	 */
	public void testConvertCSV2XMLFile() {
		String targetPath = "";
		
		String[] testfile = new String[]{
				sjisAlgeSet,
				sjisBaseSet,
				sjisBasePatternSet,
				sjisTransTable,
				sjisTransMatrix,
				sjisExTransfer,
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i];
				Document doc = FileConverter.convertCSV2XML(new File(targetPath));
				checkFromXmlDocument(doc, "from " + targetPath);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert CSV[" + targetPath + "] file to XML Document");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertCSV2XML(java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testConvertCSV2XMLFileString() {
		String targetPath = "";
		
		String[] testfile = new String[]{
				utfAlgeSet,
				utfBaseSet,
				utfBasePatternSet,
				utfTransTable,
				utfTransMatrix,
				utfExTransfer,
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i];
				Document doc = FileConverter.convertCSV2XML(new File(targetPath), UTF8);
				checkFromXmlDocument(doc, "from " + targetPath);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert CSV[" + targetPath + "] file to XML Document");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertXML2CSV(org.w3c.dom.Document, java.io.File)} のためのテスト・メソッド。
	 */
	public void testConvertXML2CSVDocumentFile() {
		String targetPath = "";
		String outputPath = "";
		
		String[][] testfile = new String[][]{
				{xmlAlgeSet,		"testdata/CSV/convertXmlDocExalgeSet.csv"},
				{xmlBaseSet,		"testdata/CSV/convertXmlDocExBaseSet.csv"},
				{xmlBasePatternSet,	"testdata/CSV/convertXmlDocExBasePatternSet.csv"},
				{xmlTransTable,		"testdata/CSV/convertXmlDocTransTable.csv"},
				{xmlTransMatrix,	"testdata/CSV/convertXmlDocTransMatrix.csv"},
				{xmlExTransfer,		"testdata/CSV/convertXmlDocExTransfer.csv"},
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i][0];
				outputPath = testfile[i][1];
				Document doc = getDocumentFromXmlFile(targetPath);
				FileConverter.convertXML2CSV(doc, new File(outputPath));
				checkFromCsvFile(outputPath);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert XML[" + targetPath + "] Document to CSV[" + outputPath + "] file");
		}
	}

	/**
	 * {@link exalge2.util.FileConverter#convertXML2CSV(org.w3c.dom.Document, java.io.File, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testConvertXML2CSVDocumentFileString() {
		String targetPath = "";
		String outputPath = "";
		
		String[][] testfile = new String[][]{
				{xmlAlgeSet,		"testdata/CSV/convertXmlDocExalgeSet_UTF8.csv"},
				{xmlBaseSet,		"testdata/CSV/convertXmlDocExBaseSet_UTF8.csv"},
				{xmlBasePatternSet,	"testdata/CSV/convertXmlDocExBasePatternSet_UTF8.csv"},
				{xmlTransTable,		"testdata/CSV/convertXmlDocTransTable_UTF8.csv"},
				{xmlTransMatrix,	"testdata/CSV/convertXmlDocTransMatrix_UTF8.csv"},
				{xmlExTransfer,		"testdata/CSV/convertXmlDocExTransfer_UTF8.csv"},
		};
		
		try {
			for (int i = 0; i < testfile.length; i++) {
				targetPath = testfile[i][0];
				outputPath = testfile[i][1];
				Document doc = getDocumentFromXmlFile(targetPath);
				FileConverter.convertXML2CSV(doc, new File(outputPath), UTF8);
				checkFromCsvFile(outputPath, UTF8);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			super.fail("Failed to test of convert XML[" + targetPath + "] Document to CSV[" + outputPath + "] file");
		}
	}

}
