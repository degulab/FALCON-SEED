/*
 * @(#)TreeFileData.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * 抽象パスを保持するファイルモデル。
 * このオブジェクトは、基本的に単一の抽象パスを保持する。
 * このオブジェクトは不変オブジェクトである。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class TreeFileData extends AbTreeFileData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public TreeFileData(VirtualFile file) {
		super(file);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモデルに関連付けられているファイルオブジェクトを、
	 * 指定された抽象パスに置き換えた、新しいモデルを生成する。
	 * 基本的に、抽象パス以外の情報は複製される。
	 * @param newFile	新しい抽象パス
	 * @return	生成された <code>ITreeFileData</code> オブジェクト
	 */
	public ITreeFileData replaceFile(VirtualFile newFile) {
		return new TreeFileData(newFile);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected TreeFileData createFileData(VirtualFile file) {
		return new TreeFileData(file);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
