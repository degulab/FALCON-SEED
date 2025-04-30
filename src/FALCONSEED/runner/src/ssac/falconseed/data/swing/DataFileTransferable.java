/*
 * @(#)DataFileTransferable.java	1.20	2012/03/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DataFileTransferable.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing;


/**
 * @deprecated {@link ssac.falconseed.file.LocalVirtualFileTransferable} に置き換えられたため、使用しない
 * データファイルの転送データ。
 * 
 * @version 1.20	2012/03/24
 * @since 1.10
 */
@Deprecated
public class DataFileTransferable
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
//	VirtualFile[]		_files;
//	
//	static public DataFlavor virtualFileListFlavor;
//	
//	static {
//		try {
//			virtualFileListFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class=ssac.util.io.VirtualFile");
//		}
//		catch (ClassNotFoundException cle) {
//			System.err.println("error initializing ssac.falconseed.data.swing.DataFileTransferable");
//		}
//	}
//
//	//------------------------------------------------------------
//	// Constructions
//	//------------------------------------------------------------
//	
//	public DataFileTransferable(VirtualFile[] files) {
//		this._files = new VirtualFile[files.length];
//		System.arraycopy(files, 0, this._files, 0, files.length);
//	}
//	
//	public DataFileTransferable(Collection<? extends VirtualFile> files) {
//		this._files = files.toArray(new VirtualFile[files.size()]);
//	}
//	
//	public DataFileTransferable(File[] files) {
//		int len = files.length;
//		this._files = new VirtualFile[len];
//		for (int i = 0; i < len; i++) {
//			this._files[i] = ModuleFileManager.fromJavaFile(files[i]);
//		}
//	}
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
//	static public VirtualFile[] getFilesFromTransferData(Transferable transfer) {
//		// virtualFileListFlavor
//		try {
//			Object data = transfer.getTransferData(virtualFileListFlavor);
//			VirtualFile[] flist = (VirtualFile[])data;
//			if (flist != null && flist.length > 0) {
//				return flist;
//			}
//		}
//		catch (Throwable ex) {
//			// ignore exception
//		}
//		
//		// not supported transfer data
//		return null;
//	}
//	
//	static public boolean isVirtualFileListFlavor(DataFlavor flavor) {
//		return virtualFileListFlavor.equals(flavor);
//	}
//	
//	static public boolean containsSupportedDataFlavor(DataFlavor[] transferFlavors) {
//		if (transferFlavors != null) {
//			for (DataFlavor flavor : transferFlavors) {
//				if (isVirtualFileListFlavor(flavor))
//				{
//					return true;
//				}
//			}
//			return false;
//		}
//		else {
//			return false;
//		}
//	}
//
//	//------------------------------------------------------------
//	// Implements Transferable interfaces
//	//------------------------------------------------------------
//	
//	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
//	{
//		if (isVirtualFileListFlavor(flavor)) {
//			return (_files==null ? new VirtualFile[0] : _files);
//		}
//		
//		throw new UnsupportedFlavorException(flavor);
//	}
//
//	public DataFlavor[] getTransferDataFlavors() {
//		if (isVirtualFileListSupported()) {
//			return new DataFlavor[]{virtualFileListFlavor};
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
//	protected boolean isVirtualFileListSupported() {
//		return (_files != null && _files.length > 0);
//	}
}
