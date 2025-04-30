/*
 * @(#)DTCContentTransferTreeData.java	1.1.0	2023/01/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import dtalge.container.editor.content.DtContainerContentTypes;
import ssac.util.logging.AppLogger;

/**
 * データコンテナ要素のノードを集約する、クリップボードコピー用ツリールート。
 * 転送データは、このオブジェクトを起点として保持される。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentTransferTreeData implements Transferable, ClipboardOwner
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static public DataFlavor dtcContentTreeDataFravor;
	
	static {
		try {
			dtcContentTreeDataFravor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class=dtalge.container.editor.content.common.tree.DTCContentTransferTreeData");
		}
		catch (ClassNotFoundException cle) {
			System.err.println("error initializing dtalge.container.editor.content.common.tree.DTCContentTransferTreeData");
		}
	}
	
	static private DataFlavor[]	FULL_SUPPORTED_FLAVORS	= { dtcContentTreeDataFravor };
	static private DataFlavor[] EMPTY_SUPPORTED_FLAVORS	= new DataFlavor[0];
	
	/** 転送対象のトップツリーノードの配列 **/
	protected DTCContentTransferTreeNode[]	_treeNodes;
	/** <em>_treeNodes</em> に格納されている最上位ツリーノードのコンテントタイプのセット **/
	protected Set<DtContainerContentTypes>	_nodeContentTypes;
	/** 転送対象トップノードを貼り付け可能な、貼り付け選択位置のコンテントタイプのセット **/
	protected Set<DtContainerContentTypes>	_allowCopyDestTypes;
	/** 転送対象トップノードを貼り付け可能な、貼り付け選択位置の親のコンテントタイプのセット **/
	protected Set<DtContainerContentTypes>	_allowCopyDestParentTypes;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 内容が空の、新しいインスタンスを生成する。
	 */
	public DTCContentTransferTreeData() {
		_allowCopyDestParentTypes = Collections.emptySet();
		_allowCopyDestTypes = Collections.emptySet();
		_nodeContentTypes = Collections.emptySet();
		_treeNodes = new DTCContentTransferTreeNode[0];
	}
	
	/**
	 * 指定されたパラメータで、コピー可能な転送データを生成する。
	 * @param nodes	コピー対象のツリーノードの配列
	 * @throws NullPointerException	引数が {@code null} の場合
	 */
	public DTCContentTransferTreeData(IDtContainerContentTreeNode[] nodes) {
		if (nodes == null)
			throw new NullPointerException("Tree nodes is null");
		
		_allowCopyDestParentTypes = new HashSet<DtContainerContentTypes>();
		_allowCopyDestTypes = new HashSet<DtContainerContentTypes>();
		_nodeContentTypes = new HashSet<DtContainerContentTypes>();
		_treeNodes = new DTCContentTransferTreeNode[nodes.length];
		for (int i = 0; i < _treeNodes.length; i++) {
			_treeNodes[i] = new DTCContentTransferTreeNode(nodes[i]);
			_nodeContentTypes.add(nodes[i].getContentType());
			appendAllowCopyContentTypes(_allowCopyDestParentTypes, _allowCopyDestTypes, nodes[i].getContentType(), nodes.length);
		}
	}
	
	/**
	 * 指定されたパラメータで、コピー可能な転送データを生成する。
	 * @param nodes	コピー対象のツリーノードのコレクション
	 * @throws NullPointerException	引数が {@code null} の場合
	 */
	public DTCContentTransferTreeData(Collection<IDtContainerContentTreeNode> nodes) {
		if (nodes == null)
			throw new NullPointerException("Tree nodes is null");
		
		_allowCopyDestParentTypes = new HashSet<DtContainerContentTypes>();
		_allowCopyDestTypes = new HashSet<DtContainerContentTypes>();
		_nodeContentTypes = new HashSet<DtContainerContentTypes>();
		_treeNodes = new DTCContentTransferTreeNode[nodes.size()];
		int i = 0;
		for (IDtContainerContentTreeNode ndTarget : nodes) {
			_treeNodes[i] = new DTCContentTransferTreeNode(ndTarget);
			_nodeContentTypes.add(ndTarget.getContentType());
			appendAllowCopyContentTypes(_allowCopyDestParentTypes, _allowCopyDestTypes, ndTarget.getContentType(), nodes.size());
			++i;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 転送データに含まれるトップレベルのツリーノードすべてのコンテントタイプを取得する。
	 * @return	コンテントタイプのセット(編集不可)
	 */
	public Set<DtContainerContentTypes> getToplevelContentTypes() {
		return Collections.unmodifiableSet(_nodeContentTypes);
	}
	
	public DTCContentTransferTreeNode[] getToplevelNodes() {
		return _treeNodes;
	}

	/**
	 * 挿入位置のコンテントタイプが、現在の転送データを貼り付け可能なものかどうかを判定する。
	 * @param ndTarget	判定に使用する、貼り付け位置のツリーノード
	 * @return	貼り付け可能位置なら {@code true}
	 */
	public boolean isAllowPasteTo(IDtContainerContentTreeNode ndTarget) {
		if (ndTarget == null)
			return false;	// 貼り付け位置なし
		
		// 貼り付け位置のコンテントタイプ
		DtContainerContentTypes ctTarget = ndTarget.getContentType();
		if (!_allowCopyDestTypes.contains(ctTarget)) {
			if (!_allowCopyDestParentTypes.contains(ndTarget.getParent().getContentType())) {
				// 選択位置もしくはその親のコンテントタイプへの貼り付けは、不可
				return false;
			}
		}
		
		// 詳細な判定
		
		// 貼り付け可能
		return true;
	}
	
	public void clean() {
		_allowCopyDestParentTypes.clear();
		_allowCopyDestTypes.clear();
		_nodeContentTypes.clear();
		if (_treeNodes != null) {
			for (DTCContentTransferTreeNode node : _treeNodes) {
				node.clean();
			}
			_treeNodes = null;
		}
	}
	
	public boolean isEmpty() {
		return (_treeNodes == null || _treeNodes.length <= 0);
	}
	
	public int getNodeCount() {
		return (_treeNodes==null ? 0 : _treeNodes.length);
	}
	
	static public boolean isContentTreeDataFlavor(DataFlavor flavor) {
		return dtcContentTreeDataFravor.equals(flavor);
	}

	/**
	 * 指定された転送データから、転送データを取得する。
	 * サポートしている DataFlavor が存在しない場合は、<tt>null</tt> を返す。
	 * @throws NullPointerException	<em>transfer</em> が <tt>null</tt> の場合
	 */
	static public DTCContentTransferTreeData getDataFromTransferable(Transferable transfer) {
		try {
			Object data = transfer.getTransferData(dtcContentTreeDataFravor);
			if (data != null) {
				return (DTCContentTransferTreeData)data;
			}
		}
		catch (Throwable ignoreEx) {}
		
		// not supported transfer data
		return null;
	}
	
	static public boolean containsSupportedDataFlavor(DataFlavor[] transferFlavors) {
		if (transferFlavors != null) {
			for (DataFlavor flavor : transferFlavors) {
				if (isContentTreeDataFlavor(flavor)) {
					return true;
				}
			}
			return false;
		}
		else {
			return false;
		}
	}
	
	/**
	 * <em>srcContentType</em> が貼り付け可能なコンテントタイプを、<em>allowContentTypes</em> に追加する。
	 * 貼り付け可能なコンテントタイプは、ソースのノード数も考慮する。
	 * @param allowParentContentTypes	ソースコンテントタイプを貼り付け可能な、貼り付け選択位置の親のコンテントタイプのセット
	 * @param allowContentTypes			ソースコンテントタイプを貼り付け可能な、貼り付け選択位置のコンテントタイプのセット
	 * @param srcContentType	ソースコンテントタイプ
	 * @param srcNodeCount		ソースのノード数
	 */
	static public void appendAllowCopyContentTypes(Set<DtContainerContentTypes> allowParentContentTypes, Set<DtContainerContentTypes> allowContentTypes, DtContainerContentTypes srcContentType, int srcNodeCount) {
		switch (srcContentType) {
			case ContentExalge:
				allowContentTypes.add(DtContainerContentTypes.ContentExAlgeSet);
				allowContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				allowParentContentTypes.add(DtContainerContentTypes.ContentExAlgeSet);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				break;
			case ContentExAlgeSet:
				allowContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				break;
			case ContentDtalge:
				if (srcNodeCount == 1) {
					// ソースノード数が 1 の場合のみ、ノートに貼り付け可能
					allowContentTypes.add(DtContainerContentTypes.ContentDtSlipNote);
					allowContentTypes.add(DtContainerContentTypes.ContentDtBinderNote);
				}
				allowContentTypes.add(DtContainerContentTypes.ContentDtAlgeSet);
				allowContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtAlgeSet);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				break;
			case ContentDtAlgeSet:
				allowContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
				break;
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				if (srcNodeCount == 1) {
					// ソースノード数が 1 の場合のみ、ノートに貼り付け可能
					allowContentTypes.add(DtContainerContentTypes.ContentDtSlipNote);
					allowContentTypes.add(DtContainerContentTypes.ContentDtBinderNote);
				}
				allowContentTypes.add(DtContainerContentTypes.ContentDtAlgeSet);		// データ代数元として貼り付け可能
				allowContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);	// データ代数元として貼り付け可能
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtAlgeSet);		// データ代数元として貼り付け可能
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtSlipObjects);	// データ代数元として貼り付け可能
				break;
			case ContentDtSlip:
				allowContentTypes.add(DtContainerContentTypes.ContentDtSlipList);
				allowContentTypes.add(DtContainerContentTypes.ContentDtBinderSlips);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtSlipList);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtBinderSlips);
				break;
			case ContentDtSlipList:
				allowContentTypes.add(DtContainerContentTypes.ContentDtBinderSlips);
				allowParentContentTypes.add(DtContainerContentTypes.ContentDtBinderSlips);
				break;
			default:
				break;	// その他は無視
		}
	}

	//------------------------------------------------------------
	// Implement java.awt.datatransfer.Transferable interfaces
	//------------------------------------------------------------
	
	/**
	 * 転送されるデータを表すオブジェクトを取得する。
	 * 返されるオブジェクトのクラスは、フレーバーの表現クラスで定義される。
	 * @param flavor	データに対して要求されたフレーバー
	 * @return	転送されるデータを表すオブジェクト
	 * @throws UnsupportedFlavorException	要求されたデータフレーバーがサポートされていない場合
	 * @throws IOException	要求されたフレーバーのデータが使用できなくなった場合
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (isContentTreeDataFlavor(flavor)) {
			return this;
		}

		throw new UnsupportedFlavorException(flavor);
	}

	/**
	 * データを提供することができるフレーバーを示す {@code DataFlavor} オブジェクトの配列を取得する。
	 * 配列は、データ提供のための優先順位に従って(もっとも詳しく記述されているものから、そうでないものへ)順序付けされている必要がある。
	 * @return	このデータを転送できるデータフレーバーの配列
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return (isContentTreeDataSupported() ? FULL_SUPPORTED_FLAVORS : EMPTY_SUPPORTED_FLAVORS);
	}

	/**
	 * 指定されたデータフレーバーが、このオブジェクトに対してサポートされているかどうかを判定する。
	 * @param flavor	データに対して要求されたフレーバー
	 * @return	データフレーバーがサポートされていれば {@code true}
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		DataFlavor[] flavors = getTransferDataFlavors();
		for (DataFlavor f : flavors) {
			if (f.equals(flavor)) {
				return true;
			}
		}
		return false;
	}

	//------------------------------------------------------------
	// Implement java.awt.datatransfer.ClipboardOwner interfaces
	//------------------------------------------------------------

	/**
	 * クリップボードのオーナーではなくなったことを、このオブジェクトに通知する。
	 * クリップボードの所有権が、別のアプリケーションもしくは別オブジェクトから主張されたときに、呼び出される。
	 * @param clipboard	所有されなくなったクリップボード
	 * @param contents	この所有者がクリップボードに配置したコンテンツ
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		if (AppLogger.isTraceEnabled()) {
			AppLogger.trace("called DTCContentTransferTreeData#lostOwnership() : clipboard=" + String.valueOf(clipboard) + " : contents=" + String.valueOf(contents));
			AppLogger.trace(String.format("===== (contents == this) => %s", String.valueOf(contents == this)));
			AppLogger.trace(String.format("===== PID=%s : thread-ID=%s", String.valueOf(ProcessHandle.current().pid()), String.valueOf(Thread.currentThread().getId())));
			AppLogger.trace(String.format("===== contents.isDataFlavorSupported(dtcContentTreeDataFravor)=>%s", String.valueOf(contents.isDataFlavorSupported(dtcContentTreeDataFravor))));
		}
		if (contents.isDataFlavorSupported(dtcContentTreeDataFravor)) {
			final DTCContentTransferTreeData transdata = DTCContentTransferTreeData.getDataFromTransferable(contents);
			if (AppLogger.isTraceEnabled()) {
				AppLogger.trace(String.format("===== DTCContentTransferTreeData.getDataFromTransferable(contents) => transdata=%s", String.valueOf(transdata)));
				AppLogger.trace(String.format("===== (transdata == this) => %s", String.valueOf(transdata == this)));
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (AppLogger.isTraceEnabled()) {
						AppLogger.trace("called DTCContentTransferTreeData#lostOwnership().Runnable#run() is invoked SwingUtilities.invokeLater()");
						AppLogger.trace(String.format("===== PID=%s : thread-ID=%s", String.valueOf(ProcessHandle.current().pid()), String.valueOf(Thread.currentThread().getId())));
					}
					clean();
				}
			});
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isContentTreeDataSupported() {
		return (_treeNodes != null && _treeNodes.length > 0);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
