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
