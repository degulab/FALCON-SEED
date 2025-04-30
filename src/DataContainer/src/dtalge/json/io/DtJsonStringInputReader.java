/*
 * @(#)DtJsonStringInputReader.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.IOException;
import java.io.StringReader;

import net.arnx.jsonic.JSON;

/**
 * JSON 形式の文字列を読み込むリーダー。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonStringInputReader extends AbDtJsonInputReader
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
	
	public DtJsonStringInputReader(CharSequence jsontext) {
		if (jsontext == null) throw new NullPointerException("Json text is null.");
		_jsonReader = new JSON().getReader(jsontext);
		setupJsonicLocatorAndMessages();
	}
	
	public DtJsonStringInputReader(StringReader reader) {
		if (reader == null) throw new NullPointerException("StringReader object is null.");
		_jsonReader = new JSON().getReader(reader);
		setupJsonicLocatorAndMessages();
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
		if (_jsonReader != null) {
			_jsonReader = null;
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
