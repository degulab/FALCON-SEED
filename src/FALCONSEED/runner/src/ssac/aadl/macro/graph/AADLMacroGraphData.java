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
 *  <author> Hideaki Yagi (MRI)
 */
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
