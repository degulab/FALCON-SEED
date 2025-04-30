/*
 * @(#)DefaultFileFactory.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.net.URI;

/**
 * {@link java.io.File} を実体とする {@link VirtualFile} インスタンスを
 * 生成するための {@link VirtualFileFactory} の実装。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class DefaultFileFactory implements VirtualFileFactory
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private DefaultFileFactory _instance;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DefaultFileFactory() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public DefaultFileFactory getInstance() {
		if (_instance == null) {
			_instance = new DefaultFileFactory();
		}
		return _instance;
	}

	//------------------------------------------------------------
	// Implements VirtualFileFactory interfaces
	//------------------------------------------------------------
	
	public DefaultFile newFile(String pathname) {
		return new DefaultFile(pathname);
	}
	
	public DefaultFile newFile(String parent, String child) {
		return new DefaultFile(parent, child);
	}
	
	public DefaultFile newFile(VirtualFile parent, String child) {
		return new DefaultFile((DefaultFile)parent, child);
	}
	
	public DefaultFile newFile(URI uri) {
		return new DefaultFile(uri);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
