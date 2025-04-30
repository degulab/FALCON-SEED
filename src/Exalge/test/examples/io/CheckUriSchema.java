/*
 * @(#)StorageURI.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package examples.io;

import java.net.URI;

/**
 * URI のスキーマをチェックするためのテスト。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CheckUriSchema
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Main method
	//------------------------------------------------------------

	public static void main(String[] args) {
		System.out.println("<<< Started --- CheckUriSchema >>>");
		try {
			checkFileSchemas();
			checkMongoSchemas();
		}
		finally {
			System.out.println("<<< Finished --- CheckUriSchema >>>");
			System.out.flush();
			try {
				Thread.sleep(1000);
			} catch (Throwable ex) {}
		}
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static private void checkMongoSchemas() {
		System.out.println("### (checkMongoSchemas) Started...");
		
		checkStringUri("mongodb://host/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://host:8765/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user@host/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user@host:8765/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user:pass@host/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user:pass@host:8765/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://host1,host2/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://host1:8765,host2:5678/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user@host1,host2/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user@host1:8765,host2:5678/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user:pass@host1,host2/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user:pass@host1:8765,host2:5678/dbname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://host/dbname.colname?authSource=authdb");
		checkStringUri("mongodb://host:8765/dbname.colname?authSource=authdb");
		checkStringUri("mongodb://user@host/dbname.colname?authSource=authdb");
		checkStringUri("mongodb://user@host:8765/dbname.colname?authSource=authdb");
		checkStringUri("mongodb://user:pass@host/dbname.colname?authSource=authdb");
		checkStringUri("mongodb://user:pass@host:8765/dbname.colname?authSource=authdb");
		checkStringUri("mongodb://user:pass@host1,host2/dbname.colname?authSource=authdb&collection=colname");
		checkStringUri("mongodb://user:pass@host1:8765,host2:5678/dbname.colname?authSource=authdb&collection=colname");
		
		System.out.println("### (checkMongoSchemas) .....done.");
	}
	
	static private void checkFileSchemas() {
		System.out.println("### (checkFileSchemas) Started...");
		
		checkStringUri("hoge/age/uge.csv");
		checkStringUri("file:hoge/age/uge.csv");
		checkStringUri("file:/hoge/age/uge.csv");
		checkStringUri("file:@hoge/age/uge.csv");
		checkStringUri("file::hoge/age/uge.csv");
		checkStringUri("file://hoge/age/uge.csv");
		checkStringUri("file:///hoge/age/uge.csv");
		checkStringUri("file://c:/hoge/age/uge.csv");
		checkStringUri("file:///c:/hoge/age/uge.csv");
		checkStringUri("csvfile:hoge/age/uge.csv");
		checkStringUri("csvfile://hoge/age/uge.csv");
		checkStringUri("csvfile:///hoge/age/uge.csv");
		checkStringUri("csvfile://c:/hoge/age/uge.csv");
		checkStringUri("csvfile:///c:/hoge/age/uge.csv");
		
		System.out.println("### (checkFileSchemas) .....done.");
	}
	
	static private void checkStringUri(String struri) {
		System.out.println("  @@@ \"" + struri + "\"...");
		
		try {
			URI uri = new URI(struri);
			System.out.println("  uri.getScheme()               = " + String.valueOf(uri.getScheme()));
			System.out.println("  uri.getSchemeSpecificPart()   = " + String.valueOf(uri.getSchemeSpecificPart()));
			System.out.println("  uri.getRawSchemeSpecificPart()= " + String.valueOf(uri.getRawSchemeSpecificPart()));
			System.out.println("  uri.getAuthority()            = " + String.valueOf(uri.getAuthority()));
			System.out.println("  uri.getRawAuthority()         = " + String.valueOf(uri.getRawAuthority()));
			System.out.println("  uri.getHost()                 = " + String.valueOf(uri.getHost()));
			System.out.println("  uri.getPort()                 = " + String.valueOf(uri.getPort()));
			System.out.println("  uri.getPath()                 = " + String.valueOf(uri.getPath()));
			System.out.println("  uri.getRawPath()              = " + String.valueOf(uri.getRawPath()));
			System.out.println("  uri.getFragment()             = " + String.valueOf(uri.getFragment()));
			System.out.println("  uri.getRawFragment()          = " + String.valueOf(uri.getRawFragment()));
			System.out.println("  uri.getQuery()                = " + String.valueOf(uri.getQuery()));
			System.out.println("  uri.getRawQuery()             = " + String.valueOf(uri.getRawQuery()));
			System.out.println("  uri.getUserInfo()             = " + String.valueOf(uri.getUserInfo()));
			System.out.println("  uri.getRawUserInfo()          = " + String.valueOf(uri.getRawUserInfo()));
			System.out.println("  uri.isAbsolute()              = " + String.valueOf(uri.isAbsolute()));
			System.out.println("  uri.isOpaque()                = " + String.valueOf(uri.isOpaque()));
			System.out.println("  uri.toASCIIString()           = " + String.valueOf(uri.toASCIIString()));
			System.out.println("  uri.toString()                = " + String.valueOf(uri.toString()));
		}
		catch (Throwable ex) {
			System.err.println("  Failed to create URI from String-URI : " + ex);
			ex.printStackTrace();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
