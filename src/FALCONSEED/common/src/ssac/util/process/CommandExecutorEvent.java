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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
