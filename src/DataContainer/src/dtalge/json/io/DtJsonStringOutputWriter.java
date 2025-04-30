/*
 * @(#)DtJsonStringOutputWriter.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.IOException;
import java.io.StringWriter;

import net.arnx.jsonic.JSON;

/**
 * JSON 形式の文字列を出力するライター。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonStringOutputWriter extends AbDtJsonOutputWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtJsonStringOutputWriter(StringBuilder buffer)
	{
		if (buffer == null) throw new NullPointerException("StringBuilder object is null.");
		try {
			_jsonWriter = new JSON().getWriter(buffer);
		} catch (IOException ignoreEx) {}
	}
	
	public DtJsonStringOutputWriter(StringWriter writer)
	{
		if (writer == null) throw new NullPointerException("StringWriter object is null.");
		try {
			_jsonWriter = new JSON().getWriter(writer);
		} catch (IOException ignoreEx) {}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ストリームを閉じてリソースを開放する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException
	{
		if (_jsonWriter != null) {
			_jsonWriter = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
