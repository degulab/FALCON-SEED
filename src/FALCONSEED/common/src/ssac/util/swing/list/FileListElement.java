/*
 * @(#)FileListElement.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.list;

import java.io.File;

/**
 * パス情報を保持するクラス。
 * 
 * @version 1.00 2008/03/24
 */
public class FileListElement
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final File targetFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FileListElement(String pathname) {
		this(new File(pathname));
	}
	
	public FileListElement(File file) {
		this.targetFile = file;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getFile() {
		return this.targetFile;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileListElement) {
			return this.targetFile.equals(((FileListElement)obj).targetFile);
		}
		
		// not equal
		return false;
	}

	@Override
	public int hashCode() {
		return this.targetFile.hashCode();
	}

	@Override
	public String toString() {
		String strout = this.targetFile.getAbsolutePath();
		if (this.targetFile.isDirectory()) {
			if (!strout.endsWith(File.separator)) {
				strout = strout + File.separator;
			}
		}
		return strout;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
