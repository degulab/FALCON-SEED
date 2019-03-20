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
 * @(#)CsvFileReader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

import java.io.IOException;

import ssac.util.nio.BufferedIndexedTextFileReader;

/**
 * CSVファイルの情報を保持するデータクラス。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class CsvFileReader extends BufferedIndexedTextFileReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private transient StringBuilder	_recordBuffer;
	private transient CsvFieldDecoder	_csvDecoder;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileReader(CsvFileData fileData) {
		super(fileData);
	}
	
	public CsvFileReader(CsvFileData fileData, int bufferCapacity) {
		super(fileData, bufferCapacity);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CsvFileData getCsvFileData() {
		return (CsvFileData)getFileData();
	}
	
	public String[] readFields(long index) throws IOException
	{
		final CsvFieldDecoder decoder = getCsvFieldDecoder();
		if (getLastIndex() != index) {
			final StringBuilder recBuffer = getRecordBuffer();
			recBuffer.setLength(0);
			readRecord(recBuffer, index);
			decoder.reset(false);
			decoder.decode();
			decoder.flush();
		}
		return decoder.fields();
	}
	
	public int readFields(String[] output, long index) throws IOException
	{
		if (output == null || output.length < 1) {
			return 0;
		}
		
		final CsvFieldDecoder decoder = getCsvFieldDecoder();
		if (getLastIndex() != index) {
			final StringBuilder recBuffer = getRecordBuffer();
			recBuffer.setLength(0);
			readRecord(recBuffer, index);
			decoder.reset(false);
			decoder.decode();
			decoder.flush();
		}
		return decoder.fields(output);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected StringBuilder getRecordBuffer() {
		if (_recordBuffer == null) {
			this._recordBuffer = new StringBuilder(getCsvFileData().getMaxRecordCharSize());
		}
		return _recordBuffer;
	}
	
	protected CsvFieldDecoder getCsvFieldDecoder() {
		if (_csvDecoder == null) {
			this._csvDecoder = new CsvFieldDecoder(getRecordBuffer());
			this._csvDecoder.setCsvParameters(getCsvFileData().getCsvParameters());
		}
		return _csvDecoder;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
