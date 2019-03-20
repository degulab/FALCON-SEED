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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MqPayload.java	0.3.0	2013/06/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * MQTT で送受信されるバイトデータのコンテナ。
 * <p>このクラスは、MQTT の通信によって送受信されるバイトデータを保持するクラス。
 * このクラスが保持するバイトデータは、バイト配列とオフセット、有効データ長からなる。<br>
 * なお、このオブジェクトは不変オブジェクトである。
 * 
 * @version 0.3.0	2013/06/27
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqPayload
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final byte[] _emptyData = new byte[0];

	/**
	 * データが空である、唯一のインスタンス。
	 */
	static public final MqPayload EmptyPayload = new MqPayload(_emptyData);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final byte[]	_array;
	private final int		_offset;
	private final int		_length;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたバイト配列を保持する、新しいインスタンスを生成する。
	 * 有効データは指定されたバイト配列全体となる。
	 * @param array	バイト配列
	 */
	public MqPayload(byte[] array) {
		if (array == null) {
			_array  = _emptyData;
			_offset = 0;
			_length = 0;
		} else {
			_array  = array;
			_offset = 0;
			_length = array.length;
		}
	}

	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * 有効データは、指定されたオフセットからバイト配列の最後までとなる。
	 * @param array		バイト配列
	 * @param offset	有効データの開始位置を示す、バイト配列先頭からのオフセット (0 ～ array.length)
	 * @throws IndexOutOfBoundsException	<em>offset</em> の値が適切ではない場合
	 */
	public MqPayload(byte[] array, int offset) {
		if (array == null) {
			array = _emptyData;
		}
		if (offset >= 0 && offset <= array.length) {
			// valid
			_array  = array;
			_offset = offset;
			_length = array.length - offset;
		} else {
			// illegal
			throw new IndexOutOfBoundsException("Illegal payload parameters : array.length=" + array.length + ", offset=" + offset);
		}
	}

	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param array		バイト配列
	 * @param offset	有効データの開始位置を示す、バイト配列先頭からのオフセット (0 ～ array.length)
	 * @param length	有効データ開始位置からの有効データ長
	 * @throws IndexOutOfBoundsException	<em>offset</em> および <em>length</em> の値が適切ではない場合
	 */
	public MqPayload(byte[] array, int offset, int length) {
		if (array == null) {
			array = _emptyData;
		}
		validPayload(array, offset, length);
		_array  = array;
		_offset = offset;
		_length = length;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * メッセージデータのバイト配列指定の正当性を検証する。
	 * このメソッドでは、<em>array</em> に <tt>null</tt> が指定された場合、長さが 0 のバイト配列とみなす。
	 * @param array		バイト配列もしくは <tt>null</tt>
	 * @param offset	有効データの開始位置を示す、バイト配列先頭からのオフセット (0 ～ array.length)
	 * @param length	有効データ開始位置からの有効データ長
	 * @throws NullPointerException		<em>array</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>offset</em> および <em>length</em> の値が適切ではない場合
	 */
	static public void validPayload(byte[] array, int offset, int length) {
		int ubound = offset + length;
		if (offset < 0 || length < 0 || ubound > array.length) {
			throw new IndexOutOfBoundsException("Illegal payload parameters : array.length=" + array.length + ", offset=" + offset + ", length=" + length);
		}
	}

	/**
	 * 有効データが存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isEmpty() {
		return (_length == 0);
	}

	/**
	 * このオブジェクトが保持するバイト配列そのものを返す。
	 */
	public byte[] getData() {
		return _array;
	}

	/**
	 * 有効データの位置を示す、バイト配列先頭からのオフセットを返す。
	 */
	public int getOffset() {
		return _offset;
	}

	/**
	 * 有効データのバイト数を返す。
	 */
	public int getLength() {
		return _length;
	}

	/**
	 * 有効データのみの新しいバイト配列を返す。
	 */
	public byte[] toArray() {
		byte[] newArray = new byte[_length];
		System.arraycopy(_array, _offset, newArray, 0, _length);
		return newArray;
	}

	/**
	 * このオブジェクトのハッシュ値を返す。
	 * このハッシュ値は、有効データ部のみで計算される。
	 */
	@Override
	public int hashCode() {
		int h = 1;
		if (_length > 0) {
			byte[] data = _array;
			int offset = _offset;
			int ubound = offset + _length;
			for (; offset < ubound; offset++) {
				h = 31 * h + data[offset];
			}
		}
		return h;
	}

	/**
	 * バイトデータ全体とオフセット、有効データ長も含めたハッシュコードを返す。
	 */
	public int exactlyHashCode() {
		int h = Arrays.hashCode(_array);
		h = 31 * h + _offset;
		h = 31 * h + _length;
		return h;
	}

	/**
	 * 指定されたオブジェクトと有効データ部のみが等しいかを検査し、等しい場合に <tt>true</tt> を返す。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj == null || obj.getClass() != MqPayload.class)
			return false;
		
		return equalsValidData((MqPayload)obj);
	}

	/**
	 * 指定されたオブジェクトとバイトデータ全体、オフセット、有効データ長がすべて等しい場合に <tt>true</tt> を返す。
	 */
	public boolean exactlyEquals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj == null || obj.getClass() != MqPayload.class)
			return false;

		MqPayload aPayload = (MqPayload)obj;
		return (Arrays.equals(this._array, aPayload._array) && this._offset==aPayload._offset && this._length==aPayload._length);
	}

	/**
	 * このオブジェクトが保持する有効データの文字列表現を返す。
	 * このメソッドでは、<code>new String(getData(), getOffset(), getLength())</code> と同じ結果を返す。
	 * @return	生成された文字列
	 */
	@Override
	public String toString() {
		return new String(_array, _offset, _length);
	}

	/**
	 * 指定された文字セットを使用して、このオブジェクトが保持する有効データを復号化し、新しい <code>String</code> を構築する。
	 * このメソッドでは、<code>new String(getData(), getOffset(), getLength(), charset)</code> と同じ結果を返す。
	 * @param charset	復号化に使用される文字セット
	 * @return	生成された文字列
	 */
	public String toString(Charset charset) {
		return new String(_array, _offset, _length, charset);
	}

	/**
	 * 指定された文字セットを使用して、このオブジェクトが保持する有効データを復号化し、新しい <code>String</code> を構築する。
	 * このメソッドでは、<code>new String(getData(), getOffset(), getLength(), charset)</code> と同じ結果を返す。
	 * @param charsetName	復号化に使用する文字セット名
	 * @return	生成された文字列
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 */
	public String toString(String charsetName) throws UnsupportedEncodingException
	{
		return new String(_array, _offset, _length, charsetName);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean equalsValidData(MqPayload aPayload) {
		if (this._length != aPayload._length)
			return false;
		
		byte[] data1 = this._array;
		byte[] data2 = aPayload._array;
		int offset1 = this._offset;
		int offset2 = aPayload._offset;
		int ubound1 = offset1 + this._length;
		
		for (; offset1 < ubound1; offset1++, offset2++) {
			if (data1[offset1] != data2[offset2]) {
				return false;
			}
		}
		
		return true;
	}
}
