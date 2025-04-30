/*
 * @(#)ProcessUtilities.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * プロセスに関するユーティリティ群。
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class ProcessUtilities
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
	
	private ProcessUtilities() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * この VM のプロセスIDを取得する。
	 * @return	取得したプロセスID
	 * @throws UnsupportedOperationException	プロセスIDが取得できない場合
	 * @since 0.8.0
	 */
	static public final long ensureCurrentProcessID() {
		try {
			String strPID = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
			BigDecimal bd = new BigDecimal(strPID);
			return bd.longValueExact();
		}
		catch (Throwable ex) {
			throw new UnsupportedOperationException("Cannot get current process ID.", ex);
		}
	}

	/**
	 * この VM のプロセスIDを取得する。
	 * @return	取得したプロセスID、取得できなかった場合は 0
	 * @since 0.8.0
	 */
	static public final long getCurrentProcessID() {
		try {
			String strPID = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
			BigDecimal bd = new BigDecimal(strPID);
			return bd.longValueExact();
		}
		catch (Throwable ex) {
			return 0L;
		}
	}

	/**
	 * 指定されたプロセスが実行中かどうかを判定する。
	 * 実行中の判定は、{@link java.lang.Process#exitValue()} が例外をスローした場合を実行中とする。
	 * @param process	対象プロセス
	 * @return	実行中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public final boolean isProcessAlive(final Process process) {
		try {
			process.exitValue();
			return false;
		}
		catch (IllegalThreadStateException ex) {
			return true;
		}
	}

	/**
	 * 指定されたプロセスの識別コード(プロセスIDとは限らない)を取得する。
	 * @param process	対象プロセス
	 * @return	プロセス識別コード
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws UnsupportedOperationException	プロセス識別コードが取得できない場合
	 */
	static public final long getProcessHandle(final Process process) {
		if (process == null)
			throw new NullPointerException("Target process object is null.");

		long retValue;
		try {
			if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
				// get Process ID
				Field f = process.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				retValue = f.getInt(process);
			} else {
				// get Process handle
				Field f = process.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				retValue = f.getLong(process);
			}
		} catch (Throwable ex) {
			throw new UnsupportedOperationException("Could not get Process ID or handle.", ex);
		}
		return retValue;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
