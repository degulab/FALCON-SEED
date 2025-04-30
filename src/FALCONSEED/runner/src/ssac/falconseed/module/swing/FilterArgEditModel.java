/*
 * @(#)ModuleArgConfig.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgConfig.java	2.0.0	2012/10/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.IModuleArgValue;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.args.IMExecArgParam;

/**
 * 引数定義編集用のデータモデル。
 * このオブジェクトは編集用であり、データのインスタンスでハッシュ値を生成するため、
 * 内部の値によらず、<code>equals</code> メソッドはインスタンス値で判定する。
 * このクラスでは、ユーザー定義データを保持することができる。
 * このオブジェクトのクローン時には、ユーザー定義データはシャローコピーとなる。
 * 
 * @version 3.1.0	2014/05/16
 * @since 3.1.0
 */
public class FilterArgEditModel extends ModuleArgConfig implements IFilterArgEditModel
{
	// @since 3.1.0 : added 'IFilterArgEditModel' interface
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FilterArgEditModel(final int argno, final ModuleArgData argdef) {
		super(argno, argdef);
	}
	
	public FilterArgEditModel(final int argno, final ModuleArgType type, final String desc, final Object value) {
		super(argno, type, desc, value);
	}
	
	public FilterArgEditModel(final int argno, final ModuleArgType type, final String desc, final IMExecArgParam param, final Object value) {
		super(argno, type, desc, param, value);
	}
	
	public FilterArgEditModel(final int argno, final IModuleArgValue value) {
		super(argno, value);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの複製を生成する。
	 */
	public FilterArgEditModel clone() {
		FilterArgEditModel newData = (FilterArgEditModel)super.clone();
		return newData;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}
	
	public boolean equalValues(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@");
		sb.append(Integer.toHexString(hashCode()));
		sb.append("[");
		appendParameters(sb);
		sb.append("]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
