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
 * @(#)StoredArrayHeader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.array;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 永続化された配列の情報を保持するヘッダ・オブジェクト
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class StoredArrayHeader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	VERSION_TEXT	= "ssac.util.nio.array.StoredArrayHeader-1.16";
	static public final byte[]	VERSION_BYTES;
	
	static public final int	LONG_BYTE_SIZE		= 8;
	static public final int	INTEGER_BYTE_SIZE	= 4;
	static public final int	SHORT_BYTE_SIZE		= 2;
	static public final int	DOUBLE_BYTE_SIZE	= 8;
	static public final int	FLOAT_BYTE_SIZE		= 4;
	
	
	static protected final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	static protected final int	INDEX_REGION_UNIT_SIZE	= 1024;
	static protected final long	HEADER_SIZE				= 1024L;
	static protected final int	VERSION_POSITION		= 0;
	static protected final int	VALUE_COUNT_POSITION;
	static protected final int	MAX_VALUE_COUNT_POSITION;
	static protected final int	LAST_BIT_COUNT_POSITION;
	static protected final int	END_POSITION;

	static 
	{
		VERSION_BYTES = getByteArray(VERSION_TEXT);
		VALUE_COUNT_POSITION = 0 + VERSION_BYTES.length;
		MAX_VALUE_COUNT_POSITION = VALUE_COUNT_POSITION + 8;
		LAST_BIT_COUNT_POSITION = MAX_VALUE_COUNT_POSITION + 8;
		END_POSITION = LAST_BIT_COUNT_POSITION + 8;
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private Charset		_charset;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public StoredArrayHeader() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された文字列から、このオブジェクトの文字セットによってエンコードした
	 * バイト配列を取得する。
	 * @param str	バイト配列に変換する文字列
	 * @return	文字列から生成されたバイト配列を返す。
	 * 			<em>str</em> が <tt>null</tt> もしくは空文字列の場合は、空のバイト配列を返す。
	 */
	static public byte[] getByteArray(String str) {
		if (str == null) {
			return EMPTY_BYTE_ARRAY;
		}
		else {
			Charset cs = getCharset();
			ByteBuffer bbuf = cs.encode(str);
			return bbuf.array();
		}
	}

	/**
	 * このオブジェクトの文字セットを返す。
	 * 基本的にこのオブジェクトの文字セットは &quot;UTF-8&quot; となっている。
	 */
	static public Charset getCharset() {
		if (_charset == null) {
			_charset = Charset.forName("UTF8");
		}
		return _charset;
	}

	/**
	 * ヘッダのバイト数を返す。
	 */
	public long getHeaderSize() {
		return 1024L;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
