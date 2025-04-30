/*
 * @(#)DtContainerContentDisplayableType.java	1.0.0	2022/12/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content;

public class DtContainerContentDisplayableType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** コンテントの種類 **/
	private final DtContainerContentTypes	_type;
	/** 表示文字列 **/
	private final String	_displayText;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtContainerContentDisplayableType(DtContainerContentTypes type, String displayText) {
		if (type == null)
			throw new NullPointerException("DtContainerContentTypes is null");
		if (displayText == null)
			throw new NullPointerException("Display text is null");
		
		_type = type;
		_displayText = displayText;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public DtContainerContentTypes type() {
		return _type;
	}
	
	public String displayText() {
		return _displayText;
	}
	
	@Override
	public String toString() {
		return _displayText;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
