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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecDefFileTransferable.java	1.10	2011/02/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFileTransferable.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import ssac.falconseed.module.swing.tree.IMExecDefFile;
import ssac.util.io.VirtualFile;

/**
 * モジュール実行定義ファイルの標準転送データ。
 * 
 * @version 1.10	2011/02/15
 */
public class MExecDefFileTransferable implements Transferable, ClipboardOwner
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private IMExecDefFile[]	_files;

	static public DataFlavor localFileListFlavor;
	
	static {
		try {
			localFileListFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class=ssac.falconseed.module.swing.tree.IMExecDefFile");
		}
		catch (ClassNotFoundException cle) {
			System.err.println("error initializing ssac.aadl.module.MExecDefFileTransferable");
		}
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefFileTransferable(IMExecDefFile[] files) {
		this._files = new IMExecDefFile[files.length];
		System.arraycopy(files, 0, this._files, 0, files.length);
	}
	
	public MExecDefFileTransferable(Collection<? extends IMExecDefFile> files) {
		this._files = files.toArray(new IMExecDefFile[files.size()]);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された転送データから、モジュール実行定義ツリーのオブジェクトとなる
	 * ファイルデータの配列を取得する。
	 * サポートしている DataFlavor が存在しない場合は、<tt>null</tt> を返す。
	 * @throws NullPointerException	<em>transfer</em> が <tt>null</tt> の場合
	 */
	static public IMExecDefFile[] getFileDataFromTransferData(Transferable transfer) {
		try {
			Object data = transfer.getTransferData(localFileListFlavor);
			IMExecDefFile[] flist = (IMExecDefFile[])data;
			if (flist != null && flist.length > 0) {
				return flist;
			}
		}
		catch (Throwable ex) {
			// ignore exception
		}
		
		// not supported transfer data
		return null;
	}

	/**
	 * 指定された転送データから、このクラスでサポートされている抽象パスの配列を取得する。
	 * サポートしている DataFlavor が存在しない場合は、<tt>null</tt> を返す。
	 * @throws NullPointerException	<em>transfer</em> が <tt>null</tt> の場合
	 */
	static public VirtualFile[] getVirtualFilesFromTransferData(Transferable transfer) {
		// virtualFileListFlavor
		try {
			Object data = transfer.getTransferData(localFileListFlavor);
			IMExecDefFile[] flist = (IMExecDefFile[])data;
			if (flist != null && flist.length > 0) {
				ArrayList<VirtualFile> vflist = new ArrayList<VirtualFile>(flist.length);
				for (IMExecDefFile fdata : flist) {
					VirtualFile vf = fdata.getFile();
					if (vf != null) {
						vflist.add(vf);
					}
				}
				if (!vflist.isEmpty()) {
					return vflist.toArray(new VirtualFile[vflist.size()]);
				}
			}
		}
		catch (Throwable ex) {
			// ignore exception
		}
		
		// not supported transfer data
		return null;
	}
	
	static public boolean containsSupportedDataFlavor(DataFlavor[] transferFlavors) {
		if (transferFlavors != null) {
			for (DataFlavor flavor : transferFlavors) {
				if (localFileListFlavor.equals(flavor)) {
					return true;
				}
			}
			return false;
		}
		else {
			return false;
		}
	}

	//------------------------------------------------------------
	// Implements Transferable interfaces
	//------------------------------------------------------------
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (localFileListFlavor.equals(flavor)) {
			return (_files==null ? new IMExecDefFile[0] : _files);
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors() {
		if (isLocalFileListSupported()) {
			return new DataFlavor[]{localFileListFlavor};
		}
		else {
			return new DataFlavor[0];
		}
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (localFileListFlavor.equals(flavor)) {
			return true;
		}
		
		// not supported
		return false;
	}

	//------------------------------------------------------------
	// Implements ClipboardOwner interfaces
	//------------------------------------------------------------

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// No entry
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isLocalFileListSupported() {
		return (_files != null && _files.length > 0);
	}
}
