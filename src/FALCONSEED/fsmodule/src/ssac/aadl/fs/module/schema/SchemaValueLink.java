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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SchemaValueLink.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeString;
import ssac.aadl.runtime.util.Objects;

/**
 * スキーマ定義における値のリンクを保持するクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaValueLink extends SchemaValueObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** リンク元のオブジェクト **/
	private SchemaElementValue			_linkTarget;
	private SchemaValueLinkParameter	_linkParameter;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaValueLink() {
		super();
	}
	
	public SchemaValueLink(SchemaElementValue linkTarget) {
		super();
		_linkTarget = linkTarget;
	}
	
	public SchemaValueLink(final SchemaValueLink src) {
		super(src);
		_linkTarget = src._linkTarget;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasLinkParameter() {
		return (_linkParameter != null);
	}
	
	public void clearLinkParameter() {
		_linkParameter = null;
	}
	
	public SchemaValueLinkParameter getLinkParameter() {
		return _linkParameter;
	}
	
	public boolean hasLinkTarget() {
		return (_linkTarget != null);
	}
	
	public SchemaElementValue getLinkTarget() {
		return _linkTarget;
	}
	
	public void setLinkTarget(SchemaElementValue newTarget) {
		_linkTarget = newTarget;
	}
	
	public boolean updateLinkTarget(SchemaElementValue newTarget) {
		if (newTarget == _linkTarget)
			return false;
		//--- modified
		_linkTarget = newTarget;
		return true;
	}

	@Override
	public SchemaValueType getValueType() {
		return (_linkTarget==null ? SchemaValueTypeString.instance : _linkTarget.getValueType());
	}

	@Override
	public void setValueType(SchemaValueType newValueType) {
		if (_linkTarget != null) {
			_linkTarget.setValueType(newValueType);
		}
	}

	@Override
	public boolean updateValueType(SchemaValueType newValueType) {
		if (_linkTarget != null) {
			return _linkTarget.updateValueType(newValueType);
		} else {
			return false;
		}
	}

	@Override
	public boolean hasValue() {
		return (_linkTarget==null ? false : _linkTarget.hasValue());
	}

	@Override
	public Object getValue() {
		return (_linkTarget==null ? null : _linkTarget.getValue());
	}

	@Override
	public void setValue(Object newValue) {
		if (_linkTarget != null) {
			_linkTarget.setValue(newValue);
		}
	}

	@Override
	public boolean updateValue(Object newValue) {
		if (_linkTarget != null) {
			return _linkTarget.updateValue(newValue);
		} else {
			return false;
		}
	}

	/**
	 * このオブジェクトに設定された要素番号が有効かどうかを判定する。
	 * @return	要素番号が無効の場合は <tt>false</tt>
	 */
	@Override
	public boolean isValidElementNo() {
		return (_linkTarget==null ? false : _linkTarget.isValidElementNo());
	}
	
	/**
	 * このオブジェクトの要素番号を取得する。
	 * @return	正の要素番号、要素番号が無効の場合は (-1)
	 */
	@Override
	public int getElementNo() {
		return (_linkTarget==null ? SchemaElementObject.INVALID_ELEMENT_NO : _linkTarget.getElementNo());
	}

	/**
	 * このオブジェクトに要素番号を設定する。
	 * @param newElemNo	新しい要素番号、無効とする場合は負の値
	 */
	@Override
	public void setElementNo(int newElemNo) {
		if (_linkTarget != null) {
			_linkTarget.setElementNo(newElemNo);
		}
	}

	/**
	 * このオブジェクトに要素番号を設定する。
	 * @param newElemNo	新しい要素番号、無効とする場合は負の値
	 * @return	要素番号が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateElementNo(int newElemNo) {
		if (_linkTarget != null) {
			return _linkTarget.updateElementNo(newElemNo);
		} else {
			return false;
		}
	}

	/**
	 * この要素の親となるオブジェクトが設定されているかを判定する。
	 * @return	要素の親オブジェクトが設定されている場合は <tt>true</tt>
	 */
	@Override
	public boolean hasParentObject() {
		return (_linkTarget==null ? false : _linkTarget.hasParentObject());
	}

	/**
	 * 設定されているこの要素の親オブジェクトを取得する。
	 * @return	設定されている親オブジェクト、設定されていな場合は <tt>null</tt>
	 */
	@Override
	public SchemaObject getParentObject() {
		return (_linkTarget==null ? null : _linkTarget.getParentObject());
	}

	/**
	 * このオブジェクトに親オブジェクトを設定する。
	 * @param newParent	新しい親オブジェクト
	 */
	@Override
	public void setParentObject(SchemaObject newParent) {
		if (_linkTarget != null) {
			_linkTarget.setParentObject(newParent);
		}
	}

	/**
	 * このオブジェクトに親オブジェクトを設定する。
	 * このメソッドでは、親オブジェクトが同一かどうか（インスタンスが同じかどうか)を判定し、
	 * 異なるオブジェクトであれば更新する。
	 * @param newParent	新しい親オブジェクト
	 * @return	親オブジェクトのインスタンスが変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateParentObject(SchemaObject newParent) {
		if (_linkTarget != null) {
			return _linkTarget.updateParentObject(newParent);
		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return (_linkTarget==null ? null : _linkTarget.getName());
	}

	@Override
	public void setName(String newName) {
		if (_linkTarget != null) {
			_linkTarget.setName(newName);
		}
	}

	@Override
	public boolean updateName(String newName) {
		if (_linkTarget != null) {
			return _linkTarget.updateName(newName);
		} else {
			return false;
		}
	}

	@Override
	public String getDescription() {
		return (_linkTarget==null ? null : _linkTarget.getDescription());
	}

	@Override
	public void setDescription(String newDesc) {
		if (_linkTarget != null) {
			_linkTarget.setDescription(newDesc);
		}
	}

	@Override
	public boolean updateDescription(String newDesc) {
		if (_linkTarget != null) {
			return _linkTarget.updateDescription(newDesc);
		} else {
			return false;
		}
	}

	@Override
	protected boolean hasName() {
		return (_linkTarget==null ? false : (_linkTarget.getName()!=null));
	}

	@Override
	protected boolean hasDescription() {
		return (_linkTarget==null ? false : (_linkTarget.getDescription()!=null));
	}

	//------------------------------------------------------------
	// Implement SchemaObject interfaces
	//------------------------------------------------------------

	@Override
	public String toNameString() {
		return (_linkTarget==null ? super.toNameString() : _linkTarget.toNameString());
	}

	/**
	 * このオブジェクトを示す変数名表現を返す。
	 * @return	このオブジェクトを示す変数名表現
	 */
	@Override
	public String toVariableNameString() {
		return (_linkTarget==null ? super.toVariableNameString() : _linkTarget.toVariableNameString());
	}

	//------------------------------------------------------------
	// Implement java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		return (_linkTarget==null ? 0 : _linkTarget.hashCode());
	}

	//------------------------------------------------------------
	// Public interfaces for XML
	//------------------------------------------------------------

	/**
	 * 指定されたライターオブジェクトにより、このオブジェクトの XML 表現を出力する。
	 * @param writer	専用のライターオブジェクト
	 * @throws XMLStreamException	XML 出力に失敗した場合
	 */
	@Override
	public void writeToXml(SchemaConfigWriter writer) throws XMLStreamException
	{
		XMLStreamWriter xmlWriter = writer.getXMLWriter();

		// start tag
		xmlWriter.writeStartElement(getXmlElementName());
		
		// make string
		if (_linkTarget != null) {
			ArrayList<String> elemPathList = new ArrayList<String>();
			SchemaObject parent;
			SchemaObject targetValue = _linkTarget;
			do {
				int elemNo;
				String className = targetValue.getXmlElementName();
				if (targetValue instanceof SchemaElementObject) {
					SchemaElementObject elem = (SchemaElementObject)targetValue;
					parent = elem.getParentObject();
					elemNo = elem.getElementNo();
					//--- check
					if (parent != null && elemNo < 1) {
						throw new IllegalArgumentException("Target value has parent, but element number is invalid : " + targetValue.toParamString());
					}
				} else {
					parent = null;
					elemNo = SchemaElementObject.INVALID_ELEMENT_NO;
				}
				//--- create and add
				elemPathList.add(0, SchemaValueLinkElemLocation.toElementString(className, elemNo));
				targetValue = parent;
			} while (parent != null);

			StringBuilder sb = new StringBuilder();
			Iterator<String> it = elemPathList.iterator();
			sb.append(it.next());
			for (; it.hasNext(); ) {
				sb.append(SchemaValueLinkParameter.DELIM_CLASS);
				sb.append(it.next());
			}
			xmlWriter.writeCharacters(sb.toString());
		}
		
		// end tag
		xmlWriter.writeEndElement();
	}

	/**
	 * 指定されたリーダーオブジェクトの現在の位置から XML 表現を読み込み、このオブジェクトの内容を復元する。
	 * このメソッドで処理した場合、そのタグの END_ELEMENT が読み込み位置となっていることを前提とする。
	 * @param reader	専用のリーダーオブジェクト
	 * @throws XMLStreamException	XML 入力に失敗した場合
	 */
	@Override
	public void readFromXml(SchemaConfigReader reader) throws XMLStreamException {
		XMLStreamReader xmlReader = reader.getXMLReader();
		
		// start element
		SchemaXmlUtil.xmlValidStartElement(xmlReader, getXmlElementName());
		
		// get character
		Location xmlLocation = xmlReader.getLocation();
		String text = xmlReader.getElementText();
		if (text != null && !text.isEmpty()) {
			_linkParameter = new SchemaValueLinkParameter(text, xmlLocation);
		}
		
		// check end element
		SchemaXmlUtil.xmlValidEndElement(xmlReader, getXmlElementName());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		buffer.append("link=");
		if (_linkTarget == null) {
			buffer.append("null");
		} else {
			buffer.append(_linkTarget.toParamString());
		}
	}

	@Override
	protected boolean equalFields(Object obj) {
		SchemaValueLink aLink = (SchemaValueLink)obj;
		
		if (!Objects.equals(aLink._linkTarget, this._linkTarget))
			return false;
		
		// equal all fields
		return true;
	}
	
	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class SchemaValueLinkElemLocation
	{
		static public final char DELIM_ELEMNO	= '@';
		
		public final String	className;
		public final int	elementNo;
		
		public SchemaValueLinkElemLocation(String targetClassName, int targetElementNo) {
			className = targetClassName;
			elementNo = targetElementNo;
		}
		
		static public SchemaValueLinkElemLocation fromString(String elemPath) {
			int delimpos = elemPath.lastIndexOf(DELIM_ELEMNO);
			if (delimpos >= 0) {
				String strname = elemPath.substring(0, delimpos);
				String strelem = elemPath.substring(delimpos + 1);
				if (!strname.isEmpty()) {
					int elemno = SchemaElementObject.INVALID_ELEMENT_NO;
					if (!strelem.isEmpty()) {
						elemno = Integer.parseInt(strelem);
					}
					//--- check class
					try {
						Class.forName(strname);
					} catch (ClassNotFoundException ex) {
						throw new IllegalArgumentException("SchemaValueLink path's class not found : " + elemPath);
					}
					return new SchemaValueLinkElemLocation(strname, elemno);
				}
				// invalid data
				throw new IllegalArgumentException("SchemaValueLink path is illegal : " + elemPath);
			}
			else {
				int elemno = SchemaElementObject.INVALID_ELEMENT_NO;
				//--- check class
				try {
					Class.forName(elemPath);
				} catch (ClassNotFoundException ex) {
					throw new IllegalArgumentException("SchemaValueLink path's class not found : " + elemPath);
				}
				return new SchemaValueLinkElemLocation(elemPath, elemno);
			}
		}
		
		public String toString() {
			return className + DELIM_ELEMNO + Integer.toString(elementNo);
		}
		
		static public String toElementString(String className, int elemNo) {
			if (elemNo == SchemaElementObject.INVALID_ELEMENT_NO) {
				return className;
			} else {
				return className + DELIM_ELEMNO + Integer.toString(elemNo);
			}
		}
	}
	
	static public class SchemaValueLinkParameter
	{
		static protected final char	DELIM_CLASS	= '+';
		
		private Location	_xmlLocation;
		private ArrayList<SchemaValueLinkElemLocation>	_elemList = new ArrayList<SchemaValueLink.SchemaValueLinkElemLocation>();
		
		public Location getXmlLocation() {
			return _xmlLocation;
		}
		
		public List<SchemaValueLinkElemLocation> getLinkList() {
			return _elemList;
		}

		/**
		 * リンクパスを解析し、リンク情報を構築する。
		 * @param linkPath		リンクパスを示す文字列
		 * @param xmlLocation	リンクパス読み込み開始位置となる XML ストリーム上の位置
		 * @throws IllegalArgumentException	解析できないリンクパスの場合
		 */
		public SchemaValueLinkParameter(String linkPath, Location xmlLocation) {
			_xmlLocation = xmlLocation;
			int len = linkPath.length();
			if (len == 0)
				throw new IllegalArgumentException("Link path is empty.");
			int spos = 0;
			for (int delimpos = linkPath.indexOf(DELIM_CLASS); delimpos >= 0; ) {
				String elemPath = linkPath.substring(spos, delimpos);
				spos = delimpos + 1;
				_elemList.add(SchemaValueLinkElemLocation.fromString(elemPath));
				if (spos < len) {
					delimpos = linkPath.indexOf(DELIM_CLASS, spos);
				}
			}
			if (spos < len) {
				String elemPath = linkPath.substring(spos);
				_elemList.add(SchemaValueLinkElemLocation.fromString(elemPath));
			}
		}

		/**
		 * 指定されたリンク元オブジェクトから、リンク情報を構築する。
		 * @param targetValue	リンク元オブジェクト
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		public SchemaValueLinkParameter(SchemaObject targetValue) {
			SchemaObject parent;
			do {
				int elemNo;
				String className = targetValue.getXmlElementName();
				if (targetValue instanceof SchemaElementObject) {
					SchemaElementObject elem = (SchemaElementObject)targetValue;
					parent = elem.getParentObject();
					elemNo = elem.getElementNo();
					//--- check
					if (parent != null && elemNo < 1) {
						throw new IllegalArgumentException("Target value has parent, but element number is invalid : " + targetValue.toParamString());
					}
					else if (parent == null && elemNo > 0) {
						throw new IllegalArgumentException("Target value has no parent, but element number is valid : " + targetValue.toParamString());
					}
				} else {
					parent = null;
					elemNo = SchemaElementObject.INVALID_ELEMENT_NO;
				}
				//--- create and add
				_elemList.add(0, new SchemaValueLinkElemLocation(className, elemNo));
				targetValue = parent;
			} while (parent != null);
		}

		/**
		 * XML ストリームへ出力可能な文字列表現を返す。
		 */
		public String toString() {
			if (_elemList.isEmpty()) {
				return null;
			}
			
			StringBuilder sb = new StringBuilder();
			Iterator<SchemaValueLinkElemLocation> it = _elemList.iterator();
			sb.append(it.next().toString());
			for (; it.hasNext(); ) {
				sb.append(DELIM_CLASS);
				sb.append(it.next().toString());
			}
			
			return sb.toString();
		}
	}
}
