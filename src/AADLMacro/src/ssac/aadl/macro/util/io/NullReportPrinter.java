/*
 * @(#)NullReportPrinter.java	2.0.0	2014/03/18 : move 'ssac.util.io' to 'ssac.aadl.macro.util.io' 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)NullReportPrinter.java	1.00	2008/10/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util.io;

import java.util.Locale;

/**
 * 何も出力しない Printer
 *
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
public class NullReportPrinter implements ReportPrinter
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public NullReportPrinter() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement java.io.Closeable interfaces
	//------------------------------------------------------------
	
	public void close() {}

	//------------------------------------------------------------
	// Implement java.io.Flushable interfaces
	//------------------------------------------------------------
	
	public void flush() {}

	//------------------------------------------------------------
	// Implement java.lang.Appendable interfaces
	//------------------------------------------------------------
	
	public NullReportPrinter append(CharSequence csq) {
		return this;
	}
	
	public NullReportPrinter append(CharSequence csq, int start, int end) {
		return this;
	}
	
	public NullReportPrinter append(char c) {
		return this;
	}

	//------------------------------------------------------------
	// Implement ReportPrinter interfaces
	//------------------------------------------------------------
	
	public boolean checkError() {
		return false;
	}
	
	public void write(byte[] buf, int off, int len) {}

	public void print(boolean b) {}
	
	public void print(char c) {}
	
	public void print(int i) {}
	
	public void print(long l) {}
	
	public void print(float f) {}
	
	public void print(double d) {}
	
	public void print(char[] s) {}
	
	public void print(String s) {}
	
	public void print(Object obj) {}

	public void println() {}
	
	public void println(boolean x) {}
	
	public void println(char x) {}
	
	public void println(int x) {}
	
	public void println(long x) {}
	
	public void println(float x) {}
	
	public void println(double x) {}
	
	public void println(char[] x) {}
	
	public void println(String x) {}
	
	public void println(Object x) {}
	
	public void printStackTrace(Throwable ex) {}

	public NullReportPrinter printf(String format, Object ... args) {
		return this;
	}
	
	public NullReportPrinter printf(Locale l, String format, Object ... args) {
		return this;
	}

	public NullReportPrinter format(String format, Object ... args) {
		return this;
	}
	
	public NullReportPrinter format(Locale l, String format, Object ... args) {
		return this;
	}
}
