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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Reporter.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.io;


/**
 * メッセージ表示クラス
 *
 * 
 * @version 1.00	2007/11/29
 */
public class Reporter
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final NullReportPrinter nullPrinter = new NullReportPrinter();
	
	private final ReportPrinter out;		// 出力先
	
	private String headerDebug = "";
	private String headerTrace = "";
	private String headerInfo = "";
	private String headerWarn = "";
	private String headerError = "";
	
	private boolean enableDebug;
	private boolean enableTrace;
	private boolean enableInfo;
	private boolean enableWarn;
	private boolean enableError;

	private ReportPrinter outDebug;
	private ReportPrinter outTrace;
	private ReportPrinter outInfo;
	private ReportPrinter outWarn;
	private ReportPrinter outError;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public Reporter(ReportPrinter printer) {
		if (printer == null)
			throw new NullPointerException();
		this.out = printer;
		// initial state
		setDebugEnable(false);
		setTraceEnable(false);
		setInfoEnable(true);
		setWarningEnable(true);
		setErrorEnable(true);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/*
	 * State
	 */
	
	public boolean isDebugEnabled() {
		return this.enableDebug;
	}
	
	public boolean isTraceEnabled() {
		return this.enableTrace;
	}
	
	public boolean isInfoEnabled() {
		return this.enableInfo;
	}
	
	public boolean isWarningEnabled() {
		return this.enableWarn;
	}
	
	public boolean isErrorEnabled() {
		return this.enableError;
	}
	
	public void setDebugEnable(boolean enable) {
		this.outDebug = enable ? this.out : this.nullPrinter;
		this.enableDebug = enable;
	}
	
	public void setTraceEnable(boolean enable) {
		this.outTrace = enable ? this.out : this.nullPrinter;
		this.enableTrace = enable;
	}
	
	public void setInfoEnable(boolean enable) {
		this.outInfo = enable ? this.out : this.nullPrinter;
		this.enableInfo = enable;
	}
	
	public void setWarningEnable(boolean enable) {
		this.outWarn = enable ? this.out : this.nullPrinter;
		this.enableWarn = enable;
	}
	
	public void setErrorEnable(boolean enable) {
		this.outError = enable ? this.out : this.nullPrinter;
		this.enableError = enable;
	}
	
	/*
	 * Message Header
	 */
	
	public String getDebugHeader() {
		return this.headerDebug;
	}
	
	public String getTraceHeader() {
		return this.headerTrace;
	}
	
	public String getInfoHeader() {
		return this.headerInfo;
	}
	
	public String getWarningHeader() {
		return this.headerWarn;
	}
	
	public String getErrorHeader() {
		return this.headerError;
	}
	
	public void setDebugHeader(String header) {
		this.headerDebug = header != null ? header : "";
	}
	
	public void setTraceHeader(String header) {
		this.headerTrace = header != null ? header : "";
	}
	
	public void setInfoHeader(String header) {
		this.headerInfo = header != null ? header : "";
	}
	
	public void setWarningHeader(String header) {
		this.headerWarn = header != null ? header : "";
	}
	
	public void setErrorHeader(String header) {
		this.headerError = header != null ? header : "";
	}
	
	/*
	 * Get printer
	 */
	
	public ReportPrinter debug() {
		return this.outDebug;
	}
	
	public ReportPrinter trace() {
		return this.outTrace;
	}
	
	public ReportPrinter info() {
		return this.outInfo;
	}
	
	public ReportPrinter warn() {
		return this.outWarn;
	}
	
	public ReportPrinter error() {
		return this.outError;
	}
	
	/*
	 * helper
	 */

	//--- for Debug
	
	public ReportPrinter debugPrint(String format, Object...args) {
		this.outDebug.print(this.headerDebug);
		this.outDebug.printf(format, args);
		return this.outDebug;
	}
	
	public ReportPrinter debugPrintln(String format, Object...args) {
		this.outDebug.print(this.headerDebug);
		this.outDebug.printf(format, args);
		this.outDebug.println();
		return this.outDebug;
	}
	
	public void debugPrintStackTrace(Throwable ex) {
		this.outDebug.print(this.headerDebug);
		this.outDebug.printStackTrace(ex);
	}

	//--- for Trace
	
	public ReportPrinter tracePrint(String format, Object...args) {
		this.outTrace.print(this.headerTrace);
		this.outTrace.printf(format, args);
		return this.outTrace;
	}
	
	public ReportPrinter tracePrintln(String format, Object...args) {
		this.outTrace.print(this.headerTrace);
		this.outTrace.printf(format, args);
		this.outTrace.println();
		return this.outTrace;
	}
	
	public void tracePrintStackTrace(Throwable ex) {
		this.outTrace.print(this.headerTrace);
		this.outTrace.printStackTrace(ex);
	}

	//--- for Info
	
	public ReportPrinter infoPrint(String format, Object...args) {
		this.outInfo.print(this.headerInfo);
		this.outInfo.printf(format, args);
		return this.outInfo;
	}
	
	public ReportPrinter infoPrintln(String format, Object...args) {
		this.outInfo.print(this.headerInfo);
		this.outInfo.printf(format, args);
		this.outInfo.println();
		return this.outInfo;
	}
	
	public void infoPrintStackTrace(Throwable ex) {
		this.outInfo.print(this.headerInfo);
		this.outInfo.printStackTrace(ex);
	}

	//--- for Warning
	
	public ReportPrinter warnPrint(String format, Object...args) {
		this.outWarn.print(this.headerWarn);
		this.outWarn.printf(format, args);
		return this.outWarn;
	}
	
	public ReportPrinter warnPrintln(String format, Object...args) {
		this.outWarn.print(this.headerWarn);
		this.outWarn.printf(format, args);
		this.outWarn.println();
		return this.outWarn;
	}
	
	public void warnPrintStackTrace(Throwable ex) {
		this.outWarn.print(this.headerWarn);
		this.outWarn.printStackTrace(ex);
	}

	//--- for Error
	
	public ReportPrinter errorPrint(String format, Object...args) {
		this.outError.print(this.headerError);
		this.outError.printf(format, args);
		return this.outError;
	}
	
	public ReportPrinter errorPrintln(String format, Object...args) {
		this.outError.print(this.headerError);
		this.outError.printf(format, args);
		this.outError.println();
		return this.outError;
	}
	
	public void errorPrintStackTrace(Throwable ex) {
		this.outError.print(this.headerError);
		this.outError.printStackTrace(ex);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
