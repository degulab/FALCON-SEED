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
 * @(#)OutputPrinter.java	3.0.0	2014/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

import ssac.aadl.macro.util.io.ReportPrinter;

/**
 * コマンド出力ブローカーに対し、書き込みを行うクラス。
 * このクラスのコンストラクタで、ブローカーと、標準出力もしくは標準エラー出力のどちらで受け付けるかを指定する。
 * 主に、サブプロセスの出力をログファイルもしくは画面へ出力するためのインタフェースとして、
 * サブプロセス起動時に使用する。
 * <p>バイトストリームの書き込みも可能なインタフェースも提供されており、コンストラクタで指定された
 * 文字セットに従い、バイトストリームを文字列に変換してから書き込みを行う。
 * 
 * @version 3.0.0	2014/03/24
 */
public class OutputPrinter implements ReportPrinter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int	DEFAULT_BYTEBUFFER_SIZE	= 8192;
	static protected final int	DEFAULT_CHARBUFFER_SIZE = 8192;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final boolean	_forError;	// 標準エラー出力処理用なら true

	protected IOutputWriter		_broker;
	protected Object			_outLock;
	protected String			_csNameForDecode;
	protected CharsetDecoder	_decoder;
	protected ByteBuffer		_byteBuffer;
	protected CharBuffer		_charBuffer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public OutputPrinter(boolean forError, IOutputWriter broker) {
		this(forError, broker, (Charset)null);
	}
	
	public OutputPrinter(boolean forError, IOutputWriter broker, Charset charsetForByteDecode)
	{
		if (broker == null)
			throw new NullPointerException();
		_forError = forError;
		_broker = broker;
		Charset cs = (charsetForByteDecode==null ? Charset.defaultCharset() : charsetForByteDecode);
		_csNameForDecode = cs.name();
		_decoder = cs.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
		initBuffer();
	}
	
	public OutputPrinter(boolean forError, IOutputWriter broker, String charsetNameForByteDecode) throws UnsupportedEncodingException
	{
		if (broker == null)
			throw new NullPointerException();
		_forError = forError;
		_broker = broker;
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
		
		// get lock object from CommandOutputBroker
		Object lockobj = _broker.getLockObject();
		if (lockobj == null) {
			lockobj = _broker;
		}
		_outLock = lockobj;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean forError() {
		return _forError;
	}
	
	public IOutputWriter getBroker() {
		return _broker;
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
			_broker.close();
		}
	}

	//------------------------------------------------------------
	// Implement java.io.Flushable interfaces
	//------------------------------------------------------------
	
	public void flush() {
		synchronized (_outLock) {
			flushBytes();
			_broker.flush();
		}
	}

	//------------------------------------------------------------
	// Implement ssac.aadl.macro.util.io.ReportPrinter interfaces
	//------------------------------------------------------------
	
	public OutputPrinter append(CharSequence csq) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, (csq==null ? "null" : csq.toString()));
			return this;
		}
	}
	
	public OutputPrinter append(CharSequence csq, int start, int end) {
		synchronized (_outLock) {
			flushBytes();
			CharSequence tcs = (csq==null ? "null" : csq);
			_broker.print(_forError, tcs.subSequence(start, end).toString());
			return this;
		}
	}
	
	public OutputPrinter append(char c) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, c);
			return this;
		}
	}
	
	public boolean checkError() {
		synchronized (_outLock) {
			flushBytes();
			return _broker.checkError();
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
			_broker.print(_forError, String.valueOf(b));
		}
	}
	
	public void print(char c) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, c);
		}
	}
	
	public void print(int i) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, String.valueOf(i));
		}
	}
	
	public void print(long l) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, String.valueOf(l));
		}
	}
	
	public void print(float f) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, String.valueOf(f));
		}
	}
	
	public void print(double d) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, String.valueOf(d));
		}
	}
	
	public void print(char[] s) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, String.valueOf(s));
		}
	}
	
	public void print(String s) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, s);
		}
	}
	
	public void print(Object obj) {
		synchronized (_outLock) {
			flushBytes();
			_broker.print(_forError, String.valueOf(obj));
		}
	}

	public void println() {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, "");
		}
	}
	
	public void println(boolean b) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, String.valueOf(b));
		}
	}
	
	public void println(char c) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, c);
		}
	}
	
	public void println(int i) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, String.valueOf(i));
		}
	}
	
	public void println(long l) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, String.valueOf(l));
		}
	}
	
	public void println(float f) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, String.valueOf(f));
		}
	}
	
	public void println(double d) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, String.valueOf(d));
		}
	}
	
	public void println(char[] s) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, String.valueOf(s));
		}
	}
	
	public void println(String s) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, s);
		}
	}
	
	public void println(Object obj) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, String.valueOf(obj));
		}
	}

	public OutputPrinter printf(String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			_broker.printf(_forError, format, args);
			return this;
		}
	}
	
	public OutputPrinter printf(Locale l, String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			_broker.printf(_forError, l, format, args);
			return this;
		}
	}

	public OutputPrinter format(String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			_broker.printf(_forError, format, args);
			return this;
		}
	}
	
	public OutputPrinter format(Locale l, String format, Object ... args) {
		synchronized (_outLock) {
			flushBytes();
			_broker.printf(_forError, l, format, args);
			return this;
		}
	}
	
	public void printStackTrace(Throwable ex) {
		synchronized (_outLock) {
			flushBytes();
			_broker.println(_forError, ex);
			StackTraceElement[] trace = ex.getStackTrace();
			for (int i = 0; i < trace.length; ++i) {
				_broker.println(_forError, "\tat " + trace[i]);
			}
			
			Throwable outCause = ex.getCause();
			if (outCause != null) {
				printStackTraceAsCause(outCause, trace);
			}
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void printStackTraceAsCause(Throwable causeEx, StackTraceElement[] causedTrace) {
		// assert Thread.holdsLock(_lock);
		
		StackTraceElement[] trace = causeEx.getStackTrace();
		int m = trace.length - 1;
		int n = causedTrace.length - 1;
		for (; m>=0 && n>=0 && trace[m].equals(causedTrace[n]); m--, n--);
		int framesInCommon = trace.length - 1 - m;
		
		_broker.println(_forError, "Caused by: " + causeEx);
		for (int i = 0; i <= m; ++i) {
			_broker.println(_forError, "\tat " + trace[i]);
		}
		if (framesInCommon != 0) {
			_broker.println(_forError, "\t... " + framesInCommon + " more");
		}
		
		Throwable outCause = causeEx.getCause();
		if (outCause != null) {
			printStackTraceAsCause(outCause, trace);
		}
	}
	
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
			_broker.print(_forError, String.valueOf(_charBuffer.array(), _charBuffer.position(), _charBuffer.remaining()));
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
				_broker.print(_forError, String.valueOf(_charBuffer.array(), _charBuffer.position(), _charBuffer.remaining()));
				_charBuffer.clear();
				
				if (cr.isUnderflow()) {
					_decoder.flush(_charBuffer);
					break;
				}
			}
			
			if (_charBuffer.position() > 0) {
				_charBuffer.flip();
				_broker.print(_forError, String.valueOf(_charBuffer.array(), _charBuffer.position(), _charBuffer.remaining()));
				_charBuffer.clear();
			}
			
			_decoder.reset();
			_byteBuffer.clear();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
