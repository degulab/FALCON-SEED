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
package ssac.util.properties;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JFrame;

/**
 * ステータスを保持する機能を提供するプロパティ・コンポーネント。
 * 
 * @version 1.00 2008/03/24
 */
public class ExConfiguration extends ExProperties
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------

	static public final String KEYNAME_STATE			= ".state";
	static public final String KEYNAME_LOC			= ".location";
	static public final String KEYNAME_SIZE			= ".size";
	static public final String KEYNAME_DIVLOC			= ".divider";
	static public final String KEYNAME_LASTFILE		= ".lastFile";
	static public final String KEYNAME_FONT_FAMILY 	= ".font.family";
	static public final String KEYNAME_FONT_STYLE		= ".font.style";
	static public final String KEYNAME_FONT_SIZE		= ".font.size";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExConfiguration() {
		super();
	}
	
	public ExConfiguration(ExProperties defaults) {
		super(defaults);
	}
	
	public ExConfiguration(IExPropertyModel model) {
		super(model);
	}
	
	public ExConfiguration(IExPropertyModel model, ExProperties defaults) {
		super(model, defaults);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//--- KEYNAME_STATE
	
	public int getWindowState(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_STATE);
		Integer intStat = getInteger(key, null);
		return (intStat != null ? intStat.intValue() : JFrame.NORMAL);
	}
	
	public void setWindowState(String prefix, int state) {
		String key = makeKeyString(prefix, KEYNAME_STATE);
		if (state != JFrame.NORMAL) {
			setInteger(key, state);
		} else {
			clearProperty(key);
		}
	}
	
	//--- KEYNAME_LOC
	
	public Point getWindowLocation(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_LOC);
		return getPoint(key, null);
	}
	
	public void setWindowLocation(String prefix, Point pos) {
		String key = makeKeyString(prefix, KEYNAME_LOC);
		if (pos != null) {
			setPoint(key, pos);
		} else {
			clearProperty(key);
		}
	}
	
	//--- KEYNAME_SIZE
	
	public Dimension getWindowSize(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_SIZE);
		Dimension rd = getDimension(key, null);
		if (rd != null) {
			if (rd.width <= 0 || rd.height <= 0) {
				rd = null;
			}
		}
		return rd;
	}
	
	public void setWindowSize(String prefix, Dimension size) {
		String key = makeKeyString(prefix, KEYNAME_SIZE);
		if (size != null) {
			setDimension(key, size);
		} else {
			clearProperty(key);
		}
	}
	
	//--- KEYNAME_DIVLOC
	
	public int getDividerLocation(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_DIVLOC);
		int rl = -1;
		Integer intPos = getInteger(key, null);
		if (intPos != null && intPos.intValue() >= 0) {
			rl = intPos.intValue();
		}
		return rl;
	}
	
	public void setDividerLocation(String prefix, int pos) {
		String key = makeKeyString(prefix, KEYNAME_DIVLOC);
		if (pos >= 0) {
			setInteger(key, pos);
		} else {
			clearProperty(key);
		}
	}
	
	//--- KEYNAME_LASTFILE
	
	public String getLastFilename(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_LASTFILE);
		return getString(key, null);
	}
	
	public void setLastFilename(String prefix, String filename) {
		String key = makeKeyString(prefix, KEYNAME_LASTFILE);
		if (filename != null && filename.length() > 0) {
			setString(key, filename);
		} else {
			clearProperty(key);
		}
	}
	
	//--- KEYNAME_FONT_FAMILY
	
	public String getFontFamily(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_FONT_FAMILY);
		return getString(key, null);
	}
	
	public void setFontFamily(String prefix, String familyName) {
		String key = makeKeyString(prefix, KEYNAME_FONT_FAMILY);
		if (familyName != null && familyName.length() > 0) {
			setString(key, familyName);
		} else {
			clearProperty(key);
		}
	}
	
	//--- KEYNAME_FONT_STYLE
	
	public int getFontStyle(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_FONT_STYLE);
		return getInteger(key, Font.PLAIN);
	}
	
	public void setFontStyle(String prefix, int style) {
		String key = makeKeyString(prefix, KEYNAME_FONT_STYLE);
		if (style != 0) {
			setInteger(key, style);
		} else {
			clearProperty(key);
		}
	}
	
	//--- KEYNAME_FONT_SIZE
	
	public int getFontSize(String prefix) {
		String key = makeKeyString(prefix, KEYNAME_FONT_SIZE);
		return getInteger(key, 10);
	}
	
	public void setFontSize(String prefix, int fontSize) {
		String key = makeKeyString(prefix, KEYNAME_FONT_SIZE);
		if (fontSize > 0) {
			setInteger(key, fontSize);
		} else {
			clearProperty(key);
		}
	}
	
	//--- Font
	
	public Font getFont(String prefix) {
		String fontFamily = getFontFamily(prefix);
		int fontStyle = getFontStyle(prefix);
		int fontSize = getFontSize(prefix);
		if (fontFamily != null && fontFamily.length() > 0) {
			return new Font(fontFamily, fontStyle, fontSize);
		} else {
			return null;
		}
	}
	
	public void setFont(String prefix, Font font) {
		if (font != null) {
			setFontFamily(prefix, font.getFamily());
			setFontStyle(prefix, font.getStyle());
			setFontSize(prefix, font.getSize());
		} else {
			clearProperty(makeKeyString(prefix, KEYNAME_FONT_FAMILY));
			clearProperty(makeKeyString(prefix, KEYNAME_FONT_STYLE));
			clearProperty(makeKeyString(prefix, KEYNAME_FONT_SIZE));
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String makeKeyString(String prefix, String keyName) {
		if (prefix != null && prefix.length() > 0)
			return (prefix + keyName);
		else
			return keyName;
	}

}
