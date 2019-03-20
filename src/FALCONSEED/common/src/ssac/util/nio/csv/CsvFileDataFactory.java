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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFileDataFactory.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileDataFactory.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ssac.util.io.Files;
import ssac.util.nio.FileUtil;
import ssac.util.nio.TextFileDataFactory;
import ssac.util.nio.array.LongStoredArrayWriter;

/**
 * <code>CsvFileData</code> オブジェクトを生成するファクトリクラス。
 * 
 * @version 1.17	2010/11/19
 * @since 1.16
 */
public class CsvFileDataFactory extends TextFileDataFactory
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** CSVパラメータ **/
	private final CsvParameters	_csvParams = new CsvParameters();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileDataFactory() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 第１行をヘッダ行として使用するかどうかを取得する。
	 * @return	第１行がヘッダ行に指定されている場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean getUseHeaderLine() {
		return _csvParams.getUseHeaderLine();
	}

	/**
	 * 第１行をヘッダ行として使用するかどうかを設定する。
	 * デフォルトでは、第１行は通常行となっている。
	 * @param toUse	第１行をヘッダ行とする場合は <tt>true</tt>、
	 * 				通常行とする場合は <tt>false</tt>
	 * @since 1.17
	 */
	public void setUseHeaderLine(boolean toUse) {
		this._csvParams.setUseHeaderLine(toUse);
	}

	/**
	 * 読み込み時にデータ型の自動判別を行うかどうかの設定を取得する。
	 * @return	データ型の自動判別を行う場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean getAutoDetectDataType() {
		return _csvParams.getAutoDetectDataType();
	}

	/**
	 * 読み込み時にデータ型の自動判別を行うかどうかを設定する。
	 * データ型の自動判別は基本的に、数値、真偽値、文字列のどれかであることを
	 * 判別する機能となる。
	 * デフォルトでは、自動判別が有効となっている。
	 * @param flag	自動判別を有効とする場合は <tt>true</tt>、
	 * 				無効とする場合は <tt>false</tt>
	 * @since 1.17
	 */
	public void setAutoDetectDataType(boolean flag) {
		this._csvParams.setAutoDetectDataType(flag);
	}

	/**
	 * デコードにおいて、クオートによるエスケープ処理が有効かどうかを取得する。
	 * デフォルトでは、クオートによるエスケープ処理は有効に設定されている。
	 * @return	クオートによるエスケープ処理が有効であれば <tt>true</tt>、
	 * 			無効であれば <tt>false</tt> を返す。
	 */
	public boolean isQuoteEscapeEnabled() {
		return _csvParams.isQuoteEscapeEnabled();
	}

	/**
	 * クオートによるエスケープ処理を有効もしくは無効に設定する。
	 * @param toEnable	クオートによるエスケープ処理を有効とする場合は <tt>true</tt>、
	 * 					無効とする場合は <tt>false</tt> を指定する。
	 */
	public void setQuoteEscapeEnabled(boolean toEnable) {
		_csvParams.setQuoteEscapeEnabled(toEnable);
	}

	/**
	 * デコードにおいて、複数行フィールドが許可されているかどうかを取得する。
	 * デフォルトでは、複数行フィールドは許可されている。
	 * <p>
	 * 複数行フィールドは、改行文字を含むフィールドであり、{@link #isQuoteEscapeEnabled()} が
	 * <tt>true</tt> を返す場合のみ、複数行フィールドとしてデコードされる。
	 * @return	複数行フィールドが許可されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isAllowMutiLineField() {
		return _csvParams.isAllowMutiLineField();
	}

	/**
	 * 複数行フィールドを許可もしくは禁止する。
	 * @param toAllow	複数行フィールドを許可する場合は <tt>true</tt>、
	 * 					禁止する場合は <tt>false</tt> を返す。
	 */
	public void setAllowMultiLineField(boolean toAllow) {
		_csvParams.setAllowMultiLineField(toAllow);
	}

	/**
	 * 現在のフィールド区切り文字を取得する。
	 * デフォルトでは、カンマ(,)に設定されている。
	 * @return	現在のフィールド区切り文字
	 */
	public char getDelimiterChar() {
		return _csvParams.getDelimiterChar();
	}

	/**
	 * フィールド区切り文字を設定する。
	 * 設定されているクオート文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param newDelim	新しいフィールド区切り文字
	 * @throws IllegalArgumentException	<em>newDelim</em> に指定された文字が、設定されているクオート文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setDelimiterChar(char newDelim) {
		_csvParams.setDelimiterChar(newDelim);
	}
	
	/**
	 * このオブジェクトのCSVパラメータを、指定されたCSVパラメータに設定する。
	 * @param newParams		CSVパラメータ
	 */
	public void setCsvParameters(final CsvParameters newParams) {
		_csvParams.setParameters(newParams);
	}

	/**
	 * このオブジェクトのCSVパラメータを、指定されたCSVパラメータにコピーする。
	 * @param params	CSVパラメータを受け取るバッファ
	 */
	public void getCsvParameters(CsvParameters params) {
		_csvParams.getParameters(params);
	}

	/**
	 * 現在のクオート文字を取得する。
	 * デフォルトでは、ダブルクオーテーション(&quot;)に設定されている。
	 * @return	現在のクオート文字
	 */
	public char getQuoteChar() {
		return _csvParams.getQuoteChar();
	}

	/**
	 * クオート文字を設定する。
	 * 設定されているフィールド区切り文字と同じ文字や、レコード終端文字(改行文字)は指定できない。
	 * @param newQuote	新しいクオート文字
	 * @throws IllegalArgumentException	<em>newQuote</em> に指定された文字が、設定されているフィールド区切り文字と同じ場合、
	 * 										もしくは改行文字('\r' または '\n')の場合
	 */
	public void setQuoteChar(char newQuote) {
		_csvParams.setQuoteChar(newQuote);
	}
	
	public CsvFileData newFileData(File textFile, String charsetName)
	throws IOException
	{
		Charset encoding = Files.getEncodingByName(charsetName);
		return newFileData(textFile, encoding);
	}
	
	public CsvFileData newFileData(File textFile, Charset encoding)
	throws IOException
	{
		File indexFile = File.createTempFile("tmp", ".dat");
		indexFile.deleteOnExit();
		return newFileData(indexFile, textFile, encoding);
	}
	
	public CsvFileData newFileData(File indexFile, File textFile, Charset encoding)
	throws IOException
	{
		return createFileData(indexFile, textFile, encoding);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected CsvParameters getCsvParameters() {
		return _csvParams;
	}
	
	/**
	 * 指定されたフィールド数よりCSVフィールド属性リストが小さい場合に、
	 * CSVフィールド属性リストの要素を追加する。
	 * @param maxFields	設定するフィールド最大数
	 * @param attrList	CSVフィールド属性リスト
	 * @since 1.17
	 */
	protected void ensureCsvFieldAttrListSize(int maxFields, final List<CsvFieldAttr> attrList) {
		int remainFields = maxFields - attrList.size();
		for (; remainFields > 0; remainFields--) {
			attrList.add(new CsvFieldAttr(null, null));
		}
	}

	/**
	 * CSVフィールド属性でデータ型不明(<code>Object</coce>)のものを文字列型(<code>String</code>)に設定する。
	 * @param attrList	フィールド属性のリスト
	 * @since 1.17
	 */
	protected void correctCsvFieldDataTypes(final List<CsvFieldAttr> attrList) {
		for (CsvFieldAttr attr : attrList) {
			if (attr.getDataType() == null) {
				attr.setDataType(String.class);
			}
		}
	}

	/**
	 * 指定されたCSVレコードの各フィールドについて、文字列型と判別されたもの以外のフィールドの
	 * データ型を判定する。
	 * @param csvFields	判定対象のフィールドの配列
	 * @param attrList	CSVフィールド属性のリスト
	 * @since 1.17
	 */
	protected void autoDetectDataTypeForCsvFields(final String[] csvFields, final List<CsvFieldAttr> attrList) {
		int numFields = csvFields.length;
		if (numFields > 0) {
			// grow attrList
			ensureCsvFieldAttrListSize(numFields, attrList);
			
			// check data type
			for (int index = 0; index < numFields; index++) {
				CsvFieldAttr attr = attrList.get(index);
				//--- 文字列型以外のものは自動判別
				Class<?> detectedType = CsvUtil.detectDataType(attr.getDataType(), csvFields[index]);
				if (detectedType != null) {
					attr.setDataType(detectedType);
				}
			}
		}
	}
	
	protected CsvFileData createFileData(final File textFile, final Charset encoding)
	throws IOException
	{
		File indexFile = File.createTempFile("tmp", ".dat");
		indexFile.deleteOnExit();
		return createFileData(indexFile, textFile, encoding);
	}

	protected CsvFileData createFileData(final File indexFile, final File textFile, final Charset encoding)
	throws IOException
	{
		CsvFileData csvData = null;
		CsvFileTokenizer csvTokenizer = null;
		LongStoredArrayWriter indexWriter = null;
		
		try {
			// Open CSV file by CsvFileTokenizer
			csvTokenizer = new CsvFileTokenizer(textFile, encoding);
			csvTokenizer.setCsvParameters(getCsvParameters());
			// Open Index file writer
			indexWriter = new LongStoredArrayWriter(indexFile);
			// create CsvFieldAttr empty list
			List<CsvFieldAttr> attrlist = new ArrayList<CsvFieldAttr>();
			String[] csvFields;
			long cntHeaderRecords = 0L;
			
			// read header record
			if (getUseHeaderLine()) {
				csvFields = csvTokenizer.nextRecord();
				if (csvFields != null) {
					cntHeaderRecords = csvTokenizer.getRecordCount();
					for (String strField : csvFields) {
						attrlist.add(new CsvFieldAttr(strField, Object.class));
					}
				}
			}
			
			// read CSV records
			if (getAutoDetectDataType()) {
				//--- データ型を判別する
				csvFields = csvTokenizer.nextRecord();
				for (; csvFields != null; ) {
					indexWriter.add(csvTokenizer.getRecordBeginIndex());
					indexWriter.add(csvTokenizer.getRecordEndIndex());
					// auto detect data type
					autoDetectDataTypeForCsvFields(csvFields, attrlist);
					// read next record
					csvFields = csvTokenizer.nextRecord();
				}
			} else {
				//--- データ型の自動判別は行わない
				csvFields = csvTokenizer.nextRecord();
				for (; csvFields != null; ) {
					indexWriter.add(csvTokenizer.getRecordBeginIndex());
					indexWriter.add(csvTokenizer.getRecordEndIndex());
					// read next record
					csvFields = csvTokenizer.nextRecord();
				}
			}
			
			// correct CsvFieldAttr list
			ensureCsvFieldAttrListSize(csvTokenizer.getMaxFieldCount(), attrlist);
			correctCsvFieldDataTypes(attrlist);
			
			// setup CsvFileData
			csvData = new CsvFileData(indexFile, textFile, encoding, getCsvParameters());
			csvData.setRecordSize(csvTokenizer.getRecordCount() - cntHeaderRecords);
			csvData.setMaxRecordByteSize(csvTokenizer.getMaxReocrdByteSize());
			csvData.setMaxRecordCharSize(csvTokenizer.getMaxRecordLength());
			csvData.setFieldAttrs(attrlist);
		}
		finally {
			if (indexWriter != null) {
				FileUtil.closeStream(indexWriter);
				indexWriter = null;
			}
			if (csvTokenizer != null) {
				FileUtil.closeStream(csvTokenizer);
				csvTokenizer = null;
			}
		}
		
		return csvData;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
