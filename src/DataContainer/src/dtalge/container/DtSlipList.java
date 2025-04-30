/*
 * @(#)DtSlipList.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container;

import java.util.ArrayList;
import java.util.Collection;

/**
 * データスリップ(<code>{@link DtSlip}</code>)の集合を保持するクラス。
 * 
 * <p>データスリップ(<code>{@link DtSlip}</code>)のインスタンスのリストであり、
 * 同じ値、もしくは同じインスタンスを格納できる。
 * <br>
 * このクラスは、{@link java.util.ArrayList} の実装となる。
 * したがって、挿入メソッド、ファイル入出力において、
 * クラス内での要素の順序は基本的に維持される。
 * <p>
 * このクラスでは、<code>null</code> を許容しない。
 * <br>また、<b>この実装は同期化されない</b>。
 * <p>
 * このクラスの {@link #iterator()} メソッドによって返される反復子は、「フェイルファスト」である。
 * 反復子の作成後に、マップが構造的に変更されると、反復子は <code>ConcurrentModificationException</code> をスローする。
 * したがって、同時変更が行われると、反復子は、将来の予測できない時点において予測できない動作が発生する危険を回避するため、
 * ただちにかつ手際よく例外をスローする。
 * <br>
 * 通常、非同期の同時変更がある場合、確かな保証を行うことは不可能なので、反復子のフェイルファストの動作を保証することはできない。
 * フェイルファスト反復子は最善努力原則に基づき、<code>ConcurrentModificationException</code> をスローする。
 * したがって、正確を期すためにこの例外に依存するプログラムを書くことは誤りである。
 * 「反復子のフェイルファストの動作はバグを検出するためにのみ使用すべきである」
 * <p>
 * {@code JSON} 形式でのシリアライズ／デシリアライズについては、{@link dtalge.json.DtJSON} クラスの説明を参照。
 * データスリップの {@code JSON} 形式については、{@link dtalge.json.serialize.DtJsonDtSlipListSerializer} の説明を参照。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @see dtalge.json.DtJSON
 * @see dtalge.json.serialize.DtJsonDtSlipListSerializer
 */
public class DtSlipList extends ArrayList<DtSlip>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -5284892961750243837L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 要素が空の、新しいインスタンスを生成する。
	 */
	public DtSlipList() {
		super();
	}

	/**
	 * 指定された要素を保持する、新しいインスタンスを生成する。
	 * @param c	このオブジェクトに格納するデータスリップ({@link DtSlip})のコレクション
	 */
	public DtSlipList(Collection<? extends DtSlip> c) {
		super(c);
	}

	/**
	 * 指定された容量を持つ、要素が空の新しいインスタンスを生成する。
	 * @param initialCapacity	初期容量
	 * @throws IllegalArgumentException	初期容量が負の値の場合
	 */
	public DtSlipList(int initialCapacity) {
		super(initialCapacity);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
