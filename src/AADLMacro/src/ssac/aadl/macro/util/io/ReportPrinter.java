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
 * @(#)ReportPrinter.java	2.0.0	2014/03/18 : modified and move 'ssac.util.io' to 'ssac.aadl.macro.util.io' 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ReportPrinter.java	1.00	2008/10/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util.io;

import java.io.Closeable;
import java.io.Flushable;
import java.util.Locale;

/**
 * 文字列レポート出力を可能とするインタフェース
 *
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
public interface ReportPrinter extends Closeable, Flushable, Appendable
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
