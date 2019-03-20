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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ReportWriter.java	2.0.0	2014/03/18 : modified methods and move 'ssac.util.io' to 'ssac.aadl.macro.util.io' 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ReportWriter.java	1.00	2008/10/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util.io;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

/**
 * ReportWriter
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
public class ReportWriter implements ReportPrinter
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static protected final int	DEFAULT_BYTEBUFFER_SIZE	= 8192;
	static protected final int	DEFAULT_CHARBUFFER_SIZE = 8192;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected Object			_outLock;
	protected PrintWriter		out;
	protected String			_csNameForDecode;
	protected CharsetDecoder	_decoder;
	protected ByteBuffer		_byteBuffer;
	protected CharBuffer		_charBuffer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ReportWriter(PrintWriter pw) {
		this(pw, (Charset)null);
	}
	
	public ReportWriter(PrintWriter pw, Charset charsetForByteDecode) {
		if (pw == null)
			throw new NullPointerException();
		this.out = pw;
		Charset cs = (charsetForByteDecode==null ? Charset.defaultCharset() : charsetForByteDecode);
		_csNameForDecode = cs.name();
		_decoder = cs.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
		initBuffer();
	}
	
	public ReportWriter(PrintWriter pw, String charsetNameForByteDecode) throws UnsupportedEncodingException
	{
		if (pw == null)
			throw new NullPointerException();
		this.out = pw;
		Charset cs;
		if (charsetNameForByteDecode == null) {
			cs = Charset.defaultCharset();
			_csNameForDecode = cs.name();
		} else {
			try {
				if (Charset.isSupported(charsetNameForByteDecode)) {
					cs = Charset.forName(charsetNameForByteDecode);
					_csNameForDecode = charsetNameForByteDecode;
				} else {
					throw new UnsupportedEncodingException(charsetNameForByteDecode);
				}
			}
			catch (IllegalCharsetNameException ex) {
				throw new UnsupportedEncodingException(charsetNameForByteDecode);
			}
			catch (UnsupportedCharsetException ex) {
				throw new UnsupportedEncodingException(charsetNameForByteDecode);
			}
		}
		_decoder = cs.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
		initBuffer();
	}
	
	private void initBuffer() {
		_byteBuffer = ByteBuffer.wrap(new byte[DEFAULT_BYTEBUFFER_SIZE]);
		_charBuffer = CharBuffer.wrap(new char[DEFAULT_CHARBUFFER_SIZE]);
		
		// get lock object from PrintWriter
		try {
			Field f = out.getClass().getDeclaredField("lock");
			_outLock = f.get(out);
		}
		catch (Throwable ex) {
			throw new AssertionError(ex);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public PrintWriter getWriter() {
		return this.out;
	}
	
	public String getByteDecoding() {
		return _csNameForDecode;
	}

	//------------------------------------------------------------
	// Implement java.io.Closeable interfaces
	//------------------------------------------------------------
	
	public void close() {
		synchronized (_outLock) {
			flushBytes();
			this.out.close();
		}
	}

	//------------------------------------------------------------
	// Implement java.io.Flushable interfaces
	//------------------------------------------------------------
	
	public void flush() {
		synchronized (_outLock) {
			flushBytes();
			this.out.flush();
		}
	}

	//------------------------------------------------------------
	// Implement java.lang.Appendable interfaces
	//------------------------------------------------------------
	
	public ReportWriter append(CharSequence csq) {
		synchronized (_outLock) {
			flushBytes();
			this.out.append(csq);
			return this;
		}
	}
	
	public ReportWriter append(CharSequence csq, int start, int end) {
		synchronized (_outLock) {
			flushBytes();
			this.out.append(csq, start, end);
			return this;
		}
	}
	
	public ReportWriter append(char c) {
		synchronized (_outLock) {
			flushBytes();
			this.out.append(c);
			return this;
		}
	}

	//------------------------------------------------------------
	// Implement ReportPrinter interfaces
	//------------------------------------------------------------
	
	public boolean checkError() {
		synchronized (_outLock) {
			flushBytes();
			return this.out.checkError();
		}
	}
	
	public void write(byte[] buf, int off, int len) {
		if (len > 0) {
			synchronized (_outLock) {
				for (; len > 0;) {
					int wrote = writeBytes(buf, off, len);
					off += wrote;
					len -= wrote;
				}
			}
		}
	}

	public void print(boolean b) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(b);
		}
	}
	
	public void print(char c) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(c);
		}
	}
	
	public void print(int i) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(i);
		}
	}
	
	public void print(long l) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(l);
		}
	}
	
	public void print(float f) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(f);
		}
	}
	
	public void print(double d) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(d);
		}
	}
	
	public void print(char[] s) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(s);
		}
	}
	
	public void print(String s) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(s);
		}
	}
	
	public void print(Object obj) {
		synchronized (_outLock) {
			flushBytes();
			this.out.print(obj);
		}
	}

	public void println() {
		synchronized (_outLock) {
			flushBytes();
			this.out.println();
		}
	}
	
	public void println(boolean x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(char x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(int x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(long x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(float x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(double x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(char[] x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(String x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void println(Object x) {
		synchronized (_outLock) {
			flushBytes();
			this.out.println(x);
		}
	}
	
	public void printStackTrace(Throwable ex) {
		synchronized (_outLock) {
			flushBytes();
			ex.printStackTrace(this.out);
		}
	}

	public ReportWriter printf(String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			this.out.printf(format, args);
			return this;
		}
	}
	
	public ReportWriter printf(Locale l, String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			this.out.printf(l, format, args);
			return this;
		}
	}

	public ReportWriter format(String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			this.out.format(format, args);
			return this;
		}
	}
	
	public ReportWriter format(Locale l, String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			this.out.format(l, format, args);
			return this;
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected int writeBytes(byte[] buf, int off, int len) {
		if (!_byteBuffer.hasRemaining()) {
			flushBytes();
		}
		
		int copied = Math.min(_byteBuffer.remaining(), len);
		_byteBuffer.put(buf, off, copied);
		_byteBuffer.flip();
		
		_charBuffer.clear();
		for (; _byteBuffer.hasRemaining(); ) {
			CoderResult cr = _decoder.decode(_byteBuffer, _charBuffer, false);
			_charBuffer.flip();
			this.out.write(_charBuffer.array(), _charBuffer.position(), _charBuffer.remaining());
			_charBuffer.clear();
			
			if (cr.isUnderflow()) {
				break;
			}
		}
		
		if (_byteBuffer.hasRemaining()) {
			_byteBuffer.compact();
		} else {
			_byteBuffer.clear();
			_decoder.reset();
		}
		
		return copied;
	}
	
	protected void flushBytes() {
		if (_byteBuffer.position() > 0) {
			_byteBuffer.flip();

			_charBuffer.clear();
			for (; _byteBuffer.hasRemaining();) {
				CoderResult cr = _decoder.decode(_byteBuffer, _charBuffer, false);
				_charBuffer.flip();
				this.out.write(_charBuffer.array(), _charBuffer.position(), _charBuffer.remaining());
				_charBuffer.clear();
				
				if (cr.isUnderflow()) {
					_decoder.flush(_charBuffer);
					break;
				}
			}
			
			if (_charBuffer.position() > 0) {
				_charBuffer.flip();
				this.out.write(_charBuffer.array(), _charBuffer.position(), _charBuffer.remaining());
				_charBuffer.clear();
			}
			
			_decoder.reset();
			_byteBuffer.clear();
		}
	}
}
