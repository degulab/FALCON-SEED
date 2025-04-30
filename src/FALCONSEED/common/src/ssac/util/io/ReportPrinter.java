/*
 * @(#)ReportPrinter.java	1.00	2008/10/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.Flushable;
import java.util.Locale;

/**
 * 文字列レポート出力を可能とするインタフェース
 *
 * 
 * @version 1.00	2008/10/07
 */
public interface ReportPrinter extends Flushable, Appendable
{
	public boolean checkError();
	
	public void write(byte[] buf, int off, int len);

	public void print(boolean b);
	public void print(char c);
	public void print(int i);
	public void print(long l);
	public void print(float f);
	public void print(double d);
	public void print(char[] s);
	public void print(String s);
	public void print(Object obj);

	public void println();
	public void println(boolean x);
	public void println(char x);
	public void println(int x);
	public void println(long x);
	public void println(float x);
	public void println(double x);
	public void println(char[] x);
	public void println(String x);
	public void println(Object x);
	
	public void printStackTrace(Throwable ex);

	public ReportPrinter printf(String format, Object ... args);
	public ReportPrinter printf(Locale l, String format, Object ... args);

	public ReportPrinter format(String format, Object ... args);
	public ReportPrinter format(Locale l, String format, Object ... args);
}
