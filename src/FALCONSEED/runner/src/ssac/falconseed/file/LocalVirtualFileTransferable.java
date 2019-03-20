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
 * @(#)LocalVirtualFileTransferable.java	1.20	2012/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;

import ssac.util.io.VirtualFile;

/**
 * <code>VirtualFile</code> の標準転送データ。
 * <code>VirtualFile</code> をサポートするプロセスのみで利用可能な転送データ。
 * 
 * @version 1.20	2012/03/14
 * @since 1.20
 */
public class LocalVirtualFileTransferable implements Transferable, ClipboardOwner
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	DataFlavor[]		_supportedFlavors;
	VirtualFile[]		_files;
	
	static public DataFlavor virtualFileListFlavor = VirtualFileTransferable.virtualFileListFlavor;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public LocalVirtualFileTransferable(VirtualFile[] files) {
		this._files = new VirtualFile[files.length];
		System.arraycopy(files, 0, this._files, 0, files.length);
		this._supportedFlavors = getSupportedDataFlavors();
	}
	
	public LocalVirtualFileTransferable(Collection<? extends VirtualFile> files) {
		this._files = files.toArray(new VirtualFile[files.size()]);
		this._supportedFlavors = getSupportedDataFlavors();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された転送データから、抽象パスの配列を取得する。
	 * サポートしている DataFlavor が存在しない場合は、<tt>null</tt> を返す。
	 * @throws NullPointerException	<em>transfer</em> が <tt>null</tt> の場合
	 */
	static public VirtualFile[] getFilesFromTransferData(Transferable transfer) {
		// virtualFileListFlavor
		try {
			Object data = transfer.getTransferData(virtualFileListFlavor);
			VirtualFile[] flist = (VirtualFile[])data;
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
	
	static public boolean isVirtualFileListFlavor(DataFlavor flavor) {
		return virtualFileListFlavor.equals(flavor);
	}
	
	static public boolean containsSupportableDataFlavor(DataFlavor[] transferFlavors) {
		if (transferFlavors != null) {
			for (DataFlavor flavor : transferFlavors) {
				if (isVirtualFileListFlavor(flavor))
				{
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

	public DataFlavor[] getTransferDataFlavors() {
		return _supportedFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		DataFlavor[] flavors = getTransferDataFlavors();
		for (DataFlavor f : flavors) {
			if (f.equals(flavor)) {
				return true;
			}
		}
		return false;
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (isVirtualFileListFlavor(flavor) && isVirtualFileListSupported()) {
			return _files;
		}
		
		// not supported
		throw new UnsupportedFlavorException(flavor);
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
	
	protected boolean isVirtualFileListSupported() {
		return (_files != null && _files.length > 0);
	}
	
	protected DataFlavor[] getSupportedDataFlavors() {
		if (isVirtualFileListSupported()) {
			return new DataFlavor[]{virtualFileListFlavor};
		} else {
			return new DataFlavor[0];
		}
	}
}
