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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IExPropertyModel.java	2.0.0	2012/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IExPropertyModel.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IExPropertyModel.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * プロパティを保持するデータモデルのインタフェース。
 * プロパティの操作とストリーム入出力の機能を提供する。
 * 
 * @version 2.0.0	2012/10/15
 */
public interface IExPropertyModel
{
	public void clear();
	public void clearProperty(String key);
	public boolean containsKey(String key);
	//public boolean hasValue(String key);
	//public boolean isEmpty(String key);
	public String getValue(String key);
	public void setValue(String key, String value);
	
	public boolean canLoadFromStream();
	public boolean canSaveToStream();
	public void loadFile(File file) throws IOException;
	public void saveFile(File file, String comments) throws IOException;
	public void loadFromStream(InputStream stream) throws IOException;
	public void saveToStream(OutputStream stream, String comments) throws IOException;
	
	public Set<Object> keySet();
}
