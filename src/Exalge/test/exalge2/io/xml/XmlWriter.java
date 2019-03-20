/*
 * @(#)XmlWriter.java	0.91	2007/08/09
 * 
 * created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import exalge2.io.FileUtil;
import exalge2.io.xml.XmlDocument;

/**
 * 未実装のため、使用できない。
 * 
 * @deprecated 未実装
 *
 */
abstract class XmlWriter {
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private PrintWriter _writer;
	
	private boolean fCanonical;
	private boolean fXML11;
	
	private int countIndent = 0;
	private String indentText = "  ";

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public XmlWriter(File csvFile)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		// create BufferedWriter
		FileOutputStream foStream = new FileOutputStream(csvFile);
		OutputStreamWriter osWriter = null;
		try {
			osWriter = new OutputStreamWriter(foStream, XmlDocument.XML_CHARSET);
		}
		catch (UnsupportedEncodingException ex) {
			FileUtil.closeStream(foStream);
			throw ex;
		}
		catch (NullPointerException ex) {
			FileUtil.closeStream(foStream);
			throw ex;
		}
		this._writer = new PrintWriter(new BufferedWriter(osWriter));
		fCanonical = false;
	}
	
	public XmlWriter(Writer writer) {
		this._writer = new PrintWriter(writer);
		fCanonical = false;
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void close() {
		FileUtil.closeStream(this._writer);
	}
	
	public void flush() throws IOException
	{
		this._writer.flush();
	}
	
	public void writeDocument(Document xmlDocument) {
		writeNode(xmlDocument);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
    /** Writes the specified node, recursively. */
    public void writeNode(Node node)
    {
        // is there anything to do?
        if (node == null) {
            return;
        }

        short type = node.getNodeType();
        switch (type) {
            case Node.DOCUMENT_NODE: {
                Document document = (Document)node;
                fXML11 = "1.1".equals(getVersion(document));
                if (!fCanonical) {
                    if (fXML11) {
                    	_writer.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
                    }
                    else {
                    	_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    }
                    _writer.flush();
                    writeNode(document.getDoctype());
                }
                writeNode(document.getDocumentElement());
                break;
            }

            case Node.DOCUMENT_TYPE_NODE: {
                DocumentType doctype = (DocumentType)node;
                _writer.print("<!DOCTYPE ");
                _writer.print(doctype.getName());
                String publicId = doctype.getPublicId();
                String systemId = doctype.getSystemId();
                if (publicId != null) {
                	_writer.print(" PUBLIC \"");
                	_writer.print(publicId);
                	_writer.print("\" \"");
                	_writer.print(systemId);
                	_writer.print('\"');
                }
                else if (systemId != null) {
                	_writer.print(" SYSTEM \"");
                	_writer.print(systemId);
                	_writer.print('\"');
                }
                String internalSubset = doctype.getInternalSubset();
                if (internalSubset != null) {
                	_writer.println(" [");
                	_writer.print(internalSubset);
                	_writer.print(']');
                }
                _writer.println('>');
                _writer.flush();
                break;
            }

            case Node.ELEMENT_NODE: {
        		// Normal Element nodes
        		printIndent();
        		_writer.print('<');
        		_writer.print(node.getNodeName());
        		Attr attrs[] = sortAttributes(node.getAttributes());
        		for (int i = 0; i < attrs.length; i++) {
        			Attr attr = attrs[i];
        			_writer.print(' ');
        			_writer.print(attr.getNodeName());
        			_writer.print("=\"");
        			normalizeAndPrint(attr.getNodeValue(), true);
        			_writer.print('"');
        		}
        		
        		if (node.getFirstChild() != null) {
        			// check children
        			boolean needLineFeed = false;
        			Node child = node.getFirstChild();
        			while (child != null) {
        				short childNodeType = child.getNodeType();
        				if (childNodeType != Node.TEXT_NODE) {
        					needLineFeed = true;
        					break;
        				}
        				child = child.getNextSibling();
        			}
        			_writer.print('>');
        			_writer.flush();
        			if (needLineFeed) {
        				_writer.println();
            			incrementIndent();
        			}

        			child = node.getFirstChild();
        			while (child != null) {
        				if (needLineFeed && child.getNodeType() == Node.TEXT_NODE) {
        					printIndent();
        					writeNode(child);
        					_writer.println();
        				} else {
        					writeNode(child);
        				}
        				child = child.getNextSibling();
        			}

        			if (needLineFeed) {
        				decrementIndent();
        				printIndent();
        			}
        			
        			// termination
        			_writer.print("</");
        			_writer.print(node.getNodeName());
        			_writer.print('>');
        			_writer.println();
        			_writer.flush();
        		}
        		else {
        			// termination
        			_writer.println(" />");
        			_writer.flush();
        		}
                break;
            }

            case Node.ENTITY_REFERENCE_NODE: {
                if (fCanonical) {
                    Node child = node.getFirstChild();
                    while (child != null) {
                        writeNode(child);
                        child = child.getNextSibling();
                    }
                }
                else {
                	printIndent();
                	_writer.print('&');
                	_writer.print(node.getNodeName());
                	_writer.print(';');
                	_writer.println();
                	_writer.flush();
                }
                break;
            }

            case Node.CDATA_SECTION_NODE: {
                if (fCanonical) {
                	printIndent();
                    normalizeAndPrint(node.getNodeValue(), false);
                }
                else {
                	printIndent();
                	_writer.print("<![CDATA[");
                	_writer.print(node.getNodeValue());
                	_writer.print("]]>");
                }
                _writer.println();
                _writer.flush();
                break;
            }

            case Node.TEXT_NODE: {
            	//printIndent();
                normalizeAndPrint(node.getNodeValue(), false);
                //fOut.println();
                _writer.flush();
                break;
            }

            case Node.PROCESSING_INSTRUCTION_NODE: {
            	printIndent();
            	_writer.print("<?");
            	_writer.print(node.getNodeName());
                String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                	_writer.print(' ');
                	_writer.print(data);
                }
                _writer.print("?>");
                _writer.println();
                _writer.flush();
                break;
            }
            
            case Node.COMMENT_NODE: {
                if (!fCanonical) {
                	printIndent();
                	_writer.print("<!--");
                    String comment = node.getNodeValue();
                    if (comment != null && comment.length() > 0) {
                    	_writer.print(comment);
                    }
                    _writer.print("-->");
                    _writer.println();
                    _writer.flush();
                }
            }
        }

    } // write(Node)
    
    private void incrementIndent() {
    	countIndent++;
    }
    
    private void decrementIndent() {
    	countIndent = (countIndent > 1 ? countIndent - 1 : 0);
    }

    /*---
    private void resetIndent() {
    	countIndent = 0;
    }
    ---*/
    
    private void printIndent() {
    	for (int i = 0; i < countIndent; i++) {
    		_writer.print(indentText);
    	}
    }
	
    /** Returns a sorted list of attributes. */
	private Attr[] sortAttributes(NamedNodeMap attrs)
	{
		int len = (attrs != null) ? attrs.getLength() : 0;
		
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++) {
			array[i] = (Attr)attrs.item(i);
		}
		
		for (int i = 0; i < len - 1; i++) {
			String name = array[i].getNodeName();
			
			int index = i;
			for (int j = i+1; j < len; j++) {
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0) {
					name = curName;
					index = j;
				}
			}
			if (index != i) {
				Attr temp = array[i];
				array[i] = array[index];
				array[index] = temp;
			}
		}
		
		return array;
	}

    /** Normalizes and prints the given string. */
    private void normalizeAndPrint(String s, boolean isAttValue)
    {
        int len = (s != null) ? s.length() : 0;
        
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            normalizeAndPrint(c, isAttValue);
        }

    }

    /** Normalizes and print the given character. */
    private void normalizeAndPrint(char c, boolean isAttValue)
    {
        switch (c) {
            case '<': {
            	_writer.print("&lt;");
                break;
            }
            case '>': {
            	_writer.print("&gt;");
                break;
            }
            case '&': {
            	_writer.print("&amp;");
                break;
            }
            case '"': {
                // A '"' that appears in character data 
                // does not need to be escaped.
                if (isAttValue) {
                	_writer.print("&quot;");
                }
                else {
                	_writer.print("\"");
                }
                break;
            }
            case '\r': {
            	// If CR is part of the document's content, it
            	// must not be printed as a literal otherwise
            	// it would be normalized to LF when the document
            	// is reparsed.
            	_writer.print("&#xD;");
            	break;
            }
            case '\n': {
                if (fCanonical) {
                	_writer.print("&#xA;");
                    break;
                }
                // else, default print char
            }
            default: {
            	// In XML 1.1, control chars in the ranges [#x1-#x1F, #x7F-#x9F] must be escaped.
            	//
            	// Escape space characters that would be normalized to #x20 in attribute values
            	// when the document is reparsed.
            	//
            	// Escape NEL (0x85) and LSEP (0x2028) that appear in content 
            	// if the document is XML 1.1, since they would be normalized to LF 
            	// when the document is reparsed.
            	if (fXML11 && ((c >= 0x01 && c <= 0x1F && c != 0x09 && c != 0x0A) 
            	    || (c >= 0x7F && c <= 0x9F) || c == 0x2028)
            	    || isAttValue && (c == 0x09 || c == 0x0A)) {
            		_writer.print("&#x");
            		_writer.print(Integer.toHexString(c).toUpperCase());
            		_writer.print(";");
                }
                else {
                	_writer.print(c);
                }        
            }
        }
    }
    
    /** Extracts the XML version from the Document. */
    private String getVersion(Document document)
    {
    	if (document == null) {
    	    return null;
    	}
    	
        String version = null;
        Method getXMLVersion = null;
        try {
            getXMLVersion = document.getClass().getMethod("getXmlVersion", new Class[]{});
            // If Document class implements DOM L3, this method will exist.
            if (getXMLVersion != null) {
                version = (String) getXMLVersion.invoke(document, (Object[]) null);
            }
        } 
        catch (Exception e) { 
            // Either this locator object doesn't have 
            // this method, or we're on an old JDK.
        }
        return version;
    }
}
