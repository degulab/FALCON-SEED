/*
 * @(#)IResource.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.util.Set;

/**
 * リソース取得用インタフェース
 * 
 * @version 1.00 2008/03/24
 */
public interface IResource
{
	public Set getPropertyNameSet();
	public Object getProperty(String name, Object defaultValue);
	public boolean setProperty(String name, Object newValue);
}
