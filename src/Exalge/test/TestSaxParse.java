import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 */

/**
 * @author ishizuka
 *
 */
public class TestSaxParse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
            // SAXパーサのファクトリーの生成
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // フィーチャーの設定
            //factory.setValidating(true);
            //factory.setNamespaceAware(true);
            //factory.setFeature("http://apache.org/xml/features/validation/schema",true);
            // SAXパーサの生成
            SAXParser parser = factory.newSAXParser();
            // ハンドラの生成
            DefaultHandler handler = new MySAXHandler();
            // 解析
            parser.parse(args[0],handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	static class MySAXHandler extends DefaultHandler {
		private Locator curLocator = null;
		
	    // ContentHandlerの実装
		public void setDocumentLocator(Locator locator) {
			// 対象ドキュメントの Locator 設定(コールバックとして、インスタンスを受け取る)
			curLocator = locator;
			if (locator != null) {
				System.out.println("Catched SAX document locator [" + locator.toString() + "]");
			}
		}
		
	    public void startDocument() throws SAXException {
	    	if (curLocator != null) {
	    		System.out.print("[Line:" + curLocator.getLineNumber());
	    		System.out.print("/" + curLocator.getColumnNumber() + "]");
	    		System.out.println("startDocument()");
	    	}
	    	else
	    		System.out.println("startDocument()");
	    }
	    public void endDocument() throws SAXException {
	    	if (curLocator != null) {
	    		System.out.print("[Line:" + curLocator.getLineNumber());
	    		System.out.print("/" + curLocator.getColumnNumber() + "]");
	    		System.out.println("endDocument()");
	    	}
	    	else
	    		System.out.println("endDocument()");
	    }
	    public void startElement(java.lang.String uri,
	                       java.lang.String localName,
	                       java.lang.String qName,
	                       Attributes atts)
	                throws SAXException {
	    	if (curLocator != null) {
	    		System.out.print("[Line:" + curLocator.getLineNumber());
	    		System.out.print("/" + curLocator.getColumnNumber() + "]");
	    		System.out.println("startElement()");
	    	}
	    	else
	    		System.out.println("startElement()");
		    System.out.println("\tnamespace=" + uri);
		    System.out.println("\tlocal name=" + localName);
		    System.out.println("\tqualified name=" + qName);
		    for (int i = 0; i < atts.getLength(); i++) {
			    System.out.println("\tattribute name=" + atts.getLocalName(i));
			    System.out.println("\tattribute qualified name=" + atts.getQName(i));
			    System.out.println("\tattribute value=" + atts.getValue(i));
			}
	    }
	    public void endElement(java.lang.String uri,
	                       java.lang.String localName,
	                       java.lang.String qName)
	                throws SAXException {
	    	if (curLocator != null) {
	    		System.out.print("[Line:" + curLocator.getLineNumber());
	    		System.out.print("/" + curLocator.getColumnNumber() + "]");
	    		System.out.println("endElement()");
	    	}
	    	else
	    		System.out.println("endElement()");
	    }
	    public void characters(char[] ch,
	                       int start,
	                       int length)
	                throws SAXException {
	    	if (curLocator != null) {
	    		System.out.print("[Line:" + curLocator.getLineNumber());
	    		System.out.print("/" + curLocator.getColumnNumber() + "]");
	    		System.out.println("characters()" + new String(ch, start, length));
	    	}
	    	else
	    		System.out.println("characters()" + new String(ch, start, length));
	    }
	    // ErrorHandlerの実装
	    public void warning(SAXParseException e) {
	        System.out.println("警告: " + e.getLineNumber() +"行目");
	        System.out.println(e.getMessage());
	    }
	    public void error(SAXParseException e) {
	        System.out.println("エラー: " + e.getLineNumber() +"行目");
	        System.out.println(e.getMessage());
	    }
	    public void fatalError(SAXParseException e) {
	        System.out.println("深刻なエラー: " + e.getLineNumber() +"行目");
	        System.out.println(e.getMessage());
	    }
	}
}
