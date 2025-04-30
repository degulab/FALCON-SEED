/*
 * @(#)AbMExecDefFile.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * モジュール実行定義ファイルモデルの共通インタフェースを実装する抽象クラス。
 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
 * 基準とし、大文字小文字を区別しないでチェックする。
 * 
 * @version 1.00	2010/12/20
 */
public abstract class AbMExecDefFile implements IMExecDefFile
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected IMExecDefFile[] EMPTY_ARRAY = new IMExecDefFile[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final VirtualFile	_file;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbMExecDefFile(VirtualFile file) {
		if (file == null)
			throw new NullPointerException("The specified file is null.");
		this._file = file;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモデルに関連付けられているファイルの実体が存在するかを検証する。
	 * @return	存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean exists() {
		return _file.exists();
	}

	/**
	 * このモデルに関連付けられているファイルが普通のファイルかどうかを判定する。
	 * ファイルは、それがディレクトリではなく、システムに依存するほかの基準を満たす場合に
	 * 「普通」のファイルと見なされる。
	 * @return	ファイルが存在し、さらにそれが普通のファイルである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isFile() {
		return _file.isFile();
	}

	/**
	 * このモデルに関連付けられているファイルがディレクトリかどうかを判定する。
	 * @return	ファイルが存在し、さらにそれがディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isDirectory() {
		return _file.isDirectory();
	}

	/**
	 * このモデルに関連付けられているファイルの最終更新時刻を取得する。
	 * @return	ファイルが最後に変更された時刻を表す <code>long</code> 値。
	 * 			エポック (1970 年 1 月 1 日 0 時 0 分 0 秒、グリニッジ標準時) からミリ秒単位で測定。
	 * 			ファイルが存在しないか、入出力エラーが発生した場合は 0L
	 */
	public long lastModified() {
		return _file.lastModified();
	}

	/**
	 * このモデルに関連付けられているファイルのパスを含まないファイル名を取得する。
	 * @return	パスを含まないファイル名
	 */
	public String getFilename() {
		return _file.getName();
	}

	/**
	 * このモデルの表示名を取得する。
	 * @return	モデルの表示名
	 */
	public String getDisplayName() {
		return _file.getName();
	}

	/**
	 * このモデルに関連付けられているファイルオブジェクトを取得する。
	 * @return	<code>VirtualFile</code> オブジェクト
	 */
	public VirtualFile getFile() {
		return _file;
	}

	/**
	 * このモデルのハッシュコードを返す。
	 * @return	ハッシュコード
	 */
	@Override
	public int hashCode() {
		return _file.hashCode();
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
		
		if (obj instanceof AbMExecDefFile) {
			return ((AbMExecDefFile)obj)._file.equals(this._file);
		}
		
		return false;
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * このメソッドが返す文字列は、{@link #getDisplayName()} が返す文字列と同じものとなる。
	 * @return	このオブジェクトの文字列表現
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
