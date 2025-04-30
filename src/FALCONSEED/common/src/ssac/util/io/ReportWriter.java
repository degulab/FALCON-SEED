/*
 * @(#)ReportWriter.java	1.00	2008/10/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.PrintWriter;
import java.util.Locale;

/**
 * ReportWriter
 * 
 * @version 1.00	2008/10/07
 */
public class ReportWriter implements ReportPrinter
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected PrintWriter out;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ReportWriter(PrintWriter pw) {
		this.out = pw;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement java.io.Flushable interfaces
	//------------------------------------------------------------
	
	public void flush() {
		this.out.flush();
	}

	//------------------------------------------------------------
	// Implement java.lang.Appendable interfaces
	//------------------------------------------------------------
	
	public ReportWriter append(CharSequence csq) {
		this.out.append(csq);
		return this;
	}
	
	public ReportWriter append(CharSequence csq, int start, int end) {
		this.out.append(csq, start, end);
		return this;
	}
	
	public ReportWriter append(char c) {
		this.out.append(c);
		return this;
	}

	//------------------------------------------------------------
	// Implement ReportPrinter interfaces
	//------------------------------------------------------------
	
	public boolean checkError() {
		return this.out.checkError();
	}
	
	public void write(byte[] buf, int off, int len) {
		this.out.print(new String(buf, off, len));
	}

	public void print(boolean b) {
		this.out.print(b);
	}
	
	public void print(char c) {
		this.out.print(c);
	}
	
	public void print(int i) {
		this.out.print(i);
	}
	
	public void print(long l) {
		this.out.print(l);
	}
	
	public void print(float f) {
		this.out.print(f);
	}
	
	public void print(double d) {
		this.out.print(d);
	}
	
	public void print(char[] s) {
		this.out.print(s);
	}
	
	public void print(String s) {
		this.out.print(s);
	}
	
	public void print(Object obj) {
		this.out.print(obj);
	}

	public void println() {
		this.out.println();
	}
	
	public void println(boolean x) {
		this.out.println(x);
	}
	
	public void println(char x) {
		this.out.println(x);
	}
	
	public void println(int x) {
		this.out.println(x);
	}
	
	public void println(long x) {
		this.out.println(x);
	}
	
	public void println(float x) {
		this.out.println(x);
	}
	
	public void println(double x) {
		this.out.println(x);
	}
	
	public void println(char[] x) {
		this.out.println(x);
	}
	
	public void println(String x) {
		this.out.println(x);
	}
	
	public void println(Object x) {
		this.out.println(x);
	}
	
	public void printStackTrace(Throwable ex) {
		ex.printStackTrace(this.out);
	}

	public ReportWriter printf(String format, Object ... args) {
		this.out.printf(format, args);
		return this;
	}
	
	public ReportWriter printf(Locale l, String format, Object ... args) {
		this.out.printf(l, format, args);
		return this;
	}

	public ReportWriter format(String format, Object ... args) {
		this.out.format(format, args);
		return this;
	}
	
	public ReportWriter format(Locale l, String format, Object ... args) {
		this.out.format(l, format, args);
		return this;
	}
}
