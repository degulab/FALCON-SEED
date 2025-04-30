/*
 * @(#)EditorBuildOptions.java	4.0.0	2021/08/27 : for Java11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

/**
 * AADLEditor の操作によって可変となる、ビルド時のオプションを保持するクラス。
 * <blockquote>
 * このオブジェクトは、スレッドセーフではないので、並列処理で参照する場合は clone すること。
 * </blockquote>
 * 
 * @version 4.0.0
 * @since 4.0.0
 */
public class EditorBuildOptions implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** AADLコンパイル時に関連ライブラリも同梱する **/
	protected boolean	_genFatJar;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EditorBuildOptions()
	{
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public EditorBuildOptions clone()
	{
		EditorBuildOptions result;
        try {
    		result = (EditorBuildOptions)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
        return result;
	}
	
	/**
	 * AADLソースのビルド時に関連ライブラリを同梱するかどうかの設定を取得する。
	 * @return	関連ライブラリを同梱するなら <tt>true</tt>
	 * @since 4.0.0
	 */
	public boolean isEnabledAadlCompileFatJar()
	{
		return _genFatJar;
	}

	/**
	 * AADLソースのビルド時に関連ライブラリを同梱するかどうかを設定する。
	 * @param toEnable	関連ライブラリを同梱するなら <tt>true</tt>
	 * @since 4.0.0
	 */
	public void setEnableAadlCompileFatJar(boolean toEnable)
	{
		_genFatJar = toEnable;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
