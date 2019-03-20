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
package ssac.util.logging;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

/**
 * ログ出力機能を提供するクラス。
 * 
 * このクラスは、アプリケーション内で唯一のインスタンスを持ち、
 * 共通のログ出力インタフェースを提供する。
 * 
 * @version 1.00 2008/03/24
 */
public final class AppLogger
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private Log logImpl = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public void setLogger(Class clazz)
		throws LogConfigurationException
	{
		Log newImpl = LogFactory.getLog(clazz);
		AppLogger.logImpl = newImpl;
	}
	
	static public void setLogger(String name)
		throws LogConfigurationException
	{
		Log newImpl = LogFactory.getLog(name);
		AppLogger.logImpl = newImpl;
	}
	
	static public void setLogger(Log newImpl) {
		AppLogger.logImpl = newImpl;
	}
	
	static public Log getLogger() {
		return AppLogger.logImpl;
	}
	
	static public boolean isFatalEnabled() {
		return (AppLogger.logImpl != null ? AppLogger.logImpl.isFatalEnabled() : false);
	}
	static public boolean isErrorEnabled() {
		return (AppLogger.logImpl != null ? AppLogger.logImpl.isErrorEnabled() : false);
	}
	static public boolean isWarnEnabled() {
		return (AppLogger.logImpl != null ? AppLogger.logImpl.isWarnEnabled() : false);
	}
	static public boolean isInfoEnabled() {
		return (AppLogger.logImpl != null ? AppLogger.logImpl.isInfoEnabled() : false);
	}
	static public boolean isDebugEnabled() {
		return (AppLogger.logImpl != null ? AppLogger.logImpl.isDebugEnabled() : false);
	}
	static public boolean isTraceEnabled() {
		return (AppLogger.logImpl != null ? AppLogger.logImpl.isTraceEnabled() : false);
	}

	static public void trace(Object message) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.trace(message);
	}
	static public void trace(Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.trace(t.getLocalizedMessage(), t);
	}
	static public void trace(Object message, Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.trace(message, t);
	}

	static public void debug(Object message) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.debug(message);
	}
	static public void debug(Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.debug(t.getLocalizedMessage(), t);
	}
	static public void debug(Object message, Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.debug(message, t);
	}

	static public void info(Object message) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.info(message);
	}
	static public void info(Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.info(t.getLocalizedMessage(), t);
	}
	static public void info(Object message, Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.info(message, t);
	}

	static public void warn(Object message) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.warn(message);
	}
	static public void warn(Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.warn(t.getLocalizedMessage(), t);
	}
	static public void warn(Object message, Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.warn(message, t);
	}

	static public void error(Object message) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.error(message);
	}
	static public void error(Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.error(t.getLocalizedMessage(), t);
	}
	static public void error(Object message, Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.error(message, t);
	}

	static public void fatal(Object message) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.fatal(message);
	}
	static public void fatal(Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.fatal(t.getLocalizedMessage(), t);
	}
	static public void fatal(Object message, Throwable t) {
		if (AppLogger.logImpl != null)
			AppLogger.logImpl.fatal(message, t);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static public final String toInputMapList(JComponent component) {
		if (component == null)
			return "Cannot logging for InputMap list by null.";
		String lineSeparator = System.getProperty("line.separator");
		if (lineSeparator == null || lineSeparator.length() <= 0) {
			lineSeparator = "\n";
		}
		StringBuilder sb = new StringBuilder();
		//----- title
		sb.append(component.getClass().getName());
		sb.append(" : List of InputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)");
		//----- InputMap
		InputMap imap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		if (imap != null) {
			KeyStroke[] keys = imap.allKeys();
			if (keys != null && keys.length > 0) {
				for (KeyStroke ks : keys) {
					sb.append(lineSeparator);
					sb.append("  key[");
					sb.append(String.valueOf(ks));
					sb.append("] = cmd[");
					sb.append(String.valueOf(imap.get(ks)));
					sb.append("]");
				}
			} else {
				sb.append(lineSeparator);
				sb.append("  No entry in InputMap.");
			}
		} else {
			sb.append(lineSeparator);
			sb.append("  No InputMap!");
		}
		//---- final line separator
		sb.append(lineSeparator);
		return sb.toString();
	}
	
	static public final String toActionMapList(JComponent component) {
		if (component == null)
			return "Cannot logging for ActionMap list by null.";
		String lineSeparator = System.getProperty("line.separator");
		if (lineSeparator == null || lineSeparator.length() <= 0) {
			lineSeparator = "\n";
		}
		StringBuilder sb = new StringBuilder();
		//----- title
		sb.append(component.getClass().getName());
		sb.append(" : List of ActionMap");
		//----- ActionMap
		ActionMap amap = component.getActionMap();
		if (amap != null) {
			Object[] keys = amap.allKeys();
			if (keys != null && keys.length > 0) {
				for (Object key : keys) {
					sb.append(lineSeparator);
					sb.append("  key[");
					sb.append(String.valueOf(key));
					sb.append("] = cmd[");
					sb.append(String.valueOf(amap.get(key)));
					sb.append("]");
				}
			} else {
				sb.append(lineSeparator);
				sb.append("  No entry in ActionMap.");
			}
		} else {
			sb.append(lineSeparator);
			sb.append("  No ActionMap!");
		}
		//---- final line separator
		sb.append(lineSeparator);
		return sb.toString();
	}
}
