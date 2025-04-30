/*
 * @(#)DtJsonFileOutputWriter.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import dtalge.io.Files;
import dtalge.util.Strings;
import net.arnx.jsonic.JSON;

/**
 * JSON 形式のファイルを出力するライター。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonFileOutputWriter extends AbDtJsonOutputWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 出力ストリームの Writer **/
	private Writer	_outWriter;
	/** 出力先のファイル **/
	private File	_target;
	/** ユーザーに指定された文字コード名、指定なしなら <code>null</code> **/
	private String	_strEncoding;
	/** 指定されたエンコーディングの文字セット **/
	private Charset	_csEncoding;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtJsonFileOutputWriter(String filepath)
			throws IOException
	{
		this(new File(filepath), null, null);
	}
	
	public DtJsonFileOutputWriter(String filepath, Charset encoding)
			throws IOException
	{
		this(new File(filepath), encoding, null);
	}
	
	public DtJsonFileOutputWriter(String filepath, String encoding)
			throws UnsupportedEncodingException, IOException
	{
		this(new File(filepath), encoding);
	}
	
	public DtJsonFileOutputWriter(File file)
			throws IOException
	{
		this(file, StandardCharsets.UTF_8, null);
	}
	
	public DtJsonFileOutputWriter(File file, Charset encoding)
			throws IOException
	{
		this(file, encoding, null);
	}
	
	public DtJsonFileOutputWriter(File file, String encoding)
			throws UnsupportedEncodingException, IOException
	{
		this(file, DtJsonIOHelper.ensureEncoding(encoding), encoding);
	}
	
	protected DtJsonFileOutputWriter(File file, Charset csEncoding, String strEncoding)
			throws IOException
	{
		// null check for file
		if (file == null) throw new NullPointerException("File object is null.");
		
		// Setup JSON writer
		FileOutputStream fos = null;
		OutputStreamWriter osr = null;
		try {
			// Open file stream
			fos = new FileOutputStream(file);
			_target = file;

			// encoding
			_csEncoding = (csEncoding==null ? StandardCharsets.UTF_8 : csEncoding);
			_strEncoding = (Strings.isNullOrEmpty(strEncoding) ? null : strEncoding);
			osr = new OutputStreamWriter(fos, _csEncoding);
			fos = null;
			
			// setup JSON reader
			_jsonWriter = new JSON().getWriter(osr);
			_outWriter = osr;
			osr = null;
		}
		finally {
			// 予期せぬ例外対策
			Files.closeStream(osr);	// if not null
			Files.closeStream(fos);	// if not null
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getTarget() {
		return _target;
	}
	
	public Charset getEncodingCharset() {
		return _csEncoding;
	}
	
	public String getEncodingName() {
		if (_strEncoding != null) {
			return _strEncoding;
		}
		else {
			return _csEncoding.name();
		}
	}

	/**
	 * ストリームを閉じてリソースを開放する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException
	{
		if (_outWriter != null) {
			_outWriter.close();
			//--- closed
			_outWriter = null;
			super.close();
			//_jsonWriter = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
