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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EditorFrame.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.awt.Component;

import javax.swing.JPopupMenu;
import javax.swing.JTree;

/**
 * <code>JTree</code> コンポーネント用ポップアップメニュー。
 * このメニューは、ツリーに表示されているアイテム上にカーソルが
 * ある場合にポップアップメニューが表示されるようになる。
 * また、ポップアップメニュー表示の際、そのアイテムが選択されて
 * いない場合は選択する。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class JTreePopupMenu extends JPopupMenu
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private boolean	_closestSelection = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JTreePopupMenu() {
		super();
	}

	public JTreePopupMenu(String label) {
		super(label);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isClosestSelection() {
		return _closestSelection;
	}
	
	public void setClosestSelection(boolean toClosest) {
		_closestSelection = toClosest;
	}

	@Override
	public void show(Component invoker, int x, int y) {
		if (invoker instanceof JTree) {
			JTree tree = (JTree)invoker;
			if (acceptShowPopup(tree, x, y)) {
				if (!invoker.requestFocusInWindow()) {
					invoker.requestFocus();
				}
				super.show(invoker, x, y);
			}
		}
		else {
			if (!invoker.requestFocusInWindow()) {
				invoker.requestFocus();
			}
			super.show(invoker, x, y);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean acceptShowPopup(JTree tree, int x, int y) {
		int row;
		if (isClosestSelection()) {
			row = tree.getClosestRowForLocation(x, y);
		} else {
			row = tree.getRowForLocation(x, y);
		}
		if (row >= 0) {
			if (!tree.isRowSelected(row)) {
				tree.setSelectionRow(row);
			}
			return true;
		}
		
		// not accept
		return false;
	}
}
