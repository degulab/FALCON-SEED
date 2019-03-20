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
 * @(#)DefaultFileTransferable.java	1.20	2012/03/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DefaultFileTransferable.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing;


/**
 * @deprecated {@link ssac.falconseed.file.VirtualFileTransferable} に置き換えられたため、使用しない。
 * JAVA 標準ファイルの転送データ。
 * 
 * @version 1.20	2012/03/24
 * @since 1.10
 */
public class DefaultFileTransferable
//implements Transferable, ClipboardOwner
{
//	//------------------------------------------------------------
//	// Constants
//	//------------------------------------------------------------
//
//	//------------------------------------------------------------
//	// Fields
//	//------------------------------------------------------------
//	
//	List<File>			_javaFileList;
//	List<URI>			_uriList;
//	//String				_textUriList;
//	
//	static public DataFlavor javaFileListFlavor;
//	static public DataFlavor uriListFlavor;
//	//static public DataFlavor uriListTextFlavor;
//	//* テキスト形式のURIリストは現時点では対象としない
//	
//	static {
//		try {
//			javaFileListFlavor = DataFlavor.javaFileListFlavor;
//			uriListFlavor = new DataFlavor(DataFlavor.javaSerializedObjectMimeType+";class=java.util.List");
//			//uriListTextFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
//		}
//		catch (ClassNotFoundException cle) {
//			System.err.println("error initializing ssac.falconseed.data.swing.DefaultFileTransferable");
//		}
//	}
//
//	//------------------------------------------------------------
//	// Constructions
//	//------------------------------------------------------------
//	
//	public DefaultFileTransferable(File...files) {
//		this._javaFileList = createJavaFileList(files);
//		this._uriList = createUriList(files);
//		//createUriListText();
//	}
//	
//	public DefaultFileTransferable(Collection<? extends File> files) {
//		this._javaFileList = createJavaFileList(files);
//		this._uriList = createUriList(files);
//		//createUriListText();
//	}
//
//	/*
//	private void createUriListText() {
//		if (_uriList.isEmpty()) {
//			this._textUriList = "";
//		} else {
//			StringBuilder sb = new StringBuilder();
//			Iterator<URI> it = _uriList.iterator();
//			sb.append(it.next());
//			while (it.hasNext()) {
//				sb.append("\r\n");
//				sb.append(it.next());
//			}
//			this._textUriList = sb.toString();
//		}
//	}
//	*/
//
//	//------------------------------------------------------------
//	// Public interfaces
//	//------------------------------------------------------------
//	
//	/**
//	 * 指定された転送データから、抽象パスの配列を取得する。
//	 * サポートしている DataFlavor が存在しない場合は、<tt>null</tt> を返す。
//	 * @throws NullPointerException	<em>transfer</em> が <tt>null</tt> の場合
//	 */
//	static public File[] getFilesFromTransferData(Transferable transfer) {
//		// javaFileListFlavor
//		try {
//			Object data = transfer.getTransferData(javaFileListFlavor);
//			List<?> flist = (List<?>)data;
//			if (flist != null && !flist.isEmpty()) {
//				File[] files = new File[flist.size()];
//				for (int i = 0; i < flist.size(); i++) {
//					files[i] = (File)flist.get(i);
//				}
//				return files;
//			}
//		}
//		catch (Throwable ex) {
//			// ignore exception
//		}
//		
//		// uriListFlavor
//		try {
//			Object data = transfer.getTransferData(uriListFlavor);
//			List<?> flist = (List<?>)data;
//			if (flist != null && !flist.isEmpty()) {
//				File[] files = new File[flist.size()];
//				for (int i = 0; i < flist.size(); i++) {
//					URI uri = (URI)flist.get(i);
//					files[i] = new File(uri);
//				}
//				return files;
//			}
//		}
//		catch (Throwable ex) {
//			// ignore exception
//		}
//		
//		// uriListTextFlavor
//		/*
//		try {
//			Object data = transfer.getTransferData(uriListTextFlavor);
//			String strList = (String)data;
//			BufferedReader br = new BufferedReader(new StringReader(strList));
//			ArrayList<VirtualFile> flist = new ArrayList<VirtualFile>();
//			String struri;
//			while ((struri = br.readLine()) != null) {
//				if (!Strings.isNullOrEmpty(struri)) {
//					URI uri = new URI(struri);
//					VirtualFile vf = ModuleFileManager.fromURI(uri);
//					if (vf == null) {
//						throw new IllegalArgumentException("URI not supported : " + uri.toString());
//					}
//					flist.add(vf);
//				}
//			}
//			if (!flist.isEmpty()) {
//				return flist.toArray(new VirtualFile[flist.size()]);
//			}
//		}
//		catch (Throwable ex) {
//			// ignore exception
//		}
//		*/
//		
//		// not supported transfer data
//		return null;
//	}
//	
//	static public boolean isJavaFileListFlavor(DataFlavor flavor) {
//		return javaFileListFlavor.equals(flavor);
//	}
//	
//	static public boolean isUriListFlavor(DataFlavor flavor) {
//		return uriListFlavor.equals(flavor);
//	}
//
//	/*
//	static public boolean isUriListTextFlavor(DataFlavor flavor) {
//		return uriListTextFlavor.equals(flavor);
//	}
//	*/
//	
//	static public boolean isDefaultFileDataFlavor(DataFlavor flavor) {
//		if (isJavaFileListFlavor(flavor))
//			return true;
//		
//		if (isUriListFlavor(flavor))
//			return true;
//		
//		//if (isUriListTextFlavor(flavor))
//		//	return true;
//		
//		// not supported
//		return false;
//	}
//	
//	static public boolean containsSupportedDataFlavor(DataFlavor[] transferFlavors) {
//		if (transferFlavors != null) {
//			for (DataFlavor flavor : transferFlavors) {
//				if (isDefaultFileDataFlavor(flavor)) {
//					return true;
//				}
//			}
//		}
//		
//		// not supported
//		return false;
//	}
//
//	//------------------------------------------------------------
//	// Implements Transferable interfaces
//	//------------------------------------------------------------
//	
//	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
//	{
//		if (isJavaFileListFlavor(flavor)) {
//			return (_javaFileList == null ? Collections.<File>emptyList() : _javaFileList);
//		}
//		
//		if (isUriListFlavor(flavor)) {
//			return (_uriList == null ? Collections.<URI>emptyList() : _uriList);
//		}
//		
//		//if (isUriListTextFlavor(flavor)) {
//		//	return (_textUriList == null ? "" : _textUriList);
//		//}
//		
//		// not supported
//		throw new UnsupportedFlavorException(flavor);
//	}
//
//	public DataFlavor[] getTransferDataFlavors() {
//		if (isJavaFileListSupported()) {
//			return new DataFlavor[]{
//					javaFileListFlavor,
//					uriListFlavor,
//					//uriListTextFlavor,
//			};
//		}
//		else if (isUriListSupported()) {
//			return new DataFlavor[]{
//					uriListFlavor,
//					//uriListTextFlavor,
//			};
//		}
//		else {
//			return new DataFlavor[0];
//		}
//	}
//
//	public boolean isDataFlavorSupported(DataFlavor flavor) {
//		DataFlavor[] flavors = getTransferDataFlavors();
//		for (DataFlavor f : flavors) {
//			if (f.equals(flavor)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	//------------------------------------------------------------
//	// Implements ClipboardOwner interfaces
//	//------------------------------------------------------------
//
//	public void lostOwnership(Clipboard clipboard, Transferable contents) {
//		// No entry
//	}
//
//	//------------------------------------------------------------
//	// Internal methods
//	//------------------------------------------------------------
//
//	/**
//	 * 指定された <code>File</code> オブジェクトの配列から、
//	 * <code>File</code> オブジェクトのリストを生成する。
//	 * このメソッドでは、配列の内容についてはチェックしない。
//	 * @param files	<code>File</code> オブジェクトの配列
//	 * @return	<code>File</code> オブジェクトのリスト
//	 */
//	static protected List<File> createJavaFileList(File...files) {
//		List<File> list = new ArrayList<File>(Arrays.asList(files));
//		return list;
//	}
//	
//	/**
//	 * 指定された <code>File</code> オブジェクトのコレクションから、
//	 * <code>File</code> オブジェクトのリストを生成する。
//	 * このメソッドでは、コレクションの内容についてはチェックしない。
//	 * @param files	<code>File</code> オブジェクトのコレクション
//	 * @return	<code>File</code> オブジェクトのリスト
//	 */
//	static protected List<File> createJavaFileList(Collection<? extends File> files) {
//		List<File> list = new ArrayList<File>(files);
//		return list;
//	}
//	
//	/**
//	 * 指定された <code>File</code> オブジェクトの配列から、
//	 * <code>URI</code> オブジェクトのリストを生成する。
//	 * このメソッドは、{@link java.io.File#toURI()} メソッドで取得した URI を
//	 * 格納するリストを生成する。
//	 * @param files	<code>File</code> オブジェクトの配列
//	 * @return	<code>URI</code> オブジェクトのリスト
//	 */
//	static protected List<URI> createUriList(File...files) {
//		List<URI> list = new ArrayList<URI>(files.length);
//		for (File f : files) {
//			list.add(f.toURI());
//		}
//		return list;
//	}
//	
//	/**
//	 * 指定された <code>File</code> オブジェクトのコレクションから、
//	 * <code>URI</code> オブジェクトのリストを生成する。
//	 * このメソッドは、{@link java.io.File#toURI()} メソッドで取得した URI を
//	 * 格納するリストを生成する。
//	 * @param files	<code>File</code> オブジェクトのコレクション
//	 * @return	<code>URI</code> オブジェクトのリスト
//	 */
//	static protected List<URI> createUriList(Collection<? extends File> files) {
//		List<URI> list = new ArrayList<URI>(files.size());
//		for (File f : files) {
//			list.add(f.toURI());
//		}
//		return list;
//	}
//	
//	protected boolean isJavaFileListSupported() {
//		return (_javaFileList != null && !_javaFileList.isEmpty());
//	}
//	
//	protected boolean isUriListSupported() {
//		return (_uriList != null && !_uriList.isEmpty());
//	}
//	
//	//protected boolean isUriListTextSupported() {
//	//	return (!Strings.isNullOrEmpty(_textUriList));
//	//}
}
