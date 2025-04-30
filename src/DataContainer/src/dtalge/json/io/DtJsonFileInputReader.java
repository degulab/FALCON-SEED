/*
 * @(#)DtJsonFileInputReader.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import dtalge.io.Files;
import dtalge.util.Strings;
import net.arnx.jsonic.JSON;

/**
 * JSON 形式のファイルを読み込むリーダー。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonFileInputReader extends AbDtJsonInputReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 入力ストリームの Reader **/
	private Reader	_inReader;
	/** 入力元のファイル **/
	private File	_target;
	/** ユーザーに指定された文字コード名、指定なしなら <code>null</code> **/
	private String	_strEncoding;
	/** 指定されたエンコーディングの文字セット **/
	private Charset	_csEncoding;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtJsonFileInputReader(String filepath)
			throws FileNotFoundException
	{
		this(new File(filepath), null, null);
	}
	
	public DtJsonFileInputReader(String filepath, Charset encoding)
			throws FileNotFoundException
	{
		this(new File(filepath), encoding, null);
	}
	
	public DtJsonFileInputReader(String filepath, String encoding)
			throws FileNotFoundException, UnsupportedEncodingException
	{
		this(new File(filepath), encoding);
	}
	
	public DtJsonFileInputReader(File file)
			throws FileNotFoundException
	{
		this(file, StandardCharsets.UTF_8, null);
	}
	
	public DtJsonFileInputReader(File file, Charset encoding)
			throws FileNotFoundException
	{
		this(file, encoding, null);
	}
	
	public DtJsonFileInputReader(File file, String encoding)
			throws FileNotFoundException, UnsupportedEncodingException
	{
		this(file, DtJsonIOHelper.ensureEncoding(encoding), encoding);
	}
	
	protected DtJsonFileInputReader(File file, Charset csEncoding, String strEncoding)
			throws FileNotFoundException
	{
		// null check for file
		if (file == null) throw new NullPointerException("File object is null.");
		
		// Setup JSON reader
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			// Open file stream
			fis = new FileInputStream(file);
			_target = file;

			// encoding
			_csEncoding = (csEncoding==null ? StandardCharsets.UTF_8 : csEncoding);
			_strEncoding = (Strings.isNullOrEmpty(strEncoding) ? null : strEncoding);
			isr = new InputStreamReader(fis, _csEncoding);
			fis = null;
			
			// setup JSON reader
			_jsonReader = new JSON().getReader(isr);
			_inReader = isr;
			isr = null;
			setupJsonicLocatorAndMessages();
		}
		finally {
			// 予期せぬ例外対策
			Files.closeStream(isr);	// if not null
			Files.closeStream(fis);	// if not null
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
		if (_inReader != null) {
			_inReader.close();
			//--- closed
			_inReader = null;
			super.close();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
