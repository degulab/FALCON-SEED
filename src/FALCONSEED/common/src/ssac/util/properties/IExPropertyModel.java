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
