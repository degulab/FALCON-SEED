/*
 * @(#)CompileMessages.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.build;

import java.awt.Component;
import java.util.EventObject;

import ssac.util.process.CommandExecutor;

/**
 * コマンド実行時の終了イベント。
 * 
 * @version 1.00 2008/03/24
 */
public class ExecutorEvent extends EventObject
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
    
    private final CommandExecutor executor;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
    
    public ExecutorEvent(Component source, CommandExecutor targetExecutor) {
    	super(source);
    	this.executor = targetExecutor;
    }

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
    
    public Component getComponent() {
    	return (source instanceof Component) ? (Component)source : null;
    }
    
    public CommandExecutor getExecutor() {
    	return executor;
    }
    
    public int getExitCode() {
    	return (executor != null ? executor.getExitCode() : 0);
    }

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
