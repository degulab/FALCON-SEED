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
 * @(#)VirtualFileTransferable.java	1.20	2012/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ssac.aadl.module.ModuleFileManager;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.VirtualFile;

/**
 * <code>VirtualFile</code> の標準転送データ。
 * 指定された <code>VirtualFile</code> は、<code>java.io.File</code> または <code>java.net.URI</code> に
 * 変換可能な場合、転送可能なフレーバーを追加する。
 * 
 * @version 1.20	2012/03/14
 * @since 1.20
 */
public class VirtualFileTransferable implements Transferable, ClipboardOwner
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	DataFlavor[]		_supportedFlavors;
	VirtualFile[]		_files;
	List<File>			_javaFileList;
	List<URI>			_uriList;
	String				_textUriList;
	
	static public DataFlavor javaFileListFlavor;
	static public DataFlavor virtualFileListFlavor;
	static public DataFlavor uriListFlavor;
	static public DataFlavor uriListTextFlavor;
	//* テキスト形式のURIリストは現時点では対象としない
	
	static {
		try {
			javaFileListFlavor = DataFlavor.javaFileListFlavor;
			virtualFileListFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class=ssac.util.io.VirtualFile");
			uriListFlavor = new DataFlavor(DataFlavor.javaSerializedObjectMimeType+";class=java.util.List");
			uriListTextFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
		}
		catch (ClassNotFoundException cle) {
			System.err.println("error initializing " + VirtualFileTransferable.class.getName());
		}
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public VirtualFileTransferable(VirtualFile[] files) {
		this._files = new VirtualFile[files.length];
		System.arraycopy(files, 0, this._files, 0, files.length);
		this._javaFileList = createJavaFileList(this._files);
		this._uriList = createUriList(this._files);
		this._textUriList = null;
//		this._textUriList = createTextUriList(_uriList);
		this._supportedFlavors = getSupportedDataFlavors();
	}
	
	public VirtualFileTransferable(Collection<? extends VirtualFile> files) {
		this._files = files.toArray(new VirtualFile[files.size()]);
		this._javaFileList = createJavaFileList(this._files);
		this._uriList = createUriList(this._files);
		this._textUriList = null;
//		this._textUriList = createTextUriList(_uriList);
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
	@SuppressWarnings("unchecked")
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
		
		// javaFileListFlavor
		try {
			Object data = transfer.getTransferData(javaFileListFlavor);
			List flist = (List)data;
			if (flist != null && !flist.isEmpty()) {
				VirtualFile[] files = new VirtualFile[flist.size()];
				for (int i = 0; i < flist.size(); i++) {
					File f = (File)flist.get(i);
					files[i] = ModuleFileManager.fromJavaFile(f);
				}
				return files;
			}
		}
		catch (Throwable ex) {
			// ignore exception
		}
		
		// uriListFlavor
		try {
			Object data = transfer.getTransferData(uriListFlavor);
			List flist = (List)data;
			if (flist != null && !flist.isEmpty()) {
				VirtualFile[] files = new VirtualFile[flist.size()];
				for (int i = 0; i < flist.size(); i++) {
					URI uri = (URI)flist.get(i);
					files[i] = ModuleFileManager.fromURI(uri);
					if (files[i] == null) {
						throw new IllegalArgumentException("URI not supported : " + uri.toString());
					}
				}
				return files;
			}
		}
		catch (Throwable ex) {
			// ignore exception
		}
		
		// uriListTextFlavor
		/*
		try {
			Object data = transfer.getTransferData(uriListTextFlavor);
			String strList = (String)data;
			BufferedReader br = new BufferedReader(new StringReader(strList));
			ArrayList<VirtualFile> flist = new ArrayList<VirtualFile>();
			String struri;
			while ((struri = br.readLine()) != null) {
				if (!Strings.isNullOrEmpty(struri)) {
					URI uri = new URI(struri);
					VirtualFile vf = ModuleFileManager.fromURI(uri);
					if (vf == null) {
						throw new IllegalArgumentException("URI not supported : " + uri.toString());
					}
					flist.add(vf);
				}
			}
			if (!flist.isEmpty()) {
				return flist.toArray(new VirtualFile[flist.size()]);
			}
		}
		catch (Throwable ex) {
			// ignore exception
		}
		*/
		
		// not supported transfer data
		return null;
	}
	
	static public boolean isVirtualFileListFlavor(DataFlavor flavor) {
		return virtualFileListFlavor.equals(flavor);
	}
	
	static public boolean isJavaFileListFlavor(DataFlavor flavor) {
		return javaFileListFlavor.equals(flavor);
	}
	
	static public boolean isUriListFlavor(DataFlavor flavor) {
		return uriListFlavor.equals(flavor);
	}

	static public boolean isUriListTextFlavor(DataFlavor flavor) {
		return uriListTextFlavor.equals(flavor);
	}
	
	static public boolean containsSupportableDataFlavor(DataFlavor[] transferFlavors) {
		if (transferFlavors != null) {
			for (DataFlavor flavor : transferFlavors) {
				if (isVirtualFileListFlavor(flavor)
					|| isJavaFileListFlavor(flavor)
					|| isUriListFlavor(flavor)
					/*|| isUriListTextFlavor(flavor)*/)
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
		else if (isJavaFileListFlavor(flavor) && isJavaFileListSupported()) {
			return _javaFileList;
		}
		else if (isUriListFlavor(flavor) && isUriListSupported()) {
			return _uriList;
		}
		else if (isUriListTextFlavor(flavor) && isUriListTextSupported()) {
			return _textUriList;
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
	
	static private List<File> createJavaFileList(VirtualFile[] files) {
		List<File> list = new ArrayList<File>(files.length);
		for (VirtualFile f : files) {
			if (f instanceof DefaultFile) {
				list.add(((DefaultFile)f).getJavaFile());
			}
		}
		return (list.isEmpty() ? null : list);
	}
	
	static private List<URI> createUriList(VirtualFile[] files) {
		List<URI> list = new ArrayList<URI>(files.length);
		for (VirtualFile f : files) {
			list.add(f.toURI());
		}
		return (list.isEmpty() ? null : list);
	}
	
//	static private String createTextUriList(Collection<? extends URI> uris) {
//		if (uris == null || uris.isEmpty())
//			return null;
//
//		String lineSeparator = System.getProperty("line.separator");
//		StringBuilder sb = new StringBuilder();
//		Iterator<? extends URI> it = uris.iterator();
//		sb.append(it.next());
//		while (it.hasNext()) {
//			sb.append(lineSeparator);
//			sb.append(it.next());
//		}
//		return sb.toString();
//	}
	
	protected boolean isVirtualFileListSupported() {
		return (_files != null && _files.length > 0);
	}
	
	protected boolean isJavaFileListSupported() {
		return (_javaFileList != null && !_javaFileList.isEmpty());
	}
	
	protected boolean isUriListSupported() {
		return (_uriList != null && !_uriList.isEmpty());
	}
	
	protected boolean isUriListTextSupported() {
		return (!Strings.isNullOrEmpty(_textUriList));
	}
	
	protected DataFlavor[] getSupportedDataFlavors() {
		ArrayList<DataFlavor> list = new ArrayList<DataFlavor>(4);
		
		if (isVirtualFileListSupported()) {
			list.add(virtualFileListFlavor);
		}
		
		if (isJavaFileListSupported()) {
			list.add(javaFileListFlavor);
		}
		
		if (isUriListSupported()) {
			list.add(uriListFlavor);
		}
		
		if (isUriListTextSupported()) {
			list.add(uriListTextFlavor);
		}
		
		return list.toArray(new DataFlavor[list.size()]);
	}
}
