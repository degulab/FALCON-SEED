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
