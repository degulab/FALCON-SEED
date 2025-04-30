/*
 * @(#)MacroShutdownHook.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroShutdownHook.java	2.0.0	2014/03/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroShutdownHook.java	1.00	2008/11/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AADLマクロ実行時のシャットダウンフック。
 * このクラスは、{@link java.lang.Runtime#addShutdownHook(Thread)} に登録される
 * インスタンスとなるクラスであり、終了時にプロセスの実行を強制終了し、システム
 * リソースを開放する。
 * 
 * @version 2.1.0	2014/05/29
 *
 * @since 1.00
 */
public final class MacroShutdownHook extends Thread
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private final String MACRO_SHUTDOWN_HOOK_NAME = "AADLMacroShutdownHook";
	
	static private MacroShutdownHook hookInstance = null;
	
	private final List<MacroSequencer> sequencers = Collections.synchronizedList(new ArrayList<MacroSequencer>(2));
	
	private boolean isVerbose;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private MacroShutdownHook() {
		super(MACRO_SHUTDOWN_HOOK_NAME);
		this.isVerbose = false;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected final synchronized void initShutdownHook() {
		if (hookInstance == null) {
			MacroShutdownHook hook = new MacroShutdownHook();
			Runtime.getRuntime().addShutdownHook(hook);
			hookInstance = hook;
		}
	}
	
	static protected final void setVerbose(boolean verbose) {
		if (hookInstance != null) {
			hookInstance.isVerbose = verbose;
		}
	}
	
	static protected final void addSequencer(final MacroSequencer sequencer) {
		if (hookInstance != null) {
			hookInstance.sequencers.add(sequencer);
		}
	}
	
	static private final void printMessage(String message) {
		System.err.println(AADLMacroEngine.PrintHeader.DEBUG.toString() + AADLMacroEngine.getCurrentTimeString() + String.valueOf(message));
	}

	/*
	static private final void printMessage(String format, Object...args) {
		printMessage(String.format(format, args));
	}
	*/
	
	@Override
	public void run() {
		if (isVerbose) {
			printMessage("start Shutdown hook for AADL Macro engine.");
		}

		//--- kill all active process
		for (MacroSequencer seq : sequencers) {
			//--- terminate all active macros
//			if (seq.hasActiveMacros()) {
			if (seq.stopMacroActivation()) {
				if (isVerbose) {
					printMessage("--- Kill all active process in MacroSequencer...");
				}
				//--- kill
				try {
//					seq.killAllActiveMacros(isVerbose);
					seq.killAllActiveMacros(isVerbose, false, false);	// no wait
				} catch (Throwable ex) {
					if (isVerbose) {
						printMessage("<ERROR> " + ex.toString());
					}
				}
			}
			//--- shutdown
			try {
				seq.shutdownExecutorService();
			} catch (Throwable ex) {
				if (isVerbose) {
					printMessage("<ERROR> " + ex.toString());
				}
			}
		}
		
		if (isVerbose) {
			printMessage("end of Shutdown hook for AADL Macro engine.");
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
