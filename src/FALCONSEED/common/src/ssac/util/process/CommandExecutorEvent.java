/*
 * @(#)CommandExecutorEvent.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.awt.Component;
import java.util.EventObject;

/**
 * コマンド実行時の終了イベント。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class CommandExecutorEvent extends EventObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
    
    private final CommandExecutor _executor;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
    
    public CommandExecutorEvent(Component source, CommandExecutor targetExecutor) {
    	super(source);
    	this._executor = targetExecutor;
    }

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
    
    public Component getComponent() {
    	return (source instanceof Component) ? (Component)source : null;
    }
    
    public CommandExecutor getExecutor() {
    	return _executor;
    }
    
    public int getExitCode() {
    	return (_executor != null ? _executor.getExitCode() : 0);
    }

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
