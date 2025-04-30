/*
 * @(#)JavaXmlPropertiesModel.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
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
