package dtalge.container.util;

import dtalge.exception.CsvFormatException;

/**
 * 複式記述簡易データ伝票 CSV フォーマットエラーを示す例外。
 * <p>
 * 複式記述簡易データ伝票 CSV ファイル読み込み時に、記述内容が適切ではない場合にスローされる。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtDoubleEntrySimpleSlipsCsvFormatError extends CsvFormatException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 3240665447339388365L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 詳細情報を持たない新規例外を構築する。
	 */
	public DtDoubleEntrySimpleSlipsCsvFormatError() {
		super();
	}

	/**
	 * 指定された詳細メッセージのみを持つ新規例外を構築する。
	 * 
	 * @param message 詳細メッセージ
	 */
	public DtDoubleEntrySimpleSlipsCsvFormatError(String message) {
		super(message);
	}

	/**
	 * 指定されたエラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param lineNo	エラー発生箇所を示す行番号
	 * @param fieldNo	エラー発生箇所を示すフィールド番号
	 */
	public DtDoubleEntrySimpleSlipsCsvFormatError(int lineNo, int fieldNo) {
		super(lineNo, fieldNo);
	}

	/**
	 * 指定された詳細メッセージ、エラー発生箇所を持つ新規例外を構築する。
	 * 
	 * @param message	詳細メッセージ
	 * @param lineNo	エラー発生箇所を示す行番号
	 * @param fieldNo	エラー発生箇所を示すフィールド番号
	 */
	public DtDoubleEntrySimpleSlipsCsvFormatError(String message, int lineNo, int fieldNo) {
		super(message, lineNo, fieldNo);
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
