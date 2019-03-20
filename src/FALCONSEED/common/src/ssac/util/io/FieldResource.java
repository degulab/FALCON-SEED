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
package ssac.util.io;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import ssac.util.logging.AppLogger;

/**
 * クラスのフィールドとプロパティとの相互変換
 * 
 * @version 1.00 2008/03/24
 */
public class FieldResource implements IResource
{
	private static final Map EMPTY_MAP = Collections.EMPTY_MAP;
	private Map _fieldMap;

	/**
	 * @return <code>Set<String></code>
	 */
	public Set getPropertyNameSet() {
		Map map = this.getFieldMap();
		return map.keySet();
	}
	public Object getProperty(String name, Object defaultValue) {
		Map map = this.getFieldMap();
		Field fd = (Field) map.get(name);
		if (fd == null) {
			return defaultValue;
		}
		try {
			Object value = fd.get(this);
			return value;
		} catch (Exception ex) {
			AppLogger.debug(ex);
		}
		return defaultValue;
	}
	public boolean setProperty(String name, Object newValue) {
		Map map = this.getFieldMap();
		Field fd = (Field) map.get(name);
		if (fd == null) {
			return false;
		}
		try {
			fd.set(this, newValue);
			return true;
		} catch (Exception ex) {
			AppLogger.debug(ex);
		}
		return false;
	}
	protected Map getFieldMap() {
		if (this._fieldMap == null) {
			this._fieldMap = this.createFieldMap();
		}
		return this._fieldMap;
	}
	@SuppressWarnings("unchecked")
	protected Map createFieldMap() {
		Class clazz = this.getClass();
		Field[] fds = clazz.getFields();
		int n = fds != null ? fds.length : 0;
		if (n < 1) {
			return EMPTY_MAP;
		}
		Map map = null;
		for (int i = 0; i < n; ++i) {
			Field fd = fds[i];
			int modifiers = fd.getModifiers();
			boolean doit = Modifier.isPublic(modifiers)
					&& !Modifier.isFinal(modifiers);
			if (!doit) {
				continue;
			}
			String name = fd.getName();
			if (map == null) {
				map = new HashMap();
			}
			map.put(name, fd);
		}
		return map != null ? map : EMPTY_MAP;
	}
	protected void updateFields(Properties resrouce) {
		Class clazz = this.getClass();
		Field[] fds = clazz.getFields();
		int n = fds != null ? fds.length : 0;
		for (int i = 0; i < n; ++i) {
			Field fd = fds[i];
			int modifiers = fd.getModifiers();
			boolean doit = Modifier.isPublic(modifiers)
					&& !Modifier.isFinal(modifiers);
			if (!doit) {
				continue;
			}
			String name = fd.getName();
			String value = null;
			try {
				value = resrouce.getProperty(name);
			} catch (RuntimeException ex) {
				AppLogger.debug(ex);
				continue;
			}
			if (value == null) {
				continue;
			}
			Class type = fd.getType();
			if (String.class.isAssignableFrom(type)) {
				try {
					fd.set(this, value);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Double.class.isAssignableFrom(type)
					|| Double.TYPE.isAssignableFrom(type)) {
				try {
					Double x = Double.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Float.class.isAssignableFrom(type)
					|| Float.TYPE.isAssignableFrom(type)) {
				try {
					Float x = Float.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Long.class.isAssignableFrom(type)
					|| Long.TYPE.isAssignableFrom(type)) {
				try {
					Integer x = Integer.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Integer.class.isAssignableFrom(type)
					|| Integer.TYPE.isAssignableFrom(type)) {
				try {
					Integer x = Integer.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Short.class.isAssignableFrom(type)
					|| Short.TYPE.isAssignableFrom(type)) {
				try {
					Short x = Short.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Byte.class.isAssignableFrom(type)
					|| Byte.TYPE.isAssignableFrom(type)) {
				try {
					Byte x = Byte.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Boolean.class.isAssignableFrom(type)
					|| Boolean.TYPE.isAssignableFrom(type)) {
				try {
					Boolean x = Boolean.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else {
				try {
					Object x = this.fromString(name, value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			}
		}
	}
	protected void updateFields(ResourceBundle resrouce) {
		Class clazz = this.getClass();
		Field[] fds = clazz.getFields();
		int n = fds != null ? fds.length : 0;
		for (int i = 0; i < n; ++i) {
			Field fd = fds[i];
			int modifiers = fd.getModifiers();
			boolean doit = Modifier.isPublic(modifiers)
					&& !Modifier.isFinal(modifiers);
			if (!doit) {
				continue;
			}
			String name = fd.getName();
			String value = null;
			try {
				value = resrouce.getString(name);
			} catch (RuntimeException ex) {
				AppLogger.debug("missing resource: " + String.valueOf(name));
				continue;
			}
			Class type = fd.getType();
			if (String.class.isAssignableFrom(type)) {
				try {
					fd.set(this, value);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Double.class.isAssignableFrom(type)
					|| Double.TYPE.isAssignableFrom(type)) {
				try {
					Double x = Double.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Float.class.isAssignableFrom(type)
					|| Float.TYPE.isAssignableFrom(type)) {
				try {
					Float x = Float.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Long.class.isAssignableFrom(type)
					|| Long.TYPE.isAssignableFrom(type)) {
				try {
					Integer x = Integer.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Integer.class.isAssignableFrom(type)
					|| Integer.TYPE.isAssignableFrom(type)) {
				try {
					Integer x = Integer.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Short.class.isAssignableFrom(type)
					|| Short.TYPE.isAssignableFrom(type)) {
				try {
					Short x = Short.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Byte.class.isAssignableFrom(type)
					|| Byte.TYPE.isAssignableFrom(type)) {
				try {
					Byte x = Byte.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else if (Boolean.class.isAssignableFrom(type)
					|| Boolean.TYPE.isAssignableFrom(type)) {
				try {
					Boolean x = Boolean.valueOf(value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			} else {
				try {
					Object x = this.fromString(name, value);
					fd.set(this, x);
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			}
		}
	}
	public void getProperties(Properties output) {
		Class clazz = this.getClass();
		Field[] fds = clazz.getFields();
		int n = fds != null ? fds.length : 0;
		for (int i = 0; i < n; ++i) {
			Field fd = fds[i];
			int modifiers = fd.getModifiers();
			boolean doit = Modifier.isPublic(modifiers)
					&& !Modifier.isFinal(modifiers);
			if (!doit) {
				continue;
			}
			String name = fd.getName();
			Object value;
			try {
				value = fd.get(this);
			} catch (Exception ex) {
				AppLogger.debug(ex);
				continue;
			}
			Class type = fd.getType();
			if (String.class.isAssignableFrom(type)) {
				output.setProperty(name, (String) value);
			} else if (Double.class.isAssignableFrom(type)
					|| Double.TYPE.isAssignableFrom(type)) {
				output.setProperty(name, value.toString());
			} else if (Float.class.isAssignableFrom(type)
					|| Float.TYPE.isAssignableFrom(type)) {
				output.setProperty(name, value.toString());
			} else if (Long.class.isAssignableFrom(type)
					|| Long.TYPE.isAssignableFrom(type)) {
				output.setProperty(name, value.toString());
			} else if (Integer.class.isAssignableFrom(type)
					|| Integer.TYPE.isAssignableFrom(type)) {
				output.setProperty(name, value.toString());
			} else if (Short.class.isAssignableFrom(type)
					|| Short.TYPE.isAssignableFrom(type)) {
				output.setProperty(name, value.toString());
			} else if (Byte.class.isAssignableFrom(type)
					|| Byte.TYPE.isAssignableFrom(type)) {
				output.setProperty(name, value.toString());
			} else if (Boolean.class.isAssignableFrom(type)
					|| Boolean.TYPE.isAssignableFrom(type)) {
				output.setProperty(name, value.toString());
			} else {
				try {
					String x = this.toString(name, value);
					if (x != null) {
						output.setProperty(name, x);
					} else {
						output.remove(name);
					}
				} catch (Exception ex) {
					AppLogger.debug(ex);
				}
			}
		}
	}
	protected String toString(String name, Object value) {
		return null;
	}
	protected Object fromString(String name, String value) {
		return null;
	}
}
