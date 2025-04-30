/*
 * @(#)CodecBase64.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.util;

import java.util.Arrays;

/**
 * Base64 コーデック。
 * 
 * このクラスでは、次の Base64 エンコーディング・スキームをサポートしています。
 * 
 * <ul>
 * <li><a name="basic"><b>基本</b></a>
 * <p>元データは、6ビットごとに、英数字および '+'、'/' 文字に置き換えられます。6 ビットに満たない場合は 0 が追加され変換されます。
 * 変換後の文字数が 4 の倍数に満たない場合は、4 の倍数となるようパディング文字（'='）が追加されます。
 * なお、エンコーダーは行区切り文字を追加しません。<br>
 * デコーダーは、これらの Base64 文字以外が含まれているデータを拒否します。
 * また、最初のパディング文字以降にパディング以外の文字が含まれているデータや、バイトにデコードするための文字が不足しているデータも拒否します。</p></li>
 *
 * <li><a name="url"><b>URLセーフ</b></a>
 * <p>元データは、6ビットごとに、英数字および '-'、'_' 文字に置き換えられます。6 ビットに満たない場合は 0 が追加され変換されます。
 * 変換後の文字数が 4 の倍数に満たない場合でも、パディング文字（'='）は追加されません。
 * なお、エンコーダーは行区切り文字を追加しません。<br>
 * デコーダーは、これらの Base64 文字以外が含まれているデータを拒否します。ただし、パディング文字は許可します。
 * また、最初のパディング文字以降にパディング以外の文字が含まれているデータや、バイトにデコードするための文字が不足しているデータも拒否します。</p></li>
 *
 * <li><a name="mime"><b>MIME</b></a>
 * <p>元データは、6ビットごとに、英数字および '+'、'/' 文字に置き換えられます。
 * 6 ビットに満たない場合は 0 が追加され変換されます。
 * 76 文字ごとに行区切り文字(CR、LF)が追加されます。エンコードされた出力の末尾に行区切り文字は追加されません。
 * 変換後の改行文字を除く文字数が 4 の倍数に満たない場合は、4 の倍数となるようパディング文字（'='）が追加されます。<br>
 * デコーダーは、これらの Base64 文字以外を無視します。
 * なお、デコーダーは最初文字のパディング以降にパディング以外の文字が含まれているデータや、バイトにデコードするための文字が不足しているデータは拒否します。</p></li>
 * </ul>
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class CodecBase64
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
	
	private CodecBase64() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <a href="#basic">基本</a>型 Base64 エンコーディング・スキームを使用してエンコードする {@link Encoder} を返します。
	 * @return	Base64 エンコーダー
	 */
	static public Encoder getBasicEncoder() {
		return Encoder.BasicEncoder;
	}

	/**
	 * <a href="#url">URLセーフ</a>型 Base64 エンコーディング・スキームを使用してエンコードする {@link Encoder} を返します。
	 * @return	Base64 エンコーダー
	 */
	static public Encoder getUrlEncoder() {
		return Encoder.URLSafeEncoder;
	}
	
	/**
	 * <a href="#mime">MIME</a>型 Base64 エンコーディング・スキームを使用してエンコードする {@link Encoder} を返します。
	 * @return	Base64 エンコーダー
	 */
	static public Encoder getMimeEncoder() {
		return Encoder.MimeEncoder;
	}

	/**
	 * <a href="#basic">基本</a>型 Base64 エンコーディング・スキームを使用してデコードする {@link Decoder} を返します。
	 * @return	Base64 デコーダー
	 */
	static public Decoder getBasicDecoder() {
		return Decoder.BasicDecoder;
	}
	
	/**
	 * <a href="#url">URLセーフ</a>型 Base64 エンコーディング・スキームを使用してデコードする {@link Decoder} を返します。
	 * @return	Base64 デコーダー
	 */
	static public Decoder getUrlDecoder() {
		return Decoder.URLSafeDecoder;
	}
	
	/**
	 * <a href="#mime">MIME</a>型 Base64 エンコーディング・スキームを使用してデコードする {@link Decoder} を返します。
	 * @return	Base64 デコーダー
	 */
	static public Decoder getMimeDecoder() {
		return Decoder.MimeDecoder;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * Base64 エンコーダー。
	 * @author RWOS Project
	 * @version 0.2.0
	 */
	static public class Encoder
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------
		
		/** パディング文字 **/
		static protected final char PADDING = '=';

		/** MIME 変換時の行最大文字数 **/
		static protected final int	MAX_MIMELINE	= 76;

		/** MIME 変換における区切り文字 (CR) **/
		static protected final char CR	= '\r';
		/** MIME 変換における区切り文字 (LF) **/
		static protected final char LF	= '\n';
		/** MIME 変換における区切り文字 **/
		static protected final char[] MIME_CRLF	= { CR, LF };

		/** 基本的な Base64 変換テーブル **/
		static protected final char[] BASIC_BASE64_TABLE = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/',
		};

		/** URL用 Base64 変換テーブル **/
		static protected final char[] URL_BASE64_TABLE = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_',
		};

		/** 基本エンコーダー **/
		static protected final Encoder BasicEncoder		= new Encoder(BASIC_BASE64_TABLE, null, 0, true);
		/** URLセーフ・エンコーダー **/
		static protected final Encoder URLSafeEncoder	= new Encoder(URL_BASE64_TABLE, null, 0, false);	// no padding
		/** MIME エンコーダー **/
		static protected final Encoder MimeEncoder		= new Encoder(BASIC_BASE64_TABLE, MIME_CRLF, MAX_MIMELINE, true);
		
		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** Base64 変換文字テーブル **/
		private final char[]	_table;
		/** 行区切り文字 **/
		private final char[]	_newline;
		/** １行の最大文字数 **/
		private final int		_linemax;
		/** パディングの有無 **/
		private final boolean	_padding;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------

		/**
		 * 指定されたパラメータで、Base64 エンコーダーの新しいインスタンスを生成します。
		 * @param table			Base64 文字のテーブル
		 * @param newline		行区切り文字、行を区切らない場合は <tt>null</tt>
		 * @param linemax		行を区切る場合の最大文字数(4 の倍数であること)、0 以下の場合は行を区切らない
		 * @param withPadding	4 文字単位でパディングする場合は <tt>true</tt>、パディングしない場合は <tt>false</tt>
		 * @throws IllegalArgumentException	行区切りが有効であり、linemax が 4 の倍数ではない場合
		 */
		protected Encoder(char[] table, char[] newline, int linemax, boolean withPadding) {
			_table = table;
			if (newline != null && newline.length > 0 && linemax > 0) {
				if ((linemax % 4) != 0)
					throw new IllegalArgumentException("'linemax' is not multiples of 4 : " + linemax);
				_newline = newline;
				_linemax = linemax;
			} else {
				_newline = null;
				_linemax = 0;
			}
			_padding = withPadding;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * 現在の設定でエンコードした結果の文字数を返します。
		 * @param srclen	エンコード対象のバイト数
		 * @return	エンコード後の文字数
		 */
		public int encodingLength(int srclen) {
			int rlen = 0;
			if (srclen > 0) {
				//--- 入力 3 バイトで 4 文字
				if (_padding) {
					rlen = 4 * ((srclen + 2) / 3);
				} else {
					int r = srclen % 3;
					//--- あまりが 1byte なら 2 文字、2byte なら 3 文字
					rlen = 4 * (srclen / 3) + (r==0 ? 0 : r+1);
				}
				//--- 行区切り
				if (_newline != null) {
					rlen += (rlen - 1) / _linemax * _newline.length;
				}
			}
			return rlen;
		}

		/**
		 * 現在の設定でエンコードします。
		 * @param src	エンコード対象のバイト配列
		 * @return	エンコード後の文字列
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		public String encodeToString(byte[] src) {
			int srclen = src.length;
			int dstlen = encodingLength(srclen);
			char[] result = new char[dstlen];
			if (dstlen > 0) {
				doEncode(src, 0, srclen, result, 0);
			}
			return new String(result);
		}

		/**
		 * 現在の設定でエンコードします。
		 * @param src		エンコード対象のバイト配列
		 * @param offset	エンコードを開始する位置のインデックス
		 * @param length	エンコード対象のバイト数
		 * @return	エンコード後の文字列
		 * @throws NullPointerException	<em>src</em> が <tt>null</tt> の場合
		 * @throws IndexOutOfBoundsException	入力バイト配列範囲もしくは出力バッファの範囲が正しくない場合
		 * @since 0.4.0
		 */
		public String encodeToString(byte[] src, int offset, int length) {
			int dstlen = encodingLength(length);
			char[] result = new char[dstlen];
			if (dstlen > 0) {
				doEncode(src, offset, offset+length, result, 0);
			}
			return new String(result);
		}

		/**
		 * 指定された数値を、現在の設定でエンコードします。
		 * @param value	0～63(0x3F)までの数値
		 * @return	エンコード結果の Base64 文字
		 * @throws IllegalArgumentException	<em>value</em> が範囲外の場合
		 * @since 0.3.0
		 */
		public char encodeValue(int value) {
			if (value < 0 || value > 0x3F) {
				throw new IllegalArgumentException("The value to encoding is out of range(0 to 63) : " + value);
			}
			
			return _table[value];
		}
		
		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
		
		/**
		 * 現在の設定で Base64 文字列をデコードします。
		 * @param src		対象のバイト配列
		 * @param srcpos	エンコード開始位置を示すインデックス
		 * @param srcend	エンコード範囲終端を示すインデックス（このインデックスは含まない）
		 * @param dst		エンコード結果出力先とする文字配列
		 * @param dstoff	エンコード結果出力開始位置とするインデックス
		 * @return	エンコード結果の文字数
		 * @throws NullPointerException	<em>src</em> もしくは <em>dst</em> が <tt>null</tt> の場合
		 * @throws IndexOutOfBoundsException	入力バイト配列範囲もしくは出力バッファの範囲が正しくない場合
		 */
		protected int doEncode(byte[] src, int srcoff, int srcend, char[] dst, int dstoff) {
			int dstpos = dstoff;
			int bytesBy4chars = (srcend - srcoff) / 3 * 3;	// 4文字境界での最大変換バイト数
			int endBy4chars = srcoff + bytesBy4chars;		// 4文字境界となる変換範囲のバイト配列終端インデックス(このインデックスは含まない)
			int spos = srcoff;
			if (_newline != null) {
				// with Line separator : result 4 character boundary
				assert ((_linemax % 4) == 0);
				int bytesByLine = _linemax / 4 * 3;	// 1行分の最大変換バイト数
				for (int endByLine = spos + bytesByLine; spos < endBy4chars; endByLine = spos + bytesByLine) {
					// output line separator as needed
					if (spos > srcoff) {
						for (char ch : _newline) {
							dst[dstpos++] = ch;
						}
					}
					// output base64 characters only one line
					int epos = Math.min(endByLine, endBy4chars);
					for (; spos < epos;) {
						// 24bit
						int bits24 = (src[spos++] & 0xff) << 16
								   | (src[spos++] & 0xff) << 8
								   | (src[spos++] & 0xff);
						dst[dstpos++] = (char)_table[(bits24 >>> 18) & 0x3f];
						dst[dstpos++] = (char)_table[(bits24 >>> 12) & 0x3f];
						dst[dstpos++] = (char)_table[(bits24 >>>  6) & 0x3f];
						dst[dstpos++] = (char)_table[bits24 & 0x3f];
					}
				}
			}
			else {
				// No Line separator : result 4 character boundary
				for (; spos < endBy4chars; ) {
					// 24bit
					int bits24 = (src[spos++] & 0xff) << 16
							   | (src[spos++] & 0xff) << 8
							   | (src[spos++] & 0xff);
					dst[dstpos++] = (char)_table[(bits24 >>> 18) & 0x3f];
					dst[dstpos++] = (char)_table[(bits24 >>> 12) & 0x3f];
					dst[dstpos++] = (char)_table[(bits24 >>>  6) & 0x3f];
					dst[dstpos++] = (char)_table[bits24 & 0x3f];
				}
			}
			int rem = srcend - spos;
			if (rem == 2) {
				// remain 2 bytes
				int bits24 = (src[spos++] & 0xff) << 16
						   | (src[spos++] & 0xff) << 8;
				dst[dstpos++] = (char)_table[(bits24 >>> 18) & 0x3f];
				dst[dstpos++] = (char)_table[(bits24 >>> 12) & 0x3f];
				dst[dstpos++] = (char)_table[(bits24 >>>  6) & 0x3f];
				if (_padding) {
					dst[dstpos++] = PADDING;
				}
			}
			else if (rem == 1) {
				// remain 1 bytes
				int bits8 = src[spos++] & 0xff;
				dst[dstpos++] = (char)_table[(bits8 >> 2) & 0x3f];
				dst[dstpos++] = (char)_table[(bits8 << 4) & 0x3f];
				if (_padding) {
					dst[dstpos++] = PADDING;
					dst[dstpos++] = PADDING;
				}
			}
			return (dstpos - dstoff);
		}
	}

	/**
	 * Base64 デコーダー。
	 * @author RWOS Project
	 * @version 0.2.0
	 */
	static public class Decoder
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------
		
		/** デコード不可能時のコード **/
		static public final byte DECODE_UNKNWON	= -1;
		/** パディング文字デコード時のコード **/
		static public final byte DECODE_PADDING	= -2;
		
		/** デコードテーブル長 **/
		static protected final int DECODE_TABLE_LEN	= 256;

		/** 基本デコードテーブル **/
		static protected final byte[] BASIC_DECODE_TABLE = new byte[DECODE_TABLE_LEN];
		static {
			Arrays.fill(BASIC_DECODE_TABLE, DECODE_UNKNWON);
			for (int i = 0; i < Encoder.BASIC_BASE64_TABLE.length; ++i) {
				BASIC_DECODE_TABLE[Encoder.BASIC_BASE64_TABLE[i]] = (byte)i;
			}
			BASIC_DECODE_TABLE[Encoder.PADDING] = DECODE_PADDING;
		}

		/** URLセーフデコードテーブル **/
		static protected final byte[] URL_DECODE_TABLE = new byte[DECODE_TABLE_LEN];
		static {
			Arrays.fill(URL_DECODE_TABLE, DECODE_UNKNWON);
			for (int i = 0; i < Encoder.URL_BASE64_TABLE.length; ++i) {
				URL_DECODE_TABLE[Encoder.URL_BASE64_TABLE[i]] = (byte)i;
			}
			URL_DECODE_TABLE[Encoder.PADDING] = DECODE_PADDING;
		}

		/** 基本デコーダー **/
		static protected final Decoder BasicDecoder		= new Decoder(BASIC_DECODE_TABLE, false);
		/** URLセーフ・デコーダー **/
		static protected final Decoder URLSafeDecoder	= new Decoder(URL_DECODE_TABLE, false);
		/** MIME デコーダー **/
		static protected final Decoder MimeDecoder		= new Decoder(BASIC_DECODE_TABLE, true);

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** Base64 デコードテーブル **/
		private final byte[]	_table;
		/** MIME デコードモード(Base64文字以外を無視する) **/
		private final boolean	_isMime;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------

		/**
		 * 指定されたパラメータで、Base64デコーダーの新しいインスタンスを生成する。
		 * @param decodeTable	Base64 文字のデコードテーブル
		 * @param isMimeType	MIMEタイプとしてデコードする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		protected Decoder(byte[] decodeTable, boolean isMimeType) {
			_table = decodeTable;
			_isMime = isMimeType;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * 指定された Base64 文字列を、現在の設定でデコードした結果の正確なバイト数を返します。
		 * このメソッドは、現在の設定でデコードできない文字列の場合は (-1) を返します。
		 * @param base64string	判定対象の Base64 文字列
		 * @return	デコード後の正確なバイト数、デコードできない場合は (-1)
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		public int decodingLengthExact(String base64string) {
			int strlen = base64string.length();
			if (strlen == 0)
				return 0;
			else
				return calcDecodeLengthExact(base64string, strlen);
		}

		/**
		 * 指定された Base64 文字列を、現在の設定でデコードした結果のバイト数を返します。
		 * このメソッドは、現在の設定で正常に変換できることを前提として変換後のバイト数を計算します。
		 * @param base64string	判定対象の Base64 文字列
		 * @return	デコード後のバイト数
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		public int decodingLength(String base64string) {
			int strlen = base64string.length();
			if (strlen == 0)
				return 0;
			else
				return calcDecodeLength(base64string, strlen);
		}

		/**
		 * 現在の設定でデコードする。
		 * @param base64string	変換対象の Base64 文字列
		 * @return	デコード結果のバイト配列
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 * @throws IllegalArgumentException	現在の設定でデコードできない文字列の場合
		 */
		public byte[] decodeFromString(String base64string) {
			int strlen = base64string.length();
			if (strlen == 0)
				return new byte[0];
			int dstlen = calcDecodeLength(base64string, strlen);
			byte[] result = new byte[dstlen];
			dstlen = doDecode(base64string, 0, strlen, result, 0);
			if (dstlen != result.length) {
				result = Arrays.copyOf(result, dstlen);
			}
			return result;
		}

		/**
		 * 入力文字をデコードします。
		 * @param input	デコードする文字
		 * @return	デコード結果（有効 6 ビット）、パディング文字の場合は {@link #DECODE_PADDING}、デコード不可能な文字の場合は {@link #DECODE_UNKNWON}
		 */
		public int decodeChar(char input) {
			int ival = input & 0xffff;
			return (ival < DECODE_TABLE_LEN ? _table[ival] : DECODE_UNKNWON);
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
		
		/**
		 * 指定された Base64 文字列を、現在の設定でデコードした結果の正確なバイト数を返します。
		 * このメソッドは、現在の設定でデコードできない文字列の場合は (-1) を返します。
		 * @param src		判定対象の Base64 文字列
		 * @param srclen	判定対象の文字数
		 * @return	デコード後の正確なバイト数、デコードできない場合は (-1)
		 * @throws NullPointerException	<em>src</em> が <tt>null</tt> の場合
		 */
		protected int calcDecodeLengthExact(String src, int srclen) {
			// count Base64 chars to first padding char or end of data
			int index = 0;
			int numchars = 0;
			int numbytes = 0;
			if (_isMime) {
				// MIME type
				for (; index < srclen; ++index) {
					int dc = decodeChar(src.charAt(index));
					if (dc == DECODE_PADDING) {
						// reached padding char
						++index;	// skip current padding char
						break;
					}
					else if (dc >= 0) {
						// valid Base64 char
						++numchars;
					}
				}
			}
			else {
				// No MIME type
				for (; index < srclen; ++index) {
					int dc = decodeChar(src.charAt(index));
					if (dc == DECODE_PADDING) {
						// reached padding char
						++index;	// skip current padding char
						break;
					}
					else if (dc < 0) {
						// invalid char : Invalid data
						return (-1);
					}
					// valid Base64 char
					++numchars;
				}
			}
			
			// check and culc result length
			int rm = numchars & 0x03;
			numbytes = numchars / 4 * 3;
			if (rm == 1) {
				// illegal length : last unit does not have enough valid bits
				return (-1);
			}
			else if (rm > 0) {
				// add last bits to bytes
				numbytes += (rm-1);
			}
			
			// check padding
			for (; index < srclen; ++index) {
				if (src.charAt(index) != Encoder.PADDING) {
					// exist char after paddings
					return (-1);
				}
			}
			
			// complete
			return numbytes;
		}

		/**
		 * 指定された文字列をデコードした結果のバイト数を概算します。
		 * このメソッドは、現在の設定で正常に変換できることを前提として変換後のバイト数を計算します。
		 * @param src		判定対象の Base64 文字列
		 * @param srclen	判定対象の文字数
		 * @return	デコード後のバイト数
		 * @throws NullPointerException	<em>src</em> が <tt>null</tt> の場合
		 */
		protected int calcDecodeLength(String src, int srclen) {
			if (_isMime) {
				// MIME type
				int nskip = 0;
				for (int i = 0; i < srclen; ++i) {
					int dc = decodeChar(src.charAt(i));
					if (dc == DECODE_PADDING) {
						// padding
						srclen = i;
						break;
					}
					else if (dc < 0) {
						// invalid base64 char
						++nskip;
					}
				}
				srclen -= nskip;
			}
			else {
				// No MIME type
				//--- exclude last paddings
				for (; srclen > 0; --srclen) {
					if (src.charAt(srclen-1) != Encoder.PADDING) {
						break;
					}
				}
			}
			int rm = srclen & 0x03;
			return (srclen / 4 * 3 + (rm==0 ? 0 : rm-1));
		}

		/**
		 * 現在の設定で Base64 文字列をデコードします。
		 * @param src		対象の Base64 文字列
		 * @param srcpos	デコード開始位置を示すインデックス
		 * @param srcend	デコード範囲終端を示すインデックス（このインデックスは含まない）
		 * @param dst		デコード結果出力先とするバイト配列
		 * @param dstoff	デコード結果出力開始位置とするインデックス
		 * @return	デコード結果のバイト数
		 * @throws NullPointerException	<em>src</em> が <tt>null</tt> の場合
		 * @throws IndexOutOfBoundsException	入力文字列範囲もしくは出力バッファの範囲が正しくない場合
		 * @throws IllegalArgumentException	入力文字列がデコードできない場合
		 */
		protected int doDecode(String src, int srcpos, int srcend, byte[] dst, int dstoff) {
			int dstpos = dstoff;
			int bits = 0;
			int shiftTo = 18;
			for (; srcpos < srcend; ) {
				int dc = decodeChar(src.charAt(srcpos++));
				if (dc == DECODE_PADDING) {
					// reached padding char
					break;
				}
				else if (dc < 0) {
					// unknown char
					if (_isMime) {
						// Skip unknown Base64 char if Type is MIME
						continue;
					} else {
						// Invalid char for Base64
						char ec = src.charAt(srcpos-1);
						throw new IllegalArgumentException("Illegal Base64 character : '" + ec + "'(0x" + Integer.toHexString(ec & 0xffff) + ")");
					}
				}
				// store bits
				bits |= (dc << shiftTo);
				shiftTo -= 6;
				if (shiftTo < 0) {
					// 24bit to 3 bytes
					dst[dstpos++] = (byte)(bits >> 16);
					dst[dstpos++] = (byte)(bits >>  8);
					dst[dstpos++] = (byte)(bits);
					shiftTo = 18;
					bits = 0;
				}
			}
			
			// check and decord end of bytes
			if (shiftTo == 6) {
				// remain 2 chars decord to 1 byte
				dst[dstpos++] = (byte)(bits >> 16);
			}
			else if (shiftTo == 0) {
				// remain 3 chars decord to 2 bytes
				dst[dstpos++] = (byte)(bits >> 16);
				dst[dstpos++] = (byte)(bits >>  8);
			}
			else if (shiftTo == 12) {
				// remain 1 char, cannot decord because it does not have enough valid bits
				throw new IllegalArgumentException("Last unit does not have enough valid bits.");
			}
			
			// check character after padding
			for (; srcpos < srcend; ) {
				if (src.charAt(srcpos++) != Encoder.PADDING) {
					char ec = src.charAt(srcpos-1);
					throw new IllegalArgumentException("Input string has incorrect character after padding at " + srcpos
							+ " : '" + ec + "'(0x" + Integer.toHexString(ec & 0xffff) + ")");
				}
			}
			
			// completed
			return (dstpos - dstoff);
		}
	}
}
