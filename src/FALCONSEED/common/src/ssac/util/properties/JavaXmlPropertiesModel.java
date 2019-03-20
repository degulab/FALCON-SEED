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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * <code>java.util.Properties</code> の実装となる、プロパティ・データモデル。
 * ストリーム入出力には <code>java.util.Properties</code> の XML 用実装を利用している。
 * 
 * @version 1.00 2008/03/24
 */
public class JavaXmlPropertiesModel extends JavaPropertiesModel
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JavaXmlPropertiesModel() {
		super();
	}
	
	public JavaXmlPropertiesModel(Properties defaults) {
		super(defaults);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// implement IExPropertyModel interfaces
	//------------------------------------------------------------

	public void loadFromStream(InputStream stream) throws IOException {
		super.loadFromXML(stream);
	}

	public void saveToStream(OutputStream stream, String comments) throws IOException {
		super.storeToXML(stream, comments);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
