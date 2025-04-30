/*
 * @(#)DefaultFileTreeData.java	1.20	2012/03/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * 汎用ファイルツリー専用ノードに格納する標準のデータオブジェクト。
 * 
 * @version 1.20	2012/03/27
 * @since 1.20
 */
public class DefaultFileTreeData extends AbFileTreeData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private VirtualFile	_file;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたファイルオブジェクトを保持する、新しいインスタンスを生成する。
	 * @param file	<code>VirtualFile</code> オブジェクト
	 */
	public DefaultFileTreeData(VirtualFile file) {
		this._file = file;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモデルに関連付けられているファイルオブジェクトを取得する。
	 * @return	<code>VirtualFile</code> オブジェクト
	 */
	public VirtualFile getFile() {
		return _file;
	}

	/**
	 * このモデルに関連付けられているファイルオブジェクトを、
	 * 指定された抽象パスに置き換える。
	 * このメソッドは，このオブジェクトのフィールドを変更する。
	 * @param newFile	新しい抽象パス
	 * @return	このオブジェクト
	 */
	public IFileTreeData replaceFile(VirtualFile newFile) {
		setFile(newFile);
		return this;
	}

	/**
	 * このモデルに新しいファイルオブジェクトを設定する。
	 * @param file	<code>VirtualFile</code> オブジェクト
	 */
	public void setFile(VirtualFile file) {
		this._file = file;
	}

	/**
	 * このモデルのハッシュコードを返す。
	 * @return	ハッシュコード
	 */
	@Override
	public int hashCode() {
		return (_file==null ? 0 : _file.hashCode());
	}

	/**
	 * このモデルが指定されたオブジェクトと等しいかどうかを判定する。
	 * 引数が <tt>null</tt> ではなく、このモデルと同じファイルまたはディレクトリを示す抽象パス名である場合だけ <tt>true</tt> を返す。
	 * @param obj	このモデルと比較されるオブジェクト
	 * @return	2 つのオブジェクトが同じ場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj!=null && obj.getClass()==this.getClass()) {
			VirtualFile aFile = ((DefaultFileTreeData)obj)._file;
			if (aFile == null) {
				return (this._file == null);
			} else {
				return aFile.equals(this._file);
			}
		}
		
		return false;
	}
	
	/**
	 * 指定された抽象パスから、このモデルの子を表すファイルモデルを生成する。
	 * このメソッドは <code>listFiles</code> メソッドからも呼び出される。
	 * @param file	格納するファイルオブジェクト
	 * @return <code>IFileTreeData</code> を実装するオブジェクト
	 */
	public IFileTreeData createFileDataForChild(VirtualFile file) {
		return new DefaultFileTreeData(file);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
