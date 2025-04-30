/*
 * @(#)AADLMacroGraphData.java	1.20	2012/03/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.graph;

import java.awt.image.BufferedImage;

import ssac.util.io.VirtualFile;

/**
 * AADLマクロを <code>GraphViz</code> を使用して生成されたグラフ情報を保持するクラス。
 * 
 * @version 1.20	2012/03/19
 * @since 1.20
 */
public class AADLMacroGraphData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** AADLマクロファイルの抽象パス **/
	private final VirtualFile	_vfMacro;
	
	private VirtualFile	_vfDot;
	private VirtualFile	_vfImage;
	private BufferedImage	_image;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLMacroGraphData(VirtualFile macrofile) {
		this._vfMacro = macrofile;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFile getMacroFile() {
		return _vfMacro;
	}
	
	public VirtualFile getDotFile() {
		return _vfDot;
	}
	
	public VirtualFile getImageFile() {
		return _vfImage;
	}
	
	public BufferedImage getGraphImage() {
		return _image;
	}
	
	public void setDotFile(VirtualFile file) {
		_vfDot = file;
	}
	
	public void setImageFile(VirtualFile file) {
		_vfImage = file;
	}
	
	public void setGraphImage(BufferedImage image) {
		_image = image;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
