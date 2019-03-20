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
 */
/*
 * @(#)MacroFilterDefArgList.java	2.0.0	2012/10/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.util.ArrayList;
import java.util.Collection;

import ssac.falconseed.module.swing.FilterArgEditModel;

/**
 * マクロフィルタ定義の引数リスト。
 * このリストは、マクロフィルタ専用の引数定義を保持する。
 * 引数定義のオブジェクトには、{@link ModuleArgConfig} を利用する。
 * <p>なお、このオブジェクトの要素には <tt>null</tt> を許容しない。
 * 
 * @version 2.0.0	2012/10/28
 * @since 2.0.0
 */
public class MacroFilterDefArgList extends ArrayList<FilterArgEditModel>
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

	public MacroFilterDefArgList() {
		super();
	}

	public MacroFilterDefArgList(int initialCapacity) {
		super(initialCapacity);
	}

	public MacroFilterDefArgList(Collection<? extends FilterArgEditModel> c) {
		super(c.size());
		int argno = 1;
		for (FilterArgEditModel element : c) {
			if (element == null)
				throw new IllegalArgumentException("FilterArgEditModel collection included Null!");
			element.setArgNo(argno++);
			super.add(element);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public boolean add(FilterArgEditModel element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		element.setArgNo(size()+1);
		return super.add(element);
	}
	
	@Override
	public void add(int index, FilterArgEditModel element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		super.add(index, element);
		updateArgNo(index);
	}

	@Override
	public boolean addAll(Collection<? extends FilterArgEditModel> c) {
		int argno = size()+1;
		for (ModuleArgConfig element : c) {
			if (element == null)
				throw new IllegalArgumentException("ModuleArgConfig collection included Null!");
			element.setArgNo(argno++);
		}
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends FilterArgEditModel> c) {
		for (ModuleArgConfig element : c) {
			if (element == null)
				throw new IllegalArgumentException("ModuleArgConfig collection included Null!");
		}
		boolean ret = super.addAll(index, c);
		if (ret) {
			updateArgNo(index);
		}
		return ret;
	}

	@Override
	public FilterArgEditModel set(int index, FilterArgEditModel element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		element.setArgNo(index+1);
		return super.set(index, element);
	}

	@Override
	public MacroFilterDefArgList clone() {
		// deep-copy
		MacroFilterDefArgList newlist = (MacroFilterDefArgList)super.clone();
		for (int index = 0; index < size(); index++) {
			newlist.fastSet(index, get(index).clone());
		}
		return newlist;
	}

	@Override
	public boolean remove(Object o) {
		int removeIndex = indexOf(o);
		if (removeIndex >= 0) {
			remove(removeIndex);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public FilterArgEditModel remove(int index) {
		FilterArgEditModel removed = null;
		try {
			removed = super.remove(index);
		}
		catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		
		if (removed != null) {
			updateArgNo(index);
		}
		return removed;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void fastAdd(final FilterArgEditModel element) {
		super.add(element);
	}

	protected void fastAdd(int index, final FilterArgEditModel element) {
		super.add(index, element);
	}
	
	protected void fastAddAll(Collection<? extends FilterArgEditModel> c) {
		super.addAll(c);
	}
	
	protected void fastAddAll(int index, Collection<? extends FilterArgEditModel> c) {
		super.addAll(index, c);
	}
	
	protected FilterArgEditModel fastSet(int index, FilterArgEditModel element) {
		return super.set(index, element);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
		updateArgNo(toIndex);
	}
	
	protected void updateArgNo(int fromIndex) {
		for (int i = fromIndex; i < size(); i++) {
			get(i).setArgNo(i+1);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
