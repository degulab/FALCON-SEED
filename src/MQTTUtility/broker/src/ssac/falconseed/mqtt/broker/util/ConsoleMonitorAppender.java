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
 * @(#)ConsoleMonitorAppender.java	0.1.0	2013/05/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.broker.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;

/**
 * <code>Log4j</code> の <code>ConsoleAppender</code> の出力を
 * キューイングするクラス。出力先は、<code>ConsoleAppender</code> 標準のものとなるが、
 * キューイングの有効／無効、標準出力（または標準エラー出力）の有効／無効をそれぞれ設定できる。
 * 
 * @version 0.1.0	2013/05/01
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class ConsoleMonitorAppender extends WriterAppender
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	public static final String SYSTEM_OUT = "System.out";
	public static final String SYSTEM_ERR = "System.err";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	static private CommandOutput	_outQueue = new CommandOutput();
	static private boolean			_queueing = false;
	static private boolean			_outStream = true;

	protected String	target = SYSTEM_OUT;
	private boolean		follow = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ConsoleMonitorAppender() {
	}

	public ConsoleMonitorAppender(Layout layout) {
		this(layout, SYSTEM_OUT);
	}

	public ConsoleMonitorAppender(Layout layout, String target) {
		setLayout(layout);
		setTarget(target);
		activateOptions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	static public CommandOutput getQueue() {
		return _outQueue;
	}

	static public boolean isQutputQueueing() {
		return _queueing;
	}

	static public void setOutputQueueing(boolean queueing) {
		_queueing = queueing;
	}

	static public boolean isOutputWriteToStream() {
		return _outStream;
	}

	static public void setOutputWriteToStream(boolean toWrite) {
		_outStream = toWrite;
	}

	//------------------------------------------------------------
	// ConsoleAppender properties
	//------------------------------------------------------------

	public void setTarget(String value) {
		String v = value.trim();

		if (SYSTEM_OUT.equalsIgnoreCase(v)) {
			target = SYSTEM_OUT;
		} else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
			target = SYSTEM_ERR;
		} else {
			targetWarn(value);
		}
	}

	public String getTarget() {
		return target;
	}

	public final void setFollow(final boolean newValue) {
		follow = newValue;
	}

	public final boolean getFollow() {
		return follow;
	}

	public void activateOptions() {
		if (follow) {
			if (target.equals(SYSTEM_ERR)) {
				setWriter(createWriter(true, new SystemErrStream()));
			} else {
				setWriter(createWriter(false, new SystemOutStream()));
			}
		} else {
			if (target.equals(SYSTEM_ERR)) {
				setWriter(createWriter(true, System.err));
			} else {
				setWriter(createWriter(false, System.out));
			}
		}

		super.activateOptions();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	void targetWarn(String val) {
		LogLog.warn("["+val+"] should be System.out or System.err.");
		LogLog.warn("Using previously set target, System.out by default.");
	}

	protected final void closeWriter() {
		if (follow) {
			super.closeWriter();
		}
	}

	protected final MonitorOutputStreamWriter createWriter(boolean isError, OutputStream os) {
		MonitorOutputStreamWriter retval = null;

		String enc = getEncoding();
		if (enc != null) {
			try {
				retval = new MonitorOutputStreamWriter(isError, os, enc);
			}
			catch (IOException ex) {
				if (ex instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
				LogLog.warn("Error initializing output writer.");
				LogLog.warn("Unsupported encoding?");
			}
		}
		if (retval == null) {
			retval = new MonitorOutputStreamWriter(isError, os);
		}
		return retval;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static private class MonitorOutputStreamWriter extends OutputStreamWriter
	{
		private final boolean _errorFlag;

		public MonitorOutputStreamWriter(boolean isError, OutputStream out) {
			super(out);
			_errorFlag = isError;
		}

		public MonitorOutputStreamWriter(boolean isError, OutputStream out, String charsetName) throws UnsupportedEncodingException
		{
			super(out, charsetName);
			_errorFlag = isError;
		}

		@Override
		public void write(int c) throws IOException {
			if (_outStream) {
				super.write(c);
			}
			if (_queueing) {
				_outQueue.push(_errorFlag, String.valueOf((char)c));
			}
		}

		@Override
		public void write(char cbuf[], int off, int len) throws IOException {
			if (_outStream) {
				super.write(cbuf, off, len);
			}
			if (_queueing) {
				_outQueue.push(_errorFlag, String.valueOf(cbuf, off, len));
			}
		}

		@Override
		public void write(String s, int off, int len) throws IOException {
			if (_outStream) {
				super.write(s, off, len);
			}
			if (_queueing) {
				_outQueue.push(_errorFlag, s.substring(off, off+len));
			}
		}

		@Override
		public void flush() throws IOException {
			if (_outStream) {
				super.flush();
			}
		}
	}

	static private class SystemErrStream extends OutputStream {
		public SystemErrStream() {
		}

		public void close() {
		}

		public void flush() {
			System.err.flush();
		}

		public void write(final byte[] b) throws IOException {
			System.err.write(b);
		}

		public void write(final byte[] b, final int off, final int len)
				throws IOException {
			System.err.write(b, off, len);
		}

		public void write(final int b) throws IOException {
			System.err.write(b);
		}
	}

	static private class SystemOutStream extends OutputStream {
		public SystemOutStream() {
		}

		public void close() {
		}

		public void flush() {
			System.out.flush();
		}

		public void write(final byte[] b) throws IOException {
			System.out.write(b);
		}

		public void write(final byte[] b, final int off, final int len)
				throws IOException {
			System.out.write(b, off, len);
		}

		public void write(final int b) throws IOException {
			System.out.write(b);
		}
	}
}
